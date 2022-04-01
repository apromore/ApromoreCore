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

package org.apromore.plugin.portal.useradmin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.useradmin.listbox.RoleModel;
import org.apromore.service.SecurityService;
import org.apromore.zk.label.LabelSupplier;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DeleteRoleController extends SelectorComposer<Window> implements LabelSupplier {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(DeleteRoleController.class);

    @WireVariable("securityService")
    private SecurityService securityService;

    private Map<String, Object> argMap = (Map<String, Object>) Executions.getCurrent().getArg();
    private List<Role> rolesToDelete = (List<Role>) argMap.get("rolesToDelete");
    private Role selectedRole = (Role) argMap.get("selectedRole");
    private String roleLabel = (String) argMap.get("roleLabel");
    private Map<String, String> roleMap = (Map<String, String>) argMap.get("displayRoleNameMap");

    private ListModelList<User> assignedUserModel;
    private ListModelList<RoleModel> roleModel;

    @Wire
    Listbox assignedUsersListbox;
    @Wire
    Listbox selectNewRoleListbox;
    @Wire
    Label deleteRoleWithUserInfo;

    @Override
    public String getBundleName() {
        return "useradmin";
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        deleteRoleWithUserInfo.setValue(MessageFormat.format(getLabel("deleteRoleWithUser_text"),
            getDisplayRoleName(selectedRole.getName())));
        loadUserList();
        loadRoleList();
    }

    @Listen("onClick = #btnApply")
    public void onClickBtnApply() {
        Messagebox.show(
            getConfirmRoleTransferMessage(),
            Labels.getLabel("brand_name", "Apromore"),
            new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.CANCEL},
            Messagebox.QUESTION,
            e -> {
                String buttonName = e.getName();
                if (Messagebox.ON_YES.equals(buttonName)) {
                    transferRoleAndDelete();
                    getSelf().detach();
                }
            }
        );
    }

    @Listen("onClick = #btnCancel")
    public void onClickBtnCancel() {
        getSelf().detach();
    }

    private String getDisplayRoleName(String originalName) {
        return roleMap.getOrDefault(originalName, originalName);
    }

    private void loadUserList() {
        assignedUserModel = new ListModelList<>(selectedRole.getUsers());
        assignedUserModel.setMultiple(true);
        assignedUsersListbox.setModel(assignedUserModel);
    }

    private void loadRoleList() {
        List<RoleModel> roleModels = securityService.getAllRoles().stream()
            .filter(r -> !rolesToDelete.contains(r))
            .map(r -> new RoleModel(r, getDisplayRoleName(r.getName())))
            .sorted((r1, r2) -> r1.getLabel().compareToIgnoreCase(r2.getLabel()))
            .collect(Collectors.toList());
        roleModel = new ListModelList<>(roleModels, false);
        roleModel.setMultiple(false);
        selectNewRoleListbox.setModel(roleModel);
    }

    private String getConfirmRoleTransferMessage() {
        int numUsersToNewRole = roleModel.getSelection().isEmpty() ? 0 : assignedUserModel.getSelection().size();
        int numUsersToNoRole = assignedUserModel.size() - numUsersToNewRole;
        String newRoleName =
            roleModel.getSelection().stream().map(r -> r.getRole().getName()).findFirst().orElse("");
        String newRoleDisplayName = getDisplayRoleName(newRoleName);

        if (numUsersToNewRole == 0) {
            return MessageFormat.format(getLabel("role_delete_confirm_all_users_no_role"), numUsersToNoRole);
        } else if (numUsersToNoRole == 0) {
            return MessageFormat.format(getLabel("role_delete_confirm_all_users_new_role"),
                numUsersToNewRole, newRoleDisplayName);
        } else {
            return MessageFormat.format(getLabel("role_delete_confirm_user_role_transfer"),
                numUsersToNoRole, numUsersToNewRole, newRoleDisplayName);
        }
    }

    private void transferRoleAndDelete() {
        //Transfer to new role
        if (!assignedUserModel.getSelection().isEmpty() && !roleModel.getSelection().isEmpty()) {
            String newRoleName =
                roleModel.getSelection().stream().map(r -> r.getRole().getName()).findFirst().orElse("");
            Role newRole = securityService.findRoleByName(newRoleName);
            //Only add the user if it isn't already in this role
            List<User> usersToAdd = new ArrayList<>();
            assignedUserModel.getSelection().forEach(u -> {
                if (newRole.getUsers().stream().map(User::getRowGuid).noneMatch(guid -> guid.equals(u.getRowGuid()))) {
                    usersToAdd.add(u);
                }
            });
            newRole.getUsers().addAll(usersToAdd);
            securityService.updateRole(newRole);
        }

        //Delete old role
        LOGGER.info("Deleting role {}", roleLabel);
        securityService.deleteRole(selectedRole);
        Map<String, String> dataMap = Map.of("type", "DELETE_ROLE");
        EventQueues
            .lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
            .publish(new Event("Role(s) Deleted", null, dataMap));
    }
}
