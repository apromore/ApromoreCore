/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Process;

/**
 * Interface domain model Data access object Process.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Process
 */
public interface ProcessRepositoryCustom  {

    /* ************************** JPA Methods here ******************************* */

    /**
     * Find all the Processes based on the following Condition. This could be done Using Specifications?
     * @param conditions the conditions
     * @return the list of processes.
     */
    List<Process> findAllProcesses(final String conditions);

    /**
     * Find all the Processes based on the Conditions and folder. This could be done Using Specifications?
     * Ammendment: This says all processes in a folder. It shouldn't worry about public or not (or should it).
     * @param folderId the folder to search in.
     * @param conditions the conditions
     * @return the list of processes.
     */
    List<Process> findAllProcessesByFolder(final Integer folderId, final String conditions);

    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
