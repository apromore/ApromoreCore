package org.apromore.graph.JBPT;

import java.util.Map;

/**
 * Interface class for {@link CpfObject}
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICpfObject {

    /**
     * Return this {@link ICpfObject} Id.
     * @return the Id of the Resource
     */
    String getId();

    /**
     * Set if this {@link ICpfObject} id.
     * @param id if configurable or not
     */
    void setId(String id);

    /**
     * Return this {@link ICpfObject} name.
     * @return the name of the object
     */
    String getName();

    /**
     * Set if this {@link ICpfObject} newName.
     * @param newName the name
     */
    void setName(String newName);

    /**
     * Return this {@link ICpfObject} ObjectId.
     * @return the ObjectId of the Resource
     */
    String getObjectId();

    /**
     * Set if this {@link ICpfObject} ObjectId.
     * @param newObjectId the objectId
     */
    void setObjectId(String newObjectId);

    /**
     * Return the OriginalId.
     * @return the original Id
     */
    String getOriginalId();

    /**
     * Set the Original Id.
     * @param newOriginalId the original id
     */
    void setOriginalId(String newOriginalId);

    /**
     * Returns the Type, either input or output
     * @return the type of this object
     */
    String getType();

    /**
     * sets the type of this object. Should only be INPUT or OUTPUT
     * @param newType the new type
     */
    void setType(final String newType);

    /**
     * Set if this {@link ICpfObject} is optional.
     * @param config if optional or not
     */
    void setOptional(boolean isOptional);

    /**
     * Return this {@link ICpfObject} optional.
     * @return if optional or not
     */
    boolean getOptional();

    /**
     * Set if this {@link ICpfObject} is Consumed.
     * @param config if Consumed or not
     */
    void setConsumed(boolean isConsumed);

    /**
     * Return this {@link ICpfObject} Consumed.
     * @return if Consumed or not
     */
    boolean getConsumed();

    /**
     * is this resource configurable.
     * @return true or false.
     */
    boolean isConfigurable();

    /**
     * Sets id this resource is configurable.
     * @param newConfigurable the new configurable value.
     */
    void setConfigurable(boolean newConfigurable);

    /**
     * add an attribute to the {@link ICpfObject}.
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    void addAttribute(String name, String value);

    /**
     * Set if this {@link ICpfObject} attributes.
     * @param attributes the map of attributes
     */
    void setAttributes(Map<String, String> attributes);

    /**
     * Return this {@link ICpfObject} attributes.
     * @return the attributes
     */
    Map<String, String> getAttributes();


    /**
     * Set if this {@link ICpfObject} is type.
     * @param type the object type
     */
    void setObjectType(ObjectType type);

    /**
     * Return the {@link ICpfObject} object type.
     * @return the object type
     */
    ObjectType getObjectType();



    /**
     * Resource Type.
     */
    public enum ObjectType {
        HARD, SOFT
    }
}


