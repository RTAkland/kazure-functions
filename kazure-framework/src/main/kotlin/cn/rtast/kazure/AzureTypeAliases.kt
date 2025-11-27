/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


package cn.rtast.kazure

public typealias HttpRequest<T> = com.microsoft.azure.functions.HttpRequestMessage<T>
public typealias HttpContext = com.microsoft.azure.functions.ExecutionContext
public typealias HttpResponse = com.microsoft.azure.functions.HttpResponseMessage
public typealias HttpResponseBuilder = com.microsoft.azure.functions.HttpResponseMessage.Builder
public typealias Param = com.microsoft.azure.functions.annotation.BindingName
public typealias HttpStatus = com.microsoft.azure.functions.HttpStatus
public typealias HttpMethod = com.microsoft.azure.functions.HttpMethod