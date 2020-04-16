/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

//package org.apromore.plugin.portal.CSVImporterPortal;
//
//import com.opencsv.CSVReader;
//
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//import org.apromore.service.csvimporter.CSVImporterLogic;
//import org.apromore.service.csvimporter.impl.LogModel;
//import org.deckfour.xes.model.XLog;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import javax.inject.Inject;
//
/////** Test suite for {@link CsvToXes}. */
//public class CsvToXesUnitTest {
//    @Inject
//    private CSVImporterLogic csvImporterLogic;
//    /** Test with <code>CallcenterExample.csv</code> */
//    @Ignore
//    @Test
//    public void testCreateXLog_CallcenterExample() throws IOException {
//        testCreateXLog("CallcenterExample.csv");
//    }
//
//    /** Test with <code>PurchasingExample.csv</code> */
//    @Ignore
//    @Test
//    public void testCreateXLog_PurchasingExample() throws IOException {
//        testCreateXLog("PurchasingExample.csv");
//    }
//
//    private void testCreateXLog(String fileName) throws IOException {
//
////        csvImporterLogic c2x = new csvImporterLogic();
//
//        // Read the header and first line to guess column types
//        InputStream in = CsvToXesUnitTest.class.getClassLoader().getResourceAsStream(fileName);
//        CSVReader csvReader = new CSVReader(new InputStreamReader(in));
//
//        String[] header = csvReader.readNext();
//        System.out.println("H: " + Arrays.toString(header));
//        csvImporterLogic.setHeads(header);
//        String[] line = csvReader.readNext();
//
//        System.out.println("L: " + Arrays.toString(line));
////        c2x.setLine(line);
////        c2x.setOtherTimestamps();
//
//        // Convert to XES
//        in = CsvToXesUnitTest.class.getClassLoader().getResourceAsStream(fileName);
//        List<LogModel> traces = csvImporterLogic.prepareXesModel(new InputStreamReader(in));
//        XLog xlog = csvImporterLogic.createXLog(traces);
//    }
//}
