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

package au.edu.qut.bpmn.structuring.core;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import au.edu.qut.bpmn.structuring.graph.Graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adriano on 28/02/2016.
 */
public class StructuringCore {
//    private static final Logger LOGGER = LoggerFactory.getLogger(StructuringCore.class);

    public enum Policy {DEPTH, ASTAR, LIM_ASTAR, BREADTH, LIM_DEPTH}

    private Policy policy;
    private int maxDepth;
    private int maxSol;
    private int maxChildren;
    private int maxStates;
    private int maxMinutes;
    private boolean timeBounded;

    private Set<Graph> structuredRigids;
    private Structurer structurer;

    public StructuringCore(Policy policy, int maxDepth, int maxSol, int maxChildren, int maxStates, int maxMinutes, boolean timeBounded) {
        this.policy = policy;
        this.maxDepth = maxDepth;
        this.maxSol = maxSol;
        this.maxChildren = maxChildren;
        this.maxStates = maxStates;
        this.maxMinutes = maxMinutes;
        this.timeBounded = timeBounded;
    }

    public Set<Graph> structureAll(Set<Graph> rigids) {
        structurer = new Structurer(policy, maxDepth, maxSol, maxChildren, maxStates, maxMinutes, timeBounded);
        structuredRigids = new HashSet<>();
        for( Graph g : rigids ) structuredRigids.add(structurer.getStructuredRigid(new StructuringState(g, 0)));
        return structuredRigids;
    }

}
