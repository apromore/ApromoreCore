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

import java.util.List;
import org.apromore.dao.model.SubprocessProcess;
import org.apromore.dao.model.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubprocessProcessRepository extends JpaRepository<SubprocessProcess, Integer> {
    /**
     * Gets the linked process if one exists.
     * @param parentProcessId the id of the process which contains the subprocess.
     * @param subprocessId the element id of the subprocess.
     * @return the linked subprocess one exists, null otherwise.
     */
    @Query("SELECT p.linkedProcess FROM SubprocessProcess p WHERE p.subprocessParent.id = ?1 AND p.subprocessId = ?2")
    Process getLinkedProcess(int parentProcessId, String subprocessId);

    @Query("SELECT p FROM SubprocessProcess p WHERE p.subprocessParent.id = ?1 AND p.subprocessId = ?2")
    SubprocessProcess getExistingLink(int parentProcessId, String subprocessId);

    /**
     * Gets a list of linked process in the process.
     * @param parentProcessId the id of the process which contains the subprocesses.
     * @return a list of linked process in the process.
     */
    @Query("SELECT DISTINCT p FROM SubprocessProcess p WHERE p.subprocessParent.id = ?1")
    List<SubprocessProcess> getLinkedSubProcesses(int parentProcessId);

}
