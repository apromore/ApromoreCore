/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.service.logimporter.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apromore.service.logimporter.io.XLSReader;
import org.apromore.service.logimporter.model.LogMetaData;


public class MetaDataServiceXLSXImpl implements MetaDataService {

    private static final int BUFFER_SIZE = 2048;
    private static final int DEFAULT_NUMBER_OF_ROWS = 10;

    private List<String> getHeader(Sheet sheet) throws Exception {
        List<String> header = new ArrayList<>();
        for (Row row : sheet) {
            for (Cell c : row) {
                if (c.getStringCellValue() == null || c.getStringCellValue().isEmpty()) {
                    throw new Exception("header must have non-empty value!");
                }
                header.add(c.getStringCellValue().trim());
            }
            break;
        }
        return header;
    }

    @Override
    public void validateLog(InputStream in, String charset) throws Exception {

        try (Workbook workbook = new XLSReader().readXLS(in, DEFAULT_NUMBER_OF_ROWS, BUFFER_SIZE);) {

            List<String> header = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);

            //Get the header
            header = getHeader(sheet);
            if (header.isEmpty()) {
                throw new Exception("header must have non-empty value!");
            }

            workbook.close();
            in.close();

        } catch (Exception e) {
            throw new Exception("Unable to import file");
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @Override
    public LogMetaData extractMetadata(InputStream in, String charset, Map<String, String> customHeaderMap)
        throws Exception {

        try (Workbook workbook = new XLSReader().readXLS(in, 10, BUFFER_SIZE)) {
            List<String> header = new ArrayList<>();

            if (workbook == null) {
                return null;
            }

            Sheet sheet = workbook.getSheetAt(0);
            //Get the header
            header = getHeader(sheet);
            return new LogMetaData(header);

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @Override
    public List<List<String>> generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {

        try (Workbook workbook = new XLSReader().readXLS(in, sampleSize + 1, BUFFER_SIZE)) {
            List<String> header = new ArrayList<>();
            List<List<String>> lines = new ArrayList<>();
            String[] line;

            if (workbook == null) {
                return null;
            }

            Sheet sheet = workbook.getSheetAt(0);

            //Get the header
            if (sheet != null) {

                header = getHeader(sheet);

                //Get the rows
                for (Row r : sheet) {
                    if (lines.size() == sampleSize) {
                        break;
                    }
                    if (r.getPhysicalNumberOfCells() > header.size()) {
                        continue;
                    }

                    line = new String[header.size()];
                    for (Cell c : r) {
                        line[c.getColumnIndex()] = c.getStringCellValue();
                    }
                    Arrays.asList(line).replaceAll(
                        //val -> val == null ? "" : val  // Java 8
                        new java.util.function.UnaryOperator<String>() {
                            @Override
                            public String apply(String val) {
                                return val == null ? "" : val;
                            }
                        }
                    );
                    lines.add(Arrays.asList(line));
                }
            }
            return lines;

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
