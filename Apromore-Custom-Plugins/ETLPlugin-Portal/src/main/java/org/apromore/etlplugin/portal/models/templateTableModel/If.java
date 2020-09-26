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

import org.jooq.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the If case type operation. The If case
 * has 2 main parts. First the conditions that is a combination
 * of multiple comparisons. Second is operation(output) that is part of
 * THEN statement.
 */
public class If extends Case {
    private static final String IF_TYPE = "IF";
    private List<Comparison> comparisons;
    private List<Operator> operators;

    /**
     * Construction to initialise list of comparison and operators;
     * add a default comparison to the list for UI.
     */
    public If() {
        super(IF_TYPE);
        comparisons = new ArrayList<Comparison>();
        comparisons.add(new Comparison());
        operators = new ArrayList<Operator>();
    }

    /**
     * This method traverse through the list of comparisons and
     * prepare the condition for the If statement.
     *
     * @return the condition for the If case statement
     */
    public Condition getCondition() {

        if ((comparisons.size() < 1) ||
            comparisons.size() != (operators.size() + 1)) {
            return null;
        }

        //Prepare the when statement with series of comparisons
        // and operators.
        Condition stepComparison = comparisons.get(0).compare();
        for (int i = 1; i < comparisons.size(); i++) {
            if (operators.get(i - 1).equals(Operator.AND)) {
                stepComparison = stepComparison
                                .and(comparisons.get(i).compare());
            } else if (operators.get(i - 1).equals(Operator.OR)) {
                stepComparison = stepComparison
                                .or(comparisons.get(i).compare());
            }
        }

        return stepComparison;
    }

    /**
     * Add new comparison object to the list comparisons.
     */
    public void addComparison() {
        comparisons.add(new Comparison());
    }

    /**
     * Adds new operator to the list of operators.
     *
     * @param operator is either AND or OR for conditions.
     */
    public void addOperator(Operator operator) {
        operators.add(operator);
        comparisons.add(new Comparison());
    }

    /**
     * This method gets the AND/OR operator associated with a comparison.
     * It is called from the view.
     * @param comparison the comparison the operator is associated with.
     * @return either AND OR or NULL
     */
    public String getOperator(Comparison comparison) {

        int comparisonIndex = this.comparisons.indexOf(comparison);

        if (comparisonIndex > 0) {
            return this.operators.get(comparisonIndex - 1).toString();
        }

        return null;
    }

    /**
     * The method removes the comparison and operator from the list.
     * This method is triggered from the view.
     *
     * @param index is the identifier in the list of comparison.
     */
    public void removeComparison(int index) {

        if (comparisons.size() <= 1) {
            return;
        }

        if (index < comparisons.size()) {
            comparisons.remove(index);
        }

        if ((index - 1) < operators.size() && (index - 1) >= 0) {
            operators.remove(index - 1);
        }
    }

    /**
     * Gets the comparison list.
     *
     * @return the list of comparisons.
     */
    public List<Comparison> getComparisons() {
        return comparisons;
    }

    /**
     * Sets the comparisons.
     *
     * @param comparisons is list of comparisons of a case statement.
     */
    public void setComparisons(List<Comparison> comparisons) {
        this.comparisons = comparisons;
    }

    /**
     * Gets the list of operators.
     *
     * @return the list of operators.
     */
    public List<Operator> getOperators() {
        return operators;
    }

    /**
     * Sets the list of operators.
     *
     * @param operators is a list of operators.
     */
    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }
}
