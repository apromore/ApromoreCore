package org.apromore.dao;

import java.util.List;
import java.util.Map;

import org.apromore.dao.dataObject.FragmentVersionDagDO;

/**
 * Interface domain model Data access object FragmentVersionDag.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.FragmentVersionDag
 */
public interface FragmentVersionDagRepositoryCustom {


    /* ************************** JPA Methods here ******************************* */

    /**
     * Returns all parent-child mappings between all fragments
     * @return mappings fragment Id -> list of child Ids for all fragments that has at least one child
     */
    Map<Integer, List<Integer>> getAllParentChildMappings();

    /**
     * Returns all child-parent mappings between all fragments
     * @return mappings fragment Id -> list of parent Ids for all non-root fragments
     */
    Map<Integer, List<Integer>> getAllChildParentMappings();



    /* ************************** JDBC Template / native SQL Queries ******************************* */

    /**
     * Finds all the DAG entries by size.
     * @param minimumChildFragmentSize the min size we are interested in
     * @return the list of fragment Version DAG entries
     */
    List<FragmentVersionDagDO> getAllDAGEntriesBySize(int minimumChildFragmentSize);
}
