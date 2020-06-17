package com.hotels.intellij.plugins.network.converter

import com.google.common.collect.ImmutableMap
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test

class RequestToCurlConverterTest {

    private var victim = RequestToCurlConverter()

    @Test
    @Throws(Exception::class)
    fun convertCreatesStringRepresentationOfCurlGetRequest() {
        //given
        val uri = "http://www.some.domain.com"
        val method = "GET"
        val headerMap: Map<String, String> = ImmutableMap.builder<String, String>()
                .put("Accept", "application/json")
                .build()

        //when
        val curlAsString = victim.convert(uri, method, headerMap, "")

        //then
        MatcherAssert.assertThat(curlAsString, CoreMatchers.`is`("curl -X GET \\\n-H \"Accept: application/json\" \\\n\"http://www.some.domain.com\""))
    }

    @Test
    @Throws(Exception::class)
    fun convertCreatesStringRepresentationOfCurlPostRequest() {
        //given
        val uri = "http://www.some.domain.com"
        val method = "POST"
        val headerMap: Map<String, String> = ImmutableMap.builder<String, String>()
                .put("Accept", "application/json")
                .build()
        val requestData = "[{\"jsonKey\": \"jsonValue\"}]"

        //when
        val curlAsString = victim.convert(uri, method, headerMap, requestData)

        //then
        MatcherAssert.assertThat(curlAsString, CoreMatchers.`is`("curl -X POST \\\n-H \"Accept: application/json\" \\\n-d \"[{\\\"jsonKey\\\": \\\"jsonValue\\\"}]\" \\\n\"http://www.some.domain.com\""))
    }
}