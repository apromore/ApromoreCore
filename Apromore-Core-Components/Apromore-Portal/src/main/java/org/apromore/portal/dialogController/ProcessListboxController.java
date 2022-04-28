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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalProcessAttributePlugin;
import org.apromore.portal.common.ArtifactOrderTypes;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.renderer.SummaryItemRenderer;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
// import org.apromore.portal.util.SummaryComparator;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.util.ArtifactsComparator;
import org.slf4j.Logger;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listheader;

public class ProcessListboxController extends BaseListboxController {

  private static final long serialVersionUID = -6874531673992239378L;
  private static final Logger LOGGER =
      PortalLoggerFactory.getLogger(ProcessListboxController.class);
  private static final String ARTIFACT_COMPARATOR="ARTIFACT_COMPARATOR";
  private static final String SORT_ASCENDING="ASCENDING";


  public ProcessListboxController(MainController mainController) {
    super(mainController, "~./macros/listbox/processSummaryListbox.zul",
        new SummaryItemRenderer(mainController));

    initializeHeader();

    // Add plugin attributes as additional columns
    for (PortalProcessAttributePlugin plugin : (List<PortalProcessAttributePlugin>) SpringUtil
        .getBean("portalProcessAttributePlugins")) {
      this.getListBox().getListhead().appendChild(plugin.getListheader());
    }

    // TODO should be replaced by ListModel listener in zk 6
    getListBox().addEventListener(Events.ON_SELECT, new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {

        // List the selected folders and processes
        List<Integer> folderIdList = new ArrayList<>();
        List<ProcessSummaryType> processSummaryList = new ArrayList<>();
        List<FolderType> selecteFolderList = new ArrayList<>();
        List<LogSummaryType> logSummaryList = new ArrayList<>();
        for (Object selectedItem : getListModel().getSelection()) {
          if (selectedItem instanceof FolderType) {
            folderIdList.add(((FolderType) selectedItem).getId());
            selecteFolderList.add((FolderType)selectedItem);
          } else if (selectedItem instanceof ProcessSummaryType) {
            processSummaryList.add((ProcessSummaryType) selectedItem);
          } else if (selectedItem instanceof LogSummaryType) {
              logSummaryList.add((LogSummaryType) selectedItem);
            }
        }

        // If there's a unique selected process, show its versions
        if(getListModel().getSelection().size() == 1) {
			if (processSummaryList.size() == 1) {
				getMainController().displayProcessVersions(processSummaryList.get(0));
			} else if (selecteFolderList.size() == 1) {
				getMainController().displayFolderVersions(selecteFolderList.get(0));
			} else if (logSummaryList.size() == 1) {
				getMainController().displayLogVersions(logSummaryList.get(0));
			}
        }else {
          getMainController().clearProcessVersions();
        }

        // Set the selected folders
        // UserSessionManager.setSelectedFolderIds(folderIdList);
        getMainController().getPortalSession().setSelectedFolderIds(folderIdList);
      }
    });
  }

  private void initializeHeader() {
    Listheader columnScore = (Listheader) this.getListBox().getFellow("columnScore");
    columnScore.setVisible(false);

    Listheader columnName = (Listheader) this.getListBox().getFellow("columnName");
    columnName.setSortAscending(new ArtifactsComparator(true, ArtifactOrderTypes.BY_NAME));
    columnName.setSortDescending(new ArtifactsComparator(false, ArtifactOrderTypes.BY_NAME));
    columnName.addEventListener(Events.ON_SORT, this::forwardSortEvent);

    Listheader columnId = (Listheader) this.getListBox().getFellow("columnId");
    columnId.setSortAscending(new ArtifactsComparator(true, ArtifactOrderTypes.BY_ID));
    columnId.setSortDescending(new ArtifactsComparator(false, ArtifactOrderTypes.BY_ID));
    columnId.addEventListener(Events.ON_SORT, this::forwardSortEvent);

    Listheader columnType = (Listheader) this.getListBox().getFellow("columnType");
    columnType.setSortAscending(new ArtifactsComparator(true, ArtifactOrderTypes.BY_TYPE));
    columnType.setSortDescending(new ArtifactsComparator(false, ArtifactOrderTypes.BY_TYPE));
    columnType.addEventListener(Events.ON_SORT, this::forwardSortEvent);

    Listheader columnLastUpdate = (Listheader) this.getListBox().getFellow("columnLastUpdate");
    columnLastUpdate.setSortAscending(new ArtifactsComparator(true, ArtifactOrderTypes.BY_UPDATE_DATE));
    columnLastUpdate.setSortDescending(new ArtifactsComparator(false, ArtifactOrderTypes.BY_UPDATE_DATE));
    columnLastUpdate.addEventListener(Events.ON_SORT, this::forwardSortEvent);

    Listheader columnLastVersion = (Listheader) this.getListBox().getFellow("columnLastVersion");
    columnLastVersion.setSortAscending(new ArtifactsComparator(true, ArtifactOrderTypes.BY_LAST_VERSION));
    columnLastVersion.setSortDescending(new ArtifactsComparator(false, ArtifactOrderTypes.BY_LAST_VERSION));
    columnLastVersion.addEventListener(Events.ON_SORT, this::forwardSortEvent);

    Listheader columnOwner = (Listheader) this.getListBox().getFellow("columnOwner");
    columnOwner.setSortAscending(new ArtifactsComparator(true, ArtifactOrderTypes.BY_OWNER));
    columnOwner.setSortDescending(new ArtifactsComparator(false, ArtifactOrderTypes.BY_OWNER));
    columnOwner.addEventListener(Events.ON_SORT, this::forwardSortEvent);

    Listheader columnCreated = (Listheader) this.getListBox().getFellow("columnCreated");
    columnCreated.setSortAscending(new ArtifactsComparator(true, ArtifactOrderTypes.BY_CREATED_DATE));
    columnCreated.setSortDescending(new ArtifactsComparator(false, ArtifactOrderTypes.BY_CREATED_DATE));
    columnCreated.addEventListener(Events.ON_SORT, this::forwardSortEvent);


  }

  private void forwardSortEvent(Event evt) {
    try {
      SortEvent event=(SortEvent)evt;
      Listheader header = (Listheader) event.getTarget();
      ArtifactsComparator comparator = event.isAscending() ? (ArtifactsComparator) header.getSortAscending() :
          (ArtifactsComparator) header.getSortDescending();
      redrawList(comparator);
    } catch (Exception ex) {
      LOGGER.error("Error in sorting", ex);
    }
  }

  public void redrawList(ArtifactsComparator comparator) {
    setSortingInformationInCookie(comparator);
    Executions.getCurrent().getDesktop().setAttribute(ARTIFACT_COMPARATOR, comparator);
    ListModel<Object> listModel = getListBox().getListModel();

    List<SummaryType> dataProcessSummaryType = new ArrayList<>();
    List<SummaryType> dataLogSummaryType = new ArrayList<>();
    List<FolderType> dataFolderType = new ArrayList<>();

    for (int i = 0; i < listModel.getSize(); i++) {
      Object obj = listModel.getElementAt(i);
      if (obj instanceof ProcessSummaryType) {
        dataProcessSummaryType.add((SummaryType) obj);
      } else if (obj instanceof LogSummaryType) {
        dataLogSummaryType.add((SummaryType) obj);
      } else if (obj instanceof FolderType) {
        dataFolderType.add((FolderType) obj);
      }
    }
    List<Object> data = sortArtifacts(dataFolderType, dataProcessSummaryType, dataLogSummaryType, comparator);
    setModel(data,dataProcessSummaryType.size());
  }

  private void setModel(List<Object> data,int processSummarySize) {
    SummaryListModel model =
        new SummaryListModel(data, processSummarySize);
    getListBox().setModel(model);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apromore.portal.dialogController.BaseListboxController#refreshContent ()
   */
  @Override
  protected void refreshContent() {
    getMainController().reloadSummaries();
  }

  /**
   * Display process versions given in summaries. If isQueryResult this results from a search whose
   * query is versionQ, given processQ
   * 
   * @param subFolders list of folders to display.
   * @param summaries the list of processes to display.
   * @param isQueryResult is this a query result from a search or process.
   */
  @SuppressWarnings("unchecked")
  public void displaySummaries(List<FolderType> subFolders, SummariesType summaries,
      Boolean isQueryResult) {
    // this.columnScore.setVisible(isQueryResult);

    getListBox().clearSelection();
    getListBox().setModel(new ListModelList<>());
    getListModel().setMultiple(true);

    // getListModel().addAll(subFolders);
    getListModel().addAll(summaries.getSummary());
    // if (isQueryResult && getListBox().getItemCount() > 0) {
    // getListBox().getItemAtIndex(0).setStyle(Constants.SELECTED_PROCESS);
    // }
  }

  public SummaryListModel displaySummaries(List<FolderType> subFolders, boolean isQueryResult) {
    // this.columnScore.setVisible(isQueryResult);
    getListBox().clearSelection();

    UserType user = UserSessionManager.getCurrentUser();
    FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();
    SummariesType processSummaries = getMainController().getManagerService().getProcessSummaries(user.getId(),
        currentFolder == null ? 0 : currentFolder.getId(), 0, SummaryListModel.pageSize);

    SummariesType logSummaries = getMainController().getManagerService().getLogSummaries(user.getId(),
        currentFolder == null ? 0 : currentFolder.getId(), 0, SummaryListModel.pageSize);
    ArtifactsComparator comparator = (ArtifactsComparator) Executions.getCurrent().getDesktop().getAttribute(ARTIFACT_COMPARATOR);
    if (comparator == null) {
      comparator = getSortedInformationFromCookie();
      if (comparator == null) {
        comparator = new ArtifactsComparator(true, ArtifactOrderTypes.BY_TYPE);
      }
      setSortingInformationInCookie(comparator);
      Executions.getCurrent().getDesktop().setAttribute(ARTIFACT_COMPARATOR, comparator);
    }


    List<Object> allArtifacts =sortArtifacts(subFolders, processSummaries.getSummary(), logSummaries.getSummary(),comparator);

    SummaryListModel model =
        new SummaryListModel(allArtifacts,
            processSummaries.getSummary().size());

    getListBox().setModel(model);

    if (isQueryResult && getListBox().getItemCount() > 0) {
      getListBox().getItemAtIndex(0).setStyle(Constants.SELECTED_PROCESS);
    }

    return model;
  }

  private void setSortingInformationInCookie(ArtifactsComparator comparator) {
    Clients.evalJavaScript("Ap.common.setCookie('PORTAL_SORTING_TYPE','" + comparator.getArtifactOrder().name() + "')");
    Clients.evalJavaScript("Ap.common.setCookie('PORTAL_SORTING_ORDER','" + (comparator.isAsc()?SORT_ASCENDING:"DESCENDING") + "')");
  }

  private String getCookieValue(String cookieName, Cookie[] cookiesData) {
    if (cookiesData != null && cookieName != null) {
      for (Cookie cookie : cookiesData) {
        if (cookieName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return "";
  }

  private ArtifactsComparator getSortedInformationFromCookie() {
    try {
      Cookie[] cookiesData =
          ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getCookies();
      String sortingType = getCookieValue("PORTAL_SORTING_TYPE", cookiesData);
      String sortingOrder = getCookieValue("PORTAL_SORTING_ORDER", cookiesData);
      if (!sortingType.isEmpty()) {
        return new ArtifactsComparator(SORT_ASCENDING.equals(sortingOrder),
            ArtifactOrderTypes.valueOf(sortingType) != null ? ArtifactOrderTypes.valueOf(sortingType) :
                ArtifactOrderTypes.BY_TYPE);
      }
    } catch (Exception ex) {
      LOGGER.error("Error in retrieving sort information", ex);
    }
    return null;
  }

  private List<Object> sortArtifacts(List<FolderType> subFolders, List<SummaryType> processSummaries,
                                     List<SummaryType> logSummaries, ArtifactsComparator comparator) {
    List<Object> allArtifacts = new ArrayList<>();

    if (comparator == null) {
      comparator = new ArtifactsComparator(true, ArtifactOrderTypes.BY_TYPE); //default
    }

    if (comparator.getArtifactOrder() == ArtifactOrderTypes.BY_TYPE) { // For Type, we are soring individually
      if (comparator.isAsc()) {
        addFolderArtifactsList(allArtifacts, subFolders, comparator, true); //1
        addSummaryToArtifactsList(allArtifacts, processSummaries, comparator, true); //2
        addSummaryToArtifactsList(allArtifacts, logSummaries, comparator, true); //3
      } else {
        addSummaryToArtifactsList(allArtifacts, logSummaries, comparator, true); //3
        addSummaryToArtifactsList(allArtifacts, processSummaries, comparator, true); //2
        addFolderArtifactsList(allArtifacts, subFolders, comparator, true); //1
      }

    } else {
      addFolderArtifactsList(allArtifacts, subFolders, comparator, false); //1
      addSummaryToArtifactsList(allArtifacts, processSummaries, comparator, false); //2
      addSummaryToArtifactsList(allArtifacts, logSummaries, comparator, false); //3
      Collections.sort(allArtifacts, comparator);
    }
    return allArtifacts;
  }

  private void addFolderArtifactsList(List<Object> allArtifacts, List<FolderType> subFolders,
                                      ArtifactsComparator comparator, boolean sortRequired) {
    if (!subFolders.isEmpty()) {
      if (sortRequired) {
        Collections.sort(subFolders, comparator);
      }
      allArtifacts.addAll(subFolders);
    }
  }

  private void addSummaryToArtifactsList(List<Object> allArtifacts, List<SummaryType> summaryTypes,
                                       ArtifactsComparator comparator, boolean sortRequired) {
    if (!summaryTypes.isEmpty()) {
      if (sortRequired) {
        Collections.sort(summaryTypes, comparator);
      }
      allArtifacts.addAll(summaryTypes);
    }
  }



  // public SummaryListModel displayProcessSummaries(List<FolderType> subFolders, boolean
  // isQueryResult) {
  // this.columnScore.setVisible(isQueryResult);
  //
  // getListBox().clearSelection();
  // SummaryListModel model = new SummaryListModel(isQueryResult ?
  // Collections.<FolderType>emptyList() : subFolders);
  //
  // getListBox().setModel(model);
  //
  // if (isQueryResult && getListBox().getItemCount() > 0) {
  // getListBox().getItemAtIndex(0).setStyle(Constants.SELECTED_PROCESS);
  // }
  //
  // return model;
  // }

  /**
   * Lazily loading list of @link{ProcessSummaryType}.
   *
   * @see http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/MVC/Model/List_Model#Huge_Amount_of_Data
   */
  // class SummaryListModel extends ListModelList {
  // final int pageSize = 10; // TODO: ought to be externally configurable
  //
  // private SummariesType summaries;
  // private int currentPageIndex = 0;
  // private List<FolderType> subFolders;
  //
  // /**
  // * Constructor.
  // *
  // * @param subFolders will be displayed before processes
  // */
  // SummaryListModel(List<FolderType> subFolders) {
  // this.subFolders = subFolders;
  // setMultiple(true);
  // }
  //
  // public Object getElementAt(int index) {
  // if (index < subFolders.size()) {
  // return subFolders.get(index);
  // } else {
  // int processIndex = index - subFolders.size();
  // return getSummaries(processIndex / pageSize).getSummary().get(processIndex % pageSize);
  // }
  // }
  //
  // public int getSize() {
  // return subFolders.size() + getSummaries(currentPageIndex).getCount().intValue();
  // }
  //
  // public int getTotalCount() {
  // return getSummaries(currentPageIndex).getTotalCount().intValue();
  // }
  //
  // private SummariesType getSummaries(int pageIndex) {
  // if (summaries == null || currentPageIndex != pageIndex) {
  // UserType user = UserSessionManager.getCurrentUser();
  // FolderType currentFolder = UserSessionManager.getCurrentFolder();
  // summaries = getService().getProcessOrLogSummaries(user.getId(), currentFolder == null ? 0 :
  // currentFolder.getId(), pageIndex, pageSize);
  // currentPageIndex = pageIndex;
  // }
  // return summaries;
  // }
  // }

  /**
   * refresh the display without reloading the data. Keeps selection if any.
   */
  protected void refresh() {
    getListBox().renderAll();
  }

  /**
   * Add the process to the table
   */
  @SuppressWarnings("unchecked")
  public void displayNewProcess(ProcessSummaryType process) {
    // getListModel().add(process); // This will trigger a UiException from ZK do to the additional
    // complexity of paged result fetching

    // FolderType currentFolder = UserSessionManager.getCurrentFolder();
    FolderType currentFolder = getMainController().getPortalSession().getCurrentFolder();
    List<FolderType> subFolders = getMainController().getManagerService().getSubFolders(
        UserSessionManager.getCurrentUser().getId(),
        currentFolder == null ? 0 : currentFolder.getId());
    SummaryListModel model = displaySummaries(subFolders, false);
  }

}
