/**
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

import static com.hotels.intellij.plugins.network.Preferences.ADDITIONAL_REQ_PARAMS_DEFAULT;
import static com.hotels.intellij.plugins.network.Preferences.ADDITIONAL_REQ_PARAMS_KEY;
import static com.hotels.intellij.plugins.network.Preferences.HTTP_PORT_DEFAULT;
import static com.hotels.intellij.plugins.network.Preferences.HTTP_PORT_KEY;
import static com.hotels.intellij.plugins.network.Preferences.REDIRECTED_HOST_TEMPLATE_DEFAULT;
import static com.hotels.intellij.plugins.network.Preferences.REDIRECTED_HOST_TEMPLATE_KEY;
import static com.hotels.intellij.plugins.network.Preferences.REDIRECT_TO_HOST_KEY;
import static com.hotels.intellij.plugins.network.Preferences.REDIRECT_TO_PORT_KEY;

import com.hotels.intellij.plugins.network.converter.DefaultByteBufToStringConverter;
import com.hotels.intellij.plugins.network.converter.FullHttpResponseToResponseContentConverter;
import com.hotels.intellij.plugins.network.converter.HeaderToTextConverter;
import com.hotels.intellij.plugins.network.converter.LZ4ByteBufToStringConverter;
import com.hotels.intellij.plugins.network.converter.NettyObjectsToRequestResponseConverter;
import com.hotels.intellij.plugins.network.converter.RequestToCurlConverter;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;
import javax.annotation.Nullable;

/**
 * Button to start the proxy server.
 */
public class StartProxyServerAction extends AnAction {

    private NetworkTableModel tableModel;

    public StartProxyServerAction(NetworkTableModel tableModel) {
        super("Start server", "", IconLoader.findIcon("/actions/execute.png"));

        this.tableModel = tableModel;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        int httpPort = getProxyPort();
        String redirectedHostTemplate = getRedirectedHostTemplate();
        String additionalReqParams = getAdditionalReqParams();

        String redirectToHost = getRedirectToHost();
        String redirectToPortString = getRedirectToPort();

        @Nullable InetSocketAddress redirectTo = null;
        if (redirectToHost != null && redirectToPortString != null) {
            redirectTo = new InetSocketAddress(redirectToHost, Integer.parseInt(redirectToPortString));
        }

        notifyProxyStartup(httpPort);

        ProxyServerComponent proxyServerComponent = event.getProject().getComponent(ProxyServerComponent.class);
        HttpProxyServer httpProxyServer = DefaultHttpProxyServer.bootstrap()
            .withAllowLocalOnly(false)
            .withPort(httpPort)
            .withServerResolver(new CustomServerResolver(redirectedHostTemplate, redirectTo))
            .withFiltersSource(getProxyHttpFiltersSourceAdapter(redirectedHostTemplate,
                                                                additionalReqParams,
                                                                redirectTo))
            .withMaxInitialLineLength(32768)
            .start();

        proxyServerComponent.setServer(httpProxyServer);
    }

    @Override
    public void update(AnActionEvent event) {
        ProxyServerComponent proxyServerComponent = event.getProject().getComponent(ProxyServerComponent.class);
        event.getPresentation().setEnabled(proxyServerComponent.getServer() == null);
    }

    private int getProxyPort() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getInt(HTTP_PORT_KEY, HTTP_PORT_DEFAULT);
    }

    private @Nullable String getRedirectToHost() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getValue(REDIRECT_TO_HOST_KEY);
    }

    private @Nullable String getRedirectToPort() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getValue(REDIRECT_TO_PORT_KEY);
    }

    private String getRedirectedHostTemplate() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getValue(REDIRECTED_HOST_TEMPLATE_KEY, REDIRECTED_HOST_TEMPLATE_DEFAULT);
    }

    private String getAdditionalReqParams() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getValue(ADDITIONAL_REQ_PARAMS_KEY, ADDITIONAL_REQ_PARAMS_DEFAULT);
    }

    private void notifyProxyStartup(int httpPort) {
        Notifications.Bus.notify(new com.intellij.notification.Notification(Notification.NOTIFICATION_GROUP_ID,
                Notification.NOTIFICATION_TITLE,
                "Starting proxy server on port " + httpPort + ".",
                NotificationType.INFORMATION));
    }

    private ProxyHttpFiltersSourceAdapter getProxyHttpFiltersSourceAdapter(String redirectedHostTemplate,
                                                                           String additionalReqParams,
                                                                           @Nullable InetSocketAddress redirectTo) {
        DefaultByteBufToStringConverter defaultByteBufToStringConverter = new DefaultByteBufToStringConverter();
        LZ4ByteBufToStringConverter lz4ByteBufToStringConverter = new LZ4ByteBufToStringConverter();
        HeaderToTextConverter headerToTextConverter = new HeaderToTextConverter();
        RequestToCurlConverter requestToCurlConverter = new RequestToCurlConverter();
        FullHttpResponseToResponseContentConverter fullHttpResponseToResponseContentConverter = new FullHttpResponseToResponseContentConverter(defaultByteBufToStringConverter, lz4ByteBufToStringConverter);
        NettyObjectsToRequestResponseConverter nettyObjectsToRequestResponseConverter = new NettyObjectsToRequestResponseConverter(defaultByteBufToStringConverter, fullHttpResponseToResponseContentConverter, headerToTextConverter, requestToCurlConverter);

        NetworkListener networkListener = new NetworkListener(tableModel, nettyObjectsToRequestResponseConverter);
        return new ProxyHttpFiltersSourceAdapter(networkListener, redirectedHostTemplate, additionalReqParams, redirectTo);
    }
}
