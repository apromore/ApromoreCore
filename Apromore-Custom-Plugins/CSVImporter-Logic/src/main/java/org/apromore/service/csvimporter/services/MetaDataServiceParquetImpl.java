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

import static org.apromore.service.csvimporter.utilities.ParquetUtilities.getHeaderFromParquet;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.io.FileWriter;
import org.apromore.service.csvimporter.io.ParquetLocalFileReader;
import org.apromore.service.csvimporter.model.LogMetaData;
import org.apromore.service.csvimporter.model.ParquetLogMetaData;

class MetaDataServiceParquetImpl implements MetaDataService {
    private ParquetReader<Group> reader;

    @Override
    public void validateLog(InputStream in, String charset) throws Exception {
        try {
            //Write InputStream to a file
            File tempFile = File.createTempFile("samplelog", "parquet");
            new FileWriter(in, tempFile).writeToFile();
            //Read Parquet file
            ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempFile);
            MessageType schema = parquetLocalFileReader.getSchema();

            if (schema == null || schema.getColumns().size() <= 0)
                throw new Exception("Unable to import file. Schema is missing.");

	    tempFile.delete();
        } catch (Exception e) {
            throw new Exception("Unable to import file", e);
        } finally {
            in.close();
        }
    }

    @Override
    public LogMetaData extractMetadata(InputStream in, String charset) throws Exception {
        try {
            //Write InputStream to a file
            File tempFile = File.createTempFile("samplelog", "parquet");
            new FileWriter(in, tempFile).writeToFile();
            //Read Parquet file
            ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempFile);
            MessageType schema = parquetLocalFileReader.getSchema();
            return new ParquetLogMetaData(getHeaderFromParquet(schema), tempFile);

        } finally {
            in.close();
        }
    }

    @Override
    public List<List<String>> generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {

        try {
            //Write InputStream to a file
            File tempFile = File.createTempFile("samplelog", "parquet");
            new FileWriter(in, tempFile).writeToFile();

            //Read Parquet file
            ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempFile);
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
	    tempFile.delete();
            return lines;

        } finally {
            reader.close();
            in.close();
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
