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

import org.jooq.CaseConditionStep;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.when;

/**
 * This class manages the rules associated with the column and
 * prepared the query with the jooq.
 */
public class Rule {
    private List<Case> cases;

    /**
     * Constructor to initialise the list of cases in the rule.
     */
    public Rule() {
        cases = new ArrayList<Case>();
    }

    /**
     * Constructor to initialise the list of cases in the rule.
     *
     * @param casesList is the list cases in the rule.
     */
    public Rule(List<Case> casesList) {
        cases = casesList;
    }

    /**
     * Get the query of the rule prepared by the jooq.
     *
     * @return the field query for the column.
     */
    public Field getQuery() {

        if (cases.size() < 1) {
            return null;
        }

        // If we only have one default statement in UI
        if (cases.get(0) instanceof Else) {
            return cases.get(0).getOperation().apply();
        }

        Case stepCase = cases.get(0);
        Field then = stepCase.getOperation().apply();

        if (then == null) {
            return null;
        }

        //Prepare the when statement for the cases statements for the column.
        CaseConditionStep caseConditionStep
            = when(stepCase.getCondition(), stepCase.getOperation().apply());
        for (int i = 1; i < cases.size(); i++) {
            stepCase = cases.get(i);
            then = stepCase.getOperation().apply();

            if (then == null) {
                return null;
            }

            if (stepCase instanceof If) {
                caseConditionStep.when(stepCase.getCondition(),
                        stepCase.getOperation().apply());
            } else if (stepCase instanceof Else) {
                caseConditionStep.otherwise(stepCase.getOperation().apply());
                return caseConditionStep;
            }
        }

        return caseConditionStep;
    }

    /**
     * Add If case to the list of cases for the rule.
     *
     * @param newCase is a new case.
     */
    public void addIfCase(If newCase) {
        cases.add(newCase);
    }

    /**
     * Gets the list of cases.
     *
     * @return the list of cases.
     */
    public List<Case> getCases() {
        return cases;
    }

    /**
     * Sets the list of cases.
     *
     * @param cases is the list of cases.
     */
    public void setCases(List<Case> cases) {
        this.cases = cases;
    }
}
