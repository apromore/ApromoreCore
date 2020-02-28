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

package org.apromore.toolbox.clustering.dissimilarity.measure;

import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.clustering.dissimilarity.GEDMatrixCalc;
import org.apromore.toolbox.clustering.dissimilarity.algorithm.CanonicalGEDDeterministicGreedy;

public class CanonicalGEDDeterministicGreedyCalc implements GEDMatrixCalc {

    private double threshold;

    static double ledcutoff = 0.5;
    static double prunewhen = 100.0;
    static double pruneto = 10.0;
    static double vweight = 1.0;
    static double sweight = 1.0;
    static double eweight = 1.0;

    static CanonicalGEDDeterministicGreedy gedepc = new CanonicalGEDDeterministicGreedy();

    public CanonicalGEDDeterministicGreedyCalc(double threshold, double ledc) {
        ledcutoff = ledc;
        Object weights[] = {"vweight", vweight, "sweight", sweight, "eweight", eweight, "ledcutoff", ledcutoff, "prunewhen", prunewhen, "pruneto", pruneto};
        gedepc.setWeight(weights);

        this.threshold = threshold;
    }

    @Override
    public String getName() {
        return "CanonicalGEDDeterministicGreedyCalc";
    }

    @Override
    public double compute(Canonical graph1, Canonical graph2) {
        gedepc.resetDeterminismFlag();
        return gedepc.compute(graph1, graph2);
    }

    @Override
    public boolean isAboveThreshold(double disim) {
        return disim > threshold;
    }

    public boolean isDeterministicGED() {
        return gedepc.isDeterministic();
    }

}
