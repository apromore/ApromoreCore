package org.apromore.dao.jpa;

import org.apromore.dao.AnnotationDao;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Canonical;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Hibernate implementation of the org.apromore.dao.AnnotationDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository(value = "AnnotationDao")
@Transactional(propagation = Propagation.REQUIRED)
public class AnnotationDaoJpa extends JpaTemplate implements AnnotationDao {


    /**
     * Returns list of annotations.
     * @see org.apromore.dao.AnnotationDao#findByUri(int)
     * {@inheritDoc}
     */
    @Override
    public List<Annotation> findByUri(final int nativeUri) {
        return execute(new JpaCallback<List<Annotation>>() {

            @SuppressWarnings("unchecked")
            public List<Annotation> doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(Annotation.FIND_BY_URI);
                query.setParameter("uri", nativeUri);
                return query.getResultList();
            }
        });
    }


    /**
     * Remove the annotation.
     * @see org.apromore.dao.AnnotationDao#delete(Annotation)
     * {@inheritDoc}
     */
    @Override
    public void save(Annotation annotation) {
        persist(annotation);
    }

    /**
     * Remove the annotation.
     * @see org.apromore.dao.AnnotationDao#delete(Annotation)
     * {@inheritDoc}
     */
    @Override
    public void update(Annotation annotation) {
        merge(annotation);
    }

    /**
     * Remove the annotation.
     * @see org.apromore.dao.AnnotationDao#delete(Annotation)
     * {@inheritDoc}
     */
    @Override
    public void delete(Annotation annotation) {
         remove(annotation);
    }
}
