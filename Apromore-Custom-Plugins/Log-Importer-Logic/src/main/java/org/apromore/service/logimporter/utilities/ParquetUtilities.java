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

package org.apromore.service.logimporter.utilities;

import java.util.ArrayList;
import java.util.List;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;
import org.apromore.service.logimporter.model.ParquetColumnType;

public class ParquetUtilities {
    public static MessageType createParquetSchema(String[] header) {
        StringBuilder sb = new StringBuilder();
        sb.append("message EventLog {\n");
        for (String s : header) {
            sb.append("optional binary ").append(formatColumn(s)).append(";\n");
        }
        sb.append("}");
        return MessageTypeParser.parseMessageType(sb.toString());
    }

    public static List<String> getHeaderFromParquet(MessageType schema) {
        List<String> header = new ArrayList<>();
        for (ColumnDescriptor columnDescriptor : schema.getColumns()) {
            header.add(columnDescriptor.getPath()[0]);
        }

        return header;
    }

    public static List<ParquetColumnType> getSchemaMappingFromParquet(MessageType schema) {
        List<ParquetColumnType> parquetColumnTypes = new ArrayList<>();
        for (ColumnDescriptor columnDescriptor : schema.getColumns()) {
            ParquetColumnType columnType = new ParquetColumnType(columnDescriptor.getPath()[0]);
            columnType.setPrimitiveType(columnDescriptor.getPrimitiveType().getPrimitiveTypeName().toString());
            LogicalTypeAnnotation logicalType = columnDescriptor.getPrimitiveType().getLogicalTypeAnnotation();
            if (logicalType != null) {
                columnType.setLogicalType(logicalType.toString());
            }
            parquetColumnTypes.add(columnType);
        }

        return parquetColumnTypes;
    }

    private static String formatColumn(String column) {
        return column
            .trim()
            .replaceAll("\\s", "_")
            .replaceAll("\\W", "")
            .toLowerCase();
    }
}
