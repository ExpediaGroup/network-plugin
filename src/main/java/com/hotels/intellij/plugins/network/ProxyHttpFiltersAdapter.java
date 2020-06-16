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
package com.hotels.intellij.plugins.network;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpFiltersAdapter;

/**
 * Littleproxy filters adapter.
 */
public class ProxyHttpFiltersAdapter extends HttpFiltersAdapter {

    private final NetworkListener networkListener;
    private Long requestSending;
    private Long requestReceiving;

    /**
     * Constructor.
     *
     * @param originalRequest {@link HttpRequest}
     * @param networkListener {@link NetworkListener}
     */
    public ProxyHttpFiltersAdapter(HttpRequest originalRequest, NetworkListener networkListener) {
        super(originalRequest);

        this.networkListener = networkListener;
    }

    @Override
    public void proxyToServerRequestSending() {
        requestSending = System.currentTimeMillis();
    }

    @Override
    public void serverToProxyResponseReceiving() {
        requestReceiving = System.currentTimeMillis();
    }

    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        long timeInMillis = (requestReceiving != null && requestSending != null) ? requestReceiving - requestSending : 0;
        networkListener.filterResponse(httpObject, originalRequest, timeInMillis);

        return httpObject;
    }
}
