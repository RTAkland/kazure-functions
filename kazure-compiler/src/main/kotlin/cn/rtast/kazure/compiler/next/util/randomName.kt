/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kazure.compiler.next.util

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val randomName get() = Uuid.random().toString().replace("-", "").substring(0, 10)