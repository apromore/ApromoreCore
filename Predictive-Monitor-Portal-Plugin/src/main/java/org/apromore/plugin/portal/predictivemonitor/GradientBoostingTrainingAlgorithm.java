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

package org.apromore.plugin.portal.predictivemonitor;

// Third party packages
import org.json.JSONException;
import org.json.JSONObject;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Window;

/**
 * A machine learning training algorithm.
 */
public class GradientBoostingTrainingAlgorithm implements TrainingAlgorithm {

    private int    nEstimators;
    private double maxFeatures;
    private double learningRate;

    public String getName() {
        return "Gradient Boosted Model";
    }

    public void readParametersFromUI(Window window) {
        nEstimators  = ((Decimalbox) window.getFellow("nEstimators")).intValue();
        maxFeatures  = ((Decimalbox) window.getFellow("maxFeatures")).doubleValue();
        learningRate = ((Decimalbox) window.getFellow("learningRate")).doubleValue();
    }

    public void addParametersToJSON(JSONObject json) throws JSONException {
        json.put("cls_method", "gbm");
        json.put("n_estimators", nEstimators);
        json.put("max_features", maxFeatures);
        json.put("learning_rate", learningRate);
    }
}
