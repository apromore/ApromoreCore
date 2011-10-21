package org.apromore.mapper;

import org.apromore.dao.model.SearchHistory;
import org.apromore.model.SearchHistoriesType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class SearchHistoryMapper {

    /**
     * Convert from the WS (SearchHistoriesType) to the DB model (SearchHistory).
     * @param srhTypes the list of SearchHistoriesType from the WebService
     * @return the set of SearchHistory dao model populated.
     */
    public static Set<SearchHistory> convertFromSearchHistoriesType(List<SearchHistoriesType> srhTypes) {
        Set<SearchHistory> searches = new HashSet<SearchHistory>();
        for (SearchHistoriesType srhType : srhTypes) {
            SearchHistory sh = new SearchHistory();
            sh.setNum(srhType.getNum());
            sh.setSearch(srhType.getSearch());
            searches.add(sh);
        }
        return searches;
    }

}
