/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController.workspaceOptions;

import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.IOException;

public class AddFolderController extends BaseController {

    private MainController mainController;
    private Window folderEditWindow;
    private Button btnSave;
    private Button btnCancel;
    private Textbox txtName;
    private Checkbox checkboxGED;
    private int folderId;

    public AddFolderController(MainController mainController, int folderId, String name, Boolean isGEDMatrixReady) throws DialogException {
        this.mainController = mainController;

        try {
//            final Window win = (Window) Executions.createComponents("macros/folderRename.zul", null, null);
            final Window win = getWindow(name, isGEDMatrixReady);

            if (folderId != 0) {
                this.folderId = folderId;
                if(name != null) txtName.setValue(name);
                if(isGEDMatrixReady != null) checkboxGED.setValue(isGEDMatrixReady);
            }

            folderEditWindow.addEventListener("onLater", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    save();
                    Clients.clearBusy();
                }
            });
            btnSave.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    Clients.showBusy("Processing...");
                    Events.echoEvent("onLater", folderEditWindow, null);
                }
            });
            btnCancel.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            win.doModal();
        } catch (Exception e) {
            throw new DialogException("Error in AddFolderController: " + e.getMessage());
        }
    }

    private  Window getWindow(String name, Boolean isGEDMatrixReady) {
        Window win = null;
        if(isGEDMatrixReady != null && name != null) {
            win = (Window) Executions.createComponents("macros/folderCreate.zul", null, null);
            this.folderEditWindow = (Window) win.getFellow("winFolderCreate");
            this.txtName = (Textbox) this.folderEditWindow.getFellow("txtName");
            this.checkboxGED = (Checkbox) this.folderEditWindow.getFellow("checkboxGED");
        }else if(isGEDMatrixReady != null) {
            win = (Window) Executions.createComponents("macros/folderGED.zul", null, null);
            this.folderEditWindow = (Window) win.getFellow("winFolderGED");
            this.checkboxGED = (Checkbox) this.folderEditWindow.getFellow("checkboxGED");
        }else if(name != null) {
            win = (Window) Executions.createComponents("macros/folderRename.zul", null, null);
            this.folderEditWindow = (Window) win.getFellow("winFolderRename");
            this.txtName = (Textbox) this.folderEditWindow.getFellow("txtName");
        }

        this.btnSave = (Button) this.folderEditWindow.getFellow("btnSave");
        this.btnCancel = (Button) this.folderEditWindow.getFellow("btnCancel");

        return win;
    }

    private void cancel() throws IOException {
        this.folderEditWindow.detach();
    }

    private void save() throws InterruptedException {
        try {
            String folderName = txtName!=null?txtName.getValue():null;
            boolean isGEDMatrixReady = checkboxGED!=null?checkboxGED.isChecked():null;
            if (folderName.isEmpty()) {
                Messagebox.show("Name cannot be empty.", "Attention", Messagebox.OK, Messagebox.ERROR);
            } else {
                String userId = UserSessionManager.getCurrentUser().getId();
                int currentParentFolderId = UserSessionManager.getCurrentFolder() == null || UserSessionManager.getCurrentFolder().getId() == 0 ? 0 : UserSessionManager.getCurrentFolder().getId();
                if (this.folderId == 0) {
                    this.mainController.getService().createFolder(userId, folderName, currentParentFolderId, isGEDMatrixReady);
                } else {
                    this.mainController.getService().updateFolder(this.folderId, folderName, isGEDMatrixReady);
                }

                this.mainController.reloadProcessSummaries();
            }
        } catch (Exception ex) {

        }
        this.folderEditWindow.detach();
    }
}
