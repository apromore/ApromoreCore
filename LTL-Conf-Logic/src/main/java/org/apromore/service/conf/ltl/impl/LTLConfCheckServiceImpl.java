/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.service.conf.ltl.impl;

import au.ltl.domain.Actions;
import au.ltl.domain.Constraint;
import au.ltl.main.ModelChecker;
import au.ltl.main.RuleVisualization;
import au.ltl.utils.ModelAbstractions;
import org.apromore.service.conf.ltl.LTLConfCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class LTLConfCheckServiceImpl implements LTLConfCheckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LTLConfCheckServiceImpl.class);

    @Override
    public HashMap<String, List<RuleVisualization>> checkConformanceLTL(ModelAbstractions model, InputStream XmlFileDeclareRules, LinkedList<Constraint> LTLConstraintList, int addActionCost, int deleteActionCost) throws Exception{
        ModelChecker checker = new ModelChecker(model,XmlFileDeclareRules,LTLConstraintList,addActionCost, deleteActionCost);
        return checker.checkNet();
    }
}