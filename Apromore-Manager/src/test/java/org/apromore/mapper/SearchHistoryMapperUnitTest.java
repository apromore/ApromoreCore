package org.apromore.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.model.SearchHistory;
import org.apromore.model.SearchHistoriesType;
import org.junit.Before;
import org.junit.Test;

/**
 * SearchHistory Mapper Unit test.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class SearchHistoryMapperUnitTest {

    SearchHistoryMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new SearchHistoryMapper();
    }

    @Test
    public void testConvertFromSearchHistoriesType() throws Exception {
        List<SearchHistoriesType> srhTypes = new ArrayList<SearchHistoriesType>();
        SearchHistoriesType typ1 = new SearchHistoriesType();
        typ1.setNum(1);
        typ1.setSearch("dogs");
        srhTypes.add(typ1);

        SearchHistoriesType typ2 = new SearchHistoriesType();
        typ2.setNum(2);
        typ2.setSearch("cats");
        srhTypes.add(typ2);

        List<SearchHistory> searches = mapper.convertFromSearchHistoriesType(srhTypes);
        assertThat(searches.size(), equalTo(srhTypes.size()));
    }

}
