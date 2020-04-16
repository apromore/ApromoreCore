/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.dao;

import org.apromore.apmlog.APMLog;
import org.apromore.dao.model.Log;
import org.deckfour.xes.model.XLog;

import java.util.List;

/**
 * Interface domain model Data access object Process.
 * @author <a href="mailto:raffaele.conforti@unimelb.edu.au">Raffaele Conforti</a>
 * @version 1.0
 * @see Process
 */
public interface LogRepositoryCustom {

    /* ************************** JPA Methods here ******************************* */

    /**
     * Find all the Processes based on the following Condition. This could be done Using Specifications?
     * @param conditions the conditions
     * @return the list of processes.
     */
    List<Log> findAllLogs(final String conditions);

    /**
     * Find all the Processes based on the Conditions and folder. This could be done Using Specifications?
     * Ammendment: This says all processes in a folder. It shouldn't worry about public or not (or should it).
     * @param folderId the folder to search in.
     * @param conditions the conditions
     * @return the list of processes.
     */
    List<Log> findAllLogsByFolder(final Integer folderId, final String conditions);

    /* ************************** JDBC Template / native SQL Queries ******************************* */

    String storeProcessLog(final Integer folderId, String logName, XLog log, Integer userID, String domain, String created);

    void deleteProcessLog(Log log);

    /**
     * Load XES log file from cache, if not found then load from Event Logs Repository
     * @param log
     * @return
     */
    XLog getProcessLog(Log log, String factoryName);

    /**
     * Get aggregated log.
     *
     * @param log
     * @return The aggregated log placed into the cache, or generated on the fly if not found or expired
     */
    APMLog getAggregatedLog(Log log);

}
