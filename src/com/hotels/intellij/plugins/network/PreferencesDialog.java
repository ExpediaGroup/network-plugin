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
package com.hotels.intellij.plugins.network;

import static com.hotels.intellij.plugins.network.Preferences.ADDITIONAL_REQ_PARAMS_DEFAULT;
import static com.hotels.intellij.plugins.network.Preferences.ADDITIONAL_REQ_PARAMS_KEY;
import static com.hotels.intellij.plugins.network.Preferences.HTTP_PORT_DEFAULT;
import static com.hotels.intellij.plugins.network.Preferences.HTTP_PORT_KEY;
import static com.hotels.intellij.plugins.network.Preferences.REDIRECTED_HOST_TEMPLATE_DEFAULT;
import static com.hotels.intellij.plugins.network.Preferences.REDIRECTED_HOST_TEMPLATE_KEY;
import static com.hotels.intellij.plugins.network.Preferences.REDIRECT_TO_HOST_KEY;
import static com.hotels.intellij.plugins.network.Preferences.REDIRECT_TO_PORT_KEY;

import com.google.common.base.Strings;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Preferences component.
 */
public class PreferencesDialog extends DialogWrapper {

    private static final Logger LOGGER = Logger.getInstance(PreferencesDialog.class);

    private JTextField portTextField;

    private JTextField redirectToTextField;

    private JTextField redirectedHostTemplateTextField;

    private JTextField additionalRequestParamsTextField;

    public PreferencesDialog(@Nullable Project project) {
        super(project);

        init();
        setTitle("Proxy Preferences");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        int httpPort = propertiesComponent.getInt(HTTP_PORT_KEY, HTTP_PORT_DEFAULT);

        @Nullable String redirectToHost = propertiesComponent.getValue(REDIRECT_TO_HOST_KEY);
        @Nullable String redirectToPortString = propertiesComponent.getValue(REDIRECT_TO_PORT_KEY);

        String redirectTo = "";
        if (redirectToHost != null && redirectToPortString != null) {
            redirectTo = redirectToHost + ":" + redirectToPortString;
        }

        String redirectedHostTemplate = propertiesComponent.getValue(REDIRECTED_HOST_TEMPLATE_KEY, REDIRECTED_HOST_TEMPLATE_DEFAULT);
        String additionalReqParams = propertiesComponent.getValue(ADDITIONAL_REQ_PARAMS_KEY, ADDITIONAL_REQ_PARAMS_DEFAULT);

        JPanel panel = new JPanel(new GridLayoutManager(4, 4));

        panel.add(new JLabel("Port: "), getGridConstraints(0, 0));
        portTextField = new JTextField(String.valueOf(httpPort), 35);

        panel.add(portTextField, getGridConstraints(0, 1));

        panel.add(new JLabel("Redirect to: "), getGridConstraints(1, 0));
        redirectToTextField = new JTextField(redirectTo, 35);

        panel.add(redirectToTextField, getGridConstraints(1, 1));

        panel.add(new JLabel("Redirected host template: "), getGridConstraints(2, 0));;
        redirectedHostTemplateTextField = new JTextField(redirectedHostTemplate, 35);

        panel.add(redirectedHostTemplateTextField, getGridConstraints(2, 1));;

        panel.add(new JLabel("Additional request params: "), getGridConstraints(3, 0));
        additionalRequestParamsTextField = new JTextField(additionalReqParams, 35);

        panel.add(additionalRequestParamsTextField, getGridConstraints(3, 1));

        return panel;
    }

    @NotNull private GridConstraints getGridConstraints(int row, int column) {
        GridConstraints constraints = new GridConstraints();
        constraints.setRow(row);
        constraints.setColumn(column);
        return constraints;
    }

    @Override
    protected void doOKAction() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

        if (!Strings.isNullOrEmpty(portTextField.getText())) {
            try {
                Integer httpPort = Integer.valueOf(portTextField.getText());

                propertiesComponent.setValue(HTTP_PORT_KEY, httpPort, HTTP_PORT_DEFAULT);
            } catch (NumberFormatException nfe) {
                LOGGER.error("Failed to convert proxy port[" + portTextField.getText() + "] to an Integer.", nfe);
            }
        }

        String redirectToText = redirectToTextField.getText();
        if (!Strings.isNullOrEmpty(redirectToText)) {
            String[] hostAndPort = redirectToText.split(":");

            propertiesComponent.setValue(REDIRECT_TO_HOST_KEY, hostAndPort[0], null);
            try {
                String redirectPort = "80";
                if (hostAndPort.length>1) {
                    redirectPort = hostAndPort[1];
                }

                propertiesComponent.setValue(REDIRECT_TO_PORT_KEY, redirectPort, null);
            } catch (NumberFormatException nfe) {
                LOGGER.error("Failed to convert redirect https proxy port[" + redirectToText + "] to an Integer.", nfe);
            }
        }else {
            propertiesComponent.setValue(REDIRECT_TO_HOST_KEY, null, null);
            propertiesComponent.setValue(REDIRECT_TO_PORT_KEY, null, null);
        }

        String redirectedHostTemplate = redirectedHostTemplateTextField.getText();
        propertiesComponent.setValue(REDIRECTED_HOST_TEMPLATE_KEY, redirectedHostTemplate, REDIRECTED_HOST_TEMPLATE_DEFAULT);

        String additionalRequestParams = additionalRequestParamsTextField.getText();
        propertiesComponent.setValue(ADDITIONAL_REQ_PARAMS_KEY, additionalRequestParams, ADDITIONAL_REQ_PARAMS_DEFAULT);

        super.doOKAction();
    }
}
