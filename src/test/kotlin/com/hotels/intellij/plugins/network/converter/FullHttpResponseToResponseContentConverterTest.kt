package com.hotels.intellij.plugins.network.converter

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaders
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class FullHttpResponseToResponseContentConverterTest {

    private val defaultByteBufToStringConverter = mockk<DefaultByteBufToStringConverter>()
    private val lz4ByteBufToStringConverter = mockk<LZ4ByteBufToStringConverter>()

    private var victim = FullHttpResponseToResponseContentConverter(defaultByteBufToStringConverter, lz4ByteBufToStringConverter)

    @Test
    @Throws(Exception::class)
    fun convertUsesDefaultConverterWhenContentTypeNotPresent() {
        //given
        val fullHttpResponse = mockk<FullHttpResponse>()
        val httpHeaders = mockk<HttpHeaders>()
        val byteBuf = mockk<ByteBuf>()
        every { httpHeaders["Content-Encoding"] } returns null
        every { httpHeaders["Content-Type"] } returns null
        every { fullHttpResponse.headers() } returns httpHeaders
        every { fullHttpResponse.content() } returns byteBuf

        every { defaultByteBufToStringConverter.convert(byteBuf) } returns ""

        //when
        victim.convert(fullHttpResponse)

        //then
        verify { defaultByteBufToStringConverter.convert(byteBuf) }
    }

    @Test
    @Throws(Exception::class)
    fun convertUsesDefaultConverterWhenContentEncodingPresentButNotKnown() {
        //given
        val fullHttpResponse = mockk<FullHttpResponse>()
        val httpHeaders = mockk<HttpHeaders>()
        val byteBuf = mockk<ByteBuf>()
        every { httpHeaders["Content-Encoding"] } returns "unknown"
        every { httpHeaders["Content-Type"] } returns null
        every { fullHttpResponse.headers() } returns httpHeaders
        every { fullHttpResponse.content() } returns byteBuf

        every { defaultByteBufToStringConverter.convert(byteBuf) } returns ""

        //when
        victim.convert(fullHttpResponse)

        //then
        verify { defaultByteBufToStringConverter.convert(byteBuf) }
    }

    @Test
    @Throws(Exception::class)
    fun convertUsesLZ4ConverterWhenContentEncodingPresentAndKnown() {
        //given
        val fullHttpResponse = mockk<FullHttpResponse>()
        val httpHeaders = mockk<HttpHeaders>()
        val byteBuf = mockk<ByteBuf>()
        every { httpHeaders["Content-Encoding"] } returns "lz4-block"
        every { httpHeaders["Content-Type"] } returns null
        every { fullHttpResponse.headers() } returns httpHeaders
        every { fullHttpResponse.content() } returns byteBuf

        every { lz4ByteBufToStringConverter.convert(byteBuf) } returns ""

        //when
        victim.convert(fullHttpResponse)

        //then
        verify { lz4ByteBufToStringConverter.convert(byteBuf) }
    }

    @Test
    @Throws(Exception::class)
    fun convertPrettyPrintsContentWhenContentTypeJsonAndValidJson() {
        //given
        val fullHttpResponse = mockk<FullHttpResponse>()
        val httpHeaders = mockk<HttpHeaders>()
        val byteBuf = mockk<ByteBuf>()
        every { httpHeaders["Content-Encoding"] } returns null
        every { httpHeaders["Content-Type"] } returns "application/json"
        every { fullHttpResponse.headers() } returns httpHeaders
        every { fullHttpResponse.content() } returns byteBuf

        every { defaultByteBufToStringConverter.convert(byteBuf) } returns "{\"valid\": \"json\"}"

        //when
        val content = victim.convert(fullHttpResponse)

        //then
        assertThat(content, `is`("{\n  \"valid\": \"json\"\n}"))
    }

    @Test
    @Throws(Exception::class)
    fun convertDoesNotPrettyPrintContentWhenContentTypeJsonAndInvalidJson() {
        //given
        val fullHttpResponse = mockk<FullHttpResponse>()
        val httpHeaders = mockk<HttpHeaders>()
        val byteBuf = mockk<ByteBuf>()
        every { httpHeaders["Content-Encoding"] } returns null
        every { httpHeaders["Content-Type"] } returns "application/json"
        every { fullHttpResponse.headers() } returns httpHeaders
        every { fullHttpResponse.content() } returns byteBuf

        every { defaultByteBufToStringConverter.convert(byteBuf) } returns "{\"invalid\": \"json\""

        //when
        val content = victim.convert(fullHttpResponse)

        //then
        assertThat(content, `is`("{\"invalid\": \"json\""))
    }
}