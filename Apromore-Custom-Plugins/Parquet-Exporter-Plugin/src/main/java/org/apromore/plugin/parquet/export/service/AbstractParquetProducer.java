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


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apromore.apmlog.csv.StringValidation;
import static org.apromore.apmlog.xes.XESAttributeCodes.CONCEPT_NAME;
import static org.apromore.apmlog.xes.XESAttributeCodes.ORG_GROUP;
import static org.apromore.apmlog.xes.XESAttributeCodes.ORG_RESOURCE;
import static org.apromore.apmlog.xes.XESAttributeCodes.ORG_ROLE;
import org.apromore.plugin.parquet.export.core.data.APMLogData;
import org.apromore.plugin.parquet.export.core.data.LogExportItem;
import org.apromore.plugin.parquet.export.util.Util;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractParquetProducer {

    protected static final String DOUBLE_TYPE = "double";

    protected static List<Pair<String, String>> getAttributeHeaders(LogExportItem logItem, boolean caseSection) {
        List<String> headers = new ArrayList<>(caseSection ?
                APMLogData.getUniqueCaseAttributeKeys(logItem.getPLog())
                :
                APMLogData.getUniqueEventAttributeKeys(logItem.getPLog().getActivityInstances()));

        removeInvalidAttributeKeys(headers);

        Collections.sort(headers);

        return headers.stream()
                .map(AbstractParquetProducer::getValidAttributeKey)
                .collect(Collectors.toList());
    }

    private static void removeInvalidAttributeKeys(List<String> headers) {
        headers.remove("Activity");
        headers.remove("Resource");
        headers.remove("Group");
    }

    /**
     *
     * @param key - XES attribute code
     * @return Pair < keyForDisplay, originalKey >
     */
    protected static Pair<String, String> getValidAttributeKey(String key) {
        String code;

        switch (key) {
            case CONCEPT_NAME:
                code = "activity";
                break;
            case ORG_RESOURCE:
                code = "resource";
                break;
            case ORG_ROLE:
                code = "role";
                break;
            case "group":
            case ORG_GROUP:
                code = "resource_group";
                break;
            default:
                code = key;
                break;
        }


        String display = code.replaceAll("[^A-Za-z0-9_]+", "");

        if (display.isEmpty())
            display = Arrays.toString(key.getBytes()).replaceAll("[^A-Za-z0-9_]+", "");

        if (Util.isNumeric(String.valueOf(display.charAt(0))))
            display = "x" + display;

        return new ImmutablePair<>(StringValidation.getValidString(display), key);
    }

    protected static JSONObject getField(String name, String type2) {
        return getField(name, "null", type2, null);
    }

    protected static JSONObject getField(String name, String type1, String type2, Object defaultVal) {
        JSONObject filed = new JSONObject();
        filed.put("name", name);
        JSONArray jaFieldType = new JSONArray();
        jaFieldType.add("null");
        jaFieldType.add(type2);
        filed.put("type", jaFieldType);
        filed.put("default", null);
        return filed;
    }

    protected static void appendCommonDurationFields(JSONArray fields) {
        JSONObject filedMinDur = getField("min_duration", DOUBLE_TYPE);
        JSONObject filedMedDur = getField("median_duration", DOUBLE_TYPE);
        JSONObject filedAvgDur = getField("average_duration",  DOUBLE_TYPE);
        JSONObject filedMaxDur = getField("max_duration",  DOUBLE_TYPE);

        fields.add(filedMinDur);
        fields.add(filedMedDur);
        fields.add(filedAvgDur);
        fields.add(filedMaxDur);
    }
}
