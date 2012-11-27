package org.apromore.graph.canonical;

/**
 * Expression data used by some of the nodes to store extra info.
 *
 * @author <a href="mailto:cam.james@gmail.com>Cameron James</a>
 */
public class CPFExpression implements IExpression {

    private String description;
    private String language;
    private String expression;
    private String returnType;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String newDescription) {
        description = newDescription;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String newLanguage) {
        language = newLanguage;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String newExpression) {
        expression = newExpression;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }

    @Override
    public void setReturnType(String newReturnType) {
        returnType = newReturnType;
    }
}
