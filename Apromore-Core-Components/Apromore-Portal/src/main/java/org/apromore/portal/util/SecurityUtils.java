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
package org.apromore.portal.util;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Base64;

public final class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    private static final String KEYSTORE_FILE = "securityServ.jks";

    private static SecretKeySpec secretKey;
    private static byte[] key;

    private SecurityUtils() {
    }

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    // @2do: change to get from environment securely
    public static final String KS_AND_KEY_PASSWORD = "topSecret";

    private static final String SYMMETRIC_CIPHER_FULL_ALG_SPEC = "AES/ECB/PKCS5Padding";
    private static final String SYMMETRIC_CIPHER_BASE_ALG = "AES";

    private static final String DIGEST_ALGORITHM = "SHA1";
    private static final String CHARSET8_ENCODING = Charsets.UTF_8.toString();

    public static final PublicKey getPublicKey(final String keyAlias) throws Exception {
        final InputStream inputStream = SecurityUtils.class.getClassLoader().getResourceAsStream(KEYSTORE_FILE);

        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(inputStream, KS_AND_KEY_PASSWORD.toCharArray());

        final Key key = keystore.getKey(keyAlias, KS_AND_KEY_PASSWORD.toCharArray());

        if (key instanceof PrivateKey) {
            // Get certificate of public key
            final Certificate cert = keystore.getCertificate(keyAlias);
            final PublicKey publicKey = cert.getPublicKey();

            return publicKey;
        } else {
            return null;
        }
    }

    public static PrivateKey getPrivateKey(final String keyAlias) throws Exception {
        final InputStream inputStream =
                SecurityUtils.class.getClassLoader().getResourceAsStream("securityServ.jks");

        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(inputStream, KS_AND_KEY_PASSWORD.toCharArray());

        final Key key = keystore.getKey(keyAlias, KS_AND_KEY_PASSWORD.toCharArray());

        if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        } else {
            return null;
        }
    }

    public static byte[] signData(final PrivateKey privateKey,
                                  final String rawDataStr,
                                  final Logger logger) {
        Assert.notNull(privateKey, "'privateKey' must not be null");
        Assert.notNull(rawDataStr, "'rawDataStr' must not be null");
        Assert.notNull(logger, "'logger' must not be null");

        byte[] signedMsg = null;

        try {
            final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);

            signature.update(rawDataStr.getBytes(StandardCharsets.UTF_8));
            signedMsg = signature.sign();
        } catch (final Exception e) {
            logger.error("Exception in digital signing: {}", e.getMessage());

            e.printStackTrace();
        } finally {
            return signedMsg;
        }
    }

    public static boolean verifyData(final PublicKey publicKey,
                                     final String dataStr,
                                     final byte[] signedMsg,
                                     final Logger logger) {
        Assert.notNull(publicKey, "'publicKey' must not be null");
        Assert.notNull(dataStr, "'dataStr' must not be null");
        Assert.notNull(signedMsg, "'signedMsg' must not be null");
        Assert.notNull(logger, "'logger' must not be null");

        boolean verified = false;

        try {
            final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

            signature.initVerify(publicKey);

            signature.update(dataStr.getBytes(StandardCharsets.UTF_8));

            verified = signature.verify(signedMsg);
        } catch (final Exception e) {
            logger.error("Exception in signature verification: {}", e.getMessage());

            e.printStackTrace();
        } finally {
            return verified;
        }
    }

    public static String symmetricEncrypt(final String strToEncrypt, final String encryptionSecret) {
        Assert.notNull(strToEncrypt, "'strToEncrypt' must not be null");
        Assert.notNull(encryptionSecret, "'encryptionSecret' must not be null");

        try {
            setKey(encryptionSecret);

            final Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER_FULL_ALG_SPEC);
            cipher.init(Cipher.ENCRYPT_MODE, SecurityUtils.secretKey);

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (final Exception e) {
            logger.error("Error while encrypting: " + e.toString());

            e.printStackTrace();
        }

        return null;
    }

    public static String symmetricDecrypt(final String strToDecrypt, final String encryptionSecret) throws Exception {
        Assert.notNull(strToDecrypt, "'strToDecrypt' must not be null");
        Assert.notNull(encryptionSecret, "'encryptionSecret' must not be null");

        try {
            setKey(encryptionSecret);

            final Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER_FULL_ALG_SPEC);
            cipher.init(Cipher.DECRYPT_MODE, SecurityUtils.secretKey);

            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (final Exception e) {
            logger.error("Error while decrypting: " + e.toString());

            throw e;
        }
    }
	
    private static void setKey(final String myKey) {
        try {
            key = myKey.getBytes(CHARSET8_ENCODING);
            final MessageDigest sha = MessageDigest.getInstance(DIGEST_ALGORITHM);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, SYMMETRIC_CIPHER_BASE_ALG);
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
