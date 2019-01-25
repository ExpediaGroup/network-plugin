package com.hotels.intellij.plugins.network.converter;

import com.google.common.collect.Maps;
import com.hotels.intellij.plugins.network.domain.RequestResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;

/**
 * Responsible for converting the netty objects into a domain object namely {@link RequestResponse}.
 */
public class NettyObjectsToRequestResponseConverter {

    private DefaultByteBufToStringConverter defaultByteBufToStringConverter;
    private FullHttpResponseToResponseContentConverter fullHttpResponseToResponseContentConverter;
    private HeaderToTextConverter headerToTextConverter;
    private RequestToCurlConverter requestToCurlConverter;

    /**
     * Constructor.
     *
     * @param defaultByteBufToStringConverter            {@link DefaultByteBufToStringConverter}
     * @param fullHttpResponseToResponseContentConverter {@link FullHttpResponseToResponseContentConverter}
     * @param headerToTextConverter                      {@link HeaderToTextConverter}
     * @param requestToCurlConverter                     {@link RequestToCurlConverter}
     */
    public NettyObjectsToRequestResponseConverter(DefaultByteBufToStringConverter defaultByteBufToStringConverter,
                                                  FullHttpResponseToResponseContentConverter fullHttpResponseToResponseContentConverter,
                                                  HeaderToTextConverter headerToTextConverter,
                                                  RequestToCurlConverter requestToCurlConverter) {
        this.defaultByteBufToStringConverter = defaultByteBufToStringConverter;
        this.fullHttpResponseToResponseContentConverter = fullHttpResponseToResponseContentConverter;
        this.headerToTextConverter = headerToTextConverter;
        this.requestToCurlConverter = requestToCurlConverter;
    }

    /**
     * Gather and convert the request and response attributes into a {@link RequestResponse}.
     *
     * @param response        {@link HttpObject}
     * @param originalRequest {@link HttpRequest}
     * @param timeInMillis    {@link Long}
     * @return {@link RequestResponse}
     */
    public RequestResponse convert(HttpObject response,
                                   HttpRequest originalRequest,
                                   Long timeInMillis) {
        FullHttpRequest fullHttpRequest = originalRequest instanceof FullHttpRequest
                ? (FullHttpRequest) originalRequest : null;

        FullHttpResponse fullHttpResponse = response instanceof FullHttpResponse
                ? (FullHttpResponse) response : null;

        String uri = originalRequest.getUri();
        String method = originalRequest.getMethod().name();
        String contentType = fullHttpResponse != null ? fullHttpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE) : "";
        String responseCode = fullHttpResponse != null ? String.valueOf(fullHttpResponse.getStatus().code()) : "";

        Map<String, String> requestHeaders = getHeadersAsMap(originalRequest.headers());
        String requestContent = fullHttpRequest != null ? defaultByteBufToStringConverter.convert(fullHttpRequest.content()) : "";
        String responseHeaders = fullHttpResponse != null ? headerToTextConverter.convert(getHeadersAsMap(fullHttpResponse.headers())) : "";
        String responseContent = fullHttpResponse != null ? fullHttpResponseToResponseContentConverter.convert(fullHttpResponse) : "";

        return new RequestResponse.Builder()
                .withRequestHeaders(headerToTextConverter.convert(requestHeaders))
                .withRequestContent(requestContent)
                .withResponseHeaders(responseHeaders)
                .withResponseContent(responseContent)
                .withUrl(uri)
                .withResponseCode(responseCode)
                .withResponseContentType(contentType)
                .withResponseTime(timeInMillis)
                .withMethod(method)
                .withCurlRequest(requestToCurlConverter.convert(uri, method, requestHeaders, requestContent))
                .build();
    }

    private Map<String, String> getHeadersAsMap(HttpHeaders headers) {
        Map<String, String> headerMap = Maps.newHashMap();

        if (headers != null) {
            headers.entries().forEach(entry -> headerMap.put(entry.getKey(), entry.getValue()));
        }

        return headerMap;
    }

}
