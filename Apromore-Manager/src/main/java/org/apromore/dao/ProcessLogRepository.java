/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.dao;

import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.deckfour.xes.model.XLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object Branch.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see ProcessBranch
 */
public interface ProcessLogRepository {

    /**
     * Returns a processLog
     * @param processLogId the id of the process log
     * @return the log
     */
    XLog findUniqueByID(Integer processLogId);

    /**
     * Returns a processLog
     * @param processLogId the id of the process log
     * @return the log
     */
    String findLogNameByID(Integer processLogId);

    /**
     * Returns a list of processIds
     * @param folderId the id of the folder
     * @return the list of processLogId contained in the folder
     */
    List<Integer> findFolderId(Integer folderId);

    /**
     * Returns a list of processIds
     * @param folderId the id of the folder
     * @param name the log name
     * @param log the log
     * @return the list of processLogId contained in the folder
     */
    void storeProcessLog(Integer folderId, String name, XLog log);

    /**
     * Returns a list of processIds
     * @param processLogId the id of the process log
     */
    void removeProcessLog(Integer processLogId);

    long count();
}
