package org.apromore.graph.canonical;

import java.util.Map;

/**
 * Interface class for {@link Object}
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface IObject {

    /**
     * Return this {@link IObject} Id.
     * @return the Id of the Resource
     */
    String getId();

    /**
     * Set if this {@link IObject} id.
     * @param id if configurable or not
     */
    void setId(String id);

    /**
     * Return this {@link IObject} name.
     * @return the name of the object
     */
    String getName();

    /**
     * Set if this {@link IObject} newName.
     * @param newName the name
     */
    void setName(String newName);

    /**
     * Return this {@link IObject} ObjectId.
     * @return the ObjectId of the Resource
     */
    String getObjectId();

    /**
     * Set if this {@link IObject} ObjectId.
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
     * Set if this {@link IObject} is optional.
     * @param isOptional if optional or not
     */
    void setOptional(boolean isOptional);

    /**
     * Return this {@link IObject} optional.
     * @return if optional or not
     */
    boolean getOptional();

    /**
     * Set if this {@link IObject} is Consumed.
     * @param isConsumed if Consumed or not
     */
    void setConsumed(boolean isConsumed);

    /**
     * Return this {@link IObject} Consumed.
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
     * Set if this {@link IObject} is type.
     * @param type the object type
     */
    void setObjectType(ObjectTypeEnum type);

    /**
     * Return the {@link IObject} object type.
     * @return the object type
     */
    ObjectTypeEnum getObjectType();



    /**
     * Resource Type.
     */
    public enum ObjectTypeEnum {
        HARD, SOFT
    }
}


