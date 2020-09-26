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
package org.apromore.etlplugin.portal.models.templateTableModel;

import org.apromore.etlplugin.logic.services.Transaction;
import org.apromore.etlplugin.portal.ETLPluginPortal;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.*;
import static org.jooq.impl.DSL.using;

/**
 * The ColumnsData Bean.
 */
@Component
public class TemplateTableBean implements InitializingBean {

    private Transaction transaction;
    private List<Column> columns;
    private List<List<String>> templateTable;
    private Table<?> table;
    private Boolean tableVisible;

    public TemplateTableBean() {
        transaction = (Transaction) ((Map) Sessions.getCurrent()
            .getAttribute(ETLPluginPortal.SESSION_ATTRIBUTE_KEY))
            .get("transaction");
    }

    /**
     * Do after bean has been initialised.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        columns = new ArrayList<>();
        addPlaceholderColumn();
    }

    /**
     * Add Column to Template Table.
     *
     * @param newColumn Column to add to Template Table.
     */
    @NotifyChange(".")
    public void addColumn(Column newColumn) {
        if (columns == null) {
            columns = new ArrayList<>();
        }

        String originalColumnName = newColumn.getOriginalColumnName();
        String originTable = newColumn.getOriginTable();

        int index = this.getColumnIndex(originTable, originalColumnName);

        if (index == -1) {

            if (this.isDuplicatedName(originalColumnName)) {
                newColumn.setColumnName(originalColumnName +
                    "_" + originTable);
            }

            columns.add(newColumn);
        }
    }

    /**
     * Remove Column from Template Table.
     *
     * @param originTable Origin Table Name.
     * @param originalColumnName Original Column Name.
     */
    @NotifyChange(".")
    public void removeColumn(String originTable, String originalColumnName) {
        if (columns.size() >= 1) {
            int index = this.getColumnIndex(originTable, originalColumnName);

            if (index != -1) {
                columns.remove(index);
            }
        }

    }

    /**
     * Remove all Columns from Template Table.
     *
     */
    @NotifyChange(".")
    public void removeAllColumns() {
        columns.clear();

    }

    /**
     * Update the template table.
     */
    @NotifyChange(".")
    public void updateTemplateTable() {
        removePlaceholderColumn();
        try {
            templateTable = transaction.executeQuery(
                    getQuery(50), true);
        } catch (SQLException e) {
            e.printStackTrace();
            Messagebox.show(
                "ERROR: Invalid rule prepared.",
                "ERROR",
                0,
                Messagebox.ERROR
            );
        }
        addPlaceholderColumn();
    }

    /**
     * If columns added to template table is empty then add a
     * place holder column.
     * Maintaining a place holder column in the table prevents
     * an issue where if the columns list iis empty it requires
     * adding a column twice to update the graphical table.
     */
    private void addPlaceholderColumn() {
        Column column = new Column(
                "-placeholder-", "-placeholder-", "-placeholder-");
        if (columns.size() == 0) {
            columns.add(column);
            tableVisible = false;
        }
    }

    /**
     * If placeholder column exists then remove and set table
     * visibility to true.
     */
    private void removePlaceholderColumn() {
        Column column = new Column(
                "-placeholder-", "-placeholder-", "-placeholder-");
        if (columns.size() > 0 &&
                columns.get(0).getOriginTable().equals("-placeholder-")) {
            columns.remove(0);
        }
        tableVisible = true;
    }

    /**
     * Get the template table.
     *
     * @return templateTable to show template table.
     */
    public List<List<String>> getTemplateTable() {
        return templateTable;
    }

    /**
     * Set the template table.
     *
     * @param templateTable to set.
     */
    @NotifyChange(".")
    public void setTemplateTable(List<List<String>> templateTable) {
        this.templateTable = templateTable;
    }

    /**
     * Returns column index if column is added to Template Table. Otherwise
     * returns -1.
     *
     * @param originTable Origin Table Name.
     * @param columnName Column Name.
     * @return index Column Index in columns List.
     */
    public int getColumnIndex(String originTable, String columnName) {
        int index = -1;

        if (columns == null) {
            return index;
        }

        for (int i = 0; i < columns.size(); i++) {
            // The column is in columns list.
            if (columns.get(i).getOriginTable().equals(originTable) &&
                (columns.get(i).getColumnName().equals(columnName) ||
                columns.get(i).getColumnName().equals(columnName + "_" +
                originTable))) {
                index = i;
            }
        }

        return index;
    }

    /**
     * Check if column name exists in Template Table.
     *
     * @param columnName Column Name to check.
     * @return isDuplicated Check Duplicated Column Name.
     */
    private Boolean isDuplicatedName(String columnName) {
        Boolean isDuplicated = false;

        if (columns == null) {
            return isDuplicated;
        }

        for (int i = 0; i < columns.size(); i++) {
            // The column name already exists.
            if (columns.get(i).getColumnName().equals(columnName)) {
                isDuplicated = true;
            }
        }

        return isDuplicated;
    }

    /**
     * Get the name of tables for added columns.
     *
     * @return tablesNames Tables names list.
     */
    public List<String> getTablesNames() {

        List<String> tablesNames = new ArrayList<String>();

        for (int i = 0; i < columns.size(); i++) {
            String tableName = columns.get(i).getOriginTable();

            Boolean isAdded = false;
            for (int j = 0; j < tablesNames.size(); j++) {
                if (tablesNames.get(j).equals(tableName)) {
                    isAdded = true;
                }
            }
            if (!isAdded) {
                tablesNames.add(tableName);
            }
        }

        return tablesNames;
    }

    /**
     * Get columns.
     *
     * @return columns columns.
     */
    public List<Column> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        return columns;
    }

    /**
     * Set columns.
     *
     * @param columns columns.
     */
    @NotifyChange(".")
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * Read all the columns and generate the query for all the fields
     * and tables.
     *
     * @param limit the number lines to output.
     * @return the query string.
     */
    public String getQuery(int limit) {
        Field[] fields = columns
            .stream()
            .map(column -> column.getField())
            .toArray(Field[]::new);

        SelectJoinStep<?> selectJoinStep = using(SQLDialect.MYSQL)
            .select(fields)
            .from(table);

        if (limit == -1) {
            return selectJoinStep.getSQL(ParamType.INLINED);
        } else {
            return selectJoinStep
                .limit(limit)
                .getSQL(ParamType.INLINED);
        }
    }

    /**
     * Get the joined tables.
     *
     * @return the jooq table object.
     */
    public Table<?> getTable() {
        return table;
    }

    /**
     * Set the joined Tables.
     *
     * @param table is the joined table.
     */
    public void setTable(Table<?> table) {
        this.table = table;
    }

    /**
     *  Get the visibility of the table.
     *
     * @return The table's visibility.
     */
    public Boolean getTableVisible() {
        return tableVisible;
    }
}
