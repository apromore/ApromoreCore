package org.apromore.dao.jpa;

import org.apromore.dao.SearchHistoryDao;
import org.apromore.dao.model.SearchHistory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Hibernate implementation of the org.apromore.dao.SearchHistoryDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class SearchHistoryDaoJpa implements SearchHistoryDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * Save a Search History.
     * @see org.apromore.dao.SearchHistoryDao#save(org.apromore.dao.model.SearchHistory)
     * {@inheritDoc}
     */
    @Override
    public void save(final SearchHistory searchHistory) {
        em.persist(searchHistory);
    }

    /**
     * Update a Search History.
     * @see org.apromore.dao.SearchHistoryDao#update(org.apromore.dao.model.SearchHistory)
     * {@inheritDoc}
     */
    @Override
    public SearchHistory update(final SearchHistory searchHistory) {
        return em.merge(searchHistory);
    }

    /**
     * Remove the Search History.
     * @see org.apromore.dao.SearchHistoryDao#delete(org.apromore.dao.model.SearchHistory)
     * {@inheritDoc}
     */
    @Override
    public void delete(final SearchHistory searchHistory) {
        em.remove(searchHistory);
    }




    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working
     * @param em the entitymanager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
