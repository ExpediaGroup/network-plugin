/**
 * Copyright 2017 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.intellij.plugins.network;

/**
 * Constants for preferences.
 */
public class Preferences {
    public static final int HTTP_PORT_DEFAULT = 8080;
    public static final String REDIRECTED_HOST_TEMPLATE_DEFAULT = "";
    public static final String ADDITIONAL_REQ_PARAMS_DEFAULT = "";

    public static final String HTTP_PORT_KEY = "com.hotels.intellij.plugins.network.http_port";
    public static final String REDIRECT_TO_HOST_KEY = "com.hotels.intellij.plugins.network.redirect_to_host";
    public static final String REDIRECT_TO_PORT_KEY = "com.hotels.intellij.plugins.network.redirect_to_port";
    public static final String REDIRECTED_HOST_TEMPLATE_KEY = "com.hotels.intellij.plugins.network.redirected_host_template";
    public static final String ADDITIONAL_REQ_PARAMS_KEY = "com.hotels.intellij.plugins.network.additional_req_params";
}
