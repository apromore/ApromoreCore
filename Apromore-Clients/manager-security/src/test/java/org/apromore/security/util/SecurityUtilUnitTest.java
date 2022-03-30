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
package org.apromore.security.util;

import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.charset.UnsupportedCharsetException;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test suite for {@link SecurityUtil}.
 */
class SecurityUtilUnitTest {

    /**
     * Test the {@link SecurityUtil#hash} methods.
     */
    @ParameterizedTest
    @CsvSource({"passwordNaCl, UTF-8, MD5,     f25b019a9470318d44d60e1416631f34",
                "secret11,     UTF-8, MD5,      674c1c09d2f8de4bab7cd4fcb3640cb",
                "passwordNaCl, UTF-8, SHA-1,   40274892d2fe01a6ab1e0fbde5c22b8312d10780",
                "passwordKCl,  UTF-8, SHA-256, c473e966c0d32d5f985853a487b6977beb98dcc54b4a00cf1092c6f18a3d1324",
                "passwordNaCl, UTF-8, SHA-256,  28480971104b37691f41c430e59e07fd4c5ae0f53317b2aa2e06cf8ddbbfe10"})
    void testHash(final String cleartext, final String charset, final String algorithm, final String expectedHash)
        throws NoSuchAlgorithmException, UnsupportedCharsetException {

        assertEquals(expectedHash, SecurityUtil.hash(cleartext, algorithm, Charset.forName(charset)));

        if ("UTF-8".equals(charset)) {
            assertEquals(expectedHash, SecurityUtil.hash(cleartext, algorithm));
        }
    }
}
