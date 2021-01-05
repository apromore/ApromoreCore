/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.service.csvimporter.dateparser;

import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.apromore.service.csvimporter.services.MetaDataService;
import org.apromore.service.csvimporter.services.legacy.LogImporter;
import org.apromore.service.csvimporter.services.legacy.LogImporterXLSXImpl;
import org.apromore.service.csvimporter.services.utilities.TestUtilities;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.dateparser.DateUtil.determineDateFormat;
import static org.apromore.service.csvimporter.dateparser.DateUtil.parseToTimestamp;

@Ignore
public class DebugUnitTest {
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", "process type");
    private TestUtilities utilities;
    private ParquetFactoryProvider parquetFactoryProvider;
    private MetaDataService metaDataService;
    private LogImporter logImporter;

    @Before
    public void init() {
        utilities = new TestUtilities();
        parquetFactoryProvider = new ParquetFactoryProvider();
        metaDataService = parquetFactoryProvider
//                .getParquetFactory("csv")
                .getParquetFactory("xlsx")
                .getMetaDataService();
//        logImporter = new LogImporterCSVImpl();

        logImporter = new LogImporterXLSXImpl();
    }

    @Test
    public void test_debug() throws Exception {

        System.out.println("\n************************************\ntest");

//        String testFile = "/TEST.xlsx";
//        LogMetaData sample = metaDataService
//                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");
//        System.out.println("getEndTimestampFormat " + sample.getEndTimestampFormat());
//
//        LogModel logModel = logImporter
//                .readLogs(this.getClass().getResourceAsStream(testFile), sample, "UTF-8", true);
//
//        System.out.println("getRowsCount " + logModel.getRowsCount());
//        System.out.println("getLogErrorReport " + logModel.getLogErrorReport());


//        System.out.println("Parsing " + parseToTimestamp("9/13/17 12:20", "dd/MM/yy HH:mm"));

//dd-MM-yyyy HH:mm:ss tt
        String dattime = "19/12/2019 08:13:05AM";
////        String dattime = "03/09/2019 15:13:05";

        if (determineDateFormat(dattime) != null) {
            System.out.println("Parsing " + determineDateFormat(dattime));
            Timestamp timestamp = parseToTimestamp(dattime, determineDateFormat(dattime), null);
            System.out.println("timestamp " + timestamp.toString());

        } else {
            System.out.println("Parsing Null");

        }
        System.out.println("Parsing " + dattime);

//
//        System.out.println("Parsing " + dattime.matches("^(\\d{4}(0[0-9]|
//        1[0-2])([0-2][0-9]|3[0-1])([0-1][0-9]|2[0-4])(([0-5][0-9]){2}))"));
//        System.out.println("Parsing " + dattime.matches("^(\\d{4}(0[1-9]|1[0-2])([0-2][0-9]|3[0-1])([0-1][0-9]|2[0-4])([0-5][0-9]){2})$"));

        System.out.println("\n************************************\n");

    }

}
