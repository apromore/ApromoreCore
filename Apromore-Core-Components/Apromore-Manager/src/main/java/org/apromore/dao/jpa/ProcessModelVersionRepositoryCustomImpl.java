/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.dao.ProcessModelVersionRepositoryCustom;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class ProcessModelVersionRepositoryCustomImpl implements ProcessModelVersionRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */

    /**
     * @see org.apromore.dao.ProcessRepositoryCustom#findAllProcesses(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getMaxModelVersions(Integer fragmentVersionId) {
        Map<String, Integer> maxModels = new HashMap<String, Integer>();
        Query query = em.createQuery("SELECT pmv.processBranch.id, max(pmv.versionNumber) " +
                "FROM ProcessModelVersion pmv, ProcessFragmentMap pfm " +
                "WHERE pmv.id = pfm.processModelVersion.id AND pfm.fragmentVersion.id = :id " +
                "GROUP BY pmv.processBranch.id");
        query.setParameter("id", fragmentVersionId);

        List<Object[]> pmvBranches = (List<Object[]>) query.getResultList();
        for (Object[] obj : pmvBranches) {
            maxModels.put((String) obj[0], (Integer) obj[1]);
        }

        return maxModels;
    }


    /**
     * @see org.apromore.dao.ProcessModelVersionRepositoryCustom#getCurrentModelVersions(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getCurrentModelVersions(final Integer fragmentVersionId) {
        Map<String, Integer> currentModels = new HashMap<String, Integer>();
        Query query = em.createQuery("SELECT pmv1.processBranch.id, max(pmv1.versionNumber) " +
                "FROM ProcessModelVersion pmv1, ProcessModelVersion pmv2, ProcessFragmentMap pfm " +
                "WHERE pmv2.id = pfm.processModelVersion.id AND pfm.fragmentVersion.id = :id " +
                "  AND pmv2.processBranch.id = pmv1.processBranch.id GROUP BY pmv1.processBranch.id");

        query.setParameter("id", fragmentVersionId);

        List<Object[]> pmvBranches = (List<Object[]>) query.getResultList();
        for (Object[] obj : pmvBranches) {
            currentModels.put((String) obj[0], (Integer) obj[1]);
        }

        return currentModels;
    }


    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
