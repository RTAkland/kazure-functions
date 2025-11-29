/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/23/25
 */


package cn.rtast.kazure.kembed

import java.io.File

fun clearDirectory(dir: File) {
    if (!dir.exists() || !dir.isDirectory) return
    dir.listFiles()?.forEach { file ->
        if (file.isDirectory) {
            clearDirectory(file)
            file.delete()
        } else {
            file.delete()
        }
    }
}