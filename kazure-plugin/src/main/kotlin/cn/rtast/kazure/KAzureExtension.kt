/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/11/25
 */


package cn.rtast.kazure

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class KAzureExtension {
    @get:Input
    abstract val listingResources: Property<Boolean>

    @get:Input
    abstract val excludeFiles: ListProperty<String>

    abstract val resourceRoutingPrefix: Property<String>

    init {
        listingResources.convention(false)
        excludeFiles.convention(mutableListOf())
        resourceRoutingPrefix.convention("")
    }
}