package org.apromore.dao;

import org.apromore.dao.model.NonPocketNode;

/**
 * Interface domain model Data access object NonPocketNode.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.NonPocketNode
 */
public interface NonPocketNodeDao {

    /**
     * Save the nonPocketNode.
     * @param nonPocketNode the nonPocketNode to persist
     */
    void save(NonPocketNode nonPocketNode);

    /**
     * Update the nonPocketNode.
     * @param nonPocketNode the nonPocketNode to update
     */
    NonPocketNode update(NonPocketNode nonPocketNode);

    /**
     * Remove the nonPocketNode.
     * @param nonPocketNode the nonPocketNode to remove
     */
    void delete(NonPocketNode nonPocketNode);

}
