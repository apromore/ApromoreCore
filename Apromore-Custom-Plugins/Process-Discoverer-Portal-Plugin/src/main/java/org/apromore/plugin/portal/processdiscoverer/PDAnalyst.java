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

package org.apromore.plugin.portal.processdiscoverer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.commons.datetime.DurationUtils;
import org.apromore.logman.ALog;
import org.apromore.logman.LogBitMap;
import org.apromore.logman.attribute.AbstractAttribute;
import org.apromore.logman.attribute.AttributeType;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeInfo;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.processdiscoverer.data.CaseDetails;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.InvalidDataException;
import org.apromore.plugin.portal.processdiscoverer.data.NotFoundAttributeException;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.PerspectiveDetails;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.impl.json.ProcessJSONVisualizer;
import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.slf4j.Logger;

/**
 * PDAnalyst represents a process analyst who will performs log analysis in the form of graphs and BPMN diagrams
 * PDAnalyst has a number of tools to do its job:
 * <p><ul>
 * <li>It uses {@link ProcessDiscoverer} which provides the graph and BPMN diagram logic
 * <li>It uses {@link ProcessVisualizer} to serialize the analysis result in a form suitable for visualization
 * <li>It uses {@link LogAPMLogFilter} to do log filtering
 * </ul>
 * @author Bruce Nguyen
 */
public class PDAnalyst {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(PDAnalyst.class);

    // Graph/BPMN analysis tool
    private ProcessDiscoverer processDiscoverer;

    // Visualization tool
    private ProcessVisualizer processVisualizer;

    // Log management tool
    private ALog aLog;
    private AttributeLog attLog;
    private Object currentFilterCriteria = new ArrayList<LogFilterRule>(); // list of log filter criteria
    private IndexableAttribute mainAttribute;
    private ImmutableList<AbstractAttribute> indexableAttributes;

    // Log filtering tool
    private APMLog originalAPMLog;
    private APMLog filteredAPMLog;
    // ==========================================
    // use PLog mainly for indexing purpose only.
    // use filteredAPMLog for the updated stats
    // ==========================================
    private PLog filteredPLog;
    private APMLogFilter apmLogFilter;

    private int sourceLogId; // plugin maintain log ID for Filter; Filter remove value to avoid conflic from multiple plugins

    public PDAnalyst(ContextData contextData, ConfigData configData, EventLogService eventLogService) throws Exception {
        XLog xlog = eventLogService.getXLog(contextData.getLogId());
        APMLog apmLog = eventLogService.getAggregatedLog(contextData.getLogId());
        if (xlog == null) {
            throw new InvalidDataException("XLog data of this log is missing");
        }
        if (apmLog == null) {
            throw new InvalidDataException("APMLog data of this log is missing");
        }
        long timer = System.currentTimeMillis();
        this.aLog = new ALog(xlog);
        LOGGER.debug("ALog.constructor: {} ms.", System.currentTimeMillis() - timer);
        indexableAttributes = aLog.getAttributeStore().getPerspectiveEventAttributes(
                configData.getMaxNumberOfUniqueValues(), AttributeType.BOOLEAN);

        this.originalAPMLog = apmLog;
        this.filteredAPMLog = apmLog;
        this.filteredPLog = new PLog(apmLog);
        apmLogFilter = new APMLogFilter(apmLog);

        // ProcessDiscoverer logic with default attribute
        this.setMainAttribute(configData.getDefaultAttribute());
        this.processDiscoverer = new ProcessDiscoverer(this.attLog);
        this.processVisualizer = new ProcessJSONVisualizer();
    }

    // Without filter, for testing only
    private PDAnalyst(ALog aLog, ConfigData configData) throws Exception {
        this.aLog = aLog;
        indexableAttributes = aLog.getAttributeStore().getPerspectiveEventAttributes(
                configData.getMaxNumberOfUniqueValues(), AttributeType.BOOLEAN);
        this.setMainAttribute(configData.getDefaultAttribute());
        this.processDiscoverer = new ProcessDiscoverer(this.attLog);
        this.processVisualizer = new ProcessJSONVisualizer();
    }

    public static PDAnalyst newInstanceWithoutFilter(ALog aLog, ConfigData configData) throws Exception {
        return new PDAnalyst(aLog, configData);
    }

    public void cleanUp() {
        processDiscoverer.cleanUp();
        processVisualizer.cleanUp();
    }

    private AbstractionParams genAbstractionParams(UserOptionsData userOptions) {
        return new AbstractionParams(
                this.getMainAttribute(),
                userOptions.getNodeFilterValue() / 100,
                userOptions.getArcFilterValue() / 100,
                userOptions.getParallelismFilterValue() / 100,
                true, true,
                userOptions.getInvertedNodesMode(),
                userOptions.getInvertedArcsMode(),
                userOptions.getIncludeSecondary(),
                userOptions.getFixedType(),
                userOptions.getFixedAggregation(),
                userOptions.getFixedRelation(),
                userOptions.getPrimaryType(),
                userOptions.getPrimaryAggregation(),
                userOptions.getPrimaryRelation(),
                userOptions.getSecondaryType(),
                userOptions.getSecondaryAggregation(),
                userOptions.getSecondaryRelation(),
                userOptions.getRelationReader(),
                null);
    }

    private AbstractionParams genAbstractionParamsForTrace(UserOptionsData userOptions) {
        return new AbstractionParams(
                this.getMainAttribute(),
                userOptions.getNodeFilterValue() / 100,
                userOptions.getArcFilterValue() / 100,
                userOptions.getParallelismFilterValue() / 100,
                false, true,
                userOptions.getInvertedNodesMode(),
                userOptions.getInvertedArcsMode(),
                false,
                userOptions.getFixedType(),
                userOptions.getFixedAggregation(),
                userOptions.getFixedRelation(),
                MeasureType.DURATION,
                MeasureAggregation.CASES,
                MeasureRelation.ABSOLUTE,
                MeasureType.FREQUENCY,
                MeasureAggregation.CASES,
                MeasureRelation.ABSOLUTE,
                null,
                null);
    }

    /*
     * This is the main processing method calling to process-discoverer-logic
     */
    public Optional<OutputData> discoverProcess(UserOptionsData userOptions) throws Exception {
        AbstractionParams params = genAbstractionParams(userOptions);

        // Find a DFG first
        Abstraction dfgAbstraction = processDiscoverer.generateDFGAbstraction(params);
        if (dfgAbstraction == null ||
                dfgAbstraction.getDiagram() == null ||
                dfgAbstraction.getDiagram().getNodes().isEmpty() ||
                dfgAbstraction.getDiagram().getEdges().isEmpty()
        ) {
            return Optional.empty();
        }

        // Actual operation with the new params
        Abstraction currentAbstraction;
        if (userOptions.getBPMNMode()) {
            currentAbstraction = processDiscoverer.generateBPMNAbstraction(params, dfgAbstraction);
        } else {
            currentAbstraction = dfgAbstraction;
        }

        String visualizedText = processVisualizer.generateVisualizationText(currentAbstraction);
        return Optional.of(new OutputData(currentAbstraction, visualizedText));
    }

    public OutputData discoverTrace(String traceID, UserOptionsData userOptions) throws Exception {
        AbstractionParams params = genAbstractionParamsForTrace(userOptions);
        Abstraction traceAbs = processDiscoverer.generateTraceAbstraction(traceID, params);
        String traceVisualization = processVisualizer.generateVisualizationText(traceAbs);
        return new OutputData(traceAbs, traceVisualization);
    }

    public int getSourceLogId() {
        return sourceLogId;
    }

    public AttributeLog getAttributeLog() {
        return this.attLog;
    }

    public XLog getXLog() {
        return this.aLog.getActualXLog();
    }

    public boolean hasEmptyData() {
        return (this.attLog != null && this.attLog.getTraces().size() == 0);
    }

    public IndexableAttribute getAttribute(String key) {
        return (IndexableAttribute)indexableAttributes.select(att -> att.getKey().equals(key)).get(0);
    }

    public void setMainAttribute(String key) throws NotFoundAttributeException  {
        long timer = 0;
        IndexableAttribute newAttribute = getAttribute(key);
        if (newAttribute != null) {
            if (mainAttribute != newAttribute) {
                mainAttribute = newAttribute;
                if (attLog == null) {
                    timer = System.currentTimeMillis();
                    attLog = new AttributeLog(aLog, mainAttribute);
                    LOGGER.debug("Create AttributeLog for the perspective attribute: {} ms.", System.currentTimeMillis() - timer);
                }
                else {
                    timer = System.currentTimeMillis();
                    attLog.setAttribute(mainAttribute);
                    LOGGER.debug("Update AttributeLog to the new perspective attribute: {} ms.", System.currentTimeMillis() - timer);
                }

            }
        }
        else {
            throw new NotFoundAttributeException("Cannot find an attribute in ALog with key = " + key);
        }
    }

    public ImmutableList<AbstractAttribute> getAvailableAttributes() {
        return this.indexableAttributes;
    }

    public IndexableAttribute getMainAttribute() {
        return this.mainAttribute;
    }

    public ListIterable<AttributeInfo> getAttributeInfoList() {
        return this.attLog.getAttributeInfoList();
    }

    //////////////////////// Filter /////////////////////////////

    public Object getCurrentFilterCriteria() {
        return this.currentFilterCriteria;
    }

    public void setCurrentFilterCriteria(Object criteria) {
        this.currentFilterCriteria = criteria;
    }

    public boolean isCurrentFilterCriteriaEmpty() {
        ArrayList<LogFilterRule> filterCriteria = (ArrayList<LogFilterRule>)this.currentFilterCriteria;
        if (filterCriteria != null && filterCriteria.size() > 0) {
            return false;
        }
        return true;
    }

    private List<LogFilterRule> copyFilterCriteria(List<LogFilterRule> criteria) {
        return criteria
                .stream()
                .map((c) -> c.clone())
                .collect(Collectors.toList());
    }

    public List<LogFilterRule> copyCurrentFilterCriteria() {
        return copyFilterCriteria((List<LogFilterRule>)this.getCurrentFilterCriteria());
    }

    public APMLog getOriginalAPMLog() {
        return this.originalAPMLog;
    }

    public PLog getFilteredPLog() {
        return this.filteredPLog;
    }

    public void clearFilter() throws Exception {
        this.filter(new ArrayList<LogFilterRule>());
    }

    // Apply a filter criterion on top of the current filter criteria
    public boolean filterAdditive(LogFilterRule filterCriterion) throws Exception {
        List<LogFilterRule> criteria = (List<LogFilterRule>)currentFilterCriteria;
        criteria.add(filterCriterion.clone());
        this.apmLogFilter.filter(criteria);
        if (apmLogFilter.getPLog().getPTraces().isEmpty()) { // Restore to the last state
            criteria.remove(criteria.get(criteria.size() - 1));
            apmLogFilter.filter(criteria);
            return false;
        }
        else {
            this.updateLog(apmLogFilter.getPLog(), apmLogFilter.getAPMLog());
            return true;
        }
    }

    // Apply filter criteria on top of the original log
    public boolean filter(List<LogFilterRule> criteria) throws Exception {
        this.apmLogFilter.filter(criteria);
        if (apmLogFilter.getPLog().getPTraces().isEmpty()) { // Restore to the last state
            apmLogFilter.filter((List<LogFilterRule>)currentFilterCriteria);
            return false;
        } else {
            this.updateLog(apmLogFilter.getPLog(), apmLogFilter.getAPMLog());
            currentFilterCriteria = copyFilterCriteria(criteria);
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

    public boolean filter_RemoveTracesAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return filterAdditive(getEventAttributeFilterRule(attKey, Choice.REMOVE, Section.CASE, Inclusion.ANY_VALUE,
                    getEventAttributeRuleValue(value, attKey, FilterType.CASE_EVENT_ATTRIBUTE)));
    }

    public boolean filter_RetainTracesAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return filterAdditive(getEventAttributeFilterRule(attKey, Choice.RETAIN, Section.CASE, Inclusion.ANY_VALUE,
                getEventAttributeRuleValue(value, attKey, FilterType.CASE_EVENT_ATTRIBUTE)));
    }

    public boolean filter_RemoveTracesAllValueOfEventAttribute(String value, String attKey) throws Exception {
        return filterAdditive(getEventAttributeFilterRule(attKey, Choice.REMOVE, Section.CASE, Inclusion.ALL_VALUES,
                getEventAttributeRuleValue(value, attKey, FilterType.CASE_EVENT_ATTRIBUTE)));
    }

    public boolean filter_RetainTracesAllValueOfEventAttribute(String value, String attKey) throws Exception {
        return filterAdditive(getEventAttributeFilterRule(attKey, Choice.RETAIN, Section.CASE, Inclusion.ALL_VALUES,
                getEventAttributeRuleValue(value, attKey, FilterType.CASE_EVENT_ATTRIBUTE)));
    }

    public boolean filter_RemoveEventsAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return filterAdditive(getEventAttributeFilterRule(attKey, Choice.REMOVE, Section.EVENT, Inclusion.ANY_VALUE,
                getEventAttributeRuleValue(value, attKey, FilterType.EVENT_EVENT_ATTRIBUTE)));
    }

    public boolean filter_RetainEventsAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return filterAdditive(getEventAttributeFilterRule(attKey, Choice.RETAIN, Section.EVENT, Inclusion.ANY_VALUE,
                getEventAttributeRuleValue(value, attKey, FilterType.EVENT_EVENT_ATTRIBUTE)));
    }

    public boolean filter_RemoveTracesAnyValueOfDirectFollowRelation(String value, String attKey) throws Exception {
        return filterAdditive(getDirectFollowFilterRule(attKey, Choice.REMOVE, Section.CASE, Inclusion.ANY_VALUE,
                getDirectFollowRuleValue(value, attKey)));
    }

    public boolean filter_RetainTracesAnyValueOfDirectFollowRelation(String value, String attKey) throws Exception {
        return filterAdditive(getDirectFollowFilterRule(attKey, Choice.RETAIN, Section.CASE, Inclusion.ANY_VALUE,
                getDirectFollowRuleValue(value, attKey)));
    }

    public boolean hasSufficientDurationVariant(String attribute, String value) {
        return LogStatsAnalyzer.getNodeDurationSize(attribute, value, filteredAPMLog) > 1;
    }

    public boolean hasSufficientDurationVariant(String attribute, String inDegree, String outDegree) {
        return LogStatsAnalyzer.getArcDurationSize(attribute, inDegree, outDegree, filteredAPMLog) > 1;
    }

    public List<CaseDetails> getCaseDetails() {
        List<ATrace> traceList = filteredAPMLog.getTraces();

        Map<Integer, List<ATrace>> caseVariantGroups =
                LogStatsAnalyzer.getCaseVariantGroupMap(filteredAPMLog.getTraces());

        List<CaseDetails> listResult = new ArrayList<CaseDetails>();
        for (ATrace aTrace : traceList) {
            String caseId = aTrace.getCaseId();
            int caseEvents = aTrace.getActivityInstances().size();
            int caseVariantId = aTrace.getCaseVariantId();
            int caseSize = caseVariantGroups.get(caseVariantId).size();
            double caseVariantFreq = (double) caseSize / traceList.size();
            CaseDetails caseDetails = CaseDetails.valueOf(caseId, aTrace.getCaseIdDigit(), caseEvents, caseVariantId, caseVariantFreq);
            listResult.add(caseDetails);
        }
        return listResult;
    }

    public List<PerspectiveDetails> getActivityDetails() {
        List<PerspectiveDetails> listResult = new ArrayList<PerspectiveDetails>();
        for (AttributeInfo info : this.getAttributeInfoList()) {
            ArrayList<String> cells = new ArrayList<>();

            String value = info.getAttributeValue();
            long occurrences = info.getAttributeOccurrenceCount();
            double freq = info.getAttributeOccurrenceFrequency();

            PerspectiveDetails perspectiveDetails = PerspectiveDetails.valueOf(value, occurrences, freq);
            listResult.add(perspectiveDetails);
        }
        return listResult;
    }

    public String getFilteredStartTime() {
        return DateTimeUtils.humanize(this.filteredAPMLog.getStartTime());
    }

    public String getFilteredEndTime() {
        return DateTimeUtils.humanize(this.filteredAPMLog.getEndTime());
    }

    public String getFilteredMinDuration() {
        double dur = TimeStatsProcessor.getCaseDurations(filteredAPMLog.getTraces()).min();
        return DurationUtils.humanize(dur, true);
    }

    public String getFilteredMedianDuration() {
        double dur = TimeStatsProcessor.getCaseDurations(filteredAPMLog.getTraces()).median();
        return DurationUtils.humanize(dur, true);
    }

    public String getFilteredMeanDuration() {
        double dur = TimeStatsProcessor.getCaseDurations(filteredAPMLog.getTraces()).average();
        return DurationUtils.humanize(dur, true);
    }

    public String getFilteredMaxDuration() {
        double dur = TimeStatsProcessor.getCaseDurations(filteredAPMLog.getTraces()).max();
        return DurationUtils.humanize(dur, true);
    }

    public long getFilteredCaseVariantSize() {
        // ==================================================================================================
        // PLog does not always have the updated case variants due to performance concern.
        // Such a value can be obtained from the filteredAPMLog (the output)
        // ==================================================================================================
        return this.filteredAPMLog.getCaseVariantGroupMap().size();
    }

    public long getFilteredActivityInstanceSize() {
        return this.filteredAPMLog.getActivityInstances().size();
    }

    public void updateLog(PLog pLog, APMLog apmLog) throws Exception {
        this.filteredAPMLog = apmLog;
        this.filteredPLog = pLog;
        List<PTrace> pTraces = pLog.getCustomPTraceList();

        LogBitMap logBitMap = new LogBitMap(aLog.getOriginalTraces().size());
        logBitMap.setTraceBitSet(pLog.getValidTraceIndexBS(), pTraces.size());

        for (int i=0; i<pTraces.size(); i++) {
            logBitMap.addEventBitSet(pTraces.get(i).getValidEventIndexBS(), aLog.getOriginalTraceFromIndex(i).getOriginalEvents().size());
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
        LOGGER.debug("PLog trace status (trace_number:bit): ");
        for (int i=0; i<pTraces.size(); i++) {
            LOGGER.debug(i + ":" + (bitSet.get(i) ? "1" : "0") + ",");
        }
        LOGGER.debug("end");

        LOGGER.debug("PLog trace event status: ");
        for (int i=0; i<pTraces.size(); i++) {
            LOGGER.debug("Trace " + i + " event status (event_number:bit):");
            BitSet eventBitSet = pTraces.get(i).getValidEventIndexBS();
            for (int j=0; j<pTraces.get(i).getImmutableEvents().size(); j++) {
                LOGGER.debug(j + ":" + (eventBitSet.get(j) ? "1" : "0") + ",");
            }
            LOGGER.debug("end");
        }

    }

    // For debug only
    private void printALogBitMap(ALog log) {
        BitSet bitSet = log.getOriginalTraceStatus();
        LOGGER.debug("ALog trace status (trace_number:bit): ");
        for (int i=0; i<log.getOriginalTraces().size(); i++) {
            LOGGER.debug(i + ":" + (bitSet.get(i) ? "1" : "0") + ",");
        }
        LOGGER.debug("end");

        LOGGER.debug("ALog trace event status: ");
        for (int i=0; i<log.getOriginalTraces().size(); i++) {
            LOGGER.debug("Trace " + i + " event status (event_number:bit):");
            BitSet eventBitSet = log.getOriginalTraceFromIndex(i).getOriginalEventStatus();
            for (int j=0; j<log.getOriginalTraceFromIndex(i).getOriginalEvents().size(); j++) {
                LOGGER.debug(j + ":" + (eventBitSet.get(j) ? "1" : "0") + ",");
            }
            LOGGER.debug("end");
        }
    }

    private void printAttributeLogBitMap(AttributeLog log) {
        BitSet bitSet = log.getOriginalTraceStatus();
        LOGGER.debug("AttributeLog trace status (trace_number:bit): ");
        for (int i=0; i<log.getOriginalTraces().size(); i++) {
            LOGGER.debug(i + ":" + (bitSet.get(i) ? "1" : "0") + ",");
        }
        LOGGER.debug("end");

        LOGGER.debug("AttributeLog trace (aggregated) event status: ");
        for (int i=0; i<log.getOriginalTraces().size(); i++) {
            LOGGER.debug("Trace " + i + " event status (event_number:bit):");
            BitSet eventBitSet = log.getOriginalTraceFromIndex(i).getOriginalEventStatus();
            // Don't count the artificial start and end events
            for (int j=1; j<(log.getOriginalTraceFromIndex(i).getOriginalValueTrace().size()-1); j++) {
                LOGGER.debug((j-1) + ":" + (eventBitSet.get(j) ? "1" : "0") + ",");
            }
            LOGGER.debug("end");
        }
    }
}
