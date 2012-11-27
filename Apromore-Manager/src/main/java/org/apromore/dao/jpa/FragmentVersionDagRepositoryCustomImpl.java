package org.apromore.dao.jpa;

import org.apromore.dao.FragmentVersionDagRepositoryCustom;
import org.apromore.dao.model.FragmentVersionDag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * implementation of the org.apromore.dao.FragmentVersionDagDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class FragmentVersionDagRepositoryCustomImpl implements FragmentVersionDagRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.FragmentVersionDagRepository#getAllParentChildMappings()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<Integer>> getAllParentChildMappings() {
        Query query = em.createQuery("SELECT fvd FROM FragmentVersionDag fvd");
        List<FragmentVersionDag> mappings = (List<FragmentVersionDag>) query.getResultList();

        Map<Integer, List<Integer>> parentChildMap = new HashMap<Integer, List<Integer>>();
        for (FragmentVersionDag mapping : mappings) {
            Integer pid = mapping.getFragmentVersion().getId();
            Integer cid = mapping.getChildFragmentVersion().getId();
            if (parentChildMap.containsKey(pid)) {
                parentChildMap.get(pid).add(cid);
            } else {
                List<Integer> childIds = new ArrayList<Integer>();
                childIds.add(cid);
                parentChildMap.put(pid, childIds);
            }
        }
        return parentChildMap;
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagRepository#getAllChildParentMappings()
     *  {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<Integer>> getAllChildParentMappings() {
        Query query = em.createQuery("SELECT fvd FROM FragmentVersionDag fvd");
        List<FragmentVersionDag> mappings = (List<FragmentVersionDag>) query.getResultList();

        Map<Integer, List<Integer>> childParentMap = new HashMap<Integer, List<Integer>>();
        for (FragmentVersionDag mapping : mappings) {
            Integer pid = mapping.getFragmentVersion().getId();
            Integer cid = mapping.getChildFragmentVersion().getId();
            if (childParentMap.containsKey(cid)) {
                childParentMap.get(cid).add(pid);
            } else {
                List<Integer> parentIds = new ArrayList<Integer>();
                parentIds.add(pid);
                childParentMap.put(cid, parentIds);
            }
        }
        return childParentMap;
    }

}
