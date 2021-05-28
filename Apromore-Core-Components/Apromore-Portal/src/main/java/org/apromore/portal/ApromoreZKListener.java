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
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.nimbusds.jwt.JWTParser;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.security.helper.JwtHelper;
import org.apromore.portal.util.SecurityUtils;
import org.slf4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.ExecutionInit;

import java.security.PublicKey;
import java.util.Base64;

/**
 * Callbacks during the lifecycle of ZK scopes.
 */
public class ApromoreZKListener implements ExecutionInit {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreZKListener.class);
    private static final String JWT_COOKIE_NAME = "App_Auth";
    private static final String KEY_ALIAS = "apseckey";

    /**
     * {@inheritDoc}
     *
     * This implementation logs the user out if the security token has expired, or refreshes it
     * if it's still valid but close to expiry.
     */
    @Override
    public void init(final Execution exec, final Execution parent) {
        LOGGER.trace("Initialize execution {} with parent {}", exec, parent);

        // If there is a parent execution, it will have already performed the required work
        if (parent != null) {
            return;
        }

        // If we are not using Keycloak, we don't have to manage JWTs
        final ConfigBean config = (ConfigBean) SpringUtil.getBean("portalConfig");
        final boolean usingKeycloak = config.isUseKeycloakSso();
        if (!usingKeycloak) {
            LOGGER.debug("Skipping JWT check because not using Keycloak");

            return;
        }

        // At this point we've committed to requiring a valid security token
        // If anything goes wrong in this try-catch block, we err on the side of caution and terminate the session
        try {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) exec.getNativeRequest();
            final HttpServletResponse httpServletResponse = (HttpServletResponse) exec.getNativeResponse();
            final String appAuthHeader = JwtHelper.readCookieValue(httpServletRequest, JWT_COOKIE_NAME);
            LOGGER.trace("Read App_Auth cookie: {}", appAuthHeader);
            final String signedAuthHeader = JwtHelper.readCookieValue(httpServletRequest, JwtHelper.SIGNED_AUTH_HEADER_KEY);
            final JWTClaimsSet jwtClaimsSet = JwtHelper.getClaimsSetFromJWT(appAuthHeader);

            if (!isJwtDigitalSignatureVerified(appAuthHeader, signedAuthHeader)) {
                throw new Exception("JWT was not correctly signed: appAuthHeader " + appAuthHeader +
                    " signedAuthHeader " + signedAuthHeader);

            } else if (JwtHelper.isJwtExpired(jwtClaimsSet)) {
                LOGGER.info("JWT is expired, signing this session out");
                signOut(exec.getSession());

            } else if (JwtHelper.doesJwtExpiryWithinNMinutes(jwtClaimsSet, config.getMinutesUntilExpiryBeforeSessionRefresh())) {
                // If the token is close to expiry, refresh it
                LOGGER.info("JWT is close to expiry, refreshing it");
                refreshSessionTimeout(jwtClaimsSet, exec, appAuthHeader, httpServletResponse);

            } else {
                LOGGER.debug("JWT is fresh");
            }
        } catch (final Exception e) {
            LOGGER.error("JWT security token check failed; terminating session", e);
            signOut(exec.getSession());
        }
    }

    /**
     * @param jwtStr  a plaintext JSON web token
     * @param base64EncodedAndSignedJwtStr  an encoded and signed JSON web token
     * @return whether  <var>base64EncodedAndSignedJwtStr</var> has content matching
     *     <var>jwtStr</var> and is correctly encoded and signed by the key pair
     *     identified by {@link KEY_ALIAS}
     */
    private boolean isJwtDigitalSignatureVerified(final String jwtStr, final String base64EncodedAndSignedJwtStr) {
        try {
            byte[] signedJwtStr = Base64.getDecoder().decode(base64EncodedAndSignedJwtStr);

            final boolean verifiedSignature = JwtHelper.isSignedStrVerifiable(jwtStr, signedJwtStr);
            LOGGER.debug(">>>>> >>> > verifiedSignature {}", verifiedSignature);

            return verifiedSignature;

        } catch (final Exception e) {
            LOGGER.error("Exception when verifying digital signature of JWT", e);

            return false;
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
            LOGGER.debug("Using securityMsHost {} and securityMsPort {}", securityMsHost, securityMsPort);
            final String refreshTokenUrl = "http://" + securityMsHost + ":" + securityMsPort + "/refreshJwtToken";
            LOGGER.debug("Using refreshTokenUrl {}", refreshTokenUrl);

            final RefreshTokenResponse refreshTokenResponse = ClientBuilder.newClient()
                .target(refreshTokenUrl)
                .path(existingJwtStr)
                .request(MediaType.APPLICATION_JSON)
                .get(RefreshTokenResponse.class);

            LOGGER.debug("Refreshed session timeout, refreshTokenResponse {}", refreshTokenResponse);

            final String updatedAuthHeader = refreshTokenResponse.getAuthHeader();
            final String updatedSignedAuthHeader = refreshTokenResponse.getSignedAuthHeader();
            LOGGER.info(">>> updatedAuthHeader {}", updatedAuthHeader);

            final JWT refreshedJwt = JWTParser.parse(updatedAuthHeader);
            final JWTClaimsSet refreshedJwtClaimsSet = refreshedJwt.getJWTClaimsSet();
            LOGGER.debug("refreshedJwtClaimsSet {}", refreshedJwtClaimsSet);
            LOGGER.debug("refreshedJwtClaimsSet.getExpirationTime() {}", refreshedJwtClaimsSet.getExpirationTime());

            boolean expiresSoon = JwtHelper.doesJwtExpiryWithinNMinutes(
                    preExistingJwtClaimsSet, 5);
            LOGGER.debug(">>> expiresSoon {}", expiresSoon);

            SessionCookieConfig cookieConfig = exec.getSession().getWebApp().getServletContext().getSessionCookieConfig();
            JwtHelper.writeCookie(httpServletResponse, JwtHelper.AUTH_HEADER_KEY, updatedAuthHeader, cookieConfig);
            JwtHelper.writeCookie(httpServletResponse, JwtHelper.SIGNED_AUTH_HEADER_KEY,
                    updatedSignedAuthHeader, cookieConfig);
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
