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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import static java.util.concurrent.TimeUnit.SECONDS;

// Third party packages
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;

public abstract class AbstractPredictor extends ProcessDataflowElement implements Predictor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPredictor.class.getCanonicalName());

    private   File       paramsFile;
    protected JSONObject datasetParamJSON;

    AbstractPredictor(File dir, String... args) {
        super(dir, args);
    }

    protected void foo(File predictiveMethodPath, File pkl, String tag, String label, XLog log, TrainingAlgorithm trainingAlgorithm, File nirdizatiPath, String pythonPath, boolean needRemtime) throws InterruptedException, IOException, JSONException {

        // Validate input parameters
        if (pkl.exists()) {
            throw new IllegalStateException("Predictor \"" + tag + "\" already exists");
        }
        if (tag.length() < 1) {
            throw new IllegalArgumentException("Dataset name required");
        }

        this.paramsFile       = new File(new File(new File(nirdizatiPath, "PredictiveMethods"), "data"), "dataset_params.json");
        this.datasetParamJSON = createDatasetParam(tag, log, trainingAlgorithm, needRemtime);

        // Export the parameters as JSON
        exportDatasetParam();

        // Export the log in CSV format
        File csvFile = new File(new File(new File(nirdizatiPath, "PredictiveMethods"), "data"), ("train_" + tag + ".csv"));
        if (csvFile.exists()) {
            if (!csvFile.delete()) {
                throw new RuntimeException("Predictor CSV already exists; unable to delete it");
            }
        }
        if (!csvFile.createNewFile()) {
            throw new RuntimeException("Unable to create predictor CSV");
        }
        export(log, csvFile);

        // Run the training script in a Python process
        ProcessBuilder pb = new ProcessBuilder(pythonPath, "train.py", tag, label);
        pb.directory(predictiveMethodPath);
        pb.redirectError(new File("/tmp/error.txt") /*File.createTempFile("error", ".txt")*/);
        pb.redirectOutput(new File("/tmp/out.txt") /*File.createTempFile("output", ".txt")*/);
        Process p = pb.start();

        // Await the result of the training script
        if (!p.waitFor(60, SECONDS)) {
            p.destroy();
            throw new RuntimeException("Timed out while trying to create predictor");
        }
        if (p.exitValue() != 0 || !pkl.isFile()) {
            throw new RuntimeException("Unable to create predictor, code=" + p.exitValue() + " pkl=" + pkl.isFile());
        }
    }

    private static void export(XLog log, File file) throws FileNotFoundException {
        LOGGER.info("Exporting log to " + file);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("case_id,Resource,AMOUNT_REQ,proctime,time,elapsed,activity_name,label,event_nr,last,remtime");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            for (XTrace trace: log) {
                XEvent lastEvent = trace.get(trace.size() - 1);
                Date lastTime = ((XAttributeTimestamp) lastEvent.getAttributes().get("time:timestamp")).getValue();
                for (XEvent event: trace) {
                    Date time = ((XAttributeTimestamp) event.getAttributes().get("time:timestamp")).getValue();
                    writer.println(
                        trace.getAttributes().get("concept:name") + "," +
                        event.getAttributes().get("Resource") + "," +
                        event.getAttributes().get("AMOUNT_REQ") + "," +
                        event.getAttributes().get("proctime") + "," +
                        dateFormat.format(time) + "," +
                        event.getAttributes().get("elapsed") + "," +
                        event.getAttributes().get("activity_name") + "," +
                        event.getAttributes().get("label") + "," +
                        event.getAttributes().get("event_nr") + "," +
                        event.getAttributes().get("last") + "," +
                        Long.toString((lastTime.getTime() - time.getTime()) / 1000)
                    );
                }
            }
        }
        LOGGER.info("Exported log to " + file);
    }

    protected JSONObject createDatasetParam(String tag, XLog log, TrainingAlgorithm trainingAlgorithm, boolean needRemtime)
        throws FileNotFoundException, JSONException {

        // Log attribute classification
        for (XAttribute attribute: log.getAttributes().values()) {
            LOGGER.info(" Log attribute: " + attribute.getKey());
        }

        Set<String> staticCols = new HashSet<>();
        for (XAttribute attribute: log.getGlobalTraceAttributes()) {
            LOGGER.info(" Trace attribute: " + attribute.getKey());
            switch (attribute.getKey()) {
            case "concept:name":
            case "variant":
            case "variant-index":
                break;
            default:
                staticCols.add(attribute.getKey());
                break;
            }
        }

        Set<String> dynamicCols = new HashSet<>();
        for (XAttribute attribute: log.getGlobalEventAttributes()) {
            LOGGER.info(" Event attribute: " + attribute.getKey());
            switch (attribute.getKey()) {
            case "concept:name":
            case "lifecycle:transition":
            case "org:resource":
            case "time:timestamp":
            case "event_nr":
            case "label":
            case "last":
                break;
            default:
                dynamicCols.add(attribute.getKey());
                break;
            }
        }
        if (needRemtime) {
            dynamicCols.add("remtime");  // synthetic column
        }

        Set<String> catCols = new HashSet<>();
        for (XEventClassifier classifier: log.getClassifiers()) {
            catCols.addAll(Arrays.asList(classifier.getDefiningAttributeKeys()));
        }

        // Export the parameters as JSON
        JSONObject json = new JSONObject();
        JSONObject jsonTag = new JSONObject();
        json.put(tag, jsonTag);
        jsonTag.put("case_id_col", "case_id");
        jsonTag.put("event_nr_col", "event_nr");
        JSONObject methodJSON = createDatasetParam(jsonTag, trainingAlgorithm);
        methodJSON.put("static_cols", new JSONArray(staticCols));
        methodJSON.put("dynamic_cols", new JSONArray(dynamicCols));
        methodJSON.put("cat_cols", new JSONArray(catCols));

        return json;
    }

    protected void exportDatasetParam() throws FileNotFoundException, JSONException {

        try (PrintWriter writer = new PrintWriter(paramsFile)) {
            writer.println(datasetParamJSON.toString());
        }
    }

    /**
     * @param json  the parent JSON object
     * @return the created JSON object, upon which the <code>static_cols</code>, <code>dynamic_cols</code>, and <code>cat_cols</code> attributes should be placed
     */
    protected abstract JSONObject createDatasetParam(JSONObject json, TrainingAlgorithm traininingAlgorithm) throws JSONException;

    @Override
    public void start(String kafkaHost, String prefixesTopic, String predictionsTopic) throws PredictorException {

        // Export the parameters as JSON again (they've been overwritten by other predictors at this point)
        try {
            exportDatasetParam();

        } catch (FileNotFoundException | JSONException e) {
            throw new PredictorException("Unable to export dataset parameter file", e);
        }

        super.start(kafkaHost, prefixesTopic, predictionsTopic);
    }

    // Predictor methods

    public abstract String getName();

    public abstract String getTrainingLog();

    public abstract String getParameters();

    public abstract void addHeaders(Listhead head);

    public abstract void addCells(Listitem item, DataflowEvent event);
}
