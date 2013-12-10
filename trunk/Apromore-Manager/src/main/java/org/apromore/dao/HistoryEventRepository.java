package org.apromore.dao;

import org.apromore.dao.model.HistoryEvent;
import org.apromore.dao.model.HistoryEnum;
import org.apromore.dao.model.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object HistoryEvent.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.HistoryEvent
 */
@Repository
public interface HistoryEventRepository extends JpaRepository<HistoryEvent, Integer> {

    /**
     * Finds all the HistoryEvent Events for a certain type and puts them in Descending order.
     * @param status the status
     * @param type the type of history Auditable
     * @return the list of history records it found.
     */
    List<HistoryEvent> findByStatusAndTypeOrderByOccurDateDesc(final StatusEnum status, final HistoryEnum type);


}
