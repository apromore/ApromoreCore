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

/**
 * This is an abstract class that takes case of different
 * types of cases that i.e. If and Else in the view.
 */
public abstract class Case {
    protected Operation operation;
    protected String type;

    /**
     * Constructor to initialize operation input box and type.
     *
     * @param type is either IF or ELSE type of case.
     */
    public Case(String type) {
        operation = new Operation();
        this.type = type;
    }

    /**
     * Abstract method implemented by the child classes to generate
     * and return step query of the case block.
     *
     * @return the condition step query inside the case
     */
    public abstract Condition getCondition();

    /**
     * The method returns the type.
     *
     * @return type of the case statement.
     */
    public String getType() {
        return type;
    }

    /**
     * The method returns the operation object that is used to create
     * query for the THEN in case statement.
     *
     * @return the operations object.
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * The method sets the operation to the case statement.
     *
     * @param operation can be performed by the Cases as part of the
     *                   THEN.
     */
    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
