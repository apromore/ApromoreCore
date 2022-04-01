/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011, 2012, 2015 - 2017 Queensland University of Technology.
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

package org.apromore.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.model.SearchHistory;
import org.apromore.portal.model.SearchHistoriesType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SearchHistory Mapper Unit test.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
class SearchHistoryMapperUnitTest {

    SearchHistoryMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new SearchHistoryMapper();
    }

    @Test
    void testConvertFromSearchHistoriesType() throws Exception {
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
