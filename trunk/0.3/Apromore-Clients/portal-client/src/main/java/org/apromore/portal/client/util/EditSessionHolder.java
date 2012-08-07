package org.apromore.portal.client.util;

import java.util.HashMap;
import java.util.Map;

import org.apromore.portal.model.EditSessionType;

/**
 * The session holder for the client. So the client can communicate to the server and know what models we are processing.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public final class EditSessionHolder {

    private static Map<Integer, EditSessionType> editSessions;

    /** Private Constructor. */
    private EditSessionHolder() { }


    /**
     * gets a session code from the map.
     * @param sessionCode the session code to get.
     * @return the session type.
     */
    public static EditSessionType getEditSession(final Integer sessionCode) {
        if (editSessions == null) {
            init();
        }
        return editSessions.get(sessionCode);
    }

    /**
     * Adds a new session id to the map.
     * @param sessionCode the new session code
     * @param editSessionType the type of session code.
     */
    public static void addEditSession(final Integer sessionCode, final EditSessionType editSessionType) {
        if (editSessions == null) {
            init();
        }
        editSessions.put(sessionCode, editSessionType);
    }

    /**
     * removes the session id from the map.
     * @param sessionCode the session code to remove
     */
    public static void removeEditSession(final Integer sessionCode) {
        throw new UnsupportedOperationException("this method is not implemented yet. [portal should know when oryx is getting closed]");
    }


    /* initializes the holder. */
    private static void init() {
        editSessions = new HashMap<Integer, EditSessionType>();
    }

}
