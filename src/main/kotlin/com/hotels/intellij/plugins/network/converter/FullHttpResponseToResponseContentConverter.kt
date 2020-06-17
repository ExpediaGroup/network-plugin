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
package com.hotels.intellij.plugins.network.converter

import com.google.common.base.Strings
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import io.netty.handler.codec.http.FullHttpResponse

/**
 * Convert the [ByteBuf] containing the response into a [String].
 */
class FullHttpResponseToResponseContentConverter(
        private val defaultByteBufToStringConverter: DefaultByteBufToStringConverter,
        private val lz4ByteBufToStringConverter: LZ4ByteBufToStringConverter
) {

    /**
     * Convert the [ByteBuf] containing the response into a [String] considering the response's content encoding and content type.
     *
     * @param fullHttpResponse [FullHttpResponse]
     * @return [String]
     */
    fun convert(fullHttpResponse: FullHttpResponse): String {
        val contentEncoding = fullHttpResponse.headers()[CONTENT_ENCODING_HEADER]

        val content: String
        content = if (!Strings.isNullOrEmpty(contentEncoding)) {
            getContentByContentEncoding(contentEncoding, fullHttpResponse)
        } else {
            defaultByteBufToStringConverter.convert(fullHttpResponse.content())
        }

        val contentType = fullHttpResponse.headers()[CONTENT_TYPE_HEADER]
        return if (!Strings.isNullOrEmpty(contentType)) {
            getContentByContentType(contentType, content)
        } else {
            content
        }
    }

    private fun getContentByContentEncoding(contentEncoding: String, fullHttpResponse: FullHttpResponse): String {
        return when (contentEncoding) {
            LZ4_CONTENT_ENCODING -> lz4ByteBufToStringConverter.convert(fullHttpResponse.content())
            else -> defaultByteBufToStringConverter.convert(fullHttpResponse.content())
        }
    }

    private fun getContentByContentType(contentType: String, content: String): String {
        return if (contentType.contains(CONTENT_TYPE_JSON)) {
            try {
                GSON.toJson(JsonParser.parseString(content))
            } catch (e: JsonParseException) {
                content
            }
        } else {
            content
        }
    }

    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().create()
        private const val CONTENT_ENCODING_HEADER = "Content-Encoding"
        private const val CONTENT_TYPE_HEADER = "Content-Type"
        private const val CONTENT_TYPE_JSON = "json"
        private const val LZ4_CONTENT_ENCODING = "lz4-block"
    }

}