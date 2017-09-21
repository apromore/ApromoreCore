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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

// Third-party packages
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;

/**
 * A Nirdizati output event, annotated with predictions.
 *
 * This is the type of the <code>events_with_predictions</code> Kafka topic.
 */
public class DataflowEvent extends Event {

    public static final String ON_DATAFLOW_EVENT = "onDataflowEvent";
    private static Logger LOGGER = LoggerFactory.getLogger(DataflowEvent.class.getCanonicalName());

    // Event properties
    private String activityName;
    private final int index;
    private final Date time;
    private final boolean last;

    // Case properties
    private final String caseId, log;
    private final Date startTime, endTime;

    /**
     * Construct an instance from one of the JSON objects obtained from the <code>events_with_predictions</code> Kafka topic.
     *
     * A representative input:
     * <pre>
     * {
     *   "remainingTime": 692840.0,
     *   "payload": {
     *     "event": {
     *       "proctime": "270",
     *       "last": "true",
     *       "log": "bpi_12",
     *       "AMOUNT_REQ": "15000",
     *       "elapsed": "0",
     *       "case_id": "201608",
     *       "label": "false",
     *       "time": "2012-01-17 07:47:42",
     *       "remtime": "0",
     *       "Resource": "10932",
     *       "activity_name": "W_Fixing_incoming_lead",
     *       "event_nr": "1"
     *     }
     *   },
     *   "outcomes": {
     *     "slow_probability": 0.211
     *   }
     * }
     * </pre>
     *
     * @param json  a JSON object similar to the above example
     * @param latestEventInCase  keyed on case IDs
     * @throws JSONException if <var>json</var> does not have the expected format
     */
    public DataflowEvent(JSONObject json, Map<String, DataflowEvent> latestEventInCase) throws DatatypeConfigurationException, JSONException, ParseException {
        super(ON_DATAFLOW_EVENT);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        JSONObject payload = json.getJSONObject("payload");
        JSONObject event = payload.getJSONObject("event");

        this.activityName = event.getString("activity_name");
        this.index = Integer.parseInt(event.getString("event_nr"));
        this.time = dateFormat.parse(event.getString("time"));
        this.last = event.getBoolean("last");

        this.caseId = event.getString("case_id");
        this.log = event.getString("log");

        DataflowEvent previous = latestEventInCase.get(caseId);

        this.startTime = (index == 1)       ? time
                       : (previous != null) ? previous.getStartTime()
                       : null;

        this.endTime = last               ? time
                     : (previous != null) ? previous.getEndTime()
                     : null;
    }

    // Event property accessors
    public String getActivityName() { return activityName; }
    public int getIndex() { return index; }
    public Date getTime() { return time; }
    public boolean isLast() { return last; }

    // Case property accessors
    public String getCaseId() { return caseId; }
    public String getLog() { return log; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }

    // Derived property accessors
    public Duration getDuration() {
        return startTime == null ? null :
               endTime == null ? null :
               Duration.between(Instant.ofEpochMilli(startTime.getTime()), Instant.ofEpochMilli(endTime.getTime()));
    }

    public String getFormattedDuration() {
        Duration duration = getDuration();
        return duration == null ? null : format(duration);
    }

    /**
     * @param duration  arbitrary {@link Duration}, never <code>null</code>
     * @return the <var>duration</var> pretty-printed in a format resembling "240d 3h 59m 0s".
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
}
