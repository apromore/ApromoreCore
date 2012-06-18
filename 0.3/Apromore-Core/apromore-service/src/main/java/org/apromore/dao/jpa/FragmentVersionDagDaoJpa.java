package org.apromore.dao.jpa;

import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

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
     * @see org.apromore.dao.FragmentVersionDagDao#findFragmentVersionDag(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FragmentVersionDag findFragmentVersionDag(String vertexId) {
        return em.find(FragmentVersionDag.class, vertexId);
    }


    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getChildMappings(String)
     * {@inheritDoc}
     */
    @Override
    public List<FragmentVersionDag> getChildMappings(final String fragmentId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_MAPPINGS);
        query.setParameter("fragVersionId", fragmentId);
        return (List<FragmentVersionDag>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getChildFragmentsByFragmentVersion(String)
     * {@inheritDoc}
     */
    @Override
    public List<FragmentVersion> getChildFragmentsByFragmentVersion(final String fragmentVersionId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_FRAGMENTS_BY_FRAGMENT_VERSION);
        query.setParameter("fragVersionId", fragmentVersionId);
        return (List<FragmentVersion>) query.getResultList();
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
     * {@inheritDoc}
     */
    @Override
    public void delete(final FragmentVersionDag fragmentVersionDag) {
        em.remove(fragmentVersionDag);
    }



    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working
     * @param em the entitymanager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
