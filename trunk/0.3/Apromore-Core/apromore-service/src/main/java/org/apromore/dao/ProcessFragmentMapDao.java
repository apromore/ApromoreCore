package org.apromore.dao;

import org.apromore.dao.model.ProcessFragmentMap;

/**
 * Interface domain model Data access object ProcessFragmentMap.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.ProcessFragmentMap
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
     * @return the merged object.
     */
    ProcessFragmentMap update(ProcessFragmentMap processFragmentMap);

    /**
     * Remove the ProcessFragmentMap.
     * @param processFragmentMap the ProcessFragmentMap to remove
     */
    void delete(ProcessFragmentMap processFragmentMap);

}
