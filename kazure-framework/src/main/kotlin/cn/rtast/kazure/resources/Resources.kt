/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.resources

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.jar.JarFile

internal fun readFileFromJar(path: String): ByteArray =
    readFileFromJarAsStream(path).use { it.readBytes() }

internal fun readFileFromJarAsStream(path: String): InputStream {
    val jarPath = File(".").listFiles()!!.find { it.extension == "jar" }!!.toString()
    val jarFile = JarFile(jarPath)
    val entry = jarFile.getJarEntry(path) ?: throw FileNotFoundException("File $path not found in $jarPath")
    return jarFile.getInputStream(entry)
}

/**
 * Loads resource files from the JAR
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

    /**
     * Read as [InputStream]
     */
    public fun readAsInputStream(path: String): InputStream = readFileFromJarAsStream(path)
}

