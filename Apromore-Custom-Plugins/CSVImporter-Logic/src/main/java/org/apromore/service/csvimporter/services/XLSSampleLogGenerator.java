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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apromore.service.csvimporter.io.XLSReader;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.model.LogSampleImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class XLSSampleLogGenerator implements SampleLogGenerator {

    private final int BUFFER_SIZE = 2048;
    private final int DEFAULT_NUMBER_OF_ROWS = 10;

    @Override
    public void validateLog(InputStream in, String charset) throws Exception {
        try {

            List<String> header = new ArrayList<>();
            Workbook workbook = new XLSReader().readXLS(in, DEFAULT_NUMBER_OF_ROWS, BUFFER_SIZE);
            Sheet sheet = workbook.getSheetAt(0);

            //Get the header
            for (Row r : sheet) {
                for (Cell c : r) {
                    header.add(c.getStringCellValue());
                }
                break;
            }
            if (header == null || header.isEmpty())
                throw new Exception("header must have non-empty value!");

        } catch (Exception e) {
            throw new Exception("Unable to import file");
        }
    }

    @Override
    public LogSample generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {

        List<String> header = new ArrayList<>();
        List<List<String>> lines = new ArrayList<>();
        Workbook workbook = new XLSReader().readXLS(in, sampleSize + 1, BUFFER_SIZE);

        if (workbook == null)
            return null;

        Sheet sheet = workbook.getSheetAt(0);
        //Get the header
        if (sheet != null) {
            for (Row r : sheet) {
                for (Cell c : r) {
                    header.add(c.getStringCellValue());
                }
                break;
            }

            //Get the rows
            for (Row r : sheet) {
                if (lines.size() == sampleSize) break;
                if (r.getLastCellNum() != header.size()) continue;
                List<String> line = new ArrayList<>();
                for (Cell c : r) {
                    line.add(c.getStringCellValue());
                }
                lines.add(line);
            }
        }
        return new LogSampleImpl(header, lines);
    }
}