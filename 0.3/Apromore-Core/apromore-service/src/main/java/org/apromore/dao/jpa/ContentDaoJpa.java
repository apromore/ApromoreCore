package org.apromore.dao.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.ContentDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Content;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.ContentDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ContentDaoJpa implements ContentDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.ContentDao#findContent(String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Content findContent(String contentId) {
        return em.find(Content.class, contentId);
    }


    /**
     * @see org.apromore.dao.ContentDao#getContentByFragmentVersion(String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Content getContentByFragmentVersion(final String fragVersionId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CONTENT_BY_FRAGMENT_VERSION);
        query.setParameter("fragVersion", fragVersionId);
        List<Content> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * @see org.apromore.dao.ContentDao#getContentByCode(String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Content getContentByCode(final String code) {
        Query query = em.createNamedQuery(NamedQueries.GET_CONTENT_BY_HASH);
        query.setParameter("code", code);
        List<Content> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }


    /**
     * @see org.apromore.dao.ContentDao#save(org.apromore.dao.model.Content)
     *      {@inheritDoc}
     */
    @Override
    public void save(final Content content) {
        em.persist(content);
    }

    /**
     * @see org.apromore.dao.ContentDao#update(org.apromore.dao.model.Content)
     *      {@inheritDoc}
     */
    @Override
    public Content update(final Content content) {
        return em.merge(content);
    }

    /**
     * @see org.apromore.dao.ContentDao#delete(org.apromore.dao.model.Content)
     *      {@inheritDoc}
     */
    @Override
    public void delete(final Content content) {
        em.remove(content);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working
     *
     * @param em the entitymanager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
