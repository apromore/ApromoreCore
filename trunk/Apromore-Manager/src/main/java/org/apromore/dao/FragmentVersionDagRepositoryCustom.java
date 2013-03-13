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

    /**
     * Returns all the child mappings for the FragmentId.
     * @param fragmentId the fragment id
     * @return the list of child fragments
     */
    List<FragmentVersionDagDO> getChildMappingsDO(Integer fragmentId);
}
