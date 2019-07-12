package dashboard;

import org.zkoss.chart.Charts;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityChartSeries {

    private List<ActivityData> activityDataList;

    public ActivityChartSeries(List<ActivityData> activityDataList){
        this.activityDataList = activityDataList;
    }

    public List<Series> getSeries(String option) {
        List<Series> seriesList = new ArrayList<Series>();

        for(int i=0; i<activityDataList.size();i++){
            ActivityData ad = activityDataList.get(i);
            List<Activity> activityList = ad.getActivityList();
            activityList = reorderList(activityList, option);
            Series series = new Series();
            series.setName(ad.getLogName());
            series.setType(Charts.COLUMN);

            if(option.equals("meanActivityDuration")) {
                List<LongIntPair> meanActDurPL =
                        ad.getMeanActivityDurations();
                for(int j=0; j<meanActDurPL.size(); j++) {
                    long dur = meanActDurPL.get(j).getKey();
                    String x = String.format("%d", dur);
                    int y = meanActDurPL.get(j).getValue();
                    String drilldown = Util.durationStringOf(dur);
                    Point point = new Point(x, y, drilldown);
                    series.addPoint(point);
                }
            }else{
                for(int j=0; j<activityList.size(); j++){
                    Activity act = activityList.get(j);
                    String x = act.getName();
                    long y = 0;
                    String timeString = "";
                    if(option.equals("frequency")){
                        y = act.getFrequency();
                        timeString = "n/a";
                    }
                    if(option.equals("medianDuration")){
                        y = act.getMedianDuration();
                        timeString = Util.durationStringOf(y);
                    }
                    if(option.equals("meanDuration")){
                        y = act.getMeanDuration();
                        timeString = Util.durationStringOf(y);
                    }
                    if(option.equals("durationRange")){
                        y = act.getDurationRange();
                        timeString = Util.durationStringOf(y);
                    }
                    if(option.equals("aggregateDuration")){
                        y = act.getAggregateDuration();
                        timeString = Util.durationStringOf(y);
                    }
//                String fooName = "foo" + x;
//                Point point = new Point(fooName, y, x);
                    Point point = new Point(x, y);
                    point.setId(timeString);
                    System.out.println(point.getDataLabels().toString());
                    series.addPoint(point);
                }
            }
            seriesList.add(series);
        }
        return seriesList;
    }

    private static List<Activity> reorderList(
            List<Activity> activities, String option)
    {
        List<Activity> reorderedList = new ArrayList<Activity>();
        reorderedList.add(activities.get(0));

        for(int i=0; i<activities.size(); i++){
            Activity act = activities.get(i);
            if(option.equals("frequency")){
                boolean added = false;
                int iFreq = act.getFrequency();
                for(int j=0; j<reorderedList.size();j++){
                    int jFreq = reorderedList.get(j).getFrequency();
                    if(iFreq >= jFreq){
                        reorderedList.add(j, activities.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(activities.get(i));
                }
            }
            if(option.equals("medianDuration")){
                boolean added = false;
                long vi = act.getMedianDuration();
                for(int j=0; j<reorderedList.size();j++){
                    long vj = reorderedList.get(j).getMedianDuration();
                    if(vi >= vj){
                        reorderedList.add(j, activities.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(activities.get(i));
                }
            }
            if(option.equals("meanDuration")){
                boolean added = false;
                long vi = act.getMeanDuration();
                for(int j=0; j<reorderedList.size();j++){
                    long vj = reorderedList.get(j).getMeanDuration();
                    if(vi >= vj){
                        reorderedList.add(j, activities.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(activities.get(i));
                }
            }
            if(option.equals("durationRange")){
                boolean added = false;
                long vi = act.getDurationRange();
                for(int j=0; j<reorderedList.size();j++){
                    long vj = reorderedList.get(j).getDurationRange();
                    if(vi >= vj){
                        reorderedList.add(j, activities.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(activities.get(i));
                }
            }
            if(option.equals("aggregateDuration")){
                boolean added = false;
                long vi = act.getAggregateDuration();
                for(int j=0; j<reorderedList.size();j++){
                    long vj = reorderedList.get(j).getAggregateDuration();
                    if(vi >= vj){
                        reorderedList.add(j, activities.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(activities.get(i));
                }
            }
        }
        return reorderedList;
    }
}
