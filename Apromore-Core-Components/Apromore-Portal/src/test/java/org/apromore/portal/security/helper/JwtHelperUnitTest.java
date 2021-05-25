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

import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JwtHelperUnitTest {

    private static final String VALID_KC_ISSUER_URL = "http://keycloak.apromoresso.net";
    private static final String VALID_TENANT_IP_ADDRESS = "203.20.10.4";

    private static final String SUBJECT_UUID = UUID.randomUUID().toString();
    private static final String SUBJECT_EMAIL_ADDRESS = "mic.giansiracusa@apromore.com";
    private static final String FIRST_NAME = "Mic";
    private static final String SURNAME = "G";

    /*
    private static SimpleKeycloakAccount s_validSimpleKeycloakAccount;

    @BeforeClass
    public static void setupBeforeClass() {
        final AccessToken accessToken = new AccessToken();
        accessToken.setGivenName(FIRST_NAME);
        accessToken.setFamilyName(SURNAME);
        accessToken.setEmail(SUBJECT_EMAIL_ADDRESS);
        accessToken.setSubject(SUBJECT_UUID);

        final AuthorizationContext authorizationContext =
                new AuthorizationContext(accessToken, new PolicyEnforcerConfig.PathConfig());

        final RefreshableKeycloakSecurityContext keycloakSecurityCtxt =
                new RefreshableKeycloakSecurityContext(null,
                        null,
                        "626893224",
                        accessToken,
                        "626893224",
                        null,
                        "886653252");
        keycloakSecurityCtxt.setAuthorizationContext(authorizationContext);

        final Principal principal = new KeycloakPrincipal<>("mic", keycloakSecurityCtxt);
        final Set<String> roles = new HashSet<>();
        roles.add("ADMIN");

        s_validSimpleKeycloakAccount = new SimpleKeycloakAccount(principal, roles, keycloakSecurityCtxt);
    }
    */

    /*
    @Test
    public void refreshJwt() {
        final JWTClaimsSet jwtClaimsSet =
                JwtHelper.createJsonWebToken(VALID_KC_ISSUER_URL,
                        VALID_TENANT_IP_ADDRESS,
                        s_validSimpleKeycloakAccount);

        final JWTClaimsSet refreshedJwtClaimsSet = JwtHelper.refreshJwt(jwtClaimsSet);

        Assert.assertEquals(jwtClaimsSet.getClaim(JwtHelper.JWT_KEY_SUBJECT_USERNAME),
                refreshedJwtClaimsSet.getClaim(JwtHelper.JWT_KEY_SUBJECT_USERNAME));
        Assert.assertEquals(jwtClaimsSet.getClaim(JwtHelper.JWT_KEY_SUBJECT_EMAIL),
                refreshedJwtClaimsSet.getClaim(JwtHelper.JWT_KEY_SUBJECT_EMAIL));
        Assert.assertEquals(jwtClaimsSet.getClaim(JwtHelper.JWT_KEY_GIVEN_NAME),
                refreshedJwtClaimsSet.getClaim(JwtHelper.JWT_KEY_GIVEN_NAME));
        Assert.assertEquals(jwtClaimsSet.getClaim(JwtHelper.JWT_KEY_FAMILY_NAME),
                refreshedJwtClaimsSet.getClaim(JwtHelper.JWT_KEY_FAMILY_NAME));
        Assert.assertEquals(jwtClaimsSet.getClaim(JwtHelper.JWT_KEY_ISSUED_AT),
                refreshedJwtClaimsSet.getClaim(JwtHelper.JWT_KEY_ISSUED_AT));
        Assert.assertEquals(jwtClaimsSet.getClaim("str" + JwtHelper.JWT_KEY_ISSUED_AT),
                refreshedJwtClaimsSet.getClaim("str" + JwtHelper.JWT_KEY_ISSUED_AT));
        Assert.assertNotEquals(jwtClaimsSet.getClaim(JwtHelper.JWT_EXPIRY_TIME),
                refreshedJwtClaimsSet.getClaim(JwtHelper.JWT_EXPIRY_TIME));
        final Long originalExp = (Long)jwtClaimsSet.getClaim(JwtHelper.JWT_EXPIRY_TIME);
        final Long newExp = (Long)refreshedJwtClaimsSet.getClaim(JwtHelper.JWT_EXPIRY_TIME);
        Assert.assertTrue(newExp.longValue() > originalExp.longValue());
    }
    */

    /**
     * Test the {@link JwtHelper#getClaimsSetFromJWT} method.
     *
     * This test shows up a problem that may originate with the security microservice, that
     * it's issuing JWTs with time-valued fields encoded as milliseconds since the unix
     * epoch rather than seconds since the unix epoch.
     */
    @Test
    public void testGetClaimsSetFromJWT() throws Exception {

        final String testJWT = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImdpdmVuTmFtZSI6IlRlc3QiLCJmYW1pbHlOYW1lIjoiVXNlciIsImV4cCI6MTYxOTE1NDgyNTkwMywiaWF0IjoxNjE5MTQ3NjI1OTAzLCJlbWFpbCI6IiIsImtjdXNlcmlkIjoiZjoyY2JjM2E2NS0yNzNhLTRjMTEtYjFlNC0zZjhkNjVlM2JiYjg6OCJ9.XtEHtIevrp7PVbYLwGbjBbvB32kZ8ZM2au-fbKGn03zjLYXojibRXbLfqJb1p45WbEn9nvoGn-oFvmSecWQsQGBSF3iGhy1RjpPgKuZxMdKBaGDaLdqix0OgQYFOk2RLaSRoBTVEdd579UCIavF66vVW9rKCC4zE2AIFSFFFPgJK25lwq7c31DyYjs5deA_SlmV5z1tfkk27ksrOuVmy-LzmAf81fFp5OTftTvM3Zfb04AmKBnMLyIuSb63zdlRlHrgjVbsE29CIr4u0NIdNt-hOrjXXe_j8PMO26tsycG0ku2Fg8ZYcvozWeTa3uvH4dvYN7Dsrn44A9P2tDwze8w";

        final JWTClaimsSet jwtClaimsSet = JwtHelper.getClaimsSetFromJWT(testJWT);

        Assertions.assertNotNull(jwtClaimsSet);
        Assertions.assertEquals("admin", jwtClaimsSet.getSubject());
        Assertions.assertEquals("Test", jwtClaimsSet.getStringClaim(JwtHelper.JWT_KEY_GIVEN_NAME));
        Assertions.assertEquals("", jwtClaimsSet.getStringClaim(JwtHelper.JWT_KEY_SUBJECT_EMAIL));
        Assertions.assertEquals("f:2cbc3a65-273a-4c11-b1e4-3f8d65e3bbb8:8", jwtClaimsSet.getStringClaim(JwtHelper.JWT_KC_USER_ID));

        // Because the expiry is misencoded using milliseconds rather than seconds in testJWT, it ends up in the nonsensical future
        Date expectedExpirationTime = new Date(1619154825903L * 1000);  // the sensible date would be if we didn't multiply by 1000
        Assertions.assertEquals(51378, expectedExpirationTime.getYear());
        Assertions.assertEquals(expectedExpirationTime, jwtClaimsSet.getExpirationTime());

        // Because the issue time is misencoded using milliseconds rather than seconds in testJWT, it ends up in the nonsensical future
        Date expectedIssueTime = new Date(1619147625903L * 1000);
        Assertions.assertEquals(51378, expectedIssueTime.getYear());
        Assertions.assertEquals(expectedIssueTime, jwtClaimsSet.getIssueTime());
    }

    /**
     * Test the {@link JwtHelper#doesJwtExpiryWithinNMinutes} method.
     *
     * It's tricky to test a method that internally uses the current time, but since we're dealing with
     * margins of minutes, it's pretty safe to assume the test runs faster than the clock does.
     */
    @Test
    public void testDoesJwtExpiryWithinNMinutes() throws Exception {

        // Test claim expiring 2 minutes hence
        final JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .expirationTime(Date.from(Instant.now().plusSeconds(120)))
            .build();

        Assertions.assertFalse(JwtHelper.doesJwtExpiryWithinNMinutes(jwtClaimsSet, 1), "Shouldn't expire within the next minute");
        Assertions.assertTrue(JwtHelper.doesJwtExpiryWithinNMinutes(jwtClaimsSet, 3), "Should expire within the next 3 minutes");
    }
}
