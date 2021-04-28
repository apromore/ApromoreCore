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
package org.apromore.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.security.PrivateKey;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.apromore.security.util.AssertUtils.*;

/**
 * Utility class for JSON Web Token (JWT) related functionality.
 *
 * N.B. This class will be refactored at some time in the near future to be encapsulated
 * in a more central Apromore Java maven library - so that it can be utilised uniformly in
 * distributed Java components and maintained without code redundancy (and, with robust
 * automated unit tests centrally).
 */
public class JwtUtils {

    public static final Duration WEBAPP_SSO_SESSION_TIMEOUT = Duration.ofMinutes(30);

    public static final String JWT_KEY_SUBJECT_USERNAME = "sub";
    public static final String JWT_KEY_ISSUED_AT = "iat";
    public static final String JWT_EXPIRY_TIME = "exp";
    public static final String JWT_KC_USER_ID = "kcuserid";

    public static final String JWT_KEY_SUBJECT_EMAIL = "email";
    public static final String JWT_KEY_GIVEN_NAME = "givenName";
    public static final String JWT_KEY_FAMILY_NAME = "familyName";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static JWTClaimsSet createJsonWebToken(
            final String keycloakIssuerUrl,
            final String tenantIpAddress,
            final SimpleKeycloakAccount simpleKeycloakAccount) {
        notNullAssert(keycloakIssuerUrl, "keycloakIssuerUrl");
        validHttpProtocolUrl(keycloakIssuerUrl, true);
        validIpAddress(tenantIpAddress);
        notNullAssert(simpleKeycloakAccount, "simpleKeycloakAccount");

        final long issuedAtEpoch = new Date().getTime();

        final Principal securityPrincipal = simpleKeycloakAccount.getPrincipal();

        final AccessToken kcAccessToken = JwtUtils.getKeycloakAccessToken(simpleKeycloakAccount);

        final String username =
                StringUtils.isEmpty(kcAccessToken.getPreferredUsername()) ?
                        kcAccessToken.getEmail() : kcAccessToken.getPreferredUsername();

        final String kcUserId = kcAccessToken.getSubject();

        final JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim(JWT_KEY_SUBJECT_USERNAME, username)
                .claim(JWT_KEY_SUBJECT_EMAIL, kcAccessToken.getEmail())
                .claim(JWT_KEY_GIVEN_NAME, kcAccessToken.getGivenName())
                .claim(JWT_KEY_FAMILY_NAME, kcAccessToken.getFamilyName())
                .claim(JWT_KC_USER_ID, kcUserId)
                .claim(JWT_KEY_ISSUED_AT, issuedAtEpoch)
                .claim(JWT_EXPIRY_TIME, calculateExpiryTime(issuedAtEpoch, WEBAPP_SSO_SESSION_TIMEOUT))
                .build();

        return jwtClaimsSet;
    }

    public static JWTClaimsSet getClaimsSetFromJWT(final String jwtStr) throws Exception {
        final JWT jwtParsed = JWTParser.parse(jwtStr);
        final JWTClaimsSet jwtClaimsSet = jwtParsed.getJWTClaimsSet();

        return jwtClaimsSet;
    }

    public static SignedJWT signJsonWebToken(final JWTClaimsSet jwtClaimsSet, final PrivateKey privateKey)
            throws JOSEException {
        notNullAssert(privateKey, "privateKey");
        notNullAssert(jwtClaimsSet, "jwtClaimsSet");

        final JWSSigner signer = new RSASSASigner(privateKey);

        final SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), jwtClaimsSet);
        signedJWT.sign(signer);

        return signedJWT;
    }

    public static boolean isJwtExpired(final JWTClaimsSet jwtClaimsSet) {
        notNullAssert(jwtClaimsSet, "jwtClaimsSet");

        final Object jwtExpiryEpochObj = jwtClaimsSet.getClaim(JWT_EXPIRY_TIME);

        long jwtExpiryEpoch;
        if (jwtExpiryEpochObj != null) {
            jwtExpiryEpoch = (Long)jwtExpiryEpochObj;
        } else { // Calculate relative
            jwtExpiryEpoch = calculateExpiryTime(
                    (Long)jwtClaimsSet.getClaim(JWT_KEY_ISSUED_AT), WEBAPP_SSO_SESSION_TIMEOUT);
        }

        final Long nowEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        return nowEpoch > jwtExpiryEpoch;
    }

    private static long calculateExpiryTime(final long createdAtEpoch, final Duration sessionDuration) {
        return createdAtEpoch + sessionDuration.toMillis();
    }

    private static AccessToken getKeycloakAccessToken(final SimpleKeycloakAccount simpleKeycloakAccount) {
        return simpleKeycloakAccount.getKeycloakSecurityContext().getToken();
    }

    public static boolean verifyJsonWebToken(
            final String keycloakIssuerUrl,
            final String tenantIpAddress,
            final RefreshableKeycloakSecurityContext refreshableKeycloakSecurityContext) {
        // Create a JWT processor for the access tokens
        final ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        // Set the required "typ" header "at+jwt" for access tokens issued by the
        // Connect2id server, may not be set by other servers
        jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(JOSEObjectType.JWT));

        // The public RSA keys to validate the signatures will be sourced from the
        // OAuth 2.0 server's JWK set, published at a well-known URL. The RemoteJWKSet
        // object caches the retrieved keys to speed up subsequent look-ups and can
        // also handle key-rollover
        // final JWKSource<SecurityContext> keySource =
        //        new RemoteJWKSet<>(new URL("https://demo.c2id.com/c2id/jwks.json"));

        // The expected JWS algorithm of the access tokens (agreed out-of-band)
        final JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(keycloakIssuerUrl).build(),
                new HashSet<>(Arrays.asList(
                        JWT_KEY_SUBJECT_USERNAME,
                        JWT_KEY_ISSUED_AT,
                        JWT_EXPIRY_TIME))));

        // @2do: Remove hard-coding

        return false;
    }

}
