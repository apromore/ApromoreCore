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

package org.apromore.service.prodrift;

import org.apromore.prodrift.model.ProDriftDetectionResult;
import org.apromore.prodrift.model.ProDriftTerminator;
import org.deckfour.xes.model.XLog;

/**
 * Interface for the Process Drift Detection Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author barca
 */
public interface ProDriftDetectionService {

    /**
     * Detect drifts in the log
     * @param xlog the log
     * @param eventStream eventStream of the xlog
     * @param logFileName log's name
     * @param isEventBased event based or run based
     * @param withGradual detect gradual drift or not
     * @param winSize the window size
     * @param activityCount number of activities in xlog
     * @param isAdwin Fixed window size or Adaptive window size("FWIN" or "ADWIN")
     * @param noiseFilterPercentage noise fitler percentage value
     * @param withConflict include conflict relation among Alpha+ relations or not
     * @param withCharacterization characterize a drift?
     * @param cummulativeChange cummulative relative relation frequency change explaining a drift
     * @param terminator for terminating ProDrift
     //* @param engineR Rengine to connect to R

     * @return the ProDriftDetectionResult
     * @throws ProDriftDetectionException if the drift detection failed
     */
    ProDriftDetectionResult proDriftDetector(XLog xlog, XLog eventStream, String logFileName, boolean isEventBased,
                                             boolean withGradual, int winSize, int activityCount, boolean isAdwin, float noiseFilterPercentage,
                                             boolean withConflict, boolean withCharacterization, int cummulativeChange, ProDriftTerminator terminator/*,
                                             Rengine engineR*/) throws ProDriftDetectionException;


}
