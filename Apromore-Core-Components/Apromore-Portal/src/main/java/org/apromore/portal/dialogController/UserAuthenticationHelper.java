/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.portal.dialogController;

import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.security.filter.SecurityPrincipal;
import org.slf4j.Logger;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserAuthenticationHelper {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(UserAuthenticationHelper.class);

    private User user;

    public UserAuthenticationHelper(User user) {
        this.user = user;
    }

    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        LOGGER.debug("In UserAuthenticationHelper.attemptAuthentication(..)");

        try {
            final User userAccount = user;
            LOGGER.debug(">>>>> after userRepository lookup of user, userAccount {}: ", userAccount);

            final String someToken = "someToken";

            final AbstractAuthenticationToken userAuthenticationToken = authUserByToken(someToken, userAccount);
            if (userAuthenticationToken == null)
                throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));

            return authenticatedToken(userAccount, userAuthenticationToken);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Failed to find the user or the password was incorrect!");
        }
    }

    /**
     * Retrieves the correct ROLE type depending on the access level, where access level is an Integer.
     * Basically, this interprets the access value whether it's for a regular user or admin.
     *
     * @param access an integer value representing the access of the user
     * @return collection of granted authorities
     */
    public List<GrantedAuthority> getAuthorities(Set<Role> access) {
        final List<GrantedAuthority> authList = new ArrayList<>();

        for (Role role : access) {
            authList.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authList;
    }

    private Authentication authenticatedToken(User user, Authentication original) {
        final List<GrantedAuthority> authorities = getAuthorities(user.getRoles());

        final PreAuthenticatedAuthenticationToken authenticated =
                new PreAuthenticatedAuthenticationToken(original.getPrincipal(),
                        original.getCredentials(), authorities);

        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        LOGGER.info(">>>>> authenticatedToken PreAuthenticatedAuthenticationToken authenticated: ", authenticated);

        return authenticated;
    }

    private AbstractAuthenticationToken authUserByToken(String tokenRaw, User userObj) {
        AbstractAuthenticationToken authToken = null;

        try {
            // String user = tokenManager.verifyAndExtractUser(tokenRaw);
            if (userObj != null) {
                final Principal principal = new SecurityPrincipal(userObj.getUsername());
                final List<GrantedAuthority> authorities = getAuthorities(userObj.getRoles());

                return new PreAuthenticatedAuthenticationToken(principal, null, authorities);
            }
        } catch (Exception e) {
            LOGGER.error("Error during authUserByToken", e);
        }

        return authToken;
    }
}
