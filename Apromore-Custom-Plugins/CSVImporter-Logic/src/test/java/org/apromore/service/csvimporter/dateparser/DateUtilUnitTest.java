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
package org.apromore.service.csvimporter.dateparser;

import junit.framework.TestCase;
import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.apromore.service.csvimporter.services.SampleLogGenerator;
import org.apromore.service.csvimporter.services.legacy.LogReader;
import org.apromore.service.csvimporter.services.legacy.LogReaderImpl;
import org.apromore.service.csvimporter.services.utilities.TestUtilities;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.dateparser.DateUtil.determineDateFormat;


public class DateUtilUnitTest {
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", "process type");
    private TestUtilities utilities;
    private ParquetFactoryProvider parquetFactoryProvider;
    private SampleLogGenerator sampleLogGenerator;
    private LogReader logReader;

    @Before
    public void init() {
        utilities = new TestUtilities();
        parquetFactoryProvider = new ParquetFactoryProvider();
        sampleLogGenerator = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator();
        logReader = new LogReaderImpl();
    }

    @Test
    public void test_timestamp_date() {

        System.out.println("\n************************************\ntest1 - Test timestamp format - date only");

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
    }

    @Test
    public void test_timestamp_date_HH_MM() {

        System.out.println("\n************************************\ntest2 - Test timestamp format - date, hours and minutes");

        assertEquals("yyyyMMddHHmm", determineDateFormat("201912191513"));
        assertEquals("yyyyMMdd HHmm", determineDateFormat("20191219 1513"));
        assertEquals("dd-MM-yyyy HH:mm", determineDateFormat("19-12-2019 15:13"));
        assertEquals("MM-dd-yyyy HH:mm", determineDateFormat("12-19-2019 15:13"));
        assertEquals("yyyy-MM-dd HH:mm", determineDateFormat("2019-12-19 15:13"));
        assertEquals("yyyy-dd-MM HH:mm", determineDateFormat("2019-19-12 15:13"));
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
    public void test_timestamp_date_HH_MM_ss() {

        System.out.println("\n************************************\ntest3 - Test timestamp format - date, hours, minutes and seconds");

        assertEquals("yyyyMMddHHmmss", determineDateFormat("20191219151305"));
        assertEquals("yyyyMMdd HHmmss", determineDateFormat("20191219 151305"));
        assertEquals("dd-MM-yyyy HH:mm:ss", determineDateFormat("19-12-2019 15:13:05"));
        assertEquals("MM-dd-yyyy HH:mm:ss", determineDateFormat("12-19-2019 15:13:05"));
        assertEquals("yyyy-MM-dd HH:mm:ss", determineDateFormat("2019-12-19 15:13:05"));
        assertEquals("yyyy-dd-MM HH:mm:ss", determineDateFormat("2019-19-12 15:13:05"));
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
    public void test_timestamp_date_HH_MM_ss_millisecond() {

        System.out.println("\n************************************\ntest4 - Test timestamp format - date, hours, minutes, seconds and millisecond");

        assertEquals("yyyyMMddHHmmssSSS", determineDateFormat("20191219151305328"));
        assertEquals("yyyyMMdd HHmmssSSS", determineDateFormat("20191219 151305328"));
        assertEquals("dd-MM-yyyy HH:mm:ss.SSS", determineDateFormat("19-12-2019 15:13:05.328"));
        assertEquals("MM-dd-yyyy HH:mm:ss.SSS", determineDateFormat("12-19-2019 15:13:05.328"));
        assertEquals("yyyy-MM-dd HH:mm:ss.SSS", determineDateFormat("2019-12-19 15:13:05.328"));
        assertEquals("yyyy-dd-MM HH:mm:ss.SSS", determineDateFormat("2019-19-12 15:13:05.328"));
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

        System.out.println("\n************************************\ntest5 - Test timestamp format - date (yy) only");

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

        System.out.println("\n************************************\ntest6 - Test timestamp format - date (yy), hours and minutes");

        assertEquals("yyMMdd HHmm", determineDateFormat("001219 1513"));
        assertEquals("dd-MM-yy HH:mm", determineDateFormat("19-12-00 15:13"));
        assertEquals("MM-dd-yy HH:mm", determineDateFormat("12-19-00 15:13"));
        assertEquals("yy-MM-dd HH:mm", determineDateFormat("00-12-19 15:13"));
        assertEquals("yy-dd-MM HH:mm", determineDateFormat("00-19-12 15:13"));
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
    public void test_timestamp_date_yy_HH_MM_ss() {

        System.out.println("\n************************************\ntest7 - Test timestamp format - date (yy), hours, minutes and seconds");

        assertEquals("yyMMdd HHmmss", determineDateFormat("001219 151305"));
        assertEquals("dd-MM-yy HH:mm:ss", determineDateFormat("19-12-00 15:13:05"));
        assertEquals("MM-dd-yy HH:mm:ss", determineDateFormat("12-19-00 15:13:05"));
        assertEquals("yy-MM-dd HH:mm:ss", determineDateFormat("00-12-19 15:13:05"));
        assertEquals("yy-dd-MM HH:mm:ss", determineDateFormat("00-19-12 15:13:05"));
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
    public void test_timestamp_date_yy_HH_MM_ss_millisecond() {

        System.out.println("\n************************************\ntest8 - Test timestamp format - date (yy), hours, minutes, seconds and millisecond");

        assertEquals("yyMMdd HHmmssSSS", determineDateFormat("001219 151305328"));
        assertEquals("dd-MM-yy HH:mm:ss.SSS", determineDateFormat("19-12-00 15:13:05.328"));
        assertEquals("MM-dd-yy HH:mm:ss.SSS", determineDateFormat("12-19-00 15:13:05.328"));
        assertEquals("yy-MM-dd HH:mm:ss.SSS", determineDateFormat("00-12-19 15:13:05.328"));
        assertEquals("yy-dd-MM HH:mm:ss.SSS", determineDateFormat("00-19-12 15:13:05.328"));
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
    }
    @Test
    public void test_timestamp_date_yy_only() {

        System.out.println("\n************************************\ntest9 - Test timestamp format - year only (00, 2000 and 200012)");
        assertEquals("yy", determineDateFormat("19"));
        assertEquals("yyyy", determineDateFormat("2019"));
        assertEquals("yyyyMM", determineDateFormat("200012"));
    }
    @Test
    public void test_timestamp_date_week_day_only() {

        System.out.println("\n************************************\ntest10 - Test timestamp format - week day only (Sat, Saturday and Sa)");
        assertEquals("EEE", determineDateFormat("Sat"));
        assertEquals("EEEE", determineDateFormat("Saturday"));
        assertEquals("EEEEE", determineDateFormat("SA"));
    }
    @Test
    public void test_timestamp_date_time_only() {

        System.out.println("\n************************************\ntest11 - Test timestamp format - time only (0208, 125959, 125959.879 or 12:08, 12:59:59, 12:59:59.879)");
        assertEquals("HHmm", determineDateFormat("0208"));
        assertEquals("HHmmss", determineDateFormat("125959"));
        assertEquals("HHmmss.SSS", determineDateFormat("125959.879"));
        assertEquals("HH:mm", determineDateFormat("12:08"));
        assertEquals("HH:mm:ss", determineDateFormat("12:59:59"));
        assertEquals("HH:mm:ss.SSS", determineDateFormat("12:59:59.879"));
    }
    @Test
    public void test_timestamp_unsupported_separators() {

        System.out.println("\n************************************\ntest11 - Test timestamp format with unsupported separators");
        assertEquals("yyyy MM dd", determineDateFormat("2019#12#19"));
        assertEquals("yyyy MM dd HH mm", determineDateFormat("2019*12*19*15*13"));
        assertEquals("dd MM yyyy HH mm ss", determineDateFormat("19#12#2019 15#13#05"));
        assertEquals("dd MM yyyy HH mm ss SSS", determineDateFormat("19~12~2019 15~13~05~328"));
        assertEquals("dd MM yy", determineDateFormat("19#12#00"));
        assertEquals("MM dd yy HH mm", determineDateFormat("12*19*00 15*13"));
        assertEquals("yy MM dd HH mm ss", determineDateFormat("00*12*19 15*13*05"));
        assertEquals("yy MM dd HH mm ss SSS", determineDateFormat("00*12*19 15*13*05*328"));
    }
}