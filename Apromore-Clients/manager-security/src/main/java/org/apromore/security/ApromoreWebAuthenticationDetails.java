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

package org.apromore.security;

import javax.servlet.http.HttpServletRequest;

import org.apromore.portal.model.UserType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Used to store the extra info needed to allow Apromore to work correctly.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ApromoreWebAuthenticationDetails extends WebAuthenticationDetails {

    private final UserType userDetails;

    /**
     * Records the remote address and will also set the session Id if a session
     * already exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public ApromoreWebAuthenticationDetails(final HttpServletRequest request, final UsernamePasswordAuthenticationToken authRequest) {
        super(request);
        this.userDetails = (UserType) authRequest.getDetails();
    }

    /**
     * Used to get the User type used by Apromore.
     * @return the UserType details
     */
    public UserType getUserDetails() {
        return userDetails;
    }

    /**
     * {@inheritDoc}
     *
     * @return as the superclass, plus the Apromore-specific user details
     */
    @Override
    public String toString() {
        return super.toString() + "; UserDetails=" + userDetails;
    }
}
