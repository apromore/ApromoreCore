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
package org.apromore.commons.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DelimiterUnitTest {

    /**
     * This test driver tests the CleanDelimiter.
     */
    @Test
    public void testCleanDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        List<String> rows = new ArrayList<String>() {{
            add("col1,col2,col3");
            add("3,fgdfd,asdasdasasd");
            add(",,jh");
            add("546,kujgh,3");
        }};
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd;dsas,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add(",,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd");
        assertNotEquals(",", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the CommaDelimiter.
     */
    @Test
    public void testCommaDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        List<String> rows = new ArrayList<String>() {{
            add("col1,col2,col3");
            add("3,fgdfd,asdasda;sasd");
            add(",,j:h");
            add("546,kujgh,3");
        }};
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd;dsas,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add(",,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd");
        assertNotEquals(",", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the SemiColonDelimiter.
     */
    @Test
    public void testSemiColonDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        List<String> rows = new ArrayList<String>() {{
            add("col1;col2;col3");
            add("3;fgdfd,asdasda;sasd");
            add(";;j:h");
            add("546;kujgh;ss");
        }};
        assertEquals(";", Delimiter.findDelimiter(rows));
        rows.add("dffd;fd;");
        assertEquals(";", Delimiter.findDelimiter(rows));
        rows.add("dffd;fd;dsas,");
        assertEquals(";", Delimiter.findDelimiter(rows));
        rows.add(";;");
        assertEquals(";", Delimiter.findDelimiter(rows));
        rows.add("dffd;fd");
        assertNotEquals(";", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the ColonDelimiter.
     */
    @Test
    public void testColonDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        List<String> rows = new ArrayList<String>() {{
            add("col1:col2:col3");
            add("3:fgdfd,asdasda:sasd");
            add("::j;h");
            add("546:kujgh:ss");
        }};
        assertEquals(":", Delimiter.findDelimiter(rows));
        rows.add("dffd:fd:");
        assertEquals(":", Delimiter.findDelimiter(rows));
        rows.add("dffd:fd:dsas,");
        assertEquals(":", Delimiter.findDelimiter(rows));
        rows.add("::");
        assertEquals(":", Delimiter.findDelimiter(rows));
        rows.add("dffd:fd");
        assertNotEquals(":", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the TabDelimiter.
     */
    @Test
    public void testTabDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        List<String> rows = new ArrayList<String>() {{
            add("col1\tcol2\tcol3");
            add("3\tfgdfd,asdasda\tsasd");
            add("\t\tj;h");
            add("546\tkujgh\tss");
        }};
        assertEquals("\t", Delimiter.findDelimiter(rows));
        rows.add("dffd\tfd\t");
        assertEquals("\t", Delimiter.findDelimiter(rows));
        rows.add("dffd\tfd\tdsas,");
        assertEquals("\t", Delimiter.findDelimiter(rows));
        rows.add("\t\t");
        assertEquals("\t", Delimiter.findDelimiter(rows));
        rows.add("dffd\tfd");
        assertNotEquals("\t", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the SpaceDelimiter.
     */
    @Test
    public void testSpaceDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        List<String> rows = new ArrayList<String>() {{
            add("col1 col2 col3");
            add("3 fgdfd,asdasda sasd");
            add("  j;h");
            add("546 kujgh ss");
        }};
        assertEquals(" ", Delimiter.findDelimiter(rows));
        rows.add("dffd fd ");
        assertEquals(" ", Delimiter.findDelimiter(rows));
        rows.add("dffd fd dsas,");
        assertEquals(" ", Delimiter.findDelimiter(rows));
        rows.add("  ");
        assertEquals(" ", Delimiter.findDelimiter(rows));
        rows.add("dffd fd");
        assertNotEquals(" ", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the PipeDelimiter.
     */
    @Test
    public void testPipeDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        List<String> rows = new ArrayList<String>() {{
            add("col1|col2|col3");
            add("3|fgdfd,asdasda|sasd");
            add("|j|h");
            add("546|kujgh|ss");
        }};
        assertEquals("\\|", Delimiter.findDelimiter(rows));
        rows.add("dffd|fd|");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
        rows.add("dffd|fd|dsas,");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
        rows.add("||");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
        rows.add("dffd|fd");
        assertNotEquals("\\|", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the InvalidInputs.
     */
    @Test
    public void testInvalidInputs() {
        List<String> rows = new ArrayList<>();
        assertEquals("!", Delimiter.findDelimiter(rows));
        assertEquals("!", Delimiter.findDelimiter(null));
        rows.add("aaa|bbb|cc");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the RealInput.
     */
    @Test
    public void testRealInput() {
        List<String> rows = new ArrayList<String>() {{
            add("OID|PID|userID|time_stamp|quantity|payment_type|delivery_mode");
            add("1|316717|5276|07/08/2010 10:33|10|paypal|normal");
            add("2|359728|2144|07/08/2010 10:33|74|cash|pickup");
            add("3|404571|4531|07/08/2010 10:33|12|mastercard|pickup");
            add("4|34122|1598|07/08/2010 10:33|43|mastercard|first_class");
            add("5|577352|2614|07/08/2010 10:33|58|visa|second_class");
            add("6|457481|3030|07/08/2010 10:33|34|mastercard|pickup");
            add("7|618694|6635|07/08/2010 10:33|70|paypal|pickup");
            add("8|896132|2828|07/08/2010 10:33|59|mastercard|second_class");
            add("9|732581|7228|07/08/2010 10:33|21|paypal|second_class");
        }};
        assertEquals("\\|", Delimiter.findDelimiter(rows));
    }
}
