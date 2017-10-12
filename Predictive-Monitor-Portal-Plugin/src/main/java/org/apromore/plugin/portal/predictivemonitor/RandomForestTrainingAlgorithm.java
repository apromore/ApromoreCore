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

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Editions
import java.io.PrintWriter;

// Third party packages
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Window;

/**
 * A machine learning training algorithm.
 */
public class RandomForestTrainingAlgorithm implements TrainingAlgorithm {

    private int    nEstimators;
    private double maxFeatures;

    public String getName() {
        return "Random Forest";
    }

    public void readParametersFromUI(Window window) {
        nEstimators  = ((Decimalbox) window.getFellow("nEstimators")).intValue();
        maxFeatures  = ((Decimalbox) window.getFellow("maxFeatures")).doubleValue();
    }

    public void writeParametersToPython(PrintWriter writer) {
        writer.println(
            "# Training algorithm parameters\n" +
            "cls_method = {}\n" +
            "n_estimators = {}\n" +
            "max_features = {}\n" +
            "\n" +
            "cls_method[dataset] = \"rf\"\n" +
            "n_estimators[dataset] = " + nEstimators + "\n" +
            "max_features[dataset] = " + maxFeatures);
    }
}
