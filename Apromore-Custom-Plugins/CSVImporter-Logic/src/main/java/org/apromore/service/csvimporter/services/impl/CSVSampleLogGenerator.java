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
package org.apromore.service.csvimporter.services.impl;

import com.opencsv.CSVReader;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.model.LogSampleImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.io.CSVFileReader.csvFileReader;

public class CSVSampleLogGenerator implements SampleLogGenerator {
    @Override
    public LogSample generateSampleLog(File importedFile, int sampleSize) throws Exception {

        CSVReader csvReader = csvFileReader(importedFile, "utf-8", ',');
        List<String> header = Arrays.asList(csvReader.readNext());

        List<List<String>> lines = new ArrayList<>();
        String[] myLine;
        while ((myLine = csvReader.readNext()) != null && lines.size() < sampleSize) {
            if (myLine.length != header.size()) continue;

            lines.add(Arrays.asList(myLine));
        }
        return new LogSampleImpl(header, lines);
    }
}
