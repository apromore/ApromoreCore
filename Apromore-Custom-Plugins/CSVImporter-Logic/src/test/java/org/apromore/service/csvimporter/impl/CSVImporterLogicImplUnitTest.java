/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.service.csvimporter.impl;

import com.google.common.io.ByteStreams;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.LogModel;
import org.apromore.service.csvimporter.LogSample;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class CSVImporterLogicImplUnitTest {

    /** Expected headers for <code>test1-valid.csv</code>. */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", " process type");

    private CSVImporterLogic csvImporterLogic = new CSVImporterLogicImpl();

    private static CSVReader newCSVReader(String filename, String charset, char delimiter) {
        return new CSVReaderBuilder(new InputStreamReader(CSVImporterLogicImplUnitTest.class.getResourceAsStream(filename), Charset.forName(charset)))
                .withSkipLines(0)
                .withCSVParser((new RFC4180ParserBuilder())
                        .withSeparator(delimiter)
                        .build())
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .build();
    }

    private static String toString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        return baos.toString();
    }

    /**
     * This is hack to convert the test case XML documents from the time zone where they were created to the
     * time zone where the test is running.
     *
     * @param in  the XML text of the test data
     * @param testDataTimezone  a regex for the timezone used in the test data, e.g. <code>"\\+03:00"</code>
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

    /** Test {@link csvImporterLogic.sampleCSV} sampling fewer lines than contained in <code>test1-valid.csv</code>. */
    @Test
    public void testSampleCSV_undersample() throws Exception {
        CSVReader csvReader = newCSVReader("/test1-valid.csv", "utf-8", ',');
        LogSample logSample = csvImporterLogic.sampleCSV(csvReader, 2);

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logSample.getHeader());
        assertEquals(2, logSample.getLines().size());
    }

    /** Test {@link CSVImporterLogic.sampleCSV} sampling more lines than contained in <code>test1-valid.csv</code>. */
    @Test
    public void testSampleCSV_oversample() throws Exception {
        CSVReader csvReader = newCSVReader("/test1-valid.csv", "utf-8", ',');
        LogSample logSample = csvImporterLogic.sampleCSV(csvReader, 5);

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logSample.getHeader());
        assertEquals(3, logSample.getLines().size());
    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against a valid CSV log <code>test1-valid.csv</code>. */
    @Test
    public void testPrepareXesModel_test1_valid() throws Exception {

        System.out.println("\n************************************\ntest1 - Valid csv test ");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test1-valid.csv", "utf-8", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test1-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test1-valid.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test2_missing_columns() throws Exception {

        System.out.println("\n************************************\ntest2 - Missing columns test");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test2-missing-columns.csv", "utf-8", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test2-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test2-missing-columns.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        System.out.println("\n************************************\ntest3 - Invalid end timestamp");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test3-invalid-end-timestamp.csv", "utf-8", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test3-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 2);
        csvReader = newCSVReader("/test3-invalid-end-timestamp.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

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

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        System.out.println("\n************************************\ntest4 - Invalid start timestamp");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test4-invalid-start-timestamp.csv", "utf-8", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test4-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 2);
        csvReader = newCSVReader("/test4-invalid-start-timestamp.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

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


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test5_empty_caseID() throws Exception {

        System.out.println("\n************************************\ntest5 - Empty caseID");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test5-empty-caseID.csv", "utf-8", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test5-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test5-empty-caseID.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        // Validate result
        assertNotNull(logModel);
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test6_different_delimiters() throws Exception {

        System.out.println("\n************************************\ntest6 - different delimiters");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test6-different-delimiters.csv", "utf-8", ';');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test6-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test6-different-delimiters.csv", "utf-8", ';');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

        /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test7_record_invalid() throws Exception {

        System.out.println("\n************************************\ntest7 - Record invalid");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test7-record-invalid.csv", "utf-8", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test7-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        sample.setStartTimestampPos(2);
        sample.getCaseAttributesPos().remove(Integer.valueOf(2));
        csvReader = newCSVReader("/test7-record-invalid.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        // Validate result
        assertNotNull(logModel);
        assertEquals(1, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());


        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

    }


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test8_all_invalid() throws Exception {

        System.out.println("\n************************************\ntest8 - All invalid");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test8-all-invalid.csv", "utf-8", ',');

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 2);
        csvReader = newCSVReader("/test8-all-invalid.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        // Validate result
        assertNotNull(logModel);
        assertEquals(0, logModel.getRowsCount());
        assertEquals(3, logModel.getLogErrorReport().size());
    }


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test9_differentiate_dates() throws Exception {

        System.out.println("\n************************************\ntest9 - Differentiate dates");
        ArrayList<String> dateFormats = new ArrayList();
        String expectedFormat = null;
        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test9-differentiate-dates.csv", "utf-8", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test9-expected.xes")), Charset.forName("utf-8")), "\\+02:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        sample.setEndTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setStartTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setEndTimestampPos(3);
        sample.setStartTimestampPos(2);
        sample.getEventAttributesPos().remove(Integer.valueOf(2));
        sample.getEventAttributesPos().remove(Integer.valueOf(3));
        csvReader = newCSVReader("/test9-differentiate-dates.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        assertNotNull(logModel);
        assertEquals(13, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test10_detect_name() throws Exception {

        System.out.println("\n************************************\ntest10 - Event Attribute");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test10-eventAttribute.csv", "utf-8", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test10-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test10-eventAttribute.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test11_encoding() throws Exception {

        System.out.println("\n************************************\ntest11 - Encoding");


        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test11-encoding.csv", "windows-1255", ',');
        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test11-expected.xes")), Charset.forName("utf-8")), "\\+02:00");

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        sample.setActivityPos(1);
        sample.getEventAttributesPos().remove(Integer.valueOf(1));
        csvReader = newCSVReader("/test11-encoding.csv", "windows-1255", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample);

        // Validate result
        assertNotNull(logModel);
        assertEquals(5, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }
}
