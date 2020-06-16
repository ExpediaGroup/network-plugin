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

import com.hotels.intellij.plugins.network.domain.RequestResponse;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * The Network tool window panel. Extension of {@link SimpleToolWindowPanel}.
 */
public class NetworkToolWindowPanel extends SimpleToolWindowPanel {

    public static final String NETWORK_TOOLBAR = "NetworkToolbar";

    private NetworkTableModel tableModel = new NetworkTableModel();
    private JTextArea requestHeaderTextArea;
    private JTextArea requestContentTextArea;
    private JTextArea responseHeaderTextArea;
    private JTextArea responseContentTextArea;
    private JTextArea curlTextArea;

    /**
     * Constructor. Used to build the necessary components.
     *
     * @param vertical
     * @param borderless
     */
    public NetworkToolWindowPanel(boolean vertical, boolean borderless) {
        super(vertical, borderless);

        createToolBar();
        createContent();
    }

    private void createToolBar() {
        JPanel toolBarPanel = new JPanel(new GridLayout());

        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.add(new StartProxyServerAction(tableModel));
        defaultActionGroup.add(new StopProxyServerAction());
        defaultActionGroup.add(new ClearAllViewAction(tableModel));
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(new PreferencesAction());
        toolBarPanel.add(ActionManager.getInstance().createActionToolbar(NETWORK_TOOLBAR, defaultActionGroup, false).getComponent());

        setToolbar(toolBarPanel);
    }

    private void createContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createTableComponent(), createTabbedComponent());
        splitPane.setDividerSize(2);

        setContent(splitPane);
    }

    private Component createTableComponent() {
        JBTable table = new JBTable(tableModel);

        DefaultTableCellRenderer rightAlignedTableCellRenderer = new DefaultTableCellRenderer();
        rightAlignedTableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(rightAlignedTableCellRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightAlignedTableCellRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightAlignedTableCellRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightAlignedTableCellRenderer);

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (table.getSelectedRow() >= 0) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    RequestResponse requestResponse = tableModel.getRow(table.getSelectedRow());

                    requestHeaderTextArea.setText((requestResponse.getRequestHeaders()));
                    requestHeaderTextArea.setCaretPosition(0);

                    requestContentTextArea.setText(requestResponse.getRequestContent());
                    requestContentTextArea.setCaretPosition(0);

                    responseHeaderTextArea.setText(requestResponse.getResponseHeaders());
                    responseHeaderTextArea.setCaretPosition(0);

                    responseContentTextArea.setText(requestResponse.getResponseContent());
                    responseContentTextArea.setCaretPosition(0);

                    curlTextArea.setText(requestResponse.getCurlRequest());
                    curlTextArea.setCaretPosition(0);
                });
            } else {
                ApplicationManager.getApplication().invokeLater(() -> {
                    requestHeaderTextArea.setText("");
                    requestContentTextArea.setText("");
                    responseHeaderTextArea.setText("");
                    responseContentTextArea.setText("");
                    curlTextArea.setText("");
                });
            }
        });

        return new JBScrollPane(table);
    }

    private Component createTabbedComponent() {
        JBTabbedPane tabbedPane = new JBTabbedPane(SwingConstants.TOP);
        tabbedPane.insertTab("Request Headers", null, createRequestHeaderComponent(), "", 0);
        tabbedPane.insertTab("Request Content", null, createRequestContentComponent(), "", 1);
        tabbedPane.insertTab("Response Headers", null, createResponseHeaderComponent(), "", 2);
        tabbedPane.insertTab("Response Content", null, createResponseContentComponent(), "", 3);
        tabbedPane.insertTab("Curl Request", null, createCurlRequestComponent(), "", 4);

        return tabbedPane;
    }

    private Component createRequestHeaderComponent() {
        requestHeaderTextArea = new JTextArea();
        requestHeaderTextArea.setEditable(false);

        return new JBScrollPane(requestHeaderTextArea);
    }

    private Component createRequestContentComponent() {
        requestContentTextArea = new JTextArea();
        requestContentTextArea.setEditable(false);

        return new JBScrollPane(requestContentTextArea);
    }

    private Component createResponseHeaderComponent() {
        responseHeaderTextArea = new JTextArea();
        responseHeaderTextArea.setEditable(false);

        return new JBScrollPane(responseHeaderTextArea);
    }

    private Component createResponseContentComponent() {
        responseContentTextArea = new JTextArea();
        responseContentTextArea.setEditable(false);

        return new JBScrollPane(responseContentTextArea);
    }

    private Component createCurlRequestComponent() {
        curlTextArea = new JTextArea();
        curlTextArea.setEditable(false);

        return new JBScrollPane(curlTextArea);
    }
}
