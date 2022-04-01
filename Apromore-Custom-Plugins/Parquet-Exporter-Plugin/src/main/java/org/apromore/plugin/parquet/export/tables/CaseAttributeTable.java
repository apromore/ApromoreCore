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
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apromore.apmlog.ATrace;
import org.apromore.plugin.parquet.export.service.AbstractParquetProducer;
import org.apromore.plugin.parquet.export.util.Util;
import org.apromore.plugin.parquet.export.core.data.APMLogData;
import org.apromore.plugin.parquet.export.core.data.LogExportItem;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

public class CaseAttributeTable extends AbstractParquetProducer {

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

    public static List<GenericData.Record> getRecords(String key, LogExportItem logItem, Schema schema) {
        Map<String, List<ATrace>> map =
                APMLogData.getCaseAttributeValueGroups(new ArrayList<>(logItem.getPLog().getPTraces()), key);

        List<GenericData.Record> data = new ArrayList<>();

        for (Map.Entry<String, List<ATrace>> entry : map.entrySet()) {
            GenericData.Record genericDataRecord = getRecord(schema, entry, logItem.getPLog().size());
            data.add(genericDataRecord);
        }

        return data;
    }

    private static GenericData.Record getRecord(Schema schema, Map.Entry<String, List<ATrace>> entry, long totalCases) {

        GenericData.Record record = new GenericData.Record(schema);

        double[] durArray = entry.getValue().stream().mapToDouble(ATrace::getDuration).toArray();
        DoubleArrayList dal = new DoubleArrayList(durArray);

        String freq = Util.getDecimalFormat().format( 100.0 * ( (double) entry.getValue().size() / totalCases ) ) + "%";

        record.put("value", entry.getKey());
        record.put("cases", entry.getValue().size());
        record.put("frequency", freq);
        record.put("min_duration", dal.min());
        record.put("median_duration", dal.median());
        record.put("average_duration", dal.average());
        record.put("max_duration", dal.max());

        return record;
    }

    private static String getSchema() {
        JSONObject schema = new JSONObject();

        schema.put("type", "record");
        schema.put("name", "case_variants");
        schema.put("namespace", "apromore.org");

        JSONObject filedVal = getField("value", "string");
        JSONObject filedCases = getField("cases", "long");
        JSONObject filedFreq = getField("frequency", "string");

        JSONArray fields = new JSONArray();
        fields.add(filedVal);
        fields.add(filedCases);
        fields.add(filedFreq);

        appendCommonDurationFields(fields);

        schema.put("fields", fields);

        return schema.toJSONString();
    }
}
