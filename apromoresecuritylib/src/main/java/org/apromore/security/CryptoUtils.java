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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static org.apromore.security.util.AssertUtils.notNullAssert;

public final class CryptoUtils {

    private static final Logger logger = LoggerFactory.getLogger(CryptoUtils.class);

    private static final String KEYSTORE_FILE = "apSecurityTS.jks";

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

        if (StringUtils.isBlank(pwdKSStr)) {
            final String errMsg = "keystore password could not be attained from environment";

            logger.error("\n\n{}\n", errMsg);
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

        CryptoUtils.ksPassword = pwdKSStr.toCharArray();
        // For safety/clear
        pwdKSStr = null;
    }

    private CryptoUtils() {
        // Intentionally private constructor
    }

    public static final PublicKey getPublicKey(final String keyAlias) throws Exception {
        logger.debug("\nkeyAlias: {} ", keyAlias);

        try (
            final InputStream inputStream = new FileInputStream(secRessKeystoreFilePathStr);
        ) {
            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(inputStream, CryptoUtils.ksPassword);

            final Key key = keystore.getKey(keyAlias, CryptoUtils.ksPassword);

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

    public static PrivateKey getPrivateKey(final String keyAlias) throws Exception {
        logger.debug("\nkeyAlias: {} ", keyAlias);
        logger.debug("\nsecRessKeystoreFilePathStr: {} ", secRessKeystoreFilePathStr);

        try (
            final InputStream inputStream = new FileInputStream(secRessKeystoreFilePathStr);
        ) {
            logger.debug("\nFileInputStream was opened");

            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

            keystore.load(inputStream, CryptoUtils.ksPassword);
            logger.debug("\nkeystore was loaded");

            final Key key = keystore.getKey(keyAlias, CryptoUtils.ksPassword);

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
            cipher.init(Cipher.ENCRYPT_MODE, CryptoUtils.secretKey);

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (final Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }

        return null;
    }

    public static String symmetricDecrypt(final String strToDecrypt, final String encryptionSecret) throws Exception {
        notNullAssert(strToDecrypt, "strToDecrypt");
        notNullAssert(encryptionSecret, "encryptionSecret");

        try {
            setKey(encryptionSecret);

            final Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER_FULL_ALG_SPEC);
            cipher.init(Cipher.DECRYPT_MODE, CryptoUtils.secretKey);

            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (final Exception e) {
            System.out.println("Error while decrypting: " + e.toString());

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
        } catch (final UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
    }
}
