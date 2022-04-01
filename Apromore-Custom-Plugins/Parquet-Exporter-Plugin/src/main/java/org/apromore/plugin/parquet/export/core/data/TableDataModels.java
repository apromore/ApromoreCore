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

package org.apromore.plugin.parquet.export.core.data;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.util.CalendarDuration;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.plugin.parquet.export.core.data.dataobjects.CaseVariantItem;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;


public class TableDataModels {

    private TableDataModels() {
        throw new IllegalStateException("Utility class");
    }

    public static List<CaseVariantItem> getCaseVariantTableModel(LogExportItem logItem) {
        return getCaseVariantTableModel(logItem.getPLog());
    }

    public static List<CaseVariantItem> getCaseVariantTableModel(PLog pLog) {

        List<Map.Entry<String, List<Map.Entry<Integer, List<ActivityInstance>>>>> groupList =
            getCaseVariantGroups(pLog);

        Number maxCases = groupList.stream()
            .collect(Collectors.summarizingLong(x -> x.getValue().size())).getMax();

        List<CaseVariantItem> model = new ArrayList<>();
        int count = 0;

        for (Map.Entry<String, List<Map.Entry<Integer, List<ActivityInstance>>>> entry : groupList) {
            count += 1;
            int id = count;
            long actSize = entry.getValue().get(0).getValue().size();
            long caseSize = entry.getValue().size();

            double[] durArray = entry.getValue().stream()
                .mapToDouble(trace -> getDurationOf(trace, pLog.getCalendarModel())).toArray();

            DoubleArrayList dal = new DoubleArrayList(durArray);
            model.add(new CaseVariantItem(id, actSize, caseSize, maxCases.intValue(),
                dal.min(), dal.median(), dal.average(), dal.max(), entry.getValue()));
        }

        return model;
    }

    private static List<Map.Entry<String, List<Map.Entry<Integer, List<ActivityInstance>>>>>
    getCaseVariantGroups(PLog pLog) {
        return Lists.reverse(pLog.getActivityInstances().stream()
            .collect(Collectors.groupingBy(ActivityInstance::getImmutableTraceIndex)).entrySet().stream()
            .collect(Collectors.groupingBy(entry ->
                Arrays.toString(entry.getValue().stream()
                    .mapToInt(ActivityInstance::getNameIndicator).toArray()))).entrySet().stream()
            .sorted(Comparator.comparing(x -> x.getValue().size())).collect(Collectors.toList()));
    }

    private static double getDurationOf(Map.Entry<Integer, List<ActivityInstance>> traceEntry,
                                        CalendarModel calendarModel) {
        LongSummaryStatistics sts = traceEntry.getValue().stream()
            .collect(Collectors.summarizingLong(ActivityInstance::getStartTime));

        LongSummaryStatistics ets = traceEntry.getValue().stream()
            .collect(Collectors.summarizingLong(ActivityInstance::getEndTime));

        return CalendarDuration.getDuration(calendarModel, sts.getMin(), ets.getMax());
    }

}
