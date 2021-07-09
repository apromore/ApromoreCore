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
package org.apromore.service.csvimporter.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.schema.MessageType;

import java.io.File;
import java.io.IOException;

public class ParquetLocalFileReader {
    private Configuration conf;
    private File file;
    private MessageType schema;

    public ParquetLocalFileReader(Configuration conf, File file) throws IOException {
        this.conf = conf;
        this.file = file;

        //get the schema
        Thread thread = Thread.currentThread();
        synchronized (thread) {
            ClassLoader originalContextClassLoader = thread.getContextClassLoader();
            try {
                thread.setContextClassLoader(Path.class.getClassLoader());
                InputFile inputFile = HadoopInputFile.fromPath(new Path(file.toURI()), conf);
                try (ParquetFileReader parquetFileReader = ParquetFileReader.open(inputFile)) {
                    schema = parquetFileReader.getFooter().getFileMetaData().getSchema();
                }

            } finally {
                thread.setContextClassLoader(originalContextClassLoader);
            }
        }
    }

    public ParquetReader<Group> getParquetReader() throws IOException {

        Thread thread = Thread.currentThread();
        synchronized (thread) {
            ClassLoader originalContextClassLoader = thread.getContextClassLoader();
            try {
                thread.setContextClassLoader(Path.class.getClassLoader());

                GroupReadSupport readSupport = new GroupReadSupport();
                readSupport.init(conf, null, schema);
                ParquetReader<Group> reader = ParquetReader.builder(readSupport, new Path(file.toURI())).build();
                return reader;

            } finally {
                thread.setContextClassLoader(originalContextClassLoader);
            }
        }
    }

    public ParquetFileReader getParquetFileReader() throws IOException {


        Thread thread = Thread.currentThread();
        synchronized (thread) {
            ClassLoader originalContextClassLoader = thread.getContextClassLoader();
            try {
                thread.setContextClassLoader(Path.class.getClassLoader());
                InputFile inputFile = HadoopInputFile.fromPath(new Path(file.toURI()), conf);
                ParquetFileReader parquetFileReader = new ParquetFileReader(inputFile, ParquetReadOptions.builder().build());


                return parquetFileReader;

            } finally {
                thread.setContextClassLoader(originalContextClassLoader);
            }
        }
    }


    public MessageType getSchema() {
        return schema;
    }
}
