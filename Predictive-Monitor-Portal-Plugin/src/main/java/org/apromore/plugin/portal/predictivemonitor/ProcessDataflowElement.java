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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import static java.util.concurrent.TimeUnit.SECONDS;

// Third party packages
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * A running {@link Process} packaged as a {@link DataflowElement}.
 */
public class ProcessDataflowElement implements DataflowElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDataflowElement.class.getCanonicalName());

    private final File     dir;
    protected final String[] args;

    /**
     * @param dir  the directory in which the process should execute
     * @param args  the command line arguments to start the process
     */
    ProcessDataflowElement(File dir, String... args) {
        this.dir  = dir;
        this.args = args;
    }

    final private List<Process> processors = new ArrayList<>();

    public void start(String kafkaHost, String prefixesTopic, String predictionsTopic) throws PredictorException {
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(dir);
        pb.redirectError(new File("/tmp/error.txt") /*File.createTempFile("error", ".txt")*/);
        pb.redirectOutput(new File("/tmp/out.txt") /*File.createTempFile("output", ".txt")*/);
        try {
            Process p = pb.start();
            processors.add(p);
        } catch (IOException | RuntimeException e) {
            throw new PredictorException("Unable to start, args were: " + arrayToArrayList(args), e);
        }
    }

    private static <T> ArrayList<T> arrayToArrayList(T[] array) {
        ArrayList<T> list = new ArrayList<T>();
        for(T elmt : array) list.add(elmt);
        return list;
    }

    public void stop() {
        while (!processors.isEmpty()) {
            Process p = processors.get(0);
            LOGGER.info("Killing process " + p + ", alive? " + p.isAlive());
            p.destroy();
            try {
                int code = p.waitFor();
                LOGGER.info("Killed process with return code " + code);
                processors.remove(p);

            } catch (InterruptedException | RuntimeException e) {
                LOGGER.warn("Unable to kill process", e);
            }
        }
    }

    public void delete() {
        // null implementation
    }
}
