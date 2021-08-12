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
package org.apromore.service.logimporter.utilities;

import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

import java.util.ArrayList;
import java.util.List;

public class ParquetUtilities {
    public static MessageType createParquetSchema(String[] header) {
        MessageType schema;
        StringBuilder sb = new StringBuilder();
        sb.append("message EventLog {\n");
        for (int i = 0; i < header.length; i++)
            sb.append("optional binary " + formatColumn(header[i]) + ";\n");
        sb.append("}");
        schema = MessageTypeParser.parseMessageType(sb.toString());
        return schema;
    }

    public static List<String> getHeaderFromParquet(MessageType schema) {
        List<String> header = new ArrayList<>();
        for (ColumnDescriptor columnDescriptor : schema.getColumns()) {
            header.add(columnDescriptor.getPath()[0]);
        }
        return header;
    }

    private static String formatColumn(String column) {
        return column
                .trim()
                .replaceAll("\\s", "_")
                .replaceAll("\\W", "")
                .toLowerCase();
    }
}
