/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.portal.custom.gui.plugin;

import org.apromore.model.*;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.custom.gui.tab.PortalTab;
import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.apromore.portal.custom.gui.tab.impl.*;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 25/03/2016.
 */
public abstract class PluginCustomGui extends DefaultPortalPlugin {

    private DecimalFormat decimalFormat = new DecimalFormat();

    public final TabRowValue createTabRowValue(String... values) {
        TabRowValue tabRowValue = new TabRowValue();
        for(String value : values) {
            tabRowValue.add(value);
        }
        return tabRowValue;
    }

    public final PortalTab addTab(String tabName, String tabRowImage, List<TabRowValue> rows, List<Listheader> listheaders, TabItemExecutor tabItemExecutor, PortalContext portalContext) {
        PortalTabImpl tab = new PortalTabImpl(tabName, tabRowImage, rows, listheaders, tabItemExecutor, portalContext);
        addTab(tab, portalContext);
        return tab;
    }

    private void addTab(Tab tab, PortalContext portalContext) {
        SessionTab.getSessionTab(portalContext).addTabToSession(portalContext.getCurrentUser().getId(), tab, true);
    }

    protected void displayProcessSummaries(String tabName, SummariesType processSummaries, PortalContext portalContext) {
        List<TabRowValue> rows = new ArrayList<>();
        for(SummaryType summaryType : processSummaries.getSummary()) {
            if(summaryType instanceof ProcessSummaryType) {
                ProcessSummaryType processSummaryType = (ProcessSummaryType) summaryType;
                rows.add(createProcessSummaryRowValue(processSummaryType, processSummaryType.getVersionSummaries().get(0)));
            }
        }

        List<Listheader> listheaders = new ArrayList<>();
        if(rows.size() > 0 && rows.get(0).size() > 7) {
            listheaders.add(new Listheader("Score", null, "3em"));
        }
        addListheader(listheaders, "Name",              null);
        addListheader(listheaders, "Id",                "3em");
        addListheader(listheaders, "Original language", "10em");
        addListheader(listheaders, "Domain",            "5em");
        addListheader(listheaders, "Rating",           "6em");
        addListheader(listheaders, "Latest version",    "9em");
        addListheader(listheaders, "Owner",             "5em");

        addTab(tabName, "/themes/ap/common/img/icons/bpmn-model.svg", rows, listheaders, new ProcessTabItemExecutor(portalContext.getMainController()), portalContext);
    }

    private void addListheader(final List<Listheader> listheaders, String name, String width) {
        final Listheader listheader = new Listheader(name, null, width);

        listheader.setSortAscending(new java.util.Comparator<TabItem>() {
            int position = listheaders.size();
            @Override
            public int compare(TabItem o1, TabItem o2) {
                return o1.getValue(position).compareTo(o2.getValue(position));
            }
        });

        listheader.setSortDescending(new java.util.Comparator<TabItem>() {
            int position = listheaders.size();
            @Override
            public int compare(TabItem o1, TabItem o2) {
                return o2.getValue(position).compareTo(o1.getValue(position));
            }
        });

        listheaders.add(listheader);
    }

    protected ProcessSummaryRowValue createProcessSummaryRowValue(ProcessSummaryType processSummaryType, VersionSummaryType versionSummaryType) {
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setMaximumFractionDigits(3);

        ProcessSummaryRowValue processSummaryRowValue = new ProcessSummaryRowValue(processSummaryType, versionSummaryType);
        if(processSummaryType.getVersionSummaries().get(0).getScore() != null) {
            processSummaryRowValue.add(Double.parseDouble(decimalFormat.format(processSummaryType.getVersionSummaries().get(0).getScore())));
        }
        processSummaryRowValue.add(processSummaryType.getName());
        processSummaryRowValue.add(processSummaryType.getId());
        processSummaryRowValue.add(processSummaryType.getOriginalNativeType());
        processSummaryRowValue.add(processSummaryType.getDomain());
        processSummaryRowValue.add(processSummaryType.getRanking());
        processSummaryRowValue.add(processSummaryType.getLastVersion());
        processSummaryRowValue.add(processSummaryType.getOwner());
        return processSummaryRowValue;
    }

}
