/**
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
package com.hotels.intellij.plugins.network.converter;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.intellij.openapi.diagnostic.Logger;
import io.netty.buffer.ByteBuf;
import net.jpountz.lz4.LZ4BlockInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Convert the {@link ByteBuf} containing the response compressed in LZ4 into a {@link String}.
 */
public class LZ4ByteBufToStringConverter {

    private static final Logger LOGGER = Logger.getInstance(LZ4ByteBufToStringConverter.class);

    /**
     * Convert the {@link ByteBuf} containing the response compressed in LZ4 into a {@link String}.
     * Reset the reader index after reading the content from the {@link ByteBuf}.
     *
     * @param content {@link ByteBuf}
     * @return {@link String}
     */
    public String convert(ByteBuf content) {
        byte[] contentBytes = new byte[content.readableBytes()];
        content.readBytes(contentBytes);
        content.readerIndex(0);

        return new String(decompress(contentBytes), UTF_8);
    }

    private byte[] decompress(byte[] input) {
        byte[] buffer = new byte[64000];
        try (InputStream inputStream = new LZ4BlockInputStream(new ByteArrayInputStream(input));
             ByteArrayOutputStream uncompressedStream = new ByteArrayOutputStream()) {
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                uncompressedStream.write(buffer, 0, bytesRead);
            }
            return uncompressedStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Failed to decompress lz4-block", e);
        }
        return new byte[0];
    }
}
