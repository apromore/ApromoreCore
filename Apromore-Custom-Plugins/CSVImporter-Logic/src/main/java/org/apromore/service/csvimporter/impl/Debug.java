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
package org.apromore.service.csvimporter.impl;

import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Debug {

    public static void main(String[] arg) throws Exception {

//        Reader reader = Files.newBufferedReader(Paths.get("D:\\Apromore\\logs\\CSV Test Logs\\Loan origination_1K.csv"));
//        CSVReader csvReader = new CSVReader(reader);
//
//        CSVImporterLogicImpl cl = new CSVImporterLogicImpl();

//        LogSample sample = cl.sampleCSV(csvReader, 100);
//        System.out.println(sample.getHeader());
//        List<String> header = Arrays.asList(csvReader.readNext());
//
//        Pattern pattern = Pattern.compile("^case$");
//        String value = header.get(0).replace("\uFEFF","").toLowerCase();
//        Matcher match = pattern.matcher(value);
//        System.out.println(header.get(0).toCharArray());
//        char[] c_arr = header.get(0).toCharArray();
//        System.out.println(match.find());

//        System.out.println(DateParserUtils.parseCalendar("1100000123"));
//        System.out.println(new Timestamp(DateParserUtils.parseCalendar(" 20.05.2011 10:20:30.922").getTimeInMillis()));


        Parse parse = new Parse();
        System.out.println(parse.getPreferMonthFirst());
        System.out.println(parse.tryParsingWithFormat("1/1/16 20:51", "ssss"));
        System.out.println(parse.tryParsing("2019-10-14 08:09"));
        System.out.println(parse.tryParsing("25/06/2020 12:16:05"));
        System.out.println(parse.tryParsing("06/01/2022 12:16:05"));
        System.out.println(parse.tryParsing("06/05/2022 12:16:05"));

    }
}
