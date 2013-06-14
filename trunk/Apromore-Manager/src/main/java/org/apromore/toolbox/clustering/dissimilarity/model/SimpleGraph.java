package org.apromore.toolbox.clustering.dissimilarity.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.epc.EPC;
import nl.tue.tm.is.ptnet.PTNet;

/**
 * Efficient implementation of a simple graph: (Vertices, Edges, labels)
 * Only for reading, cannot be modified
 */
public class SimpleGraph {
    public Set<Integer> vertices;
    public Set<TwoVertices> edges = null;
    public Set<Integer> connectors;
    public Set<Integer> events;
    public Set<Integer> functions;


    protected Map<Integer, Set<Integer>> outgoingEdges;
    protected Map<Integer, Set<Integer>> incomingEdges;
    protected Map<Integer, String> labels;
    protected Set<String> functionLabels;
    protected Set<String> eventLabels;

    public SimpleGraph() {
    }

    public SimpleGraph(SimpleGraph g) {

        vertices = new HashSet<Integer>();
        for (Integer v : g.getVertices()) {
            vertices.add(v.intValue());
        }

        edges = new HashSet<TwoVertices>();
        for (TwoVertices gtw : g.getEdges()) {
            TwoVertices tw = new TwoVertices(gtw.v1, gtw.v2);
            edges.add(tw);
        }

        connectors = new HashSet<Integer>();
        for (Integer c : g.getConnectors()) {
            connectors.add(c.intValue());
        }

        events = new HashSet<Integer>();
        for (Integer e : g.getEvents()) {
            events.add(e.intValue());
        }

        functions = new HashSet<Integer>();
        for (Integer f : g.getFunctions()) {
            functions.add(f.intValue());
        }

        outgoingEdges = new HashMap<Integer, Set<Integer>>();
        outgoingEdges.putAll(g.getOutgoingEdges());

        incomingEdges = new HashMap<Integer, Set<Integer>>();
        incomingEdges.putAll(g.getIncomingEdges());

        labels = new HashMap<Integer, String>();
        labels.putAll(g.getLabelsAsMap());

        functionLabels = new HashSet<String>();
        functionLabels.addAll(g.getFunctionLabels());

        eventLabels = new HashSet<String>();
        eventLabels.addAll(g.getEventLabels());
    }

    private SimpleGraph(Set<Integer> vertices, Map<Integer, Set<Integer>> outgoingEdges, Map<Integer, Set<Integer>> incomingEdges, Map<Integer, String> labels) {
        this.vertices = vertices;
        this.outgoingEdges = outgoingEdges;
        this.incomingEdges = incomingEdges;
        this.labels = labels;
    }

    /**
     * Initializes a simple graph from an EPC.
     */
    public SimpleGraph(EPC epc) {
        Map<String, Integer> nodeId2vertex = new HashMap<String, Integer>();
        Map<Integer, String> vertex2nodeId = new HashMap<Integer, String>();

        vertices = new HashSet<Integer>();
        edges = new HashSet<TwoVertices>();
        connectors = new HashSet<Integer>();
        events = new HashSet<Integer>();
        ;
        functions = new HashSet<Integer>();

        outgoingEdges = new HashMap<Integer, Set<Integer>>();
        incomingEdges = new HashMap<Integer, Set<Integer>>();
        labels = new HashMap<Integer, String>();
        functionLabels = new HashSet<String>();
        eventLabels = new HashSet<String>();


        int vertexId = 0;
        for (nl.tue.tm.is.epc.Node n : epc.getNodes()) {
            vertices.add(vertexId);
//			System.out.println("adding "+ vertexId+ " "+n.getName()+" "+n.getName().replace('\n', ' ').replace("\\n", " "));
            labels.put(vertexId, n.getName().replace('\n', ' ').replace("\\n", " "));

            nodeId2vertex.put(n.getId(), vertexId);
            vertex2nodeId.put(vertexId, n.getId());

            if (n instanceof nl.tue.tm.is.epc.Function && n.getName() != null) {
                functionLabels.add(n.getName().replace('\n', ' '));
                functions.add(vertexId);
            } else if (n instanceof nl.tue.tm.is.epc.Event && n.getName() != null) {
                eventLabels.add(n.getName().replace('\n', ' '));
                events.add(vertexId);
            } else if (n instanceof nl.tue.tm.is.epc.Connector) {
                connectors.add(vertexId);
            }

            vertexId++;
        }

        for (Integer v = 0; v < vertexId; v++) {
            nl.tue.tm.is.epc.Node n = epc.findNode(vertex2nodeId.get(v));

            Set<Integer> incomingCurrent = new HashSet<Integer>();
            for (nl.tue.tm.is.epc.Node s : epc.getPre(n)) {
                if (s != null) {
                    incomingCurrent.add(nodeId2vertex.get(s.getId()));
                } else {
//					System.out.println("Null preset node.");
                }
            }
            incomingEdges.put(v, incomingCurrent);

            Set<Integer> outgoingCurrent = new HashSet<Integer>();
            for (nl.tue.tm.is.epc.Node t : epc.getPost(n)) {
                if (t != null) {
                    outgoingCurrent.add(nodeId2vertex.get(t.getId()));
                    TwoVertices edge = new TwoVertices(v, nodeId2vertex.get(t.getId()));
                    edges.add(edge);
                } else {
//					System.out.println("Null postset node.");
                }
            }
            outgoingEdges.put(v, outgoingCurrent);
        }
//		System.out.println(functionLabels.size() + " "+ eventLabels.size());
    }

    /**
     * Initializes a simple graph from a Petri net.
     */
    public SimpleGraph(PTNet ptnet) {
        Map<String, Integer> transId2vertex = new HashMap<String, Integer>();
        Map<Integer, String> vertex2transId = new HashMap<Integer, String>();

        vertices = new HashSet<Integer>();
        outgoingEdges = new HashMap<Integer, Set<Integer>>();
        incomingEdges = new HashMap<Integer, Set<Integer>>();
        labels = new HashMap<Integer, String>();

        int vertexId = 0;
        for (nl.tue.tm.is.ptnet.Transition t : ptnet.transitions()) {
            vertices.add(vertexId);
            if (!t.getName().equals(nl.tue.tm.is.ptnet.Transition.SILENT_LABEL)) {
                labels.put(vertexId, t.getName());
            } else {
                labels.put(vertexId, "");
            }
            transId2vertex.put(t.getId(), vertexId);
            vertex2transId.put(vertexId, t.getId());

            vertexId++;
        }

        for (nl.tue.tm.is.ptnet.Transition t : ptnet.transitions()) {
            int corrVertex = transId2vertex.get(t.getId());

            Set<Integer> outgoingCurrent = new HashSet<Integer>();
            for (nl.tue.tm.is.ptnet.Node n : ptnet.getPost(t)) {
                for (nl.tue.tm.is.ptnet.Node n2 : ptnet.getPost(n)) {
                    outgoingCurrent.add(transId2vertex.get(n2.getId()));
                }
            }
            outgoingEdges.put(corrVertex, outgoingCurrent);

            Set<Integer> incomingCurrent = new HashSet<Integer>();
            for (nl.tue.tm.is.ptnet.Node n : ptnet.getPre(t)) {
                for (nl.tue.tm.is.ptnet.Node n2 : ptnet.getPre(n)) {
                    incomingCurrent.add(transId2vertex.get(n2.getId()));
                }
            }
            incomingEdges.put(corrVertex, incomingCurrent);
        }
    }

    public Set<Integer> getVertices() {
        return vertices;
    }

    public Collection<TwoVertices> getEdgesAsCollection() {
        return new ArrayList<TwoVertices>(edges);
    }

    public Set<TwoVertices> getEdges() {
//		if (edges == null){
//			Set<TwoVertices> result = new HashSet<TwoVertices>();
//			for (Integer src: vertices){
//				for (Integer tgt: outgoingEdges.get(src)){
//					result.add(new TwoVertices(src,tgt));
//				}
//			}
//			edges = result;
//		}

        return new HashSet<TwoVertices>(edges);
//		return edges;
//		return result;
    }

    public Set<Integer> getConnectors() {
        return connectors;
    }

    public Set<Integer> getEvents() {
        return events;
    }

    public Set<Integer> getFunctions() {
        return functions;
    }

    public Map<Integer, Set<Integer>> getOutgoingEdges() {
        return outgoingEdges;
    }

    public Map<Integer, Set<Integer>> getIncomingEdges() {
        return incomingEdges;
    }

    public Set<String> getFunctionLabels() {
        return functionLabels;
    }

    public Set<String> getEventLabels() {
        return eventLabels;
    }

    public Set<Integer> postSet(int vertex) {
        return outgoingEdges.get(vertex);
    }

    public Set<Integer> preSet(int vertex) {
        return incomingEdges.get(vertex);
    }

    public LinkedList<String> getLabels() {
        return new LinkedList<String>(labels.values());
    }

    public Map<Integer, String> getLabelsAsMap() {
        return labels;
    }

    public String getLabel(int vertex) {
        return labels.get(vertex);
    }

    public Set<String> getLabels(Set<Integer> nodes) {
        Set<String> result = new HashSet<String>();

        for (Integer node : nodes) {
            result.add(getLabel(node));
        }

        return result;
    }

    public Integer getVertex(String label) {
        for (Integer v : vertices) {
            if (labels.get(v).equals(label)) {
                return v;
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * @return vertices that do not have an incoming edge.
     */
    public Set<Integer> sourceVertices() {
        Set<Integer> result = new HashSet<Integer>();
        for (Integer i : vertices) {
            if (incomingEdges.get(i).isEmpty()) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * @return vertices that do not have an outgoing edge.
     */
    public Set<Integer> sinkVertices() {
        Set<Integer> result = new HashSet<Integer>();
        for (Integer i : vertices) {
            if (outgoingEdges.get(i).isEmpty()) {
                result.add(i);
            }
        }
        return result;
    }

    public String toString() {
        String result = "";
        for (Integer i : vertices) {
            result += i + "(" + labels.get(i) + ") {";
            for (Iterator<Integer> j = incomingEdges.get(i).iterator(); j.hasNext(); ) {
                int vertex = j.next();
                result += vertex;// + "(" + labels.get(vertex) + ")";
                result += j.hasNext() ? "," : "";
            }
            result += "} {";
            for (Iterator<Integer> j = outgoingEdges.get(i).iterator(); j.hasNext(); ) {
                int vertex = j.next();
                result += vertex;// + "(" + labels.get(vertex) + ")";
                result += j.hasNext() ? "," : "";
            }
            result += "}\n";
        }
        return result;
    }

    /**
     * @param vertex Vertex to determine the postSet for
     * @param silent Set of vertices that should not be considered
     * @return the postSet(vertex), in which all v \in silent are (recursively) replaced by their postSet(v)
     */
    public Set<Integer> nonSilentPostSet(Integer vertex, Set<Integer> silent) {
        return nonSilentPostSetHelper(vertex, silent, new HashSet<Integer>());
    }

    private Set<Integer> nonSilentPostSetHelper(Integer vertex, Set<Integer> silent, Set<Integer> visited) {
        Set<Integer> result = new HashSet<Integer>();
        Set<Integer> visitedP = new HashSet<Integer>(visited);
        visitedP.add(vertex);

        for (Integer post : postSet(vertex)) {
            if (!visited.contains(post)) {
                if (silent.contains(post)) {
                    result.addAll(nonSilentPostSetHelper(post, silent, visitedP));
                } else {
                    result.add(post);
                }
            }
        }
        return result;
    }

    /**
     * @param vertex Vertex to determine the preSet for
     * @param silent Set of vertices that should not be considered
     * @return the preSet(vertex), in which all v \in silent are (recursively) replaced by their preSet(v)
     */
    public Set<Integer> nonSilentPreSet(Integer vertex, Set<Integer> silent) {
        return nonSilentPreSetHelper(vertex, silent, new HashSet<Integer>());
    }

    private Set<Integer> nonSilentPreSetHelper(Integer vertex, Set<Integer> silent, Set<Integer> visited) {
        Set<Integer> result = new HashSet<Integer>();
        Set<Integer> visitedP = new HashSet<Integer>(visited);
        visitedP.add(vertex);

        for (Integer pre : preSet(vertex)) {
            if (!visited.contains(pre)) {
                if (silent.contains(pre)) {
                    result.addAll(nonSilentPreSetHelper(pre, silent, visitedP));
                } else {
                    result.add(pre);
                }
            }
        }
        return result;
    }

    /**
     * Returns A COPY OF the graph, such that all vertices from the given set are removed.
     * All paths (v1,v),(v,v2) via a vertex v from that set are replaced by direct arcs (v1,v2).
     * <p/>
     * Formally: for G = (V, E, l)
     * return (V-vertices, E', l-(vertices x labels)), where
     * E' = E - ((V x vertices) U (vertices X V))
     * U {(v1, v2)|v \in vertices, (v1,v) \in E \land (v,v2) \in E}
     */
    public SimpleGraph removeVertices(Set<Integer> toRemove) {
        Set<Integer> newVertices = new HashSet<Integer>(vertices);
        newVertices.removeAll(toRemove);

        Map<Integer, Set<Integer>> newOutgoingEdges = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> newIncomingEdges = new HashMap<Integer, Set<Integer>>();
        ;
        Map<Integer, String> newLabels = new HashMap<Integer, String>();

        for (Integer newVertex : newVertices) {
            newOutgoingEdges.put(newVertex, nonSilentPostSet(newVertex, toRemove));
            newIncomingEdges.put(newVertex, nonSilentPreSet(newVertex, toRemove));
            newLabels.put(newVertex, labels.get(newVertex));
        }

        return new SimpleGraph(newVertices, newOutgoingEdges, newIncomingEdges, newLabels);
    }

    /**
     * Given subset of vertices of this graph, the method builds the corresponding subgraph.
     *
     * @param _vertices Set of vertices in the subgraph
     * @return The subgraph
     */
    public SimpleGraph subgraph(Set<Integer> _vertices) {
        Set<Integer> newVertices = new HashSet<Integer>(vertices);
        newVertices.removeAll(_vertices);

        Map<Integer, Set<Integer>> newOutgoingEdges = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> newIncomingEdges = new HashMap<Integer, Set<Integer>>();
        ;
        Map<Integer, String> newLabels = new HashMap<Integer, String>();

        for (Integer newVertex : newVertices) {
            HashSet<Integer> vertexSet = new HashSet<Integer>();
            for (Integer source : preSet(newVertex))
                if (newVertices.contains(source))
                    vertexSet.add(source);
            newIncomingEdges.put(newVertex, vertexSet);

            vertexSet = new HashSet<Integer>();
            for (Integer target : postSet(newVertex))
                if (newVertices.contains(target))
                    vertexSet.add(target);
            newOutgoingEdges.put(newVertex, vertexSet);

            newLabels.put(newVertex, labels.get(newVertex));
        }

        return new SimpleGraph(newVertices, newOutgoingEdges, newIncomingEdges, newLabels);
    }
}
