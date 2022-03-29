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
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.util.TimeUtil;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CasesCSV extends AbstractStats {

    private static final Logger LOGGER = LoggerFactory.getLogger(CasesCSV.class);


    public static Path getCSVFile(PLog log, String encoding) {

        try {
            // create a temporary file
            Path tempCSV = Files.createTempFile(FullLogCSV.EXPORTED_CSV_PREFIX, FullLogCSV.EXPORTED_CSV_SUFFIX);

            StringBuilder data = new StringBuilder();

            data.append(getHeader());

            Files.write(tempCSV, data.toString().getBytes(Charset.forName(encoding)));
            //empty StringBuilder
            data.setLength(0);

            for (PTrace trace : log.getPTraces()) {

                StringBuilder row = new StringBuilder();
                row.append(StringValidation.getValidString(trace.getCaseId())).append(",");
                row.append(trace.getActivityInstances().size()).append(",");
                row.append(TimeUtil.millisecondToZonedDateTime(trace.getStartTime())).append(",");
                row.append(TimeUtil.millisecondToZonedDateTime(trace.getEndTime())).append(",");
                row.append(ContentLabels.getFloatDurationOf(trace.getDuration())).append(",");
                row.append(LogStatsAnalyzer.getCaseUtilizationOf(trace) * 100).append(",");

                DoubleArrayList procTimes = LogStatsAnalyzer.getProcessingTimesOf(trace);
                DoubleArrayList waitTImes = LogStatsAnalyzer.getWaitingTimesOf(trace);

                row.append(ContentLabels.getFloatDurationOf(procTimes.sum())).append(",");
                row.append(ContentLabels.getFloatDurationOf(procTimes.average())).append(",");
                row.append(ContentLabels.getFloatDurationOf(procTimes.max())).append(",");
                row.append(ContentLabels.getFloatDurationOf(waitTImes.sum())).append(",");
                row.append(ContentLabels.getFloatDurationOf(waitTImes.average())).append(",");
                row.append(ContentLabels.getFloatDurationOf(waitTImes.max()));
                row.append("\n");

                data.append(row);

                Files.write(tempCSV, data.toString().getBytes(Charset.forName(encoding)),
                        StandardOpenOption.APPEND);
                //empty StringBuilder
                data.setLength(0);
            }
            return tempCSV;

        } catch (IOException e) {
            LOGGER.error("Error occurred while creating temp CSV file: " + e.getMessage(), e);
            return null;
        }
    }

    private static StringBuilder getHeader() {

        StringBuilder headRow = new StringBuilder();
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.CASE_ID));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.ACTIVITY_INSTANCES));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.START_TIME));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.END_TIME));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.DURATION));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.CASE_UTILIZATION));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.TOTAL_PROCESSING_TIME));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.AVERAGE_PROCESSING_TIME));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.MAX_PROCESSING_TIME));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.TOTAL_WAITING_TIME));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.AVERAGE_WAITING_TIME));
        headRow.append(HeaderLabels.getLabelWithComma(HeaderLabels.MAX_WAITING_TIME));
        headRow.append("\n");

        return headRow;
    }
}
