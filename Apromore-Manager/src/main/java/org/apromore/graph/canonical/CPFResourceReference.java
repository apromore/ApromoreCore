package org.apromore.graph.canonical;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for CPF Resources.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CPFResourceReference extends CPFNonFlowNode implements ICPFResourceReference, Cloneable {

    private String resourceId;
    private String qualifier;

    private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>(0);


    /**
     * Create a new instance of this class where parent and label are set to <code>null</code>.
     */
    public CPFResourceReference() {
        super();
    }



    @Override
    public String getResourceId() {
        return resourceId;
    }

    @Override
    public void setResourceId(String newResourceId) {
        resourceId = newResourceId;
    }


    @Override
    public String getQualifier() {
        return qualifier;
    }

    @Override
    public void setQualifier(String newQualifier) {
        qualifier = newQualifier;
    }

    @Override
    public void setAttributes(final Map<String, IAttribute> properties) {
        this.attributes = properties;
    }

    @Override
    public Map<String, IAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public IAttribute getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(final String name, final String value, final java.lang.Object any) {
        attributes.put(name, new CPFAttribute(value, any));
    }

    @Override
    public void setAttribute(final String name, final String value) {
        setAttribute(name, value, null);
    }


    @Override
    public CPFResourceReference clone() {
        CPFResourceReference clone;
        clone = (CPFResourceReference) super.clone();
        if (this.getId() != null) {
            clone.setId(this.getId());
        }
        clone.setName(this.getName());
        clone.setResourceId(this.resourceId);
        clone.setQualifier(this.qualifier);

        clone.setAttributes(this.attributes);

        return clone;
    }

}
