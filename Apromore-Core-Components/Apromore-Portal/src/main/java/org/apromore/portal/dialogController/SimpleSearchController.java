/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.xml.bind.JAXBException;

import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionDao;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.SearchHistoriesType;
import org.apromore.portal.model.SummariesType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

public class SimpleSearchController extends BaseController {

    private MainController mainC;
    private Combobox previousSearchesCB;

    public SimpleSearchController(MainController mainController) throws UnsupportedEncodingException, ExceptionDao, JAXBException {
        mainC = mainController;

        Window simpleSearchW = (Window) mainC.getFellow("simplesearchcomp").getFellow("simplesearchwindow");
        Hbox previousSearchesH = (Hbox) simpleSearchW.getFellow("previoussearcheshbox");
        Button simpleSearchesBu = (Button) previousSearchesH.getFellow("previoussearchesbutton");
        previousSearchesCB = (Combobox) previousSearchesH.getFellow("previoussearchescombobox");

        refreshSearch("");

        simpleSearchesBu.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                processSearch();
            }
        });
        previousSearchesCB.addEventListener("onOK", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    processSearch();
                }
            });
        previousSearchesCB.addEventListener("onChanging", new EventListener<InputEvent>() {
            public void onEvent(InputEvent event) throws Exception {
                if (!event.isChangingBySelectBack()) {
                    refreshSearch(event.getValue());
                }
            }
        });
    }


    /**
     * Makes sure the Search ComboBox is empty;
     */
    public void clearSearches() {
        previousSearchesCB.setValue("");
    }


    /**
     * Refresh the DropDown of the combo box with a refined set of results.
     * @param comboValue the combox value the user entered.
     */
    private void refreshSearch(String comboValue) {
        if (UserSessionManager.getCurrentUser() == null) {
            return;
        }

        List<SearchHistoriesType> previousSearches = this.mainC.getSearchHistory();

        if (previousSearches == null) {
            return;
        }

        List<String> list = new ArrayList<>();
        for (SearchHistoriesType previousSearch: previousSearches) {
            if (previousSearch.getSearch().startsWith(comboValue)) {
                list.add(previousSearch.getSearch());
            }
        }

        previousSearchesCB.setModel(new ListModelList<>(list));
    }

    /**
     * process search specified previous search combobox: display processes satisfying the query
     * and as a short message the number of those processes.
     * @throws Exception
     */
    private void processSearch() throws Exception {
        FolderType folder = UserSessionManager.getCurrentFolder();
        if (folder == null) {
            throw new Exception("Search requires a folder to be selected");
        }

        int folderId = (folder == null) ? 0 : folder.getId();
        String query = previousSearchesCB.getValue();
        SummariesType summaries = getService().readProcessSummaries(folderId, UserSessionManager.getCurrentUser().getId(), query);
        int nbAnswers = summaries.getSummary().size();
        mainC.displayMessage("Search returned " + nbAnswers + ((nbAnswers == 1) ? " result." : " results."));
        mainC.displaySearchResult(summaries);
        mainC.updateSearchHistory(addSearchHistory(mainC.getSearchHistory(), query));
    }

    /* Add a search History for this user for later use. */
    static List<SearchHistoriesType> addSearchHistory(List<SearchHistoriesType> searchHist, String query) throws Exception {

        // If the new query is already present, remove it
        for (SearchHistoriesType sh: searchHist) {
            if (sh.getSearch().equals(query)) {
                searchHist.remove(sh);
                break;
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
        for (SearchHistoriesType sh: searchHist) {
            sh.setNum(++i);
        }

        // Send to the portal to store it.
        return searchHist;
    }

}
