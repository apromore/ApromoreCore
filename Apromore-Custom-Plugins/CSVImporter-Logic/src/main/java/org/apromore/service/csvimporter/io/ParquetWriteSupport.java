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

package org.apromore.service.csvimporter.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.model.LogEventModel;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParquetWriteSupport extends WriteSupport<LogEventModel> {
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
    public void write(LogEventModel logEventModel) {

//    if (values.size() != cols.size()) {
//      throw new ParquetEncodingException("Invalid input data. Expecting " +
//          cols.size() + " columns. Input had " + values.size() + " columns (" + cols + ") : " + values);
//    }
        int index = 0;

        recordConsumer.startMessage();

//      // val.length() == 0 indicates a NULL value.
//      if (val.length() > 0) {
//      }

//        System.out.println("Index " + index + " value " + logEventModel.getCaseID());
        //caseID
        recordConsumer.startField(cols.get(index).getPath()[0], index);
        recordConsumer.addBinary(stringToBinary(logEventModel.getCaseID()));
        recordConsumer.endField(cols.get(index).getPath()[0], index);

        //activity
        index++;
//        System.out.println("Index " + index + " value " + logEventModel.getActivity());
        recordConsumer.startField(cols.get(index).getPath()[0], index);
        recordConsumer.addBinary(stringToBinary(logEventModel.getActivity()));
        recordConsumer.endField(cols.get(index).getPath()[0], index);


        //endTimestamp
        index++;
//        System.out.println("Index " + index + " value " + logEventModel.getEndTimestamp());
        recordConsumer.startField(cols.get(index).getPath()[0], index);
        recordConsumer.addBinary(stringToBinary(logEventModel.getEndTimestamp()));
        recordConsumer.endField(cols.get(index).getPath()[0], index);

        //startTimestamp
        index++;
//        System.out.println("Index " + index + " value " + logEventModel.getStartTimestamp());
        recordConsumer.startField(cols.get(index).getPath()[0], index);
        recordConsumer.addBinary(stringToBinary(logEventModel.getStartTimestamp()));
        recordConsumer.endField(cols.get(index).getPath()[0], index);

        //getOtherTimestamps
        Map<String, Timestamp> otherTimestamps = logEventModel.getOtherTimestamps();
        if (otherTimestamps.size() > 0) {
            for (String key : otherTimestamps.keySet()) {
                index++;
//                System.out.println("Index " + index + " value " + otherTimestamps.get(key).toString());
                recordConsumer.startField(cols.get(index).getPath()[0], index);
                recordConsumer.addBinary(stringToBinary(otherTimestamps.get(key).toString()));
                recordConsumer.endField(cols.get(index).getPath()[0], index);
            }
        }

        //resource
        index++;
//        System.out.println("Index " + index + " value " + logEventModel.getResource());
        recordConsumer.startField(cols.get(index).getPath()[0], index);
        recordConsumer.addBinary(stringToBinary(logEventModel.getResource()));
        recordConsumer.endField(cols.get(index).getPath()[0], index);


        //Case Attributes
        Map<String, String> caseAttributes = logEventModel.getCaseAttributes();
        if (caseAttributes.size() > 0) {
            for (String key : caseAttributes.keySet()) {
                index++;
//                System.out.println("Index " + index + " value " + caseAttributes.get(key).toString());
                recordConsumer.startField(cols.get(index).getPath()[0], index);
                recordConsumer.addBinary(stringToBinary(caseAttributes.get(key)));
                recordConsumer.endField(cols.get(index).getPath()[0], index);
            }
        }

        //Event Attributes
        Map<String, String> eventAttributes = logEventModel.getEventAttributes();
        if (eventAttributes.size() > 0) {
            for (String key : eventAttributes.keySet()) {
                index++;
//                System.out.println("Index " + index + " value " + eventAttributes.get(key).toString());
                recordConsumer.startField(cols.get(index).getPath()[0], index);
                recordConsumer.addBinary(stringToBinary(eventAttributes.get(key)));
                recordConsumer.endField(cols.get(index).getPath()[0], index);
            }
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
