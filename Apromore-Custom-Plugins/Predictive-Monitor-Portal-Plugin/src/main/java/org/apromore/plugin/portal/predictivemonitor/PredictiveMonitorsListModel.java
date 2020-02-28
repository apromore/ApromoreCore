/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Editions
import java.util.List;

// Third party packages
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.event.ListDataEvent;

// Local packages
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

/**
 * Adapt a {@link PredictiveMonitorService} to be a ZK-compatible {@link ListModel} of {@link PredictiveMonitor}s.}
 */
public class PredictiveMonitorsListModel extends AbstractListModel<PredictiveMonitor> {
    private PredictiveMonitorService predictiveMonitorService;

    PredictiveMonitorsListModel(PredictiveMonitorService predictiveMonitorService) {
        this.predictiveMonitorService = predictiveMonitorService;
        setMultiple(true);
    }

    public int getSize() {
        return predictiveMonitorService.getPredictiveMonitors().size();
    }

    public PredictiveMonitor getElementAt(int index) {
        return predictiveMonitorService.getPredictiveMonitors().get(index);
    }

    public PredictiveMonitor createPredictiveMonitor(String name, List<Predictor> predictors) {
        PredictiveMonitor predictiveMonitor = predictiveMonitorService.createPredictiveMonitor(name, predictors);
        int s = getSize();
        fireEvent(ListDataEvent.INTERVAL_ADDED, s-1, s);  // presumes that the new element is added to the end of the list, which is the case for auto-incrementing primary key IDs
        return predictiveMonitor;
    }

    public void removeAll(Iterable<PredictiveMonitor> removedPredictiveMonitors) {
        int s = getSize();
        predictiveMonitorService.deletePredictiveMonitors(removedPredictiveMonitors);
        fireEvent(ListDataEvent.CONTENTS_CHANGED, 0, s);  // removal could've occurred anywhere, so mark the entire list changed
    }
}
