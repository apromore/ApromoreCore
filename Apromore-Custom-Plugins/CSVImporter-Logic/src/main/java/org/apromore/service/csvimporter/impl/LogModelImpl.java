package org.apromore.service.csvimporter.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apromore.service.csvimporter.LogEventModel;
import org.apromore.service.csvimporter.LogModel;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

class LogModelImpl implements LogModel {

    private List<LogEventModel> rows;
    private long lineCount;
    private long errorCount;
    private List<String> invalidRows;
    private boolean errorCheck;

    LogModelImpl(List<LogEventModel> rows, long lineCount, long errorCount, List<String> invalidRows, boolean errorCheck) {
        this.rows        = rows;
        this.lineCount   = lineCount;
        this.errorCount  = errorCount;
        this.invalidRows = invalidRows;
        this.errorCheck  = errorCheck;
    }

    @Override
    public List<LogEventModel> getRows() { return rows; }

    @Override
    public long getLineCount() { return lineCount; }

    @Override
    public long getErrorCount() { return errorCount; }

    @Override
    public List<String> getInvalidRows() { return invalidRows; }

    @Override
    public boolean getErrorCheck() { return errorCheck; }

    @Override
    public XLog getXLog() { return createXLog(rows); }


    // Internal methods

    /**
     * Creates the X log.
     * <p>
     * create xlog element, assign respective extensions and attributes for each event and trace
     *
     * @param traces the traces
     * @return the x log
     */
    private static XLog createXLog(List<LogEventModel> traces) {
        if (traces == null) return null;

        XFactory xFactory = new XFactoryNaiveImpl();
        XLog xLog = xFactory.createLog();
        XTrace xTrace = null;
        XEvent xEvent = null;
        List<XEvent> allEvents = new ArrayList<XEvent>();

        // declare standard extensions of thEe log
        XConceptExtension concept = XConceptExtension.instance();
        XLifecycleExtension lifecycle = XLifecycleExtension.instance();
        XTimeExtension timestamp = XTimeExtension.instance();
        XOrganizationalExtension resource = XOrganizationalExtension.instance();

        xLog.getExtensions().add(concept);
        xLog.getExtensions().add(lifecycle);
        xLog.getExtensions().add(timestamp);
        xLog.getExtensions().add(resource);

        lifecycle.assignModel(xLog, XLifecycleExtension.VALUE_MODEL_STANDARD);

        String newTraceID = null;    // to keep track of traces, when a new trace is created we assign its value and add the respective events for the trace.

//        Comparator<XEvent> compareTimestamp = (XEvent o1, XEvent o2) -> ((XAttributeTimestampImpl) o1.getAttributes().get("time:timestamp")).getValue().compareTo(((XAttributeTimestampImpl) o2.getAttributes().get("time:timestamp")).getValue());
        Comparator<XEvent> compareTimestamp = (XEvent o1, XEvent o2) -> {
            Date o1Date;
            Date o2Date;
            if (o1.getAttributes().get("time:timestamp") != null) {
                XAttribute o1da = o1.getAttributes().get("time:timestamp");
                if (((XAttributeTimestamp) o1da).getValue() != null) {
                    o1Date = ((XAttributeTimestamp) o1da).getValue();
                } else {
                    return -1;
                }
            } else {
                return -1;
            }

            if (o2.getAttributes().get("time:timestamp") != null) {
                XAttribute o2da = o2.getAttributes().get("time:timestamp");
                if (((XAttributeTimestamp) o2da).getValue() != null) {
                    o2Date = ((XAttributeTimestamp) o2da).getValue();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }

            if (o1Date == null || o1Date.toString().isEmpty()) {
                //Messagebox.show("o1Date is null!");
                return 1;
            } else if (o2Date == null || o2Date.toString().isEmpty()) {
                //Messagebox.show("o2Date is null!");
                return -1;
            } else {
                return o1Date.compareTo(o2Date);
            }

        };
//        Comparator<XEvent> compareTimestamp = Comparator.comparing((XEvent o) -> ((XAttributeTimestampImpl) o.getAttributes().get("time:timestamp")).getValue());

        for (LogEventModel trace : traces) {
            String caseID = trace.getCaseID();

            if (newTraceID == null || !newTraceID.equals(caseID)) {    // This could be new trace

                if (!allEvents.isEmpty()) {
                    Collections.sort(allEvents, compareTimestamp);
                    xTrace.addAll(allEvents);
                    allEvents = new ArrayList<XEvent>();
                }

                xTrace = xFactory.createTrace();
                concept.assignName(xTrace, caseID);

                XAttribute attribute;
                Map<String, String> myCaseAttributes = trace.getCaseAttributes();
                for (Map.Entry<String, String> entry : myCaseAttributes.entrySet()) {
                    if (entry.getValue() != null && entry.getValue().trim().length() != 0) {
                        attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                        xTrace.getAttributes().put(entry.getKey(), attribute);
                    }
                }
                xLog.add(xTrace);
                newTraceID = caseID;
            }

            if (trace.getStartTimestamp() != null) {
                xEvent = createEvent(trace, xFactory, concept, lifecycle, timestamp, resource, false);
                allEvents.add(xEvent);
            }
            if (timestamp != null) {
                xEvent = createEvent(trace, xFactory, concept, lifecycle, timestamp, resource, true);
            }
            allEvents.add(xEvent);
        }

        if (!allEvents.isEmpty()) {
            Collections.sort(allEvents, compareTimestamp);
            xTrace.addAll(allEvents);
        }


        if (xEvent == null) {
            return null;
        } else {
            return xLog;
        }
    }

    private static XEvent createEvent(LogEventModel theTrace, XFactory xFactory, XConceptExtension concept, XLifecycleExtension lifecycle, XTimeExtension timestamp, XOrganizationalExtension resource, Boolean isEndTimestamp) {

        XEvent xEvent = xFactory.createEvent();
        concept.assignName(xEvent, theTrace.getConcept());

        if (theTrace.getResource() != null) {
            resource.assignResource(xEvent, theTrace.getResource());
        }

        XAttribute attribute;
        if(theTrace.getOtherTimestamps() != null) {
            Map<String, Timestamp> otherTimestamps = theTrace.getOtherTimestamps();
            for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
                attribute = new XAttributeTimestampImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }
        Map<String, String> others = theTrace.getOthers();
        for (Map.Entry<String, String> entry : others.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() != 0) {
                attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }
        if (theTrace.getTimestamp() != null) {
            if (!isEndTimestamp) {
                lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.START);
                timestamp.assignTimestamp(xEvent, theTrace.getStartTimestamp());

            } else {
                lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.COMPLETE);
                timestamp.assignTimestamp(xEvent, theTrace.getTimestamp());
            }
        }

        return xEvent;
    }
}
