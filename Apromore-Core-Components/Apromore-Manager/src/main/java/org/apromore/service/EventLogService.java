/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service;

import org.apromore.apmlog.APMLog;
import org.apromore.dao.model.*;
import org.apromore.exception.*;
import org.apromore.model.ExportLogResultType;
import org.apromore.model.SummariesType;
import org.apromore.util.StatType;
import org.deckfour.xes.model.XLog;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Interface for the Process Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface EventLogService {

    /**
     * Loads all the process Summaries. It will either get all or use the keywords parameter
     * to load a subset of the processes.
     * @param folderId the folder we are currently in.
     * @param searchExpression the search expression to limit the search.
     * @return The ProcessSummariesType used for Webservices.
     */
    SummariesType readLogSummaries(final Integer folderId, final String searchExpression);

    /**
     * Import a Process.
     *
     * @param username      The user doing the importing.
     * @param folderId      The folder we are saving the process in.
     * @param logName       the name of the process being imported.
     * @param domain        the domain of the model
     * @param created       the time created
     * @param publicModel   is this a public model?
     * @return the processSummaryType
     * @throws ImportException if the import process failed for any reason.
     *
     */
    Log importLog(String username, Integer folderId, String logName, InputStream log, String extension,
                  String domain, String created, boolean publicModel)
            throws Exception;

    ExportLogResultType exportLog(Integer logId)
            throws Exception;

    XLog getXLog(Integer logId);

    XLog getXLog(Integer logId, String factoryName);

    void deleteLogs(List<Log> logs, User user) throws Exception;

    void exportToStream(OutputStream outputStream, XLog log) throws Exception;

    void updateLogMetaData(Integer logId, String logName, boolean isPublic);

    boolean isPublicLog(Integer logId);

    /**
     * Get XLog and append statistics as log level metadata
     * @param logId
     */
    XLog getXLogWithStats(Integer logId);


    /**
     * Persist statistics of XLog into DB
     * @param map nested map that represent statistics
     * @param logId logID of the XLog
     */
    void storeStats(Map<String, Map<String, Integer>> map, Integer logId);

    /**
     * @param logId logID of the XLog
     * @return List of statistic entities
     */
    List<Statistic> getStats(Integer logId);


    /**
     * Persist statistics of XLog into DB by stat types
     * TODO: explain the format of input nested map
     *
     * @param map  {String statUID {[String stat_key, String stat_value]}}
     *             The statUID is a unique identifier that is associated with a set of statistics of the same type.
     *             For example, it can be the caseID which is used to identify a set of attributes of one case.
     *             {caseID, {[attrKey, attrValue] [attrKey, attrValue]}}
     * @param logId logID of XES log file
     * @param statType enum that store all the types of statistic
     * @throws IllegalArgumentException
     */
    void storeStatsByType(Map<String, Map<String, String>> map, Integer logId, StatType statType);

    /**
     * Check if this log has this type of statistic in the database.
     *
     * @param logId logID of XES log file
     * @param statType enum that store all the types of statistic
     * @return
     */
    boolean isStatsExists(Integer logId, StatType statType);

    /**
     * Get aggregated log.
     *
     * @param logId
     * @return The aggregated log placed into the cache, or generated on the fly if not found or expired
     */
    APMLog getAggregatedLog(Integer logId);
}
