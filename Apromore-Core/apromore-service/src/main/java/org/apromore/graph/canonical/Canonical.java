package org.apromore.graph.canonical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.algo.graph.GraphAlgorithms;

/**
 * An implementation of ICanonical interface.
 * <p/>
 *
 * @author Cameron James
 */
public class Canonical extends AbstractCanonical<Edge, Node, Event, Task, Message, Timer, State, Split, Join> {

    public static GraphAlgorithms<Edge, Node> GRAPH_ALGORITHMS = new GraphAlgorithms<Edge, Node>();
    public static DirectedGraphAlgorithms<Edge, Node> DIRECTED_GRAPH_ALGORITHMS = new DirectedGraphAlgorithms<Edge, Node>();

    private Map<String, IAttribute> properties = new HashMap<String, IAttribute>(0);
    private final Map<String, Map<String, String>> nodeProperties = new HashMap<String, Map<String, String>>(0);
    private final Map<String, String> originalNodeMapping = new HashMap<String, String>(0);
    private Node entry = null;
    private Node exit = null;

    @Override
    public Node getEntry() {
        return entry;
    }

    @Override
    public void setEntry(Node entry) {
        this.entry = entry;
    }

    @Override
    public Node getExit() {
        return exit;
    }

    @Override
    public void setExit(Node exit) {
        this.exit = exit;
    }


    @Override
    public Edge addEdge(Edge newEdge) {
        Collection<Node> ss = new ArrayList<Node>();
        Collection<Node> ts = new ArrayList<Node>();

        ss.add(newEdge.getSource());
        ts.add(newEdge.getTarget());

        if (!this.checkEdge(ss, ts)) {
            return null;
        }

        return new Edge(this, newEdge);
    }

    @Override
    public Edge addEdge(Node from, Node to) {
        if (from == null || to == null) {
            return null;
        }

        Collection<Node> ss = new ArrayList<Node>();
        Collection<Node> ts = new ArrayList<Node>();

        ss.add(from);
        ts.add(to);

        if (!this.checkEdge(ss, ts)) {
            return null;
        }

        return new Edge(this, from, to);
    }

    @Override
    public Set<Node> getSourceNodes() {
        return Canonical.DIRECTED_GRAPH_ALGORITHMS.getSources(this);
    }

    @Override
    public Set<Node> getSinkNodes() {
        return Canonical.DIRECTED_GRAPH_ALGORITHMS.getSinks(this);
    }


    @Override
    public Collection<Node> getAllPredecessors(Node fn) {
        Set<Node> result = new HashSet<Node>();

        Set<Node> temp = new HashSet<Node>();
        temp.addAll(getDirectPredecessors(fn));
        result.addAll(temp);
        while(!(temp.isEmpty())) {
            Set<Node> temp2 = new HashSet<Node>();
            for (Node flowNode : temp) {
                temp2.addAll(getDirectPredecessors(flowNode));
            }
            temp = temp2;
            Set<Node> temp3 = new HashSet<Node>();
            for (Node flowNode : temp) {
                if(!(result.contains(flowNode))) {
                    result.add(flowNode);
                } else {
                    temp3.add(flowNode);
                }
            }
            for (Node flowNode : temp3) {
                temp.remove(flowNode);
            }
        }

        return result;
    }

    @Override
    public Collection<Node> getAllSuccessors(Node fn) {
        Set<Node> result = new HashSet<Node>();

        Set<Node> temp = new HashSet<Node>();
        temp.addAll(getDirectSuccessors(fn));
        result.addAll(temp);
        while(!(temp.isEmpty())) {
            Set<Node> temp2 = new HashSet<Node>();
            for (Node flowNode : temp) {
                temp2.addAll(getDirectSuccessors(flowNode));
            }
            temp = temp2;
            Set<Node> temp3 = new HashSet<Node>();
            for (Node flowNode : temp) {
                if(!(result.contains(flowNode))) {
                    result.add(flowNode);
                } else {
                    temp3.add(flowNode);
                }
            }
            for (Node flowNode : temp3) {
                temp.remove(flowNode);
            }
        }

        return result;
    }

    @Override
    public Collection<Node> getDirectPredecessors(Node node) {
        Set<Node> result = new HashSet<Node>();

        Collection<Edge> es = this.getIncomingEdges(node);
        for (Edge e : es) {
            result.addAll(e.getSourceVertices());
        }

        return result;
    }

    @Override
    public Collection<Node> getDirectPredecessors(Collection<Node> vs) {
        Set<Node> result = new HashSet<Node>();

        Collection<Edge> es = this.getEdgesWithTargets(vs);
        for (Edge e : es) {
            result.addAll(e.getSourceVertices());
        }

        return result;
    }


    @Override
    public void setNodeProperty(final String nodeId, final String propertyName, final String propertyValue) {
        Map<String, String> properties = nodeProperties.get(nodeId);
        if (properties == null) {
            properties = new HashMap<String, String>(0);
            nodeProperties.put(nodeId, properties);
        }
        properties.put(propertyName, propertyValue);
    }

    @Override
    public String getNodeProperty(final String nodeId, final String propertyName) {
        String result = null;
        Map<String, String> properties = nodeProperties.get(nodeId);
        if (properties != null) {
            result = properties.get(propertyName);
        }
        return result;
    }

    @Override
    public void setProperties(final Map<String, IAttribute> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, IAttribute> getProperties() {
        return properties;
    }

    @Override
    public IAttribute getProperty(final String name) {
        return properties.get(name);
    }

    @Override
    public void setProperty(final String name, final String value, final java.lang.Object any) {
        properties.put(name, new Attribute(value, any));
    }

    @Override
    public void setProperty(final String name, final String value) {
        setProperty(name, value, null);
    }


    public Map<String, String> getOriginalNodeMapping() {
        return originalNodeMapping;
    }

    public void addOriginalNodeMapping(final String duplicateNode, final String originalNode) {
        originalNodeMapping.put(duplicateNode, originalNode);
    }

    public boolean isDuplicateNode(final String node) {
        return originalNodeMapping.keySet().contains(node);
    }

    public String getOriginalNode(final String duplicateNode) {
        return originalNodeMapping.get(duplicateNode);
    }


}
