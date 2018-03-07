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

package org.apromore.plugin.portal.stagemining;

import java.io.FileInputStream;
import java.io.IOException;
import org.apromore.plugin.portal.PortalContext;
import org.zkoss.zul.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;
import org.json.JSONException;
import org.processmining.stagemining.models.DecompositionTree;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;
import org.zkoss.zul.ext.Selectable;


public class StageMiningShowResult { //extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private Window resultW;
    private List<String> selectedEndEvents = new ArrayList<String>();

    /**
     * @throws IOException if the <code>prodriftshowresult.zul</code> template can't be read from the classpath
     */
    public StageMiningShowResult(final PortalContext portalContext, final DecompositionTree tree, final XLog currentLog) throws IOException, JSONException, Exception {
        this.portalContext = portalContext;
        this.resultW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/result.zul", null, null);
        Button createLogButton = (Button)resultW.getFellow("stageminingresult_createLogButton");
        
        createLogButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                final Window createLogW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createlog.zul", null, null);
                Button createLogOKButton = (Button)createLogW.getFellow("createLogOKButton");
                Button createLogCancelButton = (Button)createLogW.getFellow("createLogCancelButton");

                final Listbox createLogEndEventListbox = (Listbox)createLogW.getFellow("createLogEndEventListBox");
                List<String> endingEventList = getEndingEvents(currentLog);
                ListModelList<String> statusListModel = new ListModelList<String>(endingEventList);
                createLogEndEventListbox.setModel(statusListModel);
                ((Selectable)createLogEndEventListbox.getModel()).setMultiple(true);
                createLogEndEventListbox.setCheckmark(true);
                
                createLogEndEventListbox.addEventListener("onSelect", new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        Set selection = ((Selectable)createLogEndEventListbox.getModel()).getSelection();
                        selectedEndEvents = new ArrayList<String>(selection);
                    }
                });
                
                createLogOKButton.addEventListener("onClick", new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        XLog newLog = createStagedLog(currentLog, tree, selectedEndEvents);
                        File file = File.createTempFile("apromore", "xes");
                        XesXmlSerializer writer = new XesXmlSerializer();
                        FileOutputStream fout = new FileOutputStream(file);
                        writer.serialize(newLog, fout);
                        fout.close();
                        
                        FileInputStream fin = new FileInputStream(file);
                        Filedownload.save(fin,"application/text","staged_log.xes");
                    }
                });
                
                createLogCancelButton.addEventListener("onClick", new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        createLogW.detach();
                        selectedEndEvents.clear();
                    }
                });           
                
                createLogW.doModal();
            }
        });
        
  
        

        
        String jsonString = Visualization_cytoscape.createJson(tree).toString();
        String javascript = "load('" + jsonString + "');";
        System.out.println(javascript);
        Clients.evalJavaScript(javascript);
        this.resultW.setTitle("Stage Mining Result");
        this.resultW.doOverlapped();
    }

    public void showError(String error) {
        portalContext.getMessageHandler().displayError(error, null);
    }

    protected void close() {
        this.resultW.detach();
    }
    
    private java.util.List<String> getEndingEvents(XLog log) {
        List<String> endEvents = new ArrayList<String>();
        for (XTrace trace : log) {
            int counter = 0;
            for (XEvent e : trace) {
                counter++;
                String eventName = XConceptExtension.instance().extractName(e).toLowerCase();
                if (counter == (trace.size()-1)) { // last event, (the last event is "end")
                    if (!endEvents.contains(eventName)) {
                        endEvents.add(eventName);
                    }
                }
            }
        }
        Collections.sort(endEvents, String.CASE_INSENSITIVE_ORDER);
        return endEvents;
    }
    
    private XLog createStagedLog(XLog log, DecompositionTree tree, List<String> exitEventNames) throws Exception {
        final String EVENT_STAGE_ATTRIBUTE = "stage";
        final String TRACE_STATUS_ATTRIBUTE = "status";
        final String TRACE_EXIT_STATUS = "exit";
        final String TRACE_COMPLETE_STATUS = "complete";
        final String STAGE_NAME_PREFIX = "Stage";
        
        XFactoryNaiveImpl factory = new XFactoryNaiveImpl();
        XLog newLog = factory.createLog(log.getAttributes());
        
        List<Set<String>> activitySets = tree.getActivityLabelSets(tree.getMaxLevelIndex());
        for (XTrace trace : log) {
            XTrace newTrace = factory.createTrace(trace.getAttributes());
            int counter = 0;
            int currentStageIndex = 0;
            for (XEvent e : trace) {
                counter++;
                if (counter == 1 || counter == trace.size()) continue; // start/end event is the first/last event
                String eventName = XConceptExtension.instance().extractName(e).toLowerCase();
                int eventStageIndex = this.getStageIndex(eventName, activitySets);
                if (eventStageIndex == 0) {
                    throw new Exception("Cannot find stage for event with name = " + eventName);
                }
                else {
                    // Only take events in the increasing order of stages
                    if (eventStageIndex == currentStageIndex || eventStageIndex == (currentStageIndex+1)) {
                        e.getAttributes().put(EVENT_STAGE_ATTRIBUTE, factory.createAttributeLiteral(EVENT_STAGE_ATTRIBUTE,STAGE_NAME_PREFIX + eventStageIndex,null));
                        if (counter == (trace.size()-1)) { // last event, (the last event is "end")
                            if (exitEventNames.contains(eventName)) {
                                newTrace.getAttributes().put(TRACE_STATUS_ATTRIBUTE, factory.createAttributeLiteral(TRACE_STATUS_ATTRIBUTE,TRACE_EXIT_STATUS,null));
                            }
                            else {
                                newTrace.getAttributes().put(TRACE_STATUS_ATTRIBUTE, factory.createAttributeLiteral(TRACE_STATUS_ATTRIBUTE,TRACE_COMPLETE_STATUS,null));
                            }
                        }
                        currentStageIndex = eventStageIndex;
                        newTrace.add(e);
                    }
                }
            }
            newLog.add(newTrace);
        }
        
        return newLog;
    }
    
    private int getStageIndex(String activityName, List<Set<String>> activitySets) {
        for (int i=0;i<activitySets.size();i++) {
            if (activitySets.get(i).contains(activityName)) {
                return i+1;
            }
        }
        return 0;
    }    

}

