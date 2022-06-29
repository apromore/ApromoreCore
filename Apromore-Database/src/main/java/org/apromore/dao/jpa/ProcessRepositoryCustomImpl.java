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

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

import org.apromore.dao.ProcessRepositoryCustom;
import static org.apromore.dao.model.Group.Type.PUBLIC;
import org.apromore.dao.model.Process;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class ProcessRepositoryCustomImpl implements ProcessRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_PROCESSES_JPA = "SELECT p FROM GroupProcess gp JOIN gp.process p JOIN gp.group g WHERE g.type = :public ";
    private static final String GET_ALL_PROCESSES_CORE_JPA = "SELECT p FROM GroupProcess gp JOIN gp.process p JOIN gp.group g, User u JOIN u.groups g2 WHERE (g = g2)";
    private static final String GET_ALL_PROCESSES_FOLDER_JPA = "SELECT p FROM GroupProcess gp JOIN gp.process p JOIN gp.group g JOIN p.folder f, User u JOIN u.groups g2 WHERE (g = g2) AND f.id = ";
    private static final String GET_ALL_SORT_JPA = " ORDER by p.id";


    /* ************************** JPA Methods here ******************************* */

    /**
     * @see org.apromore.dao.ProcessRepositoryCustom#findAllProcesses(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Process> findAllProcesses(final String conditions) {
        StringBuilder strQry = new StringBuilder(0);
        strQry.append(GET_ALL_PROCESSES_JPA);
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(" AND ").append(conditions);
        }
        strQry.append(GET_ALL_SORT_JPA);

        Query query = em.createQuery(strQry.toString());
        query.setParameter("public", PUBLIC);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ProcessRepositoryCustom#findAllProcessesByFolder(Integer, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Process> findAllProcessesByFolder(
        final Integer folderId,
        final String userRowGuid,
        final String conditions,
        final boolean global
    ) {
        StringBuilder strQry = new StringBuilder(0);
        String userCondition = "(u.rowGuid = :userRowGuid) AND (gp.accessRights.readOnly = TRUE)";
        if (global) {
            strQry.append(GET_ALL_PROCESSES_CORE_JPA);
        } else if (folderId == 0) {
            strQry.append(GET_ALL_PROCESSES_CORE_JPA);
            strQry.append(" AND p.folder IS NULL");
        } else {
            strQry.append(GET_ALL_PROCESSES_FOLDER_JPA).append(folderId);
        }
        if (userRowGuid != null && !userRowGuid.isEmpty()) {
            strQry.append("  AND  ").append(userCondition);
        }
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(" AND ").append(conditions);
        }
        strQry.append(GET_ALL_SORT_JPA);

        Query query = em.createQuery(strQry.toString());
        if (userRowGuid != null && !userRowGuid.isEmpty()) {
            query.setParameter("userRowGuid", userRowGuid);
        }

        return query.getResultList();
    }

    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
