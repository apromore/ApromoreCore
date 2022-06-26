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

package org.apromore.dao;


import org.apromore.dao.model.Log;

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
    List<Log> findAllLogsByFolder(final Integer folderId, final String userRowGuid, final String conditions, boolean global);

    /**
     * Find one Log based on LogId
     * @param logId the id of Log
     * @return One Log
     */
    Log getLogReference(Integer logId);

}
