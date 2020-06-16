package com.hotels.intellij.plugins.network.converter;

import com.google.common.collect.Maps;
import com.hotels.intellij.plugins.network.domain.RequestResponse;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

public class NettyObjectsToRequestResponseConverterTest {

    @Mock
    private DefaultByteBufToStringConverter defaultByteBufToStringConverter;

    @Mock
    private FullHttpResponseToResponseContentConverter fullHttpResponseToResponseContentConverter;

    @Mock
    private HeaderToTextConverter headerToTextConverter;

    @Mock
    private RequestToCurlConverter requestToCurlConverter;

    private NettyObjectsToRequestResponseConverter victim;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        victim = new NettyObjectsToRequestResponseConverter(defaultByteBufToStringConverter, fullHttpResponseToResponseContentConverter, headerToTextConverter, requestToCurlConverter);
    }

    @Test
    public void convertCreatesRequestResponse() throws Exception {
        //given
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.some.domain.com");
        fullHttpRequest.headers().add("Accept", "application/json");

        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        fullHttpResponse.headers().add("Content-Type", "application/json");

        Map<String, String> requestHeaders = Maps.newHashMap();
        requestHeaders.put("Accept", "application/json");
        given(headerToTextConverter.convert(requestHeaders)).willReturn("requestHeaders");

        Map<String, String> responseHeaders = Maps.newHashMap();
        responseHeaders.put("Content-Type", "application/json");
        given(headerToTextConverter.convert(responseHeaders)).willReturn("responseHeaders");

        given(fullHttpResponseToResponseContentConverter.convert(fullHttpResponse)).willReturn("responseContent");

        given(requestToCurlConverter.convert("http://www.some.domain.com", "GET", requestHeaders, null)).willReturn("curlRequest");

        //when
        RequestResponse requestResponse = victim.convert(fullHttpResponse, fullHttpRequest, 300L);

        //then
        assertThat(requestResponse.getUrl(), is("http://www.some.domain.com"));
        assertThat(requestResponse.getMethod(), is("GET"));
        assertThat(requestResponse.getResponseTime(), is(300L));
        assertThat(requestResponse.getResponseContentType(), is("application/json"));
        assertThat(requestResponse.getResponseCode(), is("200"));
        assertThat(requestResponse.getRequestHeaders(), is("requestHeaders"));
        assertThat(requestResponse.getRequestContent(), is(nullValue()));
        assertThat(requestResponse.getResponseHeaders(), is("responseHeaders"));
        assertThat(requestResponse.getResponseContent(), is("responseContent"));
        assertThat(requestResponse.getCurlRequest(), is("curlRequest"));
    }

}