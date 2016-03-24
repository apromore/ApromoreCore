/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package org.apromore.portal.dialogController;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.common.TabListitem;
import org.apromore.portal.common.TabQuery;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.dto.VersionDetailType;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuController extends Menubar {

    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getCanonicalName());

    private final MainController mainC;
    private Menubar menuB;
    private PortalContext portalContext;

    public MenuController(final MainController mainController) throws ExceptionFormats {
        this.mainC = mainController;
        this.portalContext = new PluginPortalContext(mainC);
        this.menuB = (Menubar) this.mainC.getFellow("menucomp").getFellow("operationMenu");


        for (final PortalPlugin plugin: PortalPluginResolver.resolve()) {
            Menuitem menuitem = new Menuitem();
            menuitem.setLabel(plugin.getLabel(Locale.getDefault()));
            menuB.getChildren().add(menuitem);
            menuitem.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    plugin.execute(new PluginPortalContext(mainC));
                }
            });
        }

        Menuitem createMI = (Menuitem) this.menuB.getFellow("createProcess");
        Menuitem importMI = (Menuitem) this.menuB.getFellow("fileImport");
        Menuitem exportMI = (Menuitem) this.menuB.getFellow("fileExport");
        Menuitem editModelMI = (Menuitem) this.menuB.getFellow("processEdit");
        Menuitem editDataMI = (Menuitem) this.menuB.getFellow("dataEdit");
        Menuitem deleteMI = (Menuitem) this.menuB.getFellow("processDelete");
        Menuitem deployMI = (Menuitem) this.menuB.getFellow("processDeploy");
        Menuitem copyMI = (Menuitem) this.menuB.getFellow("processCopy");
        copyMI.setDisabled(true);
        Menuitem pasteMI = (Menuitem) this.menuB.getFellow("processPaste");
        pasteMI.setDisabled(true);
        Menuitem moveMI = (Menuitem) this.menuB.getFellow("processMove");
        moveMI.setDisabled(true);
        Menuitem query = (Menuitem) this.menuB.getFellow("queryPQL");

        Menu filteringM = (Menu) this.menuB.getFellow("filtering");
        Menuitem similaritySearchMI = (Menuitem) this.menuB.getFellow("similaritySearch");
        Menuitem similarityClustersMI = (Menuitem) this.menuB.getFellow("similarityClusters");
        Menuitem compareMI = (Menuitem) this.menuB.getFellow("compare");
        //Menuitem exactMatchingMI = (Menuitem) this.menuB.getFellow("exactMatching");
        //exactMatchingMI.setDisabled(true);

        //proDrift
        Menuitem proDriftMI = (Menuitem) this.menuB.getFellow("ProDriftDetection");

        Menu designM = (Menu) this.menuB.getFellow("design");
        Menuitem mergeMI = (Menuitem) this.menuB.getFellow("designMerging");
        Menuitem cmapMI = (Menuitem) this.menuB.getFellow("designCmap");
        Menuitem configureMI = (Menuitem) this.menuB.getFellow("designConfiguration");
        Menuitem bpmnMinerMI = (Menuitem) this.menuB.getFellow("miningBPMNMiner");

        Menuitem structMI = (Menuitem) this.menuB.getFellow("structProcess");
        structMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                structureProcess();
            }
        });

        bpmnMinerMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                mineBPMNMinerModel();
            }
        });
        createMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                createModel();
            }
        });
        importMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                importModel();
            }
        });
        editModelMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                editNative();
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
                exportNative();
            }
        });
        deleteMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                deleteSelectedProcessVersions();
            }
        });
        similaritySearchMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                searchSimilarProcesses();
            }
        });
        similarityClustersMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                clusterSimilarProcesses();
            }
        });
        compareMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                compareSimilarProcesses();
            }
        });
        query.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                createQuery();
            }
        });
        mergeMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                mergeSelectedProcessVersions();
            }
        });
        cmapMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                cmapModel();
            }
        });
        configureMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                configureModel();
            }
        });
        deployMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                deployProcessModel();
            }
        });

        //proDrift
        proDriftMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                proDriftGetParameters();
            }
        });
    }

    /**
     * Structure a list of selected processes
     * @throws InterruptedException
     * @throws ParseException
     */
    protected void structureProcess() throws InterruptedException, ParseException  {
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        if( selectedProcessVersions.size() == 1 ) {
            new StructureProcessController(this.mainC, selectedProcessVersions);
        } else {
            Messagebox.show("Select exactly one process.", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    /**
     * Mine a BPMN model using BPMNMiner
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     */
    protected void mineBPMNMinerModel() throws SuspendNotAllowedException, InterruptedException {
//        this.mainC.eraseMessage();
        new BPMNMinerController(this.mainC);
    }

    /**
     * Deploy process mdel to a running process engine
     * @throws InterruptedException
     * @throws WrongValueException
     */
    protected void deployProcessModel() throws WrongValueException, InterruptedException, ParseException {
        this.mainC.eraseMessage();
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        if (selectedProcessVersions.size() == 1) {
            new DeployProcessModelController(this.mainC, selectedProcessVersions.entrySet().iterator().next());
        } else {
            this.mainC.displayMessage("Please select exactly one process model!");
        }
    }


    /**
     * Search for similar processes to the one currently selected
     * @throws SuspendNotAllowedException
     * @throws InterruptedException
     */
    protected void searchSimilarProcesses() throws SuspendNotAllowedException, InterruptedException, ParseException, DialogException {
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        this.mainC.eraseMessage();

        int countSelected=0;
        TabQuery tabQuery = null;
        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){
                tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                TabListitem tabItem=null;
                for(Listitem item : items){
                    if(item.isSelected()){
                        countSelected++;
                        tabItem=(TabListitem) item;
                    }
                }

                if(countSelected==1){
                    new SimilaritySearchController(this.mainC, this, tabItem.getProcessSummaryType(), tabItem.getVersionSummaryType().get(0));
                    return;
                }
                break;
            }
        }


        if (selectedProcessVersions.size() == 1 && selectedProcessVersions.get(selectedProcessVersions.keySet().iterator().next()).size() == 1) {
            ProcessSummaryType process = selectedProcessVersions.keySet().iterator().next();
            VersionSummaryType version = selectedProcessVersions.get(selectedProcessVersions.keySet().iterator().next()).get(0);
            new SimilaritySearchController(this.mainC, this, process, version);
        } else if (selectedProcessVersions.size() == 0) {
            this.mainC.displayMessage("No process version selected, should be exactly one.");
        } else {
            this.mainC.displayMessage("Too many versions selected (should be exactly one).");
        }

    }


    /**
     * Cluster similar processes in the whole repository
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     */
    protected void clusterSimilarProcesses() throws SuspendNotAllowedException, InterruptedException {
        this.mainC.eraseMessage();
        new SimilarityClustersController(this.mainC);
    }

    /**
     * Compare two similar processes.
     *
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     */
    protected void compareSimilarProcesses() throws SuspendNotAllowedException, InterruptedException, ParseException, DialogException {
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        this.mainC.eraseMessage();

        // Populate "details" with the process:version selections
        List<VersionDetailType> details = new ArrayList<>();
        for (ProcessSummaryType processSummary: selectedProcessVersions.keySet()) {
            List<VersionSummaryType> versionSummaries = selectedProcessVersions.get(processSummary);
            if (versionSummaries.isEmpty()) {
                List<VersionSummaryType> x = processSummary.getVersionSummaries();
                versionSummaries.add(x.get(x.size() - 1));  // default to the head version
            }
            for (VersionSummaryType versionSummary: versionSummaries) {
                details.add(new VersionDetailType(processSummary, versionSummary));
            }
        }

        // If we have exactly two process:version selections, perform the comparison
        switch (details.size()) {
        case 0:
        case 1:
            this.mainC.displayMessage("Must select 2 process versions to compare.");
            break;
        case 2:
            this.mainC.displayMessage("Performing comparison.");
            new CompareController(this.mainC, this, details.get(0).getProcess(), details.get(0).getVersion(), details.get(1).getProcess(), details.get(1).getVersion());
            this.mainC.displayMessage("Performed comparison.");
            break;
        default:
            this.mainC.displayMessage("There are " + selectedProcessVersions.size() + " process versions selected, but only 2 can be compared at a time.");
        }
    }

    /**
     * Open a dialog prompting for a PQL query to execute.
     *
     * @throws InterruptedException
     * @throws DialogException
     */
    protected void createQuery() throws InterruptedException, DialogException {
        this.mainC.eraseMessage();
        new PQLFilterController(this.mainC);
    }

    protected void mergeSelectedProcessVersions() throws InterruptedException, ExceptionDomains, ParseException {
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        this.mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){

                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        TabListitem tabItem=(TabListitem)item;
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                    }
                }
                if(processVersion.keySet().size()<2) {
                    this.mainC.displayMessage("Select at least 2 process models for merge.");
                    return;
                }else {
                    try {
                        new ProcessMergeController(this.mainC, processVersion);
                    } catch (SuspendNotAllowedException e) {
                        Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                    } catch (ExceptionAllUsers e) {
                        String message;
                        if (e.getMessage() == null) {
                            message = "Couldn't retrieve users reference list.";
                        } else {
                            message = e.getMessage();
                        }
                        Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                    return;
                }
            }
        }

        Iterator<List<VersionSummaryType>> selectedVersions = selectedProcessVersions.values().iterator();
        // At least 2 process versions must be selected. Not necessarily of different processes
        if (selectedProcessVersions.size() == 1 && selectedVersions.next().size() > 1 || selectedProcessVersions.size() > 1) {
            try {
                new ProcessMergeController(this.mainC, selectedProcessVersions);
            } catch (SuspendNotAllowedException e) {
                Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
            } catch (ExceptionAllUsers e) {
                String message;
                if (e.getMessage() == null) {
                    message = "Couldn't retrieve users reference list.";
                } else {
                    message = e.getMessage();
                }
                Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        } else {
            this.mainC.displayMessage("Select at least 2 process models for merge.");
        }
    }

    //proDrift
    protected void proDriftGetParameters() {
            try {
                new ProDriftController(this.mainC);
            } catch (SuspendNotAllowedException e) {
                Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
            }
    }

    protected void cmapModel() throws ParseException {

        this.mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());
        int count=0;
        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){

                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                TabListitem tabItem=null;
                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        count++;
                        tabItem=(TabListitem)item;
                    }
                }
                if(count==1){
                    try {
                        HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                        new CmapController(this.mainC, processVersion);
                    } catch (ConfigureException e) {
                        Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                    } catch (RuntimeException e) {
                        Messagebox.show("Unable to cmap model: " + e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                }else{
                    this.mainC.displayMessage("Select only 1 process model to cmap.");
                }
            }
        }
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        if (selectedProcessVersions.size() == 1) {
            try {
                new CmapController(this.mainC, selectedProcessVersions);
            } catch (ConfigureException e) {
                Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                LOGGER.log(Level.WARNING, "Unable to cmap model", e);
            } catch (RuntimeException e) {
                Messagebox.show("Unable to cmap model: " + e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                LOGGER.log(Level.WARNING, "Unable to cmap model", e);
            }
        } else {
            this.mainC.displayMessage("Select only 1 process model to cmap.");
        }
    }

    protected void configureModel() throws ParseException {
        this.mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());
        int count=0;
        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){

                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                TabListitem tabItem=null;
                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        count++;
                        tabItem=(TabListitem)item;
                    }
                }
                if(count==1){
                    try {
                        HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                        new CmapController(this.mainC, processVersion);
                    } catch (ConfigureException e) {
                        Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                    } catch (RuntimeException e) {
                        Messagebox.show("Unable to configure model: " + e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                }else{
                    this.mainC.displayMessage("Select only 1 process model to cmap.");
                }
            }
        }



        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();

        if (selectedProcessVersions.size() == 1) {
            try {
                new ConfigureController(this.mainC, selectedProcessVersions);
            } catch (ConfigureException e) {
                Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                LOGGER.log(Level.WARNING, "Unable to configure model", e);
            } catch (RuntimeException e) {
                Messagebox.show("Unable to configure model: " + e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                LOGGER.log(Level.WARNING, "Unable to configure model", e);
            }
        } else {
            this.mainC.displayMessage("Select only 1 process model to configure.");
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
                        HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                        new EditListProcessesController(this.mainC,this,processVersion);
                        return;
                    }
                }
            }
        }
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        if (selectedProcessVersions.size() != 0) {
            new EditListProcessesController(this.mainC, this, selectedProcessVersions);
        } else {
            this.mainC.displayMessage("No process version selected.");
        }
    }


    /**
     * Delete all selected process versions.
     * @throws Exception
     */
    protected void deleteSelectedProcessVersions() throws Exception {
        this.mainC.eraseMessage();
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        if (selectedProcessVersions.size() != 0) {
            this.mainC.deleteProcessVersions(selectedProcessVersions);
            mainC.clearProcessVersions();
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
    protected void exportNative() throws SuspendNotAllowedException, InterruptedException, ExceptionFormats, ParseException {
        this.mainC.eraseMessage();

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){
                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
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

        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();
        if (selectedProcessVersions.size() != 0) {
            new ExportListNativeController(this.mainC, this, selectedProcessVersions);
        } else {
            this.mainC.displayMessage("No process version selected.");
        }
    }

    protected void importModel() throws InterruptedException {
        this.mainC.eraseMessage();
        try {
            new ImportListProcessesController(mainC);
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
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedProcessVersions();

        if (selectedProcessVersions.size() != 0) {
            new EditListProcessDataController(mainC, selectedProcessVersions);
        } else {
            mainC.displayMessage("No process version selected.");
        }
    }

    public Menubar getMenuB() {
        return menuB;
    }

}
