/*
 * Copyright 2017 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.intellij.plugins.network.converter

import com.intellij.openapi.diagnostic.Logger
import io.netty.buffer.ByteBuf
import net.jpountz.lz4.LZ4BlockInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Convert the [ByteBuf] containing the response compressed in LZ4 into a [String].
 */
class LZ4ByteBufToStringConverter {
    /**
     * Convert the [ByteBuf] containing the response compressed in LZ4 into a [String].
     * Reset the reader index after reading the content from the [ByteBuf].
     *
     * @param content [ByteBuf]
     * @return [String]
     */
    fun convert(content: ByteBuf): String {
        val contentBytes = ByteArray(content.readableBytes())
        content.readBytes(contentBytes)
        content.readerIndex(0)
        return String(decompress(contentBytes)!!, Charset.forName("UTF-8"))
    }

    private fun decompress(input: ByteArray?): ByteArray? {
        var result: ByteArray? = null
        if (input != null) {
            val inputStream: InputStream = LZ4BlockInputStream(ByteArrayInputStream(input))
            val buffer = ByteArray(64000)
            val uncompressedStream = ByteArrayOutputStream()

            var bytesRead: Int
            try {
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    uncompressedStream.write(buffer, 0, bytesRead)
                }
            } catch (e: IOException) {
                LOGGER.error("Failed to decompress lz4-block", e)
            }

            result = uncompressedStream.toByteArray()
        }
        return result
    }

    companion object {
        private val LOGGER = Logger.getInstance(LZ4ByteBufToStringConverter::class.java)
    }
}