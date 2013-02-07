package org.apromore.graph.canonical;

import java.util.Map;

/**
 * Interface class for {@link Object}
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICPFObjectReference extends org.jbpt.hypergraph.abs.IGObject {

    /**
     * Return this {@link org.apromore.graph.canonical.ICPFObjectReference} ObjectId.
     * @return the ObjectId of the CPFResource
     */
    String getObjectId();

    /**
     * Set if this {@link org.apromore.graph.canonical.ICPFObjectReference} ObjectId.
     * @param newObjectId the objectId
     */
    void setObjectId(String newObjectId);

    /**
     * Set if this {@link org.apromore.graph.canonical.ICPFObjectReference} is optional.
     * @param isOptional if optional or not
     */
    void setOptional(boolean isOptional);

    /**
     * Return this {@link org.apromore.graph.canonical.ICPFObjectReference} optional.
     * @return if optional or not
     */
    boolean isOptional();

    /**
     * Set if this {@link org.apromore.graph.canonical.ICPFObjectReference} is Consumed.
     * @param isConsumed if Consumed or not
     */
    void setConsumed(boolean isConsumed);

    /**
     * Return this {@link org.apromore.graph.canonical.ICPFObjectReference} Consumed.
     * @return if Consumed or not
     */
    boolean isConsumed();

    /**
     * Returns the Type, either input or output
     * @return the type of this object
     */
    ObjectRefTypeEnum getObjectRefType();

    /**
     * sets the type of this object. Should only be INPUT or OUTPUT
     * @param newType the new type
     */
    void setObjectRefType(final ObjectRefTypeEnum newType);


    /**
     * sets the attributes.
     * @param properties the attributes
     */
    void setAttributes(Map<String, IAttribute> properties);

    /**
     * return the attributes.
     * @return the map of attributes
     */
    Map<String, IAttribute> getAttributes();

    /**
     * return a attribute.
     * @param name the name of the attribute
     * @return the value of the attribute we are searching for.
     */
    IAttribute getAttribute(String name);

    /**
     * Sets a attribute.
     * @param name  the name of the attribute
     * @param value the simple value text value of the attribute
     * @param any the complex XML value of the attribute
     */
    void setAttribute(String name, String value, java.lang.Object any);

    /**
     * Sets a attribute only the simple text based value.
     * @param name  the name of the attribute
     * @param value the simple value text value of the attribute
     */
    void setAttribute(String name, String value);

}


