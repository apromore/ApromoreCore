/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

import java.util.Collections;
import java.util.List;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.GroupAccessType;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
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
            if (hasOwnership){
                if (true /*!(group.getGroupId().equals(UserSessionManager.getCurrentUser().getGroup().getId()))*/) {
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
                    btnSave.addEventListener("onClick",
                            new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                    Component target = event.getTarget();
                                    Listitem listItem = (Listitem)target.getParent().getParent();
                                    List<Component> cells = listItem.getChildren();
                                    FolderTreeNodeTypes selectedType = UserSessionManager.getCurrentSecurityType();
                                    if (cells.size() == 5){
                                        Checkbox chkWrite = (Checkbox)cells.get(2).getChildren().get(0);
                                        Checkbox chkOwner = (Checkbox)cells.get(3).getChildren().get(0);
                                        if (chkWrite != null && chkOwner != null){
                                            String message = "";
                                            switch (selectedType) {
                                            case Folder:
                                                message = securitySetupController.getMainController().getService().saveFolderPermissions(id, group.getGroupId(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                                break;
                                            case Process:
                                                message = securitySetupController.getMainController().getService().saveProcessPermissions(id, group.getGroupId(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                                break;
                                            case Log:
                                                message = securitySetupController.getMainController().getService().saveLogPermissions(id, group.getGroupId(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                                break;
                                            }
                                            if (message.isEmpty()){
                                                Messagebox.show("Successfully saved permissions.", "Success", Messagebox.OK,
                                                        Messagebox.INFORMATION);
                                            }
                                            else{
                                                Messagebox.show(message, "Error", Messagebox.OK,
                                                        Messagebox.ERROR);
                                            }
                                        }
                                    }
                                }
                            });
                    Button btnRemove = new Button();
                    btnRemove.setLabel("Delete");
                    btnRemove.setDisabled(!hasOwnership);
                    btnRemove.addEventListener("onClick",
                            new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                    Component target = event.getTarget();
                                    Listitem listItem = (Listitem)target.getParent().getParent();
                                    List<Component> cells = listItem.getChildren();
                                    FolderTreeNodeTypes selectedType = UserSessionManager.getCurrentSecurityType();
                                    String message = "";
                                    if (cells.size() == 5){
                                        switch (selectedType) {
                                        case Folder:
                                            message = securitySetupController.getMainController().getService().removeFolderPermissions(id, group.getGroupId());
                                            break;
                                        case Process:
                                            message = securitySetupController.getMainController().getService().removeProcessPermissions(id, group.getGroupId());
                                            break;
                                        case Log:
                                            message = securitySetupController.getMainController().getService().removeLogPermissions(id, group.getGroupId());
                                            break;
                                        }
                                        if (message.isEmpty()){
                                            Messagebox.show("Successfully removed permissions.", "Success", Messagebox.OK,
                                                    Messagebox.INFORMATION);
                                            loadUsers(id, selectedType);
                                        }
                                        else{
                                            Messagebox.show(message, "Error", Messagebox.OK,
                                                    Messagebox.ERROR);
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

    }
}
