/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


@file:OptIn(SymbolInternals::class)

package cn.rtast.kazure.compiler.v2.fir

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirNamedArgumentExpression
import org.jetbrains.kotlin.fir.references.toResolvedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.visitors.FirVisitor

class FirKAzureFunctionsVisitor : FirVisitor<Unit, Unit>() {

    override fun visitFunctionCall(functionCall: FirFunctionCall, data: Unit) {
        val funcSymbol = functionCall.calleeReference.toResolvedFunctionSymbol()!!

        if (funcSymbol.callableId.asSingleFqName().asString() == "cn.rtast.kazure.v2.RoutingRegistrarKt.createRouting") {
            val params = funcSymbol.fir.valueParameters
            val args = functionCall.argumentList.arguments
            val paramMap = mutableMapOf<String, FirExpression?>()
            var positionalIndex = 0
            for (arg in args) {
                if (arg is FirNamedArgumentExpression) {
                    paramMap[arg.name.asString()] = arg.expression
                } else {
                    if (positionalIndex < params.size) {
                        paramMap[params[positionalIndex].name.asString()] = arg
                        positionalIndex++
                    }
                }
            }
            for (param in params) {
                val name = param.name.asString()
                if (!paramMap.containsKey(name)) {
                    paramMap[name] = param.defaultValue
                }
            }
            
        }
    }

    override fun visitElement(element: FirElement, data: Unit) {

    }
}