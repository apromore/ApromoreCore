package org.apromore.portal.dialogController;

import org.apromore.model.FolderType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.workspaceOptions.AddFolderController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Messagebox;

import java.util.List;

public class WorkspaceOptionsController extends Menubar {

    private MainController mainController;
    private AddFolderController addFolderController;
    private CreateProcessController createController;
    private SecuritySetupController securitySetupController;

    private Button btnAddFolder;
    private Button btnAddProcess;
    private Button btnRenameFolder;
    private Button btnRemoveFolder;
    private Button btnSecurity;
    private Button btnListView;

    public WorkspaceOptionsController(MainController mainController) throws ExceptionFormats {
        this.mainController = mainController;
        Hbox options = (Hbox) this.mainController.getFellow("workspaceOptionsPanel").getFellow("folderOptions");
        this.btnAddFolder = (Button) options.getFellow("btnAddFolder");
        this.btnAddProcess = (Button) options.getFellow("btnAddProcess");
        this.btnRenameFolder = (Button) options.getFellow("btnRenameFolder");
        this.btnRemoveFolder = (Button) options.getFellow("btnRemoveFolder");
        this.btnSecurity = (Button) options.getFellow("btnSecurity");
        this.btnListView = (Button) options.getFellow("btnListView");

        this.btnAddFolder.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                addFolder();
            }
        });

        this.btnListView.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                showList();
            }
        });

        this.btnAddProcess.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                addProcess();
            }
        });

        this.btnRenameFolder.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                renameFolder();
            }
        });

        this.btnRemoveFolder.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                removeFolder();
            }
        });

        this.btnSecurity.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                security();
            }
        });
    }

    protected void showList() {
        this.mainController.toggleView(false);
    }

    protected void addFolder() throws InterruptedException {
        this.mainController.eraseMessage();
        try {
            this.addFolderController = new AddFolderController(this, mainController, 0, "");
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void addProcess() throws InterruptedException {
        this.mainController.eraseMessage();
        try {
            this.createController = new CreateProcessController(this.mainController, this.mainController.getNativeTypes());
        } catch (SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (InterruptedException e) {
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
        this.mainController.eraseMessage();
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

                this.addFolderController = new AddFolderController(this, mainController, folderIds.get(0), selectedFolderName);
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
        Messagebox.show("Are you sure you want to delete selected item(s)?", "Prompt", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) throws Exception {
                switch (((Integer) evt.getData())) {
                    case Messagebox.YES:
                        MainController mainController = UserSessionManager.getMainController();
                        List<Integer> folderIds = UserSessionManager.getSelectedFolderIds();
                        for (int folderId : folderIds) {
                            mainController.getService().deleteFolder(folderId);
                        }
                        mainController.getMenu().deleteSelectedProcessVersions();
                        mainController.loadWorkspace();
                        break;
                    case Messagebox.NO:
                        break;
                }
            }
        });
    }

    protected void security() throws InterruptedException {
        this.mainController.eraseMessage();
        try {
            this.securitySetupController = new SecuritySetupController(this.mainController);
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
