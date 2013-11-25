package org.apromore.dao;

import org.apromore.dao.model.Metric;
import org.apromore.dao.model.ProcessModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object QueuedTaskHolder.
 *
 * @see org.apromore.dao.model.Metric
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
@Repository
public interface MetricRepository extends JpaRepository<Metric, Integer> {

    /**
     * Finds all the processes that need there metric calculations updated.
     * @param processModelVersion the process model version we want the metric calculations for.
     * @return the list of metric results for that PMV.
     */
    List<Metric> findByProcessModelVersion(ProcessModelVersion processModelVersion);


    /**
     * Checked to see if the metric exists or not in the db.
     * @param MetricName the metric name we are checking
     * @param processModelVersion the process model version
     * @return the metric if one was found or null if not found.
     */
    Metric findByNameAndProcessModelVersion(String MetricName, ProcessModelVersion processModelVersion);
}
