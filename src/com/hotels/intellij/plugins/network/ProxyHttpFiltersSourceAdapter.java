/**
 * Copyright 2017 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.intellij.plugins.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

/**
 * Littleproxy filters source adapter.
 */
public class ProxyHttpFiltersSourceAdapter extends HttpFiltersSourceAdapter {

    private final NetworkListener networkListener;

    public ProxyHttpFiltersSourceAdapter(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        return new ProxyHttpFiltersAdapter(originalRequest, networkListener);
    }

    @Override
    public int getMaximumRequestBufferSizeInBytes() {
        return 8 * 1024 * 1024;
    }

    @Override
    public int getMaximumResponseBufferSizeInBytes() {
        return 8 * 1024 * 1024;
    }

}
