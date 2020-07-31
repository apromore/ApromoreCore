package org.apromore.service.csvimporter.io;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.*;
import java.nio.charset.Charset;

public class CSVFileReader {
    public static CSVReader csvFileReader(File file, String charset, char delimiter) throws FileNotFoundException {
        return new CSVReaderBuilder(new InputStreamReader(new FileInputStream(file), Charset.forName(charset)))
                .withSkipLines(0)
                .withCSVParser((new RFC4180ParserBuilder())
                        .withSeparator(delimiter)
                        .build())
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .build();
    }
}
