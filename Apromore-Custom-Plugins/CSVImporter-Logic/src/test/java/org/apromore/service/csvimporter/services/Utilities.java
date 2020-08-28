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
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.schema.MessageType;

import java.io.File;
import java.io.IOException;

public class Utilities {
    public static String convertParquetToCSV(File parquetFile, char delimdelimiters) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        Path parquetFilePath = new Path(parquetFile.toURI());
        Configuration configuration = new Configuration(true);

        GroupReadSupport readSupport = new GroupReadSupport();
        ParquetMetadata readFooter = ParquetFileReader.readFooter(configuration, parquetFilePath);
        MessageType schema = readFooter.getFileMetaData().getSchema();
        readSupport.init(configuration, null, schema);
        ParquetReader<Group> reader = new ParquetReader<Group>(parquetFilePath, readSupport);

        Group g = null;
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
        stringBuilder.append('\n');
        return stringBuilder;
    }
}
