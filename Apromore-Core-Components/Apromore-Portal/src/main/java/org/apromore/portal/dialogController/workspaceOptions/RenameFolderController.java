/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.portal.dialogController.workspaceOptions;

import org.apromore.exception.NotAuthorizedException;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.logging.Logger;

public class RenameFolderController extends BaseController {

    private MainController mainController;
    private Window folderEditWindow;
    private Button btnSave;
    private Button btnCancel;
    private Textbox txtName;
    private int folderId;
    private Logger LOGGER = Logger.getLogger(AddFolderController.class.getCanonicalName());

    public RenameFolderController(MainController mainController, int folderId, String name) throws DialogException {
        this.mainController = mainController;

        try {
            final Window win = (Window) Executions.createComponents("macros/folderRename.zul", null, null);
            this.folderEditWindow = (Window) win.getFellow("winFolderRename");
            this.txtName = (Textbox) this.folderEditWindow.getFellow("txtName");
            this.txtName.setValue(name);
            this.btnSave = (Button) this.folderEditWindow.getFellow("btnSave");
            this.btnCancel = (Button) this.folderEditWindow.getFellow("btnCancel");
            this.folderId = folderId;

            folderEditWindow.addEventListener("onLater", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    save();
                    Clients.clearBusy();
                }
            });
            btnSave.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    submit();
                }
            });
            win.addEventListener("onOK", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    submit();
                }
            });
            btnCancel.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            win.doModal();
        } catch (Exception e) {
            throw new DialogException("Error in RenameFolderController: " + e.getMessage());
        }
    }

    private void submit() throws Exception {
        Clients.showBusy("Processing...");
        Events.echoEvent("onLater", folderEditWindow, null);
    }

    private void cancel() throws IOException {
        this.folderEditWindow.detach();
    }

    private void save() throws InterruptedException {
        try {
            String folderName = txtName.getValue().trim();
            if (folderName.isEmpty()) {
                Messagebox.show("Name cannot be empty.", "Attention", Messagebox.OK, Messagebox.ERROR);
                return;
            }

            LOGGER.warning("folderName " + folderName);
            this.mainController.getService().updateFolder(this.folderId, folderName, UserSessionManager.getCurrentUser().getUsername());
            this.mainController.reloadSummaries();
            this.folderEditWindow.detach();
        } catch (Exception ex) {
            if (ex.getCause() instanceof NotAuthorizedException || ex instanceof NotAuthorizedException) {
                Messagebox.show("You are not authorized to perform this operation. Contact your system administrator to gain relevant access rights for the folder or file you are trying to rename.", "Apromore", Messagebox.OK, Messagebox.ERROR);
            }
            if (ex instanceof WrongValueException) {
                // Messagebox.show("You have entered invalid value.", "Apromore", Messagebox.OK, Messagebox.ERROR);
                return;
            }
            LOGGER.warning("Exception ");
            StackTraceElement[] trace = ex.getStackTrace();
            for (StackTraceElement traceElement : trace)
                LOGGER.warning("\tat " + traceElement);

        }
        this.folderEditWindow.detach();
    }
}
