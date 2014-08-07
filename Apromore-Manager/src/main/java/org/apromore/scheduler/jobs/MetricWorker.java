/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.scheduler.jobs;

import org.apromore.aop.Event;
import org.apromore.dao.MetricRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.HistoryEnum;
import org.apromore.dao.model.Metric;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.SchedulerException;
import org.apromore.plugin.metric.MetricPlugin;
import org.apromore.plugin.metric.result.MetricPluginResult;
import org.apromore.scheduler.Worker;
import org.apromore.service.ComposerService;
import org.apromore.service.MetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Scheduled Job that does the Metric Calculations for all process models that need it done.
 *
 * @author Cameron James
 * @since 1.0
 */
@Component("metricWorker")
public class MetricWorker implements Worker {

    protected static Logger LOGGER = LoggerFactory.getLogger(MetricWorker.class);

    private ProcessModelVersionRepository pmvRepo;
    private MetricRepository metricRepo;
    private MetricService metricSrv;
    private ComposerService composerSrv;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param composerService the composer service.
     * @param metricRepository the metric repository.
     */
    @Inject
    public MetricWorker(final ProcessModelVersionRepository pmvRepository, final MetricRepository metricRepository,
            final @Qualifier("composerServiceImpl") ComposerService composerService,
            final @Qualifier("metricServiceImpl") MetricService metricService) {
        pmvRepo = pmvRepository;
        composerSrv = composerService;
        metricRepo = metricRepository;
        metricSrv = metricService;
    }


    /**
     * @see org.apromore.scheduler.Worker#executeJob()
     * {@inheritDoc}
     */
    @Async
    public void executeJob() throws SchedulerException {
        List<ProcessModelVersion> processes = pmvRepo.findAll();
        for (ProcessModelVersion pmv : processes) {
            try {
                metricRepo.save(runCalculations(metricSrv.listAll(), pmv));
            } catch (ExceptionDao exceptionDao) {
                throw new SchedulerException("Failed to run Metric Calculations!", exceptionDao);
            }
        }
    }

    /* Runs the Calculations for a ProcessModelVersion. */
    @Event(message = HistoryEnum.METRIC_COMPUTATION)
    private Iterable<Metric> runCalculations(Collection<MetricPlugin> metricPlugins, ProcessModelVersion pmv) throws ExceptionDao {
        List<Metric> metrics = new ArrayList<>();

        Metric metric;
        MetricPluginResult result;
        for (MetricPlugin plugin : metricPlugins) {
            if (metricRepo.findByNameAndProcessModelVersion(plugin.getName(), pmv) == null) {
                result = plugin.calculate(composerSrv.compose(pmv.getRootFragmentVersion()), null);

                if (result != null) {
                    metric = new Metric();
                    metric.setName(plugin.getName());
                    metric.setValue(result.getMetricResults());
                    metric.setProcessModelVersion(pmv);
                    metric.setLastUpdateDate(new Date().toString());

                    pmv.addMetric(metric);

                    metrics.add(metric);
                }
            }
        }
        return metrics;
    }

}
