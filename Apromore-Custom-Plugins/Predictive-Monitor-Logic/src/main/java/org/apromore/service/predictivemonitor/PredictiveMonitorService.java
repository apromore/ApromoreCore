/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service.predictivemonitor;

// Java 2 Standard Edition
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Observer;

// Third party packages
import org.deckfour.xes.model.XLog;

public interface PredictiveMonitorService {

    public void addObserver(Observer observer);

    // Predictors
    public Predictor createPredictor(String name, String type, InputStream pklFile);
    public void deletePredictors(Iterable<Predictor> predictors);
    public Predictor findPredictorById(Integer id);
    public Predictor findPredictorByName(String name);
    public List<Predictor> getPredictors();

    // PredictiveMonitors
    public PredictiveMonitor createPredictiveMonitor(String name, List<Predictor> predictors);
    public void deletePredictiveMonitors(Iterable<PredictiveMonitor> predictiveMonitors);
    public PredictiveMonitor findPredictiveMonitorByName(String name);
    public List<PredictiveMonitor> getPredictiveMonitors();
    public void exportLogToPredictiveMonitor(XLog log, PredictiveMonitor predictiveMonitor) throws Exception;

    // PredictiveMonitorEvents
    public List<PredictiveMonitorEvent> findPredictiveMonitorEvents(PredictiveMonitor predictiveMonitor);
}
