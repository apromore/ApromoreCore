/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.predictivemonitor.impl;

// Java 2 Standard Editions
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Java 2 Enterprise Edition
import javax.inject.Inject;
import javax.inject.Named;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// Local classes
import org.apromore.service.dataflow.Dataflow;
import org.apromore.service.dataflow.DataflowService;
import org.apromore.service.dataflow.Processor;
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

@Service("predictiveMonitorService")
public class PredictiveMonitorServiceImpl implements PredictiveMonitorService {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictiveMonitorServiceImpl.class.getCanonicalName());

    @Inject private DataflowService dataflowService;

    public Predictor createPredictor(String name, File pklFile) {
        return null;
    }

    public List<Predictor> getPredictors() {
        return null;
    }

    public PredictiveMonitor createPredictiveMonitor(List<Predictor> predictors) {
        LOGGER.info("Create predictive monitor");

        List<String> topicNames = Arrays.asList("foo", "bar");

        List<Processor> processors = new ArrayList<>();
        CommandProcessor cp = new CommandProcessor();
        cp.setCommand("echo foo");
        cp.setDirectory("/tmp");
        processors.add(cp);

        Dataflow dataflow = dataflowService.createDataflow(topicNames, processors);
        return new PredictiveMonitorImpl(dataflow);
    }
}
