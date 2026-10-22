/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


package cn.rtast.kazure.compiler

import cn.rtast.kazure.compiler.util.fqName


val FunctionNameFqName = "com.microsoft.azure.functions.annotation.FunctionName".fqName

/**
 * 0 req
 * 2 String route = ""
 * 3 HttpMethod[] methods = []
 * 4 AuthorizationLevel authLevel = AuthorizationLevel.FUNCTION(ANONYMOUS af)
 */
val HttpTriggerFqName = "com.microsoft.azure.functions.annotation.HttpTrigger".fqName

val AuthorizationLevelFqName = "com.microsoft.azure.functions.annotation.AuthorizationLevel".fqName
val HttpMethodFqName = "com.microsoft.azure.functions.HttpMethod".fqName

val HttpRoutingFqName = "cn.rtast.kazure.trigger.HttpRouting".fqName
val EventHubRoutingFqName = "cn.rtast.kazure.trigger.EventHubRouting".fqName
val BlobRoutingFqName = "cn.rtast.kazure.trigger.BlobRouting".fqName
val QueueRoutingFqName = "cn.rtast.kazure.trigger.QueueRouting".fqName
val TimerRoutingFqName = "cn.rtast.kazure.trigger.TimerRouting".fqName

