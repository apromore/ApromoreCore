package dashboard;

import javafx.print.Collation;
import org.apromore.model.LogSummaryType;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Series;
import org.zkoss.chart.Theme;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import dashboard.portal.DashboardPlugin;

public class MainController extends SelectorComposer<Component> {

    private final String FILE_STORE_PATH = "upload/";
    private HashMap<String, XLog> eventLogsHM = new HashMap<String, XLog>();
    ListModelList chartThemeModel =
            new ListModelList(
                    new String[] { "DEFAULT", "GRID",
                            "GRAY", "DARK_BLUE",
                            "DARK_GREEN", "DARK_UNICA",
                            "SAND_SIGNIKA", "GRID_LIGHT" });
    private Theme selectedTheme = Theme.DEFAULT; // as default
    private String selectedFileName = "none";
    private HashMap<String, String> currentColorHM = new HashMap<String, String>();
    private String logToBeFiltered = "";
    private CaseChartSeries caseChartSeries = null;
    private int WINDOW_BORDER_HEIGHT = 5;
    private int WINDOW_BORDER_WIDTH = 20;

    private EventLogService eventLogService = (EventLogService) SpringUtil.getBean("eventLogService");

    public void doFinally() {
        String id = Executions.getCurrent().getParameter("id");
        if (id == null) {
            System.err.println("Dashboard invoked with no id");
            return;
        }

        List<LogSummaryType> logSummaries = DashboardPlugin.sessionMap.get(id);
        if (logSummaries == null) {
            System.err.println("Dashboard invoked with id containing no logs");
            return;
        }

        for (LogSummaryType logSummary: logSummaries) {
            System.out.println("Loading log: " + logSummary.getName());
            XLog log = eventLogService.getXLog(logSummary.getId());

            //*********************************************
            eventLogsHM.put(logSummary.getName(), log);
            //*********************************************

        }
        System.out.println("All logs loaded");
        setEventLogs();
    }

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        lbxFilterCase.setMultiple(true);

        initUI();


        //*****************************************************
        //  TESTING
        //*****************************************************
//        List<String> nameList= new ArrayList<String>();
//
//        nameList.add("sample6.xes");
//        nameList.add("sample6b.xes");
//        nameList.add("sample6c.xes");
//
//        setEventLogs(nameList);

        //*****************************************************
    }



    @Wire
    Charts chartOVEventOT,chartOVActiveCaseOT, chartOVCaseVariant,chartOVEventsPC,
            chartOVCaseDur, chartOVMeanActDur,
            chartATFreq, chartATMediDur, chartATMeanDur,
            chartATDurRng, chartATTtlDur,
            chartRSFreq, chartRSMediDur, chartRSMeanDur,
            chartRSDurRng, chartRSTtlDur;

    @Wire
    Listbox lbxOVCases, lbxFilterCase, lbxFilterAct, lbxFilterRes,
            lbxOVCaseDetails;

    @Wire
    Listcell lcOvEvents, lcOvCases, lcOvActivities,lcOvMedianCaseDuration,
            lcOvMeanCaseDuration, lcOvStart, lcOvEnd,
            lcATActs,lcATMiniFreq,lcATMediFreq,lcATMeanFreq,
            lcATMaxiFreq,lcATFSD,
            lcRSRes, lcRSMiniFreq, lcRSMediFreq, lcRSMeanFreq,
            lcRSMaxiFreq, lcRSFSD;

    @Wire
    Listheader lhOVCaseDetail;

    @Wire
    Label lblOVSelectedLog;

    @Wire
    East eastOV, eastAT, eastRS;

    @Wire
    North northOV, northAT, northRS;

    @Wire
    South southOV;

    @Wire
    Grid gridOVVariants, gridATAllActs, gridATFIC, gridATLIC,
            gridRSAll, gridRSFIC, gridRSLIC;

    @Wire
    Tab tabOVCases, tabOVVariants, tabATActs, tabATFIC, tabATLIC,
            tabRSAll, tabRSFIC, tabRSLIC;

    @Wire
    Radiogroup rdgView;

    @Wire
    Menu menuColor, menuClose, menuFilter, menuSelectLog;

    @Wire
    West west01;

    @Wire
    Label lblOVCaseDetails;

    @Wire
    Div southOVnDiv2;



    //******************************************************
    // Composer Functions
    //******************************************************

    public void onClientInfo(ClientInfoEvent evt) {
        int dW = evt.getDesktopWidth();
        int dH = evt.getDesktopHeight();
        System.out.println("dW = " + dW + "; dH = " + dH);
        int nH = dH / 7 * 3;
        northOV.setHeight(nH + "px");
        northAT.setHeight(nH + "px");
        northRS.setHeight(nH + "px");
        int tabWidth = 230;
        int tabWidth2 = 210;


//        String northOVWidthString = northOV.getWidth();
//        System.out.println("northOV width: " + northOVWidthString);
//        int pxIndex = northOVWidthString.indexOf("px");
//        String pxValue = northOVWidthString.substring(0, pxIndex);
//        int nOVWInt = new Integer(pxValue);

        int chartWidth = dW - tabWidth - WINDOW_BORDER_WIDTH - 30;
//        int chartWidth = nOVWInt - tabWidth - 30;


        int chartWidth2 = dW - tabWidth2- WINDOW_BORDER_WIDTH  - 30;
        int chartHeight = nH - WINDOW_BORDER_HEIGHT - 32;
        chartOVEventOT.setWidth(chartWidth);
        chartOVActiveCaseOT.setWidth(chartWidth);
        chartOVCaseVariant.setWidth(chartWidth);
        chartOVEventsPC.setWidth(chartWidth);
        chartOVCaseDur.setWidth(chartWidth);
        chartATFreq.setWidth(chartWidth2);
        chartATMediDur.setWidth(chartWidth2);
        chartATMeanDur.setWidth(chartWidth2);
        chartATDurRng.setWidth(chartWidth2);
        chartATTtlDur.setWidth(chartWidth2);
        chartRSFreq.setWidth(chartWidth2);
        chartRSMediDur.setWidth(chartWidth2);
        chartRSMeanDur.setWidth(chartWidth2);
        chartRSDurRng.setWidth(chartWidth2);
        chartRSTtlDur.setWidth(chartWidth2);

        chartOVEventOT.setHeight(chartHeight);
        chartOVActiveCaseOT.setHeight(chartHeight);
        chartOVCaseVariant.setHeight(chartHeight);
        chartOVEventsPC.setHeight(chartHeight);
        chartOVCaseDur.setHeight(chartHeight);
        chartATFreq.setHeight(chartHeight);
        chartATMediDur.setHeight(chartHeight);
        chartATMeanDur.setHeight(chartHeight);
        chartATDurRng.setHeight(chartHeight);
        chartATTtlDur.setHeight(chartHeight);
        chartRSFreq.setHeight(chartHeight);
        chartRSMediDur.setHeight(chartHeight);
        chartRSMeanDur.setHeight(chartHeight);
        chartRSDurRng.setHeight(chartHeight);
        chartRSTtlDur.setHeight(chartHeight);
    }

    private void initUI() {
//        Label lblCloseOVSouth = new Label();
//        lblCloseOVSouth.setSclass("labelClose");
//        lblCloseOVSouth.setValue("Close");
//        lblCloseOVSouth.addEventListener(Events.ON_CLICK,
//                new EventListener() {
//                    public void onEvent(final Event pEvent) {
//                        closeOVSouth();
//                    }
//                });
//        southOVnDiv2.appendChild(lblCloseOVSouth);
        Button btCloseOVSouth = new Button();
        btCloseOVSouth.setIconSclass("fas fa-times");
        btCloseOVSouth.addEventListener(Events.ON_CLICK,
                new EventListener() {
                    public void onEvent(final Event pEvent) {
                        closeOVSouth();
                    }
                });
        southOVnDiv2.appendChild(btCloseOVSouth);

    }



    //******************************************************
    // General Functions
    //******************************************************

    private void setEventLogs()  {

        List<String> logNames = new ArrayList<String>();
        for(String key : eventLogsHM.keySet()) {
            logNames.add(key);
        }
        Collections.sort(logNames);

//        selectedFileName = fileNames.get(0);
        selectedFileName = logNames.get(0);
        rdgView.setSelectedIndex(0);

//        setMenuItems(fileNames);
        setMenuItems(logNames);
        setOverviewCharts();
        setOVData(selectedFileName);
        setActivityCharts();
        setATData(selectedFileName);
        setResourceCharts();
        setRSData(selectedFileName);

    }

    private void setEventLogs(List<String> fileNames) throws Exception {

        for(int i=0; i<fileNames.size(); i++){
            String fileName = fileNames.get(i);
            String path = FILE_STORE_PATH + fileName;

            File logFile = new File(path);
            List<XLog> xLogList = Util.parseXLogFile(logFile);
            eventLogsHM.put(fileName, xLogList.get(0));
        }
        selectedFileName = fileNames.get(0);

        rdgView.setSelectedIndex(0);

        setMenuItems(fileNames);

        setOverviewCharts();
        setOVData(selectedFileName);
        setActivityCharts();
        setATData(selectedFileName);
        setResourceCharts();
        setRSData(selectedFileName);

    }

    private void resetEventLogs() {
        west01.setVisible(false);
        setOverviewCharts();
        setOVData(selectedFileName);
        setActivityCharts();
        setATData(selectedFileName);
        setResourceCharts();
        setRSData(selectedFileName);
    }

    private void setOverviewCharts() {

        //****************************
        // Chart OV Event over time
        //****************************
        int sSize;
        int sCount = chartOVEventOT.getSeriesSize();
        System.out.println(sCount);
        chartOVEventOT.invalidate();
        for (int i = 0; i < sCount; i++){
            chartOVEventOT.getSeries().remove();
//            sSize = chartOVEventOT.getSeriesSize();
//            System.out.println(sSize);
        }

        sSize = chartOVEventOT.getSeriesSize();
        System.out.println(sSize);
        chartOVEventOT.getSeries(0).remove();
        sSize = chartOVEventOT.getSeriesSize();
        System.out.println(sSize);

        chartOVEventOT.getXAxis().setType("datetime");
        List<CaseData> cds = new ArrayList<CaseData>();

        System.out.println(eventLogsHM.toString());

        for(String s : eventLogsHM.keySet()) {
            XLog xLog = eventLogsHM.get(s);
            CaseData cd = new CaseData(s, xLog);
            cds.add(cd);
        }
        caseChartSeries = new CaseChartSeries(cds);
        List<Series> ls = caseChartSeries.getSeries("eventOverTime");


        for(int i=0; i<ls.size(); i++) {
            Series series = ls.get(i);
            series.setType("line");
            chartOVEventOT.addSeries(series);
        }
        chartOVEventOT.getXAxis().setMin(caseChartSeries.getOvertimeXMin());
        chartOVEventOT.getXAxis().setMax(caseChartSeries.getOvertimeXMax());
        chartOVEventOT.getYAxis().setTitle("Events");
        chartOVEventOT.getXAxis().setTitle("Log timeline");
        chartOVEventOT.getYAxis().setMin(0);

        chartOVEventOT.getTooltip().setHeaderFormat("<b>{series.name}</b><br/>");
        String mode = caseChartSeries.getEventOverTimeMode();
        if(mode.equals("day")){
            chartOVEventOT.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e}, {point.y} Events / Day");
        }
        if(mode.equals("hour")){
            chartOVEventOT.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H}, {point.y} Events / Hour");
        }
        if(mode.equals("minute")){
            chartOVEventOT.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H:%M}, {point.y} Events / Minute");
        }
        if(mode.equals("second")){
            chartOVEventOT.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H:%M:%S}, {point.y} Events / Second");
        }
        chartOVEventOT.getYAxis().getLabels().setEnabled(false);

        chartOVEventOT.getLegend().setBorderWidth(0);

        //****************************
        // Chart OV Active Case OT
        //****************************
        int ovActCaseOTSize = chartOVActiveCaseOT.getSeriesSize();
        for (int i = 0; i < ovActCaseOTSize; i++){
            chartOVActiveCaseOT.getSeries().remove();
        }

        List<Series> seriesActCaseOTList =
                caseChartSeries.getSeries("activeCaseOverTime");
        for(int i=0; i<seriesActCaseOTList.size();i++) {
            Series series = seriesActCaseOTList.get(i);
            series.setType("area");
            chartOVActiveCaseOT.addSeries(series);
        }
        chartOVActiveCaseOT.getTooltip().setHeaderFormat(
                "<b>{series.name}</b><br/>");
        chartOVActiveCaseOT.getXAxis().setMin(caseChartSeries.getOvertimeXMin());
        chartOVActiveCaseOT.getXAxis().setMax(caseChartSeries.getOvertimeXMax());
        chartOVActiveCaseOT.getXAxis().setType("datetime");
        chartOVActiveCaseOT.getYAxis().setTitle("Cases");
        chartOVActiveCaseOT.getXAxis().setTitle("Log timeline");
        chartOVActiveCaseOT.getYAxis().setMin(0);
        chartOVActiveCaseOT.setType(Charts.COLUMN);
        if(mode.equals("day")){
            chartOVActiveCaseOT.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e}, {point.y} Cases / Day");
        }
        if(mode.equals("hour")){
            chartOVActiveCaseOT.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H}, {point.y} Cases / Hour");
        }
        if(mode.equals("minute")){
            chartOVActiveCaseOT.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H:%M}, {point.y} Cases / Minute");
        }
        if(mode.equals("second")){
            chartOVActiveCaseOT.getTooltip().setPointFormat(
                    "{point.x:%Y-%m-%e T %H:%M:%S}, {point.y} Cases / Second");
        }
        chartOVActiveCaseOT.getYAxis().getLabels().setEnabled(false);

        chartOVActiveCaseOT.getLegend().setBorderWidth(0);

        //****************************
        // Chart OV Case Variant
        //****************************
        int ovCaseVSize = chartOVCaseVariant.getSeriesSize();
        for (int i = 0; i < ovCaseVSize; i++){
            chartOVCaseVariant.getSeries().remove();
            chartOVCaseVariant.getChildren().clear();
        }
        chartOVCaseVariant.getSeries(0).remove();
        List<Series> seriesCaseVariantList =
                caseChartSeries.getSeries("caseVariants");
        for(int i=0; i<seriesCaseVariantList.size();i++) {
            chartOVCaseVariant.addSeries(seriesCaseVariantList.get(i));
        }

        chartOVCaseVariant.getTooltip().setHeaderFormat(
                "<b>{series.name}</b><br/>");
        chartOVCaseVariant.getXAxis().setType("category");
        chartOVCaseVariant.getYAxis().setTitle("Number of Cases");
        chartOVCaseVariant.getXAxis().setTitle("Variants");
        chartOVCaseVariant.setType(Charts.COLUMN);
        chartOVCaseVariant.getTooltip().setPointFormat(
                "Variant {point.x} : {point.y} Cases");
        chartOVCaseVariant.getXAxis().getLabels().setEnabled(false);
        chartOVCaseVariant.getYAxis().getLabels().setEnabled(false);

        chartOVCaseVariant.getLegend().setBorderWidth(0);

        //****************************
        // Chart OV Events per Case
        //****************************
        int ovEPCSize = chartOVEventsPC.getSeriesSize();
        for (int i = 0; i < ovEPCSize; i++){
            chartOVEventsPC.getSeries().remove();
            chartOVEventsPC.getChildren().clear();
        }
        chartOVEventsPC.getSeries(0).remove();
        List<Series> seriesEventsPCList = caseChartSeries.getSeries("eventsPerCase");
        for(int i=0; i<seriesEventsPCList.size();i++) {
            chartOVEventsPC.addSeries(seriesEventsPCList.get(i));
        }
        chartOVEventsPC.getTooltip().setHeaderFormat("<b>{series.name}</b><br/>");
        chartOVEventsPC.getXAxis().setType("category");
        chartOVEventsPC.getYAxis().setTitle("Number of Cases");
        chartOVEventsPC.getXAxis().setTitle("Event");
        chartOVEventsPC.setType(Charts.COLUMN);
        chartOVEventsPC.getTooltip().setPointFormat(
                "{point.x} Events: {point.y} Cases");
        chartOVEventsPC.getXAxis().getLabels().setEnabled(false);
        chartOVEventsPC.getYAxis().getLabels().setEnabled(false);

        chartOVEventsPC.getLegend().setBorderWidth(0);

        //****************************
        // Chart OV Case Duration
        //****************************
        int ovCDurSize = chartOVCaseDur.getSeriesSize();
        for (int i = 0; i < ovCDurSize; i++){
            chartOVCaseDur.getSeries().remove();
            chartOVCaseDur.getChildren().clear();
        }
        chartOVCaseDur.getSeries(0).remove();
        List<Series> seriesCaseDurList = caseChartSeries.getSeries("caseDuration");
        System.out.println(seriesCaseDurList.toString());
        for(int i=0; i<seriesCaseDurList.size();i++) {
            chartOVCaseDur.addSeries(seriesCaseDurList.get(i));
        }
        chartOVCaseDur.getTooltip().setHeaderFormat("<b>{series.name}</b><br/>");
        chartOVCaseDur.getXAxis().setType("category");
        chartOVCaseDur.setType(Charts.COLUMN);
        chartOVCaseDur.getYAxis().setTitle("Number of cases");
        chartOVCaseDur.getYAxis().setMin(0);
        chartOVCaseDur.getXAxis().setTitle("Case duration");
        chartOVCaseDur.getXAxis().getLabels().setEnabled(false);
        chartOVCaseDur.getYAxis().getLabels().setEnabled(false);
        chartOVCaseDur.getTooltip().setPointFormat(
                "up to {point.name}.<br> Number of cases: {point.y}");

        chartOVCaseDur.getLegend().setBorderWidth(0);

        //****************************
        // Chart OV Mean Act Dur
        //****************************
        int size6 = chartOVMeanActDur.getSeriesSize();
        for (int i = 0; i < size6; i++){
            chartOVMeanActDur.getSeries().remove();
            chartOVMeanActDur.getChildren().clear();
        }
        chartOVMeanActDur.getSeries(0).remove();
        List<ActivityData> ads = new ArrayList<ActivityData>();
        for(String s : eventLogsHM.keySet()){
            XLog xLog = eventLogsHM.get(s);
            ActivityData ad = new ActivityData(s, xLog);
            ads.add(ad);
        }
        ActivityChartSeries acs = new ActivityChartSeries(ads);
        List<Series> seriesMeanActDur =
                acs.getSeries("meanActivityDuration");
        for(int i=0; i<seriesMeanActDur.size();i++) {
            chartOVMeanActDur.addSeries(seriesMeanActDur.get(i));
        }
        chartOVMeanActDur.getTooltip().setHeaderFormat(
                "<b>{series.name}</b><br/>");
        chartOVMeanActDur.getXAxis().setType("category");
        chartOVMeanActDur.setType(Charts.COLUMN);
        chartOVMeanActDur.getYAxis().setTitle("Number of activities");
        chartOVMeanActDur.getYAxis().setMin(0);
        chartOVMeanActDur.getXAxis().setTitle("Mean activity duration");
        chartOVMeanActDur.getXAxis().getLabels().setEnabled(false);
        chartOVMeanActDur.getYAxis().getLabels().setEnabled(false);
        chartOVMeanActDur.getTooltip().setPointFormat(
                "up to {point.drilldown}.<br> Number of activities: {point.y}");

        chartOVMeanActDur.getLegend().setBorderWidth(0);
    }

    private void setActivityCharts() {

        List<ActivityData> ads = new ArrayList<ActivityData>();

        for(String s : eventLogsHM.keySet()){
            XLog xLog = eventLogsHM.get(s);
            ActivityData ad = new ActivityData(s, xLog);
            ads.add(ad);
        }
        ActivityChartSeries acs = new ActivityChartSeries(ads);

        //****************************
        // Chart AT Frequency
        //****************************
        int size1 = chartATFreq.getSeriesSize();
        for (int i = 0; i < size1; i++){
            chartATFreq.getSeries().remove();
        }
        List<Series> seriesFreq = acs.getSeries("frequency");
        for(int i=0; i<seriesFreq.size();i++) {
            chartATFreq.addSeries(seriesFreq.get(i));
        }
        chartATFreq.getXAxis().getLabels().setEnabled(false);
        chartATFreq.getYAxis().getLabels().setEnabled(false);
//        chartATFreq.getPlotOptions().getColumn().setPointWidth(30);
//        chartATFreq.getPlotOptions().getColumn().setBorderColor("#333333");
//        chartATFreq.getPlotOptions().getColumn().setBorderWidth(1);
        chartATFreq.getYAxis().setTitle("Frequency");
        chartATFreq.getXAxis().setTitle("Activities");
        chartATFreq.setType(Charts.COLUMN);
        chartATFreq.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>Frequency: {point.y}");
        chartATFreq.getXAxis().setType("category");
        chartATFreq.getTooltip().setHeaderFormat("{series.name}<br/>");

        chartATFreq.getLegend().setBorderWidth(0);

        //****************************************************
        // Chart Activity median duration
        //****************************************************
        int size2 = chartATMediDur.getSeriesSize();
        for (int i = 0; i < size2; i++){
            chartATMediDur.getSeries().remove();
        }
        List<Series> seriesMedDur = acs.getSeries("medianDuration");
        for(int i=0; i<seriesMedDur.size();i++) {
            chartATMediDur.addSeries(seriesMedDur.get(i));
        }
        chartATMediDur.getXAxis().getLabels().setEnabled(false);
        chartATMediDur.getYAxis().getLabels().setEnabled(false);
        chartATMediDur.getYAxis().setTitle("Median duration");
        chartATMediDur.getXAxis().setTitle("Activities");
        chartATMediDur.setType(Charts.COLUMN);
        chartATMediDur.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>{point.id}");
        chartATMediDur.getXAxis().setType("category");
        chartATMediDur.getPlotOptions().getSeries().setAllowPointSelect(true);
        chartATMediDur.getTooltip().setHeaderFormat("{series.name}<br/>");

        chartATMediDur.getLegend().setBorderWidth(0);

        //****************************************************
        // Chart Activity mean duration
        //****************************************************
        int size3 = chartATMeanDur.getSeriesSize();
        for (int i = 0; i < size3; i++){
            chartATMeanDur.getSeries().remove();
        }
        List<Series> seriesMeaDur = acs.getSeries("meanDuration");
        for(int i=0; i<seriesMeaDur.size();i++) {
            chartATMeanDur.addSeries(seriesMeaDur.get(i));
        }
        chartATMeanDur.getXAxis().getLabels().setEnabled(false);
        chartATMeanDur.getYAxis().getLabels().setEnabled(false);
        chartATMeanDur.getYAxis().setTitle("Mean duration");
        chartATMeanDur.getXAxis().setTitle("Activities");
        chartATMeanDur.setType(Charts.COLUMN);
        chartATMeanDur.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>{point.id}");
        chartATMeanDur.getXAxis().setType("category");
        chartATMeanDur.getTooltip().setHeaderFormat("{series.name}<br/>");

        chartATMeanDur.getLegend().setBorderWidth(0);

        //****************************************************
        // Activity duration range chart
        //****************************************************
        int size4 = chartATDurRng.getSeriesSize();
        for (int i = 0; i < size4; i++){
            chartATDurRng.getSeries().remove();
        }
        List<Series> seriesDurRng = acs.getSeries("durationRange");
        for(int i=0; i<seriesDurRng.size();i++) {
            chartATDurRng.addSeries(seriesDurRng.get(i));
        }
        chartATDurRng.getXAxis().getLabels().setEnabled(false);
        chartATDurRng.getYAxis().getLabels().setEnabled(false);
        chartATDurRng.getYAxis().setTitle("Duration range");
        chartATDurRng.getXAxis().setTitle("Activities");
        chartATDurRng.setType(Charts.COLUMN);
        chartATDurRng.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>{point.id}");
        chartATDurRng.getXAxis().setType("category");
        chartATDurRng.getTooltip().setHeaderFormat("{series.name}<br/>");

        chartATDurRng.getLegend().setBorderWidth(0);

        //****************************************************
        // Activity total duration chart
        //****************************************************
        int size5 = chartATTtlDur.getSeriesSize();
        for (int i = 0; i < size5; i++){
            chartATTtlDur.getSeries().remove();
        }
        List<Series> seriesAggDur = acs.getSeries("aggregateDuration");
        for(int i=0; i<seriesAggDur.size();i++) {
            chartATTtlDur.addSeries(seriesAggDur.get(i));
        }
        chartATTtlDur.getXAxis().getLabels().setEnabled(false);
        chartATTtlDur.getYAxis().getLabels().setEnabled(false);
        chartATTtlDur.getYAxis().setTitle("Aggregate duration");
        chartATTtlDur.getXAxis().setTitle("Activities");
        chartATTtlDur.setType(Charts.COLUMN);
        chartATTtlDur.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>{point.id}");
        chartATTtlDur.getXAxis().setType("category");
        chartATTtlDur.getTooltip().setHeaderFormat("{series.name}<br/>");

        chartATTtlDur.getLegend().setBorderWidth(0);
    }

    private void setResourceCharts() {
        //************************************************************
        // Resource Charts setting initiation
        //************************************************************
        List<ResourceData> resourceDataList = new ArrayList<ResourceData>();

        for (String s : eventLogsHM.keySet()) {
            XLog xLog = eventLogsHM.get(s);
            ResourceData rd = new ResourceData(s, xLog);
            resourceDataList.add(rd);
        }
        ResourceChartSeries rcs = new ResourceChartSeries(resourceDataList);

        //************************************************************
        // Resource Frequency Chart setting
        //************************************************************
        int size1 = chartRSFreq.getSeriesSize();
        for (int i = 0; i < size1; i++){
            chartRSFreq.getSeries().remove();
        }
        List<org.zkoss.chart.Series> resFreqSeriesList =
                rcs.getSeries("frequency");
        for(int i=0; i<resFreqSeriesList.size(); i++){
            chartRSFreq.addSeries(resFreqSeriesList.get(i));
        }

        chartRSFreq.getXAxis().setType("category");
        chartRSFreq.getXAxis().setTitle("Resources");
        chartRSFreq.getYAxis().setTitle("Frequency");
        chartRSFreq.getYAxis().setMin(0);
        chartRSFreq.getXAxis().getLabels().setEnabled(false);
        chartRSFreq.getYAxis().getLabels().setEnabled(false);
        chartRSFreq.getTooltip().setHeaderFormat("<b>{series.name}</b><br/>");
        chartRSFreq.getTooltip().setPointFormat(
                "Resource: {point.name}, Frequency: {point.y}");
        chartRSFreq.getPlotOptions().getSpline().getMarker().setEnabled(true);
        chartRSFreq.getLegend().setBorderWidth(0);
        //************************************************************

        //************************************************************
        // Resource Median Duration Chart setting
        //************************************************************
        int size2 = chartRSMediDur.getSeriesSize();
        for (int i = 0; i < size2; i++){
            chartRSMediDur.getSeries().remove();
        }
        List<Series> medDurSeriesList = rcs.getSeries("medianDuration");
        for(int i=0; i<medDurSeriesList.size(); i++){
            chartRSMediDur.addSeries(medDurSeriesList.get(i));
        }
        chartRSMediDur.setTitle("");
        chartRSMediDur.getXAxis().setType("category");
        chartRSMediDur.getXAxis().setTitle("Resources");
        chartRSMediDur.getYAxis().setTitle("Median duration");
        chartRSMediDur.getYAxis().setMin(0);
        chartRSMediDur.getXAxis().getLabels().setEnabled(false);
        chartRSMediDur.getYAxis().getLabels().setEnabled(false);
        chartRSMediDur.getTooltip().setHeaderFormat(
                "<b>{series.name}</b><br/>");
        chartRSMediDur.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>Median duration: {point.id}");
        chartRSMediDur.getLegend().setBorderWidth(0);
        //************************************************************

        //************************************************************
        // Resource Mean Duration Chart setting
        //************************************************************
        int size3 = chartRSMeanDur.getSeriesSize();
        for (int i = 0; i < size3; i++){
            chartRSMeanDur.getSeries().remove();
        }
        List<Series> meanDurSeriesList = rcs.getSeries("meanDuration");
        for(int i=0; i<meanDurSeriesList.size(); i++){
            chartRSMeanDur.addSeries(meanDurSeriesList.get(i));
        }
        chartRSMeanDur.setTitle("");
        chartRSMeanDur.getXAxis().setType("category");
        chartRSMeanDur.getXAxis().setTitle("Resources");
        chartRSMeanDur.getYAxis().setTitle("Mean duration");
        chartRSMeanDur.getYAxis().setMin(0);
        chartRSMeanDur.getXAxis().getLabels().setEnabled(false);
        chartRSMeanDur.getYAxis().getLabels().setEnabled(false);
        chartRSMeanDur.getTooltip().setHeaderFormat(
                "<b>{series.name}</b><br/>");
        chartRSMeanDur.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>Mean duration: {point.id}");
        chartRSMeanDur.getLegend().setBorderWidth(0);
        //************************************************************

        //************************************************************
        // Resource Duration Range Chart setting
        //************************************************************
        int size4 = chartRSDurRng.getSeriesSize();
        for (int i = 0; i < size4; i++){
            chartRSDurRng.getSeries().remove();
        }
        List<Series> durRangeSeriesList = rcs.getSeries("durationRange");
        for(int i=0; i<durRangeSeriesList.size(); i++){
            chartRSDurRng.addSeries(durRangeSeriesList.get(i));
        }
        chartRSDurRng.setTitle("");
        chartRSDurRng.getXAxis().setType("category");
        chartRSDurRng.getXAxis().setTitle("Resources");
        chartRSDurRng.getYAxis().setTitle("Duration range");
        chartRSDurRng.getYAxis().setMin(0);
        chartRSDurRng.getXAxis().getLabels().setEnabled(false);
        chartRSDurRng.getYAxis().getLabels().setEnabled(false);
        chartRSDurRng.getTooltip().setHeaderFormat(
                "<b>{series.name}</b><br/>");
        chartRSDurRng.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>Duration range: {point.id}");
        chartRSDurRng.getLegend().setBorderWidth(0);
        //************************************************************

        //************************************************************
        // Resource Aggregate Duration Chart setting
        //************************************************************
        int size5 = chartRSTtlDur.getSeriesSize();
        for (int i = 0; i < size5; i++){
            chartRSTtlDur.getSeries().remove();
        }
        List<Series> aggDurSeriesList = rcs.getSeries("aggregateDuration");
        for(int i=0; i<aggDurSeriesList.size(); i++){
            chartRSTtlDur.addSeries(aggDurSeriesList.get(i));
        }
        chartRSTtlDur.setTitle("");
        chartRSTtlDur.getXAxis().setType("category");
        chartRSTtlDur.getXAxis().setTitle("Resources");
        chartRSTtlDur.getYAxis().setTitle("Total duration");
        chartRSTtlDur.getYAxis().setMin(0);
        chartRSTtlDur.getXAxis().getLabels().setEnabled(false);
        chartRSTtlDur.getYAxis().getLabels().setEnabled(false);
        chartRSTtlDur.getTooltip().setHeaderFormat(
                "<b>{series.name}</b><br/>");
        chartRSTtlDur.getTooltip().setPointFormat(
                "<b>{point.name}</b><br/>Total duration: {point.id}");
        chartRSTtlDur.getLegend().setBorderWidth(0);
        //************************************************************
    }

    private void setOVData(String filename){

        System.out.println(filename);

        eastOV.setTitle(filename);
        lblOVSelectedLog.setValue(filename);

        CaseData cd = new CaseData(filename, eventLogsHM.get(filename));

        Integer numEvents = cd.getNumberOfEvents();

        lcOvEvents.setLabel(numEvents.toString());

        lcOvCases.setLabel(new Integer(cd.getNumberOfCases()).toString());
        lcOvActivities.setLabel(
                new Integer(cd.getNumberOfActivities()).toString());
        String mediDurString =
                Util.durationShortStringOf(cd.getMedianCaseDuration());
        String meanDurString =
                Util.durationShortStringOf(cd.getMeanCaseDuration());
        lcOvMedianCaseDuration.setLabel(mediDurString);
        lcOvMeanCaseDuration.setLabel(meanDurString);

        lcOvStart.setLabel(Util.timestampStringOf(cd.getStartTime()));
        lcOvEnd.setLabel(Util.timestampStringOf(cd.getEndTime()));
        gridOVVariants.setModel(cd.getLmaVariants());

        lbxOVCases.setModel(cd.getLmaCases());
        lbxOVCases.setSelectedIndex(0);


        tabOVCases.setLabel(
                String.format("Cases (%d)", cd.getLmaCases().getSize()));
        tabOVVariants.setLabel(
                String.format("Variants (%d)", cd.getLmaVariants().getSize()));

        AbstractListModel<?> model = cd.getLmaCases();
        model.setMultiple(true);
        lbxFilterCase.setModel(model);
        lbxFilterCase.setMultiple(true);
    }

    private void setATData(String filename) {

        ActivityData ad = new ActivityData(filename, eventLogsHM.get(filename));

        gridATAllActs.setModel(ad.lmaActivities);
        int allActSize = ad.lmaActivities.getSize();
        tabATActs.setLabel(
                String.format("All activities (%d)", allActSize));
        gridATFIC.setModel(ad.lmaFirstInCase);
        int ficSize = ad.lmaFirstInCase.getSize();
        tabATFIC.setLabel(
                String.format("First in case (%d)", ficSize));

        gridATLIC.setModel(ad.lmaLastInCase);
        int licSize = ad.lmaLastInCase.getSize();
        tabATLIC.setLabel(
                String.format("Last in case (%d)", licSize));

        // **************************
        // Activity east value
        // **************************
        eastAT.setTitle(selectedFileName);
        lcATActs.setLabel(
                new Integer(ad.getNumberOfActivities()).toString());
        lcATMiniFreq.setLabel(
                new Integer(ad.getMinimalFrequency()).toString());
        lcATMediFreq.setLabel(
                new Integer(ad.getMedianFrequency()).toString());
        lcATMeanFreq.setLabel(ad.getMeanFrequencyString());
        lcATMaxiFreq.setLabel(
                new Integer(ad.getMaxFrequency()).toString());
        lcATFSD.setLabel(
                ad.getFrequencyStdDeviation());

        AbstractListModel<?> model = ad.lmaActivities;
        model.setMultiple(true);
        lbxFilterAct.setModel(model);
        lbxFilterAct.setMultiple(true);
    }

    private void setRSData(String filename) {

        ResourceData rd =
                new ResourceData(filename, eventLogsHM.get(filename));

        gridRSAll.setModel(rd.lmaResources);
        int allResSize = rd.lmaResources.getSize();
        tabRSAll.setLabel(
                String.format("All resources (%d)", allResSize));

        gridRSFIC.setModel(rd.lmaFirstInCase);
        int ficSize = rd.lmaFirstInCase.getSize();
        tabRSFIC.setLabel(
                String.format("First in case (%d)", ficSize));

        gridRSLIC.setModel(rd.lmaLastInCase);
        int licSize = rd.lmaLastInCase.getSize();
        tabRSLIC.setLabel(
                String.format("Last in case (%d)", licSize));
        //*************************
        // Resource east value
        //*************************
        eastRS.setTitle(selectedFileName);
        lcRSRes.setLabel(
                new Integer(rd.getNumberOfResources()).toString());
        lcRSMiniFreq.setLabel(
                new Integer(rd.getMinimalFrequency()).toString());
        lcRSMediFreq.setLabel(
                new Integer(rd.getMedianFrequency()).toString());
        lcRSMeanFreq.setLabel(
                String.format("%.2f", rd.getMeanFrequency()));

        lcRSMaxiFreq.setLabel(new Integer(rd.getMaximalFrequency()).toString());
        lcRSFSD.setLabel(String.format("%.2f", rd.getFrequenctStdDeviation()));

        //*************************
        // Resource filter
        //*************************
        AbstractListModel<?> model = rd.lmaResources;
        model.setMultiple(true);
        lbxFilterRes.setModel(model);
        lbxFilterRes.setMultiple(true);
    }

    private void reset(){
        eventLogsHM = new HashMap<String, XLog>();
        selectedFileName = "";
        System.gc();
    }



    private void setMenuItems(List<String> filenames) {

        Menupopup menuClosePop = new Menupopup();
        for(int i=0; i<filenames.size(); i++) {
            String logName = filenames.get(i);
            Menuitem menuitem = new Menuitem();
            menuitem.setLabel(logName);
            menuitem.addEventListener(Events.ON_CLICK,
                    new EventListener() {
                        public void onEvent(final Event pEvent) {
                            closeLog(logName);
                        }
                    });
            menuClosePop.appendChild(menuitem);
        }
        if(menuClose.getMenupopup() != null) {
            Menupopup oldMP = menuClose.getMenupopup();
            menuClose.removeChild(oldMP);
        }
        menuClose.appendChild(menuClosePop);

        //***********************************
        // Set select menu
        //***********************************
        Menupopup menuSelectPop = new Menupopup();
        for(int i=0; i<filenames.size(); i++) {
            String logName = filenames.get(i);
            Menuitem menuitem = new Menuitem();
            menuitem.setLabel(logName);
            menuitem.addEventListener(Events.ON_CLICK,
                    new EventListener() {
                        public void onEvent(final Event pEvent) {
                            selectLog(logName);
                        }
                    });
            menuSelectPop.appendChild(menuitem);
        }
        if(menuSelectLog.getMenupopup() != null) {
            Menupopup oldSLMP = menuSelectLog.getMenupopup();
            menuSelectLog.removeChild(oldSLMP);
        }
        menuSelectLog.appendChild(menuSelectPop);

        //***********************************
        // Set filter menu
        //***********************************
        Menupopup menuFilterPop = new Menupopup();
        for(int i=0; i<filenames.size(); i++) {
            String logName = filenames.get(i);
            Menuitem miFilename = new Menuitem();
            miFilename.setLabel(logName);
            miFilename.addEventListener(Events.ON_CLICK,
                    new EventListener() {
                        public void onEvent(final Event pEvent) {
                            filterLog(logName);
                        }
                    });
            menuFilterPop.appendChild(miFilename);
        }
        if(menuFilter.getMenupopup()!= null) {
            Menupopup oldFMP = menuFilter.getMenupopup();
            menuFilter.removeChild(oldFMP);
        }
        menuFilter.appendChild(menuFilterPop);

        //***********************************
        // Set color menu
        //***********************************

        Menupopup menuColorPop = new Menupopup();
        for(int i=0; i<filenames.size(); i++) {
            String fName = filenames.get(i);
            Menu colorMenu = getColorMenu(fName);
            menuColorPop.appendChild(colorMenu);
        }
        if(menuColor.getMenupopup()!= null) {
            Menupopup oldCMP = menuColor.getMenupopup();
            menuColor.removeChild(oldCMP);
        }
        menuColor.appendChild(menuColorPop);
    }

    class ColorPair {
        private String key;
        private String value;
        public ColorPair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    private Menu getColorMenu(String logName) {
        Menupopup mPP = new Menupopup();

//        List<Pair<String, String>> colorPL =
//                new ArrayList<Pair<String, String>>();

        List<ColorPair> colorPL =
                new ArrayList<ColorPair>();

        colorPL.add(new ColorPair("red", "#EF5350"));
        colorPL.add(new ColorPair("pink", "#F06292"));
        colorPL.add(new ColorPair("pueple", "#AB47BC"));
        colorPL.add(new ColorPair("violet", "#7E57C2"));
        colorPL.add(new ColorPair("blue", "#03A9F4"));
        colorPL.add(new ColorPair("cyan", "#00F0F0"));
        colorPL.add(new ColorPair("green", "#43A047"));
        colorPL.add(new ColorPair("olive", "#C0CA33"));
        colorPL.add(new ColorPair("yellow", "#FDD835"));
        colorPL.add(new ColorPair("orange", "#FF5722"));
        colorPL.add(new ColorPair("brown", "#795548"));
        colorPL.add(new ColorPair("gray", "#757575"));

        for(int i=0; i<colorPL.size(); i++) {
            Menuitem menuItem = new Menuitem();
            String colorLabel = colorPL.get(i).getKey();
//            menuItem.setLabel("   " + colorLabel);
            String colorString = colorPL.get(i).getValue();
            menuItem.setContext(colorString);
//            String imageString = colorPL.get(i).getKey() + ".png";
//            menuItem.setImage(imageString);
            menuItem.setZclass("foo");
            menuItem.setIconSclass("fas fa-square-full");

//            String styleString =
//                    String.format("background:#FFF; color:%s", colorString);
//
//            System.out.println(colorLabel + "; " + styleString);

            menuItem.setZclass(colorLabel);

            menuItem.addEventListener(Events.ON_CLICK,
                    new EventListener() {
                        public void onEvent(final Event pEvent) {
                            changeSeriesColor(logName, colorString);
                        }
                    });
            mPP.appendChild(menuItem);
        }
        Menu menuColor = new Menu();
        menuColor.setLabel(logName);
        menuColor.appendChild(mPP);
        return menuColor;
    }

    //************************************************
    // For UI component events
    //************************************************

    public void closeLog(String logName) {
        String msg = "Close "+logName+ "?";
        if(eventLogsHM.size() < 2) {
            Messagebox.show("You have only 1 event log.\nClose tab to exit Apromore performance dashboard.", "Error", Messagebox.OK, Messagebox.ERROR);
        }else{
            Messagebox.show(msg, "Confirm Dialog",
                    Messagebox.OK |
                            Messagebox.CANCEL, Messagebox.QUESTION,
                    new org.zkoss.zk.ui.event.EventListener() {
                        public void onEvent(Event evt) throws InterruptedException {
                            if (evt.getName().equals("onOK")) {
                                eventLogsHM.remove(logName);

                                List<String> newLogList = new ArrayList<String>();
                                for(String key : eventLogsHM.keySet()) {
                                    newLogList.add(key);
                                    selectedFileName = key;
                                }

                                setMenuItems(newLogList);
                                resetEventLogs();
                            }
                        }
                    });
        }

    }

    public void selectLog(String logName) {
        if(!selectedFileName.equals(logName)) {
            selectedFileName = logName;
            setOVData(logName);
            setATData(logName);
            setRSData(logName);
            lblOVCaseDetails.setValue("");
            lbxOVCaseDetails.setModel(new ListModelList<>());
            lbxOVCases.setSelectedIndex(0);
            ListModel lm = lbxOVCases.getModel();
            Case c = (Case) lm.getElementAt(0);
            System.out.println(c.getCaseId());
            showCaseDetails(c.getCaseId());
        }
    }

    public void changeSeriesColor(String logName, String color){

        currentColorHM.put(logName, color);

        int seriesIndex = 0;
        for(int i=0; i<chartOVEventOT.getSeriesSize(); i++) {
            String sName = chartOVEventOT.getSeries(i).getName();
            if(sName.equals(logName)) {
                seriesIndex = i;
                break;
            }
        }
        chartOVEventOT.getSeries(seriesIndex).setColor(color);

        //*************
        // The order of series should be the same for the other charts.

        chartOVActiveCaseOT.getSeries(seriesIndex).setColor(color);
        chartOVCaseVariant.getSeries(seriesIndex).setColor(color);
        chartOVEventsPC.getSeries(seriesIndex).setColor(color);
        chartOVCaseDur.getSeries(seriesIndex).setColor(color);

        chartOVMeanActDur.getSeries(seriesIndex).setColor(color);

        chartATFreq.getSeries(seriesIndex).setColor(color);
        chartATMediDur.getSeries(seriesIndex).setColor(color);
        chartATMeanDur.getSeries(seriesIndex).setColor(color);
        chartATDurRng.getSeries(seriesIndex).setColor(color);
        chartATTtlDur.getSeries(seriesIndex).setColor(color);

        chartRSFreq.getSeries(seriesIndex).setColor(color);
        chartRSMediDur.getSeries(seriesIndex).setColor(color);
        chartRSMeanDur.getSeries(seriesIndex).setColor(color);
        chartRSDurRng.getSeries(seriesIndex).setColor(color);
        chartRSTtlDur.getSeries(seriesIndex).setColor(color);
    }

    public void changeTheme(String themeString) {
        Theme theme;
        switch (themeString){
            case "GRID":
                theme = Theme.GRID;
                break;
            case "GRAY":
                theme = Theme.GRAY;
                break;
            case "DARK_BLUE":
                theme = Theme.DARK_BLUE;
                break;
            case "DARK_GREEN":
                theme = Theme.DARK_GREEN;
                break;
            case "DARK_UNICA":
                theme = Theme.DARK_UNICA;
                break;
            case "SAND_SIGNIKA":
                theme = Theme.SAND_SIGNIKA;
                break;
            case "GRID_LIGHT":
                theme = Theme.GRID_LIGHT;
                break;
            default:
                theme = Theme.DEFAULT;
        }
        chartOVEventOT.setTheme(theme);
        chartOVActiveCaseOT.setTheme(theme);
        chartOVCaseVariant.setTheme(theme);
        chartOVEventsPC.setTheme(theme);
        chartOVCaseDur.setTheme(theme);
        chartOVMeanActDur.setTheme(theme);
        chartATFreq.setTheme(theme);
        chartATMediDur.setTheme(theme);
        chartATMeanDur.setTheme(theme);
        chartATDurRng.setTheme(theme);
        chartATTtlDur.setTheme(theme);
        chartRSFreq.setTheme(theme);
        chartRSMediDur.setTheme(theme);
        chartRSMeanDur.setTheme(theme);
        chartRSDurRng.setTheme(theme);
        chartRSTtlDur.setTheme(theme);
    }

    private void filterLog(String logName) {
        logToBeFiltered = logName;
        CaseData cd = new CaseData(logName, eventLogsHM.get(logName));

        AbstractListModel<?> caseModel = cd.getLmaCases();
        caseModel.setMultiple(true);
        lbxFilterCase.setModel(caseModel);
        lbxFilterCase.setMultiple(true);

        ActivityData ad = new ActivityData(logName, eventLogsHM.get(logName));

        AbstractListModel<?> actModel = ad.lmaActivities;
        actModel.setMultiple(true);
        lbxFilterAct.setModel(actModel);
        lbxFilterAct.setMultiple(true);

        ResourceData rd = new ResourceData(logName, eventLogsHM.get(logName));

        AbstractListModel<?> resModel = rd.lmaResources;
        resModel.setMultiple(true);
        lbxFilterRes.setModel(resModel);
        lbxFilterRes.setMultiple(true);

        west01.setVisible(true);
        west01.setTitle("Filter " + logName);

    }
    private void closeFilterLog() {
        west01.setVisible(false);
    }


    public void foo(String s) {
        System.out.println("Foo! " +  s + "; northOV height: " +
                northOV.getHeight());

    }

    public void sizeNorthOV() {
        String northOVHeightString = northOV.getHeight();
        int pxIndex = northOVHeightString.indexOf("px");
        String pxValue = northOVHeightString.substring(0, pxIndex);
        System.out.println("pxValue = " + pxValue);
        int pxInt = new Integer(pxValue);
        chartOVEventOT.setHeight(pxInt - WINDOW_BORDER_HEIGHT - 35);
        chartOVActiveCaseOT.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartOVCaseVariant.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartOVEventsPC.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartOVCaseDur.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartOVMeanActDur.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
    }

    public void sizeNorthAT() {
        String northHeightString = northAT.getHeight();
        int pxIndex = northHeightString.indexOf("px");
        String pxValue = northHeightString.substring(0, pxIndex);
        System.out.println("pxValue = " + pxValue);
        int pxInt = new Integer(pxValue);
        int wbHeight = 60;
        chartATFreq.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartATMediDur.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartATMeanDur.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartATDurRng.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartATTtlDur.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
    }

    public void sizeNorthRS() {
        String northHeightString = northRS.getHeight();
        int pxIndex = northHeightString.indexOf("px");
        String pxValue = northHeightString.substring(0, pxIndex);
        System.out.println("pxValue = " + pxValue);
        int pxInt = new Integer(pxValue);
        chartRSFreq.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartRSMediDur.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartRSMeanDur.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartRSDurRng.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
        chartRSTtlDur.setHeight(pxInt- WINDOW_BORDER_HEIGHT - 35);
    }


    public void showCaseDetails(String selectedCaseId) {

        northOV.setOpen(false);
        southOV.setVisible(true);
        lblOVCaseDetails.setValue(selectedCaseId);

        List<EventData> eventDataList = new ArrayList<EventData>();

        XLog xLog = (XLog) eventLogsHM.get(selectedFileName).clone();
        for(int i=0; i<xLog.size(); i++) {
            XTrace xTrace = xLog.get(i);
            String caseID = "";
            if(xTrace.getAttributes().containsKey("concept:name")) {
                caseID = xTrace.getAttributes().get("concept:name").toString();
            }
            if(caseID.equals(selectedCaseId)) {
                for(int j=0; j<xTrace.size(); j++) {
                    XEvent xEvent = xTrace.get(j);
                    String eventId = "n/a";
                    String resId = "n/a";
                    String eventType = "activity"; //by default
                    String status = "n/a";

                    if(xEvent.getAttributes().containsKey("concept:name")) {
                        eventId =
                                xEvent.getAttributes().get(
                                        "concept:name").toString();
                    }
                    if(xEvent.getAttributes().containsKey("org:resource")) {
                        resId =
                                xEvent.getAttributes().get(
                                        "org:resource").toString();
                    }
                    if(xEvent.getAttributes().containsKey("lifecycle:transition")) {
                        status =
                                xEvent.getAttributes().get(
                                        "lifecycle:transition").toString();
                    }
                    ZonedDateTime timestamp = Util.zonedDateTimeOf(xEvent);
                    ZonedDateTime startTime = null;
                    ZonedDateTime endTime = null;

                    if(status.equals("start")) {
                        System.out.println("Activity: " + eventId +
                                "; Lifecycle: start");
                        startTime = timestamp;
                        for(int k=j; k<xTrace.size(); k++) {
                            XEvent kEvent = xTrace.get(k);
                            if(kEvent.getAttributes().containsKey("concept:name")
                            && kEvent.getAttributes().containsKey(
                                    "lifecycle:transition")) {
                                String kID = kEvent.getAttributes().get(
                                        "concept:name").toString();
                                String kLife = kEvent.getAttributes().get(
                                        "lifecycle:transition").toString();
                                if(kID.equals(eventId) &&
                                        kLife.equals("complete")) {
                                    endTime = Util.zonedDateTimeOf(kEvent);
                                    xTrace.remove(kEvent);
                                    break;
                                }
                            }
                        }
                    }

                    if(!status.equals("schedule")) {
                        EventData ed = null;

                        if(startTime != null && endTime != null) {
                            ed = new EventData(caseID, eventId,
                                    timestamp, "", eventId, resId,
                                    startTime, endTime, status);
                        }else{
                            ed = new EventData(caseID, eventId,
                                    timestamp, "", eventId, resId, status);
                        }
                        eventDataList.add(ed);
                    }
                }
                break;
            }
        }

        ListModelArray lmaCaseEvents = new ListModelArray(
                eventDataList.toArray(new EventData[eventDataList.size()]));

        lbxOVCaseDetails.setModel(lmaCaseEvents);
        lhOVCaseDetail.sort(true);
    }

    public void closeOVSouth() {
        southOV.setVisible(false);
        northOV.setOpen(true);
    }

    //************************************************
    // UI component listening events
    //************************************************

    @Listen("onClick = #btFilter")
    public void btFilterClicked() {
        String viewSelection =
                rdgView.getSelectedItem().getLabel().toLowerCase();
        if(viewSelection.equals("case")) {
            rdgView.setSelectedIndex(0);

            HashMap<String, Integer> selected = new HashMap<String, Integer>();

            Set<Listitem> items = lbxFilterCase.getSelectedItems();
            if(items.size() == 0) {
                System.out.println("none");
            }else{
                for(Listitem li : items) {
                    selected.put(li.getLabel(), 0);
                    System.out.println(li.getLabel());
                }
            }

            XLog xLog = (XLog) eventLogsHM.get(logToBeFiltered).clone();
            System.out.println("Original size: " + xLog.size());

            List<XTrace> tobeRemoved = new ArrayList<XTrace>();

            for(int i=0; i<xLog.size();i++) {
                XTrace xTrace = xLog.get(i);
                String traceName =
                        xTrace.getAttributes().get("concept:name").toString();
                if(!selected.containsKey(traceName)) {
                    tobeRemoved.add(xTrace);
                }
            }

            xLog.removeAll(tobeRemoved);

            String newName = logToBeFiltered + "-filtered";
            eventLogsHM.put(newName, xLog);

            selectedFileName = newName;

            west01.setVisible(false);

            List<String> fNames = new ArrayList<String>();
            for(String key : eventLogsHM.keySet()) {
                fNames.add(key);
            }
            setMenuItems(fNames);
            resetEventLogs();
        }
        if(viewSelection.equals("activity")) {
            rdgView.setSelectedIndex(1);
            Set<Listitem> items = lbxFilterAct.getSelectedItems();

            HashMap<String, Integer> selected = new HashMap<String, Integer>();

            if(items.size() == 0) {
                System.out.println("none");
            }else{
                for(Listitem li : items) {
                    System.out.println(li.getLabel());
                    selected.put(li.getLabel(), 0);
                }
            }

            XLog xLog = (XLog) eventLogsHM.get(logToBeFiltered).clone();

            List<XTrace> tobeRemoved = new ArrayList<XTrace>();

            for(int i=0; i < xLog.size(); i++) {
                XTrace xTrace = xLog.get(i);
                List<XEvent> tobeRemovedEvents = new ArrayList<XEvent>();
                boolean hasAct = false;
                for(int j=0; j<xTrace.size(); j++) {
                    XEvent xEvent = xTrace.get(j);
                    String jName = xEvent.getAttributes().get("concept:name").toString();
                    if(selected.containsKey(jName)) {
                        hasAct = true;
                    }else{
                        tobeRemovedEvents.add(xEvent);
                    }
                }
                xTrace.removeAll(tobeRemovedEvents);
                if(!hasAct) {
                    tobeRemoved.add(xTrace);
                }
            }

            xLog.removeAll(tobeRemoved);

            String newName = logToBeFiltered + "-filtered";
            eventLogsHM.put(newName, xLog);

            selectedFileName = newName;

            west01.setVisible(false);

            List<String> fNames = new ArrayList<String>();
            for(String key : eventLogsHM.keySet()) {
                fNames.add(key);
            }
            setMenuItems(fNames);
            resetEventLogs();
        }
        if(viewSelection.equals("resource")) {
            rdgView.setSelectedIndex(2);

            HashMap<String, Integer> selected = new HashMap<String, Integer>();

            Set<Listitem> items = lbxFilterRes.getSelectedItems();

            if(items.size() == 0) {
                System.out.println("none");
            }else{
                for(Listitem li : items) {
                    System.out.println(li.getLabel());
                    selected.put(li.getLabel(), 0);
                }
            }

            XLog xLog = (XLog) eventLogsHM.get(logToBeFiltered).clone();

            List<XTrace> tobeRemoved1 = new ArrayList<XTrace>();

            for(int i=0; i<xLog.size();i++) {
                XTrace xTrace = xLog.get(i);
                List<XEvent> tobeRemovedEvents = new ArrayList<XEvent>();
                for(int j=0; j<xTrace.size(); j++) {
                    XEvent xEvent = xTrace.get(j);
                    boolean keep = true;
                    if(xEvent.getAttributes().containsKey("org:resource")){
                        String res =
                                xEvent.getAttributes().get(
                                        "org:resource").toString();
                        if(!selected.containsKey(res)) {
                            keep = false;
                        }
                    }
                    if(!keep) {
                        tobeRemovedEvents.add(xEvent);
                    }
                }
                xTrace.removeAll(tobeRemovedEvents);
                if(xTrace.size() < 1) {
                    tobeRemoved1.add(xTrace);
                }
            }

            xLog.removeAll(tobeRemoved1);

            String newName = logToBeFiltered + "-filtered";
            eventLogsHM.put(newName, xLog);

            selectedFileName = newName;

            west01.setVisible(false);

            List<String> fNames = new ArrayList<String>();
            for(String key : eventLogsHM.keySet()) {
                fNames.add(key);
            }
            setMenuItems(fNames);
            resetEventLogs();
        }
    }

    @Listen("onCheck = #rdgView")
    public void radioBtChecked() {
        Radio radio = rdgView.getSelectedItem();
        String checkedView = radio.getLabel().toLowerCase();
        System.out.println("you checked " + checkedView);
        if(checkedView.equals("case")) {
            lbxFilterCase.setVisible(true);
            lbxFilterAct.setVisible(false);
            lbxFilterRes.setVisible(false);
        }
        if(checkedView.equals("activity")) {
            lbxFilterCase.setVisible(false);
            lbxFilterAct.setVisible(true);
            lbxFilterRes.setVisible(false);
        }
        if(checkedView.equals("resource")) {
            lbxFilterCase.setVisible(false);
            lbxFilterAct.setVisible(false);
            lbxFilterRes.setVisible(true);
        }
    }

    @Listen("onClick = #btCloseFilter")
    public void btCloseFilterClick() {
        closeFilterLog();
    }

//    @Listen("onClick = #btCloseOVSouth")
//    public void btCloseOVSouthClick() {
//        southOV.setVisible(false);
//        northOV.setOpen(true);
//    }

    @Listen("onUpload = #miOpen, #btUp")
    public void updata(UploadEvent e) throws Exception {
        System.out.println("upload file clicked.");
        reset();
        if (e.getMedias() != null)
        {
            List<String> fileNames = new ArrayList<String>();
            StringBuilder sb = new StringBuilder("You uploaded: \n");
            for (Media m : e.getMedias())
            {
                sb.append(m.getName());
                sb.append(" (");
                sb.append(m.getContentType());
                sb.append(")\n");
                String filename = m.getName();
                String fullFilePath = FILE_STORE_PATH + filename;
                fileNames.add(filename);
                String extension =
                        filename.substring(filename.lastIndexOf("."));
                File testFile = new File(fullFilePath);
                try {
                    System.out.println("File uploading in progress......");
                    byte[] rawData = m.getByteData();
                    FileOutputStream fos =
                            new FileOutputStream(fullFilePath);
                    fos.write(rawData);
                    fos.close();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    Messagebox.show(ex.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Messagebox.show(ex.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("File uploaded......");
            setEventLogs(fileNames);
        }
        else
        {
            Messagebox.show("You uploaded no files!");
        }
    }


    //******************************************************
    // Get components
    //******************************************************

    public ListModelList getChartThemeModel() {
        return chartThemeModel;
    }

}

