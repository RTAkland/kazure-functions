# Before start

Azure Functions has a Java runtime, so you can also use Kotlin to write function routes.

# Azure

When packaging or running functions via Gradle, Gradle scans the annotations in the code and generates corresponding JSON and entry points. This allows the worker to load them via reflection. The package is a zip/jar containing the app configuration, program core, and dependency libraries—in other words, a fat jar.

# Plugin

The plugin scans annotations and modifies bytecode at the IR phase through a compiler plugin, providing a more convenient, safe, and user-friendly API.

# Notes

When using it, try to avoid using reflection to load resources inside the jar. If necessary, you can access them via built-in APIs.

```kotlin
import cn.rtast.kazure.resources.Resources

fun test() {
    val plainText: String = Resources.readText("path/to/text/resources.txt")
    val bytesContent: ByteArray = Resoures.readBytes("path/to/bytes/resources.bin")
}
```