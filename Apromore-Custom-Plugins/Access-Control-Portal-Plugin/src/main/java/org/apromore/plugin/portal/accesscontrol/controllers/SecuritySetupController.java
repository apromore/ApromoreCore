/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal.accesscontrol.controllers;

import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.UserType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Window;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller used to setup security for folders and processes.
 *
 * @author Igor
 */
public class SecuritySetupController extends BaseController {

    private MainController mainController;
    private SecurityFolderTreeController folderTreeController;

    public SecuritySetupController(MainController mainController, UserType currentUser, Object selectedItem, boolean canShare) throws DialogException {
        this.mainController = mainController;

        Map arg = new HashMap<>();
        arg.put("selectedItem", canShare ? selectedItem : null);
        arg.put("currentUser", currentUser);
        arg.put("autoInherit", true);
        arg.put("showRelatedArtifacts", true);
        arg.put("enablePublish", config.getEnablePublish());
        try {
            final Window win = (Window) Executions.createComponents("/accesscontrol/zul/securitySetup.zul", null, arg);
            FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();

            this.folderTreeController = new SecurityFolderTreeController(this, win, currentFolder.getId());

            win.doModal();
            win.addEventListener("onClose", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    getMainController().loadWorkspace();
                }
            });
            EventQueues.lookup("accessControl", EventQueues.DESKTOP, true).subscribe(
                    new EventListener() {
                        public void onEvent(Event evt) {
                            if ("onClose".equals(evt.getName())) {
                                win.detach();
                            }
                        }
                    });

        } catch (Exception e) {
            throw new DialogException("Error in controller: " + e.getMessage());
        }
    }

    public SecurityFolderTreeController getFolderTreeController(){
        return this.folderTreeController;
    }

    public MainController getMainController(){
        return this.mainController;
    }
}
