/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.bpmneditor.viewmodel;

import java.util.List;
import java.util.Map;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.slf4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 * Handles the item render for the Folder Tree list.
 *
 * @author Mohammad Ali
 */
public class SubProcessTreeRenderer implements TreeitemRenderer<FolderTreeNode> {

    private static final Logger LOGGER =
        PortalLoggerFactory.getLogger(SubProcessTreeRenderer.class);

    private Map<FolderTreeNodeTypes, List<Integer>> searchResult;
    private List<Integer> processChainFolder;
    FolderType currentFolder = null;
    ProcessSummaryType processSummaryType;

    public SubProcessTreeRenderer(FolderType currentFolder,ProcessSummaryType processSummaryType,List<Integer> processChainFolder) {
        searchResult = null;
        this.currentFolder = currentFolder;
        this.processSummaryType=processSummaryType;
        this.processChainFolder=processChainFolder;
        publishEvent(processSummaryType);
    }

    public SubProcessTreeRenderer(Map<FolderTreeNodeTypes, List<Integer>> searchResult) {
        this.searchResult = searchResult;
        publishEvent(null);
    }

    private void publishEvent(ProcessSummaryType process) {
        EventQueues.lookup("linkSubProcessControl", EventQueues.DESKTOP, true)
            .publish(new Event("onSelect", null, process));
    }


    @Override
    public void render(final Treeitem treeItem, FolderTreeNode ctn, int i) throws Exception {
        Treerow dataRow = new Treerow();
        dataRow.setParent(treeItem);
        treeItem.setValue(ctn);
        Hlayout hl = new Hlayout();
        switch (ctn.getType()) {
            case Folder:
                renderFolder(treeItem, ctn, dataRow, hl);
                break;
            case Process:
                renderProcess(treeItem, ctn, dataRow, hl);
                break;
            default:
        }

        Treecell treeCell = new Treecell();
        treeCell.appendChild(hl);
        dataRow.appendChild(treeCell);

        dataRow.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                FolderTreeNode clickedNodeValue = ((Treeitem) event.getTarget().getParent()).getValue();

                try {
                    ProcessSummaryType process = null;
                    if (clickedNodeValue.getType().equals(FolderTreeNodeTypes.Process)) {
                        SummaryType summaryType = (SummaryType) clickedNodeValue.getData();
                        if (summaryType instanceof ProcessSummaryType) {
                            process = (ProcessSummaryType) summaryType;
                        }
                    }
                    publishEvent(process);
                } catch (Exception ex) {
                    LOGGER.error("SubprocessTree Renderer failed to render an item", ex);
                }
            }
        });
    }

    private void renderProcess(Treeitem treeItem, FolderTreeNode ctn, Treerow dataRow, Hlayout hl) {
        SummaryType summaryType = (SummaryType) ctn.getData();
        if(this.processSummaryType!=null && this.processSummaryType.getId().equals(summaryType.getId())){
            treeItem.setSelected(true);
            treeItem.setFocus(true);
        }
        hideOrShowTreeItem(treeItem, summaryType.getId(), FolderTreeNodeTypes.Process);
        if (summaryType instanceof ProcessSummaryType) {
            ProcessSummaryType process = (ProcessSummaryType) summaryType;
            hl.appendChild(new Image("~./img/icon/svg/bpmn_model.svg"));
            hl.setSclass("ap-ico-process h-inline-block");
            String processName = process.getName();
            hl.appendChild(new Label(processName));
            dataRow.setSclass("ap-tree-leave");
            dataRow.setTooltiptext(processName);
        }
    }

    private void renderFolder(Treeitem treeItem, FolderTreeNode ctn, Treerow dataRow, Hlayout hl) {
        FolderType folder = (FolderType) ctn.getData();
        hideOrShowTreeItem(treeItem, folder.getId(), FolderTreeNodeTypes.Folder);

        if(this.processChainFolder!=null && this.processChainFolder.contains(folder.getId())){
            treeItem.setOpen(true);
        }

        if (ctn.getChildCount() == 0) {
            dataRow.addSclass("ap-tree-leaf-node");
        }

        if (folder.getId() == 0) {
            hl.appendChild(new Image("~./img/icon/svg/folder_home.svg"));
            hl.setSclass("ap-ico-home h-inline-block");
        } else {
            hl.appendChild(new Image("~./img/icon/svg/folder_icons.svg"));
            hl.setSclass("ap-ico-folder h-inline-block");
        }

        String folderName = folder.getFolderName();
        hl.appendChild(new Label(folderName));
    }

    private void hideOrShowTreeItem(Treeitem treeItem, Integer id, FolderTreeNodeTypes type) {
        if (searchResult == null) {
            return;
        }
        treeItem.setVisible(searchResult.get(type) != null && searchResult.get(type).contains(id));
    }

}
