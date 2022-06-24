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

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.useradmin.common.SearchableListbox;
import org.apromore.plugin.portal.useradmin.listbox.AssignedUserListbox;
import org.apromore.plugin.portal.useradmin.listbox.GroupListbox;
import org.apromore.plugin.portal.useradmin.listbox.RoleListbox;
import org.apromore.plugin.portal.useradmin.listbox.RoleModel;
import org.apromore.plugin.portal.useradmin.listbox.TristateItemRenderer;
import org.apromore.plugin.portal.useradmin.listbox.TristateListbox;
import org.apromore.plugin.portal.useradmin.listbox.TristateModel;
import org.apromore.plugin.portal.useradmin.listbox.UserListbox;
import org.apromore.portal.common.security.DefaultRoles;
import org.apromore.portal.common.zk.ComponentUtils;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.types.EventQueueEvents;
import org.apromore.portal.types.EventQueueTypes;
import org.apromore.service.SecurityService;
import org.apromore.service.WorkspaceService;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModels;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

public class UserAdminController extends SelectorComposer<Window> implements LabelSupplier {

    private static final int KEY_CTRL_A_LO = 65;
    private static final int KEY_CTRL_A_BG = 97;

    private static final String NO_PERMISSION_TO_ALLOCATE_USER = "noPermissionAllocateUserToGroup_message";
    private static final String NO_PERMISSION_EDIT_GROUP = "noPermissionEditGroup_message";
    private static final String NO_PERMISSION_EDIT_ROLE = "noPermissionEditRole_message";
    private static final String UNSAVED_ROLE_MESSAGE = "unsavedRoleDetail_message";
    private static final String DELETE_PROMPT_MESSAGE = "deletePrompt_message";
    private static final String TOGGLE_CLICK_EVENT_NAME = "onToggleClick";
    private static final String SWITCH_TAB_EVENT_NAME = "onSwitchTab";
    private static final String ROLE_PERMISSION_WINDOW = "zul/edit-role-permission.zul";
    private static final List<String> CO_SELECTABLE_ROLES = Collections.singletonList("ROLE_INTEGRATOR");
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(UserAdminController.class);
    private final Map<String, String> roleMap = DefaultRoles.getInstance().getRoles();

    private Comparator userComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            String input = (String) o1;
            User user = (User) o2;
            return user.getUsername().contains(input) ? 0 : 1;
        }
    };

    public static Comparator<User> nameComparator = new Comparator<User>() {

        public int compare(User user1, User user2) {
            String username1 = user1.getUsername().toUpperCase();
            String username2 = user2.getUsername().toUpperCase();

            return username1.compareTo(username2);
        }
    };

    Window mainWindow;
    User currentUser;
    User selectedUser = null;
    Group selectedGroup;
    Role selectedRole;
    Set<User> selectedUsers;
    Set<Group> selectedRoleTabGroups = Collections.emptySet();

    ListModelList<Group> groupModel;
    ListModelList<TristateModel> assignedRoleModel;
    ListModelList<TristateModel> assignedGroupModel;

    ListModelList<User> userModel;
    ListModelList<User> candidateUserModel;
    ListModelList<User> allUserModel;
    ListModelList<User> nonAssignedUserModel;
    ListModelList<User> assignedUserModel;

    ListModelList<RoleModel> roleModel;
    ListModelList<Group> roleTabGroupModel;
    ListModelList<User> nonAssignedUserRoleModel;
    ListModelList<User> assignedUserRoleModel;
    GroupListbox roleTabGroupList;
    AssignedUserListbox nonAssignedUserRoleList;
    AssignedUserListbox assignedUserRoleList;

    UserListbox userList;
    GroupListbox groupList;
    RoleListbox roleList;
    AssignedUserListbox nonAssignedUserList;
    AssignedUserListbox assignedUserList;
    TristateListbox<Role> assignedRoleList;
    TristateListbox<Group> assignedGroupList;

    TristateItemRenderer assignedRoleItemRenderer;
    TristateItemRenderer assignedGroupItemRenderer;

    private boolean isUserDetailDirty = false;
    private boolean isGroupDetailDirty = false;
    private boolean isRoleDetailDirty = false;
    private boolean isRoleTabUserView = true;
    private String dialogTitle = "Apromore";

    boolean canViewUsers;
    boolean canEditUsers;
    boolean canEditGroups;
    boolean canEditRoles;

    private PortalContext portalContext =
        (PortalContext) Executions.getCurrent().getArg().get("portalContext");
    private SecurityService securityService =
        (SecurityService) Executions.getCurrent().getArg().get("securityService");
    private WorkspaceService workspaceService =
        (WorkspaceService) Executions.getCurrent().getArg().get("workspaceService");

    @Wire("#tabbox")
    Tabbox tabbox;

    @Wire("#userTab")
    Tab userTab;
    @Wire("#groupTab")
    Tab groupTab;
    @Wire("#roleTab")
    Tab roleTab;

    @Wire("#userListView")
    Vbox userListView;

    @Wire("#userDetailContainer")
    Vbox userDetailContainer;
    @Wire("#groupDetailContainer")
    Vbox groupDetailContainer;
    @Wire("#roleDetailContainer")
    Vbox roleDetailContainer;

    @Wire("#userListbox")
    Listbox userListbox;
    @Wire("#groupListbox")
    Listbox groupListbox;
    @Wire("#roleListbox")
    Listbox roleListbox;

    @Wire("#userDetail")
    Label userDetail;
    @Wire("#firstNameTextbox")
    Textbox firstNameTextbox;
    @Wire("#lastNameTextbox")
    Textbox lastNameTextbox;
    @Wire("#passwordTextbox")
    Textbox passwordTextbox;
    @Wire("#confirmPasswordTextbox")
    Textbox confirmPasswordTextbox;
    @Wire("#dateCreatedDatebox")
    Datebox dateCreatedDatebox;
    @Wire("#lastActivityDatebox")
    Datebox lastActivityDatebox;
    @Wire("#emailTextbox")
    Textbox emailTextbox;

    @Wire("#assignedRoleListbox")
    Listbox assignedRoleListbox;
    @Wire("#assignedGroupListbox")
    Listbox assignedGroupListbox;

    @Wire("#userSaveBtn")
    Button userSaveBtn;

    @Wire("#groupDetail")
    Label groupDetail;
    @Wire("#groupNameTextbox")
    Textbox groupNameTextbox;
    @Wire("#candidateUser")
    Combobox candidateUser;
    @Wire("#candidateUserAdd")
    Button candidateUserAdd;
    @Wire("#candidateUserRemove")
    Button candidateUserRemove;
    @Wire("#assignedUserAddBtn")
    Button assignedUserAddBtn;
    @Wire("#assignedUserRemoveBtn")
    Button assignedUserRemoveBtn;

    @Wire("#assignedUserAddView")
    Div assignedUserAddView;
    @Wire("#nonAssignedUserListbox")
    Listbox nonAssignedUserListbox;
    @Wire("#assignedUserListbox")
    Listbox assignedUserListbox;

    @Wire("#userAddBtn")
    Button userAddBtn;
    @Wire("#userEditBtn")
    Button userEditBtn;
    @Wire("#userRemoveBtn")
    Button userRemoveBtn;
    @Wire("#groupAddBtn")
    Button groupAddBtn;
    @Wire("#groupEditBtn")
    Button groupEditBtn;
    @Wire("#groupRemoveBtn")
    Button groupRemoveBtn;
    @Wire("#groupSaveBtn")
    Button groupSaveBtn;
    @Wire("#roleSelectBtn")
    Button roleSelectBtn;
    @Wire("#roleAddBtn")
    Button roleAddBtn;
    @Wire("#roleEditBtn")
    Button roleEditBtn;
    @Wire("#roleViewBtn")
    Button roleViewBtn;
    @Wire("#roleRemoveBtn")
    Button roleRemoveBtn;
    @Wire("#roleCloneBtn")
    Button roleCloneBtn;

    @Wire("#assignUserBtn")
    Button assignUserBtn;
    @Wire("#retractUserBtn")
    Button retractUserBtn;
    @Wire("#assignUserRoleBtn")
    Button assignUserRoleBtn;
    @Wire("#retractUserRoleBtn")
    Button retractUserRoleBtn;

    @Wire("#roleDetail")
    Label roleDetail;
    @Wire("#roleNameTextbox")
    Textbox roleNameTextbox;
    @Wire("#roleSaveBtn")
    Button roleSaveBtn;

    @Wire("#applyRoleUserSelection")
    Box applyRoleUserSelection;
    @Wire("#applyRoleUserSelectionButtons")
    Box applyRoleUserSelectionButtons;
    @Wire("#roleTabGroupListbox")
    Listbox roleTabGroupListbox;
    @Wire("#nonAssignedUserRoleListbox")
    Listbox nonAssignedUserRoleListbox;
    @Wire("#assignedUserRoleListbox")
    Listbox assignedUserRoleListbox;
    @Wire("#nonAssignedUserRoleCheckbox")
    Checkbox nonAssignedUserRoleCheckbox;
    @Wire("#assignedUserRoleCheckbox")
    Checkbox assignedUserRoleCheckbox;

    /**
     * Test whether the current user has a permission.
     *
     * @param permission any permission
     * @return whether the authenticated user has the <var>permission</var>
     */
    private boolean hasPermission(Permissions permission) {
        return securityService.hasAccess(portalContext.getCurrentUser().getId(),
            permission.getRowGuid());
    }

    @Override
    public String getBundleName() {
        return "useradmin";
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        mainWindow = win;
        String userName = portalContext.getCurrentUser().getUsername();
        currentUser = securityService.getUserByName(userName);
        selectedUser = null;
        dialogTitle = Labels.getLabel("brand_name");

        canViewUsers = hasPermission(Permissions.VIEW_USERS);
        canEditUsers = hasPermission(Permissions.EDIT_USERS);
        canEditGroups = hasPermission(Permissions.EDIT_GROUPS);
        canEditRoles = hasPermission(Permissions.EDIT_ROLES);

        // Users tab
        userModel = new ListModelList<>(securityService.getAllUsers(), false);
        userModel.setMultiple(true);
        userList = new UserListbox(userListbox, userModel, getLabel("userName_text"));

        refreshAssignedRoles();
        refreshAssignedGroups();

        firstNameTextbox.setReadonly(!canEditUsers);
        lastNameTextbox.setReadonly(!canEditUsers);
        emailTextbox.setReadonly(!canEditUsers);
        passwordTextbox.setReadonly(!canEditUsers);
        confirmPasswordTextbox.setReadonly(!canEditUsers);
        userAddBtn.setVisible(canEditUsers);
        userRemoveBtn.setVisible(canEditUsers);

        // Groups tab
        groupModel = new ListModelList<>(securityService.findElectiveGroups(), false);
        groupModel.setMultiple(true);
        groupList = new GroupListbox(groupListbox, groupModel, getLabel("groupName_text"));

        allUserModel = new ListModelList<User>(securityService.getAllUsers(), false);
        refreshNonAssignedUsers();

        groupNameTextbox.setReadonly(!canEditGroups);
        candidateUser.setReadonly(!canEditGroups);
        candidateUserAdd.setDisabled(!canEditGroups);
        groupAddBtn.setVisible(canEditGroups);
        groupRemoveBtn.setVisible(canEditGroups);

        // Roles tab
        List<RoleModel> roleModels = securityService.getAllRoles().stream()
            .map(r -> new RoleModel(r, getDisplayRoleName(r.getName()))).collect(
                Collectors.toList());
        roleModel = new ListModelList<>(roleModels, false);
        roleModel.setMultiple(true);
        roleList = new RoleListbox(roleListbox, roleModel, getLabel("roleName_text"));

        roleNameTextbox.setReadonly(!canEditRoles);
        roleAddBtn.setVisible(canEditRoles);
        roleEditBtn.setVisible(canEditRoles);
        roleViewBtn.setVisible(!canEditRoles);
        roleRemoveBtn.setVisible(canEditRoles);
        roleCloneBtn.setVisible(canEditRoles);

        if (canViewUsers) {
            userListbox.setVisible(true);
        } else {
            userListbox.setVisible(false);
        }

        if (!canEditUsers) {
            userListView.setVisible(false);
        }

        // Set default to nothing
        refreshUsers();
        setSelectedUsers(null);
        refreshGroups();
        setSelectedGroup(null);
        refreshRoles();
        setSelectedRole(null);

        //Set role tab to user view
        toggleApplyRoleView(isRoleTabUserView);

        /**
         * Enable toggle selection in user Listbox on individual row
         */
        userEditBtn.addEventListener(TOGGLE_CLICK_EVENT_NAME, event -> {
            JSONObject param = (JSONObject) event.getData();
            String name = (String) param.get("name");
            User user = new User();
            user.setUsername(name);
            ListModelList model = (ListModelList) userListbox.getListModel();
            int index = model.indexOf(user);
            Listitem item = userListbox.getItemAtIndex(index);
            if (item.isSelected() && userListbox.getSelectedCount() == 1) {
                userListbox.clearSelection();
                setSelectedUsers(null);
            }
        });

        /**
         * Enable toggle selection in group Listbox on individual row
         */
        groupEditBtn.addEventListener(TOGGLE_CLICK_EVENT_NAME, event -> {
            JSONObject param = (JSONObject) event.getData();
            Integer id = (Integer) param.get("id");
            Group group = new Group();
            group.setId(id);
            ListModelList model = (ListModelList) groupListbox.getListModel();
            int index = model.indexOf(group);
            Listitem item = groupListbox.getItemAtIndex(index);
            if (item.isSelected() && groupListbox.getSelectedCount() == 1) {
                groupListbox.clearSelection();
                setSelectedGroup(null);
            }
        });

        /**
         * Enable toggle selection in role Listbox on individual row
         */
        roleSelectBtn.addEventListener(TOGGLE_CLICK_EVENT_NAME, event -> {
            JSONObject param = (JSONObject) event.getData();
            Integer index = (Integer) param.get("index");
            Listitem item = roleListbox.getItemAtIndex(index);
            if (item.isSelected() && roleListbox.getSelectedCount() == 1) {
                roleListbox.clearSelection();
                setSelectedRole(null);
            }
        });

        String onSwitchTab = "function (notify, init) { " + "console.log('user', arguments);"
            + "if (this.desktop && !init && notify) { zAu.send(new zk.Event(this, 'onSwitchTab')); }"
            + "else { this.$_sel(notify, init); }" + "}";
        // https://forum.zkoss.org/question/72022/intercepting-tab-selection/
        // https://forum.zkoss.org/question/55097/set-selected-tab/
        // prevent select at client side
        userTab.setWidgetOverride("_sel", onSwitchTab);
        groupTab.setWidgetOverride("_sel", onSwitchTab);
        roleTab.setWidgetOverride("_sel", onSwitchTab);
        userTab.setSelected(true);

        groupTab.addEventListener(SWITCH_TAB_EVENT_NAME, event -> {
            Tab tab = (Tab) event.getTarget();
            Tab selectedTab = tabbox.getSelectedTab();
            if (userTab.equals(selectedTab)) {
                checkDirtyUser(null, null, null, tab);
            } else if (roleTab.equals(selectedTab)) {
                checkDirtyRole(null, null, null, tab);
            }
        });

        userTab.addEventListener(SWITCH_TAB_EVENT_NAME, event -> {
            Tab tab = (Tab) event.getTarget();
            Tab selectedTab = tabbox.getSelectedTab();
            if (groupTab.equals(selectedTab)) {
                checkDirtyGroup(null, null, null, tab);
            } else if (roleTab.equals(selectedTab)) {
                checkDirtyRole(null, null, null, tab);
            }
        });

        roleTab.addEventListener(SWITCH_TAB_EVENT_NAME, event -> {
            Tab tab = (Tab) event.getTarget();
            Tab selectedTab = tabbox.getSelectedTab();
            if (userTab.equals(selectedTab)) {
                checkDirtyUser(null, null, null, tab);
            } else if (groupTab.equals(selectedTab)) {
                checkDirtyGroup(null, null, null, tab);
            }
        });

        /*
         * // Park this for now in case in-cell editing is required later
         *
         * groupEditBtn.addEventListener("onExecute", new EventListener() {
         *
         * @Override public void onEvent(Event event) throws Exception { String groupName =
         * event.getData().toString(); setSelectedGroup(securityService.getGroupByName(groupName)); }
         * }); groupEditBtn.addEventListener("onChangeNameCancel", new EventListener() {
         *
         * @Override public void onEvent(Event event) throws Exception { JSONObject param = (JSONObject)
         * event.getData(); String groupName = (String)param.get("groupName"); String rowGuid =
         * (String)param.get("rowGuid"); Group group = securityService.getGroupByName(groupName);
         * Textbox textbox = (Textbox)mainWindow.getFellow(rowGuid); textbox.setValue(groupName); } });
         * groupEditBtn.addEventListener("onChangeNameOK", new EventListener() {
         *
         * @Override public void onEvent(Event event) throws Exception { if
         * (!hasPermission(Permissions.EDIT_GROUPS)) {
         * Messagebox.show("You do not have permission to edit group", "Apromore", Messagebox.OK,
         * Messagebox.ERROR); return; } JSONObject param = (JSONObject) event.getData(); String
         * groupName = (String)param.get("groupName"); String rowGuid = (String)param.get("rowGuid");
         * Group group = securityService.getGroupByName(groupName); Textbox textbox =
         * (Textbox)mainWindow.getFellow(rowGuid); if ("".equals(textbox.getValue())) {
         * securityService.deleteGroup(group); groupModel.remove(group); Notification.info("Group " +
         * group.getName() + " is deleted"); } else { group.setName(textbox.getValue());
         * securityService.updateGroup(group); Notification.info("Details for group " + group.getName()
         * + " is updated"); } refreshGroups(); refreshAssignedGroups(); } });
         */

        // Register ZK event handler
        EventQueue securityEventQueue =
            EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true);
        securityEventQueue.subscribe(new EventListener() {
            @Override
            public void onEvent(Event event) {
                if (getSelf().getDesktop() == null) {
                    securityEventQueue.unsubscribe(this);
                } else {
                    Map properties = (Map) event.getData();
                    String eventType = (String) properties.get("type");
                    String eventUserName = (String) properties.get("user.name");

                    // Update the user collection
                    if ("CREATE_USER".equals(eventType) || "DELETE_USER".equals(eventType)) {
                        refreshUsers();
                        refreshCandidateUsers();
                    }

                    // Update the group collection
                    if ("CREATE_GROUP".equals(eventType) || "DELETE_GROUP".equals(eventType)) {
                        refreshGroups();
                        refreshAssignedGroups();
                        refreshRoleTabGroupList();
                    }

                    // Update the role collection
                    if ("CREATE_ROLE".equals(eventType) || "DELETE_ROLE".equals(eventType)
                        || "UPDATE_ROLE".equals(eventType)) {
                        refreshRoles();
                        refreshAssignedRoles();
                    }

                    // Update the user panel
                    if ("UPDATE_USER".equals(eventType)) {
                        // TO DO: Check for dirty group detail
                        // Reset group panel
                        setSelectedGroup(null);
                    }
                    if ("UPDATE_GROUP".equals(eventType)) {
                        refreshAssignedGroups();
                        if (selectedUser != null) {
                            String selectedUsername = selectedUser.getUsername();
                            // Skip this update if it doesn't apply to the currently displayed user
                            if (eventUserName != null && !eventUserName.equals(selectedUsername)) {
                                return;
                            }
                            // TO DO: Check for dirty user detail
                            setSelectedUsers(selectedUsers); // reload the current user
                        }
                    }
                    if ("UPDATE_ROLE".equals(eventType)) {
                        // Reset role panel
                        setSelectedRole(null);
                    }
                }
            }
        });

        EventQueues.lookup(EventQueueTypes.TRANSFER_OWNERSHIP, EventQueues.DESKTOP, true)
            .subscribe(new EventListener() {
                @Override
                public void onEvent(Event evt) {
                    if (EventQueueEvents.ON_TRANSFERRED.equals(evt.getName())) {
                        User user = (User) evt.getData();
                        if (user != null) {
                            securityService.deleteUser(user);
                        }
                    }
                }
            });

        EventQueues.lookup(EventQueueTypes.PURGE_ASSETS, EventQueues.DESKTOP, true)
            .subscribe(new EventListener() {
                @Override
                public void onEvent(Event evt) {
                    if (EventQueueEvents.ON_PURGED.equals(evt.getName())) {
                        User user = (User) evt.getData();
                        if (user != null) {
                            securityService.deleteUser(user);
                        }
                    }
                }
            });

    }

    private void refreshUsers() {
        userModel = new ListModelList<>(securityService.getAllUsers(), false);
        userList.setSourceListModel(userModel);
        userList.reset();
        setSelectedUsers(null);
    }

    private void refreshCandidateUsers() {
        candidateUserModel = new ListModelList<>(securityService.getAllUsers(), false);
        candidateUserModel.setMultiple(true);
        candidateUser.setModel(ListModels.toListSubModel(candidateUserModel, userComparator, 20));
    }

    private void refreshNonAssignedUsers() {
        nonAssignedUserModel = new ListModelList<>(securityService.getAllUsers(), false);
        nonAssignedUserModel.setMultiple(true);
        nonAssignedUserList = new AssignedUserListbox(nonAssignedUserListbox, nonAssignedUserModel,
            getLabel("usersNotInGroup_text"));
    }

    private void refreshGroups() {
        groupModel = new ListModelList<>(securityService.findElectiveGroups(), false);
        groupList.setSourceListModel(groupModel);
        groupList.reset();
    }

    private void refreshRoles() {
        List<RoleModel> roleModels = securityService.getAllRoles().stream()
            .map(r -> new RoleModel(r, getDisplayRoleName(r.getName())))
            .sorted((r1, r2) -> r1.getLabel().compareToIgnoreCase(r2.getLabel()))
            .collect(Collectors.toList());
        roleModel = new ListModelList<>(roleModels, false);
        roleList.setSourceListModel(roleModel);
        roleList.reset();
    }

    private void refreshAssignedRoles() {
        Comparator<Role> compareRole = new Comparator<Role>() {
            @Override
            public int compare(Role el1, Role el2) {
                return el1.getName().compareTo(el2.getName());
            }
        };
        List<Role> roles = securityService.getAllRoles();
        Collections.sort(roles, compareRole);

        assignedRoleModel = new ListModelList<>();
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            String roleName = role.getName();
            boolean coSelectable = CO_SELECTABLE_ROLES.contains(roleName);
            assignedRoleModel
                .add(new TristateModel(getDisplayRoleName(roleName), roleName, role,
                    TristateModel.UNCHECKED, false, coSelectable));
        }
        assignedRoleModel.setMultiple(true);
        assignedRoleListbox.setModel(assignedRoleModel);
        assignedRoleListbox.setNonselectableTags("*");
        assignedRoleItemRenderer = new TristateItemRenderer();
        assignedRoleListbox.setItemRenderer(assignedRoleItemRenderer);
        assignedRoleList = new TristateListbox<Role>(assignedRoleListbox, assignedRoleModel,
            getLabel("assignedRoles_text"));
        assignedRoleItemRenderer.setList(assignedRoleList);
        assignedRoleItemRenderer.setListbox(assignedRoleListbox);

    }

    private void refreshAssignedGroups() {
        Comparator<Group> compareGroup = new Comparator<Group>() {
            @Override
            public int compare(Group el1, Group el2) {
                return el1.getName().compareTo(el2.getName());
            }
        };
        List<Group> groups = securityService.findElectiveGroups();
        Collections.sort(groups, compareGroup);

        assignedGroupModel = new ListModelList<>();
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            String groupName = group.getName();
            assignedGroupModel
                .add(
                    new TristateModel(groupName, groupName, group, TristateModel.UNCHECKED, group.isGroupFromSsoIdp()));
        }
        assignedGroupModel.setMultiple(true);
        assignedGroupListbox.setModel(assignedGroupModel);
        assignedGroupListbox.setNonselectableTags("*");
        assignedGroupItemRenderer = new TristateItemRenderer();
        assignedGroupListbox.setItemRenderer(assignedGroupItemRenderer);
        assignedGroupList = new TristateListbox<Group>(assignedGroupListbox, assignedGroupModel,
            getLabel("assignedGroups_text"));
        assignedGroupItemRenderer.setList(assignedGroupList);
        assignedGroupItemRenderer.setListbox(assignedGroupListbox);
    }

    private void refreshRoleTabGroupList() {
        roleTabGroupModel = selectedRole == null ? new ListModelList<>()
            : new ListModelList<>(securityService.findElectiveGroups(), false);
        roleTabGroupList.setSourceListModel(roleTabGroupModel);
        roleTabGroupList.reset();
    }

    private void updateTristateModels(TristateListbox list, Map<String, Integer> tally,
                                      Integer total) {
        Map<String, Integer> keyToIndexMap = list.getKeyToIndexMap();
        for (Map.Entry<String, Integer> entry : keyToIndexMap.entrySet()) {
            String key = entry.getKey();
            int index = (int) entry.getValue();
            Integer count = tally.get(key);
            Integer state;
            boolean twoStateOnly = false;
            if (count == null) { // no entry
                state = TristateModel.UNCHECKED;
                twoStateOnly = true;
            } else if (count < total) {
                state = TristateModel.INDETERMINATE;
            } else {
                state = TristateModel.CHECKED;
                twoStateOnly = true;
            }
            ListModelList<TristateModel> listModel = list.getListModel();
            TristateModel model = listModel.get(index);
            model.setState(state);
            model.setTwoStateOnly(twoStateOnly);
            listModel.set(index, model); // trigger change
        }
    }

    private void clearAssignedRoleModel() {
        assignedRoleList.reset();
    }

    private void updateAssignedRoleModel(Set<User> users) {
        if (users == null) {
            clearAssignedRoleModel();
            return;
        }
        Map<String, Integer> tally = new HashMap<String, Integer>();
        for (User user : users) {
            Set<Role> roles = securityService.findRolesByUser(user);
            for (Role role : roles) {
                String roleName = role.getName();
                Integer state = tally.get(roleName);
                if (state != null) {
                    tally.put(roleName, state + 1);
                } else {
                    tally.put(roleName, 1);
                }
            }
        }
        int userCount = users.size();
        updateTristateModels(assignedRoleList, tally, userCount);
    }

    private void saveAssignedRole(Set<User> users, boolean persist) {
        if (users == null) {
            return;
        }
        assignedRoleList.calcSelection();
        Set<Role> addedRoles = assignedRoleList.getAddedObjects();
        Set<Role> removedRoles = assignedRoleList.getRemovedObjects();
        for (User user : users) {
            Set<Role> roles = securityService.findRolesByUser(user);
            Set<Role> updatedRoles = new HashSet<Role>(roles);
            updatedRoles.addAll(addedRoles);
            updatedRoles.removeAll(removedRoles);
            user.setRoles(updatedRoles);
            if (persist) {
                securityService.updateUser(user);
            }
        }
    }

    private void clearAssignedGroupModel() {
        assignedGroupList.reset();
    }

    private void updateAssignedGroupModel(Set<User> users) {
        if (users == null) {
            clearAssignedGroupModel();
            return;
        }
        Map<String, Integer> tally = new HashMap<String, Integer>();
        for (User user : users) {
            Set<Group> groups = securityService.findGroupsByUser(user);
            for (Group group : groups) {
                String groupName = group.getName();
                Integer state = tally.get(groupName);
                if (state != null) {
                    tally.put(groupName, state + 1);
                } else {
                    tally.put(groupName, 1);
                }
            }
        }
        int userCount = users.size();
        updateTristateModels(assignedGroupList, tally, userCount);
    }

    private void saveAssignedGroup(Set<User> users, boolean persist) {
        if (users == null) {
            return;
        }
        assignedGroupList.calcSelection();
        Set<Group> addedGroups = assignedGroupList.getAddedObjects();
        Set<Group> removedGroups = assignedGroupList.getRemovedObjects();
        for (User user : users) {
            Set<Group> roles = securityService.findGroupsByUser(user);
            Set<Group> updatedGroups = new HashSet<Group>(roles);
            updatedGroups.addAll(addedGroups);
            updatedGroups.removeAll(removedGroups);
            user.setGroups(updatedGroups);
            if (persist) {
                securityService.updateUser(user);
            }
        }
    }

    private void setSelectedUsers(Set<User> users) {
        passwordTextbox.setValue("");
        confirmPasswordTextbox.setValue("");
        assignedRoleItemRenderer.setDisabled(false);
        assignedGroupItemRenderer.setDisabled(false);
        assignedRoleItemRenderer.setMultiUserSelected(false);
        assignedGroupItemRenderer.setMultiUserSelected(false);
        assignedRoleList.reset();
        assignedGroupList.reset();
        assignedRoleListbox.setDisabled(false);
        assignedGroupListbox.setDisabled(false);
        ComponentUtils.toggleSclass(userDetailContainer, true);
        boolean multiSelected = false;
        if (users == null || users.size() == 0 || users.size() > 1) {
            selectedUser = null;
            selectedUsers = users;
            firstNameTextbox.setValue("");
            lastNameTextbox.setValue("");
            dateCreatedDatebox.setValue(null);
            lastActivityDatebox.setValue(null);
            emailTextbox.setValue("");
            if (users == null) {
                userDetail.setValue(getLabel("noUserSelected_text"));
                userSaveBtn.setDisabled(true);
            } else {
                userDetail.setValue(getLabel("multipleUserSelected_text"));
                userSaveBtn.setDisabled(false);
            }
            if (users == null || users.size() == 0) {
                ComponentUtils.toggleSclass(userDetailContainer, false);
                assignedRoleItemRenderer.setDisabled(true);
                assignedGroupItemRenderer.setDisabled(true);
                assignedRoleListbox.setDisabled(true);
                assignedGroupListbox.setDisabled(true);
            } else {
                assignedRoleItemRenderer.setForceTwoState(false);
                assignedGroupItemRenderer.setForceTwoState(false);
                if (users.size() > 1) {
                    assignedRoleItemRenderer.setMultiUserSelected(true);
                    assignedGroupItemRenderer.setMultiUserSelected(true);
                    multiSelected = true;
                }
            }

        } else {
            selectedUser = users.iterator().next();
            selectedUsers = users;
            firstNameTextbox.setValue(selectedUser.getFirstName());
            lastNameTextbox.setValue(selectedUser.getLastName());
            dateCreatedDatebox.setValue(selectedUser.getDateCreated());
            lastActivityDatebox.setValue(selectedUser.getLastActivityDate());
            emailTextbox.setValue(selectedUser.getMembership().getEmail());
            userDetail.setValue(MessageFormat.format(getLabel("userUserNameTitle_text"), selectedUser.getUsername()));
            userSaveBtn.setDisabled(false);
            assignedRoleItemRenderer.setForceTwoState(true);
            assignedGroupItemRenderer.setForceTwoState(true);
        }
        disabledForMultiUserSelected(multiSelected);
        updateAssignedRoleModel(users);
        updateAssignedGroupModel(users);
        isUserDetailDirty = false; // ensure dirty is not set by field's setValue
    }

    private void disabledForMultiUserSelected(boolean multiSelected) {
        firstNameTextbox.setDisabled(!canEditUsers || multiSelected);
        lastNameTextbox.setDisabled(!canEditUsers || multiSelected);
        emailTextbox.setDisabled(!canEditUsers || multiSelected);
        passwordTextbox.setDisabled(!canEditUsers || multiSelected);
        confirmPasswordTextbox.setDisabled(!canEditUsers || multiSelected);
    }

    private void setGroupDetailReadOnly(boolean readOnly) {
        groupNameTextbox.setDisabled(readOnly);
        groupSaveBtn.setDisabled(readOnly);
        ComponentUtils.toggleSclass(groupDetailContainer, !readOnly);
        assignUserBtn.setDisabled(readOnly);
        retractUserBtn.setDisabled(readOnly);
    }

    private Group setSelectedGroup(Group group) {
        assignedUserAddView.setVisible(false);

        if (group == null) {
            groupNameTextbox.setValue("");
            groupDetail.setValue(getLabel("noGroupSelected_text"));
            assignedUserModel = new ListModelList<>();
            nonAssignedUserModel = new ListModelList<>();
            setGroupDetailReadOnly(true);
        } else {
            groupNameTextbox.setValue(group.getName());
            groupDetail.setValue(MessageFormat.format(getLabel("groupGroupNameTitle_text"), group.getName()));
            List<User> assignedUsers = new ArrayList<>(group.getUsers());
            List<User> nonAssignedUsers = new ArrayList<>(securityService.getAllUsers());
            nonAssignedUsers.removeAll(assignedUsers);
            Collections.sort(assignedUsers, nameComparator);
            Collections.sort(nonAssignedUsers, nameComparator);
            assignedUserModel = new ListModelList<User>(assignedUsers, false);
            nonAssignedUserModel = new ListModelList<User>(nonAssignedUsers, false);
            setGroupDetailReadOnly(group.isGroupFromSsoIdp());
        }
        assignedUserModel.setMultiple(true);
        assignedUserList = new AssignedUserListbox(assignedUserListbox, assignedUserModel,
            getLabel("assignedUsers_text"));
        nonAssignedUserModel.setMultiple(true);
        nonAssignedUserList = new AssignedUserListbox(nonAssignedUserListbox, nonAssignedUserModel,
            getLabel("usersNotInGroup_text"));
        selectedGroup = group;
        isGroupDetailDirty = false; // ensure dirty is not set by field's setValue
        return group;
    }

    private void setRoleDetailReadOnly(boolean readOnly) {
        setRoleDetailReadOnly(readOnly, false);
    }

    private void setRoleDetailReadOnly(boolean readOnly, boolean defaultRole) {
        roleNameTextbox.setDisabled(readOnly || defaultRole);
        roleSaveBtn.setDisabled(readOnly);
        ComponentUtils.toggleSclass(roleDetailContainer, !readOnly);
        assignUserRoleBtn.setDisabled(readOnly);
        retractUserRoleBtn.setDisabled(readOnly);
        roleViewBtn.setVisible(defaultRole);
        roleEditBtn.setVisible(!defaultRole);
    }

    private Role setSelectedRole(Role role) {
        if (role == null) {
            roleNameTextbox.setValue(getLabel("roleName_hint", "Enter role name"));
            roleDetail.setValue(getLabel("noRoleSelected_text"));
            roleTabGroupModel = new ListModelList<>();
            setRoleDetailReadOnly(true);
        } else {
            List<RoleModel> selectedRoleModel =
                roleModel.getInnerList().stream().filter(r -> role.getRowGuid().equals(r.getRole().getRowGuid()))
                    .collect(Collectors.toList());
            roleList.getListModel().setSelection(selectedRoleModel);
            String roleName = getDisplayRoleName(role.getName());
            roleNameTextbox.setValue(roleName);
            roleDetail.setValue(MessageFormat.format(getLabel("roleRoleNameTitle_text"), roleName));
            setRoleDetailReadOnly(false, isDefaultRole(role));
            roleTabGroupModel = new ListModelList<>(securityService.findElectiveGroups(), false);
        }

        roleTabGroupList = new GroupListbox(roleTabGroupListbox, roleTabGroupModel, getLabel("groups_text"));
        roleTabGroupListbox.setMultiple(false);
        roleTabGroupList.getListModel().setMultiple(false);
        loadRoleTabUserAssignmentLists(role, null);
        selectedRole = role;
        isRoleDetailDirty = false; // ensure dirty is not set by field's setValue
        return role;
    }

    /**
     * Find the User from ListModelList based on the set.
     */
    private Set<User> getUserCollection(ListModelList model, Set<User> userSet) {
        Set<String> userNames = new HashSet<String>();
        Set<User> users = new HashSet<User>();
        for (User u : userSet) {
            userNames.add(u.getUsername());
        }
        for (int i = 0; i < model.size(); i++) {
            User user = (User) model.get(i);
            if (userNames.contains(user.getUsername())) {
                users.add(user);
            }
        }
        return users;
    }

    // User-related features

    @Listen("onSelect = #userListbox")
    public void onSelectUserListbox(SelectEvent event) throws Exception {
        if (!hasPermission(Permissions.VIEW_USERS)) {
            Messagebox.show(getLabel("noPermissionViewUser_message"));
            return;
        }
        Set<User> prevUsers = event.getPreviousSelectedObjects();
        Set<User> newUsers = event.getSelectedObjects();
        checkDirtyUser(prevUsers, newUsers, null, null);
    }

    public void selectBulk(SearchableListbox list, boolean select) {
        if (select) {
            list.selectAll();
        } else {
            list.unselectAll();
        }
    }

    /**
     * Check dirty user detail.
     *
     * @param prevUsers Previously selected users
     * @param newUsers  Newly selected users
     * @param select    Null do nothing, true select all, false unselect all
     */
    public void checkDirtyUser(Set<User> prevUsers, Set<User> newUsers, Boolean select, Tab tab) {
        if (isUserDetailDirty) {
            Messagebox.show(
                getLabel("dirtyUser_message"),
                dialogTitle,
                new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.CANCEL},
                Messagebox.QUESTION,
                e -> {
                    String buttonName = e.getName();
                    if (Messagebox.ON_CANCEL.equals(buttonName)) {
                        if (prevUsers != null) {
                            userList.getListModel().setSelection(prevUsers);
                        }
                        return;
                    } else if (Messagebox.ON_YES.equals(buttonName)) {
                        onClickUserSaveButton();
                    } else {
                        refreshUsers();
                        setSelectedUsers(null);
                    }
                    if (select != null) {
                        selectBulk(userList, select);
                        if (!select) {
                            setSelectedUsers(null);
                        }
                    }
                    if (newUsers != null) {
                        updateUserDetail(newUsers);
                    }
                    if (tab != null) {
                        tab.setSelected(true);
                    }
                }
            );
        } else {
            if (select != null) {
                selectBulk(userList, select);
                if (!select) {
                    setSelectedUsers(null);
                }
            }
            if (newUsers != null) {
                updateUserDetail(newUsers);
            }
            if (tab != null) {
                tab.setSelected(true);
                refreshGroups();
                setSelectedGroup(null);
            }
        }
    }

    /**
     * Update user detail when required.
     *
     * @param newUsers Users to update.
     */
    public void updateUserDetail(Set<User> newUsers) {
        if (newUsers != null && newUsers.size() >= 1) {
            setSelectedUsers(newUsers);
        } else {
            setSelectedUsers(null);
        }
    }

    @Listen("onSelect = #assignedGroupListbox")
    public void onSelectAssignedGroupsListbox(SelectEvent event) {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            groupListbox.setSelectedItems(event.getPreviousSelectedItems());
            Notification.error(getLabel("noPermissionAssignGroups"));
            return;
        }
        isUserDetailDirty = true;
    }

    @Listen("onSelect = #assignedRoleListbox")
    public void onSelectAssignedRolesListbox(SelectEvent event) {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            assignedRoleListbox.setSelectedItems(event.getPreviousSelectedItems());
            Notification.error(getLabel("noPermissionAssignRoles"));
            return;
        }
        isUserDetailDirty = true;
    }

    @Listen("onClick = #userAddBtn")
    public void onClickuserAddBtn() {
        if (!hasPermission(Permissions.EDIT_USERS)) {
            Notification.error(getLabel("noPermissionAddUser"));
            return;
        }

        try {
            Map arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            Window window = (Window) Executions.getCurrent()
                .createComponents(getPageDefinition("zul/create-user.zul"), getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create user creation dialog", e);
            Messagebox.show(getLabel("failedLaunchCreateUser_message"));
        }
    }

    @Listen("onClick = #userRemoveBtn")
    public void onClickUserRemoveBtn() {
        if (!hasPermission(Permissions.EDIT_USERS)) {
            Notification.error(getLabel("noPermissionDeleteUser_message"));
            return;
        }
        Set<User> selectedUsers = userList.getSelection();
        if (userList.getSelectionCount() == 0) {
            Notification.error(getLabel("nothingToDelete_message"));
            return;
        }
        if (selectedUsers.contains(currentUser)) {
            Notification.error(getLabel("noDeleteSelf_message"));
            return;
        }
        if (selectedUsers.stream().anyMatch(u -> "admin".equals(u.getUsername()))) {
            Notification.error(getLabel("noDeleteAdminUser_message"));
            return;
        }
        List<String> users = new ArrayList<>();
        for (User u : selectedUsers) {
            users.add(u.getUsername());
        }
        String userNames = String.join(",", users);
        Messagebox.show(
            MessageFormat.format(getLabel(DELETE_PROMPT_MESSAGE), userNames),
            dialogTitle,
            Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
            e -> {
                if (Messagebox.ON_OK.equals(e.getName())) {
                    for (User user : selectedUsers) {
                        LOGGER.info("Deleting user " + user.getUsername());
                        if (workspaceService.isOnlyOwner(user)) {
                            try {
                                Map arg = new HashMap<>();
                                arg.put("selectedUser", user);
                                Window window = (Window) Executions.getCurrent()
                                    .createComponents(getPageDefinition("zul/delete-user.zul"), getSelf(), arg);
                                window.doModal();
                            } catch (Exception ex) {
                                LOGGER.error("Unable to create transfer owner dialog", ex);
                                Messagebox.show(getLabel("failedLaunchTransferOwner_message"));
                            }
                        } else {
                            securityService.deleteUser(user);
                            // Force logout the deleted user
                            Map dataMap = Map.of("type", "DELETE_USER");

                            EventQueues
                                .lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
                                .publish(new Event("User Deleted", null, dataMap));

                            EventQueues.lookup("forceSignOutQueue", EventQueues.APPLICATION, true)
                                .publish(new Event("onSignout", null, user.getUsername()));
                        }
                    }
                    setSelectedUsers(null);
                }
            }
        );
    }

    @Listen("onClick = #userSaveBtn")
    public void onClickUserSaveButton() {
        boolean passwordDirty = false;

        if (!hasPermission(Permissions.EDIT_USERS)) {
            Notification.error(getLabel("noPermissionEditUser_message"));
            return;
        }
        if (selectedUser != null) {
            if (passwordTextbox.getValue() != null && passwordTextbox.getValue().length() > 0) {
                if (passwordTextbox.getValue().length() < 6) {
                    Messagebox.show(getLabel("passwordTooShort_message"), null, Messagebox.OK,
                        Messagebox.ERROR);
                    return;
                } else if (!Objects.equals(passwordTextbox.getValue(), confirmPasswordTextbox.getValue())) {
                    Messagebox.show(getLabel("passwordNoMatch_message"), null, Messagebox.OK,
                        Messagebox.ERROR);
                    return;
                }
                passwordDirty = true;
            }

            selectedUser.setFirstName(firstNameTextbox.getValue());
            selectedUser.setLastName(lastNameTextbox.getValue());
            saveAssignedGroup(selectedUsers, false);
            saveAssignedRole(selectedUsers, false);
            selectedUser.getMembership().setEmail(emailTextbox.getValue());
            selectedUser.getMembership().setUser(selectedUser);
            securityService.updateUser(selectedUser);

            if (passwordDirty) {
                try {
                    securityService.updatePassword(selectedUser.getMembership(), passwordTextbox.getValue());

                } catch (Exception e) {
                    LOGGER.error("Unable to update password", e);
                    Messagebox.show(getLabel("passwordNotUpdated_message"), null, Messagebox.OK,
                        Messagebox.ERROR);
                }
            }

            Notification
                .info(MessageFormat.format(getLabel("userUpdated_message"), selectedUser.getUsername()));
        } else {
            saveAssignedGroup(selectedUsers, true);
            saveAssignedRole(selectedUsers, true);
            Notification.info(getLabel("multipleUsersUpdated_message"));
        }
        isUserDetailDirty = false;
    }

    // Group-related features

    @Listen("onSelect = #groupListbox")
    public void onSelectGroupsListbox(SelectEvent event) {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel(NO_PERMISSION_EDIT_GROUP));
            return;
        }
        Set<Group> newGroups = event.getSelectedObjects();
        Set<Group> prevGroups = event.getPreviousSelectedObjects();
        checkDirtyGroup(prevGroups, newGroups, null, null);
    }

    /**
     * Check dirty group detail.
     *
     * @param prevGroups Previously selected groups
     * @param newGroups  Newly selected groups
     * @param select     Null do nothing, true select all, false unselect all
     */
    public void checkDirtyGroup(Set<Group> prevGroups, Set<Group> newGroups, Boolean select,
                                Tab tab) {
        if (isGroupDetailDirty) {
            Messagebox.show(
                getLabel("unsavedGroupDetail_message"),
                dialogTitle,
                new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.CANCEL},
                Messagebox.QUESTION,
                e -> {
                    String buttonName = e.getName();
                    if (Messagebox.ON_CANCEL.equals(buttonName)) {
                        if (prevGroups != null) {
                            groupList.getListModel().setSelection(prevGroups);
                        }
                        return;
                    } else if (Messagebox.ON_YES.equals(buttonName)) {
                        onClickGroupSaveButton();
                    } else {
                        isGroupDetailDirty = false;
                        refreshGroups();
                        setSelectedGroup(null);
                    }
                    if (select != null) {
                        selectBulk(groupList, select);
                        if (!select) {
                            setSelectedGroup(null);
                        }
                    }
                    if (newGroups != null) {
                        updateGroupDetail(newGroups);
                    }
                    if (tab != null) {
                        tab.setSelected(true);
                    }
                }
            );
        } else {
            if (select != null) {
                selectBulk(groupList, select);
                if (!select) {
                    setSelectedGroup(null);
                }
            }
            if (newGroups != null) {
                updateGroupDetail(newGroups);
            }
            if (tab != null) {
                tab.setSelected(true);
                refreshUsers();
                setSelectedUsers(null);
            }
        }
    }

    /**
     * Update group detail when required.
     *
     * @param newGroups Groups to update.
     */
    public void updateGroupDetail(Set<Group> newGroups) {
        if (newGroups != null && newGroups.size() == 1) {
            Group group = newGroups.iterator().next();
            setSelectedGroup(securityService.getGroupByName(group.getName()));
        } else {
            setSelectedGroup(null);
        }
    }

    @Listen("onClick = #assignedUserAddBtn")
    public void onClickAssignedUserAdd() {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel(NO_PERMISSION_TO_ALLOCATE_USER));
            return;
        }
        if (assignedUserAddView.isVisible()) {
            assignedUserAddView.setVisible(false);
        } else {
            assignedUserAddView.setVisible(true);
        }
    }

    @Listen("onClick = #assignedUserRemoveBtn")
    public void onClickAssignedUserRemove() {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel(NO_PERMISSION_TO_ALLOCATE_USER));
            return;
        }
        ListModelList listModel = assignedUserList.getListModel();
        Set<User> users = listModel.getSelection();
        listModel.removeAll(users);
        assignedUserModel.remove(users);
    }

    @Listen("onClick = #candidateUserAdd")
    public void onClickCandidateUserAdd() {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel(NO_PERMISSION_TO_ALLOCATE_USER));
            return;
        }
        Set<User> users = candidateUserModel.getSelection();
        if (users != null && users.size() == 1) {
            User candidateUser = users.iterator().next();
            for (int i = 0; i < assignedUserModel.size(); i++) {
                User user = (User) assignedUserModel.get(i);
                if (candidateUser.getUsername().contains(user.getUsername())) {
                    return;
                }
            }
            assignedUserModel.add(candidateUser);
            assignedUserList.getListModel().add(candidateUser);
        }
    }

    @Listen("onClick = #retractUserBtn")
    public void onClickRetractUser() {
        if (selectedGroup == null) {
            return;
        }
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel(NO_PERMISSION_TO_ALLOCATE_USER));
            return;
        }
        List<User> users = new ArrayList<User>(assignedUserList.getSelection());
        if (users != null && users.size() >= 1) {
            for (User user : users) {
                nonAssignedUserModel.add(user);
                assignedUserModel.remove(user);
                nonAssignedUserList.reset();
                assignedUserList.reset();
            }
            isGroupDetailDirty = true;
        }
    }

    @Listen("onClick = #assignUserBtn")
    public void onClickAssignUser() {
        if (selectedGroup == null) {
            return;
        }
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel(NO_PERMISSION_TO_ALLOCATE_USER));
            return;
        }
        List<User> users = new ArrayList<User>(nonAssignedUserList.getSelection());
        if (users != null && users.size() >= 1) {
            for (User user : users) {
                assignedUserModel.add(user);
                nonAssignedUserModel.remove(user);
                nonAssignedUserList.reset();
                assignedUserList.reset();
            }
            isGroupDetailDirty = true;
        }
    }

    @Listen("onClick = #groupAddBtn")
    public void onClickgroupAddBtn() {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel("noPermissionCreateGroup_message"));
            return;
        }

        try {
            Map arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            Window window = (Window) Executions.getCurrent()
                .createComponents(getPageDefinition("zul/create-group.zul"), getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create group creation dialog", e);
            Messagebox.show(getLabel("failedLaunchCreateGroup_message"));
        }
    }

    @Listen("onClick = #groupRemoveBtn")
    public void onClickGroupRemoveBtn() {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel(NO_PERMISSION_EDIT_GROUP));
            return;
        }
        Set<Group> selectedGroups = groupList.getSelection();
        if (groupList.getSelectionCount() == 0) {
            Notification.error(getLabel("nothingToDelete_message"));
            return;
        }

        List<String> groups = new ArrayList<>();
        for (Group g : selectedGroups) {
            groups.add(g.getName());
        }
        String groupNames = String.join(",", groups);
        Messagebox.show(
            MessageFormat.format(getLabel(DELETE_PROMPT_MESSAGE), groupNames),
            dialogTitle,
            Messagebox.OK | Messagebox.CANCEL,
            Messagebox.QUESTION,
            e -> {
                if (Messagebox.ON_OK.equals(e.getName())) {
                    for (Group group : selectedGroups) {
                        LOGGER.info("Deleting user " + group.getName());
                        securityService.deleteGroup(group);
                        Map dataMap = Map.of("type", "DELETE_GROUP");
                        EventQueues
                            .lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
                            .publish(new Event("Group Created", null, dataMap));
                    }
                    setSelectedGroup(null);
                }
            }
        );
    }

    @Listen("onClick = #groupSaveBtn")
    public void onClickGroupSaveButton() {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error(getLabel(NO_PERMISSION_EDIT_GROUP));
            return;
        }
        //Check for groups with the same name if the name field has been updated
        if (!groupNameTextbox.getValue().equals(selectedGroup.getName())
            && securityService.getGroupByName(groupNameTextbox.getValue()) != null) {
            Messagebox.show(getLabel("failedUpdateGroup_message"));
            return;
        }

        ListModelList listModel = assignedUserList.getListModel();
        Set<User> users = new HashSet<User>(listModel);
        selectedGroup.setName(groupNameTextbox.getValue());
        selectedGroup.setUsers(users);
        securityService.updateGroup(selectedGroup);
        Notification.info(
            MessageFormat.format(getLabel("updatedGroupDetails_message"), selectedGroup.getName()));
        isGroupDetailDirty = false;
        refreshGroups();
        refreshAssignedGroups();
        refreshRoleTabGroupList();
        setSelectedGroup(null);
    }

    // Role-related features

    @Listen("onSelect = #roleListbox")
    public void onSelectRolesListbox(SelectEvent event) {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel(NO_PERMISSION_EDIT_ROLE));
            return;
        }
        Set<RoleModel> newRoles = event.getSelectedObjects();
        Set<RoleModel> prevRoles = event.getPreviousSelectedObjects();
        checkDirtyRole(prevRoles, newRoles, null, null);
    }

    /**
     * Check dirty role detail.
     *
     * @param prevRoles Previously selected roles
     * @param newRoles  Newly selected roles
     * @param select    Null do nothing, true select all, false unselect all
     */
    public void checkDirtyRole(Set<RoleModel> prevRoles, Set<RoleModel> newRoles, Boolean select,
                               Tab tab) {
        if (isRoleDetailDirty) {
            Messagebox.show(
                getLabel(UNSAVED_ROLE_MESSAGE),
                dialogTitle,
                new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.CANCEL},
                Messagebox.QUESTION,
                e -> {
                    String buttonName = e.getName();
                    if (Messagebox.ON_CANCEL.equals(buttonName)) {
                        if (prevRoles != null) {
                            roleList.getListModel().setSelection(prevRoles);
                        }
                        return;
                    } else if (Messagebox.ON_YES.equals(buttonName)) {
                        onClickRoleSaveButton();
                    } else if (Messagebox.ON_NO.equals(buttonName)) {
                        isRoleDetailDirty = false;
                    } else {
                        isRoleDetailDirty = false;
                        refreshRoles();
                        setSelectedRole(null);
                    }
                    if (select != null) {
                        selectBulk(roleList, select);
                        if (!select) {
                            setSelectedRole(null);
                        }
                    }
                    if (newRoles != null) {
                        updateRoleDetail(newRoles);
                    }
                    if (tab != null) {
                        tab.setSelected(true);
                    }
                }
            );
        } else {
            if (select != null) {
                selectBulk(roleList, select);
                if (!select) {
                    setSelectedRole(null);
                }
            }
            if (newRoles != null) {
                updateRoleDetail(newRoles);
            }
            if (tab != null) {
                tab.setSelected(true);
                refreshUsers();
                setSelectedUsers(null);
            }
        }
    }

    /**
     * Update role detail when required.
     *
     * @param newRoles Roles to update.
     */
    public void updateRoleDetail(Set<RoleModel> newRoles) {
        if (newRoles != null && newRoles.size() == 1) {
            Role role = newRoles.iterator().next().getRole();
            setSelectedRole(securityService.findRoleByName(role.getName()));
        } else {
            setSelectedRole(null);
        }
    }

    @Listen("onClick = #assignUserRoleBtn")
    public void onClickAssignUserRole() {
        if (selectedRole == null) {
            return;
        }
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel("noPermissionAssignRoles_message"));
            return;
        }

        List<User> users = new ArrayList<>(nonAssignedUserRoleList.getSelection());
        if (!users.isEmpty()) {
            for (User user : users) {
                assignedUserRoleModel.add(user);
                nonAssignedUserRoleModel.remove(user);
                nonAssignedUserRoleList.reset();
                assignedUserRoleList.reset();
            }
            isRoleDetailDirty = isRoleChanged(roleNameTextbox.getValue());
            assignedUserRoleCheckbox.setChecked(false);
            nonAssignedUserRoleCheckbox.setChecked(false);
        }
    }

    @Listen("onClick = #retractUserRoleBtn")
    public void onClickRetractUserRole() {
        if (selectedRole == null) {
            return;
        }
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel("noPermissionAssignRoles_message"));
            return;
        }
        List<User> users = new ArrayList<>(assignedUserRoleList.getSelection());
        if (!users.isEmpty()) {
            for (User user : users) {
                nonAssignedUserRoleModel.add(user);
                assignedUserRoleModel.remove(user);
                nonAssignedUserRoleList.reset();
                assignedUserRoleList.reset();
            }
            isRoleDetailDirty = isRoleChanged(roleNameTextbox.getValue());
            assignedUserRoleCheckbox.setChecked(false);
            nonAssignedUserRoleCheckbox.setChecked(false);
        }
    }

    private boolean isRoleChanged(String textBoxValue) {
        if (selectedRole == null) {
            return false;
        }

        List<String> currentRoleUsers = selectedRole.getUsers().stream()
            .map(User::getUsername).collect(Collectors.toList());
        List<String> unSelectedRoleUsers = nonAssignedUserRoleModel.getInnerList().stream()
            .map(User::getUsername).collect(Collectors.toList());
        List<String> selectedRoleUsers = assignedUserRoleModel.getInnerList().stream()
            .map(User::getUsername).collect(Collectors.toList());

        return !getDisplayRoleName(selectedRole.getName()).equals(textBoxValue)
            || !currentRoleUsers.containsAll(selectedRoleUsers)
            || currentRoleUsers.stream().anyMatch(unSelectedRoleUsers::contains);
    }

    @Listen("onClick = #roleAddBtn")
    public void onClickRoleAddBtn() {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel("noPermissionCreateRole_message"));
            return;
        }
        try {
            Map<String, Object> arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            arg.put("mode", "CREATE");
            Window window = (Window) Executions.getCurrent()
                .createComponents(getPageDefinition(ROLE_PERMISSION_WINDOW), getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create role creation dialog", e);
            Messagebox.show(getLabel("failedLaunchCreateRole_message"));
        }
    }

    @Listen("onClick = #roleEditBtn")
    public void onClickRoleEditBtn() {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel(NO_PERMISSION_EDIT_ROLE));
            return;
        }

        Set<RoleModel> selectedRoles = roleList.getSelection();
        if (roleList.getSelectionCount() == 0 || selectedRole == null) {
            Notification.error(getLabel("noEditNoRoleSelected_message"));
            return;
        }

        if (selectedRoles.stream().anyMatch(r -> isDefaultRole(r.getRole()))) {
            Notification.error(getLabel("noEditDefaultRole_message"));
            return;
        }

        try {
            Map<String, Object> arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            arg.put("mode", "EDIT");
            arg.put("role", selectedRole);
            Window window = (Window) Executions.getCurrent()
                .createComponents(getPageDefinition(ROLE_PERMISSION_WINDOW), getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create role edit dialog", e);
            Messagebox.show(getLabel("failedLaunchEditRole_message"));
        }
    }

    @Listen("onClick = #roleViewBtn")
    public void onClickRoleViewBtn() {
        if (roleList.getSelectionCount() == 0 || selectedRole == null) {
            Notification.error(getLabel("noEditNoRoleSelected_message"));
            return;
        }

        try {
            Map<String, Object> arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            arg.put("mode", "VIEW");
            arg.put("role", selectedRole);
            arg.put("roleLabel", getDisplayRoleName(selectedRole.getName()));
            Window window = (Window) Executions.getCurrent()
                .createComponents(getPageDefinition(ROLE_PERMISSION_WINDOW), getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create role view dialog", e);
            Messagebox.show(getLabel("failedLaunchViewRole_message"));
        }
    }

    @Listen("onClick = #roleRemoveBtn")
    public void onClickRoleRemoveBtn() {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel("noPermissionRemoveRole_message"));
            return;
        }

        Set<RoleModel> selectedRoleModels = roleList.getSelection();
        if (roleList.getSelectionCount() == 0) {
            Notification.error(getLabel("noDeleteNoRoleSelected_message"));
            return;
        }

        List<Role> selectedRoles = selectedRoleModels.stream().map(RoleModel::getRole).collect(Collectors.toList());
        if (selectedRoles.stream().anyMatch(this::isDefaultRole)) {
            Notification.error(getLabel("noDeleteDefaultRole_message"));
            return;
        }

        List<String> roles = new ArrayList<>();
        for (RoleModel r : selectedRoleModels) {
            roles.add(r.getLabel());
        }
        String roleNames = String.join(", ", roles);
        Messagebox.show(
            MessageFormat.format(getLabel(DELETE_PROMPT_MESSAGE), roleNames),
            dialogTitle,
            Messagebox.OK | Messagebox.CANCEL,
            Messagebox.QUESTION,
            e -> {
                if (Messagebox.ON_OK.equals(e.getName())) {
                    for (RoleModel r : selectedRoleModels) {
                        Role role = securityService.findRoleByName(r.getRole().getName());

                        if (CollectionUtils.isEmpty(role.getUsers())) {
                            LOGGER.info(MessageFormat.format("Deleting role {0}", role.getName()));
                            securityService.deleteRole(role);
                            Map<String, String> dataMap = Map.of("type", "DELETE_ROLE");
                            EventQueues
                                .lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
                                .publish(new Event("Role(s) Deleted", null, dataMap));
                        } else {
                            Map<String, Object> arg = new HashMap<>();
                            arg.put("rolesToDelete", selectedRoles);
                            arg.put("selectedRole", role);
                            arg.put("roleLabel", r.getLabel());
                            arg.put("displayRoleNameMap", roleMap);
                            Window window = (Window) Executions.getCurrent()
                                .createComponents(getPageDefinition("zul/delete-role.zul"), getSelf(), arg);
                            window.doModal();
                        }

                    }
                    setSelectedRole(null);
                }
            }
        );
    }

    @Listen("onClick = #roleCloneBtn")
    public void onClickRoleCloneBtn() {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel("noPermissionCloneRole_message"));
            return;
        }

        if (roleList.getSelectionCount() == 0) {
            Notification.error(getLabel("noCloneNoRoleSelected_message"));
            return;
        }

        //Clone each selected role
        roleList.getSelection().forEach(r -> securityService.createRole(cloneRole(r.getRole())));

        //Publish create role event after all roles are created
        Map<String, String> dataMap = Map.of("type", "CREATE_ROLE");
        EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
            .publish(new Event("Role Create", null, dataMap));
    }

    private Role cloneRole(Role originalRole) {
        String duplicateRoleNameFormat = "%s_%d";
        int count = 1;
        Role clonedRole = new Role();
        //Set unique role name
        String baseRoleName = getDisplayRoleName(originalRole.getName());
        String clonedRoleName = String.format(duplicateRoleNameFormat, baseRoleName, count);
        while (securityService.findRoleByName(clonedRoleName) != null) {
            clonedRoleName = String.format(duplicateRoleNameFormat, baseRoleName, ++count);
        }
        clonedRole.setName(clonedRoleName);
        clonedRole.setDescription("Custom role");
        //Get role permissions of selected log - all cloned roles should have login permission
        List<Permission> clonePermissions = securityService.getRolePermissions(originalRole.getName());
        if (clonePermissions.stream().noneMatch(p -> PermissionType.PORTAL_LOGIN.getId().equals(p.getRowGuid()))) {
            clonePermissions.add(securityService.getPermission(PermissionType.PORTAL_LOGIN.getName()));
        }
        clonedRole.setPermissions(new HashSet<>(clonePermissions));
        return clonedRole;
    }

    @Listen("onClick = #roleSaveBtn")
    public void onClickRoleSaveButton() {
        onClickRoleSaveButton(false);
    }

    private void onClickRoleSaveButton(boolean retainSelection) {
        onClickRoleSaveButton(retainSelection, selectedRoleTabGroups);
    }

    private void onClickRoleSaveButton(boolean retainSelection, Set<Group> previousSelectedGroups) {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel(NO_PERMISSION_EDIT_ROLE));
            return;
        }

        Set<User> assignedUsers = new HashSet<>(assignedUserRoleList.getListModel());
        Set<User> unassignedUsers = new HashSet<>(nonAssignedUserRoleList.getListModel());
        String newName = roleNameTextbox.getValue();

        if (CO_SELECTABLE_ROLES.contains(selectedRole.getName())) {
            saveUserRoleChanges(selectedRole, newName, assignedUsers, unassignedUsers, retainSelection);
            return;
        }

        List<String> unChangedRoles = new ArrayList<>(CO_SELECTABLE_ROLES);
        unChangedRoles.add(selectedRole.getName());
        for (User u : assignedUsers) {
            Set<Role> userRoles = securityService.findRolesByUser(u);
            //Show a confirmation message if the any user will be removed from a role.
            if (!userRoles.isEmpty() && userRoles.stream().anyMatch(r -> !unChangedRoles.contains(r.getName()))) {
                confirmSaveRole(retainSelection, previousSelectedGroups);
                return;
            }
        }
        saveUserRoleChanges(selectedRole, newName, assignedUsers, unassignedUsers, retainSelection);
    }

    private void confirmSaveRole(boolean retainSelection, Set<Group> previousSelectedGroups) {
        String displayRoleName = getDisplayRoleName(selectedRole.getName());
        Role roleToUpdate = selectedRole;
        Set<User> assignedUsers = new HashSet<>(assignedUserRoleList.getListModel());
        Set<User> unassignedUsers = new HashSet<>(nonAssignedUserRoleList.getListModel());
        String newName = roleNameTextbox.getValue();

        Messagebox.show(
            MessageFormat.format(getLabel("confirmChangeRole_message"), displayRoleName),
            dialogTitle,
            new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.CANCEL},
            Messagebox.QUESTION,
            e -> {
                String buttonName = e.getName();
                if (Messagebox.ON_YES.equals(buttonName)) {
                    saveUserRoleChanges(roleToUpdate, newName, assignedUsers, unassignedUsers, retainSelection);
                }
                if (Messagebox.ON_NO.equals(buttonName)) {
                    Set<Group> selectedGroups = selectedRoleTabGroups;
                    toggleApplyRoleView(isRoleTabUserView);
                    setSelectedRoleTabGroups(selectedGroups);
                    isRoleDetailDirty = false;
                }
                if (Messagebox.ON_CANCEL.equals(buttonName) && !isRoleTabUserView && roleToUpdate == selectedRole) {
                    roleTabGroupList.getListModel()
                        .setSelection(Objects.requireNonNullElse(previousSelectedGroups, Collections.emptySet()));
                    selectedRoleTabGroups = previousSelectedGroups;
                }
            }
        );
    }

    private void saveUserRoleChanges(Role role, String newName, Set<User> assignedUsers,
                                     Set<User> unassignedUsers, boolean retainSelection) {

        //Update assigned user roles
        if (!CO_SELECTABLE_ROLES.contains(role.getName())) {
            for (User u : assignedUsers) {
                Set<Role> userRoles = securityService.findRolesByUser(u);
                //Remove the newly assigned users from their non-integrator roles before reassigning
                if (!userRoles.isEmpty()
                    && userRoles.stream().noneMatch(r -> role.getName().equals(r.getName()))) {
                    userRoles.removeIf(r -> !CO_SELECTABLE_ROLES.contains(r.getName()));
                    u.setRoles(userRoles);
                    securityService.updateUser(u);
                }
            }
        }

        //Add and remove users to the selected role
        Set<User> currentUsers = role.getUsers();
        currentUsers.removeAll(unassignedUsers);
        currentUsers.removeAll(assignedUsers); //remove first to avoid duplicates
        currentUsers.addAll(assignedUsers);

        role.setUsers(currentUsers);
        //Only update the names of non-default roles
        if (!isDefaultRole(role)) {
            role.setName(newName);
        }

        try {
            securityService.updateRole(role);
            String displayRoleName = getDisplayRoleName(role.getName());
            Notification.info(
                MessageFormat.format(getLabel("updatedRoleDetails_message"), displayRoleName));
            isRoleDetailDirty = false;
            refreshRoles();
            refreshAssignedRoles();
            if (retainSelection && role != null) {
                String selectedRoleName = role.getName();
                ListModelList<RoleModel> currentRoleListModel = roleList.getListModel();
                RoleModel selectedRoleModel = currentRoleListModel.getInnerList().stream()
                    .filter(r -> selectedRoleName.equals(r.getRole().getName()))
                    .findFirst().orElse(null);
                currentRoleListModel.addToSelection(selectedRoleModel);
                Set<Group> currentSelectedRoleTabGroups = selectedRoleTabGroups;

                setSelectedRole(securityService.findRoleByName(selectedRoleName));
                toggleApplyRoleView(isRoleTabUserView);
                setSelectedRoleTabGroups(currentSelectedRoleTabGroups);
            } else {
                setSelectedRole(null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Messagebox.show(getLabel("failedUpdateRole_message"));
        }
    }

    @Listen("onCtrlKey = #nonAssignedUserRoleListbox")
    public void onCtrlKeyNonAssignedUserRoleListbox(KeyEvent keyEvent) {
        handleAssignedUserListboxCtrlKeyEvent(nonAssignedUserRoleList, keyEvent);
    }

    @Listen("onCtrlKey = #assignedUserRoleListbox")
    public void onCtrlKeyAssignedUserRoleListbox(KeyEvent keyEvent) {
        handleAssignedUserListboxCtrlKeyEvent(assignedUserRoleList, keyEvent);
    }

    @Listen("onClick = #assignUserGroupRoleToggle")
    public void onClickToggleApplyRoleView() {
        isRoleTabUserView = roleTabGroupListbox.isVisible(); //toggle between group and user view
        String selectedRoleName = selectedRole.getName();
        String displayRoleName = getDisplayRoleName(selectedRoleName);

        if (isRoleDetailDirty) {
            Messagebox.show(
                MessageFormat.format(getLabel(UNSAVED_ROLE_MESSAGE), displayRoleName),
                dialogTitle,
                new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.CANCEL},
                Messagebox.QUESTION,
                e -> {
                    String buttonName = e.getName();
                    if (Messagebox.ON_YES.equals(buttonName)) {
                        onClickRoleSaveButton(true);
                    }
                    if (Messagebox.ON_NO.equals(buttonName)) {
                        isRoleDetailDirty = !displayRoleName.equals(roleNameTextbox.getValue());
                        toggleApplyRoleView(isRoleTabUserView);
                    }
                }
            );
        } else {
            toggleApplyRoleView(isRoleTabUserView);
        }
    }

    /**
     * Switch between role assignment by user or group view.
     *
     * @param userView true to set role assignment to user view. false for group view.
     */
    private void toggleApplyRoleView(boolean userView) {
        isRoleTabUserView = userView;
        roleTabGroupListbox.setVisible(!userView);
        applyRoleUserSelection.setVisible(false); //To deal with flickering issue
        applyRoleUserSelection.setOrient(userView ? "horizontal" : "vertical");
        applyRoleUserSelectionButtons.setOrient(userView ? "vertical" : "horizontal");
        applyRoleUserSelectionButtons.setVflex(userView ? "1" : "min");
        applyRoleUserSelectionButtons.setHflex(userView ? "min" : "1");
        assignUserRoleBtn.setIconSclass(userView ? "z-icon-chevron-right" : "z-icon-chevron-down");
        retractUserRoleBtn.setIconSclass(userView ? "z-icon-chevron-left" : "z-icon-chevron-up");
        assignedUserRoleCheckbox.setVisible(!userView);
        nonAssignedUserRoleCheckbox.setVisible(!userView);
        applyRoleUserSelection.setVisible(true);
        ComponentUtils.toggleSclass(applyRoleUserSelection, userView);

        loadRoleTabUserAssignmentLists(selectedRole, null);
        if (!userView) {
            roleTabGroupList.unselectAll();
            assignedUserRoleCheckbox.setChecked(false);
            nonAssignedUserRoleCheckbox.setChecked(false);
        }
    }

    /**
     * Load the list of assigned and unassigned users of a role.
     *
     * @param role           assigned users are users with this role.
     * @param selectedGroups only users in this set of groups will be loaded.
     *                       Set to null to load all users regardless of groups.
     */
    private void loadRoleTabUserAssignmentLists(Role role, Set<Group> selectedGroups) {
        if (role == null || (!isRoleTabUserView && CollectionUtils.isEmpty(selectedGroups))) {
            assignedUserRoleModel = new ListModelList<>();
            nonAssignedUserRoleModel = new ListModelList<>();
            selectedRoleTabGroups = Collections.emptySet();
            ComponentUtils.toggleSclass(applyRoleUserSelection, false);
        } else {
            List<User> assignedUsers = new ArrayList<>(role.getUsers());
            List<User> nonAssignedUsers =
                isRoleTabUserView ? new ArrayList<>(securityService.getAllUsers()) : getGroupUsers(selectedGroups);
            assignedUsers
                .removeIf(u -> nonAssignedUsers.stream().noneMatch(nau -> nau.getUsername().equals(u.getUsername())));
            nonAssignedUsers.removeAll(assignedUsers);
            assignedUsers.sort(nameComparator);
            nonAssignedUsers.sort(nameComparator);
            assignedUserRoleModel = new ListModelList<>(assignedUsers, false);
            nonAssignedUserRoleModel = new ListModelList<>(nonAssignedUsers, false);
            ComponentUtils.toggleSclass(applyRoleUserSelection, true);
        }

        assignedUserRoleModel.setMultiple(true);
        assignedUserRoleList = new AssignedUserListbox(assignedUserRoleListbox, assignedUserRoleModel,
            getLabel("assignedUsers_text"));
        nonAssignedUserRoleModel.setMultiple(true);
        nonAssignedUserRoleList = new AssignedUserListbox(nonAssignedUserRoleListbox, nonAssignedUserRoleModel,
            getLabel("usersNotInRole_text"));
    }

    /**
     * Get a list of users in the selected groups.
     *
     * @param selectedGroups the groups to check for users in.
     * @return a list of distinct users in the selected groups.
     */
    private List<User> getGroupUsers(Set<Group> selectedGroups) {
        List<User> groupUsers = new ArrayList<>();
        if (selectedGroups != null) {
            for (Group g : selectedGroups) {
                Group userLoadedGroup = securityService.getGroupByName(g.getName());
                groupUsers.removeAll(userLoadedGroup.getUsers());
                groupUsers.addAll(userLoadedGroup.getUsers());
            }
        }
        return groupUsers;
    }

    @Listen("onSelect = #roleTabGroupListbox")
    public void onSelectRoleTabGroupListbox(SelectEvent<Listitem, Group> event) {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            Notification.error(getLabel(NO_PERMISSION_EDIT_ROLE));
            return;
        }

        Set<Group> newSelected = event.getSelectedObjects();
        if (isRoleDetailDirty) {
            String displayRoleName = getDisplayRoleName(selectedRole.getName());

            Messagebox.show(
                MessageFormat.format(getLabel(UNSAVED_ROLE_MESSAGE), displayRoleName),
                dialogTitle,
                new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.CANCEL},
                Messagebox.QUESTION,
                e -> {
                    String buttonName = e.getName();
                    Set<Group> previousSelectedGroups = selectedRoleTabGroups;
                    if (Messagebox.ON_YES.equals(buttonName)) {
                        selectedRoleTabGroups = newSelected;
                        onClickRoleSaveButton(true, previousSelectedGroups);
                    }
                    if (Messagebox.ON_NO.equals(buttonName)) {
                        isRoleDetailDirty = !displayRoleName.equals(roleNameTextbox.getValue());
                        setSelectedRoleTabGroups(newSelected);
                    }
                    if (Messagebox.ON_CANCEL.equals(buttonName)) {
                        roleTabGroupList.getListModel().setSelection(previousSelectedGroups);
                    }
                }
            );
        } else {
            setSelectedRoleTabGroups(newSelected);
        }
    }

    public void setSelectedRoleTabGroups(Set<Group> selectedGroups) {
        if (!isRoleTabUserView) {
            selectedRoleTabGroups = selectedGroups == null ? Collections.emptySet() : selectedGroups;
            roleTabGroupList.getListModel().setSelection(selectedRoleTabGroups);
            ComponentUtils.toggleSclass(applyRoleUserSelection, !selectedRoleTabGroups.isEmpty());
            loadRoleTabUserAssignmentLists(selectedRole, selectedRoleTabGroups);
            assignedUserRoleCheckbox.setChecked(false);
            nonAssignedUserRoleCheckbox.setChecked(false);
        } else {
            selectedRoleTabGroups = Collections.emptySet();
        }
    }

    @Listen("onClick = #userSelectAllBtn")
    public void onUserSelectAllBtn() {
        checkDirtyUser(null, null, true, null);
    }

    @Listen("onClick = #userSelectNoneBtn")
    public void onUserSelectNoneBtn() {
        checkDirtyUser(null, null, false, null);
    }

    @Listen("onClick = #groupSelectAllBtn")
    public void onGroupSelectAllBtn() {
        checkDirtyGroup(null, null, true, null);
    }

    @Listen("onClick = #groupSelectNoneBtn")
    public void onGroupSelectNoneBtn() {
        checkDirtyGroup(null, null, false, null);
    }

    @Listen("onClick = #roleSelectAllBtn")
    public void onRoleSelectAllBtn() {
        checkDirtyRole(null, null, true, null);
    }

    @Listen("onClick = #roleSelectNoneBtn")
    public void onRoleSelectNoneBtn() {
        checkDirtyRole(null, null, false, null);
    }

    @Listen("onClick = #okBtn")
    public void onClickOkButton() {
        getSelf().detach();
    }

    @Listen("onChanging = #firstNameTextbox")
    public void onChangingFirstName() {
        isUserDetailDirty = true;
    }

    @Listen("onChanging = #lastNameTextbox")
    public void onChangingLastName() {
        isUserDetailDirty = true;
    }

    @Listen("onChanging = #passwordTextbox")
    public void onChangingPassword() {
        isUserDetailDirty = true;
    }

    @Listen("onChanging = #confirmPasswordTextbox")
    public void onChangingConfirmPassword() {
        isUserDetailDirty = true;
    }

    @Listen("onChanging = #dateCreatedDatebox")
    public void onChangingCreatedDatebox() {
        isUserDetailDirty = true;
    }

    @Listen("onChanging = #lastActivityDatebox")
    public void onChangingLastActivityDatebox() {
        isUserDetailDirty = true;
    }

    @Listen("onChanging = #emailTextbox")
    public void onChangingEmail() {
        isUserDetailDirty = true;
    }

    @Listen("onChanging = #groupNameTextbox")
    public void onChangingGroupNameTextbox() {
        isGroupDetailDirty = true;
    }

    @Listen("onChanging = #roleNameTextbox")
    public void onChangingRoleNameTextbox(InputEvent event) {
        isRoleDetailDirty = isRoleChanged(event.getValue());
    }

    @Listen("onCheck = #assignedUserRoleCheckbox")
    public void selectAllAssignedUserRoles(CheckEvent event) {
        if (!assignedUserRoleList.getSourceListModel().isEmpty()) {
            selectBulk(assignedUserRoleList, event.isChecked());
        }
    }

    @Listen("onCheck = #nonAssignedUserRoleCheckbox")
    public void selectAllNonAssignedUserRoles(CheckEvent event) {
        if (!nonAssignedUserRoleList.getSourceListModel().isEmpty()) {
            selectBulk(nonAssignedUserRoleList, event.isChecked());
        }
    }

    public PageDefinition getPageDefinition(String uri) throws IOException {
        String url = "static/" + uri;
        Execution current = Executions.getCurrent();
        PageDefinition pageDefinition = current.getPageDefinitionDirectly(
            new InputStreamReader(getClass().getClassLoader().getResourceAsStream(url)), "zul");
        return pageDefinition;
    }

    private boolean isDefaultRole(Role role) {
        return roleMap.containsKey(role.getName());
    }

    private String getDisplayRoleName(String originalName) {
        return roleMap.getOrDefault(originalName, originalName);
    }

    private void handleAssignedUserListboxCtrlKeyEvent(AssignedUserListbox listbox, KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KEY_CTRL_A_LO:
            case KEY_CTRL_A_BG:
                selectBulk(listbox, true);
                break;
            default:
                LOGGER.error("Unsupported Ctrl key");
        }
    }

}
