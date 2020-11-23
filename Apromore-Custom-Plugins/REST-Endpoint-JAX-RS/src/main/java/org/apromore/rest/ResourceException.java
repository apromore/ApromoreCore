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
package org.apromore.rest;

import javax.ws.rs.core.Response.Status;

/**
 * Exceptions occurring within {@link UserResource} methods.
 */
public class ResourceException extends Exception {

    private Status status;

    /**
     * @param newStatus  machine-legible HTTP response status code
     * @param message  human-legible error explanation
     */
    public ResourceException(final Status newStatus, final String message) {
        super(message);
        status = newStatus;
    }

    /**
     * @return the HTTP response status code
     */
    public Status getStatus() {
        return status;
    }
}
