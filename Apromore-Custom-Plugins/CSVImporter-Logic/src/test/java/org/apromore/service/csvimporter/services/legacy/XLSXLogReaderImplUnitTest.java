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
package org.apromore.service.csvimporter.services.legacy;

import com.google.common.io.ByteStreams;
import org.apromore.service.csvimporter.model.LogModel;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.apromore.service.csvimporter.services.SampleLogGenerator;
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
public class XLSXLogReaderImplUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(XLSXLogReaderImplUnitTest.class);
    ParquetFactoryProvider parquetFactoryProvider = new ParquetFactoryProvider();
    private LogReader logReader = new XLSXLogReaderImpl();

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
     * This is hack to convert the test case CSV documents from the time zone where they were created to the
     * time zone where the test is running.
     *
     * @param in               the XML text of the test data
     * @param testDataTimezone a regex for the timezone used in the test data, e.g. <code>"\\+03:00"</code>
     * @return the XML text with the local time zone substituted
     */
    private String correctTimeZone(String in, String testDataTimezone) {
        TimeZone tz = TimeZone.getDefault();
        int offsetMinutes = (tz.getRawOffset() + tz.getDSTSavings()) / 60000;
        NumberFormat hoursFormat = new DecimalFormat("+00;-00)");
        NumberFormat minutesFormat = new DecimalFormat("00");

        return in.replaceAll(testDataTimezone, "\\" + hoursFormat.format(offsetMinutes / 60) + ":" + minutesFormat.format(offsetMinutes % 60));
    }

    // Test cases
    /**
     * Test {@link SampleLogGenerator} sampling fewer lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleCSV_undersample() throws Exception {

        LOGGER.info("\n************************************\ntest - sampling fewer lines than contained in test1-valid.csv");
        String testFile = "/test1-valid.xlsx";
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("xlsx")
                .createSampleLogGenerator()
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, sample.getHeader());
        assertEquals(2, sample.getLines().size());
    }

    /**
     * Test {@link SampleLogGenerator} sampling more lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleCSV_oversample() throws Exception {

        LOGGER.info("\n************************************\ntest - sampling more lines than contained in test1-valid.csv");

        String testFile = "/test1-valid.xlsx";
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("xlsx")
                .createSampleLogGenerator()
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 5, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, sample.getHeader());
        assertEquals(3, sample.getLines().size());
    }
    /**
     * Test {@link XLSXLogReaderImpl } to convert to CSVReader.
     */
    @Test
    public void test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid parquet test");

        //Xlsx file input
        String testFile = "/test1-valid.xlsx";
        String expectedFile = "/test1-expected.xes";

        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8")), "\\+03:00");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("xlsx")
                .createSampleLogGenerator()
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "UTF-8");

        LogModel logModel = logReader.readLogs(
                this.getClass().getResourceAsStream(testFile),
                sample,
                "UTF-8",
                true);

//        System.out.println("getLogErrorReport " + logModel.getLogErrorReport().size());
//        System.out.println("getLogErrorReport " + logModel.getLogErrorReport());
//        System.out.println("getRowsCount " + logModel.getRowsCount());
//        for (List<String> line : sample.getLines())
//            System.out.println("line " + line);
//        System.out.println("lines " + toString(logModel.getXLog()));

        // Validate result
//        assertNotNull(logModel);
//        assertEquals(3, logModel.getRowsCount());
//        assertEquals(0, logModel.getLogErrorReport().size());
//        assertEquals(false, logModel.isRowLimitExceeded());

        // Continue with the XES conversion
//        XLog xlog = logModel.getXLog();

        // Validate result
//        assertNotNull(xlog);
//        assertEquals(expectedXES, toString(xlog));
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test2-missing-columns.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test2_missing_columns() throws Exception {

        System.out.println("\n************************************\ntest2 - Missing columns test");

        String testFile = "/test2-missing-columns.xlsx";
        String expectedFile = "/test2-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8")), "\\+03:00");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("xlsx")
                .createSampleLogGenerator()
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "UTF-8");

        LogModel logModel = logReader.readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);


        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        System.out.println("xlog " + toString(xlog));
        System.out.println("expected " + expectedXES);
        // Validate result
        assertNotNull(xlog);
//        assertEquals(expectedXES, toString(xlog));
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test3-invalid-end-timestamp.xlsx</code>.
     */
    @Ignore
    @Test
    public void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        System.out.println("\n************************************\ntest3 - Invalid end timestamp");

        String testFile = "/test3-invalid-end-timestamp.xlsx";
        String expectedFile = "/test3-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("xlsx")
                .createSampleLogGenerator()
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        LogModel logModel = logReader.readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);


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


}
