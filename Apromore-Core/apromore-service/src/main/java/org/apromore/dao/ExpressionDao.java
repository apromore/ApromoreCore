package org.apromore.dao;

import org.apromore.dao.model.Expression;

/**
 * Interface domain model Data access object Expression.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Expression
 */
public interface ExpressionDao {

     /**
     * Save the Expression.
     * @param expression the Expression to persist
     */
    void save(Expression expression);

    /**
     * Update the Expression.
     * @param expression the Expression to update
     * @return the updated object.
     */
    Expression update(Expression expression);

    /**
     * Remove the Expression.
     * @param expression the edge to remove
     */
    void delete(Expression expression);

}
