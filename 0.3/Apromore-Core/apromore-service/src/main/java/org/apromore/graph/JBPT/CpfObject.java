package org.apromore.graph.JBPT;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for CPF Object.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CpfObject implements ICpfObject, Cloneable {

    private String id;
    private String name;
    private String objectId;
    private String originalId;
    private String type;
    private boolean optional = false;
    private boolean consumed = false;
    private boolean configurable = false;

    private ObjectType objType;
    private Map<String, String> attributes = new HashMap<String, String>(0);

    /**
     * @return a new instance of this class where parent and label are set to <code>null</code>.
     */
    public CpfObject() { }

    /**
     * @param newId of this {@link org.jbpt.pm.CpfObject}
     * @return a new instance of this class where parent is set to the given one and label is set to <code>null</code>.
     */
    public CpfObject(String newId) {
        this.id = newId;
    }



    @Override
    public CpfObject clone() throws CloneNotSupportedException {
        CpfObject clone = null;
        try {
            clone = (CpfObject) super.clone();
        } catch (CloneNotSupportedException e) {
            return clone;
        }
        if (this.id != null) {
            clone.setId(this.id);
        }
        if (this.objectId != null) {
            clone.setId(this.objectId);
        }
        clone.setOptional(this.optional);
        clone.setConsumed(this.consumed);

        return clone;
    }



    /**
     * Return this {@link org.apromore.graph.JBPT.ICpfObject} id.
     * @return the id of the Object
     */
    @Override
    public void setId(String newId) {
        id = newId;
    }

    /**
     * Set if this {@link org.apromore.graph.JBPT.ICpfObject} Id.
     * @param Id the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Set if this {@link org.apromore.graph.JBPT.ICpfObject} Name.
     * @param name the name
     */
    public String getName() {
        return name;
    }

    /**
     * Return this {@link org.apromore.graph.JBPT.ICpfObject} name.
     * @return the name of the Object
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Return this {@link org.apromore.graph.JBPT.ICpfObject} ObjectId.
     * @return the ObjectId of the Resource
     */
    @Override
    public String getObjectId() {
        return objectId;
    }

    /**
     * Set if this {@link org.apromore.graph.JBPT.ICpfObject} ObjectId.
     * @param newObjectId the objectId
     */
    @Override
    public void setObjectId(String newObjectId) {
        objectId = newObjectId;
    }

    @Override
    public String getOriginalId() {
        return originalId;
    }

    @Override
    public void setOriginalId(String newOriginalId) {
        originalId = newOriginalId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(final String newType) {
        type = newType;
    }

    @Override
    public boolean getOptional() {
        return optional;
    }

    @Override
    public void setOptional(final boolean isOptional) {
        optional = isOptional;
    }

    @Override
    public boolean getConsumed() {
        return consumed;
    }

    @Override
    public void setConsumed(boolean isConsumed) {
        consumed = isConsumed;
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
    public ObjectType getObjectType() {
        return objType;
    }

    @Override
    public void setObjectType(final ObjectType newType) {
        objType = newType;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(final Map<String, String> newAttributes) {
        attributes = newAttributes;
    }

    @Override
    public void addAttribute(final String name, final String value) {
        attributes.put(name, value);
    }
}
