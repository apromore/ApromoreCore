package dashboard;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.zkoss.zul.ListModelArray;

import java.util.*;

public class ActivityData {

    private String logName = "";
    private XLog xLog;
    private List<Activity> activityList = new ArrayList<Activity>();
    ListModelArray lmaActivities;
    ListModelArray lmaFirstInCase;
    ListModelArray lmaLastInCase;
    private int numberOfResources = 0;
    private int minimalFrequency = 0;
    private int medianFrequency = 0;
    private double meanFrequency = 0;
    private int maximalFrequency = 0;
    private double frequenctStdDeviation = 0;

    public ActivityData(String logName, XLog xLog){
        this.logName = logName;
        this.xLog = xLog;
        setData();
    }

    private void setData() {
        /**
         * K: resourceID, V: frequencyCount
         */
        HashMap<String, Integer> activityFrequencyHM =
                new HashMap<String, Integer>();

        HashMap<String, List<Long>> activityDurationHM =
                new HashMap<String, List<Long>>();

        /**
         * String : concept:name
         */
        HashMap<String, Integer> firstInCaseHM = new HashMap<String, Integer>();
        HashMap<String, Integer> lastInCaseHM = new HashMap<String, Integer>();

        for(int i=0; i<xLog.size();i++){
            XTrace xTrace = xLog.get(i);

            HashMap<Long, Integer> markedTimeMilliHM =
                    new HashMap<Long, Integer>();

            String ficAName = "n/a";
            String licAName = "n/a";
            XEvent ficEvent = xTrace.get(0);
            XEvent licEvent = xTrace.get(xTrace.size()-1);
            if(ficEvent.getAttributes().containsKey("concept:name")){
                ficAName = ficEvent.getAttributes().get("concept:name").toString();
            }
            if(licEvent.getAttributes().containsKey("concept:name")){
                licAName = licEvent.getAttributes().get("concept:name").toString();
            }
            firstInCaseHM.put(ficAName, 0);
            lastInCaseHM.put(licAName, 0);
            System.out.println(xTrace);
            System.out.println(licAName);

            for(int j=0; j<xTrace.size();j++){
                XEvent jEvent = xTrace.get(j);
                long jETime = Util.epochMilliOf(Util.zonedDateTimeOf(jEvent));
                String life = jEvent.getAttributes().get(
                        "lifecycle:transition").toString().toLowerCase();
                String jEName = jEvent.getAttributes().get(
                        "concept:name").toString();
//                String jResName = "n/a";
//                if(jEvent.getAttributes().containsKey("org:resource")){
//                    jResName = jEvent.getAttributes().get(
//                            "org:resource").toString();
//                }

                if(life.equals("complete")){
                    if(activityFrequencyHM.containsKey(jEName)){
                        int count = activityFrequencyHM.get(jEName) + 1;
                        activityFrequencyHM.put(jEName, count);
                    }else{
                        activityFrequencyHM.put(jEName, 1);
                    }
                }

                if(!markedTimeMilliHM.containsKey(jETime))
                {
                    long duration = 0;
                    if(life.equals("start")){
                        long startTimeMilli =
                                Util.epochMilliOf(Util.zonedDateTimeOf(jEvent));
                        /**
                         * Find the compelte time
                         */
                        long endTimeMilli = 0;
                        for(int k = (j+1); k < xTrace.size(); k++) {
                            XEvent kEvent = xTrace.get(k);
                            String kEName = kEvent.getAttributes().get(
                                    "concept:name").toString();
                            if(kEName.equals(jEName)){
                                String kLife = kEvent.getAttributes().get(
                                        "lifecycle:transition").toString();
                                if(kLife.toLowerCase().equals("complete")){
                                    endTimeMilli = Util.epochMilliOf(
                                            Util.zonedDateTimeOf(kEvent));
                                    /**
                                     * Ensure it will not be processed again.
                                     */
                                    markedTimeMilliHM.put(endTimeMilli, 0);

                                    duration = endTimeMilli - startTimeMilli;

                                    break;
                                }
                            }
                        }

                    }
                    System.out.println(duration);
                    if(life.equals("start") || life.equals("complete")){
                        if(activityDurationHM.containsKey(jEName)){
                            List<Long> durList = activityDurationHM.get(jEName);
                            durList.add(duration);
                            activityDurationHM.put(jEName, durList);
                        }else{
                            List<Long> durList = new ArrayList<Long>();
                            durList.add(duration);
                            activityDurationHM.put(jEName, durList);
                        }
                    }
                }
            }
        }
        System.out.println(activityFrequencyHM.toString());
        System.out.println(activityDurationHM.toString());

        System.out.println(firstInCaseHM);
        System.out.println(lastInCaseHM);

        HashMap<String, Double> relativeFrequencyHM =
                relativeFrequencyHashMap(activityFrequencyHM);

        for(String key : activityFrequencyHM.keySet()){
            int freq = 0;
            if(activityFrequencyHM.containsKey(key)) {
                freq = activityFrequencyHM.get(key);
            }
            double relFreq = 0;
            if(relativeFrequencyHM.containsKey(key)) {
                relFreq = relativeFrequencyHM.get(key);
            }
            long medDur = 0;
            long meaDur = 0;
            long durRng = 0;
            long aggDur = 0;

            if(activityDurationHM.containsKey(key)) {
                medDur = medianDurationOf(activityDurationHM.get(key));
                meaDur = meanDurationOf(activityDurationHM.get(key));
                durRng = durationRangeOf(activityDurationHM.get(key));
                aggDur = aggregateDurationOf(activityDurationHM.get(key));
            }

            Activity act = new Activity(
                    key, freq, relFreq, medDur, meaDur, durRng, aggDur);
            activityList.add(act);
        }

        System.out.println(activityList.toString());

        lmaActivities = new ListModelArray(
                activityList.toArray(new Activity[activityList.size()]));

        List<Activity> ficList = new ArrayList<Activity>();
        for(String key : firstInCaseHM.keySet()){
            if(activityFrequencyHM.containsKey(key)) {
                int freq = activityFrequencyHM.get(key);
                double relFreq = relativeFrequencyHM.get(key);
                long medDur = 0;
                long meaDur = 0;
                long durRng = 0;
                long aggDur = 0;
                if(activityDurationHM.containsKey(key)) {
                    medDur = medianDurationOf(activityDurationHM.get(key));
                    meaDur = meanDurationOf(activityDurationHM.get(key));
                    durRng = durationRangeOf(activityDurationHM.get(key));
                    aggDur = aggregateDurationOf(activityDurationHM.get(key));
                }
                Activity act = new Activity(
                        key, freq, relFreq, medDur, meaDur, durRng, aggDur);
                ficList.add(act);
            }
        }

        lmaFirstInCase = new ListModelArray(
                ficList.toArray(new Activity[ficList.size()]));


        List<Activity> licList = new ArrayList<Activity>();
        for(String key : lastInCaseHM.keySet()){
            if(activityFrequencyHM.containsKey(key)) {
                int freq = activityFrequencyHM.get(key);
                double relFreq = relativeFrequencyHM.get(key);
                long medDur = 0;
                long meaDur = 0;
                long durRng = 0;
                long aggDur = 0;
                if(activityDurationHM.containsKey(key)) {
                    medDur = medianDurationOf(activityDurationHM.get(key));
                    meaDur = meanDurationOf(activityDurationHM.get(key));
                    durRng = durationRangeOf(activityDurationHM.get(key));
                    aggDur = aggregateDurationOf(activityDurationHM.get(key));
                }
//                System.out.println(Util.durationStringOf(durRng));
                Activity act = new Activity(
                        key, freq, relFreq, medDur, meaDur, durRng, aggDur);
                licList.add(act);
            }

        }

        lmaLastInCase = new ListModelArray(
                licList.toArray(new Activity[licList.size()]));


        this.numberOfResources = this.activityList.size();
        this.minimalFrequency = minimalFrequency(this.activityList);
        this.medianFrequency = medianFrequency(this.activityList);
        this.meanFrequency = meanFrequency(this.activityList);
        this.maximalFrequency = maxFrequency(this.activityList);
        this.frequenctStdDeviation = frequencyStdDeviation(this.activityList);
    }

    public static HashMap<String, Double> relativeFrequencyHashMap(
            HashMap<String, Integer> activityFrequencyHM){

        HashMap<String, Double> relativeFreq =
                new HashMap<String, Double>();

        Set<String> keySet = activityFrequencyHM.keySet();

        double sum = 0;
        for(String key : keySet){
            double value = activityFrequencyHM.get(key);
            sum += value;
        }

        for(String key : keySet){
            double value = activityFrequencyHM.get(key);
            double relFreq = (value/sum) * 100;
            relativeFreq.put(key, relFreq);
        }
        return relativeFreq;
    }

    public static long medianDurationOf(List<Long> durationList){

        Collections.sort(durationList);

        double median = 0;
        double pos1 = Math.floor((durationList.size() - 1.0) / 2.0);
        double pos2 = Math.ceil((durationList.size() - 1.0) / 2.0);
        if (pos1 == pos2 ) {
            median = durationList.get((int)pos1);
        } else {
            median = (durationList.get((int)pos1) + durationList.get((int)pos2)) / 2.0;
        }
        return new Double(median).longValue();
    }

    public static long meanDurationOf(List<Long> durationList){
        long sum = 0;

        for(int i=0; i<durationList.size();i++){
            long dur = durationList.get(i);
            sum+=dur;
        }
        long result = sum / durationList.size();
        return result;
    }

    public static long durationRangeOf(List<Long> longList){
        Collections.sort(longList);
        long range = longList.get(longList.size()-1) - longList.get(0);
        return range;
    }

    public static long aggregateDurationOf(List<Long> durationList){
        long sum = 0;
        for(int i=0; i<durationList.size();i++){
            sum += durationList.get(i);
        }
        return sum;
    }

    public static int minimalFrequency(List<Activity> activities){
        int minimal = 0;

        for(int i=0; i<activities.size();i++){
            int freq = activities.get(i).getFrequency();
            if((minimal==0)||(freq < minimal)){
                minimal = freq;
            }
        }
        return minimal;
    }

    public static int medianFrequency(List<Activity> activities){
        List<Integer> actFrequencies = new ArrayList<Integer>();


        for(int i=0; i<activities.size();i++){
            int freq = activities.get(i).getFrequency();
            actFrequencies.add(freq);
        }

        Collections.sort(actFrequencies);


        double median = 0;
        double pos1 = Math.floor((actFrequencies.size() - 1.0) / 2.0);
        double pos2 = Math.ceil((actFrequencies.size() - 1.0) / 2.0);
        if (pos1 == pos2 ) {
            median = actFrequencies.get((int)pos1);
        } else {
            median = (actFrequencies.get((int)pos1) + actFrequencies.get((int)pos2)) / 2.0;
        }
        return new Double(median).intValue();
    }

    public static double meanFrequency(List<Activity> activities){
        double sum = 0;


        for(int i=0; i<activities.size();i++){
            int freq = activities.get(i).getFrequency();
            sum+=freq;
        }
        double result = sum / activities.size();
        return result;
    }

    public static int maxFrequency(List<Activity> activities){
        int max = 0;

        for(int i=0; i<activities.size(); i++){
            int frequency = activities.get(i).getFrequency();
            if((max==0) || (frequency > max)){
                max = frequency;
            }
        }
        return max;
    }

    private static double frequencyStdDeviation(List<Activity> activities){
        double sum = 0, sqDiff = 0, mean = 0, stdDeviation=0;
        int size = activities.size();

        for(int i=0; i<size; i++){
            sum += activities.get(i).getFrequency();
        }
        mean = sum / size;
        for(int i=0; i<size; i++){
            int value = activities.get(i).getFrequency();
            sqDiff += (value - mean) * (value - mean);
        }
        double variance = sqDiff / size;

        stdDeviation = Math.sqrt(variance);
        return stdDeviation;
    }

    public int getNumberOfActivities(){
        return Util.allActivityNamesOf(xLog).size();
    }

    public int getMinimalFrequency(){
        int minimal = 0;

        for(int i=0; i<activityList.size();i++){
            int freq = activityList.get(i).getFrequency();
            if((minimal==0)||(freq < minimal)){
                minimal = freq;
            }
        }
        return minimal;
    }

    public int getMedianFrequency(){
        List<Integer> actFrequencies = new ArrayList<Integer>();


        for(int i=0; i<activityList.size();i++){
            int freq = activityList.get(i).getFrequency();
            actFrequencies.add(freq);
        }

        Collections.sort(actFrequencies);


        double median = 0;
        double pos1 = Math.floor((actFrequencies.size() - 1.0) / 2.0);
        double pos2 = Math.ceil((actFrequencies.size() - 1.0) / 2.0);
        if (pos1 == pos2 ) {
            median = actFrequencies.get((int)pos1);
        } else {
            median = (actFrequencies.get((int)pos1) + actFrequencies.get((int)pos2)) / 2.0;
        }
        return new Double(median).intValue();
    }

    public String getMeanFrequencyString(){
        double sum = 0;


        for(int i=0; i<activityList.size();i++){
            int freq = activityList.get(i).getFrequency();
            sum+=freq;
        }
        double result = sum / activityList.size();
        return String.format("%.2f", result);
    }

    public int getMaxFrequency(){
        int max = 0;

        for(int i=0; i<activityList.size(); i++){
            int frequency = activityList.get(i).getFrequency();
            if((max==0) || (frequency > max)){
                max = frequency;
            }
        }
        return max;
    }

    public String getFrequencyStdDeviation(){
        double sum = 0, sqDiff = 0, mean = 0, stdDeviation=0;
        int size = activityList.size();

        for(int i=0; i<size; i++){
            sum += activityList.get(i).getFrequency();
        }
        mean = sum / size;
        for(int i=0; i<size; i++){
            int value = activityList.get(i).getFrequency();
            sqDiff += (value - mean) * (value - mean);
        }
        double variance = sqDiff / size;

        stdDeviation = Math.sqrt(variance);

        return String.format("%.2f", stdDeviation);
    }

    public List<LongIntPair> getMeanActivityDurations() {
        List<Long> allDurations = new ArrayList<Long>();
        for(int i=0; i<activityList.size(); i++) {
            Activity act = activityList.get(i);
            long meanDur = act.getMeanDuration();
            allDurations.add(meanDur);
        }
        Collections.sort(allDurations);
        long durFirst = allDurations.get(0);
        long durLast = allDurations.get(allDurations.size()-1);
        long unit = (durLast - durFirst) / 100;

        List<Long> unitList = new ArrayList<Long>();

        long durUnit = 0;
        int count = 0;
        while(count < 100) {
            durUnit += unit;
            unitList.add(durUnit);
            count+=1;
        }

        System.out.println(unitList.toString());

        HashMap<Long, Integer> durCountHM = new HashMap<Long, Integer>();
        for(int i=0; i<unitList.size();i++){
            durCountHM.put(unitList.get(i), 0);
        }

        for(int i=0; i<allDurations.size();i++) {
            long iDur = allDurations.get(i);
            for(int j=0; j<unitList.size(); j++) {
                long jDur = unitList.get(j);
                long jDurPlus = jDur + unit;
                long jDurMinus = jDur - unit;
                if((iDur >= jDurMinus) && (iDur <= jDurPlus)) {
                    int y = durCountHM.get(jDur) + 1;
                    durCountHM.put(jDur, y);
                }
            }
        }

        System.out.println(durCountHM.toString());

        List<LongIntPair> meanActDurPL =
                new ArrayList<LongIntPair>();

        for(int i=0; i<unitList.size();i++) {
            int y = durCountHM.get(unitList.get(i));
            meanActDurPL.add(new LongIntPairImpl(unitList.get(i), y));
        }

        System.out.println(meanActDurPL.toString());

        return meanActDurPL;
    }

    public XLog getxLog() {
        return xLog;
    }

    public String getLogName() {
        return logName;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

}
