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

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.manager.client.ManagerService;
import org.apromore.mapper.SearchHistoryMapper;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.RoleType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.security.helper.SecuritySsoHelper;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class TokenHandoffController extends SelectorComposer<Window> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenHandoffController.class);

    private static final Duration WEBAPP_SSO_SESSION_TIMEOUT = Duration.ofMinutes(30); // Duration.ofHours(2L);

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
        LOGGER.info("In TokenHandoffController.doAfterCompose(..)");
        LOGGER.info(">>>>> JWT token handoff <<<<<");

        managerService = (ManagerService) SpringUtil.getBean(Constants.MANAGER_SERVICE);
        securityService = (SecurityService) SpringUtil.getBean(Constants.SECURITY_SERVICE);
        LOGGER.info("Manager service: " + managerService);
        LOGGER.info("Security service: " + securityService);

        try {
            final UserType userType = userFromJwt();

            if (userType != null) {
                UserSessionManager.setCurrentUser(userType);

                final UserType currentUserType = UserSessionManager.getCurrentUser();
                LOGGER.info("Current session logged-in currentUserType: " + currentUserType);
                LOGGER.info("Current session logged-in username: " + currentUserType.getUsername());

                // UserSessionManager.initializeUser(managerService, configBean, userType, user);

                final SecurityContext springSecurityContext = SecurityContextHolder.getContext();
                LOGGER.info("springSecurityContext {}", springSecurityContext);

                if (springSecurityContext != null) {
                    final Authentication springAuthObj = springSecurityContext.getAuthentication();
                    LOGGER.info("springAuthObj {}", springAuthObj);

                    final User user = convertFromUserType(currentUserType, securityService);
                    LOGGER.info("user {}", user);

                    final List<GrantedAuthority> authorities = getAuthorities(user.getRoles());

                    final PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken =
                            new PreAuthenticatedAuthenticationToken(user.getUsername(),
                                    "", authorities);
                    LOGGER.info("preAuthenticatedAuthenticationToken {}", preAuthenticatedAuthenticationToken);

                    springSecurityContext.setAuthentication(preAuthenticatedAuthenticationToken);
                } else {
                    LOGGER.info("Spring SecurityContext is null!!");
                }


                Executions.sendRedirect("http://localhost:8181/index.zul");
            } else {
                Executions.sendRedirect("/login.zul?error=2");
            }
        } catch (final Exception e) {
            LOGGER.error("Exception in processing JWT/associating user {} - stackTrace {}", e.getMessage(),
                    ExceptionUtils.getStackTrace(e));

            Executions.sendRedirect("/login.zul?error=2");
        }
    }

    public List<GrantedAuthority> getAuthorities(Set<Role> access) {
        final List<GrantedAuthority> authList = new ArrayList<>();

        for (Role role : access) {
            authList.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authList;
    }

    /**
     * Return authenticated user if JWT token is not expired, otherwise <code>null</code>.
     *
     * @return
     */
    private UserType userFromJwt() {
        final String appAuthHeader = SecuritySsoHelper.getAppAuthHeader();
        final String signedAppAuthHeader = SecuritySsoHelper.getSignedAppAuthHeader();

        final SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper =
                SecuritySsoHelper.getSecurityContextHolderAwareRequestWrapper();
        final Enumeration<String> headerNames =
                securityContextHolderAwareRequestWrapper.getHeaderNames();

        LOGGER.info("\n\nALL HEADERS via: " +
                "SecuritySsoHelper.getSecurityContextHolderAwareRequestWrapper().getHeaderNames(): {}",
            headerNames);
        final String strJwtKeyIssuedAt =
                securityContextHolderAwareRequestWrapper.getHeader(STR_JWT_KEY_ISSUED_AT);
        final String strJwtKeyExpiryAt =
                securityContextHolderAwareRequestWrapper.getHeader(STR_JWT_EXPIRY_TIME);
        LOGGER.info("\nstrJwtKeyIssuedAt {}", strJwtKeyIssuedAt);
        LOGGER.info("\nstrJwtKeyExpiryAt {}", strJwtKeyExpiryAt);

        try {
            final JWTClaimsSet jwtClaimsSet = getClaimsSetFromJWT(appAuthHeader);
            LOGGER.info("jwtClaimsSet {}", jwtClaimsSet);
                    // JWTClaimsSet.parse(appAuthHeader);

            final String issuedAtStr = (String)jwtClaimsSet.getStringClaim(STR_JWT_KEY_ISSUED_AT);
            LOGGER.info("issuedAtStr {}", issuedAtStr);
            final String expiryAtStr = (String)jwtClaimsSet.getStringClaim(STR_JWT_EXPIRY_TIME);
            LOGGER.info("expiryAtStr {}", expiryAtStr);

            final boolean jwtExpired = isJwtExpired(jwtClaimsSet, issuedAtStr, expiryAtStr);

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
        final String username = (String)jwtClaimsSet.getClaim(JWT_KEY_SUBJECT_USERNAME);

        final UserType userType = managerService.readUserByUsername(username);

        return userType;
    }


    private JWTClaimsSet getClaimsSetFromJWT(final String jwtStr) throws Exception {
        final JWT jwtParsed = JWTParser.parse(jwtStr);
        final JWTClaimsSet jwtClaimsSet = jwtParsed.getJWTClaimsSet();

        return jwtClaimsSet;
    }

    private boolean isJwtExpired(final JWTClaimsSet jwtClaimsSet,
                                 final String issuedAtStr,
                                 final String expiredAtStr) {
        long jwtExpiryEpoch;
        if (expiredAtStr != null) {
            jwtExpiryEpoch = Long.valueOf(expiredAtStr.substring(3));
            LOGGER.info("jwtExpiryEpoch {}", jwtExpiryEpoch);
        } else { // Calculate relative
            final Long jwtIssuedAt = Long.valueOf(issuedAtStr.substring(3));

            jwtExpiryEpoch = calculateExpiryTime(jwtIssuedAt, WEBAPP_SSO_SESSION_TIMEOUT);
            LOGGER.info("jwtExpiryEpoch {}", jwtExpiryEpoch);
        }

        final Long nowEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        final boolean jwtExpired = (nowEpoch > jwtExpiryEpoch);
        LOGGER.info(">>>>> >>>>> >>>>> jwtExpired? {}", jwtExpired);

        return jwtExpired;
    }

    private long calculateExpiryTime(final long createdAtEpoch, final Duration sessionDuration) {
        return createdAtEpoch + sessionDuration.toMillis();
    }

    public User convertFromUserType(final UserType userType, final SecurityService securityService) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        if (userType.getLastActivityDate() != null && !userType.getLastActivityDate().equals("")) {
            try {
                date = formatter.parse(userType.getLastActivityDate());
            } catch (ParseException ex) {
                LOGGER.error("Last Activity Date: " + userType.getLastActivityDate() + " could not be parsed.");
            }
        }

        User user = new User();
        user.setLastName(userType.getLastName());
        user.setFirstName(userType.getFirstName());
        user.setUsername(userType.getUsername());
        user.setOrganization(userType.getOrganization());
        user.setRole(userType.getRole());
        user.setCountry(userType.getCountry());
        user.setPhone(userType.getPhone());
        user.setSubscription(userType.getSubscription());
        user.setRowGuid(userType.getId());
        if (date != null){
            user.setLastActivityDate(date);
        }
        if (user.getSearchHistories() != null) {
            user.setSearchHistories(SearchHistoryMapper.convertFromSearchHistoriesType(userType.getSearchHistories()));
        }

        Membership membership = new Membership();
        if (userType.getMembership() != null) {
            membership.setSalt("username");
            membership.setDateCreated(new Date());
            membership.setEmail(userType.getMembership().getEmail());
            if (userType.getMembership().getPassword() != null) {
                membership.setPassword(SecurityUtil.hashPassword(userType.getMembership().getPassword()));
            } else {
                membership.setPassword("");
            }
            membership.setQuestion(userType.getMembership().getPasswordQuestion());
            membership.setAnswer(userType.getMembership().getPasswordAnswer());
            membership.setFailedPasswordAttempts(0);
            membership.setFailedAnswerAttempts(0);
            membership.setUser(user);

            user.setMembership(membership);
        }

        for (RoleType roleType : userType.getRoles()){
            user.getRoles().add(securityService.findRoleByName(roleType.getName()));
        }

        return user;
    }
}
