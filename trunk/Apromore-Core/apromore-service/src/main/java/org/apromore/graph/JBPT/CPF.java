package org.apromore.graph.JBPT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.ProcessModel;

/**
 * Implementation of the Canonical Process Format for JBPT.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CPF extends ProcessModel implements ICpf<ControlFlow<FlowNode>, FlowNode, NonFlowNode> {

    private Map<String, ICpfAttribute> properties = new HashMap<String, ICpfAttribute>(0);
    private final Map<String, Map<String, String>> vertexProperties;
    private final Map<String, FlowNode> pmgVertices = new HashMap<String, FlowNode>(0);
    private final Map<String, String> originalNodeMapping = new HashMap<String, String>(0);


    public CPF() {
        super();
        vertexProperties = new HashMap<String, Map<String, String>>(0);
    }


    public List<FlowNode> getSourceVertices() {
        List<FlowNode> sources = new ArrayList<FlowNode>(0);
        for (FlowNode v : getFlowNodes()) {
            if (getFirstDirectPredecessor(v) == null) {
                sources.add(v);
            }
        }
        return sources;
    }

    public List<FlowNode> getSinkVertices() {
        List<FlowNode> sinks = new ArrayList<FlowNode>(0);
        for (FlowNode v : getFlowNodes()) {
            if (getFirstDirectSuccessor(v) == null) {
                sinks.add(v);
            }
        }
        return sinks;
    }

    public FlowNode getVertex(final String vid) {
        return pmgVertices.get(vid);
    }

    @Override
    public FlowNode addVertex(final FlowNode v) {
        pmgVertices.put(v.getId(), v);
        return super.addFlowNode(v);
    }

    public void setVertexProperty(final String vertex, final String propertyName, final String propertyValue) {
        Map<String, String> properties = vertexProperties.get(vertex);
        if (properties == null) {
            properties = new HashMap<String, String>(0);
            vertexProperties.put(vertex, properties);
        }
        properties.put(propertyName, propertyValue);
    }

    public String getVertexProperty(final String vertexId, final String propertyName) {
        String result = null;
        Map<String, String> properties = vertexProperties.get(vertexId);
        if (properties != null) {
            result = properties.get(propertyName);
        }
        return result;
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


    @Override
    public void setProperties(final Map<String, ICpfAttribute> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, ICpfAttribute> getProperties() {
        return properties;
    }

    @Override
    public ICpfAttribute getProperty(final String name) {
        return properties.get(name);
    }

    @Override
    public void setProperty(final String name, final String value, final Object any) {
        properties.put(name, new CpfAttribute(value, any));
    }

    @Override
    public void setProperty(final String name, final String value) {
        setProperty(name, value, null);
    }

}
