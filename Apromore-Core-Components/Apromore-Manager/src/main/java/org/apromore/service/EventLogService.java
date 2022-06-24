/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.service;

import org.apromore.apmlog.APMLog;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.exception.EventLogException;
import org.apromore.exception.UserMetadataException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.SummariesType;
import org.apromore.storage.exception.ObjectCreationException;
import org.deckfour.xes.model.XLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
   * @param username    The user doing the importing.
   * @param folderId    The folder we are saving the process in.
   * @param logName     the name of the process being imported.
   * @param log         InputStream of event log.
   * @param extension   the extension of stored log.
   * @param domain      the domain of the model
   * @param created     the time created
   * @param publicModel is this a public model?
   * @param perspective whether generate default perspective for this log
   * @return the processSummaryType
   * @throws Exception if the import process failed for any reason.
   */
  Log importLog(String username, Integer folderId, String logName, InputStream log,
      String extension, String domain, String created, boolean publicModel, boolean perspective) throws Exception;


  /**
   * Import a filtered log that's derived from original log
   *
   * @param username    The user doing the importing.
   * @param folderId    The folder we are saving the process in.
   * @param logName     the name of the process being imported.
   * @param extension   the extension of stored log.
   * @param domain      the domain of the model
   * @param created     the time created
   * @param publicModel is this a public model?
   * @param perspective whether generate default perspective for this log
   * @param sourceLogId The original log's id
   * @return the processSummaryType
   * @throws Exception if the import process failed for any reason
   */
  Log importFilteredLog(String username, Integer folderId, String logName, InputStream inputStreamLog,
                        String extension, String domain, String created, boolean publicModel,
                        boolean perspective, Integer sourceLogId) throws Exception;

  Log importLog(Integer folderId, String logName, String domain, String created,
                boolean publicModel, User user, XLog xLog);

  /**
   * @param username a username
   * @param logId identifier for a log
   * @return whether the <var>user</var> should be allowed to update the log identified by
   *         <var>logId</var>
   */
  boolean canUserWriteLog(String username, Integer logId) throws UserNotFoundException;

  /**
   * @param username a username
   * @param logId identifier for a log
   * @return whether the <var>user</var> should be allowed to read the log identified by
   *         <var>logId</var>
   * @throws UserNotFoundException if the user can't be found
   */
  boolean canUserReadLog(String username, Integer logId) throws UserNotFoundException;

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
   * @param prefix
   * @param baos
   * @return
   * @throws IOException
   * @throws ObjectCreationException
   */
  boolean saveFileToVolume(String filename, String prefix,
                           ByteArrayOutputStream baos) throws Exception;

  ConfigBean getConfigBean();

  List<Log> getLogListFromCalendarId(Long calendarId);

  /**
   * Find logs associated with a calendar and owned by a user.
   * @param calendarId calendar id.
   * @param username username of the log owner.
   * @return A list of logs owned by the user with the calendar applied.
   */
  List<Log> getLogListFromCalendarId(Long calendarId, String username);

  /**
   * Find perspective tag that are linked to the specified Log
   * @param logId Log Id
   * @return Perspective tags
   * @throws UserMetadataException when perspective tag is not found or is not valid
   */
  List<String> getPerspectiveTagByLog(Integer logId) throws UserMetadataException;

  /**
   * Save Perspective as user metadata that's associated with specified Log
   *
   * @param perspectives Perspective List
   * @param logId        logId
   * @param username     username
   * @return Perspective user metadata
   * @throws UserMetadataException if could not serialize given perspective list
   * @throws UserNotFoundException if could not find specified username
   */
  Usermetadata savePerspectiveByLog(List<String> perspectives, Integer logId, String username) throws UserMetadataException,
          UserNotFoundException;

  /**
   * @param costTables Cost tables info that associated with specified log
   * @param logId      log id
   * @param username   username
   * @return Cost tables user metadata
   * @throws UserNotFoundException If could not find specified username
   */
  Usermetadata saveCostTablesByLog(String costTables, Integer logId, String username) throws UserNotFoundException;

  /**
   * Find cost tables that are linked to the specified Log
   *
   * @param logId Log id
   * @return Json string of associated cost tables
   */
  String getCostTablesByLog(Integer logId);

  /**
   * Get perspective tag from specified event log.
   * Add default perspectives (concept:name, and org:resource if any) for existing logs
   *
   * @param logId logId
   * @return A list of perspectives
   * @throws EventLogException
   */
  List<String> getDefaultPerspectiveFromLog(Integer logId) throws EventLogException;

  boolean hasWritePermissionOnLog(User user, List<Integer> logIds);

  /**
   * Get All CustomCalendars.
    */
  List<CalendarModel> getAllCustomCalendars();

  /**
   * Get All CustomCalendars owned by a user.
   */
  List<CalendarModel> getAllCustomCalendars(String username);

  /**
   * Shallow copy associated artifacts from one Log to another
   *
   * @param oldLog        From Log
   * @param newLog        To Log
   * @param artifactTypes List of types of artifact defined in {@link org.apromore.util.UserMetadataTypeEnum}
   */
  void shallowCopyArtifacts(Log oldLog, Log newLog, List<Integer> artifactTypes);

  /**
   * Deep copy associated artifacts from one Log to another
   *
   * @param oldLog        From Log
   * @param newLog        To Log
   * @param artifactTypes List of types of artifact defined in {@link org.apromore.util.UserMetadataTypeEnum}
   * @param username      username
   * @throws UserNotFoundException
   */
  void deepCopyArtifacts(Log oldLog, Log newLog, List<Integer> artifactTypes, String username) throws UserNotFoundException;

  /**
   * Find Log by its ID
   *
   * @param logId Log ID
   * @return Log
   */
  Log findLogById(Integer logId);
}
