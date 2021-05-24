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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.ExpectedException;
import org.keycloak.AuthorizationContext;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apromore.security.config.KeycloakAppConstants;

import static org.apromore.security.JwtUtils.JWT_KEY_SUBJECT_USERNAME;
import static org.apromore.security.JwtUtils.JWT_KEY_ISSUED_AT;
import static org.apromore.security.JwtUtils.JWT_EXPIRY_TIME;

import static org.apromore.security.util.AssertUtils.notNullAssert;

public class JwtUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtilsTest.class);

    private static final String VALID_KC_ISSUER_URL = "http://keycloak.apromoresso.net";
    private static final String INVALID_KC_ISSUER_URL = "ftp" + KeycloakAppConstants.KC_DEV_SERVER_URL;
    private static final String VALID_TENANT_IP_ADDRESS = "203.20.10.4";
    private static final String INVALID_TENANT_IP_ADDRESS = "300.20.10.4";

    private static final String SUBJECT_UUID = UUID.randomUUID().toString();
    private static final String SUBJECT_EMAIL_ADDRESS = "mic.giansiracusa@apromore.com";
    private static final String FIRST_NAME = "Mic";
    private static final String SURNAME = "G";

    private static final String KS_AND_KEY_PASSWORD_ENV_KEY = "KS_PASSWD";
    private static final String SECURITY_RESOURCES_DIR_KEY = "SECURITY_RESOURCES_DIR";

    private static final String JWT_KEY_SUBJECT_ID = "sub";
    private static final String JWT_KEY_SUBJECT_EMAIL = "email";
    private static final String JWT_KEY_GIVEN_NAME = "givenName";
    private static final String JWT_KEY_FAMILY_NAME = "familyName";
    private static final String JWT_KC_USER_ID = "kcuserid";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private static SimpleKeycloakAccount s_validSimpleKeycloakAccount;

    private KeyPair keyPair;

    private String tmpDirPath;
    private String ksFilePath;

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

    @Before
    public void setup() throws NoSuchAlgorithmException, IOException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();

        ksFilePath = setupTestKeystoreInTmpDir();

        environmentVariables.set(KS_AND_KEY_PASSWORD_ENV_KEY, "topSecret");
    }


    /**
     *
     *
     * @return File path to the new keystore directory.
     */
    private String setupTestKeystoreInTmpDir() throws IOException {
        tmpDirPath = System.getProperty("java.io.tmpdir");
        environmentVariables.set(SECURITY_RESOURCES_DIR_KEY, tmpDirPath);

        final String targetFilePath = tmpDirPath + "/" + "apSecurityTS.jks";

        final String testResourcesPath = "src/test/resources";

        File file = new File(testResourcesPath);
        String absolutePath = file.getAbsolutePath() + "/apSecurityTS.jks";

        Files.copy(new File(absolutePath).toPath(), new File(targetFilePath).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return targetFilePath;
    }

    @Test
    public void isJwtExpired_nullJwtParameter_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'jwtClaimsSet' must not be null");

        JwtUtils.isJwtExpired(null);
    }

    @Test
    public void refreshJwt() {
        final JWTClaimsSet jwtClaimsSet =
                JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);

        final JWTClaimsSet refreshedJwtClaimsSet = JwtUtils.refreshJwt(jwtClaimsSet);

        Assert.assertEquals(jwtClaimsSet.getClaim(JwtUtils.JWT_KEY_SUBJECT_USERNAME),
                refreshedJwtClaimsSet.getClaim(JwtUtils.JWT_KEY_SUBJECT_USERNAME));
        Assert.assertEquals(jwtClaimsSet.getClaim(JwtUtils.JWT_KEY_SUBJECT_EMAIL),
                refreshedJwtClaimsSet.getClaim(JwtUtils.JWT_KEY_SUBJECT_EMAIL));
        Assert.assertEquals(jwtClaimsSet.getClaim(JwtUtils.JWT_KEY_GIVEN_NAME),
                refreshedJwtClaimsSet.getClaim(JwtUtils.JWT_KEY_GIVEN_NAME));
        Assert.assertEquals(jwtClaimsSet.getClaim(JwtUtils.JWT_KEY_FAMILY_NAME),
                refreshedJwtClaimsSet.getClaim(JwtUtils.JWT_KEY_FAMILY_NAME));
        Assert.assertEquals(jwtClaimsSet.getClaim(JwtUtils.JWT_KEY_ISSUED_AT),
                refreshedJwtClaimsSet.getClaim(JwtUtils.JWT_KEY_ISSUED_AT));
        Assert.assertEquals(jwtClaimsSet.getClaim("str" + JwtUtils.JWT_KEY_ISSUED_AT),
                refreshedJwtClaimsSet.getClaim("str" + JwtUtils.JWT_KEY_ISSUED_AT));
        Assert.assertNotEquals(jwtClaimsSet.getClaim(JwtUtils.JWT_EXPIRY_TIME),
                refreshedJwtClaimsSet.getClaim(JwtUtils.JWT_EXPIRY_TIME));
        final Long originalExp = (Long)jwtClaimsSet.getClaim(JwtUtils.JWT_EXPIRY_TIME);
        final Long newExp = (Long)refreshedJwtClaimsSet.getClaim(JwtUtils.JWT_EXPIRY_TIME);
        Assert.assertTrue(newExp.longValue() > originalExp.longValue());
    }

    @Test
    public void isJwtExpired_jwtIsExpired_returnsTrue() {
        final ZoneId zoneId = ZoneId.systemDefault();
        final LocalDateTime expiresTime = LocalDateTime.now();
        final LocalDateTime issuedAt = LocalDateTime.now();
        final long expiryJwtEpoch = expiresTime.atZone(zoneId).toEpochSecond();
        final long issuedAtEpoch = issuedAt.atZone(zoneId).toEpochSecond();

        try {
            Thread.sleep(1000); }
        catch(final InterruptedException ie) {};

        final JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim(JWT_KEY_SUBJECT_USERNAME, "someusername")
                .claim(JWT_KEY_SUBJECT_EMAIL, "someusername@apromore.com")
                .claim(JWT_KEY_GIVEN_NAME, "Some")
                .claim(JWT_KEY_FAMILY_NAME, "User")
                .claim(JWT_KC_USER_ID, UUID.randomUUID())
                .claim(JWT_KEY_ISSUED_AT, issuedAtEpoch)
                .claim(JWT_EXPIRY_TIME, expiryJwtEpoch)
                .build();

        final boolean jwtExpired = JwtUtils.isJwtExpired(jwtClaimsSet);

        Assert.assertTrue(jwtExpired);
    }

    @Test
    public void isJwtExpired_jwtExpiresExplicitSetIsNotExpired_returnsFalse() {
        final ZoneId zoneId = ZoneId.systemDefault();
        final LocalDateTime issuedAt = LocalDateTime.now();
        final LocalDateTime expiresTime = LocalDateTime.now().plus(30, ChronoUnit.MINUTES);
        final long expiryJwtEpoch = expiresTime.atZone(zoneId).toEpochSecond();
        final long issuedAtEpoch = issuedAt.atZone(zoneId).toEpochSecond();

        final JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim(JWT_KEY_SUBJECT_USERNAME, "someusername")
                .claim(JWT_KEY_SUBJECT_EMAIL, "someusername@apromore.com")
                .claim(JWT_KEY_GIVEN_NAME, "Some")
                .claim(JWT_KEY_FAMILY_NAME, "User")
                .claim(JWT_KC_USER_ID, UUID.randomUUID())
                .claim(JWT_KEY_ISSUED_AT, issuedAtEpoch)
                .claim(JWT_EXPIRY_TIME, expiryJwtEpoch)
                .build();

        final boolean jwtExpired = JwtUtils.isJwtExpired(jwtClaimsSet);

        Assert.assertFalse(jwtExpired);
    }

    @Test
    public void isJwtExpired_jwtExpiresImplicitSetIsNotExpired_returnsFalse() {
        final ZoneId zoneId = ZoneId.systemDefault();
        final LocalDateTime issuedAt = LocalDateTime.now();
        final long issuedAtEpoch = issuedAt.atZone(zoneId).toEpochSecond();

        final JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim(JWT_KEY_SUBJECT_USERNAME, "someusername")
                .claim(JWT_KEY_SUBJECT_EMAIL, "someusername@apromore.com")
                .claim(JWT_KEY_GIVEN_NAME, "Some")
                .claim(JWT_KEY_FAMILY_NAME, "User")
                .claim(JWT_KC_USER_ID, UUID.randomUUID())
                .claim(JWT_KEY_ISSUED_AT, issuedAtEpoch)
                .claim(JWT_EXPIRY_TIME, null)
                .build();

        final boolean jwtExpired = JwtUtils.isJwtExpired(jwtClaimsSet);

        Assert.assertFalse(jwtExpired);
    }

    @Test
    public void createJsonWebToken_nullKeycloakIssuerUrl_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'keycloakIssuerUrl' must not be null");

        JwtUtils.createJsonWebToken(null, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);
    }

    @Test
    public void createJsonWebToken_invalidKeycloakIssuerUrl_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Purported http URL '" + INVALID_KC_ISSUER_URL + "' is invalid");

        JwtUtils.createJsonWebToken(INVALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);
    }

    @Test
    public void createJsonWebToken_nonValidUrlFormatKeycloakIssuerUrl_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Purported http URL '" + INVALID_KC_ISSUER_URL + "' is invalid");

        JwtUtils.createJsonWebToken(INVALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);
    }

    @Test
    public void createJsonWebToken_nullSimpleKeycloakAccount_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'simpleKeycloakAccount' must not be null");

        JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, null);
    }

    @Test
    public void createJsonWebToken_nonValidTenantIpAddress_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);

        JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, INVALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);
    }

    @Test
    public void createJsonWebToken_validParams_jwtTokenCreated() throws ParseException {
        final JWTClaimsSet jwtClaimsSet =
                JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);

        notNullAssert(jwtClaimsSet, "jwtClaimsSet");
        Assert.assertEquals(SUBJECT_UUID, jwtClaimsSet.getStringClaim(JwtUtils.JWT_KC_USER_ID));
        Assert.assertEquals(SUBJECT_EMAIL_ADDRESS, jwtClaimsSet.getStringClaim(JwtUtils.JWT_KEY_SUBJECT_EMAIL));
        Assert.assertEquals(FIRST_NAME, jwtClaimsSet.getStringClaim(JwtUtils.JWT_KEY_GIVEN_NAME));
        Assert.assertEquals(SURNAME, jwtClaimsSet.getStringClaim(JwtUtils.JWT_KEY_FAMILY_NAME));

        jwtClaimsSet.getSubject();
    }

    @Test
    public void signJsonWebToken_nullPrivateKey_throwsIllegalArgumentException() throws JOSEException {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'privateKey' must not be null");

        final JWTClaimsSet jwtClaimsSet =
                JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);

        JwtUtils.signJsonWebToken(jwtClaimsSet, null);
    }

    @Test
    public void signJsonWebToken_nullJsonWebToken_throwsIllegalArgumentException() throws JOSEException {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'privateKey' must not be null");

        final JWTClaimsSet jwtClaimsSet =
                JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);

        JwtUtils.signJsonWebToken(null, null);
    }

    @Test
    public void signJsonWebToken_validParams_signedJwtTokenReturned() throws Exception {
        final JWTClaimsSet jwtClaimsSet =
                JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);

        final SignedJWT signedJWT = JwtUtils.signJsonWebToken(jwtClaimsSet, keyPair.getPrivate());

        notNullAssert(signedJWT, "signnedJWT");
        final JWTClaimsSet jwtClaimsSetFromSignature = signedJWT.getJWTClaimsSet();
        notNullAssert(jwtClaimsSetFromSignature, "jwtClaimsSetFromSignature");

        Assert.assertEquals(jwtClaimsSetFromSignature, jwtClaimsSet);
    }

    @Test
    public void verifySignedJsonWebToken_validParams_signedJwtTokenReturned() throws Exception {
        final JWTClaimsSet jwtClaimsSet =
                JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);

        final SignedJWT signedJWT = JwtUtils.signJsonWebToken(jwtClaimsSet, keyPair.getPrivate());

        notNullAssert(signedJWT, "signedJWT");

        try {
            final JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) keyPair.getPublic());
            final boolean verifiedSignature = signedJWT.verify(verifier);
            Assert.assertTrue(verifiedSignature);
        } catch (final JOSEException e) {
            System.err.println("Couldn't verify signature: " + e.getMessage());
        }
    }

    @Test
    public void verifySignedJsonWebToken_validParams_signedJwtTokenReturned_CHECK_SIGNATURE() throws Exception {
        final JWTClaimsSet jwtClaimsSet =
                JwtUtils.createJsonWebToken(VALID_KC_ISSUER_URL, VALID_TENANT_IP_ADDRESS, s_validSimpleKeycloakAccount);

        final SignedJWT signedJWT = JwtUtils.signJsonWebToken(jwtClaimsSet, keyPair.getPrivate());

        final String signature = signedJWT.getSignature().toString();
        final Payload payload = signedJWT.getPayload();
        final String payloadStr = payload.toString();

        final String signedJWTSerialisedToStr = signedJWT.serialize();
        final SignedJWT signedJWTFromStr = SignedJWT.parse(signedJWTSerialisedToStr);

        final Map payloadMap = payload.toJSONObject();
        final String subjectUuid = (String)payloadMap.get(JWT_KC_USER_ID);
        final String subjectEmailAddress = (String)payloadMap.get(JWT_KEY_SUBJECT_EMAIL);
        final String subjectFirstName = (String)payloadMap.get(JWT_KEY_GIVEN_NAME);
        final String subjectSurname = (String)payloadMap.get(JWT_KEY_FAMILY_NAME);

        notNullAssert(signedJWT, "signedJWT");
        notNullAssert(subjectUuid, "subjectUuid");
        notNullAssert(subjectEmailAddress, "subjectEmailAddress");
        notNullAssert(subjectFirstName, "subjectFirstName");
        notNullAssert(subjectSurname, "subjectSurname");

        Assert.assertEquals(subjectUuid, SUBJECT_UUID);
        Assert.assertEquals(subjectEmailAddress, SUBJECT_EMAIL_ADDRESS);
        Assert.assertEquals(subjectFirstName, FIRST_NAME);
        Assert.assertEquals(subjectSurname, SURNAME);

        try {
            final JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) keyPair.getPublic());
            final boolean verifiedSignature = signedJWT.verify(verifier);
            Assert.assertTrue(verifiedSignature);
        } catch (final JOSEException e) {
            final String errMsg = "Couldn't verify signature: " + e.getMessage();

            logger.error(errMsg);

            Assert.fail(errMsg);
        }
    }
}
