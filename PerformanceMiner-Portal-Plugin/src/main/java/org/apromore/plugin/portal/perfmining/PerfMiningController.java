/*
 * Copyright Â© 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.perfmining;

import org.apromore.plugin.portal.PortalContext;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apromore.plugin.portal.perfmining.util.LogUtilites;
import org.apromore.plugin.portal.perfmining.view.ResultWindowController;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.apromore.service.perfmining.models.SPF;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.apromore.service.perfmining.PerfMiningService;
import org.apromore.service.perfmining.filter.TraceAttributeFilterParameters;
import org.apromore.service.perfmining.models.SPFManager;
import org.apromore.service.perfmining.parameters.SPFConfig;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ext.Selectable;


public class PerfMiningController {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private final PerfMiningService perfMiningService;
    
    private Window licenseW;
    private Button licenseOKbutton;
    private Button licenseCancelButton;
    
    private Window importW;
    private Button logFileUpload;
    private org.zkoss.util.media.Media logFile = null;
    private Button importNextbutton;
    private Button importCancelButton;
    
    private Window configW;
    private Button configPreviousButton;
    private Button configOKbutton;
    private Button configCancelButton;
    private Combobox configTimeZoneCombo;
    private Listbox configExitStatusListbox;
    private Checkbox hasStartEndEventCheckbox;
    
    private byte[] logByteArray = null;
    private String logFileName = null;
    private XLog log = null;
    
    private SPFConfig config = new SPFConfig();

    /**
     * @throws IOException if the <code>perfmining.zul</code> template can't be read from the classpath
     */
    public PerfMiningController(PortalContext portalContext, PerfMiningService perfMiningService) throws IOException {
        this.portalContext = portalContext;
        this.perfMiningService = perfMiningService;
        
        //-----------------------------------------------------------------
        // INITIALIZE COMPONENTS
        //-----------------------------------------------------------------
        this.licenseW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/license.zul", null, null);
        licenseOKbutton = (Button) this.licenseW.getFellow("OKButton");
        licenseCancelButton = (Button) this.licenseW.getFellow("CancelButton");
        
        this.importW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/import.zul", null, null);
        this.importW.setTitle("Import Log File");
        importNextbutton = (Button) this.importW.getFellow("NextButton");
        importNextbutton.setDisabled(true);
        importCancelButton = (Button) this.importW.getFellow("CancelButton");
        
        this.configW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/configuration.zul", null, null);
        this.configW.setTitle("Performance Mining Parameters");
        configPreviousButton = (Button) this.configW.getFellow("PreviousButton");
        configOKbutton = (Button) this.configW.getFellow("OKButton");
        configCancelButton = (Button) this.configW.getFellow("CancelButton");
        configTimeZoneCombo = (Combobox) this.configW.getFellow("TimeZoneCombo");
        configExitStatusListbox = (Listbox) this.configW.getFellow("ExitStatusListBox");
        hasStartEndEventCheckbox = (Checkbox) this.configW.getFellow("hasStartEndEvents");

        this.logFileUpload = (Button) this.importW.getFellow("logFileUpload");
        final Label l = (Label) this.importW.getFellow("fileName");
        
        this.licenseW.doModal();
        
        //-----------------------------------------------------------------
        // EVENT LISTENERS
        //-----------------------------------------------------------------

       licenseOKbutton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                licenseW.detach();
                importW.doModal();
            }
        });      
       
        licenseCancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                licenseW.detach();
                importW.detach();
                configW.detach();
            }
        });
        
        this.logFileUpload.addEventListener("onUpload", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                UploadEvent uEvent = (UploadEvent) event;
                logFile = uEvent.getMedia();
                if (logFile == null) {
                    showError("Upload error. No file uploaded.");
                    return;
                }

                //logByteArray = logFile.getByteData();
                logFileName = logFile.getName();
                OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
                try {
                    System.out.println("Import log file");
                    //log = (XLog)logImporter.importFromStream(logFile.getStreamData(), logFileName, logByteArray.length);
                    log = (XLog)logImporter.importFromStream(logFile.getStreamData(), logFileName);
                }
                catch (Exception e) {
                    showError(e.getMessage());
                }

//                Session sess = Sessions.getCurrent();
//                sess.setAttribute("log", log);
                importNextbutton.setDisabled(false);

            }
        });

        importNextbutton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                SPFManager.getInstance().clear(); //start over for a new SPF
                readData(log, config, SPFManager.getInstance());
                config.setCheckStartCompleteEvents(true);
                initializeTimeZoneBox();
                initializeCaseStatusList();

                importW.setVisible(false);
                configW.doModal();
            }
        });
        
        importCancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                importW.detach();
                configW.detach();
            }
        });
        
        configPreviousButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                configW.setVisible(false);
                importW.setVisible(true);
            }
        });
        
        
        configOKbutton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                TraceAttributeFilterParameters filter = new TraceAttributeFilterParameters();
                filter.setName("Full SPF");
                minePerformance(log, config, filter);
                importW.detach();
                configW.detach();
            }
        });
        
        configCancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                configW.detach();
                importW.detach();
            }
        });
        
        configTimeZoneCombo.addEventListener("onSelect", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                config.setTimeZone(TimeZone.getTimeZone(configTimeZoneCombo.getSelectedItem().getValue().toString()));
            }
        });     
        
        configExitStatusListbox.addEventListener("onSelect", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                java.util.Set selection = ((Selectable)configExitStatusListbox.getModel()).getSelection();
                config.setExitTypeList(new ArrayList<String>(selection));
            }
        });     
        
        hasStartEndEventCheckbox.addEventListener("onCheck", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                config.setCheckStartCompleteEvents(hasStartEndEventCheckbox.isChecked());
            }
        });  
    }
    
    public void showError(String error) {
        portalContext.getMessageHandler().displayInfo(error);
        Label errorLabel = (Label) this.importW.getFellow("errorLabel");
        errorLabel.setValue(error);
    }
    
    private void initializeTimeZoneBox() {
        TimeZone defaultTZ = TimeZone.getTimeZone("Europe/Amsterdam");
        String[] ids = TimeZone.getAvailableIDs();
        Comboitem defaultItem = null;
        for (int i = 0; i < ids.length; i++) {
                TimeZone timeZone = TimeZone.getTimeZone(ids[i]);
                Comboitem item = configTimeZoneCombo.appendItem(getTimeZoneString(timeZone));
                String test = timeZone.getID();
                if (timeZone.getID().equals(defaultTZ.getID())) {
                    defaultItem = item;
                }
        }
        configTimeZoneCombo.setSelectedItem(defaultItem);
        config.setTimeZone(defaultTZ);
    }
    
    /**
     * Only call method when SPFConfig has been loaded with data from the log.
     */
    private void initializeCaseStatusList() {
        java.util.List<String> caseStatusList = config.getCaseStatusList();
        ListModelList<String> statusListModel = new ListModelList<String>(caseStatusList);
        configExitStatusListbox.setModel(statusListModel);
        ((Selectable)configExitStatusListbox.getModel()).setMultiple(true);
        configExitStatusListbox.setCheckmark(true);
    }

    protected void minePerformance(XLog log, SPFConfig config, TraceAttributeFilterParameters filter) {
        try {
            SPF result = perfMiningService.mine(log, config, filter);
            showResults(result);

        } catch (Exception e) {
            String message = "PerfMining failed (" + e.getMessage() + ")";
            showError(message);
        }
    }
    
    protected void showConfig(XLog log) {
        
    }
    
    protected void showResults(SPF result) {
        try {
//            new PerfMiningShowResult(portalContext, result, importW);
            new ResultWindowController(portalContext, result);

        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

        /**
     * This method reads the log file and populate data to the Config and
     * BPFManager object
     * 
     * @param log
     * @param config
     * @param bpfManager
     */
    private static void readData(XLog log, SPFConfig config, SPFManager bpfManager) {
            new HashMap<String, Map<String, String>>();
            config.setXLog(log);

            for (XTrace trace : log) {
                    DateTime traceStart = new DateTime(9999, 12, 1, 1, 0);
                    DateTime traceEnd = new DateTime(0);

                    //---------------------------------------
                    // Populate the Config object
                    //---------------------------------------
                    for (XEvent event : trace) {
                            String eventName = LogUtilites.getConceptName(event).toLowerCase();
                            String stage = LogUtilites.getValue(event.getAttributes().get("stage")).toLowerCase();
                            //					String type = LogUtilites.getValue(event.getAttributes().get("type")).toLowerCase();
                            String transitiontype = LogUtilites.getLifecycleTransition(event).toLowerCase();
                            DateTime eventTime = LogUtilites.getTimestamp(event);

                            if (!config.getStageList().contains(stage)) {
                                    config.getStageList().add(stage);
                            }

                            if (!config.getEventStageMap().containsKey(eventName)) {
                                    config.getEventStageMap().put(eventName, stage);
                            }

                            //					if (type.equals("activity")) {
                            //						config.getActivityList().add(eventName);
                            //					}
                            //					else if (type.equals("gate")) {
                            //						config.getGateList().add(eventName);
                            //					}

                            if (traceStart.isAfter(eventTime)
                                            && (transitiontype.equals("start") || transitiontype.equals("complete"))) {
                                    traceStart = eventTime;
                            }

                            if (traceEnd.isBefore(eventTime)
                                            && (transitiontype.equals("start") || transitiontype.equals("complete"))) {
                                    traceEnd = eventTime;
                            }
                    }

                    String traceStatus = LogUtilites.getValue(trace.getAttributes().get("status")).toLowerCase();
                    if (!config.getCaseStatusList().contains(traceStatus)) {
                            config.getCaseStatusList().add(traceStatus);
                    }

                    //---------------------------------------
                    // Populate the BPFManager object
                    //---------------------------------------
                    String curTraceID = LogUtilites.getConceptName(trace);
                    Map<String, String> casePropertyMap = new HashMap<String, String>();
                    Iterator<XAttribute> attIterator = trace.getAttributes().values().iterator();

                    while (attIterator.hasNext()) {
                            XAttribute att = attIterator.next();
                            if (!att.getKey().equals("concept:name")) {
                                    casePropertyMap.put(att.getKey(), LogUtilites.getValue(att));
                            }
                    }

                    casePropertyMap.put(SPF.CASE_START_TIME, String.valueOf(traceStart.getMillis()));
                    casePropertyMap.put(SPF.CASE_END_TIME, String.valueOf(traceEnd.getMillis()));
                    casePropertyMap.put(SPF.CASE_EVENT_ACOUNT, String.valueOf(trace.size()));

                    bpfManager.getCaseAttributeMap().put(curTraceID, casePropertyMap);
            }
    }
    
    /**
     * Based on https://www.mkyong.com/java/java-display-list-of-timezone-with-gmt/
    */
    private String getTimeZoneString(TimeZone tz) {

            long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                              - TimeUnit.HOURS.toMinutes(hours);
            // avoid -4:-30 issue
            minutes = Math.abs(minutes);

            String result = "";
            if (hours > 0) {
                    result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
            } else {
                    result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
            }

            return result;

    }
}
