/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.dao;

import org.apromore.dao.model.Log;
import org.deckfour.xes.model.XLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Interface domain model Data access object Branch.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface ProcessLogRepository extends JpaRepository<Log, Integer>, ProcessLogRepositoryCustom {

    /**
     * Returns the distinct list of domains.
     * @return the list of domains.
     */
    @Query("SELECT DISTINCT p.domain FROM Process_Log p ORDER by p.domain")
    List<String> getAllDomains();
    /**
     * Finds if a process with a particular name exists.
     * @param processName the process name
     * @return the process if one exists, null otherwise.
     */
    @Query("SELECT p FROM Process_Log p WHERE p.name = ?1 AND p.folder is null")
    Process findUniqueByName(String processName);

    /**
     * Finds if a process with a particular name in a particular folder exists.
     * @param processName the process name
     * @param folderId the folder id
     * @return the process if one exists, null otherwise.
     */
    Process findByNameAndFolderId(String processName, Integer folderId);

    /**
     * Finds processes within a folder which are in a group the user belongs to.
     * @param userRowGuid user id
     * @param pageable which page of results to produce
     * @return a page of processes
     */
    @Query("SELECT DISTINCT p FROM Process_Log p JOIN p.groupProcesses gp JOIN gp.group g1, " +
            "User u JOIN u.groups g2 " +
            "WHERE (p.folder is NULL) AND (u.rowGuid = ?1) AND (g1 = g2) ORDER BY p.id")
    Page<Process> findRootProcessesByUser(String userRowGuid, Pageable pageable);

    /**
     * Finds processes within a folder which are in a group the user belongs to.
     * @param folderId the folder id
     * @param userRowGuid user id
     * @param pageable which page of results to produce
     * @return a page of processes
     */
    @Query("SELECT DISTINCT p FROM Process_Log p JOIN p.folder f JOIN p.groupProcesses gp JOIN gp.group g1, " +
            "User u JOIN u.groups g2 " +
            "WHERE (f.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2) ORDER BY p.id")
    Page<Process> findAllProcessesInFolderForUser(Integer folderId, String userRowGuid, Pageable pageable);

    /**
     * Returns a processLog
     * @param processLogId the id of the process log
     * @return the log
     */
    @Query("SELECT DISTINCT p FROM Process_Log p WHERE p.id = ?1")
    XLog findUniqueByID(Integer processLogId);

    /**
     * Returns a list of processIds
     * @param folderId the id of the folder
     * @param name the log name
     * @param log the log
     * @return the list of processLogId contained in the folder
     */
    void storeProcessLog(Integer folderId, String name, XLog log);

    /**
     * Returns a list of processIds
     * @param processLogId the id of the process log
     */
    void removeProcessLog(Integer processLogId);

    long count();
}
