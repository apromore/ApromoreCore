package org.apromore.portal;

/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import com.nimbusds.jwt.JWTClaimsSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.security.helper.JwtHelper;
import org.slf4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.ExecutionInit;

/**
 * Callbacks during the lifecycle of ZK scopes.
 */
public class ApromoreZKListener implements ExecutionInit {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreZKListener.class);

    private static final String COOKIE_NAME = "App_Auth";
    private static final String HARDCODED_INITIAL_TEST_ONLY_JWT =
            "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImdpdmVuTmFtZSI6IlRlc3QiLCJmYW1pbHlOYW1lIjoiVXNlciIsImV4cCI6MTYxOTE1NDgyNTkwMywiaWF0IjoxNjE5MTQ3NjI1OTAzLCJlbWFpbCI6IiIsImtjdXNlcmlkIjoiZjoyY2JjM2E2NS0yNzNhLTRjMTEtYjFlNC0zZjhkNjVlM2JiYjg6OCJ9.XtEHtIevrp7PVbYLwGbjBbvB32kZ8ZM2au-fbKGn03zjLYXojibRXbLfqJb1p45WbEn9nvoGn-oFvmSecWQsQGBSF3iGhy1RjpPgKuZxMdKBaGDaLdqix0OgQYFOk2RLaSRoBTVEdd579UCIavF66vVW9rKCC4zE2AIFSFFFPgJK25lwq7c31DyYjs5deA_SlmV5z1tfkk27ksrOuVmy-LzmAf81fFp5OTftTvM3Zfb04AmKBnMLyIuSb63zdlRlHrgjVbsE29CIr4u0NIdNt-hOrjXXe_j8PMO26tsycG0ku2Fg8ZYcvozWeTa3uvH4dvYN7Dsrn44A9P2tDwze8w";

    /**
     * {@inheritDoc}
     *
     * This implementation logs the user out if the security token has expired, or refreshes it
     * if it's still valid but close to expiry.
     */
    @Override
    public void init(final Execution exec, final Execution parent) {
        LOGGER.debug("Initialize execution {} with parent {}", exec, parent);

        // If there is a parent execution, it will have already performed the required work
        if (parent != null) {
            return;
        }

        final HttpServletRequest httpServletRequest = (HttpServletRequest) exec.getNativeRequest();
        final HttpServletResponse httpServletResponse = (HttpServletResponse) exec.getNativeResponse();

        // If we are not using Keycloak, we don't have to manage JWTs
        final ConfigBean config = (ConfigBean) SpringUtil.getBean("portalConfig");
        final boolean usingKeycloak = config.isUseKeycloakSso();
        if (!usingKeycloak) {
            LOGGER.debug("Skipping JWT check because not using Keycloak");
            return;
        }

        final String appAuthHeader = JwtHelper.readCookie(httpServletRequest, COOKIE_NAME);
        LOGGER.debug("Read App_Auth cookie: {}", appAuthHeader);

        try {
            final JWTClaimsSet jwtClaimsSet = JwtHelper.getClaimsSetFromJWT(appAuthHeader);
            final String issuedAtStr = (String)jwtClaimsSet.getStringClaim(
                        JwtHelper.STR_JWT_KEY_ISSUED_AT);
            LOGGER.debug("issuedAtStr {}", issuedAtStr);
            final String expiryAtStr = (String)jwtClaimsSet.getStringClaim(
                        JwtHelper.STR_JWT_EXPIRY_TIME);
            LOGGER.debug("expiryAtStr {}", expiryAtStr);

            // If the token is expired, sign the session out
            if (JwtHelper.isJwtExpired(jwtClaimsSet, issuedAtStr, expiryAtStr)) {
                LOGGER.debug("JWT is expired");
                signOut(exec.getSession());
            } else if (true) {  // TODO: test if expiry is close
                LOGGER.debug("JWT needs refresh");
                refreshSessionTimeout(exec, HARDCODED_INITIAL_TEST_ONLY_JWT, httpServletResponse);
            } else {
                LOGGER.debug("JWT is fresh");
            }
        } catch (final Exception e) {
            LOGGER.error("JWT expiration/refresh check failed; terminating session", e);
            signOut(exec.getSession());
        }
    }

    /**
     * Issue a new JWT with an extended expiry.
     */
    private static void refreshSessionTimeout(
            final Execution exec,
            final String existingJwtStr,
            final HttpServletResponse httpServletResponse) {
        LOGGER.debug(">>>>> Refreshing session timeout");
        try {
            final RefreshTokenResponse refreshTokenResponse = ClientBuilder.newClient()
                .target("http://localhost:8282/refreshJwtToken")
                .path(existingJwtStr)
                .request(MediaType.APPLICATION_JSON)
                .get(RefreshTokenResponse.class);

            final String updatedAuthHeader = refreshTokenResponse.getAuthHeader();
            final String updatedSignedAuthHeader = refreshTokenResponse.getSignedAuthHeader();
            LOGGER.info(">>> updatedAuthHeader {}", updatedAuthHeader);
            LOGGER.info(">>> updatedSignedAuthHeader {}", updatedSignedAuthHeader);

            JwtHelper.writeCookie(httpServletResponse, JwtHelper.AUTH_HEADER_KEY, updatedAuthHeader);
            JwtHelper.writeCookie(httpServletResponse, JwtHelper.SIGNED_AUTH_HEADER_KEY,
                    updatedSignedAuthHeader);
            LOGGER.debug("Updated cookie values written to the HttpServletResponse");

            LOGGER.info("Refreshed session timeout, refreshTokenResponse {}", refreshTokenResponse);
        } catch (final Exception e) {
            LOGGER.warn("Unable to refresh session timeout", e);
        }
    }

    /**
     * Broadcast that this session has been signed out.
     */
    private static void signOut(final Session session) {
        EventQueues.lookup("signOutQueue", EventQueues.APPLICATION, true)
                .publish(new Event("onSignout", null, session));

        // executions.sendRedirect("/login.zul?error=2");
    }
}
