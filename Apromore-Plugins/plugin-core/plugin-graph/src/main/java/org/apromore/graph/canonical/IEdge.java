package org.apromore.graph.canonical;

import org.jbpt.graph.abs.IDirectedEdge;

import java.util.Map;

/**
 * Interface to a flow relation of the Canonical format.
 * 
 * @author Cameron James
 */
public interface IEdge<N extends INode> extends IDirectedEdge<N> {

    /**
     * Sets the Original Id.
     * @param newOriginalId the new Original Id
     */
    void setOriginalId(String newOriginalId);

    /**
     * returns if this Node originalId.
     * @return The originalId
     */
    String getOriginalId();


    /**
     * Set if this node is default.
     * @param newDefault the config boolean
     */
    void setDefault(boolean newDefault);

    /**
     * returns if this Node is the default.
     * @return true or false
     */
    boolean isDefault();


    /**
     * Get the Condition Expression.
     * @return the expression.
     */
    CPFExpression getConditionExpr();

    /**
     * Sets the New Expression.
     * @param newConditionExpr the new expression
     */
    void setConditionExpr(CPFExpression newConditionExpr);


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
     * Returns the Attribute for the supplied name.
     * @param name the name
     * @return the attribute it founds.
     */
    IAttribute getAttribute(String name);


}