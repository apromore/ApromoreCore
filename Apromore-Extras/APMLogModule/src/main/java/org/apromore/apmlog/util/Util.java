package org.apromore.apmlog.util;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;

import java.io.File;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Util {

    private static final int day =1000 * 60 * 60 * 24;
    private static final int hour =1000 *  60 * 60;
    private static final int minute =1000 *  60;
    private static final int second =1000;

    public static long epochMilliOf(ZonedDateTime zonedDateTime){

        long s = zonedDateTime.toInstant().toEpochMilli();
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
        Instant i = Instant.ofEpochMilli(millisecond);
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

    public static List<String> allActivityNamesOf(XLog theLog){
        List<String> nameList = new ArrayList<String>();

        HashMap<Long, Integer> markedTimeMilliHM =
                new HashMap<Long, Integer>();

        HashMap<String, Integer> tempMap = new HashMap<String, Integer>();

        for(int i=0; i<theLog.size(); i++){
            XTrace xTrace = theLog.get(i);
            for(int j=0; j<xTrace.size(); j++){
                XEvent xEvent = xTrace.get(j);
                long jETime = epochMilliOf(Util.zonedDateTimeOf(xEvent));
                String name = xEvent.getAttributes().get(
                        "concept:name").toString();
                String lifecycle = xEvent.getAttributes().get(
                        "lifecycle:transition").toString().toLowerCase();
                if(lifecycle.equals("start")) {
                    //find and mark the end of the activity
                    for(int k=j; k<xTrace.size();k++) {
                        XEvent kEvent = xTrace.get(k);
                        String kName = kEvent.getAttributes().get(
                                "concept:name").toString();
                        String kLife = kEvent.getAttributes().get(
                                "lifecycle:transition").toString().toLowerCase();
                        if(kName.equals(name) && kLife.equals("complete")) {
                            markedTimeMilliHM.put(jETime, 0);
                        }
                    }
                }
                if((lifecycle.equals("complete") || lifecycle.equals("start")) &&
                        !markedTimeMilliHM.containsKey(jETime)){
                    tempMap.put(name, 0);
                }
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
        return convertMilliseconds(duration);
//        long days = duration / (1000 * 60 * 60 * 24);
//        long hours = duration / (1000 * 60 * 60);
//        long mins = duration / (1000 * 60);
//        long secs = duration / (1000);
//        long millis = duration;
//
//        if((days < 1) && (hours >= 1) && (mins >= 1) && (secs >= 1)){
//            return String.format("%d.%dhrs",
//                    TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
//                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));
//        }
//        if((days < 1) && (hours < 1) && (mins >= 1) && (secs >= 1)){
//            return String.format("%d.%dmins",
//                    TimeUnit.MILLISECONDS.toMinutes(duration),
//                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
//        }
//        if((days < 1) && (hours < 1) && (mins < 1) && (secs >= 1)){
//            return String.format("%d.%dsecs",
//                    TimeUnit.MILLISECONDS.toSeconds(duration),
//                    TimeUnit.MILLISECONDS.toMillis(duration) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration)));
//        }
//        if((days < 1) && (hours < 1) && (mins < 1) && (secs < 1)){
//            return String.format("%dmillis", millis);
//        }
//        if((days > 1) && (hours < 1) && (mins > 1)){
//            return String.format("%dd,%dmins",
//                    TimeUnit.MILLISECONDS.toDays(duration),
//                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));
//        }
//        return String.format("%dd,%dhrs",
//                TimeUnit.MILLISECONDS.toDays(duration),
//                TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)));
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");

    public static String convertMilliseconds(long milliseconds) {
        double seconds = milliseconds / 1000.0D;
        double minutes = seconds / 60.0D;
        double hours = minutes / 60.0D;
        double days = hours / 24.0D;
        double weeks = days / 7.0D;
        double months = days / 30.0D;
        double years = days / 365.0D;

        if (years > 1.0D) {
            return decimalFormat.format(years) + " yrs";
        }

        if (months > 1.0D) {
            return decimalFormat.format(months) + " mths";
        }

        if (weeks > 1.0D) {
            return decimalFormat.format(weeks) + " wks";
        }

        if (days > 1.0D) {
            return decimalFormat.format(days) + " d";
        }

        if (hours > 1.0D) {
            return decimalFormat.format(hours) + " hrs";
        }

        if (minutes > 1.0D) {
            return decimalFormat.format(minutes) + " mins";
        }

        if (seconds > 1.0D) {
            return decimalFormat.format(seconds) + " secs";
        }

        if (milliseconds > 1.0D) {
            return decimalFormat.format(milliseconds) + " millis";
        }

        return "instant";
    }

    public static String durationStringOf(long duration) {

        return convertMilliseconds(duration);

//        long years = duration / ((1000 * 60 * 60 * 24 * 365) + (1000 * 60 * 60 * 6));
//        long days = duration / (1000 * 60 * 60 * 24);
//        long hours = duration / (1000 * 60 * 60);
//        long mins = duration / (1000 * 60);
//        long secs = duration / (1000);
//        long millis = duration;
//
//
//
//        if((days < 1) && (hours >= 1) && (mins >= 1) && (secs >= 1)){
//            return String.format("%d.%dhrs",
//                    TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
//                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));
//        }
//        if((days < 1) && (hours < 1) && (mins >= 1) && (secs >= 1)){
//            return String.format("%d.%dmins",
//                    TimeUnit.MILLISECONDS.toMinutes(duration),
//                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
//        }
//        if((days < 1) && (hours < 1) && (mins < 1) && (secs >= 1)){
//            return String.format("%d.%dsecs",
//                    TimeUnit.MILLISECONDS.toSeconds(duration),
//                    TimeUnit.MILLISECONDS.toMillis(duration) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration)));
//        }
//        if((days < 1) && (hours < 1) && (mins < 1) && (secs < 1)){
//            return String.format("%dmillis", millis);
//        }
//        if((days > 1) && (hours < 1) && (mins > 1)){
//            return String.format("%dd,%dmins",
//                    TimeUnit.MILLISECONDS.toDays(duration),
//                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)));
//        }
//        long dayNum = TimeUnit.MILLISECONDS.toDays(duration);
//        long hourNum = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
//        long minNum = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
//        return String.format("%dd,%dhrs", dayNum, hourNum);
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
        return num;
    }

    public static XLog validateXLog(XLog xLog) {
        List<XTrace> tobeRemoved = new ArrayList<XTrace>();
        for(int i=0; i<xLog.size(); i++) {
            XTrace xTrace = xLog.get(i);
            if(xTrace.size() < 1) {
                tobeRemoved.add(xTrace);
            }
            for(XEvent xEvent : xTrace) {
                if(!xEvent.getAttributes().containsKey("time:timestamp")) {
                    tobeRemoved.add(xTrace);
                }
            }
        }
        xLog.removeAll(tobeRemoved);
        return xLog;
    }



    public static XLog filteredLogOfTimeperiod(
            ZonedDateTime minTime, ZonedDateTime maxTime, XLog theLog,
            String filteredLogName)
    {

        for(int i=0; i<theLog.size(); i++) {
            XTrace xTrace = theLog.get(i);
            for(int j=0; j<xTrace.size(); j++) {
                XEvent xEvent = xTrace.get(j);
                ZonedDateTime zdt = Util.zonedDateTimeOf(xEvent);

            }
        }

        List<XTrace> tobeRemovedTraces = new ArrayList<XTrace>();
        for(int i=0; i<theLog.size(); i++) {
            XTrace xTrace = theLog.get(i);
            List<XEvent> tobeRemoved = new ArrayList<XEvent>();
            for(int j=0; j<xTrace.size(); j++) {
                XEvent xEvent = xTrace.get(j);
                ZonedDateTime zdt = Util.zonedDateTimeOf(xEvent);


                if(zdt.isBefore(minTime) ||
                        zdt.isAfter(maxTime)) {
                    tobeRemoved.add(xEvent);
                }else{
                }
            }

            xTrace.removeAll(tobeRemoved);

            if(xTrace.size() < 1) {
                tobeRemovedTraces.add(xTrace);
            }
        }
        theLog.removeAll(tobeRemovedTraces);


        return theLog;
    }

    public static String formatIntTo1DecimalString(int theInt) {
        DecimalFormat df1 = new DecimalFormat("#.#");
        if(theInt >= 1000) {
            double doubleValue = new Integer(theInt).doubleValue();
            double newValue = doubleValue / 1000;
            return df1.format(newValue) + "k";
        }
        return theInt + "";
    }

    public static boolean isNumeric(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

}
