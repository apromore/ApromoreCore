/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.portal.custom.gui.plugin;

import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.custom.gui.tab.PortalTab;
import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.apromore.portal.custom.gui.tab.impl.*;
import org.apromore.portal.model.*;
import org.zkoss.util.resource.Labels;
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
        addListheader(listheaders, Labels.getLabel("common_name_text", "Name"), true, null, null, "4");
        addListheader(listheaders, Labels.getLabel("common_id_text", "ID"), true, "center", "80px", null);
        addListheader(listheaders, Labels.getLabel("common_last_version_text", "Last version"), true, "center", "140px", null);
        addListheader(listheaders, Labels.getLabel("common_last_update_text", "Last update"), true, "center", "140px", null);
        addListheader(listheaders, Labels.getLabel("common_owner_text", "Owner"), true, "center", null, "1");

        addTab(tabName, "~./themes/ap/common/img/icons/bpmn-model.svg", rows, listheaders, new ProcessTabItemExecutor(portalContext.getMainController()), portalContext);
    }

    private Listheader addListheader(final List<Listheader> listheaders, String name, boolean visible,
                                     String align, String width, String hflex) {
        final Listheader listheader = new Listheader(name, null);

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
        listheader.setVisible(visible);
        if (align != null) {
            listheader.setAlign(align);
        }
        if (width != null) {
            listheader.setWidth(width);
        }
        if (hflex != null) {
            listheader.setHflex(hflex);
        }
        listheaders.add(listheader);
        return listheader;
    }

    protected ProcessSummaryRowValue createProcessSummaryRowValue(ProcessSummaryType processSummaryType, VersionSummaryType versionSummaryType) {
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setMaximumFractionDigits(3);

        ProcessSummaryRowValue processSummaryRowValue = new ProcessSummaryRowValue(processSummaryType, versionSummaryType);
        processSummaryRowValue.add(processSummaryType.getName());
        processSummaryRowValue.add(processSummaryType.getId());
        processSummaryRowValue.add(processSummaryType.getLastVersion());
        processSummaryRowValue.add(DateTimeUtils.normalize(versionSummaryType.getLastUpdate()));

        Boolean isMakePublic = processSummaryType.isMakePublic();
        String label;
        if (isMakePublic != null && isMakePublic == true) {
            label = "public";
        } else {
            label = processSummaryType.getOwner();
        }
        processSummaryRowValue.add(label);
        return processSummaryRowValue;
    }

}
