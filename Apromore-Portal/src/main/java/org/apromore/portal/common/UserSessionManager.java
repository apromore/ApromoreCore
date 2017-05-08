/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.portal.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.model.FolderType;
import org.apromore.model.UserType;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;

public class UserSessionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSessionManager.class);

    private static final String USER = "USER";
    private static final String CURRENT_FOLDER = "CURRENT_FOLDER";
    private static final String CURRENT_SECURITY_ITEM = "CURRENT_SECURITY_ITEM";
    private static final String CURRENT_SECURITY_TYPE = "CURRENT_SECURITY_TYPE";
    private static final String CURRENT_SECURITY_OWNERSHIP = "CURRENT_SECURITY_OWNERSHIP";
    private static final String PREVIOUS_FOLDER = "PREVIOUS_FOLDER";
    private static final String TREE = "TREE";
    private static final String MAIN_CONTROLLER = "MAIN_CONTROLLER";
    private static final String SELECTED_FOLDER_IDS = "SELECTED_FOLDER_IDS";
    private static final String SELECTED_PROCESS_IDS = "SELECTED_PROCESS_IDS";

    /**
     * Map from user session UUIDs passed as the query part of URLs, to Signavio session objects.
     */
    static Map<String,SignavioSession> editSessionMap = new HashMap<>();

    private UserSessionManager() { }


    public static void setCurrentUser(UserType user) {
        setAttribute(USER, user);
    }

    private static Session getSession() {
        Execution execution = Executions.getCurrent();
        if (execution == null) {
            throw new RuntimeException("No current execution");
        }

        Session session = execution.getSession();
        if (session == null) {
            throw new RuntimeException("Session was not set for the current execution");
        }

        return session;
    }

    private static Object getAttribute(String attribute) {
        return getSession().getAttribute(attribute);
    }

    private static void setAttribute(String attribute, Object value) {
        getSession().setAttribute(attribute, value);
    }

    public static UserType getCurrentUser() {
        if (getAttribute(USER) != null) {
            return (UserType) getAttribute(USER);
        } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
            setCurrentUser((UserType) SecurityContextHolder.getContext().getAuthentication().getDetails());
            return (UserType) getAttribute(USER);
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
        if (folder != null) {
            getMainController().setBreadcrumbs(folder.getId());
        }
    }

    public static FolderType getCurrentFolder() {
        if (getAttribute(CURRENT_FOLDER) != null) {
            return (FolderType) getAttribute(CURRENT_FOLDER);
        }

        return null;
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
