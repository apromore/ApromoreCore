/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.apromore.portal.dialogController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apromore.model.*;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.common.TabListitem;
import org.apromore.portal.common.TabQuery;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.workspaceOptions.AddFolderController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.ExplicitComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

public class MenuController extends Menubar {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

    private final MainController mainC;
    private Menubar menuB;
    private PortalContext portalContext;

    public MenuController(final MainController mainController) throws ExceptionFormats {
        this.mainC = mainController;
        this.portalContext = new PluginPortalContext(mainC);
        this.menuB = (Menubar) this.mainC.getFellow("menucomp").getFellow("operationMenu");

        Menuitem folderMI = (Menuitem) this.menuB.getFellow("createFolder");
        Menuitem createMI = (Menuitem) this.menuB.getFellow("createProcess");
        Menuitem createMI2 = (Menuitem) this.menuB.getFellow("createProcess2");
        Menuitem importMI = (Menuitem) this.menuB.getFellow("fileImport");
        Menuitem exportMI = (Menuitem) this.menuB.getFellow("fileExport");
        Menuitem editModelMI = (Menuitem) this.menuB.getFellow("processEdit");
        Menuitem editModelMI2 = (Menuitem) this.menuB.getFellow("processEdit2");
        Menuitem editDataMI = (Menuitem) this.menuB.getFellow("dataEdit");
        Menuitem deleteMI = (Menuitem) this.menuB.getFellow("processDelete");
        /*
        Menuitem copyMI = (Menuitem) this.menuB.getFellow("processCopy");
        copyMI.setDisabled(true);
        Menuitem pasteMI = (Menuitem) this.menuB.getFellow("processPaste");
        pasteMI.setDisabled(true);
        Menuitem moveMI = (Menuitem) this.menuB.getFellow("processMove");
        moveMI.setDisabled(true);
        */

        boolean showSignavio = (Boolean) SpringUtil.getBean("portalShowSignavio");
        createMI.setVisible(showSignavio);
        editModelMI.setVisible(showSignavio);

        folderMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                createFolder();
            }
        });
        createMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                createModel();
            }
        });
        createMI2.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                createModel2();
            }
        });
        importMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                importFile();
            }
        });
        editModelMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                editNative();
            }
        });
        editModelMI2.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                editNative2();
            }
        });
        editDataMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                editData();
            }
        });
        exportMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                exportFile();
            }
        });
        deleteMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                deleteSelectedElements();
            }
        });

        // If there are portal plugins, create the menus for launching them
        if (!PortalPluginResolver.resolve().isEmpty()) {
            
            // If present, this comparator expresses the preferred ordering for menus along the the menu bar
            Comparator<String> ordering = (ExplicitComparator) SpringUtil.getBean("portalMenuOrder");

            SortedMap<String, Menu> menuMap = new TreeMap<>(ordering);
            for (final PortalPlugin plugin: PortalPluginResolver.resolve()) {
                if (plugin.getAvailability(portalContext) == PortalPlugin.Availability.UNAVAILABLE) {
                    continue;
                }

                String menuName = plugin.getGroupLabel(Locale.getDefault());

                // Create a new menu if this is the first menu item within it
                if (!menuMap.containsKey(menuName)) {
                    Menu menu = new Menu(menuName);
                    menu.appendChild(new Menupopup());
                    menuMap.put(menuName, menu);
                }
                assert menuMap.containsKey(menuName);

                // Create the menu item
                Menu menu = menuMap.get(menuName);
                Menuitem menuitem = new Menuitem();
                if (plugin.getResourceAsStream(plugin.getIconPath()) != null) {
                    try {
                        menuitem.setImage("portalPluginResource/"
                            + URLEncoder.encode(plugin.getGroupLabel(Locale.getDefault()), "utf-8") + "/"
                            + URLEncoder.encode(plugin.getLabel(Locale.getDefault()), "utf-8") + "/"
                            + plugin.getIconPath());

                    } catch (UnsupportedEncodingException e) {
                        throw new Error("Hardcoded UTF-8 encoding failed", e);
                    }
                } else {
                    menuitem.setImageContent(plugin.getIcon());
                }
                menuitem.setLabel(plugin.getLabel(Locale.getDefault()));
                menuitem.setDisabled(plugin.getAvailability(portalContext) == PortalPlugin.Availability.DISABLED);

                // Insert the menu item alphabetically into the menu
                Menuitem precedingMenuitem = null;
                List<Menuitem> existingMenuitems = menu.getMenupopup().getChildren();
                for (Menuitem existingMenuitem: existingMenuitems) {
                    if (menuitem.getLabel().compareTo(existingMenuitem.getLabel()) <= 0) {
                        precedingMenuitem = existingMenuitem;
                        break;
                    }
                }
                menu.getMenupopup().insertBefore(menuitem, precedingMenuitem);

                menuitem.addEventListener("onClick", new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        plugin.execute(new PluginPortalContext(mainC));
                    }
                });
            }

            // Add the menus to the menu bar
            for (final Menu menu: menuMap.values()) {
                if (!"Account".equals(menu.getLabel())) {
                    menuB.appendChild(menu);
                }
            }

            Menuseparator separator = new Menuseparator();
            separator.setHflex("1");
            separator.setStyle("border-width: 0");
            menuB.appendChild(separator);

            for (final Menu menu: menuMap.values()) {
                if ("Account".equals(menu.getLabel())) {
                    try {
                        menu.setLabel(UserSessionManager.getCurrentUser().getUsername());
                        menu.setSclass("ap-user-menu");
                    } catch (Exception e) {
                        LOGGER.warn("Unable to set Account menu to current user name", e);
                    }

                    menuB.appendChild(menu);
                }
            }
        }
    }


    protected void createFolder() {
        this.mainC.eraseMessage();
        try {
            new AddFolderController(this.mainC);
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void createModel() throws InterruptedException {
        this.mainC.eraseMessage();
        try {
            new CreateProcessController(this.mainC, this.mainC.getNativeTypes());
        } catch (SuspendNotAllowedException | InterruptedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionDomains e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve domains reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionFormats e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve formats reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionAllUsers e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve users reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        }

    }
    
    protected void createModel2() throws InterruptedException {
        this.mainC.eraseMessage();
        try {
            new CreateProcessController2(this.mainC, this.mainC.getNativeTypes());
        } catch (SuspendNotAllowedException | InterruptedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionDomains e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve domains reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionFormats e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve formats reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionAllUsers e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve users reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        }

    }    

    /**
     * Edit all selected process versions.
     * @throws InterruptedException
     * @throws org.apromore.portal.exception.ExceptionFormats
     * @throws SuspendNotAllowedException
     */
    protected void editNative() throws InterruptedException, SuspendNotAllowedException, ExceptionFormats, ParseException {
        this.mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){

                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();

                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        TabListitem tabItem=(TabListitem)item;
                        HashMap<SummaryType, List<VersionSummaryType>> processVersion = new HashMap<>();
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                        new EditListProcessesController(this.mainC, this,processVersion);
                        return;
                    }
                }
            }
        }
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedElementsAndVersions();
        if (selectedProcessVersions.size() != 0) {
            new EditListProcessesController(this.mainC, this, selectedProcessVersions);
        } else {
            this.mainC.displayMessage("No process version selected.");
        }
    }
    
    protected void editNative2() throws InterruptedException, SuspendNotAllowedException, ExceptionFormats, ParseException {
        this.mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){

                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();

                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        TabListitem tabItem=(TabListitem)item;
                        HashMap<SummaryType, List<VersionSummaryType>> processVersion = new HashMap<>();
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                        new EditListProcessesController(this.mainC, this,processVersion);
                        return;
                    }
                }
            }
        }
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedElementsAndVersions();
        if (selectedProcessVersions.size() != 0) {
            new EditListProcessesController2(this.mainC, this, selectedProcessVersions);
        } else {
            this.mainC.displayMessage("No process version selected.");
        }
    }


    /**
     * Delete all selected process versions.
     * @throws Exception
     */
    protected void deleteSelectedElements() throws Exception {
        this.mainC.eraseMessage();
        Map<SummaryType, List<VersionSummaryType>> elements = mainC.getSelectedElementsAndVersions();
        if (elements.size() != 0) {
            this.mainC.deleteElements(elements);
            mainC.clearProcessVersions();
        } else {
            this.mainC.displayMessage("No process version selected.");
        }
    }

    protected void exportFile() throws Exception {
        if(this.mainC.getSelectedElements().size() == 1) {
            SummaryType summaryType = this.mainC.getSelectedElements().iterator().next();
            System.out.println(summaryType);
            if (summaryType instanceof LogSummaryType) {
                exportLog();
            } else if (summaryType instanceof ProcessSummaryType) {
                exportNative();
            }
        }
    }


    /**
     * Export all selected process versions, each of which in a native format to be chosen by the user
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     * @throws org.apromore.portal.exception.ExceptionFormats
     */
    protected void exportNative() throws SuspendNotAllowedException, InterruptedException, ExceptionFormats, ParseException {
        this.mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){
                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                HashMap<SummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        TabListitem tabItem=(TabListitem)item;
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                    }
                }
                if(processVersion.keySet().size()>0){
                    new ExportListNativeController(this.mainC, this, processVersion);
                    return;
                }
            }
        }

        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedElementsAndVersions();
        if (selectedProcessVersions.size() != 0) {
            new ExportListNativeController(this.mainC, this, selectedProcessVersions);
        } else {
            this.mainC.displayMessage("No process version selected.");
        }
    }

    /**
     * Export all selected process versions, each of which in a native format to be chosen by the user
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     * @throws org.apromore.portal.exception.ExceptionFormats
     */
    protected void exportLog() throws Exception {
        if(this.mainC.getSelectedElements().size() == 1) {
            SummaryType summaryType = this.mainC.getSelectedElements().iterator().next();
            System.out.println(summaryType);
            if(summaryType instanceof LogSummaryType) {
                ExportLogResultType exportResult = mainC.getService().exportLog(summaryType.getId(), summaryType.getName());
                try (InputStream native_is = exportResult.getNative().getInputStream()) {
                    this.mainC.showPluginMessages(exportResult.getMessage());
                    Filedownload.save(native_is, "application/x-gzip", summaryType.getName() + ".xes.gz");
                }
            }
        }
    }

    protected void importFile() throws InterruptedException {
        this.mainC.eraseMessage();
        try {
            new ImportController(mainC);
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    /**
     * Return all selected process versions structured in an Hash map:
     * <p, l> belongs to the result <=> for the process whose id is p, all versions whose
     * name belong to l are selected.
     * @return HashMap
     */
    @SuppressWarnings("unchecked")
    protected ArrayList<FolderType> getSelectedFolders() {
        mainC.eraseMessage();

        ArrayList<FolderType> folderList = new ArrayList<>();
        if (mainC.getBaseListboxController() instanceof ProcessListboxController) {
            Set<Object> selectedItem = (Set<Object>) mainC.getBaseListboxController().getListModel().getSelection();
            for (Object obj : selectedItem) {
                if (obj instanceof FolderType) {
                    folderList.add((FolderType) obj);
                }
            }
        }
        return folderList;
    }

    /**
     * Edit meta data of selected process versions:
     * - Process name (will be propagated to all versions of the process)
     * - Version name
     * - Domain (will be propagated to all versions of the process)
     * - Ranking
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     * @throws org.apromore.portal.exception.ExceptionAllUsers
     * @throws org.apromore.portal.exception.ExceptionDomains
     */
    protected void editData() throws SuspendNotAllowedException, InterruptedException, ExceptionDomains, ExceptionAllUsers, ParseException {
        mainC.eraseMessage();
        Map<SummaryType, List<VersionSummaryType>> selectedElements = mainC.getSelectedElementsAndVersions();

        if (selectedElements.size() != 0) {
            boolean all_processes = true;
            boolean all_logs = true;
            for(SummaryType summaryType : selectedElements.keySet()) {
                if(summaryType instanceof LogSummaryType) all_processes = false;
                if(summaryType instanceof ProcessSummaryType) all_logs = false;
            }
            if(all_logs) {
                new EditListLogDataController(mainC, selectedElements);
            }else if(all_processes) {
                new EditListProcessDataController(mainC, selectedElements);
            }else {
                mainC.displayMessage("Select only processes or logs.");
            }
        } else {
            mainC.displayMessage("No process version selected.");
        }
    }

    public Menubar getMenuB() {
        return menuB;
    }

}
