package dashboard;

import org.zkoss.chart.Charts;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;

import java.util.ArrayList;
import java.util.List;

public class ResourceChartSeries {

    private List<ResourceData> resourceDataList;

    public ResourceChartSeries(List<ResourceData> resourceDataList){
        this.resourceDataList = resourceDataList;
    }

    public List<Series> getSeries(String option){
        List<Series> seriesList = new ArrayList<Series>();
        for(int i=0; i<resourceDataList.size();i++){
            ResourceData rd = resourceDataList.get(i);
            List<Resource> resourceList = rd.getResourceList();
            resourceList = reorderList(resourceList, option);
            Series series = new Series();
            series.setName(rd.getLogName());
            series.setType(Charts.COLUMN);
            for(int j=0; j<resourceList.size(); j++){
                Resource res = resourceList.get(j);
                String x = res.getName();
                long y = 0;
                String timeString = "";
                if(option.equals("frequency")){
                    y = res.getFrequency();
                    Point point = new Point(x, y);
                    series.addPoint(point);
                }
                if(option.equals("medianDuration")){
                    y = res.getMedianDuration();
                    timeString = Util.durationStringOf(y);
                    Point point = new Point(x, y);
                    point.setId(timeString);
                    series.addPoint(point);
                }
                if(option.equals("meanDuration")){
                    y = res.getMeanDuration();
                    timeString = Util.durationStringOf(y);
                    Point point = new Point(x, y);
                    point.setId(timeString);
                    series.addPoint(point);
                }
                if(option.equals("durationRange")){
                    y = res.getDurationRange();
                    timeString = Util.durationStringOf(y);
                    Point point = new Point(x, y);
                    point.setId(timeString);
                    series.addPoint(point);
                }
                if(option.equals("aggregateDuration")){
                    y = res.getAggregateDuration();
                    timeString = Util.durationStringOf(y);
                    Point point = new Point(x, y);
                    point.setId(timeString);
                    series.addPoint(point);
                }
//                String fooName = "foo" + x;
//                Point point = new Point(fooName, y, x);
            }
            System.out.println(series.toString());
            seriesList.add(series);
        }
        return seriesList;
    }

    private static List<Resource> reorderList(List<Resource> resList, String option){
        List<Resource> reorderedList = new ArrayList<Resource>();
        reorderedList.add(resList.get(0));

        for(int i=0; i<resList.size(); i++){
            Resource res = resList.get(i);
            if(option.equals("frequency")){
                boolean added = false;
                int iFreq = res.getFrequency();
                for(int j=0; j<reorderedList.size();j++){
                    int jFreq = reorderedList.get(j).getFrequency();
                    if(iFreq >= jFreq){
                        reorderedList.add(j, resList.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(resList.get(i));
                }
            }
            if(option.equals("medianDuration")){
                boolean added = false;
                long vi = res.getMedianDuration();
                for(int j=0; j<reorderedList.size();j++){
                    long vj = reorderedList.get(j).getMedianDuration();
                    if(vi >= vj){
                        reorderedList.add(j, resList.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(resList.get(i));
                }
            }
            if(option.equals("meanDuration")){
                boolean added = false;
                long vi = res.getMeanDuration();
                for(int j=0; j<reorderedList.size();j++){
                    long vj = reorderedList.get(j).getMeanDuration();
                    if(vi >= vj){
                        reorderedList.add(j, resList.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(resList.get(i));
                }
            }
            if(option.equals("durationRange")){
                boolean added = false;
                long vi = res.getDurationRange();
                for(int j=0; j<reorderedList.size();j++){
                    long vj = reorderedList.get(j).getDurationRange();
                    if(vi >= vj){
                        reorderedList.add(j, resList.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(resList.get(i));
                }
            }
            if(option.equals("aggregateDuration")){
                boolean added = false;
                long vi = res.getAggregateDuration();
                for(int j=0; j<reorderedList.size();j++){
                    long vj = reorderedList.get(j).getAggregateDuration();
                    if(vi >= vj){
                        reorderedList.add(j, resList.get(i));
                        added = true;
                        break;
                    }
                }
                if(!added){
                    reorderedList.add(resList.get(i));
                }
            }
        }
        return reorderedList;
    }

}
