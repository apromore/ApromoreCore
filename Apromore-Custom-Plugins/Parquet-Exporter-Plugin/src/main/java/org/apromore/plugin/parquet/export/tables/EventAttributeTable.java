/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.plugin.parquet.export.tables;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apromore.plugin.parquet.export.service.AbstractParquetProducer;
import org.apromore.plugin.parquet.export.tables.tablecontents.EventAttributeContentItem;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

public class EventAttributeTable extends AbstractParquetProducer {

    /**
     *
     * @param charset e.g. StandardCharsets.UTF_8
     * @return
     */
    public static Schema getSchema(Charset charset) {
        String schemaStr = getSchema();
        byte[] bytes = schemaStr.getBytes(charset);
        String utf8EncodedString = new String(bytes, charset);

        return new Schema.Parser().parse(utf8EncodedString);
    }

    public static List<GenericData.Record> getRecords(List<EventAttributeContentItem> items, Schema schema) {
        List<GenericData.Record> data = new ArrayList<>();

        for (EventAttributeContentItem item : items) {
            GenericData.Record record = getRecord(schema, item);
            data.add(record);
        }

        return data;
    }

    private static GenericData.Record getRecord(Schema schema,
                                                EventAttributeContentItem item) {

        GenericData.Record record = new GenericData.Record(schema);

        record.put("value", item.getValue());
        record.put("cases", item.getCases());
        record.put("activity_instances", item.getActivitySize());
        record.put("frequency", item.getValueFrequencyString());
        record.put("min_duration", item.getMinDuration());
        record.put("median_duration", item.getMedianDuration());
        record.put("average_duration", item.getAverageDuration());
        record.put("max_duration", item.getMaxDuration());

        return record;
    }

    private static String getSchema() {
        JSONObject schema = new JSONObject();

        schema.put("type", "record");
        schema.put("name", "case_variants");
        schema.put("namespace", "apromore.org");

        JSONObject filedVal = getField("value", "string");
        JSONObject filedCases = getField("cases", "long");
        JSONObject filedActs = getField("activity_instances", "long");
        JSONObject filedFreq = getField("frequency", "string");

        JSONArray fields = new JSONArray();
        fields.add(filedVal);
        fields.add(filedCases);
        fields.add(filedActs);
        fields.add(filedFreq);

        appendCommonDurationFields(fields);

        schema.put("fields", fields);

        return schema.toJSONString();
    }
}
