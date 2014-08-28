/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.impl;

import org.apromore.dao.MetricRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.metric.MetricPlugin;
import org.apromore.plugin.metric.provider.MetricPluginProvider;
import org.apromore.scheduler.Worker;
import org.apromore.service.MetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * Service for common Plugin operations, like list all installed Plugins, install a new Plugin, get Plugin meta data.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
public class MetricServiceImpl implements MetricService {

    protected static Logger LOGGER = LoggerFactory.getLogger("MetricServiceImpl");

    private MetricPluginProvider metricProvider;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param metProvider the Metric Plugin provider.
     */
    @Inject
    public MetricServiceImpl(final @Qualifier("metricProvider") MetricPluginProvider metProvider) {
        metricProvider = metProvider;
    }


    /**
     * @see org.apromore.service.MetricService#listAll()
     * {@inheritDoc}
     */
    @Override
    public Collection<MetricPlugin> listAll() {
        return metricProvider.listAll();
    }

    /**
     * @see org.apromore.service.MetricService#findByName(String)
     * {@inheritDoc}
     */
    @Override
    public MetricPlugin findByName(String pluginName) throws PluginNotFoundException {
        return metricProvider.findByName(pluginName);
    }

    /**
     * @see org.apromore.service.MetricService#findByNameAndVersion(String, String)
     * {@inheritDoc}
     */
    @Override
    public MetricPlugin findByNameAndVersion(String pluginName, String versionNumber) throws PluginNotFoundException {
        return metricProvider.findByNameAndVersion(pluginName, versionNumber);
    }

}
