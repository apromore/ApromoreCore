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
package org.apromore.portal.dialogController;

import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apromore.manager.client.ManagerService;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.UserType;
import org.apromore.portal.security.helper.SecuritySsoHelper;
import org.apromore.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

import java.text.ParseException;

public class TokenHandoffController extends SelectorComposer<Window> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenHandoffController.class);

    private ManagerService managerService;

    @Override
    public void doAfterCompose(final Window window) {
        LOGGER.info("In TokenHandoffController.doAfterCompose(..)");
        LOGGER.info(">>>>> JWT token handoff <<<<<");

        managerService = (ManagerService) SpringUtil.getBean(Constants.MANAGER_SERVICE);
        LOGGER.info("Manager service: " + managerService);

        try {
            final UserType userType = userFromJwt();

            if (userType != null) {
                UserSessionManager.setCurrentUser(userType);
                LOGGER.info("Current user: " + UserSessionManager.getCurrentUser());

                Executions.sendRedirect("/index.zul");
            } else {
                Executions.sendRedirect("/login.zul?error=2");
            }
        } catch (final Exception e) {
            LOGGER.error("Exception in processing JWT/associating user {} - stackTrace {}", e.getMessage(),
                    ExceptionUtils.getStackTrace(e));

            Executions.sendRedirect("/login.zul?error=2");
        }
    }

    /**
     * Return authenticated user if JWT token is not expired, otherwise <code>null</code>.
     *
     * @return
     */
    private UserType userFromJwt() {
        final String appAuthHeader = SecuritySsoHelper.getAppAuthHeader();
        final String signedAppAuthHeader = SecuritySsoHelper.getSignedAppAuthHeader();

        try {
            final JWTClaimsSet jwtClaimsSet = JwtUtils.getClaimsSetFromJWT(appAuthHeader);
                    // JWTClaimsSet.parse(appAuthHeader);

            final Object issuedAtObj = jwtClaimsSet.getClaim(JwtUtils.JWT_KEY_ISSUED_AT);
            LOGGER.info("issuedAtObj {}", issuedAtObj);
            final Object expiryAtObj = jwtClaimsSet.getClaim(JwtUtils.JWT_EXPIRY_TIME);
            LOGGER.info("expiryAtObj {}", expiryAtObj);

            final boolean jwtExpired = JwtUtils.isJwtExpired(jwtClaimsSet);

            if (!jwtExpired) {
                final UserType authUserType = buildAuthenticatedUserFromJwt(jwtClaimsSet);

                return authUserType;
            } else {
                return null;
            }
        } catch (final Exception pe) {
            LOGGER.error("Error reading JWT: {} - stackTrace", pe.getMessage(), ExceptionUtils.getMessage(pe));

            return null;
        }
    }

    private UserType buildAuthenticatedUserFromJwt(final JWTClaimsSet jwtClaimsSet) {
        final String username = (String)jwtClaimsSet.getClaim(JwtUtils.JWT_KEY_SUBJECT_USERNAME);

        final UserType userType = managerService.readUserByUsername(username);

        return userType;
    }
}
