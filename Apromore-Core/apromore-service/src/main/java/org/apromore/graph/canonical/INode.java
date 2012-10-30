package org.apromore.graph.canonical;

import java.util.Collection;
import java.util.Map;

import org.jbpt.hypergraph.abs.IVertex;

/**
 * Interface to a Canonical node.
 *
 * @author Cameron James
 */
public interface INode extends IVertex {

    /**
     * Get label of this node.
     * @return Label of this node.
     */
    public String getLabel();

    /**
     * Set label of this node.
     * @param label String to use as label of this node.
     */
    public void setLabel(String label);

    /**
     * Sets the Original Id.
     * @param newOriginalId the new Original Id
     */
    void setOriginalId(String newOriginalId);

    /**
     * returns if this Node configurable.
     * @return true or false
     */
    String getOriginalId();


    /**
     * Set if this node is configurable.
     * @param config the config boolean
     */
    void setConfigurable(boolean config);

    /**
     * returns if this Node configurable.
     * @return true or false
     */
    boolean isConfigurable();



    /**
     * @return a {@link java.util.Collection} of all {@link IResource}s of this {@link INode}.
     */
    public Collection<IResource> getResources();

    /**
     * Add a given to this {@link org.jbpt.petri.IFlow}.
     * @param resource to add to this {@link org.jbpt.petri.IFlow}
     */
    public void addResource(IResource resource);


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

    /**
     * Returns the Attribute for the supplied name.
     * @param name the name
     * @return the attribute it founds.
     */
    IAttribute getAttribute(String name);


    /**
     * @return a {@link java.util.Collection} of all {@link IObject}s of this {@link INode}.
     */
    public Collection<IObject> getObjects();

    /**
     * Add a given {@link IObject} to this {@link INode}.
     * @param object to add to this {@link INode}
     */
    public void addObject(IObject object);

    /**
     * @return a {@link java.util.Collection} of all {@link IResource}s of this {@link INode}.
     */
    public Collection<IResource> getResource();

}