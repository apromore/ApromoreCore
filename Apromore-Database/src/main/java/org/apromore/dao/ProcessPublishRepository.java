/*-
 * #%L
 * This file is part of "Apromore Core".
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

import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessPublish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessPublishRepository extends JpaRepository<ProcessPublish, Integer> {
    /**
     * Finds if a process_publish entry for a particular process id exists.
     * @param processId the process id
     * @return the process_publish entry if one exists, null otherwise.
     */
    @Query("SELECT p FROM ProcessPublish p WHERE p.process.id = ?1")
    ProcessPublish findByProcessId(int processId);

    /**
     * Finds if a process_publish entry for a particular publish id exists.
     * @param publishId the process id
     * @return the process_publish entry if one exists, null otherwise.
     */
    @Query("SELECT p FROM ProcessPublish p WHERE p.publishId = ?1")
    ProcessPublish findByPublishId(String publishId);

    /**
     * Finds if a process with a particular publish id exists.
     * @param publishId the publish id
     * @return the related process if one exists, null otherwise.
     */
    @Query("SELECT p.process FROM ProcessPublish p WHERE p.publishId = ?1")
    Process findProcessByPublishId(String publishId);
}
