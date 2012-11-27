package org.apromore.graph.canonical;

/**
 * Interface to a flow relation of the Canonical format.
 * 
 * @author Cameron James
 */
public interface IExpression {

    /**
     * Get the Description.
     * @return the description
     */
    public String getDescription();

    /**
     * Set the Description.
     * @param newDescription the new Description
     */
    public void setDescription(String newDescription);

    /**
     * Get the Language.
     * @return the Language
     */
    public String getLanguage();

    /**
     * Set the Language.
     * @param newLanguage the new Language
     */
    public void setLanguage(String newLanguage);

    /**
     * Get the Expression.
     * @return the Expression
     */
    public String getExpression();

    /**
     * Set the Expression.
     * @param newExpression the new Expression
     */
    public void setExpression(String newExpression);

    /**
     * Get the ReturnType.
     * @return the ReturnType
     */
    public String getReturnType();

    /**
     * Set the ReturnType.
     * @param newReturnType the new ReturnType
     */
    public void setReturnType(String newReturnType);
}