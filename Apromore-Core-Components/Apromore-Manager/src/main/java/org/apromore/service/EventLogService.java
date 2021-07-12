/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.apromore.apmlog.APMLog;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.User;
import org.apromore.exception.ImportException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.SummariesType;
import org.apromore.storage.exception.ObjectCreationException;
import org.deckfour.xes.model.XLog;

/**
 * Interface for the Process Service. Defines all the methods that will do the majority of the work
 * for the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface EventLogService {

  /**
   * Loads all the process Summaries. It will either get all or use the keywords parameter to load a
   * subset of the processes.
   *
   * @param folderId the folder we are currently in.
   * @param searchExpression the search expression to limit the search.
   * @return The ProcessSummariesType used for Webservices.
   */
  SummariesType readLogSummaries(final Integer folderId, final String searchExpression);

  /**
   * Import a Process.
   *
   * @param username The user doing the importing.
   * @param folderId The folder we are saving the process in.
   * @param logName the name of the process being imported.
   * @param domain the domain of the model
   * @param created the time created
   * @param publicModel is this a public model?
   * @return the processSummaryType
   * @throws ImportException if the import process failed for any reason.
   */
  Log importLog(String username, Integer folderId, String logName, InputStream log,
      String extension, String domain, String created, boolean publicModel) throws Exception;


  Log importLog(Integer folderId, String logName, String domain, String created,
      boolean publicModel, User user, XLog xLog);

  /**
   * @param username a username
   * @param logId identifier for a log
   * @return whether the <var>user</var> should be allowed to update the log identified by
   *         <var>logId</var>
   */
  boolean canUserWriteLog(String username, Integer logId) throws UserNotFoundException;

  ExportLogResultType exportLog(Integer logId) throws Exception;

  void cloneLog(String username, Integer folderId, String logName, Integer sourcelogId,
      String domain, String created, boolean publicModel) throws Exception;

  XLog getXLog(Integer logId);

  XLog getXLog(Integer logId, String factoryName);

  void deleteLogs(List<Log> logs, User user) throws Exception;

  void exportToStream(OutputStream outputStream, XLog log) throws Exception;

  void updateLogMetaData(Integer logId, String logName, boolean isPublic);

  boolean isPublicLog(Integer logId);

  /**
   * Get aggregated log.
   *
   * @param logId logId of Log
   * @return The aggregated log placed into the cache, or generated on the fly if not found or
   *         expired
   */
  APMLog getAggregatedLog(Integer logId);


  /**
   * Update specified CustomCalendar that linked to specified Log
   *
   * @param logId logId of Log
   * @param calenderId calenderId of CustomCalendar
   */
  void updateCalendarForLog(Integer logId, Long calenderId);

  /**
   * Get specified Log by logId
   *
   * @param logId logId of Log
   * @return CalendarId
   */
  Long getCalendarIdFromLog(Integer logId);

  /**
   * Get the CustomCalendar that linked to specified Log
   *
   * @param logId logId of Log
   * @return CustomCalendar
   */
  CalendarModel getCalendarFromLog(Integer logId);

  /**
   * Save the file to the given path
   *
   * @param filename
   * @param volumePath
   * @param prefix
   * @param baos
   * @return
   * @throws IOException
   * @throws ObjectCreationException
   */
  boolean saveFileToVolume(String filename, String volumePath, String prefix,
                           ByteArrayOutputStream baos) throws IOException, ObjectCreationException;
}
