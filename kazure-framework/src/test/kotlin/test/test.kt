/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package test

import com.azure.core.util.BinaryData
import com.azure.messaging.webpubsub.client.WebPubSubClientBuilder
import com.azure.messaging.webpubsub.client.models.WebPubSubDataType

fun main() {
    val client = WebPubSubClientBuilder()
        .clientAccessUrl("wss://ronebot.webpubsub.azure.com/client/hubs/Hub?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ3c3M6Ly9yb25lYm90LndlYnB1YnN1Yi5henVyZS5jb20vY2xpZW50L2h1YnMvSHViIiwiaWF0IjoxNzYzMTI2NDA3LCJleHAiOjE3NjMxMzAwMDcsInJvbGUiOlsid2VicHVic3ViLnNlbmRUb0dyb3VwIiwid2VicHVic3ViLmpvaW5MZWF2ZUdyb3VwIl0sInN1YiI6IjExNDUxNCJ9.45p3hOWivdxQN2rtEPuM02fqhL7PsDiQH5mQgbp_nPg")
        .buildClient()
    client.start()
    client.sendEvent("Hub", BinaryData.fromString("test"), WebPubSubDataType.TEXT)
}