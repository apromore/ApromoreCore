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

import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.manager.client.ManagerService;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.UserType;
import org.apromore.portal.security.helper.JwtHelper;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;

public class TokenHandoffController extends SelectorComposer<Window> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenHandoffController.class);

    private ManagerService managerService;
    private SecurityService securityService;

    private static final String JWT_KEY_SUBJECT_USERNAME = "sub";
    private static final String JWT_KEY_ISSUED_AT = "iat";
    private static final String JWT_EXPIRY_TIME = "exp";
    private static final String STR_JWT_KEY_ISSUED_AT = "striat";
    private static final String STR_JWT_EXPIRY_TIME = "strexp";
    private static final String JWT_KC_USER_ID = "kcuserid";

    private static final String JWT_KEY_SUBJECT_EMAIL = "email";
    private static final String JWT_KEY_GIVEN_NAME = "givenName";
    private static final String JWT_KEY_FAMILY_NAME = "familyName";

    @Override
    public void doAfterCompose(final Window window) {
        LOGGER.debug("In TokenHandoffController.doAfterCompose(..)");

        managerService = (ManagerService) SpringUtil.getBean(Constants.MANAGER_SERVICE);
        securityService = (SecurityService) SpringUtil.getBean(Constants.SECURITY_SERVICE);
        LOGGER.debug("Manager service: " + managerService);
        LOGGER.debug("Security service: " + securityService);

        try {
            final HttpServletRequest httpServletRequest =
                    (HttpServletRequest) Executions.getCurrent().getNativeRequest();
            LOGGER.debug("httpServletRequest: {}", httpServletRequest);

            final String appAuthCookieStr = JwtHelper.readCookie(httpServletRequest, "App_Auth");
            final byte[] base64DecodedSignedAppAuth = Base64.getDecoder().decode(
                            JwtHelper.readCookie(httpServletRequest, "Signed_App_Auth"));
            LOGGER.debug(">>> appAuthCookieStr: {}", appAuthCookieStr);
            LOGGER.debug(">>> base64DecodedSignedAppAuth: {}", base64DecodedSignedAppAuth);

            final boolean jwtSignatureVerified =
                    JwtHelper.isSignedStrVerifiable(appAuthCookieStr, base64DecodedSignedAppAuth);
            LOGGER.info(">>> jwtSignatureVerified {}", jwtSignatureVerified);

            final UserType userType = JwtHelper.userFromJwt(managerService, appAuthCookieStr);

            if ((jwtSignatureVerified) && (userType != null)) {
                UserSessionManager.setCurrentUser(userType);

                final UserType currentUserType = UserSessionManager.getCurrentUser();
                LOGGER.debug("Current session logged-in currentUserType: " + currentUserType);
                LOGGER.debug("Current session logged-in username: " + currentUserType.getUsername());

                final SecurityContext springSecurityContext = SecurityContextHolder.getContext();
                LOGGER.debug("springSecurityContext {}", springSecurityContext);

                if (springSecurityContext != null) {
                    final Authentication springAuthObj = springSecurityContext.getAuthentication();
                    LOGGER.debug("springAuthObj {}", springAuthObj);

                    final User user =
                            JwtHelper.convertFromUserType(currentUserType, securityService);
                    LOGGER.debug("user {}", user);

                    final List<GrantedAuthority> authorities = getAuthorities(user.getRoles());

                    final PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken =
                            new PreAuthenticatedAuthenticationToken(user.getUsername(),
                                    "", authorities);
                    LOGGER.debug("preAuthenticatedAuthenticationToken {}", preAuthenticatedAuthenticationToken);

                    springSecurityContext.setAuthentication(preAuthenticatedAuthenticationToken);
                } else {
                    LOGGER.debug("Spring SecurityContext is null!!");
                }

                LOGGER.debug("Before redirect");
                Executions.getCurrent().sendRedirect("/index.zul");
                LOGGER.debug("After redirect");
            } else {
                Executions.getCurrent().sendRedirect("/login.zul?error=2");
            }
        } catch (final Exception e) {
            LOGGER.error("Unable to process JWT/associate user", e);

            Executions.getCurrent().sendRedirect("/login.zul?error=2");
        }
    }

    public List<GrantedAuthority> getAuthorities(final Set<Role> access) {
        final List<GrantedAuthority> authList = new ArrayList<>();

        for (Role role : access) {
            authList.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authList;
    }

}
