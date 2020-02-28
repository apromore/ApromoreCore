/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.portal.dialogController;

import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

/**
 * Controller used to setup security for folders and processes.
 *
 * @author Igor
 */
public class SecuritySetupController extends BaseController {

    private MainController mainController;
    private SecurityFindGroupsController findGroupsController;
    private SecurityPermissionsController permissionsController;
    private SecurityFolderTreeController folderTreeController;

    public SecuritySetupController(MainController mainController) throws DialogException {
        this.mainController = mainController;
        try {
            final Window win = (Window) Executions.createComponents("/macros/securitySetup.zul", null, null);

            this.permissionsController = new SecurityPermissionsController(this, win);
            this.findGroupsController = new SecurityFindGroupsController(this, win);
            this.folderTreeController = new SecurityFolderTreeController(this, win);

            win.doModal();

            win.addEventListener("onClose", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    UserSessionManager.getMainController().loadWorkspace();
                }
            });

        } catch (Exception e) {
            throw new DialogException("Error in controller: " + e.getMessage());
        }
    }

    public SecurityPermissionsController getPermissionsController(){
        return this.permissionsController;
    }

    public SecurityFindGroupsController getFindGroupsController(){
        return this.findGroupsController;
    }

    public SecurityFolderTreeController getFolderTreeController(){
        return this.folderTreeController;
    }

    public MainController getMainController(){
        return this.mainController;
    }
}
