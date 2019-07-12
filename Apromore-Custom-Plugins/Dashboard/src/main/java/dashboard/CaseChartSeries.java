package dashboard;

import org.zkoss.chart.Charts;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;

import java.time.ZonedDateTime;
import java.util.*;

public class CaseChartSeries {

    private List<CaseData> caseDataList;

    private long earliestTimeMilli = 0;
    private long latestTimeMilli = 0;
    private ZonedDateTime earliestTime;
    private ZonedDateTime latestTime;
    private String eventOverTimeMode = "second";
    private List<Long> timestampUnits = new ArrayList<Long>();
    private long singleTimestampUnit = 0;
    private long overtimeXMin = 0;
    private long overtimeXMax = 0;

    public CaseChartSeries(List<CaseData> caseDataList) {
        this.caseDataList = caseDataList;

        timestampUnits = getTimestampUnits();

        System.out.println(caseDataList.toString());
    }

    class ZDTIntPair {
        private ZonedDateTime key;
        private Integer value;
        public ZDTIntPair(ZonedDateTime key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public ZonedDateTime getKey() {
            return key;
        }

        public Integer getValue() {
            return value;
        }
    }

    class IntIntPair {
        private Integer key;
        private Integer value;
        public IntIntPair(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public Integer getKey() {
            return key;
        }

        public Integer getValue() {
            return value;
        }
    }

    public List<Series> getSeries(String option) {
        List<Series> seriesList = new ArrayList<Series>();


        if(option.equals("eventOverTime")) {

            for(int i=0; i<caseDataList.size(); i++) {
                CaseData cd = caseDataList.get(i);

                List<LongIntPair> pl = getEventOverTimeList(cd);

                Series series = new Series();
                series.setName(cd.getLogName());
                series.setType(Charts.LINE);
                for(int j=0; j<pl.size(); j++){
                    LongIntPair p = pl.get(j);
                    Point point = new Point(p.getKey(), p.getValue());
                    series.addPoint(point);
                }
                System.out.println(series);
                seriesList.add(series);
            }
        }
        if(option.equals("activeCaseOverTime")) {

            for(int i=0; i<caseDataList.size(); i++) {
                CaseData cData = caseDataList.get(i);

                List<ZDTIntPair> acotPL =
                        getActiveCaseOverTimeList(cData);

                Series series = new Series();
                series.setName(cData.getLogName());
                series.setType(Charts.AREA);
                for(int j=0; j<acotPL.size(); j++){
                    ZDTIntPair p = acotPL.get(j);
                    long jTime = Util.epochMilliOf(p.getKey());
                    Point point = new Point(jTime, p.getValue());
                    series.addPoint(point);
                }
                seriesList.add(series);
            }
        }
        if(option.equals("caseVariants")) {

            for(int i=0; i<caseDataList.size(); i++) {
                CaseData cData = caseDataList.get(i);

                List<IntIntPair> cvPL =
                        getCaseVariantsList(cData);

                Series series = new Series();
                series.setName(cData.getLogName());
                series.setType(Charts.COLUMN);

                for(int j=0; j<cvPL.size(); j++) {
                    IntIntPair p = cvPL.get(j);
                    int x = p.getKey();
                    int y = p.getValue();
                    Point point = new Point(x, y);
                    series.addPoint(point);
                }
                seriesList.add(series);
            }
        }
        if(option.equals("eventsPerCase")) {

            for(int i=0; i<caseDataList.size(); i++) {
                CaseData cData = caseDataList.get(i);

                List<IntIntPair> ecPL =
                        getEventsPerCaseList(cData);

                Series series = new Series();
                series.setName(cData.getLogName());
                series.setType(Charts.COLUMN);

                for(int j=0; j<ecPL.size(); j++) {
                    IntIntPair p = ecPL.get(j);
                    int x = p.getKey();
                    int y = p.getValue();
                    Point point = new Point(x, y);
                    series.addPoint(point);
                }
                seriesList.add(series);
            }
        }
        if(option.equals("caseDuration")) {

            for(int i=0; i<caseDataList.size(); i++) {
                CaseData cData = caseDataList.get(i);

                List<LongIntPair> cdPL =
                        getCaseDurationPL(cData);

                System.out.println(cdPL.toString());

                Series series = new Series();
                series.setName(cData.getLogName());
                series.setType(Charts.COLUMN);

                for(int j=0; j<cdPL.size(); j++) {
                    LongIntPair p = cdPL.get(j);
                    long x = p.getKey();
                    int y = p.getValue();
                    String pName = Util.durationStringOf(x);
                    Point point = new Point(pName, y);
//                    Point point = new Point(x, y);

//                    point.setName(pName);
                    series.addPoint(point);
                }
//                series.addPoint(cdPL.get(cdPL.size()-1).getKey() + (1000 * 60 * 60 * 2), 0);
                seriesList.add(series);
            }
        }


        return seriesList;
    }

    public List<LongIntPair> getEventOverTimeList(CaseData caseData){

        System.out.println(caseDataList);
        String mode = xUnitModeOf(durationOf(caseDataList));
        this.eventOverTimeMode = mode;

        long timeUnit = 0;
        if(mode.equals("day")) timeUnit = 1000 * 60 * 60 * 24;
        if(mode.equals("hour")) timeUnit = 1000 * 60 * 60;
        if(mode.equals("minute")) timeUnit = 1000 * 60;
        if(mode.equals("second")) timeUnit = 1000;

        ZonedDateTime sZDT = Util.formatZonedDateTime(earliestTime, mode);
        ZonedDateTime eZDT = Util.formatZonedDateTime(latestTime, mode);
        long sZDTMilli = Util.epochMilliOf(sZDT);
        long eZDTMilli = Util.epochMilliOf(eZDT);

        /**
         * make the base time units for XAxis
         */

        List<LongIntPair> timeEventsPL =
                new ArrayList<LongIntPair>();

        long bTime = sZDTMilli - timeUnit * 5;

        if((overtimeXMin==0) || (bTime <= overtimeXMin)) {
            overtimeXMin = bTime;
        }

        while(bTime <= eZDTMilli) {
            bTime += timeUnit;
            LongIntPair p = new LongIntPairImpl(bTime, 0);
            timeEventsPL.add(p);
        }

        if((overtimeXMax==0) || (bTime >= overtimeXMax)) {
            overtimeXMax = bTime;
        }

        System.out.println(timeEventsPL.toString());


        List<ZonedDateTime> allTimestamps = caseData.getTimestamps();
        Collections.sort(allTimestamps);
        for(int i=0; i<allTimestamps.size();i++){
            long iTimeMilli = Util.epochMilliOf(allTimestamps.get(i));
            for(int j=0; j<timeEventsPL.size();j++){
                long jTimeMilli = timeEventsPL.get(j).getKey();
                if(( iTimeMilli >= (jTimeMilli-timeUnit)) &&
                        ( iTimeMilli <= (jTimeMilli + timeUnit))) {
                    int y = timeEventsPL.get(j).getValue() + 1;
                    LongIntPair p = new LongIntPairImpl(jTimeMilli, y);
                    timeEventsPL.set(j, p);
                    break;
                }
            }
        }
        System.out.println(timeEventsPL.toString());
        return timeEventsPL;
    }

    private List<ZDTIntPair> getActiveCaseOverTimeList(
            CaseData caseData)
    {
        System.out.println(caseDataList);
        String mode = xUnitModeOf(durationOf(caseDataList));

        ZonedDateTime startTime = caseData.getStartTime();
        startTime = Util.formatZonedDateTime(startTime, mode);
        ZonedDateTime endTime = caseData.getEndTime();
        endTime = Util.formatZonedDateTime(endTime, mode);
        endTime.plusDays(1);

//        long sTimeMilli = Util.epochMilliOf(startTime);
        long eTimeMilli = Util.epochMilliOf(endTime);

        ZonedDateTime pTime = startTime;
        long pTimeMilli = Util.epochMilliOf(pTime);

        List<ZDTIntPair> timeCasePL =
                new ArrayList<ZDTIntPair>();

        while(pTimeMilli <= eTimeMilli) {
            timeCasePL.add(new ZDTIntPair(pTime, 0));
            if(mode.equals("day")){
                pTime = pTime.plusDays(1);
            }
            if(mode.equals("hour")){
                pTime = pTime.plusHours(1);
            }
            if(mode.equals("minute")){
                pTime = pTime.plusMinutes(1);
            }
            if(mode.equals("second")){
                pTime = pTime.plusSeconds(1);
            }
            pTimeMilli = Util.epochMilliOf(pTime);
            System.out.println("pTime=" + pTime + " | pTimeMilli=" + pTimeMilli);
        }

        System.out.println(timeCasePL.toString());

        List<Case> caseList = caseData.getCaseList();

        for(int i=0; i<caseList.size(); i++) {
            Case c = caseList.get(i);
            ZonedDateTime cSTime = c.getStartTime();
            ZonedDateTime cETime = c.getEndTime();
            for(int j=0; j<timeCasePL.size(); j++) {
                ZDTIntPair pair = timeCasePL.get(j);
                ZonedDateTime theTime = pair.getKey();
                if( ( theTime.isAfter(cSTime) || theTime.isEqual(cSTime) ) &&
                        ( theTime.isBefore(cETime) || theTime.isEqual(cETime) )) {
                    int newCount = pair.getValue() + 1;
                    timeCasePL.set(j, new ZDTIntPair(theTime, newCount));
                }
            }
        }

        System.out.println(timeCasePL.toString());

        return timeCasePL;
    }

    private List<IntIntPair> getCaseVariantsList(CaseData caseData) {
        List<IntIntPair> caseVariantsPL =
                new ArrayList<IntIntPair>();

        HashMap<Integer, Integer> caseVariantsHM =
                new HashMap<Integer, Integer>();

        List<Case> caseList = caseData.getCaseList();

        for(int i=0; i<caseList.size(); i++) {
            Case c = caseList.get(i);
            int cVID = c.getVariantId();
            if(caseVariantsHM.containsKey(cVID)){
                int count = caseVariantsHM.get(cVID) + 1;
                caseVariantsHM.put(cVID, count);
            }else{
                caseVariantsHM.put(cVID, 1);
            }
        }
        //reorder the set
        List<Integer> keyList = new ArrayList<Integer>();
        for(Integer i : caseVariantsHM.keySet()) {
            keyList.add(i);
        }
        Collections.sort(keyList);

        for(int i=0; i<keyList.size(); i++) {
            int key = keyList.get(i);
            int v = caseVariantsHM.get(key);
            caseVariantsPL.add(new IntIntPair(key, v));
        }

        return caseVariantsPL;
    }

    private List<IntIntPair> getEventsPerCaseList(CaseData caseData) {
        HashMap<Integer, Integer> eventsCasesHM =
                new HashMap<Integer, Integer>();

        List<Case> caseList = caseData.getCaseList();

        for(int i=0; i<caseList.size(); i++) {
            Case c = caseList.get(i);
            int numEvents = c.getEventsSize();
            if(eventsCasesHM.containsKey(numEvents)) {
                int y = eventsCasesHM.get(numEvents) + 1;
                eventsCasesHM.put(numEvents, y);
            }else{
                eventsCasesHM.put(numEvents, 1);
            }
        }

        // reorder the keys
        List<Integer> keyList = new ArrayList<Integer>();
        for(Integer key : eventsCasesHM.keySet()) {
            keyList.add(key);
        }
        Collections.sort(keyList);

        List<IntIntPair> eventCasePL =
                new ArrayList<IntIntPair>();
        for(int i=0; i<keyList.size();i++){
            int key = keyList.get(i);
            int count = eventsCasesHM.get(key);
            eventCasePL.add(new IntIntPair(key, count));
        }
        return eventCasePL;
    }


    private List<LongIntPair> getCaseDurationPL(CaseData caseData) {
        System.out.println(singleTimestampUnit);

        HashMap<Long, Integer> caseDurHM = new HashMap<Long, Integer>();

        for(int i=0; i<timestampUnits.size(); i++) {
            caseDurHM.put(timestampUnits.get(i), 0);
        }

        System.out.println(caseDurHM.toString());

        List<Case> caseList = caseData.getCaseList();
        for(int i=0; i<caseList.size(); i++) {
            Case c = caseList.get(i);
            long cDur = c.getDuration();
            for(int j=0; j<timestampUnits.size(); j++) {
                long jDur = timestampUnits.get(j);
                long jDurPlus = jDur + singleTimestampUnit;
                long jDurMinus = jDur - singleTimestampUnit;
                if((cDur >= jDurMinus) && (cDur <= jDurPlus)) {
                    int y = caseDurHM.get(jDur) + 1;
                    caseDurHM.put(jDur, y);
                    break;
                }
            }
        }

        System.out.println(caseDurHM.toString());


        List<Long> keyList = new ArrayList<Long>();
        for(Long key : caseDurHM.keySet()) {
            keyList.add(key);
        }
        Collections.sort(keyList);


        List<LongIntPair> caseDurationPL =
                new ArrayList<LongIntPair>();

        for(int i=0; i<keyList.size(); i++) {
            long key = keyList.get(i);
            int y = caseDurHM.get(key);
            caseDurationPL.add(new LongIntPairImpl(key, y));
        }

        System.out.println(caseDurationPL.toString());

        return caseDurationPL;
    }

    private List<Long> getTimestampUnits() {
        List<Long> allDurs = new ArrayList<Long>();
        for(int i=0; i<caseDataList.size();i++) {
            List<Case> caseList = caseDataList.get(i).getCaseList();
            for(int j=0; j<caseList.size(); j++) {
                allDurs.add(caseList.get(j).getDuration());
            }
        }
        Collections.sort(allDurs);
        long first = allDurs.get(0);
        long last = allDurs.get(allDurs.size()-1);
        long overallDur = last - first;
        singleTimestampUnit = overallDur / 100;
        long current = first;

        List<Long> timestampUnits = new ArrayList<Long>();
        int counter = 0;
        while(counter < 100) {
            timestampUnits.add(current);
            current += singleTimestampUnit;
            counter += 1;
        }
        timestampUnits.add(last);
        System.out.println(timestampUnits);
        return  timestampUnits;
    }

    private long durationOf(List<CaseData> caseDatas) {

        earliestTimeMilli = caseDatas.get(0).getStartTimeMilli();
        latestTimeMilli = caseDatas.get(0).getEndTimeMilli();

        for(int i=0; i<caseDatas.size(); i++) {
            long st = caseDatas.get(i).getStartTimeMilli();
            long et = caseDatas.get(i).getEndTimeMilli();
            System.out.println(st);
            ZonedDateTime sZDT = caseDatas.get(i).getStartTime();
            System.out.println(sZDT);
            ZonedDateTime eZDT = caseDatas.get(i).getEndTime();
            System.out.println(eZDT);
            if(st <= earliestTimeMilli){
                earliestTimeMilli = st;
                earliestTime = sZDT;
            }
            if(et >= latestTimeMilli){
                latestTimeMilli = et;
                latestTime = eZDT;
            }
            System.out.println(earliestTime);
            System.out.println(latestTime);

        }
        long dur = latestTimeMilli - earliestTimeMilli;
        return dur;
    }

    public String xUnitModeOf(long durationMilli){
        String mode = "second";
        if((durationMilli / (1000 * 60 * 60 * 24)) >= 128) {
            mode = "day";
        }else if((durationMilli / (1000 * 60 * 60 * 24) < 128) &&
                (durationMilli / (1000 * 60 * 60 * 24) >= 30)) {
            mode = "hour";
        }else if((durationMilli / (1000 * 60 * 60 * 24) < 30) &&
                (durationMilli / (1000 * 60 * 60) >= 1)) {
            mode = "minute";
        }
        return mode;
    }

    public long getEarliestTimeMilli() {
        return earliestTimeMilli;
    }

    public long getLatestTimeMilli() {
        return latestTimeMilli;
    }

    public String getEventOverTimeMode() {
        return eventOverTimeMode;
    }

    public long getOvertimeXMin() {
        return overtimeXMin;
    }

    public long getOvertimeXMax() {
        return overtimeXMax;
    }
}
