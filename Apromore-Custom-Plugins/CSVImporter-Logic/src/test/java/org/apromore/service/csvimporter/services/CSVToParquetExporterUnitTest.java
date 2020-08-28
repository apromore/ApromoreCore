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
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.model.LogModel;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.legecy.LogReaderImplUnitTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.apache.parquet.format.converter.ParquetMetadataConverter.NO_FILTER;
import static org.apromore.service.csvimporter.services.Utilities.convertParquetToCSV;
import static org.apromore.service.csvimporter.utilities.ParquetUtilities.createParquetSchema;
import static org.junit.Assert.assertEquals;

public class CSVToParquetExporterUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVToParquetExporterUnitTest.class);
    ParquetFactoryProvider parquetFactoryProvider = new ParquetFactoryProvider();
    final String parquetDir = "src/test/resources";

    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", "process type");


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

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile } to convert to CSVReader.
     */
    @Test
    public void testPrepareXesModel_test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid csv test ");

        //CSV file input
        String testFile = "/test1-valid.csv";
        String expectedTestFile = "/test1-valid-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);
        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);

    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test2_missing_columns() throws Exception {

        System.out.println("\n************************************\ntest2 - Missing columns test");
        //CSV file input
        String testFile = "/test2-missing-columns.csv";
        String expectedTestFile = "/test2-missing-columns-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(2, parquetFileReader.getRecordCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        System.out.println("\n************************************\ntest3 - Invalid end timestamp");

        //CSV file input
        String testFile = "/test3-invalid-end-timestamp.csv";
        String expectedTestFile = "/test3-invalid-end-timestamp-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 2, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        System.out.println("\n************************************\ntest4 - Invalid start timestamp");

        //CSV file input
        String testFile = "/test4-invalid-start-timestamp.csv";
        String expectedTestFile = "/test4-invalid-start-timestamp-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 2, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);
    }


    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test5_empty_caseID() throws Exception {

        System.out.println("\n************************************\ntest5 - Empty caseID");
        //CSV file input
        String testFile = "/test5-empty-caseID.csv";
        String expectedTestFile = "/test5-empty-caseID-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(2, parquetFileReader.getRecordCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test6_different_delimiters() throws Exception {

        System.out.println("\n************************************\ntest6 - different delimiters");
        //CSV file input
        String testFile = "/test6-different-delimiters.csv";
        String expectedTestFile = "/test6-different-delimiters-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);
    }


    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test7_record_invalid() throws Exception {

        System.out.println("\n************************************\ntest7 - Record invalid");
        //CSV file input
        String testFile = "/test7-record-invalid.csv";
        String expectedTestFile = "/test7-record-invalid-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 100, "UTF-8");

        sample.setStartTimestampPos(2);
        sample.getCaseAttributesPos().remove(Integer.valueOf(2));

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(1, parquetFileReader.getRecordCount());
        assertEquals(2, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);
    }


    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test8_all_invalid() throws Exception {

        System.out.println("\n************************************\ntest8 - All invalid");
        //CSV file input
        String testFile = "/test8-all-invalid.csv";

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 2, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        // Validate result
        assertEquals(0, parquetFileReader.getRecordCount());
        assertEquals(3, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test9_differentiate_dates() throws Exception {

        System.out.println("\n************************************\ntest9 - Differentiate dates");
        //CSV file input
        String testFile = "/test9-differentiate-dates.csv";
        String expectedTestFile = "/test9-differentiate-dates-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 100, "UTF-8");

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
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(13, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test10_detect_name() throws Exception {

        System.out.println("\n************************************\ntest10 - Event Attribute");
        //CSV file input
        String testFile = "/test10-eventAttribute.csv";
        String expectedTestFile = "/test10-eventAttribute-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");


        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 100, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, parquetSchema);
        assertEquals(expectedCsv, parquetToCSV);

    }

    /**
     * Test {@link CSVToParqeutExporter.generateParqeuetFile} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test11_encoding() throws Exception {

        System.out.println("\n************************************\ntest11 - Encoding");
        //CSV file input
        String testFile = "/test11-encoding.csv";
        String expectedTestFile = "/test11-encoding-expected.csv";
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");


        //Create an output parquet file
        File outputParquet = new File(parquetDir + testFile.replace(".csv", ".parquet"));
        if (outputParquet.exists())
            outputParquet.delete();

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator()
                .generateSampleLog(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile), 100, "windows-1255");
        sample.setActivityPos(1);
        sample.getEventAttributesPos().remove(Integer.valueOf(1));

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);

        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter()
                .generateParqeuetFile(CSVToParquetExporterUnitTest.class.getResourceAsStream(testFile),
                        sample,
                        "windows-1255",
                        outputParquet,
                        true);

        //Read Parquet file
        Configuration conf = new Configuration(true);

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(outputParquet.toURI()), NO_FILTER);
        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(outputParquet.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

        String parquetToCSV = convertParquetToCSV(outputParquet, '¸');

        // Validate result
        assertEquals(5, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedCsv, parquetToCSV);
    }
}