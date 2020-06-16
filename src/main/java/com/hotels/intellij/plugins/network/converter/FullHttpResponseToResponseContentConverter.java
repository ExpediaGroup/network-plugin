/*
 * Copyright 2017 Expedia Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.intellij.plugins.network.converter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Convert the {@link ByteBuf} containing the response into a {@link String}.
 */
public class FullHttpResponseToResponseContentConverter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "json";
    private static final String LZ4_CONTENT_ENCODING = "lz4-block";

    private DefaultByteBufToStringConverter defaultByteBufToStringConverter;
    private LZ4ByteBufToStringConverter lz4ByteBufToStringConverter;

    /**
     * Constructor.
     *
     * @param defaultByteBufToStringConverter {@link DefaultByteBufToStringConverter}
     * @param lz4ByteBufToStringConverter {@link LZ4ByteBufToStringConverter}
     */
    public FullHttpResponseToResponseContentConverter(DefaultByteBufToStringConverter defaultByteBufToStringConverter,
                                                      LZ4ByteBufToStringConverter lz4ByteBufToStringConverter) {
        this.defaultByteBufToStringConverter = defaultByteBufToStringConverter;
        this.lz4ByteBufToStringConverter = lz4ByteBufToStringConverter;
    }

    /**
     * Convert the {@link ByteBuf} containing the response into a {@link String} considering the response's content encoding and content type.
     *
     * @param fullHttpResponse {@link FullHttpResponse}
     * @return {@link String}
     */
    public String convert(FullHttpResponse fullHttpResponse) {
        String contentEncoding = fullHttpResponse.headers().get(CONTENT_ENCODING_HEADER);

        String content;
        if (!Strings.isNullOrEmpty(contentEncoding)) {
            content = getContentByContentEncoding(contentEncoding, fullHttpResponse);
        } else {
            content = defaultByteBufToStringConverter.convert(fullHttpResponse.content());
        }

        String contentType = fullHttpResponse.headers().get(CONTENT_TYPE_HEADER);
        if (!Strings.isNullOrEmpty(contentType)) {
            return getContentByContentType(contentType, content);
        } else {
            return content;
        }
    }

    private String getContentByContentEncoding(String contentEncoding, FullHttpResponse fullHttpResponse) {
        switch (contentEncoding) {
            case LZ4_CONTENT_ENCODING:
                return lz4ByteBufToStringConverter.convert(fullHttpResponse.content());
            default:
                return defaultByteBufToStringConverter.convert(fullHttpResponse.content());
        }
    }

    private String getContentByContentType(String contentType, String content) {
        if (contentType.contains(CONTENT_TYPE_JSON)) {
            try {
                return GSON.toJson(JSON_PARSER.parse(content));
            } catch (JsonParseException e) {
                return content;
            }
        } else {
            return content;
        }
    }
}
