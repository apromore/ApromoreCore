/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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
