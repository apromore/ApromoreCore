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

import org.apromore.dao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUsermetadataRepository extends JpaRepository<GroupUsermetadata, Integer> {

    /**
     * Find a row by its natural primary key (group and process).
     *
     * @param group Group
     * @param usermetadata Usermetadata
     * @return the identified process
     */
    GroupUsermetadata findByGroupAndUsermetadata(final Group group, final Usermetadata usermetadata);

    List<GroupUsermetadata> findByGroup(final Group group);

    /**
     * @param usermetadataId Usermetadata id
     * @return all groups containing the Usermetadata identified by <var>UsermetadataID</var>
     */
    @Query("SELECT gp FROM GroupUsermetadata gp WHERE (gp.usermetadata.id = ?1)")
    List<GroupUsermetadata> findByUsermetadataId(final Integer usermetadataId);

//    /**
//     * Search for user metadata to which a particular user has access
//     *
//     * @param userRowGuid the rowGuid of a user
//     * @return processes to which the user has access
//     */
//    @Query("SELECT gp FROM GroupUsermetadata gp JOIN gp.log p JOIN gp.group g1, " +
//            "               User u JOIN u.groups g2 " +
//            "WHERE (p.folder IS NULL) AND (u.rowGuid = ?1) AND (g1 = g2)")
//    List<GroupLog> findLogsByUser(String userRowGuid);
//
//    /**
//     * Finds all the Processes in a Folder for a User
//     *
//     * @param folderId The folder we are looking in
//     * @param userRowGuid the user we are looking for
//     * @return the list of processUser records
//     */
//    @Query("SELECT gp FROM GroupLog gp JOIN gp.log p JOIN p.folder f JOIN gp.group g1, " +
//            "               User u JOIN u.groups g2 " +
//            "WHERE (f.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2)")
//    List<GroupLog> findAllLogsInFolderForUser(final Integer folderId, final String userRowGuid);

    /**
     * Find the permissions a user has for a user metadata.
     */
    @Query("SELECT gp FROM GroupUsermetadata gp JOIN gp.group g1, " +
            "               User u JOIN u.groups g2 " +
            "WHERE (gp.usermetadata.id = ?1) AND (u.rowGuid = ?2) AND (g1 = g2)")
    List<GroupUsermetadata> findByLogAndUser(final Integer usermetadataId, final String userRowGuid);
}
