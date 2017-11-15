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

import com.google.common.base.Strings;
import com.hotels.intellij.plugins.network.domain.RequestResponse;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.StripeTable;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import org.jdesktop.swingx.VerticalLayout;

import java.awt.*;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 * The Network tool window panel. Extension of {@link SimpleToolWindowPanel}.
 */
public class NetworkToolWindowPanel extends SimpleToolWindowPanel {

    private static final String NETWORK_TOOLBAR = "NetworkToolbar";

    private NetworkTableModel tableModel = new NetworkTableModel();
    private Document requestHeaderTextAreaDocument;
    private Document requestContentTextAreaDocument;
    private Document responseHeaderTextAreaDocument;
    private Document responseContentTextAreaDocument;
    private Document curlTextAreaDocument;

    /**
     * Constructor. Used to build the necessary components.
     *
     * @param vertical
     * @param borderless
     */
    NetworkToolWindowPanel(boolean vertical, boolean borderless) {
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
        TableRowSorter<NetworkTableModel> networkTableModelTableRowSorter = new TableRowSorter<>(tableModel);

        JTextField filterText = new JTextField();
        filterText.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }

            private void applyFilter() {
                @Nullable String regex = filterText.getText();
                @Nullable RowFilter<NetworkTableModel, Object> rf = null;
                if (!Strings.isNullOrEmpty(regex)) {
                    rf = RowFilter.regexFilter(regex, 1);
                }
                try {
                    networkTableModelTableRowSorter.setRowFilter(rf);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        JBTable table = new StripeTable(tableModel);
        table.setRowSorter(networkTableModelTableRowSorter);

        DefaultTableCellRenderer rightAlignedTableCellRenderer = new DefaultTableCellRenderer();
//        rightAlignedTableCellRenderer.setPreferredSize(new Dimension(30, 0));
        rightAlignedTableCellRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = table.getColumnModel();

        TableColumn tableColumn = columnModel.getColumn(0);
//        tableColumn.setPreferredWidth(30);
        tableColumn.setCellRenderer(rightAlignedTableCellRenderer);

        TableColumn tableColumn1 = columnModel.getColumn(1);
//        tableColumn1.setPreferredWidth(150);
        tableColumn1.setCellRenderer(rightAlignedTableCellRenderer);

        TableColumn tableColumn2 = columnModel.getColumn(2);
//        tableColumn2.setPreferredWidth(45);
        tableColumn2.setCellRenderer(rightAlignedTableCellRenderer);

        TableColumn tableColumn3 = columnModel.getColumn(3);
//        tableColumn3.setPreferredWidth(50);
        tableColumn3.setCellRenderer(rightAlignedTableCellRenderer);

        TableColumn tableColumn4 = columnModel.getColumn(4);
//        tableColumn4.setPreferredWidth(20);
        tableColumn4.setCellRenderer(rightAlignedTableCellRenderer);

//        table.doLayout();

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (table.getSelectedRow() >= 0) {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    RequestResponse requestResponse = tableModel.getRow(table.getSelectedRow());

                    requestHeaderTextAreaDocument.setText(replaceIncorrectLineSeparators(requestResponse.getRequestHeaders()));
                    requestContentTextAreaDocument.setText(replaceIncorrectLineSeparators(requestResponse.getRequestContent()));
                    responseHeaderTextAreaDocument.setText(replaceIncorrectLineSeparators(requestResponse.getResponseHeaders()));
                    responseContentTextAreaDocument.setText(replaceIncorrectLineSeparators(requestResponse.getResponseContent()));
                    curlTextAreaDocument.setText(requestResponse.getCurlRequest());
                });
            } else {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    requestHeaderTextAreaDocument.setText("");
                    requestContentTextAreaDocument.setText("");
                    responseHeaderTextAreaDocument.setText("");
                    responseContentTextAreaDocument.setText("");
                    curlTextAreaDocument.setText("");
                });
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(filterText, BorderLayout.CENTER);

        JBScrollPane jbScrollPane = new JBScrollPane(table);

        JPanel containerPanel = new JPanel(new VerticalLayout());
        containerPanel.add(panel);
        containerPanel.add(jbScrollPane);

        return containerPanel;
    }

    private CharSequence replaceIncorrectLineSeparators(String text) {
        return text.replace("\r\n", "\n");
    }

    private Component createTabbedComponent() {
        JBTabbedPane tabbedPane = new JBTabbedPane(SwingConstants.TOP);
        EditorFactory editorFactory = EditorFactory.getInstance();

        tabbedPane.insertTab("Request Headers", null, createRequestHeaderComponent(editorFactory), "", 0);
        tabbedPane.insertTab("Request Content", null, createRequestContentComponent(editorFactory), "", 1);
        tabbedPane.insertTab("Response Headers", null, createResponseHeaderComponent(editorFactory), "", 2);
        tabbedPane.insertTab("Response Content", null, createResponseContentComponent(editorFactory), "", 3);
        tabbedPane.insertTab("Curl Request", null, createCurlRequestComponent(editorFactory), "", 4);

        return tabbedPane;
    }

    private Component createRequestHeaderComponent(EditorFactory editorFactory) {
        requestHeaderTextAreaDocument = editorFactory.createDocument("");

        Editor requestHeaderTextAreaEditor = editorFactory.createViewer(requestHeaderTextAreaDocument);

        return requestHeaderTextAreaEditor.getComponent();
    }

    private Component createRequestContentComponent(EditorFactory editorFactory) {
        requestContentTextAreaDocument = editorFactory.createDocument("");

        Editor requestContentTextAreaEditor = editorFactory.createViewer(requestContentTextAreaDocument);

        return requestContentTextAreaEditor.getComponent();
    }

    private Component createResponseHeaderComponent(EditorFactory editorFactory) {
        responseHeaderTextAreaDocument = editorFactory.createDocument("");

        Editor responseHeaderTextAreaEditor = editorFactory.createViewer(responseHeaderTextAreaDocument);

        return responseHeaderTextAreaEditor.getComponent();
    }

    private Component createResponseContentComponent(EditorFactory editorFactory) {
        responseContentTextAreaDocument = editorFactory.createDocument("");

        Editor responseContentEditor = editorFactory.createViewer(responseContentTextAreaDocument);

        return responseContentEditor.getComponent();
    }

    private Component createCurlRequestComponent(EditorFactory editorFactory) {
        curlTextAreaDocument = editorFactory.createDocument("");

        Editor curlTextAreaEditor = editorFactory.createViewer(curlTextAreaDocument);

        return curlTextAreaEditor.getComponent();
    }
}
