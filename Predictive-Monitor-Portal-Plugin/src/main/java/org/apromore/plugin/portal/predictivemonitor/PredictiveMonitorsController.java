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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

// Third party packages
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import static org.zkoss.zul.event.ListDataEvent.INTERVAL_REMOVED;

// Local packages
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

/**
 * In MVC terms, this is a controller whose corresponding model is {@link Dataflow} and corresponding view is <code>predictive_monitor.zul</code>.
 */
public class PredictiveMonitorsController {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictiveMonitorsController.class.getCanonicalName());
    private static String eventsTopic;

    private final Window window;

    private final Listbox predictiveMonitorsListbox;

    public PredictiveMonitorsController(PortalContext portalContext, EventLogService eventLogService, PredictiveMonitorService predictiveMonitorService, String kafkaHost, String eventsTopic) throws IOException {

        this.eventsTopic = eventsTopic;

        window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/predictiveMonitors.zul", null, null);

        final ListModelList<PredictiveMonitor> predictiveMonitorsListModel = Persistent.getPredictiveMonitorsListModel(predictiveMonitorService);

        predictiveMonitorsListbox = (Listbox) window.getFellow("predictiveMonitors");
        predictiveMonitorsListbox.setModel(predictiveMonitorsListModel);

        // Find the selected log
        Set<LogSummaryType> logSummaries = findSelectedLogs(portalContext);
        if (logSummaries.size() != 1) {
            Messagebox.show("Select exactly one log", "Attention", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        LogSummaryType logSummary = logSummaries.iterator().next();
        XLog log = eventLogService.getXLog(logSummary.getId());

        // Bind window components

        ((Button) window.getFellow("create")).addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                new CreatePredictiveMonitorController(portalContext, predictiveMonitorService);
            }
        });

        ((Button) window.getFellow("delete")).addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                predictiveMonitorService.deletePredictiveMonitors(predictiveMonitorsListModel.getSelection());
                predictiveMonitorsListModel.removeAll(predictiveMonitorsListModel.getSelection());
            }
        });

        ((Button) window.getFellow("load")).addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                LOGGER.info("Loading");
                for (PredictiveMonitor predictiveMonitor: predictiveMonitorsListModel.getSelection()) {
                    //predictiveMonitorService.exportLogToPredictiveMonitor(log, predictiveMonitor);  TODO: switch to this instead of the local export() method
                    export(log, predictiveMonitor, kafkaHost);
                }
                LOGGER.info("Loaded");
            }
        });

        ((Button) window.getFellow("monitor")).addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                LOGGER.info("Monitoring");
                Desktop desktop = window.getDesktop();
                window.detach();
                for (PredictiveMonitor predictiveMonitor: predictiveMonitorsListModel.getSelection()) {
                    new PredictiveMonitorController(portalContext, predictiveMonitor, predictiveMonitorService);
                }
                LOGGER.info("Monitored");
            }
        });

        window.doModal();
    }

    static Set<LogSummaryType> findSelectedLogs(PortalContext context) {
        Map<SummaryType, List<VersionSummaryType>> elements = context.getSelection().getSelectedProcessModelVersions();
        Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof LogSummaryType) {
                selectedLogSummaryType.add((LogSummaryType) entry.getKey());
            }
        }
        return selectedLogSummaryType;
    }

    private static void export(XLog log, PredictiveMonitor predictiveMonitor, String kafkaHost) {
        LOGGER.info("Exporting log to " + predictiveMonitor.getName());
        // Configuration
        Properties props = new Properties();
        props.put("bootstrap.servers",  kafkaHost);
        props.put("acks",               "all");
        props.put("retries",            0);
        props.put("batch.size",         16384);
        props.put("linger.ms",          1);
        //props.put("timeout.ms",         5000);
        props.put("max.block.ms",       5000);
        //props.put("metadata.fetch.timeout.ms", 5000);
        //props.put("request.timeout.ms", 5000);
        props.put("buffer.memory",      33554432);
        props.put("key.serializer",     StringSerializer.class);
        props.put("value.serializer",   StringSerializer.class);

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {

            // Merge the XES events of all traces into a single list of JSON objects
            List<JSONObject> jsons = new ArrayList<>();
            for (XTrace trace: log) {
                for (XEvent event: trace) {
                    JSONObject json = new JSONObject();
                    json.put("log", predictiveMonitor.getId());
                    json.put("case_id", trace.getAttributes().get("concept:name").toString());
                    addAttributes(log, json);
                    addAttributes(trace, json);
                    addAttributes(event, json);
                    jsons.add(json);
                }
            }

            // Sort the events by time
            final DatatypeFactory f = DatatypeFactory.newInstance();
            Collections.sort(jsons, new Comparator<JSONObject>() {
                public int compare(JSONObject a, JSONObject b) {
                    return f.newXMLGregorianCalendar(a.optString("time:timestamp")).compare(f.newXMLGregorianCalendar(b.optString("time:timestamp")));
                }
            });

            // Export to the Kafka topic
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            JSONArray columns = new JSONArray();
            for (Predictor predictor: predictiveMonitor.getPredictors()) {
                columns.put(predictor.getId());
            }
            for (JSONObject json: jsons) {
                //json.put("time:timestamp", dateFormat.format(f.newXMLGregorianCalendar(json.optString("time:timestamp")).toGregorianCalendar().getTime()));
                json.put("Activity_Start_Time", dateFormat.format(f.newXMLGregorianCalendar(json.optString("time:timestamp")).toGregorianCalendar().getTime()));
                json.put("Activity_End_Time", dateFormat.format(f.newXMLGregorianCalendar(json.optString("time:timestamp")).toGregorianCalendar().getTime()));
                json.put("Key", "1");
                json.put("columns", columns);
                producer.send(new ProducerRecord<String,String>(eventsTopic, json.toString()));
            }
            LOGGER.info("Exported " + jsons.size() + " log events");
            Messagebox.show("Exported " + jsons.size() + " log events", "Attention", Messagebox.OK, Messagebox.INFORMATION);

        } catch (DatatypeConfigurationException | JSONException e) {
            //Messagebox.show("Unable to export log", "Attention", Messagebox.OK, Messagebox.ERROR);
            LOGGER.error("Unable to export log events", e);
            return;
        }
        LOGGER.info("Exported log to " + predictiveMonitor.getName());
    }

    private static void addAttributes(XAttributable attributable, JSONObject json) throws JSONException {
        for (Map.Entry<String, XAttribute> entry: attributable.getAttributes().entrySet()) {
            switch (entry.getKey()) {
            case "concept:name":
            case "creator":
            case "library":
            case "lifecycle:model":
            case "lifecycle:transition":
            case "org:resource":
            case "variant":
            case "variant-index":
                break;
/*
            case "time:timestamp":
                json.put("time", entry.getValue().toString());
                break;
*/
            default:
                json.put(entry.getKey(), entry.getValue().toString());
                break;
            }
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
}
