/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
