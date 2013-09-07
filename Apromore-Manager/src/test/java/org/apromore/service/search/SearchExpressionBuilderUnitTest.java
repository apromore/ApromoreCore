package org.apromore.service.search;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit test the Search Expression builder.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class SearchExpressionBuilderUnitTest {

    private static final String SEARCH_EXPRESSION_SINGLE = " p.id in (select k.processId FROM Keywords k WHERE k.value like '%invoicing%' )";
    private static final String SEARCH_EXPRESSION_OR = " (  p.id in (select k.processId FROM Keywords k WHERE k.value like '%yawl%' ) or  p.id in (select k.processId FROM Keywords k WHERE k.value like '%protos%' ) ) ";
    private static final String SEARCH_EXPRESSION_AND = " p.id in (select k.processId FROM Keywords k WHERE k.value like '%yawl%' ) and  p.id in (select k.processId FROM Keywords k WHERE k.value like '%protos%' )";
    private static final String SEARCH_EXPRESSION_AND_OR = " (  p.id in (select k.processId FROM Keywords k WHERE k.value like '%yawl%' ) or  p.id in (select k.processId FROM Keywords k WHERE k.value like '%protos%' ) )  and  p.id in (select k.processId FROM Keywords k WHERE k.value like '%invoicing%' )";
    
    private String expression;
    private SearchExpressionBuilder seb;
   
    
    @Before
    public void setup() {
        seb = new SearchExpressionBuilder();
    }

    @Test
    public void buildExpressionWithEmptyNullString() throws Exception {
        expression = seb.buildSearchConditions("");
        assertThat(expression, equalTo(""));

        expression = seb.buildSearchConditions(null);
        assertThat(expression, equalTo(""));

        expression = seb.buildSearchConditions(" ");
        assertThat(expression, equalTo(""));
    }


    @Test
    public void buildExpressionWithRealData() throws Exception {
        expression = seb.buildSearchConditions("invoicing");
        assertThat(expression, containsString("k.value like '%invoicing%'"));
        assertThat(expression, equalTo(SEARCH_EXPRESSION_SINGLE));

        expression = seb.buildSearchConditions("(yawl;protos)");
        assertThat(expression, containsString("'%yawl%'"));
        assertThat(expression, containsString("'%protos%'"));
        assertThat(expression, equalTo(SEARCH_EXPRESSION_OR));

        expression = seb.buildSearchConditions("yawl,protos");
        assertThat(expression, equalTo(SEARCH_EXPRESSION_AND));

        expression = seb.buildSearchConditions("(yawl;protos),invoicing");
        assertThat(expression, equalTo(SEARCH_EXPRESSION_AND_OR));
    }
}
