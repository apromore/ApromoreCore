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

package org.apromore.security.exception;

import org.springframework.security.authentication.rcp.RemoteAuthenticationException;

/**
 * /**
 * Thrown if a <code>ApromoreRemoteResetManager</code> cannot validate the presented reset request.
 * <p>
 * This is thrown rather than the normal <code>AuthenticationException</code> because
 * <code>AuthenticationException</code> contains additional properties which may cause issues for
 * the remoting protocol.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ApromoreRemoteResetException extends RemoteAuthenticationException {

    /**
     * Constructs a <code>ApromoreRemoteResetException</code> with the
     * specified message and no root cause.
     *
     * @param msg the detail message
     */
    public ApromoreRemoteResetException(String msg) {
        super(msg);
    }
}
