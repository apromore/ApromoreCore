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

import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.FileMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.utilities.ParquetStream;

import java.io.InputStream;

import static org.apache.parquet.format.converter.ParquetMetadataConverter.NO_FILTER;

class ParquetSampleLogGenerator implements SampleLogGenerator {
    @Override
    public LogSample generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {
//        File file = new File("src/main/resources/sample.txt");
//
//        byte[] bytes = IOUtils.toByteArray(in);
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//
//        buffer.write(bytes, 0, bytes.length);
//
//        ParquetStream parquetStream = new ParquetStream("", buffer);
//
////        try(OutputStream outputStream = new FileOutputStream(file)){
////            IOUtils.copy(in, outputStream);
////        } catch (FileNotFoundException e) {
////            // handle exception here
////        } catch (IOException e) {
////            // handle exception here
////        }
//////        ParquetFileReader parquetFileReader = (ParquetFileReader) reader;
//        Configuration conf = new Configuration();
//        Path parqeutFilePath = new Path(file.toURI());
//        PageReadStore pageReadStore = null;
//
//        ParquetMetadata parquetFooter = ParquetFileReader.open();
//        FileMetaData mdata = parquetFooter.getFileMetaData();
//        MessageType parquetSchema = mdata.getSchema();
//
//
//        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, parqeutFilePath, parquetFooter.getBlocks(), parquetSchema.getColumns());
//
//
//        ParquetFileWriter writer = new ParquetFileWriter(new Path(file.toURI()), parquetSchema, true);

//        pageReadStore = parquetFileReader.readNextRowGroup();

        //TODO
//        while (pageReadStore != null) {
//
//        }

        return null;
    }

    public static void generateSampleLogsOLUTION(byte[] bytes, int sampleSize) throws Exception {


        PageReadStore pageReadStore = null;

        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(new ParquetStream("", bytes), NO_FILTER);
        FileMetaData mdata = parquetFooter.getFileMetaData();
        MessageType parquetSchema = mdata.getSchema();


        System.out.println("Parquet Schema: " + parquetSchema.toString());


//        ParquetFileReader parquetFileReader = new ParquetFileReader(new ParquetStream("", bytes), ParquetReadOptions.builder().build());
//        ParquetFileReader(new Configuration(), parqeutFilePath, parquetFooter.getBlocks(), parquetSchema.getColumns());


//        ParquetFileWriter writer = new ParquetFileWriter(new Path(file.toURI()), parquetSchema, true);

//        pageReadStore = parquetFileReader.readNextRowGroup();

//        while (pageReadStore != null) {
//
//
//
//        }

    }
}
