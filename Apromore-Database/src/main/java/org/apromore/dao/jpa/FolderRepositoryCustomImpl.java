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

package org.apromore.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.FolderRepositoryCustom;
import org.apromore.dao.model.Folder;



/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class FolderRepositoryCustomImpl implements FolderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private static final String GET_ALL_FOLDERS_CORE_JPA = "SELECT f FROM GroupFolder gf JOIN gf.folder f JOIN gf.group g, User u JOIN u.groups g2 WHERE (g = g2)";
   
    public List<Folder> findSubfolders(
        final int parentFolderId,
        final String userId,
        final String conditions,
        final boolean global
    ) {
        StringBuilder strQry = new StringBuilder(0);
        String userCondition = "(u.rowGuid = :userRowGuid)   AND (gf.accessRights.readOnly = TRUE)";
        if (global) {
            strQry.append(GET_ALL_FOLDERS_CORE_JPA);
        } else if (parentFolderId == 0) {
            strQry.append(GET_ALL_FOLDERS_CORE_JPA);
            strQry.append(" AND f.parentFolder IS NULL");
        } else {
            strQry.append(
                "SELECT f FROM GroupFolder gf JOIN gf.folder f JOIN gf.group g JOIN f.parentFolder fp, User"
                    + " u JOIN u.groups g2 WHERE (g = g2) AND fp.id = ").append(parentFolderId);
        }
        if (userId != null && !userId.isEmpty()) {
            strQry.append(" AND ").append(userCondition);
        }
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(" AND ").append(conditions);
        }
        strQry.append(" ORDER by f.id");
        Query query = em.createQuery(strQry.toString());
        if (userId != null && !userId.isEmpty()) {
            query.setParameter("userRowGuid", userId);
        }
        return query.getResultList();
    }


   
}
