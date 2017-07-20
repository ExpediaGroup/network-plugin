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

import com.google.common.collect.Lists;
import com.hotels.intellij.plugins.network.domain.RequestResponse;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Table model to populate the request table.
 */
public class NetworkTableModel extends AbstractTableModel {

    public static final int URL_INDEX = 0;
    public static final int RESPONSE_CODE_INDEX = 1;
    public static final int RESPONSE_CONTENT_TYPE_INDEX = 2;
    public static final int RESPONSE_TIME_INDEX = 3;
    public static final int METHOD_INDEX = 4;

    // Column headings.
    private static final List<String> COLUMN_NAMES = Lists.newArrayList("Name", "Status", "Type", "Time (ms)", "Method");

    private List<RequestResponse> tableRows = Lists.newArrayList();

    @Override
    public int getRowCount() {
        return tableRows.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RequestResponse requestResponse = tableRows.get(rowIndex);

        String value;
        switch (columnIndex) {
            case URL_INDEX:
                value = requestResponse.getUrl();
                break;
            case RESPONSE_CODE_INDEX:
                value = requestResponse.getResponseCode();
                break;
            case RESPONSE_CONTENT_TYPE_INDEX:
                value = requestResponse.getResponseContentType();
                break;
            case RESPONSE_TIME_INDEX:
                value = String.valueOf(requestResponse.getResponseTime());
                break;
            case METHOD_INDEX:
                value = String.valueOf(requestResponse.getMethod());
                break;
            default:
                value = "";
        }

        return value;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES.get(column);
    }

    /**
     * Add a {@link RequestResponse} to the table.
     *
     * @param requestResponse {@link RequestResponse}
     */
    public void addRow(RequestResponse requestResponse) {
        tableRows.add(requestResponse);

        fireTableRowsInserted(tableRows.size(), tableRows.size());
    }

    /**
     * Get a {@link RequestResponse} from the table.
     *
     * @param rowIndex The row index
     * @return {@link RequestResponse}
     */
    public RequestResponse getRow(int rowIndex) {
        return tableRows.get(rowIndex);
    }

    /**
     * Clear all entries from the table.
     */
    public void clear() {
        tableRows.clear();

        fireTableDataChanged();
    }
}
