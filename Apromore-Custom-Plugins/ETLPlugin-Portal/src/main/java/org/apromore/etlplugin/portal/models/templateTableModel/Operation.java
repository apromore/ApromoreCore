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

import org.apromore.etlplugin.portal.models.sidePanelModel.FileMetaData;
import org.apromore.etlplugin.portal.utils.SpringBeanContext;
import org.apromore.etlplugin.portal.utils.StringUtils;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.*;

/**
 * This class prepares the query for the operations for the case statements
 * using jooq.
 */
public class Operation {
    private List<String> fields;
    private String operationType = "";
    private String separator = "";
    private FileMetaData fileMetaData;
    private List<String> unUsedFields;

    /**
     * Constructor to initialise the list of fields and inject fileMetaData.
     *
     * @param fileMetaData is meta data bean.
     */
    public Operation(FileMetaData fileMetaData) {
        this.fileMetaData = fileMetaData;
        fields = new ArrayList<>();
        unUsedFields = new ArrayList<>();
        fields.add("");
    }

    /**
     * Constructor to initialise the list of fields.
     */
    public Operation() {
        fields = new ArrayList<>();
        unUsedFields = new ArrayList<>();
        fields.add("");
        fileMetaData = SpringBeanContext.getBean(FileMetaData.class);
    }

    private boolean isString(String column) {
        return StringUtils.getColumnType(column).equals("STRING") &&
            !fileMetaData.isColumnInList(column);
    }

    /**
     * Apply average operation.
     *
     * @param stepOperation Field
     * @return stepOperation Field
     */
    private Field applyAverage(Field stepOperation) {
        Field columnField;

        for (int i = 1; i < fields.size(); i++) {
            columnField = quoteField(i);
            stepOperation = stepOperation.add(columnField);
        }

        Field total = val(fields.size());
        stepOperation = stepOperation.div(total);
        return stepOperation;
    }

    /**
     * Apply concat operation.
     *
     * @return stepOperation Field
     */
    private Field applyConcat() {
        Field columnField;
        List<Field<?>> concatFields = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            columnField = quoteField(i);
            concatFields.add(
                field(
                    field(columnField)
                        .cast(SQLDataType.VARCHAR)
                        .toString(),
                        SQLDataType.CHAR
                )
            );

            if (i != fields.size() - 1) {
                concatFields.add(val(separator));
            }
        }

        Field stepOperation = concat(
            concatFields.toArray(new Field[concatFields.size()])
        );

        return stepOperation;
    }

    /**
     * Checks if the field is string type and in the list.
     * If field is a string and not in list then make it under quotes.
     *
     * @param index int
     * @return columnField Field
     */
    private Field quoteField(int index) {
        if (isString(fields.get(index))) {
            return val(fields.get(index));
        }

        return field(fields.get(index));
    }

    /**
     * This method prepares the operation step query for the case
     * statement rule.
     *
     * @return the field is step query for the operation.
     */
    public Field apply() {
        if (fields.size() < 1) {
            return null;
        } else if (fields.get(0) == "") {
            return null;
        }

        Field columnField = quoteField(0);

        // Matches the type of operation and prepares the step query.
        Field stepOperation;

        if (operationType.toUpperCase()
                .equals(OperationType.COLUMN_EQUALS.toString())) {
            stepOperation = field(columnField);
        } else if (operationType.toUpperCase()
                .equals(OperationType.CONCAT.toString())) {
            stepOperation = applyConcat();
        } else if (operationType.toUpperCase()
                .equals(OperationType.AVERAGE.toString())) {
            stepOperation = field(columnField);
            stepOperation = applyAverage(stepOperation);
        } else {
            stepOperation = field(columnField);

            for (int i = 1; i < fields.size(); i++) {
                columnField = quoteField(i);

                if (operationType.toUpperCase()
                        .equals(OperationType.ADD.toString())) {
                    stepOperation = stepOperation.add(columnField);
                } else if (operationType.toUpperCase()
                        .equals(OperationType.MULTIPLY.toString())) {
                    stepOperation = stepOperation.mul(columnField);
                } else if (operationType.toUpperCase()
                        .equals(OperationType.SUBTRACT.toString())) {
                    stepOperation = stepOperation.sub(columnField);
                } else if (operationType.toUpperCase()
                        .equals(OperationType.DIVIDE.toString())) {
                    stepOperation = stepOperation.div(columnField);
                } else if (operationType.toUpperCase()
                        .equals(OperationType.MAX.toString())) {
                    stepOperation = greatest(stepOperation, columnField);
                } else if (operationType.toUpperCase()
                        .equals(OperationType.MIN.toString())) {
                    stepOperation = least(stepOperation, columnField);
                }
            }
        }

        return stepOperation;
    }

    /**
     * Find the operation type error.
     * @param field String
     * @param field1DataType String
     * @param field2DataType String
     * @return isError String
     */
    String findTypeError(String field,
        String field1DataType, String field2DataType) {
        String isError = null;

        if (field2DataType.equals("STRING") &&
            !operationType.equals(OperationType.CONCAT.toString())) {
            isError = "OPERATION_ERROR";
        } else if (field2DataType.equals("BOOLEAN")) {
            isError = "OPERATION_ERROR";
        } else if (operationType.equals(OperationType.DIVIDE.toString()) &&
                field.equals("0") && (fields.lastIndexOf(field) != 0)) {
            isError = "OPERATION_ERROR";
        } else if (field1DataType.equals("STRING") &&
                !field1DataType.equals(field2DataType)) {
            isError = "TYPE_ERROR";
        } else if (
                (field1DataType.equals("INT") ||
                field1DataType.equals("DOUBLE")) &&
                !(field2DataType.equals("INT") ||
                (field2DataType.equals("DOUBLE")))) {
            isError = "TYPE_ERROR";
        }

        return isError;
    }

    /**
     * Generated the Operation validation error message.
     *
     * @param field is column or constant in operation.
     * @return error message
     */
    public String getErrorMessage(String field) {
        String errorMessage = "";

        // Dont raise any error if the fields are incomplete or null
        if ((fields.size() <= 0) ||
            (operationType == "") ||
            field.equals("") ||
            operationType.equals(OperationType.CONCAT.toString()) ||
            operationType.equals(OperationType.COLUMN_EQUALS.toString())) {
            return errorMessage;
        }

        // Get the data types for the fields
        String field1DataType = fileMetaData.getColumnDataType(fields.get(0));
        String field2DataType = fileMetaData.getColumnDataType(field);
        String isError = findTypeError(field, field1DataType, field2DataType);

        // If the error was detected then create error message
        if (isError != null) {
            if (isError.equals("OPERATION_ERROR")) {
                errorMessage = String.format(
                "WARNING: type %s is incompatible with %s",
                   field2DataType, operationType
                );
            } else if (isError.equals("TYPE_ERROR")) {
                errorMessage = String.format(
                    "WARNING: type %s is incompatible with %s",
                    field2DataType, field1DataType
                );
            }
        }

        return errorMessage;
    }

    /**
     * Add a new field to the fields list for operation.
     */
    public void addField() {
        fields.add("");
    }

    /**
     * Remove the field from the list of fields.
     *
     * @param index is the index of the field from the list.
     */
    public void removeField(int index) {
        fields.remove(index);
    }

    /**
     * Gets the list of fields based on operations.
     * If the operation is COLUMN_EQUALS then move the unused
     * field into a list.
     *
     * @return the list of fields.
     */
    public List<String> getFields() {

        // If Operation = COLUMN_EQUALS
        if ((operationType != null) && (fields.size() > 1) &&
            operationType.equals(OperationType.COLUMN_EQUALS.toString())) {
            // Move the unUsed fields into an empty list.
            unUsedFields.clear();
            for (int i = 1; i < fields.size(); i++) {
                unUsedFields.add(fields.get(i));
                fields.remove(i);
            }
        } else if ((operationType != null) && (unUsedFields.size() > 0) &&
                !operationType.equals(OperationType.COLUMN_EQUALS.toString())) {
            // Move the unUsed fields back to the fields list.
            for (String field: unUsedFields) {
                fields.add(field);
            }
            unUsedFields.clear();
        }
        return fields;
    }

    /**
     * Sets the list of fields.
     *
     * @param fields is the list of fields.
     */
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /**
     * Gets the type of operation.
     *
     * @return the operation type.
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Sets the operation type.
     *
     * @param operation is the type of operation.
     */
    public void setOperationType(String operation) {
        this.operationType = operation;
    }

    /**
     * Get the separator used when concatenating.
     *
     * @return the separator used when concatenating
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Set the separator used when concatenating.
     *
     * @param separator the separator used when concatenating
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
