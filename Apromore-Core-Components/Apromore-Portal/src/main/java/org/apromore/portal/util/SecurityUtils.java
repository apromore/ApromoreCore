/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.portal.util;

import static org.apromore.portal.util.AssertUtils.notNullAssert;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Base64;

public final class SecurityUtils {

    private static final Logger logger = PortalLoggerFactory.getLogger(SecurityUtils.class);

    private static final String KEYSTORE_FILE = "apSecurityTS.jks";

    public static final String DEFAULT_KEY_ALIAS = "apseckey";

    private static SecretKeySpec secretKey;
    private static byte[] key;

    private static char[] ksPassword;

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static final String KS_AND_KEY_PASSWORD_ENV_KEY = "KS_PASSWD";
    private static final String SECURITY_RESOURCES_DIR_KEY = "SECURITY_RESOURCES_DIR";

    private static final String SYMMETRIC_CIPHER_FULL_ALG_SPEC = "AES/ECB/PKCS5Padding";
    private static final String SYMMETRIC_CIPHER_BASE_ALG = "AES";

    private static final String DIGEST_ALGORITHM = "SHA1";
    private static final String CHARSET_ENCODING = "UTF-8";

    private static String secRessKeystoreFilePathStr = null;

    static {
        String pwdKSStr = System.getenv(KS_AND_KEY_PASSWORD_ENV_KEY);

        if (! StringUtils.hasText(pwdKSStr)) {
            final String errMsg = "keystore password could not be attained from environment";

            logger.info("\n\n{}\n", errMsg);
            throw new IllegalStateException(errMsg);
        }

        String resourcesDirKey = System.getenv(SECURITY_RESOURCES_DIR_KEY);

        if (resourcesDirKey == null) {
            throw new IllegalStateException("Can not proceed as security resources directory is not defined");
        }

        if (! resourcesDirKey.endsWith("/")) {
            resourcesDirKey = resourcesDirKey + "/";
        }

        secRessKeystoreFilePathStr = resourcesDirKey + KEYSTORE_FILE;

        SecurityUtils.ksPassword = pwdKSStr.toCharArray();
        // For safety/clear
        pwdKSStr = null;
    }

    private SecurityUtils() {
        // Intentionally private constructor
    }

    /**
     * Get public key from keystore.
     *
     * @param keyAlias The alias for looking up the target key.
     *
     * @return <code>null</code> if the looked-up/retrieved (associated keypair) is not a PrivateKey.
     *
     * @throws Exception if there is a mismatch between passed-in key store password & ks password, etc/
     */
    public static final PublicKey getPublicKey(final String keyAlias) throws Exception {
        try (
                final InputStream inputStream = new FileInputStream(secRessKeystoreFilePathStr);
        ) {
            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(inputStream, SecurityUtils.ksPassword);

            final Key key = keystore.getKey(keyAlias, SecurityUtils.ksPassword);

            if (key instanceof PrivateKey) {
                // Get certificate of public key
                final Certificate cert = keystore.getCertificate(keyAlias);
                final PublicKey publicKey = cert.getPublicKey();

                return publicKey;
            } else {
                return null;
            }
        }
    }

    /**
     * Get private key from keystore.
     *
     * @param keyAlias The alias for looking up the target key.
     *
     * @return <code>null</code> if the looked-up/retrieved key is not a PrivateKey.
     *
     * @throws Exception if there is a mismatch between passed-in key store password & ks password, etc/
     */
    public static PrivateKey getPrivateKey(final String keyAlias) throws Exception {
        logger.info("\n\nkeyAlias: {} ", keyAlias);
        logger.info("\n\nsecRessKeystoreFilePathStr: {} ", secRessKeystoreFilePathStr);

        try (
                final InputStream inputStream = new FileInputStream(secRessKeystoreFilePathStr);
        ) {
            logger.info("\n\nFileInputStream was opened");

            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

            logger.info("\n\nSecurityUtils.ksPassword: {}", new String(SecurityUtils.ksPassword));
            keystore.load(inputStream, SecurityUtils.ksPassword);
            logger.info("\n\nkeystore was loaded");

            final Key key = keystore.getKey(keyAlias, SecurityUtils.ksPassword);

            if (key instanceof PrivateKey) {
                return (PrivateKey) key;
            } else {
                return null;
            }
        }
    }

    public static byte[] signData(final PrivateKey privateKey,
                                  final String rawDataStr,
                                  final Logger logger) {
        notNullAssert(privateKey, "privateKey");
        notNullAssert(rawDataStr, "rawDataStr");
        notNullAssert(logger, "logger");

        byte[] signedMsg = null;

        try {
            final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);

            signature.update(rawDataStr.getBytes(StandardCharsets.UTF_8));
            signedMsg = signature.sign();
        } catch (final Exception e) {
            logger.error("\n\nException: {}", e.getMessage());

            e.printStackTrace();
        } finally {
            return signedMsg;
        }
    }

    public static boolean verifyData(final PublicKey publicKey,
                                     final String dataStr,
                                     final byte[] signedMsg,
                                     final Logger logger) {
        notNullAssert(publicKey, "publicKey");
        notNullAssert(dataStr, "dataStr");
        notNullAssert(signedMsg, "signedMsg");
        notNullAssert(logger, "logger");

        boolean verified = false;

        try {
            final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

            signature.initVerify(publicKey);

            signature.update(dataStr.getBytes(StandardCharsets.UTF_8));

            verified = signature.verify(signedMsg);
        } catch (final Exception e) {
            logger.error("\n\nException: {}", e.getMessage());

            e.printStackTrace();
        } finally {
            return verified;
        }
    }

    public static String symmetricEncrypt(final String strToEncrypt, final String encryptionSecret) {
        notNullAssert(strToEncrypt, "strToEncrypt");
        notNullAssert(encryptionSecret, "encryptionSecret");

        try {
            setKey(encryptionSecret);

            final Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER_FULL_ALG_SPEC);
            cipher.init(Cipher.ENCRYPT_MODE, SecurityUtils.secretKey);

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (final Exception e) {
            logger.error("Error while encrypting: {} - stackTrace {}", e.toString(),
                    ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public static String symmetricDecrypt(final String strToDecrypt, final String encryptionSecret) throws Exception {
        logger.info("strToDecript {}, encryptionSecret {}", strToDecrypt, encryptionSecret);

        notNullAssert(strToDecrypt, "strToDecrypt");
        notNullAssert(encryptionSecret, "encryptionSecret");

        try {
            setKey(encryptionSecret);

            final Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER_FULL_ALG_SPEC);
            cipher.init(Cipher.DECRYPT_MODE, SecurityUtils.secretKey);

            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (final Exception e) {
            logger.error("Error while decrypting: {} - stackTrace {}", e.toString(),
                    ExceptionUtils.getStackTrace(e));

            throw e;
        }
    }

    private static void setKey(final String myKey) {
        MessageDigest sha;

        try {
            key = myKey.getBytes(CHARSET_ENCODING);
            sha = MessageDigest.getInstance(DIGEST_ALGORITHM);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, SYMMETRIC_CIPHER_BASE_ALG);
        } catch (final NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        } catch (final UnsupportedEncodingException uEE) {
            logger.error("Error while setting key: {} - stackTrace {}", uEE.getMessage(),
                    ExceptionUtils.getStackTrace(uEE));
        }
    }
}
