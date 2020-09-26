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
 * This class handles the Else type of case that generate Jooq
 * query for the operation.
 */
public class Else extends Case {

    private static final String ELSE_TYPE = "ELSE";

    /**
     * Constructor to set the type of case.
     */
    public Else() {
        super(ELSE_TYPE);
    }

    /**
     * Dummy method to keep the compiler happy. The method is
     * implemented in If type case instead.
     *
     * @return null.
     */
    public Condition getCondition() {
        return null;
    }
}
