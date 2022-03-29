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

package org.apromore.plugin.similaritysearch.logic;

import org.apromore.exception.ExceptionSearchForSimilar;
import org.apromore.portal.model.ParametersType;
import org.apromore.portal.model.SummariesType;

/**
 * Interface for the Similarity Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface SimilarityService {

    /**
     * Search for similar processes.
     * @param processId      the processId
     * @param versionName    the name of the version we are looking for
     * @param latestVersions are we looking at the latest version or all processes
     * @param folderId       what folder are we going to search in.
     * @param userId         the user running the search. make sure we only see his processes.
     * @param method         the search algorithm
     * @param params         the params used for the
     * @return the processSummaryTypes for the found models
     * @throws ExceptionSearchForSimilar
     */
    SummariesType searchForSimilarProcesses(Integer processId, String versionName, Boolean latestVersions, Integer folderId,
                                            String userId, String method, ParametersType params) throws ExceptionSearchForSimilar;


}
