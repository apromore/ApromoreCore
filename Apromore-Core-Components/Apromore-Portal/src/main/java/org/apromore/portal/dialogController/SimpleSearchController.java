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

package org.apromore.portal.dialogController;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;

import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.mapper.SearchHistoryMapper;
import org.apromore.mapper.UserMapper;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionDao;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.SearchHistoriesType;
import org.apromore.portal.model.SummariesType;
import org.apromore.service.SecurityService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.search.SearchExpressionBuilder;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Span;
import org.zkoss.zul.Window;

public class SimpleSearchController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(SimpleSearchController.class);
    public static final String ON_CLICK = "onClick";

    private MainController mainController;
    private Combobox previousSearchesCB;
    private Span clearSearchBtn;

    public SimpleSearchController(MainController mainController, Component mainControllerComponent)
            throws UnsupportedEncodingException, ExceptionDao, JAXBException {
        this.mainController = mainController;

        Window simpleSearchW =
                (Window) this.mainController.getFellow("simplesearchcomp").getFellow("simplesearchwindow");
        Hbox previousSearchesH = (Hbox) simpleSearchW.getFellow("previoussearcheshbox");
        Button simpleSearchesBu = (Button) previousSearchesH.getFellow("previoussearchesbutton");
        previousSearchesCB = (Combobox) previousSearchesH.getFellow("previoussearchescombobox");
        clearSearchBtn = (Span) previousSearchesH.getFellow("clearSearch");
        Span doSearchBtn = (Span) previousSearchesH.getFellow("doSearch");

        refreshSearch("");
        setVisibility(clearSearchBtn, false);

        doSearchBtn.addEventListener(ON_CLICK, event -> processSearch());
        clearSearchBtn.addEventListener(ON_CLICK, event -> {
            clearSearches();
            mainController.reloadSummaries();
            setVisibility(clearSearchBtn, false);
        });
        simpleSearchesBu.addEventListener(ON_CLICK, event -> processSearch());
        simpleSearchW.addEventListener("onOK", event -> processSearch());
        previousSearchesCB.addEventListener("onOK", event -> processSearch());
        previousSearchesCB.addEventListener("onEnter", event -> processSearch());
        previousSearchesCB.addEventListener("onSelect", event -> {
            setVisibility(clearSearchBtn, true);
            processSearch();
        });
        previousSearchesCB.addEventListener("onChanging", (InputEvent event) -> {
            setVisibility(clearSearchBtn, true);
            if (!event.isChangingBySelectBack()) {
                refreshSearch(event.getValue());
            }
        });
    }

    public void setVisibility(HtmlBasedComponent comp, boolean visibility) {
        String style = comp.getStyle();
        if (style == null) {
            style = "";
        }
        if (visibility) {
            style = style.replace(";visibility: hidden;", "");
            style = style.concat(";visibility: visible;");
        } else {
            style = style.replace(";visibility: visible;", "");
            style = style.concat(";visibility: hidden;");
        }
        comp.setStyle(style);
    }

    /**
     * Makes sure the Search ComboBox is empty;
     */
    public void clearSearches() {
        previousSearchesCB.setValue("");
    }


    /**
     * Refresh the DropDown of the combo box with a refined set of results.
     *
     * @param comboValue the combox value the user entered.
     */
    private void refreshSearch(String comboValue) {
        if (UserSessionManager.getCurrentUser() == null) {
            return;
        }

        List<SearchHistoriesType> previousSearches =
                UserSessionManager.getCurrentUser().getSearchHistories();

        if (previousSearches == null) {
            return;
        }

        List<String> list = previousSearches.stream()
                .map(SearchHistoriesType::getSearch)
                .filter(s -> s.startsWith(comboValue))
                .collect(Collectors.toList());

        previousSearchesCB.setModel(new ListModelList<>(list));
    }

    /**
     * process search specified previous search combobox: display processes satisfying the query and
     * as a short message the number of those processes.
     *
     * @throws Exception
     */
    private void processSearch() {
        SecurityService securityService = (SecurityService) SpringUtil.getBean("securityService");
        UserService userService = (UserService) SpringUtil.getBean("userService");
        String query = previousSearchesCB.getValue();
        boolean global = true;

        if (query == null || query.length() == 0) {
            clearSearches();
            mainController.reloadSummaries();
            setVisibility(clearSearchBtn, false);
            return;
        }
        if (securityService == null || userService == null) {
            LOGGER.error("SecurityService and/or UserService are null");
            Messagebox.show(Labels.getLabel("portal_servicesUnavailable_message"), "Error", Messagebox.OK,
                    Messagebox.ERROR);
            return;
        }
        FolderType folder = mainController.getPortalSession().getCurrentFolder();
        int folderId = (folder == null) ? 0 : folder.getId();
        try {
            SummariesType summaries =
                    readProcessSummaries(folderId, UserSessionManager.getCurrentUser().getId(), query, global);
            int nbAnswers = summaries.getSummary().size();
            mainController.displayMessage(
                    "Search returned " + nbAnswers + ((nbAnswers == 1) ? " result." : " results."));
            mainController.displaySearchResult(summaries);

            // Update the current user's search history
            User currentUser =
                    UserMapper.convertFromUserType(UserSessionManager.getCurrentUser(), securityService);
            List<SearchHistory> searchHistories = SearchHistoryMapper.convertFromSearchHistoriesType(
                    addSearchHistory(UserSessionManager.getCurrentUser().getSearchHistories(), query));
            userService.updateUserSearchHistory(currentUser, searchHistories);
        } catch (Exception e) {
            LOGGER.error("Failed search", e);
            // Not require to show error message here
            }
    }

    private SummariesType readProcessSummaries(Integer folderId, String userRowGuid,
                                               String searchCriteria, boolean global) throws Exception {
        UserInterfaceHelper uiHelper = (UserInterfaceHelper) SpringUtil.getBean("uiHelper");
        if (uiHelper == null) {
            throw new Exception("User interface helper");
        }

        SummariesType processSummaries = null;

        try {
            processSummaries = uiHelper.buildProcessSummaryList(
                folderId,
                userRowGuid,
                SearchExpressionBuilder.buildSimpleSearchConditions(
                    searchCriteria, "p", "processId", "process"), // processes
                SearchExpressionBuilder.buildSimpleSearchConditions(
                    searchCriteria, "l", "logId","log"), // logs
                SearchExpressionBuilder.buildSimpleSearchConditions(
                    searchCriteria, "f", "folderId","folder"), // folders
                global
            );

        } catch (UnsupportedEncodingException usee) {
            throw new Exception("Failed to get Process Summaries: " + usee.toString(), usee);
        }

        return processSummaries;
    }

    /* Add a search History for this user for later use. */
    static List<SearchHistoriesType> addSearchHistory(List<SearchHistoriesType> searchHist,
                                                      String query) throws Exception {

        // Remove any element of the search history if it is equal to the query or not unique
        {
            Set<String> searchSet = new HashSet<>();
            searchSet.add(query.trim().toUpperCase());
            Iterator<SearchHistoriesType> iterator = searchHist.iterator();
            while (iterator.hasNext()) {
                SearchHistoriesType sh = iterator.next();
                if (sh.getSearch().trim().equalsIgnoreCase(query.trim()) || searchSet.contains(sh.getSearch().toUpperCase()) || searchSet.contains(sh.getSearch().trim().toUpperCase()) ) {
                    iterator.remove();
                }
                searchSet.add(sh.getSearch().toUpperCase());
            }
        }

        // Sort the elements by recency (i.e. higher number = more recent)
        searchHist.sort(new Comparator<SearchHistoriesType>() {
            public int compare(SearchHistoriesType lhs, SearchHistoriesType rhs) {
                return lhs.getNum() - rhs.getNum();
            }
        });

        // Append the new query
        {
            SearchHistoriesType sh = new SearchHistoriesType();
            sh.setSearch(query);
            searchHist.add(sh);
        }

        // Expire the oldest queries if there are too many
        while (searchHist.size() > Constants.maxSearches) {
            searchHist.remove(0);
        }

        // Update the recencies
        int i = 0;
        for (SearchHistoriesType sh : searchHist) {
            sh.setNum(++i);
        }

        // Send to the portal to store it.
        return searchHist;
    }

}
