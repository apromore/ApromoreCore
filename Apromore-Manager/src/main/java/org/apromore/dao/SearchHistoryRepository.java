package org.apromore.dao;

import org.apromore.dao.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object SearchHistory.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.SearchHistory
 */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {

}
