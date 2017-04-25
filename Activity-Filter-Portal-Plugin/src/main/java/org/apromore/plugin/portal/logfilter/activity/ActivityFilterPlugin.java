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

package org.apromore.plugin.portal.logfilter.activity;

// Java 2 Standard Edition packages
import java.io.*;
import java.util.*;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Third party packages
import org.apromore.common.Constants;
import org.apromore.dao.LogRepository;
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.service.EventLogService;
import org.apromore.service.logfilter.activity.ActivityFilterService;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

// Local packages
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Metrics service. Created by Raffaele Conforti 18/04/2016
 */
@Component("plugin")
public class ActivityFilterPlugin extends PluginCustomGui {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityFilterPlugin.class);

    @Inject private EventLogService eventLogService;
    @Inject private ActivityFilterService activityFilterService;

    private Window window;
    private Slider threshold;
    private Button cancelButton;
    private Button okButton;

    @Override
    public String getLabel(Locale locale) {
        return "Filter Out Infrequent Activities";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Discover";
    }

    @Override
    public void execute(final PortalContext portalContext) {

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof LogSummaryType) {
                selectedLogSummaryType.add((LogSummaryType) entry.getKey());
            }
        }

        // At least 2 process versions must be selected. Not necessarily of different processes
        if (selectedLogSummaryType.size() == 0) {

        }else if (selectedLogSummaryType.size() == 1) {
            try {
                this.window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/activityFilter.zul", null, null);
                this.threshold = (Slider) this.window.getFellow("activityFilterThreshold");
                this.cancelButton = (Button) this.window.getFellow("activityFilterCancelButton");
                this.okButton = (Button) this.window.getFellow("activityFilterOKButton");

                this.cancelButton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });
                this.okButton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        runComputation(portalContext, selectedLogSummaryType, threshold.getCurpos());
                    }
                });
                this.window.doModal();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            portalContext.getMessageHandler().displayInfo("Select one log for process discovery.");
            return;
        }
    }

    protected void cancel() {
        this.window.detach();
    }

    protected void runComputation(PortalContext portalContext, Set<LogSummaryType> selectedLogSummaryType, int percentage) {
        LogSummaryType logST= selectedLogSummaryType.iterator().next();

        XLog xlog = activityFilterService.filterLog(eventLogService.getXLog(logST.getId()), percentage);

        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            eventLogService.exportToStream(outputStream, xlog);

            int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

            eventLogService.importLog(portalContext.getCurrentUser().getUsername(), folderId,
                    logST.getName() + "_activity_filtered", new ByteArrayInputStream(outputStream.toByteArray()), "xes.gz",
                    logST.getDomain(), DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                    logST.isMakePublic());

            window.detach();
            portalContext.refreshContent();
        } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
