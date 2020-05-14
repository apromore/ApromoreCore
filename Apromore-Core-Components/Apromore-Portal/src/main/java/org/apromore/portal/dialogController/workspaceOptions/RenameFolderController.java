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
import java.util.logging.Logger;

public class RenameFolderController extends BaseController {

    private MainController mainController;
    private Window folderEditWindow;
    private Button btnSave;
    private Button btnCancel;
    private Button btnReset;
    private Textbox txtName;
    private String prevName;
    private int folderId;
    private Logger LOGGER = Logger.getLogger(AddFolderController.class.getCanonicalName());

    public RenameFolderController(MainController mainController, int folderId, String name) throws DialogException {
        this.mainController = mainController;
        this.prevName = name;

        try {
            final Window win = (Window) Executions.createComponents("macros/folderRename.zul", null, null);
            this.folderEditWindow = (Window) win.getFellow("winFolderRename");
            this.txtName = (Textbox) this.folderEditWindow.getFellow("txtName");
            this.txtName.setValue(name);
            this.btnSave = (Button) this.folderEditWindow.getFellow("btnSave");
            this.btnCancel = (Button) this.folderEditWindow.getFellow("btnCancel");
            this.btnReset = (Button) this.folderEditWindow.getFellow("btnReset");
            this.folderId = folderId;

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
            btnReset.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    reset();
                }
            });
            win.doModal();
        } catch (Exception e) {
            throw new DialogException("Error in RenameFolderController: " + e.getMessage());
        }
    }

    private void cancel() throws IOException {
        this.folderEditWindow.detach();
    }

    private void reset() {
        this.txtName.setValue(prevName);
    }

    private void save() throws InterruptedException {
        try {
            String folderName = txtName.getValue();
            if (folderName.isEmpty()) {
                Messagebox.show("Name cannot be empty.", "Attention", Messagebox.OK, Messagebox.ERROR);
                return;
            }

            LOGGER.warning("folderName " + folderName);
            this.mainController.getService().updateFolder(this.folderId, folderName, UserSessionManager.getCurrentUser().getUsername());
            this.mainController.reloadSummaries();

        } catch (Exception ex) {
            LOGGER.warning("Exception ");
            StackTraceElement[] trace = ex.getStackTrace();
            for (StackTraceElement traceElement : trace)
                LOGGER.warning("\tat " + traceElement);

        }
        this.folderEditWindow.detach();
    }
}
