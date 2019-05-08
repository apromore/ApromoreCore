/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.plugin.processdiscoverer.impl.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.plugin.processdiscoverer.LogFilterCriterion;
import org.apromore.plugin.processdiscoverer.impl.filter.impl.LogFilterCriterionAttribute;
import org.apromore.plugin.processdiscoverer.impl.filter.impl.LogFilterCriterionDirectFollow;
import org.apromore.plugin.processdiscoverer.impl.filter.impl.LogFilterCriterionDuration;
import org.apromore.plugin.processdiscoverer.impl.filter.impl.LogFilterCriterionEventuallyFollow;

public class LogFilterCriterionFactory {

    public static LogFilterCriterion getLogFilterCriterion(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        switch (LogFilterTypeSelector.getType(attribute)) {
            case DIRECT_FOLLOW:
                return new LogFilterCriterionDirectFollow(action, containment, level, label, attribute, value);
            case EVENTUAL_FOLLOW:
                return new LogFilterCriterionEventuallyFollow(action, containment, level, label, attribute, value);
            case TIME_DURATION:
                return new LogFilterCriterionDuration(action, containment, level, label, attribute, value);
            default:
                return new LogFilterCriterionAttribute(action, containment, level, label, attribute, value);
        }
    }
    
    public static LogFilterCriterion copyFilterCriterion(LogFilterCriterion criterion) {
    	return LogFilterCriterionFactory.getLogFilterCriterion(criterion.getAction(), 
    															criterion.getContainment(), 
    															criterion.getLevel(), 
    															criterion.getLabel(), 
    															criterion.getAttribute(), 
    															new HashSet<>(criterion.getValue()));
    }
    
    public static List<LogFilterCriterion> copyFilterCriterionList(List<LogFilterCriterion> list) {
    	List<LogFilterCriterion> newList = new ArrayList<>();
    	for (LogFilterCriterion c : list) {
    		newList.add(LogFilterCriterionFactory.copyFilterCriterion(c));
    	}
    	return newList;
    }

}
