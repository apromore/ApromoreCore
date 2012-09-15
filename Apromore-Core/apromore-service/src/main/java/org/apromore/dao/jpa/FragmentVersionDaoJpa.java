package org.apromore.dao.jpa;

import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.FragmentVersion;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hibernate implementation of the org.apromore.dao.FragmentVersionDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class FragmentVersionDaoJpa implements FragmentVersionDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.FragmentVersionDao#findFragmentVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FragmentVersion findFragmentVersion(final Integer fragmentId) {
        return em.find(FragmentVersion.class, fragmentId);
    }


    /**
     * @see org.apromore.dao.FragmentVersionDao#getAllFragmentVersion()
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getAllFragmentVersion() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_FRAGMENT_VERSION);
        return query.getResultList();
    }


    /**
     * @see org.apromore.dao.FragmentVersionDao#findFragmentVersionByURI(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FragmentVersion findFragmentVersionByURI(String uri) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENT_BY_URI);
        query.setParameter("uri", uri);
        List result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return (FragmentVersion) result.get(0);
        }
    }


    /**
     * @see org.apromore.dao.FragmentVersionDao#getMatchingFragmentVersionId(Integer, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public FragmentVersion getMatchingFragmentVersionId(final Integer contentId, final String childMappingCode) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENT_BY_CONTENT_MAPPING);
        query.setParameter("contentId", contentId);
        query.setParameter("mappingCode", childMappingCode);
        List<FragmentVersion> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getUsedProcessModels(Integer)
     *  {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Integer getUsedProcessModels(final Integer fvid) {
        Query query = em.createNamedQuery(NamedQueries.GET_USED_PROCESS_MODEL_FOR_FRAGMENT);
        query.setParameter("fragVersionId", fvid);
        List<Integer> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getParentFragments(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getParentFragments(final Integer fvid) {
        Query query = em.createNamedQuery(NamedQueries.GET_PARENT_FRAGMENT_VERSIONS);
        query.setParameter("fragVersionId", fvid);
        return (List<FragmentVersion>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getLockedParentFragmentIds(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getLockedParentFragmentIds(final Integer fvid) {
        Query query = em.createNamedQuery(NamedQueries.GET_LOCKED_PARENT_FRAGMENTS);
        query.setParameter("childFragVersionId", fvid);
        return (List<Integer>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getLockedParentFragmentIds(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getLockedParentFragmentIdsByURI(final String uri) {
        Query query = em.createNamedQuery(NamedQueries.GET_LOCKED_PARENT_FRAGMENTS_BY_URI);
        query.setParameter("uri", uri);
        return (List<String>) query.getResultList();
    }

//        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_FRAGMENTS_WITH_SIZE);
//        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_FRAGMENTS_WITH_TYPE);

    /**
     * @see org.apromore.dao.FragmentVersionDao#getAllFragmentIdsWithSize()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> getAllFragmentIdsWithSize() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_FRAGMENTS_WITH_SIZE);
        List<Object[]> fragmentSizes = query.getResultList();

        Map<Integer, Integer> fsizeMap = new HashMap<Integer, Integer>();
        for (Object[] fsize : fragmentSizes) {
            fsizeMap.put((Integer) fsize[0], (Integer) fsize[1]);
        }
        return fsizeMap;
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getContentId(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Integer getContentId(final Integer fvid) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENT_VERSION);
        query.setParameter("id", fvid);
        List<FragmentVersion> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0).getContent().getId();
        }
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getFragmentDataOfProcessModel(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getFragmentDataOfProcessModel(final Integer pmvid) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENT_DATA_OF_PROCESS_MODEL);
        query.setParameter("procModelId", pmvid);
        return (List<FragmentVersion>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getFragmentData(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public FragmentVersion getFragmentData(final Integer fragmentId) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENT_DATA);
        query.setParameter("fragVersionId", fragmentId);
        List<FragmentVersion> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getContainingFragments(java.util.List)
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getContainingFragments(final List<Integer> nodes) {
        // TODO: Implement as it is used...or should be
        return null;
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getContainingFragmentsByURI(java.util.List)
     * {@inheritDoc}
     */
    @Override
    public List<String> getContainingFragmentsByURI(final List<String> nodes) {
        // TODO: Implement as it is used...or should be
        return null;
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getContainedProcessModels(Integer)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getContainedProcessModels(final Integer fragmentId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CONTAINED_PROCESS_MODEL);
        query.setParameter("fragVersionId", fragmentId);
        return (List<Integer>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getUsedFragmentIds(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getUsedFragmentIds(final Integer matchingContentId) {
        Query query = em.createNamedQuery(NamedQueries.GET_USED_FRAGMENT_IDS);
        query.setParameter("contentId", matchingContentId);
        return (List<Integer>) query.getResultList();
    }


    /**
     * @see org.apromore.dao.FragmentVersionDao#getSimilarFragmentsBySize(int, int)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getSimilarFragmentsBySize(final int minSize, final int maxSize) {
        Query query = em.createNamedQuery(NamedQueries.GET_SIMILAR_FRAGMENTS_BY_SIZE);
        query.setParameter("min", minSize);
        query.setParameter("max", maxSize);
        return (List<FragmentVersion>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#getSimilarFragmentsBySizeAndType(int, int, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getSimilarFragmentsBySizeAndType(final int minSize, final int maxSize, final String type) {
        Query query = em.createNamedQuery(NamedQueries.GET_SIMILAR_FRAGMENTS_BY_SIZE_AND_TYPE);
        query.setParameter("min", minSize);
        query.setParameter("max", maxSize);
        query.setParameter("type", type);
        return (List<FragmentVersion>) query.getResultList();
    }


    /**
     * @see org.apromore.dao.FragmentVersionDao#save(org.apromore.dao.model.FragmentVersion)
     * {@inheritDoc}
     */
    @Override
    public void save(final FragmentVersion fragVersion) {
        em.persist(fragVersion);
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#update(org.apromore.dao.model.FragmentVersion)
     * {@inheritDoc}
     */
    @Override
    public FragmentVersion update(final FragmentVersion fragVersion) {
        return em.merge(fragVersion);
    }

    /**
     * @see org.apromore.dao.FragmentVersionDao#delete(org.apromore.dao.model.FragmentVersion)
     * {@inheritDoc}
     */
    @Override
    public void delete(final FragmentVersion fragVersion) {
        em.remove(fragVersion);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
