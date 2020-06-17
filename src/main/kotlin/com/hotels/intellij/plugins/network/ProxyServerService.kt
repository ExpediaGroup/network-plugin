package com.hotels.intellij.plugins.network

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import org.littleshoot.proxy.HttpProxyServer

/**
 * IntelliJ service to host the [HttpProxyServer].
 */
class ProxyServerService(
        project: Project?
) : Disposable {

    var httpProxyServer: HttpProxyServer? = null

    override fun dispose() {
        if (httpProxyServer != null) {
            httpProxyServer!!.stop()
        }
    }

    companion object {
        fun getInstance(project: Project): ProxyServerService {
            return ServiceManager.getService(project, ProxyServerService::class.java)
        }
    }
}