# KAzure

A simple azure functions framework

provide some simple functions to create routes

# Docs

Read [docs](docs/README.md) for more information

# Quickstart


> The latest release is may not the latest version

Kotlin versions and plugin version table

| Kotlin   | KAzure         |
|----------|----------------|
| `2.2.21` | `1.1.0-2.2.21` |
| `2.2.20` | `1.0.0-2.2.20` |

## Configure

Add maven repository

```kotlin
// build.gradle.kts
repositories {
    mavenCentral()
    maven("https://repo.maven.rtast.cn/releases")
}

// settings.gradle.kts

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.maven.rtast.cn/releases/")
    }
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

```kotlin
import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.Param
import cn.rtast.kazure.respondText
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.provider.BasicAuthorizationProvider

object Basic1AuthProvider : BasicAuthorizationProvider<Any> {
    override fun verify(
        request: HttpRequest<Any>,
        context: HttpContext,
        credential: BasicCredential?,
    ): Boolean {
        return credential?.let { credential.username == "RTAkland" && credential.password == "123" }
            ?: false
    }
}

// Using authorization provider
@AuthConsumer(Basic1AuthProvider::class)
@HttpRouting("/time3/{name}")
fun myf(req: HttpRequest, ctx: HttpContext, @Param("name") name: String): HttpResponse {
    return req.respondText(name)
}
```

## Run functions locally

In gradle, run task: `azurefunctions:azureFunctionsRun`