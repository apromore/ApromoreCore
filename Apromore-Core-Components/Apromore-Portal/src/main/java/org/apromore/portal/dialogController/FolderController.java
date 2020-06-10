/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.model.FolderType;
import org.apromore.portal.common.UserSessionManager;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Html;

/**
 * handles the navigation and movement around in folders.
 *
 * @author Igor
 */
public class FolderController extends GenericForwardComposer {

    private FolderType searchedFolder = null;

    @Override
    @SuppressWarnings("unchecked")
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
    }

    public void onFolderClick$folderWindow(Event event) {
        ForwardEvent eventx = (ForwardEvent) event;
//        Hbox folderOptions = (Hbox) eventx.getOrigin().getTarget().getParent().getFellow("workspaceOptionsPanel").getFellow("folderOptions");
//        Button btnRenameFolder = (Button) folderOptions.getFellow("btnRenameFolder");
//        Button btnRemoveFolder = (Button) folderOptions.getFellow("btnRemoveFolder");

        try {
            String idsString = eventx.getOrigin().getData().toString();

            UserSessionManager.setSelectedFolderIds(new ArrayList<Integer>());
            UserSessionManager.setSelectedProcessIds(new ArrayList<Integer>());
            UserSessionManager.getMainController().clearProcessVersions();

            if (!idsString.isEmpty()) {
//                folderOptions.setVisible(true);
                String[] ids = idsString.split(",");
//                if (ids.length == 1) {
//                    btnRenameFolder.setVisible(true);
//                } else {
//                    btnRenameFolder.setVisible(false);
//                }
                List<Integer> folderIds = new ArrayList<>();
                List<Integer> processIds = new ArrayList<>();
                boolean canDelete = true;
                boolean canRename = true;
                for (String id : ids) {
                    String[] tokens = id.split("~");

                    if (tokens.length == 3) {
                        if (tokens[0].equals("f")) {
                            folderIds.add(Integer.parseInt(tokens[1]));
                        } else {
                            processIds.add(Integer.parseInt(tokens[1]));
                        }
                        int access = Integer.parseInt(tokens[2]);
                        if (access < 4) {
                            canDelete = false;
                        }
                        if (access < 2) {
                            canRename = false;
                        }
                    }
                }

//                btnRemoveFolder.setVisible(canDelete);
//                btnRenameFolder.setVisible(btnRenameFolder.isVisible() && canRename);
                UserSessionManager.setSelectedFolderIds(folderIds);
                UserSessionManager.setSelectedProcessIds(processIds);
                UserSessionManager.getMainController().updateSelectedListBox(processIds);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Clients.evalJavaScript("bindTiles();");
    }

    public void onFolderDblClick$folderWindow(Event event) {
        getData(event, true);
    }

    public void onBreadCrumbClick$breadCrumbsWindow(Event event) {
        getData(event, false);
    }

    public void getData(Event event, boolean isFolder) {
        ForwardEvent eventx = (ForwardEvent) event;
        int selectedFolderId = Integer.parseInt(eventx.getOrigin().getData().toString());
        Html html = (Html) eventx.getOrigin().getTarget().getParent().getFellow("folders");

//            Hbox folderOptions = (Hbox) eventx.getOrigin().getTarget().getParent().getFellow("workspaceOptionsPanel").getFellow("folderOptions");
//            Button btnRenameFolder = (Button) folderOptions.getFellow("btnRenameFolder");
//            Button btnRemoveFolder = (Button) folderOptions.getFellow("btnRemoveFolder");
//            btnRenameFolder.setVisible(false);
//            btnRemoveFolder.setVisible(false);

        if (html != null) {
            FolderType selectedFolder = null;
            List<FolderType> availableFolders = UserSessionManager.getCurrentFolder() == null || UserSessionManager.getCurrentFolder().getId() == 0 ? UserSessionManager.getTree() : UserSessionManager.getCurrentFolder().getFolders();
            //List<ProcessSummaryType> availableProcesses = ((ManagerService) SpringUtil.getBean("managerClient")).getProcessOrLogSummaries(UserSessionManager.getCurrentUser().getId(), selectedFolderId);

            if (isFolder) {
                for (FolderType folder : availableFolders) {
                    if (folder.getId() == selectedFolderId) {
                        selectedFolder = folder;
                        break;
                    }
                }
            } else {
                FolderType folderType = new FolderType();
                folderType.setId(0);
                for (FolderType folder : UserSessionManager.getTree()) {
                    folderType.getFolders().add(folder);
                }
                findFolder(folderType, selectedFolderId);
                selectedFolder = searchedFolder;
            }

            if (selectedFolder != null) {
                UserSessionManager.setPreviousFolder(UserSessionManager.getCurrentFolder());
                UserSessionManager.setCurrentFolder(selectedFolder);

                UserSessionManager.getMainController().reloadSummaries();
                //loadWorkspace(html, selectedFolder.getFolders(), availableProcesses);
                //Clients.evalJavaScript("bindTiles();");
            }
        }
    }

    private void findFolder(FolderType folder, int selectedFolderId) {
        if (folder.getId() == selectedFolderId) {
            searchedFolder = folder;
            return;
        }

        for (FolderType folderType : folder.getFolders()) {
            findFolder(folderType, selectedFolderId);
        }
    }

//    public void loadWorkspace(Html html, List<FolderType> folders, List<ProcessSummaryType> processes) {
//        String content = "<ul class='workspace'>";
//
//        for (FolderType folder : folders) {
//            int access = 1;
//            if (folder.isHasWrite()) {
//                access = 2;
//            }
//            if (folder.isHasOwnership()) {
//                access = 4;
//            }
//            content += String.format("<li class='folder' id='%d' access='%d'><div>%s</div></li>", folder.getId(), access, folder.getFolderName());
//        }
//
//        for (ProcessSummaryType process : processes) {
//            int access = 1;
//            if (process.isHasWrite()) {
//                access = 2;
//            }
//            if (process.isHasOwnership()) {
//                access = 4;
//            }
//            content += String.format("<li class='process' id='%d' access='%d'><div>%s</div></li>", process.getId(), access, process.getName().length() > 20 ? process.getName().substring(0, 16) + "" : process.getName());
//        }
//
//        content += "</ul>";
//        html.setContent(content);
//    }

}
