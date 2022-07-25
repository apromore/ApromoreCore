/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

/**
 *
 */
package org.apromore.dao.jpa;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.LogRepositoryCustom;
import org.apromore.dao.model.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * implementation of the org.apromore.dao.LogRepositoryCustom interface.
 * @author <a href="mailto:raffaele.conforti@unimelb.edu.au">Raffaele Conforti</a>
 */
public class LogRepositoryCustomImpl implements LogRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogRepositoryCustomImpl.class);

    private static final String APMLOG_CACHE_KEY_SUFFIX = "APMLog";
    private static final String GET_ALL_LOGS_JPA = "SELECT l FROM Log l ";
    private static final String GET_ALL_LOGS_CORE_JPA = "SELECT l FROM GroupLog gl JOIN gl.log l JOIN gl.group g, User u JOIN u.groups g2 WHERE (g = g2)";
    private static final String GET_ALL_LOGS_FOLDER_JPA = "SELECT l FROM GroupLog gl JOIN gl.log l JOIN gl.group g JOIN l.folder f, User u JOIN u.groups g2 WHERE (g = g2) AND f.id = ";
    private static final String GET_ALL_SORT_JPA = " ORDER by l.id";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    @PersistenceContext
    private EntityManager em;

   
    /* ************************** JPA Methods here ******************************* */

    /**
     * @see org.apromore.dao.LogRepositoryCustom#findAllLogs(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Log> findAllLogs(final String conditions) {
        StringBuilder strQry = new StringBuilder(0);
        strQry.append(GET_ALL_LOGS_JPA);
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(" WHERE ").append(conditions);
        }
        strQry.append(GET_ALL_SORT_JPA);

        Query query = em.createQuery(strQry.toString());
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.LogRepositoryCustom#findAllLogsByFolder(Integer, String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Log> findAllLogsByFolder(
        final Integer folderId,
        final String userRowGuid,
        final String conditions,
        final boolean global
    ) {
        StringBuilder strQry = new StringBuilder(0);
        String userCondition = "(u.rowGuid = :userRowGuid) AND (gl.accessRights.readOnly = TRUE)";
        if (global) {
            strQry.append(GET_ALL_LOGS_CORE_JPA);
        } else if (folderId == 0) {
            strQry.append(GET_ALL_LOGS_CORE_JPA);
            strQry.append(" AND l.folder IS NULL");
        } else {
            strQry.append(GET_ALL_LOGS_FOLDER_JPA).append(folderId);
        }
        if (userRowGuid != null && !userRowGuid.isEmpty()) {
            strQry.append(" AND ").append(userCondition);
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
    
    public Log getLogReference(Integer logId)
    {
      return em.getReference(Log.class, logId);
    }

}
