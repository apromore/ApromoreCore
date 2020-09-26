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

import org.apromore.etlplugin.portal.models.templateTableModel.*;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * View model for add rules modal.
 * @author janeh
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AddRulesViewModel {

    @WireVariable
    private TemplateTableBean templateTableBean;
    private Column column;
    private boolean newColumn = false;
    private List<Case> ifCases;
    private Else elseCase = null;
    private Rule rule;

    /**
     * Initialize the view model.
     * @param colName the column rules are being applied to
     */
    @Init
    public void init(@ExecutionArgParam("colName") Column colName) {
        column = colName;
        ifCases = new ArrayList<>();

        if (colName != null && colName.getRule() != null) {
            // If last case is else
            for (Case c: colName.getRule().getCases()) {
                if (c instanceof Else) {
                    elseCase = (Else) c;
                } else {
                    ifCases.add(c);
                }
            }
        } else if (colName != null) {
            elseCase = new Else();
            Operation operation = new Operation();
            operation.setOperationType(OperationType.COLUMN_EQUALS.toString());
            operation.setFields(
                new ArrayList<>(
                    Arrays.asList(
                        new String[] {
                            colName.getOriginTable() +
                                "." +
                                colName.getOriginalColumnName()
                        }
                    )
                )
            );
            elseCase.setOperation(operation);
        } else {
            elseCase = new Else();
        }
    }

    /**
     * Describe what to do when "IF" button is pressed.
     */
    @Command
    @NotifyChange("cases")
    public void onClickIf() {
        ifCases.add(new If());
    }

    /**
     * Describe what to do when "ELSE" button is pressed.
     */
    @Command
    @NotifyChange("cases")
    public void onClickElse() {
        if (elseCase == null) {
            elseCase = new Else();
        }
    }

    /**
     * Delete all add rules content.
     */
    @Command
    @NotifyChange("cases")
    public void clearContent() {

        ifCases.clear();
        elseCase = null;
    }

    /**
     * Update the "newColumn" value when the Add Result in New Column
     * checkbox is checked/unchecked.
     */
    @Command
    public void updateNewColumn() {
        if (newColumn) {
            newColumn = false;
        } else {
            newColumn = true;
        }
    }

    /**
     * Describe what to do when the "Done" button is pressed.
     *
     * @param target the window to close once a rule has been submitted
     */
    @Command
    public void onSubmit(@BindingParam("target")Component target) {
        List<Case> cases = getCases();

        if (cases.size() == 0) {
            Messagebox.show(
                "ERROR: Fields must not be empty.",
                "ERROR",
                0,
                Messagebox.ERROR
            );
            return;
        }

        rule = new Rule(getCases());

        if (rule.getQuery() == null) {
            Messagebox.show(
                "ERROR: Fields must not be empty.",
                "ERROR",
                0,
                Messagebox.ERROR
            );
            return;
        }

        column.setRule(rule);
        templateTableBean.updateTemplateTable();
        BindUtils.postNotifyChange(
            null,
            null,
            templateTableBean,
            "templateTable"
        );
        BindUtils.postNotifyChange(
            null,
            null,
            templateTableBean,
            "columns"
        );
        target.detach();
    }

    /**
     * Return the title to display on the window.
     * @return the window title;
     */
    public String getTitle() {

        String title = "Add Rule: ";

        if (column != null) {
            title += column.getColumnName();
        }

        return title;
    }

    /**
     * Get all cases.
     *
     * @return a list of if and else cases
     */
    public List<Case> getCases() {
        List<Case> cases = new ArrayList<>(ifCases);

        if (elseCase != null) {
            cases.add(elseCase);
        }

        return cases;
    }

    /**
     * Remove an if case.
     * @param index the index of the if case to remove
     */
    @Command
    @NotifyChange("cases")
    public void removeIfCase(@BindingParam("index") int index) {
        ifCases.remove(index);
    }

    /**
     * Remove the else case.
     */
    @Command
    @NotifyChange("cases")
    public void removeElseCase() {
        elseCase = null;
    }

    /**
     * Indicates whether the result should be placed in a newColumn.
     * @return true if the newColumn box is checked.
     */
    public boolean getNewColumn() {
        return newColumn;
    }
}
