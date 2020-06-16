package com.hotels.intellij.plugins.network;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.littleshoot.proxy.HttpProxyServer;

/**
 * IntelliJ service to host the {@link HttpProxyServer}.
 */
public class ProxyServerService implements Disposable {

    private HttpProxyServer httpProxyServer;

    public ProxyServerService(Project project) { }

    public static ProxyServerService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ProxyServerService.class);
    }

    public void setHttpProxyServer(HttpProxyServer httpProxyServer) {
        this.httpProxyServer = httpProxyServer;
    }

    public HttpProxyServer getHttpProxyServer() {
        return httpProxyServer;
    }

    @Override
    public void dispose() {
        if (httpProxyServer != null) {
            httpProxyServer.stop();
        }
    }
}
