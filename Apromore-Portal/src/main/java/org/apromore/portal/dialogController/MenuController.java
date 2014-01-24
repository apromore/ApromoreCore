package org.apromore.portal.dialogController;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
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
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MenuController extends Menubar {

    private final MainController mainC;
    private Menubar menuB;

    public MenuController(final MainController mainController) throws ExceptionFormats {
        this.mainC = mainController;
        this.menuB = (Menubar) this.mainC.getFellow("menucomp").getFellow("operationMenu");

        Menuitem createMI = (Menuitem) this.menuB.getFellow("createProcess");
        Menuitem importMI = (Menuitem) this.menuB.getFellow("fileImport");
        Menuitem exportMI = (Menuitem) this.menuB.getFellow("fileExport");
        Menuitem editModelMI = (Menuitem) this.menuB.getFellow("processEdit");
        Menuitem editDataMI = (Menuitem) this.menuB.getFellow("dataEdit");
        Menuitem deleteMI = (Menuitem) this.menuB.getFellow("processDelete");
        Menuitem copyMI = (Menuitem) this.menuB.getFellow("processCopy");
        Menuitem pasteMI = (Menuitem) this.menuB.getFellow("processPaste");
        Menuitem deployMI = (Menuitem) this.menuB.getFellow("processDeploy");

        Menu filteringM = (Menu) this.menuB.getFellow("filtering");
        Menuitem similaritySearchMI = (Menuitem) this.menuB.getFellow("similaritySearch");
        Menuitem similarityClustersMI = (Menuitem) this.menuB.getFellow("similarityClusters");
        Menuitem exactMatchingMI = (Menuitem) this.menuB.getFellow("exactMatching");
        exactMatchingMI.setDisabled(true);

        Menu designM = (Menu) this.menuB.getFellow("design");
        Menuitem mergeMI = (Menuitem) this.menuB.getFellow("designMerging");
        Menuitem configureMI = (Menuitem) this.menuB.getFellow("designConfiguration");

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
        mergeMI.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                mergeSelectedProcessVersions();
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
    }

    /**
     * Deploy process mdel to a running process engine
     * @throws InterruptedException
     * @throws WrongValueException
     */
    protected void deployProcessModel() throws WrongValueException, InterruptedException, ParseException {
        this.mainC.eraseMessage();
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();
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
    protected void searchSimilarProcesses() throws SuspendNotAllowedException, InterruptedException, ParseException {
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();
        this.mainC.eraseMessage();
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

    protected void mergeSelectedProcessVersions() throws InterruptedException, ExceptionDomains, ParseException {
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();
        this.mainC.eraseMessage();

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

    protected void configureModel() throws ParseException {
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();
        this.mainC.eraseMessage();
        if (selectedProcessVersions.size() == 1) {
            try {
                new ConfigureController(this.mainC, selectedProcessVersions);
            } catch (ConfigureException e) {
                Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
            } catch (RuntimeException e) {
                Messagebox.show("Unable to configure model: " + e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
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
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();
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
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();
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
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();
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
    protected HashMap<ProcessSummaryType, List<VersionSummaryType>> getSelectedProcessVersions() throws ParseException {
        HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersions = new HashMap<>();
        String versionNumber;
        mainC.eraseMessage();

        if (mainC.getBaseListboxController() instanceof ProcessListboxController) {
            ArrayList<VersionSummaryType> versionList;

            VersionDetailType selectedVersion = ((ProcessVersionDetailController) mainC.getDetailListbox()).getSelectedVersion();
            Set<Object> selectedProcesses = (Set<Object>) mainC.getBaseListboxController().getListModel().getSelection();
            for (Object obj : selectedProcesses) {
                if (obj instanceof ProcessSummaryType) {
                    versionList = new ArrayList<>();
                    if (selectedVersion != null) {
                        versionList.add(selectedVersion.getVersion());
                    } else {
                        for (VersionSummaryType summaryType : ((ProcessSummaryType) obj).getVersionSummaries()) {
                            versionNumber = ((ProcessSummaryType) obj).getLastVersion();
                            if (summaryType.getVersionNumber().compareTo(versionNumber) == 0) {
                                versionList.add(summaryType);
                            }
                        }
                    }
                    processVersions.put((ProcessSummaryType) obj, versionList);
                }
            }
        }
        return processVersions;
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
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();

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
