package org.apromore.graph.JBPT;

import java.util.Map;

import org.jbpt.pm.IControlFlow;
import org.jbpt.pm.IFlowNode;
import org.jbpt.pm.INonFlowNode;
import org.jbpt.pm.IProcessModel;

/**
 * The Interface for the Canonical Format for the JBPT Implementation.
 * Currently there is no need for entires as this CPF is very similar to the PM implementation in JBPT.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICpf<CF extends IControlFlow<FN>, FN extends IFlowNode, NFN extends INonFlowNode> extends IProcessModel<CF, FN, NFN> {

    /**
     * Set the properties.
     *
     * @param properties the properties
     */
    void setProperties(Map<String, ICpfAttribute> properties);

    /**
     * return the properties
     *
     * @return the map of properties
     */
    Map<String, ICpfAttribute> getProperties();

    /**
     * return a property.
     *
     * @param name the name of the property
     * @return the value of the property we are searching for.
     */
    ICpfAttribute getProperty(String name);

    /**
     * Sets a property.
     *
     * @param name  the name of the property
     * @param value the simple value text value of the property
     * @param any the complex XML value of the property
     */
    void setProperty(String name, String value, Object any);

    /**
     * Sets a property only the simple text based value.
     *
     * @param name  the name of the property
     * @param value the simple value text value of the property
     */
    void setProperty(String name, String value);    
 
}
