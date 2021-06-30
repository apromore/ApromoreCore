/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.processdiscoverer.impl.apmlog;

import java.text.DecimalFormat;

import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.components.LogStatsController;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

/**
 * LogStatsControllerWithAPMLog is a {@link LogStatsController} with some data
 * statistics getting from APMLog.
 * 
 * @author Chii Chang, Ivo Widjaja
 * Modified: Chii Chang (03/02/2020)
 * Modified: Ivo Widjaja
 * Modified: Bruce Nguyen: extends from LogStatsController.
 *
 */
public class LogStatsControllerWithAPMLog extends LogStatsController {
    private Component wdLogStats;
    
    private Button btnCaseHeading;
    private Label lblCaseHeading;
    
    private Button btnCaseVariantHeading;
    private Label lblCaseVariantHeading;
    
    private Button btnEventHeading;
    private Label lblEventHeading;
    
    private Button btnActivityHeading;
    private Label lblActivityHeading;
    
    private Label lblCasePercent, lblVariantPercent, lblEventPercent;
    private Label lblCaseNumberFiltered, lblCaseNumberTotal, lblVariantNumberFiltered, lblVariantNumberTotal, lblEventNumberFiltered, lblEventNumberTotal;
    private Label lblNodePercent, lblNodeNumberFiltered, lblNodeNumberTotal;

    // TO DO: Check if total can be persisted during init
    private long totalEventCount;
    private long totalCaseCount;
    private long totalVariantCount;
    private long totalNodeCount;
    
    private PDAnalyst analyst;
    
    private boolean disabled = false;

    public LogStatsControllerWithAPMLog(PDController parent)  throws Exception {
        super(parent);
        analyst = parent.getProcessAnalyst();
    }

    @Override
    public void initializeControls(Object data) {
        if (this.parent == null) return;

        wdLogStats = parent.query(".ap-pd-logstats");
        
        btnCaseHeading = (Button) wdLogStats.getFellow("btnCaseHeading");
        lblCaseHeading = (Label) wdLogStats.getFellow("lblCaseHeading");
        
        btnCaseVariantHeading = (Button) wdLogStats.getFellow("btnCaseVariantHeading");
        lblCaseVariantHeading = (Label) wdLogStats.getFellow("lblCaseVariantHeading");
        
        btnEventHeading = (Button) wdLogStats.getFellow("btnEventHeading");
        lblEventHeading = (Label) wdLogStats.getFellow("lblEventHeading");
        
        btnActivityHeading = (Button) wdLogStats.getFellow("btnActivityHeading");
        lblActivityHeading = (Label) wdLogStats.getFellow("lblActivityHeading");

        lblCasePercent = (Label) wdLogStats.getFellow("lblCasePercent");
        lblVariantPercent = (Label) wdLogStats.getFellow("lblVariantPercent");
        lblEventPercent = (Label) wdLogStats.getFellow("lblEventPercent");
        lblNodePercent = (Label) wdLogStats.getFellow("lblNodePercent");

        lblCaseNumberFiltered = (Label) wdLogStats.getFellow("lblCaseNumberFiltered");
        lblCaseNumberTotal = (Label) wdLogStats.getFellow("lblCaseNumberTotal");
        lblVariantNumberFiltered = (Label) wdLogStats.getFellow("lblVariantNumberFiltered");
        lblVariantNumberTotal = (Label) wdLogStats.getFellow("lblVariantNumberTotal");
        lblEventNumberFiltered = (Label) wdLogStats.getFellow("lblEventNumberFiltered");
        lblEventNumberTotal = (Label) wdLogStats.getFellow("lblEventNumberTotal");
        lblNodeNumberFiltered = (Label) wdLogStats.getFellow("lblNodeNumberFiltered");
        lblNodeNumberTotal = (Label) wdLogStats.getFellow("lblNodeNumberTotal");

        AttributeLogSummary oriLogSummary = parent.getProcessAnalyst().getAttributeLog().getOriginalLogSummary();
        updateFromLogSummary(oriLogSummary, oriLogSummary);
    }
    
    @Override
    public void initializeEventListeners(Object data) throws Exception {
        btnCaseHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabID"));
            }
        });
        
        lblCaseHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabID"));
            }
        });
        
        btnCaseVariantHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabVariant"));
            }
        });
        
        lblCaseVariantHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabVariant"));
            }
        });
        
        btnEventHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "EventTabAttribute"));
            }
        });
        
        lblEventHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "EventTabAttribute"));
            }
        });
        
        btnActivityHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabAttribute"));
            }
        });
        
        lblActivityHeading.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                parent.openLogFilter(new Event("", null, "CaseTabAttribute"));
            }
        });
    }

    private void updateFromLogSummary(AttributeLogSummary filtered, AttributeLogSummary total) {
        PLog pLog = analyst.getFilteredPLog();

        totalEventCount = pLog.getImmutableLog().getActivityInstances().size();
        totalCaseCount = pLog.getImmutableLog().size();
        totalVariantCount = LogStatsAnalyzer.getCaseVariantGroupMap(pLog.getImmutableLog().getTraces()).size();
        totalNodeCount = total.getActivityCount();

        long filteredEventCount = analyst.getFilteredActivityInstanceSize();
        long filteredCaseCount = pLog.getValidTraceIndexBS().cardinality();
        long filteredVariantCount = analyst.getFilteredCaseVariantSize();
        long filteredNodeCount = filtered.getActivityCount();

        setNumber(this.lblCaseNumberFiltered, filteredCaseCount);
        setNumber(this.lblCaseNumberTotal, totalCaseCount);
        showPercentage(this.lblCasePercent, filteredCaseCount, totalCaseCount, "case");

        setNumber(this.lblVariantNumberFiltered, filteredVariantCount);
        setNumber(this.lblVariantNumberTotal, totalVariantCount);
        showPercentage(this.lblVariantPercent, filteredVariantCount, totalVariantCount, "variant");

        setNumber(this.lblEventNumberFiltered, filteredEventCount);
        setNumber(this.lblEventNumberTotal, totalEventCount);
        showPercentage(this.lblEventPercent, filteredEventCount, totalEventCount, "event");

        setNumber(this.lblNodeNumberFiltered, filteredNodeCount);
        setNumber(this.lblNodeNumberTotal, totalNodeCount);
        showPercentage(this.lblNodePercent, filteredNodeCount, totalNodeCount, "perspective");
    }

    private void setNumber(Label label, long number) {
        String strNumber = Long.toString(number);
        label.setValue(toShortString(number));
        label.setClientAttribute("title", strNumber);
        label.setClientDataAttribute("raw", strNumber);
    }

    private void showPercentage(Label label, long filtered, long total, String chartType) {
        DecimalFormat decimalFormat = new DecimalFormat("###############.#");
        double retained = 100.0;

        if (total == 0 || filtered == 0) {
            retained = 0;
        } else if(filtered != total) {
            retained = 100 * (double)filtered / total;
        }
        String retainedPercent = decimalFormat.format(retained) + "%";
        if (retainedPercent.equals("0%") && retained > 0) {
            retainedPercent = "~0%";
        }
        label.setValue(retainedPercent);
        Clients.evalJavaScript("Ap.pd.genChart('" + chartType + "', " + retained + ")");
    }

    private String toShortString(long longNumber) {
        DecimalFormat df1 = new DecimalFormat("###############.#");
        String numberString = "";
        if(longNumber > 1000000) numberString = "" + df1.format((double) longNumber / 1000000) + "M";
        else if (longNumber > 1000) numberString = "" + df1.format((double)longNumber / 1000) + "K";
        else numberString = longNumber + "";

        return numberString;
    }

    @Override
    public void updateUI(Object data) {
        AttributeLog attLog = parent.getProcessAnalyst().getAttributeLog();
        AttributeLogSummary oriLogSummary = attLog.getOriginalLogSummary();
        AttributeLogSummary logSummary = attLog.getLogSummary();
        updateFromLogSummary(logSummary, oriLogSummary);
    }
    
    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void updatePerspectiveHeading(String perspective) {
        lblActivityHeading.setValue(perspective);
    }
}
