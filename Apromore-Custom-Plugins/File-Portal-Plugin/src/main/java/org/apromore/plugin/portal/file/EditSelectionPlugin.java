package org.apromore.plugin.portal.file;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.TabListitem;
import org.apromore.portal.common.TabQuery;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.EditListProcessesController2;
import org.apromore.portal.dialogController.MainController;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;

public class EditSelectionPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(EditSelectionPlugin.class);

    private String label = "Edit model";
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
        return "model-edit.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            MainController mainC = (MainController) portalContext.getMainController();

            mainC.eraseMessage();

            List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

            for(Tab tab : tabs){
                if(tab.isSelected() && tab instanceof TabQuery){

                    TabQuery tabQuery=(TabQuery)tab;
                    List<Listitem> items=tabQuery.getListBox().getItems();

                    for(Listitem item : items){
                        if(item.isSelected() && item instanceof TabListitem){
                            TabListitem tabItem=(TabListitem)item;
                            HashMap<SummaryType, List<VersionSummaryType>> processVersion = new HashMap<>();
                            processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                            new EditListProcessesController2(mainC, null, processVersion);
                            return;
                        }
                    }
                }
            }
            Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedElementsAndVersions();
            if (selectedProcessVersions.size() != 0) {
                new EditListProcessesController2(mainC, null, selectedProcessVersions);
            } else {
                mainC.displayMessage("No process version selected.");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to edit selection", e);
            Messagebox.show("Unable to edit selection");
        }
    }
}
