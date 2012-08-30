package org.apromore.service.search;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create the Search Expression used in the read process summaries.
 */
public class SearchExpressionBuilder {

    public String buildSearchConditions(String searchExpression) throws UnsupportedEncodingException {
        String condition = "";
        if (searchExpression != null && searchExpression.compareTo("") != 0) {
            condition = " and ";

            // map search expression into a list of terms
            // example: (yawl;protos),invoicing => [(,yawl,or,protos,),and,invoicing]
            List<String> expression = mapQuery(searchExpression);
            for (String current : expression) {
                if (current.compareTo(" and ") == 0 || current.compareTo(" or ") == 0 ||
                        current.compareTo(" ) ") == 0 || current.compareTo(" ( ") == 0) {
                    condition += current;
                } else {
                    condition += " p.processId in (select k.id.processId FROM Keyword k WHERE k.id.word like '%" + current + "%' )";
                }
            }
        }
        return condition;
    }

    /**
     * Interpretation of the query received by customer
     * "," => and
     * ";" => or
     * "(" and ")" remain
     * each term of the query is an element in the result
     * a,b;(d,e) => [a, and, b, or, (, a, and, e, )]
     *
     * @param keywordSearch the search expression
     * @return the SQL condition corresponding to keywordSearch
     */
    public List<String> mapQuery(String keywordSearch) {
        List<String> res = new ArrayList<String>();
        String term = "";
        int state = 1;
        String currentChar;
        for (int i = 0; i < keywordSearch.length(); i++) {
            currentChar = keywordSearch.substring(i, i + 1);
            if (state == 1) {
                if (currentChar.compareTo(",") == 0) {
                    res.add(" and ");
                } else if (currentChar.compareTo(";") == 0) {
                    res.add(" or ");
                } else if (currentChar.compareTo("(") == 0) {
                    res.add(" ( ");
                } else if (currentChar.compareTo(" ") != 0) {
                    term = currentChar;
                    state = 2;
                }
            } else {
                if (currentChar.compareTo(",") == 0) {
                    res.add(term);
                    res.add(" and ");
                    state = 1;
                } else if (currentChar.compareTo(";") == 0) {
                    res.add(term);
                    res.add(" or ");
                    state = 1;
                } else if (currentChar.compareTo(")") == 0) {
                    res.add(term);
                    res.add(" ) ");
                    state = 1;
                } else if (currentChar.compareTo(" ") != 0) {
                    term += currentChar;
                }
            }
        }
        if (state == 2) {
            res.add(term);
        }
        return res;
    }

}
