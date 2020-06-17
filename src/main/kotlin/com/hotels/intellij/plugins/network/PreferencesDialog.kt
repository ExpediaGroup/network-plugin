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
package com.hotels.intellij.plugins.network

import com.google.common.base.Strings
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * Preferences component.
 */
class PreferencesDialog(
        project: Project?
) : DialogWrapper(project) {

    private var portTextField: JTextField? = null

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(FlowLayout())
        panel.add(JLabel("Http Port: "))
        val propertiesComponent = PropertiesComponent.getInstance()
        val httpPort = propertiesComponent.getInt(Preferences.HTTP_PORT_KEY, Preferences.HTTP_PORT_DEFAULT)
        portTextField = JTextField(httpPort.toString(), 6)
        panel.add(portTextField)
        return panel
    }

    override fun doOKAction() {
        if (!Strings.isNullOrEmpty(portTextField!!.text)) {
            try {
                val httpPort = Integer.valueOf(portTextField!!.text)
                val propertiesComponent = PropertiesComponent.getInstance()
                propertiesComponent.setValue(Preferences.HTTP_PORT_KEY, httpPort, Preferences.HTTP_PORT_DEFAULT)
            } catch (nfe: NumberFormatException) {
                LOGGER.error("Failed to convert proxy port[" + portTextField!!.text + "] to an Integer.", nfe)
            }
        }
        super.doOKAction()
    }

    companion object {
        private val LOGGER = Logger.getInstance(PreferencesDialog::class.java)
    }

    init {
        init()
        title = "Proxy Preferences"
    }
}