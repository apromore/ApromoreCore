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


import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.xes.XESAttributeCodes;
import org.apromore.plugin.parquet.export.types.EncodeOption;
import org.apromore.plugin.parquet.export.core.data.APMLogWrapper;
import org.apromore.plugin.parquet.export.util.LabelUtil;
import org.apromore.plugin.parquet.export.util.LoggerUtil;
import org.apromore.plugin.parquet.export.util.Util;
import org.apromore.plugin.parquet.export.types.ParquetCell;
import org.apromore.plugin.parquet.export.types.ParquetCol;
import org.apromore.plugin.parquet.export.core.data.APMLogData;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

public class ParquetExporterService extends AbstractParquetProducer {

    public static final String PARQUET_EXT = ".parquet";
    private final APMLogWrapper apmLogWrapper;
    private final Properties labels;
    private final List<ParquetCol> headers = new ArrayList<>();
    private final List<List<ParquetCell>> parquetRows = new ArrayList<>();
    private final List<ParquetCol> caseAttributes = new ArrayList<>();
    private final List<ParquetCol> eventAttributes = new ArrayList<>();
    private static final String CASE_ID = "case_id";
    private static final String TIMESTAMP = "timestamp";
    private static final String START_TIMESTAMP = "start_timestamp";
    private static final String END_TIMESTAMP = "end_timestamp";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String STRING = "string";
    private static final String LONG = "long";
    private static final String UTF8 = "UTF-8";

    public static final Set<String> invalidAttributes = Set.of("case:variant", "casevariant", "variant", CASE_ID, "caseid",
            TIMESTAMP, "starttimestamp", "endtimestamp", "Activity", "Resource", START_TIME, END_TIME,
            "lifecycletransition");

    private final List<EncodeOption> encodeOptions = List.of(
            new EncodeOption(UTF8, UTF8, true),
            new EncodeOption("windows-1250 (Eastern European)", "windows-1250", false),
            new EncodeOption("windows-1251 (Cyrillic)", "windows-1251", false),
            new EncodeOption("windows-1252 (Latin)", "windows-1252", false),
            new EncodeOption("windows-1253 (Greek)", "windows-1253", false),
            new EncodeOption("windows-1254 (Turkish)", "windows-1254", false),
            new EncodeOption("windows-1257 (Baltic)", "windows-1257", false),
            new EncodeOption("windows-1255 (Hebrew)", "windows-1255", false),
            new EncodeOption("ISO-8859-8 (Hebrew)", "ISO-8859-8", false),
            new EncodeOption("windows-1258 (Vietnamese)", "windows-1258", false),
            new EncodeOption("windows-31j (Japanese)", "windows-31j", false),
            new EncodeOption("Shift_JIS (Japanese)", "Shift_JIS", false),
            new EncodeOption("GB2312 (Simplified Chinese)", "GB2312", false),
            new EncodeOption("Big5 (Traditional Chinese)", "Big5", false)
    );

    public ParquetExporterService(APMLogWrapper apmLogCombo) {
        this.apmLogWrapper = apmLogCombo;
        labels = LabelUtil.getLabelProperties();
        initHeaders();
    }

    public String getLogLabel() {
        return apmLogWrapper.getLabel();
    }

    public Properties getLabels() {
        return labels;
    }

    public List<ParquetCol> getHeaders() {
        return headers;
    }

    public List<List<ParquetCell>> getParquetRows() {
        initRows();
        return parquetRows;
    }

    public List<EncodeOption> getEncodeOptions() {
        return encodeOptions;
    }

    public boolean exportParquetFileToOutputStream(OutputStream outputStream) {
        if (!isDownloadAllowed()){
            return false;
        }

        initRows();
        String charsetVal = encodeOptions.stream()
                .filter(EncodeOption::isSelected)
                .map(EncodeOption::getValue)
                .findFirst().orElse(UTF8);
        String filename = getValidParquetLabel(apmLogWrapper.getLabel()) + PARQUET_EXT;
        Schema schema = getSchema(charsetVal);
        ParquetExport.writeParquetToOutputStream(filename, getData(schema), schema, outputStream);
        return true;
    }

    public boolean downloadParquetFile() {
        if (!isDownloadAllowed())
            return false;

        String charsetVal = encodeOptions.stream()
                .filter(EncodeOption::isSelected)
                .map(EncodeOption::getValue)
                .findFirst().orElse(UTF8);
        String filename = getValidParquetLabel(apmLogWrapper.getLabel()) + PARQUET_EXT;
        Schema schema = getSchema(charsetVal);
        ParquetExport.downloadParquet(filename, getData(schema), schema);
        return true;
    }

    public boolean isDownloadAllowed() {
        List<ParquetCol> checkedCols = headers.stream().filter(ParquetCol::isChecked).collect(Collectors.toList());
        return checkedCols.stream().noneMatch(ParquetCol::isInvalidLabel);
    }

    public void checkHeaderDuplication() {
        Map<String, List<ParquetCol>> groups = headers.stream()
                .filter(ParquetCol::isChecked)
                .collect(Collectors.groupingBy(ParquetCol::getLabel));

        Set<ParquetCol> invalids = groups.entrySet().stream()
                .filter(x -> x.getValue().size() > 1)
                .flatMap(x -> x.getValue().stream())
                .collect(Collectors.toSet());

        for (ParquetCol col : headers) {
            if (invalids.contains(col))
                col.setInvalidLabel(true);
        }

    }

    // ====================================================================================================
    // Private
    // ====================================================================================================
    private void initHeaders() {
        caseAttributes.addAll(getValidCaseAttributes());
        eventAttributes.addAll(getValidEventAttributes());

        headers.addAll(caseAttributes);
        headers.addAll(eventAttributes);

        headers.add(0, new ParquetCol(TIMESTAMP, TIMESTAMP, LONG, true));
        headers.add(0, new ParquetCol(END_TIME, END_TIME,LONG, true));
        headers.add(0, new ParquetCol(START_TIME, START_TIME, LONG, true));
        headers.add(0, new ParquetCol(END_TIMESTAMP, END_TIMESTAMP, STRING, true));
        headers.add(0, new ParquetCol(START_TIMESTAMP, START_TIMESTAMP, STRING, true));
        headers.add(0, new ParquetCol(CASE_ID, CASE_ID, STRING, true));

        validateHeaders();
        checkHeaderDuplication();
    }

    private void validateHeaders() {
        Map<String, List<ParquetCol>> groups = headers.stream()
                .collect(Collectors.groupingBy(ParquetCol::getLabel));

        for (Map.Entry<String, List<ParquetCol>> entry : groups.entrySet()) {
            if (entry.getValue().size() > 1) {
                for (int i = 1; i < entry.getValue().size(); i++) {
                    ParquetCol col = entry.getValue().get(i);
                    String rawLabel = col.getLabel();
                    col.setLabel(rawLabel + "_" + i);
                }
            }
        }

    }

    private boolean isChecked(String headerValue) {
        ParquetCol col = headers.stream()
                .filter(x -> x.getValue().equals(headerValue)).findFirst().orElse(null);

        if (col == null)
            return false;

        return col.isChecked();
    }

    private void initRows() {
        parquetRows.clear();

        int size = Math.min(apmLogWrapper.getAPMLog().size(), 50);
        List<ActivityInstance> activityInstances = apmLogWrapper.getAPMLog().getActivityInstances().subList(0, size);

        parquetRows.addAll(createRows(activityInstances));
    }

    private ATrace getTrace(int index) {
        return apmLogWrapper.getAPMLog().get(index);
    }

    private List<ParquetCol> getValidCaseAttributes() {
        return getValidAttributes(APMLogData.getUniqueCaseAttributeKeys(apmLogWrapper.getAPMLog().getTraces()));
    }

    private List<ParquetCol> getValidEventAttributes() {
        return getValidAttributes(APMLogData.getUniqueEventAttributeKeys(apmLogWrapper.getAPMLog().getActivityInstances())
                .stream().filter(x -> !invalidAttributes.contains(x)).collect(Collectors.toSet()));
    }

    private List<ParquetCol> getValidAttributes(Set<String> keys) {
        return keys.stream().sorted()
                .filter(x -> !invalidAttributes.contains(x.toLowerCase()))
                .map(this::getParquetCol)
                .collect(Collectors.toList());
    }

    private ParquetCol getParquetCol(String xesCode) {
        String label;
        switch (xesCode.toLowerCase()) {
            case XESAttributeCodes.CONCEPT_NAME:
                label = "activity";
                break;
            case XESAttributeCodes.ORG_RESOURCE:
                label = "resource";
                break;
            case XESAttributeCodes.ORG_GROUP:
            case "group":
                label = "resource_group";
                xesCode = "resource_group";
                break;
            default:
                if (invalidAttributes.contains(xesCode))
                    xesCode = "_" + xesCode;

                label = getValidParquetLabel(xesCode);
                if (label.isEmpty())
                    label = xesCode;
                break;
        }
        return new ParquetCol(label, xesCode, STRING, xesCode.equalsIgnoreCase(XESAttributeCodes.CONCEPT_NAME));
    }

    public static String getValidParquetLabel(String text) {
        String label = text.replaceAll("[^A-Za-z0-9_]+", "");

        if (!label.isEmpty() && Util.isNumeric(String.valueOf(label.charAt(0))))
            label = "x" + label;

        return label;
    }

    private Schema getSchema(String charsetValue) {
        Charset charset = Charset.forName(charsetValue);
        String schemaStr = getSchemaJSON();
        byte[] bytes = schemaStr.getBytes(charset);
        String utf8EncodedString = new String(bytes, charset);
        return new Schema.Parser().parse(utf8EncodedString);
    }

    private String getSchemaJSON() {
        JSONObject schema = new JSONObject();

        schema.put("type", "record");
        schema.put("name", "event_log");
        schema.put("namespace", "apromore.org");

        JSONArray fields = new JSONArray();

        for (ParquetCol parquetCol : headers) {
            if (parquetCol.isChecked()) {
                JSONObject field = getField(parquetCol.getLabel(), parquetCol.getType());
                fields.add(field);
            }
        }

        schema.put("fields", fields);

        return schema.toJSONString();
    }

    private String getHeaderLabel(String headerCode) {
        return headers.stream().filter(x -> x.getValue().equals(headerCode))
                .map(ParquetCol::getLabel)
                .findFirst().orElse("");
    }

    public Path saveParquetFile(String chartSet,String logFileName) {
        try {
            if (!isDownloadAllowed()) {
                return null;
            }
            initRows();
            String charsetVal = encodeOptions.stream()
                    .filter(item -> item.getValue().equals(chartSet))
                    .map(EncodeOption::getValue)
                    .findFirst().orElse(UTF8);
            String filename = getValidParquetLabel(logFileName) + PARQUET_EXT;
            Schema schema = getSchema(charsetVal);
            return ParquetExport.writeAndReturnParquetFilePath(filename, getData(schema), schema);
        }catch(Exception ex){
            LoggerUtil.getLogger(ParquetExporterService.class).error("Failed to generate parquet file", ex);
        }
        return null;
    }

    private List<GenericData.Record> getData(Schema schema) {

        List<GenericData.Record> data = new ArrayList<>();

        List<List<ParquetCell>> rows = createRows(apmLogWrapper.getAPMLog().getActivityInstances());

        for (List<ParquetCell> row : rows) {
            GenericData.Record dataRecord = new GenericData.Record(schema);
            for (ParquetCell cell : row) {
                if (cell.isEnabled()) {
                    dataRecord.put(getHeaderLabel(cell.getHeaderCode()), cell.getValue());
                }
            }
            data.add(dataRecord);
        }

        return data;
    }

    private List<List<ParquetCell>> createRows(List<ActivityInstance> activityInstances) {
        List<List<ParquetCell>> rows = new ArrayList<>();

        for (ActivityInstance ai : activityInstances) {
            ATrace trace = getTrace(ai.getMutableTraceIndex());
            String caseId = trace.getCaseId();
            long startTime = ai.getStartTime();
            long endTtime = ai.getEndTime();
            String startTimestamp = Util.timestampStringOf(Util.millisecondToZonedDateTime(startTime));
            String endTimestamp = Util.timestampStringOf(Util.millisecondToZonedDateTime(endTtime));

            List<ParquetCell> cells = new ArrayList<>();

            cells.add(new ParquetCell(CASE_ID, caseId, isChecked(CASE_ID)));
            cells.add(new ParquetCell(START_TIMESTAMP, startTimestamp, isChecked(START_TIMESTAMP)));
            cells.add(new ParquetCell(END_TIMESTAMP, endTimestamp, isChecked(END_TIMESTAMP)));
            cells.add(new ParquetCell(START_TIME, startTime, isChecked(START_TIME)));
            cells.add(new ParquetCell(END_TIME, endTtime, isChecked(END_TIME)));
            cells.add(new ParquetCell(TIMESTAMP, endTtime, isChecked(TIMESTAMP)));

            for (ParquetCol parquetCol : caseAttributes) {
                Object val = trace.getAttributes().get(parquetCol.getValue());
                String valStr = val != null ? val.toString() : "";
                ParquetCell cell = new ParquetCell(parquetCol.getValue(), valStr, isChecked(parquetCol.getValue()));
                cells.add(cell);
            }

            for (ParquetCol parquetCol : eventAttributes) {
                Object val = ai.getAttributeValue(parquetCol.getValue());
                String valStr = val != null ? val.toString() : "";
                ParquetCell cell = new ParquetCell(parquetCol.getValue(), valStr, isChecked(parquetCol.getValue()));
                cells.add(cell);
            }

            rows.add(cells);
        }

        return rows;
    }
}
