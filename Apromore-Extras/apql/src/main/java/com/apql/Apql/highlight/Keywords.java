/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.apql.Apql.highlight;

import java.util.Collections;
import java.util.LinkedList;

public final class Keywords {
    public static final String VARIABLES = "VARIABLES";
    public static final String SELECT = "SELECT";
    public static final String FROM = "FROM";
    public static final String WHERE = "WHERE";

    public static final String UNION = "UNION";
    public static final String INTERSECT = "INTERSECT";
    public static final String EXCEPT = "EXCEPT";
    public static final String NOT = "NOT";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String ANY = "ANY";
    public static final String EACH = "EACH";
    public static final String ALL = "ALL";
    public static final String IN = "IN";
    public static final String IS = "IS";
    public static final String OF = "OF";
    public static final String EQUALS = "EQUALS";
    public static final String OVERLAPS = "OVERLAPS";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String UNIVERSE = "UNIVERSE";

    public static final String _STAR_ = "*";
    public static final String domain = "domain";
    public static final String id = "id";
    public static final String language = "language";
    public static final String name = "name";
    public static final String owner = "owner";
    public static final String ranking = "ranking";
    public static final String version = "version";

//    public static final String model = "model";
    public static final String WITH = "WITH";
    public static final String SUBSET = "SUBSET";
    public static final String PROPER = "PROPER";
    public static final String GET_TASKS = "GetTasks";
    public static final String ALWAYS_OCCURS = "AlwaysOccurs";
    public static final String CAN_CONFLICT = "CanConflict";
    public static final String CAN_COOCCUR = "CanCooccur";
    public static final String CAN_OCCUR = "CanOccur";
    public static final String CONFLICT = "Conflict";
    public static final String COOCCUR = "Cooccur";
    public static final String TOTAL_CAUSAL = "TotalCausal";
    public static final String TOTAL_CONCURRENT = "TotalConcurrent";
    private static String[] keywords;
    private static String[] selectClause;
    private static String[] whereClause;

    private Keywords() {
    }

    public static String[] getSelectClause(){
        if(selectClause==null || selectClause.length==0){
            selectClause=new String[]{Keywords._STAR_,Keywords.domain, Keywords.id, Keywords.language, Keywords.owner, Keywords.name,Keywords.ranking, Keywords.FROM};
        }
        return selectClause;
    }

    public static String[] getWhereClause(){
        if(whereClause==null || whereClause.length==0){
            whereClause=new String[]{};
            LinkedList<String> tmp = new LinkedList<String>();
            tmp.add(Keywords.UNION);
            tmp.add(Keywords.INTERSECT);
            tmp.add(Keywords.EXCEPT);
            tmp.add(Keywords.AND);
            tmp.add(Keywords.OR);
            tmp.add(Keywords.ANY);
            tmp.add(Keywords.EACH);
            tmp.add(Keywords.ALL);
            tmp.add(Keywords.IN);
            tmp.add(Keywords.IS);
            tmp.add(Keywords.OF);
            tmp.add(Keywords.EQUALS);
            tmp.add(Keywords.NOT);
            tmp.add(Keywords.OVERLAPS);
            tmp.add(Keywords.TRUE);
            tmp.add(Keywords.FALSE);
            tmp.add(Keywords.UNKNOWN);
            tmp.add(Keywords.UNIVERSE);
            tmp.add(Keywords.WITH);
            tmp.add(Keywords.SUBSET);
            tmp.add(Keywords.PROPER);
            tmp.add(Keywords.GET_TASKS);
            tmp.add(Keywords.GET_TASKS + Keywords.ALWAYS_OCCURS);
            tmp.add(Keywords.GET_TASKS + Keywords.CAN_OCCUR);
            tmp.add(Keywords.GET_TASKS + Keywords.CAN_CONFLICT);
            tmp.add(Keywords.GET_TASKS + Keywords.CAN_COOCCUR);
            tmp.add(Keywords.GET_TASKS + Keywords.CONFLICT);
            tmp.add(Keywords.GET_TASKS + Keywords.COOCCUR);
            tmp.add(Keywords.GET_TASKS + Keywords.TOTAL_CAUSAL);
            tmp.add(Keywords.GET_TASKS + Keywords.TOTAL_CONCURRENT);
            tmp.add(Keywords.ALWAYS_OCCURS);
            tmp.add(Keywords.CAN_CONFLICT);
            tmp.add(Keywords.CAN_COOCCUR);
            tmp.add(Keywords.CAN_OCCUR);
            tmp.add(Keywords.CONFLICT);
            tmp.add(Keywords.COOCCUR);
            tmp.add(Keywords.TOTAL_CAUSAL);
            tmp.add(Keywords.TOTAL_CONCURRENT);
            Collections.sort(tmp);
            whereClause = (tmp.toArray(whereClause));
        }
        return whereClause;
    }

    public static String[] getKeywords(){
        if(keywords==null || keywords.length==0) {
            keywords=new String[]{};
            LinkedList<String> tmp = new LinkedList<String>();
//            tmp.add(Keywords.VARIABLES);
            tmp.add(Keywords.SELECT);
            tmp.add(Keywords.FROM);
            tmp.add(Keywords.WHERE);
            tmp.add(Keywords.UNION);
            tmp.add(Keywords.INTERSECT);
            tmp.add(Keywords.EXCEPT);
            tmp.add(Keywords.AND);
            tmp.add(Keywords.OR);
            tmp.add(Keywords.ANY);
            tmp.add(Keywords.EACH);
            tmp.add(Keywords.ALL);
            tmp.add(Keywords.IN);
            tmp.add(Keywords.IS);
            tmp.add(Keywords.OF);
            tmp.add(Keywords.EQUALS);
            tmp.add(Keywords.NOT);
            tmp.add(Keywords.OVERLAPS);
            tmp.add(Keywords.TRUE);
            tmp.add(Keywords.FALSE);
            tmp.add(Keywords.UNKNOWN);
            tmp.add(Keywords.UNIVERSE);
            tmp.add(Keywords.WITH);
            tmp.add(Keywords.SUBSET);
            tmp.add(Keywords.PROPER);
            tmp.add(Keywords.GET_TASKS);
            tmp.add(Keywords.GET_TASKS + Keywords.ALWAYS_OCCURS);
            tmp.add(Keywords.GET_TASKS + Keywords.CAN_OCCUR);
            tmp.add(Keywords.GET_TASKS + Keywords.CAN_CONFLICT);
            tmp.add(Keywords.GET_TASKS + Keywords.CAN_COOCCUR);
            tmp.add(Keywords.GET_TASKS + Keywords.CONFLICT);
            tmp.add(Keywords.GET_TASKS + Keywords.COOCCUR);
            tmp.add(Keywords.GET_TASKS + Keywords.TOTAL_CAUSAL);
            tmp.add(Keywords.GET_TASKS + Keywords.TOTAL_CONCURRENT);
            tmp.add(Keywords.ALWAYS_OCCURS);
            tmp.add(Keywords.CAN_CONFLICT);
            tmp.add(Keywords.CAN_COOCCUR);
            tmp.add(Keywords.CAN_OCCUR);
            tmp.add(Keywords.CONFLICT);
            tmp.add(Keywords.COOCCUR);
            tmp.add(Keywords.TOTAL_CAUSAL);
            tmp.add(Keywords.TOTAL_CONCURRENT);
            Collections.sort(tmp);
//            tmp.addFirst(model);
            tmp.addFirst(name);
            tmp.addFirst(id);
            tmp.addFirst(_STAR_);
            keywords = (tmp.toArray(keywords));
        }
        return keywords;
    }

    public static boolean contains(String word){
        for(String s: getKeywords()){
            if(word.equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }
}
