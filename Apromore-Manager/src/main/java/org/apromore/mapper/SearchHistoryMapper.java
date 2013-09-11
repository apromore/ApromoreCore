package org.apromore.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.model.SearchHistory;
import org.apromore.model.SearchHistoriesType;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class SearchHistoryMapper {

    /**
     * Convert from the WS (SearchHistoriesType) to the DB model (SearchHistory).
     *
     * @param srhTypes the list of SearchHistoriesType from the WebService
     * @return the set of SearchHistory dao model populated.
     */
    public static List<SearchHistory> convertFromSearchHistoriesType(List<SearchHistoriesType> srhTypes) {
        List<SearchHistory> searches = new ArrayList<>();
        for (SearchHistoriesType srhType : srhTypes) {
            SearchHistory sh = new SearchHistory();
            sh.setIndex(srhType.getNum());
            sh.setSearch(srhType.getSearch());
            searches.add(sh);
        }
        return searches;
    }

}
