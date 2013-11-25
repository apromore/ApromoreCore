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
        try {
            clusterSrv.computeGEDMatrix();
        } catch (RepositoryException repoException) {
            throw new SchedulerException("Failed to run GED Matrix Computation!", repoException);
        }
    }

}
