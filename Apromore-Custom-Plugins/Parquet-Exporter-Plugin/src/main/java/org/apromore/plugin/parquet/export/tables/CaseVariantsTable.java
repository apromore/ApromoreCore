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
import org.apromore.plugin.parquet.export.core.data.LogExportItem;
import org.apromore.plugin.parquet.export.core.data.TableDataModels;
import org.apromore.plugin.parquet.export.core.data.dataobjects.CaseVariantItem;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

public class CaseVariantsTable extends AbstractParquetProducer {

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

    public static List<GenericData.Record> getRecords(LogExportItem logItem, Schema schema) {
        List<CaseVariantItem> records = TableDataModels.getCaseVariantTableModel(logItem);

        List<GenericData.Record> data = new ArrayList<>();

        for (CaseVariantItem item : records) {
            GenericData.Record record = getRecord(schema, item);
            data.add(record);
        }

        return data;
    }

    private static GenericData.Record getRecord(Schema schema, CaseVariantItem caseVariantItem) {

        GenericData.Record record = new GenericData.Record(schema);

        record.put("case_variant", caseVariantItem.getId());
        record.put("activity_instances", caseVariantItem.getActivities());
        record.put("cases", caseVariantItem.getCases());
        record.put("min_duration", caseVariantItem.getMinDuration());
        record.put("median_duration", caseVariantItem.getMedianDuration());
        record.put("average_duration", caseVariantItem.getAverageDuration());
        record.put("max_duration", caseVariantItem.getMaxDuration());

        return record;
    }

    private static String getSchema() {
        JSONObject schema = new JSONObject();

        schema.put("type", "record");
        schema.put("name", "case_variants");
        schema.put("namespace", "apromore.org");

        JSONObject filedVariId = getField("case_variant", "long");
        JSONObject filedActs = getField("activity_instances", "long");
        JSONObject filedCases = getField("cases", "long");
        JSONObject filedMinDur = getField("min_duration", "double");
        JSONObject filedMedDur = getField("median_duration", "double");
        JSONObject filedAvgDur = getField("average_duration", "double");
        JSONObject filedMaxDur = getField("max_duration", "double");

        JSONArray fields = new JSONArray();
        fields.add(filedVariId);
        fields.add(filedActs);
        fields.add(filedCases);
        fields.add(filedMinDur);
        fields.add(filedMedDur);
        fields.add(filedAvgDur);
        fields.add(filedMaxDur);

        schema.put("fields", fields);

        return schema.toJSONString();
    }
}
