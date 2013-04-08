package org.apromore.dao;

import java.util.List;

import org.apromore.dao.dataObject.FragmentVersionDO;

/**
 * Interface domain model Data access object FragmentVersion.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.FragmentVersion
 */
public interface FragmentVersionRepositoryCustom {

    /* ************************** JPA Methods here ******************************* */


    /* ************************** JDBC Template / native SQL Queries ******************************* */

    /**
     * Finds all the similar fragments by there size.
     * @param minSize the min size we are looking for
     * @param maxSize the max size we are looking for
     * @return the list of found fragment versions
     */
    List<FragmentVersionDO> getAllSimilarFragmentsBySize(int minSize, int maxSize);
}
