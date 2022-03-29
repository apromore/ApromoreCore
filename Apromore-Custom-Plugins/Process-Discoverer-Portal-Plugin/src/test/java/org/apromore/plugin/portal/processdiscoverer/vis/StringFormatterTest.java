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
package org.apromore.plugin.portal.processdiscoverer.vis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class StringFormatterTest {

    private final StringFormatter stringFormatter = new StringFormatter();

    @Test
    void testShortenName() throws Exception {
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

    @Test
    void testWrapName() {
        assertEquals("Great\\nWhiteVeryLongString\\nWhiteVeryLongStr...", stringFormatter.wrapName("Great WhiteVeryLongStringWhiteVeryLongStringWhiteVeryLongStringWhiteVeryLongString Shark", 0));
        assertEquals("1234567890123456789\\n0123456789012345678\\n9012345678901234...", stringFormatter.wrapName("123456789012345678901234567890123456789012345678901234567890", 0));
        assertEquals("12345678901234\\n1234567890\\n1234567890 12345...", stringFormatter.wrapName("12345678901234 1234567890 1234567890 1234567890 1234567890 1234567890", 0));
        assertEquals("1234567890\\n1234567890\\n1234567890 12345...", stringFormatter.wrapName("1234567890 1234567890 1234567890 1234567890 1234567890", 0));
        assertEquals("1234567890\\n1234567890\\n1234567890\\n1234567890 12345...", stringFormatter.wrapName("1234567890 1234567890 1234567890 1234567890 12345678900", 4));
        assertEquals("Great White", stringFormatter.wrapName("Great White", 0));
        assertEquals("1234567890123456789\\n0 12345678", stringFormatter.wrapName("12345678901234567890 12345678", 0));
        assertEquals("12345678\\n1234567890123456789\\n0", stringFormatter.wrapName("12345678 12345678901234567890", 0));
        assertEquals("1234567890123456789\\n0", stringFormatter.wrapName("12345678901234567890", 0));
        assertEquals("一二三四五六七八九\\n十０１２３４５６７\\n８９０", stringFormatter.wrapName("一二三四五六七八九十０１２３４５６７８９０", 0));
        assertEquals("123456789\\n0一二三四五六七八\\n九十０１２３...", stringFormatter.wrapName("1234567890一二三四五六七八九十０１２３４５６７８９０", 0));
        assertEquals("あいうえおかきくけ\\nこたちつてと", stringFormatter.wrapName("あいうえおかきくけこたちつてと", 0));
        assertEquals("アイウエオカキクケ\\nコ", stringFormatter.wrapName("アイウエオカキクケコ", 0));
        assertEquals("ｱｲｳｴｵｶｷｸｹｺ plus\\nother half width\\ncharacters", stringFormatter.wrapName("ｱｲｳｴｵｶｷｸｹｺ plus other half-width characters", 0));
        assertEquals("*", stringFormatter.wrapName(null, 0));
        assertEquals("*", stringFormatter.wrapName("", 0));
        assertEquals("_", stringFormatter.wrapName("_", 0));
        assertEquals("-", stringFormatter.wrapName("-", 0));
        assertEquals("RA", stringFormatter.wrapName("RA", 0));
    }
}
