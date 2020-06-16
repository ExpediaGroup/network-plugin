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

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.littleshoot.proxy.HttpProxyServer;

/**
 * IntelliJ component to host the {@link HttpProxyServer}.
 */
public class ProxyServerComponent implements ProjectComponent {

    private HttpProxyServer server;

    public ProxyServerComponent(Project project) {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @Override
    @NotNull
    public String getComponentName() {
        return "ProxyServerComponent";
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {
        if (server != null) {
            server.stop();
        }
    }

    public void setServer(HttpProxyServer server) {
        this.server = server;
    }

    public HttpProxyServer getServer() {
        return server;
    }
}
