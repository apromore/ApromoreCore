/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

package org.apromore.service.impl;

import org.apromore.service.BPMNDiagramImporter;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Association;
import org.processmining.models.graphbased.directed.bpmn.elements.CallActivity;
import org.processmining.models.graphbased.directed.bpmn.elements.DataAssociation;
import org.processmining.models.graphbased.directed.bpmn.elements.DataObject;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.processmining.plugins.bpmn.BpmnAssociation;
import org.processmining.plugins.bpmn.BpmnDefinitions;


import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ee.ut.bpstruct.Restructurer;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.serialize.JSON2Process;
import de.hpi.bpt.process.serialize.Process2JSON;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apromore.service.StructuringService;
import org.springframework.stereotype.Service;


@Service
public class StructuringServiceImpl implements StructuringService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StructuringServiceImpl.class);

	private BPMNDiagram diagram;		//initial diagram
	private long taskCounter = 0;		//id for processes and tasks

	/**** not mappable elements on bpStruct json scheme ****/
	private Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> unmappableEdges;
	private Set<BPMNNode> unmappableNodes;

	/**** mappable elements on bpStruct json scheme ****/
	private Map<Long, BPMNNode> mappedNodes;
    private Map<Long, Swimlane> idToPool;
	private Set<SubProcess> subProcessesToParse;

	/**** support maps to restore the diagram's edges correctly ****/
	private Map<SubProcess, Long> subProcessToID;
	private LinkedList<Long> rebuildOrder;
	private Map<Event, Activity> boundToFix;
	private Set<Event> startEvents;
	private Set<Event> fakeStartEvents;
	private Set<Event> endEvents;
	private Set<Event> fakeEndEvents;
	private Map<Event, BPMNNode> compensationActivities;
	private Map<BPMNNode, Set<BPMNNode>> blackList;
	private Map<BPMNNode, Set<BPMNNode>> whiteList;
	private Map<BPMNNode, BPMNNode> matrices;

	/**** mapping between processes' IDs and their .json structured version ****/
	private Map<Long, String> idToJson;

    private boolean isValid;
	private Map<Long, String> errors;

	public StructuringServiceImpl() { }

	@Override
	public Map<Long, String> getErrors() {
		if( isValid ) return this.errors;
		else return null;
	}

	@Override
	public BPMNDiagram getStructuredDiagram() {
		if( isValid ) return this.diagram;
        else return null;
	}

	@Override
	public String structureBPMNModel(BPMNDiagram diagram) throws Exception {
        isValid = false;
		this.diagram = diagram;
		structureDiagram();
        isValid = true;
		return transformToXml();
	}

	@Override
	public String structureBPMNModel(String xmlProcess) throws Exception {
		BPMNDiagramImporter diagramImporter = new BPMNDiagramImporterImpl();
        isValid = false;
		diagram = diagramImporter.importBPMNDiagram(xmlProcess);
		LOGGER.info("Diagram parsed! Found: " + diagram.getPools().size() + " pools AND " + diagram.getNodes().size() + " nodes AND " + diagram.getEdges().size() + " edges.");
		structureDiagram();
		isValid = true;
		return transformToXml();
	}

	private String transformToXml() {
		UIContext context = new UIContext();
		UIPluginContext uiPluginContext = context.getMainPluginContext();
		StringBuilder sb = new StringBuilder();
		String xmlProcessChecked;

		BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, this.diagram);
		BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

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

		xmlProcessChecked = sb.toString();

		/** formatting /n for attribute's value **/
		xmlProcessChecked = xmlProcessChecked.replaceAll("\n", "&#10;");
		xmlProcessChecked = xmlProcessChecked.replaceAll(">&#10;", ">\n");
		xmlProcessChecked = xmlProcessChecked.replaceAll("\"&#10;", "\"\n");

		//LOGGER.info("Final Process >\n" + xmlProcessChecked);
		return xmlProcessChecked;
	}

	private void structureDiagram() throws Exception {
		errors = new HashMap<>();
		taskCounter = 0;
		unmappableEdges = new HashSet<>();
		unmappableNodes = new HashSet<>();
		mappedNodes = new HashMap<>();
        idToPool = new HashMap<>();
		subProcessesToParse = new HashSet<>();
		subProcessToID = new HashMap<>();
		rebuildOrder = new LinkedList<>();
		startEvents = new HashSet<>();
		fakeStartEvents = new HashSet<>();
		endEvents = new HashSet<>();
		fakeEndEvents = new HashSet<>();
		boundToFix = new HashMap<>();
		compensationActivities = new HashMap<>();
		blackList = new HashMap<>();
		whiteList = new HashMap<>();
		matrices = new HashMap<>();
		idToJson = new HashMap<>();

		Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edgeToRemove = new HashSet<>();
		Set<BPMNNode> nodeToRemove = new HashSet<>();

		/**** STEP0: back up of all not mappable edges and nodes on the .json file ****/
		unmappableEdges.addAll(diagram.getAssociations());
		unmappableEdges.addAll(diagram.getDataAssociations());
		unmappableEdges.addAll(diagram.getMessageFlows());

		unmappableNodes.addAll(diagram.getTextAnnotations());
		unmappableNodes.addAll(diagram.getDataObjects());

		/** STEP1: restructure the diagram in order to remove
		 * - double edges
		 * - fakeGates (those gate with only 1 entering and 1 exiting flow)
		 * - multiple start and end events
		 * - boundary events
		 **/
		fixImplicitGateways();
		removeDoubleEdges();
		removeMultipleStartEvents();
		removeMultipleEndEvents();
		handleBoundaryEvents();

		/**** STEP2: get all the structured version (as .json file) for each subProcess ****/
		for( SubProcess sp : diagram.getSubProcesses() ) {
			taskCounter++;
			subProcessesToParse.add(sp);
			mappedNodes.put(taskCounter, sp);
			subProcessToID.put(sp, taskCounter);
			LOGGER.info("SubProcess: " + sp.getId() + " aka:  subProcess_" + taskCounter);
		}
		for( Swimlane pool : diagram.getPools() ) parsePool(pool);
        parsePool(null);
		parseSubProcesses(null);

		/**** STEP3: remove all the edges and not mappable nodes from the diagram ****/
		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getEdges() ) edgeToRemove.add(e);
		for( Gateway g : diagram.getGateways() ) nodeToRemove.add(g);

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : edgeToRemove ) diagram.removeEdge(e);
		for( BPMNNode g : nodeToRemove ) removeNode(g);

		/**** STEP4: reconnect all the elements inside the diagram exploiting .json files and support maps ****/
		while( !rebuildOrder.isEmpty() ) {
			long idp = rebuildOrder.removeLast();
			boolean err = rebuildProcess(idp);
			if( err ) throw new Exception("Unable to rebuild the subProcess_" + idp);
		}

		/**** STEP5: restore edges and boundary, start, end events where feasible ****/
        restoreEdges();
		restoreEvents();

		/**** STEP6: fixing possible not valid configurations ****/
		removeDoubleEdges();
		for( Gateway g : new HashSet<>(diagram.getGateways()) ) checkFakeGateway(g);
	}

	private void parsePool(Swimlane pool) {
		LOGGER.info("Analyzing Pool: " + (pool == null ? "Top-Level" : pool.getLabel()) + "(" + (pool == null ? "null" : pool.getId()) + ")");
		taskCounter++;
        idToPool.put(taskCounter, pool);
		generateJson(taskCounter, diagram.getFlows(pool));
	}

	private void parseSubProcesses(SubProcess parent) {
		//LOGGER.info("Analyzing subProcess of the Parent Process: " + (parent == null ? "null" : subProcessToID.get(parent)));

		HashSet<SubProcess> analyzed = new HashSet<>();

		for( SubProcess sp : subProcessesToParse )
			if( sp.getParentSubProcess() == parent ) {
				generateJson(subProcessToID.get(sp), diagram.getFlows(sp));
				//LOGGER.info("Analyzed: subProcess_" + subProcessToID.get(sp));
				analyzed.add(sp);
			}

		for( SubProcess spa : analyzed ) subProcessesToParse.remove(spa);
		for( SubProcess spa : analyzed ) parseSubProcesses(spa);
	}

	private void removeDoubleEdges() {
		HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> towAway = new HashSet<>();
		HashMap<BPMNNode, HashSet<BPMNNode>> flows = new HashMap<>();

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getEdges() ) {
			if( !flows.containsKey(f.getSource()) ) {
				flows.put(f.getSource(), new HashSet<BPMNNode>());
				flows.get(f.getSource()).add(f.getTarget());
			} else {
				if( flows.get(f.getSource()).contains(f.getTarget()) ) towAway.add(f);
				else flows.get(f.getSource()).add(f.getTarget());
			}
		}

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : towAway ) {
			//LOGGER.info("DoubleFlow removed: " + ff.getSource().getId() + " > " + ff.getTarget().getId());
			diagram.removeEdge(ff);
			if( ff.getSource() instanceof Gateway )	checkFakeGateway((Gateway) ff.getSource());
			if( ff.getTarget() instanceof Gateway )	checkFakeGateway((Gateway) ff.getTarget());
		}
	}

	private void fixImplicitGateways() {
		Gateway g;
		HashSet<Flow> inFlows;
		HashSet<Flow> outFlows;

		for (BPMNNode n : diagram.getNodes()) {
			if( n instanceof Activity || n instanceof CallActivity ) {
				inFlows = new HashSet<>();
				outFlows = new HashSet<>();

				for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(n) )
					if(e instanceof Flow) inFlows.add((Flow)e);

				for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(n) )
					if(e instanceof Flow) outFlows.add((Flow)e);

				if( inFlows.size() > 1 ) {
					g = diagram.addGateway("exGate", Gateway.GatewayType.DATABASED, n.getParentSubProcess());
					g.setParentSwimlane(n.getParentSwimlane());
					for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : inFlows ) {
						diagram.addFlow(e.getSource(), g, "");
						diagram.removeEdge(e);
					}
					diagram.addFlow(g, n, "");
				}

				if( outFlows.size() > 1 ) {
					g = diagram.addGateway("exGate", Gateway.GatewayType.PARALLEL, n.getParentSubProcess());
					g.setParentSwimlane(n.getParentSwimlane());
					for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : outFlows ) {
						diagram.addFlow(g, e.getTarget(), "");
						diagram.removeEdge(e);
					}
					diagram.addFlow(n, g, "");
				}
			}
		}
	}

	private void checkFakeGateway(Gateway g) {
		//LOGGER.info("Checking fake gateways: " + g.getId());
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> in = null;
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> out = null;
		int incoming = 0;
		int outgoing = 0;

		for( Flow f : diagram.getFlows() ) {
			if( f.getSource() == g ) {
				out = f;
				outgoing++;
			}
			if( f.getTarget() == g ) {
				in = f;
				incoming++;
			}
		}

		if( (outgoing == 1) && (incoming == 1) ) {
			diagram.addFlow(in.getSource(), out.getTarget(), "");
			diagram.removeEdge(in);
			diagram.removeEdge(out);
			removeNode(g);
			LOGGER.info("Found and removed a fake gate: " + g.getId());
		}
	}

	private void removeMultipleStartEvents() {
		HashMap<SubProcess, HashSet<Event>> sp2se = new HashMap<>(); //subprocesses
		HashMap<Swimlane, HashSet<Event>> pl2se = new HashMap<>();	//pools
		Gateway g;
		Event fakeStart;

		for( SubProcess sp : diagram.getSubProcesses() ) sp2se.put(sp, new HashSet<Event>());
		for( Swimlane pl : diagram.getPools() ) pl2se.put(pl, new HashSet<Event>());
		pl2se.put(null, new HashSet<Event>());

		/* adding each startEvent to the right set */
		for( Event e : diagram.getEvents() )
			if( e.getEventType() == Event.EventType.START ) {
				if( e.getParentSubProcess() == null ) pl2se.get(e.getParentPool()).add(e);
				else sp2se.get(e.getParentSubProcess()).add(e);
			}

		/* for all those processes with multiple endEvents it is created a fakeEndPoint */
		for( SubProcess spe : sp2se.keySet() )
			if( sp2se.get(spe).size() > 1 ) {
				g = diagram.addGateway("fakeStartXOR", Gateway.GatewayType.DATABASED, spe);
				fakeStart = diagram.addEvent("fakeStart", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, spe, true, null);
				fakeStartEvents.add(fakeStart);

				for( Event e : sp2se.get(spe) ) {
					diagram.addFlow(g, e, "fakeMergingFlow");
					startEvents.add(e);
					matrices.put(e, fakeStart);
				}

				diagram.addFlow(fakeStart, g, "fakeStartFlow");
				LOGGER.info("Added fakeStart (" + fakeStart.getId() + ") for subProcess: " + (spe == null ? "null" : spe.getId()));
			}

		for( Swimlane ple : pl2se.keySet() )
			if( pl2se.get(ple).size() > 1 ) {
				g = diagram.addGateway("fakeStartXOR", Gateway.GatewayType.DATABASED, ple);
				fakeStart = diagram.addEvent("fakeStart", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, ple, true, null);
				fakeStartEvents.add(fakeStart);

				for( Event e : pl2se.get(ple) ) {
					diagram.addFlow(g, e, "fakeMergingFlow");
					startEvents.add(e);
					matrices.put(e, fakeStart);
				}

				diagram.addFlow(fakeStart, g, "fakeStartFlow");
				LOGGER.info("Added fakeStart (" + fakeStart.getId() + ") for pool: " + (ple == null ? "null" : ple.getId()));
			}
	}

	private void removeMultipleEndEvents() {
		HashMap<SubProcess, HashSet<Event>> sp2ee = new HashMap<>(); //subprocesses
		HashMap<Swimlane, HashSet<Event>> pl2ee = new HashMap<>();	//pools
		Gateway g;
		Event fakeEnd;

		for( SubProcess sp : diagram.getSubProcesses() ) sp2ee.put(sp, new HashSet<Event>());
		for( Swimlane pl : diagram.getPools() ) pl2ee.put(pl, new HashSet<Event>());
        pl2ee.put(null, new HashSet<Event>());

		/* adding each endEvent to the right set */
		for( Event e : diagram.getEvents() )
			if( e.getEventType() == Event.EventType.END ) {
				if( e.getParentSubProcess() == null ) pl2ee.get(e.getParentPool()).add(e);
				else sp2ee.get(e.getParentSubProcess()).add(e);
			}

		/* for all those processes with multiple endEvents it is created a fakeEndPoint */
		for( SubProcess spe : sp2ee.keySet() )
			if( sp2ee.get(spe).size() > 1 ) {
				g = diagram.addGateway("fakeEndXOR", Gateway.GatewayType.DATABASED, spe);
				fakeEnd = diagram.addEvent("fakeEnd", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, spe, true, null);
				fakeEndEvents.add(fakeEnd);

				for( Event e : sp2ee.get(spe) ) {
					diagram.addFlow(e, g, "fakeMergingFlow");
					endEvents.add(e);
					matrices.put(e, fakeEnd);
				}

				diagram.addFlow(g, fakeEnd, "toFakeEnd");
				LOGGER.info("Added fakeEnd (" + fakeEnd.getId() + ") for subProcess: " + (spe == null ? "null" : spe.getId()));
			}

		for( Swimlane ple : pl2ee.keySet() )
			if( pl2ee.get(ple).size() > 1 ) {
				g = diagram.addGateway("fakeEndXOR", Gateway.GatewayType.DATABASED, ple);
				fakeEnd = diagram.addEvent("fakeEnd", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, ple, true, null);
				fakeEndEvents.add(fakeEnd);

				for( Event e : pl2ee.get(ple) ) {
					diagram.addFlow(e, g, "fakeMergingFlow");
					endEvents.add(e);
					matrices.put(e, fakeEnd);
				}

				diagram.addFlow(g, fakeEnd, "toFakeEnd");
				LOGGER.info("Added fakeEnd (" + fakeEnd.getId() + ") for pool: " + (ple == null ? "null" : ple.getId()));
			}
	}

	private void handleBoundaryEvents() {
		Activity a;
		Gateway g;
		BPMNNode tgt;

		for( Event e : diagram.getEvents() )
			if( (a = e.getBoundingNode()) != null ) {
				if( e.getEventTrigger() == Event.EventTrigger.COMPENSATION ) {
					for( Association association : diagram.getAssociations() )
						if( association.getSource() == e )
                            if( ((association.getTarget() instanceof Activity) && ((Activity) association.getTarget()).isBCompensation()) ||
                                ((association.getTarget() instanceof CallActivity) && ((CallActivity) association.getTarget()).isBCompensation()) )
                            {
                                LOGGER.info("Detaching compensation event and activity: " + e.getId() + " > " + association.getTarget().getId());
                                boundToFix.put(e, a);
                                compensationActivities.put(e, association.getTarget());
                                break;
                            }
				} else {
					for (Flow f : diagram.getFlows())
						if (f.getSource().equals(a)) {
							tgt = f.getTarget();
							if ((tgt instanceof Gateway) && ((((Gateway) tgt).getGatewayType() == Gateway.GatewayType.EVENTBASED) ||
									(((Gateway) tgt).getGatewayType() == Gateway.GatewayType.DATABASED))) {
								g = (Gateway) tgt;
							} else {
								g = diagram.addGateway("boundaryXOR", Gateway.GatewayType.DATABASED, a.getParentSubProcess());
								g.setParentSwimlane(a.getParentSwimlane());
								diagram.addFlow(a, g, "");
								diagram.addFlow(g, tgt, "");
								diagram.removeEdge(f);
							}
							diagram.addFlow(g, e, "");
							e.setParentSubprocess(a.getParentSubProcess());
							boundToFix.put(e, a);
							LOGGER.info("Boundary event found: " + a.getId() + " > " + e.getId());
							break;
						}
				}
			}
	}

	private void generateJson(long processID, Collection<Flow> edges) {

        if( edges.size() == 0 ) return;

		Map<BPMNNode, UUID> processedNodes = new HashMap<>();
		Set<JsonTask> tasks = new HashSet<>();
		Set<JsonGateway> gateways = new HashSet<>();
		Set<JsonFlow> flows = new HashSet<>();
		JsonTask jTask;
		JsonGateway jGate;
		JsonFlow jFlow;
		JsonProcess jProcess;
		String jResponse;
        boolean retry = false;

		BPMNNode src, tgt;
		UUID srcUID, tgtUID;

		for( Flow flow : edges ) {

			src = flow.getSource();
			tgt = flow.getTarget();

			/**** we cannot map these elements within the json scheme and so neither the current flow, this SHOULD NOT HAPPEN ****/
			if( src instanceof DataObject || src instanceof Swimlane || src instanceof TextAnnotation ||
				tgt instanceof DataObject || tgt instanceof Swimlane || tgt instanceof TextAnnotation ) {
                    LOGGER.info("Unmappable flow: " + src.getId() +  " > " + tgt.getId());
					unmappableEdges.add(flow);
					continue;
			}

			/**** checking whether srcNode and tgtNode have been already processed before ****/
			srcUID = processedNodes.get(src);
			tgtUID = processedNodes.get(tgt);

			/**** first time we meet this tgtNode ****/
			if( tgtUID == null ) {
				if( tgt instanceof SubProcess ) {
					jTask = new JsonTask(subProcessToID.get(tgt), Long.toString(subProcessToID.get(tgt)));
					tasks.add(jTask);
					tgtUID = jTask.uid;
				} else {
					taskCounter++;
					if(tgt instanceof Gateway) {
                        if( (((Gateway) tgt).getGatewayType() == Gateway.GatewayType.COMPLEX) ||
                            (((Gateway) tgt).getGatewayType() == Gateway.GatewayType.INCLUSIVE) ) retry = true;

						jGate = new JsonGateway(taskCounter, ((Gateway) tgt).getGatewayType());
						gateways.add(jGate);
						tgtUID = jGate.uid;
					} else {
						jTask = new JsonTask(taskCounter, Long.toString(taskCounter));
						tasks.add(jTask);
						tgtUID = jTask.uid;
					}
					mappedNodes.put(taskCounter, tgt);
				}
				processedNodes.put(tgt, tgtUID);
			}

			/**** first time we meet this srcNode ****/
			if( srcUID == null ) {
				if( src instanceof SubProcess ) {
					jTask = new JsonTask(subProcessToID.get(src), Long.toString(subProcessToID.get(src)));
					tasks.add(jTask);
					srcUID = jTask.uid;
				} else {
					taskCounter++;
					if (src instanceof Gateway) {
                        if( (((Gateway) src).getGatewayType() == Gateway.GatewayType.COMPLEX) ||
                            (((Gateway) src).getGatewayType() == Gateway.GatewayType.INCLUSIVE) ) retry = true;

						jGate = new JsonGateway(taskCounter, ((Gateway) src).getGatewayType());
						gateways.add(jGate);
						srcUID = jGate.uid;
					} else {
						jTask = new JsonTask(taskCounter, Long.toString(taskCounter));
						tasks.add(jTask);
						srcUID = jTask.uid;
					}
					mappedNodes.put(taskCounter, src);
				}
				processedNodes.put(src, srcUID);
			}

			jFlow = new JsonFlow("", srcUID, tgtUID);
			flows.add(jFlow);
            //LOGGER.info("json- Added flow: " + src.getId() +  " > " + tgt.getId());
		}

		jProcess = new JsonProcess(Long.toString(processID), tasks, gateways, flows);

		try {
			LOGGER.info("Process:" + jProcess.toString());
			jResponse = bpStruct(jProcess.toString());
            //jResponse = jProcess.toString();
			if( jResponse == null ) throw new Exception("Process NULL.");
			LOGGER.info("Response GOT.");
			errors.put(processID, "Successfully structured.");
		} catch (Exception e) {
			if( retry ) {
                try {
                    LOGGER.info("Attempting again, without OR gates");
                    jProcess.deleteORgates();
                    LOGGER.info("Process:" + jProcess.toString());
                    jResponse = bpStruct(jProcess.toString());
                    if( jResponse == null ) throw new Exception("Process NULL.");
                    LOGGER.info("Response GOT.");
					errors.put(processID, "Successfully structured [turned OR into XOR].");
                } catch (Exception ee) {
                    LOGGER.error("Exception [" + ee.getClass().getSimpleName() + "] for process: " + Long.toString(processID) + "\t", ee);
                    jResponse = jProcess.toString();
                    LOGGER.info("Response COPIED.");
					errors.put(processID, "Got exception in bpstruct, check manager.log.");
                }
            } else {
				LOGGER.error("Exception [" + e.getClass().getSimpleName() + "] for process: " + Long.toString(processID) + "\t", e);
                jResponse = jProcess.toString();
                LOGGER.info("Response COPIED.");
				errors.put(processID, "Got exception in bpstruct, check manager.log.");
            }
		}

		idToJson.put(processID, jResponse);
		rebuildOrder.addLast(processID);
	}

	private boolean rebuildProcess(long processID) {
		LOGGER.info("Rebuilding: subProcess_" + processID);
		SubProcess parentProcess;

		Set<Long> greyList = new HashSet<>();
		Map<String, BPMNNode> processedNodes = new HashMap<>();

    	JSONObject jsonProcessObject;
    	JSONArray tasks, gateways, flows;
    	JSONObject o;
		int i;

		BPMNNode node, src, tgt;
		String nodeUID, srcUID, tgtUID;
		long taskID;

		boolean error = false;

		try {
			if( mappedNodes.get(processID) instanceof SubProcess ) parentProcess = (SubProcess) mappedNodes.get(processID);
			else parentProcess = null;

			jsonProcessObject = new JSONObject(idToJson.get(processID));

    		/** populating the blackList with duplicated activities **/
            tasks = jsonProcessObject.getJSONArray("tasks");
            for( i = 0; i < tasks.length(); i++ ) {
            	o = tasks.getJSONObject(i);
    			taskID = Long.parseLong( o.getString("label") );
    			nodeUID = o.getString("id");

    			if( mappedNodes.containsKey(taskID) ) {
    				if( greyList.contains(taskID) ) {
    					/* this node has been duplicated by BPstruct tool so we do the same.
    					 * NOTE: duplication is always performed using the original node as template
    					 */
    					node = duplicateNode(mappedNodes.get(taskID), parentProcess, true);
    					if( node == null ) throw new JSONException("Error parsing tasks: " + taskID + " cannot be duplicated!");
    				} else {
    					/* the first time we encounter a node we retrieve the original one and mark it in the greyList */
    					node = mappedNodes.get(taskID);
    					greyList.add(taskID);
    				}
    			} else throw new JSONException("error parsing tasks: " + taskID + " not Found!");

    			processedNodes.put(nodeUID, node);
    		}

    		/** pushing gateways **/
            gateways = jsonProcessObject.getJSONArray("gateways");
            for( i = 0; i < gateways.length(); i++ ) {
            	o = gateways.getJSONObject(i);
    			nodeUID = o.getString("id");
				node = pushGateway(o.getString("type"), parentProcess, processID);
				if( node == null ) throw new JSONException("Error parsing gateways: " + nodeUID + " cannot be pushed!");
				processedNodes.put(nodeUID, node);
    		}

    		/** applying mapped flows **/
            flows = jsonProcessObject.getJSONArray("flows");
            for( i = 0; i < flows.length(); i++ ) {
            	o = flows.getJSONObject(i);
    			srcUID = o.getString("src");
    			tgtUID = o.getString("tgt");

    			if( processedNodes.containsKey(srcUID) ) src = processedNodes.get(srcUID);
    			else throw new JSONException("error parsing flows. Source not found: " + srcUID);

    			if( processedNodes.containsKey(tgtUID) ) tgt = processedNodes.get(tgtUID);
    			else throw new JSONException("error parsing flows. Target not found: " + tgtUID);

    			diagram.addFlow(src, tgt, o.getString("label"));
                //LOGGER.info("diagram- added flow: " + src.getId() +  " > " + tgt.getId());
    		}

		} catch(Exception e) {
			LOGGER.error("Error rebuilding subProcess: " + processID, e);
			error = true;
		}

		return error;
	}

	private Gateway pushGateway(String gateType, SubProcess parentProcess, long processID) {
		Gateway g = null;

		if( gateType.equalsIgnoreCase("xor") ) g = diagram.addGateway("", Gateway.GatewayType.DATABASED, parentProcess );
		else if( gateType.equalsIgnoreCase("or") ) g = diagram.addGateway("", Gateway.GatewayType.INCLUSIVE, parentProcess );
		else if( gateType.equalsIgnoreCase("and") ) g = diagram.addGateway("", Gateway.GatewayType.PARALLEL, parentProcess );

        if( g != null && idToPool.containsKey(processID) ) g.setParentSwimlane(idToPool.get(processID));
		return g;
	}

	private BPMNNode duplicateNode(BPMNNode node, SubProcess parentProcess, boolean blackMark) {
		BPMNNode duplicate = null;

		if( blackMark ) {
			if( !blackList.containsKey(node) ) blackList.put(node, new HashSet<BPMNNode>());
		} else {
			//LOGGER.info("Duplicating (" + blackMark + "): " + node.getClass().getSimpleName() + " : " + node.getId() + " with Parent: " + (parentProcess == null ? "top-Level" : parentProcess.getId()));
			if( !whiteList.containsKey(node) ) whiteList.put(node, new HashSet<BPMNNode>());
		}

		if( node instanceof SubProcess ) {
            //LOGGER.info("Duplicating subProcess: " + node.getId());
			BPMNNode src, tgt, osrc, otgt;
			boolean mark;
            HashMap<BPMNNode, BPMNNode> mapping = new HashMap<>();

			duplicate = diagram.addSubProcess( node.getLabel(),
											   ((SubProcess) node).isBLooped(),
											   ((SubProcess) node).isBAdhoc(),
											   ((SubProcess) node).isBCompensation(),
											   ((SubProcess) node).isBMultiinstance(),
											   ((SubProcess) node).isBCollapsed(),
											   ((SubProcess) node).getTriggeredByEvent(),
											   parentProcess);

			duplicate.setParentSwimlane(node.getParentSwimlane());
			((SubProcess) duplicate).setDecorator(((SubProcess) node).getDecorator());

            //LOGGER.info("("+ diagram.getFlows((SubProcess) node).size() +")STARTING>>> flows duplication for: " + duplicate.getId() + " : " + node.getId());
			for( Flow flow : diagram.getFlows((SubProcess) node) ) {
                src = osrc = flow.getSource();
                tgt = otgt = flow.getTarget();

                if( !mapping.containsKey(osrc) ) {
					/* checking if these nodes have been duplicated or not */
                    mark = false;
                    if( matrices.containsKey(osrc) ) {
                        src = matrices.get(osrc);
                        mark = blackList.containsKey(src);
                    }
                    src = duplicateNode(src, (SubProcess) duplicate, mark);
                    mapping.put(osrc, src);
                } else src = mapping.get(osrc);

                if( !mapping.containsKey(otgt) ) {
                    mark = false;
                    if (matrices.containsKey(otgt)) {
                        tgt = matrices.get(otgt);
                        mark = blackList.containsKey(tgt);
                    }
                    tgt = duplicateNode(tgt, (SubProcess) duplicate, mark);
                    mapping.put(otgt, tgt);
                } else tgt = mapping.get(otgt);

                if (src == null || tgt == null) return null;
                diagram.addFlow(src, tgt, flow.getLabel());
                //LOGGER.info("Added Flow: " + src.getId() + " > " + tgt.getId());
            }
		} else if( node instanceof Activity ) {
			duplicate = diagram.addActivity( node.getLabel(),
											((Activity) node).isBLooped(),
											((Activity) node).isBAdhoc(),
											((Activity) node).isBCompensation(),
											((Activity) node).isBMultiinstance(),
											((Activity) node).isBCollapsed(),
											 parentProcess);

            duplicate.setParentSwimlane(node.getParentSwimlane());
			((Activity) duplicate).setDecorator(((Activity) node).getDecorator());

		} else if( node instanceof CallActivity ) {
			duplicate = diagram.addCallActivity( node.getLabel(),
												((CallActivity) node).isBLooped(),
												((CallActivity) node).isBAdhoc(),
												((CallActivity) node).isBCompensation(),
												((CallActivity) node).isBMultiinstance(),
												((CallActivity) node).isBCollapsed(),
												 parentProcess);

            duplicate.setParentSwimlane(node.getParentSwimlane());
			((CallActivity) duplicate).setDecorator(((CallActivity) node).getDecorator());

		} else if( node instanceof Event ) {
			duplicate = diagram.addEvent( node.getLabel(),
										 ((Event) node).getEventType(),
										 ((Event) node).getEventTrigger(),
										 ((Event) node).getEventUse(),
										 parentProcess,
										 true,
										 null);

            duplicate.setParentSwimlane(node.getParentSwimlane());
			((Event) duplicate).setDecorator(((Event) node).getDecorator());

			if( endEvents.contains(node) ) endEvents.add((Event) duplicate);
			if( startEvents.contains(node) ) startEvents.add((Event) duplicate);
			if( matrices.containsKey(node) ) matrices.put(duplicate, matrices.get(node));

			/* this should happen only during a subProcess duplication, because BPstruct is supposed to do not duplicate end Events */
			if( fakeEndEvents.contains(node) ) fakeEndEvents.add((Event) duplicate);
			if( fakeStartEvents.contains(node) ) fakeStartEvents.add((Event) duplicate);

		} else if( node instanceof Gateway ) {
			duplicate = diagram.addGateway( node.getLabel(),
											((Gateway) node).getGatewayType(),
											parentProcess);

            duplicate.setParentSwimlane(node.getParentSwimlane());
            ((Gateway) duplicate).setMarkerVisible(((Gateway) node).isMarkerVisible());
			((Gateway) duplicate).setDecorator(((Gateway) node).getDecorator());
		}

		if( duplicate != null ) {
			if( blackMark ) blackList.get(node).add(duplicate);
			else whiteList.get(node).add(duplicate);
			matrices.put(duplicate, node);
		}

		//LOGGER.info("Added new node (" + duplicate.getClass().getSimpleName() + "): " + duplicate.getId());
		return duplicate;
	}

	private void restoreEdges() {
		BPMNNode src, tgt;

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : unmappableEdges ) {
			src = e.getSource();
			tgt = e.getTarget();

			if( blackList.containsKey(src) || blackList.containsKey(tgt) ||
                whiteList.containsKey(src) || whiteList.containsKey(tgt) ) continue;

			if( unmappableNodes.contains(src) ) unmappableNodes.remove(src);
			if( unmappableNodes.contains(tgt) ) unmappableNodes.remove(tgt);

			if( e instanceof MessageFlow) diagram.addMessageFlow(src, tgt, (Swimlane) null, e.getLabel());
			else if( e instanceof Flow ) diagram.addFlow(src, tgt, e.getLabel());
			else if( e instanceof Association) diagram.addAssociation(src, tgt, ((Association) e).getDirection());
			else if( e instanceof DataAssociation ) diagram.addDataAssociation(src, tgt, e.getLabel());

			LOGGER.info(e.getClass().getSimpleName() + " restored: " + src.getId() + " > " + tgt.getId());
		}

		for( BPMNNode n : unmappableNodes ) {
			LOGGER.info("Removing unmappable node: " + n.getClass().getSimpleName() + " : " + n.getId());
			removeNode(n);
		}
	}

	private void restoreEvents() {
        Event de;
		Activity a;
		BPMNNode ca;
		Gateway g;
		boolean error;
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> startFlow;
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> endFlow;
		Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> fakeFlows;
		Set<Event> removableFakeStarts = new HashSet<>();
		Set<Event> removableFakeEnds = new HashSet<>();


		/* Restoring other boundary events */
		for( Event e : boundToFix.keySet() ) {
			a = boundToFix.get(e);

			if( compensationActivities.containsKey(e) ) {
				if( blackList.containsKey(a) )
					for( BPMNNode bn : blackList.get(a) ) {
						de = (Event) duplicateNode(e, bn.getParentSubProcess(), false);
						ca = duplicateNode(compensationActivities.get(e), bn.getParentSubProcess(), false);
						de.setExceptionFor((Activity)bn);
						diagram.addAssociation(de, ca, BpmnAssociation.AssociationDirection.ONE);
					}

				if( whiteList.containsKey(a) )
					for( BPMNNode wn : whiteList.get(a) ) {
						de = (Event) duplicateNode(e, wn.getParentSubProcess(), false);
						ca = duplicateNode(compensationActivities.get(e), wn.getParentSubProcess(), false);
						de.setExceptionFor((Activity)wn);
						diagram.addAssociation(de, ca, BpmnAssociation.AssociationDirection.ONE);
					}
            } else {
				if( blackList.containsKey(a) || blackList.containsKey(e) ) {
					e.setExceptionFor(null);
					LOGGER.info("[" + blackList.containsKey(a) + ":" + blackList.containsKey(e)  + "] unfixable bound between: " + a.getId() + " > " + e.getId());
				} else if( !whiteList.containsKey(a) && !whiteList.containsKey(e) ) {
					tryToFixBound(e, a, false);
				} else if( whiteList.containsKey(a) && whiteList.containsKey(e) ) {
					tryToFixBound(e, a, false);
					for( BPMNNode en : whiteList.get(e) )
						for( BPMNNode an : whiteList.get(a) ) tryToFixBound((Event) en, (Activity) an, true);
				} else e.setExceptionFor(null);
			}
		}

		/* Restoring multiple start Events */
		for( Event fse : fakeStartEvents ) {
			//LOGGER.info("FakeStart : " + fse.getId());
			g = null;
			error = false;
			startFlow = null;
			fakeFlows = new HashSet<>();

			for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getFlows() ) //loop on flows to find the fakeGateway of this fakeStartEvent
				if( (f.getSource() == fse) && (f.getTarget() instanceof Gateway) ) {
					startFlow = f;
					g = (Gateway) f.getTarget();
					for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : diagram.getFlows() ) //loop on flows to find all the fakeStartFlows
						if( ff.getSource() == g ) {
							if( startEvents.contains(ff.getTarget()) ) fakeFlows.add(ff);
							else {
								error = true;
								LOGGER.info("Fixing FSE: " + fse.getId());
								fixStartEvent(fse);
								break;
							}
						}

					if( error ) break;
				}

			if( g != null && !error ) {
					LOGGER.info("Removing FSE: " + fse.getId());
					for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> fff : fakeFlows ) diagram.removeEdge(fff);
					diagram.removeEdge(startFlow);
					removeNode(g);
					removableFakeStarts.add(fse);
			}
		}
		for( Event rfs : removableFakeStarts ) removeNode(rfs);

		/* Restoring multiple end Events */
		for( Event fee : fakeEndEvents ) {
			//LOGGER.info("FakeEnd : " + fee.getId());
			g = null;
			error = false;
			endFlow = null;
			fakeFlows = new HashSet<>();

			for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getFlows() ) //loop on flows to find the fakeGateway of this fakeEndEvent
				if( (f.getTarget() == fee) && (f.getSource() instanceof Gateway) ) {
					endFlow = f;
					g = (Gateway) f.getSource();
					for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : diagram.getFlows() ) //loop on flows to find all the fakeEndFlows
						if( ff.getTarget() == g ) {
							if( endEvents.contains(ff.getSource()) ) fakeFlows.add(ff);
							else {
								error = true;
								LOGGER.info("Fixing FEE: " + fee.getId());
								fixEndEvent(fee);
								break;
							}
						}

					if( error ) break;
				}

			if( g != null && !error ) {
					LOGGER.info("Removing FEE: " + fee.getId());
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> fff : fakeFlows) diagram.removeEdge(fff);
					diagram.removeEdge(endFlow);
					removeNode(g);
					removableFakeEnds.add(fee);
			}
		}
		for( Event rfe : removableFakeEnds ) removeNode(rfe);
	}

	private void tryToFixBound(Event e, Activity a, boolean increment) {

		if( !(e.getParentSubProcess() == a.getParentSubProcess()) ) return;
		for( Flow f : diagram.getFlows() )
			if( f.getTarget().equals(e) && (f.getSource() instanceof Gateway) ) {
				diagram.removeEdge(f);
				e.setExceptionFor(a);
				if( increment ) a.incNumOfBoundaryEvents();
				LOGGER.info("Boundary event restored: " + a.getId() + " > " + e.getId());
				return;
			}
	}

	private void fixStartEvent( Event fakeStart ) {
		Event e;
		for( BPMNNode n : matrices.keySet() )
			if( matrices.get(n) == fakeStart ) {
				e = (Event) n;
				e.setEventType(Event.EventType.INTERMEDIATE);

                if( blackList.containsKey(e) )
				    for( BPMNNode bde : blackList.get(e) )
					    if( bde instanceof Event ) ((Event)bde).setEventType(Event.EventType.INTERMEDIATE);

                if( whiteList.containsKey(e) )
				    for( BPMNNode wde : whiteList.get(e) )
					    if( wde instanceof Event ) ((Event)wde).setEventType(Event.EventType.INTERMEDIATE);
			}
	}

	private void fixEndEvent( Event fakeEnd ) {
		Event e;
		for( BPMNNode n : matrices.keySet() )
			if( matrices.get(n) == fakeEnd ) {
				e = (Event) n;
				e.setEventType(Event.EventType.INTERMEDIATE);

                if( blackList.containsKey(e) )
				    for( BPMNNode bde : blackList.get(e) )
					    if( bde instanceof Event ) ((Event)bde).setEventType(Event.EventType.INTERMEDIATE);

                if( whiteList.containsKey(e) )
				    for( BPMNNode wde : whiteList.get(e) )
					    if( wde instanceof Event ) ((Event)wde).setEventType(Event.EventType.INTERMEDIATE);
			}
	}


	private String bpStruct(String jsonProc) throws Exception {

		Process process = JSON2Process.convert(jsonProc);

		int gCounter = 0;
		for(de.hpi.bpt.process.Gateway g : process.getGateways() ) {
			if( g.getName().isEmpty() ) g.setName("gw" + gCounter++);
		}

		Restructurer restructurer = new Restructurer(process);
		if(restructurer.perform()) {
			return Process2JSON.convert(restructurer.proc);
		} else {
			return null;
		}
	}

	private void removeNode(BPMNNode n) {
		diagram.removeNode(n);
		if( n.getParentSubProcess() != null ) n.getParentSubProcess().getChildren().remove(n);
		if( n.getParentSwimlane() != null ) n.getParentSwimlane().getChildren().remove(n);
	}

	private class JsonProcess {
		private String jsonProcess;

		private JsonProcess(String name, Set<JsonTask> tasks, Set<JsonGateway> gateways, Set<JsonFlow> flows) {
			boolean first;
			Set<String>  duplicate = new HashSet<>();

			jsonProcess = "{\"name\":\"" + name + "\",\"gateways\":[";
			first = true;
			for(JsonGateway g : gateways) {
				if(!first) jsonProcess += ",";
				else first = false;
				jsonProcess += g.toString();
			}

			jsonProcess += "],\"tasks\":[";
			first = true;
			for(JsonTask t : tasks) {
				if(!first) jsonProcess += ",";
				else first = false;
				jsonProcess += t.toString();
			}

			jsonProcess += "],\"flows\":[";
			first = true;
			for(JsonFlow f : flows) {
				if(!duplicate.contains(f.toString())) duplicate.add(f.toString());
				else {
					LOGGER.info("JsonProcess => FOUND DUPLICATE FLOW: " + f.toString());
					continue;
				}
				if(!first) jsonProcess += ",";
				else first = false;
				jsonProcess += f.toString();
			}

			jsonProcess += "]}";
		}

		public void deleteORgates() {
			jsonProcess = jsonProcess.replaceAll("\"OR\"", "\"XOR\"");
		}

		@Override
		public String toString() {
			return jsonProcess;
		}
	}

	private class JsonTask {
		private UUID uid;
		private String jTask;

		private JsonTask(long id, String label) {
			this.uid = new UUID(id, id);
			jTask = new String("{\"id\":\"" + uid.toString() + "\",\"label\":\"" + label + "\"}");
		}

		@Override
		public String toString() {
			return jTask;
		}
	}

	private class JsonGateway {
		private String jGate;
		private UUID uid;

		private JsonGateway(long id, Gateway.GatewayType type) {
			String sType = "XOR";
			this.uid = new UUID(id, id);

			switch( type ) {
			case DATABASED:
			case EVENTBASED:
						sType = "XOR";
						break;
			case INCLUSIVE:
			case COMPLEX:
						sType = "OR";//XOR
						break;
			case PARALLEL:
						sType = "AND";
						break;
			}

			jGate = new String("{\"id\":\"" + uid.toString() + "\",\"type\":\"" + sType + "\"}");
		}
		
		@Override
		public String toString() {
			return jGate;
		}
	}
	
	private class JsonFlow {
		private String jFlow;
		
		private JsonFlow(String label, UUID srcUID, UUID tgtUID) {
			jFlow = new String("{\"label\":\"" + label + "\",\"src\":\"" + srcUID.toString() + "\",\"tgt\":\"" + tgtUID.toString() + "\"}");
		}
		
		@Override
		public String toString() {
			return jFlow;
		}
	}

}
