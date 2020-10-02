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
import org.apromore.service.csvimporter.services.XLSXToParquetExporterUnitTest;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class XLSXLogReaderImplUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(XLSXLogReaderImplUnitTest.class);
    ParquetFactoryProvider parquetFactoryProvider = new ParquetFactoryProvider();
    private LogReader logReader = new XLSXLogReaderImpl();

    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", "process type");

    private static String toString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        return baos.toString();
    }

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
     * Test {@link XLSXLogReaderImpl } to convert to CSVReader.
     */
    @Test
    public void test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid parquet test");

        //Xlsx file input
        String testFile = "/test1-valid.xlsx";

        String expectedXES = correctTimeZone(new String(ByteStreams.toByteArray(XLSXToParquetExporterUnitTest.class.getResourceAsStream("/test1-expected.xes")), Charset.forName("utf-8")), "\\+03:00");

        //Generate sample
        LogSample sample = parquetFactoryProvider
                .getParquetFactory("xlsx")
                .createSampleLogGenerator()
                .generateSampleLog(XLSXToParquetExporterUnitTest.class.getResourceAsStream(testFile), 3, "UTF-8");

        LogModel logModel = logReader.readLogs(XLSXToParquetExporterUnitTest.class.getResourceAsStream(testFile), sample, "UTF-8", false);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));
    }
}
