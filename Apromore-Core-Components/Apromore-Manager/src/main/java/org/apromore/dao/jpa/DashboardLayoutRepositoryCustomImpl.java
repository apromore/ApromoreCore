/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class DashboardLayoutRepositoryCustomImpl implements org.apromore.dao.DashboardLayoutRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardLayoutRepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveLayoutByLogId(Integer userId, Integer logId, String layout) {

        if (null != userId && null != logId) {
            String sql = "INSERT INTO dashboard_layout (userId, logId, layout) VALUES (?,?,?)";

            jdbcTemplate.update(sql, userId, logId, layout);
            LOGGER.info("Save dashboard layout for log: " + logId);
            return;

        }
    }
}
