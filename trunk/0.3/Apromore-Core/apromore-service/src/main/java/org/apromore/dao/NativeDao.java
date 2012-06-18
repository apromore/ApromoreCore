package org.apromore.dao;

import org.apromore.dao.model.Native;
import org.apromore.exception.NativeFormatNotFoundException;

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
    public List<Native> findNativeByCanonical(final Integer processId, final String versionName);

    /**
     * Get the Canonical format. this is just a string but contains the xml Canonical Format.
     * @param processId the processId of the Canonical format.
     * @param version the version of the canonical format
     * @param nativeType the native type (XPDL, BPMN)
     * @return the XML as a string
     * @throws NativeFormatNotFoundException if the record can not be found.
     */
    Native getNative(final Integer processId, final String version, final String nativeType) throws NativeFormatNotFoundException;


    /**
     * Save the Native.
     * @param natve the Native to persist
     */
    void save(Native natve);

    /**
     * Update the Native.
     * @param natve the Native to update
     */
    Native update(Native natve);

    /**
     * Remove the Native.
     * @param natve the Native to remove
     */
    void delete(Native natve);

}
