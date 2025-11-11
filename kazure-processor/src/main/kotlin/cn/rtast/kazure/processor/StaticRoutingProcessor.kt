/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/11/25
 */


package cn.rtast.kazure.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import java.util.*

class StaticRoutingProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true
        val staticAssets = resolver.getSymbolsWithAnnotation("cn.rtast.kazure.resources.StaticAssets")
            .filterIsInstance<KSPropertyDeclaration>()
        val entries = mutableMapOf<String, String>()
        for (s in staticAssets) {
            if (s.type.resolve().declaration.qualifiedName?.asString() == "kotlin.ByteArray") {
                val annotation = s.annotations.find {
                    it.annotationType.resolve().declaration.qualifiedName?.asString() == "cn.rtast.kazure.resources.StaticAssets"
                }!!
                val routeArgValue = annotation.arguments.find { it.name?.asString() == "route" }!!.value as String
                val resArg = annotation.arguments.find { it.name?.asString() == "res" }!!.value as String
                val resArgValue = if (resArg == $$"$DEFAULT") routeArgValue else resArg
                entries[routeArgValue] = resArgValue
            } else {
                logger.error("StaticAssets type must be ByteArray -> ${s.qualifiedName?.asString()}")
                continue
            }
        }
        val fileContent = StringBuilder().apply {
            appendLine("package kazure.generated.a.b.c.d.e.f.g.h.i.j.k")
            appendLine("import cn.rtast.kazure.response.respondBytes")
        }
        entries.forEach { (route, res) ->
            val funcName = "assets_${UUID.randomUUID().toString().replace("-", "")}".substring(0, 12)
            val functionBody = """
                |@cn.rtast.kazure.trigger.HttpRouting("$route")
                |public fun $funcName
                |(req: com.microsoft.azure.functions.HttpRequestMessage<kotlin.Any?>,
                |ctx: com.microsoft.azure.functions.ExecutionContext): com.microsoft.azure.functions.HttpResponseMessage
                |= req.respondBytes(cn.rtast.kazure.resources.Resources.readBytes("$res"))
                """.trimMargin()
            fileContent.appendLine(functionBody)
        }
        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "kazure.generated.a.b.c.d.e.f.g.h.i.j.k",
            "staticAssets"
        )
        file.write(fileContent.toString().toByteArray())
        return emptyList()
    }
}