package org.apromore.portal.dialogController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.model.FolderType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.workspaceOptions.AddFolderController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;

public abstract class BaseListboxController extends BaseController {

    private static final long serialVersionUID = -4693075788311730404L;

    private final Listbox listBox;

    private final MainController mainController; // the main controller
    private AddFolderController addFolderController;
    private CreateProcessController createController;
    private SecuritySetupController securitySetupController;

    private final Paging pg;

    private final Button revertSelectionB;
    private final Button selectAllB;
    private final Button unselectAllB;
    private final Button refreshB;
    private final Button btnAddFolder;
    private final Button btnAddProcess;
    private final Button btnRenameFolder;
    private final Button btnRemoveFolder;
    private final Button btnSecurity;


    public BaseListboxController(MainController mainController, String componentId, ListitemRenderer itemRenderer) {
        super();
        setHflex("true");
        setVflex("true");

        this.mainController = mainController;

        this.listBox = createListbox(componentId);

        this.pg = (Paging) mainController.getFellow("pg");
        getListBox().setPaginal(pg);

        this.revertSelectionB = (Button) mainController.getFellow("revertSelectionB");
        this.unselectAllB = (Button) mainController.getFellow("unselectAllB");
        this.selectAllB = (Button) mainController.getFellow("selectAllB");
        this.refreshB = (Button) mainController.getFellow("refreshB");
        this.btnAddFolder = (Button) mainController.getFellow("btnAddFolder");
        this.btnAddProcess = (Button) mainController.getFellow("btnAddProcess");
        this.btnRenameFolder = (Button) mainController.getFellow("btnRenameFolder");
        this.btnRemoveFolder = (Button) mainController.getFellow("btnRemoveFolder");
        this.btnSecurity = (Button) mainController.getFellow("btnSecurity");

        getListBox().setItemRenderer(itemRenderer);
        getListBox().setModel(new ListModelList());

        attachEvents();

        appendChild(listBox);
    }

    protected void attachEvents() {
        this.revertSelectionB.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                revertSelection();
            }
        });

        this.selectAllB.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                selectAll();
            }
        });

        this.unselectAllB.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                unselectAll();
            }
        });

        this.refreshB.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                refreshContent();
            }
        });

        this.btnAddFolder.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                addFolder();
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void revertSelection() {
        Set selectedItems = getListBox().getSelectedItems();
        Set reveredSet = new HashSet();
        for (Object obj : getListBox().getItems()) {
            if (!selectedItems.contains(obj)) {
                reveredSet.add(obj);
            }
        }
        getListBox().clearSelection();
        getListBox().setSelectedItems(reveredSet);
    }

    protected void addFolder() throws InterruptedException {
        this.mainController.eraseMessage();
        try {
            this.addFolderController = new AddFolderController(mainController, 0, "");
        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void addProcess() throws InterruptedException {
        this.mainController.eraseMessage();
        try {
            this.createController = new CreateProcessController(this.mainController, this.mainController.getNativeTypes());
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

                this.addFolderController = new AddFolderController(mainController, folderIds.get(0), selectedFolderName);
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

    public MainController getMainController() {
        return mainController;
    }

}
