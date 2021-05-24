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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JwtHelperTest {

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


}
