package org.apromore.portal.common;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.PermissionsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 7:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityFolderTreeRenderer implements TreeitemRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFolderTreeRenderer.class.getName());

    private MainController mainC;
    private PermissionsController permissionsController;

    public SecurityFolderTreeRenderer() {
    }

    public SecurityFolderTreeRenderer(MainController mainController, PermissionsController permissionsController) {
        mainC = mainController;
        this.permissionsController = permissionsController;
    }

    public void setController(MainController mainController, PermissionsController permissionsController) {
        this.mainC = mainController;
        this.permissionsController = permissionsController;
    }

    @Override
    public void render(final Treeitem treeItem, Object treeNode, int i) throws Exception {
        FolderTreeNode ctn = (FolderTreeNode) treeNode;

        Treerow dataRow = new Treerow();
        dataRow.setParent(treeItem);
        treeItem.setValue(ctn);
        treeItem.setOpen(true);

        if (ctn.getType() == FolderTreeNodeTypes.Folder) {
            FolderType folder = (FolderType) ctn.getData();
            if (folder.getParentId() == null || folder.getParentId() == 0 || checkOpenFolderTree(folder, UserSessionManager.getCurrentFolder())) {
                treeItem.setOpen(true);
                if (UserSessionManager.getCurrentFolder() != null && folder.getId().equals(UserSessionManager.getCurrentFolder().getId())) {
                    treeItem.setSelected(true);
                }
            } else {
                treeItem.setOpen(false);
            }
        }

        Hlayout hl = new Hlayout();

        if (ctn.getType() == FolderTreeNodeTypes.Folder) {
            FolderType folder = (FolderType) ctn.getData();
            if (folder.getId() == 0) {
                hl.appendChild(new Image("/img/home-folder24.png"));
            } else {
                hl.appendChild(new Image("/img/folder24.png"));
            }
            String name = folder.getFolderName();
            hl.appendChild(new Label(name.length() > 15 ? name.substring(0, 13) + "..." : name));

        } else if (ctn.getType() == FolderTreeNodeTypes.Process) {
            ProcessSummaryType process = (ProcessSummaryType) ctn.getData();
            hl.appendChild(new Image("/img/process24.png"));
            String name = process.getName();
            hl.appendChild(new Label(name.length() > 15 ? name.substring(0, 13) + "..." : name));
        }

        hl.setSclass("h-inline-block");
        Treecell treeCell = new Treecell();
        treeCell.appendChild(hl);
        dataRow.appendChild(treeCell);

        dataRow.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                FolderTreeNode clickedNodeValue = ((Treeitem) event.getTarget().getParent()).getValue();

                try {
                    int selectedId = 0;
                    boolean hasOwnership = false;
                    if (clickedNodeValue.getType() == FolderTreeNodeTypes.Folder) {
                        FolderType selectedFolder = (FolderType) clickedNodeValue.getData();
                        hasOwnership = selectedFolder.isHasOwnership();
                        selectedId = selectedFolder.getId();
                    } else if (clickedNodeValue.getType() == FolderTreeNodeTypes.Process) {
                        ProcessSummaryType selectedProcess = (ProcessSummaryType) clickedNodeValue.getData();
                        hasOwnership = selectedProcess.isHasOwnership();
                        selectedId = selectedProcess.getId();
                    }

                    UserSessionManager.setCurrentSecurityOwnership(hasOwnership);
                    permissionsController.loadUsers(selectedId, clickedNodeValue.getType());
                } catch (Exception ex) {
                    LOGGER.error("SecurityFolderTree Renderer failed to render an item", ex);
                }
            }
        });
    }


    /* Check the folder tree and make sure we return true if we are looking at a folder that is opened by a user.
 * Could be multiples levels down the tree. */
    private boolean checkOpenFolderTree(FolderType folder, FolderType currentFolder) {
        boolean found = false;
        if (currentFolder != null) {
            if (currentFolder.getId().equals(folder.getId())) {
                found = true;
            }
            if (!found) {
                found = checkDownTheFolderTree(folder.getFolders(), currentFolder);
            }
        }
        return found;
    }


    private boolean checkDownTheFolderTree(List<FolderType> subFolders, FolderType currentFolder) {
        boolean result = false;
        for (FolderType folderType : subFolders) {
            if (folderType.getId().equals(currentFolder.getId())) {
                result = true;
                break;
            }
        }
        if (!result) {
            for (FolderType folderType : subFolders) {
                result = checkDownTheFolderTree(folderType.getFolders(), currentFolder);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }
}
