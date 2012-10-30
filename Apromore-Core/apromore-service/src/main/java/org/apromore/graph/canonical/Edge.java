package org.apromore.graph.canonical;

import java.util.HashMap;
import java.util.Map;

import org.jbpt.graph.abs.AbstractMultiDirectedGraph;

/**
 * Interface to a Canonical Edge.
 *
 * @author Cameron James
 */
public class Edge extends AbstractEdge<Node> implements IEdge<Node> {

    private String originalId;
    private boolean isDefault = false;
    private Expression conditionExpr;
    private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>(0);


    public Edge(AbstractMultiDirectedGraph<?, Node> g, Node source, Node target) {
        super(g, source, target);
    }

    public Edge(AbstractMultiDirectedGraph<?, Node> g, Edge edge) {
        super(g, edge.getSource(), edge.getTarget());

        originalId = edge.getOriginalId();
        isDefault = edge.isDefault();
        conditionExpr = edge.getConditionExpr();
        attributes = edge.getAttributes();
    }


    @Override
    public void setOriginalId(final String newOriginalId) {
        originalId = newOriginalId;
    }

    @Override
    public String getOriginalId() {
        return originalId;
    }

    @Override
    public void setDefault(final boolean newDefault) {
        isDefault = newDefault;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setConditionExpr(final Expression newConditionExpr) {
        conditionExpr = newConditionExpr;
    }

    @Override
    public Expression getConditionExpr() {
        return conditionExpr;
    }

    @Override
    public void addAttribute(final String name, final String value, final Object any) {
        attributes.put(name, new Attribute(value, any));
    }

    @Override
    public void addAttribute(final String name, final String value) {
        addAttribute(name, value, null);
    }

    @Override
    public void setAttributes(final Map<String, IAttribute> newAttributes) {
        attributes = newAttributes;
    }

    @Override
    public Map<String, IAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public IAttribute getAttribute(final String name) {
        return attributes.get(name);
    }
}
