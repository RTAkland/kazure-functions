/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/4
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kazure.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class KAzureProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var invoked = false

    private val bindingName = ClassName("com.microsoft.azure.functions.annotation", "BindingName")
    private val httpTrigger = ClassName("com.microsoft.azure.functions.annotation", "HttpTrigger")

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true
        val fileSpecBuilder = FileSpec.builder("kazure.generated", "KAzureGenerated")
            .addImport("com.microsoft.azure.functions", "HttpStatus")
            .addImport("cn.rtast.kazure.response", "respondText")
            .addImport("cn.rtast.kazure.routes._RouteProcessorKt", "___processRoute")
        val functions = resolver.getSymbolsWithAnnotation(ROUTING_FQ_NAME)
            .filterIsInstance<KSPropertyDeclaration>()
            .forEach {
                val funSpec = FunSpec.builder("f_${Uuid.random().toString().replace("-", "")}")
                    .addModifiers(KModifier.PUBLIC)
                val httpTriggerAnnotation = AnnotationSpec.builder(httpTrigger)
                    .addMember("name = %S", "req")
                    .addMember("dataType = %S", "")
                    .addMember("route = ")
                ((it.annotations.first { an -> an.shortName.asString() == "Routing" }
                    .arguments.firstOrNull { arg -> arg.name?.asString() == "params" }?.value as? Array<*>)
                    ?.filterIsInstance<String>()?.toList() ?: emptyList())
                    .forEach { p ->
                        val asp = AnnotationSpec.builder(bindingName)
                            .addMember("%S", p).build()
                        val paramSpec = ParameterSpec.builder(p, ClassName("kotlin", "String"))
                            .addAnnotation(asp).build()
                        funSpec.addParameter(paramSpec)

                    }

                val bindingNameAnnotations = AnnotationSpec.builder(bindingName)
                    .addMember("%S")


            }

        return emptyList()
    }
}