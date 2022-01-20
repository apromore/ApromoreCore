/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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
package org.apromore.plugin.portal.processdiscoverer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.logman.Constants;
import org.apromore.logman.attribute.AttributeLevel;
import org.apromore.logman.attribute.AttributeType;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeInfo;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.plugin.portal.processdiscoverer.data.CaseDetails;
import org.apromore.plugin.portal.processdiscoverer.data.CaseVariantDetails;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.InvalidDataException;
import org.apromore.plugin.portal.processdiscoverer.data.PerspectiveDetails;
import org.apromore.processsimulation.dto.SimulationData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.layout.Layout;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.api.list.ListIterable;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PDAnalystTest extends TestDataSetup {

    @Test
    public void test_AnalystConstructor_ValidData() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());

        assertFalse(analyst.hasEmptyData());

        assertEquals(2, analyst.getAvailableAttributes().size());
        assertFalse(Objects.isNull(analyst.getAttributeLog()));
        assertFalse(Objects.isNull(analyst.getAttribute("concept:name")));
        assertFalse(Objects.isNull(analyst.getAttribute("lifecycle:transition")));
        assertTrue(Objects.isNull(analyst.getAttribute("org:resource")));

        assertEquals("concept:name", analyst.getMainAttribute().getKey());
        assertEquals(AttributeLevel.EVENT, analyst.getMainAttribute().getLevel());
        assertEquals(AttributeType.LITERAL, analyst.getMainAttribute().getType());

        assertTrue(((List)analyst.getCurrentFilterCriteria()).isEmpty());

        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
    }

    @Test (expected = InvalidDataException.class)
    public void test_AnalystConstructor_MissingActivityPerspective() throws Exception {
        XLog validLog = readLogWithOneTraceOneEvent();
        ContextData contextData = ContextData.valueOf("domain1", "username1", 0,
                "logName", 0, "folderName", false, true);
        Mockito.when(eventLogService.getXLog(contextData.getLogId())).thenReturn(validLog);
        Mockito.when(eventLogService.getAggregatedLog(contextData.getLogId())).thenReturn(
                XLogToImmutableLog.convertXLog("ProcessLog", validLog));
        Mockito.when(eventLogService.getPerspectiveTagByLog(contextData.getLogId())).thenReturn(Arrays.asList("org:resource"));
        ConfigData configData = ConfigData.DEFAULT;
        PDAnalyst analyst = new PDAnalyst(contextData, configData, eventLogService);
    }

    @Test (expected = InvalidDataException.class)
    public void test_AnalystConstructor_NoPerspectiveAttributes() throws Exception {
        XLog validLog = readLogWithOneTraceOneEvent();
        ContextData contextData = ContextData.valueOf("domain1", "username1", 0,
                "logName", 0, "folderName", false, true);
        Mockito.when(eventLogService.getXLog(contextData.getLogId())).thenReturn(validLog);
        Mockito.when(eventLogService.getAggregatedLog(contextData.getLogId())).thenReturn(
                XLogToImmutableLog.convertXLog("ProcessLog", validLog));
        Mockito.when(eventLogService.getPerspectiveTagByLog(contextData.getLogId())).thenReturn(new ArrayList<>());
        ConfigData configData = ConfigData.DEFAULT;
        PDAnalyst analyst = new PDAnalyst(contextData, configData, eventLogService);
    }

    @Test (expected = InvalidDataException.class)
    public void test_AnalystConstructor_TooManyPerspectiveAttributeValues() throws Exception {
        XLog validLog = readLogWithTwoTraceEachTwoEvents();
        ContextData contextData = ContextData.valueOf("domain1", "username1", 0,
                "logName", 0, "folderName", false, true);
        Mockito.when(eventLogService.getXLog(contextData.getLogId())).thenReturn(validLog);
        Mockito.when(eventLogService.getAggregatedLog(contextData.getLogId())).thenReturn(
                XLogToImmutableLog.convertXLog("ProcessLog", validLog));
        Mockito.when(eventLogService.getPerspectiveTagByLog(contextData.getLogId())).thenReturn(Arrays.asList(new String[] {"concept:name"}));
        ConfigData configData = new ConfigData("concept:name", 1, Integer.MAX_VALUE);
        PDAnalyst analyst = new PDAnalyst(contextData, configData, eventLogService);
    }

    @Test
    public void test_getCaseDetails() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        List<CaseDetails> caseDetails = analyst.getCaseDetails();
        assertEquals(1, caseDetails.size());
        assertEquals("Case1", caseDetails.get(0).getCaseId());
        assertEquals(1.0, caseDetails.get(0).getCaseIdDigit());
        assertEquals("Case", caseDetails.get(0).getCaseIdString());
        assertEquals(0, caseDetails.get(0).getDuration(), 0);
        assertEquals("instant", caseDetails.get(0).getDurationString());
        assertEquals(1, caseDetails.get(0).getCaseVariantId());
        assertEquals(1, caseDetails.get(0).getCaseEvents());
    }

    @Test
    public void test_getCaseVariantDetails() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        List<CaseVariantDetails> caseVariantDetails = analyst.getCaseVariantDetails();
        assertEquals(2, caseVariantDetails.size());
        assertEquals(1, caseVariantDetails.get(0).getCaseVariantId());
        assertEquals(2, caseVariantDetails.get(0).getActivityInstances());
        assertEquals(0, caseVariantDetails.get(0).getAvgDuration(), 0);
        assertEquals("instant", caseVariantDetails.get(0).getAvgDurationStr());
        assertEquals(0.5, caseVariantDetails.get(0).getFreq(), 0);
        assertEquals("50", caseVariantDetails.get(0).getFreqStr());
    }

    @Test
    public void test_filtered_getCaseVariantDetails() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());
        analyst.filter_RemoveEventsAnyValueOfEventAttribute("a", "concept:name");
        List<CaseVariantDetails> caseVariantDetails = analyst.getCaseVariantDetails();
        assertEquals(1, caseVariantDetails.size());
        assertEquals(1, caseVariantDetails.get(0).getCaseVariantId());
        assertEquals(2, caseVariantDetails.get(0).getActivityInstances());
        assertEquals(0, caseVariantDetails.get(0).getAvgDuration(), 0);
        assertEquals("instant", caseVariantDetails.get(0).getAvgDurationStr());
        assertEquals(1, caseVariantDetails.get(0).getFreq(), 0);
        assertEquals("100", caseVariantDetails.get(0).getFreqStr());
    }

    @Test
    public void test_getActivityDetails() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        List<PerspectiveDetails> actDetails = analyst.getActivityDetails();
        assertEquals(1, actDetails.size());
        assertEquals("a", actDetails.get(0).getValue());
        assertEquals(1, actDetails.get(0).getFreq(),0);
        assertEquals("100", actDetails.get(0).getFreqStr());
        assertEquals(1, actDetails.get(0).getOccurrences());
    }

    @Test
    public void test_getAttributeInfoList() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        ListIterable<AttributeInfo> attInfoList = analyst.getAttributeInfoList();
        assertEquals(1, attInfoList.size());
        assertEquals("a", attInfoList.get(0).getAttributeValue());
        assertEquals(1, attInfoList.get(0).getAttributeOccurrenceFrequency(), 0);
        assertEquals(1, attInfoList.get(0).getAttributeOccurrenceCount());
    }

    @Test
    public void test_getStatistics() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        assertEquals(1, analyst.getFilteredActivityInstanceSize());
        assertEquals(1, analyst.getFilteredCaseVariantSize());
        assertEquals(DateTimeUtils.humanize(DateTime.parse("2010-10-27T22:31:19.495+10:00").getMillis()), analyst.getFilteredStartTime());
        assertEquals(DateTimeUtils.humanize(DateTime.parse("2010-10-27T22:31:19.495+10:00").getMillis()), analyst.getFilteredEndTime());

        assertEquals("instant", analyst.getFilteredMinDuration());
        assertEquals("instant", analyst.getFilteredMaxDuration());
        assertEquals("instant", analyst.getFilteredMeanDuration());
        assertEquals("instant", analyst.getFilteredMedianDuration());
    }

    @Test
    public void test_getXLog() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        XLog xlog = analyst.getXLog();
        assertEquals(1, xlog.size());
        assertEquals(1, xlog.get(0).getAttributes().size());
        assertEquals("Case1", xlog.get(0).getAttributes().get("concept:name").toString());
        assertEquals(1, xlog.get(0).size());
        assertEquals(3, xlog.get(0).get(0).getAttributes().size());
        assertEquals("a", xlog.get(0).get(0).getAttributes().get("concept:name").toString());
        assertEquals(DateTime.parse("2010-10-27T22:31:19.495+10:00").getMillis(),
                ((XAttributeTimestamp)xlog.get(0).get(0).getAttributes().get("time:timestamp")).getValueMillis());
        assertEquals("complete", xlog.get(0).get(0).getAttributes().get("lifecycle:transition").toString());
    }

    @Test
    public void test_Filters() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithTwoTraceEachTwoEvents());

        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size()); //added artificial start and end events
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size()); //added artificial start and end events

        analyst.filter_RemoveTracesAnyValueOfEventAttribute("b", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RemoveTracesAnyValueOfEventAttribute("d", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RetainTracesAnyValueOfEventAttribute("a", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        // Need to confirm if this is a correct result
        analyst.filter_RetainTracesAnyValueOfEventAttribute("d", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RemoveEventsAnyValueOfEventAttribute("a", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RemoveEventsAnyValueOfEventAttribute("d", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RetainEventsAnyValueOfEventAttribute("a", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        // Need to confirm
        analyst.filter_RetainEventsAnyValueOfEventAttribute("d", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RemoveTracesAnyValueOfDirectFollowRelation("b => c", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("a", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RemoveTracesAnyValueOfDirectFollowRelation("a => c", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RetainTracesAnyValueOfDirectFollowRelation("b => c", "concept:name");
        assertEquals(1, attLog.getTraces().size());
        assertEquals(4, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals("b", attLog.getStringFromValue(attLog.getTraces().get(0).getValueTrace().get(1)));

        analyst.clearFilter();
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

        analyst.filter_RetainTracesAnyValueOfDirectFollowRelation("a => c", "concept:name"); // non-existent value
        assertEquals(2, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
        assertEquals(4, attLog.getTraces().get(1).getValueTrace().size());

    }

    @Test
    public void test_getActivityAttributeAverageMap() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithThreeTraceOneVariant());
        Map<String, String> activityAverages = analyst.getActivityAttributeAverageMap(1, 1);

        Map<String, String> expectedMap = Map.of(
                "concept:name", "a",
                "Average riskLevelNumber", "3.0"
        );

        assertEquals(expectedMap, activityAverages);
    }

    @Test
    public void test_filtered_getActivityAttributeAverageMap() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithThreeTraceOneVariant());
        analyst.filter_RemoveTracesAnyValueOfEventAttribute("low", "riskLevelString");
        Map<String, String> activityAverages = analyst.getActivityAttributeAverageMap(1, 1);

        Map<String, String> expectedMap = Map.of(
                "concept:name", "a",
                "Average riskLevelNumber", "3.5"
        );

        assertEquals(expectedMap, activityAverages);
    }

    @Test
    public void test_discoverTraceVariant_bad_variant_ID() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        try {
            analyst.discoverTraceVariant(100, null);
            fail("No exception found");
        } catch (Exception e) {
            String badVariantIDMsg = "No traces were found for trace variant id = 100";
            assertEquals(badVariantIDMsg, e.getMessage());
        }
    }

    @Test
    public void test_RetainingLayout() throws Exception {
        // Initial layout with standard options
        PDAnalyst analyst = createPDAnalyst(readLogWithStartCompleteEventsNonOverlapping());
        Layout layout1 = analyst.discoverProcess(
                createUserOptions(100, 100, 40,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        false, false,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false,
                        false)).get().getAbstraction().getLayout();

        // Change user options, layout must be retained

        // Change primary measure aggregation type
        Layout layout2 = analyst.discoverProcess(
                createUserOptions(100, 100, 40,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        false, false,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.MIN, // changed
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false,
                        false)).get().getAbstraction().getLayout();
        assertSame(layout1, layout2);

        // Change primary measure type
        Layout layout3 = analyst.discoverProcess(
                createUserOptions(100, 100, 40,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        false, false,
                        MeasureType.DURATION,       // changed
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false,
                        false)).get().getAbstraction().getLayout();
        assertSame(layout1, layout3);

        // Add secondary measure
        Layout layout4 = analyst.discoverProcess(
                createUserOptions(100, 100, 40,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        false, false,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        true,            // changed
                        false)).get().getAbstraction().getLayout();
        assertSame(layout1, layout4);


        // Change user options, layout is not retained

        // Change structural measure type
        Layout layout5 = analyst.discoverProcess(
                createUserOptions(100, 100, 40,
                        MeasureType.DURATION,           // changed
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false, false,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false,
                        false)).get().getAbstraction().getLayout();
        assertNotSame(layout1, layout5);

        // Change perspective attribute
        analyst.setMainAttribute(Constants.ATT_KEY_LIFECYCLE_TRANSITION); // changed
        Layout layout6 = analyst.discoverProcess(
                createUserOptions(100, 100, 40,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        false, false,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false,
                        false)).get().getAbstraction().getLayout();
        assertNotSame(layout1, layout6);

        // Change graph filtering threshold
        Layout layout7 = analyst.discoverProcess(
                createUserOptions(100, 10, 40, // Changed
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        false, false,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false,
                        false)).get().getAbstraction().getLayout();
        assertNotSame(layout1, layout7);

        // Change the weight ordering of nodes/arcs in the structural measure
        Layout layout8 = analyst.discoverProcess(
                createUserOptions(100, 10, 40,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        true, true, // changed
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false,
                        false)).get().getAbstraction().getLayout();
        assertNotSame(layout1, layout8);

        // After applying filter, layout must change
        analyst.filter_RemoveTracesAnyValueOfEventAttribute("b", "concept:name");
        Layout layout9 = analyst.discoverProcess(
                createUserOptions(100, 100, 40,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        false, false,
                        MeasureType.FREQUENCY,
                        MeasureAggregation.CASES,
                        MeasureRelation.ABSOLUTE,
                        MeasureType.DURATION,
                        MeasureAggregation.MEAN,
                        MeasureRelation.ABSOLUTE,
                        false,
                        false)).get().getAbstraction().getLayout();
        assertNotSame(layout1, layout9);
    }

    @Test
    public void test_SimulationData() throws Exception {
        UserOptionsData userOptions = createUserOptions(100, 100, 40,
                MeasureType.FREQUENCY,
                MeasureAggregation.CASES,
                MeasureRelation.ABSOLUTE,
                false, false,
                MeasureType.FREQUENCY,
                MeasureAggregation.CASES,
                MeasureRelation.ABSOLUTE,
                MeasureType.DURATION,
                MeasureAggregation.MEAN,
                MeasureRelation.ABSOLUTE,
                false,
                false);
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceStartCompleteEventsNonOverlapping());
        Abstraction abs = analyst.discoverProcess(userOptions).get().getAbstraction();
        SimulationData data = analyst.getSimulationData(abs);
        assertEquals(1, data.getCaseCount());
        assertEquals(5, data.getResourceCount());
        assertEquals(DateTime.parse("2010-10-27T21:59:19.308+10:00").getMillis(), data.getStartTime());
        assertEquals(DateTime.parse("2010-10-27T22:55:19.308+10:00").getMillis(), data.getEndTime());
        assertEquals(60, data.getDiagramNodeDuration(getNodeId("a", abs.getDiagram())), 0.0);
        assertEquals(60, data.getDiagramNodeDuration(getNodeId("b", abs.getDiagram())), 0.0);
        assertEquals(60, data.getDiagramNodeDuration(getNodeId("c", abs.getDiagram())), 0.0);
        assertEquals(60, data.getDiagramNodeDuration(getNodeId("d", abs.getDiagram())), 0.0);

        // Filter out some events
        analyst.filter_RemoveEventsAnyValueOfEventAttribute("c", "concept:name");
        Abstraction abs2 = analyst.discoverProcess(userOptions).get().getAbstraction();
        SimulationData data2 = analyst.getSimulationData(abs2);
        assertEquals(1, data2.getCaseCount());
        assertEquals(4, data2.getResourceCount()); // changed
        assertEquals(DateTime.parse("2010-10-27T21:59:19.308+10:00").getMillis(), data2.getStartTime());
        assertEquals(DateTime.parse("2010-10-27T22:45:19.308+10:00").getMillis(), data2.getEndTime()); // changed
        assertEquals(60, data.getDiagramNodeDuration(getNodeId("b", abs2.getDiagram())), 0.0);
    }

    private String getNodeId(String label, BPMNDiagram diagram) {
        return diagram.getNodes().stream()
                .filter(n -> n.getLabel().equals(label))
                .findFirst()
                .get()
                .getId()
                .toString();
    }

}
