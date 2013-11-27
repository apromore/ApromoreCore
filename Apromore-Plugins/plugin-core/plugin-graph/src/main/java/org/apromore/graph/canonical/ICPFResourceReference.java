package org.apromore.graph.canonical;

import java.util.Map;

/**
 * Interface class for {@link org.apromore.graph.canonical.CPFResource}
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICPFResourceReference extends INonFlowNode {

    /**
     * Returns the Linked Resource Id.
     * @return the Resource Id.
     */
    String getResourceId();

    /**
     * Sets the Linked Resource Id.
     * @param newResourceId the new resource id
     */
    void setResourceId(String newResourceId);

    /**
     * Returns the qualifier.
     * @return the qualifier.
     */
    String getQualifier();

    /**
     * Sets the qualifier.
     * @param newQualifier the new qualifier.
     */
    void setQualifier(String newQualifier);


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


