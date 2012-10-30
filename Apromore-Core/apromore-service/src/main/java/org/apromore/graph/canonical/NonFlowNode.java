package org.apromore.graph.canonical;

import java.util.HashMap;
import java.util.Map;

import org.jbpt.hypergraph.abs.Vertex;

/**
 * Base class for nodes that does not take part of the control flow.
 *
 * @author Tobias Hoppe
 */
public class NonFlowNode extends Vertex implements INonFlowNode {

    protected Map<String, IAttribute> attributes = new HashMap<String, IAttribute>(0);


    /**
     * Create a new node that does not take part of the control flow.
     */
    public NonFlowNode() {
        super();
    }

    /**
     * Create a new node with the given name that does not take part of the control flow.
     *
     * @param name of the node
     */
    public NonFlowNode(String name) {
        super(name);
    }

    /**
     * Create a new node with the given name and description, that does not take part of the control flow.
     *
     * @param name        of the node
     * @param description of the node
     */
    public NonFlowNode(String name, String description) {
        super(name, description);
    }



    @Override
    public void addAttribute(final String name, final String value, final Object any) {
        attributes.put(name, new Attribute(value, any));
    }

    @Override
    public void addAttribute(String name, String value) {
        addAttribute(name, value, null);
    }

    @Override
    public void setAttributes(Map<String, IAttribute> newAttributes) {
        attributes = newAttributes;
    }

    @Override
    public Map<String, IAttribute> getAttributes() {
        return attributes;
    }

}
