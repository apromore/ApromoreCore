/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.dialogController;

import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.ResourceNotFoundException;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.workspaceOptions.AddFolderController;
import org.apromore.portal.dialogController.workspaceOptions.RenameFolderController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.helper.Version;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.model.ProcessData;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Span;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.event.ColSizeEvent;

public abstract class BaseListboxController extends BaseController {

  private static final long serialVersionUID = -4693075788311730404L;
  private static final Logger LOGGER = PortalLoggerFactory.getLogger(BaseListboxController.class);

  private static final String TILE_VIEW = "tile";
  private static final String LIST_VIEW = "list";
  public static final String APROMORE = "Apromore";
  public static final String ON_CLICK = "onClick";
  public static final String AP_TILES_VIEW = "ap-tiles-view";
  public static final String PORTAL_WARNING_TEXT = "portal_warning_text";

  private final Listbox listBox;

  private final MainController mainController;

  private final Button refreshB;
  private final Button btnUpload;
  private final Button btnDownload;
  private final Button btnCreateDataPipeline;
  private final Button btnManageDataPipelines;
  private final Span btnEtlSep;
  private final Button btnSelectAll;
  private final Button btnSelectNone;
  private final Button btnCut;
  private final Button btnCopy;
  private final Button btnPaste;
  private final Button btnAddFolder;
  private final Button btnRenameFolder;
  private final Button btnRemoveFolder;
  private final Button btnView;
  private final Button btnSecurity;
  private final Button btnUserMgmt;
  private final Button btnShare;
  private final Span btnCalendarSep;
  private final Button btnCalendar;

  private User currentUser;

  private PortalContext portalContext;
  private Map<String, PortalPlugin> portalPluginMap;
  private ArrayList<LogSummaryType> sourceLogs = new ArrayList<>();
  private ArrayList<FolderType> sourceFolders = new ArrayList<>();
  private ArrayList<ProcessSummaryType> sourceProcesses = new ArrayList<>();


  public BaseListboxController(MainController mainController, String componentId,
      ListitemRenderer itemRenderer) {
    super();
    setHflex("100%");
    setVflex("100%");

    this.mainController = mainController;
    this.portalContext = new PluginPortalContext(mainController);
    listBox = createListbox(componentId);
    // listBox.setPaginal((Paging) mainController.getFellow("pg"));
    listBox.setItemRenderer(itemRenderer);

    refreshB = (Button) mainController.getFellow("refreshB");
    btnUpload = (Button) mainController.getFellow("btnUpload");
    btnDownload = (Button) mainController.getFellow("btnDownload");
    btnCreateDataPipeline = (Button) mainController.getFellow("btnCreateDataPipeline");
    btnManageDataPipelines = (Button) mainController.getFellow("btnManageDataPipelines");
    btnEtlSep = (Span) mainController.getFellow("btnEtlSep");
    btnSelectAll = (Button) mainController.getFellow("btnSelectAll");
    btnSelectNone = (Button) mainController.getFellow("btnSelectNone");
    btnCut = (Button) mainController.getFellow("btnCut");
    btnCopy = (Button) mainController.getFellow("btnCopy");
    btnPaste = (Button) mainController.getFellow("btnPaste");
    btnAddFolder = (Button) mainController.getFellow("btnAddFolder");
    btnRenameFolder = (Button) mainController.getFellow("btnRenameFolder");
    btnRemoveFolder = (Button) mainController.getFellow("btnRemoveFolder");
    btnView = (Button) mainController.getFellow("btnView");
    btnSecurity = (Button) mainController.getFellow("btnSecurity");
    btnUserMgmt = (Button) mainController.getFellow("btnUserMgmt");
    btnShare = (Button) mainController.getFellow("btnShare");
    btnCalendarSep = (Span) mainController.getFellow("btnCalendarSep");
    btnCalendar = (Button) mainController.getFellow("btnCalendar");
    portalPluginMap = PortalPluginResolver.getPortalPluginMap();

    attachEvents();

    appendChild(listBox);
    setTileView(TILE_VIEW.equals(getPersistedView()));

    try {
      currentUser = mainController.getSecurityService()
          .getUserById(UserSessionManager.getCurrentUser().getId());
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public void setPersistedView(String view) {
    Clients.evalJavaScript("Ap.common.setCookie('view','" + view + "')");
  }

  public String getPersistedView() {
    Cookie[] cookies =
        ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("view".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  protected void attachEvents() {

    this.listBox.addEventListener(Events.ON_RIGHT_CLICK, new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        if (listBox.getSelectedCount() > 0) {
          unselectAll();
        }
        Map args = new HashMap();
        args.put("POPUP_TYPE", "CANVAS");
        Menupopup menupopup = (Menupopup)Executions.createComponents("~./macros/popupMenu.zul", null, args);
        menupopup.open(event.getTarget(), "at_pointer");
      }
    });

    this.listBox.setDroppable("true");
    this.listBox.addEventListener(Events.ON_DROP, new EventListener<DropEvent>() {
      @Override
      public void onEvent(DropEvent event) throws Exception {
        try {
          FolderType currentFolder = mainController.getPortalSession().getCurrentFolder();
          Set<Object> droppedObjects = new HashSet<>();
          if (event.getDragged() instanceof Listitem) {
            Listitem draggedItem = (Listitem) event.getDragged();
            draggedItem.getListbox().getSelectedItems().stream().map(Listitem::getValue).forEach(value -> {
              droppedObjects.add(value);
            });
            droppedObjects.add(draggedItem.getValue());
          } else if (event.getDragged() instanceof Treerow) {
            FolderTreeNode draggedItem = ((Treeitem) event.getDragged().getParent()).getValue();
            ((Treeitem) event.getDragged().getParent()).getTree().getSelectedItems().stream()
                .map(Treeitem::getValue).forEach(value -> {
                  droppedObjects.add(((FolderTreeNode) value).getData());
                });
            droppedObjects.add(draggedItem.getData());
          }

          if (currentFolder != null && droppedObjects.size() > 0) {
            mainController.getBaseListboxController().drop(currentFolder, droppedObjects,false);
          }
        } catch (Exception e) {
          LOGGER.error("Error Occured in Drag and Drop", e);
        }
      }
    });

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

    this.refreshB.addEventListener(ON_CLICK, (Event event) -> refreshContent());
    this.btnUpload.addEventListener(ON_CLICK, (Event event) -> importFile());
    this.btnDownload.addEventListener(ON_CLICK, (Event event) -> exportFile());

    if (portalPluginMap.containsKey(PluginCatalog.PLUGIN_ETL)) {
      boolean createPipelinePermission = portalContext.getCurrentUser()
              .hasAnyPermission(PermissionType.PIPELINE_CREATE);
      boolean managePipelinesPermission = portalContext.getCurrentUser()
              .hasAnyPermission(PermissionType.PIPELINE_MANAGE);

      btnEtlSep.setVisible(mainController.getConfig().isEnableEtl() &&
              (createPipelinePermission || managePipelinesPermission));
      btnCreateDataPipeline.setVisible(mainController.getConfig().isEnableEtl() && createPipelinePermission);
      btnManageDataPipelines.setVisible(mainController.getConfig().isEnableEtl() && managePipelinesPermission);

      this.btnCreateDataPipeline.addEventListener(ON_CLICK, (Event event) -> openETL());
      this.btnManageDataPipelines.addEventListener(ON_CLICK, (Event event) -> openPipelineManager());
    }

    this.btnSelectAll.addEventListener(ON_CLICK, (Event event) -> selectAll());
    this.btnSelectNone.addEventListener(ON_CLICK, (Event event) -> unselectAll());
    this.btnCut.addEventListener(ON_CLICK, (Event event) -> cut());
    this.btnCopy.addEventListener(ON_CLICK, (Event event) -> copy());
    this.btnPaste.addEventListener(ON_CLICK, (Event event) -> paste());
    this.btnAddFolder.addEventListener(ON_CLICK, (Event event) -> addFolder());
    this.btnRenameFolder.addEventListener(ON_CLICK, (Event event) -> rename());
    this.btnRemoveFolder.addEventListener(ON_CLICK, (Event event) -> removeFolder());
    this.btnView.addEventListener(ON_CLICK, (Event event) -> setTileView(LIST_VIEW.equals(getPersistedView())));
    this.btnSecurity.addEventListener(ON_CLICK, (Event event) -> security());
    this.btnSecurity.setVisible(portalContext.getCurrentUser().hasAnyPermission(PermissionType.ACCESS_RIGHTS_MANAGE));

    if (portalContext.getCurrentUser().hasAnyPermission(PermissionType.USERS_EDIT)) {
      this.btnUserMgmt.addEventListener(ON_CLICK, new EventListener<Event>() {
        @Override
        public void onEvent(Event event) throws Exception {
          userMgmt();
        }
      });
      this.btnUserMgmt.setVisible(true);
    } else {
      this.btnUserMgmt.setVisible(false);
    }

    this.btnShare.addEventListener(ON_CLICK, (Event event) -> share());

    boolean calendarPermission = portalContext.getCurrentUser()
            .hasAnyPermission(PermissionType.CALENDAR);
    this.btnCalendarSep.setVisible(mainController.getConfig().isEnableCalendar() && calendarPermission);
    this.btnCalendar.setVisible(mainController.getConfig().isEnableCalendar() && calendarPermission);
    this.btnCalendar.addEventListener(ON_CLICK, new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {

        Set<Object> selections = getSelection();

        if (selections.size() != 1
            || !selections.iterator().next().getClass().equals(LogSummaryType.class)) {
          Notification.error(Labels.getLabel("portal_selectOneLog_message"));
          return;
        }

        LogSummaryType selectedItem = (LogSummaryType) selections.iterator().next();
        launchCalendar(selectedItem.getName(), selectedItem.getId());
      }
    });

    this.listBox.getListhead().addEventListener("onColSize", new EventListener<ColSizeEvent>() {
      @Override
      public void onEvent(ColSizeEvent event) throws Exception {
        try {
          int widthSetByClient = Integer.parseInt((event.getWidth().substring(0,event.getWidth().indexOf("px"))));
          int newWidth = 0;
          Listheader listHeader = (Listheader) event.getColumn();
          if (event.getColIndex() == 0) {
            newWidth = Math.max(widthSetByClient, 40);
          } else {
            newWidth = Math.max(widthSetByClient, 60);
          }

          listHeader.setWidth(newWidth + "px");
        } catch (Exception ex) {
          LOGGER.error("Error in resize",ex);
        }
      }
    });

  }

  public void setTileView(boolean tileOn) {
    Listhead listHead = (Listhead) this.listBox.query(".ap-listbox-process-head");
    String sclass = Objects.requireNonNull(this.listBox.getSclass(), "");
    if (tileOn) {
      if (!sclass.contains(AP_TILES_VIEW)) {
        this.listBox.setSclass(sclass.trim() + " ap-tiles-view");
      }
      if (listHead != null) {
        listHead.setVisible(false);
      }
      toggleComponentSclass(btnView, true, "ap-icon-tiles", "ap-icon-list");
      setPersistedView(TILE_VIEW);
      btnView.setTooltiptext(Labels.getLabel("portal_viewList_hint"));
    } else {
      if (sclass.contains(AP_TILES_VIEW)) {
        this.listBox.setSclass(sclass.replace(AP_TILES_VIEW, ""));
      }
      if (listHead != null) {
        listHead.setVisible(true);
      }
      toggleComponentSclass(btnView, false, "ap-icon-tiles", "ap-icon-list");
      setPersistedView(LIST_VIEW);
      btnView.setTooltiptext(Labels.getLabel("portal_viewTile_hint"));
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
    getListModel().clearSelection();
    getMainController().clearProcessVersions();
  }

  public void selectAll() {
    ListModelList model = getListModel();
    for (int i = 0; i < model.getSize(); i++) {
      Object obj = model.getElementAt(i);
      getListModel().addToSelection(obj);
    }
    getMainController().clearProcessVersions();
  }

  protected void importFile() throws InterruptedException {
    getMainController().eraseMessage();
    FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();

    boolean canChange = currentFolder == null || currentFolder.getId() == 0 ? true : false;
    try {
      canChange = canChange || ItemHelpers.canModify(currentUser, currentFolder);;
    } catch (Exception e) {
      Notification.error(e.getMessage());
      return;
    }
    if (canChange) {
      try {
        new ImportController(getMainController());
      } catch (DialogException e) {
        Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
      }
    } else {
      Notification.error(Labels.getLabel("portal_noUploadInReadOnly_message"));
    }
  }

  protected void exportFile() throws Exception {
    PortalPlugin downloadPlugin;

    try {
      downloadPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_DOWNLOAD);
      downloadPlugin.execute(portalContext);
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  protected void openETL() throws Exception {
    PortalPlugin etlPlugin;

    try {
      etlPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_ETL);
      etlPlugin.execute(portalContext);
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  protected void openPipelineManager() throws Exception {
    PortalPlugin pipelineManager;

    try {
      pipelineManager = portalPluginMap.get(PluginCatalog.PLUGIN_JOB_SCHEDULER);
      pipelineManager.execute(portalContext);
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  protected void addFolder() throws InterruptedException {
    FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();
    getMainController().eraseMessage();
    try {
      new AddFolderController(getMainController(), currentUser, currentFolder);
    } catch (DialogException e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
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
        List<FolderType> availableFolders =
            getMainController().getPortalSession().getCurrentFolder() == null
                || getMainController().getPortalSession().getCurrentFolder().getId() == 0
                    ? getMainController().getPortalSession().getTree()
                    : getMainController().getPortalSession().getCurrentFolder().getFolders();
        for (FolderType folder : availableFolders) {
          if (folder.getId() == selectedFolderId) {
            selectedFolderName = folder.getFolderName();
            break;
          }
        }
        new RenameFolderController(getMainController(), folderIds.get(0), selectedFolderName);
      } else if (folderIds.size() > 1) {
        Notification.error(Labels.getLabel("portal_noMultipleRename_message"));
      }
    } catch (DialogException e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  protected void renameLogOrProcess() throws DialogException {
    PortalPlugin editSelectionMetadataPlugin;

    getMainController().eraseMessage();
    try {
      editSelectionMetadataPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_RENAME);
      editSelectionMetadataPlugin.execute(portalContext);
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  public boolean isSingleFileSelected() {
    return getSelectionCount() == 1;
  }

  public void rename() throws InterruptedException {
    try {
      if (!isSingleFileSelected()) {
        Notification.error(Labels.getLabel("portal_selectOneItemRename_message"));
        return;
      }
      Object selectedItem = getSelection().iterator().next();

      boolean canChange = false;
      try {
        canChange = ItemHelpers.canModify(currentUser, selectedItem);
      } catch (Exception e) {
        Notification.error(e.getMessage());
        return;
      }

      if (canChange) {
        List<Integer> folderIds = getMainController().getPortalSession().getSelectedFolderIds();
        if (folderIds.size() == 0) {
          renameLogOrProcess();
        } else {
          renameFolder();
        }
      } else {
        Notification.error(Labels.getLabel("portal_noPrivilegeRename_message"));
      }

    } catch (DialogException e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  private boolean validateNotFolderTypeItem(Object selectedItem) {
    if (selectedItem instanceof FolderType) {
      Notification.error(Labels.getLabel("portal_onlyShareLogOrModel_message"));
      return false;
    }
    return true;
  }

  protected void removeFolder() throws Exception {
    ArrayList<FolderType> folders = getSelectedFolders();
    Map<SummaryType, List<VersionSummaryType>> elements =
        getMainController().getSelectedElementsAndVersions();

    if (!doesSelectionExist(folders, elements)) {
      Notification.error(Labels.getLabel("portal_resourceAlreadyDeleted_message"));
      return;
    }
    // See if the user has mixed folders and process models. we handle everything
    // differently.
    if( !(getMainController().getManagerService().hasWritePermission(UserSessionManager.getCurrentUser().getUsername(),new ArrayList<>(getSelection())))) {
        Notification.error(Labels.getLabel("portal_deleteItemRestricted_message"));
        return;
    }

    if (doesSelectionContainFoldersAndElements(folders, elements)) { // mixed
      showMessageFoldersAndElementsDelete(getMainController(), folders);
    } else {
      if (!folders.isEmpty()) { // folder only
        showMessageFolderDelete(getMainController(), folders);
      } else if (!elements.isEmpty()) { // processes and logs
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
        FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();

        if (this.mainController.getCopyPasteController().cut(getSelection(), getSelectionCount(), currentFolder)) {
            getListBox().getItems().stream().forEach(item -> {
                item.removeSclass("ap-item-cut-selected");
            });

            getListBox().getSelectedItems().stream().forEach(item -> {
                item.setSclass("ap-item-cut-selected");
            });
        }
  }

  public void copy() {
    FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();

    this.mainController.getCopyPasteController().copy(getSelection(), getSelectionCount(), currentFolder);
  }

  public void paste() throws Exception {
    FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();

    this.mainController.getCopyPasteController().paste(currentFolder);
    refreshWorkspace();
  }

  public void paste(FolderType currentFolder) throws Exception {
    this.mainController.getCopyPasteController().paste(currentFolder);
    refreshWorkspace();
  }
  private void refreshWorkspace(){
    mainController.reloadSummariesWithOpenTreeItems(mainController.getNavigationController().getAllOpenFolderItems());
  }
  public void drop(FolderType dropToFolder,Set<Object> dropObjects,boolean droppedToTree) throws Exception {
    if (dropObjects.stream().anyMatch(dropObject -> {
      return (dropObject instanceof FolderType && dropToFolder.getId().equals(((FolderType) dropObject).getId()));
    })) {
      Notification.error(Labels.getLabel("portal_source_destination_folder_notsame_message"));
      return;
    }
    FolderType currentFolder = this.mainController.getPortalSession().getCurrentFolder();
    if(currentFolder.getId().equals(dropToFolder.getId()) && !droppedToTree){
      return;
    }
    this.mainController.getPortalSession().setCurrentFolder(dropToFolder);
    this.mainController.getCopyPasteController().drop(dropObjects, dropObjects.size(), dropToFolder);
    this.mainController.getPortalSession().setCurrentFolder(currentFolder);
    refreshWorkspace();
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
    Messagebox.show(Labels.getLabel("portal_deleteModelPrompt_message"),
        Labels.getLabel(PORTAL_WARNING_TEXT), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
        new EventListener<Event>() {
          @Override
          public void onEvent(Event evt) throws Exception {
            switch (((Integer) evt.getData())) {
              case Messagebox.YES:
                deleteElements(mainController);
                refreshWorkspace();
                break;
              case Messagebox.NO:
                break;
              default:
            }
          }
        });
  }

  /* Show the message tailored to deleting log model. */
  private void showMessageLogsDelete(final MainController mainController) throws Exception {
    Messagebox.show(Labels.getLabel("portal_deleteLogPrompt_message"),
        Labels.getLabel(PORTAL_WARNING_TEXT), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
        new EventListener<Event>() {
          @Override
          public void onEvent(Event evt) throws Exception {
            switch (((Integer) evt.getData())) {
              case Messagebox.YES:
                deleteElements(mainController);
                refreshWorkspace();
                break;
              case Messagebox.NO:
                break;
              default:
            }
          }
        });
  }

  /* Show a message tailored to deleting a combo of folders and processes */
  private void showMessageElementsDelete(final MainController mainController) throws Exception {
    Messagebox.show(Labels.getLabel("portal_deleteMixedPrompt_message"),
        Labels.getLabel(PORTAL_WARNING_TEXT), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
        new EventListener<Event>() {
          @Override
          public void onEvent(Event evt) throws Exception {
            switch (((Integer) evt.getData())) {
              case Messagebox.YES:
                deleteElements(mainController);
                refreshWorkspace();
                break;
              case Messagebox.NO:
                break;
              default:
            }
          }
        });
  }

  /* Show the message tailored to deleting one or more folders. */
  private void showMessageFolderDelete(final MainController mainController,
      final ArrayList<FolderType> folders) throws Exception {
    Messagebox.show(Labels.getLabel("portal_deleteFolderPrompt_message"),
        Labels.getLabel(PORTAL_WARNING_TEXT), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
        new EventListener<Event>() {
          @Override
          public void onEvent(Event evt) throws Exception {
            switch (((Integer) evt.getData())) {
              case Messagebox.YES:
                deleteFolders(folders, mainController);
                refreshWorkspace();
                break;
              case Messagebox.NO:
                break;
              default:
            }
          }
        });
  }

  /* Show a message tailored to deleting a combo of folders and processes */
  private void showMessageFoldersAndElementsDelete(final MainController mainController,
      final ArrayList<FolderType> folders) throws Exception {
    Messagebox.show(Labels.getLabel("portal_deleteMixedPrompt_message"),
        Labels.getLabel(PORTAL_WARNING_TEXT), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
        new EventListener<Event>() {
          @Override
          public void onEvent(Event evt) throws Exception {
            switch (((Integer) evt.getData())) {
              case Messagebox.YES:
                deleteFolders(folders, mainController);
                deleteElements(mainController);
                refreshWorkspace();
                break;
              case Messagebox.NO:
                break;
              default:
            }
          }
        });
  }

  protected void userMgmt() throws InterruptedException {
    PortalPlugin userMgmtPlugin;

    getMainController().eraseMessage();
    try {
      userMgmtPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_USER_ADMIN);
      userMgmtPlugin.execute(portalContext);
    } catch (Exception e) {
      LOGGER.error(Labels.getLabel("portal_failedLaunchUserAdmin_message"), e);
      Messagebox.show(Labels.getLabel("portal_failedLaunchUserAdmin_message"));
    }
  }

  /* Setup the Security controller. */
  protected void security() throws InterruptedException {
    PortalPlugin accessControlPlugin;
    Object selectedItem;

    getMainController().eraseMessage();
    try {
      if (getSelectionCount() == 0 || getSelectionCount() > 1) {
        selectedItem = null;
      } else {
        selectedItem = getSelection().iterator().next();
      }
      accessControlPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_ACCESS_CONTROL);
      Map arg = new HashMap<>();
      arg.put("withFolderTree", true);
      arg.put("selectedItem", selectedItem);
      arg.put("currentUser", UserSessionManager.getCurrentUser()); // UserType
      arg.put("autoInherit", true);
      arg.put("showRelatedArtifacts", true);
      arg.put("enablePublish", getMainController().getConfig().isEnablePublish());
      accessControlPlugin.setSimpleParams(arg);
      accessControlPlugin.execute(portalContext);
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  /**
   * Share folder/log/process model
   */
  protected void share() {
    PortalPlugin accessControlPlugin;

    getMainController().eraseMessage();
    // Check for ownership is moved to plugin level
    try {
      if (getSelectionCount() == 0) {
        Notification.error(Labels.getLabel("portal_selectOneLogOrModel_message"));
        return;
      } else if (getSelectionCount() > 1) {
        Notification.error(Labels.getLabel("portal_noMultipleShare_message"));
        return;
      }

      FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();
      accessControlPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_ACCESS_CONTROL);
      Map arg = new HashMap<>();
      if (getSelectionCount() == 1) {
        Object selectedItem = getSelection().iterator().next();
        arg.put("withFolderTree", false);
        arg.put("selectedItem", selectedItem);
      } else {
        arg.put("withFolderTree", false);
        arg.put("selectedItem", currentFolder);
      }

      arg.put("currentUser", UserSessionManager.getCurrentUser()); // UserType
      arg.put("autoInherit", true);
      arg.put("showRelatedArtifacts", true);
      arg.put("enablePublish", getMainController().getConfig().isEnablePublish());
      arg.put("enableUsersList", getMainController().getConfig().isEnableUsersList());
      accessControlPlugin.setSimpleParams(arg);
      accessControlPlugin.execute(portalContext);
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  public void launchCalendar(String artifactName, Integer logId) {
    PortalPlugin calendarPlugin;
    Long calendarId = getMainController().getEventLogService().getCalendarIdFromLog(logId);

    try {
      Map<String, Object> attrMap = new HashMap<>();
      attrMap.put("portalContext", portalContext);
      attrMap.put("artifactName", artifactName);
      attrMap.put("logId", logId);
      attrMap.put("calendarId", calendarId);
      calendarPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_CALENDAR);
      calendarPlugin.setSimpleParams(attrMap);
      calendarPlugin.execute(portalContext);
    } catch (Exception e) {
      LOGGER.error(Labels.getLabel("portal_failedLaunchCustomCalendar_message"), e);
      Messagebox.show(Labels.getLabel("portal_failedLaunchCustomCalendar_message"));
    }
  }

  /*
   * Removes all the selected processes, either the select version or the latest if no version is
   * selected.
   */
  private void deleteElements(MainController mainController) throws Exception {
    // mainController.getMenu().deleteSelectedElements();

    this.mainController.eraseMessage();
    Map<SummaryType, List<VersionSummaryType>> elements =
        mainController.getSelectedElementsAndVersions();
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
        mainController.getManagerService().deleteFolder(folderId.getId(),
            UserSessionManager.getCurrentUser().getUsername());
      } catch (Exception e) {
        failures += 1;
      }
    }
    if (failures > 0) {
      Messagebox.show(
          "Could not perform all delete operations. You may not be authorized to delete some of the resources.",
          APROMORE, Messagebox.OK, Messagebox.ERROR);
    }
  }

  public abstract void displaySummaries(List<FolderType> subFolders, SummariesType summaries,
      Boolean isQueryResult);

  public abstract SummaryListModel displaySummaries(List<FolderType> subFolders,
      boolean isQueryResult);

  /* Does the selection in the main detail list contain folders and processes. */
  private boolean doesSelectionContainFoldersAndElements(ArrayList<FolderType> folders,
      Map<SummaryType, List<VersionSummaryType>> elements) throws Exception {
    return (folders != null && !folders.isEmpty()) && (elements != null && !elements.isEmpty());
  }

  /* Does the selection in the main detail list exist. */
  private boolean doesSelectionExist(
      ArrayList<FolderType> folders,
      Map<SummaryType, List<VersionSummaryType>> elements
  ) throws Exception {

    for (FolderType folder: folders) {
      Folder f = getMainController().getWorkspaceService().getFolder(folder.getId());
      if (f == null) {
        return false;
      }
    }
    for (Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
      Integer id = entry.getKey().getId();
      if (entry.getKey() instanceof ProcessSummaryType) {
        try {
          getMainController().getProcessService().getProcessById(id);
        } catch (RepositoryException e) {
          return false;
        }
      } else if (entry.getKey() instanceof LogSummaryType) {
        Log log = getMainController().getEventLogService().findLogById(id);
        if (log == null) {
          return false;
        }
      } else {
        throw new ResourceNotFoundException("Resource not found: " + entry.getKey());
      }
    }
    return true;
  }

  public MainController getMainController() {
    return mainController;
  }

  public class SummaryListModel extends ListModelList {
    // @todo: ought to be externally configurable
    // Need refactoring, for now assume paging is not used and items < 10000 per view
    static final int pageSize = 10000;
    private int totalProcessSummary=0;
    private transient List<Object> objectList;

    /**
     * Constructor.
     *
     * @param objectList will be displayed before processes
     */

    SummaryListModel(List<Object> objectList,int totalProcessSummary) {
      this.objectList = objectList;
      this.totalProcessSummary=totalProcessSummary;
      setMultiple(true);
    }
    public int getTotalCount() {
      return totalProcessSummary;
    }

    @Override
    public Object getElementAt(int index) {
      return objectList.get(index); //
    }

    @Override
    public int getSize() {
      return objectList.size();
    }
  }
}
