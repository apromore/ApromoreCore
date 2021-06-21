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
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.nimbusds.jwt.JWTParser;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.security.helper.JwtHelper;
import org.apromore.portal.util.SecurityUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.util.WebAppInit;

import java.security.PublicKey;
import java.util.Base64;

import static org.apromore.portal.dialogController.TokenHandoffController.JWTS_MAP_BY_USER_KEY;

/**
 * Callbacks during the lifecycle of ZK scopes.
 */
public class ApromoreZKListener implements ExecutionInit, WebAppInit {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreZKListener.class);
    private static final String JWT_COOKIE_NAME = "App_Auth";
    private static final String KEY_ALIAS = "apseckey";

    /**
     * If this property key appears in <code>site.cfg</code>, it will be parsed as a boolean and
     * determines whether session cookie "HttpOnly" flags will be set.
     */
    public static final String COOKIE_HTTP_ONLY = "site.cookie.httpOnly";

    /**
     * If this property key appears in <code>site.cfg</code>, it will be parsed as a boolean and
     * determines whether session cookie "Secure" flags will be set.
     */
    public static final String COOKIE_SECURE = "site.cookie.secure";

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

            Long validIssueTimeForUser = null;
            Map<String, Long> usersToValidIssuedAtAtJwtMap = new HashMap<>();
            final Object validJwtsObj =
                    httpServletRequest.getServletContext().getAttribute(JWTS_MAP_BY_USER_KEY);
            if (validJwtsObj != null) {
                usersToValidIssuedAtAtJwtMap = (Map<String, Long>)validJwtsObj;
                validIssueTimeForUser =
                        usersToValidIssuedAtAtJwtMap.get(jwtClaimsSet.getSubject());
            }

            if (!isJwtDigitalSignatureVerified(appAuthHeader, signedAuthHeader, exec.getSession())) {
                throw new Exception("JWT was not correctly signed: appAuthHeader " + appAuthHeader +" signedAuthHeader " + signedAuthHeader);
            } else if (jwtClaimsSet.getIssueTime().getTime() != validIssueTimeForUser) {
                LOGGER.warn("JWT has been invalidated, signing out");
                signOut(exec.getSession());
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
     * {@inheritDoc}
     *
     * This implementation allows the cookie configuration to be set from the central application
     * configuration file <code>site.cfg</code>, overriding the Portal's <code>web.xml</code>.
     */
    @Override
    public void init(final WebApp webApp) {
        LOGGER.trace("Initialize web app {}", webApp);
        try {
            Map<String, Object> siteConfiguration = OSGi.getConfiguration("site", webApp.getServletContext());
            LOGGER.trace("Site configuration {}", siteConfiguration);

            // Allow override of the "HttpOnly" cookie flag
            if (siteConfiguration.containsKey(COOKIE_HTTP_ONLY)) {
                boolean isHttpOnly = Boolean.parseBoolean((String) siteConfiguration.get(COOKIE_HTTP_ONLY));
                LOGGER.info("Overriding session cookie configuration: HttpOnly is now {}", isHttpOnly);
                webApp.getServletContext().getSessionCookieConfig().setHttpOnly(isHttpOnly);
            }

            // Allow override of the "Secure" cookie flag
            if (siteConfiguration.containsKey(COOKIE_SECURE)) {
                boolean isSecure = Boolean.parseBoolean((String) siteConfiguration.get(COOKIE_SECURE));
                LOGGER.info("Overriding session cookie configuration: Secure is now {}", isSecure);
                webApp.getServletContext().getSessionCookieConfig().setSecure(isSecure);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to initialize web app", e);
        }
    }

    /**
     * @param jwtStr  a plaintext JSON web token
     * @param base64EncodedAndSignedJwtStr  an encoded and signed JSON web token
     * @return whether  <var>base64EncodedAndSignedJwtStr</var> has content matching
     *     <var>jwtStr</var> and is correctly encoded and signed by the key pair
     *     identified by {@link KEY_ALIAS}
     */
    private boolean isJwtDigitalSignatureVerified(final String jwtStr,
                                                  final String base64EncodedAndSignedJwtStr,
                                                  final Session session) {

        Object previousBase64EncodedAndSignedJwtStr = session.getAttribute(JwtHelper.SIGNED_AUTH_HEADER_KEY);
        if (previousBase64EncodedAndSignedJwtStr != null &&
            previousBase64EncodedAndSignedJwtStr.equals(base64EncodedAndSignedJwtStr)) {

            return true;  // this is the same JWT we previously confirmed to be correctly signed
        }

        try {
            byte[] signedJwtStr = Base64.getDecoder().decode(base64EncodedAndSignedJwtStr);

            final boolean verifiedSignature = JwtHelper.isSignedStrVerifiable(jwtStr, signedJwtStr);
            LOGGER.debug(">>>>> >>> > verifiedSignature {}", verifiedSignature);
            session.setAttribute(JwtHelper.SIGNED_AUTH_HEADER_KEY, base64EncodedAndSignedJwtStr);

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
        session.removeAttribute(JwtHelper.SIGNED_AUTH_HEADER_KEY);
        EventQueues.lookup("signOutQueue", EventQueues.APPLICATION, true)
                .publish(new Event("onSignout", null, session));
    }
}
