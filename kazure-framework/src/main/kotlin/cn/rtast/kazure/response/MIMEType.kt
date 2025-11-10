package cn.rtast.kazure.response

import java.io.File

/**
 * MIMEType code from kotless
 * https://github.com/JetBrains/kotless/blob/596c77f41410409207647dfd195e34f4562728ed/model/src/main/kotlin/io/kotless/HTTP.kt
 * LICENSE Apache2.0 https://github.com/JetBrains/kotless/blob/596c77f41410409207647dfd195e34f4562728ed/LICENSE.txt
 */
public enum class MIMEType(public val mimeText: String, public val isBinary: Boolean, public val extension: String) {
    PLAIN("text/plain", false, "txt"),
    MARKDOWN("text/markdown", false, "md"),
    HTML("text/html", false, "html"),
    CSS("text/css", false, "css"),

    PNG("image/png", true, "png"),
    APNG("image/apng", true, "apng"),
    GIF("image/gif", true, "gif"),
    SVG("image/svg", true, "svg"),
    JPEG("image/jpeg", true, "jpeg"),
    BMP("image/bmp", true, "bmp"),
    WEBP("image/webp", true, "webp"),
    TTF("font/ttf", true, "ttf"),

    JS("application/javascript", false, "js"),
    JSMAP("application/json", false, "map"),
    JSON("application/json", false, "json"),
    XML("application/xml", false, "xml"),
    ZIP("application/zip", true, "zip"),
    GZIP("application/gzip", true, "gzip");

    public companion object {
        public fun binary(): Array<MIMEType> = entries.filter { it.isBinary }.toTypedArray()
        public fun forDeclaration(type: String, subtype: String): MIMEType? = entries.find { "${type}/${subtype}" == it.mimeText }
        public fun forFile(file: File): MIMEType? = entries.find { it.extension == file.extension }
    }
}