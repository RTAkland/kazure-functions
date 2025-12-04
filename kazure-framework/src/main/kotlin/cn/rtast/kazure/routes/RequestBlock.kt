/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/4
 */


package cn.rtast.kazure.routes

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse

public typealias RequestBlock<T, C> = HttpRequest<T>.(RequestContext<C>) -> HttpResponse
