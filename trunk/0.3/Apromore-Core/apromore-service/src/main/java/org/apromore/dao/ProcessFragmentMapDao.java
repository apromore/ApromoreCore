package org.apromore.dao;

import org.apromore.dao.model.ProcessFragmentMap;

/**
 * Interface domain model Data access object ProcessFragmentMap.
 *
 * @see org.apromore.dao.model.ProcessFragmentMap
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface ProcessFragmentMapDao {

    /**
     * Save the ProcessFragmentMap.
     * @param processFragmentMap the ProcessFragmentMap to persist
     */
    void save(ProcessFragmentMap processFragmentMap);

    /**
     * Update the ProcessFragmentMap.
     * @param processFragmentMap the ProcessFragmentMap to update
     */
    ProcessFragmentMap update(ProcessFragmentMap processFragmentMap);

    /**
     * Remove the ProcessFragmentMap.
     * @param processFragmentMap the ProcessFragmentMap to remove
     */
    void delete(ProcessFragmentMap processFragmentMap);

}
