/**
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
package com.hotels.intellij.plugins.network.domain;

import java.util.Map;

/**
 * Domain object to hold the request and response data for displaying.
 */
public class RequestResponse {
    private String url;
    private String responseCode;
    private String responseContentType;
    private Long responseTime;
    private String method;
    private String requestHeaders;
    private String requestContent;
    private String responseHeaders;
    private String responseContent;
    private String curlRequest;

    private RequestResponse() {
        // Intentionally empty. Create via Builder.
    }

    public String getUrl() {
        return url;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public String getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public String getCurlRequest() {
        return curlRequest;
    }

    /**
     * RequestResponse.Builder.
     */
    public static final class Builder {
        private RequestResponse requestResponse = new RequestResponse();

        public Builder() {
            this.requestResponse = new RequestResponse();
        }

        public Builder withUrl(String url) {
            this.requestResponse.url = url;
            return this;
        }

        public Builder withResponseCode(String responseCode) {
            this.requestResponse.responseCode = responseCode;
            return this;
        }

        public Builder withResponseContentType(String responseContentType) {
            this.requestResponse.responseContentType = responseContentType;
            return this;
        }

        public Builder withResponseTime(Long responseTime) {
            this.requestResponse.responseTime = responseTime;
            return this;
        }

        public Builder withMethod(String method) {
            this.requestResponse.method = method;
            return this;
        }

        public Builder withRequestHeaders(String requestHeaders) {
            this.requestResponse.requestHeaders = requestHeaders;
            return this;
        }

        public Builder withRequestContent(String requestContent) {
            this.requestResponse.requestContent = requestContent;
            return this;
        }

        public Builder withResponseHeaders(String responseHeaders) {
            this.requestResponse.responseHeaders = responseHeaders;
            return this;
        }

        public Builder withResponseContent(String responseContent) {
            this.requestResponse.responseContent = responseContent;
            return this;
        }

        public Builder withCurlRequest(String curlRequest) {
            this.requestResponse.curlRequest = curlRequest;
            return this;
        }

        public RequestResponse build() {
            return this.requestResponse;
        }
    }
}
