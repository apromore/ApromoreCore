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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.model.ParquetLogSampleImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.parquet.format.converter.ParquetMetadataConverter.NO_FILTER;
import static org.apromore.service.csvimporter.utilities.ParquetUtilities.getHeaderFromParquet;

class ParquetSampleLogGenerator implements SampleLogGenerator {
    @Override
    public void validateLog(InputStream in, String charset) { /**To be Implemented**/}

    @Override
    public LogSample generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {


        //Write InputStream to a file
        File tempFile = File.createTempFile("parqeut", "parquet");
        OutputStream os = new FileOutputStream(tempFile);
        byte[] buffer = new byte[2048];
        int bytesRead;

        //read from is to buffer
        while ((bytesRead = in.read(buffer)) != -1)
            os.write(buffer, 0, bytesRead);

        in.close();
        //flush OutputStream to write any buffered data to file
        os.flush();
        os.close();

        Configuration conf = new Configuration(true);
        
        //Read Parquet file
        InputFile inputFile = HadoopInputFile.fromPath(new Path(tempFile.toURI()), conf);
        ParquetFileReader parquetFileReader = ParquetFileReader.open(inputFile);
        MessageType schema = parquetFileReader.getFooter().getFileMetaData().getSchema();

        GroupReadSupport readSupport = new GroupReadSupport();
        readSupport.init(conf, null, schema);
        ParquetReader<Group> reader = ParquetReader.builder(readSupport, new Path(tempFile.toURI())).build();


        List<List<String>> lines = new ArrayList<>();
        Group g = null;
        int lineIndex = 0;
        while ((g = reader.read()) != null && lineIndex < sampleSize) {
            String[] myLine = readGroup(g, schema);readGroup(g, schema);
            lines.add(Arrays.asList(myLine));
            lineIndex++;
        }
        reader.close();
        return new ParquetLogSampleImpl(getHeaderFromParquet(schema), lines, tempFile);
    }

    private String[] readGroup(Group g, MessageType schema) {

        String[] line = new String[schema.getColumns().size()];
        for (int j = 0; j < schema.getFieldCount(); j++) {

            String valueToString = g.getValueToString(j, 0);
            line[j] = valueToString;
        }
        return line;
    }
}