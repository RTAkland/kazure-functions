/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


package cn.rtast.kazure

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.annotation.BindingName

public typealias HttpRequest<T> = HttpRequestMessage<T>

public typealias HttpContext = ExecutionContext

public typealias HttpResponse = HttpResponseMessage

public typealias HttpResponseBuilder = HttpResponseMessage.Builder

public typealias Param = BindingName

public typealias HttpStatus = com.microsoft.azure.functions.HttpStatus

public typealias HttpMethod = com.microsoft.azure.functions.HttpMethod