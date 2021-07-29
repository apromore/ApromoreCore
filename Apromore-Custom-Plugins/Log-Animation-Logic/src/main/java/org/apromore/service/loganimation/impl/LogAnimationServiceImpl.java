/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2017 Queensland University of Technology.
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

package org.apromore.service.loganimation.impl;

import java.io.IOException;
// Java 2 Standard Edition
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.loganimation.LogAnimationService;
import org.apromore.service.loganimation.json.AnimationJSONBuilder;
import org.apromore.service.loganimation.replay.AnimationLog;
import org.apromore.service.loganimation.replay.BPMNDiagramHelper;
import org.apromore.service.loganimation.replay.Optimizer;
import org.apromore.service.loganimation.replay.ReplayParams;
import org.apromore.service.loganimation.replay.ReplayTrace;
import org.apromore.service.loganimation.replay.Replayer;
// Third party packages
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
//import org.apromore.processmining.plugins.signaturediscovery.encoding.EncodeTraces;
//import org.apromore.processmining.plugins.signaturediscovery.encoding.EncodingNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

@Service("logAnimationService")
@Deprecated 
public class LogAnimationServiceImpl extends DefaultParameterAwarePlugin implements LogAnimationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnimationServiceImpl.class);

    @Override
    public String createAnimation(String bpmn, List<Log> logs) throws BpmnConverterException, IOException, JAXBException, JSONException {

        Set<XLog> xlogs = new HashSet<>();
        for (Log log: logs) {
            xlogs.add(log.xlog);
        }

        Definitions bpmnDefinition = BPMN2DiagramConverter.parseBPMN(bpmn, getClass().getClassLoader());

        /*
        * ------------------------------------------
        * Optimize logs and process model
        * ------------------------------------------
        */
        Optimizer optimizer = new Optimizer();
        for (Log log : logs) {
            log.xlog = optimizer.optimizeLog(log.xlog);
        }
        bpmnDefinition = optimizer.optimizeProcessModel(bpmnDefinition);


        /*
        * ------------------------------------------
        * Check BPMN diagram validity and replay log
        * ------------------------------------------
        */
        //Reading backtracking properties for testing
        String propertyFile = "properties.xml";
        InputStream is = getClass().getClassLoader().getResourceAsStream(propertyFile);
        Properties props = new Properties();
        props.loadFromXML(is);
        ReplayParams params = new ReplayParams();
        params.setMaxCost(Double.valueOf(props.getProperty("MaxCost")).doubleValue());
        params.setMaxDepth(Integer.valueOf(props.getProperty("MaxDepth")).intValue());
        params.setMinMatchPercent(Double.valueOf(props.getProperty("MinMatchPercent")).doubleValue());
        params.setMaxMatchPercent(Double.valueOf(props.getProperty("MaxMatchPercent")).doubleValue());
        params.setMaxConsecutiveUnmatch(Integer.valueOf(props.getProperty("MaxConsecutiveUnmatch")).intValue());
        params.setActivityMatchCost(Double.valueOf(props.getProperty("ActivityMatchCost")).doubleValue());
        params.setActivitySkipCost(Double.valueOf(props.getProperty("ActivitySkipCost")).doubleValue());
        params.setEventSkipCost(Double.valueOf(props.getProperty("EventSkipCost")).doubleValue());
        params.setNonActivityMoveCost(Double.valueOf(props.getProperty("NonActivityMoveCost")).doubleValue());
        params.setTraceChunkSize(Integer.valueOf(props.getProperty("TraceChunkSize")).intValue());
        params.setMaxNumberOfNodesVisited(Integer.valueOf(props.getProperty("MaxNumberOfNodesVisited")).intValue());
        params.setMaxActivitySkipPercent(Double.valueOf(props.getProperty("MaxActivitySkipPercent")).doubleValue());
        params.setMaxNodeDistance(Integer.valueOf(props.getProperty("MaxNodeDistance")).intValue());
        params.setTimelineSlots(Integer.valueOf(props.getProperty("TimelineSlots")).intValue());
        params.setTotalEngineSeconds(Integer.valueOf(props.getProperty("TotalEngineSeconds")).intValue());
        params.setProgressCircleBarRadius(Integer.valueOf(props.getProperty("ProgressCircleBarRadius")).intValue());
        params.setSequenceTokenDiffThreshold(Integer.valueOf(props.getProperty("SequenceTokenDiffThreshold")).intValue());
        params.setMaxTimePerTrace(Long.valueOf(props.getProperty("MaxTimePerTrace")).longValue());
        params.setMaxTimeShortestPathExploration(Long.valueOf(props.getProperty("MaxTimeShortestPathExploration")).longValue());
        params.setExactTraceFitnessCalculation(props.getProperty("ExactTraceFitnessCalculation"));
        params.setBacktrackingDebug(props.getProperty("BacktrackingDebug"));
        params.setExploreShortestPathDebug(props.getProperty("ExploreShortestPathDebug"));
        params.setCheckViciousCycle(props.getProperty("CheckViciousCycle"));
        params.setStartEventToFirstEventDuration(Integer.valueOf(props.getProperty("StartEventToFirstEventDuration")).intValue());
        params.setLastEventToEndEventDuration(Integer.valueOf(props.getProperty("LastEventToEndEventDuration")).intValue());

        Replayer replayer = new Replayer(bpmnDefinition, params);
        ArrayList<AnimationLog> replayedLogs = new ArrayList();
        if (replayer.isValidProcess()) {
//            LOGGER.info("Process " + bpmnDefinition.getId() + " is valid");
//            EncodeTraces.getEncodeTraces().read(xlogs); //build a mapping from traceId to charstream
            for (Log log: logs) {

                AnimationLog animationLog = replayer.replay(log.xlog, log.color);
                animationLog.setFileName(log.fileName);
                
                //AnimationLog animationLog = replayer.replayWithMultiThreading(log.xlog, log.color);
                if (animationLog !=null && !animationLog.isEmpty()) {
                    replayedLogs.add(animationLog);
                }
            }

        } else {
//            LOGGER.info(replayer.getProcessCheckingMsg());
        }

        /*
        * ------------------------------------------
        * Return Json animation
        * ------------------------------------------
        */
//        LOGGER.info("Start sending back JSON animation script to browser");
        if (replayedLogs.size() > 0) {

            //To be replaced
            AnimationJSONBuilder jsonBuilder = new AnimationJSONBuilder(replayedLogs, params);
            JSONObject json = jsonBuilder.parseLogCollection();
            json.put("success", true);  // Ext2JS's file upload requires this flag
            String string = json.toString();
            //LOGGER.info(string);
            jsonBuilder.clear();

            return string;
        }
        else {
            return "{success:false, errors: {errormsg: '" + "No logs can be played." + "'}}";
        }
    }
    
    // Build id mapping from diagram with gateways to the corresponding diagram with no gateways
    // Mapping rules:
    //  - Events mapped to Events
    //  - Activities mapped to Activities
    //  - Arc activity->activity mapped to the same arc activity->activity
    //  - Arc event->activity mapped to the same arc event->activity
    //  - Arc activity->event mapped to the same arc activity->event
    //  - A-->XORSplit-->B: the arc XORSplit-->B is mapped to the arc A-->B
    //  - A-->XORJoin-->B: the arc A-->XORJoin is mapped to the arc A-->B
    private Map<String,String> buildDiagramMapping(Definitions diagramWithGateways, Definitions diagramNoGateways) {
        Map<String,String> idMapping = new HashMap<String, String>();
        Process processWithGateways = (Process)diagramWithGateways.getRootElement().get(0);
        Process processNoGateways = (Process)diagramNoGateways.getRootElement().get(0);
        for (FlowElement ele: processWithGateways.getFlowElement()) {
            if (ele instanceof FlowNode) {
                FlowNode node = (FlowNode)ele;
                // Direct mapping for activities and events
                if (node instanceof Task || node instanceof Event) {
                    FlowNode corresponding = searchCorrespondingNode(processNoGateways, node);
                    if (corresponding != null) {
                        idMapping.put(node.getId(), corresponding.getId());
                    }
                }
            }
            else if (ele instanceof SequenceFlow) {
                SequenceFlow flow = (SequenceFlow)ele;
                SequenceFlow corresponding = searchCorrespondingFlow(processNoGateways, flow);
                if (corresponding != null) {
                    idMapping.put(flow.getId(), corresponding.getId());
                }
            }
        }
        return idMapping;
    }
    
    private FlowNode searchCorrespondingNode(Process process, FlowNode node) {
        for (FlowElement ele : process.getFlowElement()) {
            if (ele instanceof FlowNode && ele.getName().equalsIgnoreCase(node.getName())) {
                return (FlowNode)ele;
            }
        }
        return null;
    }
    
    private SequenceFlow searchCorrespondingFlow(Process process, FlowNode source, FlowNode target) {
        for (FlowElement ele : process.getFlowElement()) {
            if (ele instanceof SequenceFlow) {
                SequenceFlow flow = (SequenceFlow)ele;
                if (flow.getSourceRef().getName().equalsIgnoreCase(source.getName()) &&
                    flow.getTargetRef().getName().equalsIgnoreCase(target.getName())) {
                    return flow;
                }
            }
        }
        return null; 
    }
    
    private SequenceFlow searchCorrespondingFlow(Process process, SequenceFlow flow) {
        FlowNode source = (FlowNode)flow.getSourceRef();
        FlowNode target = (FlowNode)flow.getTargetRef();
        
        FlowNode searchSource = null, searchTarget = null;
        if ((source instanceof Task || source instanceof Event) && 
                (target instanceof Task || target instanceof Event)) {
            searchSource = source;
            searchTarget = target;
        }
        else if (BPMNDiagramHelper.isDecision(source) && (target instanceof Task || target instanceof Event)) { // no nested gateways
            searchSource = (FlowNode)source.getIncomingSequenceFlows().get(0).getSourceRef(); 
            searchTarget = target;
        }
        else if ((source instanceof Task || source instanceof Event) && BPMNDiagramHelper.isMerge(target)) { // no nested gateways
            searchSource = source;
            searchTarget = (FlowNode)target.getOutgoingSequenceFlows().get(0).getTargetRef();
        }
        else {
            //assume not happen
        }
        
        if (searchSource != null && searchTarget != null) {
            return searchCorrespondingFlow(process, searchSource, searchTarget);
        }
        else {
            return null;
        }
    }
    
    // The input animation log will be modified after the call to this method
    private void transformToNonGateways(AnimationLog diagramAnimationLog, Map<String,String> diagramMapping) {
        for (ReplayTrace trace : diagramAnimationLog.getTraces()) {
            trace.convertToNonGateways();
            for (FlowNode node : trace.getNodes()) {
                node.setId(diagramMapping.get(node.getId()));
            }
            for (SequenceFlow flow : trace.getSequenceFlows()) {
                flow.setId(diagramMapping.get(flow.getId()));
            }
        }
    }
    
    /**
     * This method adds a step to convert the replay result to JSON for the diagram with no gateways (i.e. graphs).
     * The input BPMN diagram with gateways are used to replay the logs. 
     * The input BPMN digram with no gateways is the corresponding graph which must have JSON text to be visualized.
     * Approach:
     *      1. A mapping is built to map IDs of nodes/arcs on bpmnWithGateways to those on bpmnNoGateways
     *      2. The log is replayed on bpmnWithGateways to have animationLog
     *      3. All traces in the animationLog are converted to non-gateway traces with sequence flows adjusted
     *      4. Apply the ID mapping to set IDs for all nodes/arcs in the animationLog to the IDs of nodes/arcs on bpmnNoGateways
     *      5. The new animationLog is used to generate JSON text for bpmnNoGateways
     * @param bpmnWithGateways: BPMN text with XOR gateways
     * @param bpmnNoGateways: corresponding BPMN text with no XOR gateways
     * @param logs: logs to be replayed
     */ 
    @Override
    public String createAnimationWithNoGateways(String bpmnWithGateways, String bpmnNoGateways, List<Log> logs) throws BpmnConverterException, IOException, JAXBException, JSONException {

        Set<XLog> xlogs = new HashSet<>();
        for (Log log: logs) {
            xlogs.add(log.xlog);
        }

        Definitions bpmnDefWithGateways = BPMN2DiagramConverter.parseBPMN(bpmnWithGateways, getClass().getClassLoader());
        Definitions bpmnDefNoGateways = BPMN2DiagramConverter.parseBPMN(bpmnNoGateways, getClass().getClassLoader());
        Map<String,String> diagramMapping = buildDiagramMapping(bpmnDefWithGateways, bpmnDefNoGateways);

        /*
        * ------------------------------------------
        * Optimize logs and process model
        * ------------------------------------------
        */
        Optimizer optimizer = new Optimizer();
        for (Log log : logs) {
            log.xlog = optimizer.optimizeLog(log.xlog);
        }
        bpmnDefWithGateways = optimizer.optimizeProcessModel(bpmnDefWithGateways);
        bpmnDefNoGateways = optimizer.optimizeProcessModel(bpmnDefNoGateways);


        /*
        * ------------------------------------------
        * Check BPMN diagram validity and replay log
        * ------------------------------------------
        */
        //Reading backtracking properties for testing
        String propertyFile = "properties.xml";
        InputStream is = getClass().getClassLoader().getResourceAsStream(propertyFile);
        Properties props = new Properties();
        props.loadFromXML(is);
        ReplayParams params = new ReplayParams();
        params.setMaxCost(Double.valueOf(props.getProperty("MaxCost")).doubleValue());
        params.setMaxDepth(Integer.valueOf(props.getProperty("MaxDepth")).intValue());
        params.setMinMatchPercent(Double.valueOf(props.getProperty("MinMatchPercent")).doubleValue());
        params.setMaxMatchPercent(Double.valueOf(props.getProperty("MaxMatchPercent")).doubleValue());
        params.setMaxConsecutiveUnmatch(Integer.valueOf(props.getProperty("MaxConsecutiveUnmatch")).intValue());
        params.setActivityMatchCost(Double.valueOf(props.getProperty("ActivityMatchCost")).doubleValue());
        params.setActivitySkipCost(Double.valueOf(props.getProperty("ActivitySkipCost")).doubleValue());
        params.setEventSkipCost(Double.valueOf(props.getProperty("EventSkipCost")).doubleValue());
        params.setNonActivityMoveCost(Double.valueOf(props.getProperty("NonActivityMoveCost")).doubleValue());
        params.setTraceChunkSize(Integer.valueOf(props.getProperty("TraceChunkSize")).intValue());
        params.setMaxNumberOfNodesVisited(Integer.valueOf(props.getProperty("MaxNumberOfNodesVisited")).intValue());
        params.setMaxActivitySkipPercent(Double.valueOf(props.getProperty("MaxActivitySkipPercent")).doubleValue());
        params.setMaxNodeDistance(Integer.valueOf(props.getProperty("MaxNodeDistance")).intValue());
        params.setTimelineSlots(Integer.valueOf(props.getProperty("TimelineSlots")).intValue());
        params.setTotalEngineSeconds(Integer.valueOf(props.getProperty("TotalEngineSeconds")).intValue());
        params.setProgressCircleBarRadius(Integer.valueOf(props.getProperty("ProgressCircleBarRadius")).intValue());
        params.setSequenceTokenDiffThreshold(Integer.valueOf(props.getProperty("SequenceTokenDiffThreshold")).intValue());
        params.setMaxTimePerTrace(Long.valueOf(props.getProperty("MaxTimePerTrace")).longValue());
        params.setMaxTimeShortestPathExploration(Long.valueOf(props.getProperty("MaxTimeShortestPathExploration")).longValue());
        params.setExactTraceFitnessCalculation(props.getProperty("ExactTraceFitnessCalculation"));
        params.setBacktrackingDebug(props.getProperty("BacktrackingDebug"));
        params.setExploreShortestPathDebug(props.getProperty("ExploreShortestPathDebug"));
        params.setCheckViciousCycle(props.getProperty("CheckViciousCycle"));
        params.setStartEventToFirstEventDuration(Integer.valueOf(props.getProperty("StartEventToFirstEventDuration")).intValue());
        params.setLastEventToEndEventDuration(Integer.valueOf(props.getProperty("LastEventToEndEventDuration")).intValue());

        Replayer replayer = new Replayer(bpmnDefWithGateways, params);
        ArrayList<AnimationLog> replayedLogs = new ArrayList();
        if (replayer.isValidProcess()) {
//            LOGGER.info("Process " + bpmnDefinition.getId() + " is valid");
//            EncodeTraces.getEncodeTraces().read(xlogs); //build a mapping from traceId to charstream
            for (Log log: logs) {

                AnimationLog animationLog = replayer.replay(log.xlog, log.color);
                animationLog.setFileName(log.fileName);
                
                //AnimationLog animationLog = replayer.replayWithMultiThreading(log.xlog, log.color);
                if (animationLog !=null && !animationLog.isEmpty()) {
                    replayedLogs.add(animationLog);
                }
            }

        } else {
//            LOGGER.info(replayer.getProcessCheckingMsg());
        }
        
        /*
         * ------------------------------------------
         * Convert the animation log to the one for graph, i.e. without gateways
         * This new animation log is then used to generate the JSON text.
         * ------------------------------------------
         */
        for (AnimationLog animationLog : replayedLogs) {
            transformToNonGateways(animationLog, diagramMapping);
        }
        LOGGER.info("Finish replaying log over model");

        /*
        * ------------------------------------------
        * Return Json animation
        * ------------------------------------------
        */
//        LOGGER.info("Start sending back JSON animation script to browser");
        if (replayedLogs.size() > 0) {

            //To be replaced
            AnimationJSONBuilder jsonBuilder = new AnimationJSONBuilder(replayedLogs, params);
            JSONObject json = jsonBuilder.parseLogCollection();
            json.put("success", true);  // Ext2JS's file upload requires this flag
            String string = json.toString();
            //LOGGER.info(string);
            jsonBuilder.clear();
            LOGGER.info("Finish generating JSON and start sending to the browser");
            return string;
        }
        else {
            return "{success:false, errors: {errormsg: '" + "No logs can be played." + "'}}";
        }
    }

    private Definitions getBPMNfromJson(String jsonData) throws BpmnConverterException, JSONException {
        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonData);
        Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions definitions = converter.getDefinitionsFromDiagram();

        return definitions;
    }
}
