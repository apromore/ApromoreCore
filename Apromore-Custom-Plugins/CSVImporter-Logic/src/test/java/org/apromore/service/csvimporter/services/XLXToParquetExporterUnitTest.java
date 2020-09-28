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
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.io.ParquetLocalFileReader;
import org.apromore.service.csvimporter.model.LogModel;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.legecy.LogReaderImplUnitTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;

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

public class XLXToParquetExporterUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVToParquetExporterUnitTest.class);
    ParquetFactoryProvider parquetFactoryProvider = new ParquetFactoryProvider();

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
     * Test {@link XLSToParquetExporterLegecy } to convert to CSVReader.
     */
    @Test
    public void test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid parquet test");

        //Parquet file input
        String testFile = "/test1-valid.xlsx";
        String expectedTestFile = "/test1-valid-expected.csv";

        InputStream in = XLXToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        String expectedCsv = correctTimeZone(new String(ByteStreams.toByteArray(LogReaderImplUnitTest.class.getResourceAsStream(expectedTestFile))), "\\+03:00");

        //Create an output parquet file
        File tempOutput = File.createTempFile("test", "parquet");
//
        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("xlsx")
                .createSampleLogGenerator()
                .generateSampleLog(in, 3, "UTF-8");

        //Construct an expected schema
        MessageType expectedParquetSchema = createParquetSchema(TEST1_EXPECTED_HEADER.toArray(new String[0]), sample);
        in = XLXToParquetExporterUnitTest.class.getResourceAsStream(testFile);
        //Export parquet
        LogModel logModel = parquetFactoryProvider
                .getParquetFactory("xlsx")
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

        System.out.println("parquetToCSV " + parquetToCSV);
        // Validate result
        assertEquals(3, parquetFileReader.getRecordCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedParquetSchema, schema);
        assertEquals(expectedCsv, parquetToCSV);
    }
}
