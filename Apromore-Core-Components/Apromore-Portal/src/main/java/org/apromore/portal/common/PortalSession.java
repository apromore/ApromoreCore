/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderType;
import org.slf4j.Logger;

public class PortalSession {

    private final Logger LOGGER = PortalLoggerFactory.getLogger(PortalSession.class);

    public final String CURRENT_FOLDER = "CURRENT_FOLDER";
    public final String PREVIOUS_FOLDER = "PREVIOUS_FOLDER";
    public final String TREE = "TREE";
    public final String MAIN_CONTROLLER = "MAIN_CONTROLLER";
    public final String SELECTED_FOLDER_IDS = "SELECTED_FOLDER_IDS";
    public final String SELECTED_PROCESS_IDS = "SELECTED_PROCESS_IDS";

    /**
     * Map for storing current portal session
     */
    Map<String,Object> map = new HashMap<>();

    public PortalSession(MainController mainController) {
        setMainController(mainController);
    }

    private Object getAttribute(String attribute) {
        return map.get(attribute);
    }

    private void setAttribute(String attribute, Object value) {
        map.put(attribute, value);
    }

    public void setCurrentFolder(FolderType folder) {
        setAttribute(CURRENT_FOLDER, folder);
        if (folder != null && getMainController() != null) {
            getMainController().setBreadcrumbs(folder.getId());
        }
    }

    public FolderType getCurrentFolder() {
        FolderType folder = (FolderType) getAttribute(CURRENT_FOLDER);
        if (folder == null) {
            folder = new FolderType();
            folder.setId(0);
            folder.setFolderName("Home");
            setCurrentFolder(folder);
        }

        return folder;
    }

    public void setSelectedFolderIds(List<Integer> folderIds) {
        setAttribute(SELECTED_FOLDER_IDS, folderIds);
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getSelectedFolderIds() {
        if (getAttribute(SELECTED_FOLDER_IDS) != null) {
            return (List<Integer>) getAttribute(SELECTED_FOLDER_IDS);
        }

        return new ArrayList<>();
    }

    public void setPreviousFolder(FolderType folder) {
        setAttribute(PREVIOUS_FOLDER, folder);
    }

    public FolderType getPreviousFolder() {
        if (getAttribute(PREVIOUS_FOLDER) != null) {
            return (FolderType) getAttribute(PREVIOUS_FOLDER);
        }

        return null;
    }

    public void setTree(List<FolderType> folders) {
        setAttribute(TREE, folders);
    }

    @SuppressWarnings("unchecked")
    public List<FolderType> getTree() {
        if (getAttribute(TREE) != null) {
            return (List<FolderType>) getAttribute(TREE);
        }

        return null;
    }

    public void setMainController(MainController mainController) {
        setAttribute(MAIN_CONTROLLER, mainController);
    }

    public MainController getMainController() {
        if (getAttribute(MAIN_CONTROLLER) != null) {
            return (MainController) getAttribute(MAIN_CONTROLLER);
        }

        return null;
    }
}
