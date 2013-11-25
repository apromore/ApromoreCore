package org.apromore.service;

/**
 * Interface for the Scheduler Service. Defines all the scheduled tasks that need to implemented.
 *
 * @author Cameron James
 * @since 1.0
 */
public interface SchedulerService {

    /**
     * Runs a series of metric calculations (plugins) across a number of process model version.
     * Only process Models that don't have any metrics currently stored for them will be executed.
     */
    void runMetricCalculations();


    /**
     * Runs the GED Matrix Computation
     */
    void runGEDMatrixComputation();
}
