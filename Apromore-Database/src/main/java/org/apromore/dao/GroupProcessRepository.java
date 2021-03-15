/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@link org.apromore.dao.model.Group}/{@link org.apromore.dao.model.Process}  instance pairs.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Repository
public interface GroupProcessRepository extends JpaRepository<GroupProcess, Integer> {

    /**
     * Find a row by its natural primary key (group and process).
     *
     * @param group
     * @param process
     * @return the identified process
     */
    GroupProcess findByGroupAndProcess(final Group group, final Process process);

    /**
     * @param processId
     * @return all groups containing the process identified by <var>processId</var>
     */
    @Query("SELECT gp FROM GroupProcess gp WHERE (gp.process.id = ?1)")
    List<GroupProcess> findByProcessId(final Integer processId);

    /**
     * @param groupId Id of Group
     * @return all groups containing the group identified by <var>groupId</var>
     */
    @Query("SELECT gp FROM GroupProcess gp WHERE (gp.group.id = ?1)")
    List<GroupProcess> findByGroupId(final Integer groupId);

    /**
     * Search for processes to which a particular user has access
     *
     * @param userRowGuid the rowGuid of a user
     * @return processes to which the user has access
     */
    @Query("SELECT gp FROM GroupProcess gp JOIN gp.process p JOIN gp.group g1, " +
           "               User u JOIN u.groups g2 " +
           "WHERE (p.folder IS NULL) AND (u.rowGuid = ?1) AND (g1 = g2)")
    List<GroupProcess> findRootProcessesByUser(String userRowGuid);

    /**
     * Finds all the Processes in a Folder for a User
     *
     * @param folderId The folder we are looking in
     * @param userRowGuid the user we are looking for
     * @return the list of processUser records
     */
    @Query("SELECT gp FROM GroupProcess gp JOIN gp.process p JOIN p.folder f JOIN gp.group g1, " +
           "               User u JOIN u.groups g2 " +
           "WHERE (f.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2)")
    List<GroupProcess> findAllProcessesInFolderForUser(final Integer folderId, final String userRowGuid);

    /**
     * Find the permissions a user has for a process.
     */
    @Query("SELECT gp FROM GroupProcess gp JOIN gp.group g1, " +
           "               User u JOIN u.groups g2 " +
           "WHERE (gp.process.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2)")
    List<GroupProcess> findByProcessAndUser(final Integer processId, final String userRowGuid);

    /**
     * Return a list of GroupProcess that are OWNER of specified process.
     */
    @Query("SELECT gp FROM GroupProcess gp WHERE (gp.process.id = ?1) AND (gp.accessRights.ownerShip = 1)")
    List<GroupProcess> findOwnerByProcessId(final Integer processId);
}
