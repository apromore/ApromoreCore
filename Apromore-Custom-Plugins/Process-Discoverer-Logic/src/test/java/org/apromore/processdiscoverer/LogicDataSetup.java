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

package org.apromore.processdiscoverer;

import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.plugins.bpmn.plugins.BpmnImportPlugin;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LogicDataSetup {
    private BpmnImportPlugin bpmnImport = new BpmnImportPlugin();
    
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
    
    
    private BPMNDiagram readBPMNDiagram(String fullFilePath) throws FileNotFoundException, Exception {
        return bpmnImport.importFromStreamToDiagram(new FileInputStream(new File(fullFilePath)), fullFilePath);
    }
    
    public CalendarModel getAllDayAllTimeCalendar() {
        return new CalendarModelBuilder().withAllDayAllTime().build();
    }
    
    // ----------------------------------------------------
    
    public XLog readLogWithOneTraceOneEvent() {
        return this.readXESFile("src/test/logs/L1_1trace_1event.xes");
    }

    public XLog readLogWithOneTrace_StartCompleteEvents() {
        return this.readXESFile("src/test/logs/L1_1trace_2activity_with_start_complete_events.xes");
    }
    
    public BPMNDiagram readDFG_LogWithOneTraceOneEvent() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_1trace_1event.bpmn");
    }

    public BPMNDiagram readDFG_LogWithOneTrace_StartCompleteEvents() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_1trace_2activity_with_start_complete_events.bpmn");
    }
    
    //-------------------------------------------------
    
    public BPMNDiagram readDFG_LogWithCompleteEventsOnly() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_complete_events_only_with_resources_DFG.bpmn");
    }
    
    public BPMNDiagram readDFG_LogWithCompleteEventsOnly_100_10() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_complete_events_only_with_resources_DFG_100_10.bpmn");
    }
    
    public BPMNDiagram readDFG_LogWithCompleteEventsOnly_100_10_Filtered() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_complete_events_only_with_resources_DFG_100_10_filtered.bpmn");
    }
    
    public BPMNDiagram readBPMN_LogWithCompleteEventsOnly() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_complete_events_only_with_resources_BPMN.bpmn");
    }
    
    public XLog readLogWithCompleteEventsOnly() {
        return this.readXESFile("src/test/logs/L1_complete_events_only_with_resources.xes");
        
    }
    
    //-------------------------------------------------
    
    public BPMNDiagram readDFG_LogWithStartCompleteEventsOverlapping() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_start_complete_overlapping_DFG.bpmn");
    }
    
    public BPMNDiagram readBPMN_LogWithStartCompleteEventsOverlapping() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_start_complete_overlapping_BPMN.bpmn");
    }
    
    public XLog readLogWithStartCompleteEventsOverlapping() {
        return this.readXESFile("src/test/logs/L1_start_complete_overlapping.xes");
        
    }
    
    public XLog read_Sepsis() {
        return this.readXESCompressedFile("src/test/logs/Sepsis.xes.gz");
    }
    
    public BPMNDiagram readBPMN_Sepsis_100_30() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/Sepsis_100_30_BPMN.bpmn");
    }
    
    public BPMNDiagram readDFG_Sepsis_100_10() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/Sepsis_100_10_DFG.bpmn");
    }
    
    public XLog read_SimpleLog3Traces() {
        return this.readXESFile("src/test/logs/L1_complete_events_only_with_resources_3traces.xes");
    }
    
    public BPMNDiagram readDFG_SimpleLog3Traces_100_10() throws FileNotFoundException, Exception {
        return this.readBPMNDiagram("src/test/logs/L1_complete_events_only_with_resources_3traces.bpmn");
    }
    
}
