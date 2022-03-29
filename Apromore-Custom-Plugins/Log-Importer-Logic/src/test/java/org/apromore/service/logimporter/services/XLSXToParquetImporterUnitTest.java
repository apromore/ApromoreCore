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

package org.apromore.service.logimporter.services;

import static org.apromore.service.logimporter.services.utilities.TestUtilities.convertParquetToCsv;
import static org.apromore.service.logimporter.utilities.ParquetUtilities.getHeaderFromParquet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.logimporter.io.ParquetLocalFileReader;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.LogModel;
import org.apromore.service.logimporter.services.utilities.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class XLSXToParquetImporterUnitTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(XLSXToParquetImporterUnitTest.class);
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private static final List<String> SAMPLE_EXPECTED_HEADER =
        Arrays.asList("case id", "activity", "start date", "completion time", "process type");
    private static final List<String> PARQUET_EXPECTED_HEADER =
        Arrays.asList("case_id", "activity", "start_date", "completion_time", "process_type");

    private TestUtilities utilities;
    private MetaDataService metaDataService;
    private ParquetImporter parquetImporter;
    private MetaDataUtilities metaDataUtilities;

    @BeforeEach
    void init() {
        utilities = new TestUtilities();
        ParquetImporterFactory parquetImporterFactory = new ParquetFactoryProvider().getParquetFactory("xlsx");
        metaDataService = parquetImporterFactory.getMetaDataService();
        metaDataUtilities = parquetImporterFactory.getMetaDataUtilities();
        parquetImporter = parquetImporterFactory.getParquetImporter();
    }

    /**
     * Test {@link MetaDataService} sampling fewer lines than contained in <code>test1-valid.xlsx</code>.
     */
    @Test
    void testSampleCSV_undersample() throws Exception {

        LOGGER.info(
            "\n************************************\ntest - sampling fewer lines than contained in test1-valid.xlsx");

        // Test file data
        String testFile = "/test1-valid.xlsx";
        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);

        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        // Validate result
        assertEquals(SAMPLE_EXPECTED_HEADER, logMetaData.getHeader());
        assertEquals(2, sampleLog.size());
    }

    /**
     * Test {@link MetaDataService} sampling more lines than contained in <code>test1-valid.xlsx</code>.
     */
    @Test
    void testSampleCSV_oversample() throws Exception {

        LOGGER.info(
            "\n************************************\ntest - sampling more lines than contained in test1-valid.xlsx");

        // Test file data
        String testFile = "/test1-valid.xlsx";
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);

        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(this.getClass().getResourceAsStream(testFile), 5, "UTF-8");

        // Validate result
        assertEquals(SAMPLE_EXPECTED_HEADER, logMetaData.getHeader());
        assertEquals(3, sampleLog.size());
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an valid xlsx log <code>test1-valid.xlsx</code>.
     */
    @Test
    void test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid parquet test");

        // Test file data
        String testFile = "/test1-valid.xlsx";
        String expectedTestFile = "/test1-valid-expected.csv";
        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");
        final String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                3,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        //Read Parquet file
        final MessageType schema = new ParquetLocalFileReader(new Configuration(true), tempOutput).getSchema();
        final String parquetToCSV = convertParquetToCsv(tempOutput, ',');

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an invalid xlsx log <code>test2-missing-columns.xlsx</code>.
     */
    @Test
    void testPrepareXesModel_test2_missing_columns() throws Exception {

        System.out.println("\n************************************\ntest2 - Missing columns test");

        String testFile = "/test2-missing-columns.xlsx";
        String expectedFile = "/test2-missing-columns-expected.csv";
        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        final String expectedCsv = TestUtilities.resourceToString(expectedFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                3,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        //Read Parquet file
        final MessageType schema = new ParquetLocalFileReader(new Configuration(true), tempOutput).getSchema();
        final String parquetToCSV = convertParquetToCsv(tempOutput, ',');

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an invalid xlsx log <code>test3-invalid-end-timestamp.xlsx</code>.
     */
    @Test
    void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        System.out.println("\n************************************\ntest3 - Invalid end timestamp");

        String testFile = "/test3-invalid-end-timestamp.xlsx";
        String expectedFile = "/test3-invalid-end-timestamp-expected.csv";
        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        final String expectedCsv = TestUtilities.resourceToString(expectedFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                2,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        //Read Parquet file
        final MessageType schema = new ParquetLocalFileReader(new Configuration(true), tempOutput).getSchema();
        final String parquetToCSV = convertParquetToCsv(tempOutput, ',');

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an invalid xlsx log <code>test4-invalid-start-timestamp.xlsx</code>.
     */
    @Test
    void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        System.out.println("\n************************************\ntest4 - Invalid start timestamp");

        String testFile = "/test4-invalid-start-timestamp.xlsx";
        String expectedFile = "/test4-invalid-start-timestamp-expected.csv";
        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        final String expectedCsv = TestUtilities.resourceToString(expectedFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                2,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        //Read Parquet file
        final MessageType schema = new ParquetLocalFileReader(new Configuration(true), tempOutput).getSchema();
        final String parquetToCSV = convertParquetToCsv(tempOutput, ',');

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an invalid xlsx log <code>test5-expected.xlsx</code>.
     */
    @Test
    void testPrepareXesModel_test5_empty_caseID() throws Exception {

        System.out.println("\n************************************\ntest5 - Empty caseID");

        String testFile = "/test5-empty-caseID.xlsx";
        String expectedFile = "/test5-empty-caseID-expected.csv";
        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        final String expectedCsv = TestUtilities.resourceToString(expectedFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                2,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        //Read Parquet file
        final MessageType schema = new ParquetLocalFileReader(new Configuration(true), tempOutput).getSchema();
        final String parquetToCSV = convertParquetToCsv(tempOutput, ',');

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an invalid xlsx log <code>test7-record-invalid.xlsx</code>.
     */
    @Test
    void testPrepareXesModel_test6_record_invalid() throws Exception {

        System.out.println("\n************************************\ntest6 - Record invalid");

        String testFile = "/test7-record-invalid.xlsx";
        String expectedFile = "/test7-record-invalid-expected.csv";
        //Create an output parquet file
        final File tempOutput = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        final String expectedCsv = TestUtilities.resourceToString(expectedFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                100,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        logMetaData.setStartTimestampPos(2);
        logMetaData.setStartTimestampFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        logMetaData.getCaseAttributesPos().remove(Integer.valueOf(2));

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        //Read Parquet file
        final MessageType schema = new ParquetLocalFileReader(new Configuration(true), tempOutput).getSchema();
        final String parquetToCSV = convertParquetToCsv(tempOutput, ',');

        // Validate result
        assertNotNull(logModel);
        assertEquals(1, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link ParquetImporterXLSXImpl } against an invalid xlsx log <code>test8-all-invalid.xlsx</code>.
     */
    @Test
    void testPrepareXesModel_test7_all_invalid() throws Exception {

        System.out.println("\n************************************\ntest7 - All invalid");
        String testFile = "/test8-all-invalid.xlsx";
        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                2,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        // Validate result
        assertNotNull(logModel);
        assertEquals(0, logModel.getRowsCount());
        assertEquals(3, logModel.getLogErrorReport().size());
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an invalid xlsx log <code>test9-differentiate-dates.xlsx</code>.
     */
    @Test
    void testPrepareXesModel_test8_differentiate_dates() throws Exception {

        System.out.println("\n************************************\ntest8 - Differentiate dates");

        String testFile = "/test9-differentiate-dates.xlsx";
        String expectedFile = "/test9-differentiate-dates-expected.csv";
        //Create an output parquet file
        final File tempOutput = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        final String expectedCsv = TestUtilities.resourceToString(expectedFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                100,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");
        logMetaData.setEndTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        logMetaData.setStartTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        logMetaData.setEndTimestampPos(3);
        logMetaData.setStartTimestampPos(2);
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(2));
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(3));

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        //Read Parquet file
        final MessageType schema = new ParquetLocalFileReader(new Configuration(true), tempOutput).getSchema();
        final String parquetToCSV = convertParquetToCsv(tempOutput, ',');

        assertNotNull(logModel);
        assertEquals(13, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an invalid xlsx log <code>test10-eventAttribute.xlsx</code>.
     */
    @Test
    void testPrepareXesModel_test9_detect_name() throws Exception {

        System.out.println("\n************************************\ntest9 - Event Attribute");

        String testFile = "/test10-eventAttribute.xlsx";
        String expectedFile = "/test10-eventAttribute-expected.csv";
        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        final String expectedCsv = TestUtilities.resourceToString(expectedFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "UTF-8", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                100,
                "UTF-8");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "UTF-8",
                tempOutput,
                true);

        //Read Parquet file
        final MessageType schema = new ParquetLocalFileReader(new Configuration(true), tempOutput).getSchema();
        final String parquetToCSV = convertParquetToCsv(tempOutput, ',');

        // Validate result
        assertNotNull(logModel);
        assertEquals(6, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link ParquetImporterXLSXImpl} against an invalid xlsx log <code>test11-encoding.xlsx</code>.
     */
    @Disabled
    @Test
    void testPrepareXesModel_test10_encoding() throws Exception {

        System.out.println("\n************************************\ntest10 - Encoding");

        String testFile = "/test11-encoding.xlsx";
        String expectedFile = "/test11-encoding-expected.csv";
        //Create an output parquet file
        final File tempOutput = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        final String expectedCsv = TestUtilities.resourceToString(expectedFile);

        // Perform the test
        LogMetaData logMetaData = metaDataService
            .extractMetadata(this.getClass().getResourceAsStream(testFile), "windows-1255", null);
        List<List<String>> sampleLog = metaDataService
            .generateSampleLog(
                this.getClass().getResourceAsStream(testFile),
                3,
                "windows-1255");
        logMetaData = metaDataUtilities.processMetaData(logMetaData, sampleLog);
        //logMetaData.setTimeZone("Australia/Melbourne");

        logMetaData.setActivityPos(1);
        logMetaData.getEventAttributesPos().remove(Integer.valueOf(1));
        logMetaData.setEndTimestampFormat("MM/dd/yy HH:mm");

        //Export parquet
        LogModel logModel = parquetImporter
            .importParquetFile(
                this.getClass().getResourceAsStream(testFile),
                logMetaData,
                "windows-1255",
                tempOutput,
                true);

        //Read Parquet file
        final String parquetToCSV = convertParquetToCsv(tempOutput, '¸');

        // Validate result
        assertNotNull(logModel);
        assertEquals(5, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(expectedCsv, parquetToCSV);
    }
}
