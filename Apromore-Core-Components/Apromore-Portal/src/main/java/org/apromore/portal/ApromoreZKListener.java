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

    /**
     * {@inheritDoc}
     *
     * This implementation logs the user out if the security token has expired, or refreshes it
     * if it's still valid but close to expiry.
     */
    @Override
    public void init(Execution exec, Execution parent) {
        LOGGER.debug("Initialize execution {} with parent {}", exec, parent);

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

        final HttpServletRequest httpServletRequest = (HttpServletRequest) exec.getNativeRequest();
        final String appAuthHeader = JwtHelper.readCookie(httpServletRequest, "App_Auth");
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
            }
            LOGGER.debug("JWT is not expired");

            // If the token is close to expiry, refresh it (currently, we do this whether close to expiry or not)
            if (true) {
                final JWTClaimSet newJWTClaimsSet = JwtHelper.refreshJwt(jwtClaimsSet);
                LOGGER.debug("JWT expiry refreshed from {} to {}", expiryAtStr,
                    newJWTClaimsSet.getStringClaim(JwtHelper.STR_JWT_EXPIRY_TIME));
                // TODO: update the JWT on the servlet response
            }
        } catch (Exception e) {
            LOGGER.error("JWT expiration/refresh check failed; terminating session", e);
            signOut(exec.getSession());
        }
    }

    /**
     * Issue a new JWT with an extended expiry.
     */
    private static void refreshSessionTimeout(final Execution exec) {
        // exec.addResponseHeader(...);  or maybe  ((HttpServletResponse) exec.getNativeResponse())...
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
