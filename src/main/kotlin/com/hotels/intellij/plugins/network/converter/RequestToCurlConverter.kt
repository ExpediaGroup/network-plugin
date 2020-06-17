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

import java.util.function.Consumer

/**
 * Creates an executable cURL request from the original request.
 */
class RequestToCurlConverter {
    /**
     * Create an executable cURL request from the original request.
     *
     * @param uri            [String]
     * @param method         [String]
     * @param requestHeaders [Map]
     * @param requestContent [String]
     * @return [String]
     */
    fun convert(uri: String, method: String?, requestHeaders: Map<String, String>, requestContent: String): String {
        return when (method) {
            "GET" -> convertGet(requestHeaders, uri)
            "POST" -> convertPost(requestHeaders, requestContent, uri)
            else -> ""
        }
    }

    private fun convertGet(requestHeaders: Map<String, String>, uri: String): String {
        val stringBuilder = StringBuilder("curl -X GET \\\n")
        requestHeaders.entries.forEach(Consumer { entry: Map.Entry<String, String> -> addHeaderTo(stringBuilder, entry) })
        stringBuilder.append("\"")
        stringBuilder.append(uri)
        stringBuilder.append("\"")
        return stringBuilder.toString()
    }

    private fun convertPost(requestHeaders: Map<String, String>, requestContent: String, uri: String): String {
        val stringBuilder = StringBuilder("curl -X POST \\\n")
        requestHeaders.entries.forEach(Consumer { entry: Map.Entry<String, String> -> addHeaderTo(stringBuilder, entry) })
        stringBuilder.append("-d \"")
        stringBuilder.append(requestContent.replace("\"", "\\\""))
        stringBuilder.append("\" \\\n")
        stringBuilder.append("\"")
        stringBuilder.append(uri)
        stringBuilder.append("\"")
        return stringBuilder.toString()
    }

    private fun addHeaderTo(stringBuilder: StringBuilder, entry: Map.Entry<String, String>) {
        stringBuilder.append("-H \"")
        stringBuilder.append(entry.key)
        stringBuilder.append(": ")
        stringBuilder.append(entry.value)
        stringBuilder.append("\" \\\n")
    }
}