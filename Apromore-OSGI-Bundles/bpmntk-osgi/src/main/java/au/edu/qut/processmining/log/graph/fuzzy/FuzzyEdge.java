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

package au.edu.qut.processmining.log.graph.fuzzy;

import au.edu.qut.processmining.log.graph.LogEdge;

/**
 * Created by Adriano on 15/06/2016.
 */

public class FuzzyEdge extends LogEdge implements Comparable {
    private int frequency;

    public FuzzyEdge(FuzzyNode source, FuzzyNode target){
        super(source, target);
        frequency = 0;
    }

    public FuzzyEdge(FuzzyNode source, FuzzyNode target, String label){
        super(source, target, label);
        frequency = 0;
    }

    public FuzzyEdge(FuzzyNode source, FuzzyNode target, int frequency){
        super(source, target);
        this.frequency = frequency;
    }

    public FuzzyEdge(FuzzyNode source, FuzzyNode target, String label, int frequency){
        super(source, target, label);
        this.frequency = frequency;
    }

    public void increaseFrequency() { frequency++; }
    public void increaseFrequency(int amount) { frequency += amount; }

    public int getFrequency(){ return frequency; }

}
