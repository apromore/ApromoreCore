package org.apromore.service.csvimporter.io;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apromore.service.csvimporter.utilities.InvalidCSVException;
import org.zkoss.zul.Messagebox;

import java.io.*;

import static org.apromore.service.csvimporter.constants.Constants.supportedSeparators;

public class CSVFileReader {
//    public static CSVReader csvFileReader(File file, String charset, char delimiter) throws FileNotFoundException {
//        return new CSVReaderBuilder(new InputStreamReader(new FileInputStream(file), Charset.forName(charset)))
//                .withSkipLines(0)
//                .withCSVParser((new RFC4180ParserBuilder())
//                        .withSeparator(delimiter)
//                        .build())
//                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
//                .build();
//    }

    public CSVReader newCSVReader(InputStream in, String charset) {
        try{
            // Guess at ethe separator character
            Reader reader = new InputStreamReader(in, charset);

//            InputStream in = media.isBinary() ? media.getStreamData() : new ByteArrayInputStream(media.getByteData()) ;


            BufferedReader brReader = new BufferedReader(reader);
            String firstLine = brReader.readLine();
            if (firstLine == null || firstLine.isEmpty()) {
                throw new InvalidCSVException("Failed to read the log! header must have non-empty value!");
            }

            char separator = getMaxOccurringChar(firstLine);
            if (!(new String(supportedSeparators).contains(String.valueOf(separator)))) {
                throw new InvalidCSVException("Failed to read the log! Try different encoding");
            }

            // Create the CSV reader
            reader = new InputStreamReader(in, charset);
            return (new CSVReaderBuilder(reader))
                    .withSkipLines(0)
                    .withCSVParser((new RFC4180ParserBuilder()).withSeparator(separator).build())
                    .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                    .build();
        }catch (InvalidCSVException e){
            Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
            return null;
        } catch (IOException e) {
            Messagebox.show("Unable to import file : " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
            return null;
        }
    }

    private char getMaxOccurringChar(String str) {
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isLetter(str.charAt(i))) {
                for (char supportedSeparator : supportedSeparators) {
                    if (str.charAt(i) == supportedSeparator) {
                        char ch = str.charAt(i);
                        if (++charcnt[ch] >= maxcnt) {
                            maxcnt = charcnt[ch];
                            maxchar = ch;
                        }
                    }
                }
            }
        }
        return maxchar;
    }
}
