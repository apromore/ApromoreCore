package org.apromore.portal.dialogController;

import org.apromore.model.SearchHistoriesType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import java.util.Iterator;
import java.util.List;

/**
 * source taken from ZK Small Talk "Autocomplete with Combobox"
 * @author fauvet
 */
public class SearchHistoriesController extends Combobox {

    private MainController mainC;
    private List<SearchHistoriesType> previousSearches;
    private SimpleSearchController caller;

    public SearchHistoriesController(MainController mainC, SimpleSearchController caller) {
        this.mainC = mainC;
        this.caller = caller;
        refresh(""); //init the child comboitems
    }

    public SearchHistoriesController(MainController mainC, String value) {
        super(value); //it invokes setValue(), which inits the child comboitems
        this.mainC = mainC;
    }

    public void setValue(String value) {
        super.setValue(value);
        refresh(value); //refresh the child comboitems
    }

    /**
     * Listens what an user is entering.
     */
    public void onChanging(InputEvent evt) {
        if (!evt.isChangingBySelectBack()) {
            refresh(evt.getValue());
        }
    }

    /**
     * Refreshes comboitem based on the specified value.
     */
    public void refresh(String val) {

        if (UserSessionManager.getCurrentUser() != null) {
            this.previousSearches = this.mainC.getSearchHistory();

            if (previousSearches != null) {
                int j = 0;
                while (j < this.previousSearches.size() && this.previousSearches.get(j).getSearch().compareTo(val) < 0) {
                    j++;
                }

                Iterator<Comboitem> it = getItems().iterator();
                for (int cnt = 10; --cnt >= 0 && j < this.previousSearches.size() && this.previousSearches.get(j).getSearch().startsWith(val); ++j) {
                    if (it != null && it.hasNext()) {
                        it.next().setLabel(this.previousSearches.get(j).getSearch());
                    } else {
                        it = null;
                        new Comboitem(this.previousSearches.get(j).getSearch()).setParent(this);
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
     * Inserts this query in current user's search history (maintain history sorted and
     * with elements pairwise distinct).
     * @param query
     */
    public void addSearchHist(String query) {

        List<SearchHistoriesType> searchHist = this.mainC.getSearchHistory();
        // if maxSearches reached, remove the oldest search
        if (searchHist.size() == Constants.maxSearches) {
            // find the oldest search to remove: the one whose num is the smallest
            // -1 is the num value given for searches added during the session
            int min = -1,
                    indMin = 0;
            for (int i = 0; i < searchHist.size(); i++) {
                SearchHistoriesType hist = searchHist.get(i);
                if (min == -1 || hist.getNum() != -1 && hist.getNum() < min) {
                    min = hist.getNum();
                    indMin = i;
                }
            }
            //this.mainC.getCurrentUser().getSearchHistories().remove(indMin);
        }

        // insert the search query. Keep the list ordered, with elements pairwise distinct
        int i = 0;
        while (i < searchHist.size() && searchHist.get(i).getSearch().compareTo(query) < 0) {
            i++;
        }
        if (i == searchHist.size()) {
            // query is the greatest
            SearchHistoriesType h = new SearchHistoriesType();
            h.setSearch(query);
            h.setNum(-1);
            searchHist.add(h);
        } else {
            if (searchHist.get(i).getSearch().compareTo(query) > 0) {
                // not found. Smaller than this.searchHist.get(i)
                SearchHistoriesType h = new SearchHistoriesType();
                h.setSearch(query);
                h.setNum(-1);
                searchHist.add(i, h);
            }
        }
    }

    public String getValue() {
        return super.getValue();
    }
}
