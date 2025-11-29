/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.resources

import cn.rtast.kazure.resources.kembed.Chunk
import cn.rtast.kazure.resources.kembed.Resource
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
@Deprecated("This API is unstable, not recommended to use.")
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

