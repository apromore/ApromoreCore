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

// Java 2 Standard Edition
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

// Third party packages
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

public class CaseOutcomePredictor extends AbstractPredictor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseOutcomePredictor.class.getCanonicalName());

    private final String headerText;
    private final String tag;
    private final String label;
    private final String positiveLabelValue;
    private final String jsonField;
    private final String trainingLog;
    private final File   nirdizatiPath;
    private final String pythonPath;

    private final File pkl;

    CaseOutcomePredictor(String headerText, String tag, String label, String positiveLabelValue, String jsonField, String trainingLog, XLog log, TrainingAlgorithm trainingAlgorithm, File nirdizatiPath, String pythonPath) throws InterruptedException, IOException, JSONException {

        super(nirdizatiPath, pythonPath, "PredictiveMethods/CaseOutcome/case-outcome-kafka-processor.py", /*kafkaHost, prefixesTopic, predictionsTopic,*/ null, null, null, tag, /*datasetParamJSON*/ null, label, jsonField);

        this.headerText    = headerText;
        this.tag           = tag;
        this.label         = label;
        this.positiveLabelValue = positiveLabelValue;
        this.jsonField     = jsonField;
        this.trainingLog   = trainingLog;
        this.nirdizatiPath = nirdizatiPath;
        this.pythonPath    = pythonPath;

        File predictiveMethodPath = new File(new File(nirdizatiPath, "PredictiveMethods"), "CaseOutcome");

        // Validate form fields
        pkl = new File(new File(new File(nirdizatiPath, "PredictiveMethods"), "pkl"), "predictive_monitor_" + tag + "_" + label + ".pkl");
        foo(predictiveMethodPath, pkl, tag, label, log, trainingAlgorithm, nirdizatiPath, pythonPath, false);
    }

    protected JSONObject createDatasetParam(JSONObject json, TrainingAlgorithm trainingAlgorithm) throws JSONException {
        JSONObject caseOutcomeJSON = new JSONObject();
        json.put("CaseOutcome", caseOutcomeJSON);
        JSONObject labelJSON = new JSONObject();
        caseOutcomeJSON.put(this.label, labelJSON);
        labelJSON.put("pos_label", this.positiveLabelValue);
        trainingAlgorithm.addParametersToJSON(labelJSON);

        return caseOutcomeJSON;
    }

    public String getName() {
        return "Case outcome: " + headerText;
    }

    public String getTrainingLog() {
        return trainingLog;
    }

    public String getParameters() {
        return "dataset: " + tag + ", label: " + label;
    }

    @Override
    public void start(String kafkaHost, String prefixesTopic, String predictionsTopic) throws PredictorException {
        this.args[2] = kafkaHost;
        this.args[3] = prefixesTopic;
        this.args[4] = predictionsTopic;
        this.args[6] = datasetParamJSON.toString();
        super.start(kafkaHost, prefixesTopic, predictionsTopic);
    }

    public void addHeaders(Listhead head) {
        head.appendChild(new Listheader(headerText));
    }

    public void addCells(Listitem item, DataflowEvent event) {
        String s;
        try {
            s = NumberFormat.getPercentInstance().format((event.getJSON().getJSONObject("outcomes").getDouble(jsonField)));
        } catch (JSONException e) {
            s = "";
        }
        item.appendChild(new Listcell(s));
    }

    @Override
    public void delete() {
        final boolean success = pkl.delete();
    }
}
