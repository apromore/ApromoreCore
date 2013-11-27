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
    String getDescription();

    /**
     * Set the Description.
     * @param newDescription the new Description
     */
    void setDescription(String newDescription);

    /**
     * Get the Language.
     * @return the Language
     */
    String getLanguage();

    /**
     * Set the Language.
     * @param newLanguage the new Language
     */
    void setLanguage(String newLanguage);

    /**
     * Get the Expression.
     * @return the Expression
     */
    String getExpression();

    /**
     * Set the Expression.
     * @param newExpression the new Expression
     */
    void setExpression(String newExpression);

    /**
     * Get the ReturnType.
     * @return the ReturnType
     */
    String getReturnType();

    /**
     * Set the ReturnType.
     * @param newReturnType the new ReturnType
     */
    void setReturnType(String newReturnType);
}