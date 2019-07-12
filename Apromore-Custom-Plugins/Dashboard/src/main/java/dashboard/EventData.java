package dashboard;

import java.time.ZonedDateTime;

public class EventData {
    private String caseId;
    private String eventId;
    private ZonedDateTime timestamp;
    private long timestampMilli;
    private String timestampString;
    private String eventType;
    private String activityName;
    private String resourceName;
    private ZonedDateTime startTime;
    private String startTimeString;
    private ZonedDateTime endTime;
    private String endTimeString;
    private long startTimeMilli;
    private long endTimeMilli;
    private long duration;
    private String status;
    public EventData(String caseId,
                 String eventId,
                 ZonedDateTime timestamp,
                 String eventType,
                 String activityName,
                 String resourceName,
                 ZonedDateTime startTime,
                 ZonedDateTime endTime,
                 String status) {
        this.caseId = caseId;
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.timestampMilli = Util.epochMilliOf(timestamp);
        this.timestampString = Util.timestampStringOf(timestamp);
        this.eventType = eventType;
        this.activityName = activityName;
        this.resourceName = resourceName;
        this.startTime = startTime;
        this.startTimeMilli = Util.epochMilliOf(startTime);
        this.startTimeString = Util.timestampStringOf(startTime);
        this.endTime = endTime;
        this.endTimeMilli = Util.epochMilliOf(endTime);
        this.endTimeString = Util.timestampStringOf(endTime);
        this.status = status;
    }

    public EventData(String caseId,
                     String eventId,
                     ZonedDateTime timestamp,
                     String eventType,
                     String activityName,
                     String resourceName,
                     String status) {
        this.caseId = caseId;
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.timestampMilli = Util.epochMilliOf(timestamp);
        this.timestampString = Util.timestampStringOf(timestamp);
        this.eventType = eventType;
        this.activityName = activityName;
        this.resourceName = resourceName;
        this.status = status;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public long getTimestampMilli() {
        return timestampMilli;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public long getEndTimeMilli() {
        return endTimeMilli;
    }

    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestampString() {
        return timestampString;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public String getEndTimeString() {
        return endTimeString;
    }
}
