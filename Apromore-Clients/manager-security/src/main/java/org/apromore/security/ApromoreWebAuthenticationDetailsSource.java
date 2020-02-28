/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */

package org.apromore.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Implementation of {@link org.apromore.security.ApromoreAuthenticationDetailsSource} which builds the details object from
 * an <tt>HttpServletRequest</tt> object and <tt>UsernamePasswordAuthenticationToken</tt>, creating a {@code ApromoreWebAuthenticationDetails}.
 */
public class ApromoreWebAuthenticationDetailsSource implements
        ApromoreAuthenticationDetailsSource<HttpServletRequest, UsernamePasswordAuthenticationToken, ApromoreWebAuthenticationDetails> {

    /**
     * @param context the {@code HttpServletRequest} object.
     * @param token the {@code UsernamePasswordAuthenticationToken} object.
     * @return the {@code WebAuthenticationDetails} containing information about the current request
     */
    public ApromoreWebAuthenticationDetails buildDetails(HttpServletRequest context, UsernamePasswordAuthenticationToken token) {
        return new ApromoreWebAuthenticationDetails(context, token);
    }

}
