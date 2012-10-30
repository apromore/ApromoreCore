package org.apromore.graph.canonical;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.hypergraph.abs.Vertex;

/**
 * Implementation of a Canonical node.
 * A node of a Canonical is one of the following:
 * {@link org.apromore.graph.canonical.Message} or
 * {@link org.apromore.graph.canonical.Timer} or
 * {@link org.apromore.graph.canonical.Task} or
 * {@link org.apromore.graph.canonical.State} or
 * {@link org.apromore.graph.canonical.OrSplit} or
 * {@link org.apromore.graph.canonical.XOrSplit} or
 * {@link Split} or
 * {@link org.apromore.graph.canonical.OrJoin} or
 * {@link org.apromore.graph.canonical.XOrJoin} or
 * {@link Join}.
 *
 * @author Cameron James
 */
public class Node extends Vertex implements INode {

    private String originalId;
    private boolean configurable = false;

    private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>(0);
    private Set<IResource> resources = new HashSet<IResource>(0);
    private Set<IObject> objects = new HashSet<IObject>(0);


    /**
     * Empty constructor.
     */
    public Node() {
        super();
    }

    /**
     * Constructor with label of the node parameter.
     * @param label String to use as a label of this node.
     */
    public Node(String label) {
        super();
        this.setLabel(label);
    }

    /**
     * Constructor with label and description of the node parameters.
     * @param label String to use as a label of this node.
     * @param desc  String to use as a description of this node.
     */
    public Node(String label, String desc) {
        super();
        this.setLabel(label);
        this.setDescription(desc);
    }

    @Override
    public String getLabel() {
        return this.getName();
    }

    @Override
    public void setLabel(String label) {
        this.setName(label);
    }

    /**
     * Set the original Id of this Node.
     * @param newOriginalId the originalId
     */
    @Override
    public void setOriginalId(String newOriginalId) {
        originalId = newOriginalId;
    }

    /**
     * returns if this Node configurable.
     * @return true or false
     */
    @Override
    public String getOriginalId() {
        return originalId;
    }

    /**
     * Set if this node is configurable.
     * @param config the config boolean
     */
    @Override
    public void setConfigurable(boolean config) {
        configurable = config;
    }

    /**
     * returns if this Node configurable.
     * @return true or false
     */
    @Override
    public boolean isConfigurable() {
        return configurable;
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

    @Override
    public IAttribute getAttribute(String name) {
        return attributes.get(name);
    }


    /**
     * @return a {@link java.util.Collection} of all {@link IResource}s of this {@link INode}.
     * @return collection of Resources
     */
    @Override
    public Collection<IResource> getResources() {
        return resources;
    }

    /**
     * Add a given {@link IResource} to this {@link INode}.
     * @param newResource to add to this {@link INode}
     */
    @Override
    public void addResource(IResource newResource) {
        resources.add(newResource);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link IObject}s of this {@link INode}.
     */
    @Override
    public Collection<IObject> getObjects() {
        return objects;
    }

    /**
     * Add a given {@link IObject} to this {@link INode}.
     * @param object to add to this {@link INode}
     */
    @Override
    public void addObject(IObject object) {
        objects.add(object);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link IResource}s of this {@link INode}.
     */
    @Override
    public Collection<IResource> getResource() {
        return resources;
    }
}
