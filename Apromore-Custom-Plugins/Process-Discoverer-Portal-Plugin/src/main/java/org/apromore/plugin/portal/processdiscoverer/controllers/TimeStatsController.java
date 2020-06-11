/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.processdiscoverer.controllers;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;

/**
 * @author Ivo Widjaja
 * Modified: Ivo Widjaja
 */
public class TimeStatsController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeStatsController.class);

    // Graph settings
    private Label meanDuration;
    private Label medianDuration;
    private Label maxDuration;
    private Label minDuration;
    private Label logStartTime;
    private Label logEndTime;

    public TimeStatsController(PDController parent) {
        super(parent);
    }

    @Override
    public void initializeControls(Object data) {
        if (this.parent == null) return;

        LOGGER.info("TimeSettingsController");
        Component compTimeStats = parent.query(".ap-pd-timestats");

        // Time statistics
        meanDuration = (Label) compTimeStats.getFellow("meanDuration");
        meanDuration.setValue("-");
        medianDuration = (Label) compTimeStats.getFellow("medianDuration");
        medianDuration.setValue("-");
        maxDuration = (Label) compTimeStats.getFellow("maxDuration");
        maxDuration.setValue("-");
        minDuration = (Label) compTimeStats.getFellow("minDuration");
        minDuration.setValue("-");

        logStartTime = (Label) compTimeStats.getFellow("startTime");
        logStartTime.setValue("-");
        logEndTime = (Label) compTimeStats.getFellow("endTime");
        logEndTime.setValue("-");
    }

    @Override
    public void updateUI(Object data) {
        LogData logData = parent.getLogData();

        minDuration.setValue(logData.getFilteredMinDuration());
        medianDuration.setValue(logData.getFilteredMedianDuration());
        meanDuration.setValue(logData.getFilteredMeanDuration());
        maxDuration.setValue(logData.getFilteredMaxDuration());

        logStartTime.setValue(logData.getFilteredStartTime());
        logEndTime.setValue(logData.getFilteredEndTime());
    }

    @Override
    public void onEvent(Event event) throws Exception {
        throw new Exception("Unsupported interactive Event Handler");
    }
}
