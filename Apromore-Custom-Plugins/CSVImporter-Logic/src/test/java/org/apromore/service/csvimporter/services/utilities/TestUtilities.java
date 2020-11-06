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
package org.apromore.service.csvimporter.services.utilities;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.io.ParquetLocalFileReader;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class TestUtilities {
    public static String convertParquetToCSV(File parquetFile, char delimdelimiters) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), parquetFile);
        MessageType schema = parquetLocalFileReader.getSchema();
        ParquetReader<Group> reader = parquetLocalFileReader.getParquetReader();

        Group g;
        while ((g = reader.read()) != null) {
            StringBuilder s = writeGroup(g, schema, delimdelimiters);
            stringBuilder.append(s);
        }
        reader.close();
        return stringBuilder.toString();
    }

    private static StringBuilder writeGroup(Group g, MessageType schema, char delimdelimiters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < schema.getFieldCount(); j++) {
            if (j > 0)
                stringBuilder.append(delimdelimiters);

            String valueToString = g.getValueToString(j, 0);
            stringBuilder.append(valueToString);
        }
        stringBuilder.append(System.getProperty("line.separator"));
        return stringBuilder;
    }

    public String removeTimezone(String logString) {
        // regex for the timezone used in the test data  e.g. +03:00
//        Pattern p = Pattern.compile("([\\+-]\\d{2}:\\d{2})|Z");
        Pattern p = Pattern.compile("(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])");
        return logString.replaceAll(p.pattern(), "");
    }

    public String xlogToString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        return baos.toString();
    }
}
