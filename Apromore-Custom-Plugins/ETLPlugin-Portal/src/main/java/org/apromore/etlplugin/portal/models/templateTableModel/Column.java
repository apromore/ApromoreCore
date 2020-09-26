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

import org.jooq.Field;

import static org.jooq.impl.DSL.field;

/**
 * Column class is to store information of a table column
 * such as column name, the table column belongs to,
 * list of the rules applied to column, etc.
 */
public class Column {
    private String columnName;
    private String originalColumnName;
    private String originTable;
    private Rule rule;
    private String dataType;

    /**
     * Constructor to create the Column.
     *
     * @param columnName Name of the column.
     * @param originalColumnName Original name of the column.
     * @param originTable Name of the origin table.
     */
    public Column(String columnName, String originalColumnName,
        String originTable) {
        this.columnName = columnName;
        this.originalColumnName = originalColumnName;
        this.originTable = originTable;
        rule = null;
    }

    /**
     * Constructor to create the Column.
     *
     * @param columnName Name of the column.
     * @param originalColumnName Original name of the column.
     * @param originTable Name of the origin table.
     * @param dataType The data type of the column.
     */
    public Column(String columnName, String originalColumnName,
                  String originTable, String dataType) {
        this.columnName = originalColumnName;
        this.originalColumnName = originalColumnName;
        this.originTable = originTable;
        this.dataType = dataType;
        rule = null;
    }

    /**
     * Getter to get the columnName field of the class.
     *
     * @return columnName Column name field.
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Setter to set the columnName field.
     *
     * @param columnName New column name.
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Getter to get the originalColumnName field of the class.
     *
     * @return originalColumnName originalColumnName field.
     */
    public String getOriginalColumnName() {
        return this.originalColumnName;
    }

    /**
     * Setter to set the originalColumnName field.
     *
     * @param originalColumnName Original column name.
     */
    public void setOriginalColumnName(String originalColumnName) {
        this.originalColumnName = originalColumnName;
    }

    /**
     * Getter to get the name of the column originTable.
     *
     * @return tableName Column name field.
     */
    public String getOriginTable() {
        return this.originTable;
    }

    /**
     * Setter to set the originTable field.
     *
     * @param tableName Name of the Column Table.
     */
    public void setOriginTable(String tableName) {
        this.originTable = tableName;
    }

    /**
     * Get the rule applied to this column.
     * @return the rule applied to this column.
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * Set the rule to be applied to this column.
     * @param rule the rule applied to this column.
     */
    public void setRule(Rule rule) {
        this.rule = rule;
    }

    /**
     * Get the data type of the column.
     *
     * @return the data type
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType is type of column.
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * Generates the field for column from field or rule.
     *
     * @return the query field.
     */
    public Field getField() {
        if (rule == null || rule.getQuery() == null) {
            return field("`" + originTable + "`.`" +
                    originalColumnName + "`")
                    .as(columnName);
        } else {
            return rule.getQuery().as(columnName);
        }
    }
}
