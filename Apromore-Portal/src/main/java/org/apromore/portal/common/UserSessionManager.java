/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.common;

import java.util.ArrayList;
import java.util.List;

import org.apromore.model.FolderType;
import org.apromore.model.UserType;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;

public class UserSessionManager {

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
    private static final String SIGNAVIO_SESSION = "SIGNAVIO_SESSION";


    private UserSessionManager() { }


    public static void setCurrentUser(UserType user) {
        Executions.getCurrent().getSession().setAttribute(USER, user);
    }

    public static UserType getCurrentUser() {
        if (Executions.getCurrent().getSession().getAttribute(USER) != null) {
            return (UserType) Executions.getCurrent().getSession().getAttribute(USER);
        } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
            setCurrentUser((UserType) SecurityContextHolder.getContext().getAuthentication().getDetails());
            return (UserType) Executions.getCurrent().getSession().getAttribute(USER);
        }
        return null;
    }

    public static void setEditSession(String id, SignavioSession session) {
        Executions.getCurrent().getSession().setAttribute(SIGNAVIO_SESSION + id, session);
    }

    public static SignavioSession getEditSession(String id) {
        if (Executions.getCurrent().getSession().getAttribute(SIGNAVIO_SESSION + id) != null) {
            return (SignavioSession) Executions.getCurrent().getSession().getAttribute(SIGNAVIO_SESSION + id);
        }
        return null;
    }

    public static void setCurrentFolder(FolderType folder) {
        Executions.getCurrent().getSession().setAttribute(CURRENT_FOLDER, folder);
    }

    public static FolderType getCurrentFolder() {
        if (Executions.getCurrent().getSession().getAttribute(CURRENT_FOLDER) != null) {
            return (FolderType) Executions.getCurrent().getSession().getAttribute(CURRENT_FOLDER);
        }

        return null;
    }

    public static void setSelectedFolderIds(List<Integer> folderIds) {
        Executions.getCurrent().getSession().setAttribute(SELECTED_FOLDER_IDS, folderIds);
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> getSelectedFolderIds() {
        if (Executions.getCurrent().getSession().getAttribute(SELECTED_FOLDER_IDS) != null) {
            return (List<Integer>) Executions.getCurrent().getSession().getAttribute(SELECTED_FOLDER_IDS);
        }

        return new ArrayList<>();
    }

    public static void setSelectedProcessIds(List<Integer> processIds) {
        Executions.getCurrent().getSession().setAttribute(SELECTED_PROCESS_IDS, processIds);
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> getSelectedProcessIds() {
        if (Executions.getCurrent().getSession().getAttribute(SELECTED_PROCESS_IDS) != null) {
            return (List<Integer>) Executions.getCurrent().getSession().getAttribute(SELECTED_PROCESS_IDS);
        }

        return new ArrayList<>();
    }

    public static void setPreviousFolder(FolderType folder) {
        Executions.getCurrent().getSession().setAttribute(PREVIOUS_FOLDER, folder);
    }

    public static FolderType getPreviousFolder() {
        if (Executions.getCurrent().getSession().getAttribute(PREVIOUS_FOLDER) != null) {
            return (FolderType) Executions.getCurrent().getSession().getAttribute(PREVIOUS_FOLDER);
        }

        return null;
    }

    public static void setTree(List<FolderType> folders) {
        Executions.getCurrent().getSession().setAttribute(TREE, folders);
    }

    @SuppressWarnings("unchecked")
    public static List<FolderType> getTree() {
        if (Executions.getCurrent().getSession().getAttribute(TREE) != null) {
            return (List<FolderType>) Executions.getCurrent().getSession().getAttribute(TREE);
        }

        return null;
    }

    public static void setMainController(MainController mainController) {
        Executions.getCurrent().getSession().setAttribute(MAIN_CONTROLLER, mainController);
    }

    public static MainController getMainController() {
        if (Executions.getCurrent().getSession().getAttribute(MAIN_CONTROLLER) != null) {
            return (MainController) Executions.getCurrent().getSession().getAttribute(MAIN_CONTROLLER);
        }

        return null;
    }

    public static void setCurrentSecurityItem(Integer id) {
        Executions.getCurrent().getSession().setAttribute(CURRENT_SECURITY_ITEM, id);
    }

    public static Integer getCurrentSecurityItem() {
        if (Executions.getCurrent().getSession().getAttribute(CURRENT_SECURITY_ITEM) != null) {
            return (Integer) Executions.getCurrent().getSession().getAttribute(CURRENT_SECURITY_ITEM);
        }

        return null;
    }

    public static void setCurrentSecurityType(FolderTreeNodeTypes type) {
        Executions.getCurrent().getSession().setAttribute(CURRENT_SECURITY_TYPE, type);
    }

    public static FolderTreeNodeTypes getCurrentSecurityType() {
        if (Executions.getCurrent().getSession().getAttribute(CURRENT_SECURITY_TYPE) != null) {
            return (FolderTreeNodeTypes) Executions.getCurrent().getSession().getAttribute(CURRENT_SECURITY_TYPE);
        }

        return FolderTreeNodeTypes.Folder;
    }

    public static void setCurrentSecurityOwnership(boolean hasOwnership) {
        Executions.getCurrent().getSession().setAttribute(CURRENT_SECURITY_OWNERSHIP, hasOwnership);
    }

    public static boolean getCurrentSecurityOwnership() {
        if (Executions.getCurrent().getSession().getAttribute(CURRENT_SECURITY_OWNERSHIP) != null) {
            return (Boolean) Executions.getCurrent().getSession().getAttribute(CURRENT_SECURITY_OWNERSHIP);
        }

        return false;
    }
}
