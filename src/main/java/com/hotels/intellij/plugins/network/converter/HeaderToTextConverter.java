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
 * Convert the {@link Map} containing headers into a {@link String}.
 */
public class HeaderToTextConverter {

    /**
     * Convert the {@link Map} containing headers into a {@link String}.
     *
     * @param headers {@link Map}
     * @return {@link String}
     */
    public String convert(Map<String, String> headers) {
        StringBuilder stringBuilder = new StringBuilder();
        headers.entrySet().forEach(entry -> {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(": ");
            stringBuilder.append(entry.getValue());
            stringBuilder.append("\n");
        });

        return stringBuilder.toString();
    }

}
