package com.hotels.intellij.plugins.network.converter;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestToCurlConverterTest {

    private RequestToCurlConverter victim;

    @Before
    public void setUp() throws Exception {
        victim = new RequestToCurlConverter();
    }

    @Test
    public void convertCreatesStringRepresentationOfCurlGetRequest() throws Exception {
        //given
        String uri = "http://www.some.domain.com";
        String method = "GET";
        Map<String, String> headerMap = ImmutableMap.<String, String>builder()
                .put("Accept", "application/json")
                .build();

        //when
        String curlAsString = victim.convert(uri, method, headerMap, null);

        //then
        assertThat(curlAsString, is("curl -X GET \\\n-H \"Accept: application/json\" \\\n\"http://www.some.domain.com\""));
    }

    @Test
    public void convertCreatesStringRepresentationOfCurlPostRequest() throws Exception {
        //given
        String uri = "http://www.some.domain.com";
        String method = "POST";
        Map<String, String> headerMap = ImmutableMap.<String, String>builder()
                .put("Accept", "application/json")
                .build();
        String requestData = "[{\"jsonKey\": \"jsonValue\"}]";

        //when
        String curlAsString = victim.convert(uri, method, headerMap, requestData);

        //then
        assertThat(curlAsString, is("curl -X POST \\\n-H \"Accept: application/json\" \\\n-d \"[{\\\"jsonKey\\\": \\\"jsonValue\\\"}]\" \\\n\"http://www.some.domain.com\""));
    }

}