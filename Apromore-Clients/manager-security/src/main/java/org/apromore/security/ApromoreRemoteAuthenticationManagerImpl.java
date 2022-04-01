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

import org.apromore.security.exception.ApromoreRemoteAuthenticationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * Server-side processor of a remote authentication request.
 * <p>
 * This bean requires no security interceptor to protect it. Instead, the bean uses the configured
 * <code>AuthenticationManager</code> to resolve an authentication request.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ApromoreRemoteAuthenticationManagerImpl implements ApromoreRemoteAuthenticationManager, InitializingBean {

    private AuthenticationManager authenticationManager;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.authenticationManager, "authenticationManager is required");
    }

    public Authentication attemptAuthentication(String username, String password) throws ApromoreRemoteAuthenticationException {
        UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(username, password);

        try {
            return authenticationManager.authenticate(request);
        } catch (AuthenticationException authEx) {
            throw new ApromoreRemoteAuthenticationException(authEx.getMessage());
        }
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
