package org.apromore.dao.jpa;

import org.apromore.dao.SearchHistoryDao;
import org.apromore.dao.UserDao;
import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Hibernate implementation of the org.apromore.dao.SearchHistoryDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository(value = "SearchHistoryDao")
@Transactional(propagation = Propagation.REQUIRED)
public class SearchHistoryDaoJpa extends JpaTemplate implements SearchHistoryDao {

    /**
     * Save a Search History.
     * @see org.apromore.dao.SearchHistoryDao#save(org.apromore.dao.model.SearchHistory)
     * {@inheritDoc}
     */
    @Override
    public final void save(SearchHistory searchHistory) {
        persist(searchHistory);
    }

    /**
     * Update a Search History.
     * @see org.apromore.dao.SearchHistoryDao#update(org.apromore.dao.model.SearchHistory)
     * {@inheritDoc}
     */
    @Override
    public final void update(SearchHistory searchHistory) {
        merge(searchHistory);
    }

    /**
     * Remove the Search History.
     * @see org.apromore.dao.SearchHistoryDao#delete(org.apromore.dao.model.SearchHistory)
     * {@inheritDoc}
     */
    @Override
    public void delete(SearchHistory searchHistory) {
        remove(searchHistory);
    }

}
