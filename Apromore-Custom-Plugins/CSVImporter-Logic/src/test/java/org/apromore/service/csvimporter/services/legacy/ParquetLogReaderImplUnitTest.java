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
import org.apromore.service.csvimporter.services.utilities.TestUtilities;
import org.deckfour.xes.model.XLog;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ParquetLogReaderImplUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParquetLogReaderImplUnitTest.class);
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private final List<String> PARQUET_EXPECTED_HEADER = Arrays.asList("case_id", "activity", "start_date", "completion_time", "process_type");
    private TestUtilities utilities;
    private ParquetFactoryProvider parquetFactoryProvider;
    private SampleLogGenerator sampleLogGenerator;
    private LogReader logReader;

    @Before
    public void init() {
        utilities = new TestUtilities();
        parquetFactoryProvider = new ParquetFactoryProvider();
        sampleLogGenerator = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator();
        logReader = new ParquetLogReaderImpl();
    }

    /**
     * Test {@link SampleLogGenerator} sampling fewer lines than contained in <code>test1-valid.parquet</code>.
     */
    @Test
    public void testSampleParquet_undersample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - undersample");

        String testFile = "/test1-valid.parquet";

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        // Validate result
        assertEquals(PARQUET_EXPECTED_HEADER, sample.getHeader());
        assertEquals(2, sample.getLines().size());
    }

    /**
     * Test {@link SampleLogGenerator} sampling more lines than contained in <code>test1-valid.parquet</code>.
     */
    @Test
    public void testSampleCSV_oversample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - oversample");
        String testFile = "/test1-valid.parquet";

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 5, "UTF-8");

        // Validate result
        assertEquals(PARQUET_EXPECTED_HEADER, sample.getHeader());
        assertEquals(3, sample.getLines().size());
    }

    /**
     * Test {@link ParquetLogReaderImpl} against an valid parquet log <code>test1-valid.parquet</code>.
     */
    @Test
    public void test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid parquet test");

        //Parquet file input
        String testFile = "/test1-valid.parquet";
        String expectedFile = "/test1-expected-parquet.xes";
        // Set up inputs and expected outputs
        String expectedXES = new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        //Export parquet
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
     * Test {@link ParquetLogReaderImpl} against an invalid parquet log <code>test3-invalid-end-timestamp.parquetv</code>.
     */
    @Test
    public void test3_invalid_end_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest3 - Invalid end timestamp");
        //Parquet file input
        String testFile = "/test3-invalid-end-timestamp.parquet";
        String expectedFile = "/test3-expected-parquet.xes";
        // Set up inputs and expected outputs
        String expectedXES = new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        //Export parquet
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
     * Test {@link ParquetLogReaderImpl} against an invalid parquet log <code>test4-invalid-start-timestamp.parquet</code>.
     */
    @Test
    public void test4_invalid_start_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest4 - Invalid start timestamp");
        //Parquet file input
        String testFile = "/test4-invalid-start-timestamp.parquet";
        String expectedFile = "/test4-expected-parquet.xes";
        // Set up inputs and expected outputs
        String expectedXES = new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        //Export parquet
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
     * Test {@link ParquetLogReaderImpl} against an invalid parquet log <code>test5-empty-caseID.parquet</code>.
     */
    @Test
    public void test5_empty_caseID() throws Exception {

        LOGGER.info("\n************************************\ntest5 - Empty caseID");
        //Parquet file input
        String testFile = "/test5-empty-caseID.parquet";
        String expectedFile = "/test5-expected-parquet.xes";
        // Set up inputs and expected outputs
        String expectedXES = new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        //Export parquet
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
     * Test {@link ParquetLogReaderImpl} against an invalid parquet log <code>test8-all-invalid.parquet</code>.
     */
    @Test
    public void test7_all_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest7 - All invalid");
        //Parquet file input
        String testFile = "/test8-all-invalid.parquet";

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        //Export parquet
        LogModel logModel = logReader
                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(0, logModel.getRowsCount());
        assertEquals(3, logModel.getLogErrorReport().size());
    }

    /**
     * Test {@link ParquetLogReaderImpl} against an invalid parquet log <code>test9-differentiate-dates.parquet</code>.
     */
    @Test
    public void test8_differentiate_dates() throws Exception {

        LOGGER.info("\n************************************\ntest8 - Differentiate dates");
        //Parquet file input
        String testFile = "/test9-differentiate-dates.parquet";
        String expectedFile = "/test9-expected-parquet.xes";
        // Set up inputs and expected outputs
        String expectedXES = new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        sample.setEndTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sample.setStartTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sample.setEndTimestampPos(3);
        sample.setStartTimestampPos(2);
        sample.getEventAttributesPos().remove(Integer.valueOf(2));
        sample.getEventAttributesPos().remove(Integer.valueOf(3));

        //Export parquet
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
     * Test {@link ParquetLogReaderImpl} against an invalid parquet log <code>test10-eventAttribute.parquet</code>.
     */
    @Test
    public void test9_detect_name() throws Exception {

        LOGGER.info("\n************************************\ntest9 - Event Attribute");
        //Parquet file input
        String testFile = "/test10-eventAttribute.parquet";
        String expectedFile = "/test10-expected-parquet.xes";
        // Set up inputs and expected outputs
        String expectedXES = new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        //Export parquet
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
     * Test {@link ParquetLogReaderImpl} against an invalid parquet log <code>test11-encoding.parquet</code>.
     */
    @Test
    public void test10_encoding() throws Exception {

        LOGGER.info("\n************************************\ntest10 - Encoding");
        //Parquet file input
        String testFile = "/test11-encoding.parquet";
        String expectedFile = "/test11-expected-parquet.xes";
        // Set up inputs and expected outputs
        String expectedXES = new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "windows-1255");

        sample.setActivityPos(1);
        sample.getEventAttributesPos().remove(Integer.valueOf(1));
        //Export parquet
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
