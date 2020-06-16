package com.hotels.intellij.plugins.network.converter;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HeaderToTextConverterTest {

    private HeaderToTextConverter victim;

    @Before
    public void setUp() throws Exception {
        victim = new HeaderToTextConverter();
    }

    @Test
    public void convertCreatesStringRepresentation() throws Exception {
        //given
        Map<String, String> headerMap = ImmutableMap.<String, String>builder()
                .put("Content-Type", "contentType")
                .put("Content-Encoding", "contentEncoding")
                .build();

        //when
        String headerMapAsString = victim.convert(headerMap);

        //then
        assertThat(headerMapAsString, is("Content-Type: contentType\nContent-Encoding: contentEncoding\n"));
    }

}