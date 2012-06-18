package org.apromore.graph.JBPT;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.IFlowNode;
import org.jbpt.pm.IResource;

import java.util.Collection;
import java.util.Map;

/**
 * CPF Work interface
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICpfNode extends IVertex {


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
     * @return a {@link Collection} of all {@link IResource}s of this {@link IFlowNode}.
     */
    public Collection<IResource> getResources();

    /**
     * Add a given {@link IResource} to this {@link IFlowNode}.
     * @param resource to add to this {@link IFlowNode}
     */
    public void addResource(IResource resource);


    /**
     * add an attribute to the {@link ICpfNode}.
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    void addAttribute(String name, String value);

    /**
     * Set if this {@link ICpfNode} attributes.
     * @param attributes the map of attributes
     */
    void setAttributes(Map<String, String> attributes);

    /**
     * Returns a single Attribute value for a given key.
     * @param name the key of the Attribute we are looking for
     * @return the value of the found Attribute, otherwise null is not in the list.
     */
    String getAttribute(String name);

    /**
     * Return this {@link ICpfNode} attributes.
     * @return the attributes
     */
    Map<String, String> getAttributes();

    /**
     * @return a {@link java.util.Collection} of all {@link ICpfObject}s of this {@link IFlowNode}.
     */
    public Collection<ICpfObject> getObjects();

    /**
     * Add a given {@link ICpfObject} to this {@link IFlowNode}.
     * @param object to add to this {@link IFlowNode}
     */
    public void addObject(ICpfObject object);

    /**
     * @return a {@link java.util.Collection} of all {@link ICpfResource}s of this {@link IFlowNode}.
     */
    public Collection<ICpfResource> getResource();

    /**
     * Add a given {@link ICpfResource} to this {@link IFlowNode}.
     * @param newResource to add to this {@link IFlowNode}
     */
    public void addResource(ICpfResource newResource);
}
