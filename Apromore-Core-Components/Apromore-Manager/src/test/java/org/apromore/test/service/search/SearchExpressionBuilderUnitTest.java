/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2013, 2015 - 2017 Queensland University of Technology.
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

package org.apromore.test.service.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import org.apromore.service.search.SearchExpressionBuilder;

/**
 * Unit test the Search Expression builder.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
class SearchExpressionBuilderUnitTest {

    private static final String SEARCH_EXPRESSION_SINGLE = " p.id in (select k.processId FROM Keywords k WHERE k.value like '%invoicing%' AND k.type = 'process')";
    private static final String SEARCH_EXPRESSION_OR = " (  p.id in (select k.processId FROM Keywords k WHERE k.value like '%yawl%' AND k.type = 'process') or  p.id in (select k.processId FROM Keywords k WHERE k.value like '%protos%' AND k.type = 'process') ) ";
    private static final String SEARCH_EXPRESSION_AND = " p.id in (select k.processId FROM Keywords k WHERE k.value like '%yawl%' AND k.type = 'process') and  p.id in (select k.processId FROM Keywords k WHERE k.value like '%protos%' AND k.type = 'process')";
    private static final String SEARCH_EXPRESSION_AND_OR = " (  p.id in (select k.processId FROM Keywords k WHERE k.value like '%yawl%' AND k.type = 'process') or  p.id in (select k.processId FROM Keywords k WHERE k.value like '%protos%' AND k.type = 'process') )  and  p.id in (select k.processId FROM Keywords k WHERE k.value like '%invoicing%' AND k.type = 'process')";
    private static final String SEARCH_EXPRESSION_APOSTROPHE = " p.id in (select k.processId FROM Keywords k WHERE k.value like '%''apostrophe''%' AND k.type = 'process')";
    private static final String SEARCH_EXPRESSION_2APOSTROPHE = " p.id in (select k.processId FROM Keywords k WHERE k.value like '%double''''apostrophe%' AND k.type = 'process')";
    private static final String SEARCH_EXPRESSION_BACKSLASH = " p.id in (select k.processId FROM Keywords k WHERE k.value like '%\\backslash\\%' AND k.type = 'process')";
    
    private String expression;

    @Test
    void buildExpressionWithEmptyNullString() throws Exception {
        expression = SearchExpressionBuilder.buildSearchConditions("", "p", "processId", "process");
        assertThat(expression, equalTo(""));

        expression = SearchExpressionBuilder.buildSearchConditions(null, "p", "processId", "process");
        assertThat(expression, equalTo(""));

        expression = SearchExpressionBuilder.buildSearchConditions(" ", "p", "processId", "process");
        assertThat(expression, equalTo(""));
    }


    @Test
    void buildExpressionWithRealData() throws Exception {
        expression = SearchExpressionBuilder.buildSearchConditions("invoicing", "p", "processId", "process");
        assertThat(expression, containsString("k.value like '%invoicing%'"));
        assertThat(expression, equalTo(SEARCH_EXPRESSION_SINGLE));

        expression = SearchExpressionBuilder.buildSearchConditions("(yawl;protos)", "p", "processId", "process");
        assertThat(expression, containsString("'%yawl%'"));
        assertThat(expression, containsString("'%protos%'"));
        assertThat(expression, equalTo(SEARCH_EXPRESSION_OR));

        expression = SearchExpressionBuilder.buildSearchConditions("yawl,protos", "p", "processId", "process");
        assertThat(expression, equalTo(SEARCH_EXPRESSION_AND));

        expression = SearchExpressionBuilder.buildSearchConditions("(yawl;protos),invoicing", "p", "processId", "process");
        assertThat(expression, equalTo(SEARCH_EXPRESSION_AND_OR));

        expression = SearchExpressionBuilder.buildSearchConditions("'apostrophe'", "p", "processId", "process");
        assertThat(expression, equalTo(SEARCH_EXPRESSION_APOSTROPHE));

        expression = SearchExpressionBuilder.buildSearchConditions("double''apostrophe", "p", "processId", "process");
        assertThat(expression, equalTo(SEARCH_EXPRESSION_2APOSTROPHE));

        expression = SearchExpressionBuilder.buildSearchConditions("\\backslash\\", "p", "processId", "process");
        assertThat(expression, equalTo(SEARCH_EXPRESSION_BACKSLASH));
    }
}
