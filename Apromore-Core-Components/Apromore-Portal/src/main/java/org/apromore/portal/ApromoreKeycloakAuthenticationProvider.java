package org.apromore.portal;

import org.apromore.manager.client.ManagerService;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

            //First time users have analyst and user role by default
            List<String> roles = user == null ? Arrays.asList("ROLE_ANALYST", "ROLE_USER") :
                    user.getRoles().stream().map(RoleType::getName).collect(Collectors.toList());

            for (String role : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), mapAuthorities(grantedAuthorities));
    }

    private Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return grantedAuthoritiesMapper != null
                ? grantedAuthoritiesMapper.mapAuthorities(authorities)
                : authorities;
    }

    /**
     * Get Apromore user from the principal
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
}
