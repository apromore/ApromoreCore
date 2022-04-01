/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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

package org.apromore.portal.dialogController;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apromore.dao.model.User;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.common.FolderTreeRenderer;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.workspaceOptions.RenameFolderController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.FolderType;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

public class NavigationController extends BaseController {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(BaseListboxController.class);

    private MainController mainC;
    private Component mainComponent;
    private Tree tree;
    private PortalContext portalContext;
    private Map<String, PortalPlugin> portalPluginMap;
    private List<Integer> openFolderItems;
    public static final String APROMORE = "Apromore";
    public static final String PORTAL_WARNING_TEXT = "portal_warning_text";


    public NavigationController(MainController newMainC,Component mainComponent) throws Exception {
        this.mainComponent = mainComponent;
        this.mainC=newMainC;

        Window treeW = (Window) mainComponent.getFellow("navigationcomp").getFellow("treeW");
//        treeW.setContentStyle("background-image: none; background-color: white");
        Center centre = (Center) mainComponent.getFellow("leftInnerCenterPanel");


        tree = (Tree) treeW.getFellow("tree");
//        tree.setStyle("background-image: none; background-color: white");
        this.portalContext = new PluginPortalContext(this.mainC);
        this.portalPluginMap = PortalPluginResolver.getPortalPluginMap();

        Button expandBtn = (Button) treeW.getFellow("expand");
        Button contractBtn = (Button) treeW.getFellow("contract");
        expandBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                doCollapseExpandAll(tree, true);
            }
        });
        contractBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                doCollapseExpandAll(tree, false);
            }
        });

        centre.addEventListener(Events.ON_RIGHT_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                //Do nothing. But just to catch the event to avoid browser default right menu
            }
        });

    }


    /**
     * Loads the workspace.
     */
    public void loadWorkspace() {
        FolderTreeModel model = new FolderTreeModel(new FolderTree(false, mainC).getRoot(), null);
        tree.setItemRenderer(new FolderTreeRenderer(mainC));
        tree.setModel(model);
    }
    /*Load Tree with selected open items */
    public void loadTreeSpace(List<Integer> folderIds) {
        FolderTreeModel model = new FolderTreeModel(new FolderTree(false, mainC).getRoot(), null);
        tree.setItemRenderer(new FolderTreeRenderer(mainC, folderIds));
        tree.setModel(model);
    }

    /* Expand or Collapse the tree. */
    private void doCollapseExpandAll(Component component, boolean open) {
        if (component instanceof Treeitem) {
            Treeitem treeitem = (Treeitem) component;
            treeitem.setOpen(open);
        }
        Collection<?> children = component.getChildren();
        if (children != null) {
            for (Object child : children) {
                doCollapseExpandAll((Component) child, open);

            }
        }
    }

    public void currentFolderChanged() {
        updateFolders(tree, mainC.getPortalSession().getCurrentFolder());
    }

    /**
     * Update all folders at or below the passed component, setting them to be correctly opened or selected
     */
    private static boolean updateFolders(Component component, FolderType currentFolder) {

        boolean containsCurrentFolder = false;
        for (Component child: component.getChildren()) {
            boolean childContainsCurrentFolder = updateFolders(child, currentFolder);
            containsCurrentFolder = containsCurrentFolder || childContainsCurrentFolder;
        }

        if (component instanceof Treeitem) {
            Treeitem treeitem = (Treeitem) component;

            Object value = treeitem.getValue();
            if (value instanceof FolderTreeNode) {
                FolderType folder = (FolderType) ((FolderTreeNode) value).getData();
                boolean match = currentFolder.equals(folder);
                treeitem.setSelected(match);
                containsCurrentFolder = containsCurrentFolder || match || folder.getId() == 0;
            }
            treeitem.setOpen(containsCurrentFolder);
        }

        return containsCurrentFolder;
    }

    public void selectCurrentFolder() {
        findAndSelectFolder(tree, mainC.getPortalSession().getCurrentFolder());
    }

    private static void findAndSelectFolder(Component component, FolderType currentFolder) {
        for (Component child : component.getChildren()) {
            findAndSelectFolder(child, currentFolder);
        }

        if (component instanceof Treeitem) {
            Treeitem treeitem = (Treeitem) component;
            Object value = treeitem.getValue();
            if (value instanceof FolderTreeNode) {
                FolderType folder = (FolderType) ((FolderTreeNode) value).getData();
                boolean match = currentFolder.getId() == folder.getId();
                treeitem.setSelected(match);
            }
        }
    }

    public void copy(FolderType selectedFolder) {
        mainC.getCopyPasteController().copy(Collections.singleton(selectedFolder), 1, selectedFolder);
    }

    public void cut(FolderType selectedFolder) {
        if (mainC.getCopyPasteController().cut(Collections.singleton(selectedFolder), 1, selectedFolder)) {
            tree.getItems().stream().forEach(item -> {
                item.removeSclass("ap-item-cut-selected");
            });

            tree.getSelectedItems().stream().forEach(item -> {
                item.setSclass("ap-item-cut-selected");
            });
        }
    }

    public void paste(FolderType selectedFolder) {
        if (selectedFolder == null) {
            Notification.error(Labels.getLabel("portal_failedFind_message"));
            return;
        }
        if (mainC.getCopyPasteController().getSelectedItems().isEmpty()) {
            Notification.error(Labels.getLabel("portal_selectOneItemAndCutCopy_message"));
            return;
        }

        for(Object item:mainC.getCopyPasteController().getSelectedItems()) {
            if(item instanceof FolderType) {
                if (((FolderType)item).getId() == selectedFolder.getId()) {
                    Notification.error(Labels.getLabel("portal_source_destination_folder_notsame_message"));
                    return;
                }
            }
        }
        try {
            mainC.getCopyPasteController().paste(selectedFolder);
            mainC.reloadSummariesWithOpenTreeItems(mainC.getNavigationController().getAllOpenFolderItems());
        } catch (Exception e) {
            LOGGER.error("Error in cut/copy folder from tree",e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void share(FolderType selectedFolder) {
        PortalPlugin accessControlPlugin;
        mainC.eraseMessage();
        // Check for ownership is moved to plugin level
        try {
            portalPluginMap = PortalPluginResolver.getPortalPluginMap();
            accessControlPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_ACCESS_CONTROL);

            Map arg = new HashMap<>();
            arg.put("withFolderTree", false);
            arg.put("selectedItem", selectedFolder);
            arg.put("currentUser", UserSessionManager.getCurrentUser());
            arg.put("autoInherit", true);
            arg.put("showRelatedArtifacts", true);
            arg.put("enablePublish", mainC.getConfig().isEnablePublish());

            accessControlPlugin.setSimpleParams(arg);
            accessControlPlugin.execute(portalContext);
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
        }
    }


    public void rename(FolderType selectedFolder) throws InterruptedException {

        if (selectedFolder == null) {
            Notification.error(Labels.getLabel("portal_failedFind_message"));
            return;
        }
        try {
            if (ItemHelpers.canModify(mainC.getSecurityService().getUserById(UserSessionManager.getCurrentUser().getId()),
                selectedFolder)) {
                mainC.eraseMessage();
                new RenameFolderController(mainC, selectedFolder.getId(), selectedFolder.getFolderName());
            } else {
               Notification.error(Labels.getLabel("portal_noPrivilegeRename_message"));
            }
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            Notification.error(e.getMessage());
            return;
        }
    }

    public void removeFolder(FolderType selectedFolder) {

        if (selectedFolder == null) {
            Notification.error(Labels.getLabel("portal_failedFind_message"));
            return;
        }
        if( !(mainC.getManagerService().hasWritePermission(UserSessionManager.getCurrentUser().getUsername(),Arrays.asList(selectedFolder)))) {
            Notification.error(Labels.getLabel("portal_deleteItemRestricted_message"));
            return;
        }

        Messagebox.show(Labels.getLabel("portal_deleteFolderPrompt_message"), Labels.getLabel(PORTAL_WARNING_TEXT),
                Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                case Messagebox.YES:
                    try {
                        mainC.getManagerService().deleteFolder(selectedFolder.getId(),
                                UserSessionManager.getCurrentUser().getUsername());
                    } catch (Exception e) {
                        LOGGER.error("Failed to delete folder from Tree", e);
                        Notification.error(Labels.getLabel("portal_deleteItemRestricted_message"));
                    }
                    mainC.reloadSummariesWithOpenTreeItems(mainC.getNavigationController().getAllOpenFolderItems());
                    break;
                case Messagebox.NO:
                    break;
                default:
                }
            }
        });
    }

    public List<Integer> getAllOpenFolderItems(){
        openFolderItems = new ArrayList<>();
        try {
            findAndStoreOpenFolderItemList(tree);
        }catch (Exception ex){
            LOGGER.error("Error in getting tree information",ex);
        }
        return openFolderItems;
    }
    public void restoreTreeItem(List<Integer> openFolderItems){
        if(openFolderItems.isEmpty()){
            return;
        }
        try {
            this.openFolderItems = new ArrayList<>(openFolderItems);
            findAndReopenFolderItem(tree);
        }catch (Exception ex){
            LOGGER.error("Error in restoring tree information",ex);
        }
    }
    private void findAndStoreOpenFolderItemList(Component component) {
        if(openFolderItems==null){
            return;
        }
        for (Component child : component.getChildren()) {
            findAndStoreOpenFolderItemList(child);
        }

        if (component instanceof Treeitem) {
            Treeitem treeitem = (Treeitem) component;
            Object value = treeitem.getValue();
            if (treeitem.isOpen() && value instanceof FolderTreeNode) {
                FolderType folder = (FolderType) ((FolderTreeNode) value).getData();
                openFolderItems.add(folder.getId());
            }
        }
    }

    private void findAndReopenFolderItem(Component component) {
        if(openFolderItems.isEmpty()){
            return;
        }
        for (Component child : component.getChildren()) {
            findAndReopenFolderItem(child);
        }
        if (component instanceof Treeitem) {
            Treeitem treeitem = (Treeitem) component;
            Object value = treeitem.getValue();
            if (value instanceof FolderTreeNode) {
                FolderType folder = (FolderType) ((FolderTreeNode) value).getData();
                if(openFolderItems.contains(folder.getId())){
                    treeitem.setOpen(true);
                }
            }
        }
    }

}
