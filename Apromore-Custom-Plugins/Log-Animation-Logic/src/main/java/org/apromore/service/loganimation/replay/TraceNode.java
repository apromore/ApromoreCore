/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.service.loganimation.replay;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import java.util.ArrayList;
import org.joda.time.DateTime;

public class TraceNode extends FlowNode {
    private DateTime start = null;
    private DateTime end = null;
    private FlowNode modelNode;
    private boolean isActivityMatched = false; //whether this node has been matched with a log trace (for activity node)
    private boolean isActivitySkipped = false;
    
   
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
    
    public boolean isActivityMatched() {
        return this.isActivityMatched;
    }
    
    public void setActivityMatched(boolean isActivityMatched) {
        this.isActivityMatched = isActivityMatched;
    }
    
    //Indicate that if this node has a defined or calculated timestamp or not
    //When it is timed, the start date must have a Date value
    public boolean isTimed() {
        return (this.start != null);
    }
    
    public boolean isActivitySkipped() {
        return isActivitySkipped;
    }
    
    public void setActivitySkipped(boolean isActivitySkipped) {
        this.isActivitySkipped = isActivitySkipped;
    }
}
