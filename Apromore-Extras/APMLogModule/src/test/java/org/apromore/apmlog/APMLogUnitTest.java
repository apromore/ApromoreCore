/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.apromore.apmlog.customcalendartests.CusCalArcDurFilterTestSupport;
import org.apromore.apmlog.customcalendartests.CusCalTest;
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
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.xes.XESAttributeCodes;
import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite for {@link APMLog}.
 */
public class APMLogUnitTest {

    private XLog bpi2013;

    @BeforeEach
    void before() throws Exception {
        bpi2013 = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/BPI Challenge 2013 closed problems.xes.gz")).get(0);

    }

    @Test
    void testAPMLogParsing() throws Exception {
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
    void testCaseTimeFilter1() throws Exception {
        APMLog apmLog = getImmutableLog("time_active_in","files/time_active_in.xes" );
        CaseTimeFilterTest.testActiveIn(apmLog, this);
    }

    @Test
    void testCaseTimeFilter2() throws Exception {
        APMLog apmLog = getImmutableLog("time_active_in", "files/time_active_in.xes");
        CaseTimeFilterTest.testContainIn(apmLog, this);
    }

    @Test
    void testCaseTimeFilter3() throws Exception {
        APMLog apmLog = getImmutableLog("time_active_in", "files/time_active_in.xes");
        CaseTimeFilterTest.testStartIn(apmLog, this);
    }

    @Test
    void testCaseTimeFilter4() throws Exception {
        APMLog apmLog = getImmutableLog("time_active_in", "files/time_active_in.xes");
        CaseTimeFilterTest.testEndIn(apmLog, this);
    }

    @Test
    void testPerfFilter1() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testDuration(apmLog, this);
    }

    @Test
    void testPerfFilter2() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testTotalProcessTime(apmLog, this);
    }

    @Test
    void testPerfFilter3() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testAverageProcessTime(apmLog, this);
    }

    @Test
    void testPerfFilter4() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testMaxProcessTime(apmLog, this);
    }

    @Test
    void testPerfFilter5() throws Exception {
        APMLog apmLog = getImmutableLog("perf", "files/perf.xes");
        PerfFilterTest.testTotalWaitTime(apmLog, this);
    }

    @Test
    void testPerfFilter6() throws Exception {
        APMLog apmLog = getImmutableLog("perf_avg_wt", "files/perf_avg_wt.xes");
        PerfFilterTest.testAverageWaitTime(apmLog, this);
    }

    @Test
    void testPerfFilter7() throws Exception {
        APMLog apmLog = getImmutableLog("perf_avg_wt", "files/perf_avg_wt.xes");
        PerfFilterTest.testMaxWaitTime(apmLog, this);
    }

    @Test
    void testPerfFilter8() throws Exception {
        APMLog apmLog = getImmutableLog("perf_avg_wt", "files/perf_avg_wt.xes");
        PerfFilterTest.testUtilization(apmLog, this);
    }

    @Test
    void testPerfFilter9() throws Exception {
        /**
         * Note: Case length is the size of activities, not events.
         * (an activity contains 1...N events)
         */
        APMLog apmLog = getImmutableLog("caseLengthTest", "files/caseLengthTest.xes");
        PerfFilterTest.testCaseLength(apmLog);
    }

    @Test
    void testEventualFollowFilter1() {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("EventualFollow", "files/EventualFollow.xes");
            EventualFollowFilterTest.runTest1(apmLog, this);
        });
    }

    @Test
    void testDirectFollowFilter2() {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("_sample5", "files/_sample5.xes.gz");
            DirectFollowFilterTest.runTest1(apmLog, this);
            DirectFollowFilterTest.runTest2(apmLog, this);
        });
    }


    @Test
    void testRework1() {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
            ReworkRepetitionFilterTest.testGreaterOnly(apmLog, this);
        });
    }

    @Test
    void testRework2() {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
            ReworkRepetitionFilterTest.testGreaterEqual(apmLog, this);
        });
    }

    @Test
    void testRework3() {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
            ReworkRepetitionFilterTest.testLessOnly(apmLog, this);
        });
    }

    @Test
    void testRework4() {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
            ReworkRepetitionFilterTest.testLessEqual(apmLog, this);
        });
    }

    @Test
    void testRework5() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
            ReworkRepetitionFilterTest.testGreaterAndLessEqual(apmLog, this);
        });
    }

    @Test
    void testRework6() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("rework", "files/rework.xes");
            ReworkRepetitionFilterTest.testLessEqual0(apmLog, this);
        });
    }

    @Test
    void testRework7() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("_reworkTest2", "files/_reworkTest2.xes");
            ReworkRepetitionFilterTest.testGreaterEqual0(apmLog, this);
        });
    }

    @Test
    void testEventSectionEventAttribute1() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("eventattr", "files/eventattr.xes");
            EventSectionAttributeFilterTest.testResource(apmLog, this);
        });
    }

    @Test
    void testEventSectionEventAttribute2() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("eventattr", "files/eventattr.xes");
            EventSectionAttributeFilterTest.testActivity(apmLog, this);
        });
    }

    @Test
    void testCaseSectionEventAttribute1() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("eventattr", "files/eventattr.xes");
            CaseSectionEventAttributeFilterTest.testActivity(apmLog, this);
        });
    }

    @Test
    void testCaseSectionEventTime1() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("eventattr", "files/eventattr.xes");
            EventTimeFilterTest.testRetain(apmLog, this);
        });
    }

    @Test
    void testAttrCombFilterEventEvent1() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("attrCombTest", "files/attrCombTest.xes");
            AttributeCombinationTest.testRetainEventEvent1(apmLog, this);
        });
    }

    @Test
    void testAttrCombFilterEventCase1() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("attrCombTest", "files/attrCombTest.xes");
            AttributeCombinationTest.testRetainEventCase1(apmLog, this);
        });
    }

    @Test
    void testAttrArcDur1() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("attrArcDurTest", "files/attrArcDurTest.xes");
            AttributeArcDurationTest.testRetain1(apmLog, this);
        });
    }

    @Test
    void testAttrArcDur2() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("attrArcDurTest", "files/attrArcDurTest.xes");
            AttributeArcDurationTest.testRetain2(apmLog, this);
        });
    }

    @Test
    void testAttrArcDur3() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("attrArcDurTest", "files/attrArcDurTest.xes");
            AttributeArcDurationTest.testRetain3(apmLog, this);
        });
    }

    @Test
    void testAPMLogDurations() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
            LogsDurationsTest.testAPMLogDurations(apmLog);
        });
    }

    @Test
    void testPLogDurations() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
            LogsDurationsTest.testPLogDurations(apmLog);
        });
    }

    @Test
    void testImmutableLogDurations() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
            LogsDurationsTest.testImmutableLogDurations(apmLog);
        });
    }

    @Test
    void testClonedImmutableLogDurations() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
            LogsDurationsTest.testClonedImmutableLogDurations(apmLog);
        });
    }

    @Test
    void testImmutableTraceTimestamp() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("durationTest", "files/durationTest.xes");
            ImmutableTraceTest.testStartEndTimestamps(apmLog);
        });
    }

    @Test
    void testEventAttrFreqAfterEventAttrFilter() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("Production2cases", "files/Production2cases.xes");
            EventSectionAttributeFilterTest.testEventAttrFreqAfterEventAttrFilter(apmLog, this);
        });
    }

    @Test
    void testTripleOverlap() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("TripleOverlap", "files/TripleOverlap.xes");
            TripleOverlapTest.test1(apmLog);
        });
    }

    @Test
    void testCaseIdRemove() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog originalLog = getImmutableLog("5cases", "files/5cases.xes");
            CaseIdFilterTest.test1(originalLog);
        });
    }

    @Test
    void testSequencialEventThenArc01() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog originalLog = getImmutableLog("ArcFilterTest02", "files/ArcFilterTest02.xes");
            AttributeArcDurationTest.testSequencialFiltering01(originalLog);
        });
    }

    @Test
    void testCaseDurationAfterEventAttrFilter() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("ArcSimple03", "files/ArcSimple03.xes");
            CaseStatsTest.testCaseDurationAfterEventAttrFilter(apmLog);
        });
    }

    @Test
    void testProcureToPayAdvFilter1() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("Procure-to-Pay", "files/Procure-to-Pay.xes.gz");
            ProcureToPayAdvFilterTest.run(apmLog, this);
        });
    }

    @Test
    void testNoErrorByCalling0WaitingTime() throws Exception {
        APMLog apmLog = getImmutableLog("A2_overlap_mixed", "files/A2_overlap_mixed.xes");
        PLog pLog = new PLog(apmLog);
        for (PTrace pTrace : pLog.getPTraces()) {
            DoubleArrayList dss = LogStatsAnalyzer.getWaitingTimesOf(pTrace);
            assertEquals(0, dss.min(), 0.0);
            assertEquals(0, dss.median(), 0.0);
            assertEquals(0, dss.average(), 0.0);
            assertEquals(0, dss.max(), 0.0);
            assertEquals(0, dss.sum(), 0.0);
        }
    }

    @Test
    void testCasePerspectiveActivityFrequency() throws Exception {
        APMLog apmLog = getImmutableLog("_reworkTest2", "files/_reworkTest2.xes");

        UnifiedMap<String, Integer> expected = new UnifiedMap<>();
        expected.put("a", 2); // 'a' appear in two cases
        expected.put("b", 1); // 'b' appear in one case

        Map<String, Number> data =
                LogStatsAnalyzer.getEventAttributeValueCaseFrequencies(XESAttributeCodes.CONCEPT_NAME,
                        apmLog.getActivityInstances());

        for (Map.Entry<String, Number> entry : data.entrySet()) {
            assertEquals(expected.get(entry.getKey()), entry.getValue().doubleValue(), 0);
        }
    }

    @Test
    void testMissOrderedLog() throws Exception {
        APMLog apmLog = getImmutableLog("_reworkTest2", "files/new_connection.xes.gz");

        PLog pLog = new PLog(apmLog);

        assertTrue(pLog.getStartTime() == 1551919260000L);
        assertTrue(pLog.getEndTime() == 1607672640000L);
    }

    @Test
    void testCaseSecEventAndEventAttributeFilter1() throws Exception {
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
    void testCustomCalendar01() throws Exception {
        assertDoesNotThrow(() -> {
            APMLog apmLog = getImmutableLog("2Traces", "files/2Traces-calendar-test01.xes");
            CusCalTest.run(apmLog);
            CusCalArcDurFilterTestSupport.run(apmLog);
        });
    }

    public static ImmutableLog getImmutableLog(String logName, String path) throws Exception {
        XLog xLog = getXLog(path);
        ImmutableLog immutableLog = XLogToImmutableLog.convertXLog(logName, xLog);
        return immutableLog;
    }

    public void printString(String unicodeMessage) throws UnsupportedEncodingException {
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        out.println(unicodeMessage);
    }

    public static XLog getXLog(String filepath) throws Exception {
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
