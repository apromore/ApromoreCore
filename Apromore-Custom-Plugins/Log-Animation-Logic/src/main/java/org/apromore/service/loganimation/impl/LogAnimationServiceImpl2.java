/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2017 Queensland University of Technology.
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

package org.apromore.service.loganimation.impl;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.loganimation.AnimationResult;
import org.apromore.service.loganimation.LogAnimationService2;
import org.apromore.service.loganimation.json.AnimationJSONBuilder2;
import org.apromore.service.loganimation.replay.*;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service("logAnimationService2")
@Qualifier("logAnimationService2")
public class LogAnimationServiceImpl2 extends DefaultParameterAwarePlugin implements LogAnimationService2 {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnimationServiceImpl2.class);

    @Override
    public AnimationResult createAnimation(String bpmn, List<Log> logs) throws Exception {

        /*
         * ------------------------------------------
         * Check process model validity for animation
         * ------------------------------------------
         */
        Definitions bpmnDefinition = BPMN2DiagramConverter.parseBPMN(bpmn, getClass().getClassLoader());
        BPMNDiagramHelper diagramHelper = new BPMNDiagramHelper();
        ModelCheckResult checkResult = diagramHelper.checkModel(bpmnDefinition);
        if (!checkResult.isValid()) {
            throw new AnimationException("The BPMN diagram is not valid for animation. " +
                    "Reason: " + checkResult.invalidMessage());
        }

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

        // Clean traces for animation and compute the transition duration for start/end events
        cleanLogs(logs);
        int artificialTransitionRatio = Integer.valueOf(props.getProperty("ArtificialTransitionDurationRatio")).intValue();
        int artificalTransitionDur = (int)computeArtificialTransitionDuration(logs, artificialTransitionRatio);
        params.setStartEventToFirstEventDuration(artificalTransitionDur);
        params.setLastEventToEndEventDuration(artificalTransitionDur);
        
        Replayer replayer = new Replayer(bpmnDefinition, params, diagramHelper);
        ArrayList<AnimationLog> replayedLogs = new ArrayList<>();
        for (Log log: logs) {
            AnimationLog animationLog = replayer.replay(log.xlog, log.color);
            animationLog.setFileName(log.fileName);

            if (animationLog !=null && !animationLog.isEmpty()) {
                replayedLogs.add(animationLog);
            }
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
            throw new AnimationException("Unable to animate as no alignment was found between the log and model.\n" +
                    "Possible cause: the log and model are too different.");
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
            throws Exception {

        Definitions bpmnDefWithGateways = BPMN2DiagramConverter.parseBPMN(bpmnWithGateways, getClass().getClassLoader());
        Definitions bpmnDefNoGateways = BPMN2DiagramConverter.parseBPMN(bpmnNoGateways, getClass().getClassLoader());
        ElementIDMapper diagramMapping = new ElementIDMapper(bpmnDefNoGateways);
        BPMNDiagramHelper diagramHelper = new BPMNDiagramHelper();
        diagramHelper.checkModel(bpmnDefWithGateways); // only scan, no need to check model validity as this is graph.

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
        
        cleanLogs(logs);
        int artificialTransitionRatio = Integer.valueOf(props.getProperty("ArtificialTransitionDurationRatio")).intValue();
        int artificalTransitionDur = (int)computeArtificialTransitionDuration(logs, artificialTransitionRatio);
        params.setStartEventToFirstEventDuration(artificalTransitionDur);
        params.setLastEventToEndEventDuration(artificalTransitionDur);

        Replayer replayer = new Replayer(bpmnDefWithGateways, params, diagramHelper);
        List<AnimationLog> replayedLogs = new ArrayList<>();
        for (Log log: logs) {
            AnimationLog animationLog = replayer.replay(log.xlog, log.color);
            animationLog.setFileName(log.fileName);

            if (animationLog !=null && !animationLog.isEmpty()) {
                replayedLogs.add(animationLog);
            }
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
            throw new AnimationException("Unable to animate as no alignment was found between the log and model.\n" +
                    "Possible cause: the log and model are too different.");
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
    
    private void cleanLogs(List<Log> logs) {
        for (Log log : logs) {
            for (XTrace trace : log.xlog) {
                cleanTrace(trace);
            }
        }
    }
    
    private void cleanTrace(XTrace trace) {
        if (trace == null || trace.isEmpty()) return;
        
        Date startTimestamp = LogUtility.getTimestamp(trace.get(0));
        Date endTimestamp = LogUtility.getTimestamp(trace.get(trace.size()-1));
        Iterator<XEvent> iterator = trace.iterator();
        while (iterator.hasNext()) {
            XEvent event = iterator.next();
            if (!LogUtility.getLifecycleTransition(event).toLowerCase().equals("complete")) {
                iterator.remove();
            }
        }
        
        // Adjust the timestamp of the first/last events to ensure the clean log has a matched start/end date with the original one.
        if (!trace.isEmpty()) {
            LogUtility.setTimestamp(trace.get(0), startTimestamp);
            if (trace.size() > 1) LogUtility.setTimestamp(trace.get(trace.size()-1), endTimestamp);
        }
    }

    /**
     * Compute an artificial transition duration which is used for the transition
     * from the Start Event to the next node and from a node to the End Event.
     * @param logs: logs used in animation
     * @param artificialTransDurRatio: the parameter for the artificial duration compared to the total timeline duration, e.g. 20 means 1/20
     * @return: artificial transition duration in seconds
     */
    private double computeArtificialTransitionDuration(List<Log> logs, int artificialTransDurRatio) {
        double UPPER_BOUND = 1.0/artificialTransDurRatio;
        double LOWER_BOUND =  1.0/(2*artificialTransDurRatio);
        
        // Scan the log the compute the average transition duration and log duration
        // Artificial transition duration is set according to the log
        double totalAvgTransitionDur = 0;
        long minLogTimestamp = Long.MAX_VALUE, maxLogTimestamp = 0;
        int traceCount = 0;
        for (Log log : logs) {
            for (XTrace trace : log.xlog) {
                minLogTimestamp = Math.min(minLogTimestamp, !trace.isEmpty() ? LogUtility.getTimestamp(trace.get(0)).getTime() : Long.MAX_VALUE);
                maxLogTimestamp = Math.max(maxLogTimestamp, !trace.isEmpty() ? LogUtility.getTimestamp(trace.get(trace.size()-1)).getTime() : 0);
                if (trace.size() >= 2) {
                    long traceDuration = LogUtility.getTimestamp(trace.get(trace.size()-1)).getTime() - LogUtility.getTimestamp(trace.get(0)).getTime();
                    totalAvgTransitionDur += traceDuration/(trace.size()-1);
                    traceCount++;
                }
            }
        }
        
        double avgTransitionDur = (traceCount != 0) ? totalAvgTransitionDur/traceCount : 0;
        long logDuration = (minLogTimestamp >= maxLogTimestamp) ? 0 : (maxLogTimestamp - minLogTimestamp);
        double timelineDur = logDuration + 2*avgTransitionDur;
        double avgTransitionRatio = (timelineDur > 0) ? avgTransitionDur/timelineDur : 0;
        
        // Adjust the artificial transition duration so that it's not too big or small.
        double artificialTransitionDur = avgTransitionDur/1000;
        if (avgTransitionRatio >= UPPER_BOUND) {
            artificialTransitionDur = timelineDur*UPPER_BOUND/1000;
        }
        else if (avgTransitionRatio <= LOWER_BOUND) {
            artificialTransitionDur = timelineDur*LOWER_BOUND/1000;
        }
        
        if (artificialTransitionDur == 0) artificialTransitionDur = 10;

        return artificialTransitionDur;
    }

}
