package org.apromore.portal.custom.gui.plugin;

import org.apromore.helper.Version;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.custom.gui.tab.PortalTab;
import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.apromore.portal.custom.gui.tab.impl.*;
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

    public final TabHeader createTabHeader(String... values) {
        TabHeader tabHeader = new TabHeader();
        for(String value : values) {
            tabHeader.add(value);
        }
        return tabHeader;
    }

    public final TabRowValue createTabRowValue(String... values) {
        TabRowValue tabRowValue = new TabRowValue();
        for(String value : values) {
            tabRowValue.add(value);
        }
        return tabRowValue;
    }

    public final PortalTab addTab(String tabName, String tabRowImage, List<TabRowValue> rows, TabHeader tabHeader, TabItemExecutor tabItemExecutor, PortalContext portalContext) {
        PortalTabImpl tab = new PortalTabImpl(tabName, tabRowImage, rows, tabHeader, tabItemExecutor, portalContext);
        addTab(tab, portalContext);
        return tab;
    }

    private void addTab(Tab tab, PortalContext portalContext) {
        SessionTab.getSessionTab(portalContext).addTabToSession(portalContext.getCurrentUser().getId(), tab);
    }

    protected void displayProcessSummaries(String tabName, ProcessSummariesType processSummaries, PortalContext portalContext) {
        List<TabRowValue> rows = new ArrayList<>();
        for(ProcessSummaryType processSummaryType : processSummaries.getProcessSummary()) {
            rows.add(createProcessSummaryRowValue(processSummaryType, processSummaryType.getVersionSummaries().get(0)));
        }

        TabHeader tabHeader;
        if(rows.get(0).size() > 7) {
            tabHeader = createTabHeader("Score", "Name", "Id", "Original language", "Domain", "Ranking", "Latest version", "Owner");
        }else {
            tabHeader = createTabHeader("Name", "Id", "Original language", "Domain", "Ranking", "Latest version", "Owner");
        }

        addTab(tabName, "img/icon/bpmn-22x22.png", rows, tabHeader, new ProcessTabItemExecutor(portalContext.getMainController()), portalContext);
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
