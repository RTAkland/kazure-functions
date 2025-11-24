/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package test

import cn.rtast.kazure.v2.createRouting
import cn.rtast.kazure.v2.respondText


fun main() {
    val a by createRouting<Any>("/api/") {
        respondText("111")
    }
//    createRouting<Any>("/api/hello") {
//        respondText("")
//    }
}

/**
 * public fun <T> createRouting(
 *     route: String,
 *     methods: Array<HttpMethod> = arrayOf(HttpMethod.GET),
 *     authLevel: AuthLevel = AuthLevel.ANONYMOUS,
 *     bindings: Array<String> = arrayOf(),
 *     block: HttpRoutingContext<T>.() -> HttpResponse,
 * ): Nothing = throw AssertionError()
 *
 * 我会给用户这么一个函数，用户调用后我值接visitCall,获取用户调用传入的参数，然后生成一个新的函数
 *
 * public fun sgdaiudsbhaskjdnjka(): Nothing = createRouting<Any>("/api/hello") {
 *     respondText("Hi")
 * }
 *
 * public fun gen_sgdaiudsbhaskjdnjka(
 *     @HttpTrigger(
 *         name = "req",
 *         dataType = "",
 *         route = "/api/hello",
 *         methods = [HttpMethod.GET],
 *         authLevel = AuthorizationLevel.ANONYMOUS
 *     ) req: HttpRequestMessage<Any>,
 *     ctx: ExecutionContext,
 * ) {
 *
 * }
 * 第一个函数是用户写的源代码，第二个函数是生成的函数
 */