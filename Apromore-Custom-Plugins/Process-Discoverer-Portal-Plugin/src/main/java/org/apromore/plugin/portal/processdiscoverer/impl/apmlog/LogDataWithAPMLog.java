/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.processdiscoverer.impl.apmlog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.stats.AAttributeGraph;
import org.apromore.apmlog.stats.AAttributeValue;
import org.apromore.logman.ALog;
import org.apromore.logman.LogBitMap;
import org.apromore.logman.attribute.log.AttributeInfo;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.plugin.portal.processdiscoverer.data.CaseDetails;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.LogData;
import org.apromore.plugin.portal.processdiscoverer.data.PerspectiveDetails;
import org.apromore.commons.datetime.DateTimeUtil;
import org.apromore.commons.datetime.DurationUtil;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

/**
 * LogDataWithAPMLog is {@link LogData} but uses APMLog to do filtering and some statistics data.
 * 
 * @author Bruce Nguyen
 *
 */
public class LogDataWithAPMLog extends LogData {
    private APMLog originalAPMLog; 
    private APMLog filteredAPMLog;
    private PLog filteredPLog;
    private APMLogFilter apmLogFilter;
    
    public LogDataWithAPMLog(ConfigData configData, ALog log, APMLog apmLog) {
        super(configData, log);
        this.originalAPMLog = apmLog;
        this.filteredAPMLog = apmLog;
        this.filteredPLog = new PLog(apmLog);
        apmLogFilter = new APMLogFilter(apmLog);
    }
    
    public APMLog getOriginalAPMLog() {
        return this.originalAPMLog;
    }
    
    public APMLog getFilteredAPMLog() {
        return this.filteredAPMLog;
    } 
    
    public PLog getFilteredPLog() {
        return this.filteredPLog;
    }
    
    private boolean filter(LogFilterRule filterCriterion) throws Exception {
        List<LogFilterRule> criteria = (List<LogFilterRule>)currentFilterCriteria;
        criteria.add(filterCriterion);
        this.apmLogFilter.filter(criteria); 
        if (apmLogFilter.getPLog().getPTraceList().isEmpty()) { // Restore to the last state
            apmLogFilter.filter((List<LogFilterRule>)currentFilterCriteria);
            return false;
        } else {
            this.updateLog(apmLogFilter.getPLog(), apmLogFilter.getApmLog());
            currentFilterCriteria = criteria;
            return true;
        }
    }
    
    private Set<RuleValue> getEventAttributeRuleValue(String value, String attributeKey, FilterType filterType) {
        Set<RuleValue> ruleValues = new HashSet<>();
        ruleValues.add(new RuleValue(filterType, OperationType.EQUAL, attributeKey, value));
        return ruleValues;
    }
    
    private Set<RuleValue> getDirectFollowRuleValue(String value, String attributeKey) {
        String[] edgeParts = value.split(" => ");
        RuleValue from = new RuleValue(FilterType.DIRECT_FOLLOW, OperationType.FROM, attributeKey, edgeParts[0]);
        RuleValue to = new RuleValue(FilterType.DIRECT_FOLLOW, OperationType.TO, attributeKey, edgeParts[1]);
        return new HashSet<RuleValue>(Arrays.asList(new RuleValue[] {from,to}));
    }
    
    private LogFilterRule getEventAttributeFilterRule(String attributeKey, 
                                                Choice choice, Section section, Inclusion inclusion, 
                                                Set<RuleValue> values) {
        FilterType filterType =
                section == Section.CASE ? FilterType.CASE_EVENT_ATTRIBUTE : FilterType.EVENT_EVENT_ATTRIBUTE;

        return new LogFilterRuleImpl(choice, inclusion, section,
                filterType,
                attributeKey,
                values,
                null);
    }
    
    private LogFilterRule getDirectFollowFilterRule(String attributeKey, 
                                            Choice choice, Section section, Inclusion inclusion, 
                                            Set<RuleValue> values) {
        
        return new LogFilterRuleImpl(choice, inclusion, section, 
                                    FilterType.DIRECT_FOLLOW,
                                    attributeKey,
                                    values,
                                    null);
    }    
  
    @Override
    public boolean filter_RemoveTracesAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return filter(getEventAttributeFilterRule(attKey, Choice.REMOVE, Section.CASE, Inclusion.ANY_VALUE,
                    getEventAttributeRuleValue(value, attKey, FilterType.CASE_EVENT_ATTRIBUTE)));
    }
    
    @Override
    public boolean filter_RetainTracesAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return filter(getEventAttributeFilterRule(attKey, Choice.RETAIN, Section.CASE, Inclusion.ANY_VALUE,
                getEventAttributeRuleValue(value, attKey, FilterType.CASE_EVENT_ATTRIBUTE)));
    }
    
    @Override
    public boolean filter_RemoveTracesAllValueOfEventAttribute(String value, String attKey) throws Exception {
        return filter(getEventAttributeFilterRule(attKey, Choice.REMOVE, Section.CASE, Inclusion.ALL_VALUES,
                getEventAttributeRuleValue(value, attKey, FilterType.CASE_EVENT_ATTRIBUTE)));
    }
    
    @Override
    public boolean filter_RetainTracesAllValueOfEventAttribute(String value, String attKey) throws Exception {
        return filter(getEventAttributeFilterRule(attKey, Choice.RETAIN, Section.CASE, Inclusion.ALL_VALUES,
                getEventAttributeRuleValue(value, attKey, FilterType.CASE_EVENT_ATTRIBUTE)));        
    }
    
    @Override
    public boolean filter_RemoveEventsAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return filter(getEventAttributeFilterRule(attKey, Choice.REMOVE, Section.EVENT, Inclusion.ANY_VALUE,
                getEventAttributeRuleValue(value, attKey, FilterType.EVENT_EVENT_ATTRIBUTE)));
    }
    
    @Override
    public boolean filter_RetainEventsAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return filter(getEventAttributeFilterRule(attKey, Choice.RETAIN, Section.EVENT, Inclusion.ANY_VALUE,
                getEventAttributeRuleValue(value, attKey, FilterType.EVENT_EVENT_ATTRIBUTE)));
    }
    
    @Override
    public boolean filter_RemoveTracesAnyValueOfDirectFollowRelation(String value, String attKey) throws Exception {
        return filter(getDirectFollowFilterRule(attKey, Choice.REMOVE, Section.CASE, Inclusion.ANY_VALUE,
                getDirectFollowRuleValue(value, attKey)));
    }
    
    @Override
    public boolean filter_RetainTracesAnyValueOfDirectFollowRelation(String value, String attKey) throws Exception {
        return filter(getDirectFollowFilterRule(attKey, Choice.RETAIN, Section.CASE, Inclusion.ANY_VALUE,
                getDirectFollowRuleValue(value, attKey)));
    }

    @Override
    public boolean hasSufficientDurationVariant(String attribute, String value) {
        AAttributeGraph aAttributeGraph = this.originalAPMLog.getAAttributeGraph();
        AAttributeValue aAttributeValue = aAttributeGraph.getAttributeValue(attribute, value);
        return (aAttributeValue.getDurationsIndexMap().size() > 1);
    }

    @Override
    public boolean hasSufficientDurationVariant(String attribute, String inDegree, String outDegree) {
        AAttributeGraph aAttributeGraph = this.originalAPMLog.getAAttributeGraph();
        UnifiedSet<Double> durations = aAttributeGraph.getDurations(attribute, inDegree, outDegree, this.originalAPMLog);
        return (durations.size() > 1);
    }

    @Override
    public List<CaseDetails> getCaseDetails() {
        UnifiedMap<List<Integer>, Integer> actNameListCaseSizeMap = new UnifiedMap<>();
        List<ATrace> traceList = filteredAPMLog.getTraceList();
        int traceSize = traceList.size();
        
        for (int i = 0; i < traceSize; i++) {
            ATrace aTrace = traceList.get(i);
            List<Integer> actNameIndxList = aTrace.getActivityNameIndexList();
            if (actNameListCaseSizeMap.containsKey(actNameIndxList)) {
                int caseSize = actNameListCaseSizeMap.get(actNameIndxList) + 1;
                actNameListCaseSizeMap.put(actNameIndxList, caseSize);
            } else {
                actNameListCaseSizeMap.put(actNameIndxList, 1);
            }
        }

        List<CaseDetails> listResult = new ArrayList<CaseDetails>();
        for (int i = 0; i < traceSize; i++) {
            ATrace aTrace = traceList.get(i);
            String caseId = aTrace.getCaseId();
            int caseEvents = aTrace.getActivityList().size();
            int caseVariantId = aTrace.getCaseVariantId();
            List<Integer> actNameIdxList = aTrace.getActivityNameIndexList();
            int caseSize = actNameListCaseSizeMap.get(actNameIdxList);
            double caseVariantFreq = (double) caseSize / traceSize;
            CaseDetails caseDetails = new CaseDetails(caseId, caseEvents, caseVariantId, caseVariantFreq);
            listResult.add(caseDetails);
        }   
        
        return listResult;
    }
    
    @Override
    public List<PerspectiveDetails> getActivityDetails() {
        List<PerspectiveDetails> listResult = new ArrayList<PerspectiveDetails>();
        for (AttributeInfo info : this.getAttributeInfoList()) {
            ArrayList<String> cells = new ArrayList<>();

            String value = info.getAttributeValue();
            long occurrences = info.getAttributeOccurrenceCount();
            double freq = info.getAttributeOccurrenceFrequency();

            PerspectiveDetails perspectiveDetails = new PerspectiveDetails(value, occurrences, freq);
            listResult.add(perspectiveDetails);
        }
        return listResult;
    }
    
    @Override
    public String getFilteredStartTime() {
        return DateTimeUtil.humanize(this.filteredAPMLog.getStartTime());
    }

    @Override
    public String getFilteredEndTime() {
        return DateTimeUtil.humanize(this.filteredAPMLog.getEndTime());
    }

    @Override
    public String getFilteredMinDuration() {
        return DurationUtil.humanize(this.filteredAPMLog.getMinDuration(), true);
    }

    @Override
    public String getFilteredMedianDuration() {
        return DurationUtil.humanize(this.filteredAPMLog.getMedianDuration(), true);
    }

    @Override
    public String getFilteredMeanDuration() {
        return DurationUtil.humanize(this.filteredAPMLog.getAverageDuration(), true);
    }

    @Override
    public String getFilteredMaxDuration() {
        return DurationUtil.humanize(this.filteredAPMLog.getMaxDuration(), true);
    }
    
    public void updateLog(PLog pLog, APMLog apmLog) throws Exception {
        this.filteredAPMLog = apmLog;
        this.filteredPLog = pLog;
        List<PTrace> pTraces = pLog.getCustomPTraceList(); 
        
        LogBitMap logBitMap = new LogBitMap(aLog.getOriginalTraces().size());
        logBitMap.setTraceBitSet(pLog.getValidTraceIndexBS(), pTraces.size());
        
        for (int i=0; i<pTraces.size(); i++) {
            logBitMap.addEventBitSet(pTraces.get(i).getValidEventIndexBitSet(), aLog.getOriginalTraceFromIndex(i).getOriginalEvents().size());
        }
        aLog.updateLogStatus(logBitMap);
        attLog.refresh();
        
        //Use for debugging the bitset transfer from PLog to ALog/AttributeLog
        //printPLogBitMap(pLog);
        //printALogBitMap(aLog);
        //printAttributeLogBitMap(attLog);
    }
    
    // For debug only
    private void printPLogBitMap(PLog log) {
        BitSet bitSet = log.getValidTraceIndexBS();
        List<PTrace> pTraces = log.getCustomPTraceList(); 
        System.out.println("PLog trace status (trace_number:bit): ");
        for (int i=0; i<pTraces.size(); i++) {
            System.out.print(i + ":" + (bitSet.get(i) ? "1" : "0") + ",");
        }
        System.out.print("end");
        System.out.println();
        
        System.out.println("PLog trace event status: ");
        for (int i=0; i<pTraces.size(); i++) {
            System.out.println("Trace " + i + " event status (event_number:bit):");
            BitSet eventBitSet = pTraces.get(i).getValidEventIndexBitSet();
            for (int j=0; j<pTraces.get(i).getOriginalEventList().size(); j++) {
                System.out.print(j + ":" + (eventBitSet.get(j) ? "1" : "0") + ",");
            }
            System.out.print("end");
            System.out.println();
        }
        
    }
    
    // For debug only
    private void printALogBitMap(ALog log) {
        BitSet bitSet = log.getOriginalTraceStatus();
        System.out.println("ALog trace status (trace_number:bit): ");
        for (int i=0; i<log.getOriginalTraces().size(); i++) {
            System.out.print(i + ":" + (bitSet.get(i) ? "1" : "0") + ",");
        }
        System.out.print("end");
        System.out.println();
        
        System.out.println("ALog trace event status: ");
        for (int i=0; i<log.getOriginalTraces().size(); i++) {
            System.out.println("Trace " + i + " event status (event_number:bit):");
            BitSet eventBitSet = log.getOriginalTraceFromIndex(i).getOriginalEventStatus();
            for (int j=0; j<log.getOriginalTraceFromIndex(i).getOriginalEvents().size(); j++) {
                System.out.print(j + ":" + (eventBitSet.get(j) ? "1" : "0") + ",");
            }
            System.out.print("end");
            System.out.println();
        }
    }
    
    private void printAttributeLogBitMap(AttributeLog log) {
        BitSet bitSet = log.getOriginalTraceStatus();
        System.out.println("AttributeLog trace status (trace_number:bit): ");
        for (int i=0; i<log.getOriginalTraces().size(); i++) {
            System.out.print(i + ":" + (bitSet.get(i) ? "1" : "0") + ",");
        }
        System.out.print("end");
        System.out.println();
        
        System.out.println("AttributeLog trace (aggregated) event status: ");
        for (int i=0; i<log.getOriginalTraces().size(); i++) {
            System.out.println("Trace " + i + " event status (event_number:bit):");
            BitSet eventBitSet = log.getOriginalTraceFromIndex(i).getOriginalEventStatus();
            // Don't count the artificial start and end events
            for (int j=1; j<(log.getOriginalTraceFromIndex(i).getOriginalValueTrace().size()-1); j++) {
                System.out.print((j-1) + ":" + (eventBitSet.get(j) ? "1" : "0") + ",");
            }
            System.out.print("end");
            System.out.println();
        }
    }
}
