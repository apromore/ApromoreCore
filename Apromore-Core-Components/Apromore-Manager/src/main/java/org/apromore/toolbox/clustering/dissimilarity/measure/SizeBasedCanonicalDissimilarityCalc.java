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

public class SizeBasedCanonicalDissimilarityCalc implements GEDMatrixCalc {

    private double threshold;

    double weightSkippedVertex = 1.0;
    double weightSubstitutedVertex = 1.0;
    double weightSkippedEdge = 1.0;

    public SizeBasedCanonicalDissimilarityCalc(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public String getName() {
        return "SizeBasedCanonicalDissimilarityCalc";
    }

    @Override
    public double compute(Canonical graph1, Canonical graph2) {
        double totalNrVertices = graph1.getVertices().size() + graph2.getVertices().size();
        double totalNrEdges = graph1.getEdges().size() + graph2.getEdges().size();

        double vskip = Math.abs(graph1.getVertices().size() - graph2.getVertices().size()) / totalNrVertices;
        double eskip = Math.abs(graph1.getEdges().size() - graph2.getEdges().size()) / totalNrEdges;
        double vsubs = 0.0;

        return ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs) + (weightSkippedEdge * eskip)) / (weightSkippedVertex + weightSubstitutedVertex + weightSkippedEdge);
    }

    @Override
    public boolean isAboveThreshold(double disim) {
        return disim > threshold;
    }
}
