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
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.model.LogSample;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static org.apache.parquet.format.converter.ParquetMetadataConverter.NO_FILTER;
import static org.apromore.service.csvimporter.services.Utilities.convertParquetToCSV;

public class ParquetToParquetExporterUnitTest {
    ParquetFactoryProvider parquetFactoryProvider = new ParquetFactoryProvider();
    final String parquetDir = "src/test/resources";

    // Test cases

    /**
     * Test {@link SampleLogGenerator.generateSampleLog} sampling fewer lines than contained in <code>test1-valid.csv</code>.
     */
    @Test
    public void testSampleParquet_undersample() throws Exception {

        String testFile = "/test1-valid.parquet";        //Create an output parquet file
        InputStream in = ParquetToParquetExporterUnitTest.class.getResourceAsStream(testFile);

        LogSample logSample =  parquetFactoryProvider
                .getParquetFactory("parquet")
                .createSampleLogGenerator()
                .generateSampleLog(in, 100, "UTF-8");

        System.out.println("*******************************************************");
        System.out.println("logSample " + logSample.getLines());
        System.out.println("parquetFileReader " + logSample.getHeader());

        System.out.println("*******************************************************");

//        File output = new File("src/main/resources/temp.parquet");
//
//        //Read Parquet file
//        Configuration conf = new Configuration(true);
//        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, new Path(output.toURI()), NO_FILTER);
//        MessageType parquetSchema = parquetFooter.getFileMetaData().getSchema();
//        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, new Path(output.toURI()), parquetFooter.getBlocks(), parquetSchema.getColumns());

//        System.out.println("Output " + parquetSchema.getColumns());

    }
}
