/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.util

import java.io.File
import java.util.jar.JarFile

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

public object Resources {
    public fun readText(path: String): String = readFileFromJar(path).decodeToString()
    public fun readBytes(path: String): ByteArray = readFileFromJar(path)
}