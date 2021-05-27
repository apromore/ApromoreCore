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

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class JwtHelperUnitTest {

    private static final String VALID_KC_ISSUER_URL = "http://keycloak.apromoresso.net";
    private static final String VALID_TENANT_IP_ADDRESS = "203.20.10.4";

    private static final String SUBJECT_UUID = UUID.randomUUID().toString();
    private static final String SUBJECT_EMAIL_ADDRESS = "mic.giansiracusa@apromore.com";
    private static final String FIRST_NAME = "Mic";
    private static final String SURNAME = "G";

    /**
     * Test the {@link JwtHelper#getClaimsSetFromJWT} method.
     */
    @Test
    public void testGetClaimsSetFromJWT() throws Exception {

        final String testJWT = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImdpdmVuTmFtZSI6IlRlc3QiLCJmYW1pbHlOYW1lIjoiVXNlciIsInN0cmV4cCI6IjE2MjIwODExNTEiLCJleHAiOjE2MjIwODExNTEsImlhdCI6MTYyMjA4MTAwNCwiZW1haWwiOiIiLCJzdHJpYXQiOiJzdHIxNjIyMDgxMDA0Iiwia2N1c2VyaWQiOiJmOjJjYmMzYTY1LTI3M2EtNGMxMS1iMWU0LTNmOGQ2NWUzYmJiODo4In0.IR-srkII0aPVmWkGMdHf9UkHQbCTZOpId-tZwi4HTRoAKkOV4zv08jGshMWiiLG87RRBY-kOjoS3Hfr2-P5C09cTLucZOKsOHKa5_yKBem-IqO3fSLZvD80wSDd_36g1t9XLOESxIzoS0px7NwgGWK86uQ3D1PsqSLST6aW_usuiaV7zuQP5_1d2FsUObMTaI1QX_f5ZRnB8YQ6ElF5qSECJ0o_D9YQfUd_Cq07SP010za-azzmPjzExWyzmfZPXwHODHUupr1QtJDCmOJf3NygTbvO4QOPmgO-5mC1NnUi0WS8ONIcfTlBLi7mPzaCVnN7UeNLkuVC8FcmBfRWbMA;";

        final JWTClaimsSet jwtClaimsSet = JwtHelper.getClaimsSetFromJWT(testJWT);

        Assertions.assertNotNull(jwtClaimsSet);
        Assertions.assertEquals("admin", jwtClaimsSet.getSubject());
        Assertions.assertEquals("Test", jwtClaimsSet.getStringClaim(JwtHelper.JWT_KEY_GIVEN_NAME));
        Assertions.assertEquals("", jwtClaimsSet.getStringClaim(JwtHelper.JWT_KEY_SUBJECT_EMAIL));
        Assertions.assertEquals("f:2cbc3a65-273a-4c11-b1e4-3f8d65e3bbb8:8", jwtClaimsSet.getStringClaim(JwtHelper.JWT_KC_USER_ID));

        // Expiry time
        Date expectedExpirationTime = new Date(1622081151 * 1000L);  // 1622081151 is seconds since the epoch; Date expects milliseconds
        Assertions.assertEquals(Instant.parse("2021-05-27T02:05:51Z"), expectedExpirationTime.toInstant());
        Assertions.assertEquals(expectedExpirationTime, jwtClaimsSet.getExpirationTime());

        // Issue time
        Date expectedIssueTime = new Date(1622081004 * 1000L);  // 1622081004 is seconds since the epoch; Date expects milliseconds
        Assertions.assertEquals(Instant.parse("2021-05-27T02:03:24Z"), expectedIssueTime.toInstant());
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
            .expirationTime(Date.from(Instant.now().plus(Duration.ofMinutes(2))))
            .build();

        Assertions.assertFalse(JwtHelper.doesJwtExpiryWithinNMinutes(jwtClaimsSet, 1), "Shouldn't expire within the next minute");
        Assertions.assertTrue(JwtHelper.doesJwtExpiryWithinNMinutes(jwtClaimsSet, 3), "Should expire within the next 3 minutes");
    }
}
