package dashboard;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.zkoss.zul.ListModelArray;

import java.time.ZonedDateTime;
import java.util.*;

public class ResourceData {

    private String logName = "";
    private XLog xLog;

    private int numberOfResources = 0;
    private int minimalFrequency = 0;
    private int medianFrequency = 0;
    private double meanFrequency = 0;
    private int maximalFrequency = 0;
    private double frequenctStdDeviation = 0;

//    private HashMap<String, Integer> resourceFrequencyHM =
//            new HashMap<String, Integer>();

    private List<Resource> resourceList = new ArrayList<Resource>();
    ListModelArray lmaResources;
    ListModelArray lmaFirstInCase;
    ListModelArray lmaLastInCase;

    public ResourceData(String logName, XLog xLog){
        this.logName = logName;
        this.xLog = xLog;
        setData();
    }

    private void setData(){

        /**
         * K: resourceID, V: frequencyCount
         */
        HashMap<String, Integer> resourceFrequencyHM =
                new HashMap<String, Integer>();

        HashMap<String, List<Long>> resourceDurationHM =
                new HashMap<String, List<Long>>();

        /**
         * String : resourceID
         */
        HashMap<String, Integer> firstInCaseHM = new HashMap<String, Integer>();
        HashMap<String, Integer> lastInCaseHM = new HashMap<String, Integer>();

        for(int i=0; i<xLog.size();i++){
            XTrace xTrace = xLog.get(i);

            HashMap<Long, Integer> markedTimeMilliHM =
                new HashMap<Long, Integer>();

            String ficRName = "";
            String licRName = "";
            XEvent ficEvent = xTrace.get(0);
            XEvent licEvent = xTrace.get(xTrace.size()-1);
            if(ficEvent.getAttributes().containsKey("org:resource")){
                ficRName = ficEvent.getAttributes().get("org:resource").toString();
            }
            if(licEvent.getAttributes().containsKey("org:resource")){
                licRName = licEvent.getAttributes().get("org:resource").toString();
            }
            firstInCaseHM.put(ficRName, 0);
            lastInCaseHM.put(licRName, 0);
            System.out.println(xTrace);
            System.out.println(licRName);

            for(int j=0; j<xTrace.size();j++){
                XEvent jEvent = xTrace.get(j);
                long jETime = Util.epochMilliOf(Util.zonedDateTimeOf(jEvent));
                String life = jEvent.getAttributes().get(
                        "lifecycle:transition").toString().toLowerCase();
                String jEName = jEvent.getAttributes().get(
                        "concept:name").toString();
                String jResName = "";
                if(jEvent.getAttributes().containsKey("org:resource")){
                    jResName = jEvent.getAttributes().get(
                            "org:resource").toString();
                }

                if(life.equals("complete")){
                    if(resourceFrequencyHM.containsKey(jResName)){
                        int count = resourceFrequencyHM.get(jResName) + 1;
                        resourceFrequencyHM.put(jResName, count);
                    }else{
                        resourceFrequencyHM.put(jResName, 1);
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
                            String kResName = "";
                            if(kEvent.getAttributes().containsKey("org:resource")){
                                kResName = kEvent.getAttributes().get(
                                        "org:resource").toString();
                            }
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
                    if(life.equals("start") || life.equals("complete")){
                        if(resourceDurationHM.containsKey(jResName)){
                            List<Long> durList = resourceDurationHM.get(jResName);
                            durList.add(duration);
                            resourceDurationHM.put(jResName, durList);
                        }else{
                            List<Long> durList = new ArrayList<Long>();
                            durList.add(duration);
                            resourceDurationHM.put(jResName, durList);
                        }
                    }
                }
            }
        }

        System.out.println(resourceFrequencyHM.toString());
        System.out.println(resourceDurationHM.toString());

        System.out.println(firstInCaseHM);
        System.out.println(lastInCaseHM);


        HashMap<String, Double> relativeFrequencyHM =
                relativeFrequencyHashMap(resourceFrequencyHM);

        System.out.println(relativeFrequencyHM.toString());



        for(String key : resourceFrequencyHM.keySet()){
            int freq = 0;
            if(resourceFrequencyHM.containsKey(key)) {
                freq = resourceFrequencyHM.get(key);
            }
            double relFreq = 0;
            if(relativeFrequencyHM.containsKey(key)) {
                relFreq = relativeFrequencyHM.get(key);
            }
            long medDur = 0;
            long meaDur = 0;
            long durRng = 0;
            long aggDur = 0;
            if(resourceDurationHM.containsKey(key)) {
                medDur = medianDurationOf(resourceDurationHM.get(key));
                meaDur = meanDurationOf(resourceDurationHM.get(key));
                durRng = durationRangeOf(resourceDurationHM.get(key));
                aggDur = aggregateDurationOf(resourceDurationHM.get(key));
            }
            Resource res = new Resource(
                    key, freq, relFreq, medDur, meaDur, durRng, aggDur);
            resourceList.add(res);
        }

        System.out.println(resourceList);

        lmaResources = new ListModelArray(
                resourceList.toArray(new Resource[resourceList.size()]));


        List<Resource> ficList = new ArrayList<Resource>();
        for(String key : firstInCaseHM.keySet()){
            int freq = 0;
            if(resourceFrequencyHM.containsKey(key)) {
                freq = resourceFrequencyHM.get(key);
            }
            double relFreq = 0;
            if(relativeFrequencyHM.containsKey(key)) {
                relFreq = relativeFrequencyHM.get(key);
            }
            long medDur = 0;
            long meaDur = 0;
            long durRng = 0;
            long aggDur = 0;
            if(resourceDurationHM.containsKey(key)) {
                medDur = medianDurationOf(resourceDurationHM.get(key));
                meaDur = meanDurationOf(resourceDurationHM.get(key));
                durRng = durationRangeOf(resourceDurationHM.get(key));
                aggDur = aggregateDurationOf(resourceDurationHM.get(key));
            }
            Resource res = new Resource(
                    key, freq, relFreq, medDur, meaDur, durRng, aggDur);
            ficList.add(res);
        }

        lmaFirstInCase = new ListModelArray(
                ficList.toArray(new Resource[ficList.size()]));


        List<Resource> licList = new ArrayList<Resource>();
        for(String key : lastInCaseHM.keySet()){
            int freq = 0;
            if(resourceFrequencyHM.containsKey(key)) {
                freq = resourceFrequencyHM.get(key);
            }
            double relFreq = 0;
            if(relativeFrequencyHM.containsKey(key)) {
                relFreq = relativeFrequencyHM.get(key);
            }
            long medDur = 0;
            long meaDur = 0;
            long durRng = 0;
            long aggDur = 0;
            if(resourceDurationHM.containsKey(key)) {
                medDur = medianDurationOf(resourceDurationHM.get(key));
                meaDur = meanDurationOf(resourceDurationHM.get(key));
                durRng = durationRangeOf(resourceDurationHM.get(key));
                aggDur = aggregateDurationOf(resourceDurationHM.get(key));
            }
            Resource res = new Resource(
                    key, freq, relFreq, medDur, meaDur, durRng, aggDur);
            licList.add(res);

        }

        lmaLastInCase = new ListModelArray(
                licList.toArray(new Resource[licList.size()]));


        this.numberOfResources = this.resourceList.size();
        this.minimalFrequency = minimalFrequency(this.resourceList);
        this.medianFrequency = medianFrequency(this.resourceList);
        this.meanFrequency = meanFrequency(this.resourceList);
        this.maximalFrequency = maxFrequency(this.resourceList);
        this.frequenctStdDeviation = frequencyStdDeviation(this.resourceList);
    }

    private List<Long> getDurationListOf(String resourceID){

        HashMap<ZonedDateTime, Integer> markedTime =
                new HashMap<ZonedDateTime, Integer>();

        List<Long> durationList = new ArrayList<Long>();
        for(int i=0; i<xLog.size();i++) {
            XTrace xTrace = xLog.get(i);
            for (int j = 0; j < xTrace.size(); j++) {
                AEventData aed = new AEventData(xTrace.get(j));
                if(aed.resource.equals(resourceID)){
                   if(aed.lifecycleTransition.equals("start")){
                       ZonedDateTime endTime =
                               getEndTimeOfActivity(aed.name, xTrace, j+1);
                       long dur = Util.durationOf(aed.timestamp, endTime);
                       durationList.add(dur);
                       markedTime.put(endTime, 0);
                   }
                   if(aed.lifecycleTransition.equals("complete") &&
                           !markedTime.containsKey(aed.timestamp)){
                       durationList.add(new Long(0));
                   }
                }
            }
        }
        return durationList;
    }

    private ZonedDateTime getEndTimeOfActivity(String eventName, XTrace xTrace, int startIndex){
        for(int i=startIndex; i<xTrace.size();i++){
            AEventData kAED = new AEventData(xTrace.get(i));
            if(kAED.name.equals(eventName) &&
                    kAED.lifecycleTransition.equals("complete")){
                return kAED.timestamp;
            }
        }
        return null;
    }

    private int getFrequencyOf(String resourceID){
        return 0;
//        return resourceFrequencyHM.get(resourceID);
//        int count = 0;
//        for(int i=0; i<xLog.size();i++) {
//            XTrace xTrace = xLog.get(i);
//            for (int j = 0; j < xTrace.size(); j++) {
//                AEventData aed = new AEventData(xTrace.get(j));
//                System.out.println("[" + j + "/" + i +"] aedResource: " +
//                        aed.resource + ", life: " + aed.lifecycleTransition);
//                if(aed.resource.equals(resourceID) &&
//                    aed.lifecycleTransition.equals("complete")){
//
//                    count += 1;
//                    System.out.println("count = " + count);
//                }
//            }
//        }
//        System.out.println(resourceID + " count = " + count);
//        return count;
    }

    private List<String> getResourceNames(){
//        HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
//        for(int i=0; i<xLog.size();i++) {
//            XTrace xTrace = xLog.get(i);
//            for (int j = 0; j < xTrace.size(); j++) {
//                XEvent xEvent = xTrace.get(j);
//                String xRerName = null;
//                if(xEvent.getAttributes().containsKey("org:resource")){
//                    xRerName =
//                            xEvent.getAttributes().get("org:resource").toString();
////                    System.out.println(xerName);
//                    tempMap.put(xRerName, 0);
//                }else{
//                    xRerName = "n/a";
//                    tempMap.put(xRerName, 0);
//                }
//                String life = xEvent.getAttributes().get(
//                        "lifecycle:transition").toString().toLowerCase();
//                if(life.equals("complete")){
//                    if(resourceFrequencyHM.containsKey(xRerName)){
//                        int y = resourceFrequencyHM.get(xRerName) + 1;
//                        resourceFrequencyHM.put(xRerName, y);
//                    }else {
//                        resourceFrequencyHM.put(xRerName, 1);
//                    }
//                }
//            }
//        }
//
//        System.out.println(resourceFrequencyHM);
//
//        Set<String> keySet = tempMap.keySet();
//        List<String> keyList = new ArrayList<String>();
//        for(String s : keySet){
//            keyList.add(s);
//        }
//
////        System.out.println(keyList);
//        return keyList;

        return null;
    }

    public static long aggregateDurationOf(List<Long> durationList){
        long sum = 0;
        for(int i=0; i<durationList.size();i++){
            sum += durationList.get(i);
        }
        return sum;
    }

    private long getAggregateDurationOf(String resourceName){
        List<Long> durList = getDurationListOf(resourceName);
        long sum = 0;
        for(int i=0; i<durList.size();i++){
            sum += durList.get(i);
        }
        return sum;
    }

    public static HashMap<String, Double> relativeFrequencyHashMap(
            HashMap<String, Integer> resourceFrequencyHM){

        HashMap<String, Double> relativeFreq =
                new HashMap<String, Double>();

        Set<String> keySet = resourceFrequencyHM.keySet();

        double sum = 0;
        for(String key : keySet){
            double value = resourceFrequencyHM.get(key);
            sum += value;
        }

        for(String key : keySet){
            double value = resourceFrequencyHM.get(key);
            double relFreq = (value/sum) * 100;
            relativeFreq.put(key, relFreq);
        }
        return relativeFreq;
    }

    public HashMap<String, Double> getRelativeFrequencyHashMap(){

        List<String> rNames = getResourceNames();

        HashMap<String, Integer> resourceFrequencyHM =
                new HashMap<String, Integer>();

        for(int i=0; i<rNames.size(); i++){
            String name = rNames.get(i);
            resourceFrequencyHM.put(name, getFrequencyOf(name));
        }

        HashMap<String, Double> relativeFreq =
                new HashMap<String, Double>();

        Set<String> keySet = resourceFrequencyHM.keySet();

        double sum = 0;
        for(String key : keySet){
            double value = resourceFrequencyHM.get(key);
            sum += value;
        }


        for(String key : keySet){
            double value = resourceFrequencyHM.get(key);
            double relFreq = (value/sum) * 100;
            relativeFreq.put(key, relFreq);
        }
        return relativeFreq;
    }

    public static List<String> allFirstInCaseOf(XLog xLog){
        List<String> nameList = new ArrayList<String>();

        HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
        for(int i=0; i<xLog.size();i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size();j++){
                XEvent e0 = xTrace.get(0);
                String name =
                        e0.getAttributes().get("concept:name").toString();
                tempMap.put(name, 0);
            }
        }
        Set<String> keySet = tempMap.keySet();

        List<String> firstInCaseList = new ArrayList<String>();
        for(String s : keySet){
            firstInCaseList.add(s);
        }

        return firstInCaseList;
    }

    public static List<String> allLastInCaseOf(XLog xLog){
        List<String> lastInCaseList = new ArrayList<String>();
        HashMap<String, Integer> tempMap = new HashMap<String, Integer>();

        for(int i=0; i<xLog.size(); i++){
            XTrace xTrace = xLog.get(i);
            XEvent xEvent = xTrace.get(xTrace.size()-1);
            String xeName =
                    xEvent.getAttributes().get("concept:name").toString();
            tempMap.put(xeName, 0);
        }

        Set<String> keySet = tempMap.keySet();
        for(String s : keySet){
            lastInCaseList.add(s);
        }
        return lastInCaseList;
    }

    public static int minimalFrequency(List<Resource> resources){
        int minimal = 0;

        for(int i=0; i<resources.size();i++){
            int freq = resources.get(i).getFrequency();
            if((minimal==0)||(freq < minimal)){
                minimal = freq;
            }
        }
        return minimal;
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

    public static int medianFrequency(List<Resource> resources){
        List<Integer> resFrequencies = new ArrayList<Integer>();


        for(int i=0; i<resources.size();i++){
            int freq = resources.get(i).getFrequency();
            resFrequencies.add(freq);
        }

        Collections.sort(resFrequencies);


        double median = 0;
        double pos1 = Math.floor((resFrequencies.size() - 1.0) / 2.0);
        double pos2 = Math.ceil((resFrequencies.size() - 1.0) / 2.0);
        if (pos1 == pos2 ) {
            median = resFrequencies.get((int)pos1);
        } else {
            median = (resFrequencies.get((int)pos1) + resFrequencies.get((int)pos2)) / 2.0;
        }
        return new Double(median).intValue();
    }

    public static double meanFrequency(List<Resource> resources){
        double sum = 0;


        for(int i=0; i<resources.size();i++){
            int freq = resources.get(i).getFrequency();
            sum+=freq;
        }
        double result = sum / resources.size();
        return result;
//        return String.format("%.2f", result);
    }



    public static int maxFrequency(List<Resource> resources){
        int max = 0;

        for(int i=0; i<resources.size(); i++){
            int frequency = resources.get(i).getFrequency();
            if((max==0) || (frequency > max)){
                max = frequency;
            }
        }
        return max;
    }

    private static double frequencyStdDeviation(List<Resource> resources){
        double sum = 0, sqDiff = 0, mean = 0, stdDeviation=0;
        int size = resources.size();

        for(int i=0; i<size; i++){
            sum += resources.get(i).getFrequency();
        }
        mean = sum / size;
        for(int i=0; i<size; i++){
            int value = resources.get(i).getFrequency();
            sqDiff += (value - mean) * (value - mean);
        }
        double variance = sqDiff / size;

        stdDeviation = Math.sqrt(variance);
        return stdDeviation;
//        return String.format("%.2f", stdDeviation);
    }



    class AEventData{
        public String name = "";
        public String resource = "";
        public String lifecycleTransition = "";
        public ZonedDateTime timestamp;
        public AEventData(XEvent xEvent){
            if(xEvent.getAttributes().containsKey("concept:name")){
                this.name = xEvent.getAttributes().get(
                        "concept:name").toString();
            }
            if(xEvent.getAttributes().containsKey("org:resource")){
                this.resource =
                        xEvent.getAttributes().get(
                                "org:resource").toString();
            }
            if(xEvent.getAttributes().containsKey("lifecycle:transition")){
                this.lifecycleTransition = xEvent.getAttributes().get(
                        "lifecycle:transition").toString().toLowerCase();
            }
            if(xEvent.getAttributes().containsKey("time:timestamp")){
                this.timestamp = Util.zonedDateTimeOf(xEvent);
            }
        }
    }

    public XLog getxLog() {
        return xLog;
    }


    public ListModelArray getLmaResources() {
        return lmaResources;
    }

    public ListModelArray getLmaFirstInCase() {
        return lmaFirstInCase;
    }

    public ListModelArray getLmaLastInCase() {
        return lmaLastInCase;
    }

    public int getNumberOfResources() {
        return numberOfResources;
    }

    public int getMinimalFrequency() {
        return minimalFrequency;
    }

    public int getMedianFrequency() {
        return medianFrequency;
    }

    public double getMeanFrequency() {
        return meanFrequency;
    }

    public int getMaximalFrequency() {
        return maximalFrequency;
    }

    public double getFrequenctStdDeviation() {
        return frequenctStdDeviation;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public String getLogName() {
        return logName;
    }
}
