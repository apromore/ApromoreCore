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
package org.apromore.plugin.parquet.export.tables.tablecontents;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.apromore.apmlog.util.NumberFormatStyle;
import org.apromore.plugin.parquet.export.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;

public class EventAttributeContentItem {
    private String value;
    private int cases;
    private int activitySize;
    private double percentage;
    private String valueFrequencyString;
    private double minDuratoin, medianDuration, averageDuration, maxDuration;
    private double oppActivitySize;

    @Data
    @ToString
    @AllArgsConstructor
    static class TempDurationStats {
        private double min;
        private double median;
        private double average;
        private double max;
    }

    public EventAttributeContentItem(String value, int cases, int activitySize, double percentage,
                                     DoubleArrayList durationStats, double oppActivitySize) {
        init(value, cases, activitySize, percentage, new TempDurationStats(durationStats.min(), durationStats.median(),
                durationStats.average(), durationStats.max()), oppActivitySize);
    }

    public EventAttributeContentItem(String value, int cases, int activitySize, double percentage,
                                     double minDuratoin, double medianDuration, double averageDuration,
                                     double maxDuration, double oppActivitySize) {
        init(value, cases, activitySize, percentage,
                new TempDurationStats(minDuratoin, medianDuration, averageDuration, maxDuration),
                oppActivitySize);
    }

    private void init(String value, int cases, int activitySize, double percentage,
                      TempDurationStats tempDurationStats, double oppActivitySize) {
        this.value = value;
        this.cases = cases;
        this.activitySize = activitySize;
        this.percentage = percentage;
        this.minDuratoin = tempDurationStats.getMin();
        this.medianDuration = tempDurationStats.getMedian();
        this.averageDuration = tempDurationStats.getAverage();
        this.maxDuration = tempDurationStats.getMax();
        valueFrequencyString = Util.getDecimalFormat().format(percentage * 100) + "%";
        this.oppActivitySize = oppActivitySize;
    }

    public String getValue() {
        return value;
    }

    public int getCases() {
        return cases;
    }

    public int getActivitySize() {
        return activitySize;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getValueFrequencyString() {
        return valueFrequencyString;
    }

    public double getOppActivitySize() {
        return oppActivitySize;
    }

    public double getMinDuration() {
        return minDuratoin;
    }

    public double getMedianDuration() {
        return medianDuration;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public double getMaxDuration() {
        return maxDuration;
    }

    public String getMinDurationString() {
        return Util.durationStringOf(minDuratoin);
    }

    public String getMedianDurationString() {
        return Util.durationStringOf(medianDuration);
    }

    public String getAverageDurationString() {
        return Util.durationStringOf(averageDuration);
    }

    public String getMaxDurationString() {
        return Util.durationStringOf(maxDuration);
    }

    public String getActivitySizeString() {
        NumberFormatStyle nfs = Util.getNumberFormatStyle();
        return nfs == null ? String.valueOf(activitySize) : nfs.getDecimalFormat().format(activitySize);
    }

    public String getCasesString() {
        NumberFormatStyle nfs = Util.getNumberFormatStyle();
        return nfs == null ? String.valueOf(cases) : nfs.getDecimalFormat().format(cases);
    }
}
