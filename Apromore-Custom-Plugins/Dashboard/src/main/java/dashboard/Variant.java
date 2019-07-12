package dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Variant {

    private int id, numberOfCases, numberOfEvents;
    private long medianDuration, meanDuration;
    private String meanDurationString, medianDurationString;


    public Variant(int id, int numberOfCases, int numberOfEvents,
                   long medianDuration, long meanDuration){
        this.id = id;
        this.numberOfCases = numberOfCases;
        this.numberOfEvents = numberOfEvents;
        this.medianDuration = medianDuration;
        this.meanDuration = meanDuration;
        this.meanDurationString = makeDurationString(meanDuration);
        this.medianDurationString = makeDurationString(medianDuration);
    }

    public String makeDurationString(long duration){

//        return String.format("%d days, %02d:%02d:%02d", TimeUnit.MILLISECONDS.toDays(duration), TimeUnit.MILLISECONDS.toHours(duration),
//                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
//                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long mins = TimeUnit.MILLISECONDS.toMinutes(duration);
        long secs = TimeUnit.MILLISECONDS.toSeconds(duration);
        long millis = TimeUnit.MILLISECONDS.toMillis(duration);

        if(days == 0){
            return String.format("%d hours, %d mins",
                    TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));

        }
        if((days == 0) && (hours == 0)){
            return String.format("%d mins, %d secs",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

        }
        if((days == 0) && (hours == 0) && (mins == 0)){
            return String.format("%d secs, %d millis",
                    TimeUnit.MILLISECONDS.toSeconds(duration),
                    TimeUnit.MILLISECONDS.toMillis(duration) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration)));
        }
        if((days == 0) && (hours == 0) && (mins == 0) && (secs == 0)){
            return String.format("%d millis", millis);
        }

        return String.format("%d days, %d hours",
                TimeUnit.MILLISECONDS.toDays(duration),
                TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)));

        //        return String.format("%d days, %02d:%02d:%02d",
//                TimeUnit.MILLISECONDS.toDays(duration),
//                TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
//                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
//                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumberOfCases(int numberOfCases) {
        this.numberOfCases = numberOfCases;
    }

    public void setNumberOfEvents(int numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    public void setMedianDuration(long medianDuration) {
        this.medianDuration = medianDuration;
    }

    public void setMeanDuration(long meanDuration) {
        this.meanDuration = meanDuration;
    }

    public void setMeanDurationString(String meanDurationString) {
        this.meanDurationString = meanDurationString;
    }

    public void setMedianDurationString(String medianDurationString) {
        this.medianDurationString = medianDurationString;
    }

    public int getId() {
        return id;
    }

    public int getNumberOfCases() {
        return numberOfCases;
    }

    public int getNumberOfEvents() {
        return numberOfEvents;
    }

    public long getMedianDuration() {
        return medianDuration;
    }

    public long getMeanDuration() {
        return meanDuration;
    }

    public String getMeanDurationString() {
        return meanDurationString;
    }

    public String getMedianDurationString() {
        return medianDurationString;
    }
}
