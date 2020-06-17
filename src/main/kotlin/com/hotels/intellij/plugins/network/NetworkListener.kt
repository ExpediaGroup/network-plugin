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
package com.hotels.intellij.plugins.network

import com.hotels.intellij.plugins.network.converter.NettyObjectsToRequestResponseConverter
import com.hotels.intellij.plugins.network.domain.RequestResponse
import com.intellij.openapi.application.ApplicationManager
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest

/**
 * Responsible for converting the request and response into a [RequestResponse] domain object.
 */
class NetworkListener(
        private val networkTableModel: NetworkTableModel,
        private val nettyObjectsToRequestResponseConverter: NettyObjectsToRequestResponseConverter
) {

    /**
     * Add the converted [RequestResponse] to the [NetworkTableModel].
     *
     * @param response        [HttpObject]
     * @param originalRequest [HttpRequest]
     * @param timeInMillis    [Long]
     */
    fun filterResponse(response: HttpObject?, originalRequest: HttpRequest?, timeInMillis: Long?) {
        val requestResponse = nettyObjectsToRequestResponseConverter.convert(response!!, originalRequest!!, timeInMillis!!)
        ApplicationManager.getApplication().invokeLater { networkTableModel.addRow(requestResponse) }
    }

}