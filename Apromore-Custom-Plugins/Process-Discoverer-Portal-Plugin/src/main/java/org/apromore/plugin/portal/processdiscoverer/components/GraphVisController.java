/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.processdiscoverer.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnEdgeRemoveTrace;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnEdgeRetainTrace;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnElementFilter;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRemoveEvent;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRemoveTrace;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRetainEvent;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnNodeRetainTrace;
import org.apromore.plugin.portal.processdiscoverer.vis.InvalidOutputException;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.plugins.bpmn.BpmnDefinitions;
import org.apromore.service.loganimation.AnimationResult;
import org.apromore.service.loganimation.LogAnimationService2;
import org.apromore.service.loganimation.modelmapping.BpmnModelMapping;
import org.apromore.service.loganimation.recording.AnimationContext;
import org.apromore.service.loganimation.recording.AnimationIndex;
import org.apromore.service.loganimation.recording.FrameRecorder;
import org.apromore.service.loganimation.recording.ModelMapping;
import org.apromore.service.loganimation.recording.Movie;
import org.apromore.service.loganimation.recording.Stats;
import org.apromore.service.loganimation.replay.AnimationLog;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;

/**
 * Manage the graph visualization
 */
public class GraphVisController extends VisualController {

    private final String STARTEVENT_REL_PATTERN = "|> =>";
    private final String STARTEVENT_NEW_REL_PATTERN = "[Start] =>";

    private final String ENDEVENT_REL_PATTERN = "=> []";
    private final String ENDEVENT_NEW_REL_PATTERN = "=> [End]";

    private final String XOR_FROM_PATTERN = "X =>";
    private final String XOR_TO_PATTERN = "=> X";

    private final String OR_FROM_PATTERN = "O =>";
    private final String OR_TO_PATTERN = "=> O";

    private final String AND_FROM_PATTERN = "+ =>";
    private final String AND_TO_PATTERN = "=> +";
    
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(GraphVisController.class);

    private Component vizBridge;
    
    private AnimationContext animateContext;
    private Movie animationMovie;
    
    public GraphVisController(PDController controller) {
        super(controller);
    }

    @Override
    public void initializeControls(Object data) {
        if (this.parent == null) return;
        vizBridge = parent.getFellow("vizBridge");
    }
    
    @Override
    public void initializeEventListeners(Object data) {
        vizBridge.addEventListener("onNodeRemovedTrace", event -> {
            FilterActionOnElementFilter action = new FilterActionOnNodeRemoveTrace(parent, parent.getProcessAnalyst());
            action.setElement(event.getData().toString(), parent.getUserOptions().getMainAttributeKey());
            parent.getActionManager().executeAction(action);
        });
        
        vizBridge.addEventListener("onNodeRetainedTrace", event -> {
            FilterActionOnElementFilter action = new FilterActionOnNodeRetainTrace(parent, parent.getProcessAnalyst());
            action.setElement(event.getData().toString(), parent.getUserOptions().getMainAttributeKey());
            parent.getActionManager().executeAction(action);
        });
        
        vizBridge.addEventListener("onNodeRemovedEvent", event -> {
            FilterActionOnElementFilter action = new FilterActionOnNodeRemoveEvent(parent, parent.getProcessAnalyst());
            action.setElement(event.getData().toString(), parent.getUserOptions().getMainAttributeKey());
            parent.getActionManager().executeAction(action);
        });
        
        vizBridge.addEventListener("onNodeRetainedEvent", event -> {
            FilterActionOnElementFilter action = new FilterActionOnNodeRetainEvent(parent, parent.getProcessAnalyst());
            action.setElement(event.getData().toString(), parent.getUserOptions().getMainAttributeKey());
            parent.getActionManager().executeAction(action);
        });
        
        vizBridge.addEventListener("onEdgeRemoved", event -> {
            String edge = event.getData().toString();
            if (isGatewayEdge(edge)) return;
            if (isStartOrEndEdge(edge)) edge = convertStartOrEndEdge(edge);
            FilterActionOnElementFilter action = new FilterActionOnEdgeRemoveTrace(parent, parent.getProcessAnalyst());
            action.setElement(edge, parent.getUserOptions().getMainAttributeKey());
            parent.getActionManager().executeAction(action);
        });
        
        vizBridge.addEventListener("onEdgeRetained", event -> {
            String edge = event.getData().toString();
            if (isGatewayEdge(edge)) return;
            if (isStartOrEndEdge(edge)) edge = convertStartOrEndEdge(edge);
            FilterActionOnElementFilter action = new FilterActionOnEdgeRetainTrace(parent, parent.getProcessAnalyst());
            action.setElement(edge, parent.getUserOptions().getMainAttributeKey());
            parent.getActionManager().executeAction(action);
        });
    }

    private boolean isGatewayEdge(String edge) {
        return (edge.startsWith(AND_FROM_PATTERN) || edge.endsWith(AND_TO_PATTERN) ||
                edge.startsWith(OR_FROM_PATTERN) || edge.endsWith(OR_TO_PATTERN) ||
                edge.startsWith(XOR_FROM_PATTERN) || edge.endsWith(XOR_TO_PATTERN));
    }

    private boolean isStartOrEndEdge(String edge) {
        return (edge.contains(STARTEVENT_REL_PATTERN) || edge.contains(ENDEVENT_REL_PATTERN));
    }

    private String convertStartOrEndEdge(String edge) {
        if (edge.contains(STARTEVENT_REL_PATTERN)) {
            return edge.replace(STARTEVENT_REL_PATTERN, STARTEVENT_NEW_REL_PATTERN);
        } else if (edge.contains(ENDEVENT_REL_PATTERN)) {
            return edge.replace(ENDEVENT_REL_PATTERN, ENDEVENT_NEW_REL_PATTERN);
        } else {
            return null;
        }
    }
    
    /**
     * Display a new diagram.
     * 
     * String values go from Java -> JSON -> Javascript, thus they must conform to three Java, JSON and Javascript rules
     * in the same order. Special characters such as ', " and \ must be escaped according to these three rules.
     * In Java and Javascript, special characters must be escaped (i.e. adding "\")
     * In JSON:
     * - Double quotes (") and backslashes (\) must be escaped
     * - Single quotes (') may not be escaped
     * JSONArray.toString strictly conforms to JSON syntax rules, i.e. it will escape special characters.
     * For example, a special character "\" appears in a string.
     * - First it must be escaped in Java strings to be valid ("\\")
     * - Next, JSONArray.toString will make it valid JSON strings, so it becomes "\\\\".
     * - When it is parsed to JSON object in Javascript, the parser will remove escape chars, convert it back to "\\"
     * - When this string is used at client side, it is understood as one backslash character ("\")
     *
     */
    public void displayDiagram(String visualizedText) {
        //int retainZoomPan = parent.getUserOptions().getRetainZoomPan() ? 1 : 0;
        String javascript = "Ap.pd.loadLog('" +
                            visualizedText + "'," +
                            parent.getUserOptions().getSelectedLayout() + "," +
                            parent.getUserOptions().getRetainZoomPan() + ");";
        Clients.evalJavaScript(javascript);
        parent.getUserOptions().setRetainZoomPan(false);
    }

    /*
     * Change layout, the same diagram.
     */
    public void changeLayout() {
        if (parent.getUserOptions().getLayoutHierarchy()) {
            parent.getUserOptions().setSelectedLayout(0);
        }
        else if (parent.getUserOptions().getLayoutDagre()) {
            parent.getUserOptions().setSelectedLayout(2);
        }
        
        this.displayDiagram(parent.getOutputData().getVisualizedText());
    }

    public void centerToWindow() {
        Clients.evalJavaScript("Ap.pd.center(" + parent.getUserOptions().getSelectedLayout() + ");");
    }

    public void fitToWindow() {
        Clients.evalJavaScript("Ap.pd.fit(" + parent.getUserOptions().getSelectedLayout() + ");");
    }

    public void exportPDF(String name) {
        String command = String.format("Ap.pd.exportPDF('%s');", name);
        Clients.evalJavaScript(command);
    }

    public void exportPNG(String name) {
        String command = String.format("Ap.pd.exportPNG('%s');", name);
        Clients.evalJavaScript(command);
    }
    
    public void exportJSON(String name) {
        String command = String.format("Ap.pd.exportJSON('%s');", name);
        Clients.evalJavaScript(command);
    }

    @Override
    public void onEvent(Event event) throws Exception {
        throw new Exception("Unsupported interactive Event Handler");
    }
    
    /**
     * Display the model from the current view
     */
    public void switchToModelView() {
        String javascript = "Ap.pd.switchToInteractiveView()";
        Clients.evalJavaScript(javascript);
    }
    
    /**
     * Switch to the animation view from the current view
     * @throws Exception
     */
    public void switchToAnimationView() throws Exception {
        // Align log and model
        BPMNDiagram oriDiagram = parent.getOutputData().getAbstraction().getDiagram();
        BPMNDiagram alignDiagram = parent.getOutputData().getAbstraction().getValidBPMNDiagram();
        AnimationResult alignmentResult = createAlignment(oriDiagram, alignDiagram, parent.getProcessAnalyst().getXLog());
        
        // Prepare animation data
        animateContext = new AnimationContext(alignmentResult.getAnimationLogs());
        ModelMapping modelMapping = new BpmnModelMapping(oriDiagram);
        
        long timer = System.currentTimeMillis();
        List<AnimationIndex> animationIndexes = new ArrayList<>();
        JSONArray logStartFrameIndexes = new JSONArray();
        JSONArray logEndFrameIndexes = new JSONArray();
        for (AnimationLog log : alignmentResult.getAnimationLogs()) {
            animationIndexes.add(new AnimationIndex(log, modelMapping, animateContext));
            logStartFrameIndexes.put(animateContext.getFrameIndexFromLogTimestamp(log.getStartDate().getMillis()));
            logEndFrameIndexes.put(animateContext.getFrameIndexFromLogTimestamp(log.getEndDate().getMillis()));
        }
        LOGGER.info("Create animation index: " + (System.currentTimeMillis() - timer)/1000 + " seconds.");
        
        LOGGER.info("Start recording frames");
        timer = System.currentTimeMillis();
        animationMovie = FrameRecorder.record(animationIndexes, animateContext);
        LOGGER.info("Finished recording frames: " + (System.currentTimeMillis() - timer)/1000 + " seconds.");
        
        // Prepare initial setup data for the animation
        JSONObject setupData = alignmentResult.getSetupJSON();
        setupData.put("recordingFrameRate", animateContext.getRecordingFrameRate());
        setupData.put("recordingDuration", animateContext.getRecordingDuration());
        setupData.put("logStartFrameIndexes", logStartFrameIndexes);
        setupData.put("logEndFrameIndexes", logEndFrameIndexes);
        setupData.put("elementIndexIDMap", modelMapping.getElementJSON());
        setupData.put("caseCountsByFrames", Stats.computeCaseCountsJSON(animationMovie));
        
        // Show animation view
        String javascript = "Ap.pd.switchToAnimationView('" + setupData.toString() + "')";
        Clients.evalJavaScript(javascript);
    }
    
    /**
     * Display the trace
     * @param visualizedText
     */
    public void displayTraceDiagram(String visualizedText) {
        String javascript = "Ap.pd.loadTrace('" + visualizedText + "');";
        Clients.evalJavaScript(javascript);
    }
    
    private List<LogAnimationService2.Log> createLogs(XLog eventlog) {
        List<LogAnimationService2.Log> logs = new ArrayList<>();
        Iterator<String> colors = Arrays.asList("#0088FF", "#FF8800", "#88FF00").iterator();
        LogAnimationService2.Log log = new LogAnimationService2.Log();
        log.fileName = parent.getContextData().getLogName();
        log.xlog     = eventlog;
        log.color    = colors.hasNext() ? colors.next() : "red";
        logs.add(log);
        return logs;
    }
    
    private AnimationResult createAlignment(BPMNDiagram oriDiagram, BPMNDiagram alignDiagram, XLog log) throws Exception {
        Abstraction abs = parent.getOutputData().getAbstraction();
        if (abs.getLayout() == null) {
            throw new InvalidOutputException("Missing layout of the process map for animating");
        }
        
        LogAnimationService2 logAnimationService = parent.getLogAnimationService();
        AnimationResult result = null;
        if (parent.isBPMNView()) {
            result = logAnimationService.createAnimation(getBPMN(alignDiagram), createLogs(log));
            
        }
        else {
            result = logAnimationService.createAnimationWithNoGateways(getBPMN(alignDiagram),
                                                                        getBPMN(oriDiagram),
                                                                        createLogs(log));
        }
        
        if (result == null) {
            throw new Exception("No animation result was created");
        }
        return result;
    }
    
    public Movie getAnimationMovie() {
        return this.animationMovie;
    }
    
    public void setFrameSkip(String frameSkip) {
        animateContext.setFrameSkip(Integer.valueOf((frameSkip)));
    }
    
    private String getBPMN(BPMNDiagram diagram) {
        BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = null;
        definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(diagram);
        BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">");
        sb.append(definitions.exportElements());
        sb.append("</definitions>");
        String bpmnText = sb.toString();
        bpmnText.replaceAll("\n", "");
        return bpmnText;
    }
}
