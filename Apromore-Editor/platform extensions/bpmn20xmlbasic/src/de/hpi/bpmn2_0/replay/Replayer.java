/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.backtracking2.Backtracking;
import de.hpi.bpmn2_0.backtracking2.Node;
import de.hpi.bpmn2_0.backtracking2.State;
import de.hpi.bpmn2_0.backtracking2.StateElementStatus;
import de.vogella.algorithms.dijkstra.model.Vertex;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.processmining.plugins.signaturediscovery.encoding.EncodeTraces;
import org.processmining.plugins.signaturediscovery.encoding.EncodingNotFoundException;
import servlet.BPMNAnimationServlet;

/**
 *
 * @author Administrator
 */
public class Replayer {
    private Definitions bpmnDefinition = null;
    private ReplayParams params;
    private float fitness;
    private double logStartTime;
    private double logEndTime;
    private BPMNDiagramHelper helper;
    private boolean isValidProcess = true;
    private String processCheckMessage = "";
    private String[] colors = {"blue","orange","red","green","margenta"};
    private int curColorIndex = -1; //index of the current color used
    
    //Contains number of token increased/decreased carried on every edge
    //Used for OR-join enabledness check
    private Map<SequenceFlow,Integer> edgeToTokenChangeMap = new HashMap();     
    
    //EncodeTraces encodedTraces; // mapping from traceId to trace encoded char string
    Map<String, Node> traceBacktrackingNodeMap = new HashMap(); // mapping from trace string to leaf node of backtracking
    
    private static final Logger LOGGER = Logger.getLogger(Replayer.class.getCanonicalName());
    
    public Replayer(Definitions bpmnDefinition, ReplayParams params) {
        this.bpmnDefinition = bpmnDefinition;
        this.params = params;
        this.isValidProcess = true;
        try {
            helper = new BPMNDiagramHelper();
            helper.checkModel(bpmnDefinition);
            ORJoinEnactmentManager.init(helper);
        } catch (Exception ex) {
            isValidProcess = false;
            processCheckMessage = ex.getMessage();
            Logger.getLogger(Replayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public ReplayParams getReplayParams() {
        return this.params;
    }
    
    public boolean isValidProcess() {
        return isValidProcess;
    }
    
    public String getProcessCheckingMsg() {
        return processCheckMessage;
    }
    
    public AnimationLog replay(XLog log, String color) {
        AnimationLog animationLog = new AnimationLog();
        animationLog.setColor(color /*this.getLogColor()*/);
        ReplayTrace replayTrace;
        
        //-------------------------------------------
        // Set the initial and very far log start/end date as marker only
        //-------------------------------------------
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 1, 1);
        DateTime logStartDate = new DateTime(cal.getTime());
        cal.set(1920, 1, 1);
        DateTime logEndDate = new DateTime(cal.getTime());
        
        //-------------------------------------------
        // Replay every trace in the log
        //-------------------------------------------
        long startTime = DateTimeUtils.currentTimeMillis();
        for (XTrace trace : log) {
            replayTrace = this.replay(trace);
            if (!replayTrace.isEmpty()) {
                LOGGER.info("Trace " + LogUtility.getConceptName(trace) + ": " + replayTrace.getBacktrackingNode().getPathString());
                replayTrace.calcTiming();
                if (logStartDate.isAfter(replayTrace.getStartDate())) {
                    logStartDate = replayTrace.getStartDate();
                }
                if (logEndDate.isBefore(replayTrace.getEndDate())) {
                    logEndDate = replayTrace.getEndDate();
                }
                animationLog.add(trace, replayTrace);
            }
            else {
                LOGGER.info("Trace " + LogUtility.getConceptName(trace) + ": No path found!");
            }                    
        }

        if (!animationLog.isEmpty()) {
            animationLog.setCalculationTime(DateTimeUtils.currentTimeMillis() - startTime);
            animationLog.setStartDate(new DateTime(logStartDate));
            animationLog.setEndDate(new DateTime(logEndDate));
            animationLog.setName(log.getAttributes().get("concept:name").toString());

            LOGGER.info("LOG " + animationLog.getName() + ". TraceCount:" + animationLog.getTraces().size() + 
                        ". StartDate:" + animationLog.getStartDate().toString() +
                        ". EndDate:" + animationLog.getEndDate() +
                        ". Color:" + animationLog.getColor());                
        } else {
            LOGGER.info("LOG " + animationLog.getName() + ": no traces can be replayed");
        }

        return animationLog;
    }
    
    /*
    * Preprocess the trace, including
    * - remove all events with name not found in the process model
    * - add start and end event since trace only contains activities
    * - calculate timing for start and end event from the first and last trace activity
    */
    /*
    private XTrace preProcess(XTrace trace) {
        XTrace processTrace = new XTraceImpl(trace.getAttributes());
        
        //Remove unfound event name in process model
        for (XEvent event : trace) {
            if (helper.getActivityNames().contains(event.getAttributes().get("concept:name").toString())) {
                processTrace.insertOrdered(event);
            }
        }
        
        //Create start event
        //Timing 4 seconds prior to the first activity of trace
        //Name is taken from the start event of process mdoel
        XEvent event = (XEvent)trace.get(0).clone();        
        DateTime timestamp = new DateTime(LogUtility.getTimestamp(processTrace.get(0))); 
        LogUtility.setConceptName(event, helper.getStartEvent().getName());
        LogUtility.setTimestamp(event, timestamp.minusSeconds(4).toDate());
        processTrace.insertOrdered(event);
        
        //Create end event
        //Timing 60 seconds after to the last activity of trace
        //Name is taken from the end event of process mdoel
        event = (XEvent)trace.get(0).clone();        
        timestamp = new DateTime(LogUtility.getTimestamp(processTrace.get(processTrace.size()-1))); 
        LogUtility.setConceptName(event, helper.getEndEvent().getName());
        LogUtility.setTimestamp(event, timestamp.plusSeconds(60).toDate());        
        processTrace.insertOrdered(event);
        
        return processTrace;
    }
    */
    
    

    
    /*
    * Replay using backtracking algorithm
    * Return replay trace or empty if backtracking returns no result
    * Noted side effect: the input trace can be preprocessed (activities not in the model are removed)
    * Assume: encodedTraces has been created for the log.
    */
    public ReplayTrace replay(XTrace trace) {
        Node leafNode; //contains the found backtracking leaf node
        
        //---------------------------------------------------
        // Use clustering. If the same trace has been replayed
        // before, then no need another replay but just reuse the 
        // replay result done for previous trace.
        //---------------------------------------------------
        String traceString = EncodeTraces.getEncodeTraces().getCharStream(LogUtility.getConceptName(trace));
        if (traceBacktrackingNodeMap.containsKey(traceString)) {
            leafNode = traceBacktrackingNodeMap.get(traceString);
        }
        else { // has to replay a new trace
            //--------------------------------------------
            //Remove unfound event name in process model
            //--------------------------------------------
            for (XEvent event : trace) {
                if (!helper.getActivityNames().contains(LogUtility.getConceptName(event))) {
                    trace.remove(event);
                }
            }

            //--------------------------------------------
            // Replay trace with backtracking algorithm
            //--------------------------------------------
            Backtracking backtrack = new Backtracking(this.params, trace, helper);
            leafNode = backtrack.explore();    
            if (leafNode != null && !traceBacktrackingNodeMap.containsKey(traceString)) {
                traceBacktrackingNodeMap.put(traceString, leafNode); //traceString: the original trace string.
            }
        }
        
        //---------------------------------
        // Reverse the path to start from the root node
        //---------------------------------
        Stack<Node> stack = new Stack<>();
        Node node = leafNode;
        while (node != null) {
            stack.push(node);
            node = node.getParent();
        }
        
        //---------------------------------------------
        // Create replay trace from the backtracking result
        // Use the model node and markings in every backtracking node's state 
        // to identify the connection between nodes. The state's model node is
        // the activity or gateway taken and the markings contains
        // all tokens (process state) after the element is taken.
        //---------------------------------------------
        ReplayTrace replayTrace = new ReplayTrace(new XTrace2(trace), this, leafNode);
        
        if (!stack.isEmpty()) {
            FlowNode modelNode=null;
            TraceNode traceNode;
            Set<SequenceFlow> previousMarkings=null;
            Set<SequenceFlow> moves;
            
            //---------------------------------------------
            // Traverse backtracking result from the beginning
            //---------------------------------------------
            while (!stack.empty()) {
                node = (Node)stack.pop();
                modelNode = node.getState().getElement();
                
                //Skip EVENT_SKIPPED nodes: no need to show them on the replay
                if (node.getState().getElementStatus() == StateElementStatus.EVENT_SKIPPED) {
                    continue;
                }
                
                //Set up starting node
                if (replayTrace.getStart() == null) {
                    traceNode = new TraceNode(modelNode);
                    if (node.getState().getElementStatus() == StateElementStatus.STARTEVENT) {
                        traceNode.setStart((new DateTime(LogUtility.getTimestamp(trace.get(0)))).minusSeconds(20));
                    }   
                    replayTrace.setStart(traceNode);
                    replayTrace.addToReplayedList(modelNode);
                    previousMarkings = node.getState().getMarkings();
                    continue;
                }
                
                //---------------------------------------
                //Add normal node to the replay trace
                //---------------------------------------
                traceNode = new TraceNode(modelNode);
                
                //Connect this node to its incoming nodes based on the replay result
                moves = (Set)((HashSet)previousMarkings).clone();
                moves.removeAll(node.getState().getMarkings()); //moves contains tokens consumed                
                for (SequenceFlow takenFlow : moves) {
                    replayTrace.add((FlowNode)takenFlow.getSourceRef(), traceNode); 
                }
                
                //Set timestamp
                if (traceNode.isActivity()) {
                    if (node.getState().getElementStatus() == StateElementStatus.ACTIVITY_MATCHED) {
                        traceNode.setActivityMatched(true);
                        traceNode.setStart(new DateTime(LogUtility.getTimestamp(trace.get(node.getState().getTraceIndex()-1))));
                    } else {
                        traceNode.setActivitySkipped(true);
                    }
                }           
                
                replayTrace.addToReplayedList(modelNode);                
                previousMarkings = node.getState().getMarkings();
            }
            
            //----------------------------------------------
            // Set timing for end event if it is connected to the last node in the replay trace
            // modelNode: point to the last node of the replay trace
            //----------------------------------------------
            if (helper.getTargets(modelNode).contains(helper.getEndEvent())) {
                //traceNode = replayTrace.getMarkingsMap().get(helper.getEndEvent());
                traceNode = new TraceNode(helper.getEndEvent());
                traceNode.setStart((new DateTime(LogUtility.getTimestamp(trace.get(trace.size()-1)))).plusSeconds(20));
                replayTrace.add(modelNode, traceNode);
                replayTrace.addToReplayedList(helper.getEndEvent());
            }
        }
        
        return replayTrace;
    }
       
    /*
    * When current node is fired
    *   - Each of its active incoming sequences flow will reduce one token
    *   - Each of its outgoing sequences will have one new token
    */
    /*
    private void updateTokenChangeForEdges(FlowNode current, FlowNode previous, Collection<FlowNode> targets) {
        int currentCount;
        
        //Incoming sequence flows
        if (helper.getAllMerges().contains(current)) {  //XOR join: remove one token on the active incoming flow
            for (SequenceFlow flow : current.getIncomingSequenceFlows()) {
                if (previous == (FlowNode)flow.getSourceRef()) {
                    currentCount = this.edgeToTokenChangeMap.get(flow);
                    currentCount--;
                    this.edgeToTokenChangeMap.put(flow, currentCount);
                }
            }
        } 
        else if (helper.getAllJoins().contains(current)) { //AND join: remove one token on every incoming flow
            for (SequenceFlow flow : current.getIncomingSequenceFlows()) {
                currentCount = this.edgeToTokenChangeMap.get(flow);
                currentCount--;
                this.edgeToTokenChangeMap.put(flow, currentCount);
            }
        }
        else if (helper.getAllORJoins().contains(current)) { //OR join: remove one token on every active incoming flow (has token)
            for (SequenceFlow flow : current.getIncomingSequenceFlows()) {
                currentCount = this.edgeToTokenChangeMap.get(flow);
                if (currentCount > 0) {
                    currentCount--;
                    this.edgeToTokenChangeMap.put(flow, currentCount);
                }
            }
        }
        else { // Activity node, XOR split, AND split, OR split: remove one token on the only incoming sequence flow
            for (SequenceFlow flow : current.getIncomingSequenceFlows()) {
                currentCount = this.edgeToTokenChangeMap.get(flow);
                currentCount--;
                this.edgeToTokenChangeMap.put(flow, currentCount);
            }
        }
        
        
        //Outgoing sequence flows
        //XOR split: add one token on the selected outgoing flow
        //AND or OR split: add one token on every outgoing flow
        //Others: add one token on the only outgoing flow
        for (SequenceFlow flow : current.getOutgoingSequenceFlows()) {
            if (targets.contains((FlowNode)flow.getTargetRef())) {
                currentCount = this.edgeToTokenChangeMap.get(flow);
                currentCount++;
                this.edgeToTokenChangeMap.put(flow, currentCount);
            }
        }            
        
        //Call to update all OR-join state
        ORJoinEnactmentManager.update(edgeToTokenChangeMap);
    }
    */
 
    public float getFitness() {
        return fitness;
    }   
    
    
    public double getLogStartTime() {
        return logStartTime;
    }
    
    public double getLogEndTime() {
        return logEndTime;
    }    
    
    public double getTaskAverageDuration(String taskId) {
        return 0;
    }    
    
    public double getEdgeAverageDuration(String edgeId) {
        return 0;
    }
    
    private String getLogColor() {
        curColorIndex++;
        if (curColorIndex < this.colors.length) {
            return this.colors[curColorIndex];
        } else {
            return "black"; //black is used to indicate out of color index
        }
    }
}
