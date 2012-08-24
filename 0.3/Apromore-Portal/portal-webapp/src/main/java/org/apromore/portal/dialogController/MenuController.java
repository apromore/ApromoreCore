package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;

public class MenuController extends Menubar {

    private MainController mainC;
    private Menubar menuB;

    private Menuitem mergeMI;

    private Menuitem evalQualityMI;
    private Menuitem evalCorrectnessMI;
    private Menuitem evalPerformanceMI;

    public MenuController(MainController mainController) throws ExceptionFormats {
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

        Menu evaluationM = (Menu) this.menuB.getFellow("evaluation");

        Menu filteringM = (Menu) this.menuB.getFellow("filtering");
        Menuitem similaritySearchMI = (Menuitem) this.menuB.getFellow("similaritySearch");
        Menuitem similarityClustersMI = (Menuitem) this.menuB.getFellow("similarityClusters");
        Menuitem exactMatchingMI = (Menuitem) this.menuB.getFellow("exactMatching");

        Menu designM = (Menu) this.menuB.getFellow("design");
        this.mergeMI = (Menuitem) this.menuB.getFellow("designMerging");
        Menu presentationM = (Menu) this.menuB.getFellow("presentation");

        createMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                createModel();
            }
        });
        importMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                importModel();
            }
        });
        editModelMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                editNative();
            }
        });
        editDataMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                editData();
            }
        });
        exportMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                exportNative();
            }
        });
        deleteMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                deleteSelectedProcessVersions();
            }
        });
        similaritySearchMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                searchSimilarProcesses();
            }
        });
        similarityClustersMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                clusterSimilarProcesses();
            }
        });
        this.mergeMI.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                mergeSelectedProcessVersions();
            }
        });
    }


    /**
     * Search for similar processes to the one currently selected
     * @throws SuspendNotAllowedException
     * @throws InterruptedException
     */
    protected void searchSimilarProcesses() throws SuspendNotAllowedException, InterruptedException {
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

    protected void mergeSelectedProcessVersions() throws InterruptedException, ExceptionDomains {
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();
        this.mainC.eraseMessage();

        Iterator<List<VersionSummaryType>> selectedVersions = selectedProcessVersions.values().iterator();
        // At least 2 process versions must be selected. Not necessarily of different processes
        if (selectedProcessVersions.size() == 1 && selectedVersions.next().size() > 1 || selectedProcessVersions.size() > 1) {
            try {
                new ProcessMergeController(this.mainC, this, selectedProcessVersions);
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
    protected void editNative() throws InterruptedException, SuspendNotAllowedException, ExceptionFormats {
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
    protected void exportNative() throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
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
            new ImportListProcessesController(this, mainC);
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
    private HashMap<ProcessSummaryType, List<VersionSummaryType>> getSelectedProcessVersions() {
        this.mainC.eraseMessage();

        if (this.mainC.getBaseListboxController() instanceof ProcessListboxController) {
            Set selectedProcesses = this.mainC.getBaseListboxController().getListModel().getSelection();

            HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersions = new HashMap<>();

            ProcessVersionDetailController detailController = ((ProcessVersionDetailController) this.mainC.getDetailListbox());
            VersionSummaryType selectedVersion = detailController.getSelectedVersion();

            for (Object obj : selectedProcesses) {
                ProcessSummaryType selectedProcess = (ProcessSummaryType) obj;
                ArrayList<VersionSummaryType> versionList = new ArrayList<>();
                if (selectedVersion != null) {
                    versionList.add(selectedVersion);
                } else {
                    for (VersionSummaryType summaryType : selectedProcess.getVersionSummaries()) {
                        if (summaryType.getName().equals(selectedProcess.getLastVersion())) {
                            versionList.add(summaryType);
                        }
                    }
                }
                processVersions.put(selectedProcess, versionList);
            }

            return processVersions;
        } else {
            return new HashMap<>();
        }

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
    private void editData() throws SuspendNotAllowedException, InterruptedException, ExceptionDomains, ExceptionAllUsers {
        this.mainC.eraseMessage();
        HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = getSelectedProcessVersions();

        if (selectedProcessVersions.size() != 0) {
            new EditListProcessDataController(this.mainC, this, selectedProcessVersions);
        } else {
            this.mainC.displayMessage("No process version selected.");
        }
    }

    public Menubar getMenuB() {
        return menuB;
    }

    public void setMenuB(Menubar menuB) {
        this.menuB = menuB;
    }

    public Menuitem getMergeMI() {
        return mergeMI;
    }

}
