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
package org.apromore.etlplugin.portal.viewModels;

import org.apromore.etlplugin.portal.models.sidePanelModel.FileMetaData;
import org.apromore.etlplugin.portal.models.templateTableModel.*;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * View model for if and else conditions.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ConditionViewModel {
    @WireVariable
    private FileMetaData fileMetaData;
    private List<String> conditions = new ArrayList<>();
    private List<String> operations = new ArrayList<>();
    private List<String> columns = new ArrayList<>();
    private Case ifElseCase;
    private boolean showSeparator = false;

    /**
     * Initialize the view model.
     * @param ifElseCase the if or else case this view model is associated with
     */
    @Init
    public void init(@ExecutionArgParam("ifElseCase") Case ifElseCase) {
        this.ifElseCase = ifElseCase;

        if (ifElseCase != null) {
            this.showSeparator = ifElseCase
                .getOperation()
                .getOperationType()
                .toUpperCase()
                .equals(OperationType.CONCAT.toString());
        } else {
            this.showSeparator = false;
        }

        for (ComparisonType type: ComparisonType.values()) {
            conditions.add(type.toString());
        }

        for (OperationType type: OperationType.values()) {
            operations.add(type.toString());
        }

        if (fileMetaData != null) {
            // Prepare the Available Columns
            for (String tableName : fileMetaData.getInputFileMeta().keySet()) {
                for (String columnName: fileMetaData.getInputFileMeta()
                        .get(tableName)) {
                    columns.add(tableName + "." + columnName);
                }
            }
        }
    }

    /**
     * Notify the case of any changes and do nothing else.
     */
    @Command
    @NotifyChange("ifElseCase")
    public void notifyCase() {
    }

    /**
     * Determine whether the separator field should be shown.
     */
    @Command
    @NotifyChange({"ifElseCase", "showSeparator"})
    public void notifySeparator() {
        showSeparator = ifElseCase
            .getOperation()
            .getOperationType()
            .toUpperCase()
            .equals(OperationType.CONCAT.toString());
    }

    /**
     * Add a field to the operations.
     */
    @Command
    @NotifyChange("ifElseCase")
    public void addField() {
        ifElseCase.getOperation().addField();
    }

    /**
     * Add a field to the operations.
     */
    @Command
    @NotifyChange("ifElseCase")
    public void removeField() {
        int fieldsSize = ifElseCase.getOperation().getFields().size();
        if (fieldsSize > 1) {
            ifElseCase.getOperation().removeField(fieldsSize - 1);
        }
    }

    /**
     * Describes what should happen when the "AND" button is clicked.
     */
    @Command
    @NotifyChange("ifElseCase")
    public void onClickAnd() {
        if (ifElseCase instanceof If) {
            ((If) ifElseCase).addOperator(Operator.AND);
        }
    }

    /**
     * Describes what should happen when the "OR" button is clicked.
     */
    @Command
    @NotifyChange("ifElseCase")
    public void onClickOr() {
        if (ifElseCase instanceof If) {
            ((If) ifElseCase).addOperator(Operator.OR);
        }
    }

    /**
     * Remove a condition based on its index.
     *
     * @param index the index of the condition to remove.
     */
    @Command
    @NotifyChange("ifElseCase")
    public void deleteCondition(@BindingParam("index") int index) {
        if (ifElseCase instanceof If) {
            ((If) ifElseCase).removeComparison(index);
        }
    }

    /**
     * Get the if or else case associated with this view model.
     * @return an if or else case
     */
    public Case getIfElseCase() {
        return ifElseCase;
    }

    /**
     * Set the if or else case associated with this view model.
     * @param ifElseCase an if or else case
     */
    public void setIfElseCase(Case ifElseCase) {
        this.ifElseCase = ifElseCase;
    }

    /**
     * Get a list of conditions to choose from.
     * @return a list of conditions.
     */
    public List<String> getConditions() {
        return conditions;
    }

    /**
     * Get a list of operations to choose from.
     * @return a list of operations
     */
    public List<String> getOperations() {
        return operations;
    }

    /**
     * Get a list of columns to choose from.
     * @return a list of columns
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * Get whether the separator field should be shown.
     * @return true if the separator field should be shown
     */
    public boolean getShowSeparator() {
        return showSeparator;
    }
}
