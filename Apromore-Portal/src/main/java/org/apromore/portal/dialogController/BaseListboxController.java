package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.workspaceOptions.AddFolderController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;

public abstract class BaseListboxController extends BaseController {

    private static final long serialVersionUID = -4693075788311730404L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseListboxController.class);

    private static final String ALERT = "Alert";
    private static final String FOLDER_DELETE = "Are you sure you want to delete selected Folder(s) and all it's contents?";
    private static final String PROCESS_DELETE = "Are you sure you want to delete selected Process(es)? If no version has been select the latest version will be removed.";
    private static final String FOLDER_PROCESS_DELETE = "Are you sure you want to delete selected Folders and Processes?";

    private final Listbox listBox;

    private final MainController mainController;

    private final Button refreshB;
    private final Button btnAddFolder;
    private final Button btnAddProcess;
    private final Button btnRenameFolder;
    private final Button btnRemoveFolder;
    private final Button btnSecurity;


    public BaseListboxController(MainController mainController, String componentId, ListitemRenderer itemRenderer) {
        super();
        setHflex("100%");
        setVflex("100%");

        this.mainController = mainController;
        listBox = createListbox(componentId);
        listBox.setPaginal((Paging) mainController.getFellow("pg"));
        listBox.setItemRenderer(itemRenderer);

        refreshB = (Button) mainController.getFellow("refreshB");
        btnAddFolder = (Button) mainController.getFellow("btnAddFolder");
        btnAddProcess = (Button) mainController.getFellow("btnAddProcess");
        btnRenameFolder = (Button) mainController.getFellow("btnRenameFolder");
        btnRemoveFolder = (Button) mainController.getFellow("btnRemoveFolder");
        btnSecurity = (Button) mainController.getFellow("btnSecurity");

        attachEvents();

        appendChild(listBox);
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

        this.btnAddFolder.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                addFolder();
            }
        });

        this.btnAddProcess.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                addProcess();
            }
        });

        this.btnRenameFolder.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                renameFolder();
            }
        });

        this.btnRemoveFolder.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                removeFolder();
            }
        });

        this.btnSecurity.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                security();
            }
        });
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

    protected void addFolder() throws InterruptedException {
        getMainController().eraseMessage();
        try {
            new AddFolderController(getMainController(), 0, "");
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void addProcess() throws InterruptedException {
        getMainController().eraseMessage();
        try {
            new CreateProcessController(getMainController(), getMainController().getNativeTypes());
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

    protected void renameFolder() throws InterruptedException {
        getMainController().eraseMessage();
        try {
            List<Integer> folderIds = UserSessionManager.getSelectedFolderIds();

            if (folderIds.size() == 1) {
                int selectedFolderId = folderIds.get(0);
                String selectedFolderName = "";
                List<FolderType> availableFolders = UserSessionManager.getCurrentFolder() == null || UserSessionManager.getCurrentFolder().getId() == 0 ? UserSessionManager.getTree() : UserSessionManager.getCurrentFolder().getFolders();
                for (FolderType folder : availableFolders) {
                    if (folder.getId() == selectedFolderId) {
                        selectedFolderName = folder.getFolderName();
                        break;
                    }
                }

                new AddFolderController(getMainController(), folderIds.get(0), selectedFolderName);
            } else if (folderIds.size() > 1) {
                Messagebox.show("Only one item can be renamed at the time.", "Attention", Messagebox.OK, Messagebox.ERROR);
            } else {
                Messagebox.show("Please select item to rename", "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void removeFolder() throws Exception {
        // See if the user has mixed folders and process models. we handle everything differently.
        ArrayList<FolderType> folders =  getMainController().getMenu().getSelectedFolders();
        HashMap<ProcessSummaryType, List<VersionSummaryType>> processes =  getMainController().getMenu().getSelectedProcessVersions();

        if (doesSelectionContainFoldersAndProcesses(folders, processes)) {
            showMessageFoldersAndProcessesDelete(getMainController(), folders);
        } else {
            if (folders != null && !folders.isEmpty()) {
                showMessageFolderDelete(getMainController(), folders);
            } else if (processes != null && !processes.isEmpty()) {
                showMessageProcessesDelete(getMainController());
            } else {
               LOGGER.error("Nothing selected to delete?");
            }
        }
        mainController.loadWorkspace();
    }

    /* Show the message tailored to deleting one or more folders. */
    private void showMessageProcessesDelete(final MainController mainController) throws Exception {
        Messagebox.show(PROCESS_DELETE, ALERT, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        deleteProcesses(mainController);
                        break;
                    case Messagebox.NO:
                        break;
                }
            }
        });    }

    /* Show the message tailored to deleting one or more folders. */
    private void showMessageFolderDelete(final MainController mainController, final ArrayList<FolderType> folders) throws Exception {
        Messagebox.show(FOLDER_DELETE, ALERT, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        deleteFolders(folders, mainController);
                        break;
                    case Messagebox.NO:
                        break;
                }
            }
        });
    }

    /* Show a message tailored to deleting a combo of folders and processes */
    private void showMessageFoldersAndProcessesDelete(final MainController mainController, final ArrayList<FolderType> folders) throws Exception {
        Messagebox.show(FOLDER_PROCESS_DELETE, ALERT, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        deleteFolders(folders, mainController);
                        deleteProcesses(mainController);
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



    /* Removes all the selected processes, either the select version or the latest if no version is selected. */
    private void deleteProcesses(MainController mainController) throws Exception {
        mainController.getMenu().deleteSelectedProcessVersions();
    }

    /* Removes all the selected folders and the containing folders and processes. */
    private void deleteFolders(ArrayList<FolderType> folders, MainController mainController) {
        for (FolderType folderId : folders) {
            mainController.getService().deleteFolder(folderId.getId());
        }
        //mainController.baseListboxController.refreshContent();
        mainController.reloadProcessSummaries();
    }

    /* Does the selection in the main detail list contain folders and processes. */
    private boolean doesSelectionContainFoldersAndProcesses(ArrayList<FolderType> folders, HashMap<ProcessSummaryType, List<VersionSummaryType>> processes) throws Exception {
        return (folders != null && !folders.isEmpty()) && (processes != null && !processes.isEmpty());
    }

    public MainController getMainController() {
        return mainController;
    }
}
