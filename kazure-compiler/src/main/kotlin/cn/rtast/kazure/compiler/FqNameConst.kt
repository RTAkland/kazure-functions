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

/**
 * 0 timer
 * 2 String schedule
 */
val TimerTriggerFqName = "com.microsoft.azure.functions.annotation.TimerTrigger".fqName

/**
 * 0 eh
 * 2 String eventHubName = ""
 * 3 Cardinality cardinality = Cardinality.MANY
 * 4 String consumerGroup = "$Default"
 * 5 String connection
 */
val EventHubTriggerFqName = "com.microsoft.azure.functions.annotation.EventHubTrigger".fqName

/**
 * 0 blob
 * 2 String path
 * 3 String connection = ""
 * 4 String source = ""
 */
val BlobTriggerFqName = "com.microsoft.azure.functions.annotation.BlobTrigger".fqName

/**
 * 0 queue
 * 2 String queueName
 * 3 String connection = ""
 */
val QueueTriggerFqName = "com.microsoft.azure.functions.annotation.QueueTrigger".fqName

val AuthorizationLevelFqName = "com.microsoft.azure.functions.annotation.AuthorizationLevel".fqName
val HttpMethodFqName = "com.microsoft.azure.functions.HttpMethod".fqName
val CardinalityFqName = "com.microsoft.azure.functions.annotation.Cardinality".fqName

val AzureFunctionFqName = "cn.rtast.kazure.functions.AzureFunction".fqName
val HttpAzureFunctionFqName = "cn.rtast.kazure.functions.HttpAzureFunction".fqName
val EventHubAzureFunctionFqName = "cn.rtast.kazure.functions.EventHubAzureFunction".fqName
val BlobAzureFunctionFqName = "cn.rtast.kazure.functions.BlobAzureFunction".fqName
val QueueAzureFunctionFqName = "cn.rtast.kazure.functions.QueueAzureFunction".fqName
val TimerAzureFunctionFqName = "cn.rtast.kazure.functions.TimerAzureFunction".fqName

val HttpRoutingFqName = "cn.rtast.kazure.trigger.HttpRouting".fqName
val EventHubRoutingFqName = "cn.rtast.kazure.trigger.EventHubRouting".fqName
val BlobRoutingFqName = "cn.rtast.kazure.trigger.BlobRouting".fqName
val QueueRoutingFqName = "cn.rtast.kazure.trigger.QueueRouting".fqName
val TimerRoutingFqName = "cn.rtast.kazure.trigger.TimerRouting".fqName

val HttpRequestMessageFqName = "com.microsoft.azure.functions.HttpRequestMessage".fqName

val BindingNameFqName = "com.microsoft.azure.functions.annotation.BindingName".fqName
