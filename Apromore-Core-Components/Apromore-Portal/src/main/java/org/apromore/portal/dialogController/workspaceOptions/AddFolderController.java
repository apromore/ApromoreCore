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

package org.apromore.portal.dialogController.workspaceOptions;

import java.io.IOException;

import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.*;

import org.apromore.dao.model.User;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.notification.Notification;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.DialogException;

public class AddFolderController extends BaseController {

    private MainController mainController;
    private Window folderEditWindow;
    private Button btnSave;
    private Button btnCancel;
    private Textbox txtName;
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(AddFolderController.class);

    public AddFolderController(MainController mainController, User currentUser, FolderType currentFolder) throws DialogException {
        this.mainController = mainController;

        try {
            if (!ItemHelpers.isOwner(currentUser, currentFolder)) {
                Notification.error(Labels.getLabel("portal_onlyOwnerCanAddFolder_message"));
                return;
            }
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Apromore", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        try {
            final Window win = (Window) Executions.createComponents("macros/folderCreate.zul", null, null);
            this.folderEditWindow = (Window) win.getFellow("winFolderCreate");
            this.txtName = (Textbox) this.folderEditWindow.getFellow("txtName");
            this.btnSave = (Button) this.folderEditWindow.getFellow("btnSave");
            this.btnCancel = (Button) this.folderEditWindow.getFellow("btnCancel");

            folderEditWindow.addEventListener("onLater", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    save();
                    Clients.clearBusy();
                }
            });
            win.addEventListener("onOK", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    submit();
                }
            });
            btnSave.addEventListener("onClick", new EventListener<Event>() {
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
            throw new DialogException("Error in AddFolderController: " + e.getMessage());
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
                Messagebox.show(Labels.getLabel("portal_noEmptyName_message"), "Apromore", Messagebox.OK, Messagebox.ERROR);
                return;
            }

            LOGGER.info("Creating folder " + folderName);
            String userId = UserSessionManager.getCurrentUser().getId();
            FolderType currentFolder = this.mainController.getPortalSession().getCurrentFolder();
            int currentParentFolderId = currentFolder == null || currentFolder.getId() == 0 ? 0 : currentFolder.getId();
            this.mainController.getService().createFolder(userId, folderName, currentParentFolderId);
            this.mainController.reloadSummaries();

        } catch (WrongValueException ex) {
            LOGGER.debug("Unable to create folder", ex);
            // Messagebox.show(Labels.getLabel("portal_invalidValue_message"), "Apromore", Messagebox.OK, Messagebox.ERROR);
            return;

        } catch (Exception ex) {
            if (ex.getCause() instanceof NotAuthorizedException || ex instanceof NotAuthorizedException) {
                Messagebox.show(Labels.getLabel("portal_noAuthorizedRename_message"), "Apromore", Messagebox.OK, Messagebox.ERROR);
            }
            LOGGER.warn("Unable to create folder", ex);
        }
        this.folderEditWindow.detach();
    }
}
