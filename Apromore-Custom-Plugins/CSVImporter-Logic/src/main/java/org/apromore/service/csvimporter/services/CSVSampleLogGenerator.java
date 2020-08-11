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

import com.opencsv.CSVReader;
import org.apromore.service.csvimporter.constants.Constants;
import org.apromore.service.csvimporter.io.CSVFileReader;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.model.LogSampleImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CSVSampleLogGenerator implements SampleLogGenerator {

    @Override
    public void validateLog(InputStream in, String charset) throws Exception {

        try {
            Reader reader = new InputStreamReader(in, charset);

            BufferedReader brReader = new BufferedReader(reader);
            String firstLine = brReader.readLine();
            if (firstLine == null || firstLine.isEmpty()) {
                throw new Exception("header must have non-empty value!");
            }

            char separator = getMaxOccurringChar(firstLine);
            if (!(new String(Constants.supportedSeparators).contains(String.valueOf(separator)))) {
                throw new Exception("Try different encoding");
            }

        } catch (IOException e) {
            throw new Exception("Unable to import file");
        }
    }


    @Override
    public LogSample generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {

        System.out.println(" " + charset + " " + sampleSize);

        CSVReader csvReader = new CSVFileReader().newCSVReader(in, charset);

        if (csvReader == null)
            return null;

        List<String> header = Arrays.asList(csvReader.readNext());

        List<List<String>> lines = new ArrayList<>();
        String[] myLine;
        while ((myLine = csvReader.readNext()) != null && lines.size() < sampleSize) {
            if (myLine.length != header.size()) continue;

            lines.add(Arrays.asList(myLine));
        }
        return new LogSampleImpl(header, lines);

//        return null;
    }

    private char getMaxOccurringChar(String str) {
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isLetter(str.charAt(i))) {
                for (char supportedSeparator : Constants.supportedSeparators) {
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
