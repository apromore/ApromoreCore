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
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.model.RoleType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.util.SecurityUtils;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;

import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import static org.apromore.portal.util.AssertUtils.notNullAssert;

public class JwtHelper {

    public static final String JWT_KEY_SUBJECT_USERNAME = "sub";
    public static final String JWT_KEY_ISSUED_AT = "iat";
    public static final String JWT_EXPIRY_TIME = "exp";
    public static final String JWT_KC_USER_ID = "kcuserid";

    public static final String JWT_KEY_SUBJECT_EMAIL = "email";
    public static final String JWT_KEY_GIVEN_NAME = "givenName";
    public static final String JWT_KEY_FAMILY_NAME = "familyName";

    public static final String STR_JWT_KEY_ISSUED_AT = "striat";
    public static final String STR_JWT_EXPIRY_TIME = "strexp";
    public static final Duration WEBAPP_SSO_SESSION_TIMEOUT = Duration.ofMinutes(30);

    public static final String AUTH_HEADER_KEY = "App_Auth";
    public static final String SIGNED_AUTH_HEADER_KEY = "Signed_App_Auth";

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(JwtHelper.class);

    /**
     * @throws Exception  if <var>cookieName</var> is not present in <var>httpServletRequest</var>
     */
    public static String readCookieValue(final HttpServletRequest httpServletRequest,
                                         final String cookieName) throws Exception {
        final Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    LOGGER.trace("cookie {} read value {}", cookieName, cookie);
                    return cookie.getValue();
                }
            }
        }

        throw new Exception("Cookie " + cookieName + " is not set");
    }

    /**
     * @param cookieConfig  the cookie will have its HttpOnly and Secure flags set to match this config
     */
    public static void writeCookie(final HttpServletResponse httpServletResponse,
                                   final String cookieName,
                                   final String cookieValue,
                                   final SessionCookieConfig cookieConfig) {

        final Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(cookieConfig.isHttpOnly());
        cookie.setSecure(cookieConfig.isSecure());

        httpServletResponse.addCookie(cookie);
        LOGGER.debug("Added written cookie {} with name {} and value {} to the HttpServletResponse",
                cookie, cookie.getName(), cookie.getValue());
    }

    public static JWTClaimsSet refreshJwt(final JWTClaimsSet jwtClaimsSet) {
        try { Thread.sleep(10); } catch (final InterruptedException ie) { };

        final Instant newExpiryTime = Instant.now().plus(WEBAPP_SSO_SESSION_TIMEOUT);

        final JWTClaimsSet updatedJwtClaimsSet = new JWTClaimsSet.Builder()
                .claim(JWT_KEY_SUBJECT_USERNAME, jwtClaimsSet.getSubject())
                .claim(JWT_KEY_SUBJECT_EMAIL, jwtClaimsSet.getClaim(JWT_KEY_SUBJECT_EMAIL))
                .claim(JWT_KEY_GIVEN_NAME, jwtClaimsSet.getClaim(JWT_KEY_GIVEN_NAME))
                .claim(JWT_KEY_FAMILY_NAME, jwtClaimsSet.getClaim(JWT_KEY_FAMILY_NAME))
                .claim(JWT_KC_USER_ID, jwtClaimsSet.getClaim(JWT_KC_USER_ID))
                .claim(JWT_KEY_ISSUED_AT, jwtClaimsSet.getIssueTime())
                .claim(JWT_EXPIRY_TIME, (newExpiryTime.toEpochMilli() / 1000)) // JWT spec is epoch *seconds* relative
                .claim("str" + JWT_KEY_ISSUED_AT, jwtClaimsSet.getClaim("str" + JWT_KEY_ISSUED_AT))
                .claim("str" + JWT_EXPIRY_TIME, jwtClaimsSet.getClaim("str" + JWT_EXPIRY_TIME))
                .build();

        return updatedJwtClaimsSet;
    }

    public static boolean isJwtExpired(final JWTClaimsSet jwtClaimsSet) {
        return doesJwtExpiryWithinNMinutes(jwtClaimsSet, 0);
    }

    public static boolean doesJwtExpiryWithinNMinutes(final JWTClaimsSet preExistingJwtClaimsSet,
                                                      final int nMinutes) {

        assert preExistingJwtClaimsSet.getExpirationTime() != null;

        return Duration.between(Instant.now(), preExistingJwtClaimsSet.getExpirationTime().toInstant())
                       .minus(Duration.ofMinutes(nMinutes))
                       .isNegative();
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

            final String issuedAtStr = jwtClaimsSet.getStringClaim("str" + JwtHelper.STR_JWT_KEY_ISSUED_AT);
            final String expiryAtStr = jwtClaimsSet.getStringClaim("str" + JwtHelper.STR_JWT_EXPIRY_TIME);

            final boolean jwtExpired = doesJwtExpiryWithinNMinutes(jwtClaimsSet, 0);

            if (!jwtExpired) {
                final UserType authUserType =
                        buildAuthenticatedUserFromJwt(jwtClaimsSet, managerService);

                return authUserType;
            } else {
                LOGGER.debug("JWT has expired, returning null per method contract");
                return null;
            }
        } catch (final Exception pe) {
            LOGGER.error("Exception in processing JWT (returning null)", pe);

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

    public static boolean isSignedStrVerifiable(final String dataStr, final byte[] signedMsg) {
        LOGGER.debug("dataStr {}", dataStr);
        LOGGER.debug("signedMsg {}", signedMsg);

        boolean verified = false;

        try {
            final Signature signature = Signature.getInstance("SHA256withRSA");

            final PublicKey publicKey = SecurityUtils.getPublicKey(SecurityUtils.DEFAULT_KEY_ALIAS);
            LOGGER.debug("publicKey for verification of JWT signed: {}", publicKey);

            signature.initVerify(publicKey);
            signature.update(dataStr.getBytes(StandardCharsets.UTF_8));
            verified = signature.verify(signedMsg);
        } catch (final Exception e) {
            LOGGER.error("Exception in verifying signed JWT message", e);
        } finally {
            return verified;
        }
    }

    private static long calculateExpiryTime(final long createdAtEpoch, final Duration sessionDuration) {
        return createdAtEpoch + sessionDuration.toMillis();
    }
}
