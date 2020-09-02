/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.plugin.portal.processdiscoverer.test;

import static org.junit.Assert.fail;
import org.apromore.plugin.portal.processdiscoverer.vis.StringFormatter;
import org.junit.Test;

public class StringFormatterTest {

    private final StringFormatter stringFormatter = new StringFormatter();

    @Test
    public void testShortenName() {
        try {
            if (!stringFormatter.shortenName("Great WhiteVeryLongStringWhiteVeryLongStringWhiteVeryLongStringWhiteVeryLongString Shark", 0)
                    // .equals("Great ... Shark")) {
                    .equals("Great...")) {
                fail("Shorten name is not correct");
            }
            if (!stringFormatter.shortenName("123456789012345678901234567890123456789012345678901234567890", 0)
                    .equals("123456789012345...")) {
                fail("Shorten name is not correct");
            }
            if (!stringFormatter.shortenName("12345678901234 1234567890 1234567890 1234567890 1234567890 1234567890", 0)
                    // .equals("12345678901234 ... 1234567890")) {
                    .equals("12345678901234 1234567890...")) {
                fail("Shorten name is not correct");
            }
            if (!stringFormatter.shortenName("1234567890 1234567890 1234567890 1234567890 1234567890", 0)
                    .equals("1234567890 1234567890 1234567890 1234567890 1234567890")) {
                fail("Shorten name is not correct");
            }
            if (!stringFormatter.shortenName("1234567890 1234567890 1234567890 1234567890", 30)
                    // .equals("1234567890 ... 1234567890")) {
                    .equals("1234567890 1234567890...")) {
                fail("Shorten name is not correct");
            }
            if (!stringFormatter.shortenName("Great White", 0).equals("Great White")) {
                fail("Shorten name is not correct");
            }
            if (!stringFormatter.shortenName("12345678901234567890 12345678", 0)
                    .equals("123456789012345...")) {
                fail("Shorten name is not correct");
            }
            if (!stringFormatter.shortenName("12345678 12345678901234567890", 0)
                    .equals("12345678...")) {
                fail("Shorten name is not correct");
            }
            if (!stringFormatter.shortenName("12345678901234567890", 0)
                    .equals("123456789012345...")) {
                fail("Shorten name is not correct");
            }
            System.out.println("Testing StringFormatter - shortenName()");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
