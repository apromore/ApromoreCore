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
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.useradmin;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.model.PermissionType;
import org.apromore.service.SecurityService;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class EditRolePermissionController extends SelectorComposer<Window> implements LabelSupplier {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(EditRolePermissionController.class);
    private static final PermissionType[] MANAGE_USERS_PERMISSIONS = {
        PermissionType.USERS_VIEW, PermissionType.USERS_EDIT,
        PermissionType.GROUPS_EDIT, PermissionType.ROLES_EDIT};
    private static final String CREATE_MODE = "CREATE";
    private static final String EDIT_MODE = "EDIT";
    private static final String VIEW_MODE = "VIEW";

    private final PortalContext portalContext =
        (PortalContext) Executions.getCurrent().getArg().get("portalContext");
    private final SecurityService securityService =
        (SecurityService) Executions.getCurrent().getArg().get("securityService");
    private final String mode = (String) Executions.getCurrent().getArg().get("mode");

    private final EnumMap<PermissionType, Checkbox> permissionToggles = new EnumMap<>(PermissionType.class);

    @Wire
    private Textbox roleNameTextbox;
    @Wire
    private Button createBtn;
    @Wire
    private Button editBtn;
    @Wire
    private Checkbox rolePermissionModelCreate;
    @Wire
    private Checkbox rolePermissionModelEdit;
    @Wire
    private Checkbox rolePermissionModelView;
    @Wire
    private Checkbox rolePermissionModelDiscoverView;
    @Wire
    private Checkbox rolePermissionModelDiscoverFull;
    @Wire
    private Checkbox rolePermissionModelPublish;
    @Wire
    private Checkbox rolePermissionFilterView;
    @Wire
    private Checkbox rolePermissionFilterFull;
    @Wire
    private Checkbox rolePermissionCalendar;
    @Wire
    private Checkbox rolePermissionLogAnim;
    @Wire
    private Checkbox rolePermissionDashView;
    @Wire
    private Checkbox rolePermissionDashFull;
    @Wire
    private Checkbox rolePermissionCheckConformance;
    @Wire
    private Checkbox rolePermissionModelSimulate;
    @Wire
    private Checkbox rolePermissionModelCompare;
    @Wire
    private Checkbox rolePermissionModelMerge;
    @Wire
    private Checkbox rolePermissionModelSimSearch;
    @Wire
    private Checkbox rolePermissionEtlPipelineCreate;
    @Wire
    private Checkbox rolePermissionEtlPipelineManage;
    @Wire
    private Checkbox rolePermissionManageAccessRights;
    @Wire
    private Checkbox rolePermissionManageUsers;

    private Role role;
    private String roleLabel;

    public EditRolePermissionController() {
        Map<String, Object> args = (Map<String, Object>) Executions.getCurrent().getArg();
        role = (Role) args.getOrDefault("role", createCustomRoleTemplate());
        roleLabel = (String) args.get("roleLabel");
    }

    @Override
    public String getBundleName() {
        return "useradmin";
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        updatePermissionToggleMap();
        populateFormWithRoleData();
        displayFormInMode(win);

        permissionToggles.values().stream().distinct()
            .forEach(c -> c.addEventListener(Events.ON_CHECK, e -> updateButtons()));
    }

    @Listen("onChange = #roleNameTextbox")
    public void onChangeName() {
        updateButtons();
    }

    @Listen("onError = #roleNameTextbox")
    public void onNameError() {
        updateButtons();
    }

    @Listen("onClick = #createBtn")
    public void onClickCreateButton() {
        boolean canEditRoles = securityService.hasAccess(portalContext.getCurrentUser().getId(),
            PermissionType.ROLES_EDIT.getId());
        if (!canEditRoles) {
            Messagebox.show(getLabel("noPermissionCreateRole_message"));
            return;
        }

        try {
            updateRoleWithFormData();
            securityService.createRole(role);

            //Publish create event
            Map<String, String> dataMap = Map.of("type", "CREATE_ROLE");
            EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
                .publish(new Event("Role Create", null, dataMap));

            getSelf().detach();
        } catch (Exception e) {
            LOGGER.error("Unable to create role", e);
            Messagebox.show(getLabel("failedCreateRole_message"));
        }

    }

    @Listen("onClick = #editBtn")
    public void onClickEditButton() {
        boolean canEditRoles = securityService.hasAccess(portalContext.getCurrentUser().getId(),
            PermissionType.ROLES_EDIT.getId());
        if (!canEditRoles) {
            Messagebox.show(getLabel("noPermissionCreateRole_message"));
            return;
        }

        try {
            updateRoleWithFormData();
            securityService.updateRole(role);

            //Publish create event
            Map<String, String> dataMap = Map.of("type", "UPDATE_ROLE");
            EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
                .publish(new Event("Role Update", null, dataMap));

            Notification.info(MessageFormat.format(getLabel("updatedRoleDetails_message"), role.getName()));
            getSelf().detach();
        } catch (Exception e) {
            LOGGER.error("Unable to create role", e);
            Messagebox.show(getLabel("failedCreateRole_message"));
        }

    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelButton() {
        getSelf().detach();
    }

    //Exclusive toggles - toggling one on triggers another to be turned off.
    @Listen("onCheck = #rolePermissionModelDiscoverView")
    public void onToggleDiscoverView() {
        if (rolePermissionModelDiscoverView.isChecked()) {
            rolePermissionModelDiscoverFull.setChecked(false);
        }
    }

    @Listen("onCheck = #rolePermissionModelDiscoverFull")
    public void onToggleDiscoverEdit() {
        if (rolePermissionModelDiscoverFull.isChecked()) {
            rolePermissionModelDiscoverView.setChecked(false);
        }
    }

    @Listen("onCheck = #rolePermissionFilterView")
    public void onToggleFilterView() {
        if (rolePermissionFilterView.isChecked()) {
            rolePermissionFilterFull.setChecked(false);
        }
    }

    @Listen("onCheck = #rolePermissionFilterFull")
    public void onToggleFilterEdit() {
        if (rolePermissionFilterFull.isChecked()) {
            rolePermissionFilterView.setChecked(false);
        }
    }

    @Listen("onCheck = #rolePermissionDashView")
    public void onToggleDashView() {
        if (rolePermissionDashView.isChecked()) {
            rolePermissionDashFull.setChecked(false);
        }
    }

    @Listen("onCheck = #rolePermissionDashFull")
    public void onToggleDashEdit() {
        if (rolePermissionDashFull.isChecked()) {
            rolePermissionDashView.setChecked(false);
        }
    }

    /**
     * Create an empty role with login permission.
     *
     * @return a custom role with login permission.
     */
    private Role createCustomRoleTemplate() {
        Role newRole = new Role();
        newRole.setDescription("Custom role");
        Set<Permission> permissions = newRole.getPermissions();
        permissions.add(securityService.getPermission(PermissionType.PORTAL_LOGIN.getName()));
        return newRole;
    }

    /**
     * Enable/Disable buttons based on the selection.
     */
    private void updateButtons() {
        boolean anyToggleOn = permissionToggles.values().stream().distinct().anyMatch(Checkbox::isChecked);
        if (CREATE_MODE.equals(mode)) {
            createBtn.setDisabled(!roleNameTextbox.isValid() || !anyToggleOn);
        } else if (EDIT_MODE.equals(mode)) {
            editBtn.setDisabled(!roleNameTextbox.isValid() || !anyToggleOn);
        }
    }

    /**
     * Update the window title based on the window mode.
     *
     * @param win the window whose title will be updated.
     */
    private void updateTitle(Window win) {
        if (CREATE_MODE.equals(mode)) {
            win.setTitle(getLabel("createRoleTitle_text", "Create role"));
        } else if (EDIT_MODE.equals(mode)) {
            win.setTitle(getLabel("editRoleTitle_text", "Edit role"));
        } else {
            win.setTitle(getLabel("viewRoleTitle_text", "View role"));
        }
    }

    /**
     * Update the form UI based on the mode.
     *
     * @param win the form window.
     */
    private void displayFormInMode(Window win) {
        updateTitle(win);

        createBtn.setVisible(CREATE_MODE.equals(mode));
        editBtn.setVisible(EDIT_MODE.equals(mode));

        permissionToggles.values().stream().distinct()
            .forEach(c -> c.setDisabled(!CREATE_MODE.equals(mode) && !EDIT_MODE.equals(mode)));
        roleNameTextbox.setReadonly(!CREATE_MODE.equals(mode) && !EDIT_MODE.equals(mode));
    }

    /**
     * Map toggles to their permission types.
     */
    private void updatePermissionToggleMap() {
        permissionToggles.put(PermissionType.MODEL_CREATE, rolePermissionModelCreate);
        permissionToggles.put(PermissionType.MODEL_EDIT, rolePermissionModelEdit);
        permissionToggles.put(PermissionType.MODEL_VIEW, rolePermissionModelView);
        permissionToggles.put(PermissionType.MODEL_DISCOVER_VIEW, rolePermissionModelDiscoverView);
        permissionToggles.put(PermissionType.MODEL_DISCOVER_EDIT, rolePermissionModelDiscoverFull);
        permissionToggles.put(PermissionType.PUBLISH_MODELS, rolePermissionModelPublish);
        permissionToggles.put(PermissionType.FILTER_VIEW, rolePermissionFilterView);
        permissionToggles.put(PermissionType.FILTER_EDIT, rolePermissionFilterFull);
        permissionToggles.put(PermissionType.CALENDAR, rolePermissionCalendar);
        permissionToggles.put(PermissionType.ANIMATE, rolePermissionLogAnim);
        permissionToggles.put(PermissionType.DASH_VIEW, rolePermissionDashView);
        permissionToggles.put(PermissionType.DASH_EDIT, rolePermissionDashFull);
        permissionToggles.put(PermissionType.CHECK_CONFORMANCE, rolePermissionCheckConformance);
        permissionToggles.put(PermissionType.SIMULATE_MODEL, rolePermissionModelSimulate);
        permissionToggles.put(PermissionType.COMPARE_MODELS, rolePermissionModelCompare);
        permissionToggles.put(PermissionType.MERGE_MODELS, rolePermissionModelMerge);
        permissionToggles.put(PermissionType.SEARCH_MODELS, rolePermissionModelSimSearch);
        permissionToggles.put(PermissionType.PIPELINE_CREATE, rolePermissionEtlPipelineCreate);
        permissionToggles.put(PermissionType.PIPELINE_MANAGE, rolePermissionEtlPipelineManage);
        permissionToggles.put(PermissionType.ACCESS_RIGHTS_MANAGE, rolePermissionManageAccessRights);
        permissionToggles.put(PermissionType.USERS_VIEW, rolePermissionManageUsers);
        permissionToggles.put(PermissionType.USERS_EDIT, rolePermissionManageUsers);
        permissionToggles.put(PermissionType.GROUPS_EDIT, rolePermissionManageUsers);
        permissionToggles.put(PermissionType.ROLES_EDIT, rolePermissionManageUsers);
    }

    /**
     * Update the role permission form based on the role object.
     */
    private void populateFormWithRoleData() {
        if (EDIT_MODE.equals(mode) || VIEW_MODE.equals(mode)) {
            //Update role name textbox
            if (!StringUtils.isEmpty(roleLabel)) {
                roleNameTextbox.setValue(roleLabel);
            } else if (!StringUtils.isEmpty(role.getName())) {
                roleNameTextbox.setValue(role.getName());
            }

            //Update toggles with existing permissions
            role.setPermissions(new HashSet<>(securityService.getRolePermissions(role.getName())));
            Set<PermissionType> permissionTypes = role.getPermissions().stream()
                .map(p -> PermissionType.getPermissionTypeById(p.getRowGuid()))
                .collect(Collectors.toSet());

            for (PermissionType permissionType : permissionTypes) {
                if (permissionToggles.containsKey(permissionType)) {
                    permissionToggles.get(permissionType).setChecked(true);
                }
            }

            //Only set manage users as checked if the user has all relevant permissions
            boolean manageUsersPermission = permissionTypes.containsAll(Arrays.asList(MANAGE_USERS_PERMISSIONS));
            rolePermissionManageUsers.setChecked(manageUsersPermission);
        }
    }

    /**
     * Update the role object based on the state of the form.
     */
    private void updateRoleWithFormData() {
        String roleName = roleNameTextbox.getValue().trim();
        role.setName(roleName);
        Set<Permission> permissions = role.getPermissions();

        permissionToggles.forEach((permissionType, checkbox) -> {
            if (checkbox.isChecked()
                && permissions.stream().noneMatch(p -> permissionType.getId().equals(p.getRowGuid()))) {
                //Add checked permissions if not in the permission set
                permissions.add(securityService.getPermission(permissionType.getName()));
            } else if (!checkbox.isChecked()) {
                //Remove unchecked permissions from the permission set
                permissions.removeIf(p -> permissionType.getId().equals(p.getRowGuid()));
            }
        });
    }

}
