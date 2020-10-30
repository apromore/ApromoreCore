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


public class LogReaderImplUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogReaderImplUnitTest.class);
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

    /**
     * Test {@link SampleLogGenerator} sampling fewer lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleCSV_undersample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - undersample");

        // Test file data
        String testFile = "/test1-valid.csv";
        LogSample logSample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logSample.getHeader());
        assertEquals(2, logSample.getLines().size());
    }

    /**
     * Test {@link SampleLogGenerator} sampling more lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleCSV_oversample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - oversample");

        // Test file data
        String testFile = "/test1-valid.csv";
        LogSample logSample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 5, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logSample.getHeader());
        assertEquals(3, logSample.getLines().size());
    }

    /**
     * Test {@link LogReaderImpl} against an valid CSV log <code>test1-valid.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid csv test ");

        // Test file data
        String testFile = "/test1-valid.csv";
        String expectedFile = "/test1-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test2_missing_columns() throws Exception {

        LOGGER.info("\n************************************\ntest2 - Missing columns test");

        // Test file data
        String testFile = "/test2-missing-columns.csv";
        String expectedFile = "/test2-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test3-invalid-end-timestamp.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest3 - Invalid end timestamp");

        // Test file data
        String testFile = "/test3-invalid-end-timestamp.csv";
        String expectedFile = "/test3-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test4-invalid-start-timestamp.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest4 - Invalid start timestamp");

        // Test file data
        String testFile = "/test4-invalid-start-timestamp.csv";
        String expectedFile = "/test4-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }


    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test5-empty-caseID.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test5_empty_caseID() throws Exception {

        LOGGER.info("\n************************************\ntest5 - Empty caseID");

        // Test file data
        String testFile = "/test5-empty-caseID.csv";
        String expectedFile = "/test5-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));

    }

    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test6-different-delimiters.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test6_different_delimiters() throws Exception {

        LOGGER.info("\n************************************\ntest6 - different delimiters");

        // Test file data
        String testFile = "/test6-different-delimiters.csv";
        String expectedFile = "/test6-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }


    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test7-record-invalid.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test7_record_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest7 - Record invalid");

        // Test file data
        String testFile = "/test7-record-invalid.csv";
        String expectedFile = "/test7-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        sample.setStartTimestampPos(2);
        sample.getCaseAttributesPos().remove(Integer.valueOf(2));

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(1, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }


    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test8-all-invalid.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test8_all_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest8 - All invalid");

        // Test file data
        String testFile = "/test8-all-invalid.csv";

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
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test9-differentiate-dates.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test9_differentiate_dates() throws Exception {

        LOGGER.info("\n************************************\ntest9 - Differentiate dates");

        // Test file data
        String testFile = "/test9-differentiate-dates.csv";
        String expectedFile = "/test9-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "UTF-8");

        sample.setEndTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setStartTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setEndTimestampPos(3);
        sample.setStartTimestampPos(2);
        sample.getEventAttributesPos().remove(Integer.valueOf(2));
        sample.getEventAttributesPos().remove(Integer.valueOf(3));

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(13, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }


    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test10-eventAttribute.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test10_detect_name() throws Exception {

        LOGGER.info("\n************************************\ntest10 - Event Attribute");

        // Test file data
        String testFile = "/test10-eventAttribute.csv";
        String expectedFile = "/test10-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogReaderImpl} against an invalid CSV log <code>test11-encoding.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test11_encoding() throws Exception {

        LOGGER.info("\n************************************\ntest11 - Encoding");

        // Test file data
        String testFile = "/test11-encoding.csv";
        String expectedFile = "/test11-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "windows-1255");

        sample.setActivityPos(1);
        sample.getEventAttributesPos().remove(Integer.valueOf(1));

        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "windows-1255", true);
        //Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(5, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(
                utilities.removeTimezone(expectedXES),
                utilities.removeTimezone(utilities.xlogToString(xlog)));
    }
}
