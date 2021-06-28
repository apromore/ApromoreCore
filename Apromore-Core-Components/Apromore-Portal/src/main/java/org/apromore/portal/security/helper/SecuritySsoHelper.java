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
package org.apromore.portal.security.helper;

import static org.apromore.portal.common.UserSessionManager.initializeUser;
import static org.apromore.portal.util.SecurityUtils.symmetricDecrypt;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.manager.client.ManagerService;
import org.apromore.mapper.UserMapper;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.PortalSession;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.UserType;
import org.apromore.portal.util.ApromoreEnvUtils;
import org.apromore.service.SecurityService;
import org.apromore.service.UserService;
import org.slf4j.Logger;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;

import java.util.StringTokenizer;

public class SecuritySsoHelper {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(SecuritySsoHelper.class);

    private static final String SYMMETRIC_KEY_SECRET_ENV_KEY = "ENV_KEY";

    public static String getEnvEncKey() {
        String encPassphStr = ApromoreEnvUtils.getEnvPropValue(
                SYMMETRIC_KEY_SECRET_ENV_KEY, "encryption passphrase could *not* be attained from environment");

        LOGGER.info("\n\nObtained encKey from environment\n");

        return encPassphStr;
    }

    public static String getAppAuthHeader() {
        final SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper =
                SecuritySsoHelper.getSecurityContextHolderAwareRequestWrapper();

        final String appAuthHeader =
                securityContextHolderAwareRequestWrapper.getHeader("App_Auth");
        LOGGER.info("\n### appAuthHeader {}", appAuthHeader);

        return appAuthHeader;
    }

    public static String getSignedAppAuthHeader() {
        final SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper =
                SecuritySsoHelper.getSecurityContextHolderAwareRequestWrapper();

        final String signedAppAuthHeader =
                securityContextHolderAwareRequestWrapper.getHeader("Signed_App_Auth");
        LOGGER.info("\n### signedAppAuthHeader {}", signedAppAuthHeader);

        return signedAppAuthHeader;
    }

    public static String getSsoUsername(final String urlDecoded, String encKey) throws Exception {
        final String decryptedUrlParam = symmetricDecrypt(urlDecoded, encKey);
        LOGGER.info("\n\n>>>>> >>>>> >>>>> decryptedUrlParam: {}", decryptedUrlParam);

        final StringTokenizer stringTokenizer = new StringTokenizer(decryptedUrlParam, ";");

        final String usernameKeyValuePair = stringTokenizer.nextToken();
        final String usernameParsed = usernameKeyValuePair.substring(usernameKeyValuePair.indexOf("=") + 1);
        LOGGER.info("\n\n>>>>> >>>>> >>>>> usernameParsed: {}", usernameParsed);

        return usernameParsed;
    }

    public static PortalSessionQePair initialiseKeycloakUser(
            final String usernameParsed,
            final UserService userService,
            final SecurityService securityService,
            final ManagerService managerService,
            final ConfigBean config,
            final MainController mainController) {
        PortalSessionQePair portalSessionQePair = null;

        try {
            Sessions.getCurrent().setMaxInactiveInterval(-10);
            LOGGER.info("\n\n>>> Set max inactive interval to negative number <<<");

            final User samlSsoUser = userService.findUserByLogin(usernameParsed);
            final UserType userType = UserMapper.convertUserTypes(samlSsoUser, securityService);

            portalSessionQePair =
                    new PortalSessionQePair(
                            new PortalSession(mainController),
                            EventQueues.lookup(
                                    Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, false)
                    );

            initializeUser(managerService, config, userType, samlSsoUser);
        } catch (UserNotFoundException e) {
            LOGGER.error("\n\nUserNotFoundException: {} - stackTrace {}", e.getMessage(),
                    ExceptionUtils.getStackTrace(e));
        }

        return portalSessionQePair;
    }

    public static SecurityContextHolderAwareRequestWrapper getSecurityContextHolderAwareRequestWrapper() {
        final Object nativeRequest = Executions.getCurrent().getNativeRequest();
        LOGGER.info("\n### nativeRequest {}", nativeRequest);
        final SecurityContextHolderAwareRequestWrapper servReqWrapper =
                (SecurityContextHolderAwareRequestWrapper) nativeRequest;

        return servReqWrapper;
    }

    public static boolean logoutCurrentUser(final UserType currentUser, final Event event, final ConfigBean config,
            final ManagerService managerService) {
        LOGGER.debug("\n\nLOGGING OUT currentUser username [{}]", currentUser.getUsername());

        boolean logoutSuccess = false;

        Session session = null;
        try {
            boolean kcLogoutSuccess;

            session = Sessions.getCurrent();

            if ((config.isUseKeycloakSso()) && (event.getData().equals(session))) {
                kcLogoutSuccess =
                        managerService.logoutUserAllSessions(
                                currentUser.getUsername(),
                                config.getSecurityMsHttpLogoutUrl(),
                                config.getSecurityMsHttpsLogoutUrl());
                LOGGER.info("\nkcLogoutSuccess: {}", kcLogoutSuccess);
            }
        } catch (final Exception e) {
            LOGGER.error("\n\nException in logging out user from Keycloak [{}]",
                    currentUser.getUsername(), e.getMessage());
        } finally {
            // Regardless of whether the server is configured to use Keycloak or not, or whether for some exceptional
            // case Keycloak server session logout fails - the user should be logged out of ZK/servlet/Spring security.
            // Even, in the exceptional case (i.e. the Keycloak logout fails), the Jwt is timed to maximum period -
            // usually 30 minutes, therefore this is not a super huge problem.
            try {
                if (session == null || event.getData().equals(session)) {
                    LOGGER.info("User {} logout", currentUser.getUsername());
                    Clients.evalJavaScript("window.close()");
                    Executions.sendRedirect("/j_spring_security_logout");

                    logoutSuccess = true;
                }
            } catch (final Exception e) {
                LOGGER.error("\n\nException in logging out user via spring security [{}]",
                        currentUser.getUsername(), e.getMessage());
            }

            return logoutSuccess;

        }
    }
}
