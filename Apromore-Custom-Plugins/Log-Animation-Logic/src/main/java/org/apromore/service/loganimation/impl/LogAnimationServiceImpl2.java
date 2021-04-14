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
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.loganimation.AnimationResult;
import org.apromore.service.loganimation.LogAnimationService2;
import org.apromore.service.loganimation.json.AnimationJSONBuilder2;
import org.apromore.service.loganimation.replay.AnimationLog;
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
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

@Service
public class LogAnimationServiceImpl2 extends DefaultParameterAwarePlugin implements LogAnimationService2 {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnimationServiceImpl2.class);

    @Override
    public AnimationResult createAnimation(String bpmn, List<Log> logs)
            throws BpmnConverterException, IOException, JAXBException, JSONException, AnimationException {

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
        params.setTotalEngineSeconds(600); //Override this setting for testing
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
        ArrayList<AnimationLog> replayedLogs = new ArrayList<>();
        if (replayer.isValidProcess()) {
            for (Log log: logs) {

                AnimationLog animationLog = replayer.replay(log.xlog, log.color);
                animationLog.setFileName(log.fileName);
                
                if (animationLog !=null && !animationLog.isEmpty()) {
                    replayedLogs.add(animationLog);
                }
            }

        } else {
            throw new AnimationException("The BPMN diagram is not valid for animation. " +
                                         "Reason: " + replayer.getProcessCheckingMsg());
        }
        
        for (AnimationLog animationLog : replayedLogs) {
            animationLog.setDiagram(bpmnDefinition);
        }
        
        /*
        * ------------------------------------------
        * Return Json animation
        * ------------------------------------------
        */
        if (replayedLogs.size() > 0) {
            AnimationJSONBuilder2 jsonBuilder = new AnimationJSONBuilder2(replayedLogs, bpmnDefinition, params);
            JSONObject json = jsonBuilder.parseLogCollection();
            json.put("success", true);  // Ext2JS's file upload requires this flag
            return new AnimationResult(replayedLogs, bpmnDefinition, json);
        }
        else {
            throw new AnimationException("Internal error. No log is replayed successfully.");
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
     * @throws DiagramMappingException
     * @throws AnimationException
     */
    @Override
    public AnimationResult createAnimationWithNoGateways(String bpmnWithGateways, String bpmnNoGateways, List<Log> logs)
            throws BpmnConverterException, IOException, JAXBException, JSONException, DiagramMappingException, AnimationException {
        
        Set<XLog> xlogs = new HashSet<>();
        for (Log log: logs) {
            xlogs.add(log.xlog);
        }

        Definitions bpmnDefWithGateways = BPMN2DiagramConverter.parseBPMN(bpmnWithGateways, getClass().getClassLoader());
        Definitions bpmnDefNoGateways = BPMN2DiagramConverter.parseBPMN(bpmnNoGateways, getClass().getClassLoader());
        ElementIDMapper diagramMapping = new ElementIDMapper(bpmnDefNoGateways);

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
        params.setTotalEngineSeconds(600); //Override this setting for testing
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
        List<AnimationLog> replayedLogs = new ArrayList<>();
        if (replayer.isValidProcess()) {
            for (Log log: logs) {
                AnimationLog animationLog = replayer.replay(log.xlog, log.color);
                animationLog.setFileName(log.fileName);
                
                if (animationLog !=null && !animationLog.isEmpty()) {
                    replayedLogs.add(animationLog);
                }
            }

        } else {
            throw new AnimationException("The BPMN diagram is not valid for animation. " +
                                         "Reason: " + replayer.getProcessCheckingMsg());
        }
        
        /*
         * ------------------------------------------
         * Convert the animation log to the one for graph, i.e. without gateways
         * This new animation log is then used to generate the JSON text.
         * ------------------------------------------
         */
        for (AnimationLog animationLog : replayedLogs) {
            transformToNonGateways(animationLog, diagramMapping);
            animationLog.setDiagram(bpmnDefNoGateways);
        }
        LOGGER.info("Finish replaying log over model");
        
        /*
        * ------------------------------------------
        * Return Json animation
        * ------------------------------------------
        */
        if (replayedLogs.size() > 0) {
            //To be replaced
            AnimationJSONBuilder2 jsonBuilder = new AnimationJSONBuilder2(replayedLogs, bpmnDefNoGateways, params);
            JSONObject json = jsonBuilder.parseLogCollection();
            json.put("success", true);  // Ext2JS's file upload requires this flag

            //return string;
            LOGGER.info("Finish generating JSON and start sending to the browser");
            return new AnimationResult(replayedLogs, bpmnDefNoGateways, json);
        }
        else {
            throw new AnimationException("Internal error. No log is replayed successfully.");
        }
    }
    
    // The input animation log will be modified after the call to this method
    private void transformToNonGateways(AnimationLog diagramAnimationLog, ElementIDMapper diagramMapping) throws DiagramMappingException {
        for (ReplayTrace trace : diagramAnimationLog.getTraces()) {
            trace.convertToNonGateways();
            for (FlowNode node : trace.getNodes()) {
                String newId = diagramMapping.getId(node);
                if (newId.equals(ElementIDMapper.UNFOUND)) {
                    throw new DiagramMappingException("Couldn't find id for the node with name = " + node.getName());
                }
                node.setId(newId);
            }
            for (SequenceFlow flow : trace.getSequenceFlows()) {
                String newId = diagramMapping.getId(flow);
                if (newId.equals(ElementIDMapper.UNFOUND)) {
                    throw new DiagramMappingException("Couldn't find id for the sequence flow with name = " + flow.getName());
                }
                flow.setId(newId);
            }
        }
    }

    private Definitions getBPMNfromJson(String jsonData) throws BpmnConverterException, JSONException {
        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonData);
        Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions definitions = converter.getDefinitionsFromDiagram();

        return definitions;
    }

}
