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
package com.hotels.intellij.plugins.network

import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import org.littleshoot.proxy.HttpFiltersAdapter

/**
 * Littleproxy filters adapter.
 */
class ProxyHttpFiltersAdapter(
        originalRequest: HttpRequest?,
        private val networkListener: NetworkListener
) : HttpFiltersAdapter(originalRequest) {

    private var requestSending: Long? = null
    private var requestReceiving: Long? = null

    override fun proxyToServerRequestSending() {
        requestSending = System.currentTimeMillis()
    }

    override fun serverToProxyResponseReceiving() {
        requestReceiving = System.currentTimeMillis()
    }

    override fun proxyToClientResponse(httpObject: HttpObject): HttpObject {
        val timeInMillis = if (requestReceiving != null && requestSending != null) requestReceiving!! - requestSending!! else 0
        networkListener.filterResponse(httpObject, originalRequest, timeInMillis)
        return httpObject
    }

}