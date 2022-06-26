/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.service.helper;

import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.model.*;
import org.apromore.util.AccessType;

/**
 * UI Helper Interface. Kinda a hack, need to re-look at this.
 * @author <a href="mailto:cam.james@gmail.com>Cameron James</a>
 */
public interface UserInterfaceHelper {

    /**
     * Create a Process Summary record for the Front UI display.
     * @param process       the process
     * @param branch        the process branch
     * @param pmv           the process model version
     * @param nativeType    the native type of this model
     * @param domain        The domain of this model
     * @param created       the Date create
     * @param lastUpdate    the Date Last Updated
     * @param username      the user who updated the
     * @param isPublic      Is this model public.
     * @return the created Process Summary
     */
    ProcessSummaryType createProcessSummary(Process process, ProcessBranch branch, ProcessModelVersion pmv, String nativeType,
        String domain, String created, String lastUpdate, String username, boolean isPublic);


    /**
     * Builds the list of process Summaries and kicks off the versions and annotations.
     * @param folderId the search conditions
     * @param conditions the search conditions for process models
     * @param logConditions the search conditions for logs
     * @param folderConditions the search conditions for folders
     * @return the list of process Summaries
     */
    SummariesType buildProcessSummaryList(Integer folderId, String userRowGuid, String conditions, String logConditions, String folderConditions, boolean global);

    /**
     * Builds the list of process Summaries and kicks off the versions and annotations.
     * @param userId the search conditions
     * @param folderId the search conditions
     * @param similarProcesses something
     * @return the list of process Summaries
     */
    SummariesType buildProcessSummaryList(String userId, Integer folderId, ProcessVersionsType similarProcesses);

    /**
     * Build a page out of the list of process summaries.
     *
     * @param userId the search conditions
     * @param folderId the search conditions
     * @param pageIndex the index into the sequence of pages of results
     * @param pageSize the desired size of the page of results
     * @return the list of process Summaries on the requested page
     */
    SummariesType buildProcessSummaryList(String userId, Integer folderId, Integer pageIndex, Integer pageSize);

    SummaryType buildLogSummary(Log log);

    SummariesType buildLogSummaryList(String userId, Integer folderId, Integer pageIndex, Integer pageSize);

    UserMetadataSummaryType buildUserMetadataSummary(String userId, Usermetadata usermetadata,
                                                     AccessType accessType) throws UserNotFoundException;

    /**
     * Populate a process summary.
     *
     * @param process
     * @return the populated process summary
     */
    ProcessSummaryType buildProcessSummary(final Process process);
}
