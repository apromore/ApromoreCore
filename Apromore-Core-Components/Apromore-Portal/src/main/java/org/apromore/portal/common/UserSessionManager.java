/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.portal.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.FolderType;
import org.apromore.model.MembershipType;
import org.apromore.model.UserType;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.security.ApromoreWebAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Sessions;

public abstract class UserSessionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSessionManager.class);

    public static final String USER = "USER";
    public static final String CURRENT_FOLDER = "CURRENT_FOLDER";
    public static final String CURRENT_SECURITY_ITEM = "CURRENT_SECURITY_ITEM";
    public static final String CURRENT_SECURITY_TYPE = "CURRENT_SECURITY_TYPE";
    public static final String CURRENT_SECURITY_OWNERSHIP = "CURRENT_SECURITY_OWNERSHIP";
    public static final String PREVIOUS_FOLDER = "PREVIOUS_FOLDER";
    public static final String TREE = "TREE";
    public static final String MAIN_CONTROLLER = "MAIN_CONTROLLER";
    public static final String SELECTED_FOLDER_IDS = "SELECTED_FOLDER_IDS";
    public static final String SELECTED_PROCESS_IDS = "SELECTED_PROCESS_IDS";

    /**
     * Map from user session UUIDs passed as the query part of URLs, to Signavio session objects.
     */
    static Map<String,SignavioSession> editSessionMap = new HashMap<>();

    public static void setCurrentUser(UserType user) {
        setAttribute(USER, user);
    }

    private static Object getAttribute(String attribute) {
        return Sessions.getCurrent().getAttribute(attribute);
    }

    private static void setAttribute(String attribute, Object value) {
        Sessions.getCurrent().setAttribute(attribute, value);
    }

    public static UserType getCurrentUser() {
        return (UserType) getAttribute(USER);
    }

    public static void initializeUser(ManagerService manager) {

        // No initialization required if the user is already set
        if (getAttribute(USER) != null) {
            return;
        }

        LOGGER.debug("Initializing user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object details = authentication.getDetails();
            if (details instanceof ApromoreWebAuthenticationDetails) {  // LDAP login
                String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                LOGGER.debug("LDAP login, user=" + username);
                UserType user = manager.readUserByUsername(username);
                if (user == null) {
                    try {
                        user = constructUserType(username);
                        manager.writeUser(user);

                    } catch (Exception e) {
                        LOGGER.error("Unable to initialize user " + username + " for LDAP login", e);
                        return;
                    }
                }
                setCurrentUser(user);

            } else if (details instanceof UserType) {  // Locally created user
                LOGGER.debug("Local login, user=" + details);
                setCurrentUser((UserType) details);

            } else if (details != null) {
                LOGGER.warn("Unsupported details class " + details.getClass());
            } else {
                LOGGER.warn("User's authentication has null details");
            }

        } else {
            LOGGER.debug("Current user neither set on the security context, nor authenticated");
        }
    }

    // Lifted from NewUserRegistrationHttpServletRequestHandler.java
    private static UserType constructUserType(String username) throws NamingException {

        // Obtain a JNDI context
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldaps://centaur.unimelb.edu.au");
        InitialDirContext context = new InitialDirContext(env);

        // Query the LDAP directory
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attributes = { "givenName", "sn", "mail" };
        constraints.setReturningAttributes(attributes);
        NamingEnumeration results = context.search("ou=people,o=unimelb", String.format("uid=%s", username), constraints);
        SearchResult      result  = (SearchResult) results.next();
        Attributes        attrs   = result.getAttributes();

        // Create the user record
        MembershipType membership = new MembershipType();
        membership.setEmail(findAttributeFieldByName(attrs, attributes[2]));
        //membership.setPassword("password");  // Beware that if you specify a value here, it (in addition to the LDAP password) can be used to authenticate
        //membership.setPasswordQuestion("question");
        //membership.setPasswordAnswer("answer");
        //membership.setFailedLogins(0);
        //membership.setFailedAnswers(0);

        UserType user = new UserType();
        user.setFirstName(findAttributeFieldByName(attrs, attributes[0]));
        user.setLastName(findAttributeFieldByName(attrs, attributes[1]));
        user.setUsername(username);
        user.setMembership(membership);

        return user;
    }

    private static String findAttributeFieldByName(Attributes attributes, String fieldName) throws NamingException {
        Attribute attr = attributes.get(fieldName);
        NamingEnumeration e = attr.getAll();
        while (e.hasMore()) {
            return (String) e.next();
        }
        return null;
    }

    // TODO: fix the memory leak by reclaiming stale sessions
    public static void setEditSession(String id, SignavioSession session) {
        editSessionMap.put(id, session);
    }

    public static SignavioSession getEditSession(String id) {
        return editSessionMap.get(id);
    }

    public static void setCurrentFolder(FolderType folder) {
        setAttribute(CURRENT_FOLDER, folder);
        if (folder != null && getMainController() != null) {
            getMainController().setBreadcrumbs(folder.getId());
        }
    }

    public static FolderType getCurrentFolder() {
        FolderType folder = (FolderType) getAttribute(CURRENT_FOLDER);
        if (folder == null) {
            folder = new FolderType();
            folder.setId(0);
            folder.setFolderName("Home");
            setCurrentFolder(folder);
        }

        return folder;
    }

    public static void setSelectedFolderIds(List<Integer> folderIds) {
        setAttribute(SELECTED_FOLDER_IDS, folderIds);
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> getSelectedFolderIds() {
        if (getAttribute(SELECTED_FOLDER_IDS) != null) {
            return (List<Integer>) getAttribute(SELECTED_FOLDER_IDS);
        }

        return new ArrayList<>();
    }

    public static void setSelectedProcessIds(List<Integer> processIds) {
        setAttribute(SELECTED_PROCESS_IDS, processIds);
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> getSelectedProcessIds() {
        if (getAttribute(SELECTED_PROCESS_IDS) != null) {
            return (List<Integer>) getAttribute(SELECTED_PROCESS_IDS);
        }

        return new ArrayList<>();
    }

    public static void setPreviousFolder(FolderType folder) {
        setAttribute(PREVIOUS_FOLDER, folder);
    }

    public static FolderType getPreviousFolder() {
        if (getAttribute(PREVIOUS_FOLDER) != null) {
            return (FolderType) getAttribute(PREVIOUS_FOLDER);
        }

        return null;
    }

    public static void setTree(List<FolderType> folders) {
        setAttribute(TREE, folders);
    }

    @SuppressWarnings("unchecked")
    public static List<FolderType> getTree() {
        if (getAttribute(TREE) != null) {
            return (List<FolderType>) getAttribute(TREE);
        }

        return null;
    }

    public static void setMainController(MainController mainController) {
        setAttribute(MAIN_CONTROLLER, mainController);
    }

    public static MainController getMainController() {
        if (getAttribute(MAIN_CONTROLLER) != null) {
            return (MainController) getAttribute(MAIN_CONTROLLER);
        }

        return null;
    }

    public static void setCurrentSecurityItem(Integer id) {
        setAttribute(CURRENT_SECURITY_ITEM, id);
    }

    public static Integer getCurrentSecurityItem() {
        if (getAttribute(CURRENT_SECURITY_ITEM) != null) {
            return (Integer) getAttribute(CURRENT_SECURITY_ITEM);
        }

        return null;
    }

    public static void setCurrentSecurityType(FolderTreeNodeTypes type) {
        setAttribute(CURRENT_SECURITY_TYPE, type);
    }

    public static FolderTreeNodeTypes getCurrentSecurityType() {
        if (getAttribute(CURRENT_SECURITY_TYPE) != null) {
            return (FolderTreeNodeTypes) getAttribute(CURRENT_SECURITY_TYPE);
        }

        return FolderTreeNodeTypes.Folder;
    }

    public static void setCurrentSecurityOwnership(boolean hasOwnership) {
        setAttribute(CURRENT_SECURITY_OWNERSHIP, hasOwnership);
    }

    public static boolean getCurrentSecurityOwnership() {
        if (getAttribute(CURRENT_SECURITY_OWNERSHIP) != null) {
            return (Boolean) getAttribute(CURRENT_SECURITY_OWNERSHIP);
        }

        return false;
    }
}
