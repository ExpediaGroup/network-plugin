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

import com.hotels.intellij.plugins.network.domain.RequestResponse
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest

/**
 * Responsible for converting the netty objects into a domain object namely [RequestResponse].
 */
class NettyObjectsToRequestResponseConverter(
        private val defaultByteBufToStringConverter: DefaultByteBufToStringConverter,
        private val fullHttpResponseToResponseContentConverter: FullHttpResponseToResponseContentConverter,
        private val requestToCurlConverter: RequestToCurlConverter
) {

    /**
     * Gather and convert the request and response attributes into a [RequestResponse].
     *
     * @param response        [HttpObject]
     * @param originalRequest [HttpRequest]
     * @param timeInMillis    [Long]
     * @return [RequestResponse]
     */
    fun convert(response: HttpObject,
                originalRequest: HttpRequest,
                timeInMillis: Long): RequestResponse {
        val fullHttpRequest = if (originalRequest is FullHttpRequest) originalRequest else null
        val fullHttpResponse = if (response is FullHttpResponse) response else null

        val uri = originalRequest.uri()
        val method = originalRequest.method().name()
        val contentType = fullHttpResponse?.headers()?.get(HttpHeaderNames.CONTENT_TYPE) ?: ""
        val responseCode = fullHttpResponse?.status()?.code()?.toString() ?: ""
        val requestHeadersMap = getHeadersAsMap(originalRequest.headers())
        val requestContent = if (fullHttpRequest != null) defaultByteBufToStringConverter.convert(fullHttpRequest.content()) else ""
        val responseHeaders = if (fullHttpResponse != null) getHeadersAsMap(fullHttpResponse.headers()).toDisplayString() else ""
        val responseContent = if (fullHttpResponse != null) fullHttpResponseToResponseContentConverter.convert(fullHttpResponse) else ""

        return RequestResponse(
                url = uri,
                responseCode = responseCode,
                responseContentType = contentType,
                responseTime = timeInMillis,
                method = method,
                requestHeaders = requestHeadersMap.toDisplayString(),
                requestContent = requestContent,
                responseHeaders = responseHeaders,
                responseContent = responseContent,
                curlRequest = requestToCurlConverter.convert(uri, method, requestHeadersMap, requestContent))
    }

    private fun getHeadersAsMap(headers: HttpHeaders?): Map<String, String> =
            headers?.entries()?.associate { entry: Map.Entry<String, String> -> entry.key to entry.value } ?: HashMap()

}