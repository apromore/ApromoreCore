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
            rows.add(createProcessSummaryRowValue(processSummaryType));
        }

        TabHeader tabHeader = null;
        if(rows.get(0).size() > 7) {
            tabHeader = createTabHeader("Score", "Name", "Id", "Original language", "Domain", "Ranking", "Latest version", "Owner");
        }else {
            tabHeader = createTabHeader("Name", "Id", "Original language", "Domain", "Ranking", "Latest version", "Owner");
        }

        addTab(tabName, "img/icon/bpmn-22x22.png", rows, tabHeader, new ProcessTabItemExecutor(portalContext.getMainController()), portalContext);
    }

//    protected TabItemExecutor createTabItemExecutor(final PortalContext portalContext) {
//        return new TabItemExecutor(portalContext) {
//
//            @Override
//            public void execute(TabItem listItem) {
//                String instruction = "";
//
//                ProcessSummaryType processSummaryType = new ProcessSummaryType();
//                int offset = 0;
//                if(listItem.getTabRowValue().size() > 7) offset++;
//                processSummaryType.setName((String) listItem.getTabRowValue().get(offset++));
//                processSummaryType.setId((Integer) listItem.getTabRowValue().get(offset++));
//                processSummaryType.setOriginalNativeType((String) listItem.getTabRowValue().get(offset++));
//                processSummaryType.setDomain((String) listItem.getTabRowValue().get(offset++));
//                processSummaryType.setRanking((String) listItem.getTabRowValue().get(offset++));
//                processSummaryType.setLastVersion((String) listItem.getTabRowValue().get(offset++));
//                processSummaryType.setOwner((String) listItem.getTabRowValue().get(offset++));
//
//                VersionSummaryType versionSummaryType = new VersionSummaryType();
//
//                EditSessionType editSession = createEditSession(portalContext, processSummaryType, versionSummaryType, processSummaryType.getOriginalNativeType(), "");
//
//                try {
//                    String id = UUID.randomUUID().toString();
//                    PluginPropertiesHelper pluginPropertiesHelper = new PluginPropertiesHelper();
//                    SignavioSession session = new SignavioSession(editSession, null, this, processSummaryType, versionSummaryType, null, null, pluginPropertiesHelper.readPluginProperties(Canoniser.DECANONISE_PARAMETER));
//                    UserSessionManager.setEditSession(id, session);
//
//                    String url = "macros/openModelInSignavio.zul?id=" + id;
//                    instruction += "window.open('" + url + "');";
//
//                    Clients.evalJavaScript(instruction);
//                } catch (Exception e) {
//                    Messagebox.show("Cannot edit " + processSummaryType.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
//                }
//            }
//        }
//    }

    private EditSessionType createEditSession(final PortalContext portalContext, final ProcessSummaryType process, final VersionSummaryType version, final String nativeType, final String annotation) {

        EditSessionType editSession = new EditSessionType();

        editSession.setDomain(process.getDomain());
        editSession.setNativeType(nativeType.equals("XPDL 2.2")?"BPMN 2.0":nativeType);
        editSession.setProcessId(process.getId());
        editSession.setProcessName(process.getName());
        editSession.setUsername(portalContext.getCurrentUser().getUsername());
        editSession.setPublicModel(process.isMakePublic());
        editSession.setOriginalBranchName(version.getName());
        editSession.setOriginalVersionNumber(version.getVersionNumber());
        editSession.setCurrentVersionNumber(version.getVersionNumber());
        editSession.setMaxVersionNumber(findMaxVersion(process));

        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        if (annotation == null) {
            editSession.setWithAnnotation(false);
        } else {
            editSession.setWithAnnotation(true);
            editSession.setAnnotation(annotation);
        }

        return editSession;
    }

    private String findMaxVersion(ProcessSummaryType process) {
        Version versionNum;
        Version max = new Version(0, 0);
        for (VersionSummaryType version : process.getVersionSummaries()) {
            versionNum = new Version(version.getVersionNumber());
            if (versionNum.compareTo(max) > 0) {
                max = versionNum;
            }
        }
        return max.toString();
    }

    protected ProcessSummaryRowValue createProcessSummaryRowValue(ProcessSummaryType processSummaryType) {
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setMaximumFractionDigits(3);

        ProcessSummaryRowValue processSummaryRowValue = new ProcessSummaryRowValue();
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
