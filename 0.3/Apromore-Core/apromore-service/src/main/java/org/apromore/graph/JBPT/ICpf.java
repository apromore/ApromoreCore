package org.apromore.graph.JBPT;

import org.jbpt.pm.IControlFlow;
import org.jbpt.pm.IFlowNode;
import org.jbpt.pm.INonFlowNode;
import org.jbpt.pm.IProcessModel;

import java.util.Map;

/**
 * The Interface for the Canonical Format for the JBPT Implementation.
 * Currently there is no need for entires as this CPF is very similar to the PM implementation in JBPT.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICpf <CF extends IControlFlow<FN>, FN extends IFlowNode, NFN extends INonFlowNode> extends IProcessModel<CF, FN, NFN> {

    /**
     * Set the properties.
     * @param properties the properties
     */
    void setProperties(Map<String, String> properties);

    /**
     * return the properties
     * @return the map of properties
     */
    Map<String, String> getProperties();

    /**
     * return a property.
     * @param name the name of the property
     * @return the value of the property we are searching for.
     */
    String getProperty(String name);

    /**
     * sets a property.
     * @param name the name of the property
     * @param value the value of the property
     */
    void setProperty(String name, String value);

}
