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
import org.apromore.apmlog.ATrace;
import org.apromore.plugin.parquet.export.service.AbstractParquetProducer;
import org.apromore.plugin.parquet.export.core.data.LogExportItem;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

public class CasesTable extends AbstractParquetProducer {

    /**
     *
     * @param charset e.g. StandardCharsets.UTF_8
     */
    public static Schema getSchema(Charset charset) {
        String schemaStr = getSchema();
        byte[] bytes = schemaStr.getBytes(charset);
        String utf8EncodedString = new String(bytes, charset);

        return new Schema.Parser().parse(utf8EncodedString);
    }

    public static List<GenericData.Record> getRecords(LogExportItem logItem, Schema schema) {
        List<GenericData.Record> data = new ArrayList<>();

        for (ATrace trace : logItem.getPLog().getPTraces()) {
            GenericData.Record record = getRecord(schema, trace);
            data.add(record);
        }

        return data;
    }

    private static GenericData.Record getRecord(Schema SCHEMA, ATrace trace) {

        GenericData.Record record = new GenericData.Record(SCHEMA);

        record.put("case_id", trace.getCaseId());
        record.put("activity_instances", trace.getActivityInstances().size());
        record.put("start_time", trace.getStartTime());
        record.put("end_time", trace.getEndTime());
        record.put("duration", (long) trace.getDuration());

        return record;
    }

    private static String getSchema() {
        JSONObject schema = new JSONObject();

        schema.put("type", "record");
        schema.put("name", "cases");
        schema.put("namespace", "apromore.org");

        JSONObject filedCaseId = getField("case_id", "string");
        JSONObject filedActs = getField("activity_instances", "long");
        JSONObject filedStartTime = getField("start_time", "long");
        JSONObject filedEndTime = getField("end_time", "long");
        JSONObject filedDuration = getField("duration", "long");

        JSONArray fields = new JSONArray();
        fields.add(filedCaseId);
        fields.add(filedActs);
        fields.add(filedStartTime);
        fields.add(filedEndTime);
        fields.add(filedDuration);

        schema.put("fields", fields);

        return schema.toJSONString();
    }
}
