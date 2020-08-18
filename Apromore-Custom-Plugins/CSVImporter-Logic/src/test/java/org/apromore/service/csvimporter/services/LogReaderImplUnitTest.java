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

import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.legecy.LogReader;
import org.apromore.service.csvimporter.services.legecy.LogReaderImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.TimeZone;


public class LogReaderImplUnitTest {


    private LogReader logReader = new LogReaderImpl();

    private static String toString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        return baos.toString();
    }

    /**
     * This is hack to convert the test case XML documents from the time zone where they were created to the
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
     * Test {@link } to convert to parquet.
     */
    @Test
    public void testConvertToParquet() throws Exception {

        System.out.println("\n************************************\ntest1 - Valid csv test ");

        File testFile = new File("/Users/own_strong/Dev/Apromore-prod/7.17/ApromoreEE/ApromoreCore/Apromore-Custom-Plugins/CSVImporter-Logic/src/test/resources/test1-valid.csv");

        ParquetFactoryProvider parquetFactoryProvider = new ParquetFactoryProvider();
        ConvertToParquetFactory convertToParquetFactory = parquetFactoryProvider.getParquetFactory("csv");

        SampleLogGenerator sampleLogGenerator = convertToParquetFactory.createSampleLogGenerator();
        ParquetExporter parquetExporter = convertToParquetFactory.createParquetExporter();

        LogSample sample = sampleLogGenerator.generateSampleLog(new FileInputStream(testFile), 100, "UTF-8");

//        String st = sample == null ? "Null" : "Not Null";
//        System.out.println("---------------------------------------------------");
//        System.out.println("st: " + st);
//        System.out.println("---------------------------------------------------");
//        parquetExporter.generateParqeuetFile(new FileInputStream(testFile), sample,new File("/" + testFile.getName()+".parquet"));

        for (List l : sample.getLines()){
            System.out.println(l);
        }

        System.out.println("\n************************************************************************\n");
    }

    @Test
    public void testMediaToParquet() {
    }
}
