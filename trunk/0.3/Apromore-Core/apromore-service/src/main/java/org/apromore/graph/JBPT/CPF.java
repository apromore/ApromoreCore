package org.apromore.graph.JBPT;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.ProcessModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Canonical Process Format for JBPT.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CPF extends ProcessModel implements ICpf<ControlFlow<FlowNode>, FlowNode, NonFlowNode> {

    private Map<String, String> properties = new HashMap<String, String>(0);
    private Map<String, Map<String, String>> vertexProperties;
    private Map<String, FlowNode> pmgVertices = new HashMap<String, FlowNode>(0);
    private Map<String, String> originalNodeMapping = new HashMap<String, String>(0);


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

    public FlowNode getVertex(String vid) {
        return pmgVertices.get(vid);
    }

    @Override
    public FlowNode addVertex(FlowNode v) {
        pmgVertices.put(v.getId(), v);
        return super.addFlowNode(v);
    }

    public void setVertexProperty(String vertex, String propertyName, String propertyValue) {
        Map<String, String> properties = vertexProperties.get(vertex);
        if (properties == null) {
            properties = new HashMap<String, String>(0);
            vertexProperties.put(vertex, properties);
        }
        properties.put(propertyName, propertyValue);
    }

    public String getVertexProperty(String vertexId, String propertyName) {
        String result  = null;
        Map<String, String> properties = vertexProperties.get(vertexId);
        if (properties != null) {
            result = properties.get(propertyName);
        }
        return result;
    }

    public Map<String, String> getOriginalNodeMapping() {
        return originalNodeMapping;
    }

    public void addOriginalNodeMapping(String duplicateNode, String originalNode) {
        originalNodeMapping.put(duplicateNode, originalNode);
    }

    public boolean isDuplicateNode(String node) {
        return originalNodeMapping.keySet().contains(node);
    }

    public String getOriginalNode(String duplicateNode) {
        return originalNodeMapping.get(duplicateNode);
    }


    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public void setProperty(String name, String value) {
        properties.put(name, value);
    }
}
