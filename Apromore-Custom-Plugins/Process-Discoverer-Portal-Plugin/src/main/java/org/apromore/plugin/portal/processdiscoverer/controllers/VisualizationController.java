/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.processdiscoverer.controllers;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

public class VisualizationController extends AbstractActionController {

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

    Component vizBridge;

    public VisualizationController(PDController controller) {
        super(controller);
    }

    public void initializeControls() {
        if (this.parent == null) return;
        vizBridge = parent.getFellow("vizBridge");
    }
    
    private void showEmptyLogMessageBox() {
        Messagebox.show("The log is empty after applying all filter criteria! Please use different criteria.",
              "Process Discoverer",
              Messagebox.OK,
              Messagebox.INFORMATION);
    }

    public void initializeEventListeners() {
        vizBridge.addEventListener("onNodeRemovedTrace", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                //filterForNodeEvent(event, Action.REMOVE, Level.TRACE, Containment.CONTAIN_ANY);
                if (parent.getLogData().filter_RemoveTracesAnyValueOfEventAttribute(event.getData().toString(), 
                        parent.getUserOptions().getMainAttributeKey())) {
                    parent.updateUI(false);
                }
                else {
                    showEmptyLogMessageBox();
                }
            }
        });
        vizBridge.addEventListener("onNodeRetainedTrace", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                //filterForNodeEvent(event, Action.RETAIN, Level.TRACE, Containment.CONTgetDataAIN_ANY);
                if (parent.getLogData().filter_RetainTracesAnyValueOfEventAttribute(event.getData().toString(), 
                        parent.getUserOptions().getMainAttributeKey())) {
                    parent.updateUI(false);
                }
                else {
                    showEmptyLogMessageBox();
                }
            }
        });
        vizBridge.addEventListener("onNodeRemovedEvent", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                //filterForNodeEvent(event, Action.REMOVE, Level.EVENT, Containment.CONTAIN_ANY);
                if (parent.getLogData().filter_RemoveEventsAnyValueOfEventAttribute(event.getData().toString(), 
                        parent.getUserOptions().getMainAttributeKey())) {
                    parent.updateUI(false);
                }
                else {
                    showEmptyLogMessageBox();
                }
            }
        });
        vizBridge.addEventListener("onNodeRetainedEvent", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                //filterForNodeEvent(event, Action.RETAIN, Level.EVENT, Containment.CONTAIN_ANY);
                if (parent.getLogData().filter_RetainEventsAnyValueOfEventAttribute(event.getData().toString(), 
                        parent.getUserOptions().getMainAttributeKey())) {
                    parent.updateUI(false);
                }
                else {
                    showEmptyLogMessageBox();
                }
            }
        });
        vizBridge.addEventListener("onEdgeRemoved", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                //filterForEdgeEvent(event, Action.REMOVE, Level.TRACE, Containment.CONTAIN_ANY);
                String edge = event.getData().toString();
                if (isGatewayEdge(edge)) return;
                if (isStartOrEndEdge(edge)) edge = convertStartOrEndEdge(edge);
                if (parent.getLogData().filter_RemoveTracesAnyValueOfDirectFollowRelation(edge, 
                        parent.getUserOptions().getMainAttributeKey())) {
                    parent.updateUI(false);
                }
                else {
                    showEmptyLogMessageBox();
                }
            }
        });
        vizBridge.addEventListener("onEdgeRetained", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                //filterForEdgeEvent(event, Action.RETAIN, Level.TRACE, Containment.CONTAIN_ANY);
                String edge = event.getData().toString();
                if (isGatewayEdge(edge)) return;
                if (isStartOrEndEdge(edge)) edge = convertStartOrEndEdge(edge);
                if (parent.getLogData().filter_RetainTracesAnyValueOfDirectFollowRelation(edge, 
                        parent.getUserOptions().getMainAttributeKey())) {
                    parent.updateUI(false);
                }
                else {
                    showEmptyLogMessageBox();
                }
            }
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

//    private void filterForNodeEvent(Event event, Action action, Level level, Containment containment) throws InterruptedException {
//        Set<String> removeNodes = new HashSet<>();
//        String node = event.getData().toString();
//        removeNodes.add(node);
//
//        if (removeNodes.size() > 0) {
//            parent.applyCriterion(parent.getLogFilterCriterionFactory().getLogFilterCriterion(
//                    action,
//                    containment,
//                    level,
//                    userOptions.getMainAttributeKey(),
//                    userOptions.getMainAttributeKey(),
//                    removeNodes
//            ));
//        }
//    }
//    
//    private void filterForNodeEvent(Event event, Choice choice, Section section, Inclusion inclusion) throws InterruptedException {
//        Set<RuleValue> removeNodes = new HashSet<>();
//        String nodeValue = event.getData().toString();
//        removeNodes.add(new RuleValue(FilterType.EVENT_EVENT_ATTRIBUTE, OperationType.EQUAL, userOptions.getMainAttributeKey(), nodeValue));
//
//        if (removeNodes.size() > 0) {
//            parent.applyCriterion(getNodeFilterRule(choice, section, inclusion, removeNodes));
//        }
//    }

//    private void filterForEdgeEvent(Event event, Action action, Level level, Containment containment) throws InterruptedException {
//        Set<String> removedArcs = new HashSet<>();
//        String edge = event.getData().toString();
//
//        if (isGatewayEdge(edge)) {
//            return;
//        }
//
//        if (isStartOrEndEdge(edge)) {
//            edge = convertStartOrEndEdge(edge);
//        }
//
//        removedArcs.add(edge);
//        if (removedArcs.size() > 0) {
//            parent.applyCriterion(parent.getLogFilterCriterionFactory().getLogFilterCriterion(
//                    action,
//                    containment,
//                    level,
//                    userOptions.getMainAttributeKey(),
//                    "direct:follow",
//                    removedArcs
//            ));
//        }
//    }
//    
//    private void filterForEdgeEvent(Event event, Choice choice, Section section, Inclusion inclusion) throws InterruptedException {
//        Set<String> removedArcs = new HashSet<>();
//        String edge = event.getData().toString();
//
//        if (isGatewayEdge(edge)) {
//            return;
//        }
//
//        if (isStartOrEndEdge(edge)) {
//            edge = convertStartOrEndEdge(edge);
//        }
//
//        removedArcs.add(edge);
//        if (removedArcs.size() > 0) {
//            parent.applyCriterion(getEdgeFilterRule(choice, section, inclusion, getDirectFollowRuleValues(edge)));
//        }
//    } 

    /**
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
        String javascript = "Ap.pd.loadLog('" + visualizedText + "'," +  parent.getUserOptions().getSelectedLayout() + "," +
                            parent.getUserOptions().getRetainZoomPan() + ");";
        Clients.evalJavaScript(javascript);
        parent.getUserOptions().setRetainZoomPan(false);
    }
    
    public void displayTraceDiagram(String visualizedText) {
        String javascript = "Ap.pd.loadTrace('" + visualizedText + "');";
        Clients.evalJavaScript(javascript);
    }

    public void changeLayout() throws InterruptedException {
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
}
