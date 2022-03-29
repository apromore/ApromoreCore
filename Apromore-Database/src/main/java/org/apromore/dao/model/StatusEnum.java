/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.dao.model;

/**
 * Enumeration to definite all the different HistoryEvent Statuses that can occur.
 *
 * @author Cameron James
 * @since 1.0
 */
public enum StatusEnum {

    // Importing Models
    START("START"),
    FINISHED("FINISHED"),
    ERROR("ERROR");

    private String status;

    private StatusEnum(String status) {
        this.status = status;
    }

    /**
     * Get the name of the Enum.
     * @return the name.
     */
    public String getName() {
        return toString();
    }

    /**
     * Get the status of the Enum.
     * @return the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the status as a String.
     * @return the status as a string.
     */
    @Override
    public String toString() {
        return this.name();
    }
}
