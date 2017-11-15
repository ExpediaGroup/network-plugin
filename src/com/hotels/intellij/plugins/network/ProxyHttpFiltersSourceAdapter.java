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
import org.apache.http.client.utils.URIBuilder;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import javax.annotation.Nullable;

/**
 * Littleproxy filters source adapter.
 */
public class ProxyHttpFiltersSourceAdapter extends HttpFiltersSourceAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final NetworkListener networkListener;
    private final String redirectedHostTemplate;
    private final String additionalReqParams;
    private final @Nullable InetSocketAddress redirectTo;

    public ProxyHttpFiltersSourceAdapter(NetworkListener networkListener,
                                         String redirectedHostTemplate,
                                         String additionalReqParams,
                                         @Nullable InetSocketAddress redirectTo) {
        this.networkListener = networkListener;
        this.redirectedHostTemplate = redirectedHostTemplate;
        this.additionalReqParams = additionalReqParams;
        this.redirectTo = redirectTo;
    }

    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        String uri = originalRequest.uri();
        ;
        if (redirectTo != null && uri.startsWith("http")) {
            try {
                URIBuilder uriBuilder = new URIBuilder(uri);

                String host = uriBuilder.getHost();

                if (host != null && ( redirectedHostTemplate.isEmpty() || host.contains(redirectedHostTemplate))) {
                    uriBuilder.setHost(redirectTo.getHostString());
                    uriBuilder.setPort(redirectTo.getPort());

                    uri = uriBuilder.build().toString();
                    originalRequest.setUri(uri);
                }
            } catch (URISyntaxException e) {
                logger.error("Can't construct url from string: [{}]", uri, e);
            }
        }

        if (!additionalReqParams.isEmpty() && !uri.contains(additionalReqParams)) {
            if (uri.contains("?")) {
                uri += "&" + additionalReqParams;
            } else if(uri.endsWith("?")){
                uri += additionalReqParams;
            } else {
                uri += "?" + additionalReqParams;
            }

            originalRequest.setUri(uri);
        }

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
