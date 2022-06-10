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

package org.apromore.plugin.portal.processdiscoverer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
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
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.portal.util.CostTable;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.abstraction.BPMNAbstraction;
import org.apromore.processdiscoverer.layout.Layout;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processsimulation.dto.EdgeFrequency;
import org.apromore.processsimulation.dto.SimulationData;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.api.list.ListIterable;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PDAnalystTest extends TestDataSetup {

    @Test
    void test_AnalystConstructor_ValidData() throws Exception {
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

        assertTrue(((List) analyst.getCurrentFilterCriteria()).isEmpty());

        AttributeLog attLog = analyst.getAttributeLog();
        assertEquals(1, attLog.getTraces().size());
        assertEquals(3, attLog.getTraces().get(0).getValueTrace().size());
    }

    @Test
    void test_AnalystConstructor_MissingActivityPerspective() throws Exception {
        XLog validLog = readLogWithOneTraceOneEvent();
        ContextData contextData = ContextData.valueOf("domain1", "username1", 0,
            "logName", 0, "folderName", false, true);
        Mockito.when(eventLogService.getXLog(contextData.getLogId())).thenReturn(validLog);
        Mockito.when(eventLogService.getAggregatedLog(contextData.getLogId())).thenReturn(
            XLogToImmutableLog.convertXLog("ProcessLog", validLog));
        Mockito.when(eventLogService.getPerspectiveTagByLog(contextData.getLogId()))
            .thenReturn(Arrays.asList("org:resource"));
        ConfigData configData = ConfigData.DEFAULT;

        assertThrows(InvalidDataException.class, () -> new PDAnalyst(contextData, configData, eventLogService));
    }

    @Test
    void test_AnalystConstructor_NoPerspectiveAttributes() throws Exception {
        XLog validLog = readLogWithOneTraceOneEvent();
        ContextData contextData = ContextData.valueOf("domain1", "username1", 0,
            "logName", 0, "folderName", false, true);
        Mockito.when(eventLogService.getXLog(contextData.getLogId())).thenReturn(validLog);
        Mockito.when(eventLogService.getAggregatedLog(contextData.getLogId())).thenReturn(
            XLogToImmutableLog.convertXLog("ProcessLog", validLog));
        Mockito.when(eventLogService.getPerspectiveTagByLog(contextData.getLogId())).thenReturn(new ArrayList<>());
        ConfigData configData = ConfigData.DEFAULT;

        assertThrows(InvalidDataException.class, () -> new PDAnalyst(contextData, configData, eventLogService));
    }

    @Test
    void test_AnalystConstructor_TooManyPerspectiveAttributeValues() throws Exception {
        XLog validLog = readLogWithTwoTraceEachTwoEvents();
        ContextData contextData = ContextData.valueOf("domain1", "username1", 0,
            "logName", 0, "folderName", false, true);
        Mockito.when(eventLogService.getXLog(contextData.getLogId())).thenReturn(validLog);
        Mockito.when(eventLogService.getAggregatedLog(contextData.getLogId())).thenReturn(
            XLogToImmutableLog.convertXLog("ProcessLog", validLog));
        Mockito.when(eventLogService.getPerspectiveTagByLog(contextData.getLogId()))
            .thenReturn(Arrays.asList(new String[] {"concept:name"}));
        ConfigData configData = new ConfigData("concept:name", 1, Integer.MAX_VALUE);

        assertThrows(InvalidDataException.class, () -> new PDAnalyst(contextData, configData, eventLogService));
    }

    @Test
    void test_getCaseDetails() throws Exception {
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
    void test_getCaseVariantDetails() throws Exception {
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
    void test_filtered_getCaseVariantDetails() throws Exception {
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
    void test_getActivityDetails() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        List<PerspectiveDetails> actDetails = analyst.getActivityDetails();
        assertEquals(1, actDetails.size());
        assertEquals("a", actDetails.get(0).getValue());
        assertEquals(1, actDetails.get(0).getFreq(), 0);
        assertEquals("100", actDetails.get(0).getFreqStr());
        assertEquals(1, actDetails.get(0).getOccurrences());
    }

    @Test
    void test_getAttributeInfoList() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        ListIterable<AttributeInfo> attInfoList = analyst.getAttributeInfoList();
        assertEquals(1, attInfoList.size());
        assertEquals("a", attInfoList.get(0).getAttributeValue());
        assertEquals(1, attInfoList.get(0).getAttributeOccurrenceFrequency(), 0);
        assertEquals(1, attInfoList.get(0).getAttributeOccurrenceCount());
    }

    @Test
    void test_getStatistics() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        assertEquals(1, analyst.getFilteredActivityInstanceSize());
        assertEquals(1, analyst.getFilteredCaseVariantSize());
        assertEquals(DateTimeUtils.humanize(DateTime.parse("2010-10-27T22:31:19.495+10:00").getMillis()),
            analyst.getFilteredStartTime());
        assertEquals(DateTimeUtils.humanize(DateTime.parse("2010-10-27T22:31:19.495+10:00").getMillis()),
            analyst.getFilteredEndTime());

        assertEquals("instant", analyst.getFilteredMinDuration());
        assertEquals("instant", analyst.getFilteredMaxDuration());
        assertEquals("instant", analyst.getFilteredMeanDuration());
        assertEquals("instant", analyst.getFilteredMedianDuration());
    }

    @Test
    void test_getXLog() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceOneEvent());
        XLog xlog = analyst.getXLog();
        assertEquals(1, xlog.size());
        assertEquals(1, xlog.get(0).getAttributes().size());
        assertEquals("Case1", xlog.get(0).getAttributes().get("concept:name").toString());
        assertEquals(1, xlog.get(0).size());
        assertEquals(3, xlog.get(0).get(0).getAttributes().size());
        assertEquals("a", xlog.get(0).get(0).getAttributes().get("concept:name").toString());
        assertEquals(DateTime.parse("2010-10-27T22:31:19.495+10:00").getMillis(),
            ((XAttributeTimestamp) xlog.get(0).get(0).getAttributes().get("time:timestamp")).getValueMillis());
        assertEquals("complete", xlog.get(0).get(0).getAttributes().get("lifecycle:transition").toString());
    }

    @Test
    void test_Filters() throws Exception {
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
    void test_getActivityAttributeAverageMap() throws Exception {
        PDAnalyst analyst = createPDAnalyst(readLogWithThreeTraceOneVariantMissingValues());
        Map<String, String> activityAverages = analyst.getActivityAttributeAverageMap(1, 1);

        Map<String, String> expectedMap = Map.of(
            "concept:name", "a",
            "Average riskLevelNumber", "2.5"
        );

        assertEquals(expectedMap, activityAverages);
    }

    @Test
    void test_filtered_getActivityAttributeAverageMap() throws Exception {
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
    void test_discoverTraceVariant_bad_variant_ID() throws Exception {
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
    void test_RetainingLayout() throws Exception {
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
        UserOptionsData userOptions = createUserOptions(100, 100, 40,
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
            false);
        Layout layout5 = analyst.discoverProcess(userOptions).get().getAbstraction().getLayout();
        assertNotSame(layout1, layout5);

        // Change perspective attribute
        analyst.loadAttributeData(Constants.ATT_KEY_LIFECYCLE_TRANSITION,
            userOptions.getCalendarModel(),
            userOptions.getCostTable()); // changed
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
    void test_SimulationData() throws Exception {
        // given
        CalendarModel businessCalendar = new CalendarModelBuilder().with5DayWorking().build();
        businessCalendar.setName("Business_Calendar");

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
        userOptions.setCalendarModel(businessCalendar);

        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceStartCompleteEventsNonOverlapping(),
            businessCalendar, CostTable.EMPTY);
        Abstraction abs = analyst.discoverProcess(userOptions).get().getAbstraction();
        BPMNAbstraction bpmnAbstraction = analyst.convertToBpmnAbstractionForExport(abs);

        // when
        SimulationData data = analyst.getSimulationData(bpmnAbstraction, userOptions);

        // then
        assertEquals(1, data.getCaseCount());
        assertEquals(5, data.getResourceCount());
        assertEquals(1, data.getResourceCountsByRole().size());
        assertEquals(5, data.getResourceCountsByRole().get("DEFAULT_ROLE").intValue());
        assertEquals(0, data.getLogId());
        assertEquals("DEFAULT_ROLE", data.getRoleNameByNodeId(getNodeId("a", bpmnAbstraction.getDiagram())));
        assertEquals("DEFAULT_ROLE", data.getRoleNameByNodeId(getNodeId("b", bpmnAbstraction.getDiagram())));
        assertEquals("DEFAULT_ROLE", data.getRoleNameByNodeId(getNodeId("c", bpmnAbstraction.getDiagram())));
        assertEquals("DEFAULT_ROLE", data.getRoleNameByNodeId(getNodeId("d", bpmnAbstraction.getDiagram())));
        assertEquals(DateTime.parse("2010-10-27T21:59:19.308+10:00").getMillis(), data.getStartTime());
        assertEquals(DateTime.parse("2010-10-27T22:55:19.308+10:00").getMillis(), data.getEndTime());
        assertEquals(60000, data.getDiagramNodeDuration(getNodeId("a", bpmnAbstraction.getDiagram())), 0.0);
        assertEquals(60000, data.getDiagramNodeDuration(getNodeId("b", bpmnAbstraction.getDiagram())), 0.0);
        assertEquals(60000, data.getDiagramNodeDuration(getNodeId("c", bpmnAbstraction.getDiagram())), 0.0);
        assertEquals(60000, data.getDiagramNodeDuration(getNodeId("d", bpmnAbstraction.getDiagram())), 0.0);

        Map<String, Map<String, Double>> expectedEdgeFrequencies = Map.of(
            "node4", Map.of("edge3", 0.0, "edge4", 1.0),
            "node5", Map.of("edge5", 1.0),
            "node8", Map.of("edge11", 1.0),
            "node9", Map.of("edge9", 1.0, "edge10", 1.0)
        );
        assertGateways(expectedEdgeFrequencies, data.getEdgeFrequencies());

        assertNotNull(data.getCalendarModel());
        assertEquals("Business_Calendar", data.getCalendarModel().getName());

        // Filter out some events
        analyst.filter_RemoveEventsAnyValueOfEventAttribute("c", "concept:name");
        Abstraction abs2 = analyst.discoverProcess(userOptions).get().getAbstraction();
        BPMNAbstraction bpmnAbstraction2 = analyst.convertToBpmnAbstractionForExport(abs);
        SimulationData data2 = analyst.getSimulationData(
            analyst.convertToBpmnAbstractionForExport(abs2),
            userOptions);
        assertEquals(1, data2.getCaseCount());
        assertEquals(4, data2.getResourceCount()); // changed
        assertEquals(0, data2.getLogId());
        assertEquals(DateTime.parse("2010-10-27T21:59:19.308+10:00").getMillis(), data2.getStartTime());
        assertEquals(DateTime.parse("2010-10-27T22:45:19.308+10:00").getMillis(), data2.getEndTime()); // changed
        assertEquals(60000, data2.getDiagramNodeDuration(getNodeId("b", bpmnAbstraction2.getDiagram())), 0.0);

        Map<String, Map<String, Double>> expectedEdgeFrequencies2 = Map.of(
            "node10", Map.of("edge14", 1.0, "edge17", 1.0, "edge18", 0.0),
            "node8", Map.of("edge9", 1.0, "edge20", 1.0),
            "node9", Map.of("edge15", 1.0),
            "node7", Map.of("edge10", 1.0)
        );
        assertGateways(expectedEdgeFrequencies2, data2.getEdgeFrequencies());
    }

    @Test
    void test_SimulationData_with_roles() throws Exception {
        // given
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
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceStartCompleteEventsNonOverlappingWithRoles());
        Abstraction abs = analyst.discoverProcess(userOptions).get().getAbstraction();
        BPMNAbstraction bpmnAbstraction = analyst.convertToBpmnAbstractionForExport(abs);

        // when
        SimulationData data = analyst.getSimulationData(bpmnAbstraction, userOptions);

        // then
        assertEquals(1, data.getCaseCount());
        assertEquals(5, data.getResourceCount());
        assertEquals(6, data.getResourceCountsByRole().size());
        assertEquals(2, data.getResourceCountsByRole().get("role_1").intValue());
        assertEquals(2, data.getResourceCountsByRole().get("role_2").intValue());
        assertEquals(1, data.getResourceCountsByRole().get("role_3").intValue());
        assertEquals(1, data.getResourceCountsByRole().get("role_4").intValue());
        assertEquals(4, data.getResourceCountsByRole().get("DEFAULT_ROLE").intValue());
        assertEquals(0, data.getLogId());
        assertEquals("role_1", data.getRoleNameByNodeId(getNodeId("a", bpmnAbstraction.getDiagram())));
        assertEquals("role_2", data.getRoleNameByNodeId(getNodeId("b", bpmnAbstraction.getDiagram())));
        assertEquals("role_3", data.getRoleNameByNodeId(getNodeId("c", bpmnAbstraction.getDiagram())));
        assertEquals("role_4", data.getRoleNameByNodeId(getNodeId("d", bpmnAbstraction.getDiagram())));
        assertEquals("DEFAULT_ROLE", data.getRoleNameByNodeId(getNodeId("e", bpmnAbstraction.getDiagram())));
        assertEquals("role_5", data.getRoleNameByNodeId(getNodeId("f", bpmnAbstraction.getDiagram())));
    }

    @Test
    void test_SimulationData_role_precedence_for_activities() throws Exception {
        // given
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
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceStartCompleteEventsNonOverlappingWithMissingRoles());
        Abstraction abs = analyst.discoverProcess(userOptions).get().getAbstraction();
        BPMNAbstraction bpmnAbstraction = analyst.convertToBpmnAbstractionForExport(abs);

        // when
        SimulationData data = analyst.getSimulationData(bpmnAbstraction, userOptions);

        // then
        assertEquals(1, data.getCaseCount());
        assertEquals(5, data.getResourceCount());
        assertEquals(5, data.getResourceCountsByRole().size());
        assertEquals(2, data.getResourceCountsByRole().get("role_1").intValue());
        assertEquals(2, data.getResourceCountsByRole().get("role_2").intValue());
        assertEquals(1, data.getResourceCountsByRole().get("role_3").intValue());
        assertEquals(1, data.getResourceCountsByRole().get("role_4").intValue());
        assertEquals(4, data.getResourceCountsByRole().get("DEFAULT_ROLE").intValue());
        assertEquals(0, data.getLogId());
        assertEquals("role_2", data.getRoleNameByNodeId(getNodeId("a", bpmnAbstraction.getDiagram())));
        assertEquals("role_2", data.getRoleNameByNodeId(getNodeId("b", bpmnAbstraction.getDiagram())));
        assertEquals("role_3", data.getRoleNameByNodeId(getNodeId("c", bpmnAbstraction.getDiagram())));
        assertEquals("role_4", data.getRoleNameByNodeId(getNodeId("d", bpmnAbstraction.getDiagram())));
        assertEquals("DEFAULT_ROLE", data.getRoleNameByNodeId(getNodeId("e", bpmnAbstraction.getDiagram())));
        assertEquals("role_1", data.getRoleNameByNodeId(getNodeId("f", bpmnAbstraction.getDiagram())));
    }

    @Test
    void test_simulation_data_with_null_abstraction() throws Exception {
        // given
        PDAnalyst analyst = createPDAnalyst(readLogWithOneTraceStartCompleteEventsNonOverlapping());

        // when
        SimulationData data = analyst.getSimulationData(null,
            UserOptionsData.DEFAULT(ConfigData.DEFAULT));

        // then
        assertNull(data);
    }

    @Test
    void test_only_xor_gateways_in_simulation_data() throws Exception {
        // given
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
        BPMNAbstraction bpmnAbstraction = analyst.convertToBpmnAbstractionForExport(abs);

        assertEquals(4, bpmnAbstraction.getDiagram().getGateways().size());
        bpmnAbstraction.getDiagram().addGateway("MockGw1", Gateway.GatewayType.INCLUSIVE);
        bpmnAbstraction.getDiagram().addGateway("MockGw2", Gateway.GatewayType.EVENTBASED);
        bpmnAbstraction.getDiagram().addGateway("MockGw3", Gateway.GatewayType.PARALLEL);
        bpmnAbstraction.getDiagram().addGateway("MockGw4", Gateway.GatewayType.COMPLEX);
        assertEquals(8, bpmnAbstraction.getDiagram().getGateways().size());

        // when
        SimulationData data = analyst.getSimulationData(bpmnAbstraction, userOptions);

        // then
        Map<String, Map<String, Double>> expectedEdgeFrequencies = Map.of(
            "node4", Map.of("edge3", 0.0, "edge4", 1.0),
            "node5", Map.of("edge5", 1.0),
            "node8", Map.of("edge11", 1.0),
            "node9", Map.of("edge9", 1.0, "edge10", 1.0)
        );
        assertGateways(expectedEdgeFrequencies, data.getEdgeFrequencies());

    }

    private void assertGateways(
        Map<String, Map<String, Double>> expectedEdgeFrequencies,
        Map<String, List<EdgeFrequency>> actualEdgeFrequencies) {

        assertEquals(expectedEdgeFrequencies.size(), actualEdgeFrequencies.size());
        expectedEdgeFrequencies.entrySet().forEach(expectedEntry -> {
            assertEdgeFrequencies(expectedEntry.getValue(), actualEdgeFrequencies.get(expectedEntry.getKey()));
        });
    }

    private void assertEdgeFrequencies(Map<String, Double> expectedEdgeFrequencies,
                                       List<EdgeFrequency> actualEdgeFrequencies) {
        assertEquals(expectedEdgeFrequencies.size(), actualEdgeFrequencies.size());
        assertTrue(expectedEdgeFrequencies.keySet().containsAll(
            actualEdgeFrequencies.stream().map(EdgeFrequency::getEdgeId).collect(Collectors.toList())));
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
