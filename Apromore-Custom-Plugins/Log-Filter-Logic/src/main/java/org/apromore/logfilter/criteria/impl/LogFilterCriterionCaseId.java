/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
package org.apromore.logfilter.criteria.impl;

import org.apromore.logfilter.criteria.impl.util.Util;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Chii Chang (25/09/2019)
 * Modified: Chii Chang (24/01/2020)
 */
public class LogFilterCriterionCaseId extends AbstractLogFilterCriterion {

    public LogFilterCriterionCaseId(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    protected boolean matchesCriterion(XEvent event) {
        return false;
    }

    @Override
    protected boolean matchesCriterion(XTrace trace) {
        if(trace.getAttributes().containsKey("concept:name")) {
            String caseId = trace.getAttributes().get("concept:name").toString();
            if(value.contains(caseId)) return true;
            else return false;
        }
        return false;
    }

    @Override
    public String getAttribute() {
        return "case:id";
    }

    @Override
    public String toString() {

        boolean numeric = true;
        for (String s : value) {
            if (!Util.isNumeric(s)) {
                numeric = false;
                break;
            }
        }

        if (!numeric) {
            return super.getAction().toString().substring(0,1).toUpperCase() +
                    super.getAction().toString().substring(1).toLowerCase() +
                    " cases such that the trace ID equals to " + value;
        } else {
            String desc = "";
            switch (super.getAction()) {
                case RETAIN: desc += " Retain "; break;
                default: desc += "Remove "; break;
            }

            desc += "all cases where case ID is in ";
            desc += getValueString(super.getValue());

            return desc;
        }
    }

    private String getValueString(Set<String> values) {

        String updatedDesc = "";

        List<Long> vIds = new ArrayList<>();
        for (String s : values) {
            vIds.add(Long.valueOf(s));
        }
        Collections.sort(vIds);

        boolean continuous = isContinuous(vIds);

        if (continuous) {
            updatedDesc += vIds.get(0) + " to " + vIds.get(vIds.size()-1);

        } else {
            TreeSortedMap<Long, Long> linkMap = getLinkMap(vIds);
            for(long key : linkMap.keySet()) {
                if(linkMap.get(key) > key)  updatedDesc += "[" + key + " to " + linkMap.get(key) + "]";
                else updatedDesc += "[" + key + "]";
            }
        }

        return updatedDesc;
    }


    private boolean isContinuous(List<Long>  caseVariantIdList) {

        boolean continuous = true;
        for (int i = 0; i < caseVariantIdList.size(); i++) {
            if (i < (caseVariantIdList.size() - 1)) {
                if (caseVariantIdList.get(i + 1) != caseVariantIdList.get(i) + 1) {
                    continuous = false;
                    break;
                }
            }
        }
        return continuous;
    }

    private TreeSortedMap<Long, Long> getLinkMap(List<Long> variantIds) {
        TreeSortedMap<Long, Long> linkMap = new TreeSortedMap<>();
        long current = variantIds.get(0);
        linkMap.put(current, current);

        for(int i=1; i < variantIds.size(); i++) {
            if(variantIds.get(i) - 1 == variantIds.get(i-1)) {
                linkMap.put(current, variantIds.get(i));
            } else {
                linkMap.put(variantIds.get(i), variantIds.get(i));
                current = variantIds.get(i);
            }
        }
        return linkMap;
    }
}
