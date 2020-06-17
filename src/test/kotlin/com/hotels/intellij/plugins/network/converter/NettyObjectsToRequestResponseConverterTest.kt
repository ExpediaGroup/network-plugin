package com.hotels.intellij.plugins.network.converter

import io.mockk.every
import io.mockk.mockk
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class NettyObjectsToRequestResponseConverterTest {

    private val defaultByteBufToStringConverter = mockk<DefaultByteBufToStringConverter>()
    private val fullHttpResponseToResponseContentConverter = mockk<FullHttpResponseToResponseContentConverter>()
    private val requestToCurlConverter = mockk<RequestToCurlConverter>()

    private var victim = NettyObjectsToRequestResponseConverter(defaultByteBufToStringConverter, fullHttpResponseToResponseContentConverter, requestToCurlConverter)

    @Test
    @Throws(Exception::class)
    fun convertCreatesRequestResponse() {
        //given
        val fullHttpRequest: FullHttpRequest = DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.some.domain.com")
        fullHttpRequest.headers().add("Accept", "application/json")
        every { defaultByteBufToStringConverter.convert(fullHttpRequest.content()) } returns "requestContent"

        val fullHttpResponse: FullHttpResponse = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        fullHttpResponse.headers().add("Content-Type", "application/json")
        every { fullHttpResponseToResponseContentConverter.convert(fullHttpResponse) } returns "responseContent"

        every { requestToCurlConverter.convert("http://www.some.domain.com", "GET", mapOf("Accept" to "application/json"), "requestContent") } returns "curlRequest"

        //when
        val requestResponse = victim.convert(fullHttpResponse, fullHttpRequest, 300L)

        //then
        assertThat(requestResponse.url, `is`("http://www.some.domain.com"))
        assertThat(requestResponse.method, `is`("GET"))
        assertThat(requestResponse.responseTime, `is`(300L))
        assertThat(requestResponse.responseContentType, `is`("application/json"))
        assertThat(requestResponse.responseCode, `is`("200"))
        assertThat(requestResponse.requestHeaders, `is`("Accept: application/json"))
        assertThat(requestResponse.requestContent, `is`("requestContent"))
        assertThat(requestResponse.responseHeaders, `is`("Content-Type: application/json"))
        assertThat(requestResponse.responseContent, `is`("responseContent"))
        assertThat(requestResponse.curlRequest, `is`("curlRequest"))
    }
}