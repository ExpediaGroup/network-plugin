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

import com.hotels.intellij.plugins.network.converter.DefaultByteBufToStringConverter
import com.hotels.intellij.plugins.network.converter.FullHttpResponseToResponseContentConverter
import com.hotels.intellij.plugins.network.converter.LZ4ByteBufToStringConverter
import com.hotels.intellij.plugins.network.converter.NettyObjectsToRequestResponseConverter
import com.hotels.intellij.plugins.network.converter.RequestToCurlConverter
import com.intellij.icons.AllIcons
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

/**
 * Button to start the proxy server.
 */
class StartProxyServerAction(
        private val tableModel: NetworkTableModel
) : AnAction("Start Server", "", AllIcons.Actions.Execute) {

    override fun actionPerformed(event: AnActionEvent) {
        val httpPort = proxyPort
        notifyProxyStartup(httpPort)

        val httpProxyServer = DefaultHttpProxyServer.bootstrap()
                .withPort(httpPort)
                .withFiltersSource(proxyHttpFiltersSourceAdapter)
                .withMaxInitialLineLength(32768)
                .withTransparent(true)
                .start()

        val proxyServerService = event.project!!.getService(ProxyServerService::class.java)
        proxyServerService.httpProxyServer = httpProxyServer
    }

    override fun update(event: AnActionEvent) {
        val proxyServerService = event.project!!.getService(ProxyServerService::class.java)
        event.presentation.isEnabled = proxyServerService.httpProxyServer == null
    }

    private val proxyPort: Int
        get() {
            val propertiesComponent = PropertiesComponent.getInstance()
            return propertiesComponent.getInt(Preferences.HTTP_PORT_KEY, Preferences.HTTP_PORT_DEFAULT)
        }

    private fun notifyProxyStartup(httpPort: Int) {
        val notification = Notification(NotificationConstants.NOTIFICATION_GROUP_ID,
                NotificationConstants.NOTIFICATION_TITLE,
                "Starting proxy server on port $httpPort.",
                NotificationType.INFORMATION)
        Notifications.Bus.notify(notification)
    }

    private val proxyHttpFiltersSourceAdapter: ProxyHttpFiltersSourceAdapter
        get() {
            val defaultByteBufToStringConverter = DefaultByteBufToStringConverter()
            val lz4ByteBufToStringConverter = LZ4ByteBufToStringConverter()
            val requestToCurlConverter = RequestToCurlConverter()
            val fullHttpResponseToResponseContentConverter = FullHttpResponseToResponseContentConverter(defaultByteBufToStringConverter, lz4ByteBufToStringConverter)
            val nettyObjectsToRequestResponseConverter = NettyObjectsToRequestResponseConverter(defaultByteBufToStringConverter, fullHttpResponseToResponseContentConverter, requestToCurlConverter)
            val networkListener = NetworkListener(tableModel, nettyObjectsToRequestResponseConverter)

            return ProxyHttpFiltersSourceAdapter(networkListener)
        }

}