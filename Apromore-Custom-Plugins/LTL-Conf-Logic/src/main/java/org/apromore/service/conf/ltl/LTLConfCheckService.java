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

package org.apromore.service.conf.ltl;

import au.ltl.domain.Constraint;
import au.ltl.main.ModelChecker;
import au.ltl.main.RuleVisualization;
import au.ltl.utils.ModelAbstractions;
import org.deckfour.xes.model.XLog;

import au.ltl.domain.Actions;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 04/26/2016.
 */
public interface LTLConfCheckService {

    HashMap<String, List<RuleVisualization>> checkConformanceLTL(ModelAbstractions model, InputStream XmlFileDeclareRules, LinkedList<Constraint> LTLConstraintList, int addActionCost, int deleteActionCost) throws Exception;
}
