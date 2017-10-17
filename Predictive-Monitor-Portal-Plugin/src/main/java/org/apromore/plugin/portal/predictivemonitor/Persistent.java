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

// Java 2 Standard Edition
import java.io.File;

// Third party packages
import org.zkoss.zul.ListModelList;

/**
 * Placeholder for database-backed entities.
 */
class Persistent {

    /** This is static only because I've been too lazy to implement proper persistence for it yet. */
    final static ListModelList<Dataflow> dataflows = new ListModelList<>();;

    /** This is static only because I've been too lazy to implement proper persistence for it yet. */
    static ListModelList<Predictor> predictors = null;  // lazily initialized in constructor

    static void initPredictors(File nirdizatiPath, String pythonPath) {
        predictors = new ListModelList<>();
        try { predictors.add(new CaseOutcomePredictor("Slow?", "bpi12", "label", "slow_probability", nirdizatiPath, pythonPath)); } catch (IllegalStateException e) {}
        try { predictors.add(new CaseOutcomePredictor("Slow?", "bpi17", "label", "slow_probability", nirdizatiPath, pythonPath)); } catch (IllegalStateException e) {}
        try { predictors.add(new CaseOutcomePredictor("Rejected?", "bpi17", "label2", "rejected_probability", nirdizatiPath, pythonPath)); } catch (IllegalStateException e) {}
        try { predictors.add(new RemainingTimePredictor("bpi12", nirdizatiPath, pythonPath)); } catch (IllegalStateException e) {}
        try { predictors.add(new RemainingTimePredictor("bpi17", nirdizatiPath, pythonPath)); } catch (IllegalStateException e) {}

        predictors.setMultiple(true);
    }
}
