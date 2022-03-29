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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LogImporterCSVImplUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogImporterCSVImplUnitTest.class);
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private static final List<String> TEST1_EXPECTED_HEADER =
        Arrays.asList("case id", "activity", "start date", "completion time", " process type");
    private TestUtilities utilities;
    private MetaDataService metaDataService;
    private LogImporterCSVImpl logImporter;
    private MetaDataUtilities metaDataUtilities;

    @BeforeEach
    void init() {
        utilities = new TestUtilities();
        ParquetImporterFactory parquetImporterFactory =
            new ParquetFactoryProvider().getParquetFactory("csv");
        metaDataService = parquetImporterFactory.getMetaDataService();
        metaDataUtilities = parquetImporterFactory.getMetaDataUtilities();
        logImporter = new LogImporterCSVImpl();
        logImporter.config = new ConfigBean();
    }

    /**
     * Test {@link MetaDataService} sampling fewer lines than contained in
     * <code>test1-valid.csv</code>.
     */
    @Test
    void testSampleCSV_undersample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - undersample");

        // Test file data
        String testFile = "/test1-valid.csv";
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);

        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logMetaData.getHeader());
        assertEquals(2, sampleLog.size());
    }

    /**
     * Test {@link MetaDataService} sampling more lines than contained in
     * <code>test1-valid.csv</code>.
     */
    @Test
    void testSampleCSV_oversample() throws Exception {

        LOGGER.info("\n************************************\ntest sample generator - oversample");

        // Test file data
        String testFile = "/test1-valid.csv";
        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);

        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 5, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logMetaData.getHeader());
        assertEquals(3, sampleLog.size());
    }

    /**
     * Test {@link LogImporterCSVImpl} against an valid CSV log <code>test1-valid.csv</code>.
     */
    @Test
    void testPrepareXesModel_test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid csv test ");

        // Test file data
        String testFile = "/test1-valid.csv";
        String expectedFile = "/test1-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
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
     * Test {@link LogImporterCSVImpl} against an valid CSV log <code>test1-valid.csv</code> when
     * upload limiting is in effect.
     */
    @Test
    @Disabled
    void testImportLog_maxEventCount() throws Exception {

        // Test file data
        String testFile = "/test1-valid.csv";

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
     * Test {@link LogImporterCSVImpl} against an invalid CSV log
     * <code>test2-missing-columns.csv</code>.
     */
    @Test
    void testPrepareXesModel_test2_missing_columns() throws Exception {

        LOGGER.info("\n************************************\ntest2 - Missing columns test");

        // Test file data
        String testFile = "/test2-missing-columns.csv";
        String expectedFile = "/test2-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
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
     * Test {@link LogImporterCSVImpl} against an invalid CSV log
     * <code>test3-invalid-end-timestamp.csv</code>.
     */
    @Test
    void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest3 - Invalid end timestamp");

        // Test file data
        String testFile = "/test3-invalid-end-timestamp.csv";
        String expectedFile = "/test3-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
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
     * Test {@link LogImporterCSVImpl} against an invalid CSV log
     * <code>test4-invalid-start-timestamp.csv</code>.
     */
    @Test
    void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest4 - Invalid start timestamp");

        // Test file data
        String testFile = "/test4-invalid-start-timestamp.csv";
        String expectedFile = "/test4-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
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
     * Test {@link LogImporterCSVImpl} against an invalid CSV log <code>test5-empty-caseID.csv</code>.
     */
    @Test
    void testPrepareXesModel_test5_empty_caseID() throws Exception {

        LOGGER.info("\n************************************\ntest5 - Empty caseID");

        // Test file data
        String testFile = "/test5-empty-caseID.csv";
        String expectedFile = "/test5-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
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
     * Test {@link LogImporterCSVImpl} against an invalid CSV log
     * <code>test6-different-delimiters.csv</code>.
     */
    @Test
    void testPrepareXesModel_test6_different_delimiters() throws Exception {

        LOGGER.info("\n************************************\ntest6 - different delimiters");

        // Test file data
        String testFile = "/test6-different-delimiters.csv";
        String expectedFile = "/test6-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
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
     * Test {@link LogImporterCSVImpl} against an invalid CSV log
     * <code>test7-record-invalid.csv</code>.
     */
    @Test
    void testPrepareXesModel_test7_record_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest7 - Record invalid");

        // Test file data
        String testFile = "/test7-record-invalid.csv";
        String expectedFile = "/test7-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        logMetaData.setStartTimestampPos(2);
        logMetaData.setStartTimestampFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        logMetaData.getCaseAttributesPos().remove(Integer.valueOf(2));
        // logMetaData.setTimeZone("Australia/Melbourne");

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
     * Test {@link LogImporterCSVImpl} against an invalid CSV log <code>test8-all-invalid.csv</code>.
     */
    @Test
    void testPrepareXesModel_test8_all_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest8 - All invalid");

        // Test file data
        String testFile = "/test8-all-invalid.csv";

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Validate result
        assertNotNull(logModel);
        assertEquals(1, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());
    }


    /**
     * Test {@link LogImporterCSVImpl} against an invalid CSV log
     * <code>test9-differentiate-dates.csv</code>.
     */
    @Test
    void testPrepareXesModel_test9_differentiate_dates() throws Exception {

        LOGGER.info("\n************************************\ntest9 - Differentiate dates");

        // Test file data
        String testFile = "/test9-differentiate-dates.csv";
        String expectedFile = "/test9-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        logMetaData.setEndTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        logMetaData.setStartTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        logMetaData.setEndTimestampPos(3);
        logMetaData.setStartTimestampPos(2);
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(2));
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(3));
        // logMetaData.setTimeZone("Australia/Melbourne");

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
     * Test {@link LogImporterCSVImpl} against an invalid CSV log
     * <code>test10-eventAttribute.csv</code>.
     */
    @Test
    void testPrepareXesModel_test10_detect_name() throws Exception {

        LOGGER.info("\n************************************\ntest10 - Event Attribute");

        // Test file data
        String testFile = "/test10-eventAttribute.csv";
        String expectedFile = "/test10-expected.xes";
        // Set up inputs and expected outputs
        final String expectedXES = TestUtilities.resourceToString(expectedFile);

        LogMetaData logMetaData =
            metaDataService.extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        // logMetaData.setTimeZone("Australia/Melbourne");
        LogModel logModel = logImporter.importLog(this.getClass().getResourceAsStream(testFile),
            logMetaData, "UTF-8", true, null, null, null);

        // Continue with the XES conversion
        final XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(logModel);
        assertEquals(6, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertNotNull(xlog);
        assertEquals(utilities.removeTimezone(expectedXES),
            utilities.removeTimezone(utilities.xlogToString(xlog)));
    }

    /**
     * Test {@link LogImporterCSVImpl} against an invalid CSV log <code>test11-encoding.csv</code>.
     */
    @Test
    void testPrepareXesModel_test11_encoding() throws Exception {

        LOGGER.info("\n************************************\ntest11 - Encoding");

        // Test file data
        String testFile = "/test11-encoding.csv";
        String expectedFile = "/test11-expected.xes";
        // Set up inputs and expected outputs
        String expectedXES = TestUtilities.resourceToString(expectedFile);


        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "windows-1255", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        logMetaData.setActivityPos(1);
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(1));
        // logMetaData.setTimeZone("Australia/Melbourne");

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
