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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class CryptoUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(CryptoUtilsTest.class);

    private static final String SOME_RAW_TEXT_STRING = "Some raw text string";
    private static final byte[] NON_NULL_BYTE_ARRAY = new byte[256];

    private static final String SYMMETRIC_ENCRYPTION_SECRET = "keepItSSSSSSSShhhh!!";

    private static final String KEYSTORE_FILE = "apSecurityTS.jks";

    private static final String KS_AND_KEY_PASSWORD_ENV_KEY = "KS_PASSWD";
    private static final String SECURITY_RESOURCES_DIR_KEY = "SECURITY_RESOURCES_DIR";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private KeyPair keyPair;

    private String tmpDirPath;
    private String ksFilePath;

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
    public void signData_nullPrivateKey_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'privateKey' must not be null");

        CryptoUtils.signData(null,
                "Some raw text string",
                LoggerFactory.getLogger(CryptoUtilsTest.class));
    }

    @Test
    public void signData_nullRawDataStr_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'rawDataStr' must not be null");

        CryptoUtils.signData(keyPair.getPrivate(),
                null,
                LoggerFactory.getLogger(CryptoUtilsTest.class));
    }

    @Test
    public void signData_nullLogger_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'logger' must not be null");

        CryptoUtils.signData(keyPair.getPrivate(),
                "Some raw text string",
                null);
    }

    @Test
    public void signData_allSuppliedArguments_signsData() {
        try {
            final byte[] signedDataBytes = CryptoUtils.signData(keyPair.getPrivate(),
                    SOME_RAW_TEXT_STRING,
                    LoggerFactory.getLogger(CryptoUtilsTest.class));
            Assert.assertEquals(256, signedDataBytes.length);

            Assert.assertNotNull(signedDataBytes);
        } catch (final Exception e) {
            Assert.fail("Exception with class " + e.getClass() + " should not have been thrown; exception message " +
                "was " + e.getMessage());
        }
    }

    @Test
    public void verifyData_nullPublicKey_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'publicKey' must not be null");

        CryptoUtils.verifyData(null,
                SOME_RAW_TEXT_STRING,
                NON_NULL_BYTE_ARRAY,
                LoggerFactory.getLogger(CryptoUtilsTest.class));
    }

    @Test
    public void verifyData_nullDataStr_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'dataStr' must not be null");

        CryptoUtils.verifyData(keyPair.getPublic(),
                null,
                NON_NULL_BYTE_ARRAY,
                LoggerFactory.getLogger(CryptoUtilsTest.class));
    }

    @Test
    public void verifyData_nullSignedMsg_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'signedMsg' must not be null");

        CryptoUtils.verifyData(keyPair.getPublic(),
                SOME_RAW_TEXT_STRING,
                null,
                LoggerFactory.getLogger(CryptoUtilsTest.class));
    }

    @Test
    public void verifyData_nullLogger_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'logger' must not be null");

        CryptoUtils.verifyData(keyPair.getPublic(),
                SOME_RAW_TEXT_STRING,
                NON_NULL_BYTE_ARRAY,
                null);
    }

    @Test
    public void verifyData_allSuppliedCorrectMatchingArguments_verifyReturnsTrue() {
        try {
            final byte[] signedDataBytes =
                    CryptoUtils.signData(keyPair.getPrivate(),
                        SOME_RAW_TEXT_STRING,
                        LoggerFactory.getLogger(CryptoUtilsTest.class));
            Assert.assertEquals(256, signedDataBytes.length);
            Assert.assertNotNull(signedDataBytes);

            final boolean verifiedFlag =
                    CryptoUtils.verifyData(keyPair.getPublic(),
                        SOME_RAW_TEXT_STRING,
                        signedDataBytes,
                        LoggerFactory.getLogger(CryptoUtilsTest.class));

            Assert.assertTrue(verifiedFlag);
        } catch (final Exception e) {
            Assert.fail("Exception with class " + e.getClass() + " should not have been thrown; exception message " +
                    "was " + e.getMessage());
        }
    }

    @Test
    public void verifyData_allSuppliedCorrectMatchingArguments_verifyReturnsFalse() {
        try {
            final byte[] signedDataBytes =
                    CryptoUtils.signData(keyPair.getPrivate(),
                            SOME_RAW_TEXT_STRING,
                            LoggerFactory.getLogger(CryptoUtilsTest.class));
            Assert.assertEquals(256, signedDataBytes.length);
            Assert.assertNotNull(signedDataBytes);

            final boolean verifiedFlag =
                    CryptoUtils.verifyData(keyPair.getPublic(),
                            SOME_RAW_TEXT_STRING + "_",
                            signedDataBytes,
                            LoggerFactory.getLogger(CryptoUtilsTest.class));

            Assert.assertFalse(verifiedFlag);
        } catch (final Exception e) {
            Assert.fail("Exception with class " + e.getClass() + " should not have been thrown; exception message " +
                    "was " + e.getMessage());
        }
    }

    @Test
    public void symmetricEncrypt_nullStrToEncrypt_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'strToEncrypt' must not be null");

        CryptoUtils.symmetricEncrypt(null, SYMMETRIC_ENCRYPTION_SECRET);
    }

    @Test
    public void symmetricEncrypt_nullEncryptionSecret_throwsIllegalArgumentException() throws Exception  {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'encryptionSecret' must not be null");

        CryptoUtils.symmetricEncrypt(SOME_RAW_TEXT_STRING, null);
    }

    @Test
    public void symmetricEncrypt_suppliedValidParams_generatesEncryptedStr() {
        final String encryptedString = CryptoUtils.symmetricEncrypt(SOME_RAW_TEXT_STRING,
                SYMMETRIC_ENCRYPTION_SECRET);

        Assert.assertNotNull(encryptedString);
        Assert.assertFalse(encryptedString.equals(SOME_RAW_TEXT_STRING));
        Assert.assertEquals(44, encryptedString.length());
    }

    @Test
    public void symmetricDecrypt_nullStrToDecrypt_throwsIllegalArgumentException() throws Exception  {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'strToDecrypt' must not be null");

        CryptoUtils.symmetricDecrypt(null, SYMMETRIC_ENCRYPTION_SECRET);
    }

    @Test
    public void symmetricDecrypt_nullEncryptionSecret_throwsIllegalArgumentException() throws Exception  {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'encryptionSecret' must not be null");

        CryptoUtils.symmetricDecrypt("Blah", null);
    }

    @Test
    public void symmetricDecrypt_suppliedParamsButInvalidKey_throwsBadPaddingException() throws Exception  {
        exceptionRule.expect(BadPaddingException.class);
        exceptionRule.expectMessage("Given final block not properly padded. Such issues can " +
                "arise if a bad key is used during decryption.");


        final String encryptedString = CryptoUtils.symmetricEncrypt(SOME_RAW_TEXT_STRING,
                SYMMETRIC_ENCRYPTION_SECRET);

        final String decryptedString =
                CryptoUtils.symmetricDecrypt(encryptedString,
                        "Blah" + SYMMETRIC_ENCRYPTION_SECRET);
    }

    @Test
    public void symmetricDecrypt_suppliedValidParams_decryptsAsExpected() throws Exception {
        final String encryptedString = CryptoUtils.symmetricEncrypt(SOME_RAW_TEXT_STRING,
                SYMMETRIC_ENCRYPTION_SECRET);

        final String decryptedString =
                CryptoUtils.symmetricDecrypt(encryptedString, SYMMETRIC_ENCRYPTION_SECRET);

        Assert.assertNotNull(decryptedString);
        Assert.assertEquals(SOME_RAW_TEXT_STRING, decryptedString);
    }

}
