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

import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertTrue;

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
        APMLog apmLog = LogFactory.convertXLog(bpi2013);
    }

    @Test
    public void testCaseVariantFrequency() throws Exception {
        XLog xLog = getXLog("files/sepsis-cases-young.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
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
        XLog xLog = getXLog("files/time_active_in.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        CaseTimeFilterTest.testActiveIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter2() throws Exception {
        XLog xLog = getXLog("files/time_active_in.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        CaseTimeFilterTest.testContainIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter3() throws Exception {
        XLog xLog = getXLog("files/time_active_in.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        CaseTimeFilterTest.testStartIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter4() throws Exception {
        XLog xLog = getXLog("files/time_active_in.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        CaseTimeFilterTest.testEndIn(apmLog, this);
    }

    @Test
    public void testPerfFilter1() throws Exception {
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testDuration(apmLog, this);
    }

    @Test
    public void testPerfFilter2() throws Exception {
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testTotalProcessTime(apmLog, this);
    }

    @Test
    public void testPerfFilter3() throws Exception {
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testAverageProcessTime(apmLog, this);
    }

    @Test
    public void testPerfFilter4() throws Exception {
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testMaxProcessTime(apmLog, this);
    }

    @Test
    public void testPerfFilter5() throws Exception {
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testTotalWaitTime(apmLog, this);
    }

    @Test
    public void testPerfFilter6() throws Exception {
        XLog xLog = getXLog("files/perf_avg_wt.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testAverageWaitTime(apmLog, this);
    }

    @Test
    public void testPerfFilter7() throws Exception {
        XLog xLog = getXLog("files/perf_avg_wt.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testMaxWaitTime(apmLog, this);
    }

    @Test
    public void testPerfFilter8() throws Exception {
        XLog xLog = getXLog("files/perf_avg_wt.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testUtilization(apmLog, this);
    }

    @Test
    public void testPerfFilter9() throws Exception {
        XLog xLog = getXLog("files/caseLengthTest.xes");
        /**
         * Note: Case length is the size of activities, not events.
         * (an activity contains 1...N events)
         */
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PerfFilterTest.testCaseLength(apmLog);
    }

    @Test
    public void testEventualFollowFilter1() throws Exception {
        XLog xLog = getXLog("files/EventualFollow.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        EventualFollowFilterTest.runTest1(apmLog, this);
    }

    @Test
    public void testDirectFollowFilter2() throws Exception {
        XLog xLog = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/_sample5.xes.gz")).get(0);
        APMLog apmLog = LogFactory.convertXLog(xLog);
        DirectFollowFilterTest.runTest1(apmLog, this);
        DirectFollowFilterTest.runTest2(apmLog, this);
    }


    @Test
    public void testRework1() throws Exception {
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ReworkRepetitionFilterTest.testGreaterOnly(apmLog, this);
    }

    @Test
    public void testRework2() throws Exception {
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ReworkRepetitionFilterTest.testGreaterEqual(apmLog, this);
    }

    @Test
    public void testRework3() throws Exception {
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ReworkRepetitionFilterTest.testLessOnly(apmLog, this);
    }

    @Test
    public void testRework4() throws Exception {
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ReworkRepetitionFilterTest.testLessEqual(apmLog, this);
    }

    @Test
    public void testRework5() throws Exception {
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ReworkRepetitionFilterTest.testGreaterAndLessEqual(apmLog, this);
    }

    @Test
    public void testRework6() throws Exception {
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ReworkRepetitionFilterTest.testLessEqual0(apmLog, this);
    }

    @Test
    public void testRework7() throws Exception {
        XLog xLog = getXLog("files/_reworkTest2.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ReworkRepetitionFilterTest.testGreaterEqual0(apmLog, this);
    }

    @Test
    public void testEventSectionEventAttribute1() throws Exception {
        XLog xLog = getXLog("files/eventattr.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        EventSectionAttributeFilterTest.testResource(apmLog, this);
    }

    @Test
    public void testEventSectionEventAttribute2() throws Exception {
        XLog xLog = getXLog("files/eventattr.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        EventSectionAttributeFilterTest.testActivity(apmLog, this);
    }

    @Test
    public void testCaseSectionEventAttribute1() throws Exception {
        XLog xLog = getXLog("files/eventattr.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        CaseSectionEventAttributeFilterTest.testActivity(apmLog, this);
    }

    @Test
    public void testCaseSectionEventTime1() throws Exception {
        XLog xLog = getXLog("files/eventattr.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        EventTimeFilterTest.testRetain(apmLog, this);
    }

    @Test
    public void testAttrCombFilterEventEvent1() throws Exception {
        XLog xLog = getXLog("files/attrCombTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        AttributeCombinationTest.testRetainEventEvent1(apmLog, this);
    }

    @Test
    public void testAttrCombFilterEventCase1() throws Exception {
        XLog xLog = getXLog("files/attrCombTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        AttributeCombinationTest.testRetainEventCase1(apmLog, this);
    }

    @Test
    public void testAttrDuration1() throws Exception {
        XLog xLog = getXLog("files/attrCombTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        AttributeDurationTest.testRetainAttributeDuration1(apmLog, this);
    }

    @Test
    public void testAttrArcDur1() throws Exception {
        XLog xLog = getXLog("files/attrArcDurTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        AttributeArcDurationTest.testRetain1(apmLog, this);
    }

    @Test
    public void testAttrArcDur2() throws Exception {
        XLog xLog = getXLog("files/attrArcDurTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        AttributeArcDurationTest.testRetain2(apmLog, this);
    }

    @Test
    public void testAttrArcDur3() throws Exception {
        XLog xLog = getXLog("files/attrArcDurTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        AttributeArcDurationTest.testRetain3(apmLog, this);
    }

    @Test
    public void testAttrArcDur4() throws Exception {
        XLog xLog = getXLog("files/2TracesArcDurTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        AttributeArcDurationTest.testAvgDur1(apmLog, this);
    }

    @Test
    public void testAPMLogDurations() throws Exception {
        XLog xLog = getXLog("files/durationTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        LogsDurationsTest.testAPMLogDurations(apmLog);
    }

    @Test
    public void testPLogDurations() throws Exception {
        XLog xLog = getXLog("files/durationTest.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        LogsDurationsTest.testPLogDurations(apmLog);
    }

    @Test
    public void testImmutableLogDurations() throws Exception {
        XLog xLog = getXLog("files/durationTest.xes");
        LogsDurationsTest.testImmutableLogDurations(xLog);
    }

    @Test
    public void testClonedImmutableLogDurations() throws Exception {
        XLog xLog = getXLog("files/durationTest.xes");
        LogsDurationsTest.testClonedImmutableLogDurations(xLog);
    }

    @Test
    public void testImmutableTraceTimestamp() throws Exception {
        XLog xLog = getXLog("files/durationTest.xes");
        ImmutableTraceTest.testStartEndTimestamps(xLog);
    }

    @Test
    public void testLogsActivityNameIndexes() throws Exception {
        XLog xLog = getXLog("files/TestLogFactory.xes");
        LogsMethodsTests.testActivityNameIndexes(xLog);
    }

    @Test
    public void testEventAttrFreqAfterEventAttrFilter() throws Exception {
        XLog xLog = getXLog("files/Production2cases.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        EventSectionAttributeFilterTest.testEventAttrFreqAfterEventAttrFilter(apmLog);
    }

    @Test
    public void testPLogAttributeGraph() throws Exception {
        XLog xLog = getXLog("files/5cases.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PLogAttributeGraphTest.testArc(apmLog);
    }

    @Test
    public void testTripleOverlap() throws Exception {
        XLog xLog = getXLog("files/TripleOverlap.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        TripleOverlapTest.test1(apmLog);
    }

    @Test
    public void testCaseIdRemove() throws Exception {
        XLog xLog = getXLog("files/5cases.xes");
        APMLog originalLog = LogFactory.convertXLog(xLog);
        CaseIdFilterTest.test1(originalLog);
    }

    @Test
    public void testSequencialEventThenArc01() throws Exception {
        XLog xLog = getXLog("files/ArcFilterTest02.xes");
        APMLog originalLog = LogFactory.convertXLog(xLog);
        AttributeArcDurationTest.testSequencialFiltering01(originalLog);
    }

    @Test
    public void testCaseDurationAfterEventAttrFilter() throws Exception {
        XLog xLog = getXLog("files/ArcSimple03.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        CaseStatsTest.testCaseDurationAfterEventAttrFilter(apmLog);
    }

    @Test
    public void testProcureToPayAdvFilter1() throws Exception {
        XLog xLog = getXLog("files/Procure-to-Pay.xes.gz");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ProcureToPayAdvFilterTest.run(apmLog, this);
    }

    @Test
    public void testNoErrorByCalling0WaitingTime() throws Exception {
        XLog xLog = getXLog("files/A2_overlap_mixed.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);
        PLog pLog = new PLog(apmLog);
        for (PTrace pTrace : pLog.getPTraceList()) {
            assertTrue(pTrace.getWaitingTimes().min() == 0);
            assertTrue(pTrace.getWaitingTimes().median() == 0);
            assertTrue(pTrace.getWaitingTimes().average() == 0);
            assertTrue(pTrace.getWaitingTimes().max() == 0);
            assertTrue(pTrace.getWaitingTimes().sum() == 0);
        }
    }

    @Test
    public void testCasePerspectiveActivityFrequency() throws Exception {
        XLog xLog = getXLog("files/_reworkTest2.xes");
        APMLog apmLog = LogFactory.convertXLog(xLog);

        UnifiedMap<String, String> expected = new UnifiedMap<>();
        expected.put("a", "100.00");
        expected.put("b", "50.00");

        UnifiedSet<EventAttributeValue> eavSet = apmLog.getEventAttributeValues().get("concept:name");
        for (EventAttributeValue eav : eavSet) {
            assertTrue(eav.getFrequency().equals(expected.get(eav.getValue())));
        }
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
