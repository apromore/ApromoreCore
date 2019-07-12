package dashboard;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;

import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Util {

    private static final int day =1000 * 60 * 60 * 24;
    private static final int hour =1000 *  60 * 60;
    private static final int minute =1000 *  60;
    private static final int second =1000;

    public static long epochMilliOf(ZonedDateTime zonedDateTime){
        int totalSeconds = zonedDateTime.getOffset().getTotalSeconds();
        long s = zonedDateTime.toInstant().toEpochMilli();
        s = s + (1000 * totalSeconds); // millisecond * totalSeconds
        return s;
    }

    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        XAttribute da =
                xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
        Date d = ((XAttributeTimestamp) da).getValue();
        ZonedDateTime z =
                ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
        return z;
    }

    public static List<ZonedDateTime> allTimestampsOf(XTrace xTrace){
        List<ZonedDateTime> allTimestamps = new ArrayList<ZonedDateTime>();
        for(int j=0; j<xTrace.size(); j++){
            XEvent xEvent = xTrace.get(j);
            ZonedDateTime zdt = zonedDateTimeOf(xEvent);
            allTimestamps.add(zdt);
        }
        Collections.sort(allTimestamps);
        return  allTimestamps;
    }

    public static List<Long> allTimestampMillisOf(XLog xLog){
        List<Long> allTimestamps = new ArrayList<Long>();
        for(int i=0; i<xLog.size(); i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                long timeMilli = epochMilliOf(zonedDateTimeOf(xEvent));
                allTimestamps.add(timeMilli);
            }
        }
        Collections.sort(allTimestamps);
        return  allTimestamps;
    }

    public static List<Long> allEndTimestampMillisOf(XLog xLog){
        List<Long> allTimestamps = new ArrayList<Long>();
        for(int i=0; i<xLog.size(); i++){
            XTrace xTrace = xLog.get(i);
            XEvent endXEvent = xTrace.get(xTrace.size()-1);
            long endTimeMilli = epochMilliOf(zonedDateTimeOf(endXEvent));
            allTimestamps.add(endTimeMilli);
        }
        Collections.sort(allTimestamps);
        return  allTimestamps;
    }

    public static long firstTimeMilliOf(XLog xlog){
        ZonedDateTime fTime = zonedDateTimeOf(xlog.get(0).get(0));
        for(int i=0; i<xlog.size(); i++){
            XTrace xTrace = xlog.get(0);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                ZonedDateTime xeTime = zonedDateTimeOf(xEvent);
                if(xeTime.isBefore(fTime)){
                    fTime = xeTime;
                }
            }
        }
        return epochMilliOf(fTime);
    }

    public static long lastTimeMilliOf(XLog xlog){
        ZonedDateTime lTime = zonedDateTimeOf(xlog.get(0).get(0));
        for(int i=0; i<xlog.size(); i++){
            XTrace xTrace = xlog.get(0);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                ZonedDateTime xeTime = zonedDateTimeOf(xEvent);
                if(xeTime.isAfter(lTime)){
                    lTime = xeTime;
                }
            }
        }
        return epochMilliOf(lTime);
    }

    public static List<ZonedDateTime> allTimestampsOf(XLog xLog){
        List<ZonedDateTime> allTimestamps = new ArrayList<ZonedDateTime>();
        for(int i=0; i<xLog.size(); i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                if(xEvent.getAttributes().containsKey("lifecycle:transition")) {
                    String life = xEvent.getAttributes().get(
                                    "lifecycle:transition").toString().toLowerCase();
                    if(!life.equals("schedule")) {
                        ZonedDateTime zdt = zonedDateTimeOf(xEvent);
                        allTimestamps.add(zdt);
                    }
                }
            }
        }
        Collections.sort(allTimestamps);
        return allTimestamps;
    }

    public static List<ZonedDateTime> allTimestampsOf(List<XLog> xLogList){
        List<ZonedDateTime> allTimestamps = new ArrayList<ZonedDateTime>();
        for(int k=0; k<xLogList.size(); k++){
            XLog xLog = xLogList.get(k);
            for(int i=0; i<xLog.size(); i++){
                XTrace xTrace = xLog.get(i);
                for(int j=0; j<xTrace.size(); j++){
                    XEvent xEvent = xTrace.get(j);
                    ZonedDateTime zdt = zonedDateTimeOf(xEvent);
                    allTimestamps.add(zdt);
                }
            }
        }
        Collections.sort(allTimestamps);
        return allTimestamps;
    }

    public static String xAxisBaseUnitOf(XLog xLog){
        Duration duration = durationOf(xLog);
        int durationSeconds = (int) duration.getSeconds();
        String mode = "second";
        if(((durationSeconds / Util.yearInSecond()) > 0) ||
                (xLog.size() > 10000)){
            mode = "day";
        }else if(((durationSeconds / Util.monthInSecond()) > 0)||
                (xLog.size() > 1000)){
            mode = "hour";
        }else if(((durationSeconds / Util.dayInSecond()) > 0)||
                (xLog.size() > 100)){
            mode = "minute";
        }
        return mode;
    }

    public static String xAxisBaseUnitOf(List<ZonedDateTime> timestampList){
        ZonedDateTime firstTimestamp = timestampList.get(0);
        ZonedDateTime lastTimestamp = timestampList.get(timestampList.size()-1);
        Duration duration = Duration.between(firstTimestamp, lastTimestamp);
        int durationSeconds = (int) duration.getSeconds();
        String mode = "second";
        if((durationSeconds / Util.yearInSecond()) > 0){
            mode = "day";
        }else if((durationSeconds / Util.monthInSecond()) > 0){
            mode = "hour";
        }else if((durationSeconds / Util.dayInSecond()) > 0){
            mode = "minute";
        }
        return mode;
    }

    public static List<Long> defaultTimelineMilliOf(List<XLog> xLogList){
        String mode = xAxisBaseUnitOf(xLogList.get(0));
        List<ZonedDateTime> allTimestamps = allTimestampsOf(xLogList);
        ZonedDateTime d1 = allTimestamps.get(0);
        ZonedDateTime de = allTimestamps.get(allTimestamps.size()-1);
        d1 = formatZonedDateTime(d1, mode);
        de = formatZonedDateTime(de, mode);
        long d1Milli = epochMilliOf(d1);
        long deMilli = epochMilliOf(de);
        List<Long> defaultTimeline = new ArrayList<Long>();
        long xMilli = d1Milli;
        while(xMilli <= deMilli){
            defaultTimeline.add(xMilli);
            if(mode.equals("day")){
                xMilli += 1000 * 60 * 60 *24;
            }else if(mode.equals("hour")){
                xMilli += 1000 * 60 * 60;
            }else if(mode.equals("minute")){
                xMilli += 1000 * 60;
            }else{
                xMilli += 1000;
            }
        }
        return defaultTimeline;
    }

    public static List<Long> defaultTimelineMilliOf(XLog xLog){
        String mode = xAxisBaseUnitOf(xLog);
        List<ZonedDateTime> allTimestamps = allTimestampsOf(xLog);
        ZonedDateTime d1 = allTimestamps.get(0);
        ZonedDateTime de = allTimestamps.get(allTimestamps.size()-1);
        d1 = formatZonedDateTime(d1, mode);
        de = formatZonedDateTime(de, mode);
        long d1Milli = epochMilliOf(d1);
        long deMilli = epochMilliOf(de);
        List<Long> defaultTimeline = new ArrayList<Long>();
        long xMilli = d1Milli;
        while(xMilli <= deMilli){
            defaultTimeline.add(xMilli);
            if(mode.equals("day")){
                xMilli += 1000 * 60 * 60 *24;
            }else if(mode.equals("hour")){
                xMilli += 1000 * 60 * 60;
            }else if(mode.equals("minute")){
                xMilli += 1000 * 60;
            }else{
                xMilli += 1000;
            }
        }
        return defaultTimeline;
    }

    public static Duration durationOf(XTrace xTrace){
        List<ZonedDateTime> timestamps = allTimestampsOf(xTrace);
        ZonedDateTime firstTimestamp = timestamps.get(0);
        ZonedDateTime lastTimestamp = timestamps.get(timestamps.size()-1);
        Duration duration = Duration.between(firstTimestamp, lastTimestamp);
        return duration;
    }

    public static Duration durationOf(XLog xLog){
        List<ZonedDateTime> allTimestamps = allTimestampsOf(xLog);
        Collections.sort(allTimestamps);
        ZonedDateTime firstTimestamp = allTimestamps.get(0);
        ZonedDateTime lastTimestamp = allTimestamps.get(allTimestamps.size()-1);
        Duration duration = Duration.between(firstTimestamp, lastTimestamp);
        return duration;
    }

    public static Duration durationOf(List<XLog> xLogList){
        List<ZonedDateTime> allTimestamps = allTimestampsOf(xLogList);
        Collections.sort(allTimestamps);
        ZonedDateTime firstTimestamp = allTimestamps.get(0);
        ZonedDateTime lastTimestamp = allTimestamps.get(allTimestamps.size()-1);
        Duration duration = Duration.between(firstTimestamp, lastTimestamp);
        return duration;
    }

    public static long durationOf(ZonedDateTime startTime,
                                      ZonedDateTime endTime){
        if((startTime==null)||(endTime==null)) return 0;
        return Duration.between(startTime, endTime).toMillis();
    }

    public static int yearInSecond(){
        return 60 * 60 * 24 * (356 + (365/4));
    }

    public static int monthInSecond(){
        return 60 * 60 * 24 * 30;
    }

    public static int dayInSecond(){
        return 60 * 60 * 24;
    }

    public static ZonedDateTime formatZonedDateTime(
            ZonedDateTime zdt, String mode){
        ZonedDateTime newZDT = zdt;

        if(mode.equals("day")){
            newZDT = ZonedDateTime.of(
                    zdt.getYear(),
                    zdt.getMonthValue(),
                    zdt.getDayOfMonth(),
                    0,
                    0,
                    0,
                    0,
                    ZoneId.systemDefault()
            );
        }
        if(mode.equals("hour")){
            newZDT = ZonedDateTime.of(
                    zdt.getYear(),
                    zdt.getMonthValue(),
                    zdt.getDayOfMonth(),
                    zdt.getHour(),
                    0,
                    0,
                    0,
                    ZoneId.systemDefault()
            );
        }
        if(mode.equals("minute")){
            newZDT = ZonedDateTime.of(
                    zdt.getYear(),
                    zdt.getMonthValue(),
                    zdt.getDayOfMonth(),
                    zdt.getHour(),
                    zdt.getMinute(),
                    0,
                    0,
                    ZoneId.systemDefault()
            );
        }
        if(mode.equals("second")){
            newZDT = ZonedDateTime.of(
                    zdt.getYear(),
                    zdt.getMonthValue(),
                    zdt.getDayOfMonth(),
                    zdt.getHour(),
                    zdt.getMinute(),
                    zdt.getSecond(),
                    0,
                    ZoneId.systemDefault()
            );
        }
        return newZDT;
    }

    public static List<XLog> parseXLogFile(File xLogFile) throws Exception {
        XesXmlParser parser  =new XesXmlParser();
        String fileName = xLogFile.getName();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        if(extension.equals(".gz")) parser = new XesXmlGZIPParser();
        return parser.parse(xLogFile);
    }

    /**
     *
     * @param timestampList
     * @param mode
     * @return
     * mode as "day", "hour", "minute", "second"
     * use this method to generate the units of the XAxis of a datetime based
     * zk chart
     */
    public static List<Long> xAxisTimelineUnitsOf(
            List<ZonedDateTime> timestampList, String mode){
        ZonedDateTime firstTimestamp = timestampList.get(0);
        ZonedDateTime lastTimestamp = timestampList.get(timestampList.size()-1);
        Duration duration = Duration.between(firstTimestamp, lastTimestamp);
        long durationSeconds = duration.getSeconds();

        firstTimestamp = formatZonedDateTime(firstTimestamp, mode);
        lastTimestamp = formatZonedDateTime(lastTimestamp, mode);
        long ftMilli = epochMilliOf(firstTimestamp);
        long ltMilli = epochMilliOf(lastTimestamp);

        List<Long> xAxisMilliUnits = new ArrayList<Long>();

        //add one more unit before and after the actual timeline

        if(mode.equals("day")){
            ftMilli -= day;
            ltMilli+=day;
        }
        if(mode.equals("hour")){
            ftMilli -= hour;
            ltMilli += hour;
        }
        if(mode.equals("minute")){
            ftMilli -= minute;
            ltMilli += minute;
        }
        if(mode.equals("second")){
            ftMilli -= second;
            ltMilli += second;
        }

        long xMilli = ftMilli;

        while(xMilli <= ltMilli){
            xAxisMilliUnits.add(xMilli);
            if(mode.equals("day")){
                xMilli += day;
            }
            if(mode.equals("hour")){
                xMilli += hour;
            }
            if(mode.equals("minute")){
                xMilli += minute;
            }
            if(mode.equals("second")){
                xMilli += second;
            }
        }
        return xAxisMilliUnits;
    }

    public static List<Long> xAxisUnitsOf(
            List<Long> milliTimestamps, String baseXAxisUnit){

        long ftMilli = milliTimestamps.get(0);
        long ltMilli = milliTimestamps.get(milliTimestamps.size()-1);

        List<Long> xAxisMilliUnits = new ArrayList<Long>();

        //add one more unit before and after the actual timeline

        if(baseXAxisUnit.equals("day")){
            ftMilli -= day;
            ltMilli+=day;
        }
        if(baseXAxisUnit.equals("hour")){
            ftMilli -= hour;
            ltMilli += hour;
        }
        if(baseXAxisUnit.equals("minute")){
            ftMilli -= minute;
            ltMilli += minute;
        }
        if(baseXAxisUnit.equals("second")){
            ftMilli -= second;
            ltMilli += second;
        }

        long xMilli = ftMilli;

        while(xMilli <= ltMilli){
            xAxisMilliUnits.add(xMilli);
            if(baseXAxisUnit.equals("day")){
                xMilli += day;
            }
            if(baseXAxisUnit.equals("hour")){
                xMilli += hour;
            }
            if(baseXAxisUnit.equals("minute")){
                xMilli += minute;
            }
            if(baseXAxisUnit.equals("second")){
                xMilli += second;
            }
        }
        return xAxisMilliUnits;
    }

    public static List<Long> toMilliTimestamps(List<ZonedDateTime> timestamps){
        List<Long> milliTimestamps = new ArrayList<Long>();
        for(int i=0;i <timestamps.size();i++){
            long mt = epochMilliOf(timestamps.get(i));
            milliTimestamps.add(mt);
        }
        return milliTimestamps;
    }

    public static List<Long> refineActiveCasesTimestamps(
            List<ZonedDateTime> timestamps, String baseXAxisUnit){

        ZonedDateTime fZDT = timestamps.get(0);
        ZonedDateTime lZDT = timestamps.get(timestamps.size()-1);

        fZDT = formatZonedDateTime(fZDT, baseXAxisUnit);
        lZDT = formatZonedDateTime(lZDT, baseXAxisUnit);

        long ft = epochMilliOf(fZDT);
        long lt = epochMilliOf(lZDT);
        long xMilli = ft;

        List<Long> newTimestamps = new ArrayList<Long>();

        while(xMilli <= lt){
            newTimestamps.add(xMilli);
            if(baseXAxisUnit.equals("day")){
                xMilli += day;
            }
            if(baseXAxisUnit.equals("hour")){
                xMilli += hour;
            }
            if(baseXAxisUnit.equals("minute")){
                xMilli += minute;
            }
            if(baseXAxisUnit.equals("second")){
                xMilli += second;
            }
        }
        return newTimestamps;
    }

    public static List<String> sequencedEventNamesOf(XTrace xTrace){
        List<String> eventNames = new ArrayList<String>();
        for(int i=0; i<xTrace.size(); i++){
            XEvent xEvent = xTrace.get(i);
            String eName = xEvent.getAttributes().get("concept:name").toString();
            eventNames.add(eName);
        }
        return eventNames;
    }

    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        Instant i = Instant.ofEpochSecond(millisecond);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return z;
    }

    public static LocalDateTime millisecondToLocalDateTime(long millisecond){
        Instant instant = Instant.ofEpochMilli(millisecond);
        LocalDateTime localDateTime =
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime;
    }


    public static long formatDurationBasedOnHour(long millisecond){
        /*
            return the value based on hour
         */
        ZonedDateTime zdt = millisecondToZonedDateTime(millisecond);
        int hour = zdt.getHour();
        int minute = zdt.getMinute();
        int second = zdt.getSecond();
        int nano = zdt.getNano();
        if((minute > 0) || (second > 0) || (nano > 0)){
            hour += 1;
        }
        zdt = ZonedDateTime.of(
                zdt.getYear(),
                zdt.getMonthValue(),
                zdt.getDayOfMonth(),
                hour,
                0,
                0,
                0,
                ZoneId.systemDefault()
        );

        return epochMilliOf(zdt);
    }

    public static long millisecondToHour(long milliseconds){
        int hour = 1000 * 60 * 60;
        milliseconds = milliseconds / hour;
        if(milliseconds < 1) milliseconds = 1;
        return  milliseconds;
    }

    public static long millisecondToDay(long milliseconds){
        int day = 1000 * 60 * 60 * 24;
        milliseconds = milliseconds / day;
        if(milliseconds < 1) milliseconds = 1;
        return  milliseconds;
    }

    public static String caseDurationUnitSuggestion(
            List<ZonedDateTime> timestampList){
        int size = timestampList.size();
        if(size > 1000){
            return "day";
        }else{
            return "hour";
        }
    }

    /**
     * Code referred from
     * https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
     */
    public static HashMap<String, Integer>
    sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static long unitToMillisecond(String unitName){
        if(unitName.equals("day")){
            return 1000 * 60 * 60 * 24;
        }
        else if(unitName.equals("hour")){
            return 1000 * 60 * 60;
        }
        else if(unitName.equals("minute")){
            return 1000 * 60;
        }
        else if(unitName.equals("second")){
            return 1000;
        }else{
            return 0;
        }
    }

    public static String timestampStringOf(ZonedDateTime zdt){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return zdt.format(formatter);
    }

//    public static ListModelArray variantListModelArrayFrom(List<Trace> traces){
//
//        List<Variant> variants = new ArrayList<Variant>();
//
//        HashMap<Integer, ArrayList<String>> variantMap = new HashMap<Integer, ArrayList<String>>();
//        HashMap<Integer, ArrayList<Long>> durationMap = new HashMap<Integer, ArrayList<Long>>();
//        HashMap<Integer, Integer> variantCases = new HashMap<Integer, Integer>();
//
//
//        for(int i=0; i<traces.size();i++){
//            Trace trace = traces.get(i);
//            int variantId = trace.getVariantId();
//            ArrayList<String> eventSequence =
//                    new ArrayList<String>(trace.getEventSequence());
//            long traceDuration = trace.getDuration();
//            variantMap.put(variantId, eventSequence);
//            if(!durationMap.containsKey(variantId) ||
//                    (durationMap.size()==0)){
//                ArrayList<Long> newList = new ArrayList<Long>();
//                newList.add(traceDuration);
//                durationMap.put(variantId, newList);
//            }else{
//                ArrayList<Long> theList = durationMap.get(variantId);
//                theList.add(traceDuration);
//                durationMap.put(variantId, theList);
//            }
//            if((variantCases.size()==0) ||
//                    !variantCases.containsKey(variantId)){
//                variantCases.put(variantId, 1);
//            }else{
//                int numOfCases = variantCases.get(variantId) + 1;
//                variantCases.put(variantId, numOfCases);
//            }
//        }

//        Set<Integer> vIdSet = variantMap.keySet();
//        ArrayList<Integer> vIdList = new ArrayList<Integer>();
//
//        for(Integer i : vIdSet){
//            vIdList.add(i);
//        }

//        Collections.sort(vIdList);
//
//        for(int i=0; i<vIdList.size(); i++){
//            int vId = vIdList.get(i);
//            int numCases = variantCases.get(vId);
//            int numEvents = variantMap.get(vId).size();
//            ArrayList<Long> durationList = durationMap.get(vId);
//            Collections.sort(durationList);
//            double median = 0;
//            double pos1 = Math.floor((durationList.size() - 1.0) / 2.0);
//            double pos2 = Math.ceil((durationList.size() - 1.0) / 2.0);
//            if (pos1 == pos2 ) {
//                median = durationList.get((int)pos1);
//            } else {
//                median = (durationList.get((int)pos1) +
//                        durationList.get((int)pos2)) / 2.0;
//            }
//            long medianDuration = (long) median;
//            long totalDuration = 0;
//            for(int j=0; j<durationList.size(); j++){
//                totalDuration += durationList.get(j);
//            }
//            long meanDuration = totalDuration / numCases;
//            //Variant(int id, int numberOfCases, int numberOfEvents,
//            //                   long medianDuration, long meanDuration)
//            variants.add(new Variant(vId, numCases, numEvents,
//                    medianDuration, meanDuration));
//        }
//
//        return new ListModelArray(
//                variants.toArray(new Variant[variants.size()]));
//
//    }

    public static List<String> allEventNamesOf(XLog xLog){
        List<String> nameList = new ArrayList<String>();

        HashMap<String, Integer> tempMap = new HashMap<String, Integer>();

        for(int i=0; i<xLog.size(); i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                String name =
                        xEvent.getAttributes().get("concept:name").toString();
                tempMap.put(name, 0);
            }
        }

        Set<String> keySet = tempMap.keySet();
        for(String s : keySet){
            nameList.add(s);
        }

        return nameList;
    }

    public static List<String> allActivityNamesOf(XLog xLog){
        List<String> nameList = new ArrayList<String>();

        HashMap<String, Integer> tempMap = new HashMap<String, Integer>();

        for(int i=0; i<xLog.size(); i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                String name = xEvent.getAttributes().get(
                        "concept:name").toString();
                String lifecycle = xEvent.getAttributes().get(
                        "lifecycle:transition").toString().toLowerCase();
                if(lifecycle.equals("complete")){
                    tempMap.put(name, 0);
                }
//                String composedName = name + "-" + lifecycle;
//                tempMap.put(composedName, 0);
            }
        }

        Set<String> keySet = tempMap.keySet();
        for(String s : keySet){
            nameList.add(s);
        }

        return nameList;
    }

    public static HashMap<String, Integer> activityFrequencyOf(XLog xLog){
        HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
        for(int i=0; i<xLog.size(); i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                String name = xEvent.getAttributes().get(
                        "concept:name").toString();
                String lifecycle = xEvent.getAttributes().get(
                        "lifecycle:transition").toString().toLowerCase();
//                String composedName = name + "-" + lifecycle;
                if(lifecycle.equals("complete")){
                    if((tempMap.size()==0) ||
                            (!tempMap.containsKey(name))){
                        tempMap.put(name, 1);
                    }else{
                        int y = tempMap.get(name) + 1;
                        tempMap.put(name, y);
                    }
                }
            }
        }
        return tempMap;
    }

    public static HashMap<String, Double> relativeFrequencyOf(
            HashMap<String, Integer> activityFrequency){

        HashMap<String, Double> relativeFreq =
                new HashMap<String, Double>();

        Set<String> keySet = activityFrequency.keySet();

        double sum = 0;
        for(String key : keySet){
            double value = activityFrequency.get(key);
            sum += value;
        }


        for(String key : keySet){
            double value = activityFrequency.get(key);
//            System.out.println(value);
            double relFreq = (value/sum) * 100;
            relativeFreq.put(key, relFreq);
        }
//        System.out.println(relativeFreq);
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

//    private static boolean hasTimeline(String eventName, XTrace xTrace){
//        int count = 0;
//        for(int i=0; i<xTrace.size(); i++){
//            XEvent xEvent = xTrace.get(i);
//            String name =
//                    xEvent.getAttributes().get("concept:name").toString();
//            if(name.equals(eventName)){
//                count += 1;
//            }
//        }
//        if(count > 0) return true;
//
//        return false;
//    }
//
//    private static ZonedDateTime getCompleteTime(String eventName, XTrace xTrace){
//        for(int i=0; i<xTrace.size();i++){
//            XEvent xEvent = xTrace.get(i);
//            String name =
//                    xEvent.getAttributes().get("concept:name").toString();
//            String lifecycle =
//                    xEvent.getAttributes().get("lifecycle:transition").toString();
//            if(name.equals(eventName) && lifecycle.equals("complete")){
//                return zonedDateTimeOf(xEvent);
//            }
//        }
//        return null;
//    }

    public static List<Activity> activitiesOf(XLog xLog){

        HashMap<String, List<Long>> durationHashMap =
                new HashMap<String, List<Long>>();

        for(int i=0; i<xLog.size();i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);

                String name =
                        xEvent.getAttributes().get(
                                "concept:name").toString();
                String life =
                        xEvent.getAttributes().get(
                                "lifecycle:transition").toString().toLowerCase();

                ZonedDateTime timestamp =
                        zonedDateTimeOf(xEvent);

                long duration = 0;

                int eventCount = eventOccurCount(name, xTrace);

                if(life.equals("start") && (eventCount>0)){
                    for(int k=j; k<xTrace.size();k++){
                        XEvent kEvent = xTrace.get(k);
                        String kName = kEvent.getAttributes().get(
                                "concept:name").toString();
                        String kLife = kEvent.getAttributes().get(
                                "lifecycle:transition").toString().toLowerCase();
//                        System.out.println(kName + "; " + kLife);
                        if(name.equals(kName) && kLife.equals("complete")){
                            ZonedDateTime endTime = zonedDateTimeOf(kEvent);
                            duration = durationOf(timestamp, endTime);
                            break;
                        }
                    }
                    if((durationHashMap.size()==0) ||
                            (!durationHashMap.containsKey(name))){
                        List<Long> newDurations = new ArrayList<Long>();
                        newDurations.add(duration);
                        durationHashMap.put(name, newDurations);
                    }else{
                        List<Long> existDurations =
                                durationHashMap.get(name);
                        existDurations.add(duration);
                        durationHashMap.put(name, existDurations);
                    }
                }
            }
        }

//        System.out.println(durationHashMap);

        HashMap<String, Long> medianDurationHashMap =
                new HashMap<String, Long>();

        HashMap<String, Long> meanDurationHashMap =
                new HashMap<String, Long>();

        HashMap<String, Long> durationRangeHashMap =
                new HashMap<String, Long>();

        Set<String> durationkeySet = durationHashMap.keySet();
        for(String s : durationkeySet){
            List<Long> longList = durationHashMap.get(s);
            long medianDuration = medianValueOf(longList);
            long meanDuration = meanValueOf(longList);
            long durationRange = durationRangeOf(longList);
            medianDurationHashMap.put(s, medianDuration);
            meanDurationHashMap.put(s, meanDuration);
            durationRangeHashMap.put(s, durationRange);
        }

//        System.out.println(medianDurationStringHashMap);
//        System.out.println(meanDurationStringHashMap);
//        System.out.println(durationRangeStringHashMap);

        List<String> actNameList = allActivityNamesOf(xLog);
        HashMap<String, Integer> actFreqHashMap =
                activityFrequencyOf(xLog);
        HashMap<String, Double> relFreqHashMap =
                relativeFrequencyOf(actFreqHashMap);
        HashMap<String, Long> aggDurHashMap =
                activityAggregateDurationOf(xLog);

        List<Activity> activityList = new ArrayList<Activity>();

        for(int i=0; i<actNameList.size(); i++){
            String name = actNameList.get(i);
            int actFreq = actFreqHashMap.get(name);
            double relFreq = relFreqHashMap.get(name);
            long medianDur = 0;
            long meanDur = 0;
            long durRange = 0;
            long aggDur = 0;
            if(medianDurationHashMap.containsKey(name)){
                medianDur = medianDurationHashMap.get(name);
            }
            if(meanDurationHashMap.containsKey(name)){
                meanDur = meanDurationHashMap.get(name);
            }
            if(durationRangeHashMap.containsKey(name)){
                durRange = durationRangeHashMap.get(name);
            }
            if(aggDurHashMap.containsKey(name)){
                aggDur = aggDurHashMap.get(name);
            }
            Activity act = new Activity(
                    name, actFreq, relFreq, medianDur,
                    meanDur, durRange, aggDur);
            activityList.add(act);
        }

//        System.out.println(activityList.toString());


        return activityList;
    }

    public static long medianValueOf(List<Long> longList){
        Collections.sort(longList);
        double median = 0;
        double pos1 = Math.floor((longList.size() - 1.0) / 2.0);
        double pos2 = Math.ceil((longList.size() - 1.0) / 2.0);
        if (pos1 == pos2 ) {
            median = longList.get((int)pos1);
        } else {
            median = (longList.get((int)pos1) + longList.get((int)pos2)) / 2.0;
        }
        return new Double(median).longValue();
    }

    public static long meanValueOf(List<Long> longList){
        long sum = 0;
        for(int i=0; i<longList.size(); i++){
            sum += longList.get(i);
        }
        return sum / longList.size();
    }

    public static long durationRangeOf(List<Long> longList){
        Collections.sort(longList);
        long range = longList.get(longList.size()-1) - longList.get(0);
        return range;
    }

    public static String durationShortStringOf(long duration) {

        long days = duration / (1000 * 60 * 60 * 24);
        long hours = duration / (1000 * 60 * 60);
        long mins = duration / (1000 * 60);
        long secs = duration / (1000);
        long millis = duration;

        if((days < 1) && (hours >= 1) && (mins >= 1) && (secs >= 1)){
            return String.format("%d hrs, %d mins",
                    TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));
        }
        if((days < 1) && (hours < 1) && (mins >= 1) && (secs >= 1)){
            return String.format("%d mins, %d secs",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }
        if((days < 1) && (hours < 1) && (mins < 1) && (secs >= 1)){
            return String.format("%d secs, %d millis",
                    TimeUnit.MILLISECONDS.toSeconds(duration),
                    TimeUnit.MILLISECONDS.toMillis(duration) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration)));
        }
        if((days < 1) && (hours < 1) && (mins < 1) && (secs < 1)){
            return String.format("%d millis", millis);
        }
        if((days > 1) && (hours < 1) && (mins > 1)){
            return String.format("%d d, %d mins",
                    TimeUnit.MILLISECONDS.toDays(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));
        }
        return String.format("%d d, %d hrs",
                TimeUnit.MILLISECONDS.toDays(duration),
                TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)));
    }

    public static String durationStringOf(long duration) {

        long days = duration / (1000 * 60 * 60 * 24);
        long hours = duration / (1000 * 60 * 60);
        long mins = duration / (1000 * 60);
        long secs = duration / (1000);
        long millis = duration;

        if((days < 1) && (hours >= 1) && (mins >= 1) && (secs >= 1)){
            return String.format("%d hours, %d mins",
                    TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));
        }
        if((days < 1) && (hours < 1) && (mins >= 1) && (secs >= 1)){
            return String.format("%d mins, %d secs",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }
        if((days < 1) && (hours < 1) && (mins < 1) && (secs >= 1)){
            return String.format("%d secs, %d millis",
                    TimeUnit.MILLISECONDS.toSeconds(duration),
                    TimeUnit.MILLISECONDS.toMillis(duration) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration)));
        }
        if((days < 1) && (hours < 1) && (mins < 1) && (secs < 1)){
            return String.format("%d millis", millis);
        }
        if((days > 1) && (hours < 1) && (mins > 1)){
            return String.format("%d days, %d mins",
                    TimeUnit.MILLISECONDS.toDays(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));
        }
        return String.format("%d days, %d hours",
                TimeUnit.MILLISECONDS.toDays(duration),
                TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)));
    }


    public static int eventOccurCount(String eventName, XTrace xTrace){
        int count = 0;
        for(int i=0; i<xTrace.size(); i++){
            XEvent xEvent = xTrace.get(i);
            String name = xEvent.getAttributes().get("concept:name").toString();
            if(name.equals(eventName)){
                count+=1;
            }
        }
        return count;
    }

    /**
     * It returns the duration of an activity described in a trace,
     * as long as it has 'start' and 'complete'. Otherwise, it returns 0;
     * @param eventName
     * @param xTrace
     * @return
     */
    public static long activityDurationOf(String eventName, XTrace xTrace){
        int count = eventOccurCount(eventName, xTrace);
        if(count <= 1) return 0;

        ZonedDateTime startTime = null;
        ZonedDateTime endTime = null;
        for(int i=0; i<xTrace.size(); i++){
            XEvent xEvent = xTrace.get(i);
            String xeName =
                    xEvent.getAttributes().get("concept:name").toString();
            String lifecycle =
                    xEvent.getAttributes().get("lifecycle:transition").toString().toLowerCase();
            ZonedDateTime zdt = zonedDateTimeOf(xEvent);
            if(xeName.equals(eventName) && lifecycle.equals("start")){
                startTime = zdt;
            }
            if(xeName.equals(eventName) && lifecycle.equals("complete")){
                endTime = zdt;
            }
        }
        return durationOf(startTime, endTime);
    }

    public static List<Long> activityDurationListOf(
            String eventName, XLog xLog)
    {
        List<Long> durationList = new ArrayList<Long>();
        for(int i=0; i<xLog.size(); i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                String xeName =
                        xEvent.getAttributes().get("concept:name").toString();
                int occur = eventOccurCount(xeName, xTrace);
                if(occur > 1){
                    long duration = activityDurationOf(xeName, xTrace);
                    durationList.add(duration);
                }
            }
        }
        return durationList;
    }

    public static HashMap<String, List<Long>> durationListHashMapOf(XLog xLog){
        HashMap<String, List<Long>> durationHashMap =
                new HashMap<String, List<Long>>();

        for(int i=0; i<xLog.size();i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);

                String name =
                        xEvent.getAttributes().get(
                                "concept:name").toString();
                String life =
                        xEvent.getAttributes().get(
                                "lifecycle:transition").toString().toLowerCase();

                ZonedDateTime timestamp =
                        zonedDateTimeOf(xEvent);

                long duration = 0;

                int eventCount = eventOccurCount(name, xTrace);

                if(life.equals("start") && (eventCount>0)){
                    for(int k=j; k<xTrace.size();k++){
                        XEvent kEvent = xTrace.get(k);
                        String kName = kEvent.getAttributes().get(
                                "concept:name").toString();
                        String kLife = kEvent.getAttributes().get(
                                "lifecycle:transition").toString().toLowerCase();
//                        System.out.println(kName + "; " + kLife);
                        if(name.equals(kName) && kLife.equals("complete")){
                            ZonedDateTime endTime = zonedDateTimeOf(kEvent);
                            duration = durationOf(timestamp, endTime);
                            break;
                        }
                    }
                    if((durationHashMap.size()==0) ||
                            (!durationHashMap.containsKey(name))){
                        List<Long> newDurations = new ArrayList<Long>();
                        newDurations.add(duration);
                        durationHashMap.put(name, newDurations);
                    }else{
                        List<Long> existDurations =
                                durationHashMap.get(name);
                        existDurations.add(duration);
                        durationHashMap.put(name, existDurations);
                    }
                }
            }
        }

//        System.out.println(durationHashMap);
        return durationHashMap;
    }


    public static HashMap<String, Long> activityAggregateDurationOf(XLog xLog){
        HashMap<String, List<Long>>
                actDurListHashMap = durationListHashMapOf(xLog);

        HashMap<String, Long> aggDurations = new HashMap<String, Long>();

        HashMap<String, String> aggStrings = new HashMap<String, String>();

        Set<String> keySet = actDurListHashMap.keySet();
        for(String key : keySet){
            List<Long> durList = actDurListHashMap.get(key);
            long sum = 0;
            for(int i=0; i<durList.size();i++){
                sum += durList.get(i);
            }
//            long aggDur = sum / durList.size();
            long aggDur = sum;
            aggDurations.put(key, aggDur);
            aggStrings.put(key, durationStringOf(aggDur));
        }

//        System.out.println(aggStrings.toString());

        return aggDurations;
    }

    public static long since1970(long timeMillis){
        ZonedDateTime st = ZonedDateTime.of(
                1970,
                1,
                1,
                0,
                0,
                0,
                0,
                ZoneId.systemDefault()
        );
        long stMillis = epochMilliOf(st);
        return timeMillis - stMillis;
    }

    public static int eventsSizeOf(XLog xLog) {
        int num = 0;
        for(int i=0; i<xLog.size();i++){
            XTrace xTrace = xLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                String life = "";
                if(xEvent.getAttributes().containsKey("lifecycle:transition")){
                    life = xEvent.getAttributes().get(
                            "lifecycle:transition").toString().toLowerCase();
                }
                if(life.equals("complete")){
                    num += 1;
                }
            }
        }
        System.out.println(num);
        return num;
    }
}
