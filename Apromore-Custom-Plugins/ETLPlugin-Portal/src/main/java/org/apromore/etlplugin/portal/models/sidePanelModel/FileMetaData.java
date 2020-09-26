/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.etlplugin.portal.models.sidePanelModel;

import org.apromore.etlplugin.portal.models.templateTableModel.Column;
import org.apromore.etlplugin.portal.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class containing the file meta data.
 */
@Component
public class FileMetaData {

    private HashMap<String, List<String>> inputFileMeta;

    // Store imported table names mapped to list of their column names.
    private HashMap<String, List<String>> fileMetaMap;

    // List of imported columns.
    private List<Column> columns;

    @Value("${some.key:false}")
    private Boolean joinDone;

    /**
     * Get the file meta map.
     * @return the file meta map.
     */
    public HashMap<String, List<String>> getFileMetaMap() {
        if (fileMetaMap == null) {
            fileMetaMap = new HashMap<String, List<String>>();
        }
        return fileMetaMap;
    }

    /**
     * Get the input file meta.
     * @return A hashmap containing data on the input file(s).
     */
    public HashMap<String, List<String>> getInputFileMeta() {
        if (inputFileMeta == null) {
            inputFileMeta = new HashMap<String, List<String>>();
        }
        return inputFileMeta;
    }

    /**
     * Put newly imported file into the fileMetaMap (key: table name, value:
     * column names). Also create a column object for each column.
     *
     * @param tableName The name of the table.
     * @param tableData The table's data.
     */
    public void putNewFile(String tableName, List<List<String>> tableData) {

        if (fileMetaMap == null) {
            fileMetaMap = new HashMap<String, List<String>>();
        }
        if (columns == null) {
            columns = new ArrayList<Column>();
        }

        fileMetaMap.put(tableName, tableData.get(0));
        for (int i = 0; i < tableData.get(0).size(); i++) {
            String columnName = tableData.get(0).get(i);
            String dataType = StringUtils
                                .getColumnType(tableData.get(1).get(i));
            columns.add(new Column(columnName,
                                    columnName,
                                    tableName,
                                    dataType));
        }
    }

    /**
     * Get the column data type based on field Name inside the combobox.
     * *** The user can not enter the column name without table name.
     *
     * @param fieldName is name of tableName.
     * @return the data Type.
     */
    public String getColumnDataType(String fieldName) {

        if (fieldName == null) {
            return null;
        }

        String fieldDataType;

        // List of tables and columns
        if (fieldName.contains(".") &&
                StringUtils.getColumnType(fieldName).equals("STRING")) {
            String [] tableColumn = fieldName.split("\\.");
            for (Column column: columns) {
                if (column.getOriginTable().equals(tableColumn[0]) &&
                        column.getColumnName().equals(tableColumn[1])) {
                    return column.getDataType();
                }
            }
        }

        fieldDataType = StringUtils.getColumnType(fieldName);

        return fieldDataType;
    }

    /**
     * Check if the column is in the column list.
     * @param colName name of the column.
     * @return A boolean variable whether the column is in the columns list.
     */
    public boolean isColumnInList(String colName) {

        if (colName.contains(".")) {
            colName = colName.split("\\.")[1];
        }

        // Check if the column name is in list
        for (String tableName : inputFileMeta.keySet()) {
            for (String columnName: inputFileMeta.get(tableName)) {
                if (colName.equals(columnName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Set the input file meta.
     * @param inputFileMeta the input file meta.
     */
    @NotifyChange(".")
    public void setInputFileMeta(HashMap<String, List<String>> inputFileMeta) {
        this.inputFileMeta = inputFileMeta;
    }

    /**
     * Indicates whether a join has been done.
     * @return true if the join has been completed.
     */
    public Boolean getJoinDone() {
        return joinDone;
    }

    /**
     * Set whether or not the join is done and notify all of the change.
     * @param joinDone true if the join is done.
     */
    @NotifyChange(".")
    public void setJoinDone(Boolean joinDone) {
        this.joinDone = joinDone;
    }

    /**
     * Returns column object if column is found. Otherwise returns null.
     *
     * @param originTable Origin Table Name.
     * @param columnName Column Name.
     * @return column Column in columns List.
     */
    public Column getColumnObject(String originTable, String columnName) {

        for (int i = 0; i < columns.size(); i++) {
            // Check if the column is in columns list.
            if (columns.get(i).getOriginTable().equals(originTable) &&
                (columns.get(i).getColumnName().equals(columnName) ||
                columns.get(i).getColumnName().equals(columnName + "_" +
                originTable))) {
                return columns.get(i);
            }
        }

        return null;
    }

    /**
     * Get the list of columns uploaded in the system.
     *
     * @return the list of columns
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Sets the list of columns.
     *
     * @param columns are the columns of the ETL template table.
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * Set the file meta map.
     * @param fileMetaMap the input file meta.
     */
    public void setFileMetaMap(HashMap<String, List<String>> fileMetaMap) {
        this.fileMetaMap = fileMetaMap;
    }
}
