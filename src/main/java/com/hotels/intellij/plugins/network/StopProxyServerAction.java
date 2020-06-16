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
package com.hotels.intellij.plugins.network;

import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.littleshoot.proxy.HttpProxyServer;

/**
 * Button to stop the proxy server.
 */
public class StopProxyServerAction extends AnAction {

    public StopProxyServerAction() {
        super("Stop Server", "", AllIcons.Actions.Suspend);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        notifyProxyShutdown();

        ProxyServerService proxyServerService = event.getProject().getService(ProxyServerService.class);
        HttpProxyServer httpProxyServer = proxyServerService.getHttpProxyServer();

        httpProxyServer.stop();
        proxyServerService.setHttpProxyServer(null);
    }

    @Override
    public void update(AnActionEvent event) {
        ProxyServerService proxyServerService = event.getProject().getService(ProxyServerService.class);
        event.getPresentation().setEnabled(proxyServerService.getHttpProxyServer() != null);
    }

    private void notifyProxyShutdown() {
        com.intellij.notification.Notification notification = new com.intellij.notification.Notification(NotificationConstants.NOTIFICATION_GROUP_ID,
                NotificationConstants.NOTIFICATION_TITLE,
                "Stopping proxy server.",
                NotificationType.INFORMATION);

        Notifications.Bus.notify(notification);
    }
}
