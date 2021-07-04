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

package org.apromore.apmlog;

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
import org.apromore.apmlog.logobjects.ImmutableLog;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.util.Util;
import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Test suite for {@link APMLog}.
 */
public class APMLogUnitTest {

    private XLog bpi2013;

    @Before
    public void before() throws Exception {
        bpi2013 = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/BPI Challenge 2013 closed problems.xes.gz")).get(0);

    }

    @Ignore("This test demonstrates the defect AP-1037")
    @Test
    public void testConstructor_BPIC13() {
//        APMLog apmLog = new APMLog(bpi2013);
//        APMLog apmLog = LogFactory.convertXLog(bpi2013);
    }

    @Test
    public void testCaseVariantFrequency() throws Exception {
        APMLog apmLog = getImmutableLog("sepsis-cases-young", "files/sepsis-cases-young.xes");
        Map<String, String> map = getExpectedMap("files/sepsis-cases-young-case-variant-freq.csv");
        CaseStatsTest.testCaseVariantFrequency(apmLog, map, this);
    }

    @Test
    public void testAPMLogParsing() throws Exception {
        XLog xLog = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/c18lmm7-2.xes.gz")).get(0);

        APMLogParsingTest.testConcurrentStartCompleteEvents(xLog,this);

        xLog = getXLog("files/simplelogs/L1_1trace_start_complete_events_non_overlapping.xes");
        APMLogParsingTest.testStartCompleteNoOverlap(xLog, this);

        xLog = getXLog("files/simplelogs/L1_activities with start or complete events only.xes");
        APMLogParsingTest.testActivityStartCompleteEventsOnly(xLog, this);

        xLog = getXLog("files/simplelogs/L1_complete_events_only_with_resources.xes");
        APMLogParsingTest.testCompleteOnlyWithResources(xLog, this);

        xLog = getXLog("files/BPI Challenge 2017-480049.xes");
        APMLogParsingTest.testCountAsSameActivityEvenResourcesAreDifferent(xLog, this);
    }

    @Test
    public void testCaseTimeFilter1() throws Exception {
        APMLog apmLog = getImmutableLog("time_active_in","files/time_active_in.xes" );
        CaseTimeFilterTest.testActiveIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter2() throws Exception {
        APMLog apmLog = getImmutableLog("time_active_in", "files/time_active_in.xes");
        CaseTimeFilterTest.testContainIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter3() throws Exception {
        APMLog apmLog = getImmutableLog("time_active_in", "files/time_active_in.xes");
        CaseTimeFilterTest.testStartIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter4() throws Exception {
        APMLog apmLog = getImmutableLog("time_active_in", "files/time_active_in.xes");
        CaseTimeFilterTest.testEndIn(apmLog, this);
    }

    @Test
    public void testPerfFilter1() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testDuration(apmLog, this);
    }

    @Test
    public void testPerfFilter2() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testTotalProcessTime(apmLog, this);
    }

    @Test
    public void testPerfFilter3() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testAverageProcessTime(apmLog, this);
    }

    @Test
    public void testPerfFilter4() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testMaxProcessTime(apmLog, this);
    }

    @Test
    public void testPerfFilter5() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testTotalWaitTime(apmLog, this);
    }

    @Test
    public void testPerfFilter6() throws Exception {
        APMLog apmLog = getImmutableLog("perf_avg_wt", "files/perf_avg_wt.xes");
        PerfFilterTest.testAverageWaitTime(apmLog, this);
    }

    @Test
    public void testPerfFilter7() throws Exception {
        APMLog apmLog = getImmutableLog("perf_avg_wt", "files/perf_avg_wt.xes");
        PerfFilterTest.testMaxWaitTime(apmLog, this);
    }

    @Test
    public void testPerfFilter8() throws Exception {
        APMLog apmLog = getImmutableLog("perf_avg_wt", "files/perf_avg_wt.xes");
        PerfFilterTest.testUtilization(apmLog, this);
    }

    @Test
    public void testPerfFilter9() throws Exception {
        /**
         * Note: Case length is the size of activities, not events.
         * (an activity contains 1...N events)
         */
        APMLog apmLog = getImmutableLog("caseLengthTest", "files/caseLengthTest.xes");
        PerfFilterTest.testCaseLength(apmLog);
    }

    @Test
    public void testEventualFollowFilter1() throws Exception {
        APMLog apmLog = getImmutableLog("EventualFollow", "files/EventualFollow.xes");
        EventualFollowFilterTest.runTest1(apmLog, this);
    }

    @Test
    public void testDirectFollowFilter2() throws Exception {
        APMLog apmLog = getImmutableLog("_sample5", "files/_sample5.xes.gz");
        DirectFollowFilterTest.runTest1(apmLog, this);
        DirectFollowFilterTest.runTest2(apmLog, this);
    }


    @Test
    public void testRework1() throws Exception {
        APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
        ReworkRepetitionFilterTest.testGreaterOnly(apmLog, this);
    }

    @Test
    public void testRework2() throws Exception {
        APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
        ReworkRepetitionFilterTest.testGreaterEqual(apmLog, this);
    }

    @Test
    public void testRework3() throws Exception {
        APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
        ReworkRepetitionFilterTest.testLessOnly(apmLog, this);
    }

    @Test
    public void testRework4() throws Exception {
        APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
        ReworkRepetitionFilterTest.testLessEqual(apmLog, this);
    }

    @Test
    public void testRework5() throws Exception {
        APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
        ReworkRepetitionFilterTest.testGreaterAndLessEqual(apmLog, this);
    }

    @Test
    public void testRework6() throws Exception {
        APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
        ReworkRepetitionFilterTest.testLessEqual0(apmLog, this);
    }

    @Test
    public void testRework7() throws Exception {
        APMLog apmLog = getImmutableLog("_reworkTest2", "files/_reworkTest2.xes");
        ReworkRepetitionFilterTest.testGreaterEqual0(apmLog, this);
    }

    @Test
    public void testEventSectionEventAttribute1() throws Exception {
        APMLog apmLog = getImmutableLog("eventattr", "files/eventattr.xes");
        EventSectionAttributeFilterTest.testResource(apmLog, this);
    }

    @Test
    public void testEventSectionEventAttribute2() throws Exception {
        APMLog apmLog = getImmutableLog("eventattr", "files/eventattr.xes");
        EventSectionAttributeFilterTest.testActivity(apmLog, this);
    }

    @Test
    public void testCaseSectionEventAttribute1() throws Exception {
        APMLog apmLog = getImmutableLog("eventattr", "files/eventattr.xes");
        CaseSectionEventAttributeFilterTest.testActivity(apmLog, this);
    }

    @Test
    public void testCaseSectionEventTime1() throws Exception {
        APMLog apmLog = getImmutableLog("eventattr", "files/eventattr.xes");
        EventTimeFilterTest.testRetain(apmLog, this);
    }

    @Test
    public void testAttrCombFilterEventEvent1() throws Exception {
        APMLog apmLog = getImmutableLog("attrCombTest", "files/attrCombTest.xes");
        AttributeCombinationTest.testRetainEventEvent1(apmLog, this);
    }

    @Test
    public void testAttrCombFilterEventCase1() throws Exception {
        APMLog apmLog = getImmutableLog("attrCombTest", "files/attrCombTest.xes");
        AttributeCombinationTest.testRetainEventCase1(apmLog, this);
    }

    @Test
    public void testAttrDuration1() throws Exception {
        APMLog apmLog = getImmutableLog("attrCombTest", "files/attrCombTest.xes");
        AttributeDurationTest.testRetainAttributeDuration1(apmLog, this);
    }

    @Test
    public void testAttrArcDur1() throws Exception {
        APMLog apmLog = getImmutableLog("attrArcDurTest", "files/attrArcDurTest.xes");
        AttributeArcDurationTest.testRetain1(apmLog, this);
    }

    @Test
    public void testAttrArcDur2() throws Exception {
        APMLog apmLog = getImmutableLog("attrArcDurTest", "files/attrArcDurTest.xes");
        AttributeArcDurationTest.testRetain2(apmLog, this);
    }

    @Test
    public void testAttrArcDur3() throws Exception {
        APMLog apmLog = getImmutableLog("attrArcDurTest", "files/attrArcDurTest.xes");
        AttributeArcDurationTest.testRetain3(apmLog, this);
    }

    @Test
    public void testAttrArcDur4() throws Exception {
        APMLog apmLog = getImmutableLog("2TracesArcDurTest", "files/2TracesArcDurTest.xes");
        AttributeArcDurationTest.testAvgDur1(apmLog, this);
    }

    @Test
    public void testAPMLogDurations() throws Exception {
        APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
        LogsDurationsTest.testAPMLogDurations(apmLog);
    }

    @Test
    public void testPLogDurations() throws Exception {
        APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
        LogsDurationsTest.testPLogDurations(apmLog);
    }

    @Test
    public void testImmutableLogDurations() throws Exception {
        APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
        LogsDurationsTest.testImmutableLogDurations(apmLog);
    }

    @Test
    public void testClonedImmutableLogDurations() throws Exception {
        APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
        LogsDurationsTest.testClonedImmutableLogDurations(apmLog);
    }

    @Test
    public void testImmutableTraceTimestamp() throws Exception {
        APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
        ImmutableTraceTest.testStartEndTimestamps(apmLog);
    }

    @Test
    public void testEventAttrFreqAfterEventAttrFilter() throws Exception {
        APMLog apmLog = getImmutableLog("Production2cases", "files/Production2cases.xes");
        EventSectionAttributeFilterTest.testEventAttrFreqAfterEventAttrFilter(apmLog, this);
    }

    @Test
    public void testTripleOverlap() throws Exception {
        APMLog apmLog = getImmutableLog("TripleOverlap", "files/TripleOverlap.xes");
        TripleOverlapTest.test1(apmLog);
    }

    @Test
    public void testCaseIdRemove() throws Exception {
        APMLog originalLog = getImmutableLog("5cases", "files/5cases.xes");
        CaseIdFilterTest.test1(originalLog);
    }

    @Test
    public void testSequencialEventThenArc01() throws Exception {
        APMLog originalLog = getImmutableLog("ArcFilterTest02", "files/ArcFilterTest02.xes");
        AttributeArcDurationTest.testSequencialFiltering01(originalLog);
    }

    @Test
    public void testCaseDurationAfterEventAttrFilter() throws Exception {
        APMLog apmLog = getImmutableLog("ArcSimple03", "files/ArcSimple03.xes");
        CaseStatsTest.testCaseDurationAfterEventAttrFilter(apmLog);
    }

    @Test
    public void testProcureToPayAdvFilter1() throws Exception {
        APMLog apmLog = getImmutableLog("Procure-to-Pay", "files/Procure-to-Pay.xes.gz");
        ProcureToPayAdvFilterTest.run(apmLog, this);
    }

    @Test
    public void testNoErrorByCalling0WaitingTime() throws Exception {
        APMLog apmLog = getImmutableLog("A2_overlap_mixed", "files/A2_overlap_mixed.xes");
        PLog pLog = new PLog(apmLog);
        for (PTrace pTrace : pLog.getPTraces()) {
            assertTrue(pTrace.getWaitingTimes().min() == 0);
            assertTrue(pTrace.getWaitingTimes().median() == 0);
            assertTrue(pTrace.getWaitingTimes().average() == 0);
            assertTrue(pTrace.getWaitingTimes().max() == 0);
            assertTrue(pTrace.getWaitingTimes().sum() == 0);
        }
    }

    @Test
    public void testCasePerspectiveActivityFrequency() throws Exception {
        APMLog apmLog = getImmutableLog("_reworkTest2", "files/_reworkTest2.xes");

        UnifiedMap<String, String> expected = new UnifiedMap<>();
        expected.put("a", "100.00");
        expected.put("b", "50.00");

        UnifiedSet<EventAttributeValue> eavSet = apmLog.getImmutableEventAttributeValues().get("concept:name");
        for (EventAttributeValue eav : eavSet) {
            assertTrue(eav.getFrequency().equals(expected.get(eav.getValue())));
        }
    }

    @Test
    public void testMissOrderedLog() throws Exception {
        APMLog apmLog = getImmutableLog("_reworkTest2", "files/new_connection.xes.gz");

        PLog pLog = new PLog(apmLog);

        assertTrue(pLog.getStartTime() == 1551919260000L);
        assertTrue(pLog.getEndTime() == 1607672640000L);
    }

    @Test
    public void testCaseSecEventAndEventAttributeFilter1() throws Exception {
        APMLog apmLog = getImmutableLog("5casesMode", "files/5casesMOD.xes");

        FilterType filterType = FilterType.CASE_SECTION_ATTRIBUTE_COMBINATION;

        RuleValue rv1 = new RuleValue(filterType, OperationType.EQUAL, "org:resource", "Kalv");
        rv1.putCustomAttribute("section", "event");
        Set<RuleValue> primaryValues = new HashSet<>(Arrays.asList(rv1));

        RuleValue rv2 = new RuleValue(filterType, OperationType.EQUAL,
                "concept:name", new HashSet<>(Arrays.asList("Prepare package", "Proceed order")));
        rv2.putCustomAttribute("section", "event");
        Set<RuleValue> secondaryValues = new HashSet<>(Arrays.asList(rv2));

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ALL_VALUES, Section.CASE,
                filterType, "org:resource", primaryValues, secondaryValues);

        List<LogFilterRule> criteria = Arrays.asList(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);

        apmLogFilter.filter(criteria);

        APMLog filteredLog = apmLogFilter.getAPMLog();
        assertTrue(filteredLog.size() == 1);
        assertTrue(filteredLog.get(0).getCaseId().equals("3007"));
    }

    @Test
    public void testAverageActivityDuration() throws Exception {
        APMLog apmLog = getImmutableLog("logCSM_15Kcases", "files/logCSM_15Kcases.xes.gz");
        UnifiedSet<EventAttributeValue> eavSet = apmLog.getImmutableEventAttributeValues().get("concept:name");
        DoubleArrayList dal = LogStatsAnalyzer.getEventAttributeValueDurationList(eavSet, "average",
                new UnifiedSet<>(apmLog.getActivityInstances()));
        String displayVal = Util.durationStringOf(dal.average());
        assertTrue(displayVal.equalsIgnoreCase("18.09 hrs"));
    }

    private ImmutableLog getImmutableLog(String logName, String path) throws Exception {
        XLog xLog = getXLog(path);
        ImmutableLog immutableLog = XLogToImmutableLog.convertXLog(logName, xLog);
        return immutableLog;
    }

    public void printString(String unicodeMessage) throws UnsupportedEncodingException {
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        out.println(unicodeMessage);
    }

    public XLog getXLog(String filepath) throws Exception {
        String path = filepath;
        File xLogFile = new File(path);
        String fileName = xLogFile.getName();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        XesXmlParser parser  = extension.equals(".gz") ? new XesXmlGZIPParser() : new XesXmlParser();
        XLog xLog = parser.parse(xLogFile).get(0);
        return xLog;
    }

    public Map<String, String> getExpectedMap(String filepath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        Map<String, String> map = br.lines()
                .map((Object row) -> ((String) row).split(",", 2))
                .collect(Collectors.toMap((String[] cols) -> cols[0], (String[] cols) -> cols[1]));
        return map;
    }

}
