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
package org.apromore.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


class DelimiterUnitTest {

    private List<String> rows;

    /**
     * Setup for tests.
     */
    @BeforeEach
    void setup() {
        rows = new ArrayList<>();
    }

    /**
     * This test driver tests the CleanDelimiter.
     */
    @Test
    void testCleanDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        rows.add("col1,col2,col3");
        rows.add("3,fgdfd,asdasdasasd");
        rows.add(",,jh");
        rows.add("546,kujgh,3");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd;dsas,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add(",,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd");
        assertEquals(",", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the CommaDelimiter.
     */
    @Test
    void testCommaDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        rows.add("col1,col2,col3");
        rows.add("3,fgdfd,asdasda;sasd");
        rows.add(",,j:h");
        rows.add("546,kujgh,3");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd;dsas,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add(",,");
        assertEquals(",", Delimiter.findDelimiter(rows));
        rows.add("dffd,fd");
        assertEquals(",", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the SemiColonDelimiter.
     */
    @Test
    void testSemiColonDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        rows.add("col1;col2;col3");
        rows.add("3;fgdfd,asdasda;sasd");
        rows.add(";;j:h");
        rows.add("546;kujgh;ss");
        assertEquals(";", Delimiter.findDelimiter(rows));
        rows.add("dffd;fd;");
        assertEquals(";", Delimiter.findDelimiter(rows));
        rows.add("dffd;fd;dsas,");
        assertEquals(";", Delimiter.findDelimiter(rows));
        rows.add(";;");
        assertEquals(";", Delimiter.findDelimiter(rows));
        rows.add("dffd;fd");
        assertEquals(";", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the ColonDelimiter.
     */
    @Test
    void testColonDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        rows.add("col1:col2:col3");
        rows.add("3:fgdfd,asdasda:sasd");
        rows.add("::j;h");
        rows.add("546:kujgh:ss");
        assertEquals(":", Delimiter.findDelimiter(rows));
        rows.add("dffd:fd:");
        assertEquals(":", Delimiter.findDelimiter(rows));
        rows.add("dffd:fd:dsas,");
        assertEquals(":", Delimiter.findDelimiter(rows));
        rows.add("::");
        assertEquals(":", Delimiter.findDelimiter(rows));
        rows.add("dffd:fd");
        assertEquals(":", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the TabDelimiter.
     */
    @Test
    void testTabDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        rows.add("col1\tcol2\tcol3");
        rows.add("3\tfgdfd,asdasda\tsasd");
        rows.add("\t\tj;h");
        rows.add("546\tkujgh\tss");
        assertEquals("\t", Delimiter.findDelimiter(rows));
        rows.add("dffd\tfd\t");
        assertEquals("\t", Delimiter.findDelimiter(rows));
        rows.add("dffd\tfd\tdsas,");
        assertEquals("\t", Delimiter.findDelimiter(rows));
        rows.add("\t\t");
        assertEquals("\t", Delimiter.findDelimiter(rows));
        rows.add("dffd\tfd");
        assertEquals("\t", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the SpaceDelimiter.
     */
    @Test
    @Disabled
    void testSpaceDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        rows.add("col1 col2 col3");
        rows.add("3 fgdfd,asdasda sasd");
        rows.add("  j;h");
        rows.add("546 kujgh ss");
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
    void testPipeDelimiter() {
        // Create a 1D list Strings [header, r1, r2, r3]
        rows.add("col1|col2|col3");
        rows.add("3|fgdfd,asdasda|sasd");
        rows.add("|j|h");
        rows.add("546|kujgh|ss");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
        rows.add("dffd|fd|");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
        rows.add("dffd|fd|dsas,");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
        rows.add("||");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
        rows.add("dffd|fd");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver tests the InvalidInputs.
     */
    @Test
    void testInvalidInputs() {
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
    void testRealInput() {
        rows.add("OID|PID|userID|time_stamp|quantity|payment_type|delivery_mode");
        rows.add("1|316717|5276|07/08/2010 10:33|10|paypal|normal");
        rows.add("2|359728|2144|07/08/2010 10:33|74|cash|pickup");
        rows.add("3|404571|4531|07/08/2010 10:33|12|mastercard|pickup");
        rows.add("4|34122|1598|07/08/2010 10:33|43|mastercard|first_class");
        rows.add("5|577352|2614|07/08/2010 10:33|58|visa|second_class");
        rows.add("6|457481|3030|07/08/2010 10:33|34|mastercard|pickup");
        rows.add("7|618694|6635|07/08/2010 10:33|70|paypal|pickup");
        rows.add("8|896132|2828|07/08/2010 10:33|59|mastercard|second_class");
        rows.add("9|732581|7228|07/08/2010 10:33|21|paypal|second_class");
        assertEquals("\\|", Delimiter.findDelimiter(rows));
    }

    /**
     * This test driver duplicate testPrepareXesModel_test7_record_invalid() in LogImporterCSVImplUnitTest.
     */
    @Test
    void testInvalidCSV() {
        rows.add("case id,activity,start date,completion time, process type");
        rows.add("case2,activity1,2019-09-23T15:13:05.071,2019-09-23T15:13:05.132,1,hi,extra");
        rows.add("case1,activity1,2019-09-23T15:13:05.114,2019-09-23T15:13:05.132,1");
        rows.add("case2,activity2,not a timestamp,2019-09-23T15:13:05.133,1");
        assertEquals(",", Delimiter.findDelimiter(rows));
    }
}
