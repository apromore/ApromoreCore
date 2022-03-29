/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.plugin.portal.logimporter;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apromore.service.logimporter.exception.EmptyHeaderException;
import org.apromore.service.logimporter.exception.UnsupportedSeparatorException;
import org.zkoss.util.media.Media;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

public class CSVFileReader {

    CSVReader newCSVReader(Media media, String charset) throws Exception {
        // Guess at ethe separator character
        Reader reader = media.isBinary() ? new InputStreamReader(media.getStreamData(), charset) :
                media.getReaderData();

        BufferedReader brReader = new BufferedReader(reader);
        String firstLine = brReader.readLine();
        brReader.close();
        if (firstLine == null || firstLine.isEmpty()) {
            throw new EmptyHeaderException();
        }

        char separator = getMaxOccurringChar(firstLine);
        if (!(new String(Constants.SUPPORTED_SEPARATORS).contains(String.valueOf(separator)))) {
            throw new UnsupportedSeparatorException();
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
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isLetter(str.charAt(i))) {
                for (char supportedSeparator : Constants.SUPPORTED_SEPARATORS) {
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
