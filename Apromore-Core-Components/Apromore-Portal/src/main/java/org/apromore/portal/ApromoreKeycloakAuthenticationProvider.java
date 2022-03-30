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
package org.apromore.portal;

import org.apromore.manager.client.ManagerService;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.RoleType;
import org.apromore.portal.model.UserType;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An adaptation of {@link KeycloakAuthenticationProvider} which maps
 * authorities based on a user's roles in Apromore.
 *
 * @author Jane Hoh
 */
public class ApromoreKeycloakAuthenticationProvider extends KeycloakAuthenticationProvider {
    private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Autowired
    private ManagerService managerClient;

    public ApromoreKeycloakAuthenticationProvider(ManagerService manager) {
        managerClient = manager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if (token.getPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal =
                    (KeycloakPrincipal<KeycloakSecurityContext>) token.getPrincipal();
            UserType user = getUserFromPrincipal(kcPrincipal);

            //Add Apromore roles and permissions as authorities. First time users have the analyst role by default.
            if (user == null) {
                grantedAuthorities.addAll(getNewUserAuthorities());
            } else {
                for (RoleType role : user.getRoles()) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
                }
                for (PermissionType permission : user.getPermissions()) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
                }
            }
        }

        return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), mapGrantedAuthorities(grantedAuthorities));
    }

    private Collection<? extends GrantedAuthority> mapGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return grantedAuthoritiesMapper != null
                ? grantedAuthoritiesMapper.mapAuthorities(authorities)
                : authorities;
    }

    /**
     * Get Apromore user from the principal.
     */
    private UserType getUserFromPrincipal(KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal) {
        String userName = kcPrincipal.getName();

        AccessToken accessToken = kcPrincipal.getKeycloakSecurityContext().getToken();
        String email = accessToken.getEmail();
        userName = Objects.requireNonNullElse(userName, email);

        if (managerClient != null) {
            return managerClient.readUserByUsername(userName);
        }
        return null;
    }

    private List<GrantedAuthority> getNewUserAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ANALYST"));

        if (managerClient != null) {
            List<PermissionType> permissions = managerClient.getRolePermissions("ROLE_ANALYST");
            for (PermissionType permission : permissions) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        return grantedAuthorities;
    }
}
