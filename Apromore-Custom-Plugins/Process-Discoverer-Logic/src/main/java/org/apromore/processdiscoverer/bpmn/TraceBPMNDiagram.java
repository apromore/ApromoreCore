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

package org.apromore.processdiscoverer.bpmn;

import org.apromore.calendar.model.CalendarModel;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.factory.primitive.ObjectLongMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * TraceBPMNDiagram is a <b>SimpleBPMNDiagram</b> that is used to visualize an <b>AttributeTrace</b>
 * It contains a Task for each middle event in the trace, start event for the first artificial event and
 * end event for the last artificial event. It has no gateways.
 * Note that Tasks with the same label can occur multiple times if the event is repeated in the trace.
 * 
 * @author Bruce Nguyen
 *
 */
public class TraceBPMNDiagram extends SimpleBPMNDiagram {
    private MutableObjectLongMap<BPMNNode> nodeDurationMap = ObjectLongMaps.mutable.empty();
    private MutableObjectLongMap<BPMNEdge<BPMNNode, BPMNNode>> arcDurationMap = ObjectLongMaps.mutable.empty();
    private BPMNNode startNode;
    private BPMNNode endNode;
    
    public TraceBPMNDiagram(AttributeTrace attTrace, AttributeLog log) {
        super(log);
        IntList valueTrace = attTrace.getValueTrace();
        CalendarModel cal = log.getCalendarModel();
        Map<Integer, BPMNNode> createdNodes = new HashMap<Integer, BPMNNode>();
        for(int i=1; i<valueTrace.size(); i++) {
            int node1Index = i-1;
            int node1Value = valueTrace.get(node1Index);
            int node2Index = i;
            int node2Value = valueTrace.get(node2Index);
            BPMNNode node1=null, node2=null;
            if (!createdNodes.containsKey(node1Index)) {
                node1 = this.addNode(node1Value);
                nodeDurationMap.put(node1, cal.getDurationMillis(attTrace.getStartTimeAtIndex(node1Index),
                        attTrace.getEndTimeAtIndex(node1Index)));
                createdNodes.put(node1Index, node1);
            }
            else {
                node1 = createdNodes.get(node1Index);
            }
            
            if (!createdNodes.containsKey(node2Index)) {
                node2 = this.addNode(node2Value);
                nodeDurationMap.put(node2, cal.getDurationMillis(attTrace.getStartTimeAtIndex(node2Index),
                        attTrace.getEndTimeAtIndex(node2Index)));
                createdNodes.put(node2Index, node2);
            }
            else {
                node2 = createdNodes.get(node2Index);
            }
            
            if (i==1) startNode = node1;
            if (i==(valueTrace.size()-1)) endNode = node2;

            BPMNEdge<BPMNNode, BPMNNode> edge = this.addFlow(node1, node2, "");
            arcDurationMap.put(edge, attTrace.getDurationAtPairIndexes(node1Index, node2Index));
            arcDurationMap.put(edge, cal.getDurationMillis(attTrace.getEndTimeAtIndex(node1Index),
                    attTrace.getStartTimeAtIndex(node2Index)));
        }
        
        BPMNDiagramHelper.updateStandardEventLabels(this);
    }
    
    public BPMNNode getStartNode() {
        return startNode;
    }
    
    public BPMNNode getEndNode() {
        return endNode;
    }
    
    public long getNodeWeight(BPMNNode node) {
        return nodeDurationMap.getIfAbsent(node, -1);
    }
    
    public long getArcWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
        return arcDurationMap.getIfAbsent(edge, -1);
    }
}
