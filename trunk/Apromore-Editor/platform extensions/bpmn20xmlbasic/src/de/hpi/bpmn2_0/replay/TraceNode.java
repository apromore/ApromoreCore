package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.deckfour.xes.model.XEvent;
import org.joda.time.DateTime;

public class TraceNode extends FlowNode {
    private DateTime start = null;
    private DateTime end = null;
    private FlowNode modelNode;
    private boolean isMatched = false; //whether this node has been matched with a log trace (for activity node)
    private boolean isVirtual = false;
    
   
    public TraceNode(FlowNode modelNode) {
        this.modelNode = modelNode;
        this.setName(this.modelNode.getName());
        this.setId(this.modelNode.getId());
    }    
    
    public DateTime getStart() {
        return start;
    }
    
    public void setStart(DateTime start) {
        this.start = start;
    }    
    
    public DateTime getComplete() {
        return end;
    }
    
    public void setComplete(DateTime end) {
        this.end = end;
    } 
    
    public FlowNode getModelNode() {
        return this.modelNode;
    }
    
    public ArrayList<TraceNode> getTargets() {
        ArrayList<TraceNode> targets = new ArrayList<>();
        for (SequenceFlow sflow : this.getOutgoingSequenceFlows()) {
            targets.add((TraceNode)sflow.getTargetRef());
        }        
        return targets;
    }
    
    public ArrayList<TraceNode> getSources() {
        ArrayList<TraceNode> sources = new ArrayList<>();
        for (SequenceFlow sflow : this.getIncomingSequenceFlows()) {
            sources.add((TraceNode)sflow.getSourceRef());
        }        
        return sources;
    }    
    
    public boolean isFork() {
        return BPMNDiagramHelper.isFork(modelNode);
    }
    
    public boolean isJoin() {
        return BPMNDiagramHelper.isJoin(modelNode);
    }
    
    public boolean isDecision() {
        return BPMNDiagramHelper.isDecision(modelNode);
    }
    
    public boolean isMerge() {
        return BPMNDiagramHelper.isMerge(modelNode);
    }    
    
    public boolean isORSplit() {
        return BPMNDiagramHelper.isORSplit(modelNode);
    }
    
    public boolean isORJoin() {
        return BPMNDiagramHelper.isORJoin(modelNode);
    }
    
    public boolean isActivity() {
        return (modelNode instanceof Activity);
    }
    
    public boolean isEndEvent() {
        return (modelNode instanceof EndEvent);
    }
    
    public boolean isStartEvent() {
        return (modelNode instanceof StartEvent);
    }
    
    public boolean isMatched() {
        return this.isMatched;
    }
    
    public void setIsMatched(boolean isMatched) {
        this.isMatched = isMatched;
    }
    
    //Indicate that if this node has a defined or calculated timestamp or not
    //When it is timed, the start date must have a Date value
    public boolean isTimed() {
        return (this.start != null);
    }
    
    public boolean isVirtual() {
        return isVirtual;
    }
    
    public void setVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
    }
}