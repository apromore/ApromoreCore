/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.apromore.service.prodrift.impl;

import org.apromore.prodrift.config.DriftDetectionSensitivity;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_EventStream;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_RunStream;
import org.apromore.prodrift.model.ProDriftDetectionResult;
import org.apromore.service.prodrift.ProDriftDetectionException;
import org.apromore.service.prodrift.ProDriftDetectionService;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of the ProDriftDetectionService Contract.
 *
 * @author barca
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ProDriftDetectionServiceImpl implements ProDriftDetectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProDriftDetectionServiceImpl.class);

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     */
    public ProDriftDetectionServiceImpl() {}

    /**
     * @see ProDriftDetectionService#proDriftDetector(XLog, XLog, String, boolean,
            boolean, int, int, boolean, float, DriftDetectionSensitivity, boolean, boolean, int);
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProDriftDetectionResult proDriftDetector(XLog xlog, XLog eventStream, String logFileName, boolean isEventBased,
                                                    boolean withGradual, int winSize, int activityCount, boolean isAdwin, float noiseFilterPercentage, DriftDetectionSensitivity ddSensitivity,
                                                    boolean withConflict, boolean withCharacterization, int cummulativeChange /*, Rengine engineR*/) throws ProDriftDetectionException, InterruptedException {

        ProDriftDetectionResult pddRes = null;

        if(isEventBased)
        {

            ControlFlowDriftDetector_EventStream driftDertector = new ControlFlowDriftDetector_EventStream(xlog, eventStream, winSize, activityCount, isAdwin, noiseFilterPercentage, ddSensitivity, withConflict, logFileName, withCharacterization, cummulativeChange);
            pddRes = driftDertector.ControlFlowDriftDetectorStart();

        }else
        {

            ControlFlowDriftDetector_RunStream driftDertector = new ControlFlowDriftDetector_RunStream(xlog, winSize, isAdwin, logFileName, withGradual);
            pddRes = driftDertector.ControlFlowDriftDetectorStart();

        }

        return pddRes;
    }


}
