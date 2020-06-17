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

import com.google.common.collect.Lists
import com.hotels.intellij.plugins.network.domain.RequestResponse
import javax.swing.table.AbstractTableModel

/**
 * Table model to populate the request table.
 */
class NetworkTableModel : AbstractTableModel() {
    private val tableRows: MutableList<RequestResponse> = Lists.newArrayList()

    override fun getRowCount(): Int = tableRows.size

    override fun getColumnCount(): Int = 5

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = when (columnIndex) {
        URL_INDEX -> getRow(rowIndex).url
        RESPONSE_CODE_INDEX -> getRow(rowIndex).responseCode
        RESPONSE_CONTENT_TYPE_INDEX -> getRow(rowIndex).responseContentType
        RESPONSE_TIME_INDEX -> getRow(rowIndex).responseTime.toString()
        METHOD_INDEX -> getRow(rowIndex).method
        else -> ""
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

    override fun getColumnName(column: Int): String = COLUMN_NAMES[column]

    /**
     * Add a [RequestResponse] to the table.
     *
     * @param requestResponse [RequestResponse]
     */
    fun addRow(requestResponse: RequestResponse) {
        tableRows.add(requestResponse)
        fireTableRowsInserted(tableRows.size, tableRows.size)
    }

    /**
     * Get a [RequestResponse] from the table.
     *
     * @param rowIndex The row index
     * @return [RequestResponse]
     */
    fun getRow(rowIndex: Int): RequestResponse {
        return tableRows[rowIndex]
    }

    /**
     * Clear all entries from the table.
     */
    fun clear() {
        tableRows.clear()
        fireTableDataChanged()
    }

    companion object {
        const val URL_INDEX = 0
        const val RESPONSE_CODE_INDEX = 1
        const val RESPONSE_CONTENT_TYPE_INDEX = 2
        const val RESPONSE_TIME_INDEX = 3
        const val METHOD_INDEX = 4

        // Column headings.
        private val COLUMN_NAMES: List<String> = Lists.newArrayList("URL", "Status", "Type", "Time (ms)", "Method")
    }
}