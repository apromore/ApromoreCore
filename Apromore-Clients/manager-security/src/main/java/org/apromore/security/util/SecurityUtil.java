package org.apromore.security.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: cameron
 * Date: 20/11/2013
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityUtil {

    private SecurityUtil() {
        // No instances allowed.
    }

    public static String hashPassword(String password) {
        String hashword = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);
        } catch (NoSuchAlgorithmException nsae) {
            // ignore
        }
        return hashword;
    }
}
