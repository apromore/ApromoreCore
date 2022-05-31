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

package org.apromore.dao;

import java.util.Collection;
import java.util.List;

import org.apromore.dao.model.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Process.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Process
 */
@Repository
public interface ProcessRepository extends JpaRepository<Process, Integer>, ProcessRepositoryCustom {

    /**
     * Returns the distinct list of domains.
     * @return the list of domains.
     */
    @Query("SELECT DISTINCT p.domain FROM Process p ORDER by p.domain")
    List<String> getAllDomains();
    /**
     * Finds if a process with a particular name exists.
     * @param processName the process name
     * @return the process if one exists, null otherwise.
     */
    @Query("SELECT p FROM Process p WHERE p.name = ?1 AND p.folder is null")
    Process findUniqueByName(String processName);

    @Query("SELECT p FROM Process p WHERE p.name LIKE CONCAT(?1, '%')")
    List<Process> findWithPrefix(String prefix);

    /**
     * Finds if a process with a particular name in a particular folder exists.
     * @param processName the process name
     * @param folderId the folder id
     * @return the process if one exists, null otherwise.
     */
    Process findByNameAndFolderId(String processName, Integer folderId);

    /**
     * Finds processes within a folder which are in a group the user belongs to.
     * @param folderId the folder id
     * @param userRowGuid user id
     * @param pageable which page of results to produce
     * @return a page of processes
     */
    @Query("SELECT DISTINCT p FROM Process p JOIN p.groupProcesses gp JOIN gp.group g1, " +
           "User u JOIN u.groups g2 " +
           "WHERE (p.folder is NULL) AND (u.rowGuid = ?1) AND (g1 = g2) ORDER BY p.id")
    Page<Process> findRootProcessesByUser(String userRowGuid, Pageable pageable);

    /**
     * Finds processes within a folder.
     * @param pageable which page of results to produce
     * @return a page of processes
     */
    @Query("SELECT DISTINCT p FROM Process p  " +
         "WHERE (p.folder is NULL) ORDER BY p.id")
    Page<Process> findRootProcesses(Pageable pageable);

    /**
     * Finds processes within a folder which are in a group the user belongs to.
     * @param folderId the folder id
     * @param userRowGuid user id
     * @param pageable which page of results to produce
     * @return a page of processes
     */
    @Query("SELECT DISTINCT p FROM Process p JOIN p.folder f JOIN p.groupProcesses gp JOIN gp.group g1, " +
           "User u JOIN u.groups g2 " +
           "WHERE (f.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2) ORDER BY p.id")
    Page<Process> findAllProcessesInFolderForUser(Integer folderId, String userRowGuid, Pageable pageable);

    /**
     * Finds processes within a folder which are in a group the user belongs to.
     * @param folderId the folder id
     * @param pageable which page of results to produce
     * @return a page of processes
     */
    @Query("SELECT DISTINCT p FROM Process p JOIN p.folder f  " +
        "WHERE (f.id = ?1) ORDER BY p.id")
    Page<Process> findAllProcessesInFolder(Integer folderId, Pageable pageable);

    /**
     * Returns a Process
     * @param processId the id of the process
     * @return the process
     */
    @Query("SELECT DISTINCT p FROM Process p WHERE p.id = ?1")
    Process findUniqueByID(Integer processId);

    @Query("SELECT DISTINCT p FROM Process p WHERE p.folder.id in ?1")
    List<Process> findByFolderIdIn(Collection<Integer> folderIds);
}
