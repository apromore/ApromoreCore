package org.apromore.dao;

import org.apromore.dao.model.SearchHistory;

/**
 * Interface domain model Data access object SearchHistory.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.SearchHistory
 */
public interface SearchHistoryDao {

    /**
     * Save the searchHistory.
     *
     * @param searchHistory the searchHistory to persist
     */
    void save(SearchHistory searchHistory);

    /**
     * Update the searchHistory.
     *
     * @param searchHistory the searchHistory to update
     */
    SearchHistory update(SearchHistory searchHistory);

    /**
     * Remove the searchHistory.
     *
     * @param searchHistory the searchHistory to remove
     */
    void delete(SearchHistory searchHistory);
}
