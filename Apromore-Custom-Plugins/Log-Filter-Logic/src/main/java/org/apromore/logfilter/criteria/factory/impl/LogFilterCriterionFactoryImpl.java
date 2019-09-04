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

package org.apromore.logfilter.criteria.factory.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.factory.LogFilterCriterionFactory;
import org.apromore.logfilter.criteria.impl.*;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.apromore.logfilter.criteria.model.LogFilterTypeSelector;

public class LogFilterCriterionFactoryImpl implements LogFilterCriterionFactory {

	@Override
    public LogFilterCriterion getLogFilterCriterion(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        switch (LogFilterTypeSelector.getType(attribute)) {
            case DIRECT_FOLLOW:
                return new LogFilterCriterionDirectFollow(action, containment, level, label, attribute, value);
            case EVENTUAL_FOLLOW:
                return new LogFilterCriterionEventuallyFollow(action, containment, level, label, attribute, value);
//            case TIME_DURATION:
//                return new LogFilterCriterionDuration(action, containment, level, label, attribute, value);
            case DURATION_RANGE:
                return new LogFilterCriterionDurationRange(action, containment, level, label, attribute, value);
            case CASE_VARIANT:
                return new LogFilterCriterionCaseVariant(action, containment, level, label, attribute, value);
            default:
                return new LogFilterCriterionAttribute(action, containment, level, label, attribute, value);
        }
    }
    
	@Override
    public LogFilterCriterion copyFilterCriterion(LogFilterCriterion criterion) {
    	return this.getLogFilterCriterion(criterion.getAction(), 
    															criterion.getContainment(), 
    															criterion.getLevel(), 
    															criterion.getLabel(), 
    															criterion.getAttribute(), 
    															new HashSet<>(criterion.getValue()));
    }
    
	@Override
    public List<LogFilterCriterion> copyFilterCriterionList(List<LogFilterCriterion> list) {
    	List<LogFilterCriterion> newList = new ArrayList<>();
    	for (LogFilterCriterion c : list) {
    		newList.add(this.copyFilterCriterion(c));
    	}
    	return newList;
    }

}
