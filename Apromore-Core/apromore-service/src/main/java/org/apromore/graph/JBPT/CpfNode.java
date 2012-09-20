package org.apromore.graph.JBPT;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.pm.Activity;

/**
 * CPF Work implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CpfNode extends Activity implements ICpfNode {

    private boolean configurable = false;
    private Map<String, ICpfAttribute> attributes = new HashMap<String, ICpfAttribute>(0);
    private Set<ICpfResource> resources = new HashSet<ICpfResource>(0);
    private Set<ICpfObject> objects = new HashSet<ICpfObject>(0);

    public CpfNode() {
        super();
    }

    public CpfNode(String name) {
        super(name);
    }


    /**
     * Return this {@link org.apromore.graph.JBPT.ICpfNode} attributes.
     *
     * @return the attributes
     */
    @Override
    public Map<String, ICpfAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Set if this node is configurable.
     *
     * @param config the config boolean
     */
    @Override
    public void setConfigurable(boolean config) {
        configurable = config;
    }

    /**
     * returns if this Node configurable.
     *
     * @return true or false
     */
    @Override
    public boolean isConfigurable() {
        return configurable;
    }

    /**
     * add an attribute to the {@link org.apromore.graph.JBPT.ICpfNode}.
     *
     * @param name  the name of the attribute
     * @param value the value of the attribute
     */
    @Override
    public void addAttribute(final String name, final String value, final Object any) {
        attributes.put(name, new CpfAttribute(value, any));
    }
    
    @Override
    public void addAttribute(String name, String value) {
        addAttribute(name, value, null);
    }    

    /**
     * Set if this {@link org.apromore.graph.JBPT.ICpfNode} attributes.
     *
     * @param newAttributes the map of attributes
     */
    @Override
    public void setAttributes(Map<String, ICpfAttribute> newAttributes) {
        attributes = newAttributes;
    }

    /**
     * Returns a single Attribute value for a given key.
     *
     * @param name the key of the Attribute we are looking for
     * @return the value of the found Attribute, otherwise null is not in the list.
     */
    @Override
    public ICpfAttribute getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Add a given {@link org.apromore.graph.JBPT.ICpfResource} to this {@link IFlowNode}.
     *
     * @param newResource to add to this {@link IFlowNode}
     */
    @Override
    public void addResource(ICpfResource newResource) {
        resources.add(newResource);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link org.apromore.graph.JBPT.ICpfObject}s of this {@link IFlowNode}.
     */
    @Override
    public Collection<ICpfObject> getObjects() {
        return objects;
    }

    /**
     * Add a given {@link org.apromore.graph.JBPT.ICpfObject} to this {@link IFlowNode}.
     *
     * @param object to add to this {@link IFlowNode}
     */
    @Override
    public void addObject(ICpfObject object) {
        objects.add(object);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link org.apromore.graph.JBPT.ICpfResource}s of this {@link IFlowNode}.
     */
    @Override
    public Collection<ICpfResource> getResource() {
        return resources;
    }

}
