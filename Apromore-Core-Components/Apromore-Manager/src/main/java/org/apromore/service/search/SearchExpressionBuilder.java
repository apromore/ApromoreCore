/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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

package org.apromore.service.search;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create the Search Expression used in the read process summaries.
 */
public abstract class SearchExpressionBuilder {

    public static String buildSearchConditions(String searchExpression, String tableVar, String keywordsTableIdField, String type) throws UnsupportedEncodingException {
        String condition = "";
        if (searchExpression != null && searchExpression.compareTo("") != 0) {
            //condition = " and ";

            // map search expression into a list of terms
            // example: (yawl;protos),invoicing => [(,yawl,or,protos,),and,invoicing]
            List<String> expression = mapQuery(searchExpression);
            for (String current : expression) {
                if (current.compareTo(" and ") == 0 || current.compareTo(" or ") == 0 ||
                        current.compareTo(" ) ") == 0 || current.compareTo(" ( ") == 0) {
                    condition += current;
                } else {
                    condition += " " + tableVar + ".id in (select k." + keywordsTableIdField + " FROM Keywords k WHERE k.value like '%" + current + "%' AND k.type = '" + type + "')";
                }
            }
        }
        return condition;
    }

    /**
     * A variant of {@link #buildSearchConditions} which bypasses the support for boolean search logic.
     */
    public static String buildSimpleSearchConditions(String searchExpression, String tableVar, String keywordsTableIdField, String type) throws UnsupportedEncodingException {
        return (searchExpression != null && !searchExpression.isEmpty())
            ? " " + tableVar + ".id in (select k." + keywordsTableIdField + " FROM Keywords k WHERE k.value like '%" + mapSimpleQuery(searchExpression) + "%' AND k.type = '" + type + "')"
            : "";
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
    public static List<String> mapQuery(String keywordSearch) {
        List<String> res = new ArrayList<>();
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
                    term = escape(currentChar);
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
                    term += escape(currentChar);
                }
            }
        }
        if (state == 2) {
            res.add(term);
        }
        return res;
    }

    private static String mapSimpleQuery(String keywordSearch) {
        return keywordSearch.replaceAll("%", "_")    // SQL doesn't have an escape for %, so just match any single character instead
                            .replaceAll("'", "''");  // JPQL escape for apostrophes
    }

    /**
     * @param character  a single character
     * @return the JPQL-escaped version of the <var>character</var>
     * @see http://docs.oracle.com/cd/E11035_01/kodo41/full/html/ejb3_langref.html#ejb3_langref_lit
     */
    private static String escape(String character) {
        assert character.length() == 1;
        switch (character) {
        case "'":
            return "''";
        default:
            return character;
        }
    }

}
