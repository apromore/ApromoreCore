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
                                context.setFractionComplete(Double.valueOf(matcher.group("fractionComplete")));
                                context.setDescription(matcher.group("description"));
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
