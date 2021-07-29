/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.apmlog.filter;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.histogram.TimeHistogram;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.typefilters.*;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class handles log filtering mechanisms for APMLog.
 * It creates PLog (pointer log) of APMLog for performing filtering without modifying the original APMLog.
 * Two filtering methods are provided
 * (1) filter then update the stats of the log
 * (2) filter without update the stats of the log in which only the indexes (BitSets) and the traceList were updated
 *
 * @author Chii Chang
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (12/03/2020)
 * Modified: Chii Chang (10/04/2020)
 * Modified: Chii Chang (15/04/2020)
 * Modified: Chii Chang (17/04/2020) - bug fixed
 * Modified: Chii Chang (26/01/2020)
 * Modified: Chii Chang (16/03/2021) - Replaced with new code
 * Modified: Chii Chang (22/06/2021)
 */
public class APMLogFilter {

    private PLog pLog;

    private static final Logger LOGGER = LoggerFactory.getLogger(APMLogFilter.class);

    public APMLogFilter(APMLog apmLog) {
        this.pLog = new PLog(apmLog);
    }

    public PLog getPLog() {
        return pLog;
    }

    public APMLog getAPMLog() throws EmptyInputException {
        return this.pLog.toImmutableLog();
    }

    /**
     * Filter PLog's indexes without update stats
     * @param logFilterRuleList
     * @return
     */
    public PLog filterIndex(List<LogFilterRule> logFilterRuleList) {
        proceedFiltering(logFilterRuleList, false);
        return pLog;
    }

    /**
     * Filter PLog and update stats
     * @param logFilterRuleList
     */
    public void filter(List<LogFilterRule> logFilterRuleList) {
        proceedFiltering(logFilterRuleList, true);
    }

    private void proceedFiltering(List<LogFilterRule> logFilterRuleList, boolean updateStats) {

        List<PTrace> traces = new ArrayList<>(pLog.getOriginalPTraces());

        for (PTrace trace : traces) {
            trace.reset();
        }

        if (logFilterRuleList != null && !logFilterRuleList.isEmpty()) {
            for (LogFilterRule rule : logFilterRuleList) {
                FilterType filterType = rule.getFilterType();
                switch (filterType) {
                    case CASE_ID:
                    case CASE_VARIANT:
                        LogStatsAnalyzer.updateCaseVariants(traces.stream().collect(Collectors.toList()));
                        traces = filterByCaseSectionCaseAttribute(rule, traces);
                        break;
                    case CASE_CASE_ATTRIBUTE:
                        traces = filterByCaseSectionCaseAttribute(rule, traces);
                        break;
                    case CASE_EVENT_ATTRIBUTE:
                        traces = filterByCaseSectEventAttribute(rule, traces);
                        break;
                    case CASE_SECTION_ATTRIBUTE_COMBINATION:
                        traces = filterByCaseSectAttributeCombination(rule, traces);
                        break;
                    case CASE_TIME:
                    case STARTTIME:
                    case ENDTIME:
                        traces = filterByCaseTime(rule, traces);
                        break;
                    case EVENT_ATTRIBUTE_DURATION:
                        traces = NodeDurationFilter.filter(rule, traces);
                        break;
                    case ATTRIBUTE_ARC_DURATION:
                        traces = filterByArcDuration(rule, traces);
                        break;
                    case DURATION:
                    case TOTAL_WAITING_TIME:
                    case AVERAGE_WAITING_TIME:
                    case MAX_WAITING_TIME:
                    case TOTAL_PROCESSING_TIME:
                    case AVERAGE_PROCESSING_TIME:
                    case MAX_PROCESSING_TIME:
                        traces = filterByDuration(rule, traces);
                        break;
                    case CASE_UTILISATION:
                        traces = filterByCaseUtilization(rule, traces);
                        break;
                    case CASE_LENGTH:
                        traces = filterByCaseLength(rule, traces);
                        break;
                    case DIRECT_FOLLOW:
                    case EVENTUAL_FOLLOW:
                        traces = filterByPath(rule, traces);
                        break;
                    case REWORK_REPETITION:
                        traces = filterByRework(rule, traces);
                        break;
                    case EVENT_EVENT_ATTRIBUTE:
                        traces = filterByEventSectAttribute(rule, traces);
                        break;
                    case EVENT_TIME:
                        traces = filterByEventSectTime(rule, traces);
                        break;
                    default:
                        break;
                }
            }
        }

        BitSet validTracesBS = new BitSet(pLog.getImmutableLog().size());
        for (PTrace trace : traces) {
            validTracesBS.set(trace.getImmutableIndex());
        }

        pLog.setValidTraceIndexBS(validTracesBS);
        if (updateStats) pLog.setPTraces(traces);
    }

    private List<PTrace> filterByCaseSectionCaseAttribute(LogFilterRule rule, List<PTrace> traces) {
        return traces.stream()
                .filter(x -> CaseSectionCaseAttributeFilter.toKeep(x, rule))
                .collect(Collectors.toList());
    }

    private List<PTrace> filterByCaseSectEventAttribute(LogFilterRule rule, List<PTrace> traces) {
        // CaseSectionEventAttributeFilter handles Choice
        return traces.stream()
                .filter(x -> CaseSectionEventAttributeFilter.toKeep(x, rule))
                .collect(Collectors.toList());
    }

    private List<PTrace> filterByCaseSectAttributeCombination(LogFilterRule rule, List<PTrace> traces) {
        // CaseSectionAttributeCombinationFilter handles Choice
        return traces.stream()
                .filter(x -> CaseSectionAttributeCombinationFilter.toKeep(x, rule))
                .collect(Collectors.toList());
    }

    public static List<PTrace> filterByCaseTime(LogFilterRule rule, List<PTrace> traces) {

        return traces.stream()
                .filter(x -> CaseTimeFilter.toKeep(x, rule))
                .collect(Collectors.toList());

    }

    private List<PTrace> filterByArcDuration(LogFilterRule rule, List<PTrace> traces) {

        return traces.stream()
                .filter(x -> AttributeArcDurationFilter.toKeep(x, rule))
                .collect(Collectors.toList());

    }

    private static List<PTrace> filterByDuration(LogFilterRule rule, List<PTrace> traces) {
        return traces.stream()
                .filter(x -> DurationFilter.toKeep(x, rule))
                .collect(Collectors.toList());
    }

    private List<PTrace> filterByCaseUtilization(LogFilterRule rule, List<PTrace> traces) {

        return traces.stream()
                .filter(x -> CaseUtilisationFilter.toKeep(x, rule))
                .collect(Collectors.toList());

    }

    private List<PTrace> filterByCaseLength(LogFilterRule rule, List<PTrace> traces) {

        return traces.stream()
                .filter(x -> CaseLengthFilter.toKeep(x, rule))
                .collect(Collectors.toList());

    }

    public List<PTrace> filterByEventSectAttribute(LogFilterRule rule, List<PTrace> traces) {
        return traces.stream()
                .filter(x -> filterActivitiesByAttribute(rule, x))
                .collect(Collectors.toList());
    }

    private boolean filterActivitiesByAttribute(LogFilterRule rule, PTrace pTrace) {
        String key = rule.getKey();

        if (rule.getPrimaryValues() == null || rule.getPrimaryValues().isEmpty()) return false;

        Set<String> values = (Set<String>) rule.getPrimaryValues().iterator().next().getObjectVal();

        List<ActivityInstance> validActs = LogStatsAnalyzer.getValidActivitiesOf(pTrace).stream()
                .filter(x -> toKeepByEventAttr(x, key, values, rule.getChoice() == Choice.RETAIN))
                .collect(Collectors.toList());

        if (validActs.isEmpty()) {
            pTrace.setValidEventIndexBS(new BitSet(pTrace.getImmutableEvents().size()));
            return false;
        }

        BitSet bitSet = new BitSet(pTrace.getOriginalActivityInstances().size());

        for (ActivityInstance act : validActs) {
            for (int index : act.getImmutableEventIndexes()) {
                bitSet.set(index);
            }
        }

        pTrace.setValidEventIndexBS(bitSet);

        return true;
    }

    private static boolean toKeepByEventAttr(ActivityInstance activity, String key, Set<String> values, boolean retain) {
        return retain ?
                activity.getAttributes().containsKey(key) && values.contains(activity.getAttributes().get(key))
                :
                !activity.getAttributes().containsKey(key) || !values.contains(activity.getAttributes().get(key));
    }

    public List<PTrace> filterByEventSectTime(LogFilterRule rule, List<PTrace> traces) {
        return traces.stream()
                .filter(x -> filterActivitiesByTime(rule, x))
                .collect(Collectors.toList());
    }

    private boolean filterActivitiesByTime(LogFilterRule rule, PTrace pTrace) {

        long fromTime = 0, toTime = 0;
        for (RuleValue ruleValue : rule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) fromTime = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) toTime = ruleValue.getLongValue();
        }

        long finalFromTime = fromTime;
        long finalToTime = toTime;
        List<ActivityInstance> validActs = LogStatsAnalyzer.getValidActivitiesOf(pTrace).stream()
                .filter(x -> toKeepByEventTime(x, finalFromTime, finalToTime, rule.getChoice() == Choice.RETAIN))
                .collect(Collectors.toList());

        if (validActs.isEmpty()) {
            pTrace.setValidEventIndexBS(new BitSet(pTrace.getImmutableEvents().size()));
            return false;
        }

        BitSet bitSet = new BitSet(pTrace.getOriginalActivityInstances().size());

        for (ActivityInstance act : validActs) {
            for (int index : act.getImmutableEventIndexes()) {
                bitSet.set(index);
            }
        }

        pTrace.setValidEventIndexBS(bitSet);

        return true;
    }

    private static boolean toKeepByEventTime(ActivityInstance activity, long startTime, long endTime, boolean retain) {
        return retain ?
                TimeHistogram.withinTime(activity.getStartTime(), activity.getEndTime(), startTime, endTime)
                :
                !TimeHistogram.withinTime(activity.getStartTime(), activity.getEndTime(), startTime, endTime);
    }

    public static List<PTrace> filterByEventTime(LogFilterRule rule, List<PTrace> traces) {
        return traces.stream()
                .filter(x -> filterEventsByTime(rule, x))
                .collect(Collectors.toList());
    }

    private static boolean filterEventsByTime(LogFilterRule rule, PTrace pTrace) {
        List<Integer> validEventIndexList = pTrace.getActivityInstances().stream()
                .filter(x -> EventTimeFilter.toKeep(x, rule))
                .flatMap(x -> x.getImmutableEventIndexes().stream())
                .collect(Collectors.toList());

        BitSet validBS = new BitSet(pTrace.getImmutableEvents().size());

        for (int validIndex : validEventIndexList) {
            validBS.set(validIndex);
        }

        pTrace.setValidEventIndexBS(validBS);

        return validEventIndexList == null || validEventIndexList.isEmpty();
    }

    private List<PTrace> filterByPath(LogFilterRule rule, List<PTrace> traces) {
        // PathFilter handles Choice
        return traces.stream()
                .filter(x -> PathFilter.toKeep(x, rule))
                .collect(Collectors.toList());
    }

    private List<PTrace> filterByRework(LogFilterRule rule, List<PTrace> traces) {
        // PathFilter handles Choice
        return traces.stream()
                .filter(x -> ReworkFilter.toKeep(x, rule))
                .collect(Collectors.toList());
    }

}
