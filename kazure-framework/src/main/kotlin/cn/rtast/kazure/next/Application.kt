/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


package cn.rtast.kazure.next

/**
 * The Logic application
 */
public interface Application {
    /**
     * logic routes manager
     */
    public val routes: MutableList<RegisteredRouteSpec<*>>
}