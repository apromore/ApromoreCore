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
package org.apromore.service.csvimporter.services;

import com.google.common.io.ByteStreams;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.io.ParquetLocalFileReader;
import org.apromore.service.csvimporter.model.LogModel;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.legecy.LogReaderImplUnitTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.apromore.service.csvimporter.services.Utilities.convertParquetToCSV;
import static org.apromore.service.csvimporter.utilities.ParquetUtilities.createParquetSchema;
import static org.junit.Assert.assertEquals;

public class ParquetToParquetExporterUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParquetToParquetExporterUnitTest.class);
    ParquetFactoryProvider parquetFactoryProvider = new ParquetFactoryProvider();
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("caseID", "activity", "startTimestamp", "endTimestamp", "processtype");

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
     * Test {@link SampleLogGenerator.generateSampleLog} sampling fewer lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleParquet_undersample() throws Exception {

        String testFile = "/test1-valid.parquet";
        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);

        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 2, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, sample.getHeader());
        assertEquals(2, sample.getLines().size());
    }

    /**
     * Test {@link SampleLogGenerator.generateSampleLog} sampling more lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleCSV_oversample() throws Exception {
        String testFile = "/test1-valid.parquet";
        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);

        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 5, "UTF-8");

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, sample.getHeader());
        assertEquals(3, sample.getLines().size());
    }

    /**
     * Test {@link ParquetToParquetExporter.generateParqeuetFile } to convert to CSVReader.
     */
    @Test
    public void test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid parquet test");

        //Parquet file input
        String testFile = "/test1-valid.parquet";
        String expectedTestFile = "/test1-valid-expected.csv";

        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "UTF-8",
                        tempOutput,
                        true);


        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        String parquetToCSV = convertParquetToCSV(tempOutput, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, schema);
        assertEquals(expectedCsv, parquetToCSV);

    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void test2_missing_columns() throws Exception {

        System.out.println("\n************************************\ntest2 - Missing columns test");
        //Parquet file input
        String testFile = "/test2-missing-columns.parquet";
        String expectedTestFile = "/test2-missing-columns-expected.csv";

        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "UTF-8",
                        tempOutput,
                        true);


        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        String parquetToCSV = convertParquetToCSV(tempOutput, ',');

        // Validate result
//        assertEquals(2, parquetFileReader.getRecordCount());
//        assertEquals(1, logModel.getLogErrorReport().size());
//        assertEquals(false, logModel.isRowLimitExceeded());
//        assertEquals(expectedParquetSchema, schema);
//        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void test3_invalid_end_timestamp() throws Exception {

        System.out.println("\n************************************\ntest3 - Invalid end timestamp");
        //Parquet file input
        String testFile = "/test3-invalid-end-timestamp.parquet";
        String expectedTestFile = "/test3-invalid-end-timestamp-expected.csv";

        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 2, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "UTF-8",
                        tempOutput,
                        true);


        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        String parquetToCSV = convertParquetToCSV(tempOutput, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, schema);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void test4_invalid_start_timestamp() throws Exception {

        System.out.println("\n************************************\ntest4 - Invalid start timestamp");
        //Parquet file input
        String testFile = "/test4-invalid-start-timestamp.parquet";
        String expectedTestFile = "/test4-invalid-start-timestamp-expected.csv";

        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 2, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "UTF-8",
                        tempOutput,
                        true);


        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        String parquetToCSV = convertParquetToCSV(tempOutput, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, schema);
        assertEquals(expectedCsv, parquetToCSV);
    }
    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void test5_empty_caseID() throws Exception {

        System.out.println("\n************************************\ntest5 - Empty caseID");
        //Parquet file input
        String testFile = "/test5-empty-caseID.parquet";
        String expectedTestFile = "/test5-empty-caseID-expected.csv";

        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "UTF-8",
                        tempOutput,
                        true);


        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        String parquetToCSV = convertParquetToCSV(tempOutput, ',');

        // Validate result
        assertEquals(2, parquetFileReader.getRecordCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, schema);
        assertEquals(expectedCsv, parquetToCSV);
    }
    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void test7_all_invalid() throws Exception {

        System.out.println("\n************************************\ntest7 - All invalid");
        //Parquet file input
        String testFile = "/test8-all-invalid.parquet";
        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 2, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "UTF-8",
                        tempOutput,
                        true);


        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        // Validate result
        assertEquals(0, parquetFileReader.getRecordCount());
        assertEquals(3, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, schema);
    }
    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void test8_differentiate_dates() throws Exception {

        System.out.println("\n************************************\ntest8 - Differentiate dates");
        //Parquet file input
        String testFile = "/test9-differentiate-dates.parquet";
        String expectedTestFile = "/test9-differentiate-dates-expected.csv";

        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 100, "UTF-8");

        sample.setEndTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setStartTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setEndTimestampPos(3);
        sample.setStartTimestampPos(2);
        sample.getEventAttributesPos().remove(Integer.valueOf(2));
        sample.getEventAttributesPos().remove(Integer.valueOf(3));

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "UTF-8",
                        tempOutput,
                        true);

        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        String parquetToCSV = convertParquetToCSV(tempOutput, ',');

        // Validate result
        assertEquals(13, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, schema);
        assertEquals(expectedCsv, parquetToCSV);
    }
    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void test9_detect_name() throws Exception {

        System.out.println("\n************************************\ntest9 - Event Attribute");
        //Parquet file input
        String testFile = "/test10-eventAttribute.parquet";
        String expectedTestFile = "/test10-eventAttribute-expected.csv";

        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "UTF-8",
                        tempOutput,
                        true);


        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        String parquetToCSV = convertParquetToCSV(tempOutput, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, schema);
        assertEquals(expectedCsv, parquetToCSV);
    }
    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void test10_encoding() throws Exception {

        System.out.println("\n************************************\ntest10 - Encoding");
        //Parquet file input
        String testFile = "/test11-encoding.parquet";
        String expectedTestFile = "/test11-encoding-expected.csv";

        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 100, "windows-1255");

        sample.setActivityPos(1);
        sample.getEventAttributesPos().remove(Integer.valueOf(1));

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("parquet")
                .createParquetExporter()
                .generateParqeuetFile(
                        in,
                        sample,
                        "windows-1255",
                        tempOutput,
                        true);


        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempOutput);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetFileReader parquetFileReader = parquetLocalFileReader.getParquetFileReader();

        String parquetToCSV = convertParquetToCSV(tempOutput, '¸');

        // Validate result
        assertEquals(5, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedCsv, parquetToCSV);
    }
}
