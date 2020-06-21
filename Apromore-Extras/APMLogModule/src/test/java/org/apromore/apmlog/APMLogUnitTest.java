/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;


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
        APMLog apmLog = new APMLog(bpi2013);
    }

    @Test
    public void testAPMLogParsing() throws Exception {
        XLog xLog = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/c18lmm7-2.xes.gz")).get(0);

        APMLogParsingTest.testConcurrentStartCompleteEvents(xLog,this);

        xLog = getXLog("files/simplelogs/L1_1trace_start_complete_events_non_overlapping.xes");
        APMLogParsingTest.testStartCompleteNoOverlap(xLog, this);

        xLog = getXLog("files/simplelogs/L1_activities with start or complete events only.xes");
        APMLogParsingTest.testActivityStartCompleteEventsOnly(xLog, this);

        xLog = getXLog("files/simplelogs/L1_complete_events_only_with_resources_missing_timestamps.xes");
        APMLogParsingTest.testMissingTimestamp(xLog, this);

        xLog = getXLog("files/simplelogs/L1_complete_events_only_with_resources.xes");
        APMLogParsingTest.testCompleteOnlyWithResources(xLog, this);

        xLog = getXLog("files/BPI Challenge 2017-480049.xes");
        APMLogParsingTest.testCountAsSameActivityEvenResourcesAreDifferent(xLog, this);
    }

    @Test
    public void testCaseTimeFilter1() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Case Timeframe - Active In'");
        XLog xLog = getXLog("files/time_active_in.xes");
        APMLog apmLog = new APMLog(xLog);
        CaseTimeFilterTest.testActiveIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter2() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Case Timeframe - Contain In'");
        XLog xLog = getXLog("files/time_active_in.xes");
        APMLog apmLog = new APMLog(xLog);
        CaseTimeFilterTest.testContainIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter3() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Case Timeframe - Start In'");
        XLog xLog = getXLog("files/time_active_in.xes");
        APMLog apmLog = new APMLog(xLog);
        CaseTimeFilterTest.testStartIn(apmLog, this);
    }

    @Test
    public void testCaseTimeFilter4() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Case Timeframe - End In'");
        XLog xLog = getXLog("files/time_active_in.xes");
        APMLog apmLog = new APMLog(xLog);
        CaseTimeFilterTest.testEndIn(apmLog, this);
    }

    @Test
    public void testDurationFilter1() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Duration'");
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = new APMLog(xLog);
        DurationFilterTest.testDuration(apmLog, this);
    }

    @Test
    public void testDurationFilter2() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Total Processing Time'");
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = new APMLog(xLog);
        DurationFilterTest.testTotalProcessTime(apmLog, this);
    }

    @Test
    public void testDurationFilter3() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Average Processing Time'");
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = new APMLog(xLog);
        DurationFilterTest.testAverageProcessTime(apmLog, this);
    }

    @Test
    public void testDurationFilter4() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Max Processing Time'");
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = new APMLog(xLog);
        DurationFilterTest.testMaxProcessTime(apmLog, this);
    }

    @Test
    public void testDurationFilter5() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Total Waiting Time'");
        XLog xLog = getXLog("files/perf.xes");
        APMLog apmLog = new APMLog(xLog);
        DurationFilterTest.testTotalWaitTime(apmLog, this);
    }

    @Test
    public void testDurationFilter6() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Average Waiting Time'");
        XLog xLog = getXLog("files/perf_avg_wt.xes");
        APMLog apmLog = new APMLog(xLog);
        DurationFilterTest.testAverageWaitTime(apmLog, this);
    }

    @Test
    public void testDurationFilter7() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Max Waiting Time'");
        XLog xLog = getXLog("files/perf_avg_wt.xes");
        APMLog apmLog = new APMLog(xLog);
        DurationFilterTest.testMaxWaitTime(apmLog, this);
    }

    @Test
    public void testDurationFilter8() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Case Utilization'");
        XLog xLog = getXLog("files/perf_avg_wt.xes");
        APMLog apmLog = new APMLog(xLog);
        DurationFilterTest.testUtilization(apmLog, this);
    }

    @Test
    public void testDirectFollowFilter1() throws Exception {
        printString("\n(/ 'o')/ ~ Test 'Direct Follow' Filter 1");
        XLog sample5 = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/_sample5.xes.gz")).get(0);
        APMLog apmLog = new APMLog(sample5);
        DirectFollowFilterTest.runTest1(apmLog, this);
    }

    @Test
    public void testDirectFollowFilter2() throws Exception {
        printString("\n(/ 'o')/ ~ Test 'Direct Follow' Filter 2");
        XLog sample5 = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/_sample5.xes.gz")).get(0);
        APMLog apmLog = new APMLog(sample5);
        DirectFollowFilterTest.runTest2(apmLog, this);
    }

    @Test
    public void testEventualFollowFilter1() throws Exception {
        printString("\n(/ 'o')/ ~ Test 'Eventual Follow' Filter 1");
        XLog xLog = getXLog("files/_sample2ef.xes");
        APMLog apmLog = new APMLog(xLog);
        EventualFollowFilterTest.runTest1(apmLog, this);
    }

    @Test
    public void testRework1() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Rework & Repetition - Greater Only'");
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = new APMLog(xLog);
        ReworkRepetitionFilterTest.testGreaterOnly(apmLog, this);
    }

    @Test
    public void testRework2() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Rework & Repetition - Greater Equal'");
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = new APMLog(xLog);
        ReworkRepetitionFilterTest.testGreaterEqual(apmLog, this);
    }

    @Test
    public void testRework3() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Rework & Repetition - Less Only'");
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = new APMLog(xLog);
        ReworkRepetitionFilterTest.testLessOnly(apmLog, this);
    }

    @Test
    public void testRework4() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Rework & Repetition - Less Equal'");
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = new APMLog(xLog);
        ReworkRepetitionFilterTest.testLessEqual(apmLog, this);
    }

    @Test
    public void testRework5() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Rework & Repetition - Greater and Less Equal'");
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = new APMLog(xLog);
        ReworkRepetitionFilterTest.testGreaterAndLessEqual(apmLog, this);
    }

    @Test
    public void testRework6() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Rework & Repetition - Less Equal 0'");
        XLog xLog = getXLog("files/rework.xes");
        APMLog apmLog = new APMLog(xLog);
        ReworkRepetitionFilterTest.testLessEqual0(apmLog, this);
    }

    @Test
    public void testRework7() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Rework & Repetition - Greater Equal 0'");
        XLog xLog = getXLog("files/_reworkTest2.xes");
        APMLog apmLog = new APMLog(xLog);
        ReworkRepetitionFilterTest.testGreaterEqual0(apmLog, this);
    }

    @Test
    public void testEventSectionEventAttribute1() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Event Section Event Attribute - Resource'");
        XLog xLog = getXLog("files/eventattr.xes");
        APMLog apmLog = new APMLog(xLog);
        EventSectionAttributeFilterTest.testResource(apmLog, this);
    }

    @Test
    public void testEventSectionEventAttribute2() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Event Section Event Attribute - Activity'");
        XLog xLog = getXLog("files/eventattr.xes");
        APMLog apmLog = new APMLog(xLog);
        EventSectionAttributeFilterTest.testActivity(apmLog, this);
    }

    @Test
    public void testCaseSectionEventAttribute1() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Case Section Event Attribute - Activity'");
        XLog xLog = getXLog("files/eventattr.xes");
        APMLog apmLog = new APMLog(xLog);
        CaseSectionEventAttributeFilterTest.testActivity(apmLog, this);
    }

    @Test
    public void testCaseSectionEventTime1() throws Exception {
        printString("\n(/ 'o')/ ~ Test Filter 'Event Time 1'");
        XLog xLog = getXLog("files/eventattr.xes");
        APMLog apmLog = new APMLog(xLog);
        EventTimeFilterTest.testRetain(apmLog, this);
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

}
