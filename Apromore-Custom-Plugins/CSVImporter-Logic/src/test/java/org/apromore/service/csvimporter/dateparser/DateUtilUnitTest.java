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
package org.apromore.service.csvimporter.dateparser;

import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.apromore.service.csvimporter.services.MetaDataService;
import org.apromore.service.csvimporter.services.legacy.LogImporter;
import org.apromore.service.csvimporter.services.legacy.LogImporterCSVImpl;
import org.apromore.service.csvimporter.services.utilities.TestUtilities;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.dateparser.DateUtil.determineDateFormat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class DateUtilUnitTest {
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", "process type");
    private TestUtilities utilities;
    private ParquetFactoryProvider parquetFactoryProvider;
    private MetaDataService metaDataService;
    private LogImporter logImporter;

    @Before
    public void init() {
        utilities = new TestUtilities();
        parquetFactoryProvider = new ParquetFactoryProvider();
        metaDataService = parquetFactoryProvider
                .getParquetFactory("csv")
                .getMetaDataService();
        logImporter = new LogImporterCSVImpl();
    }

    @Test
    public void test_timestamp_date() {

        System.out.println("\n************************************\ntest - Test timestamp format - date only");

        //12/19/2019 15:13:05
        assertEquals("yyyyMMdd", determineDateFormat("20191219"));
        assertEquals("yyyyddMM", determineDateFormat("20191912"));
        assertEquals("ddMMyyyy", determineDateFormat("19122019"));
        assertEquals("MMddyyyy", determineDateFormat("12192019"));
        assertEquals("dd-MM-yyyy", determineDateFormat("19-12-2019"));
        assertEquals("MM-dd-yyyy", determineDateFormat("12-19-2019"));
        assertEquals("yyyy-MM-dd", determineDateFormat("2019-12-19"));
        assertEquals("yyyy-dd-MM", determineDateFormat("2019-19-12"));
        assertEquals("dd/MM/yyyy", determineDateFormat("19/12/2019"));
        assertEquals("MM/dd/yyyy", determineDateFormat("12/19/2019"));
        assertEquals("yyyy/MM/dd", determineDateFormat("2019/12/19"));
        assertEquals("yyyy/dd/MM", determineDateFormat("2019/19/12"));
        assertEquals("dd MM yyyy", determineDateFormat("19 12 2019"));
        assertEquals("MM dd yyyy", determineDateFormat("12 19 2019"));
        assertEquals("yyyy MM dd", determineDateFormat("2019 12 19"));
        assertEquals("yyyy dd MM", determineDateFormat("2019 19 12"));
        assertEquals("dd MMM yyyy", determineDateFormat("19 Dec 2019"));
        assertEquals("dd MMMM yyyy", determineDateFormat("19 DECEMBER 2019"));
        assertNull("10 dd or MM is not valid timestamp", determineDateFormat("10"));
        assertNull("10/12 dd/MM is not valid timestamp", determineDateFormat("10/12"));
        assertNull("2019 yyyy is not valid timestamp", determineDateFormat("2019"));
        assertNull("Friday WWW is not valid timestamp", determineDateFormat("Friday"));
        assertNull("12:20 HH:mm is not valid timestamp", determineDateFormat("12:20"));
        assertNull("1220 HHmm is not valid timestamp", determineDateFormat("1220"));
        assertNull("13:20:30 HH:mm:ss is not valid timestamp", determineDateFormat("13:20:30"));
    }

    @Test
    public void test_timestamp_date_HH_MM() {

        System.out.println("\n************************************\ntest - Test timestamp format - date, hours and minutes");

        assertEquals("yyyyMMddHHmm", determineDateFormat("201912191513"));
        assertEquals("yyyyMMdd HHmm", determineDateFormat("20191219 1513"));
        assertEquals("dd-MM-yyyy HH:mm", determineDateFormat("19-12-2019 15:13"));
        assertEquals("MM-dd-yyyy HH:mm", determineDateFormat("12-19-2019 15:13"));
        assertEquals("yyyy-MM-dd HH:mm", determineDateFormat("2019-12-19 15:13"));
        assertEquals("yyyy-dd-MM HH:mm", determineDateFormat("2019-19-12 15:13"));
        assertEquals("dd-MM-yyyy'T'HH:mm", determineDateFormat("19-12-2019T15:13"));
        assertEquals("MM-dd-yyyy'T'HH:mm", determineDateFormat("12-19-2019T15:13"));
        assertEquals("yyyy-MM-dd'T'HH:mm", determineDateFormat("2019-12-19T15:13"));
        assertEquals("yyyy-dd-MM'T'HH:mm", determineDateFormat("2019-19-12T15:13"));
        assertEquals("dd/MM/yyyy HH:mm", determineDateFormat("19/12/2019 15:13"));
        assertEquals("MM/dd/yyyy HH:mm", determineDateFormat("12/19/2019 15:13"));
        assertEquals("yyyy/MM/dd HH:mm", determineDateFormat("2019/12/19 15:13"));
        assertEquals("yyyy/dd/MM HH:mm", determineDateFormat("2019/19/12 15:13"));
        assertEquals("dd MM yyyy HH:mm", determineDateFormat("19 12 2019 15:13"));
        assertEquals("MM dd yyyy HH:mm", determineDateFormat("12 19 2019 15:13"));
        assertEquals("yyyy MM dd HH:mm", determineDateFormat("2019 12 19 15:13"));
        assertEquals("yyyy dd MM HH:mm", determineDateFormat("2019 19 12 15:13"));
        assertEquals("dd MMM yyyy HH:mm", determineDateFormat("19 DEC 2019 15:13"));
        assertEquals("dd MMMM yyyy HH:mm", determineDateFormat("19 DECEMBER 2019 15:13"));
    }

    @Test
    public void test_timestamp_date_HH_MM_AM_PM() {

        System.out.println("\n************************************\ntest - Test timestamp format - date, hours and minutes AM/PM");

        assertEquals("dd-MM-yyyy HH:mm a", determineDateFormat("19-12-2019 08:13 AM"));
        assertEquals("MM-dd-yyyy HH:mm a", determineDateFormat("12-19-2019 08:13 AM"));
        assertEquals("yyyy-MM-dd HH:mm a", determineDateFormat("2019-12-19 08:13 AM"));
        assertEquals("yyyy-dd-MM HH:mm a", determineDateFormat("2019-19-12 08:13 AM"));
        assertEquals("dd-MM-yyyy'T'HH:mm a", determineDateFormat("19-12-2019T08:13 AM"));
        assertEquals("MM-dd-yyyy'T'HH:mm a", determineDateFormat("12-19-2019T08:13 AM"));
        assertEquals("yyyy-MM-dd'T'HH:mm a", determineDateFormat("2019-12-19T08:13 AM"));
        assertEquals("yyyy-dd-MM'T'HH:mm a", determineDateFormat("2019-19-12T08:13 AM"));
        assertEquals("dd/MM/yyyy HH:mm a", determineDateFormat("19/12/2019 08:13 AM"));
        assertEquals("MM/dd/yyyy HH:mm a", determineDateFormat("12/19/2019 08:13 AM"));
        assertEquals("yyyy/MM/dd HH:mm a", determineDateFormat("2019/12/19 08:13 AM"));
        assertEquals("yyyy/dd/MM HH:mm a", determineDateFormat("2019/19/12 08:13 AM"));
        assertEquals("dd MM yyyy HH:mm a", determineDateFormat("19 12 2019 08:13 AM"));
        assertEquals("MM dd yyyy HH:mm a", determineDateFormat("12 19 2019 08:13 AM"));
        assertEquals("yyyy MM dd HH:mm a", determineDateFormat("2019 12 19 08:13 AM"));
        assertEquals("yyyy dd MM HH:mm a", determineDateFormat("2019 19 12 08:13 AM"));
        assertEquals("dd MMM yyyy HH:mm a", determineDateFormat("19 DEC 2019 08:13 AM"));
        assertEquals("dd MMMM yyyy HH:mm a", determineDateFormat("19 DECEMBER 2019 08:13 AM"));
        assertEquals("dd-MM-yyyy HH:mma", determineDateFormat("19-12-2019 08:13AM"));
        assertEquals("MM-dd-yyyy HH:mma", determineDateFormat("12-19-2019 08:13AM"));
        assertEquals("yyyy-MM-dd HH:mma", determineDateFormat("2019-12-19 08:13AM"));
        assertEquals("yyyy-dd-MM HH:mma", determineDateFormat("2019-19-12 08:13AM"));
        assertEquals("dd-MM-yyyy'T'HH:mma", determineDateFormat("19-12-2019T08:13AM"));
        assertEquals("MM-dd-yyyy'T'HH:mma", determineDateFormat("12-19-2019T08:13AM"));
        assertEquals("yyyy-MM-dd'T'HH:mma", determineDateFormat("2019-12-19T08:13AM"));
        assertEquals("yyyy-dd-MM'T'HH:mma", determineDateFormat("2019-19-12T08:13AM"));
        assertEquals("dd/MM/yyyy HH:mma", determineDateFormat("19/12/2019 08:13AM"));
        assertEquals("MM/dd/yyyy HH:mma", determineDateFormat("12/19/2019 08:13AM"));
        assertEquals("yyyy/MM/dd HH:mma", determineDateFormat("2019/12/19 08:13AM"));
        assertEquals("yyyy/dd/MM HH:mma", determineDateFormat("2019/19/12 08:13AM"));
        assertEquals("dd MM yyyy HH:mma", determineDateFormat("19 12 2019 08:13AM"));
        assertEquals("MM dd yyyy HH:mma", determineDateFormat("12 19 2019 08:13AM"));
        assertEquals("yyyy MM dd HH:mma", determineDateFormat("2019 12 19 08:13AM"));
        assertEquals("yyyy dd MM HH:mma", determineDateFormat("2019 19 12 08:13AM"));
        assertEquals("dd MMM yyyy HH:mma", determineDateFormat("19 DEC 2019 08:13AM"));
        assertEquals("dd MMMM yyyy HH:mma", determineDateFormat("19 DECEMBER 2019 08:13AM"));
    }

    @Test
    public void test_timestamp_date_HH_MM_ss() {

        System.out.println("\n************************************\ntest - Test timestamp format - date, hours, minutes and seconds");

        assertEquals("yyyyMMddHHmmss", determineDateFormat("20191219151305"));
        assertEquals("yyyyMMdd HHmmss", determineDateFormat("20191219 151305"));
        assertEquals("dd-MM-yyyy HH:mm:ss", determineDateFormat("19-12-2019 15:13:05"));
        assertEquals("MM-dd-yyyy HH:mm:ss", determineDateFormat("12-19-2019 15:13:05"));
        assertEquals("yyyy-MM-dd HH:mm:ss", determineDateFormat("2019-12-19 15:13:05"));
        assertEquals("yyyy-dd-MM HH:mm:ss", determineDateFormat("2019-19-12 15:13:05"));
        assertEquals("dd-MM-yyyy'T'HH:mm:ss", determineDateFormat("19-12-2019T15:13:05"));
        assertEquals("MM-dd-yyyy'T'HH:mm:ss", determineDateFormat("12-19-2019T15:13:05"));
        assertEquals("yyyy-MM-dd'T'HH:mm:ss", determineDateFormat("2019-12-19T15:13:05"));
        assertEquals("yyyy-dd-MM'T'HH:mm:ss", determineDateFormat("2019-19-12T15:13:05"));
        assertEquals("dd/MM/yyyy HH:mm:ss", determineDateFormat("19/12/2019 15:13:05"));
        assertEquals("MM/dd/yyyy HH:mm:ss", determineDateFormat("12/19/2019 15:13:05"));
        assertEquals("yyyy/MM/dd HH:mm:ss", determineDateFormat("2019/12/19 15:13:05"));
        assertEquals("yyyy/dd/MM HH:mm:ss", determineDateFormat("2019/19/12 15:13:05"));
        assertEquals("dd MM yyyy HH:mm:ss", determineDateFormat("19 12 2019 15:13:05"));
        assertEquals("MM dd yyyy HH:mm:ss", determineDateFormat("12 19 2019 15:13:05"));
        assertEquals("yyyy MM dd HH:mm:ss", determineDateFormat("2019 12 19 15:13:05"));
        assertEquals("yyyy dd MM HH:mm:ss", determineDateFormat("2019 19 12 15:13:05"));
        assertEquals("dd MMM yyyy HH:mm:ss", determineDateFormat("19 DEC 2019 15:13:05"));
        assertEquals("dd MMMM yyyy HH:mm:ss", determineDateFormat("19 DECEMBER 2019 15:13:05"));
    }

    @Test
    public void test_timestamp_date_HH_MM_ss_am_pm() {

        System.out.println("\n************************************\ntest - Test timestamp format - date, hours, minutes and seconds AM/PM");

        assertEquals("dd-MM-yyyy HH:mm:ss a", determineDateFormat("19-12-2019 08:13:05 pm"));
        assertEquals("MM-dd-yyyy HH:mm:ss a", determineDateFormat("12-19-2019 08:13:05 pm"));
        assertEquals("yyyy-MM-dd HH:mm:ss a", determineDateFormat("2019-12-19 08:13:05 pm"));
        assertEquals("yyyy-dd-MM HH:mm:ss a", determineDateFormat("2019-19-12 08:13:05 pm"));
        assertEquals("dd-MM-yyyy'T'HH:mm:ss a", determineDateFormat("19-12-2019T08:13:05 pm"));
        assertEquals("MM-dd-yyyy'T'HH:mm:ss a", determineDateFormat("12-19-2019T08:13:05 pm"));
        assertEquals("yyyy-MM-dd'T'HH:mm:ss a", determineDateFormat("2019-12-19T08:13:05 pm"));
        assertEquals("yyyy-dd-MM'T'HH:mm:ss a", determineDateFormat("2019-19-12T08:13:05 pm"));
        assertEquals("dd/MM/yyyy HH:mm:ss a", determineDateFormat("19/12/2019 08:13:05 pm"));
        assertEquals("MM/dd/yyyy HH:mm:ss a", determineDateFormat("12/19/2019 08:13:05 pm"));
        assertEquals("yyyy/MM/dd HH:mm:ss a", determineDateFormat("2019/12/19 08:13:05 pm"));
        assertEquals("yyyy/dd/MM HH:mm:ss a", determineDateFormat("2019/19/12 08:13:05 pm"));
        assertEquals("dd MM yyyy HH:mm:ss a", determineDateFormat("19 12 2019 08:13:05 pm"));
        assertEquals("MM dd yyyy HH:mm:ss a", determineDateFormat("12 19 2019 08:13:05 pm"));
        assertEquals("yyyy MM dd HH:mm:ss a", determineDateFormat("2019 12 19 08:13:05 pm"));
        assertEquals("yyyy dd MM HH:mm:ss a", determineDateFormat("2019 19 12 08:13:05 pm"));
        assertEquals("dd MMM yyyy HH:mm:ss a", determineDateFormat("19 DEC 2019 08:13:05 pm"));
        assertEquals("dd MMMM yyyy HH:mm:ss a", determineDateFormat("19 DECEMBER 2019 08:13:05 pm"));
        assertEquals("dd-MM-yyyy HH:mm:ssa", determineDateFormat("19-12-2019 08:13:05pm"));
        assertEquals("MM-dd-yyyy HH:mm:ssa", determineDateFormat("12-19-2019 08:13:05pm"));
        assertEquals("yyyy-MM-dd HH:mm:ssa", determineDateFormat("2019-12-19 08:13:05pm"));
        assertEquals("yyyy-dd-MM HH:mm:ssa", determineDateFormat("2019-19-12 08:13:05pm"));
        assertEquals("dd-MM-yyyy'T'HH:mm:ssa", determineDateFormat("19-12-2019T08:13:05pm"));
        assertEquals("MM-dd-yyyy'T'HH:mm:ssa", determineDateFormat("12-19-2019T08:13:05pm"));
        assertEquals("yyyy-MM-dd'T'HH:mm:ssa", determineDateFormat("2019-12-19T08:13:05pm"));
        assertEquals("yyyy-dd-MM'T'HH:mm:ssa", determineDateFormat("2019-19-12T08:13:05pm"));
        assertEquals("dd/MM/yyyy HH:mm:ssa", determineDateFormat("19/12/2019 08:13:05pm"));
        assertEquals("MM/dd/yyyy HH:mm:ssa", determineDateFormat("12/19/2019 08:13:05pm"));
        assertEquals("yyyy/MM/dd HH:mm:ssa", determineDateFormat("2019/12/19 08:13:05pm"));
        assertEquals("yyyy/dd/MM HH:mm:ssa", determineDateFormat("2019/19/12 08:13:05pm"));
        assertEquals("dd MM yyyy HH:mm:ssa", determineDateFormat("19 12 2019 08:13:05pm"));
        assertEquals("MM dd yyyy HH:mm:ssa", determineDateFormat("12 19 2019 08:13:05pm"));
        assertEquals("yyyy MM dd HH:mm:ssa", determineDateFormat("2019 12 19 08:13:05pm"));
        assertEquals("yyyy dd MM HH:mm:ssa", determineDateFormat("2019 19 12 08:13:05pm"));
        assertEquals("dd MMM yyyy HH:mm:ssa", determineDateFormat("19 DEC 2019 08:13:05pm"));
        assertEquals("dd MMMM yyyy HH:mm:ssa", determineDateFormat("19 DECEMBER 2019 08:13:05pm"));
    }

    @Test
    public void test_timestamp_date_HH_MM_ss_millisecond() {

        System.out.println("\n************************************\ntest - Test timestamp format - date, hours, minutes, seconds and millisecond");

        assertEquals("yyyyMMddHHmmssSSS", determineDateFormat("20191219151305328"));
        assertEquals("yyyyMMdd HHmmssSSS", determineDateFormat("20191219 151305328"));
        assertEquals("dd-MM-yyyy HH:mm:ss.SSS", determineDateFormat("19-12-2019 15:13:05.328"));
        assertEquals("MM-dd-yyyy HH:mm:ss.SSS", determineDateFormat("12-19-2019 15:13:05.328"));
        assertEquals("yyyy-MM-dd HH:mm:ss.SSS", determineDateFormat("2019-12-19 15:13:05.328"));
        assertEquals("yyyy-dd-MM HH:mm:ss.SSS", determineDateFormat("2019-19-12 15:13:05.328"));
        assertEquals("dd-MM-yyyy'T'HH:mm:ss.SSS", determineDateFormat("19-12-2019T15:13:05.328"));
        assertEquals("MM-dd-yyyy'T'HH:mm:ss.SSS", determineDateFormat("12-19-2019T15:13:05.328"));
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS", determineDateFormat("2019-12-19T15:13:05.328"));
        assertEquals("yyyy-dd-MM'T'HH:mm:ss.SSS", determineDateFormat("2019-19-12T15:13:05.328"));
        assertEquals("dd/MM/yyyy HH:mm:ss.SSS", determineDateFormat("19/12/2019 15:13:05.328"));
        assertEquals("MM/dd/yyyy HH:mm:ss.SSS", determineDateFormat("12/19/2019 15:13:05.328"));
        assertEquals("yyyy/MM/dd HH:mm:ss.SSS", determineDateFormat("2019/12/19 15:13:05.328"));
        assertEquals("yyyy/dd/MM HH:mm:ss.SSS", determineDateFormat("2019/19/12 15:13:05.328"));
        assertEquals("dd MM yyyy HH:mm:ss.SSS", determineDateFormat("19 12 2019 15:13:05.328"));
        assertEquals("MM dd yyyy HH:mm:ss.SSS", determineDateFormat("12 19 2019 15:13:05.328"));
        assertEquals("yyyy MM dd HH:mm:ss.SSS", determineDateFormat("2019 12 19 15:13:05.328"));
        assertEquals("yyyy dd MM HH:mm:ss.SSS", determineDateFormat("2019 19 12 15:13:05.328"));
        assertEquals("dd MMM yyyy HH:mm:ss.SSS", determineDateFormat("19 DEC 2019 15:13:05.328"));
        assertEquals("dd MMMM yyyy HH:mm:ss.SSS", determineDateFormat("19 DECEMBER 2019 15:13:05.328"));
    }

    @Test
    public void test_timestamp_date_yy() {

        System.out.println("\n************************************\ntest - Test timestamp format - date (yy) only");

        //12/19/2019 15:13:05
        assertEquals("yyMMdd", determineDateFormat("001219"));
        assertEquals("yyddMM", determineDateFormat("001912"));
        assertEquals("ddMMyy", determineDateFormat("191200"));
        assertEquals("MMddyy", determineDateFormat("121900"));
        assertEquals("dd-MM-yy", determineDateFormat("19-12-00"));
        assertEquals("MM-dd-yy", determineDateFormat("12-19-00"));
        assertEquals("yy-MM-dd", determineDateFormat("00-12-19"));
        assertEquals("yy-dd-MM", determineDateFormat("00-19-12"));
        assertEquals("dd/MM/yy", determineDateFormat("19/12/00"));
        assertEquals("MM/dd/yy", determineDateFormat("12/19/00"));
        assertEquals("yy/MM/dd", determineDateFormat("00/12/19"));
        assertEquals("yy/dd/MM", determineDateFormat("00/19/12"));
        assertEquals("dd MM yy", determineDateFormat("19 12 00"));
        assertEquals("MM dd yy", determineDateFormat("12 19 00"));
        assertEquals("yy MM dd", determineDateFormat("00 12 19"));
        assertEquals("yy dd MM", determineDateFormat("00 19 12"));
        assertEquals("dd MMM yy", determineDateFormat("19 Dec 00"));
        assertEquals("dd MMMM yy", determineDateFormat("19 DECEMBER 00"));
    }

    @Test
    public void test_timestamp_date_yy_HH_MM() {

        System.out.println("\n************************************\ntest - Test timestamp format - date (yy), hours and minutes");

        assertEquals("yyMMdd HHmm", determineDateFormat("001219 1513"));
        assertEquals("dd-MM-yy HH:mm", determineDateFormat("19-12-00 15:13"));
        assertEquals("MM-dd-yy HH:mm", determineDateFormat("12-19-00 15:13"));
        assertEquals("yy-MM-dd HH:mm", determineDateFormat("00-12-19 15:13"));
        assertEquals("yy-dd-MM HH:mm", determineDateFormat("00-19-12 15:13"));
        assertEquals("dd-MM-yy'T'HH:mm", determineDateFormat("19-12-00T15:13"));
        assertEquals("MM-dd-yy'T'HH:mm", determineDateFormat("12-19-00T15:13"));
        assertEquals("yy-MM-dd'T'HH:mm", determineDateFormat("00-12-19T15:13"));
        assertEquals("yy-dd-MM'T'HH:mm", determineDateFormat("00-19-12T15:13"));
        assertEquals("dd/MM/yy HH:mm", determineDateFormat("19/12/00 15:13"));
        assertEquals("MM/dd/yy HH:mm", determineDateFormat("12/19/00 15:13"));
        assertEquals("yy/MM/dd HH:mm", determineDateFormat("00/12/19 15:13"));
        assertEquals("yy/dd/MM HH:mm", determineDateFormat("00/19/12 15:13"));
        assertEquals("dd MM yy HH:mm", determineDateFormat("19 12 00 15:13"));
        assertEquals("MM dd yy HH:mm", determineDateFormat("12 19 00 15:13"));
        assertEquals("yy MM dd HH:mm", determineDateFormat("00 12 19 15:13"));
        assertEquals("yy dd MM HH:mm", determineDateFormat("00 19 12 15:13"));
        assertEquals("dd MMM yy HH:mm", determineDateFormat("19 DEC 00 15:13"));
        assertEquals("dd MMMM yy HH:mm", determineDateFormat("19 DECEMBER 00 15:13"));
    }

    @Test
    public void test_timestamp_date_yy_HH_MM_AM_PM() {

        System.out.println("\n************************************\ntest - Test timestamp format - date (yy), hours and minutes - AM/PM");

        assertEquals("dd-MM-yy HH:mm a", determineDateFormat("19-12-00 08:13 AM"));
        assertEquals("MM-dd-yy HH:mm a", determineDateFormat("12-19-00 08:13 AM"));
        assertEquals("yy-MM-dd HH:mm a", determineDateFormat("00-12-19 08:13 AM"));
        assertEquals("yy-dd-MM HH:mm a", determineDateFormat("00-19-12 08:13 AM"));
        assertEquals("dd-MM-yy'T'HH:mm a", determineDateFormat("19-12-00T08:13 AM"));
        assertEquals("MM-dd-yy'T'HH:mm a", determineDateFormat("12-19-00T08:13 AM"));
        assertEquals("yy-MM-dd'T'HH:mm a", determineDateFormat("00-12-19T08:13 AM"));
        assertEquals("yy-dd-MM'T'HH:mm a", determineDateFormat("00-19-12T08:13 AM"));
        assertEquals("dd/MM/yy HH:mm a", determineDateFormat("19/12/00 08:13 AM"));
        assertEquals("MM/dd/yy HH:mm a", determineDateFormat("12/19/00 08:13 AM"));
        assertEquals("yy/MM/dd HH:mm a", determineDateFormat("00/12/19 08:13 AM"));
        assertEquals("yy/dd/MM HH:mm a", determineDateFormat("00/19/12 08:13 AM"));
        assertEquals("dd MM yy HH:mm a", determineDateFormat("19 12 00 08:13 AM"));
        assertEquals("MM dd yy HH:mm a", determineDateFormat("12 19 00 08:13 AM"));
        assertEquals("yy MM dd HH:mm a", determineDateFormat("00 12 19 08:13 AM"));
        assertEquals("yy dd MM HH:mm a", determineDateFormat("00 19 12 08:13 AM"));
        assertEquals("dd MMM yy HH:mm a", determineDateFormat("19 DEC 00 08:13 AM"));
        assertEquals("dd MMMM yy HH:mm a", determineDateFormat("19 DECEMBER 00 08:13 AM"));
        assertEquals("dd-MM-yy HH:mma", determineDateFormat("19-12-00 08:13AM"));
        assertEquals("MM-dd-yy HH:mma", determineDateFormat("12-19-00 08:13AM"));
        assertEquals("yy-MM-dd HH:mma", determineDateFormat("00-12-19 08:13AM"));
        assertEquals("yy-dd-MM HH:mma", determineDateFormat("00-19-12 08:13AM"));
        assertEquals("dd-MM-yy'T'HH:mma", determineDateFormat("19-12-00T08:13AM"));
        assertEquals("MM-dd-yy'T'HH:mma", determineDateFormat("12-19-00T08:13AM"));
        assertEquals("yy-MM-dd'T'HH:mma", determineDateFormat("00-12-19T08:13AM"));
        assertEquals("yy-dd-MM'T'HH:mma", determineDateFormat("00-19-12T08:13AM"));
        assertEquals("dd/MM/yy HH:mma", determineDateFormat("19/12/00 08:13AM"));
        assertEquals("MM/dd/yy HH:mma", determineDateFormat("12/19/00 08:13AM"));
        assertEquals("yy/MM/dd HH:mma", determineDateFormat("00/12/19 08:13AM"));
        assertEquals("yy/dd/MM HH:mma", determineDateFormat("00/19/12 08:13AM"));
        assertEquals("dd MM yy HH:mma", determineDateFormat("19 12 00 08:13AM"));
        assertEquals("MM dd yy HH:mma", determineDateFormat("12 19 00 08:13AM"));
        assertEquals("yy MM dd HH:mma", determineDateFormat("00 12 19 08:13AM"));
        assertEquals("yy dd MM HH:mma", determineDateFormat("00 19 12 08:13AM"));
        assertEquals("dd MMM yy HH:mma", determineDateFormat("19 DEC 00 08:13AM"));
        assertEquals("dd MMMM yy HH:mma", determineDateFormat("19 DECEMBER 00 08:13AM"));
    }

    @Test
    public void test_timestamp_date_yy_HH_MM_ss() {

        System.out.println("\n************************************\ntest - Test timestamp format - date (yy), hours, minutes and seconds");

        assertEquals("yyMMdd HHmmss", determineDateFormat("001219 151305"));
        assertEquals("dd-MM-yy HH:mm:ss", determineDateFormat("19-12-00 15:13:05"));
        assertEquals("MM-dd-yy HH:mm:ss", determineDateFormat("12-19-00 15:13:05"));
        assertEquals("yy-MM-dd HH:mm:ss", determineDateFormat("00-12-19 15:13:05"));
        assertEquals("yy-dd-MM HH:mm:ss", determineDateFormat("00-19-12 15:13:05"));
        assertEquals("dd-MM-yy'T'HH:mm:ss", determineDateFormat("19-12-00T15:13:05"));
        assertEquals("MM-dd-yy'T'HH:mm:ss", determineDateFormat("12-19-00T15:13:05"));
        assertEquals("yy-MM-dd'T'HH:mm:ss", determineDateFormat("00-12-19T15:13:05"));
        assertEquals("yy-dd-MM'T'HH:mm:ss", determineDateFormat("00-19-12T15:13:05"));
        assertEquals("dd/MM/yy HH:mm:ss", determineDateFormat("19/12/00 15:13:05"));
        assertEquals("MM/dd/yy HH:mm:ss", determineDateFormat("12/19/00 15:13:05"));
        assertEquals("yy/MM/dd HH:mm:ss", determineDateFormat("00/12/19 15:13:05"));
        assertEquals("yy/dd/MM HH:mm:ss", determineDateFormat("00/19/12 15:13:05"));
        assertEquals("dd MM yy HH:mm:ss", determineDateFormat("19 12 00 15:13:05"));
        assertEquals("MM dd yy HH:mm:ss", determineDateFormat("12 19 00 15:13:05"));
        assertEquals("yy MM dd HH:mm:ss", determineDateFormat("00 12 19 15:13:05"));
        assertEquals("yy dd MM HH:mm:ss", determineDateFormat("00 19 12 15:13:05"));
        assertEquals("dd MMM yy HH:mm:ss", determineDateFormat("19 DEC 00 15:13:05"));
        assertEquals("dd MMMM yy HH:mm:ss", determineDateFormat("19 DECEMBER 00 15:13:05"));
    }

    @Test
    public void test_timestamp_date_yy_HH_MM_ss_am_pm() {

        System.out.println("\n************************************\ntest - Test timestamp format - date (yy), hours, minutes and seconds - AM/PM");

        assertEquals("dd-MM-yy HH:mm:ss a", determineDateFormat("19-12-00 09:13:05 pm"));
        assertEquals("MM-dd-yy HH:mm:ss a", determineDateFormat("12-19-00 09:13:05 pm"));
        assertEquals("yy-MM-dd HH:mm:ss a", determineDateFormat("00-12-19 09:13:05 pm"));
        assertEquals("yy-dd-MM HH:mm:ss a", determineDateFormat("00-19-12 09:13:05 pm"));
        assertEquals("dd-MM-yy'T'HH:mm:ss a", determineDateFormat("19-12-00T09:13:05 pm"));
        assertEquals("MM-dd-yy'T'HH:mm:ss a", determineDateFormat("12-19-00T09:13:05 pm"));
        assertEquals("yy-MM-dd'T'HH:mm:ss a", determineDateFormat("00-12-19T09:13:05 pm"));
        assertEquals("yy-dd-MM'T'HH:mm:ss a", determineDateFormat("00-19-12T09:13:05 pm"));
        assertEquals("dd/MM/yy HH:mm:ss a", determineDateFormat("19/12/00 09:13:05 pm"));
        assertEquals("MM/dd/yy HH:mm:ss a", determineDateFormat("12/19/00 09:13:05 pm"));
        assertEquals("yy/MM/dd HH:mm:ss a", determineDateFormat("00/12/19 09:13:05 pm"));
        assertEquals("yy/dd/MM HH:mm:ss a", determineDateFormat("00/19/12 09:13:05 pm"));
        assertEquals("dd MM yy HH:mm:ss a", determineDateFormat("19 12 00 09:13:05 pm"));
        assertEquals("MM dd yy HH:mm:ss a", determineDateFormat("12 19 00 09:13:05 pm"));
        assertEquals("yy MM dd HH:mm:ss a", determineDateFormat("00 12 19 09:13:05 pm"));
        assertEquals("yy dd MM HH:mm:ss a", determineDateFormat("00 19 12 09:13:05 pm"));
        assertEquals("dd MMM yy HH:mm:ss a", determineDateFormat("19 DEC 00 09:13:05 pm"));
        assertEquals("dd MMMM yy HH:mm:ss a", determineDateFormat("19 DECEMBER 00 09:13:05 pm"));
        assertEquals("dd-MM-yy HH:mm:ssa", determineDateFormat("19-12-00 09:13:05pm"));
        assertEquals("MM-dd-yy HH:mm:ssa", determineDateFormat("12-19-00 09:13:05pm"));
        assertEquals("yy-MM-dd HH:mm:ssa", determineDateFormat("00-12-19 09:13:05pm"));
        assertEquals("yy-dd-MM HH:mm:ssa", determineDateFormat("00-19-12 09:13:05pm"));
        assertEquals("dd-MM-yy'T'HH:mm:ssa", determineDateFormat("19-12-00T09:13:05pm"));
        assertEquals("MM-dd-yy'T'HH:mm:ssa", determineDateFormat("12-19-00T09:13:05pm"));
        assertEquals("yy-MM-dd'T'HH:mm:ssa", determineDateFormat("00-12-19T09:13:05pm"));
        assertEquals("yy-dd-MM'T'HH:mm:ssa", determineDateFormat("00-19-12T09:13:05pm"));
        assertEquals("dd/MM/yy HH:mm:ssa", determineDateFormat("19/12/00 09:13:05pm"));
        assertEquals("MM/dd/yy HH:mm:ssa", determineDateFormat("12/19/00 09:13:05pm"));
        assertEquals("yy/MM/dd HH:mm:ssa", determineDateFormat("00/12/19 09:13:05pm"));
        assertEquals("yy/dd/MM HH:mm:ssa", determineDateFormat("00/19/12 09:13:05pm"));
        assertEquals("dd MM yy HH:mm:ssa", determineDateFormat("19 12 00 09:13:05pm"));
        assertEquals("MM dd yy HH:mm:ssa", determineDateFormat("12 19 00 09:13:05pm"));
        assertEquals("yy MM dd HH:mm:ssa", determineDateFormat("00 12 19 09:13:05pm"));
        assertEquals("yy dd MM HH:mm:ssa", determineDateFormat("00 19 12 09:13:05pm"));
        assertEquals("dd MMM yy HH:mm:ssa", determineDateFormat("19 DEC 00 09:13:05pm"));
        assertEquals("dd MMMM yy HH:mm:ssa", determineDateFormat("19 DECEMBER 00 09:13:05pm"));
    }

    @Test
    public void test_timestamp_date_yy_HH_MM_ss_millisecond() {

        System.out.println("\n************************************\ntest - Test timestamp format - date (yy), hours, minutes, seconds and millisecond");

        assertEquals("yyMMdd HHmmssSSS", determineDateFormat("001219 151305328"));
        assertEquals("dd-MM-yy HH:mm:ss.SSS", determineDateFormat("19-12-00 15:13:05.328"));
        assertEquals("MM-dd-yy HH:mm:ss.SSS", determineDateFormat("12-19-00 15:13:05.328"));
        assertEquals("yy-MM-dd HH:mm:ss.SSS", determineDateFormat("00-12-19 15:13:05.328"));
        assertEquals("yy-dd-MM HH:mm:ss.SSS", determineDateFormat("00-19-12 15:13:05.328"));
        assertEquals("dd-MM-yy'T'HH:mm:ss.SSS", determineDateFormat("19-12-00T15:13:05.328"));
        assertEquals("MM-dd-yy'T'HH:mm:ss.SSS", determineDateFormat("12-19-00T15:13:05.328"));
        assertEquals("yy-MM-dd'T'HH:mm:ss.SSS", determineDateFormat("00-12-19T15:13:05.328"));
        assertEquals("yy-dd-MM'T'HH:mm:ss.SSS", determineDateFormat("00-19-12T15:13:05.328"));
        assertEquals("dd/MM/yy HH:mm:ss.SSS", determineDateFormat("19/12/00 15:13:05.328"));
        assertEquals("MM/dd/yy HH:mm:ss.SSS", determineDateFormat("12/19/00 15:13:05.328"));
        assertEquals("yy/MM/dd HH:mm:ss.SSS", determineDateFormat("00/12/19 15:13:05.328"));
        assertEquals("yy/dd/MM HH:mm:ss.SSS", determineDateFormat("00/19/12 15:13:05.328"));
        assertEquals("dd MM yy HH:mm:ss.SSS", determineDateFormat("19 12 00 15:13:05.328"));
        assertEquals("MM dd yy HH:mm:ss.SSS", determineDateFormat("12 19 00 15:13:05.328"));
        assertEquals("yy MM dd HH:mm:ss.SSS", determineDateFormat("00 12 19 15:13:05.328"));
        assertEquals("yy dd MM HH:mm:ss.SSS", determineDateFormat("00 19 12 15:13:05.328"));
        assertEquals("dd MMM yy HH:mm:ss.SSS", determineDateFormat("19 DEC 00 15:13:05.328"));
        assertEquals("dd MMMM yy HH:mm:ss.SSS", determineDateFormat("19 DECEMBER 00 15:13:05.328"));
        assertEquals("MMM dd yy HH:mm:ss.SSS", determineDateFormat("DEC 19 00 15:13:05.328"));
        assertEquals("MMMM dd yy HH:mm:ss.SSS", determineDateFormat("DECEMBER 19 00 15:13:05.328"));
        assertEquals("yy MMM dd HH:mm:ss.SSS", determineDateFormat("00 DEC 19 15:13:05.328"));
        assertEquals("yy MMMM dd HH:mm:ss.SSS", determineDateFormat("00 DECEMBER 19 15:13:05.328"));
        assertEquals("dd MMM yy HH mm ss SSS", determineDateFormat("19 DEC 00 15 13 05 328"));
        assertEquals("dd MMMM yy HH mm ss SSS", determineDateFormat("19 DECEMBER 00 15 13 05 328"));
        assertEquals("MMM dd yy HH mm ss SSS", determineDateFormat("DEC 19 00 15 13 05 328"));
        assertEquals("MMMM dd yy HH mm ss SSS", determineDateFormat("DECEMBER 19 00 15 13 05 328"));
        assertEquals("yy MMM dd HH mm ss SSS", determineDateFormat("00 DEC 19 15 13 05 328"));
        assertEquals("yy MMMM dd HH mm ss SSS", determineDateFormat("00 DECEMBER 19 15 13 05 328"));
    }

    @Test
    public void test_timestamp_date_yy_only() {

        System.out.println("\n************************************\ntest - Test timestamp format - year only (00, 2000 and 200012)");
        assertNull("yy", determineDateFormat("19"));
        assertNull("yyyy", determineDateFormat("2019"));
        assertNull("yyyyMM", determineDateFormat("200012"));
    }

    @Test
    public void test_timestamp_date_week_day_only() {

        System.out.println("\n************************************\ntest - Test timestamp format - week day only (Sat, Saturday and Sa)");
        assertNull("EEE", determineDateFormat("Sat"));
        assertNull("EEEE", determineDateFormat("Saturday"));
        assertNull("EEEEE", determineDateFormat("SA"));
    }

    @Test
    public void test_timestamp_date_time_only() {

        System.out.println("\n************************************\ntest - Test timestamp format - time only (0208, 125959, 125959.879 or 12:08, 12:59:59, 12:59:59.879)");
        assertNull("HHmm", determineDateFormat("0208"));
        assertNull("HHmmss", determineDateFormat("125959"));
        assertNull("HHmmss.SSS", determineDateFormat("125959.879"));
        assertNull("HH:mm", determineDateFormat("12:08"));
        assertNull("HH:mm:ss", determineDateFormat("12:59:59"));
        assertNull("HH:mm:ss.SSS", determineDateFormat("12:59:59.879"));
        assertNull("HH:mm a", determineDateFormat("12:08 am"));
        assertNull("HH:mm:ss a", determineDateFormat("12:59:59 pm"));
        assertNull("HH:mm:ss.SSS a", determineDateFormat("12:59:59.879 am"));
        assertNull("HH:mma", determineDateFormat("12:08am"));
        assertNull("HH:mm:ssa", determineDateFormat("12:59:59pm"));
        assertNull("HH:mm:ss.Sa", determineDateFormat("12:59:59.8am"));
        assertNull("HH:mm:ss.SSSa", determineDateFormat("12:59:59.879am"));
        assertNull("mm:ss", determineDateFormat("59:59"));
        assertNull("mm:ss.S", determineDateFormat("59:59.8"));
        assertNull("mm:ss.SSS", determineDateFormat("59:59.879"));
    }

    @Test
    public void test_timestamp_unsupported_separators() {

        System.out.println("\n************************************\ntest - Test timestamp format with unsupported separators");
        assertEquals("yyyy MM dd", determineDateFormat("2019#12#19"));
        assertEquals("yyyy MM dd HH mm", determineDateFormat("2019*12*19*15*13"));
        assertEquals("dd MM yyyy HH mm ss", determineDateFormat("19#12#2019 15#13#05"));
        assertEquals("dd MM yyyy HH mm ss SSS", determineDateFormat("19~12~2019 15~13~05~328"));
        assertEquals("dd MM yy", determineDateFormat("19#12#00"));
        assertEquals("MM dd yy HH mm", determineDateFormat("12*19*00 15*13"));
        assertEquals("yy MM dd HH mm ss", determineDateFormat("00*12*19 15*13*05"));
        assertEquals("yy MM dd HH mm ss SSS", determineDateFormat("00*12*19 15*13*05*328"));
    }

    @Test
    public void test_timestamp_date_HH_MM_ss_S() {

        System.out.println("\n************************************\ntest - Test timestamp format - date, hours, minutes , seconds and single digit millisecond");

        assertEquals("yyyyMMddHHmmssS", determineDateFormat("201912191513059"));
        assertEquals("yyyyMMdd HHmmssS", determineDateFormat("20191219 1513059"));
        assertEquals("dd-MM-yyyy HH:mm:ss.S", determineDateFormat("19-12-2019 15:13:05.9"));
        assertEquals("MM-dd-yyyy HH:mm:ss.S", determineDateFormat("12-19-2019 15:13:05.9"));
        assertEquals("yyyy-MM-dd HH:mm:ss.S", determineDateFormat("2019-12-19 15:13:05.9"));
        assertEquals("yyyy-dd-MM HH:mm:ss.S", determineDateFormat("2019-19-12 15:13:05.9"));
        assertEquals("HH:mm:ss.S yyyy-MM-dd", determineDateFormat("15:13:05.9 2019-12-19"));
        assertEquals("dd-MM-yyyy'T'HH:mm:ss.S", determineDateFormat("19-12-2019T15:13:05.9"));
        assertEquals("MM-dd-yyyy'T'HH:mm:ss.S", determineDateFormat("12-19-2019T15:13:05.9"));
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.S", determineDateFormat("2019-12-19T15:13:05.9"));
        assertEquals("yyyy-dd-MM'T'HH:mm:ss.S", determineDateFormat("2019-19-12T15:13:05.9"));
        assertEquals("dd/MM/yyyy HH:mm:ss.S", determineDateFormat("19/12/2019 15:13:05.9"));
        assertEquals("MM/dd/yyyy HH:mm:ss.S", determineDateFormat("12/19/2019 15:13:05.9"));
        assertEquals("yyyy/MM/dd HH:mm:ss.S", determineDateFormat("2019/12/19 15:13:05.9"));
        assertEquals("yyyy/dd/MM HH:mm:ss.S", determineDateFormat("2019/19/12 15:13:05.9"));
        assertEquals("dd MM yyyy HH:mm:ss.S", determineDateFormat("19 12 2019 15:13:05.9"));
        assertEquals("MM dd yyyy HH:mm:ss.S", determineDateFormat("12 19 2019 15:13:05.9"));
        assertEquals("yyyy MM dd HH:mm:ss.S", determineDateFormat("2019 12 19 15:13:05.9"));
        assertEquals("yyyy dd MM HH:mm:ss.S", determineDateFormat("2019 19 12 15:13:05.9"));
        assertEquals("dd MMM yyyy HH:mm:ss.S", determineDateFormat("19 DEC 2019 15:13:05.9"));
        assertEquals("dd MMMM yyyy HH:mm:ss.S", determineDateFormat("19 DECEMBER 2019 15:13:05.9"));
    }
}
