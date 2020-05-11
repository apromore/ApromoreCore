/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.scheduler.jobs;

import org.apromore.exception.RepositoryException;
import org.apromore.exception.SchedulerException;
import org.apromore.scheduler.Worker;
import org.apromore.service.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Scheduled Job that does the Metric Calculations for all process models that need it done.
 *
 * @author Cameron James
 * @since 1.0
 */
@Component("gedMatrixWorker")
public class GEDMatrixWorker implements Worker {

    protected static Logger LOGGER = LoggerFactory.getLogger(GEDMatrixWorker.class);

    private ClusterService clusterSrv;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param clusterService the clustering service.
     */
    @Inject
    public GEDMatrixWorker(final ClusterService clusterService) {
        clusterSrv = clusterService;
    }

    /**
     * @see org.apromore.scheduler.Worker#executeJob()
     * {@inheritDoc}
     */
    @Async
    public void executeJob() throws SchedulerException {
        //Temporarily disabled because of allegedly high memory consumption
        try {
            clusterSrv.computeGEDMatrix();
        } catch (RepositoryException repoException) {
            throw new SchedulerException("Failed to run GED Matrix Computation!", repoException);
        }
    }

}
