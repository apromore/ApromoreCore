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
package org.apromore.plugin.portal.useradmin;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apromore.portal.common.zk.ComponentUtils;
import org.apromore.portal.common.notification.Notification;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.portal.model.UserType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.types.EventQueueTypes;
import org.apromore.portal.types.EventQueueEvents;
import org.apromore.service.SecurityService;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.WorkspaceService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
//import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
//import org.zkoss.spring.SpringUtil;
import org.zkoss.json.JSONObject;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModels;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import org.apromore.plugin.portal.useradmin.common.SearchableListbox;
import org.apromore.plugin.portal.useradmin.listbox.*;
import org.apromore.plugin.portal.useradmin.listbox.TristateModel;

public class UserAdminController extends SelectorComposer<Window> {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(UserAdminController.class);
    private Map<String, String> roleMap = new HashMap<String, String>() {
        {
            put("ROLE_USER", "User");
            put("ROLE_ADMIN", "Administrator");
            put("ROLE_MANAGER", "Manager");
            put("ROLE_ANALYST", "Analyst");
            put("ROLE_OBSERVER", "Observer");
            put("ROLE_DESIGNER", "Designer");
            put("ROLE_DATA_SCIENTIST", "Data Scientist");
            put("ROLE_OPERATIONS", "Operations");
            put("ROLE_INTEGRATOR", "Integrator");
        }
    };

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
        }};

    Window mainWindow;
    User currentUser;
    User selectedUser = null;
    Group selectedGroup;
    Set<User> selectedUsers;

    ListModelList<Group> groupModel;
    ListModelList<TristateModel> assignedRoleModel;
    ListModelList<TristateModel> assignedGroupModel;

    ListModelList<User> userModel;
    ListModelList<User> candidateUserModel;
    ListModelList<User> allUserModel;
    ListModelList<User> nonAssignedUserModel;
    ListModelList<User> assignedUserModel;

    UserListbox userList;
    GroupListbox groupList;
    AssignedUserListbox nonAssignedUserList;
    AssignedUserListbox assignedUserList;
    TristateListbox<Role> assignedRoleList;
    TristateListbox<Group> assignedGroupList;

    TristateItemRenderer assignedRoleItemRenderer;
    TristateItemRenderer assignedGroupItemRenderer;

    private boolean isUserDetailDirty = false;
    private boolean isGroupDetailDirty = false;

    boolean canViewUsers;
    boolean canEditUsers;
    boolean canEditGroups;
    boolean canEditRoles;

    private PortalContext portalContext = (PortalContext) Executions.getCurrent().getArg().get("portalContext");
    private SecurityService securityService = (SecurityService) Executions.getCurrent().getArg().get("securityService");
    private WorkspaceService workspaceService = (WorkspaceService) Executions.getCurrent().getArg().get("workspaceService");

    @Wire("#tabbox")
    Tabbox tabbox;

    @Wire("#userTab")
    Tab userTab;
    @Wire("#groupTab")
    Tab groupTab;

    @Wire("#userListView")
    Vbox userListView;

    @Wire("#userDetailContainer")
    Vbox userDetailContainer;
    @Wire("#groupDetailContainer")
    Vbox groupDetailContainer;

    @Wire("#userListbox")
    Listbox userListbox;
    @Wire("#groupListbox")
    Listbox groupListbox;

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

    /**
     * Test whether the current user has a permission.
     *
     * @param permission any permission
     * @return whether the authenticated user has the <var>permission</var>
     */
    private boolean hasPermission(Permissions permission) {
        return securityService.hasAccess(portalContext.getCurrentUser().getId(), permission.getRowGuid());
    }

    public ResourceBundle getLabels() {
        // Locale locale = Locales.getCurrent()
        Locale locale = (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE);
        return ResourceBundle.getBundle("metainfo.zk-label",
            locale,
            UserAdminController.class.getClassLoader());
    }

    public String getLabel(String key) {
        return getLabels().getString(key);
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        mainWindow = win;
        String userName = portalContext.getCurrentUser().getUsername();
        currentUser = securityService.getUserByName(userName);
        selectedUser = null;

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

        /**
         * Enable toggle selection in user Listbox on individual row
         */
        userEditBtn.addEventListener("onToggleClick", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
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
            }
        });

        /**
         * Enable toggle selection in group Listbox on individual row
         */
        groupEditBtn.addEventListener("onToggleClick", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
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
            }
        });

        String onSwitchTab = "function (notify, init) { " +
                "console.log('user', arguments);" +
                "if (this.desktop && !init && notify) { zAu.send(new zk.Event(this, 'onSwitchTab')); }" +
                "else { this.$_sel(notify, init); }" +
                "}";
        // https://forum.zkoss.org/question/72022/intercepting-tab-selection/
        // https://forum.zkoss.org/question/55097/set-selected-tab/
        // prevent select at client side
        userTab.setWidgetOverride("_sel", onSwitchTab);
        groupTab.setWidgetOverride("_sel", onSwitchTab);
        userTab.setSelected(true);

        groupTab.addEventListener("onSwitchTab", new EventListener() {
            @Override
            public void onEvent(Event event) throws InterruptedException {
                Tab tab = (Tab) event.getTarget();
                Tab selectedTab = tabbox.getSelectedTab();
                if (userTab.equals(selectedTab)) {
                    checkDirtyUser(null, null, null, tab);
                }
            }
        });

        userTab.addEventListener("onSwitchTab", new EventListener() {
            @Override
            public void onEvent(Event event) throws InterruptedException {
                Tab tab = (Tab) event.getTarget();
                Tab selectedTab = tabbox.getSelectedTab();
                if (groupTab.equals(selectedTab)) {
                    checkDirtyGroup(null, null, null, tab);
                }
            }
        });

        /*
        // Park this for now in case in-cell editing is required later

        groupEditBtn.addEventListener("onExecute", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                String groupName = event.getData().toString();
                setSelectedGroup(securityService.getGroupByName(groupName));
            }
        });
        groupEditBtn.addEventListener("onChangeNameCancel", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                JSONObject param = (JSONObject) event.getData();
                String groupName = (String)param.get("groupName");
                String rowGuid = (String)param.get("rowGuid");
                Group group = securityService.getGroupByName(groupName);
                Textbox textbox = (Textbox)mainWindow.getFellow(rowGuid);
                textbox.setValue(groupName);
            }
        });
        groupEditBtn.addEventListener("onChangeNameOK", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (!hasPermission(Permissions.EDIT_GROUPS)) {
                    Messagebox.show("You do not have permission to edit group", "Apromore", Messagebox.OK, Messagebox.ERROR);
                    return;
                }
                JSONObject param = (JSONObject) event.getData();
                String groupName = (String)param.get("groupName");
                String rowGuid = (String)param.get("rowGuid");
                Group group = securityService.getGroupByName(groupName);
                Textbox textbox = (Textbox)mainWindow.getFellow(rowGuid);
                if ("".equals(textbox.getValue())) {
                    securityService.deleteGroup(group);
                    groupModel.remove(group);
                    Notification.info("Group " + group.getName() + " is deleted");
                } else {
                    group.setName(textbox.getValue());
                    securityService.updateGroup(group);
                    Notification.info("Details for group " + group.getName() + " is updated");
                }
                refreshGroups();
                refreshAssignedGroups();
            }
        });
        */

        // Register ZK event handler
        EventQueue securityEventQueue = EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true);
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
                    if (eventType.equals("CREATE_USER") || eventType.equals("DELETE_USER")) {
                        refreshUsers();
                        refreshCandidateUsers();
                    }

                    // Update the group collection
                    if (eventType.equals("CREATE_GROUP") || eventType.equals("DELETE_GROUP")) {
                        refreshGroups();
                        refreshAssignedGroups();
                    }

                    // Update the user panel
                    if ("UPDATE_USER".equals(eventType)) {
                        // TO DO: Check for dirty group detail
                        // Reset group panel
                        setSelectedGroup(null);
                    } if ("UPDATE_GROUP".equals(eventType)) {
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
                }
            }
        });

        EventQueues.lookup(EventQueueTypes.TRANSFER_OWNERSHIP, EventQueues.DESKTOP, true)
            .subscribe(
                new EventListener() {
                    @Override
                    public void onEvent(Event evt) {
                        if (EventQueueEvents.ON_TRANSFERRED.equals(evt.getName())) {
                            User user = (User) evt.getData();
                            if (user != null) {
                                securityService.deleteUser(user);
                            }
                        }
                    }
                }
            );

        EventQueues.lookup(EventQueueTypes.PURGE_ASSETS, EventQueues.DESKTOP, true)
            .subscribe(
                new EventListener() {
                    @Override
                    public void onEvent(Event evt) {
                        if (EventQueueEvents.ON_PURGED.equals(evt.getName())) {
                            User user = (User) evt.getData();
                            if (user != null) {
                                securityService.deleteUser(user);
                            }
                        }
                    }
                }
            );

        // Register OSGi event handler
        BundleContext bundleContext = (BundleContext) getSelf().getDesktop().getWebApp().getServletContext().getAttribute("osgi-bundlecontext");
        String filter = "(" + EventConstants.EVENT_TOPIC + "=" + SecurityService.EVENT_TOPIC + ")";
        Collection<ServiceReference> forwarders = bundleContext.getServiceReferences(EventHandler.class, filter);
        if (forwarders.isEmpty()) {
            Dictionary<String, Object> properties = new Hashtable<>();
            properties.put(EventConstants.EVENT_TOPIC, SecurityService.EVENT_TOPIC);
            bundleContext.registerService(EventHandler.class.getName(), new EventHandler() {
                @Override
                public final void handleEvent(org.osgi.service.event.Event event) {
                    Map<String, Object> properties = new HashMap<>();
                    for (String propertyName : event.getPropertyNames()) {
                        properties.put(propertyName, event.getProperty(propertyName));
                    }
                    securityEventQueue.publish(new Event(event.getTopic(), null, properties));
                }
            }, properties);
        }
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
        nonAssignedUserList = new AssignedUserListbox(nonAssignedUserListbox, nonAssignedUserModel, "Users not in the group");
    }

    private void refreshGroups() {
        groupModel = new ListModelList<>(securityService.findElectiveGroups(), false);
        groupList.setSourceListModel(groupModel);
        groupList.reset();
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
            assignedRoleModel.add(new TristateModel(roleMap.get(roleName), roleName, role, TristateModel.UNCHECKED));
        }
        assignedRoleModel.setMultiple(true);
        assignedRoleListbox.setModel(assignedRoleModel);
        assignedRoleListbox.setNonselectableTags("*");
        assignedRoleItemRenderer = new TristateItemRenderer();
        assignedRoleListbox.setItemRenderer(assignedRoleItemRenderer);
        assignedRoleList = new TristateListbox<Role>(assignedRoleListbox, assignedRoleModel, "Assigned Roles");
        assignedRoleItemRenderer.setList(assignedRoleList);

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
            assignedGroupModel.add(new TristateModel(groupName, groupName, group, TristateModel.UNCHECKED));
        }
        assignedGroupModel.setMultiple(true);
        assignedGroupListbox.setModel(assignedGroupModel);
        assignedGroupListbox.setNonselectableTags("*");
        assignedGroupItemRenderer = new TristateItemRenderer();
        assignedGroupListbox.setItemRenderer(assignedGroupItemRenderer);
        assignedGroupList = new TristateListbox<Group>(assignedGroupListbox, assignedGroupModel, "Assigned Groups");
        assignedGroupItemRenderer.setList(assignedGroupList);
    }

    private void updateTristateModels(TristateListbox list, Map<String, Integer> tally, Integer total) {
        Map<String, Integer> keyToIndexMap = list.getKeyToIndexMap();
        for (Map.Entry<String, Integer> entry : keyToIndexMap.entrySet()) {
            String key = entry.getKey();
            int index = (int)entry.getValue();
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
        for (User user: users) {
            Set<Role> roles = securityService.findRolesByUser(user);
            for (Role role: roles) {
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
        for (User user: users) {
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
        for (User user: users) {
            Set<Group> groups = securityService.findGroupsByUser(user);
            for (Group group: groups) {
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
        for (User user: users) {
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
        assignedRoleList.reset();
        assignedGroupList.reset();
        assignedRoleListbox.setDisabled(false);
        assignedGroupListbox.setDisabled(false);
        ComponentUtils.toggleSclass(userDetailContainer, true);
        if (users == null || users.size() == 0 || users.size() > 1) {
            selectedUser = null;
            selectedUsers = users;
            firstNameTextbox.setValue("");
            lastNameTextbox.setValue("");
            dateCreatedDatebox.setValue(null);
            lastActivityDatebox.setValue(null);
            emailTextbox.setValue("");
            if (users == null) {
                userDetail.setValue("No user is selected");
                userSaveBtn.setDisabled(true);
            } else {
                userDetail.setValue("Multiple users are selected");
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
            }
        } else {
            selectedUser = users.iterator().next();
            selectedUsers = users;
            firstNameTextbox.setValue(selectedUser.getFirstName());
            lastNameTextbox.setValue(selectedUser.getLastName());
            dateCreatedDatebox.setValue(selectedUser.getDateCreated());
            lastActivityDatebox.setValue(selectedUser.getLastActivityDate());
            emailTextbox.setValue(selectedUser.getMembership().getEmail());
            userDetail.setValue("User: " + selectedUser.getUsername());
            userSaveBtn.setDisabled(false);
            assignedRoleItemRenderer.setForceTwoState(true);
            assignedGroupItemRenderer.setForceTwoState(true);
        }
        updateAssignedRoleModel(users);
        updateAssignedGroupModel(users);
        isUserDetailDirty = false; // ensure dirty is not set by field's setValue
    }

    private Group setSelectedGroup(Group group) {
        assignedUserAddView.setVisible(false);

        if (group == null) {
            groupNameTextbox.setValue("");
            groupDetail.setValue("No group is selected");
            assignedUserModel = new ListModelList<>();
            nonAssignedUserModel = new ListModelList<>();
            groupSaveBtn.setDisabled(true);
            ComponentUtils.toggleSclass(groupDetailContainer, false);
        } else {
            groupNameTextbox.setValue(group.getName());
            groupDetail.setValue("Group: " + group.getName());
            List<User> assignedUsers = new ArrayList<>(group.getUsers());
            List<User> nonAssignedUsers = new ArrayList<>(securityService.getAllUsers());
            nonAssignedUsers.removeAll(assignedUsers);
            Collections.sort(assignedUsers, nameComparator);
            Collections.sort(nonAssignedUsers, nameComparator);
            assignedUserModel = new ListModelList<User>(assignedUsers, false);
            nonAssignedUserModel = new ListModelList<User>(nonAssignedUsers, false);
            groupSaveBtn.setDisabled(false);
            ComponentUtils.toggleSclass(groupDetailContainer, true);
        }
        assignedUserModel.setMultiple(true);
        assignedUserList = new AssignedUserListbox(assignedUserListbox, assignedUserModel, "Assigned Users");
        nonAssignedUserModel.setMultiple(true);
        nonAssignedUserList = new AssignedUserListbox(nonAssignedUserListbox, nonAssignedUserModel, "Users not in the group");
        selectedGroup = group;
        isGroupDetailDirty = false; // ensure dirty is not set by field's setValue
        return group;
    }

    /**
     * Find the User from ListModelList based on the set
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
            Messagebox.show("You do not have privilege to view user.");
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
     * Check dirty user detail
     *
     * @param prevUsers Previously selected users
     * @param newUsers Newly selected users
     * @param select Null do nothing, true select all, false unselect all
     * @return
     */
    public void checkDirtyUser(Set<User> prevUsers, Set<User> newUsers, Boolean select, Tab tab) {
        if (isUserDetailDirty) {
            Messagebox.show(getLabel("dirtyUser_message"),
                    "Apromore",
                    new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.CANCEL},
                    Messagebox.QUESTION,
                    new org.zkoss.zk.ui.event.EventListener() {
                        public void onEvent(Event e) {
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
                            }
                            if (newUsers != null) {
                                updateUserDetail(newUsers);
                            }
                            if (tab != null) {
                                 tab.setSelected(true);
                            }
                        }
                    }
            );
        } else {
            if (select != null) {
                selectBulk(userList, select);
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
     * Update user detail when required
     *
     * @param newUsers
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
            Notification.error("You do not have permission to assign group(s)");
            return;
        }
        isUserDetailDirty = true;
    }

    @Listen("onSelect = #assignedRoleListbox")
    public void onSelectAssignedRolesListbox(SelectEvent event) {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            assignedRoleListbox.setSelectedItems(event.getPreviousSelectedItems());
            Notification.error("You do not have permission to assign roles");
            return;
        }
        isUserDetailDirty = true;
    }

    @Listen("onClick = #userAddBtn")
    public void onClickuserAddBtn() {
        if (!hasPermission(Permissions.EDIT_USERS)) {
            Notification.error("You do not have permission to add user");
            return;
        }

        try {
            Map arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            Window window = (Window) Executions.getCurrent().createComponents("user-admin/zul/create-user.zul", getSelf(), arg);
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
        List<String> users = new ArrayList<>();
        for (User u : selectedUsers) {
            users.add(u.getUsername());
        }
        String userNames = String.join(",", users);
        Messagebox.show(
                MessageFormat.format(getLabel("deletePrompt_message"), userNames),
                "Apromore",
                Messagebox.OK | Messagebox.CANCEL,
                Messagebox.QUESTION,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(Event e) {
                        if (Messagebox.ON_OK.equals(e.getName())) {
                            for (User user : selectedUsers) {
                                LOGGER.info("Deleting user " + user.getUsername());
                                if (workspaceService.isOnlyOwner(user)) {
                                    try {
                                        Map arg = new HashMap<>();
                                        arg.put("selectedUser", user);
                                        Window window = (Window) Executions.getCurrent().createComponents("user-admin/zul/delete-user.zul", getSelf(), arg);
                                        window.doModal();
                                    } catch (Exception ex) {
                                        LOGGER.error("Unable to create transfer owner dialog", ex);
                                        Messagebox.show(getLabel("failedLaunchTransferOwner_message"));
                                    }
                                } else {
                                    securityService.deleteUser(user);
                                    // Force logout the deleted user
                                    EventQueues.lookup("forceSignOutQueue", EventQueues.APPLICATION, true)
                                            .publish(new Event("onSignout", null, user.getUsername()));
                                }
                            }
                            setSelectedUsers(null);
                        }
                    }
                }
        );
    }

    @Listen("onClick = #userSaveBtn")
    public void onClickUserSaveButton() {
        boolean passwordDirty = false;

        if (!hasPermission(Permissions.EDIT_USERS)) {
                Notification.error("You do not have permission to edit user");
            return;
        }
        if (selectedUser != null) {
            if (passwordTextbox.getValue() != null && passwordTextbox.getValue().length() > 0) {
                if (passwordTextbox.getValue().length() < 6) {
                    Messagebox.show(getLabel("passwordTooShort_message"), null, Messagebox.OK, Messagebox.ERROR);
                    return;
                } else if (!Objects.equals(passwordTextbox.getValue(), confirmPasswordTextbox.getValue())) {
                    Messagebox.show(getLabel("passwordNoMatch_message"), null, Messagebox.OK, Messagebox.ERROR);
                    return;
                }
                passwordDirty = true;
            }

            selectedUser.setFirstName(firstNameTextbox.getValue());
            selectedUser.setLastName(lastNameTextbox.getValue());
            saveAssignedGroup(selectedUsers, false);
            saveAssignedRole(selectedUsers, false);
            selectedUser.getMembership().setEmail(emailTextbox.getValue());
            if (passwordDirty) {
                selectedUser.getMembership().setPassword(SecurityUtil.hashPassword(passwordTextbox.getValue()));
                selectedUser.getMembership().setSalt("username");
            }
            selectedUser.getMembership().setUser(selectedUser);
            securityService.updateUser(selectedUser);
            Notification.info(MessageFormat.format(getLabel("userUpdated_message"), selectedUser.getUsername()));
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
            Notification.error("You do not have permission to edit group");
            return;
        }
        Set<Group> newGroups = event.getSelectedObjects();
        Set<Group> prevGroups = event.getPreviousSelectedObjects();
        checkDirtyGroup(prevGroups, newGroups, null, null);
    }

    /**
     * Check dirty group detail
     *
     * @param prevGroups Previously selected groups
     * @param newGroups Newly selected groups
     * @param select Null do nothing, true select all, false unselect all
     * @return
     */
    public void checkDirtyGroup(Set<Group> prevGroups, Set<Group> newGroups, Boolean select, Tab tab) {
        if (isGroupDetailDirty) {
            Messagebox.show("There is unsaved group detail. Do you want to save the information?",
                    "Question",
                    new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.CANCEL},
                    Messagebox.QUESTION,
                    new org.zkoss.zk.ui.event.EventListener() {
                        public void onEvent(Event e) {
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
                            }
                            if (newGroups != null) {
                                updateGroupDetail(newGroups);
                            }
                            if (tab != null) {
                                tab.setSelected(true);
                            }
                        }
                    }
            );
        } else {
            if (select != null) {
                selectBulk(groupList, select);
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
     * Update group detail when required
     *
     * @param newGroups
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
            Notification.error("You do not have permission to allocate users to a group");
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
            Notification.error("You do not have permission to allocate users to a group");
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
            Notification.error("You do not have permission to allocate users to a group");
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

    @Listen("onClick = #retractUser")
    public void onClickRetractUser() {
        if (selectedGroup == null) {
            return;
        }
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error("You do not have permission to allocate users to a group");
            return;
        }
        List<User> users = new ArrayList<User>(assignedUserList.getSelection());
        if (users != null && users.size() >= 1) {
            for(User user: users) {
                nonAssignedUserModel.add(user);
                assignedUserModel.remove(user);
                nonAssignedUserList.reset();
                assignedUserList.reset();
            }
            isGroupDetailDirty = true;
        }
    }

    @Listen("onClick = #assignUser")
    public void onClickAssignUser() {
        if (selectedGroup == null) {
            return;
        }
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error("You do not have permission to allocate users to a group");
            return;
        }
        List<User> users = new ArrayList<User>(nonAssignedUserList.getSelection());
        if (users != null && users.size() >= 1) {
            for(User user: users) {
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
            Notification.error("You do not have permission to create group");
            return;
        }

        try {
            Map arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            Window window = (Window) Executions.getCurrent().createComponents("user-admin/zul/create-group.zul", getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create group creation dialog", e);
            Messagebox.show("Unable to create group creation dialog");
        }
    }

    @Listen("onClick = #groupRemoveBtn")
    public void onClickGroupRemoveBtn() {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error("You do not have permission to delete group");
            return;
        }
        Set<Group> selectedGroups = groupList.getSelection();
        if (groupList.getSelectionCount() == 0) {
            Notification.error("Nothing to delete");
            return;
        }

        List<String> groups = new ArrayList<>();
        for (Group g : selectedGroups) {
            groups.add(g.getName());
        }
        String groupNames = String.join(",", groups);
        Messagebox.show("Do you really want to delete " + groupNames + "?",
                "Question",
                Messagebox.OK | Messagebox.CANCEL,
                Messagebox.QUESTION,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(Event e) {
                        if (Messagebox.ON_OK.equals(e.getName())) {
                            for (Group group : selectedGroups) {
                                LOGGER.info("Deleting user " + group.getName());
                                securityService.deleteGroup(group);
                            }
                            setSelectedGroup(null);
                        }
                    }
                }
        );
    }

    @Listen("onClick = #groupSaveBtn")
    public void onClickGroupSaveButton() {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            Notification.error("You do not have permission to edit groups");
            return;
        }
        ListModelList listModel = assignedUserList.getListModel();
        Set<User> users = new HashSet<User>(listModel);
        selectedGroup.setName(groupNameTextbox.getValue());
        selectedGroup.setUsers(users);
        securityService.updateGroup(selectedGroup);
        Notification.info("Details for group " + selectedGroup.getName() + " is updated");
        isGroupDetailDirty = false;
        refreshGroups();
        setSelectedGroup(null);
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

}
