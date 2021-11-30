package org.apromore.portal;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Prevent users without login permissions from logging in.
 */
public class ApromoreKeycloakAuthenticationSuccessHandler extends KeycloakAuthenticationSuccessHandler {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreKeycloakAuthenticationSuccessHandler.class);

    public ApromoreKeycloakAuthenticationSuccessHandler (AuthenticationSuccessHandler fallback) {
        super(fallback);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation logs out a user if they are authenticated but do not have portal login permissions.
     * Otherwise, it redirects to <code>index.zul</code>.
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String[] loginAuthorizedRoles = {"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_ANALYST",
                "ROLE_OBSERVER", "ROLE_DESIGNER", "ROLE_DATA_SCIENTIST", "ROLE_OPERATIONS"};

        //Logout if the user does not have a role with login permissions
        if (authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).noneMatch(
                a -> Arrays.asList(loginAuthorizedRoles).contains(a))) {

            LOGGER.info("User \"{}\" does not have login permissions", authentication.getName());
            response.sendRedirect("/logout");

        } else {
            LOGGER.info("User \"{}\" login via keycloak", authentication.getName());
            super.onAuthenticationSuccess(request, response, authentication);
        }


    }
}
