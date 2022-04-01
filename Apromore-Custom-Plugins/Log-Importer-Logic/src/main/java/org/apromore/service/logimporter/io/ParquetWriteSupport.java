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

package org.apromore.service.logimporter.io;

import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;

public class ParquetWriteSupport extends WriteSupport<String[]> {
    MessageType schema;
    RecordConsumer recordConsumer;
    List<ColumnDescriptor> cols;

    // TODO: support specifying encodings and compression
    public ParquetWriteSupport(MessageType schema) {
        this.schema = schema;
        this.cols = schema.getColumns();
    }

    @Override
    public WriteSupport.WriteContext init(Configuration config) {
        return new WriteContext(schema, new HashMap<String, String>());
    }

    @Override
    public void prepareForWrite(RecordConsumer r) {
        recordConsumer = r;
    }

    @Override
    public void write(String[] event) {

        recordConsumer.startMessage();
        for (int i = 0; i < event.length; i++) {
            recordConsumer.startField(cols.get(i).getPath()[0], i);
            recordConsumer.addBinary(stringToBinary(event[i]));
            recordConsumer.endField(cols.get(i).getPath()[0], i);
        }
        recordConsumer.endMessage();
    }

    private Binary stringToBinary(Object value) {
        if (value == null) {
            return Binary.EMPTY;
        } else {
            return Binary.fromString(value.toString());
        }
    }
}
