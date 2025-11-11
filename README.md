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
| `2.2.21` | `1.2.3-2.2.21` |
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

### Apply the gradle plugin

```kotlin
plugins {
    id("kazure") version "<version>"
    id("com.google.devtools.ksp") version "2.2.21-2.0.4"
    id("com.microsoft.azure.azurefunctions") version "1.16.1"  // It is best to use the latest version.
}

// Enable context-parameter for auth provider Required!!!
kotlin {
    jvmToolchain(11)
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xcontext-parameters"))
    }
}
```

### Configure azure plugin

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
    "FUNCTIONS_WORKER_RUNTIME": "java",
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

// Make sure your auth provider class is object class
object Basic1AuthProvider : BasicAuthorizationProvider {
    override fun verify(
        request: HttpRequest<*>,
        context: HttpContext,
        credential: BasicCredential,
    ): Boolean {
        return (credential.username == "admin" && credential.password == "123")
    }
}

// Using authorization provider
context(cred: BasicCredential)
@AuthConsumer(Basic1AuthProvider::class)
@HttpRouting("/time3/{name}", methods = [HttpMethod.POST])
fun myf(req: HttpRequest, ctx: HttpContext, @Param("name") name: String): HttpResponse {
    return req.respondText("Hello ${cred.username} at $name")
}
```

## Run functions locally

In gradle, run task: `azurefunctions:azureFunctionsRun`