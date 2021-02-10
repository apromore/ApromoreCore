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
package org.apromore.plugin.portal.processdiscoverer.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StringFormatterTest {

    private final StringFormatter stringFormatter = new StringFormatter();

    @Test
    public void testShortenName() throws Exception {
        assertEquals("Great ... Shark", stringFormatter.shortenName("Great WhiteVeryLongStringWhiteVeryLongStringWhiteVeryLongStringWhiteVeryLongString Shark", 0));
        assertEquals("123456789012345...", stringFormatter.shortenName("123456789012345678901234567890123456789012345678901234567890", 0));
        assertEquals("12345678901234 ... 1234567890", stringFormatter.shortenName("12345678901234 1234567890 1234567890 1234567890 1234567890 1234567890", 0));
        assertEquals("1234567890 1234567890 1234567890 1234567890 1234567890", stringFormatter.shortenName("1234567890 1234567890 1234567890 1234567890 1234567890", 0));
        assertEquals("1234567890 ... 1234567890", stringFormatter.shortenName("1234567890 1234567890 1234567890 1234567890", 30));
        assertEquals("Great White", stringFormatter.shortenName("Great White", 0));
        assertEquals("123456789012345...", stringFormatter.shortenName("12345678901234567890 12345678", 0));
        assertEquals("12345678...", stringFormatter.shortenName("12345678 12345678901234567890", 0));
        assertEquals("123456789012345...", stringFormatter.shortenName("12345678901234567890", 0));
        assertEquals("*", stringFormatter.shortenName(null, 0));
        assertEquals("*", stringFormatter.shortenName("", 0));
        assertEquals("_", stringFormatter.shortenName("_", 0));
        assertEquals("-", stringFormatter.shortenName("-", 0));
        assertEquals("RA", stringFormatter.shortenName("RA", 0));
    }
}
