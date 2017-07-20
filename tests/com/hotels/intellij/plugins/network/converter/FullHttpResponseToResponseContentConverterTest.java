package com.hotels.intellij.plugins.network.converter;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FullHttpResponseToResponseContentConverterTest {

    @Mock
    private DefaultByteBufToStringConverter defaultByteBufToStringConverter;

    @Mock
    private LZ4ByteBufToStringConverter lz4ByteBufToStringConverter;

    private FullHttpResponseToResponseContentConverter victim;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        victim = new FullHttpResponseToResponseContentConverter(defaultByteBufToStringConverter, lz4ByteBufToStringConverter);
    }

    @Test
    public void convertUsesDefaultConverterWhenContentTypeNotPresent() throws Exception {
        //given
        FullHttpResponse fullHttpResponse = mock(FullHttpResponse.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        ByteBuf byteBuf = mock(ByteBuf.class);

        given(fullHttpResponse.headers()).willReturn(httpHeaders);
        given(fullHttpResponse.content()).willReturn(byteBuf);

        //when
        victim.convert(fullHttpResponse);

        //then
        verify(defaultByteBufToStringConverter).convert(byteBuf);
    }

    @Test
    public void convertUsesDefaultConverterWhenContentEncodingPresentButNotKnown() throws Exception {
        //given
        FullHttpResponse fullHttpResponse = mock(FullHttpResponse.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        ByteBuf byteBuf = mock(ByteBuf.class);

        given(httpHeaders.get("Content-Encoding")).willReturn("unknown");
        given(fullHttpResponse.headers()).willReturn(httpHeaders);
        given(fullHttpResponse.content()).willReturn(byteBuf);

        //when
        victim.convert(fullHttpResponse);

        //then
        verify(defaultByteBufToStringConverter).convert(byteBuf);
    }

    @Test
    public void convertUsesLZ4ConverterWhenContentEncodingPresentAndKnown() throws Exception {
        //given
        FullHttpResponse fullHttpResponse = mock(FullHttpResponse.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        ByteBuf byteBuf = mock(ByteBuf.class);

        given(httpHeaders.get("Content-Encoding")).willReturn("lz4-block");
        given(fullHttpResponse.headers()).willReturn(httpHeaders);
        given(fullHttpResponse.content()).willReturn(byteBuf);

        //when
        victim.convert(fullHttpResponse);

        //then
        verify(lz4ByteBufToStringConverter).convert(byteBuf);
    }

    @Test
    public void convertPrettyPrintsContentWhenContentTypeJsonAndValidJson() throws Exception {
        //given
        FullHttpResponse fullHttpResponse = mock(FullHttpResponse.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        ByteBuf byteBuf = mock(ByteBuf.class);

        given(httpHeaders.get("Content-Type")).willReturn("application/json");
        given(defaultByteBufToStringConverter.convert(byteBuf)).willReturn("{\"valid\": \"json\"}");
        given(fullHttpResponse.headers()).willReturn(httpHeaders);
        given(fullHttpResponse.content()).willReturn(byteBuf);

        //when
        String content = victim.convert(fullHttpResponse);

        //then
        assertThat(content, is("{\n  \"valid\": \"json\"\n}"));
    }

    @Test
    public void convertDoesNotPrettyPrintContentWhenContentTypeJsonAndInvalidJson() throws Exception {
        //given
        FullHttpResponse fullHttpResponse = mock(FullHttpResponse.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        ByteBuf byteBuf = mock(ByteBuf.class);

        given(httpHeaders.get("Content-Type")).willReturn("application/json");
        given(defaultByteBufToStringConverter.convert(byteBuf)).willReturn("{\"invalid\": \"json\"");
        given(fullHttpResponse.headers()).willReturn(httpHeaders);
        given(fullHttpResponse.content()).willReturn(byteBuf);

        //when
        String content = victim.convert(fullHttpResponse);

        //then
        assertThat(content, is("{\"invalid\": \"json\""));
    }
}