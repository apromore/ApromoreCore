package dashboard;

import java.time.ZonedDateTime;

public class Activity {

    private String name;
    private int frequency;
    private double relativeFrequency;
    private long medianDuration = 0;
    private long meanDuration = 0;
    private long durationRange = 0;
    private long aggregateDuration = 0;
    private String medianDurationString = "0 millis";
    private String meanDurationString ="0 millis";
    private String durationRangeString ="0 millis";
    private String aggregateDurationString ="0 millis";

    public Activity(String name, int frequency, double relativeFrequency){
        this.name = name;
        this.frequency = frequency;
        this.relativeFrequency = relativeFrequency;
    }

    public Activity(String name, int frequency, double relativeFrequency,
                    long medianDuration,
                    long meanDuration,
                    long durationRange,
                    long aggregateDuration){
        this.name = name;
        this.frequency = frequency;
        this.relativeFrequency = relativeFrequency;
        if(medianDuration>0){
            this.medianDuration = medianDuration;
            this.medianDurationString = Util.durationStringOf(medianDuration);
        }
        if(meanDuration>0){
            this.meanDuration = meanDuration;
            this.meanDurationString = Util.durationStringOf(meanDuration);
        }
        if(durationRange>0){
            this.durationRange = durationRange;
            this.durationRangeString = Util.durationStringOf(durationRange);
        }
        if(aggregateDuration>0){
            this.aggregateDuration = aggregateDuration;
            this.aggregateDurationString =
                    Util.durationStringOf(aggregateDuration);
        }
    }

    public String getName() {
        return name;
    }

    public int getFrequency() {
        return frequency;
    }

    public double getRelativeFrequency() {
        return relativeFrequency;
    }

    public String getRelativeFrequencyString() {
        return String.format("%.2f%%", relativeFrequency);
    }

    public String getMedianDurationString() {
        return medianDurationString;
    }

    public String getMeanDurationString() {
        return meanDurationString;
    }

    public String getDurationRangeString() {
        return durationRangeString;
    }

    public long getMedianDuration() {
        return medianDuration;
    }

    public long getMeanDuration() {
        return meanDuration;
    }

    public long getDurationRange() {
        return durationRange;
    }

    public long getAggregateDuration() {
        return aggregateDuration;
    }

    public String getAggregateDurationString() {
        return aggregateDurationString;
    }

}
