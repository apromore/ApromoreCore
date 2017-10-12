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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;

public abstract class AbstractPredictor extends ProcessDataflowElement implements Predictor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPredictor.class.getCanonicalName());

    AbstractPredictor(File dir, String... args) {
        super(dir, args);
    }

    protected static void foo(File predictiveMethodPath, File pkl, String tag, String label, XLog log, TrainingAlgorithm trainingAlgorithm, File nirdizatiPath, String pythonPath, boolean needRemtime) throws InterruptedException, IOException {

        // Validate input parameters
        if (pkl.exists()) {
            throw new IllegalStateException("Predictor \"" + tag + "\" already exists");
        }
        if (tag.length() < 1) {
            throw new IllegalArgumentException("Dataset name required");
        }

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
        LOGGER.info("Static columns: " + formatAsPythonArray(staticCols));

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
        LOGGER.info("Dynamic columns: " + formatAsPythonArray(dynamicCols));

        Set<String> catCols = new HashSet<>();
        for (XEventClassifier classifier: log.getClassifiers()) {
            catCols.addAll(Arrays.asList(classifier.getDefiningAttributeKeys()));
        }
        LOGGER.info("CatCols: " + formatAsPythonArray(catCols));

        // Export the parameters as Python source code
        File paramsFile = new File(new File(predictiveMethodPath, "batch"), "dataset_params.py");
        try (PrintWriter writer = new PrintWriter(paramsFile)) {
            writer.println(
                "case_id_col = {}\n" +
                "event_nr_col = {}\n" +
                "label_col = {}\n" +
                "pos_label = {}\n" +
                "\n" +
                "static_cols = {}\n" +
                "dynamic_cols = {}\n" +
                "cat_cols = {}\n" +
                "\n" +
                "# Dataset parameters\n" +
                "dataset = \"" + tag + "\"\n" +
                "case_id_col[dataset] = \"case_id\"\n" +
                "event_nr_col[dataset] = \"event_nr\"\n" +
                "label_col[dataset] = \"" + label + "\"\n" +
                "pos_label[dataset] = \"true\"\n"+
                "\n" +
                "static_cols[dataset] = " + formatAsPythonArray(staticCols) + "\n" +
                "dynamic_cols[dataset] = " + formatAsPythonArray(dynamicCols) + "\n" +
                "cat_cols[dataset] = " + formatAsPythonArray(catCols) + "\n" +
                "\n");
            trainingAlgorithm.writeParametersToPython(writer);
        }

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
        pb.redirectError(File.createTempFile("error", ".txt"));
        pb.redirectOutput(File.createTempFile("output", ".txt"));
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

    private static String formatAsPythonArray(Collection<String> strings) {
        String s = "[";
        for (String string: strings) {
            if (!"[".equals(s)) { s += ", "; }
            s += "\"" + string + "\"";
        }
        s += "]";
        return s;
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

    // Predictor methods

    public abstract String getName();

    public abstract String getTrainingLog();

    public abstract String getParameters();

    public abstract void addHeaders(Listhead head);

    public abstract void addCells(Listitem item, DataflowEvent event);
}
