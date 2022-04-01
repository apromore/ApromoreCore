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

package org.apromore.service.logimporter.services.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.io.ByteStreams;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apromore.commons.config.ConfigBean;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.LogModel;
import org.apromore.service.logimporter.services.MetaDataService;
import org.apromore.service.logimporter.services.MetaDataUtilities;
import org.apromore.service.logimporter.services.ParquetFactoryProvider;
import org.apromore.service.logimporter.services.ParquetImporterFactory;
import org.apromore.service.logimporter.services.utilities.TestUtilities;
import org.deckfour.xes.model.XLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ParquetLogImporterCSVImplUnitTest {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(ParquetLogImporterCSVImplUnitTest.class);
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private static final List<String> PARQUET_EXPECTED_HEADER =
        Arrays.asList("case_id", "activity", "start_date", "completion_time", "process_type");
    private TestUtilities utilities;
    private MetaDataService metaDataService;
    private LogImporterParquetImpl logImporter;
    private MetaDataUtilities metaDataUtilities;

    @BeforeEach
    void init() {
        utilities = new TestUtilities();
        ParquetImporterFactory parquetImporterFactory =
            new ParquetFactoryProvider().getParquetFactory("parquet");
        metaDataService = parquetImporterFactory.getMetaDataService();
        metaDataUtilities = parquetImporterFactory.getMetaDataUtilities();
        logImporter = new LogImporterParquetImpl();
        logImporter.config = new ConfigBean();
    }

    /**
     * Test {@link MetaDataService} sampling fewer lines than contained in
     * <code>test1-valid.parquet</code>.
     */
    @Test
    void testSampleParquet_undersample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - undersample");

        String testFile = "/test1-valid.parquet";

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        // Validate result
        assertEquals(PARQUET_EXPECTED_HEADER, logMetaData.getHeader());
        assertEquals(2, sampleLog.size());
    }

    /**
     * Test {@link MetaDataService} sampling more lines than contained in
     * <code>test1-valid.parquet</code>.
     */
    @Test
    void testSampleCSV_oversample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - oversample");
        String testFile = "/test1-valid.parquet";

        // Perform the test
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 5, "UTF-8");

        // Validate result
        assertEquals(PARQUET_EXPECTED_HEADER, logMetaData.getHeader());
        assertEquals(3, sampleLog.size());
    }

    /**
     * Test {@link LogImporterParquetImpl} against an valid parquet log
     * <code>test1-valid.parquet</code>.
     */
    @Test
    void test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid parquet test");

        // Parquet file input
        String testFile = "/test1-valid.parquet";
        String expectedFile = "/test1-expected-parquet.xes";
        // Set up inputs and expected outputs
        final String expectedXES =
            new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)),
                Charset.forName("utf-8"));

        // Perform the test
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        // Export parquet
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Continue with the XES conversion
        final XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertNotNull(xlog);
        assertEquals(utilities.removeTimezone(expectedXES),
            utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogImporterParquetImpl} against an valid Parquet log
     * <code>test1-valid.parquet</code> when upload limiting is in effect.
     */
    @Test
    void testImportLog_maxEventCount() throws Exception {

        // Test file data
        String testFile = "/test1-valid.parquet";

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);

        // Log size below the limit
        logImporter.config.setMaxEventCount(4);
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());

        // Log size exactly equal to the limit
        logImporter.config.setMaxEventCount(3);
        logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile), logMetaData,
            "UTF-8", true, null, null, null);

        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());

        // Log size exceeding the limit
        logImporter.config.setMaxEventCount(2);
        logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile), logMetaData,
            "UTF-8", true, null, null, null);

        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(true, logModel.isRowLimitExceeded());
    }

    /**
     * Test {@link LogImporterParquetImpl} against an invalid parquet log
     * <code>test3-invalid-end-timestamp.parquetv</code>.
     */
    @Test
    void test3_invalid_end_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest3 - Invalid end timestamp");
        // Parquet file input
        String testFile = "/test3-invalid-end-timestamp.parquet";
        String expectedFile = "/test3-expected-parquet.xes";
        // Set up inputs and expected outputs
        final String expectedXES =
            new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)),
                Charset.forName("utf-8"));

        // Perform the test
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        // Export parquet
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Continue with the XES conversion
        final XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(utilities.removeTimezone(expectedXES),
            utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogImporterParquetImpl} against an invalid parquet log
     * <code>test4-invalid-start-timestamp.parquet</code>.
     */
    @Test
    void test4_invalid_start_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest4 - Invalid start timestamp");
        // Parquet file input
        String testFile = "/test4-invalid-start-timestamp.parquet";
        String expectedFile = "/test4-expected-parquet.xes";
        // Set up inputs and expected outputs
        final String expectedXES =
            new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)),
                Charset.forName("utf-8"));

        // Perform the test
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        // Export parquet
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Continue with the XES conversion
        final XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(4, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(utilities.removeTimezone(expectedXES),
            utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogImporterParquetImpl} against an invalid parquet log
     * <code>test5-empty-caseID.parquet</code>.
     */
    @Test
    void test5_empty_caseID() throws Exception {

        LOGGER.info("\n************************************\ntest5 - Empty caseID");
        // Parquet file input
        String testFile = "/test5-empty-caseID.parquet";
        String expectedFile = "/test5-expected-parquet.xes";
        // Set up inputs and expected outputs
        final String expectedXES =
            new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)),
                Charset.forName("utf-8"));

        // Perform the test
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        // Export parquet
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Continue with the XES conversion
        final XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(utilities.removeTimezone(expectedXES),
            utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogImporterParquetImpl} against an invalid parquet log
     * <code>test8-all-invalid.parquet</code>.
     */
    @Test
    void test7_all_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest7 - All invalid");
        // Parquet file input
        String testFile = "/test8-all-invalid.parquet";

        // Perform the test
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        // Export parquet
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Validate result
        assertNotNull(logModel);
        assertEquals(1, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());
    }

    /**
     * Test {@link LogImporterParquetImpl} against an invalid parquet log
     * <code>test9-differentiate-dates.parquet</code>.
     */
    @Test
    void test8_differentiate_dates() throws Exception {

        LOGGER.info("\n************************************\ntest8 - Differentiate dates");
        // Parquet file input
        String testFile = "/test9-differentiate-dates.parquet";
        String expectedFile = "/test9-expected-parquet.xes";
        // Set up inputs and expected outputs
        final String expectedXES =
            new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)),
                Charset.forName("utf-8"));

        // Perform the test
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        logMetaData.setEndTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
        logMetaData.setStartTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
        logMetaData.setEndTimestampPos(3);
        logMetaData.setStartTimestampPos(2);
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(2));
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(3));

        // Export parquet
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Continue with the XES conversion
        final XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(13, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(utilities.removeTimezone(expectedXES),
            utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogImporterParquetImpl} against an invalid parquet log
     * <code>test10-eventAttribute.parquet</code>.
     */
    @Test
    void test9_detect_name() throws Exception {

        LOGGER.info("\n************************************\ntest9 - Event Attribute");
        // Parquet file input
        String testFile = "/test10-eventAttribute.parquet";
        String expectedFile = "/test10-expected-parquet.xes";
        // Set up inputs and expected outputs
        final String expectedXES =
            new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)),
                Charset.forName("utf-8"));

        // Perform the test
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        // Export parquet
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Continue with the XES conversion
        final XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(utilities.removeTimezone(expectedXES),
            utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogImporterParquetImpl} against an invalid parquet log
     * <code>test11-encoding.parquet</code>.
     */
    @Test
    void test10_encoding() throws Exception {

        LOGGER.info("\n************************************\ntest10 - Encoding");
        // Parquet file input
        String testFile = "/test11-encoding.parquet";
        String expectedFile = "/test11-expected-parquet.xes";
        // Set up inputs and expected outputs
        String expectedXES =
            new String(ByteStreams.toByteArray(this.getClass().getResourceAsStream(expectedFile)),
                Charset.forName("utf-8"));

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "windows-1255", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "windows-1255");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        logMetaData.setActivityPos(1);
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(1));

        // Export parquet
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "windows-1255", true, null, null, null);

        // Continue with the XES conversion
        final XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(5, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        // Temporarily comment out following asserting since decoding behaviours differently on Windows
        //    assertEquals(utilities.removeTimezone(expectedXES),
        //        utilities.removeTimezone(utilities.xlogToString(xlog)));
    }
}
