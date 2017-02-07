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

package au.edu.qut.processmining.miners.heuristic.net;

import au.edu.qut.processmining.log.graph.LogEdge;

import java.text.DecimalFormat;

/**
 * Created by Adriano on 24/10/2016.
 */
public class HeuristicEdge extends LogEdge {

    private static DecimalFormat dFormat = new DecimalFormat(".###");

    private int frequency;
    private double localDependencyScore;

    public HeuristicEdge(HeuristicNode source, HeuristicNode target){
        super(source, target);
        frequency = 0;
        localDependencyScore = 0;
    }

    public HeuristicEdge(HeuristicNode source, HeuristicNode target, String label){
        super(source, target, label);
        frequency = 0;
        localDependencyScore = 0;
    }

    public HeuristicEdge(HeuristicNode source, HeuristicNode target, int frequency){
        super(source, target);
        this.frequency = frequency;
        localDependencyScore = 0;
    }

    public HeuristicEdge(HeuristicNode source, HeuristicNode target, String label, int frequency){
        super(source, target, label);
        this.frequency = frequency;
        localDependencyScore = 0;
    }

    public void increaseFrequency() { frequency++; }
    public void increaseFrequency(int amount) { frequency += amount; }

    public double getLocalDependencyScore() { return localDependencyScore; }
    public void setLocalDependencyScore(double localDependencyScore) { this.localDependencyScore = localDependencyScore; }

    public int getFrequency(){ return frequency; }

    @Override
    public String toString() { return dFormat.format(localDependencyScore) + "/" + frequency; }

    @Override
    public int compareTo(Object o) {
        if( (o instanceof HeuristicEdge) && ((HeuristicEdge) o).getLocalDependencyScore() > localDependencyScore ) return 1;
        else return -1;
    }
}
