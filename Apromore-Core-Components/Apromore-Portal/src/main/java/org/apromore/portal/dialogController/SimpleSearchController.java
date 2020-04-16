/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.apromore.model.FolderType;
import org.apromore.model.SummariesType;
import org.apromore.model.SearchHistoriesType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionDao;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hbox;
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
        if (UserSessionManager.getCurrentUser() != null) {
            List<SearchHistoriesType> previousSearches = this.mainC.getSearchHistory();

            if (previousSearches != null) {
                int j = 0;
                while (j < previousSearches.size() && previousSearches.get(j).getSearch().compareTo(comboValue) < 0) {
                    j++;
                }

                Iterator<Comboitem> it = previousSearchesCB.getItems().iterator();
                while (j < previousSearches.size() && previousSearches.get(j).getSearch().startsWith(comboValue)) {
                    j++;
                    if (it != null && it.hasNext()) {
                        it.next().setLabel(previousSearches.get(j).getSearch());
                    } else {
                        it = null;
                        if (j < previousSearches.size()) {
                            new Comboitem(previousSearches.get(j).getSearch()).setParent(previousSearchesCB);
                        }
                    }
                }

                while (it != null && it.hasNext()) {
                    it.next();
                    it.remove();
                }
            }
        }
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
        String query = previousSearchesCB.getValue();
        SummariesType processSummaries = getService().readProcessSummaries(folder.getId(), query);
        int nbAnswers = processSummaries.getSummary().size();
        String message = "Search returned " + nbAnswers;
        if (nbAnswers > 1) {
            message += " processes.";
        } else {
            message += " process.";
        }
        mainC.displayMessage(message);
        mainC.displayProcessSummaries(processSummaries, true);
        addSearchHistory(query);
    }

    /* Add a search History for this user for later use. */
    private void addSearchHistory(String query) throws Exception {
        List<SearchHistoriesType> searchHist = this.mainC.getSearchHistory();
        if (searchHist != null) {
            if (searchHist.size() == Constants.maxSearches) {
                int min = -1, indMin = 0;
                for (int i = 0; i < searchHist.size(); i++){
                    SearchHistoriesType hist = searchHist.get(i);
                    if (min == -1 || hist.getNum() != -1 && hist.getNum() < min) {
                        min = hist.getNum();
                        indMin = i;
                    }
                }
                mainC.getSearchHistory().remove(indMin);
            }

            int i = 0;
            while (i < searchHist.size() && searchHist.get(i).getSearch().compareTo(query) < 0) {
                i++;
            }
            if (i == searchHist.size()) {
                SearchHistoriesType h = new SearchHistoriesType();
                h.setSearch(query);
                h.setNum(-1);
                searchHist.add(h);
            } else {
                if (searchHist.get(i).getSearch().compareTo(query) > 0) {
                    SearchHistoriesType h = new SearchHistoriesType();
                    h.setSearch(query);
                    h.setNum(-1);
                    searchHist.add(i, h);
                }
            }
        }

        // Send to the portal to store it.
        mainC.updateSearchHistory(searchHist);
    }

}
