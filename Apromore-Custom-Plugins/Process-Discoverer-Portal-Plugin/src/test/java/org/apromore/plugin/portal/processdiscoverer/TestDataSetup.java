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

package org.apromore.plugin.portal.processdiscoverer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.portal.util.CostTable;
import org.apromore.service.EventLogService;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TestDataSetup {
    @Mock
    protected EventLogService eventLogService;

    public PDAnalyst createPDAnalyst(XLog xlog) throws Exception {
        return createPDAnalyst(xlog, getAllDayAllTimeCalendar(), CostTable.EMPTY);
    }

    public PDAnalyst createPDAnalyst(XLog xlog, CalendarModel calendarModel, CostTable costTable) throws Exception {
        ContextData contextData = ContextData.valueOf("domain1", "username1", 0,
            "logName", 0, "folderName", true, true);
        Mockito.when(eventLogService.getXLog(contextData.getLogId())).thenReturn(xlog);
        Mockito.when(eventLogService.getAggregatedLog(contextData.getLogId())).thenReturn(
            XLogToImmutableLog.convertXLog("ProcessLog", xlog));
        Mockito.when(eventLogService.getCalendarFromLog(contextData.getLogId())).thenReturn(calendarModel);
        Mockito.when(eventLogService.getPerspectiveTagByLog(contextData.getLogId())).thenReturn(
            Arrays.asList(new String[] {"concept:name", "lifecycle:transition"}));
        ConfigData configData = ConfigData.DEFAULT;
        PDAnalyst analyst = new PDAnalyst(contextData, configData, eventLogService);
        analyst.loadAttributeData(configData.getDefaultAttribute(), calendarModel, costTable);
        return analyst;
    }

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    private XLog readXESFile(String fullFilePath) {
        XesXmlParser parser = new XesXmlParser();
        try {
            return parser.parse(new File(fullFilePath)).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private XLog readXESCompressedFile(String fullFilePath) {
        XesXmlParser parser = new XesXmlGZIPParser();
        try {
            return parser.parse(new File(fullFilePath)).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CalendarModel getAllDayAllTimeCalendar() {
        return new CalendarModelBuilder().withAllDayAllTime().build();
    }

    public XLog readLogWithOneTraceOneEvent() {
        return this.readXESFile("src/test/logs/L1_1trace_1event.xes");
    }

    public XLog readLogWithOneTraceOneEvent_NoConceptName() {
        return this.readXESFile("src/test/logs/L1_1trace_1event_no_concept_name.xes");
    }

    public XLog readLogWithTwoTraceEachTwoEvents() {
        return this.readXESFile("src/test/logs/L1_2traces_each_1event.xes");
    }

    public XLog readLogWithThreeTraceOneVariant() {
        return this.readXESFile("src/test/logs/L1_3traces_1variant.xes");
    }

    public XLog readLogWithThreeTraceOneVariantMissingValues() {
        return this.readXESFile("src/test/logs/L1_3traces_1variant_missing_values.xes");
    }

    public XLog readLogWithStartCompleteEventsNonOverlapping() {
        return this.readXESFile("src/test/logs/L1_start_complete_no_overlapping.xes");
    }

    public XLog readLogWithOneTraceStartCompleteEventsNonOverlapping() {
        return this.readXESFile("src/test/logs/L1_1trace_start_complete_events_non_overlapping.xes");
    }

    public XLog readLogWithOneTraceStartCompleteEventsNonOverlappingWithRoles() {
        return this.readXESFile("src/test/logs/L1_1trace_start_complete_events_non_overlapping_with_roles.xes");
    }

    public XLog readLogWithOneTraceStartCompleteEventsNonOverlappingWithMissingRoles() {
        return this.readXESFile("src/test/logs/L1_1trace_start_complete_events_non_overlapping_with_missing_roles.xes");
    }

    public JSONArray readJSON_DFG_Frequency_LogWithStartCompleteEventsNonOverlapping_100_100() throws JSONException, FileNotFoundException {
        JSONTokener tokener = new JSONTokener(new FileReader("src/test/logs/L1_start_complete_no_overlapping_DFG_100_100_Frequency.json"));
        return new JSONArray(tokener);
    }

    public JSONArray readJSON_DFG_Duration_LogWithStartCompleteEventsNonOverlapping_100_100() throws JSONException, FileNotFoundException {
        JSONTokener tokener = new JSONTokener(new FileReader("src/test/logs/L1_start_complete_no_overlapping_DFG_100_100_Duration.json"));
        return new JSONArray(tokener);
    }

    public JSONArray readJSON_DFG_DoubleWeight_LogWithStartCompleteEventsNonOverlapping_100_100() throws JSONException, FileNotFoundException {
        JSONTokener tokener = new JSONTokener(new FileReader("src/test/logs/L1_start_complete_no_overlapping_DFG_100_100_DoubleWeight.json"));
        return new JSONArray(tokener);
    }

    public JSONArray readJSON_BPMN_Frequency_LogWithStartCompleteEventsNonOverlapping_100_100() throws JSONException, FileNotFoundException {
        JSONTokener tokener = new JSONTokener(new FileReader("src/test/logs/L1_start_complete_no_overlapping_BPMN_100_100_Frequency.json"));
        return new JSONArray(tokener);
    }

    public JSONArray readJSON_BPMN_Duration_LogWithStartCompleteEventsNonOverlapping_100_100() throws JSONException, FileNotFoundException {
        JSONTokener tokener = new JSONTokener(new FileReader("src/test/logs/L1_start_complete_no_overlapping_BPMN_100_100_Duration.json"));
        return new JSONArray(tokener);
    }

    public JSONArray readJSON_BPMN_DoubleWeight_LogWithStartCompleteEventsNonOverlapping_100_100() throws JSONException, FileNotFoundException {
        JSONTokener tokener = new JSONTokener(new FileReader("src/test/logs/L1_start_complete_no_overlapping_BPMN_100_100_DoubleWeight.json"));
        return new JSONArray(tokener);
    }

    public XLog readLogWithStartCompleteEventsOverlapping() {
        return this.readXESFile("src/test/logs/L1_start_complete_overlapping.xes");
    }

    public XLog readRealLog_BPI12() {
        return this.readXESCompressedFile("src/test/logs/financial_log.xes.gz");

    }

    public XLog readRealLog_BPI15() {
        return this.readXESCompressedFile("src/test/logs/BPIC15 Municipality 1.xes.gz");
    }

    public XLog readRealLog_BPI17_Application() {
        return this.readXESCompressedFile("src/test/logs/BPI Challenge 2017 - Application log.xes.gz");
    }

    public XLog readRealLog_BPI17_Offer() {
        return this.readXESCompressedFile("src/test/logs/BPI Challenge 2017 - Offer log.xes.gz");
    }

    public XLog readRealLog_BPI18() {
        return this.readXESCompressedFile("src/test/logs/BPI Challenge 2018.xes.gz");

    }

    public XLog readRealLog_teys() {
        return this.readXESCompressedFile("src/test/logs/teys_complete_cases.xes.gz");

    }

    public XLog readRealLog_procmin() {
        return this.readXESCompressedFile("src/test/logs/procmin20180612_F2_5M.xes.gz");
    }

    protected OutputData discoverProcess(XLog xlog,
                                         double nodeSlider, double arcSlider, double paraSlider,
                                         MeasureType structureType,
                                         MeasureAggregation structureAggregate,
                                         MeasureRelation structureRelation,
                                         MeasureType primaryType,
                                         MeasureAggregation primaryAggregate,
                                         MeasureRelation primaryRelation,
                                         MeasureType secondaryType,
                                         MeasureAggregation secondaryAggregate,
                                         MeasureRelation secondaryRelation,
                                         boolean useSecondary,
                                         boolean bpmn) throws Exception {
        PDAnalyst analyst = createPDAnalyst(xlog);
        UserOptionsData userOptions = createUserOptions(nodeSlider, arcSlider, paraSlider,
                structureType, structureAggregate, structureRelation,
                false, false,
                primaryType, primaryAggregate, primaryRelation,
                secondaryType, secondaryAggregate, secondaryRelation,
                useSecondary, bpmn);
        return analyst.discoverProcess(userOptions).get();
    }

    protected UserOptionsData createUserOptions(double nodeSlider, double arcSlider, double paraSlider,
                                                MeasureType structureType,
                                                MeasureAggregation structureAggregate,
                                                MeasureRelation structureRelation,
                                                boolean invertedNodes, boolean invertedArcs,
                                                MeasureType primaryType,
                                                MeasureAggregation primaryAggregate,
                                                MeasureRelation primaryRelation,
                                                MeasureType secondaryType,
                                                MeasureAggregation secondaryAggregate,
                                                MeasureRelation secondaryRelation,
                                                boolean useSecondary,
                                                boolean bpmn)
                                                throws Exception {
        UserOptionsData userOptions = UserOptionsData.DEFAULT(ConfigData.DEFAULT);

        userOptions.setNodeFilterValue(nodeSlider);
        userOptions.setArcFilterValue(arcSlider);
        userOptions.setParallelismFilterValue(paraSlider);

        userOptions.setFixedType(structureType);
        userOptions.setFixedAggregation(structureAggregate);
        userOptions.setFixedRelation(structureRelation);

        userOptions.setInvertedNodesMode(invertedNodes);
        userOptions.setInvertedArcsMode(invertedArcs);

        userOptions.setPrimaryType(primaryType);
        userOptions.setPrimaryAggregation(primaryAggregate);
        userOptions.setPrimaryRelation(primaryRelation);

        userOptions.setSecondaryType(secondaryType);
        userOptions.setSecondaryAggregation(secondaryAggregate);
        userOptions.setSecondaryRelation(secondaryRelation);

        userOptions.setIncludeSecondary(useSecondary);
        userOptions.setBPMNMode(bpmn);

        return userOptions;
    }
}
