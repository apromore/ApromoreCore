/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2022 Apromore Pty Ltd.
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

package org.apromore.service.logimporter.exception;

/**
 * Exception for when a log metadata isn't found or is not valid.
 *
 * @author frankm
 */
public class InvalidLogMetadataException extends Exception {

    /**
     * Default Constructor.
     */
    public InvalidLogMetadataException() {
    }

    /**
     * Constructor.
     *
     * @param message the message to put with the exception.
     */
    public InvalidLogMetadataException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause The exception that caused this exception to be thrown.
     */
    public InvalidLogMetadataException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message the message to put with the exception.
     * @param cause   The exception that caused this exception to be thrown.
     */
    public InvalidLogMetadataException(String message, Throwable cause) {
        super(message, cause);
    }
}
