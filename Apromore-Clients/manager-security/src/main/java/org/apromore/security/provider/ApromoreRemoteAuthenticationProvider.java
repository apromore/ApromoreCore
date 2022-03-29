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

package org.apromore.security.provider;

import org.apromore.security.ApromoreRemoteAuthenticationManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * Client-side object which queries a  {@link org.apromore.security.ApromoreRemoteAuthenticationManager} to validate an authentication request.
 * <p>
 * A new <code>Authentication</code> object is created by this class comprising the request <code>Authentication</code>
 * object's <code>principal</code>, <code>credentials</code> and the <code>GrantedAuthority</code>[]s returned by the
 * <code>ApromoreRemoteAuthenticationManager</code>.
 * <p>
 * The <code>ApromoreRemoteAuthenticationManager</code> should not require any special username or password setting on
 * the remoting client proxy factory to execute the call. Instead the entire authentication request must be
 * encapsulated solely within the <code>Authentication</code> request object. In practical terms this means the
 * <code>ApromoreRemoteAuthenticationManager</code> will <b>not</b> be protected by BASIC or any other HTTP-level
 * authentication.</p>
 * <p>If authentication fails, a <code>ApromoreRemoteAuthenticationException</code> will be thrown. This exception should
 * be caught and displayed to the user, enabling them to retry with alternative credentials etc.</p>
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ApromoreRemoteAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private ApromoreRemoteAuthenticationManager remoteAuthenticationManager;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.remoteAuthenticationManager, "remoteAuthenticationManager is mandatory");
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        Object credentials = authentication.getCredentials();
        String password = credentials == null ? null : credentials.toString();
        Authentication auth = remoteAuthenticationManager.attemptAuthentication(username, password);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, auth.getAuthorities());
        token.setDetails(auth.getDetails());
        return token;
    }

    public ApromoreRemoteAuthenticationManager getRemoteAuthenticationManager() {
        return remoteAuthenticationManager;
    }

    public void setRemoteAuthenticationManager(ApromoreRemoteAuthenticationManager remoteAuthenticationManager) {
        this.remoteAuthenticationManager = remoteAuthenticationManager;
    }

    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
