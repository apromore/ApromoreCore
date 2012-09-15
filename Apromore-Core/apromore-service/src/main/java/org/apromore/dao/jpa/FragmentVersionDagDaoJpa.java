package org.apromore.dao.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.FragmentVersionDagDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class FragmentVersionDagDaoJpa implements FragmentVersionDagDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.FragmentVersionDagDao#findFragmentVersionDag(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FragmentVersionDag findFragmentVersionDag(final Integer vertexId) {
        return em.find(FragmentVersionDag.class, vertexId);
    }


    /**
     * @see org.apromore.dao.FragmentVersionDagDao#findFragmentVersionDagByURI(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FragmentVersionDag findFragmentVersionDagByURI(String uri) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENT_VERSION_DAG_BY_URI);
        query.setParameter("uri", uri);
        List result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return (FragmentVersionDag) result.get(0);
        }
    }


    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getChildMappings(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersionDag> getChildMappings(final Integer fragmentId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_MAPPINGS);
        query.setParameter("fragVersionId", fragmentId);
        return (List<FragmentVersionDag>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getChildMappingsByURI(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersionDag> getChildMappingsByURI(final String fragmentUri) {
        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_MAPPINGS_BY_URI);
        query.setParameter("uri", fragmentUri);
        return (List<FragmentVersionDag>) query.getResultList();
    }


    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getAllParentChildMappings()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<Integer>> getAllParentChildMappings() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_PARENT_CHILD_MAPPINGS);
        List<FragmentVersionDag> mappings = (List<FragmentVersionDag>) query.getResultList();

        Map<Integer, List<Integer>> parentChildMap = new HashMap<Integer, List<Integer>>();
        for (FragmentVersionDag mapping : mappings) {
            Integer pid = mapping.getFragmentVersionId().getId();
            Integer cid = mapping.getChildFragmentVersionId().getId();
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
     * @see org.apromore.dao.FragmentVersionDagDao#getAllChildParentMappings()
     *  {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<Integer>> getAllChildParentMappings() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_PARENT_CHILD_MAPPINGS);
        List<FragmentVersionDag> mappings = (List<FragmentVersionDag>) query.getResultList();

        Map<Integer, List<Integer>> childParentMap = new HashMap<Integer, List<Integer>>();
        for (FragmentVersionDag mapping : mappings) {
            Integer pid = mapping.getFragmentVersionId().getId();
            Integer cid = mapping.getChildFragmentVersionId().getId();
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

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getChildFragmentsByFragmentVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getChildFragmentsByFragmentVersion(final Integer fragmentVersionId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_FRAGMENTS_BY_FRAGMENT_VERSION);
        query.setParameter("fragVersionId", fragmentVersionId);
        return (List<FragmentVersion>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getAllDAGEntries(int)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersionDag> getAllDAGEntries(int minimumChildFragmentSize) {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_DAGS_WITH_SIZE);
        query.setParameter("minSize", minimumChildFragmentSize);
        return (List<FragmentVersionDag>) query.getResultList();
    }



    /**
     * @see org.apromore.dao.FragmentVersionDagDao#save(org.apromore.dao.model.FragmentVersionDag)
     * {@inheritDoc}
     */
    @Override
    public void save(final FragmentVersionDag fragmentVersionDag) {
        em.persist(fragmentVersionDag);
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#update(org.apromore.dao.model.FragmentVersionDag)
     * {@inheritDoc}
     */
    @Override
    public FragmentVersionDag update(final FragmentVersionDag fragmentVersionDag) {
        return em.merge(fragmentVersionDag);
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#delete(org.apromore.dao.model.FragmentVersionDag)
     *  {@inheritDoc}
     */
    @Override
    public void delete(final FragmentVersionDag fragmentVersionDag) {
        em.remove(fragmentVersionDag);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
