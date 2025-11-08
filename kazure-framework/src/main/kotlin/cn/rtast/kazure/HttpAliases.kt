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
import java.util.*

public typealias HttpRequest = HttpRequestMessage<Optional<String>>

public typealias HttpContext = ExecutionContext

public typealias HttpResponse = HttpResponseMessage

public typealias Param = BindingName