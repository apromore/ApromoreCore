package org.apromore.dao;

import org.apromore.dao.model.Native;

import java.util.List;

/**
 * Interface domain model Data access object Native.
 *
 * @see org.apromore.dao.model.Native
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface NativeDao {

    /**
     * Find the annotation record by it's native uri id.
     * @param processId the processId
     * @param versionName the version name
     * @return the native, a list of them for all the different canonical versions.
     */
    public List<Native> findNativeByCanonical(final int processId, final String versionName);


    /**
     * Save the Native.
     * @param natve the Native to persist
     */
    void save(Native natve);

    /**
     * Update the Native.
     * @param natve the Native to update
     */
    void update(Native natve);

    /**
     * Remove the Native.
     * @param natve the Native to remove
     */
    void delete(Native natve);

}
