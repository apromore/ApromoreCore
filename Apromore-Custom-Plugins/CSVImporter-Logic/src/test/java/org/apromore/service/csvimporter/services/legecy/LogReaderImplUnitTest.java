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

package org.apromore.service.csvimporter.services.legecy;

import com.google.common.io.ByteStreams;
import org.apromore.service.csvimporter.model.LogModel;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@Ignore
public class LogReaderImplUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogReaderImplUnitTest.class);
    private LogReader logReader = new LogReaderImpl();

    ParquetFactoryProvider parquetFactoryProvider = new ParquetFactoryProvider();

    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", "process type");

    private static String toString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        return baos.toString();
    }

    /**
     * This is hack to convert the test case XML documents from the time zone where they were created to the
     * time zone where the test is running.
     *
     * @param in               the XML text of the test data
     * @param testDataTimezone a regex for the timezone used in the test data, e.g. <code>"\\+03:00"</code>
     * @return the XML text with the local time zone substituted
     */
    private String correctTimeZone(String in, String testDataTimezone) {
        TimeZone tz = TimeZone.getDefault();

        System.out.println("TimeZone " + tz.getRawOffset());
//        int offsetMinutes = (tz.getRawOffset() + tz.getDSTSavings()) / 60000;
        int offsetMinutes = (tz.getRawOffset()) / 60000;
        NumberFormat hoursFormat = new DecimalFormat("+00;-00)");
        NumberFormat minutesFormat = new DecimalFormat("00");

        return in.replaceAll(testDataTimezone, "\\" + hoursFormat.format(offsetMinutes / 60) + ":" + minutesFormat.format(offsetMinutes % 60));
    }


    // Test cases

    /**
     * Test {@link SampleLogGenerator.generateSampleLog} sampling fewer lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleCSV_undersample() throws Exception {

        LogSample logSample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test1-valid.csv"), 2, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logSample.getHeader());
        assertEquals(2, logSample.getLines().size());
    }

    /**
     * Test {@link SampleLogGenerator.generateSampleLog} sampling more lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleCSV_oversample() throws Exception {

        LogSample logSample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test1-valid.csv"), 5, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logSample.getHeader());
        assertEquals(3, logSample.getLines().size());
    }

    /**
     * Test {@link LogReaderImpl.readLogs} to convert to CSVReader.
     */
    @Test
    public void testPrepareXesModel_test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid csv test ");

        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test1-expected.xes")), Charset.forName("utf-8")), "\\+03:00");
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test1-valid.csv"), 100, "UTF-8");

        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test1-valid.csv"), sample, "UTF-8", false);


        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

    /**
     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test2_missing_columns() throws Exception {

        System.out.println("\n************************************\ntest2 - Missing columns test");

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test2-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test2-missing-columns.csv"), 100, "UTF-8");

        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test2-missing-columns.csv"), sample, "UTF-8", false);

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

    /**
     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        System.out.println("\n************************************\ntest3 - Invalid end timestamp");

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test3-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test3-invalid-end-timestamp.csv"), 2, "UTF-8");

        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test3-invalid-end-timestamp.csv"), sample, "UTF-8", true);


        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

    /**
     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        System.out.println("\n************************************\ntest4 - Invalid start timestamp");

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test4-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test4-invalid-start-timestamp.csv"), 2, "UTF-8");

        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test4-invalid-start-timestamp.csv"), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }


    /**
     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test5_empty_caseID() throws Exception {

        System.out.println("\n************************************\ntest5 - Empty caseID");

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test5-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test5-empty-caseID.csv"), 100, "UTF-8");

        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test5-empty-caseID.csv"), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

    }

    /**
     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test6_different_delimiters() throws Exception {

        System.out.println("\n************************************\ntest6 - different delimiters");

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test6-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test6-different-delimiters.csv"), 100, "UTF-8");

        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test6-different-delimiters.csv"), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }


    /**
     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test7_record_invalid() throws Exception {

        System.out.println("\n************************************\ntest7 - Record invalid");

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test7-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test7-record-invalid.csv"), 100, "UTF-8");
        sample.setStartTimestampPos(2);
        sample.getCaseAttributesPos().remove(Integer.valueOf(2));
        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test7-record-invalid.csv"), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(1, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());


        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        //Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }


    /**
     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test8_all_invalid() throws Exception {

        System.out.println("\n************************************\ntest8 - All invalid");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test8-all-invalid.csv"), 2, "UTF-8");

        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test8-all-invalid.csv"), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(0, logModel.getRowsCount());
        assertEquals(3, logModel.getLogErrorReport().size());
    }


//    /**
//     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
//     */
//    @Test
//    public void testPrepareXesModel_test9_differentiate_dates() throws Exception {
//
//        System.out.println("\n************************************\ntest9 - Differentiate dates");
//
//        // Set up inputs and expected outputs
//        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test9-expected.xes")), Charset.forName("utf-8")), "\\+02:00");
//
//        // Perform the test
//        LogSample sample = parquetFactoryProvider
//                .getParquetFactory("csv")
//                .createSampleLogGenerator()
//                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test9-differentiate-dates.csv"), 100, "UTF-8");
//
//        sample.setEndTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
//        sample.setStartTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
//        sample.setEndTimestampPos(3);
//        sample.setStartTimestampPos(2);
//        sample.getEventAttributesPos().remove(Integer.valueOf(2));
//        sample.getEventAttributesPos().remove(Integer.valueOf(3));
//        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test9-differentiate-dates.csv"), sample, "UTF-8", true);
//
//
////        assertNotNull(logModel);
////        assertEquals(13, logModel.getRowsCount());
////        assertEquals(0, logModel.getLogErrorReport().size());
//
//        // Continue with the XES conversion
//        XLog xlog = logModel.getXLog();
//
////        System.out.println("expectedXES " + expectedXES);
////        System.out.println("toString(xlog) " + toString(xlog));
////        // Validate result
////        assertNotNull(xlog);
////        assertEquals(expectedXES, toString(xlog));
//    }


    /**
     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test10_detect_name() throws Exception {

        System.out.println("\n************************************\ntest10 - Event Attribute");

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test10-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test10-eventAttribute.csv"), 100, "UTF-8");
        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test10-eventAttribute.csv"), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

//    /**
//     * Test {@link LogReaderImpl.readLogs} against an invalid CSV log <code>test2-missing-columns.csv</code>.
//     */
//    @Test
//    public void testPrepareXesModel_test11_encoding() throws Exception {
//
//        System.out.println("\n************************************\ntest11 - Encoding");
//
//        // Set up inputs and expected outputs
//        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream("/test11-expected.xes")), Charset.forName("utf-8")), "\\+02:00");
//
//        // Perform the test
//        LogSample sample = parquetFactoryProvider
//                .getParquetFactory("csv")
//                .createSampleLogGenerator()
//                .generateSampleLog(LogReaderImplUnitTest.class.getResourceAsStream("/test11-encoding.csv"), 100, "windows-1255");
//        sample.setActivityPos(1);
//        sample.getEventAttributesPos().remove(Integer.valueOf(1));
//        LogModel logModel = logReader.readLogs(LogReaderImplUnitTest.class.getResourceAsStream("/test11-encoding.csv"), sample, "windows-1255", true);
//
//        // Validate result
//        assertNotNull(logModel);
//        assertEquals(5, logModel.getRowsCount());
//        assertEquals(0, logModel.getLogErrorReport().size());
//        // Continue with the XES conversion
//        XLog xlog = logModel.getXLog();
//
////        System.out.println("expectedXES " + expectedXES);
////        System.out.println("toString(xlog) " + toString(xlog));
//        // Validate result
////        assertNotNull(xlog);
////        assertEquals(expectedXES, toString(xlog));
//    }
}