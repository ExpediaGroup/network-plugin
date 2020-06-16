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
package com.hotels.intellij.plugins.network;

import com.google.common.base.Strings;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Preferences component.
 */
public class PreferencesDialog extends DialogWrapper {

    private static final Logger LOGGER = Logger.getInstance(PreferencesDialog.class);

    private JTextField portTextField;

    public PreferencesDialog(@Nullable Project project) {
        super(project);

        init();
        setTitle("Proxy Preferences");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Http Port: "));

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        int httpPort = propertiesComponent.getInt(Preferences.HTTP_PORT_KEY, Preferences.HTTP_PORT_DEFAULT);

        portTextField = new JTextField(String.valueOf(httpPort), 6);
        panel.add(portTextField);

        return panel;
    }

    @Override
    protected void doOKAction() {
        if (!Strings.isNullOrEmpty(portTextField.getText())) {
            try {
                Integer httpPort = Integer.valueOf(portTextField.getText());

                PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
                propertiesComponent.setValue(Preferences.HTTP_PORT_KEY, httpPort, Preferences.HTTP_PORT_DEFAULT);
            } catch (NumberFormatException nfe) {
                LOGGER.error("Failed to convert proxy port[" + portTextField.getText() + "] to an Integer.", nfe);
            }
        }

        super.doOKAction();
    }
}
