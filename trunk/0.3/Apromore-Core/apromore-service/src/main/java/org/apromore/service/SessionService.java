package org.apromore.service;

import org.apromore.dao.model.EditSession;
import org.apromore.model.EditSessionType;

/**
 * Interface for the Session Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface SessionService {

    /**
     * Reads the session.
     * @param sessionCode the session id.
     * @return the found session.
     */
    EditSession readSession(int sessionCode);


    /**
     * Creates a session object in the DB.
     * @param editSession the details from the Portal.
     * @return the new session id that will be used.
     */
    int createSession(EditSessionType editSession);

    /**
     * Removes a session from the Repository.
     * @param sessionCode the session id
     */
    void deleteSession(int sessionCode);
}
