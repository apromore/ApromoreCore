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

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.nimbusds.jwt.JWTParser;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.security.helper.JwtHelper;
import org.slf4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.ExecutionInit;

import java.util.Date;

/**
 * Callbacks during the lifecycle of ZK scopes.
 */
public class ApromoreZKListener implements ExecutionInit {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreZKListener.class);

    private static final String JWT_COOKIE_NAME = "App_Auth";

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

        final String appAuthHeader = JwtHelper.readCookieValue(httpServletRequest, JWT_COOKIE_NAME);
        LOGGER.trace("Read App_Auth cookie: {}", appAuthHeader);

        try {
            final JWTClaimsSet jwtClaimsSet = JwtHelper.getClaimsSetFromJWT(appAuthHeader);
            // final jwtVerified = @todo: JwtHelper.isSignedStrVerifiable();

            final boolean jwtVerified = true;

            if (jwtVerified) {
                final String issuedAtStr = jwtClaimsSet.getStringClaim(JwtHelper.STR_JWT_KEY_ISSUED_AT);
                LOGGER.debug("issuedAtStr {}", issuedAtStr);
                final Date issuedAtDate = jwtClaimsSet.getIssueTime();
                LOGGER.trace("issuedAtDate {}", issuedAtDate);
                final Date expiryAtDate = jwtClaimsSet.getExpirationTime();
                LOGGER.info("expiryAtDate {}", expiryAtDate);

                // If the token is expired, sign the session out
                if (JwtHelper.isJwtExpired(jwtClaimsSet, issuedAtDate, expiryAtDate)) {
                    LOGGER.info("JWT is expired");
                    signOut(exec.getSession());

                    // ----------------------------------------------------------
                    // [N.B. Mic] Ensure being signed out keycloak ( should be in
                    // eventQueue listener 'signOutQueue' event ).
                    // ----------------------------------------------------------
                    // managerService.logout();
                    return;
                }
                LOGGER.info("JWT is not expired");

                // If the token is close to expiry, refresh it
                if (JwtHelper.doesJwtExpiryWithinNMinutes(jwtClaimsSet, 1)) {
                    LOGGER.info("JWT needs refresh");
                    refreshSessionTimeout(jwtClaimsSet, exec, appAuthHeader, httpServletResponse);
                } else {
                    LOGGER.info("JWT is fresh");
                }
            } else {
                LOGGER.warn("JWT signature verification failed");
                return;
            }
        } catch (final Exception e) {
            LOGGER.error("JWT expiration/refresh check failed; terminating session", e);
            signOut(exec.getSession());
            return;
        }
    }

    /**
     * Issue a new JWT with an extended expiry.
     */
    private static void refreshSessionTimeout(
            final JWTClaimsSet preExistingJwtClaimsSet,
            final Execution exec,
            final String existingJwtStr,
            final HttpServletResponse httpServletResponse) {
        LOGGER.debug(">>>>> Refreshing session timeout");
        try {
            final ConfigBean config = (ConfigBean) SpringUtil.getBean("portalConfig");
            final String securityMsHost = config.getSecurityMsHost();
            final String securityMsPort = config.getSecurityMsPort();
            LOGGER.info("Using securityMsHost {} and securityMsPort {}", securityMsHost, securityMsPort);
            final String refreshTokenUrl = "http://" + securityMsHost + ":" + securityMsPort + "/refreshJwtToken";
            LOGGER.info("Using refreshTokenUrl {}", refreshTokenUrl);

            final RefreshTokenResponse refreshTokenResponse = ClientBuilder.newClient()
                .target(refreshTokenUrl)
                .path(existingJwtStr)
                .request(MediaType.APPLICATION_JSON)
                .get(RefreshTokenResponse.class);

            LOGGER.info("Refreshed session timeout, refreshTokenResponse {}", refreshTokenResponse);

            final String updatedAuthHeader = refreshTokenResponse.getAuthHeader();
            final String updatedSignedAuthHeader = refreshTokenResponse.getSignedAuthHeader();
            LOGGER.info(">>> updatedAuthHeader {}", updatedAuthHeader);

            final JWT refreshedJwt = JWTParser.parse(updatedAuthHeader);
            final JWTClaimsSet refreshedJwtClaimsSet = refreshedJwt.getJWTClaimsSet();
            LOGGER.info("refreshedJwtClaimsSet {}", refreshedJwtClaimsSet);
            LOGGER.info("refreshedJwtClaimsSet.getExpirationTime() {}", refreshedJwtClaimsSet.getExpirationTime());

            boolean expiresSoon = JwtHelper.doesJwtExpiryWithinNMinutes(
                    preExistingJwtClaimsSet, 5);
            LOGGER.info(">>> expiresSoon {}", expiresSoon);

            JwtHelper.writeCookie(httpServletResponse, JwtHelper.AUTH_HEADER_KEY, updatedAuthHeader);
            JwtHelper.writeCookie(httpServletResponse, JwtHelper.SIGNED_AUTH_HEADER_KEY,
                    updatedSignedAuthHeader);
            LOGGER.debug("Updated cookie values written to the HttpServletResponse");
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
    }
}
