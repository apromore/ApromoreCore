package org.apromore.graph.canonical;

import org.apromore.cpf.EdgeType;
import org.jbpt.algo.graph.DirectedGraphAlgorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of ICanonical interface.
 * <p/>
 *
 * @author Cameron James
 */
public class Canonical extends AbstractCanonical<CPFEdge, CPFNode> {

    public static DirectedGraphAlgorithms<CPFEdge, CPFNode> DIRECTED_GRAPH_ALGORITHMS = new DirectedGraphAlgorithms<CPFEdge, CPFNode>();

    private CPFNode entry = null;
    private CPFNode exit = null;

    private String uri;
    private String version;
    private String author;
    private String creationDate;
    private String modifiedDate;

    private Set<ICPFObject> objects = new HashSet<ICPFObject>(0);
    private Set<ICPFResource> resources = new HashSet<ICPFResource>(0);
    private Map<String, IAttribute> properties = new HashMap<String, IAttribute>(0);
    private final Map<String, Map<String, String>> nodeProperties = new HashMap<String, Map<String, String>>(0);
    private final Map<String, String> originalNodeMapping = new HashMap<String, String>(0);


    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setUri(String newUri) {
        uri = newUri;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String newVersion) {
        version = newVersion;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String newAuthor) {
        author = newAuthor;
    }

    @Override
    public String getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(String newCreationDate) {
        creationDate = newCreationDate;
    }

    @Override
    public String getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public void setModifiedDate(String newModifiedDate) {
        modifiedDate = newModifiedDate;
    }

    @Override
    public CPFNode getEntry() {
        return entry;
    }

    @Override
    public void setEntry(CPFNode entry) {
        this.entry = entry;
    }

    @Override
    public CPFNode getExit() {
        return exit;
    }

    @Override
    public void setExit(CPFNode exit) {
        this.exit = exit;
    }


    @Override
    public Set<ICPFResource> getResources() {
        return resources;
    }

    @Override
    public void setResources(final Set<ICPFResource> resources) {
        this.resources = resources;
    }

    @Override
    public void addResource(ICPFResource newResource) {
        resources.add(newResource);
    }

    @Override
    public Set<ICPFObject> getObjects() {
        return objects;
    }

    @Override
    public void addObject(ICPFObject object) {
        objects.add(object);
    }



    @Override
    public CPFEdge addEdge(CPFEdge newEdge) {
        Collection<CPFNode> ss = new ArrayList<CPFNode>();
        Collection<CPFNode> ts = new ArrayList<CPFNode>();

        ss.add(newEdge.getSource());
        ts.add(newEdge.getTarget());

        if (!this.checkEdge(ss, ts)) {
            return null;
        }

        return new CPFEdge(this, newEdge);
    }

    @Override
    public CPFEdge addEdge(CPFNode from, CPFNode to) {
        if (from == null || to == null) {
            return null;
        }

        Collection<CPFNode> ss = new ArrayList<CPFNode>();
        Collection<CPFNode> ts = new ArrayList<CPFNode>();

        ss.add(from);
        ts.add(to);

        if (!this.checkEdge(ss, ts)) {
            return null;
        }

        return new CPFEdge(this, from, to);
    }

    @Override
    public CPFEdge updateEdge(CPFEdge edge, EdgeType edgeType, CPFExpression expr) {
        if (edge == null) {
            return null;
        }

        for (CPFEdge e : this.getEdges()) {
            if (e.getSource().getId().equals(edge.getSource().getId()) && e.getSource().getId().equals(edge.getSource().getId())) {
                e.setId(edgeType.getId());
                e.setOriginalId(edgeType.getOriginalID());
                e.setDefault(edgeType.isDefault());
                e.setConditionExpr(expr);
                return e;
            }
        }

        return null;
    }


    @Override
    public Set<CPFNode> getSourceNodes() {
        return Canonical.DIRECTED_GRAPH_ALGORITHMS.getSources(this);
    }

    @Override
    public Set<CPFNode> getSinkNodes() {
        return Canonical.DIRECTED_GRAPH_ALGORITHMS.getSinks(this);
    }


    @Override
    public Collection<CPFNode> getAllPredecessors(CPFNode fn) {
        Set<CPFNode> result = new HashSet<CPFNode>();

        Set<CPFNode> temp = new HashSet<CPFNode>();
        temp.addAll(getDirectPredecessors(fn));
        result.addAll(temp);
        while(!(temp.isEmpty())) {
            Set<CPFNode> temp2 = new HashSet<CPFNode>();
            for (CPFNode flowNode : temp) {
                temp2.addAll(getDirectPredecessors(flowNode));
            }
            temp = temp2;
            Set<CPFNode> temp3 = new HashSet<CPFNode>();
            for (CPFNode flowNode : temp) {
                if(!(result.contains(flowNode))) {
                    result.add(flowNode);
                } else {
                    temp3.add(flowNode);
                }
            }
            for (CPFNode flowNode : temp3) {
                temp.remove(flowNode);
            }
        }

        return result;
    }

    @Override
    public Collection<CPFNode> getAllSuccessors(CPFNode fn) {
        Set<CPFNode> result = new HashSet<CPFNode>();

        Set<CPFNode> temp = new HashSet<CPFNode>();
        temp.addAll(getDirectSuccessors(fn));
        result.addAll(temp);
        while(!(temp.isEmpty())) {
            Set<CPFNode> temp2 = new HashSet<CPFNode>();
            for (CPFNode flowNode : temp) {
                temp2.addAll(getDirectSuccessors(flowNode));
            }
            temp = temp2;
            Set<CPFNode> temp3 = new HashSet<CPFNode>();
            for (CPFNode flowNode : temp) {
                if(!(result.contains(flowNode))) {
                    result.add(flowNode);
                } else {
                    temp3.add(flowNode);
                }
            }
            for (CPFNode flowNode : temp3) {
                temp.remove(flowNode);
            }
        }

        return result;
    }

    @Override
    public Collection<CPFNode> getDirectPredecessors(CPFNode node) {
        Set<CPFNode> result = new HashSet<CPFNode>();

        Collection<CPFEdge> es = this.getIncomingEdges(node);
        for (CPFEdge e : es) {
            result.addAll(e.getSourceVertices());
        }

        return result;
    }

    @Override
    public Collection<CPFNode> getDirectPredecessors(Collection<CPFNode> vs) {
        Set<CPFNode> result = new HashSet<CPFNode>();

        Collection<CPFEdge> es = this.getEdgesWithTargets(vs);
        for (CPFEdge e : es) {
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
        properties.put(name, new CPFAttribute(value, any));
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
