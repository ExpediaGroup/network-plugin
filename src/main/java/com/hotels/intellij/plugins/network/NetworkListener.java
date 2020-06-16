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
package com.hotels.intellij.plugins.network;

import com.hotels.intellij.plugins.network.converter.NettyObjectsToRequestResponseConverter;
import com.hotels.intellij.plugins.network.domain.RequestResponse;
import com.intellij.openapi.application.ApplicationManager;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Responsible for converting the request and response into a {@link RequestResponse} domain object.
 */
public class NetworkListener {

    private NetworkTableModel networkTableModel;
    private NettyObjectsToRequestResponseConverter nettyObjectsToRequestResponseConverter;

    /**
     * Constructor.
     *
     * @param networkTableModel                      {@link NetworkTableModel}
     * @param nettyObjectsToRequestResponseConverter {@link NettyObjectsToRequestResponseConverter}
     */
    public NetworkListener(NetworkTableModel networkTableModel,
                           NettyObjectsToRequestResponseConverter nettyObjectsToRequestResponseConverter) {
        this.networkTableModel = networkTableModel;
        this.nettyObjectsToRequestResponseConverter = nettyObjectsToRequestResponseConverter;
    }

    /**
     * Add the converted {@link RequestResponse} to the {@link NetworkTableModel}.
     *
     * @param response        {@link HttpObject}
     * @param originalRequest {@link HttpRequest}
     * @param timeInMillis    {@link Long}
     */
    public void filterResponse(HttpObject response, HttpRequest originalRequest, Long timeInMillis) {
        RequestResponse requestResponse = nettyObjectsToRequestResponseConverter.convert(response, originalRequest, timeInMillis);
        ApplicationManager.getApplication().invokeLater(() -> networkTableModel.addRow(requestResponse));
    }
}
