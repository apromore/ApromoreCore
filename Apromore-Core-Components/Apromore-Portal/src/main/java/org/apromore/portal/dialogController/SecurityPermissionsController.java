/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.dialogController;

import java.util.Collections;
import java.util.List;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.GroupAccessType;
import org.apromore.model.UserType;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.apromore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * Used to setup access groups for processes and folders.
 */
public class SecurityPermissionsController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityPermissionsController.class);

    private Listbox lstPermissions;
    private SecuritySetupController securitySetupController;

    public SecurityPermissionsController(SecuritySetupController securitySetupController, Window win) throws DialogException {
        this.securitySetupController = securitySetupController;
        this.lstPermissions = (Listbox)win.getFellow("existingPermissions").getFellow("lstPermissions");
    }

    @SuppressWarnings("unchecked")
    public void loadUsers(final int id, FolderTreeNodeTypes type){
        List<GroupAccessType> groups;
        ManagerService service = securitySetupController.getMainController().getService();
        switch (type) {
        case Folder:
            groups = service.getFolderGroups(id);
            break;

        case Process:
            groups = service.getProcessGroups(id);
            break;

        case Log:
            groups = service.getLogGroups(id);
            break;

        default:
            groups = Collections.emptyList();
        }

        lstPermissions.getItems().clear();
        lstPermissions.setPageSize(6);
        UserSessionManager.setCurrentSecurityItem(id);
        UserSessionManager.setCurrentSecurityType(type);
        boolean hasOwnership = UserSessionManager.getCurrentSecurityOwnership();

        for (final GroupAccessType group : groups) {
            Listitem newItem = new Listitem();
            newItem.appendChild(new Listcell(group.getName()));
            newItem.setHeight("20px");

            Checkbox chkRead = new Checkbox();
            chkRead.setChecked(group.isHasRead());
            chkRead.setDisabled(true);
            Listcell cellRead = new Listcell();
            cellRead.appendChild(chkRead);
            newItem.appendChild(cellRead);

            Checkbox chkWrite = new Checkbox();
            chkWrite.setChecked(group.isHasWrite());
            chkWrite.setDisabled(!hasOwnership);
            Listcell cellWrite = new Listcell();
            cellWrite.appendChild(chkWrite);
            newItem.appendChild(cellWrite);

            Checkbox chkOwner = new Checkbox();
            chkOwner.setChecked(group.isHasOwnership());
            chkOwner.setDisabled(!hasOwnership);
            Listcell cellOwner = new Listcell();
            cellOwner.appendChild(chkOwner);
            newItem.appendChild(cellOwner);

            Button btnSave = new Button();
            btnSave.setLabel("Save");
            btnSave.setDisabled(!hasOwnership);
            btnSave.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    Component target = event.getTarget();
                    Listitem listItem = (Listitem)target.getParent().getParent();
                    List<Component> cells = listItem.getChildren();
                    if (cells.size() == 5){
                        Checkbox chkWrite = (Checkbox)cells.get(2).getChildren().get(0);
                        Checkbox chkOwner = (Checkbox)cells.get(3).getChildren().get(0);
                        if (chkWrite != null && chkOwner != null){
                            String message = "";
                            switch (type) {
                            case Folder:
                                message = service.saveFolderPermissions(id, group.getGroupId(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                break;
                            case Process:
                                message = service.saveProcessPermissions(id, group.getGroupId(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                break;
                            case Log:
                                message = service.saveLogPermissions(id, group.getGroupId(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                break;
                            }
                            if (message.isEmpty()){
                                Messagebox.show("Successfully saved permissions.", "Success", Messagebox.OK, Messagebox.INFORMATION);
                            } else {
                                Messagebox.show(message, "Error", Messagebox.OK, Messagebox.ERROR);
                            }
                        }
                    }
                }
            });
            Button btnRemove = new Button();
            btnRemove.setLabel("Delete");
            btnRemove.setDisabled(!hasOwnership);
            btnRemove.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    Component target = event.getTarget();
                    Listitem listItem = (Listitem)target.getParent().getParent();
                    List<Component> cells = listItem.getChildren();
                    String message = "";
                    if (cells.size() == 5){
                        switch (type) {
                        case Folder:
                            message = service.removeFolderPermissions(id, group.getGroupId());
                            break;
                        case Process:
                            message = service.removeProcessPermissions(id, group.getGroupId());
                            break;
                        case Log:
                            message = service.removeLogPermissions(id, group.getGroupId());
                            break;
                        }
                        if (message.isEmpty()){
                            Messagebox.show("Successfully removed permissions.", "Success", Messagebox.OK, Messagebox.INFORMATION);
                            loadUsers(id, type);
                        } else {
                            Messagebox.show(message, "Error", Messagebox.OK, Messagebox.ERROR);
                        }

                    }
                }
            });
            Listcell cellCommand = new Listcell();
            cellCommand.appendChild(btnSave);
            cellCommand.appendChild(btnRemove);
            newItem.appendChild(cellCommand);

            lstPermissions.getItems().add(newItem);
        }
    }
}
