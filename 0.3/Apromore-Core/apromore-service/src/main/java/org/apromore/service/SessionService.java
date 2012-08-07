package org.apromore.service;

import org.apromore.model.EditSessionType;

/**
 * Interface for the Session Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface SessionService {

    /**
     * Creates a session object in the DB
     * @param editSession the details from the Portal.
     * @return the new session id that willl be used.
     */
    int createSession(EditSessionType editSession);

}
