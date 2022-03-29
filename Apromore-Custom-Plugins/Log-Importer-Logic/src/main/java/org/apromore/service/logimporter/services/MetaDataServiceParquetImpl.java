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

import static org.apromore.service.logimporter.utilities.ParquetUtilities.getHeaderFromParquet;
import static org.apromore.service.logimporter.utilities.ParquetUtilities.getSchemaMappingFromParquet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.logimporter.io.FileWriter;
import org.apromore.service.logimporter.io.ParquetLocalFileReader;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.ParquetColumnType;
import org.apromore.service.logimporter.model.ParquetLogMetaData;
import org.apromore.service.logimporter.utilities.FileUtils;

public class MetaDataServiceParquetImpl implements MetaDataService {

    public static final String SAMPLELOG = "samplelog";
    public static final String PARQUET_EXT = "parquet";
    private ParquetReader<Group> reader;

    @Override
    public void validateLog(InputStream in, String charset) throws Exception {
        try (in) {
            //Write InputStream to a file
            File tempFile = File.createTempFile(SAMPLELOG, PARQUET_EXT);
            new FileWriter(in, tempFile).writeToFile();
            //Read Parquet file
            ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true),
                tempFile);
            MessageType schema = parquetLocalFileReader.getSchema();

            if (schema == null || schema.getColumns().size() <= 0) {
                throw new Exception("Unable to import file. Schema is missing.");
            }

            FileUtils.deleteFile(tempFile);
        } catch (Exception e) {
            throw new Exception("Unable to import file", e);
        }
    }

    @Override
    public LogMetaData extractMetadata(InputStream in, String charset, Map<String, String> customHeaderMap)
        throws Exception {
        File tempFile = File.createTempFile(SAMPLELOG, PARQUET_EXT);
        MessageType schema = extractSchema(in, tempFile);

        List<String> headers = getHeaderFromParquet(schema);
        if (customHeaderMap != null) {
            headers.replaceAll(h -> customHeaderMap.getOrDefault(h, h));
        }

        return new ParquetLogMetaData(headers, tempFile);
    }

    @Override
    public List<List<String>> generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {

        try (in) {
            //Write InputStream to a file
            File tempFile = File.createTempFile(SAMPLELOG, PARQUET_EXT);
            new FileWriter(in, tempFile).writeToFile();

            //Read Parquet file
            ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true),
                tempFile);
            MessageType schema = parquetLocalFileReader.getSchema();
            reader = parquetLocalFileReader.getParquetReader();

            List<List<String>> lines = new ArrayList<>();
            Group g;
            int lineIndex = 0;
            while ((g = reader.read()) != null && lineIndex < sampleSize) {
                String[] myLine = readGroup(g, schema);
                lines.add(Arrays.asList(myLine));
                lineIndex++;
            }
            FileUtils.deleteFile(tempFile);
            return lines;

        } finally {
            reader.close();
        }
    }

    public List<ParquetColumnType> parseSchemaHeaderType(InputStream in) throws IOException {
        File tempFile = File.createTempFile(SAMPLELOG, PARQUET_EXT);
        return getSchemaMappingFromParquet(extractSchema(in, tempFile));
    }

    private MessageType extractSchema(InputStream in, File tempFile) throws IOException {
        try (in) {
            //Write InputStream to a file
            new FileWriter(in, tempFile).writeToFile();
            //Read Parquet file
            ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true),
                tempFile);
            return parquetLocalFileReader.getSchema();
        }
    }

    private String[] readGroup(Group g, MessageType schema) {

        String[] line = new String[schema.getColumns().size()];
        for (int j = 0; j < schema.getFieldCount(); j++) {
            String valueToString;

            try {
                valueToString = g.getValueToString(j, 0);
            } catch (Exception e) {
                valueToString = "";
            }

            line[j] = valueToString;
        }
        return line;
    }
}
