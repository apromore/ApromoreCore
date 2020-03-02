/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import javax.xml.datatype.DatatypeFactory;

// Third party packages
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.ext.SelectionControl;;

// Local packages
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.PredictiveMonitorEvent;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

/**
 * In MVC terms, this is a controller whose corresponding model is a {@link PredictiveMonitor} and corresponding view is <code>predictiveMonitor.zul</code>.
 */
public class PredictiveMonitorController implements Observer {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictiveMonitorController.class.getCanonicalName());
    private static DatatypeFactory f;

    static {
        try {
            f = DatatypeFactory.newInstance();
        } catch (Exception e) {
            LOGGER.error("Unable to initialize DatatypeFactory", e);
        }
    }

    private final PredictiveMonitor predictiveMonitor;
    private final PredictiveMonitorService predictiveMonitorService;
    private final Window window;

    private final List<Column> columnList = new ArrayList<Column>();

    private final Label runningCasesLabel;
    private final Label completedCasesLabel;
    private final Label completedEventsLabel;
    private final Label averageCaseLengthLabel;
    private final Label averageCaseDurationLabel;
    private final Textbox filterCaseTextbox;

    private final NumberFormat numberFormat = new DecimalFormat("0.##");

    private final PredictiveMonitorListModel eventsModel;
    private final PredictiveMonitorListModel casesModel;

    private final List<PredictiveMonitorEvent> events = new ArrayList<>();
    private final List<PredictiveMonitorEvent> caseEvents = new ArrayList<>();
    private final Map<String, PredictiveMonitorEvent> caseFirstEventMap = new HashMap<>();

    private final Listbox eventsListbox;

    /**
     * @param predictiveMonitor  never <code>null</code>
     */
    public PredictiveMonitorController(final PortalContext portalContext, PredictiveMonitor predictiveMonitor, PredictiveMonitorService predictiveMonitorService) throws IOException {

        this.predictiveMonitor = predictiveMonitor;
        this.predictiveMonitorService = predictiveMonitorService;
        this.window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/predictiveMonitor.zul", null, null);
        this.eventsModel = new PredictiveMonitorListModel(predictiveMonitorService, predictiveMonitor, events);
        this.casesModel = new PredictiveMonitorListModel(predictiveMonitorService, predictiveMonitor, caseEvents);

        addColumns(columnList, predictiveMonitor.getPredictors(), caseFirstEventMap);

        /*Listbox*/ eventsListbox    = (Listbox) window.getFellow("events");
        Listbox casesListbox     = (Listbox) window.getFellow("cases");

        runningCasesLabel        = (Label) window.getFellow("runningCases");
        completedCasesLabel      = (Label) window.getFellow("completedCases");
        completedEventsLabel     = (Label) window.getFellow("completedEvents");
        averageCaseLengthLabel   = (Label) window.getFellow("averageCaseLength");
        averageCaseDurationLabel = (Label) window.getFellow("averageCaseDuration");
        filterCaseTextbox        = (Textbox) window.getFellow("filterCase");

        // Button callback
        ((Button) window.getFellow("csv")).addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                try {
                    File file = File.createTempFile("log-", ".csv");
                    exportCSV(new FileOutputStream(file));
                    Filedownload.save(new FileInputStream(file), "text/csv", "log.csv");  // MIME specified by RFC 7111 (https://tools.ietf.org/html/rfc7111)
                    file.delete();
                }
                catch (Exception e) {
                    Messagebox.show("Export to CSV failed", "Attention", Messagebox.OK, Messagebox.ERROR);
                    LOGGER.error("Export to CSV failed", e);
                }
            }
        });
        
        // Add header columns
        for (Column column: columnList) {
            eventsListbox.getListhead().appendChild(column.getListheader());
            casesListbox.getListhead().appendChild(column.getListheader());
        }

        eventsListbox.setRows(15);  // TODO: figure out how to make vflex work with this
        casesListbox.setRows(15);
        ListitemRenderer<PredictiveMonitorEvent> renderer = new ListitemRenderer<PredictiveMonitorEvent> () {
            public void render(Listitem item, PredictiveMonitorEvent event, int index) {
                try {
                    JSONObject json = new JSONObject(event.getJson());

                    // Highlight rows in green if the magical "last" column says they're the final event in a case
                    item.setStyle("true".equals(json.optString("last")) ? "background-color: #EEFFEE" : "");

                    for (Column column: columnList) {
                        item.appendChild(column.getListcell(event));
                    }
                } catch (Exception e) {
                    LOGGER.warn("Unable to render event", e);
                }
            }
        };
        eventsListbox.setItemRenderer(renderer);
        eventsListbox.setModel(eventsModel);
        casesListbox.setItemRenderer(renderer);
        casesListbox.setModel(casesModel);
        predictiveMonitorService.addObserver(this);
        reload();

        window.doModal();
    }

    private static void addColumns(List<Column> columnList, Set<Predictor> predictors, Map<String, PredictiveMonitorEvent> caseFirstEventMap) {
        columnList.add(new ColumnImpl("Case", new ColumnImpl.EventFormatter() {
            public String format(PredictiveMonitorEvent event) {
                return event.getCaseId();
            }
        }));

        columnList.add(new ColumnImpl("Event #", new ColumnImpl.EventFormatter() {
            public String format(PredictiveMonitorEvent event) {
                return event.getEventNr().toString();
            }
        }));

        columnList.add(new ColumnImpl("Activity", new ColumnImpl.EventFormatter() {
            public String format(PredictiveMonitorEvent event) {
                try {
                    JSONObject json = new JSONObject(event.getJson());
                    return json.getString("concept:name");
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
        }));

        columnList.add(new ColumnImpl("Event time", new ColumnImpl.EventFormatter() {
            private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd h:mm:ss a");

            private String formatTime(Date date) { return date == null ? "" : dateFormat.format(date); }

            public String format(PredictiveMonitorEvent event) {
                try {
                    JSONObject json = new JSONObject(event.getJson());
                    return formatTime(f.newXMLGregorianCalendar(json.getString("time:timestamp")).toGregorianCalendar().getTime());
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
        }));

        columnList.add(new ColumnImpl("Elapsed", new ColumnImpl.EventFormatter() {
            public String format(PredictiveMonitorEvent event) {
                try {
                    PredictiveMonitorEvent firstEvent = caseFirstEventMap.get(event.getCaseId());
                    JSONObject startJson = new JSONObject(firstEvent.getJson());
                    Date start = f.newXMLGregorianCalendar(startJson.getString("time:timestamp")).toGregorianCalendar().getTime();

                    JSONObject json = new JSONObject(event.getJson());
                    Date timestamp = f.newXMLGregorianCalendar(json.getString("time:timestamp")).toGregorianCalendar().getTime();

                    return PredictiveMonitorController.format(Duration.between(Instant.ofEpochMilli(start.getTime()), Instant.ofEpochMilli(timestamp.getTime())));
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
        }));

        for (Predictor predictor: predictors) {
            switch (predictor.getType()) {
            case "next":
                columnList.add(new AbstractColumn("Next activity") {
                    private List<String> histogramKeyList = null;

                    private String format(PredictiveMonitorEvent event) {
                        try {
                            JSONObject json = new JSONObject(event.getJson());
                            if ("true".equals(json.optString("last"))) { return "-"; }
                            JSONObject jsonPredictions = json.optJSONObject("predictions");
                            JSONObject histogram = jsonPredictions.optJSONObject("next");
                            if (histogram == null) { return "..."; }
                            
                            Iterator i;

                            // Populate the histogram key list so that we know what CSV header columns should exist
                            if (histogramKeyList == null) {
                                histogramKeyList = new ArrayList<String>();
                                i = histogram.keys();
                                while (i.hasNext()) {
                                    histogramKeyList.add((String) i.next());
                                }
                            }

                            String value = "-";
                            double currentHighestProbability = -1;
                            i = histogram.keys();
                            while (i.hasNext()) {
                                String key = (String) i.next();
                                double probability = histogram.getDouble(key);
                                if (probability > currentHighestProbability) {
                                    value = NumberFormat.getPercentInstance().format(probability) + " " + key;
                                    currentHighestProbability = probability;
                                }
                            }
                            return value;
                            
                        } catch (Exception e) {
                            return e.getMessage();
                        }
                    }

                    public Listcell getListcell(PredictiveMonitorEvent event) {
                        return new Listcell(format(event));
                    }

                    @Override public String getCSVHeader() {
                        if (histogramKeyList == null) {
                            throw new IllegalStateException("At least one event with a next activity prediction must be present");
                        }

                        String header = "";
                        Iterator<String> i = histogramKeyList.iterator();
                        while (i.hasNext()) {
                            header += "next activity: " + i.next();
                            if (i.hasNext()) { header += ","; }
                        }
                        return header;
                    }

                    public String getCSVItem(PredictiveMonitorEvent event, JSONObject json) {
                        JSONObject histogram = null;
                        try {
                            histogram = json.getJSONObject("predictions").optJSONObject("next");
                        } catch (JSONException e) {}

                        String item = "";
                        Iterator<String> i = histogramKeyList.iterator();
                        while (i.hasNext()) {
                            String key = i.next();
                            if (histogram != null) {
                                try { item += NumberFormat.getPercentInstance().format(histogram.getDouble(key)); } catch (JSONException e) {}
                            }
                            if (i.hasNext()) { item += ","; }
                        }
                        return item;
                    }
                });
                break;
                    
            case "remtime":
                columnList.add(new ColumnImpl("Predicted case end", new ColumnImpl.EventFormatter() {
                    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd h:mm:ss a");
                    private String formatTime(Date date) { return date == null ? "" : dateFormat.format(date); }
                    public String format(PredictiveMonitorEvent event) {
                        try {
                            JSONObject json = new JSONObject(event.getJson());
                            if ("true".equals(json.optString("last"))) { return "Complete"; }
                            JSONObject jsonPredictions = json.optJSONObject("predictions");
                            JSONObject remtime = jsonPredictions.optJSONObject("remtime");
                            if (remtime == null) { return "..."; }
                            
                            int remainingSeconds = remtime.getInt("remtime");
                            //final DatatypeFactory f = DatatypeFactory.newInstance();
                            Calendar calendar = f.newXMLGregorianCalendar(json.getString("time:timestamp")).toGregorianCalendar();
                            calendar.add(Calendar.SECOND, remainingSeconds);
                            return formatTime(calendar.getTime());
                            
                        } catch (Exception e) {
                            return e.getMessage();
                        }
                    }
                }));
                columnList.add(new ColumnImpl("Remaining time", new ColumnImpl.EventFormatter() {
                    public String format(PredictiveMonitorEvent event) {
                        try {
                            JSONObject json = new JSONObject(event.getJson());
                            if ("true".equals(json.optString("last"))) { return "-"; }
                            JSONObject jsonPredictions = json.optJSONObject("predictions");
                            JSONObject remtime = jsonPredictions.optJSONObject("remtime");
                            if (remtime == null) { return "..."; }
                            return PredictiveMonitorController.format(Duration.ofSeconds(remtime.getLong("remtime")));

                        } catch (Exception e) {
                            return e.getMessage();
                        }
                    }
                }));
                break;

            default:
                try {
                    Double threshold = Double.valueOf(predictor.getType());
                    String header = "Case outcome > " + (threshold < 0 ? "median" : (predictor.getType() + "s"));

                    columnList.add(new AbstractColumn(header) {
                        private String mostProbableHistogramKey(JSONObject histogram) throws JSONException {
                            String mostProbableKey = null;
                            double currentHighestProbability = -1;
                            Iterator<String> i = histogram.keys();
                            while (i.hasNext()) {
                                String key = i.next();
                                double probability = histogram.getDouble(key);
                                if (probability > currentHighestProbability) {
                                    mostProbableKey = key;
                                    currentHighestProbability = probability;
                                }
                            }
                            return mostProbableKey;
                        }

                        public Listcell getListcell(PredictiveMonitorEvent event) {
                            String value = "-";
                            String style = null;
                            try {
                                Double threshold = Double.valueOf(predictor.getType());
                                try {
                                    double currentHighestProbability = -1;
                                    JSONObject json = new JSONObject(event.getJson());
                                    JSONObject jsonPredictions = json.optJSONObject("predictions");
                                    JSONObject histogram = jsonPredictions.optJSONObject(predictor.getType());
                                    if (histogram == null) { value = "..."; }

                                    String key = mostProbableHistogramKey(histogram);
                                    double probability = histogram.getDouble(key);
                                    int brightness = 155 + Math.min(100, (new Double((2.0 - 2.0 * probability) * 100)).intValue());
                                    switch (key) {
                                        case "true":
                                            value = NumberFormat.getPercentInstance().format(probability) + " slow";
                                            style = "background-color: rgb(" + 255 + ", " + brightness + ", " + brightness +")";
                                            break;
                                        case "false":
                                            value = NumberFormat.getPercentInstance().format(probability) + " quick";
                                            style = "background-color: rgb(" + brightness + ", " + 255 + ", " + brightness +")";
                                            break;
                                        default:
                                            value = NumberFormat.getPercentInstance().format(probability) + " " + key;
                                    }
                                        
                                } catch (Exception e) {
                                    value = e.getMessage();
                                }
                                    
                            } catch (NumberFormatException e) {
                                value = "Unknown type: " + predictor.getType();

                            } finally {
                                Listcell listcell = new Listcell(value);
                                listcell.setStyle(style);
                                return listcell;   
                            }
                        }

                        @Override public String getCSVHeader() {
                            return header + " (quick)," + header + " (slow)";
                        }

                        public String getCSVItem(PredictiveMonitorEvent event, JSONObject json) {
                            try {
                                JSONObject histogram = json.getJSONObject("predictions").getJSONObject(predictor.getType());

                                return NumberFormat.getPercentInstance().format(histogram.getDouble("true")) + "," +
                                       NumberFormat.getPercentInstance().format(histogram.getDouble("false"));

                            } catch (Exception e) {
                                return ",";
                            }
                        }
                    });
                                                              
                } catch (NumberFormatException e) {
                    // no predictor column will be added
                }
            }
        }
    }

    /**
     * @param duration  arbitrary {@link Duration}
     * @return the <var>duration</var> pretty-printed in a format resembling "240d 3h 59m 0s", or <code>null</code> if <var>duration</var> is <code>null</code>
     */
    static String format(Duration duration) {
       if (duration == null) { return null; }
       String result = duration.getSeconds() % 60 + "s";

       long minutes = duration.toMinutes();
       if (minutes == 0) { return result; }
       result = minutes % 60 + "m " + result;

       long hours = duration.toHours();
       if (hours == 0) { return result; }
       result = hours % 24 + "h " + result;

       long days = duration.toDays();
       if (days == 0) { return result; }
       return days + "d " + result;
    }


    // Implementation of Observer

    private boolean needsReload = true;

    /**
     * Handle the addition of new predictions to the {@link PredictiveMonitor}.
     *
     * This method bridges between Kafka and ZK events.  It may be invoked from any thread.
     */
    public void update(Observable observable, Object arg) {
        needsReload = true;
        Event event = new Event("dummy", null, arg);
        Executions.schedule(window.getDesktop(), eventsModel, event);
        Executions.schedule(window.getDesktop(), casesModel, event);
    }

    private void reload() {
        if (!needsReload) { return; }

        int completedCaseCount = 0;
        int completedCaseEventCount = 0;
        int runningCaseCount = 0;

        List<PredictiveMonitorEvent> pmEvents = predictiveMonitorService.findPredictiveMonitorEvents(predictiveMonitor);
        //LOGGER.info("PMLM got " + pmEvents.size() + " event(s)");
        Collections.reverse(pmEvents);

        // Reload events
        events.clear();
        String filterCase = filterCaseTextbox.getValue();
        if (filterCase.isEmpty()) {
            events.addAll(pmEvents);

        } else {
            for (PredictiveMonitorEvent pmEvent: pmEvents) {
                if (filterCase.equals(pmEvent.getCaseId())) {
                    events.add(pmEvent);
                }
            }
            eventsListbox.setModel(eventsModel);
        }

        // Populate caseFirstEventMap
        for (PredictiveMonitorEvent pmEvent: pmEvents) {
            if (pmEvent.getEventNr() == 1) {
                caseFirstEventMap.put(pmEvent.getCaseId(), pmEvent);
            }
        }

        // Reload most recent events for each case
        caseEvents.clear();
        Set<String> caseSet = new HashSet<>();
        Duration totalCompletedCaseDuration = Duration.ZERO;
        for (PredictiveMonitorEvent pmEvent: pmEvents) {
            String caseId = pmEvent.getCaseId();
            if (!caseSet.contains(caseId)) {
                caseSet.add(caseId);
                caseEvents.add(pmEvent);
                try {
                    JSONObject json = new JSONObject(pmEvent.getJson());
                    if ("true".equals(json.optString("last"))) {
                        completedCaseCount++;
                        completedCaseEventCount += pmEvent.getEventNr();

                        try {
                            PredictiveMonitorEvent firstEvent = caseFirstEventMap.get(pmEvent.getCaseId());
                            JSONObject startJson = new JSONObject(firstEvent.getJson());
                            Date start = f.newXMLGregorianCalendar(startJson.getString("time:timestamp")).toGregorianCalendar().getTime();
                            Date timestamp = f.newXMLGregorianCalendar(json.getString("time:timestamp")).toGregorianCalendar().getTime();
                            Duration elapsed = Duration.between(Instant.ofEpochMilli(start.getTime()), Instant.ofEpochMilli(timestamp.getTime()));
                            totalCompletedCaseDuration = totalCompletedCaseDuration.plus(elapsed);

                        } catch (Exception e) {
                            LOGGER.error("Unable to calculate elapsed duration", e);
                        }
                    } else {
                        runningCaseCount++;
                    }
                } catch (JSONException e) {
                    LOGGER.error("Unable to parse event JSON", e);
                }
            }

        }
        Collections.sort(caseEvents, new Comparator<PredictiveMonitorEvent>() {
            public int compare(PredictiveMonitorEvent lhs, PredictiveMonitorEvent rhs) { return lhs.getCaseId().compareTo(rhs.getCaseId()); }
        });

        // Display aggregate statistics
        completedEventsLabel.setValue(Integer.toString(pmEvents.size()));
        completedCasesLabel.setValue(Integer.toString(completedCaseCount));
        runningCasesLabel.setValue(Integer.toString(runningCaseCount));
        if (completedCaseCount > 0) {
            NumberFormat numberFormat = new DecimalFormat("0.##");
            averageCaseLengthLabel.setValue(numberFormat.format((completedCaseEventCount / (double) completedCaseCount)));
            averageCaseDurationLabel.setValue(format(totalCompletedCaseDuration.dividedBy(completedCaseCount)));
        }

        needsReload = false;
    }

    private void exportCSV(OutputStream out) throws IOException, JSONException {
        try (Writer w = new BufferedWriter(new OutputStreamWriter(out))) {

            // Write the header
            Iterator<Column> i = columnList.iterator();
            while (i.hasNext()) {
                Column column = i.next();
                w.write(column.getCSVHeader());
                w.write(i.hasNext() ? "," : "\n");
            }

            // Write the content
            for (PredictiveMonitorEvent event: predictiveMonitorService.findPredictiveMonitorEvents(predictiveMonitor)) {
                JSONObject json = new JSONObject(event.getJson());

                i = columnList.iterator();
                while (i.hasNext()) {
                    Column column = i.next();
                    try { w.write(column.getCSVItem(event, json)); } catch (Exception e) {}
                    w.write(i.hasNext() ? "," : "\n");
                }
            }
        }
    }

    private interface Column {
        Listheader getListheader();
        Listcell getListcell(PredictiveMonitorEvent event);

        String getCSVHeader();
        String getCSVItem(PredictiveMonitorEvent event, JSONObject json);
    }

    private static abstract class AbstractColumn implements Column {
        private String header;

        AbstractColumn(String header) {
            this.header = header;
        }

        public Listheader getListheader() {
            return new Listheader(header);
        }

        public abstract Listcell getListcell(PredictiveMonitorEvent event);

        public String getCSVHeader() {
            if (header.contains(",")) {
                throw new RuntimeException("Header " + header + " contains a comma; escaping commas isn't implemented");
            }
            return header;
        }

        public abstract String getCSVItem(PredictiveMonitorEvent event, JSONObject json);
    }

    private static class ColumnImpl extends AbstractColumn {

        interface EventFormatter {
            String format(final PredictiveMonitorEvent event);
        }

        private EventFormatter eventFormatter;

        ColumnImpl(String header, EventFormatter eventFormatter) {
            super(header);

            this.eventFormatter = eventFormatter;
        }

        public Listcell getListcell(PredictiveMonitorEvent event) {
            return new Listcell(eventFormatter.format(event));
        }

        public String getCSVItem(PredictiveMonitorEvent event, JSONObject json) {
            return eventFormatter.format(event);
        }
    }

    /**
     * Wrap a {@link PredictiveMonitor} for use as a ZK {@link ListModel}.
     */
    private class PredictiveMonitorListModel implements EventListener<Event>, ListModel<PredictiveMonitorEvent>, Selectable<PredictiveMonitorEvent> {

        private final Set<ListDataListener> listeners = new HashSet<>();
        private final PredictiveMonitorService predictiveMonitorService;
        private final PredictiveMonitor predictiveMonitor;
        private final List<PredictiveMonitorEvent> events;

        PredictiveMonitorListModel(PredictiveMonitorService predictiveMonitorService, PredictiveMonitor predictiveMonitor, List<PredictiveMonitorEvent> events) {
            this.predictiveMonitorService = predictiveMonitorService;
            this.predictiveMonitor = predictiveMonitor;
            this.events = events;
        }


        // Implementation of EventListener

        /**
         * Handle the addition of new predictions to the {@link PredictiveMonitor}.
         *
         * This method is called on the main thread with events originating from {@link #update}.
         * Events are then forwarded to {@link ListDataListener}s.
         */
        public void onEvent(Event event) {
            //LOGGER.info("PMLM onEvent " + event);

            reload();

            // Notify ListDataListener observers
            ListDataEvent listDataEvent = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, events.size());
            for (ListDataListener listener: listeners) { listener.onChange(listDataEvent); }
        }

        // Implementation of ListModel

        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        public PredictiveMonitorEvent getElementAt(int index) {
            return events.get(index);
        }

        public int getSize() {
            return events.size();
        }

        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        // Implementation of Selectable

        private Boolean multiple = false;
        private SelectionControl selectionControl = null;
        private Set<PredictiveMonitorEvent> selection = new HashSet<>();

        public boolean addToSelection(PredictiveMonitorEvent obj) { return selection.add(obj); }
        public void clearSelection() { selection.clear(); }
        public Set<PredictiveMonitorEvent> getSelection() { return selection; }
        public SelectionControl getSelectionControl() { return selectionControl; }
        public boolean isMultiple() { return multiple; }
        public boolean isSelected(Object obj) { return selection.contains(obj); }
        public boolean isSelectionEmpty() { return selection.isEmpty(); }
        public boolean removeFromSelection(Object obj) { return selection.remove(obj); }
        public void setMultiple(boolean m) { multiple = m; }
        public void setSelection(Collection<? extends PredictiveMonitorEvent> s) { selection.clear();  selection.addAll(s); }
        public void setSelectionControl(SelectionControl c) { this.selectionControl = c; }
    }
}
