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

package org.apromore.service.logimporter.services.utilities;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.logimporter.io.ParquetLocalFileReader;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;

public class TestUtilities {
    public static String convertParquetToCsv(File parquetFile, char delimdelimiters) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader =
            new ParquetLocalFileReader(new Configuration(true), parquetFile);
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
            if (j > 0) {
                stringBuilder.append(delimdelimiters);
            }

            String valueToString = g.getValueToString(j, 0);
            stringBuilder.append(valueToString);
        }
        stringBuilder.append(System.getProperty("line.separator"));
        return stringBuilder;
    }

    /**
     * To make XES documents generated in different time zones comparable by simple text comparison,
     * we use regex filtering to remove trailing zeros in the millisecond field of ISO 8601 timestamps,
     * and the time zone field.
     *
     * @param logString an XES log
     * @return the <var>logString</var> with all patterns that resemble ISO 8601 timezones and
     *         zero-entended millisecond fields filtered out
     */
    public String removeTimezone(String logString) {
        // regex for the timezone used in the test data  e.g. +03:00
        // Pattern p = Pattern.compile("([\\+-]\\d{2}:\\d{2})|Z");
        Pattern p = Pattern.compile("(.000)?(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])");
        // Remove all line separators to avoid test failing on Windows
        return logString.replaceAll(p.pattern(), "").replaceAll(p.pattern(), "");
    }

    public String xlogToString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        // Remove all line separators to avoid test failing on Windows
        return baos.toString().replaceAll("\r", "");
    }

    /**
     * Get the string encoding of a resource file.
     *
     * @param resource the classpath of a UTF-8-encoded test document, e.g. "/test1-expected.xes"
     * @return the content of the document, with line endings normalized to LF (i.e. unix convention)
     * @throws IOException if the <var>resource</var> cannot be read
     */
    public static String resourceToString(String resource) throws IOException {
        return new String(ByteStreams.toByteArray(TestUtilities.class.getResourceAsStream(resource)),
            Charset.forName("utf-8"));
    }
}
