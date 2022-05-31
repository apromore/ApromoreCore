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

package org.apromore.plugin.portal.accesscontrol.renderer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.accesscontrol.controllers.SecuritySetupController;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.GroupAccessType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.service.UserService;
import org.slf4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;
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
 * @author Igor
 */
public class SecurityFolderTreeRenderer implements TreeitemRenderer {

  private static final Logger LOGGER =
      PortalLoggerFactory.getLogger(SecurityFolderTreeRenderer.class);

  private SecuritySetupController securitySetupController;
  private Map<FolderTreeNodeTypes, List<Integer>> searchResult=null;

  public SecurityFolderTreeRenderer(SecuritySetupController securitySetupController) {
    this.securitySetupController = securitySetupController;
    searchResult=null;
  }

  public SecurityFolderTreeRenderer(SecuritySetupController securitySetupController,Map<FolderTreeNodeTypes, List<Integer>> searchResult) {
    this.securitySetupController = securitySetupController;
    this.searchResult=searchResult;
    EventQueues.lookup("accessControl", EventQueues.DESKTOP, true)
        .publish(new Event("onSelect", null, null));
  }

  /*
   * private SecurityPermissionsController permissionsController;
   * 
   * public SecurityFolderTreeRenderer() { }
   * 
   * public SecurityFolderTreeRenderer(SecurityPermissionsController permissionsController) {
   * this.permissionsController = permissionsController; }
   * 
   * public void setController(SecurityPermissionsController permissionsController) {
   * this.permissionsController = permissionsController; }
   */

  @Override
  public void render(final Treeitem treeItem, Object treeNode, int i) throws Exception {
    FolderTreeNode ctn = (FolderTreeNode) treeNode;

    Treerow dataRow = new Treerow();
    dataRow.setParent(treeItem);
    treeItem.setValue(ctn);
    treeItem.setOpen(true);

    Hlayout hl = new Hlayout();

    SummaryType summaryType;

    switch (ctn.getType()) {
      case Folder:
        FolderType folder = (FolderType) ctn.getData();
        hideOrShow(treeItem,folder.getId(),FolderTreeNodeTypes.Folder);
        FolderType currentFolder =
            this.securitySetupController.getMainController().getPortalSession().getCurrentFolder();

        if (folder.getId() == 0 || checkOpenFolderTree(folder, currentFolder)) {
          treeItem.setOpen(true);
          if (currentFolder != null && folder.getId().equals(currentFolder.getId())) {
            treeItem.setSelected(true);
          }
        } else {
          treeItem.setOpen(false);
        }

        if (ctn.getChildCount()==0 ) {
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
        // hl.appendChild(new Label(folderName.length() > 15 ? folderName.substring(0, 13) + "..." :
        // folderName));
        hl.appendChild(new Label(folderName));
        break;

      case Process:
        summaryType = (SummaryType) ctn.getData();
        hideOrShow(treeItem,summaryType.getId(),FolderTreeNodeTypes.Process);
        if (summaryType instanceof ProcessSummaryType) {
          ProcessSummaryType process = (ProcessSummaryType) summaryType;
          hl.appendChild(new Image("~./img/icon/svg/bpmn_model.svg"));
          hl.setSclass("ap-ico-process h-inline-block");
          String processName = process.getName();
          // hl.appendChild(new Label(processName.length() > 15 ? processName.substring(0, 13) +
          // "..." : processName));
          hl.appendChild(new Label(processName));
          dataRow.setSclass("ap-tree-leave");
          dataRow.setTooltiptext(processName);
        }
        break;

      case Log:
        summaryType = (SummaryType) ctn.getData();
        hideOrShow(treeItem,summaryType.getId(),FolderTreeNodeTypes.Log);
        if (summaryType instanceof LogSummaryType) {
          LogSummaryType log = (LogSummaryType) summaryType;
          hl.appendChild(new Image("~./img/icon/svg/log_icon.svg"));
          hl.setSclass("ap-ico-log h-inline-block");
          String processName = log.getName();
          // hl.appendChild(new Label(processName.length() > 15 ? processName.substring(0, 13) +
          // "..." : processName));
          hl.appendChild(new Label(processName));
          dataRow.setSclass("ap-tree-leave");
          dataRow.setTooltiptext(processName);
        }
        break;

      default:
        assert false : "Folder tree node with type " + ctn.getType() + " is not implemented";
    }

    // hl.setSclass("h-inline-block");
    Treecell treeCell = new Treecell();
    treeCell.appendChild(hl);
    dataRow.appendChild(treeCell);

    dataRow.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        FolderTreeNode clickedNodeValue = ((Treeitem) event.getTarget().getParent()).getValue();

        try {
          int selectedId = 0;
          List<GroupAccessType> groups = Collections.emptyList();
          Object selectedItem = null;

          ManagerService service = (ManagerService) SpringUtil.getBean("managerClient");
          switch (clickedNodeValue.getType()) {
            case Folder:
              FolderType selectedFolder = (FolderType) clickedNodeValue.getData();
              selectedId = selectedFolder.getId();
              if (selectedFolder.getId() == 0) {
                // Nobody can edit the permissions of the root folder "Home"
              } else {
                groups = service.getFolderGroups(selectedId);
              }
              selectedItem = selectedFolder;
              break;

            case Process:
              SummaryType summaryType = (SummaryType) clickedNodeValue.getData();
              if (summaryType instanceof ProcessSummaryType) {
                ProcessSummaryType process = (ProcessSummaryType) summaryType;
                selectedId = process.getId();
                selectedItem = process;
                groups = service.getProcessGroups(selectedId);
              }
              break;

            case Log:
              SummaryType lsummaryType = (SummaryType) clickedNodeValue.getData();
              if (lsummaryType instanceof LogSummaryType) {
                LogSummaryType log = (LogSummaryType) lsummaryType;
                selectedId = log.getId();
                selectedItem = log;
                groups = service.getLogGroups(selectedId);
              }
              break;

            default:
              assert false : "Clicked tree node with type " + clickedNodeValue.getType()
                  + " is not implemented";
          }
          boolean hasOwnership = currentUserHasOwnership(groups);
          UserSessionManager.setCurrentSecurityOwnership(hasOwnership);
          UserSessionManager.setCurrentSecurityItem(selectedId);
          UserSessionManager.setCurrentSecurityType(clickedNodeValue.getType());
          EventQueues.lookup("accessControl", EventQueues.DESKTOP, true)
              .publish(new Event("onSelect", null, selectedItem));
        } catch (Exception ex) {
          LOGGER.error("SecurityFolderTree Renderer failed to render an item", ex);
        }
      }
    });
  }

  private void hideOrShow(Treeitem treeItem, Integer id, FolderTreeNodeTypes type) {
    if (searchResult == null) {
      return;
    }
    treeItem.setVisible(searchResult.get(type) != null && searchResult.get(type).contains(id));
  }


  /**
   * @param groups permission groups (typically for a folder, log, or process model)
   * @return whether the <var>groups</var> grant ownership to the current user
   */
  private boolean currentUserHasOwnership(List<GroupAccessType> groups) {
    try {
      UserService userService = (UserService) SpringUtil.getBean("userService");
      User user = userService.findUserByLogin(UserSessionManager.getCurrentUser().getUsername());
      for (final GroupAccessType group : groups) {
        if (group.isHasOwnership()) {
          for (final Group userGroup : user.getGroups()) {
            if (userGroup.getName().equals(group.getName())) {
              return true;
            }
          }
        }
      }

    } catch (UserNotFoundException e) {
      LOGGER.error("Unrecognized current user", e);
    }

    return false;
  }


  /**
   * Check the folder tree and make sure we return true if we are looking at a folder that is opened
   * by a user. Could be multiple levels down the tree.
   *
   * @param folder a folder to search for
   * @param currentFolder the root of a folder tree to search within
   * @return whether the <var>folder</var> is present within the folder tree rooted at
   *         <var>currentFolder</var>
   */
  private boolean checkOpenFolderTree(FolderType folder, FolderType currentFolder) {
    return checkDownTheFolderTree(Collections.singletonList(folder), currentFolder);
  }


  private boolean checkDownTheFolderTree(List<FolderType> subFolders, FolderType currentFolder) {
    for (FolderType folderType : subFolders) {
      if (folderType.getId().equals(currentFolder.getId())) {
        return true;
      }
    }
    for (FolderType folderType : subFolders) {
      if (checkDownTheFolderTree(folderType.getFolders(), currentFolder)) {
        return true;
      }
    }
    return false;
  }
}
