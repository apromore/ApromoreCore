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

package org.apromore.security.filter;

import javax.servlet.http.HttpServletRequest;

import org.apromore.security.ApromoreAuthenticationDetailsSource;
import org.apromore.security.ApromoreWebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Apromore needs to have some extra information sent from the server to operate.
 * This class allows the extra info to be include while no effecting the existing security code.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ApromoreUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    protected ApromoreAuthenticationDetailsSource<HttpServletRequest, UsernamePasswordAuthenticationToken, ?> authenticationDetailsSource =
            new ApromoreWebAuthenticationDetailsSource();

    /**
     * Constructor that called the Super classes constructor.
     */
    public ApromoreUsernamePasswordAuthenticationFilter() {
        super();
    }


    /**
     * Provided so that subclasses may configure what is put into the authentication request's details
     * property.
     * @param request that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details set
     */
    @Override
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request, authRequest));
    }


}
