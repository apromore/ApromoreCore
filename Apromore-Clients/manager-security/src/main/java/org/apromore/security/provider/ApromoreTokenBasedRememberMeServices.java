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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apromore.portal.model.MembershipType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.RoleType;
import org.apromore.portal.model.SearchHistoriesType;
import org.apromore.portal.model.UserType;
import org.apromore.security.model.ApromorePermissionDetails;
import org.apromore.security.model.ApromoreRoleDetails;
import org.apromore.security.model.ApromoreSearchHistoryDetails;
import org.apromore.security.model.ApromoreUserDetails;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

/**
 * Apromore Token based Remember Me Service, used to find previously logged in users.
 *
 * {@inheritDoc}
 *
 * @author Cameron James
 */
public class ApromoreTokenBasedRememberMeServices extends TokenBasedRememberMeServices {

    /**
     * @deprecated Use with-args constructor
     */
//    @Deprecated
//    public ApromoreTokenBasedRememberMeServices() {
//        super();
//    }


    /**
     * Constructor.
     * @param key the cookie name.
     * @param userDetailsService User Service.
     */
    public ApromoreTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }


    /**
     * Creates the final <tt>Authentication</tt> object returned from the <tt>autoLogin</tt> method.
     * <p>
     * By default it will create a <tt>RememberMeAuthenticationToken</tt> instance.
     *
     * @param request       the original request. The configured <tt>AuthenticationDetailsSource</tt> will
     *                      use this to build the details property of the returned object.
     * @param user          the <tt>UserDetails</tt> loaded from the <tt>UserDetailsService</tt>. This will be
     *                      stored as the principal.
     *
     * @return the <tt>Authentication</tt> for the remember-me authenticated user
     */
    @Override
    protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {
        RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(getKey(), user, user.getAuthorities());
        auth.setDetails(convertUserTypes((ApromoreUserDetails) user));
        return auth;
    }


    /* Used to convert the security objects into Apromore objects. */
    private UserType convertUserTypes(ApromoreUserDetails user) {
        UserType userType = new UserType();
        userType.setId(user.getId());
        userType.setLastName(user.getLastName());
        userType.setFirstName(user.getFirstName());
        userType.setUsername(user.getUsername());

        MembershipType membership = new MembershipType();
        membership.setEmail(user.getEmail());
        userType.setMembership(membership);

        RoleType newRole;
        for (ApromoreRoleDetails role : user.getRoles()) {
            newRole = new RoleType();
            newRole.setId(role.getId());
            newRole.setName(role.getName());
            userType.getRoles().add(newRole);
        }

        PermissionType permissionType;
        for (ApromorePermissionDetails permission : user.getPermissions()) {
            permissionType = PermissionType.getPermissionTypeById(permission.getId());

            if (!userType.getPermissions().contains(permissionType)){
                userType.getPermissions().add(permissionType);
            }
        }

        SearchHistoriesType searchHistoryType;
        for (ApromoreSearchHistoryDetails searchHistory : user.getSearchHistories()) {
            searchHistoryType = new SearchHistoriesType();
            searchHistoryType.setNum(searchHistory.getId());
            searchHistoryType.setSearch(searchHistory.getSearchString());
            userType.getSearchHistories().add(searchHistoryType);
        }

        return userType;
    }
}
