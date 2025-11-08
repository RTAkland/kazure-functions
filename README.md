# KAzure

A simple azure functions framework

provide some simple functions to create routes

# Quickstart

## Configure

Add maven repository

```kotlin
repositories {
    mavenCentral()
    maven("https://repo.maven.rtast.cn/releases")
}
```

Apply the gradle plugin

```kotlin
plugins {
    id("kazure") version "<version>"
    id("com.microsoft.azure.azurefunctions") version "1.16.1"  // It is best to use the latest version.
}
```

Configure azure plugin

> Before start please go to azure and create a functions app

```kotlin
azurefunctions {
    appName = "Your app name here"
}
```

> Create 2 files in root project path: `host.json` `local.settings.json`

host.json
```json
{
  "version": "2.0",
  "extensionBundle": {
    "id": "Microsoft.Azure.Functions.ExtensionBundle",
    "version": "[4.*, 5.0.0)"
  },
  "extensions": {
    "http": {
      "routePrefix": ""
    }
  }
}
```

---

local.settings.json
```json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "UseDevelopmentStorage=true",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "CosmosDBDatabaseName": "",
    "CosmosDBCollectionName": "",
    "AzureWebJobsCosmosDBConnectionString": "",
    "AzureWebJobsEventGridOutputBindingTopicUriString": "",
    "AzureWebJobsEventGridOutputBindingTopicKeyString": "",
    "AzureWebJobsEventHubSender": "",
    "AzureWebJobsEventHubSender_2": "",
    "BrokerList": "",
    "ConfluentCloudUsername": "",
    "ConfluentCloudPassword": "",
    "AzureWebJobsServiceBus": "",
    "SBTopicName": "",
    "SBTopicSubName": "",
    "SBQueueName": ""
  }
}
```

## Add http routing

> There's 2 ways to create http routing
```kotlin
// first, inherit HttpAzureFunction
// this way can not use path template

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.functions.HttpAzureFunction
import cn.rtast.kazure.respondResource
import cn.rtast.kazure.trigger.HttpRouting

class MyFunction : HttpAzureFunction() {
    @HttpRouting("time")
    override fun httpEntrypoint(
        request: HttpRequest,
        context: HttpContext,
    ): HttpResponse {
        println()
        return request.respondResource("test-content.txt")
    }

    @HttpRouting("time2")
    fun e2(request: HttpRequest, context: HttpContext): HttpResponse {
        return request.respondResource("test-content.txt")
    }
}
```

```kotlin
// second topLevel functions

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.Param
import cn.rtast.kazure.respondText
import cn.rtast.kazure.trigger.HttpRouting

@HttpRouting("/time3/{name}")
fun myf(req: HttpRequest, ctx: HttpContext, @Param("name") name: String): HttpResponse {
    return req.respondText(name)
}
```

## Run functions locally

In gradle, run task: `azurefunctions:azureFunctionsRun`