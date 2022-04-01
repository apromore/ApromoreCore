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
package org.apromore.apmlog.csv;

import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.util.TimeUtil;
import org.apromore.apmlog.xes.XESAttributeCodes;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullLogCSV extends AbstractStats {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullLogCSV.class);

    public static final String EXPORTED_CSV_PREFIX = "DASHBOARD_";
    public static final String EXPORTED_CSV_SUFFIX = ".CSV";

    public static Path getCSVFile(PLog log, String encoding) {

        try {
            // create a temporary file
            Path tempCSV = Files.createTempFile(EXPORTED_CSV_PREFIX, EXPORTED_CSV_SUFFIX);

            tempCSV.toFile().deleteOnExit();
            LOGGER.debug("Create temp CSV path \"{}\"", tempCSV);

            StringBuilder sb = new StringBuilder();

            List<String> caseAttrNames = getCaseAttrNames(log);
            Collections.sort(caseAttrNames);
            List<String> eventAttrNames =
                    new ArrayList<>(LogStatsAnalyzer.getUniqueEventAttributeKeys(log.getActivityInstances()));
            Collections.sort(eventAttrNames);

            sb.append(getEventBasedCSVHeader(caseAttrNames, eventAttrNames));

            // write headers
            Files.write(tempCSV, sb.toString().getBytes(Charset.forName(encoding)));
            //empty StringBuilder
            sb.setLength(0);

            for (PTrace trace : log.getPTraces()) {
                String caseAttrsString = getCaseAttributeString(trace, caseAttrNames);

                String caseId = trace.getCaseId();

                for (ActivityInstance activityInstance : trace.getActivityInstances()) {
                    sb.append(getActivityInstanceRow(activityInstance, caseId, caseAttrsString,
                            eventAttrNames));
                    Files.write(tempCSV, sb.toString().getBytes(Charset.forName(encoding)),
                            StandardOpenOption.APPEND);
                    //empty StringBuilder
                    sb.setLength(0);
                }
            }

            if (Files.notExists(tempCSV)) {
                LOGGER.debug("The temp CSV path \"{}\" doesn't exist!", tempCSV);
                return null;
            }

            return tempCSV;

        } catch (IOException e) {
            LOGGER.error("Error occurred while creating temp CSV file: " + e.getMessage(), e);
            return null;
        }
    }

    private static String getActivityInstanceRow(ActivityInstance activityInstance,
                                                        String caseId,
                                                        String caseAttrsString,
                                                        List<String> eventAttrNames) {
        StringBuilder row = new StringBuilder();
        row.append(caseId).append(",");
        row.append(TimeUtil.millisecondToLocalDateTime(activityInstance.getStartTime())).append(",");
        row.append(TimeUtil.millisecondToLocalDateTime(activityInstance.getEndTime())).append(",");
        row.append(caseAttrsString);

        UnifiedMap<String, String> eventAttrValMap = activityInstance.getAttributes();

        for (int i = 0; i < eventAttrNames.size(); i++) {
            String attrKey = eventAttrNames.get(i);
            String val = "";
            if (eventAttrValMap.containsKey(attrKey)) {
                val = eventAttrValMap.get(attrKey);
            }
            row.append(val);
            if (i < eventAttrNames.size() - 1) {
                row.append(",");
            }
        }

        row.append("\n");

        return row.toString();
    }

    private static String getEventBasedCSVHeader(List<String> caseAttrNames, List<String> eventAttrNames) {


        StringBuilder headRow = new StringBuilder();
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.CASE_ID));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.START_TIME));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.END_TIME));

        for (String attrName : caseAttrNames) {
            headRow.append(StringValidation.getValidString(attrName)).append(",");
        }

        for (int i = 0; i < eventAttrNames.size(); i++) {
            String eav = eventAttrNames.get(i);

            headRow.append(XESAttributeCodes.getDisplayLabelForSingle(eav).replace(" ", "_"));

            if (i < (eventAttrNames.size() - 1)) {
                headRow.append(",");
            }
        }

        headRow.append("\n");

        return headRow.toString();
    }

}
