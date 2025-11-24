/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.processor.v2

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.FileSpec

class KAzureProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var invoked = false
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true
        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "kazure.v2.generated",
            "azure_functions_generated"
        )
        val fileSpec = FileSpec.builder("kazure.v2.generated", "azure_functions_generated")

        val properties = resolver.getSymbolsWithAnnotation("cn.rtast.kazure.v2.routing.HttpRouting")
            .filterIsInstance<KSPropertyDeclaration>()
        properties.forEach {
            val delegate = it.getter!!
        }
        return emptyList()
    }
}