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

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object GroupFolder.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Repository
public interface GroupFolderRepository extends JpaRepository<GroupFolder, Integer> {

    /**
     * Find the Group and Folder combination.
     *
     * @param group the group that has access
     * @param folder the folder we are looking in
     * @return the permissions for that user in the FolderUser.
     */
    GroupFolder findByGroupAndFolder(final Group group, final Folder folder);

    /**
     * @param folderId
     * @return all groups containing the folder identified by <var>folderId</var>
     */
    @Query("SELECT gf FROM GroupFolder gf WHERE (gf.folder.id = ?1)")
    List<GroupFolder> findByFolderId(final Integer folderId);

    /**
     * @param groupId Id of Group
     * @return all groups containing the group identified by <var>groupId</var>
     */
    @Query("SELECT gf FROM GroupFolder gf WHERE (gf.group.id = ?1)")
    List<GroupFolder> findByGroupId(final Integer groupId);

    /**
     * Returns a list of Folder Users for the folder and user combination.
     *
     * @param parentFolderId the parent folder Id
     * @param userGuid the Users Row Globally unique Id
     * @return the list of found records
     */
    @Query("SELECT gf FROM GroupFolder gf JOIN gf.folder f JOIN gf.group g1 LEFT JOIN f.parentFolder f1, " +
           "               User u JOIN u.groups g2 " +
           "WHERE ((?1 = 0 AND f1 IS NULL) OR (f1.id = ?1)) AND (u.rowGuid = ?2) AND (g1 = g2) order by f.name asc")
    List<GroupFolder> findByParentFolderAndUser(final Integer parentFolderId, final String userGuid);

    /**
     * Find the permissions a user has for a process.
     */
    @Query("SELECT gf FROM GroupFolder gf JOIN gf.group g1, " +
           "               User u JOIN u.groups g2 " +
           "WHERE (gf.folder.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2)")
    List<GroupFolder> findByFolderAndUser(final Integer folderId, final String userRowGuid);

    /**
     * Return a list of GroupFolder that are OWNER of specified folder.
     */
    @Query("SELECT gf FROM GroupFolder gf WHERE (gf.folder.id = ?1) AND (gf.accessRights.ownerShip = 1)")
    List<GroupFolder> findOwnerByFolderId(final Integer folderId);

}
