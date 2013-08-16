package org.apromore.portal.common;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.PermissionsController;
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

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 7:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityFolderTreeRenderer implements TreeitemRenderer {

    private MainController mainC;
    private PermissionsController permissionsController;

    public void setController(MainController mainController, PermissionsController permissionsController){
        this.mainC = mainController;
        this.permissionsController = permissionsController;
    }

    public SecurityFolderTreeRenderer(){

    }

    public SecurityFolderTreeRenderer(MainController mainController, PermissionsController permissionsController){
        this.mainC = mainController;
        this.permissionsController = permissionsController;
    }
    
    @Override
    public void render(final Treeitem treeItem, Object treeNode, int i) throws Exception {
        FolderTreeNode ctn = (FolderTreeNode)treeNode;
                    
        Treerow dataRow = new Treerow();
        dataRow.setParent(treeItem);
        treeItem.setValue(ctn);
        treeItem.setOpen(true);

        Hlayout hl = new Hlayout();
        
        if (ctn.getType() == FolderTreeNodeTypes.Folder){
            FolderType folder = (FolderType)ctn.getData();
            if (folder.getId() == 0){
                hl.appendChild(new Image("/img/home-folder24.png"));
            }
            else{
                hl.appendChild(new Image("/img/folder24.png"));
            }
            String name = folder.getFolderName();
            hl.appendChild(new Label(name.length() > 15 ? name.substring(0, 13) + "..." : name));
        }
        else if (ctn.getType() == FolderTreeNodeTypes.Process){
            ProcessSummaryType process = (ProcessSummaryType)ctn.getData();
            hl.appendChild(new Image("/img/process24.png"));
            String name = process.getName();
            hl.appendChild(new Label(name.length() > 15 ? name.substring(0, 13) + "..." : name));
        }
        hl.setSclass("h-inline-block");
        Treecell treeCell = new Treecell();
        treeCell.appendChild(hl);
        dataRow.appendChild(treeCell);
        dataRow.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                FolderTreeNode clickedNodeValue = (FolderTreeNode)((Treeitem)event.getTarget().getParent()).getValue();

                try{
                    int selectedId = 0;
                    boolean hasOwnership = false;
                    if (clickedNodeValue.getType() == FolderTreeNodeTypes.Folder){
                        FolderType selectedFolder = (FolderType)clickedNodeValue.getData();
                        hasOwnership = selectedFolder.isHasOwnership();
                        selectedId = selectedFolder.getId();
                    }
                    else if (clickedNodeValue.getType() == FolderTreeNodeTypes.Process){
                        ProcessSummaryType selectedProcess = (ProcessSummaryType)clickedNodeValue.getData();
                        hasOwnership = selectedProcess.isHasOwnership();
                        selectedId = selectedProcess.getId();
                    }

                    UserSessionManager.setCurrentSecurityOwnership(hasOwnership);
                    permissionsController.loadUsers(selectedId, clickedNodeValue.getType());
                }
                catch(Exception ex){

                }
            }
        });
    }
}
