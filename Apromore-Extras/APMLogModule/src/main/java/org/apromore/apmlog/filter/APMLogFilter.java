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

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.typefilters.*;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
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


    private APMLog finalAPMLog;

    public APMLog getFinalAPMLog() {
        if (finalAPMLog == null) finalAPMLog = pLog.toAPMLog();
        return this.finalAPMLog;
    }

    public APMLog getApmLog() {
        finalAPMLog = this.pLog.toAPMLog();
        return finalAPMLog;
    }

    /**
     * Filter PLog's indexes without update stats
     * @param logFilterRuleList
     * @return
     */
    public PLog filterIndex(List<LogFilterRule> logFilterRuleList) {
        proceedFiltering(logFilterRuleList);
        return pLog;
    }

    /**
     * Filter PLog and update stats
     * @param logFilterRuleList
     */
    public void filter(List<LogFilterRule> logFilterRuleList) {
        proceedFiltering(logFilterRuleList);
        pLog.updateStats();
    }

    private void proceedFiltering(List<LogFilterRule> logFilterRuleList) {
        // Reset indexes
        List<PTrace> traces = new ArrayList<>(pLog.getOriginalPTraceList());
        for (PTrace pTrace : traces) {
            pTrace.setValidEventIndexBS(pTrace.getOriginalValidEventIndexBS());
        }

        if (logFilterRuleList != null && !logFilterRuleList.isEmpty()) {
            for (LogFilterRule rule : logFilterRuleList) {
                FilterType filterType = rule.getFilterType();
                switch (filterType) {
                    case CASE_ID:
                    case CASE_VARIANT:
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
                        traces = filterByNodeDuration(rule, traces);
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
                        traces = filterByEventTime(rule, traces);
                        break;
                    default:
                        break;
                }
            }
        }

        BitSet validTracesBS = new BitSet(pLog.getOriginalPTraceList().size());
        for (PTrace trace : traces) {
            validTracesBS.set(trace.getImmutableIndex());
        }

        pLog.setValidTraceIndexBS(validTracesBS);
        pLog.setPTraceList(traces);
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

    private List<PTrace> filterByNodeDuration(LogFilterRule rule, List<PTrace> traces) {

        return traces.stream()
                .filter(x -> EventAttributeDurationFilter.toKeep(x, rule))
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

    private List<PTrace> filterByEventSectAttribute(LogFilterRule rule, List<PTrace> traces) {
        return traces.stream()
                .filter(x -> filterActivitiesByAttribute(rule, x))
                .collect(Collectors.toList());
    }

    private boolean filterActivitiesByAttribute(LogFilterRule rule, PTrace pTrace) {
        String key = rule.getKey();

        if (rule.getPrimaryValues() == null || rule.getPrimaryValues().isEmpty()) return false;

        Set<String> values = (Set<String>) rule.getPrimaryValues().iterator().next().getObjectVal();

        List<AActivity> validActs = pTrace.getOriginalActivityList().stream()
                .filter(x -> pTrace.getValidEventIndexBitSet().get(x.getEventIndexes().get(0)))
                .collect(Collectors.toList());

        List<AActivity> matched = pTrace.getOriginalActivityList().stream()
                .filter(x -> x.getAllAttributes().containsKey(key) && values.contains(x.getAllAttributes().get(key)))
                .collect(Collectors.toList());

        if (rule.getChoice() == Choice.RETAIN) {
            validActs = matched;
        } else {
            validActs.removeAll(matched);
        }

        if (validActs.size() == 0) return false;

        // update valid event index BitSet
        pTrace.setValidEventIndexBS(new BitSet(pTrace.getOriginalValidEventIndexBS().size()));

        for (AActivity activity : validActs) {
            updateValidEventBitSet(pTrace, activity);
        }

        return true;
    }

    public static List<PTrace> filterByEventTime(LogFilterRule rule, List<PTrace> traces) {
        return traces.stream()
                .filter(x -> filterEventsByTime(rule, x))
                .collect(Collectors.toList());
    }

    private static boolean filterEventsByTime(LogFilterRule rule, PTrace pTrace) {
        List<AEvent> validEvents = pTrace.getOriginalEventList().stream()
                .filter(x -> pTrace.getValidEventIndexBitSet().get(x.getIndex()) &&
                        EventTimeFilter.toKeep(x, rule))
                .collect(Collectors.toList());
        pTrace.getEventList().clear();
        pTrace.getEventList().addAll(validEvents);
        pTrace.setValidEventIndexBS(new BitSet(pTrace.getOriginalEventList().size()));
        for (AEvent aEvent : validEvents) {
            pTrace.getValidEventIndexBitSet().set(aEvent.getIndex());
        }
        return !validEvents.isEmpty();
    }

    private static void updateValidEventBitSet(PTrace trace, AActivity activity) {
        for (int i = 0; i < activity.getEventIndexes().size(); i++) {
            trace.getValidEventIndexBitSet().set(activity.getEventIndexes().get(i));
        }
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

    private List<PTrace> filterByDirectlyFollows(LogFilterRule rule, List<PTrace> traces) {
        String attrKey = rule.getKey();
        Choice choice = rule.getChoice();

        UnifiedSet<String> fromVals = rule.getPrimaryValues().stream()
                .filter(x -> x.getOperationType() == OperationType.FROM)
                .map(x -> x.getStringValue())
                .collect(Collectors.toCollection(UnifiedSet::new));

        UnifiedSet<String> toVals = rule.getPrimaryValues().stream()
                .filter(x -> x.getOperationType() == OperationType.TO)
                .map(x -> x.getStringValue())
                .collect(Collectors.toCollection(UnifiedSet::new));

        return traces.stream()
                .filter(x -> containsOneOfDirectlyFollows(choice, attrKey, fromVals, toVals, x))
                .collect(Collectors.toList());
    }

    private boolean containsOneOfDirectlyFollows(Choice choice,
                                                 String key,
                                                 UnifiedSet<String> fromVals,
                                                 UnifiedSet<String> toVals,
                                                 PTrace pTrace) {


        List<AActivity> validActs = pTrace.getOriginalActivityList().stream()
                .filter(x -> pTrace.getValidEventIndexBitSet().get(x.getImmutableEventList().get(0).getIndex()))
                .collect(Collectors.toList());

        boolean match = false;

        for (int i = 0; i < validActs.size() - 1; i++) {
            AActivity act1 = validActs.get(i);
            AActivity act2 = validActs.get(i + 1);
            if (
                    (act1.getAllAttributes().containsKey(key) &&
                            fromVals.contains(act1.getAllAttributes().get(key)))
                            &&
                            (act2.getAllAttributes().containsKey(key) &&
                                    toVals.contains(act2.getAllAttributes().get(key)))
            ) {
                match = true;
                break;
            }
        }

        return (match && choice == Choice.RETAIN) || (!match && choice == Choice.REMOVE);
    }
}
