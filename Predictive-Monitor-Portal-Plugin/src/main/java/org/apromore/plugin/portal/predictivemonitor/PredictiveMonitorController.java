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
import java.io.Closeable;
import java.io.IOException;
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
    public PredictiveMonitorController(PortalContext portalContext, PredictiveMonitor predictiveMonitor, PredictiveMonitorService predictiveMonitorService) throws IOException {

        this.predictiveMonitor = predictiveMonitor;
        this.predictiveMonitorService = predictiveMonitorService;
        this.window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/predictiveMonitor.zul", null, null);
        this.eventsModel = new PredictiveMonitorListModel(predictiveMonitorService, predictiveMonitor, events);
        this.casesModel = new PredictiveMonitorListModel(predictiveMonitorService, predictiveMonitor, caseEvents);

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
                Messagebox.show("Not yet implemented", "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        });
        
        // Add header columns
        eventsListbox.getListhead().appendChild(new Listheader("Activity"));
        casesListbox.getListhead().appendChild(new Listheader("Activity"));

        eventsListbox.getListhead().appendChild(new Listheader("Event time"));
        casesListbox.getListhead().appendChild(new Listheader("Event time"));

        eventsListbox.getListhead().appendChild(new Listheader("Elapsed"));
        casesListbox.getListhead().appendChild(new Listheader("Elapsed"));

        Set<Predictor> predictors = predictiveMonitor.getPredictors();
        for (Predictor predictor: predictors) {
            switch (predictor.getType()) {
            case "-1":
                eventsListbox.getListhead().appendChild(new Listheader("Case outcome"));
                casesListbox.getListhead().appendChild(new Listheader("Case outcome"));
                break;

            case "next":
                eventsListbox.getListhead().appendChild(new Listheader("Next activity"));
                casesListbox.getListhead().appendChild(new Listheader("Next activity"));
                break;

            case "remtime":
                eventsListbox.getListhead().appendChild(new Listheader("Predicted case end"));
                eventsListbox.getListhead().appendChild(new Listheader("Remaining time"));
                casesListbox.getListhead().appendChild(new Listheader("Predicted case end"));
                casesListbox.getListhead().appendChild(new Listheader("Remaining time"));
                break;

            default:
                eventsListbox.getListhead().appendChild(new Listheader(predictor.getType()));
                casesListbox.getListhead().appendChild(new Listheader(predictor.getType()));
            }
        }

        eventsListbox.setRows(15);  // TODO: figure out how to make vflex work with this
        casesListbox.setRows(15);
        ListitemRenderer<PredictiveMonitorEvent> renderer = new ListitemRenderer<PredictiveMonitorEvent> () {

            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd h:mm:ss a");

            private String formatTime(Date date) { return date == null ? "" : dateFormat.format(date); }

            public void render(Listitem item, PredictiveMonitorEvent event, int index) {
                JSONObject json;
                try {
                    json = new JSONObject(event.getJson());

                } catch (JSONException e) {
                    LOGGER.error("Unable to parse JSON from predictive monitor event", e);
                    return;
                }

                boolean isLast = "true".equals(json.optString("last"));

                // Hardcoded columns
                item.appendChild(new Listcell(event.getCaseId()));
                item.appendChild(new Listcell(event.getEventNr().toString()));

                // Activity
                String value = "-";
                String style = null;
                try {
                    value = json.getString("activity_name");
                } catch (Exception e) { value = e.getMessage(); }
                item.appendChild(new Listcell(value));

                // Timestamp
                value = "-";
                Date timestamp = null;
                try {
                    /*Date*/ timestamp = f.newXMLGregorianCalendar(json.getString("time:timestamp")).toGregorianCalendar().getTime();
                    value = formatTime(timestamp);
                } catch (Exception e) { value = e.getMessage(); }
                item.appendChild(new Listcell(value));

                // Elapsed
                value = "-";
                try {
                    PredictiveMonitorEvent firstEvent = caseFirstEventMap.get(event.getCaseId());
                    JSONObject startJson = new JSONObject(firstEvent.getJson());
                    Date start = f.newXMLGregorianCalendar(startJson.getString("time:timestamp")).toGregorianCalendar().getTime();
                    Duration elapsed = Duration.between(Instant.ofEpochMilli(start.getTime()), Instant.ofEpochMilli(timestamp.getTime()));
                    value = format(elapsed);
                } catch (Exception e) { value = e.getMessage(); }
                item.appendChild(new Listcell(value));

                // Populate the columns added by predictors
                JSONObject jsonPredictions = json.optJSONObject("predictions");
                for (Predictor predictor: predictors) {
                    switch (predictor.getType()) {
                    case "-1":
                        value = "-";
                        style = null;
                        try {
                            double currentHighestProbability = -1;
                            JSONObject histogram = jsonPredictions.optJSONObject("-1");
                            if (histogram == null) {
                                value = "...";
                            } else {
                                Iterator i = histogram.keys();
                                while (i.hasNext()) {
                                    String key = (String) i.next();
                                    double probability = histogram.getDouble(key);
                                    if (probability > currentHighestProbability) {
                                        currentHighestProbability = probability;
                                        int brightness = 155 + Math.min(100, (new Double((2.0 - 2.0 * probability) * 100)).intValue());
                                        switch (key) {
                                        case "true":
                                            value = NumberFormat.getPercentInstance().format(probability) + " slow";
                                            style = "background-color: rgb(" + 255 + ", " + brightness + ", " + brightness +")";
                                            if (isLast) {
                                                item.setStyle("background-color: rgb(" + 255 + ", " + brightness + ", " + brightness +")");
                                            }
                                            break;
                                        case "false":
                                            value = NumberFormat.getPercentInstance().format(probability) + " quick";
                                            if (isLast) {
                                                item.setStyle("background-color: rgb(" + brightness + ", " + 255 + ", " + brightness +")");
                                            }
                                            break;
                                        default:
                                            value = NumberFormat.getPercentInstance().format(probability) + " " + key;
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            value = e.getMessage();

                        } finally {
                            Listcell listcell = new Listcell(value);
                            listcell.setStyle(style);
                            item.appendChild(listcell);
                        }
                        break;

                    case "next":
                        value = "-";
                        try {
                            if (!isLast) {
                                double currentHighestProbability = -1;
                                JSONObject histogram = jsonPredictions.optJSONObject("next");
                                if (histogram == null) {
                                    value = "...";
                                } else {
                                    Iterator i = histogram.keys();
                                    while (i.hasNext()) {
                                        String key = (String) i.next();
                                        double probability = histogram.getDouble(key);
                                        if (probability > currentHighestProbability) {
                                            value = NumberFormat.getPercentInstance().format(probability) + " " + key;
                                            currentHighestProbability = probability;
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            value = e.getMessage();

                        } finally {
                            item.appendChild(new Listcell(value));
                        }
                        break;

                    case "remtime":
                        // Predicted case end column
                        value = "-";
                        try {
                            if ("true".equals(json.optString("last"))) {
                                value = "Complete";

                            } else {
                                //final DatatypeFactory f = DatatypeFactory.newInstance();
                                Calendar calendar = f.newXMLGregorianCalendar(json.getString("time:timestamp")).toGregorianCalendar();

                                JSONObject remtime = jsonPredictions.optJSONObject("remtime");
                                if (remtime == null) {
                                    value = "...";
                                } else {
                                    int remainingSeconds = remtime.getInt("remtime");
                                    calendar.add(Calendar.SECOND, remainingSeconds);

                                    value = formatTime(calendar.getTime());
                                }
                            }

                        } catch (Exception e) {
                            value = e.getMessage();

                        } finally {
                            item.appendChild(new Listcell(value));
                        }

                        // Remaining time column
                        value = "-";
                        try {
                            if ("true".equals(json.optString("last"))) {
                                value = "-";

                            } else {
                                JSONObject remtime = jsonPredictions.optJSONObject("remtime");
                                if (remtime == null) {
                                    value = "...";
                                } else {
                                    value = format(Duration.ofSeconds(remtime.getLong("remtime")));
                                }
                            }
                        } catch (Exception e) {
                            value = e.getMessage();

                        } finally {
                            item.appendChild(new Listcell(value));
                        }
                        break;

                    default:
                         item.appendChild(new Listcell("Unknown type: " + predictor.getType()));
                    }
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
        LOGGER.info("PMLM got " + pmEvents.size() + " event(s)");
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
                            LOGGER.error("Unable calculate elapsed duration", e);
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
            public int compare(PredictiveMonitorEvent lhs, PredictiveMonitorEvent rhs) { return Integer.parseInt(lhs.getCaseId()) - Integer.parseInt(rhs.getCaseId()); }
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
