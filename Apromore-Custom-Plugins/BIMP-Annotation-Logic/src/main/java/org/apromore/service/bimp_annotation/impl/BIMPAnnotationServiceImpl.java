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

package org.apromore.service.bimp_annotation.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.apromore.service.bimp_annotation.BIMPAnnotationService;

/**
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Service
public class BIMPAnnotationServiceImpl implements BIMPAnnotationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BIMPAnnotationServiceImpl.class);

    private final String pythonExecutable;
    private final File simoScript;
    private final int timeout;

    @Inject
    public BIMPAnnotationServiceImpl(
            @Qualifier("python") final String pythonExecutable,
            @Qualifier("simo") final File simoScript,
            @Qualifier("timeout") final Integer timeout) {
        this.pythonExecutable = pythonExecutable;
        this.simoScript = simoScript;
        this.timeout = timeout;
    }

    /** Calculates the overall progress of a task broken into subtask intervals. */
    private static class ProgressLogic {

        /** A subtask interval. */
        private static class Interval {
            double start;
            double duration;
            Interval(double start, double duration) { this.start = start;  this.duration = duration; }
        };

        private Map<String, Interval> intervalForDescription = new HashMap<>();
        double totalDuration = 0;

        /**
         * Add a new subtask interval.
         *
         * You must call this at least once before {@link #fractionComplete}.
         *
         * @param description a name that hasn't previously been added
         * @param duration a value greater than zero
         * @return this object
         * @throws IllegalArgumentException if <i>description</i> has already been added or <i>duration</i> isn't positive
         */
        ProgressLogic add(String description, double duration) {

            // Validation
            if (intervalForDescription.containsKey(description)) {
                throw new IllegalArgumentException(description + " subtask already exists");
            }
            if (duration <= 0) {
                throw new IllegalArgumentException("Duration must be positive, was " + duration);
            }

            // Add the new subtask interval
            intervalForDescription.put(description, new Interval(totalDuration, duration));
            totalDuration += duration;

            return this;
        }

        /**
         * @param description  a subtask description, previously registered by the {@link #add} method
         * @param fractionComplete  how complete the subtask is, a value in the range 0..1
         * @return how complete the total task is, a value in the range 0..1
         */
        Double fractionComplete(String description, double fractionComplete) {
            Interval interval = intervalForDescription.get(description);
            if (interval == null) { return null; }
            return (interval.start + interval.duration * fractionComplete) / totalDuration;
        }
    }

    @Override
    public String annotateBPMNModelForBIMP(String model, XLog log, BIMPAnnotationService.Context context)
        throws IOException, InterruptedException, TimeoutException {

        LOGGER.info("Annotating BPMN model for BIMP, python = " + pythonExecutable);

        // Data is passed to Python via scratch files
        File inputLog = File.createTempFile("inputLog_", ".xes", null);
        File inputModel = File.createTempFile("inputModel_", ".bpmn", null);
        File outputModel = File.createTempFile("outputModel_", ".bpmn", null);

        // Write the log to its scratch file
        try (FileOutputStream out = new FileOutputStream(inputLog)) {
            LOGGER.info("Serializing log to " + inputLog);
            (new XesXmlSerializer()).serialize(log, out);
        }

        // Write the process model to its scratch file
        try (FileWriter writer = new FileWriter(inputModel)) {
            LOGGER.info("Serializing model to " + inputModel);
             writer.write(model);
        }

        // Execute the Python script
        ProcessBuilder pb = new ProcessBuilder(pythonExecutable, simoScript.getName(), inputLog.toString(), inputModel.toString(), outputModel.toString());
        pb.directory(simoScript.getParentFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));  // gather any error messages
        StringWriter messages = new StringWriter();

        // Spawn a thread to parse the  script's progress messages
        Runnable runnable = new Runnable() {
            public void run() {
                PrintWriter print = new PrintWriter(messages);
                Matcher matcher = Pattern.compile("(?<description>.*)\\s(?<fractionComplete>\\d+\\.\\d+)%\\.\\.\\.\\s*(\\[DONE\\])?")
                                         .matcher("");;
                ProgressLogic logic = new ProgressLogic()
                    .add("Loading of bpmn structure from file", 70)
                    .add("Analysing resource pool", 130)
                    .add("Replaying process traces", 380)
                    .add("Defining inter-arrival rate", 14)
                    .add("Analysing gateways probabilities", 1)
                    .add("Analysing tasks data", 1100);
                
                String currentDescription = null;
                do {
                    try {
                        String line = reader.readLine();
                        if (line == null) {
                            LOGGER.info("Script output complete");
                            return;
                        }
                        if (!p.isAlive()) {
                            LOGGER.info("Script terminated");
                            return;
                        }

                        matcher.reset(line);
                        if (matcher.matches()) {
                            if (context != null) {
                                String description = matcher.group("description");

                                context.setFractionComplete(logic.fractionComplete(description, 0.01 * Double.valueOf(matcher.group("fractionComplete"))));

                                if (!Objects.equals(description, currentDescription)) {
                                    context.setDescription(description);
                                    print.println(description);
                                    currentDescription = description;
                                }
                                assert Objects.equals(description, currentDescription);
                            }
                        } else {
                            print.println(line);
                        }

                    } catch (IOException e) {
                        LOGGER.warn("Exception parsing script output", e);
                    }

                } while (true);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        // Block and await Python execution
        if (!p.waitFor(timeout, SECONDS)) {
            p.destroy();
            throw new TimeoutException("Timed out waiting for BIMP annotation");
        }

        // See if the Python script executed successfully
        assert !p.isAlive();
        if (p.exitValue() == 0) {
            // Read the annotated process model back from the scratch file
            LOGGER.info("Obtaining annotated model from " + outputModel);
            model = new BufferedReader(new FileReader(outputModel)).lines().collect(Collectors.joining("\n"));

            // Delete all the scratch files
            outputModel.delete();
            inputModel.delete();
            inputLog.delete();
            return model;

        } else {
            // Fail, hopefully with a useful diagnostic message
            throw new RuntimeException("Exited with error code " + p.exitValue() + "\n" + messages.toString());
        }
    }
}
