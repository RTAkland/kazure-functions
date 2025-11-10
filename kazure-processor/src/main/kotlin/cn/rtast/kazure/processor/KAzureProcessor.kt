/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

package cn.rtast.kazure.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName


class KAzureProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var invoked = false

    private fun resolveAnnotationParametersWithDefaults(annotation: KSAnnotation): Map<String, Any?> {
        val annotationClass = annotation.annotationType.resolve().declaration as KSClassDeclaration
        val constructor = annotationClass.primaryConstructor
            ?: error("No primary constructor found in ${annotationClass.simpleName.asString()}")
        val explicitArgs = annotation.arguments.associateBy({ it.name?.getShortName() }, { it.value })
        val result = mutableMapOf<String, Any?>()
        for (param in constructor.parameters) {
            val name = param.name?.asString() ?: continue
            if (explicitArgs.containsKey(name)) {
                result[name] = explicitArgs[name]
            } else {
                if (param.hasDefault) {
                    val defaultExpr = param.toString()
                    result[name] = defaultExpr
                } else {
                    result[name] = null
                }
            }
        }
        return result
    }

    fun createParameterSpecs(func: KSFunctionDeclaration): List<ParameterSpec> {
        val params = mutableListOf<ParameterSpec>()
        func.parameters.forEach { p ->
            val paramName = p.name?.getShortName() ?: return@forEach
            val paramType = p.type.toTypeName()
            val paramAnnotation = p.annotations.find { it.shortName.asString() == "BindingName" }
            if (paramAnnotation != null) {
                val paramValue = resolveAnnotationParametersWithDefaults(paramAnnotation)["value"]
                val bindingNameAnnotationSpec = AnnotationSpec.builder(
                    ClassName("com.microsoft.azure.functions.annotation", "BindingName")
                )
                if (paramValue != null) {
                    bindingNameAnnotationSpec.addMember("%S", paramValue)
                }
                val paramSpec = ParameterSpec.builder(paramName, paramType)
                    .addAnnotation(bindingNameAnnotationSpec.build())
                    .build()
                params.add(paramSpec)
            } else {
                val paramSpec = ParameterSpec.builder(paramName, paramType).build()
                params.add(paramSpec)
            }
        }
        return params
    }

    private fun KSType.isDirectSubclassOf(fqName: String): Boolean {
        return (this.declaration as KSClassDeclaration)
            .superTypes.any { it.resolve().declaration.qualifiedName?.asString() == fqName }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true
        val functions = resolver.getSymbolsWithAnnotation(HttpRoutingFqName)
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { it.annotations.any { an -> an.annotationType.resolve().declaration.qualifiedName?.asString() == "cn.rtast.kazure.auth.AuthConsumer" } }
        val fileSpecBuilder = FileSpec.builder("kazure.generated", "KAzureGenerated")
            .addImport("cn.rtast.kazure.response", "respondText")
            .addImport("com.microsoft.azure.functions", "HttpMethod")
            .addImport("com.microsoft.azure.functions.annotation", "AuthorizationLevel")
            .addImport("com.microsoft.azure.functions", "HttpStatus")
        functions.forEach { func ->
            val newFunction = FunSpec.builder(func.simpleName.asString() + "_wrapped")
            val originHttpRoutingAnnotation =
                func.annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() == "cn.rtast.kazure.trigger.HttpRouting" }!!
            val routingValues = resolveAnnotationParametersWithDefaults(originHttpRoutingAnnotation)
            val httpRoutingAnnotationSpec = AnnotationSpec.builder(ClassName("cn.rtast.kazure.trigger", "HttpRouting"))
                .addMember("path = %S", routingValues["path"]!!)
                .addMember("methods = %L", routingValues["methods"]!!)
                .addMember("authLevel = %L", routingValues["authLevel"]!!)
                .build()
            newFunction.addAnnotation(httpRoutingAnnotationSpec)
            newFunction.addParameters(createParameterSpecs(func))
            newFunction.returns(func.returnType?.toTypeName()!!)
            val taggedAnnotationClassName = ClassName("cn.rtast.kazure.trigger.internal", "KspTaggedRouting")
            val taggedAnnotation = AnnotationSpec.builder(taggedAnnotationClassName).build()
            newFunction.addAnnotation(taggedAnnotation)
            val providerKSType = func.annotations.toList().first {
                val fqName = it.annotationType.resolve().declaration.qualifiedName?.asString()
                fqName == "cn.rtast.kazure.auth.AuthConsumer"
            }.arguments.first().value as KSType
            val providerLiteralType = when {
                providerKSType.isDirectSubclassOf("cn.rtast.kazure.auth.provider.BasicAuthorizationProvider") -> "Basic"
                providerKSType.isDirectSubclassOf("cn.rtast.kazure.auth.provider.BearerAuthorizationProvider") -> "Bearer"
                else -> throw IllegalStateException("Unknown auth provider type ${providerKSType.declaration.qualifiedName?.asString()}")
            }
            val firstParamName = func.parameters.first().name!!.asString()
            val secondParamName = func.parameters[1].name!!.asString()
            val allParamName = func.parameters.joinToString(", ") { it.name!!.asString() }
            newFunction.addCode(
                """
                |fun u() = $firstParamName.respondText("UNAUTHORIZED", status = HttpStatus.UNAUTHORIZED)
                |val c = cn.rtast.kazure.auth.provider.func.__get${providerLiteralType}Credential($firstParamName) ?: return u()
                |val r = ${providerKSType.declaration.qualifiedName!!.asString()}.verify($firstParamName, $secondParamName, c)
                |return if (!r) u() else with(c) { ${func.qualifiedName?.asString()}($allParamName) }
                """.trimMargin()
            )
            fileSpecBuilder.addFunction(newFunction.build())
        }

        val fileSpec = fileSpecBuilder.build()
        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "kazure.generated",
            "KAzureGenerated"
        )
        file.write(fileSpec.toString().toByteArray())
        return emptyList()
    }
}