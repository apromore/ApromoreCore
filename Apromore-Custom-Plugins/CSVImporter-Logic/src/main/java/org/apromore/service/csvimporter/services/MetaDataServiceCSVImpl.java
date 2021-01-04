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
package org.apromore.service.csvimporter.services;

import com.opencsv.CSVReader;
import org.apache.commons.io.input.ReaderInputStream;
import org.apromore.service.csvimporter.constants.Constants;
import org.apromore.service.csvimporter.io.CSVFileReader;
import org.apromore.service.csvimporter.model.LogMetaData;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.utilities.CSVUtilities.getMaxOccurringChar;

class MetaDataServiceCSVImpl implements MetaDataService {

    private Reader reader;
    private BufferedReader brReader;
    private InputStream in2;
    private CSVReader csvReader;

    @Override
    public void validateLog(InputStream in, String charset) throws Exception {

        try {
            reader = new InputStreamReader(in, Charset.forName(charset));
            brReader = new BufferedReader(reader);
            String firstLine = brReader.readLine();
            if (firstLine == null || firstLine.isEmpty()) {
                throw new Exception("header must have non-empty value!");
            }

            char separator = getMaxOccurringChar(firstLine);
            if (!(new String(Constants.supportedSeparators).contains(String.valueOf(separator))))
                throw new Exception("Try different encoding");

        } catch (IOException e) {
            throw new Exception("Unable to import file");
        } finally {
            closeQuietly(in);
        }
    }

    @Override
    public LogMetaData extractMetadata(InputStream in, String charset) throws Exception {
        try {
            reader = new InputStreamReader(in, Charset.forName(charset));
            brReader = new BufferedReader(reader);
            String firstLine = brReader.readLine();
            firstLine = firstLine.replaceAll("\"", "");
            char separator = getMaxOccurringChar(firstLine);

            if (!(new String(Constants.supportedSeparators).contains(String.valueOf(separator))))
                throw new Exception("Try different encoding");

            List<String> header = Arrays.asList(firstLine.split("\\s*" + separator + "\\s*"));

            return new LogMetaData(header);

        } finally {
            closeQuietly(in);
        }
    }


    @Override
    public List<List<String>> generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {

        try {
            reader = new InputStreamReader(in, Charset.forName(charset));
            brReader = new BufferedReader(reader);
            String firstLine = brReader.readLine();
            firstLine = firstLine.replaceAll("\"", "");
            char separator = getMaxOccurringChar(firstLine);

            if (!(new String(Constants.supportedSeparators).contains(String.valueOf(separator))))
                throw new Exception("Try different encoding");

            List<String> header = Arrays.asList(firstLine.split("\\s*" + separator + "\\s*"));

            in2 = new ReaderInputStream(brReader, charset);
            csvReader = new CSVFileReader().newCSVReader(in2, charset, separator);

            if (csvReader == null)
                return null;

            List<List<String>> lines = new ArrayList<>();
            String[] myLine;


            while ((myLine = csvReader.readNext()) != null && lines.size() < sampleSize) {
                if (myLine.length != header.size()) continue;
                lines.add(Arrays.asList(myLine));
            }

            return lines;

        } finally {
            closeQuietly(in);
        }
    }

    private void closeQuietly(InputStream in) throws IOException {
        if (in != null)
            in.close();
        if (this.csvReader != null)
            this.csvReader.close();
        if (this.brReader != null)
            this.brReader.close();
        if (this.reader != null)
            this.reader.close();
        if (this.in2 != null)
            this.in2.close();
    }
}
