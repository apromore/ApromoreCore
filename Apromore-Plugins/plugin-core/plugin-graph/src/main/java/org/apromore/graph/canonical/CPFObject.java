package org.apromore.graph.canonical;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for CPF Object.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CPFObject extends CPFNonFlowNode implements ICPFObject, Cloneable {

    private String originalId;
    private String netId;
    private String softType;
    private boolean configurable = false;

    private ObjectTypeEnum objectType;

    private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>(0);


    /**
     * @return a new instance of this class where parent and label are set to <code>null</code>.
     */
    public CPFObject() { }


    @Override
         public String getOriginalId() {
        return originalId;
    }

    @Override
    public void setOriginalId(String newOriginalId) {
        originalId = newOriginalId;
    }

    @Override
    public String getNetId() {
        return netId;
    }

    @Override
    public void setNetId(String newNetId) {
        netId = newNetId;
    }

    @Override
    public String getSoftType() {
        return softType;
    }

    @Override
    public void setSoftType(String newSoftType) {
        softType = newSoftType;
    }

    @Override
    public boolean isConfigurable() {
        return configurable;
    }

    @Override
    public void setConfigurable(boolean newConfigurable) {
        configurable = newConfigurable;
    }


    @Override
    public ObjectTypeEnum getObjectType() {
        return objectType;
    }

    @Override
    public void setObjectType(final ObjectTypeEnum newType) {
        objectType = newType;
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
    public CPFObject clone() {
        CPFObject clone;
        clone = (CPFObject) super.clone();
        if (this.getId() != null) {
            clone.setId(this.getId());
        }
        if (this.originalId != null) {
            clone.setOriginalId(this.originalId);
        }
        clone.setName(this.getName());
        clone.setConfigurable(this.configurable);

        clone.setObjectType(this.objectType);
        clone.setAttributes(this.attributes);

        return clone;
    }

    @Override
    public boolean canMerge(final ICPFObject toMergeObject) {
        return (toMergeObject.getName() != null && getName().equals(toMergeObject.getName()) &&
                toMergeObject.getObjectType() != null && getObjectType().equals(toMergeObject.getObjectType()));
    }

}
