package org.apromore.plugin.portal.CSVImporterPortal;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.LogSample;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Messagebox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ConstructCSVSample {

    char[] supportedSeparators = {',','|',';','\t'};
    private CSVImporterLogic csvImporterLogic;
    private Media media;

    public ConstructCSVSample(CSVImporterLogic csvImporterLogic, Media media) {
        this.csvImporterLogic = csvImporterLogic;
        this.media = media;
    }

    LogSample getCSVSample(String fileEncoding, int logSampleSize) {
        String charset = fileEncoding;
        try (CSVReader csvReader = newCSVReader(charset)) {
            return csvImporterLogic.sampleCSV(csvReader, logSampleSize);
        } catch (InvalidCSVException e) {
            Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            Messagebox.show("Failed to read the log!", "Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
            return null;
        }
    }

    private CSVReader newCSVReader(String charset) throws InvalidCSVException, IOException {
        // Guess at ethe separator character
        Reader reader = media.isBinary() ? new InputStreamReader(media.getStreamData(), charset) : media.getReaderData();
        BufferedReader brReader = new BufferedReader(reader);
        String firstLine = brReader.readLine();
        char separator = getMaxOccurringChar(firstLine);

        if (separator == Character.UNASSIGNED || !(new String(supportedSeparators).contains(String.valueOf(separator)))) {
            throw new InvalidCSVException("Failed to read the log! Either separator is not supported or encoding is invalid");
        }

        // Create the CSV reader
        reader = media.isBinary() ? new InputStreamReader(media.getStreamData(), charset) : media.getReaderData();
        return (new CSVReaderBuilder(reader))
                .withSkipLines(0)
                .withCSVParser((new RFC4180ParserBuilder()).withSeparator(separator).build())
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .build();
    }

    private char getMaxOccurringChar(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Failed to read the log! header must have non-empty value!");
        }
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
