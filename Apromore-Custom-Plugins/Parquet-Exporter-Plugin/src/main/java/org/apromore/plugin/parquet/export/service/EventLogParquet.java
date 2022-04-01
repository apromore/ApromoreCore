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
package org.apromore.plugin.parquet.export.service;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.commons.lang3.tuple.Pair;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.plugin.parquet.export.core.data.APMLogData;
import org.apromore.plugin.parquet.export.util.Util;
import org.apromore.plugin.parquet.export.core.data.LogExportItem;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

public class EventLogParquet extends AbstractParquetProducer {

    private static final String CASE_ID_COL = "case_id";
    private static final String START_TIMESTAMP_COL = "start_timestamp";
    private static final String END_TIMESTAMP_COL = "end_timestamp";
    private static final String START_TIME_COL = "start_time";
    private static final String END_TIME_COL = "end_time";
    private static final String TIMESTAMP_COL = "timestamp";
    private static final String LONG_TYPE = "long";
    private static final String STRING_TYPE = "string";

    private static final Set<String> invalidNames =
            Set.of(START_TIME_COL, END_TIME_COL, TIMESTAMP_COL, START_TIMESTAMP_COL, END_TIMESTAMP_COL);

    public static Schema getEventLogSchema(LogExportItem logItem, Charset charset) {
        String schemaStr = getSchema(logItem);
        byte[] bytes = schemaStr.getBytes(charset);
        String utf8EncodedString = new String(bytes, charset);

        return new Schema.Parser().parse(utf8EncodedString);
    }

    public static List<GenericData.Record> getEventLogRecords(LogExportItem logItem, Schema schema) {
        List<GenericData.Record> data = new ArrayList<>();
        List<Pair<String, String>> caList = getAttributeHeaders(logItem, true);
        List<Pair<String, String>> eaList = getAttributeHeaders(logItem, false);

        List<ActivityInstance> activityInstances = logItem.getPLog().getActivityInstances();
        for (ActivityInstance act : activityInstances) {
            GenericData.Record recordGen = getRecord(schema, act, logItem, caList, eaList);
            data.add(recordGen);
        }

        return data;
    }



    private static List<Pair<String, String>> getValidPairs(List<Pair<String, String>> data) {
        return data.stream()
                .filter(x -> !invalidNames.contains(x.getLeft()))
                .collect(Collectors.toList());
    }

    private static GenericData.Record getRecord(Schema schema, ActivityInstance activityInstance,
                                                LogExportItem logItem,
                                                List<Pair<String, String>> caseAttrs,
                                                List<Pair<String, String>> eventAttrs) {
        caseAttrs = getValidPairs(caseAttrs);
        eventAttrs = getValidPairs(eventAttrs);

        GenericData.Record recordGen = new GenericData.Record(schema);

        if (caseAttrs.stream().noneMatch(x -> x.getLeft().equals(CASE_ID_COL)))
            recordGen.put(CASE_ID_COL, activityInstance.getParentTraceId());

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(START_TIMESTAMP_COL)))
            recordGen.put(START_TIMESTAMP_COL,
                    Util.timestampStringOf(Util.millisecondToZonedDateTime(activityInstance.getStartTime())));

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(END_TIMESTAMP_COL)))
            recordGen.put(END_TIMESTAMP_COL,
                    Util.timestampStringOf(Util.millisecondToZonedDateTime(activityInstance.getEndTime())));

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(START_TIME_COL)))
            recordGen.put(START_TIME_COL, activityInstance.getStartTime());

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(END_TIME_COL)))
            recordGen.put(END_TIME_COL, activityInstance.getEndTime());

        ATrace trace = logItem.getPLog().getPTracesMap().get(activityInstance.getParentTraceId());

        putCaseAttrsToGenericDataRecord(caseAttrs, trace, recordGen);
        putEventAttrsToGenericDataRecord(eventAttrs, activityInstance, recordGen);

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(TIMESTAMP_COL)))
            recordGen.put(TIMESTAMP_COL, activityInstance.getEndTime());

        return recordGen;
    }

    private static void putCaseAttrsToGenericDataRecord(List<Pair<String, String>> caseAttrs,
                                                        ATrace trace,
                                                        GenericData.Record genericDataRecord) {
        for (Pair<String, String> caPair : caseAttrs) {
            String originalKey = caPair.getRight();
            if (trace.getAttributes().containsKey(originalKey)) {
                String valStr = trace.getAttributes().get(originalKey);
                boolean numeric = Util.isNumeric(valStr);
                genericDataRecord.put(caPair.getLeft(), numeric ? Double.valueOf(valStr) : valStr);
            }
        }
    }

    private static void putEventAttrsToGenericDataRecord(List<Pair<String, String>> eventAttrs,
                                                         ActivityInstance activityInstance,
                                                         GenericData.Record genericDataRecord) {
        for (Pair<String, String> eaPair : eventAttrs) {
            String originalKey = eaPair.getRight();
            if (activityInstance.getAttributes().containsKey(originalKey)) {
                String valStr = activityInstance.getAttributes().get(originalKey);
                boolean numeric = Util.isNumeric(valStr);
                genericDataRecord.put(eaPair.getLeft(), numeric ? Double.valueOf(valStr) : valStr);
            }
        }
    }

    private static String getSchema(LogExportItem logItem) {
        JSONObject schema = new JSONObject();

        schema.put("type", "record");
        schema.put("name", "event_log");
        schema.put("namespace", "apromore.org");

        List<Pair<String, String>> caseAttrs = getValidPairs(getAttributeHeaders(logItem, true));
        List<Pair<String, String>> eventAttrs = getValidPairs(getAttributeHeaders(logItem, false));

        JSONArray fields = new JSONArray();

        if (caseAttrs.stream().noneMatch(x -> x.getLeft().equals(CASE_ID_COL))) {
            JSONObject filedCaseId = getField(CASE_ID_COL, STRING_TYPE);
            fields.add(filedCaseId);
        }

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(START_TIMESTAMP_COL))) {
            JSONObject filedStartTimestamp = getField(START_TIMESTAMP_COL, STRING_TYPE);
            fields.add(filedStartTimestamp);
        }

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(END_TIMESTAMP_COL))) {
            JSONObject filedEndTimestamp = getField(END_TIMESTAMP_COL, STRING_TYPE);
            fields.add(filedEndTimestamp);
        }

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(START_TIME_COL))) {
            JSONObject filedStartTime = getField(START_TIME_COL, LONG_TYPE);
            fields.add(filedStartTime);
        }

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(END_TIME_COL))) {
            JSONObject filedEndTime = getField(END_TIME_COL, LONG_TYPE);
            fields.add(filedEndTime);
        }

        for (Pair<String, String> pair : caseAttrs) {
            JSONObject caseAttr = getField(getValidColName(pair.getLeft()),
                    getCaseAttrType(pair.getRight(), logItem));
            fields.add(caseAttr);
        }

        for (Pair<String, String> pair : eventAttrs) {
            JSONObject eventAttr = getField(getValidColName(pair.getLeft()),
                    getEventAttrType(pair.getRight(), logItem));
            fields.add(eventAttr);
        }

        if (eventAttrs.stream().noneMatch(x -> x.getLeft().equals(TIMESTAMP_COL))) {
            JSONObject filedTimestamp = getField(TIMESTAMP_COL, LONG_TYPE);
            fields.add(filedTimestamp);
        }

        schema.put("fields", fields);

        return schema.toJSONString();
    }

    private static String getValidColName(String colName) {
        if (colName.equalsIgnoreCase("group"))
            return "resource_group";

        return colName;
    }

    private static String getCaseAttrType(String attribute, LogExportItem logItem) {
        if (attribute.toLowerCase().contains("case") && attribute.toLowerCase().contains("id"))
            return STRING_TYPE;

        Set<String> values = APMLogData.getUniqueCaseAttributeValues(logItem.getPLog(), attribute);
        return values.stream().anyMatch(x -> !Util.isNumeric(x)) ? STRING_TYPE : DOUBLE_TYPE;
    }

    private static String getEventAttrType(String attribute, LogExportItem logItem) {
        Set<String> values =
                APMLogData.getUniqueEventAttributeValues(logItem.getPLog().getActivityInstances(), attribute);
        return values.stream().anyMatch(x -> !Util.isNumeric(x)) ? STRING_TYPE : DOUBLE_TYPE;
    }
}
