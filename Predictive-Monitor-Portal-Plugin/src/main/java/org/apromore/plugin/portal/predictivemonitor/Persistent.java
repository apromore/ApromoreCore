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

package org.apromore.plugin.portal.predictivemonitor;

// Third party packages
import org.zkoss.zul.ListModelList;

// Local packages
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

public class Persistent {

    private static ListModelList<PredictiveMonitor> predictiveMonitorsListModel;

    static ListModelList<PredictiveMonitor> getPredictiveMonitorsListModel(PredictiveMonitorService predictiveMonitorService) {
        if (predictiveMonitorsListModel == null) {
            predictiveMonitorsListModel = new ListModelList<PredictiveMonitor>(predictiveMonitorService.getPredictiveMonitors(), true);
            predictiveMonitorsListModel.setMultiple(true);
        }
        return predictiveMonitorsListModel;
    }


    private static ListModelList<Predictor> predictorsListModel;

    static ListModelList<Predictor> getPredictorsListModel(PredictiveMonitorService predictiveMonitorService) {
        if (predictorsListModel == null) {
            predictorsListModel = new ListModelList<Predictor>(predictiveMonitorService.getPredictors(), true);
            predictorsListModel.setMultiple(true);
        }
        return predictorsListModel;
    }
}
