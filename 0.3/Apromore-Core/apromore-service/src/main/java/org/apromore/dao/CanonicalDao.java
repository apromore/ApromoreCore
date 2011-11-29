package org.apromore.dao;

import org.apromore.dao.model.Canonical;
import org.apromore.exception.CanonicalFormatNotFoundException;

import java.util.List;

/**
 * Interface domain model Data access object Canonical.
 *
 * @see org.apromore.dao.model.Canonical
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface CanonicalDao {


    /**
     * Find the canoncial record by it's process id.
     * @return the canonical process, a list of them for all the different versions.
     */
    List<Canonical> findByProcessId(final long processId);

    /**
     * Get the Canonical format. this is just a string but contains the xml Canonical Format.
     * @param processId the processId of the Canonical format.
     * @param version the version of the canonical format
     * @return the XML as a string
     * @throws org.apromore.exception.CanonicalFormatNotFoundException if the process model can not be found.
     */
    Canonical getCanonical(final long processId, final String version) throws CanonicalFormatNotFoundException;



    /**
     * Save the canonical.
     * @param canonical the canonical to persist
     */
    void save(Canonical canonical);

    /**
     * Update the canonical.
     * @param canonical the canonical to update
     */
    void update(Canonical canonical);

    /**
     * Remove the canonical.
     * @param canonical the canonical to remove
     */
    void delete(Canonical canonical);

}
