package org.apromore.portal.dialogController;

import javax.xml.bind.JAXBException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.ProcessIdColComparator;
import org.apromore.portal.common.ProcessNameColComparator;
import org.apromore.portal.common.ProcessRankingColComparator;
import org.apromore.portal.common.VersionNameColComparator;
import org.apromore.portal.common.VersionRankingColComparator;
import org.apromore.portal.exception.ExceptionDao;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Toolbarbutton;

public class ProcessTableController extends BaseController {

    private MainController mainC;                             // the main controller
//    private AddFolderController addFolderController;
//    private CreateProcessController createController;
//    private SecuritySetupController securitySetupController;


    private Grid processSummariesGrid;                         // the grid for process summaries
    private Rows processSummariesRows;                         // the rows for process summaries
    //	private Window processTableW;							// the window which the entry point
    private HashMap<Checkbox, ProcessSummaryType> processHM;    // HashMap of checkboxes: one entry for each process
    private HashMap<Checkbox, VersionSummaryType> processVersionsHM;// HashMap of checkboxes: one entry for each process version
    private HashMap<Checkbox, List<Checkbox>> mapProcessVersions; // <p, listV> in mapProcessVersions: checkboxes in listV are
    // associated with checkbox p
    private Integer latestVersionPos;                        // position of label latest version in row of process summary
    private Integer processTbPos;                            // position of toolbarbuttons associated with process names in rows of process summary
    private Integer processSummaryCBPos;                    // position of checkbox associated with process summary

    private Hbox pagingAndButtons;                            // hbox which contains paging ang button components
    private Hbox buttons;
    private Paging pg;
    private Button revertSelectionB;                        // button which reverts the process selections
    private Button selectAllB;
    private Button unselectAllB;
    private Button refreshB;
//    private Button btnAddFolder;
//    private Button btnAddProcess;
//    private Button btnRenameFolder;
//    private Button btnRemoveFolder;
//    private Button btnSecurity;

    private Column columnScore;                                // column to display process score
    // for the purpose of answering query
    private Column columnName;                                // column to display process name
    private Column columnRanking;                             // column to display process ranking
    private Column columnId;                                 // column to display process Id

    private Boolean isQueryResult;                            // says whether the data to be displayed have been produced by a query
    private ProcessSummaryType processQ;
    private VersionSummaryType versionQ;

    public ProcessTableController(MainController mainController) throws Exception {
        /**
         * get components of the process version table part
         */
        this.mainC = mainController;
        //this.processTableW = (Window) this.mainC.getFellow("processtablecomp").getFellow("processTableWindow");
        this.processSummariesGrid = (Grid) this.mainC.getFellow("processSummariesGrid");
        this.processSummariesRows = (Rows) this.processSummariesGrid.getFellow("processSummariesRows");
        this.pagingAndButtons = (Hbox) this.processSummariesGrid.getFellow("pagingandbuttons");
        this.pg = (Paging) this.processSummariesGrid.getFellow("pg");
        this.buttons = (Hbox) this.processSummariesGrid.getFellow("buttons");
        this.revertSelectionB = (Button) this.processSummariesGrid.getFellow("revertSelectionB");
        this.unselectAllB = (Button) this.processSummariesGrid.getFellow("unselectAllB");
        this.selectAllB = (Button) this.processSummariesGrid.getFellow("selectAllB");
        this.refreshB = (Button) this.processSummariesGrid.getFellow("refreshB");
//        this.btnAddFolder = (Button) this.processSummariesGrid.getFellow("btnAddFolder");
//        this.btnAddProcess = (Button) this.processSummariesGrid.getFellow("btnAddProcess");
//        this.btnRenameFolder = (Button) this.processSummariesGrid.getFellow("btnRenameFolder");
//        this.btnRemoveFolder = (Button) this.processSummariesGrid.getFellow("btnRemoveFolder");
//        this.btnSecurity = (Button) this.processSummariesGrid.getFellow("btnSecurity");

        this.columnName = (Column) this.processSummariesGrid.getFellow("columnName");
        this.columnRanking = (Column) this.processSummariesGrid.getFellow("columnRanking");
        this.columnId = (Column) this.processSummariesGrid.getFellow("columnId");
        this.columnScore = (Column) this.processSummariesGrid.getFellow("columnScore");

        // create comparators for rows according to items corresponding to
        // Name, Ranking and Id of a process (as these items are not of a comparable type)
        ProcessNameColComparator asc1 = new ProcessNameColComparator(true),
                dsc1 = new ProcessNameColComparator(false);
        this.columnName.setSortAscending(asc1);
        this.columnName.setSortDescending(dsc1);

        ProcessRankingColComparator asc2 = new ProcessRankingColComparator(true),
                dsc2 = new ProcessRankingColComparator(false);
        this.columnRanking.setSortAscending(asc2);
        this.columnRanking.setSortDescending(dsc2);

        ProcessIdColComparator asc3 = new ProcessIdColComparator(true),
                dsc3 = new ProcessIdColComparator(false);
        this.columnId.setSortAscending(asc3);
        this.columnId.setSortDescending(dsc3);

        // if change grid layouts modify value accordingly
        this.latestVersionPos = 9;
        this.processTbPos = 4;
        this.processSummaryCBPos = 1;

        // initialize hashmaps
        this.processHM = new HashMap<Checkbox, ProcessSummaryType>();
        this.processVersionsHM = new HashMap<Checkbox, VersionSummaryType>();
        this.mapProcessVersions = new HashMap<Checkbox, List<Checkbox>>();

        this.revertSelectionB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        revertSelection();
                    }
                });

        this.selectAllB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        selectAll();
                    }
                });

        this.unselectAllB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        unselectAll();
                    }
                });

        this.refreshB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        reloadData();
                    }
                });

        this.processSummariesGrid.addEventListener("onSort",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        refresh();
                    }
                });

//        this.btnAddFolder.addEventListener("onClick", new EventListener() {
//            public void onEvent(Event event) throws Exception {
//                addFolder();
//            }
//        });
//
//        this.btnAddProcess.addEventListener("onClick", new EventListener() {
//            public void onEvent(Event event) throws Exception {
//                addProcess();
//            }
//        });
//
//        this.btnRenameFolder.addEventListener("onClick", new EventListener() {
//            public void onEvent(Event event) throws Exception {
//                renameFolder();
//            }
//        });
//
//        this.btnRemoveFolder.addEventListener("onClick", new EventListener() {
//            public void onEvent(Event event) throws Exception {
//                removeFolder();
//            }
//        });
//
//        this.btnSecurity.addEventListener("onClick", new EventListener() {
//            public void onEvent(Event event) throws Exception {
//                security();
//            }
//        });

        /**
         * At creation of the controller, get summaries of all processes.
         * for each process: a row in the main grid with detail (grid inside)
         * no keywords given
         */
        ProcessSummariesType processSummaries = getService().readProcessSummaries("");
        this.mainC.displayMessage(processSummaries.getProcessSummary().size() + " processes.");
        displayProcessSummaries(processSummaries, false, null, null);
    }

    protected void reloadData() throws Exception {
        this.mainC.reloadProcessSummaries();
    }


    protected void unselectAll() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException {
        List<Row> processSummariesRs = this.processSummariesRows.getChildren();
        for (int i = 0; i < processSummariesRs.size(); i++) {
            Row processSummaryR = processSummariesRs.get(i);
            Checkbox processSummaryCB = (Checkbox) processSummaryR.getChildren().get(this.processSummaryCBPos);
            if (processSummaryCB.isChecked()) {
                reverseProcessSelection(i);
            }
        }
    }

    protected void selectAll() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException {
        List<Row> processSummariesRs = this.processSummariesRows.getChildren();
        for (int i = 0; i < processSummariesRs.size(); i++) {
            Row processSummaryR = processSummariesRs.get(i);
            Checkbox processSummaryCB = (Checkbox) processSummaryR.getChildren().get(this.processSummaryCBPos);
            if (!processSummaryCB.isChecked()) {
                reverseProcessSelection(i);
            }
        }
    }

//    protected void addFolder() throws InterruptedException {
//        this.mainC.eraseMessage();
//        try {
//            this.addFolderController = new AddFolderController(mainC, 0, "");
//        } catch (DialogException e) {
//            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
//        }
//    }
//
//    protected void addProcess() throws InterruptedException {
//        this.mainC.eraseMessage();
//        try {
//            this.createController = new CreateProcessController(this.mainC, this.mainC.getNativeTypes());
//        } catch (SuspendNotAllowedException e) {
//            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
//        } catch (InterruptedException e) {
//            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
//        } catch (ExceptionDomains e) {
//            String message;
//            if (e.getMessage() == null) {
//                message = "Couldn't retrieve domains reference list.";
//            } else {
//                message = e.getMessage();
//            }
//            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
//        } catch (ExceptionFormats e) {
//            String message;
//            if (e.getMessage() == null) {
//                message = "Couldn't retrieve formats reference list.";
//            } else {
//                message = e.getMessage();
//            }
//            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
//        } catch (ExceptionAllUsers e) {
//            String message;
//            if (e.getMessage() == null) {
//                message = "Couldn't retrieve users reference list.";
//            } else {
//                message = e.getMessage();
//            }
//            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
//        }
//    }
//
//    protected void renameFolder() throws InterruptedException {
//        this.mainC.eraseMessage();
//        try {
//            List<Integer> folderIds = UserSessionManager.getSelectedFolderIds();
//
//            if (folderIds.size() == 1) {
//                int selectedFolderId = folderIds.get(0);
//                String selectedFolderName = "";
//                List<FolderType> availableFolders = UserSessionManager.getCurrentFolder() == null || UserSessionManager.getCurrentFolder().getId() == 0 ? UserSessionManager.getTree() : UserSessionManager.getCurrentFolder().getFolders();
//                for (FolderType folder : availableFolders) {
//                    if (folder.getId() == selectedFolderId) {
//                        selectedFolderName = folder.getFolderName();
//                        break;
//                    }
//                }
//
//                this.addFolderController = new AddFolderController(mainC, folderIds.get(0), selectedFolderName);
//            } else if (folderIds.size() > 1) {
//                Messagebox.show("Only one item can be renamed at the time.", "Attention", Messagebox.OK, Messagebox.ERROR);
//            } else {
//                Messagebox.show("Please select item to rename", "Attention", Messagebox.OK, Messagebox.ERROR);
//            }
//        } catch (DialogException e) {
//            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
//        }
//    }
//
//    protected void removeFolder() throws Exception {
//        Messagebox.show("Are you sure you want to delete selected item(s)?", "Prompt", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
//            public void onEvent(Event evt) throws Exception {
//                switch (((Integer) evt.getData())) {
//                    case Messagebox.YES:
//                        MainController mainController = UserSessionManager.getMainController();
//                        List<Integer> folderIds = UserSessionManager.getSelectedFolderIds();
//                        for (int folderId : folderIds) {
//                            mainController.getService().deleteFolder(folderId);
//                        }
//                        mainController.getMenu().deleteSelectedProcessVersions();
//                        mainController.loadWorkspace();
//                        break;
//                    case Messagebox.NO:
//                        break;
//                }
//            }
//        });
//    }
//
//    protected void security() throws InterruptedException {
//        this.mainC.eraseMessage();
//        try {
//            this.securitySetupController = new SecuritySetupController(this.mainC);
//        } catch (DialogException e) {
//            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
//        }
//    }

    public void emptyProcessSummaries() {
        this.processHM.clear();
        this.processVersionsHM.clear();
        this.mapProcessVersions.clear();

        while (this.processSummariesRows.getChildren().size() > 0) {
            this.processSummariesRows.removeChild(this.processSummariesRows.getFirstChild());
        }
    }

    // when refreshing the table, the associated paging is deleted
    /* recreate paging associated with the table
      */
    public void newPaging() {
        Hbox hbox = this.pagingAndButtons;
        while (hbox.getChildren().size() > 0) {
            hbox.removeChild(hbox.getFirstChild());
        }
        Paging newPg = new Paging();
        this.pg = newPg;
        this.processSummariesGrid.setPaginal(newPg);
        this.pagingAndButtons.appendChild(newPg);
        this.pagingAndButtons.appendChild(this.buttons);

    }

    /**
     * Display process versions given in processSummaries. If isQueryResult this results from
     * a search whose query is versionQ, given processQ
     *
     * @param processSummaries
     * @param isQueryResult
     * @param processQ
     * @param versionQ
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public void displayProcessSummaries(ProcessSummariesType processSummaries,
                                        Boolean isQueryResult,
                                        ProcessSummaryType processQ, VersionSummaryType versionQ)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.isQueryResult = isQueryResult;
        this.processQ = processQ;
        this.versionQ = versionQ;
        this.columnScore.setVisible(isQueryResult);
        for (int i = 0; i < processSummaries.getProcessSummary().size(); i++) {
            ProcessSummaryType process = processSummaries.getProcessSummary().get(i);
            displayOneProcess(process);
        }
    }

    public void displayOneProcess(ProcessSummaryType process) {
        // one row for each process
        Row processSummaryR = new Row();
        Detail processSummaryD = new Detail();
        String processIdS = process.getId().toString();
        processSummaryD.setId(processIdS);
        processSummaryD.setOpen(false);
        this.processSummariesRows.appendChild(processSummaryR);
        /**
         * assign process summary values to labels
         */
        Checkbox processCB = new Checkbox();

        // update hashmaps
        this.processHM.put(processCB, process);
        List<Checkbox> listV = new ArrayList<Checkbox>();
        this.mapProcessVersions.put(processCB, listV);
        Label processScoreLb = new Label();
        List<VersionSummaryType> processVersions = process.getVersionSummaries();
        // find the score of the latest version, if any: this a one which will be displayed
        // with the process
        int i = 0;
        while (i < processVersions.size()
                && processVersions.get(i).getName() != null
                && processVersions.get(i).getName().compareTo(process.getLastVersion()) != 0) {
            i++;
        }
        // Each process should have at least one version. So it should have a legal value which
        // is the index of the process latest version.
        // But some are faulty!!!
        if (i < processVersions.size() && processVersions.get(i).getScore() != null) {
            processScoreLb.setValue(processVersions.get(i).getScore().toString());
        } else {
            processScoreLb.setValue("1.0");
        }
        Label processIdLb = new Label(process.getId().toString());
        Component processName = null;
        processName = new Toolbarbutton(process.getName());
        ((Toolbarbutton) processName).setStyle(Constants.TOOLBARBUTTON_STYLE);
        Label processOriginalLanguage = new Label(process.getOriginalNativeType());
        Label processDomain = new Label(process.getDomain());
        Hbox processRankingHB = new Hbox();
        if (process.getRanking() != null && process.getRanking().toString().compareTo("") != 0) {
            displayRanking(processRankingHB, process.getRanking());
        }
        Label processRankingValue = new Label(process.getRanking());
        Label processLatestVersion = new Label(process.getLastVersion());
        Label processOwner = new Label(process.getOwner());
        processSummaryR.appendChild(processSummaryD);
        processSummaryR.appendChild(processCB);
        processSummaryR.appendChild(processScoreLb);
        processSummaryR.appendChild(processIdLb);
        processSummaryR.appendChild(processName);
        processSummaryR.appendChild(processOriginalLanguage);
        processSummaryR.appendChild(processDomain);
        processSummaryR.appendChild(processRankingHB);
        processSummaryR.appendChild(processRankingValue);
        processSummaryR.appendChild(processLatestVersion);
        processSummaryR.appendChild(processOwner);
        highlightP(processSummaryR, false);
        // click on process name to select it
        if (processName.getClass().getName().equals("org.zkoss.zul.Toolbarbutton")) {
            processName.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    maintainSelectedProcesses(event);
                }
            });
        }
        // click on "+" to get process details
        processSummaryD.addEventListener("onOpen", new EventListener() {
            public void onEvent(Event event) throws Exception {
                Detail processSummaryD = (Detail) event.getTarget();
                displayVersionsSummaries(processSummaryD);
            }
        });
    }

    /**
     * Build grid to display version details of the corresponding process
     * If process selected, highlight latest version
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     */
    protected void displayVersionsSummaries(Detail processSummaryD)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException {
        /* details might have been already build, in this case the Detail processSummaryD has at least
           * on children.
           */
        if (processSummaryD.getChildren().size() == 0) {
            Checkbox processCB = (Checkbox) processSummaryD.getNextSibling();
            ProcessSummaryType process = this.processHM.get(processCB);
            // the grid for process versions
            Grid processVersionG = new Grid();
            processVersionG.setVflex(true);
            Columns versionHeads = new Columns();
            versionHeads.setSizable(true);
            Column checkboxes = new Column();
            checkboxes.setWidth("0px");
            Column headScore = new Column("Score");
            headScore.setSort("auto");
            headScore.setVisible(this.isQueryResult);
            Column headVersionName = new Column("Version name");
            headVersionName.setSort("auto");
            Column headCreationDate = new Column("Creation date");
            headCreationDate.setSort("auto");
            Column headLastUpdate = new Column("Last update");
            headLastUpdate.setSort("auto");
            Column headAnnotation = new Column("Annotation(s)");
            Column headRanking = new Column("Ranking");
            headRanking.setSort("auto");

            // create comparators for rows according to items corresponding to
            // Name and Ranking of a version
            VersionNameColComparator asc1 = new VersionNameColComparator(true),
                    dsc1 = new VersionNameColComparator(false);
            headVersionName.setSortAscending(asc1);
            headVersionName.setSortDescending(dsc1);

            VersionRankingColComparator asc2 = new VersionRankingColComparator(true),
                    dsc2 = new VersionRankingColComparator(false);
            headRanking.setSortAscending(asc2);
            headRanking.setSortDescending(dsc2);

            headScore.setWidth("10%");
            headVersionName.setWidth("15%");
            headCreationDate.setWidth("25%");
            headLastUpdate.setWidth("25%");
            headAnnotation.setWidth("25%");
            headRanking.setWidth("15%");

            processVersionG.appendChild(versionHeads);
            versionHeads.appendChild(checkboxes);
            versionHeads.appendChild(headScore);
            versionHeads.appendChild(headVersionName);
            versionHeads.appendChild(headCreationDate);
            versionHeads.appendChild(headLastUpdate);
            versionHeads.appendChild(headAnnotation);
            versionHeads.appendChild(headRanking);

            processSummaryD.appendChild(processVersionG);
            Rows processVersionsR = new Rows();
            processVersionG.appendChild(processVersionsR);

            for (int j = 0; j < process.getVersionSummaries().size(); j++) {
                VersionSummaryType version = process.getVersionSummaries().get(j);
                /**
                 * for each version a row, with a checkbox identified by processId/versionName
                 */
                Row versionR = new Row();
                versionR.setStyle(Constants.UNSELECTED_VERSION);
                Checkbox versionCB = new Checkbox();
                this.processVersionsHM.put(versionCB, version);
                this.mapProcessVersions.get(processCB).add(versionCB);
                versionCB.setVisible(false);
                versionCB.setId(process.getId().toString() + "/" + version.getName());

                Label scoreL = new Label();
                if (version.getScore() != null) scoreL.setValue(version.getScore().toString());
                // as the version might be a query to be displayed, its name doesn't behave
                // as a toolbarbutton
                Component versionName = null;
                if (process.getId() < 0) {
                    versionName = new Label(version.getName());
                } else {
                    versionName = new Toolbarbutton(version.getName());
                    ((Toolbarbutton) versionName).setStyle(Constants.TOOLBARBUTTON_STYLE);
                }
                Label versionCreationDate = new Label();
                if (version.getCreationDate() != null) {
                    versionCreationDate.setValue(version.getCreationDate().toString());
                }
                Label versionLastUpdate = new Label();
                if (version.getLastUpdate() != null) {
                    versionLastUpdate.setValue(version.getLastUpdate().toString());
                }
                Hbox versionRanking = new Hbox();
                if (version.getRanking() != null && version.getRanking().toString().compareTo("") != 0) {
                    displayRanking(versionRanking, version.getRanking());
                }
                Label versionRankingValue = new Label(version.getRanking());

                // build drop down list of annotations: one line for each associated native type,
                // and for each of which the list of existing annotations
                Listbox annotationLB = new Listbox();
                annotationLB.setMold("select");
                annotationLB.setRows(1);
                annotationLB.setStyle(Constants.UNSELECTED_VERSION);
                for (int i = 0; i < version.getAnnotations().size(); i++) {
                    String language = version.getAnnotations().get(i).getNativeType();
                    for (int k = 0; k < version.getAnnotations().get(i).getAnnotationName().size(); k++) {
                        Listitem annotationsI = new Listitem();
                        annotationLB.appendChild(annotationsI);
                        String annotationName = version.getAnnotations().get(i).getAnnotationName().get(k);
                        annotationsI.setLabel(annotationName + " (" + language + ")");
                    }
                }
                processVersionsR.appendChild(versionR);
                versionR.appendChild(versionCB);
                versionR.appendChild(scoreL);
                versionR.appendChild(versionName);
                versionR.appendChild(versionCreationDate);
                versionR.appendChild(versionLastUpdate);
                versionR.appendChild(annotationLB);
                versionR.appendChild(versionRanking);
                versionR.appendChild(versionRankingValue);

                highlightV(versionR, false);
                /* the process might has been already selected, thus its latest version has to be marked as
                     * selected too.
                     */
                if (processCB.isChecked()) {
                    if (version.getName().compareTo(process.getLastVersion()) == 0) {
                        versionCB.setChecked(true);
                    }
                }
                /*
                     * click on version name
                     */
                if (versionName.getClass().getName().equals("org.zkoss.zul.Toolbarbutton")) {
                    versionName.addEventListener("onClick", new EventListener() {
                        public void onEvent(Event event) throws Exception {
                            revertProcessVersion(event);
                        }
                    });
                }
            }
        }
    }

    /**
     * Display in hbox versionRanking, 5 stars according to ranking (0...5).
     * Pre-condition: ranking is a non empty string.
     * TODO: allow users to rank a process version directly by interacting with
     * the stars displayed.
     *
     * @param ranking
     */
    private void displayRanking(Hbox rankingHb, String ranking) {
        String imgFull = Constants.STAR_FULL_ICON;
        String imgMid = Constants.STAR_MID_ICON;
        String imgBlank = Constants.STAR_BLK_ICON;
        Image star;
        Float rankingF = Float.parseFloat(ranking);
        int fullStars = rankingF.intValue();
        int i;
        for (i = 1; i <= fullStars; i++) {
            star = new Image();
            rankingHb.appendChild(star);
            star.setSrc(imgFull);
        }
        if (i <= 5) {
            if (Math.floor(rankingF) != rankingF) {
                star = new Image();
                star.setSrc(imgMid);
                rankingHb.appendChild(star);
                i = i + 1;
            }
            for (int j = i; j <= 5; j++) {
                star = new Image();
                star.setSrc(imgBlank);
                rankingHb.appendChild(star);
            }
        }
    }

    /**
     * remove from the table displayed the process versions processVersions
     *
     * @param processVersions
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public void unDisplay(Map<ProcessSummaryType, List<VersionSummaryType>> processVersions)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException {
        // Update the table
        Set<ProcessSummaryType> keySet = processVersions.keySet();
        Iterator<ProcessSummaryType> it = keySet.iterator();
        while (it.hasNext()) {
            ProcessSummaryType process = it.next();
            // versions contains all versions of the process (either selected or not)
            List<VersionSummaryType> versions = process.getVersionSummaries();
            // retrieve the process Detail in this.processSummariesRows
            Detail processD = (Detail) this.processSummariesRows.getFellow(process.getId().toString(), true);
            Checkbox processCB = (Checkbox) processD.getNextSibling();
            Row processR = (Row) processD.getParent();
            processCB.setChecked(false);
            highlightP(processR, false);
            Grid processG = (Grid) processD.getFirstChild();
            Rows versionsR = (Rows) processG.getFirstChild().getNextSibling();
            Iterator<VersionSummaryType> itV = versions.iterator();
            List<VersionSummaryType> toBeDeleted = new ArrayList<VersionSummaryType>();
            while (itV.hasNext()) {
                //remove from the Detail grid, the row corresponding to version, if checked:
                // its checkbox has id=processid+"/"+versionName
                VersionSummaryType version = itV.next();
                String idToCheck = process.getId().toString() + "/" + version.getName();
                Checkbox versionCB = (Checkbox) processD.getFellow(idToCheck, true);
                Row versionR = (Row) versionCB.getParent();
                if (versionCB.isChecked()) {
                    versionsR.removeChild(versionR);
                    toBeDeleted.add(version);
                    // Update the data structures
                    // processVersionHM, mapProcessVersions
                    this.processVersionsHM.remove(versionCB);
                    this.mapProcessVersions.get(processCB).remove(
                            (this.mapProcessVersions.get(processCB).indexOf(versionCB)));
                }
            }
            for (int i = 0; i < toBeDeleted.size(); i++) {
                versions.remove(toBeDeleted.get(i));
            }
            // if no row left, remove the process row
            if (versionsR.getChildren().size() == 0) {
                this.processSummariesRows.removeChild(processR);
                //Update the data structure processHM
                this.processHM.remove(processCB);
            } else {
                // update the label of the latest version
                Label latest = (Label) processR.getChildren().get(this.latestVersionPos);
                Row lastVersionR = (Row) versionsR.getLastChild();
                Checkbox lastVersionCB = (Checkbox) lastVersionR.getFirstChild();
                latest.setValue(this.processVersionsHM.get(lastVersionCB).getName());
            }
        }
        refresh();
    }

    /**
     * revert selection of version corresponding to event
     *
     * @throws InterruptedException
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws java.text.ParseException
     * @throws NumberFormatException
     */
    protected void revertProcessVersion(Event event)
            throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            ExceptionDao, JAXBException, NumberFormatException, ParseException {

        // click on a version might have selected/unselected the corresponding process too
        /* for the process:
           * - if the version is the only one selected => un select the process
           * - if no versions are selected => select the process
           */

        Toolbarbutton versionNameB = (Toolbarbutton) event.getTarget();

        Row versionR = (Row) versionNameB.getParent();                         // selected version
        Rows versionsR = (Rows) versionR.getParent();                         // rows (versions)
        List<Row> versions = versionsR.getChildren();                        // one row for each versions of the same process

        Detail versionD = (Detail) versionsR.getParent().getParent();         // detail (related to the process)
        Row processR = (Row) versionD.getParent();                             // process

        Checkbox versionCB = (Checkbox) versionNameB.getPreviousSibling().getPreviousSibling();    // checkbox associated with the version
        Checkbox processCB = (Checkbox) processR.getChildren().get(1);        // checkbox associated with the process

        /*
           * Was the version selected?
           * if no, select it and its process
           */
        if (!versionCB.isChecked()) {
            versionCB.setChecked(true);
            processCB.setChecked(true);
            highlightP(processR, true);
            highlightV(versionR, true);

        } else {
            // the version was selected => unselect it
            versionCB.setChecked(false);
            highlightV(versionR, false);
            // for the same process, if no versions remain selected, unselect the process
            // search whether one is checked
            int j = 0;
            while (j < versions.size() && !((Checkbox) versions.get(j).getFirstChild()).isChecked()) {
                j++;
            }
            // none is checked
            if (j == versions.size()) {
                processCB.setChecked(false);
                highlightP(processR, false);
            }
        }
    }

    private void highlightV(Row versionR, Boolean highlighted) {

        String selected = Constants.SELECTED_VERSION;
        String unselected = Constants.UNSELECTED_VERSION;
        String querySelected = "background-color:#FF9900" + ";" + Constants.TOOLBARBUTTON_STYLE;
        String queryUnSelected = "background-color:#FFCC99" + ";" + Constants.TOOLBARBUTTON_STYLE;
        Listbox annotations = (Listbox) versionR.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
        String pId_versionName = versionR.getFirstChild().getId();
        if (highlighted) {
            if (this.isQueryResult && pId_versionName.compareTo(this.processQ.getId().toString() + "/" + this.versionQ.getName()) == 0) {
                versionR.setStyle(querySelected);    // highlight version
                // highlight drop down list box
                annotations.setStyle(querySelected);
            } else {
                versionR.setStyle(selected);    // highlight version
                // highlight drop down list box
                annotations.setStyle(selected);
            }
            ColorFont(versionR, "#FFFFFF");
            ColorFont(annotations, "#FFFFFF");
        } else {
            if (this.isQueryResult && pId_versionName.compareTo(this.processQ.getId().toString() + "/" + this.versionQ.getName()) == 0) {
                versionR.setStyle(queryUnSelected);    // highlight version
                // highlight drop down list box
                annotations.setStyle(queryUnSelected);
            } else {
                versionR.setStyle(unselected);
                // highlight drop down list box
                annotations.setStyle(unselected);
            }
            ColorFont(versionR, "#000000");
            ColorFont(annotations, "#000000");
        }


    }


    private void highlightP(Row processR, Boolean highlighted) {

        // #FFCC99 is orange
        // #FF9900 is orange
        // #FFFFFF is white
        // #000000 is black

        String selected = Constants.SELECTED_PROCESS;
        String queryUnselected = "background-color:#FFCC99" + ";" + Constants.TOOLBARBUTTON_STYLE;
        String unselectedEven = Constants.UNSELECTED_EVEN;
        String unselectedOdd = Constants.UNSELECTED_ODD;
        String querySelected = "background-color:#FF9900" + ";" + Constants.TOOLBARBUTTON_STYLE;
        Integer index = processR.getParent().getChildren().indexOf(processR);
        Detail processD = (Detail) processR.getFirstChild();
        Integer processId = Integer.parseInt(processD.getId());
        if (highlighted) {
            if (this.isQueryResult && processId.equals(this.processQ.getId())) {
                processR.setStyle(querySelected);
                processD.setStyle(querySelected);
            } else {
                processR.setStyle(selected);
                processD.setStyle(selected);
            }
            ColorFont(processR, "#FFFFFF");
        } else {
            if (index % 2 == 0) {
                //index is even
                if (this.isQueryResult && processId.equals(this.processQ.getId())) {
                    processR.setStyle(queryUnselected);
                    processD.setStyle(queryUnselected);
                } else {
                    processR.setStyle(unselectedEven);
                    processD.setStyle(unselectedEven);
                }
            } else {
                //index is odd
                if (this.isQueryResult && processId.equals(this.processQ.getId())) {
                    processR.setStyle(queryUnselected);
                    processD.setStyle(queryUnselected);
                } else {
                    processR.setStyle(unselectedOdd);
                    processD.setStyle(unselectedOdd);
                }
            }
            ColorFont(processR, "#000000");
        }
    }

    /**
     * control the selected processes:
     * - simple click reverse selection of the process
     * - click + shift selects all processes from the previous selected (if any) and the current one
     *
     * @param event corresponding to the click
     * @throws InterruptedException
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws java.text.ParseException
     * @throws NumberFormatException
     */
    protected void maintainSelectedProcesses(Event event)
            throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            ExceptionDao, JAXBException, NumberFormatException, ParseException {
        /*
           * selectedProcess contains selected processes (ordered by id)
           * 1 - click on a process: reverts selection of it
           * 2 - click+shift on a process: select all processes whose index
           * is between the last in the list and the one previously selected (if any)
           */
        MouseEvent e = (MouseEvent) event;
        Toolbarbutton tb = (Toolbarbutton) e.getTarget();     // toolbarbutton whose label is a processName
        Row processSummaryR = (Row) tb.getParent();         // row associated with a process process
        Rows processSummariesR = (Rows) processSummaryR.getParent();
        Integer index = processSummariesR.getChildren().indexOf(processSummaryR);    // index of the selected row

        // find the process which is the last selected (if any) in the list of displayed processes
        // because there is no warranty on the order of objects returned by getChildren()
        // need to find the max index among those selected.
        Iterator<Row> itRow = processSummariesR.getChildren().iterator();
        int maxIndex = -1,
                curIndex = 0;
        while (itRow.hasNext()) {
            Row currentRow = itRow.next();
            curIndex = currentRow.getParent().getChildren().indexOf(currentRow);
            Checkbox currentCB = (Checkbox) currentRow.getChildren().get(1);
            if (currentCB.isChecked()) {
                if (curIndex > maxIndex) maxIndex = curIndex;
            }
        }
        // if no selected process, whatever is the click, select the process
        if (maxIndex == -1) {
            reverseProcessSelection(index);
        } else {
            // if click+shift: select all processes between the one click+shift selected
            // and the last one in the list.
            if (e.getKeys() == 260) {
                Integer lower, upper;
                if (maxIndex > index) {
                    // selection before the last previously selected
                    upper = maxIndex - 1;
                    lower = index;
                } else {
                    // selection after the last previously selected
                    upper = index;
                    lower = maxIndex + 1;
                }
                // between lower and lower: for those selected do nothing
                // for others: select them,
                for (int j = lower; j <= upper; j++) {
                    Row currentRow = (Row) processSummariesR.getChildren().get(j);
                    Checkbox currentCB = (Checkbox) currentRow.getChildren().get(1);
                    if (!currentCB.isChecked()) reverseProcessSelection(j);
                }
            } else {
                // if simple click: revert the selection
                reverseProcessSelection(index);
            }
        }
    }

    /**
     * Revert processes selections: perform minus
     * this.selectedProcess = all processes - this.selectedProcess
     *
     * @throws java.text.ParseException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws InterruptedException
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws java.text.ParseException
     * @throws NumberFormatException
     */
    protected void revertSelection()
            throws NumberFormatException, InterruptedException, ExceptionDao, JAXBException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, ParseException {
        Iterator<Row> itRow = this.processSummariesRows.getChildren().iterator();
        int curIndex = 0;
        while (itRow.hasNext()) {
            Row currentRow = itRow.next();
            curIndex = currentRow.getParent().getChildren().indexOf(currentRow);
            reverseProcessSelection(curIndex);
        }
    }


    /**
     * Reverse process selection.
     * - if selected -> unselected: unselect associated versions
     * if process details open: no version highlighted
     * - if unselected -> selected: select latest version
     * if process details open: highlight latest version
     *
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws java.text.ParseException
     */
    protected void reverseProcessSelection(Integer index) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ExceptionDao, JAXBException {
        Row processSummaryR = (Row) this.processSummariesRows.getChildren().get(index); // row for process
        Label latestVersionL = (Label) processSummaryR.getChildren().get(this.latestVersionPos); // process latest version
        String latestVersion = latestVersionL.getValue();
        Detail processSummaryD = (Detail) processSummaryR.getFirstChild();    // detail for process
        Checkbox cbP = (Checkbox) processSummaryR.getChildren().get(1); // checkbox for process
        if (cbP.isChecked()) {
            // process was selected
            // unselect all selected version(s)
            //unselectProcessVersions (index);
            // no version highlighted
            Grid versionsG = (Grid) processSummaryD.getFirstChild(); // grid for process versions
            Rows versionsR = (Rows) versionsG.getChildren().get(1); // rows for process versions
            List<Row> versionR = versionsR.getChildren();
            for (int i = 0; i < versionR.size(); i++) {
                Checkbox cbV = (Checkbox) versionR.get(i).getFirstChild();
                cbV.setChecked(false);
                highlightV(versionR.get(i), false);
            }
        } else {
            // process was not selected
            if (processSummaryD.getChildren().size() == 0) {
                // detail needs to be built
                displayVersionsSummaries(processSummaryD);
            }
            // highlight latest version
            // find latest version
            Grid versionsG = (Grid) processSummaryD.getFirstChild(); // grid for process versions
            Rows versionsR = (Rows) versionsG.getChildren().get(1); // rows for process versions
            List<Row> versionR = versionsR.getChildren();
            int i = 0;
            while (i < versionR.size()) {
                Row version = (Row) versionR.get(i);
                Checkbox cbV = (Checkbox) version.getFirstChild();
                if (cbV.getId().split("/")[1].compareTo(latestVersion) == 0) {
                    break;
                }
                i++;
            }
            if (i < versionR.size()) {
                // must be true!
                Checkbox cbV = (Checkbox) versionR.get(i).getFirstChild();
                cbV.setChecked(true);
                highlightV(versionR.get(i), true);
            }
        }
        cbP.setChecked(!cbP.isChecked());
        highlightP(processSummaryR, cbP.isChecked());
    }

    /**
     * refresh the display without reloading the data. Keeps selection if any.
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     * @throws javax.xml.bind.JAXBException
     */
    protected void refresh() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException {
        List<Row> processSummariesRs = this.processSummariesRows.getChildren();
        for (int i = 0; i < processSummariesRs.size(); i++) {
            Row processSummaryR = processSummariesRs.get(i);
            Checkbox cbP = (Checkbox) processSummaryR.getChildren().get(1); // checkbox for process
            Detail processSummaryD = (Detail) processSummaryR.getFirstChild();    // detail for process
            Grid versionsG = (Grid) processSummaryD.getFirstChild(); // grid for process versions, might not exist!
            if (versionsG != null) {
                Rows versionsR = (Rows) versionsG.getChildren().get(1); // rows for process versions
                List<Row> versionR = versionsR.getChildren();
                for (int j = 0; j < versionR.size(); j++) {
                    Checkbox cbV = (Checkbox) versionR.get(j).getFirstChild();
                    highlightV(versionR.get(j), cbV.isChecked());
                }
            }
            highlightP(processSummaryR, cbP.isChecked());
        }
    }

    private void ColorFont(HtmlBasedComponent v, String color) {

        //		Iterator<HtmlBasedComponent> itV = v.getChildren().iterator();
        //		while (itV.hasNext()) {
        //			HtmlBasedComponent child = (HtmlBasedComponent) itV.next();
        //			child.setStyle("color:"+color + ";" + Constants.TOOLBARBUTTON_STYLE);
        //		}
    }

    public HashMap<Checkbox, VersionSummaryType> getProcessVersionsHM() {
        return processVersionsHM;
    }

    public HashMap<Checkbox, ProcessSummaryType> getProcessHM() {
        return processHM;
    }

    public HashMap<Checkbox, List<Checkbox>> getMapProcessVersions() {
        return mapProcessVersions;
    }

    /**
     * Add the process to the table
     */
    public void displayNewProcess(ProcessSummaryType process) {
        this.displayOneProcess(process);
    }


    public Grid getProcessSummariesGrid() {
        return processSummariesGrid;
    }

    public Paging getPg() {
        return pg;
    }

    public void setPg(Paging pg) {
        this.pg = pg;
    }

    public Boolean getIsQueryResult() {
        return isQueryResult;
    }


}
