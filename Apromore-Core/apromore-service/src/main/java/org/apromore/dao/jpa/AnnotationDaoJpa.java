package org.apromore.dao.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.AnnotationDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Annotation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.AnnotationDao interface.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class AnnotationDaoJpa implements AnnotationDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.AnnotationDao#findByUri(Integer)
     * {@inheritDoc}
     */
    @Override
    public List<Annotation> findByUri(final Integer nativeUri) {
        Query query = em.createNamedQuery(NamedQueries.GET_ANNOTATION_BY_URI);
        query.setParameter("uri", nativeUri);
        return query.getResultList();
    }


    /**
     * Returns the Annotation as XML.
     * @see org.apromore.dao.AnnotationDao#getAnnotation(Integer, String, String)
     * {@inheritDoc}
     */
    @Override
    public Annotation getAnnotation(final Integer processId, final String version, final String name)
            throws NoResultException, NonUniqueResultException {
        Query query = em.createNamedQuery(NamedQueries.GET_ANNOTATION);
        query.setParameter("processId", processId);
        query.setParameter("versionName", version);
        query.setParameter("name", name);
        return (Annotation) query.getSingleResult();
    }


    /**
     * @see org.apromore.dao.AnnotationDao#delete(Annotation)
     * {@inheritDoc}
     */
    @Override
    public void save(final Annotation annotation) {
        em.persist(annotation);
    }

    /**
     * @see org.apromore.dao.AnnotationDao#delete(Annotation)
     * {@inheritDoc}
     */
    @Override
    public Annotation update(final Annotation annotation) {
        return em.merge(annotation);
    }

    /**
     * @see org.apromore.dao.AnnotationDao#delete(Annotation)
     * {@inheritDoc}
     */
    @Override
    public void delete(final Annotation annotation) {
        em.remove(annotation);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
