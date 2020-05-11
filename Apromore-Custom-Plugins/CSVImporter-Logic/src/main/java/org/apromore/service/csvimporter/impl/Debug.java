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
        System.out.println(parse.tryParsingWithFormat("1/1/16 20:51", "ssss"));
        System.out.println(parse.tryParsing("2019-10-14 08:09"));

    }
}
