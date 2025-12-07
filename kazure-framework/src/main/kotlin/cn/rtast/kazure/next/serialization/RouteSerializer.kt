/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/7
 */


package cn.rtast.kazure.next.serialization

import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure
import cn.rtast.kazure.next.RequestHandler
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object HandlerSerializer : KSerializer<RequestHandler<*>> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("HandlerAsString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: RequestHandler<*>) {
        encoder.encodeString(value::class.qualifiedName!!)
    }

    override fun deserialize(decoder: Decoder): RequestHandler<*> {
        TODO("Not yet implemented")
    }
}

public object AuthProviderSerializer : KSerializer<AuthorizationConfigure<out BaseCredential>> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("AuthProviderAsString", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: AuthorizationConfigure<out BaseCredential>,
    ) {
        encoder.encodeString(value::class.qualifiedName!!)
    }

    override fun deserialize(decoder: Decoder): AuthorizationConfigure<out BaseCredential> {
        TODO("Not yet implemented")
    }
}

