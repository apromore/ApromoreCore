/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */

package org.apromore.plugin.merge.logic;

import org.apromore.exception.ExceptionMergeProcess;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdsType;

/**
 * Interface for the Merge Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface MergeService {

    /**
     * Merge multiple Processes and Save the Result.
     *
     * @param processName the new process name
     * @param version     the version name
     * @param domain      the domain
     * @param username    the username that did this merge
     * @param algo        the algorithm used
     * @param folderId    The folder we are going to store the new model in.
     * @param parameters  the algorithm photos
     * @param ids         ID'd of the processes to merge.
     * @param makePublic  do we make this new model public?
     * @return the new process summary of the newly merged process
     * @throws ExceptionMergeProcess if the merge failed
     */
    ProcessSummaryType mergeProcesses(String processName, String version, String domain, String username, String algo, Integer folderId,
                                      ParametersType parameters, ProcessVersionIdsType ids, boolean makePublic) throws ExceptionMergeProcess;


}