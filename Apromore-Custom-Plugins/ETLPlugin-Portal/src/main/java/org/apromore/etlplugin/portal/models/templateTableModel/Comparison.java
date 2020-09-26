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
import org.jooq.Condition;
import org.jooq.Field;

import static org.jooq.impl.DSL.*;

/**
 * The class handles comparisons step query that the Jooq is responsible for
 * creating.
 */
public class Comparison {

    private String field1 = null;
    private String field2 = null;
    private String operation = null;
    private boolean errorFlag = false;
    private String errorMessage = "";
    private FileMetaData fileMetaData;

    /**
     * Constructor to initialise and inject fileMetaData.
     *
     * @param fileMetaData is meta data bean.
     */
    public Comparison(FileMetaData fileMetaData) {
        this.fileMetaData = fileMetaData;
    }

    /**
     * Constructor to initialise the fileMetaData.
     */
    public Comparison() {
        fileMetaData = SpringBeanContext
                .getBean(FileMetaData.class);
    }

    private boolean isString(String column) {
        return StringUtils.getColumnType(column).equals("STRING") &&
            !fileMetaData.isColumnInList(column);
    }

    /**
     * The method uses the field1, field2 and operation Type to generate
     * step query using Jooq.
     *
     * @return the condition object.
     */
    public Condition compare() {
        if (field1 == null || field2 == null || operation == null) {
            return null;
        }

        Condition stepCondition = null;

        Field tableField1;
        Field tableField2;

        // If field1 is a string and not in list then make it under quotes.
        if (isString(field1)) {
            tableField1 = val(field1);
        } else {
            tableField1 = field(field1);
        }

        if (isString(field2)) {
            tableField2 = val(field2);
        } else {
            tableField2 = field(field2);
        }

        // Find the Operation type and generate the Jooq query.
        if (operation.toUpperCase()
                .equals(ComparisonType.GREATER_THAN.toString())) {
            stepCondition = tableField1.gt(field(tableField2));
        } else if (operation.toUpperCase()
                .equals(ComparisonType.LESS_THAN.toString())) {
            stepCondition = tableField1.lt(field(tableField2));
        }  else if (operation.toUpperCase()
                .equals(ComparisonType.EQUALS.toString())) {
            stepCondition = tableField1.eq(field(tableField2));
        }  else if (operation.toUpperCase()
                .equals(ComparisonType.NOT_EQUAL.toString())) {
            stepCondition = tableField1.ne(field(tableField2));
        }  else if (operation.toUpperCase()
                .equals(ComparisonType.LESS_THAN_EQUAL.toString())) {
            stepCondition = tableField1.le(field(tableField2));
        }  else if (operation.toUpperCase()
                .equals(ComparisonType.GREATER_THAN_EQUAL.toString())) {
            stepCondition = tableField1.ge(field(tableField2));
        }

        return stepCondition;
    }

    /**
     * Checks if the data Type of field1 and field2 are incorrect. If yes
     * then raise the error flag and sets the proper error Message.
     */
    private void checkDataTypeError() {

        if (field1 == null ||
            field1.equals("") ||
            field2 == null ||
            field2.equals("")) {
            return;
        }

        String field1DataType = fileMetaData.getColumnDataType(field1);
        String field2DataType = fileMetaData.getColumnDataType(field2);

        if (field1DataType.equals("STRING") &&
                field1DataType.equals(field2DataType)) {
            errorFlag = false;
        } else if ((field1DataType.equals("INT") ||
                field1DataType.equals("DOUBLE")) &&
                (field2DataType.equals("INT") ||
                (field2DataType.equals("DOUBLE")))) {
            errorFlag = false;
        } else if (field1DataType.equals("BOOLEAN") &&
                field2DataType.equals("BOOLEAN") &&
                (operation.equals(ComparisonType.NOT_EQUAL.toString()) ||
                operation.equals(ComparisonType.EQUALS.toString()))) {
            errorFlag = false;
        } else {
            errorFlag = true;
            errorMessage = String.format(
                "WARNING: cannot compare %s with %s type for %s operation.",
                    field1DataType, field2DataType, operation
            );
        }
    }

    /**
     * Get the field1 for the comparison.
     *
     * @return the field1.
     */
    public String getField1() {
        return field1;
    }

    /**
     * Set the Field1 for the comparison.
     *
     * @param field1 is the first field on the left.
     */
    public void setField1(String field1) {
        this.field1 = field1;
        checkDataTypeError();
    }

    /**
     * Get the field2 for the comparison.
     *
     * @return the field2.
     */
    public String getField2() {
        return field2;
    }

    /**
     * Set the Field2 for the comparison.
     *
     * @param field2 is the second field on the right.
     */
    public void setField2(String field2) {
        this.field2 = field2;
        checkDataTypeError();
    }

    /**
     * Get the operation type.
     *
     * @return the operation type.
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Sets the operation type.
     *
     * @param operation is the type of operation the comparison
     *                  executes.
     */
    public void setOperation(String operation) {
        this.operation = operation;
        checkDataTypeError();
    }

    /**
     * Get error flag.
     * @return boolean variable
     */
    public boolean getErrorFlag() {
        return errorFlag;
    }

    /**
     * Set error flag.
     * @param errorFlag boolean error flag.
     */
    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }

    /**
     * Get error message.
     * @return error message string.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set error message.
     * @param errorMessage error message.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
