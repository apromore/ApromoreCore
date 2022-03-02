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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.model.PermissionType;
import org.apromore.service.SecurityService;
import org.apromore.zk.label.LabelSupplier;
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

    public EditRolePermissionController() {
        Map<String, Object> args = (Map<String, Object>) Executions.getCurrent().getArg();
        role = (Role) args.getOrDefault("role", createCustomRoleTemplate());
    }

    @Override
    public String getBundleName() {
        return "useradmin";
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        updateTitle(win);
        updatePermissionToggleMap();

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
        boolean toggleOn = permissionToggles.values().stream().distinct().anyMatch(Checkbox::isChecked);
        createBtn.setDisabled(!roleNameTextbox.isValid() || !toggleOn);
    }

    /**
     * Update the window title based on the window mode.
     *
     * @param win the window whose title will be updated.
     */
    private void updateTitle(Window win) {
        if ("CREATE".equals(mode)) {
            win.setTitle(getLabel("createRoleTitle_text", "Create role"));
        }
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
     * Update the role object based on the state of the form.
     */
    private void updateRoleWithFormData() {
        String roleName = roleNameTextbox.getValue().trim();
        role.setName(roleName);
        Set<Permission> permissions = role.getPermissions();

        permissionToggles.forEach((permissionType, checkbox) -> {
            if (checkbox.isChecked()
                && permissions.stream().noneMatch(p -> permissionType.getId().equals(p.getRowGuid()))) {
                permissions.add(securityService.getPermission(permissionType.getName()));
            }
        });
    }

}
