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

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.table.JBTable
import java.awt.Component
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.JTextArea
import javax.swing.SwingConstants
import javax.swing.event.ListSelectionEvent
import javax.swing.table.DefaultTableCellRenderer

/**
 * The Network tool window panel. Extension of [SimpleToolWindowPanel].
 */
class NetworkToolWindowPanel(
        vertical: Boolean,
        borderless: Boolean
) : SimpleToolWindowPanel(vertical, borderless) {

    private val tableModel = NetworkTableModel()

    private var requestHeaderTextArea: JTextArea? = null
    private var requestContentTextArea: JTextArea? = null
    private var responseHeaderTextArea: JTextArea? = null
    private var responseContentTextArea: JTextArea? = null
    private var curlTextArea: JTextArea? = null

    private fun createToolBar() {
        val defaultActionGroup = DefaultActionGroup()
        defaultActionGroup.add(StartProxyServerAction(tableModel))
        defaultActionGroup.add(StopProxyServerAction())
        defaultActionGroup.add(ClearAllViewAction(tableModel))
        defaultActionGroup.addSeparator()
        defaultActionGroup.add(PreferencesAction())

        val toolBarPanel = JPanel(GridLayout())
        toolBarPanel.add(ActionManager.getInstance().createActionToolbar(NETWORK_TOOLBAR, defaultActionGroup, false).component)

        toolbar = toolBarPanel
    }

    private fun createContent() {
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createTableComponent(), createTabbedComponent())
        splitPane.dividerSize = 2
        setContent(splitPane)
    }

    private fun createTableComponent(): Component {
        val rightAlignedTableCellRenderer = DefaultTableCellRenderer()
        rightAlignedTableCellRenderer.horizontalAlignment = JLabel.CENTER

        val table = JBTable(tableModel)
        table.columnModel.getColumn(1).cellRenderer = rightAlignedTableCellRenderer
        table.columnModel.getColumn(2).cellRenderer = rightAlignedTableCellRenderer
        table.columnModel.getColumn(3).cellRenderer = rightAlignedTableCellRenderer
        table.columnModel.getColumn(4).cellRenderer = rightAlignedTableCellRenderer
        table.selectionModel.addListSelectionListener { _: ListSelectionEvent? ->
            if (table.selectedRow >= 0) {
                ApplicationManager.getApplication().invokeLater {
                    val requestResponse = tableModel.getRow(table.selectedRow)
                    requestHeaderTextArea!!.text = requestResponse.requestHeaders
                    requestHeaderTextArea!!.caretPosition = 0
                    requestContentTextArea!!.text = requestResponse.requestContent
                    requestContentTextArea!!.caretPosition = 0
                    responseHeaderTextArea!!.text = requestResponse.responseHeaders
                    responseHeaderTextArea!!.caretPosition = 0
                    responseContentTextArea!!.text = requestResponse.responseContent
                    responseContentTextArea!!.caretPosition = 0
                    curlTextArea!!.text = requestResponse.curlRequest
                    curlTextArea!!.caretPosition = 0
                }
            } else {
                ApplicationManager.getApplication().invokeLater {
                    requestHeaderTextArea!!.text = ""
                    requestContentTextArea!!.text = ""
                    responseHeaderTextArea!!.text = ""
                    responseContentTextArea!!.text = ""
                    curlTextArea!!.text = ""
                }
            }
        }
        return JBScrollPane(table)
    }

    private fun createTabbedComponent(): Component {
        val tabbedPane = JBTabbedPane(SwingConstants.TOP)
        tabbedPane.insertTab("Request Headers", null, createRequestHeaderComponent(), "", 0)
        tabbedPane.insertTab("Request Content", null, createRequestContentComponent(), "", 1)
        tabbedPane.insertTab("Response Headers", null, createResponseHeaderComponent(), "", 2)
        tabbedPane.insertTab("Response Content", null, createResponseContentComponent(), "", 3)
        tabbedPane.insertTab("Curl Request", null, createCurlRequestComponent(), "", 4)
        return tabbedPane
    }

    private fun createRequestHeaderComponent(): Component {
        requestHeaderTextArea = JTextArea()
        requestHeaderTextArea!!.isEditable = false
        return JBScrollPane(requestHeaderTextArea)
    }

    private fun createRequestContentComponent(): Component {
        requestContentTextArea = JTextArea()
        requestContentTextArea!!.isEditable = false
        return JBScrollPane(requestContentTextArea)
    }

    private fun createResponseHeaderComponent(): Component {
        responseHeaderTextArea = JTextArea()
        responseHeaderTextArea!!.isEditable = false
        return JBScrollPane(responseHeaderTextArea)
    }

    private fun createResponseContentComponent(): Component {
        responseContentTextArea = JTextArea()
        responseContentTextArea!!.isEditable = false
        return JBScrollPane(responseContentTextArea)
    }

    private fun createCurlRequestComponent(): Component {
        curlTextArea = JTextArea()
        curlTextArea!!.isEditable = false
        return JBScrollPane(curlTextArea)
    }

    companion object {
        const val NETWORK_TOOLBAR = "NetworkToolbar"
    }

    /**
     * Constructor. Used to build the necessary components.
     *
     * @param vertical
     * @param borderless
     */
    init {
        createToolBar()
        createContent()
    }
}