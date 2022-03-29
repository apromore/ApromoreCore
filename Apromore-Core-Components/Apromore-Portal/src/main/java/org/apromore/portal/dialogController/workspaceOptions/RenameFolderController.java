/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
import java.util.Arrays;

import org.apromore.exception.NotAuthorizedException;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.DialogException;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class RenameFolderController extends BaseController {

  private MainController mainController;
  private Window folderEditWindow;
  private Button btnSave;
  private Button btnCancel;
  private Textbox txtName;
  private int folderId;
  private String folderName;
  private Logger LOGGER = PortalLoggerFactory.getLogger(AddFolderController.class);

  public RenameFolderController(MainController mainController, int folderId, String name)
      throws DialogException {
    this.mainController = mainController;

    try {
      final Window win =
          (Window) Executions.createComponents("~./macros/folderRename.zul", null, null);
      this.folderEditWindow = (Window) win.getFellow("winFolderRename");
      this.txtName = (Textbox) this.folderEditWindow.getFellow("txtName");
      this.txtName.setValue(name);
      this.txtName.setSelectionRange(0, name.length());
      this.folderName=name;
      this.btnSave = (Button) this.folderEditWindow.getFellow("btnSave");
      this.btnCancel = (Button) this.folderEditWindow.getFellow("btnCancel");
      this.folderId = folderId;
      if(!this.mainController.getWorkspaceService().hasWritePermissionOnFolder(mainController.getSecurityService().getUserByName(UserSessionManager.getCurrentUser().getUsername()), Arrays.asList(this.folderId)))
      {
    	  Notification.error(Labels.getLabel("portal_noPrivilegeRename_message"));
    	  if(this.folderEditWindow!=null) {
    		  this.folderEditWindow.detach();
    	  }
		  return;
      }

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
      Button resetB = (Button) this.folderEditWindow.getFellow("resetButton");
      resetB.addEventListener("onClick", event -> {
    	    resetFolderName();
      });
      win.doModal();
    } catch (Exception e) {
      throw new DialogException("Error in RenameFolderController: " + e.getMessage());
    }
  }

  protected void resetFolderName() {
	  txtName.setText(this.folderName);
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
        Messagebox.show(Labels.getLabel("portal_noEmptyName_message"), "Apromore", Messagebox.OK,
            Messagebox.ERROR);
        return;
      }
      if(!this.mainController.getWorkspaceService().hasWritePermissionOnFolder(mainController.getSecurityService().getUserByName(UserSessionManager.getCurrentUser().getUsername()), Arrays.asList(this.folderId)))
      {
    	  Notification.error(Labels.getLabel("portal_noPrivilegeRename_message"));
    	  this.folderEditWindow.detach();
		  return;
      }
      LOGGER.info("Rename folder " + folderName);
      this.mainController.getManagerService().updateFolder(this.folderId, folderName,
          UserSessionManager.getCurrentUser().getUsername());
      this.mainController.reloadSummariesWithOpenTreeItems(this.mainController.getNavigationController().getAllOpenFolderItems());
      this.folderEditWindow.detach();

    } catch (WrongValueException ex) {
      LOGGER.debug("Unable to rename folder", ex);
      // Messagebox.show(Labels.getLabel("portal_invalidValue_message"), "Apromore", Messagebox.OK,
      // Messagebox.ERROR);
      return;

    } catch (Exception ex) {
      if (ex.getCause() instanceof NotAuthorizedException || ex instanceof NotAuthorizedException) {
        Messagebox.show(Labels.getLabel("portal_noAuthorizedRename_message"), "Apromore",
            Messagebox.OK, Messagebox.ERROR);
      }
      LOGGER.warn("Unable to rename folder", ex);
    }
    this.folderEditWindow.detach();
  }
}
