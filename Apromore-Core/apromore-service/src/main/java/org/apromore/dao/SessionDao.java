package org.apromore.dao;

import org.apromore.dao.model.EditSession;
import org.apromore.dao.model.ProcessBranch;

/**
 * Interface domain model Data access object Session.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.EditSession
 */
public interface SessionDao {


    /**
     * Returns a single Edit Session based on the primary Key.
     * @param sessionId the EditSession Id
     * @return the found EditSession
     */
    EditSession findSession(Integer sessionId);



    /**
     * Save the session.
     * @param session the session to persist
     */
    void save(EditSession session);

    /**
     * Update the session.
     * @param session the edge to update
     * @return the updated object.
     */
    EditSession update(EditSession session);

    /**
     * Remove the session.
     * @param session the session to remove
     */
    void delete(EditSession session);

}
