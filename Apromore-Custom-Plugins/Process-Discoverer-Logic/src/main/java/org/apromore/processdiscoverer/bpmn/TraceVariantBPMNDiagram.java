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

public class TraceVariantBPMNDiagram extends SimpleBPMNDiagram {
    private MutableObjectLongMap<BPMNNode> nodeDurationMap = ObjectLongMaps.mutable.empty();
    private MutableObjectLongMap<BPMNEdge<BPMNNode, BPMNNode>> arcDurationMap = ObjectLongMaps.mutable.empty();
    private BPMNNode startNode;
    private BPMNNode endNode;

    public TraceVariantBPMNDiagram(List<AttributeTrace> attTraces, AttributeLog log) {
        super(log);
        //All activity labels should be the same for each trace
        IntList valueTrace = attTraces.get(0).getValueTrace();
        int numDurTraces = attTraces.get(0).getDurationTrace().size();

        //Get average durationTrace
        double[] avgDurationTraces = IntStream.range(0, numDurTraces).boxed()
                .mapToDouble(i -> attTraces.stream().mapToLong(t -> t.getDurationTrace().get(i)).sum() /
                        Double.valueOf(numDurTraces)).toArray();

        Map<Integer, BPMNNode> createdNodes = new HashMap<>();
        for(int i=1; i<valueTrace.size(); i++) {
            int node1Index = i-1;
            int node1Value = valueTrace.get(node1Index);
            int node2Index = i;
            int node2Value = valueTrace.get(node2Index);
            BPMNNode node1=null, node2=null;
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
        return (long) attTraces.stream().mapToLong(t -> t.getDurationAtPairIndexes(node1Index, node2Index))
                .average().orElse(0);
    }
}
