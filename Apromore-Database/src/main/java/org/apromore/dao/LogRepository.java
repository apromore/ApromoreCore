/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import org.apromore.dao.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interface domain model Data access object Branch.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface LogRepository extends JpaRepository<Log, Integer>, LogRepositoryCustom {

    /**
     * Returns the distinct list of domains.
     * @return the list of domains.
     */
    @Query("SELECT DISTINCT l.domain FROM Log l ORDER by l.domain")
    List<String> getAllDomains();

    /**
     * Finds if a process with a particular name exists.
     * @param logName the process name
     * @return the process if one exists, null otherwise.
     */
    @Query("SELECT l FROM Log l WHERE l.name = ?1 AND l.folder is null")
    Log findUniqueByName(String logName);

    @Query("SELECT l FROM Log l WHERE l.name LIKE CONCAT(?1, '%')")
    List<Log> findWithPrefix(String prefix);

    /**
     * Finds if a process with a particular name in a particular folder exists.
     * @param logName the process name
     * @param folderId the folder id
     * @return the process if one exists, null otherwise.
     */
    Log findByNameAndFolderId(String logName, Integer folderId);

    /**
     * Finds processes within a folder which are in a group the user belongs to.
     * @param userRowGuid user id
     * @param pageable which page of results to produce
     * @return a page of processes
     */
    @Query("SELECT DISTINCT l FROM Log l JOIN l.groupLogs gl JOIN gl.group g1, " +
            "User u JOIN u.groups g2 " +
            "WHERE (l.folder is NULL) AND (u.rowGuid = ?1) AND (g1 = g2) ORDER BY l.id")
    Page<Log> findRootLogsByUser(String userRowGuid, Pageable pageable);

    /**
     * Finds processes within a folder which are in a group the user belongs to.
     * @param folderId the folder id
     * @param userRowGuid user id
     * @param pageable which page of results to produce
     * @return a page of processes
     */
    @Query("SELECT DISTINCT l FROM Log l JOIN l.folder f JOIN l.groupLogs gl JOIN gl.group g1, " +
            "User u JOIN u.groups g2 " +
            "WHERE (f.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2) ORDER BY l.id")
    Page<Log> findAllLogsInFolderForUser(Integer folderId, String userRowGuid, Pageable pageable);

    /**
     * Returns a Log
     * @param logId the id of the process log
     * @return the log
     */
    @Query("SELECT DISTINCT l FROM Log l WHERE l.id = ?1")
    Log findUniqueByID(Integer logId);
    
 
    Long countByStorageId(Long storageId);

    @Query("SELECT DISTINCT l FROM Log l WHERE l.folder.id in ?1")
    List<Log> findByFolderIdIn(List<Integer> folderIds);

//    /**
//     * Returns a list of processIds
//     * @param folderId the id of the folder
//     * @param name the log name
//     * @param log the log
//     * @return the list of LogId contained in the folder
//     */
//    void storeLog(Integer folderId, String name, XLog log);
//
//    /**
//     * Returns a list of processIds
//     * @param LogId the id of the process log
//     */
//    void removeLog(Integer LogId);
//
//    long count();
}
