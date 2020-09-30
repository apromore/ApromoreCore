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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

import javax.xml.datatype.DatatypeFactory;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.notification.Notification;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.workspaceOptions.AddFolderController;
import org.apromore.portal.dialogController.workspaceOptions.RenameFolderController;
import org.apromore.portal.dialogController.workspaceOptions.CopyAndPasteController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ImportProcessResultType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.SecurityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;

public abstract class BaseListboxController extends BaseController {

    private static final long serialVersionUID = -4693075788311730404L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseListboxController.class);

    private static final String ALERT = "Alert";
    private static final String FOLDER_DELETE = "Are you sure you want to delete selected folder(s) and all it's contents?";
    private static final String LOG_DELETE = "Are you sure you want to delete selected log(s)?";
    private static final String PROCESS_DELETE = "Are you sure you want to delete the selected process model(s)? If no version has been selected, the latest version will be removed.";
    private static final String MIXED_DELETE = "Are you sure you want to delete the selected file(s)? For a process model, if no version has been selected, the latest version will be removed.";

    private static final String TILE_VIEW = "tile";
    private static final String LIST_VIEW = "list";

    private final Listbox listBox;

    private final MainController mainController;

    private final Button refreshB;
    private final Button btnUpload;
    private final Button btnDownload;
    private final Button btnSelectAll;
    private final Button btnSelectNone;
    private final Button btnCut;
    private final Button btnCopy;
    private final Button btnPaste;
    private final Button btnAddFolder;
    private final Button btnAddProcess;
    //private final Button btnGEDFolder;
    private final Button btnRenameFolder;
    private final Button btnRemoveFolder;
    private final Button btnListView;
    private final Button btnTileView;
    private final Button btnSecurity;
    private final Button btnUserMgmt;

    private PortalContext portalContext;
    private Map<String, PortalPlugin> portalPluginMap;
    private ArrayList<LogSummaryType> sourceLogs = new ArrayList<>();
    private ArrayList<FolderType> sourceFolders = new ArrayList<>();
    private ArrayList<ProcessSummaryType> sourceProcesses = new ArrayList<>();

    private CopyAndPasteController copyAndPasteController;

    public BaseListboxController(MainController mainController, String componentId, ListitemRenderer itemRenderer) {
        super();
        setHflex("100%");
        setVflex("100%");

        this.copyAndPasteController = new CopyAndPasteController(mainController, UserSessionManager.getCurrentUser());
        this.mainController = mainController;
        this.portalContext = new PluginPortalContext(mainController);
        listBox = createListbox(componentId);
        listBox.setPaginal((Paging) mainController.getFellow("pg"));
        listBox.setItemRenderer(itemRenderer);

        refreshB = (Button) mainController.getFellow("refreshB");
        btnUpload = (Button) mainController.getFellow("btnUpload");
        btnDownload = (Button) mainController.getFellow("btnDownload");
        btnSelectAll = (Button) mainController.getFellow("btnSelectAll");
        btnSelectNone = (Button) mainController.getFellow("btnSelectNone");
        btnCut = (Button) mainController.getFellow("btnCut");
        btnCopy = (Button) mainController.getFellow("btnCopy");
        btnPaste = (Button) mainController.getFellow("btnPaste");
        btnAddFolder = (Button) mainController.getFellow("btnAddFolder");
        btnAddProcess = (Button) mainController.getFellow("btnAddProcess");
        //btnGEDFolder = (Button) mainController.getFellow("btnGEDFolder");
        btnRenameFolder = (Button) mainController.getFellow("btnRenameFolder");
        btnRemoveFolder = (Button) mainController.getFellow("btnRemoveFolder");
        btnListView = (Button) mainController.getFellow("btnListView");
        btnTileView = (Button) mainController.getFellow("btnTileView");
        btnSecurity = (Button) mainController.getFellow("btnSecurity");
        btnUserMgmt = (Button) mainController.getFellow("btnUserMgmt");

        attachEvents();

        appendChild(listBox);
        if (LIST_VIEW.equals(getPersistedView())) {
            setTileView(false);
        } else {
            setTileView(true);
        }

        portalPluginMap = PortalPluginResolver.getPortalPluginMap();
    }

    public void setPersistedView(String view) {
        Clients.evalJavaScript("Ap.common.setCookie('view','" + view + "')");
    }

    public String getPersistedView() {
        Cookie[] cookies = ((HttpServletRequest)Executions.getCurrent().getNativeRequest()).getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("view".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    protected void attachEvents() {

        this.listBox.addEventListener("onKeyPress", new EventListener<KeyEvent>() {
            @Override
            public void onEvent(KeyEvent keyEvent) throws Exception {
                if ((keyEvent.isCtrlKey() && keyEvent.getKeyCode() == 65)) {
                    if (listBox.getSelectedCount() > 0) {
                        selectAll();
                    } else {
                        unselectAll();
                    }
                }
            }
        });

        this.refreshB.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                refreshContent();
            }
        });

        this.btnUpload.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                importFile();
            }
        });

        this.btnDownload.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                exportFile();
            }
        });

        this.btnSelectAll.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                selectAll();
            }
        });

        this.btnSelectNone.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                unselectAll();
            }
        });

        this.btnCut.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                cut();
            }
        });

        this.btnCopy.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                copy();
            }
        });

        this.btnPaste.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                paste();
            }
        });

        this.btnAddFolder.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                addFolder();
            }
        });

        this.btnAddProcess.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                mainController.openNewProcess();
            }
        });

        /*
        this.btnGEDFolder.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                changeGED();
            }
        });
        */

        this.btnRenameFolder.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                rename();
            }
        });

        this.btnRemoveFolder.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                removeFolder();
            }
        });

        this.btnListView.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                setTileView(false);
            }
        });

        this.btnTileView.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                setTileView(true);
            }
        });


        this.btnSecurity.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                security();
            }
        });

        this.btnUserMgmt.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                userMgmt();
            }
        });

    }

    public void setTileView(boolean tileOn) {
        Listhead listHead = (Listhead)this.listBox.query(".ap-listbox-process-head");
        String sclass = Objects.requireNonNull(this.listBox.getSclass(), "");
        if (tileOn) {
            if (!sclass.contains("ap-tiles-view")) {
                this.listBox.setSclass(sclass.trim() + " ap-tiles-view");
            }
            if (listHead != null) {
                listHead.setVisible(false);
            }
            toggleComponentSclass(btnTileView, true, "ap-btn-off", "ap-btn-on");
            toggleComponentSclass(btnListView, false, "ap-btn-off", "ap-btn-on");
            setPersistedView(TILE_VIEW);
        } else {
            if (sclass.contains("ap-tiles-view")) {
                this.listBox.setSclass(sclass.replace("ap-tiles-view", ""));
            }
            if (listHead != null) {
                listHead.setVisible(true);
            }
            toggleComponentSclass(btnListView, true, "ap-btn-off", "ap-btn-on");
            toggleComponentSclass(btnTileView, false, "ap-btn-off", "ap-btn-on");
            setPersistedView(LIST_VIEW);
        }
    }

    /**
     * Refresh the currently displayed content from any kind of data source
     */
    protected abstract void refreshContent();

    protected Listbox createListbox(String componentId) {
        return (Listbox) Executions.createComponents(componentId, getMainController(), null);
    }

    protected Listbox getListBox() {
        return listBox;
    }

    protected ListModelList getListModel() {
        return (ListModelList) listBox.getModel();
    }

    public void unselectAll() {
        getListBox().clearSelection();
    }

    public void selectAll() {
        getListBox().selectAll();
    }

    protected void importFile() throws InterruptedException {
        getMainController().eraseMessage();
        try {
            new ImportController(getMainController());
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void exportFile() throws Exception {
        PortalPlugin downloadPlugin;

        try {
            downloadPlugin = portalPluginMap.get("Download");
            downloadPlugin.execute(portalContext);
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void addFolder() throws InterruptedException {
        getMainController().eraseMessage();
        try {
            new AddFolderController(getMainController());
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void renameFolder() throws DialogException {
        getMainController().eraseMessage();
        try {
            // List<Integer> folderIds = UserSessionManager.getSelectedFolderIds();
            List<Integer> folderIds = getMainController().getPortalSession().getSelectedFolderIds();

            if (folderIds.size() == 1) {
                int selectedFolderId = folderIds.get(0);
                String selectedFolderName = "";
                List<FolderType> availableFolders = getMainController().getPortalSession().getCurrentFolder() == null ||
                        getMainController().getPortalSession().getCurrentFolder().getId() == 0 ?
                        getMainController().getPortalSession().getTree() :
                        getMainController().getPortalSession().getCurrentFolder().getFolders();
                for (FolderType folder : availableFolders) {
                    if (folder.getId() == selectedFolderId) {
                        selectedFolderName = folder.getFolderName();
                        break;
                    }
                }
                new RenameFolderController(getMainController(), folderIds.get(0), selectedFolderName);
            } else if (folderIds.size() > 1) {
                Notification.error("Only one item can be renamed at a time.");
            }
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void renameLogOrProcess() throws DialogException {
        PortalPlugin editSelectionMetadataPlugin;

        getMainController().eraseMessage();
        try {
            editSelectionMetadataPlugin = portalPluginMap.get("Rename");
            editSelectionMetadataPlugin.execute(portalContext);
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void rename() throws InterruptedException {
        try {
            if (getSelectionCount() == 0) {
                Notification.error("Please select a file/folder to rename");
                return;
            } else if (getSelectionCount() > 1) {
                Notification.error("You can not rename multiple selections");
                return;
            }
            List<Integer> folderIds = getMainController().getPortalSession().getSelectedFolderIds();

            if (folderIds.size() == 0) {
                renameLogOrProcess();
            } else {
                renameFolder();
            }
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void removeFolder() throws Exception {
        // See if the user has mixed folders and process models. we handle everything differently.
        ArrayList<FolderType> folders = getSelectedFolders();
        Map<SummaryType, List<VersionSummaryType>> elements =  getMainController().getSelectedElementsAndVersions();

        if (doesSelectionContainFoldersAndElements(folders, elements)) { // mixed
            showMessageFoldersAndElementsDelete(getMainController(), folders);
        } else {
            if (folders != null && !folders.isEmpty()) { // folder only
                showMessageFolderDelete(getMainController(), folders);
            } else if (elements != null && !elements.isEmpty()) { // processes and logs
                if (getSelectedProcesses().size() == 0) { // log only
                    showMessageLogsDelete(getMainController());
                } else if (getSelectedLogs().size() == 0) { // process only
                    showMessageProcessesDelete(getMainController());
                } else { // mixed log(s) and process(es)
                    showMessageElementsDelete(getMainController());
                }
            } else {
                LOGGER.error("Nothing selected to delete?");
            }
        }
    }

    public void cut() {
        copyAndPasteController.cut(getSelection(), getSelectionCount());
    }

    public void copy() {
        copyAndPasteController.copy(getSelection(), getSelectionCount());
    }

    public void paste() throws Exception {
        // FolderType currentFolder = UserSessionManager.getCurrentFolder();
        FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();
        Integer targetFolderId = currentFolder == null ? 0 : currentFolder.getId();
        try {
            copyAndPasteController.paste(targetFolderId);
        } catch (Exception e) {
            Messagebox.show("An error is occured during paste process", "Apromore", Messagebox.OK, Messagebox.ERROR);
        }
        refreshContent();
    }

    private ArrayList<FolderType> getSelectedFolders() {
        ArrayList<FolderType> folderList = new ArrayList<>();
        if (this instanceof ProcessListboxController) {
            Set<Object> selectedItem = getListModel().getSelection();
            for (Object obj : selectedItem) {
                if (obj instanceof FolderType) {
                    folderList.add((FolderType) obj);
                }
            }
        }
        return folderList;
    }

    private ArrayList<LogSummaryType> getSelectedLogs() {
        ArrayList<LogSummaryType> logList = new ArrayList<>();
        if (this instanceof ProcessListboxController) {
            Set<Object> selectedItem = getListModel().getSelection();
            for (Object obj : selectedItem) {
                if (obj instanceof LogSummaryType) {
                    logList.add((LogSummaryType) obj);
                }
            }
        }
        return logList;
    }

    private ArrayList<ProcessSummaryType> getSelectedProcesses() {
        ArrayList<ProcessSummaryType> processList = new ArrayList<>();
        if (this instanceof ProcessListboxController) {
            Set<Object> selectedItem = getListModel().getSelection();
            for (Object obj : selectedItem) {
                if (obj instanceof ProcessSummaryType) {
                    processList.add((ProcessSummaryType) obj);
                }
            }
        }
        return processList;
    }

    public Set<Object> getSelection() {
        return getListModel().getSelection();
    }

    public int getSelectionCount() {
        return listBox.getSelectedCount();
    }

    /* Show the message tailored to deleting process model. */
    private void showMessageProcessesDelete(final MainController mainController) throws Exception {
        Messagebox.show(PROCESS_DELETE, ALERT, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        deleteElements(mainController);
                        mainController.loadWorkspace();
                        refreshContent();
                        break;
                    case Messagebox.NO:
                        break;
                }
            }
        });
    }

    /* Show the message tailored to deleting log model. */
    private void showMessageLogsDelete(final MainController mainController) throws Exception {
        Messagebox.show(LOG_DELETE, ALERT, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        deleteElements(mainController);
                        mainController.loadWorkspace();
                        refreshContent();
                        break;
                    case Messagebox.NO:
                        break;
                }
            }
        });
    }

    /* Show a message tailored to deleting a combo of folders and processes */
    private void showMessageElementsDelete(final MainController mainController) throws Exception {
        Messagebox.show(MIXED_DELETE, ALERT, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        deleteElements(mainController);
                        mainController.loadWorkspace();
                        refreshContent();
                        break;
                    case Messagebox.NO:
                        break;
                }
            }
        });
    }

    /* Show the message tailored to deleting one or more folders. */
    private void showMessageFolderDelete(final MainController mainController, final ArrayList<FolderType> folders) throws Exception {
        Messagebox.show(FOLDER_DELETE, ALERT, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        deleteFolders(folders, mainController);
                        mainController.loadWorkspace();
                        refreshContent();
                        break;
                    case Messagebox.NO:
                        break;
                }
            }
        });
    }

    /* Show a message tailored to deleting a combo of folders and processes */
    private void showMessageFoldersAndElementsDelete(final MainController mainController, final ArrayList<FolderType> folders) throws Exception {
        Messagebox.show(MIXED_DELETE, ALERT, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        deleteFolders(folders, mainController);
                        deleteElements(mainController);
                        mainController.loadWorkspace();
                        refreshContent();
                        break;
                    case Messagebox.NO:
                        break;
                }
            }
        });
    }

    /* Setup the Security controller. */
    protected void security() throws InterruptedException {
        getMainController().eraseMessage();
        try {
            new SecuritySetupController(getMainController());
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void userMgmt() throws InterruptedException {
        PortalPlugin userMgmtPlugin;

        getMainController().eraseMessage();
        try {
            userMgmtPlugin = portalPluginMap.get("Manage user permissions");
            userMgmtPlugin.execute(portalContext);
        } catch(Exception e) {
            LOGGER.error("Unable to create user administration dialog", e);
            Messagebox.show("Unable to create user administration dialog");
        }
    }



    /* Removes all the selected processes, either the select version or the latest if no version is selected. */
    private void deleteElements(MainController mainController) throws Exception {
        //mainController.getMenu().deleteSelectedElements();

        this.mainController.eraseMessage();
        Map<SummaryType, List<VersionSummaryType>> elements = mainController.getSelectedElementsAndVersions();
        if (elements.size() != 0) {
            this.mainController.deleteElements(elements);
            mainController.clearProcessVersions();
        } else {
            this.mainController.displayMessage("No process version selected.");
        }
    }

    /* Removes all the selected folders and the containing folders and processes. */
    private void deleteFolders(ArrayList<FolderType> folders, MainController mainController) {
        int failures = 0;

        for (FolderType folderId : folders) {
            try {
                mainController.getService().deleteFolder(folderId.getId(), UserSessionManager.getCurrentUser().getUsername());
            } catch (Exception e) {
                failures += 1;
            }
        }
        if (failures > 0) {
            Messagebox.show("Could not perform all delete operations. You may not be authorized to delete some of the resources.", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
        mainController.reloadSummaries();
    }

    public abstract void displaySummaries(List<FolderType> subFolders, SummariesType summaries, Boolean isQueryResult);

    public abstract SummaryListModel displaySummaries(List<FolderType> subFolders, boolean isQueryResult);

    /* Does the selection in the main detail list contain folders and processes. */
    private boolean doesSelectionContainFoldersAndElements(ArrayList<FolderType> folders, Map<SummaryType, List<VersionSummaryType>> elements) throws Exception {
        return (folders != null && !folders.isEmpty()) && (elements != null && !elements.isEmpty());
    }

    public MainController getMainController() {
        return mainController;
    }

    public class SummaryListModel extends ListModelList {
        final int pageSize = 10;  // TODO: ought to be externally configurable

        private SummariesType summaries, logSummaries;
        private int currentPageIndex = 0, currentLogPageIndex = 0;
        private List<FolderType> subFolders;

        /**
         * Constructor.
         *
         * @param subFolders  will be displayed before processes
         */
        SummaryListModel(List<FolderType> subFolders) {
            this.subFolders = subFolders;
            setMultiple(true);
        }

        @Override
        public Object getElementAt(int index) {

            // Elements are always accessed in the following order: subfolders, then process models, then logs

            if (index < subFolders.size()) {
                return subFolders.get(index);  // subfolder
            } else {
                int processIndex = index - subFolders.size();
                SummariesType summaries = getSummaries(processIndex / pageSize);
                if (processIndex % pageSize < summaries.getSummary().size()) {
                    return summaries.getSummary().get(processIndex % pageSize);  // process model
                } else {
                    int logIndex = processIndex - summaries.getCount().intValue();
                    return getLogSummaries(logIndex / pageSize).getSummary().get(logIndex % pageSize);  // log
                }
            }
        }

        @Override
        public int getSize() {
            return subFolders.size() + getSummaries(currentPageIndex).getCount().intValue() + getLogSummaries(currentLogPageIndex).getCount().intValue();
        }

        public int getTotalCount() {
            return getSummaries(currentPageIndex).getTotalCount().intValue();
        }

        private SummariesType getSummaries(int pageIndex) {
            if (summaries == null || currentPageIndex != pageIndex) {
                UserType user = UserSessionManager.getCurrentUser();
                // FolderType currentFolder = UserSessionManager.getCurrentFolder();
                FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();
                summaries = getService().getProcessSummaries(user.getId(), currentFolder == null ? 0 : currentFolder.getId(), pageIndex, pageSize);
                currentPageIndex = pageIndex;
            }
            return summaries;
        }

        private SummariesType getLogSummaries(int pageIndex) {
            if (logSummaries == null || currentLogPageIndex != pageIndex) {
                UserType user = UserSessionManager.getCurrentUser();
                // FolderType currentFolder = UserSessionManager.getCurrentFolder();
                FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();
                logSummaries = getService().getLogSummaries(user.getId(), currentFolder == null ? 0 : currentFolder.getId(), pageIndex, pageSize);
                currentLogPageIndex = pageIndex;
            }
            return logSummaries;
        }
    }
}
