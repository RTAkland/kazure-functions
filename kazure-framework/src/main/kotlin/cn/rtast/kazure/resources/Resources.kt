/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.resources

import java.io.File
import java.util.jar.JarFile
import kotlin.reflect.KProperty

internal fun readFileFromJar(filePathInJar: String): ByteArray {
    val jarPath = File(".").listFiles()!!.find { it.extension == "jar" }!!.toString()
    JarFile(jarPath).use { jarFile ->
        val entry = jarFile.getJarEntry(filePathInJar)
            ?: throw IllegalArgumentException("File $filePathInJar not found in $jarPath")
        jarFile.getInputStream(entry).use { inputStream ->
            return inputStream.readBytes()
        }
    }
}

/**
 * Utilities to load resource files from the JAR
 *
 * Provides methods to read resource content as text or bytes
 */
public object Resources {
    /**
     * Read plain text content from jar
     */
    public fun readText(path: String): String = readFileFromJar(path).decodeToString()

    /**
     * Read raw bytes content from jar
     */
    public fun readBytes(path: String): ByteArray = readFileFromJar(path)
}

/**
 * Property delegate to load resource files by property name and type
 *
 * Usage example:
 * ```
 * // variable name can not contains "." character
 * val `indexHtml`: String by resources
 * val `test-contentBin`: ByteArray by resources
 * // Use this way
 * val `test-content`: ByteArray by resources("test-content.bin")
 * ```
 *
 * The property name is used as the resource path
 * The property type decides which method from [Resources] is called:
 * - [String] properties will load text content
 * - [ByteArray] properties will load raw bytes
 *
 * @throws IllegalArgumentException if type is not [ByteArray] or [String]
 */
@Suppress("ClassName")
public object resources {
    public class ResourcesDelegate(public val resourceName: String?) {
        public inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
            val name = resourceName ?: property.name
            return when (T::class) {
                ByteArray::class -> Resources.readBytes(name) as T
                String::class -> Resources.readText(name) as T
                else -> throw IllegalArgumentException("Unsupported property type ${T::class}")
            }
        }
    }
    public inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
        return ResourcesDelegate(null).getValue(thisRef, property)
    }

    public operator fun invoke(resourceName: String): ResourcesDelegate = ResourcesDelegate(resourceName)
}