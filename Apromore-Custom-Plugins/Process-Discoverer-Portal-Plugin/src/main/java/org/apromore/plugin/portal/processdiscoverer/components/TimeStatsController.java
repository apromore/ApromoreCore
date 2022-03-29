/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.processdiscoverer.components;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;

/**
 * @author Ivo Widjaja
 * Modified: Ivo Widjaja
 */
public class TimeStatsController extends AbstractController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(TimeStatsController.class);
    
    private Span spnCaseHeading;
    private Label lblCaseHeading;
    
    private Span spnLogHeading;
    private Label lblLogHeading;

    // Graph settings
    private Label meanDuration;
    private Label medianDuration;
    private Label maxDuration;
    private Label minDuration;
    private Label logStartTime;
    private Label logEndTime;
    
    private boolean disabled = false;

    public TimeStatsController(PDController parent) {
        super(parent);
    }

    @Override
    public void initializeControls(Object data) {
        if (this.parent == null) return;

        LOGGER.debug("TimeStatsController");
        Component compTimeStats = parent.query(".ap-pd-timestats");
        
        spnCaseHeading = (Span) compTimeStats.getFellow("spnCaseHeading");
        lblCaseHeading = (Label) compTimeStats.getFellow("lblCaseHeading");
        spnLogHeading = (Span) compTimeStats.getFellow("spnLogHeading");
        lblLogHeading = (Label) compTimeStats.getFellow("lblLogHeading");

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
    public void initializeEventListeners(Object data) throws Exception {
        spnCaseHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabPerformance"));
            }
        });
        
        lblCaseHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabPerformance"));
            }
        });
        
        spnLogHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabTimeframe"));
            }
        });
        
        lblLogHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabTimeframe"));
            }
        });
    }

    @Override
    public void updateUI(Object data) {
        PDAnalyst analyst = parent.getProcessAnalyst();

        minDuration.setValue(analyst.getFilteredMinDuration());
        medianDuration.setValue(analyst.getFilteredMedianDuration());
        meanDuration.setValue(analyst.getFilteredMeanDuration());
        maxDuration.setValue(analyst.getFilteredMaxDuration());

        logStartTime.setValue(analyst.getFilteredStartTime());
        logEndTime.setValue(analyst.getFilteredEndTime());
    }

    @Override
    public void onEvent(Event event) throws Exception {
        throw new Exception("Unsupported interactive Event Handler");
    }
    
    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
