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

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.User;
import org.apromore.manager.client.ManagerService;
import org.apromore.mapper.SearchHistoryMapper;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.RoleType;
import org.apromore.portal.model.UserType;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtHelper {

    public static final String JWT_KEY_SUBJECT_USERNAME = "sub";
    public static final String STR_JWT_KEY_ISSUED_AT = "striat";
    public static final String STR_JWT_EXPIRY_TIME = "strexp";
    public static final Duration WEBAPP_SSO_SESSION_TIMEOUT = Duration.ofMinutes(30);

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtHelper.class);

    public static String readCookie(final HttpServletRequest httpServletRequest,
                                    final String cookieName) {
        final Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public static boolean isJwtExpired(final JWTClaimsSet jwtClaimsSet,
                                       final String issuedAtStr,
                                       final String expiredAtStr) {
        long jwtExpiryEpoch;
        if (expiredAtStr != null) {
            jwtExpiryEpoch = Long.valueOf(expiredAtStr.substring(3));
        } else { // Calculate relative
            final Long jwtIssuedAt = Long.valueOf(issuedAtStr.substring(3));

            jwtExpiryEpoch = calculateExpiryTime(jwtIssuedAt, WEBAPP_SSO_SESSION_TIMEOUT);
        }

        final Long nowEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        final boolean jwtExpired = (nowEpoch > jwtExpiryEpoch);

        return jwtExpired;
    }

    public static JWTClaimsSet getClaimsSetFromJWT(final String jwtStr) throws Exception {
        final JWT jwtParsed = JWTParser.parse(jwtStr);
        final JWTClaimsSet jwtClaimsSet = jwtParsed.getJWTClaimsSet();

        return jwtClaimsSet;
    }

    /**
     * Return authenticated user if JWT token is not expired, otherwise <code>null</code>.
     *
     * @return <code>null</code> if the JWT has expired, otherwise the associated user.
     */
    public static UserType userFromJwt(final ManagerService managerService, final String appAuthHeader) {
        try {
            final JWTClaimsSet jwtClaimsSet = JwtHelper.getClaimsSetFromJWT(appAuthHeader);

            final String issuedAtStr = jwtClaimsSet.getStringClaim(JwtHelper.STR_JWT_KEY_ISSUED_AT);
            final String expiryAtStr = jwtClaimsSet.getStringClaim(JwtHelper.STR_JWT_EXPIRY_TIME);

            final boolean jwtExpired = JwtHelper.isJwtExpired(jwtClaimsSet, issuedAtStr, expiryAtStr);

            if (!jwtExpired) {
                final UserType authUserType =
                        buildAuthenticatedUserFromJwt(jwtClaimsSet, managerService);

                return authUserType;
            } else {
                LOGGER.debug("JWT has expired, returning null per method contract");
                return null;
            }
        } catch (final Exception pe) {
            LOGGER.debug("Exception in processing JWT (returning null)", pe);

            return null;
        }
    }

    public static UserType buildAuthenticatedUserFromJwt(final JWTClaimsSet jwtClaimsSet,
                                                         final ManagerService managerService) {
        final String username = (String)jwtClaimsSet.getClaim(JwtHelper.JWT_KEY_SUBJECT_USERNAME);

        final UserType userType = managerService.readUserByUsername(username);

        return userType;
    }

    public static User convertFromUserType(final UserType userType, final SecurityService securityService) {
        final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        if (userType.getLastActivityDate() != null && !userType.getLastActivityDate().equals("")) {
            try {
                date = formatter.parse(userType.getLastActivityDate());
            } catch (final ParseException ex) {
                LOGGER.warn("Parse exception in attaining the pertinent associated user (this should not happen)", ex);

                return null;
            }
        }

        final User user = new User();
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
            user.setSearchHistories(
                    SearchHistoryMapper.convertFromSearchHistoriesType(userType.getSearchHistories()));
        }

        final Membership membership = new Membership();
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

        for (final RoleType roleType : userType.getRoles()){
            user.getRoles().add(securityService.findRoleByName(roleType.getName()));
        }

        return user;
    }

    private static long calculateExpiryTime(final long createdAtEpoch, final Duration sessionDuration) {
        return createdAtEpoch + sessionDuration.toMillis();
    }
}
