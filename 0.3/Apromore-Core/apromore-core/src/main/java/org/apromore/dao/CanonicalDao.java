package org.apromore.dao;

import org.apromore.dao.model.Canonical;

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
    List<Canonical> findByProcessId(final int processId);



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
