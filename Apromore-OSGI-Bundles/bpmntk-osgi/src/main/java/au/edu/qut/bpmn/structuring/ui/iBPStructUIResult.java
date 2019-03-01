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

package au.edu.qut.bpmn.structuring.ui;

import au.edu.qut.bpmn.structuring.core.StructuringCore;

/**
 * Created by Adriano on 29/02/2016.
 */
public class iBPStructUIResult {

    protected static final int MAX_DEPTH = 100;
    protected static final int MAX_CHILDREN = 10;
    protected static final int MAX_SOL = 500;
    protected static final int MAX_STATES = 100;
    protected static final int MAX_MINUTES = 2;

    private StructuringCore.Policy policy;
    private int maxDepth;
    private int maxSol;
    private int maxChildren;
    private int maxStates;
    private int maxMinutes;
    private boolean timeBounded;
    private boolean forceStructuring;
    private boolean keepBisimulation;

    public iBPStructUIResult() {
        policy = StructuringCore.Policy.ASTAR;
        maxDepth = MAX_DEPTH;
        maxSol = MAX_SOL;
        maxChildren = MAX_CHILDREN;
        maxStates = MAX_STATES;
        maxMinutes = MAX_MINUTES;
        keepBisimulation = true;
        timeBounded = true;
        forceStructuring = false;
    }


    public boolean isForceStructuring() { return forceStructuring; }
    public void setForceStructuring(boolean forceStructuring) { this.forceStructuring = forceStructuring; }

    public boolean isKeepBisimulation() {
        return keepBisimulation;
    }
    public void setKeepBisimulation(boolean keepBisimulation) { this.keepBisimulation = keepBisimulation; }

    public boolean isTimeBounded() { return timeBounded; }
    public void setTimeBounded(boolean timeBounded) { this.timeBounded = timeBounded; }

    public StructuringCore.Policy getPolicy() {
        return policy;
    }
    public void setPolicy(StructuringCore.Policy policy) {
        this.policy = policy;
    }

    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }

    public int getMaxSol() { return maxSol; }
    public void setMaxSol(int maxSol) { this.maxSol = maxSol; }

    public int getMaxMinutes() { return maxMinutes; }
    public void setMaxMinutes(int maxMinutes) { this.maxMinutes = maxMinutes; }

    public int getMaxChildren() {
        return maxChildren;
    }
    public void setMaxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
    }

    public int getMaxStates() {
        return maxStates;
    }
    public void setMaxStates(int maxStates) {
        this.maxStates = maxStates;
    }
}
