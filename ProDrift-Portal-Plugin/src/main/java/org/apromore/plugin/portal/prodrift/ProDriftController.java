/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.prodrift;

import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_EventStream;
import org.apromore.prodrift.model.ProDriftDetectionResult;
import org.apromore.prodrift.util.LogStreamer;
import org.apromore.prodrift.util.XLogManager;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.prodrift.ProDriftDetectionService;
import org.deckfour.xes.model.XLog;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class ProDriftController {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private final ProDriftDetectionService proDriftDetectionService;
    private Window proDriftW;

    private Button logFileUpload;
    private Listbox driftDetMechLBox;
    private Listbox logTypeLBox;
    private Checkbox gradDriftCBox;
    private Intbox winSizeVal;
    private Listbox fWinOrAwinLBox;
    private Doublespinner noiseFilterSpinner;
    private Listbox conflictLBox;
    private Button OKbutton;

    //    private byte[] logByteArray = null;
    private XLog xlog = null;
    XLog eventStream = null;
    private String logFileName = null;

    private boolean running = false;

    int caseCount = 0;
    int eventCount = 0;
    int activityCount = 0;

    int defaultWinSizeRuns = 100;
    int defaultWinSizeEvents = -1;
    int winSizeDividedBy = 10;



    /**
     * @throws IOException if the <code>prodrift.zul</code> template can't be read from the classpath
     */
    public ProDriftController(PortalContext portalContext, ProDriftDetectionService proDriftDetectionService, Map<XLog, String> logs) throws IOException {
        this.portalContext = portalContext;
        this.proDriftDetectionService = proDriftDetectionService;

        this.proDriftW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/prodrift.zul", null, null);
        this.proDriftW.setTitle("ProDrift: Set Parameters.");

        Intbox maxWinValueRunsIntBox = (Intbox) proDriftW.getFellow("maxWinValueRuns");
        maxWinValueRunsIntBox.setValue(defaultWinSizeRuns);

        Intbox maxWinValueEventsSynIntBox = (Intbox) proDriftW.getFellow("maxWinValueEvents");
        maxWinValueEventsSynIntBox.setValue(defaultWinSizeEvents);

        Intbox winSizeCoefficientIntBoX = (Intbox) proDriftW.getFellow("winSizeCoefficient");
        winSizeCoefficientIntBoX.setValue(ControlFlowDriftDetector_EventStream.winSizeCoefficient);

        this.logFileUpload = (Button) this.proDriftW.getFellow("logFileUpload");

        showError("");
        if(logs.size() > 0)
        {

//            Row logUploadRow = (Row) this.proDriftW.getFellow("logF");
//            logUploadRow.setVisible(false);


            if(logs.size() > 1)
            {

                showError("Please select only one log!");

            }else
            {

                this.logFileUpload.setVisible(false);

                Map.Entry<XLog, String> xl_entry = logs.entrySet().iterator().next();
                String xl_name = xl_entry.getValue() + ".xes.gz";
                XLog xl = xl_entry.getKey();

                initializeLogVars(xl, null, xl_name);

            }

        }

        this.logFileUpload.addEventListener("onUpload", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                UploadEvent uEvent = (UploadEvent) event;
                org.zkoss.util.media.Media logFileMedia = uEvent.getMedia();

                initializeLogVars(null, logFileMedia.getStreamData(), logFileMedia.getName());

            }
        });


//        this.winSize = (Row) this.proDriftW.getFellow("winsize");
//        Row fWinOrAwinChoiceR = (Row) this.proDriftW.getFellow("fWinOrAwinChoice");
//        this.fWinOrAwin = (Listbox) fWinOrAwinChoiceR.getFirstChild().getNextSibling();
//        Listitem listItem = new Listitem();
//        listItem.setLabel("Adaptive Window");
//        this.fWinOrAwin.appendChild(listItem);
//        listItem.setSelected(true);
//        listItem = new Listitem();
//        listItem.setLabel("Fixed Window");
//        this.fWinOrAwin.appendChild(listItem);

        this.OKbutton = (Button) this.proDriftW.getFellow("proDriftOKButton");
        Button cancelButton = (Button) this.proDriftW.getFellow("proDriftCancelButton");

        this.OKbutton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                proDriftDetector();
            }
        });
        this.OKbutton.addEventListener("onOK", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                proDriftDetector();
            }
        });
        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                cancel();
            }
        });


//        Popup popup = (Popup) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/prodriftcharacterizationpopup.zul", null, null);
//
//        ListModel<String> cs = new ListModelList<String>();
//
//        ((ListModelList<String>) cs).add("gggggggggg gggggggggggg wwwwww wwwwww wwww wwwwww wwww");
//
//        ((ListModelList<String>) cs).add("gggggggggg gggggggggggg wwwwww wwwwww wwww wwwwww wwww");
////
////        Grid grid = (Grid) popup.getFellow("myGrid");
////        grid.setModel(cs);
////
//        Listbox grid = (Listbox) popup.getFellow("myBox");
//        grid.setModel(cs);
//
//        cancelButton.setPopup(popup);



        this.proDriftW.doModal();
    }

    private void initializeLogVars(XLog xl, InputStream is, String logName) {

        final Label l = (Label) this.proDriftW.getFellow("fileName");
//        boolean valild = false;
        xlog = xl;

        int winSize_timeBased = 0;

        if(is != null)
            xlog = XLogManager.validateLog(is, logName);

        if (xlog == null) {

            l.setStyle("color: red");
            l.setValue("Unacceptable Log Format.");

//                    showError("Please select a log file(.xml, .mxml, .xes, .mxml.gz, .xes.gz)");

        } else {

            try {
                StringBuilder activityCountStr = new StringBuilder();
                StringBuilder winSizeStr = new StringBuilder();

                this.eventStream = LogStreamer.logStreamer(xlog, activityCountStr, winSizeStr, logName);

                caseCount = xlog.size();
                eventCount = eventStream.size();
                activityCount = Integer.parseInt(activityCountStr.toString());

                winSize_timeBased = Integer.parseInt(winSizeStr.toString());

            }catch (NumberFormatException ex) {}

            showError("");
            l.setStyle("color: blue");
            l.setValue(logName + " (Cases=" + caseCount + ", Activities=" + activityCount + ", Events~" + eventCount + ")");

//            if(xl != null)
//                xlog = xl;

            logFileName = logName;

            setDefaultWinSizes(winSize_timeBased);

        }

    }

    private void setDefaultWinSizes(int winSize_timeBased) {

        Intbox maxWinValueRunsIntBoX = (Intbox) proDriftW.getFellow("maxWinValueRuns");
        Intbox maxWinValueEventsIntBoX = (Intbox) proDriftW.getFellow("maxWinValueEvents");

        Intbox activityCountIntBoX = (Intbox) proDriftW.getFellow("activityCount");
        activityCountIntBoX.setValue(activityCount);

        if (winSize_timeBased < 100)
            this.defaultWinSizeEvents = Math.max(winSize_timeBased, activityCount * activityCount * 5);
        else
            this.defaultWinSizeEvents = winSize_timeBased;

        if(defaultWinSizeRuns > caseCount / 2)
            defaultWinSizeRuns = caseCount / 2;

        if(defaultWinSizeEvents > eventCount / 2)
            defaultWinSizeEvents = eventCount / 2;

//        if (caseCount / winSizeDividedBy < defaultWinSizeRuns) {
//
//            maxWinValueRunsIntBoX.setValue(roundNum(caseCount / winSizeDividedBy));
//
//        } else {
//
            maxWinValueRunsIntBoX.setValue(defaultWinSizeRuns);
//
//        }

//        if ((eventCount / winSizeDividedBy) < desiredWinSizeEvents) {
//
//            maxWinValueEventsIntBoX.setValue(roundNum(eventCount / winSizeDividedBy));
//
//        } else {
//
            maxWinValueEventsIntBoX.setValue(defaultWinSizeEvents);
//        }


        Listbox driftDetMechLBox = (Listbox) proDriftW.getFellow("driftDetMechLBox");
        boolean isEventBased = driftDetMechLBox.getSelectedItem().getLabel().startsWith("E") ? true : false;

//        Listbox logTypeLBox = (Listbox) proDriftW.getFellow("logTypeLBox");
//        boolean isSynthetic = logTypeLBox.getSelectedItem().getLabel().startsWith("M") ? true : false;

//        boolean isSynthetic = true;
//        if(activityCount < activityLimit)
//            isSynthetic = true;
//        else
//            isSynthetic = false;


        Intbox winSizeIntBox = (Intbox) proDriftW.getFellow("winSizeIntBox");

        if (isEventBased)
        {
            /*if (isSynthetic) {
                ((Listitem)proDriftW.getFellow("synLog")).setSelected(true);

                ((Listitem)proDriftW.getFellow("ADWIN")).setSelected(true);
                *//*if((activityCount * activityCount * ControlFlowDriftDetector_EventStream.winSizeCoefficient)
                        > maxWinValueEventsSynIntBoX.getValue())
                    ((Listitem)proDriftW.getFellow("FWIN")).setSelected(true);
                else
                    ((Listitem)proDriftW.getFellow("ADWIN")).setSelected(true);*//*

                ((Doublespinner) proDriftW.getFellow("noiseFilterSpinner")).setValue(5.0);
                winSizeIntBox.setValue(maxWinValueEventsSynIntBoX.getValue());
            }else {*/
//                ((Listitem)proDriftW.getFellow("reLog")).setSelected(true);
            ((Listitem) proDriftW.getFellow("ADWIN")).setSelected(true);
            ((Doublespinner) proDriftW.getFellow("noiseFilterSpinner")).setValue(10.0);
            ((Doublespinner) proDriftW.getFellow("driftDetectionSensitivitySpinner")).setValue(0.70);
            winSizeIntBox.setValue(maxWinValueEventsIntBoX.getValue());
//            }
        }else
            winSizeIntBox.setValue(maxWinValueRunsIntBoX.getValue());

        }

    int roundNum(int a)
    {

        return (a/100)*100 > 0 ? (a/100)*100 : 1;

    }

    public void showError(String error) {
        portalContext.getMessageHandler().displayInfo(error);
        Label errorLabel = (Label) this.proDriftW.getFellow("errorLabel");
        errorLabel.setValue(error);
    }

    protected void cancel() throws IOException {
   //     boolean detach = !running;
    //    if(detach)
    //    {
            showError(""); this.proDriftW.detach();
    //    }

    }

    protected void proDriftDetector() {
        String message;
        if (xlog != null)
        {

            Intbox winSizeIntBox = (Intbox) proDriftW.getFellow("winSizeIntBox");
            int winSize = winSizeIntBox.getValue();

            Listbox driftDetMechLBox = (Listbox) proDriftW.getFellow("driftDetMechLBox");
            boolean isEventBased = driftDetMechLBox.getSelectedItem().getLabel().startsWith("E") ? true : false;
            Session sess = Sessions.getCurrent();
            sess.setAttribute("isEventBased", isEventBased);

            if((isEventBased && winSize <= eventCount / 2) || (!isEventBased && winSize <= caseCount / 2))
            {

                showError("");
                try {

                    Checkbox gradDriftCBox = (Checkbox) proDriftW.getFellow("gradDriftCBox");
                    boolean withGradual = gradDriftCBox.isChecked() ? true : false;

                    Listbox fWinOrAwinLBox = (Listbox) proDriftW.getFellow("fWinOrAwinLBox");
                    boolean isAdwin = fWinOrAwinLBox.getSelectedItem().getLabel().startsWith("A") ? true : false;

                    Doublespinner noiseFilterSpinner = (Doublespinner) proDriftW.getFellow("noiseFilterSpinner");
                    float noiseFilterPercentage = (float)noiseFilterSpinner.getValue().doubleValue();

                    Doublespinner driftDetectionSensitivitySpinner = (Doublespinner) proDriftW.getFellow("driftDetectionSensitivitySpinner");
                    float driftDetectionSensitivity = (float)driftDetectionSensitivitySpinner.getValue().doubleValue();

                    boolean withConflict = /*isSynthetic ? true : */false;

                    Checkbox withCharacterizationCBox = (Checkbox) proDriftW.getFellow("withCharacterizationCBox");
                    boolean withCharacterization = withCharacterizationCBox.isChecked() ? true : false;

                    Spinner cummulativeChangeSpinner = (Spinner) proDriftW.getFellow("cummulativeChangeSpinner");
                    int cummulativeChange = cummulativeChangeSpinner.getValue().intValue();

//                    Rengine engineR = null;
//                    Object obj = sess.getAttribute("engineR");
//                    if(obj == null) {
//                        engineR = new Rengine(new String[]{"--no-save"}, false, null);
//                        sess.setAttribute("engineR", engineR);
//                    }else
//                        engineR = (Rengine) obj;

                    running = true;

                    ProDriftDetectionResult result = proDriftDetectionService.proDriftDetector(xlog, eventStream, logFileName,
                            isEventBased, withGradual, winSize, activityCount, isAdwin, noiseFilterPercentage, driftDetectionSensitivity, withConflict,
                            withCharacterization, cummulativeChange/*, engineR*/);

                    proDriftShowResults_(result, isEventBased, xlog, logFileName, withCharacterization, cummulativeChange);
                    message = "Completed Successfully";
                    portalContext.getMessageHandler().displayInfo(message);

                } catch (Exception e) {
                    message = "ProDrift failed (" + e.getMessage() + ")";
                    showError(message);
//                e.printStackTrace();
                }

            }else
            {

                if(isEventBased)
                    showError("Window size cannot be bigger than " + eventCount / 2);
                else
                    showError("Window size cannot be bigger than " + caseCount / 2);

            }
//            this.proDriftW.detach();
        }else
        {
            showError("Please select a log file first.");
        }

        running = false;
    }


    protected void proDriftShowResults_(ProDriftDetectionResult result, boolean isEventBased, XLog xlog, String logFileName,
                                        boolean withCharacterization, int cummulativeChange) {
        try {

            new ProDriftShowResult(portalContext, result, isEventBased, xlog, logFileName, withCharacterization, cummulativeChange);

        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

}
