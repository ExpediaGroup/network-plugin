/*
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
package com.hotels.intellij.plugins.network

import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * Button to stop the proxy server.
 */
class StopProxyServerAction : AnAction("Stop Server", "", AllIcons.Actions.Suspend) {

    override fun actionPerformed(event: AnActionEvent) {
        notifyProxyShutdown()
        val proxyServerService = event.project!!.getService(ProxyServerService::class.java)
        val httpProxyServer = proxyServerService.httpProxyServer
        httpProxyServer!!.stop()
        proxyServerService.httpProxyServer = null
    }

    override fun update(event: AnActionEvent) {
        val proxyServerService = event.project!!.getService(ProxyServerService::class.java)
        event.presentation.isEnabled = proxyServerService.httpProxyServer != null
    }

    private fun notifyProxyShutdown() {
        val notification = Notification(NotificationConstants.NOTIFICATION_GROUP_ID,
                NotificationConstants.NOTIFICATION_TITLE,
                "Stopping proxy server.",
                NotificationType.INFORMATION)

        Notifications.Bus.notify(notification)
    }
}