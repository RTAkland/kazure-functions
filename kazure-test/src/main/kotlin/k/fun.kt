/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package k

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.credentials.HeaderCredential
import cn.rtast.kazure.resources.StaticAssets
import cn.rtast.kazure.resources.staticAssets
import cn.rtast.kazure.response.receive
import cn.rtast.kazure.response.respond
import cn.rtast.kazure.response.respondText
import cn.rtast.kazure.trigger.HttpRouting
import kotlinx.serialization.Serializable

//class MyFunction : HttpAzureFunction<String>() {
//    //    @PathParam("name", String::class)
//    @AuthConsumer("bs")
//    @HttpRouting("time")
//    override fun httpEntrypoint(
//        request: HttpRequest<String>,
//        context: HttpContext,
//    ): HttpResponse {
//        val name = "RTAkladsand"
//        val res = Resources.readText("-s.dsadatxt")
//        val ssss = "21231"
//        return request.respondText(name + res + ssss)
//    }
//}
//
//
//val a: String by resources
//
//val testContent: String by resources("test/test-content.txt")
//
////context(cred: BasicCredential)
//@HttpRouting("hello")
////@AuthConsumer(Basic1AuthProvider::class)
//fun index(req: HttpRequest<String?>, ctx: HttpContext): HttpResponse {
////    return req.respondText("Hello" + cred.username + " at $s")
//    return req.respondText("$a|$testContent")
//}
//
//context(cred: BasicCredential)
//@HttpRouting("hello/auth/{s}")
//@AuthConsumer(Basic1AuthProvider::class)
//fun indexWithAuth(req: HttpRequest<String?>, ctx: HttpContext, @Param("s") s: String): HttpResponse {
//    return req.respondText("Hello" + cred.username + " at $s")
//}
//
@HttpRouting("hello/auth2")
@AuthConsumer(Basic1AuthProvider::class)
context(cred: BasicCredential)
fun indexWithAuth1(req: HttpRequest, ctx: HttpContext): HttpResponse {
    return req.respondText("Hello" + cred.username)
}

//
@HttpRouting("hello/auth3", [HttpMethod.POST])
@AuthConsumer(Tok::class)
context(cred: HeaderCredential)
fun indexWithAuth2(req: HttpRequest, ctx: HttpContext): HttpResponse {
//    return req.respondText("Hello" + cred.token)
    val a = req.receive<String>()
    ctx.logger.info(a)
    return req.respond(Test("ssss"))
}

@Serializable
data class Test(val a: String)

//fun main() {
//    with(BasicCredential("", "")) {
//        index()
//    }
//}

@StaticAssets("c", "test/a")
val c: ByteArray by staticAssets
//
//@StaticAssets("d", "a")
//val d: ByteArray by staticAssets

@HttpRouting("/hello312")
fun indexWithAuth3(req: HttpRequest, ctx: HttpContext): HttpResponse {
    return req.respondText("Hello")
}