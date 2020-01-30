package apromore.plugin.portal.logfilteree;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.zkoss.chart.*;
import org.zkoss.chart.plotOptions.AreaPlotOptions;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimpleXYModel;
import org.zkoss.zul.XYModel;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LogFilterEEChartUtil {
    private XLog xLog;
    private Series caseOTSeries;
    private Series caseDurationSeries;
    private String caseOTMode = "second";

    public LogFilterEEChartUtil(XLog xlog) {
        this.xLog = xlog;

        /**
         * Loop (1):
         * - retrieves min and max time of the log
         * - retrieves all the case/trace durations of the log
         */
        long earliestTime = 0; //for case overtime
        long latestTime = 0; //for case overtime
        List<Long> allDurations = new ArrayList<>(); // for case durations

        for(int i=0; i<xlog.size() ;i++) {
            XTrace xTrace = xlog.get(i);
            if(xTrace.size() > 0) {
                /**
                 * For case over time
                 */
                for(int j=0; j<xTrace.size(); j++) {
                    XEvent xEvent = xTrace.get(j);
                    long xTimeMilli = Util.epochMilliOf(Util.zonedDateTimeOf(xEvent));
                    if(earliestTime == 0 || xTimeMilli < earliestTime) earliestTime = xTimeMilli;
                    if(latestTime == 0 || xTimeMilli > latestTime) latestTime = xTimeMilli;
                }
                /**
                 * For case duration
                 */
                long s = Util.epochMilliOf(Util.zonedDateTimeOf(xTrace.get(0)));
                long e = Util.epochMilliOf(Util.zonedDateTimeOf(xTrace.get(xTrace.size()-1)));
                allDurations.add(e - s);
            }
        }

        /**
         * For case over time
         */
        long duration = latestTime - earliestTime;
        this.caseOTMode = xUnitModeOf(duration);
        int caseOTCateNum = 100;
        long caseOTCateUnit = (latestTime - earliestTime) / 100;

        UnifiedMap<Long, Integer> caseOTFreqMap = new UnifiedMap<>(); // Long as epochMilli of the timestamp, Integer as frequency
        long currentTime = earliestTime;
        caseOTFreqMap.put(earliestTime, 0);
        for(int i=1; i<=caseOTCateNum; i++) {
            caseOTFreqMap.put(currentTime, 0);
            currentTime += caseOTCateUnit;
        }
        caseOTFreqMap.put(latestTime, 0);
        /**
         * For case duration
         */
        Collections.sort(allDurations);
        int numOfUnits = 100;
        long first = allDurations.get(0);
        long last = allDurations.get(allDurations.size() - 1);
        long cateUnit = (last - first) / numOfUnits;

        UnifiedMap<Long, Integer> caseDurMap = new UnifiedMap<>(); // Long as duration, Integer as frequency
        long current = first;
        caseDurMap.put(first, 0);
        for(int i=1; i <= numOfUnits; i++) {
            caseDurMap.put(current, 0);
            current += cateUnit;
        }
        caseDurMap.put(last, 0);

        /**
         * Loop (2):
         * - Fill in the frequency of the case over time map
         * - Fill in the case duration map
         */
        for(int i=0; i<xlog.size(); i++) {
            XTrace xTrace = xlog.get(i);
            if(xTrace.size() > 0) {
                long sTime = Util.epochMilliOf(Util.zonedDateTimeOf(xTrace.get(0)));
                long eTime = Util.epochMilliOf(Util.zonedDateTimeOf(xTrace.get(xTrace.size()-1)));
                for(Long key : caseOTFreqMap.keySet()) {
                    if(key >= sTime && key <= eTime) {
                        int y = caseOTFreqMap.get(key) + 1;
                        caseOTFreqMap.put(key, y);
                    }
                }

                long caseDuration = eTime - sTime;
                for(Long key : caseDurMap.keySet()) {
                    long p = key - cateUnit;
                    long n = key + cateUnit;
                    if(caseDuration >= p && caseDuration <= n) {
                        int y = caseDurMap.get(key) + 1;
                        caseDurMap.put(key, y);
                        break;
                    }
                }
            }
        }
        /**
         * re-order the categories of Case Over Time
         */
        List<Long> caseOTkeyList = new ArrayList<>();
        for(Long key : caseOTFreqMap.keySet()) {
            caseOTkeyList.add(key);
        }
        Collections.sort(caseOTkeyList);

        /**
         * re-order the categories of case duration
         */
        List<Long> caseDurKeyList = new ArrayList<>();
        for(Long key : caseDurMap.keySet()) {
            caseDurKeyList.add(key);
        }
        Collections.sort(caseDurKeyList);

        /**
         * Make Case Over Time chart series
         */
        this.caseOTSeries = new Series();
        for(int i=0; i<caseOTkeyList.size(); i++) {
            long x = caseOTkeyList.get(i);
            int y = caseOTFreqMap.get(x);
            Point point = new Point(x, y);
            this.caseOTSeries.addPoint(point);
        }

        /**
         * Make Case Duration chart series
         */
        this.caseDurationSeries = new Series();
        for(int i=0; i<caseDurKeyList.size(); i++) {
            long x = caseDurKeyList.get(i);
            int y = caseDurMap.get(x);
            if(y > 0) {
                String id = Util.durationStringOf(x);
                Point point = new Point(x, y);
                point.setId(id);
                this.caseDurationSeries.addPoint(point);
            }
        }
        System.out.println(caseDurationSeries);
    }


    public Charts createCaseOverTimeChart(Charts theChart) {
        for (int i = 0; i < theChart.getSeriesSize(); i++){
            theChart.getSeries().remove();
            theChart.getChildren().clear();
        }
        theChart.addSeries(this.caseOTSeries);
        if(this.caseOTMode.equals("day")){
            theChart.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e}, {point.y} Cases / Day");
        }
        if(this.caseOTMode.equals("hour")){
            theChart.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H}, {point.y} Cases / Hour");
        }
        if(this.caseOTMode.equals("minute")){
            theChart.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H:%M}, {point.y} Cases / Minute");
        }
        if(this.caseOTMode.equals("second")){
            theChart.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H:%M:%S}, {point.y} Cases / Second");
        }
        theChart.getXAxis().setType("datetime");
        theChart.setType(Charts.AREA);
        theChart.getExporting().setEnabled(false);

        theChart.getYAxis().getLabels().setEnabled(false);
        theChart.getXAxis().setTitle("");
        theChart.getYAxis().setTitle("");
        theChart.getLegend().setEnabled(false);
        theChart.getCredits().setEnabled(false);
        return theChart;
    }

    public Charts createCaseDurationChart(Charts theChart) {
        for (int i = 0; i < theChart.getSeriesSize(); i++){
            theChart.getSeries().remove();
            theChart.getChildren().clear();
        }
        theChart.addSeries(this.caseDurationSeries);
        theChart.getTooltip().setHeaderFormat("");
        theChart.getTooltip().setPointFormat("up to {point.id}<br/>Number of Cases: {point.y}");
        theChart.setType(Charts.COLUMN);
        theChart.getExporting().setEnabled(false);
        theChart.getYAxis().getLabels().setEnabled(false);
        theChart.getXAxis().getLabels().setEnabled(false);
        theChart.getXAxis().setTitle("");
        theChart.getYAxis().setTitle("");
        theChart.getLegend().setEnabled(false);
        theChart.getCredits().setEnabled(false);
        theChart.getYAxis().setType("logarithmic");
        return theChart;
    }

    public static Charts casesOverTimeChartOf(Charts theChart, XLog xlog) {

        ZonedDateTime zdtMin = null;
        ZonedDateTime zdtMax = null;
        for(int i=0; i<xlog.size() ;i++) {
            XTrace xTrace = xlog.get(i);
            if(xTrace.size() > 0) {
                for(int j=0; j<xTrace.size(); j++) {
                    XEvent xEvent = xTrace.get(j);
                    ZonedDateTime xZdt = Util.zonedDateTimeOf(xEvent);
                    if(zdtMax == null || xZdt.isAfter(zdtMax)) zdtMax = xZdt;
                    if(zdtMin == null || xZdt.isBefore(zdtMin)) zdtMin = xZdt;
                }
            }
        }

        if(zdtMin == null || zdtMax == null) return null;

        long minTimeMilli = Util.epochMilliOf(zdtMin);
        long maxTimeMilli = Util.epochMilliOf(zdtMax);
        long duration = maxTimeMilli - minTimeMilli;

        String mode = xUnitModeOf(duration);

        zdtMax.plusDays(1);


        ZonedDateTime pTime = zdtMin;
        long pTimeMilli = Util.epochMilliOf(pTime);

        List<ZDTIntPair> timeCasePL = new ArrayList<>();

        while(pTimeMilli <= maxTimeMilli) {
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
        }

        for(int i=0; i<xlog.size(); i++) {
            XTrace xTrace = xlog.get(i);
            if(xTrace.size() > 0) {
                ZonedDateTime cSTime = Util.zonedDateTimeOf(xTrace.get(0));
                ZonedDateTime cETime = Util.zonedDateTimeOf(xTrace.get(xTrace.size()-1));
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
        }

        XYModel xyModel = new SimpleXYModel();
        for(int i=0; i<timeCasePL.size(); i++) {
            ZDTIntPair pair = timeCasePL.get(i);
            long x = Util.epochMilliOf(pair.getKey());
            int y = pair.getValue();
            xyModel.addValue("", x, y);
        }

        for (int i = 0; i < theChart.getSeriesSize(); i++){
            theChart.getSeries().remove();
            theChart.getChildren().clear();
        }

        theChart.setModel(xyModel);

        if(mode.equals("day")){
            theChart.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e}, {point.y} Cases / Day");
        }
        if(mode.equals("hour")){
            theChart.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H}, {point.y} Cases / Hour");
        }
        if(mode.equals("minute")){
            theChart.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H:%M}, {point.y} Cases / Minute");
        }
        if(mode.equals("second")){
            theChart.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H:%M:%S}, {point.y} Cases / Second");
        }

        theChart.getXAxis().setType("datetime");
        theChart.setType(Charts.AREA);
        theChart.getExporting().setEnabled(false);

        theChart.getYAxis().getLabels().setEnabled(false);
        theChart.getXAxis().setTitle("");
        theChart.getYAxis().setTitle("");
        theChart.getLegend().setEnabled(false);
        theChart.getCredits().setEnabled(false);
//        theChart.getYAxis().setType("logarithmic");

        return theChart;
    }

    public static Charts caseDurationChartOf(Charts theChart, XLog xLog) {
        List<Long> allDurations = new ArrayList<>();
        for(int i=0; i<xLog.size();i++) {
            XTrace trace = xLog.get(i);
            long s = Util.epochMilliOf(Util.zonedDateTimeOf(trace.get(0)));
            long e = Util.epochMilliOf(Util.zonedDateTimeOf(trace.get(trace.size()-1)));
            allDurations.add(e - s);
        }
        Collections.sort(allDurations);
        int numOfUnits = 100;
        long first = allDurations.get(0);
        long last = allDurations.get(allDurations.size() - 1);
        long cateUnit = (last - first) / numOfUnits;

        UnifiedMap<Long, Integer> caseDurMap = new UnifiedMap<>();
        long current = first;
        caseDurMap.put(first, 0);
        for(int i=1; i <= numOfUnits; i++) {
            caseDurMap.put(current, 0);
            current += cateUnit;
        }
//        System.out.println(caseDurMap);

        for(int i=0; i<xLog.size(); i++) {
            XTrace trace = xLog.get(i);
            long s = Util.epochMilliOf(Util.zonedDateTimeOf(trace.get(0)));
            long e = Util.epochMilliOf(Util.zonedDateTimeOf(trace.get(trace.size()-1)));
            long duration = e - s;
            for(Long key : caseDurMap.keySet()) {
                long p = key - cateUnit;
                long n = key + cateUnit;
                if(duration >= p && duration <= n) {
                    int y = caseDurMap.get(key) + 1;
                    caseDurMap.put(key, y);
                    break;
                }
            }
        }
//        System.out.println(caseDurMap);

        /**
         * re-order the categories
         */
        List<Long> keyList = new ArrayList<>();
        for(Long key : caseDurMap.keySet()) {
            keyList.add(key);
        }
        Collections.sort(keyList);

        /**
         * Set chart model
         */
        for (int i = 0; i < theChart.getSeriesSize(); i++){
            theChart.getSeries().remove();
            theChart.getChildren().clear();
        }

        Series series = new Series();
        for(int i=0; i<keyList.size(); i++) {
            long x = keyList.get(i);
            int y = caseDurMap.get(x);
            String id = Util.durationStringOf(x);
            Point point = new Point(x, y);
            point.setId(id);
            series.addPoint(point);
        }

        theChart.addSeries(series);
        theChart.getTooltip().setHeaderFormat("");
        theChart.getTooltip().setPointFormat("up to {point.id}<br/>Number of Cases: {point.y}");
        theChart.setType(Charts.COLUMN);
        theChart.getExporting().setEnabled(false);
        theChart.getYAxis().getLabels().setEnabled(false);
        theChart.getXAxis().getLabels().setEnabled(false);
        theChart.getXAxis().setTitle("");
        theChart.getYAxis().setTitle("");
        theChart.getLegend().setEnabled(false);
        theChart.getCredits().setEnabled(false);
        theChart.getYAxis().setType("logarithmic");
        return theChart;
    }


    public static long earliestTimeMilliOf(XLog xlog) {
        ZonedDateTime zdtMin = null;
        for(int i=0; i<xlog.size() ;i++) {
            XTrace xTrace = xlog.get(i);
            if(xTrace.size() > 0) {
                for(int j=0; j<xTrace.size(); j++) {
                    XEvent xEvent = xTrace.get(j);
                    ZonedDateTime xZdt = Util.zonedDateTimeOf(xEvent);
                    if(zdtMin == null || xZdt.isBefore(zdtMin)) zdtMin = xZdt;
                }
            }
        }
        if(zdtMin == null) return 0;
        return Util.epochMilliOf(zdtMin);
    }

    public static long latestTimeMilliOf(XLog xlog) {
        ZonedDateTime zdtMax = null;
        for(int i=0; i<xlog.size() ;i++) {
            XTrace xTrace = xlog.get(i);
            if(xTrace.size() > 0) {
                for(int j=0; j<xTrace.size(); j++) {
                    XEvent xEvent = xTrace.get(j);
                    ZonedDateTime xZdt = Util.zonedDateTimeOf(xEvent);
                    if(zdtMax == null || xZdt.isAfter(zdtMax)) zdtMax = xZdt;
                }
            }
        }
        if(zdtMax == null) return 0;
        return Util.epochMilliOf(zdtMax);
    }

    public static long shortestDurationOf(XLog xLog) {
        List<Long> allDurations = new ArrayList<>();
        for (int i = 0; i < xLog.size(); i++) {
            XTrace trace = xLog.get(i);
            long s = Util.epochMilliOf(Util.zonedDateTimeOf(trace.get(0)));
            long e = Util.epochMilliOf(Util.zonedDateTimeOf(trace.get(trace.size() - 1)));
            allDurations.add(e - s);
        }
        Collections.sort(allDurations);
        return allDurations.get(0);
    }

    public static long longestDurationOf(XLog xLog) {
        List<Long> allDurations = new ArrayList<>();
        for (int i = 0; i < xLog.size(); i++) {
            XTrace trace = xLog.get(i);
            long s = Util.epochMilliOf(Util.zonedDateTimeOf(trace.get(0)));
            long e = Util.epochMilliOf(Util.zonedDateTimeOf(trace.get(trace.size() - 1)));
            allDurations.add(e - s);
        }
        Collections.sort(allDurations);
        return allDurations.get(allDurations.size() - 1);
    }

    private static String xUnitModeOf(long durationMilli){
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

}
