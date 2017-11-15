package com.hotels.intellij.plugins.network;

import org.littleshoot.proxy.DefaultHostResolver;
import org.littleshoot.proxy.HostResolver;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import javax.annotation.Nullable;

/**
 * @author Titov Mykhaylo on 08.11.17.
 */
public class CustomServerResolver implements HostResolver {
    private final DefaultHostResolver defaultHostResolver = new DefaultHostResolver();
    private final String redirectedHostTemplate;
    private final @Nullable InetSocketAddress redirectTo;

    CustomServerResolver(String redirectedHostTemplate, @Nullable InetSocketAddress redirectTo) {
        this.redirectedHostTemplate = redirectedHostTemplate;
        this.redirectTo = redirectTo;
    }

    @Override public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
        if (redirectTo != null && (redirectedHostTemplate.isEmpty() || host.contains(redirectedHostTemplate))) {
            return redirectTo;
        }

        return defaultHostResolver.resolve(host, port);
    }
}
