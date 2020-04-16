/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.csvimporter;

import java.util.List;

/**
 * Indicates that a CSV file is too malformed to be processed at all.
 */
public class InvalidCSVException extends Exception {

    // Member fields

    /**
     * The unparsed text of invalid rows.
     *
     * This is retained in case the user needs to download it for troubleshooting purposes.
     * It's okay for it to be null; this indicates that the user doesn't need to be prompted
     * for whether they want to download the error report, for instance.
     */
    private List<String> invalidRows;


    // Constructors

    public InvalidCSVException(String message) {
        super(message);
    }

    public InvalidCSVException(String message, List<String> invalidRows) {
        super(message);
        this.invalidRows = invalidRows;
    }


    // Accessors

    /**
     * @return the unparsed text of invalid rows; may be null
     */
    public List<String> getInvalidRows() {
        return invalidRows;
    }
};
