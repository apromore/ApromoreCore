package org.apromore.portal.common;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 7:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderTreeRenderer implements TreeitemRenderer {

    private MainController mainC;
    
    public void setController(MainController controller){
        this.mainC = controller;
    }
    
    public FolderTreeRenderer(){

    }

    public FolderTreeRenderer(MainController controller){
        this.mainC = controller;
    }
    
    @Override
    public void render(final Treeitem treeItem, Object treeNode) throws Exception {
        FolderTreeNode ctn = (FolderTreeNode)treeNode;
        FolderType folder = (FolderType)ctn.getData();
        Treerow dataRow = new Treerow();
        dataRow.setParent(treeItem);
        treeItem.setValue(ctn);
        treeItem.setOpen(true);

        Hlayout hl = new Hlayout();
        if (folder.getId() == 0){
            hl.appendChild(new Image("/img/home-folder24.png"));
        }
        else{
            hl.appendChild(new Image("/img/folder24.png"));
        }
        hl.appendChild(new Label(folder.getFolderName()));
        hl.setSclass("h-inline-block");
        Treecell treeCell = new Treecell();
        treeCell.appendChild(hl);
        dataRow.appendChild(treeCell);
        dataRow.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                FolderTreeNode clickedNodeValue = (FolderTreeNode)((Treeitem)event.getTarget().getParent()).getValue();
                FolderType selectedFolder = (FolderType)clickedNodeValue.getData();

                try{
                    int selectedFolderId = selectedFolder.getId();
                    Component currentComponent = event.getTarget().getParent();
                    while (!currentComponent.getId().equalsIgnoreCase("mainW") && currentComponent != null) {
                        currentComponent = currentComponent.getParent();
                    }

                    List<FolderType> breadcrumbFolders = mainC.getService().getBreadcrumbs(UserSessionManager.getCurrentUser().getId(), selectedFolderId);
                    Collections.reverse(breadcrumbFolders);
                    String content = "<table cellspacing='0' cellpadding='5' id='breadCrumbsTable'><tr>";

                    int i = 0;
                    for (FolderType breadcrumb : breadcrumbFolders){
                        if (i > 0){
                            content += "<td style='font-size: 9pt;'>&gt;</td>";
                        }
                        content += "<td><a class='breadCrumbLink' style='cursor: pointer; font-size: 9pt; color: Blue; text-decoration: underline;' id='" + breadcrumb.getId().toString() + "'>" + breadcrumb.getFolderName() + "</a></td>";
                        i++;
                    }

                    content += "</tr></table>";
                    mainC.breadCrumbs.setContent(content);
                    Clients.evalJavaScript("bindBreadcrumbs();");

                    Html html = (Html)currentComponent.getFellow("folders");
                    //Hbox folderOptions = (Hbox)currentComponent.getFellow("folderOptions");
                    //Hbox fileOptions = (Hbox)currentComponent.getFellow("pagingandbuttons");
                    //folderOptions.setVisible(false);
                    //fileOptions.setVisible(false);
                    
                    if (html != null){
                        List<FolderType> availableFolders = mainC.getService().getSubFolders(UserSessionManager.getCurrentUser().getId(), selectedFolderId);
                        //List<ProcessSummaryType> availableProcesses = mainC.getService().getProcesses(UserSessionManager.getCurrentUser().getId(), selectedFolderId);

                        if (selectedFolder.getFolders().size() == 0)
                        for (FolderType folderType : availableFolders){
                            selectedFolder.getFolders().add(folderType);
                        }

                        UserSessionManager.setPreviousFolder(UserSessionManager.getCurrentFolder());
                        UserSessionManager.setCurrentFolder(selectedFolder);

                        mainC.reloadProcessSummaries();
                        //loadWorkspace(html, availableFolders, availableProcesses);
                        //Clients.evalJavaScript("bindTiles();");
                    }
                } catch(Exception ignored){
                }
            }

            public void loadWorkspace(Html html, List<FolderType> folders, List<ProcessSummaryType> processes){
                String content = "<ul class='workspace'>";

                for(FolderType folder : folders){
                    int access = 1;
                    if (folder.isHasWrite()){
                        access = 2;
                    }
                    if (folder.isHasOwnership()){
                        access = 4;
                    }
                    content += String.format("<li class='folder' id='%d' access='%d'><div>%s</div></li>", folder.getId(), access, folder.getFolderName());
                }

                for(ProcessSummaryType process : processes){
                    int access = 1;
                    if (process.isHasWrite()){
                        access = 2;
                    }
                    if (process.isHasOwnership()){
                        access = 4;
                    }
                    content += String.format("<li class='process' id='%d' access='%d'><div>%s</div></li>", process.getId(), access, process.getName().length() > 20 ? process.getName().substring(0, 16) + "" : process.getName());
                }

                content += "</ul>";
                html.setContent(content);

            }
        });
    }
}
