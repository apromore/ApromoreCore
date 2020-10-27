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

import org.apromore.service.csvimporter.model.LogModel;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.apromore.service.csvimporter.services.SampleLogGenerator;
import org.apromore.service.csvimporter.services.utilities.TestUtilities;
import org.deckfour.xes.model.XLog;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class XLSXLogReaderImplUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(XLSXLogReaderImplUnitTest.class);
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private final List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", "process type");
    private TestUtilities utilities;
    private ParquetFactoryProvider parquetFactoryProvider;
    private SampleLogGenerator sampleLogGenerator;
    private LogReader logReader;

    @Before
    public void init() {
        utilities = new TestUtilities();
        parquetFactoryProvider = new ParquetFactoryProvider();
        sampleLogGenerator = parquetFactoryProvider
                .getParquetFactory("xlsx")
                .createSampleLogGenerator();
        logReader = new XLSXLogReaderImpl();
    }

    /**
     * Test {@link SampleLogGenerator} sampling fewer lines than contained in <code>test1-valid.xlsx</code>.
     */
    @Test
    public void testSampleCSV_undersample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - undersample");

        // Test file data
        String testFile = "/test1-valid.xlsx";
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, sample.getHeader());
        assertEquals(2, sample.getLines().size());
    }

    /**
     * Test {@link SampleLogGenerator} sampling more lines than contained in <code>test1-valid.xlsx</code>.
     */
    @Test
    public void testSampleCSV_oversample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - oversample");

        // Test file data
        String testFile = "/test1-valid.xlsx";
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 5, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, sample.getHeader());
        assertEquals(3, sample.getLines().size());
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an valid xlsx log <code>test1-valid.xlsx</code>.
     */
    @Test
    public void test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1-valid.xlsx");

        // Test file data
        String testFile = "/test1-valid.xlsx";
        String expectedFile = "/test1-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        //Generate sample
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());

        //  Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test2-missing-columns.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test2_missing_columns() throws Exception {

        LOGGER.info("\n************************************\ntest2 - Missing columns test");

        String testFile = "/test2-missing-columns.xlsx";
        String expectedFile = "/test2-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        //Generate sample
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);


        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test3-invalid-end-timestamp.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest3 - Invalid end timestamp");

        String testFile = "/test3-invalid-end-timestamp.xlsx";
        String expectedFile = "/test3-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);


        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test4-invalid-start-timestamp.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest4 - Invalid start timestamp");

        String testFile = "/test4-invalid-start-timestamp.xlsx";
        String expectedFile = "/test4-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test5-expected.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test5_empty_caseID() throws Exception {

        LOGGER.info("\n************************************\ntest5 - Empty caseID");

        String testFile = "/test5-empty-caseID.xlsx";
        String expectedFile = "/test5-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));

    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test7-record-invalid.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test6_record_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest6 - Record invalid");

        String testFile = "/test7-record-invalid.xlsx";
        String expectedFile = "/test7-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        sample.setStartTimestampPos(2);
        sample.getCaseAttributesPos().remove(Integer.valueOf(2));

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(1, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        //Validate result
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link XLSXLogReaderImpl } against an invalid xlsx log <code>test8-all-invalid.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test7_all_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest7 - All invalid");
        String testFile = "/test8-all-invalid.xlsx";

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(0, logModel.getRowsCount());
        assertEquals(3, logModel.getLogErrorReport().size());
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test9-differentiate-dates.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test8_differentiate_dates() throws Exception {

        LOGGER.info("\n************************************\ntest8 - Differentiate dates");

        String testFile = "/test9-differentiate-dates.xlsx";
        String expectedFile = "/test9-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        sample.setEndTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setStartTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setEndTimestampPos(3);
        sample.setStartTimestampPos(2);
        sample.getEventAttributesPos().remove(Integer.valueOf(2));
        sample.getEventAttributesPos().remove(Integer.valueOf(3));

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);


        assertNotNull(logModel);
        assertEquals(13, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test10-eventAttribute.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test9_detect_name() throws Exception {

        LOGGER.info("\n************************************\ntest9 - Event Attribute");

        String testFile = "/test10-eventAttribute.xlsx";
        String expectedFile = "/test10-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link XLSXLogReaderImpl} against an invalid xlsx log <code>test11-encoding.xlsx</code>.
     */
    @Test
    public void testPrepareXesModel_test10_encoding() throws Exception {

        LOGGER.info("\n************************************\ntest10 - Encoding");

        String testFile = "/test11-encoding.xlsx";
        String expectedFile = "/test11-expected.xes";

        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile).replaceAll("\\r\\n?", "\n");

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "windows-1255");

        sample.setActivityPos(1);
        sample.getEventAttributesPos().remove(Integer.valueOf(1));

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "windows-1255", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(5, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        //Continue with the XES conversion
        XLog xlog = logModel.getXLog();
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }
}
