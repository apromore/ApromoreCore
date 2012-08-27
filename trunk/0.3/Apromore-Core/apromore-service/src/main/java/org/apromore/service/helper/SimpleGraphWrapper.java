/**
 *
 */
package org.apromore.service.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.graph.SimpleGraph;
import nl.tue.tm.is.graph.TwoVertices;
import org.apromore.common.Constants;
import org.apromore.graph.JBPT.CPF;
import org.jbpt.pm.FlowNode;

/**
 * A Wrapper for to build a SimpleGraph from a Full Canonical Graph.
 *
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class SimpleGraphWrapper extends SimpleGraph {

    /**
     * Constructor for the Simple Graph Wrapper.
     * @param pg the canonical Graph.
     */
    public SimpleGraphWrapper(CPF pg) {
        super();

        Map<String, Integer> nodeId2vertex = new HashMap<String, Integer>(0);
        Map<Integer, String> vertex2nodeId = new HashMap<Integer, String>(0);

        vertices = new HashSet<Integer>(0);
        connectors = new HashSet<Integer>(0);
        events = new HashSet<Integer>(0);
        functions = new HashSet<Integer>(0);

        outgoingEdges = new HashMap<Integer, Set<Integer>>(0);
        incomingEdges = new HashMap<Integer, Set<Integer>>(0);
        labels = new HashMap<Integer, String>(0);
        functionLabels = new HashSet<String>(0);
        eventLabels = new HashSet<String>(0);
        edges = new HashSet<TwoVertices>(0);

        int vertexId = 0;
        for (FlowNode n : pg.getVertices()) {
            vertices.add(vertexId);
            labels.put(vertexId, n.getName().replace('\n', ' ').replace("\\n", " "));

            nodeId2vertex.put(n.getId(), vertexId);
            vertex2nodeId.put(vertexId, n.getId());

            if (Constants.FUNCTION.equals(pg.getVertexProperty(n.getId(), Constants.TYPE)) && n.getName() != null) {
                functionLabels.add(n.getName().replace('\n', ' '));
                functions.add(vertexId);
            } else if (Constants.EVENT.equals(pg.getVertexProperty(n.getId(), Constants.TYPE)) && n.getName() != null) {
                eventLabels.add(n.getName().replace('\n', ' '));
                events.add(vertexId);
            } else if (Constants.CONNECTOR.equals(pg.getVertexProperty(n.getId(), Constants.TYPE))) {
                connectors.add(vertexId);
            }
            vertexId++;
        }

        for (Integer v = 0; v < vertexId; v++) {
            FlowNode pgv = pg.getVertex(vertex2nodeId.get(v));

            Set<Integer> incomingCurrent = new HashSet<Integer>(0);
            for (FlowNode preV : pg.getAllPredecessors(pgv)) {
                incomingCurrent.add(nodeId2vertex.get(preV.getId()));
            }
            incomingEdges.put(v, incomingCurrent);

            Set<Integer> outgoingCurrent = new HashSet<Integer>(0);
            for (FlowNode postV : pg.getAllSuccessors(pgv)) {
                outgoingCurrent.add(nodeId2vertex.get(postV.getId()));
                TwoVertices edge = new TwoVertices(v, nodeId2vertex.get(postV.getId()));
                edges.add(edge);
            }
            outgoingEdges.put(v, outgoingCurrent);
        }
    }
}
