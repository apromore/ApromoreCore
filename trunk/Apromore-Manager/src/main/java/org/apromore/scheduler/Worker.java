package org.apromore.scheduler;

import org.apromore.exception.SchedulerException;

/**
 * Worker Interface. Used by the scheduler tasks to do the work.
 *
 * @author Cameron James
 * @since 1.0
 */
public interface Worker {

    /**
     * Does the work.
     */
    public void executeJob() throws SchedulerException;

}
