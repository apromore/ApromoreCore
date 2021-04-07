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

import org.apromore.security.util.DateTimeUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class TokenUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtilsTest.class);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void createToken_nullUsernameParameters_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'username' must not be empty");

        final String timestamp = DateTimeUtils.dateTimeStampNoSpaces();
        final String token =
                TokenUtils.createToken(null, "micg@outlook.com", timestamp);
    }

    @Test
    public void createToken_blankUsernameParameters_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'username' must not be empty");

        final String timestamp = DateTimeUtils.dateTimeStampNoSpaces();
        final String token =
                TokenUtils.createToken("", "micg@outlook.com", timestamp);
    }

    @Test
    public void createToken_nullEmailAddressParameters_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'emailAddress' must not be empty");

        final String timestamp = DateTimeUtils.dateTimeStampNoSpaces();
        final String token =
                TokenUtils.createToken("micg_outlook_com", null, timestamp);
    }

    @Test
    public void createToken_blankEmailAddressParameters_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'emailAddress' must not be empty");

        final String timestamp = DateTimeUtils.dateTimeStampNoSpaces();
        final String token =
                TokenUtils.createToken("micg_outlook_com", "", timestamp);
    }

    @Test
    public void createToken_nullTimestampParameters_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'timestamp' must not be empty");

        final String timestamp = DateTimeUtils.dateTimeStampNoSpaces();
        final String token =
                TokenUtils.createToken("micg_outlook_com", "micg@outlook.com", null);
    }

    @Test
    public void createToken_blankTimestampParameters_throwsIllegalArgumentException() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'timestamp' must not be empty");

        final String timestamp = DateTimeUtils.dateTimeStampNoSpaces();
        final String token =
                TokenUtils.createToken("micg_outlook_com", "micg@outlook.com", "");
    }

    @Test
    public void createToken_allSuppliedParameters_throwsIllegalArgumentException() {
        final String timestamp = DateTimeUtils.dateTimeStampNoSpaces();
        final String token =
                TokenUtils.createToken("micg_outlook_com", "micg@outlook.com", timestamp);

        Assert.assertEquals("username=micg_outlook_com;email=micg@outlook.com;timestamp=" + timestamp, token);
    }

    @Test
    public void testingEncodedUrlParameter() throws Exception {
        final String testingEncodedParameter =
                "SmysSHgJACt4mJTtAM3WckeR%2BYl0Zssyvk7he4i9GEMMOeVF3jceW2o9qKkKmRMyPUlo%2Fe%2FKQ21IfD7i6SuulUm3V3XeY2f8RcTIk3RDpM4XVpe%2FO5LPSlbkuCalf1XJAZvuj8PWj%2FFPeEVYJZzuEA%3D%3D";

        final String urlDecoded = URLDecoder.decode(testingEncodedParameter, StandardCharsets.UTF_8.toString());
        final String decryptedUrlParam = CryptoUtils.symmetricDecrypt(urlDecoded, "changeThisTopSecret");
        logger.info("\ndecryptedUrlParam: {}", decryptedUrlParam);
        Assert.assertEquals(
                "username=mic.giansiracusa_outlook.com;email=mic.giansiracusa@outlook.com;timestamp=20210119_101957",
                decryptedUrlParam);
    }
}
