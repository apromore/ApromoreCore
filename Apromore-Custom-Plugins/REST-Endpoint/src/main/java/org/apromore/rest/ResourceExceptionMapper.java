/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translate {@link ResourceException}s into HTTP {@link Response}s.
 */
@Provider
public final class ResourceExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceExceptionMapper.class);

    @Override
    public Response toResponse(final Throwable throwable) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        
        if (throwable instanceof ResourceException) {
            status = ((ResourceException) throwable).getStatus();
        } else {
            LOGGER.warn("Error processing REST resource", throwable);
        }

        Response.ResponseBuilder builder = Response.status(status);

        // Always include the error message as the plain text body of the response
        builder.type(MediaType.TEXT_PLAIN)
               .entity(throwable.getMessage());

        // HTTP 401 Unauthorized response must indicate that Basic authentication is required
        if (status == Response.Status.UNAUTHORIZED) {
            builder.header("WWW-Authenticate", "Basic realm=\"apromore\"");
        }

        return builder.build();
    }
}
