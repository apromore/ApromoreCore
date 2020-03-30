package org.apromore.plugin.portal.file;

import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import org.apromore.model.*;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.common.*;
import org.apromore.portal.dialogController.*;
import org.apromore.portal.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.*;

public class DownloadSelectionPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(DownloadSelectionPlugin.class);

    private String label = "Download";
    private String groupLabel = "File";


    // PortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    @Override
    public String getIconPath() {
        return "download.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            MainController mainC = (MainController) portalContext.getMainController();

            if(mainC.getSelectedElements().size() == 1) {
                SummaryType summaryType = mainC.getSelectedElements().iterator().next();
                System.out.println(summaryType);
                if (summaryType instanceof LogSummaryType) {
                    exportLog(mainC);
                } else if (summaryType instanceof ProcessSummaryType) {
                    exportNative(mainC, portalContext);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to download selection", e);
            Messagebox.show("Unable to download selection");
        }
    }

    /**
     * Export all selected process versions, each of which in a native format to be chosen by the user
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     * @throws org.apromore.portal.exception.ExceptionFormats
     */
    protected void exportNative(MainController mainC, PortalContext portalContext) throws SuspendNotAllowedException, InterruptedException, ExceptionFormats, ParseException {
        mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){
                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                HashMap<SummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        TabListitem tabItem=(TabListitem)item;
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                    }
                }
                if(processVersion.keySet().size()>0){
                    new ExportListNativeController(mainC, null, processVersion);
                    return;
                }
            }
        }

        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedElementsAndVersions();
        if (selectedProcessVersions.size() != 0) {
            new ExportListNativeController(mainC, null, selectedProcessVersions);
        } else {
            mainC.displayMessage("No process version selected.");
        }
    }

    /**
     * Export all selected process versions, each of which in a native format to be chosen by the user
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     * @throws org.apromore.portal.exception.ExceptionFormats
     */
    protected void exportLog(MainController mainC) throws Exception {
        if(mainC.getSelectedElements().size() == 1) {
            SummaryType summaryType = mainC.getSelectedElements().iterator().next();
            System.out.println(summaryType);
            if(summaryType instanceof LogSummaryType) {
                ExportLogResultType exportResult = mainC.getService().exportLog(summaryType.getId(), summaryType.getName());
                try (InputStream native_is = exportResult.getNative().getInputStream()) {
                    mainC.showPluginMessages(exportResult.getMessage());
                    Filedownload.save(native_is, "application/x-gzip", summaryType.getName() + ".xes.gz");
                }
            }
        }
    }
}
