/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import java.util.Locale.getDefault

class KAzureProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private fun KSAnnotated.hasAnno(fq: String): Boolean =
        annotations.any {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == fq
        }

    private fun KSAnnotated.getAnnotationParameterValues(fqName: String): Map<String, Any?>? {
        val anno = annotations.firstOrNull {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == fqName
        } ?: return null
        val result = mutableMapOf<String, Any?>()
        for (arg in anno.arguments) {
            val name = arg.name?.asString() ?: continue
            val value = arg.value
            result[name] = value
        }
        return result
    }

    private fun KSFunctionDeclaration.getAllPathParams(): List<Pair<String, KSType>> {
        return annotations.filter {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == PathParamFqName
        }.mapNotNull { anno ->
            val nameArg = anno.arguments.firstOrNull { it.name?.asString() == "name" }?.value as? String
            val typeArg = anno.arguments.firstOrNull { it.name?.asString() == "type" }?.value as? KSType
            if (nameArg != null && typeArg != null) {
                nameArg to typeArg
            } else null
        }.toList()
    }

    private fun isValidKotlinIdentifier(name: String): Boolean {
        val regex = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")
        return regex.matches(name)
    }

    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true
        val candidates = resolver.getSymbolsWithAnnotation(HttpRoutingFqName)
            .filterIsInstance<KSFunctionDeclaration>()
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies.ALL_FILES,
            packageName = "kazure.generated",
            fileName = "KAzureGenerated"
        )
        val fileContentBuilder = StringBuilder().apply {
            appendLine("package kazure.generated")
        }

        for (func in candidates) {
            if (!func.hasAnno(PathParamFqName)) continue
            val httpPathValue = func.getAnnotationParameterValues(HttpRoutingFqName)!!["path"] as String
            val pathParams = func.getAllPathParams()
            val parent = (func.parent as KSClassDeclaration)
            val clsFqName = parent.qualifiedName!!.asString()
            pathParams.forEach { (n, t) ->
                val fullExtName = "$n${
                    parent.simpleName.asString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
                }"
                val finalFullExtName = if (isValidKotlinIdentifier(fullExtName)) fullExtName else "`$fullExtName`"
                fileContentBuilder.apply {
                    appendLine("public val $clsFqName.$finalFullExtName")
                    appendLine("get(): ${t.declaration.qualifiedName?.asString() ?: "kotlin.Any"}")
                    appendLine("= cn.rtast.kazure.util.extractPathParams(\"$httpPathValue\", this.__request!!.uri.toString(), \"$fullExtName\")")
                    appendLine()
                }
            }
        }
        file.write(fileContentBuilder.toString().encodeToByteArray())
        return emptyList()
    }
}