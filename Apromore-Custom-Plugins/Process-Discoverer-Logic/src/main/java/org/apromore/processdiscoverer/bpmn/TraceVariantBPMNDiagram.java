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

import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.factory.primitive.ObjectLongMaps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * A representation of a BPMN diagram which shows the average durations between traces of a trace variant.
 */
public class TraceVariantBPMNDiagram extends SimpleBPMNDiagram {
    private MutableObjectLongMap<BPMNNode> nodeDurationMap = ObjectLongMaps.mutable.empty();
    private MutableObjectLongMap<BPMNEdge<BPMNNode, BPMNNode>> arcDurationMap = ObjectLongMaps.mutable.empty();
    private BPMNNode startNode;
    private BPMNNode endNode;

    /**
     * Constructor for creating a trace variant BPMN diagram.
     * @param attTraces a list of traces of the same variant.
     * @param log a view of ALog based on an attribute.
     */
    public TraceVariantBPMNDiagram(List<AttributeTrace> attTraces, AttributeLog log) throws IllegalArgumentException {
        super(log);
        //All activity labels should be the same for each trace
        IntList valueTrace = attTraces.get(0).getValueTrace();
        if (!attTraces.stream().allMatch(t -> t.getValueTrace().equals(valueTrace))) {
            throw new IllegalArgumentException("All traces must be of the same variant");
        }

        int numTraces = attTraces.get(0).getValueTrace().size();

        //Get average durationTrace
        double[] avgDurationTraces = IntStream.range(0, numTraces).boxed()
                .mapToDouble(i -> IntStream.range(0, attTraces.size())
                        .mapToDouble(attIndex -> log.getCalendarModel().getDurationMillis(attTraces.get(attIndex).getStartTimeAtIndex(i),
                                        attTraces.get(attIndex).getEndTimeAtIndex(i)))
                        .sum() / Double.valueOf(attTraces.size()))
                .toArray();

        Map<Integer, BPMNNode> createdNodes = new HashMap<>();
        for(int i=1; i<valueTrace.size(); i++) {
            int node1Index = i-1;
            int node1Value = valueTrace.get(node1Index);
            int node2Index = i;
            int node2Value = valueTrace.get(node2Index);
            BPMNNode node1 = null;
            BPMNNode node2 = null;
            if (!createdNodes.containsKey(node1Index)) {
                node1 = this.addNode(node1Value);
                nodeDurationMap.put(node1, (long) avgDurationTraces[node1Index]);
                createdNodes.put(node1Index, node1);
            }
            else {
                node1 = createdNodes.get(node1Index);
            }

            if (!createdNodes.containsKey(node2Index)) {
                node2 = this.addNode(node2Value);
                nodeDurationMap.put(node2, (long) avgDurationTraces[node2Index]);
                createdNodes.put(node2Index, node2);
            }
            else {
                node2 = createdNodes.get(node2Index);
            }

            if (i==1) startNode = node1;
            if (i==(valueTrace.size()-1)) endNode = node2;

            BPMNEdge<BPMNNode, BPMNNode> edge = this.addFlow(node1, node2, "");
            arcDurationMap.put(edge, getAverageDurationAtPairIndexes(attTraces, node1Index, node2Index));
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

    private long getAverageDurationAtPairIndexes(List<AttributeTrace> attTraces, int node1Index, int node2Index) {
        return (long) attTraces.stream().mapToLong(t -> log.getCalendarModel().getDurationMillis(
                t.getEndTimeAtIndex(node1Index), t.getStartTimeAtIndex(node2Index)))
                .average().orElse(0);
    }
}
