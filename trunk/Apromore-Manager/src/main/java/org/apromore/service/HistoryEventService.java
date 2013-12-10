package org.apromore.service;

import org.apromore.dao.model.HistoryEvent;
import org.apromore.dao.model.HistoryEnum;
import org.apromore.dao.model.StatusEnum;

/**
 * Interface for the Clustering Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface HistoryEventService {

    /**
     * Adds a new HistoryEvent Auditable to the DB.
     * @param status the status start, end, or error
     * @param historyType the event Type
     * @return the newly created history record.
     */
    HistoryEvent addNewEvent(final StatusEnum status, final HistoryEnum historyType);

    /**
     * Returns the latest HistoryEvent Auditable for a particuler type.
     * @param historyType the type of history.
     * @return the HistoryEvent record or null
     */
    HistoryEvent findLatestHistoryEventType(final HistoryEnum historyType);
}
