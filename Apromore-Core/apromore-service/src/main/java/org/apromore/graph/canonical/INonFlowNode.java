package org.apromore.graph.canonical;

import java.util.Map;

import org.jbpt.hypergraph.abs.IVertex;

/**
 * Interface for all nodes of a process, that do not take part of the control flow.
 * 
 * @author Cindy Fhnrich, Tobias Hoppe
 *
 */
public interface INonFlowNode extends IVertex {

    /**
     * Add an attribute to the {@link INode}.
     * @param name  the name of the attribute
     * @param value the simple value of the {@link IAttribute}
     * @param any the complex value of the {@link IAttribute}
     */
    void addAttribute(String name, String value, Object any);

    /**
     * Add an attribute to the {@link INode}.
     * @param name  the name of the attribute
     * @param value the simple value of the {@link IAttribute}
     */
    void addAttribute(String name, String value);

    /**
     * Set if this {@link INode} attributes.
     * @param attributes the map of attributes
     */
    void setAttributes(Map<String, IAttribute> attributes);

    /**
     * Return this {@link INode} attributes.
     * @return the attributes
     */
    Map<String, IAttribute> getAttributes();

}
