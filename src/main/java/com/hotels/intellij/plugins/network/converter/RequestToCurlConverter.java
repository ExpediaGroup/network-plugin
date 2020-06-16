/*
 * Copyright 2017 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.intellij.plugins.network.converter;

import java.util.Map;

/**
 * Creates an executable cURL request from the original request.
 */
public class RequestToCurlConverter {

    /**
     * Create an executable cURL request from the original request.
     *
     * @param uri            {@link String}
     * @param method         {@link String}
     * @param requestHeaders {@link Map}
     * @param requestContent {@link String}
     * @return {@link String}
     */
    public String convert(String uri, String method, Map<String, String> requestHeaders, String requestContent) {
        switch (method) {
            case "GET":
                return convertGet(requestHeaders, uri);
            case "POST":
                return convertPost(requestHeaders, requestContent, uri);
            default:
                return "";
        }
    }

    private String convertGet(Map<String, String> requestHeaders, String uri) {
        StringBuilder stringBuilder = new StringBuilder("curl -X GET \\\n");
        requestHeaders.entrySet().forEach(entry -> {
            addHeaderTo(stringBuilder, entry);
        });
        stringBuilder.append("\"");
        stringBuilder.append(uri);
        stringBuilder.append("\"");

        return stringBuilder.toString();
    }

    private String convertPost(Map<String, String> requestHeaders, String requestContent, String uri) {
        StringBuilder stringBuilder = new StringBuilder("curl -X POST \\\n");
        requestHeaders.entrySet().forEach(entry -> {
            addHeaderTo(stringBuilder, entry);
        });
        stringBuilder.append("-d \"");
        stringBuilder.append(requestContent.replace("\"", "\\\""));
        stringBuilder.append("\" \\\n");
        stringBuilder.append("\"");
        stringBuilder.append(uri);
        stringBuilder.append("\"");

        return stringBuilder.toString();
    }

    private void addHeaderTo(StringBuilder stringBuilder, Map.Entry<String, String> entry) {
        stringBuilder.append("-H \"");
        stringBuilder.append(entry.getKey());
        stringBuilder.append(": ");
        stringBuilder.append(entry.getValue());
        stringBuilder.append("\" \\\n");
    }
}
