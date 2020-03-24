/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.loganimation.impl;

import java.io.IOException;
// Java 2 Standard Edition
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.loganimation.LogAnimationService;
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

// Local classes
import de.hpi.bpmn2_0.animation.AnimationJSONBuilder;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.replay.AnimationLog;
import de.hpi.bpmn2_0.replay.Optimizer;
import de.hpi.bpmn2_0.replay.ReplayParams;
import de.hpi.bpmn2_0.replay.ReplayTrace;
import de.hpi.bpmn2_0.replay.Replayer;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

@Service
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
    
    private Map<FlowNode,FlowNode> buildDiagramToGraphMapping(Definitions diagram, Definitions graph) {
        return null;
    }
    
    private ReplayTrace createGraphReplayTrace(ReplayTrace replayTrace, Map<FlowNode,FlowNode> diagramToGraphMapping) {
        ReplayTrace newTrace = new ReplayTrace(replayTrace.getOriginalTrace(), 
                                                replayTrace.getReplayer(),
                                                replayTrace.getBacktrackingNode(), 
                                                replayTrace.getAlgoRuntime());
        for (FlowNode oldNode: replayTrace.getNodes()) {
            
        }
        return null;
    }
    
    private AnimationLog createGraphAnimationLog(AnimationLog diagramAnimationLog, Map<FlowNode,FlowNode> diagramToGraphMapping) {
        return null;
    }
    
    /**
     * This method adds a step to convert the replay result to JSON for the graph without gateways.
     * The BPMN diagram with gateways are used to replay the logs. The BPMN digram without gateways
     * is the corresponding graph version without the gateways which must have JSON text to be visualized.
     * @param bpmnWithGateways: BPMN text with XOR gateways
     * @param bpmnGraph: corresponding BPMN text without XOR gateways (graph only)
     * @param logs: logs to be replayed
     */
    public String createAnimation(String bpmnWithGateways, String bpmnGraph, List<Log> logs) throws BpmnConverterException, IOException, JAXBException, JSONException {

        Set<XLog> xlogs = new HashSet<>();
        for (Log log: logs) {
            xlogs.add(log.xlog);
        }

        Definitions bpmnDefWithGateways = BPMN2DiagramConverter.parseBPMN(bpmnWithGateways, getClass().getClassLoader());
        Definitions bpmnDefGraph = BPMN2DiagramConverter.parseBPMN(bpmnWithGateways, getClass().getClassLoader());
        Map<FlowNode,FlowNode> diagramToGraphMapping = buildDiagramToGraphMapping(bpmnDefWithGateways, bpmnDefGraph);

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
        bpmnDefGraph = optimizer.optimizeProcessModel(bpmnDefGraph);


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
            //AnimationLog newAnimationLog = new Anim
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

    private Definitions getBPMNfromJson(String jsonData) throws BpmnConverterException, JSONException {
        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonData);
        Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions definitions = converter.getDefinitionsFromDiagram();

        return definitions;
    }
}
