/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/4
 */


package cn.rtast.kazure

public interface KAzureApplication


@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
public annotation class Routing(val params: Array<String> = [])