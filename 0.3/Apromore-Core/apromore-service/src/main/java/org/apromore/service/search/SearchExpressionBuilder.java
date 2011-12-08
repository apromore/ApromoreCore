package org.apromore.service.search;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * Create the Search Expression used in the read process summaries.
 */
public class SearchExpressionBuilder {

    public String buildSearchConditions(String searchExpression) throws UnsupportedEncodingException {
        String condition = "";
        if (searchExpression != null && searchExpression.compareTo("") != 0) {
            // map search expression into a list of terms
            // example: (yawl;protos),invoicing => [(,yawl,or,protos,),and,invoicing]
            Vector<String> expression = mapQuery(searchExpression) ;
            for (int i = 0; i<expression.size();i++){
                String current = expression.elementAt(i);
                if (current.compareTo(" and ")==0 || current.compareTo(" or ")==0 ||
                        current.compareTo(" ) ")==0 || current.compareTo(" ( ")==0) {
                    condition += current ;
                } else {
                    condition += " p.processId in (select k.processId FROM Keyword k WHERE k.word like '%" + current + "%' )";
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
     * @param keywordssearch the search expression
     * @return Vector<String> : the SQL condition corresponding to keywordssearch
     * @throws UnsupportedEncodingException
     */
    private Vector<String> mapQuery(String keywordssearch) throws UnsupportedEncodingException {
        Vector<String> res = new Vector<String>();
        String term = "";
        int state = 1;    // initial state in the recognition automaton
        String currentChar = "";
        for (int i = 0; i < keywordssearch.length(); i++) {
            currentChar = keywordssearch.substring(i, i + 1);
            if (state == 1) {
                if (currentChar.compareTo(",") == 0) {
                    // and
                    res.add(" and ");
                } else {
                    if (currentChar.compareTo(";") == 0) {
                        // or
                        res.add(" or ");
                    } else {
                        if (currentChar.compareTo(")") == 0) {
                            res.add(" ) ");
                        } else {
                            if (currentChar.compareTo("(") == 0) {
                                res.add(" ( ");
                            } else {
                                if (currentChar.compareTo(" ") != 0) {
                                    // not an operator, not a space
                                    term = currentChar;
                                    state = 2;
                                }
                            }
                        }
                    }
                }
            } else {
                if (state == 2) {
                    if (currentChar.compareTo(",") == 0) {
                        // and
                        res.add(term);
                        res.add(" and ");
                        state = 1;
                    } else {
                        if (currentChar.compareTo(";") == 0) {
                            // or
                            res.add(term);
                            res.add(" or ");
                            state = 1;
                        } else {
                            if (currentChar.compareTo(")") == 0) {
                                res.add(term);
                                res.add(" ) ");
                                state = 1;
                            } else {
                                if (currentChar.compareTo("(") == 0) {
                                    res.add(term);
                                    res.add(" ( ");
                                    state = 1;
                                } else {
                                    if (currentChar.compareTo(" ") != 0) {
                                        // not an operator, not a space
                                        term += currentChar;
                                    } else {
                                        state = 3;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // state = 3
                    if (currentChar.compareTo(",") == 0) {
                        // and
                        res.add(term);
                        res.add(" and ");
                        state = 1;
                    } else {
                        if (currentChar.compareTo(";") == 0) {
                            // or
                            res.add(term);
                            res.add(" or ");
                            state = 1;
                        } else {
                            if (currentChar.compareTo(")") == 0) {
                                res.add(term);
                                res.add(" ) ");
                                state = 1;
                            } else {
                                if (currentChar.compareTo("(") == 0) {
                                    res.add(term);
                                    res.add(" ( ");
                                    state = 1;
                                } else {
                                    if (currentChar.compareTo(" ") != 0) {
                                        // not an operator, not a space
                                        term += " " + currentChar;
                                        state = 2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (state == 2 || state == 3) res.add(term);
        return res;
    }
    
    
    
    public static void main(String[] args) throws Exception {
        SearchExpressionBuilder sb = new SearchExpressionBuilder();
        System.out.println(sb.buildSearchConditions("(yawl;protos),invoicing"));
    }
}
