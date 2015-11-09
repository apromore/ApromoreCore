package org.apromore.service.impl;

import org.apromore.service.BPMNDiagramImporter;
import org.processmining.models.graphbased.NodeID;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
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
	private Set<NodeID> analyzedSubprocesses;

	/**** support maps to restore the diagram's edges correctly ****/
	private Map<Event, Activity> boundaryEvents;
	private Set<BPMNNode> endEvents;
	private Set<Event> fakeEndEvents;
	private Map<NodeID, Set<BPMNNode>> blackList;
	private Map<NodeID, Set<BPMNNode>> whiteList;
	private Map<NodeID, BPMNNode> matrices;

	/**** mapping between processes' IDs and their .json structured version ****/
	private Map<Long, String> idToJson;
	private Map<Long, SubProcess> standAloneSubprocesses;
	private Set<String> subversives;


	public StructuringServiceImpl() { }

	public StructuringServiceImpl(BPMNDiagram diagram) {
		this.diagram = diagram;
		taskCounter = 0;

		unmappableEdges = new HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>();
		unmappableNodes = new HashSet<BPMNNode>();

		mappedNodes = new HashMap<Long, BPMNNode>();
		analyzedSubprocesses = new HashSet<NodeID>();

		endEvents = new HashSet<BPMNNode>();
		fakeEndEvents = new HashSet<Event>();
		boundaryEvents = new HashMap<Event, Activity>();
		blackList = new HashMap<NodeID, Set<BPMNNode>>();
		whiteList = new HashMap<NodeID, Set<BPMNNode>>();
		matrices = new HashMap<NodeID, BPMNNode>();

		idToJson = new HashMap<Long, String>();
		standAloneSubprocesses = new HashMap<Long, SubProcess>();

		subversives = new HashSet<String>();

		structureDiagram();
	}

	public String checkXML(String xmlProcess) {
		String xmlProcessChecked = xmlProcess;
		String subversive;

		for( String s : subversives ) {
			subversive = s.replace("node ", "node_");
			xmlProcessChecked = xmlProcessChecked.replaceAll(subversive, "");
		}

		return xmlProcessChecked;
	}

	@Override
	public BPMNDiagram getStructuredDiagram() {	return diagram; }

	@Override
	public String structureBPMNModel(String xmlProcess) {
		UIContext context;
		UIPluginContext uiPluginContext;
		BPMNDiagramImporter diagramImporter = new BPMNDiagramImporterImpl();

		diagram = diagramImporter.importBPMNDiagram(xmlProcess);

		if( diagram == null ) {
			LOGGER.info("Import FAILED!");
			return xmlProcess;
		}

		LOGGER.info("Diagram parsed! Found: " + diagram.getNodes().size() + " nodes AND " + diagram.getFlows().size() + " flows.");

		taskCounter = 0;
		unmappableEdges = new HashSet<>();
		unmappableNodes = new HashSet<>();

		mappedNodes = new HashMap<>();
		analyzedSubprocesses = new HashSet<>();

		endEvents = new HashSet<>();
		fakeEndEvents = new HashSet<>();
		boundaryEvents = new HashMap<>();
		blackList = new HashMap<>();
		whiteList = new HashMap<>();
		matrices = new HashMap<>();

		idToJson = new HashMap<>();
		standAloneSubprocesses = new HashMap<>();

		subversives = new HashSet<>();
		structureDiagram();

		context = new UIContext();
		uiPluginContext = context.getMainPluginContext();
		BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, diagram);
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


		return checkXML(sb.toString());
	}


	private void structureDiagram() {

		Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edgeToRemove = new HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>();
		Set<BPMNNode> nodeToRemove = new HashSet<BPMNNode>();


		/**** STEP0: back up of all not mappable edges and nodes on the .json file ****/
		unmappableEdges.addAll(diagram.getAssociations());
		unmappableEdges.addAll(diagram.getDataAssociations());
		unmappableEdges.addAll(diagram.getMessageFlows());

		unmappableNodes.addAll(diagram.getTextAnnotations());
		unmappableNodes.addAll(diagram.getDataObjects());


		/** STEP1: restructure the diagram in order to remove
		 * - double edges
		 * - fakeGates (those gate with only 1 entering and 1 exiting flow)
		 * - double end places
		 * - boundary events
		 **/
		removeDoubleEdges();
		removeMultipleEndPlaces();
		handleBoundaryEvents();


		/**** STEP2: get all the structured version (as .json file) for each subProcess ****/
		//idToJson.put(taskCounter, generateJson(taskCounter, null, true));

		/**** variant with pool start ****/
		for( Swimlane pool : diagram.getPools() ) {
			LOGGER.info("Found Pool: " + pool.getLabel() + "[" + pool.getId() + "]");
			idToJson.put(taskCounter, generateJson(taskCounter, null, diagram.getFlows(pool), true));
			taskCounter++;
		}
		idToJson.put(taskCounter, generateJson(taskCounter, null, diagram.getFlows((Swimlane) null), true));
		/**** variant with pool ends ****/

		for(SubProcess sp : diagram.getSubProcesses()) {
			if( !analyzedSubprocesses.contains(sp.getId()) ) {
				taskCounter++;
				standAloneSubprocesses.put(taskCounter, sp);
				idToJson.put(taskCounter, generateJson(taskCounter, sp, diagram.getFlows(sp), false));
			}
		}


		/**** STEP3: remove all the edges and not mappable nodes from the diagram ****/
		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getEdges() ) edgeToRemove.add(e);
		for( Gateway g : diagram.getGateways() ) nodeToRemove.add(g);
		for( TextAnnotation ta : diagram.getTextAnnotations() ) nodeToRemove.add(ta);
		for( DataObject d : diagram.getDataObjects() ) nodeToRemove.add(d);

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : edgeToRemove ) diagram.removeEdge(e);
		for( BPMNNode n : nodeToRemove ) {
			LOGGER.info("Removing node: " + n.getId());
			diagram.removeNode(n);
			subversives.add("<\\w+ id=\"" + n.getId() + "\" name=\"" + n.getLabel() + "\"/>\n");
		}


		/**** STEP4: reconnect all the elements inside the diagram exploiting .json files and support maps ****/
		for( Long id : (new TreeSet<>(standAloneSubprocesses.keySet())).descendingSet() )
			if( rebuildProcess( id, standAloneSubprocesses.get(id)) ) LOGGER.info("Error rebuilding the process!");

		if( rebuildProcess((long) 0, null) )
			LOGGER.info("Error rebuilding the process!");


		/**** STEP5: restore edges and boundaryEvents where feasible ****/
		restoreEdges();
		restoreEvents();

	}

	private void removeDoubleEdges() {
		HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> towAway = new HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>();
		HashMap<BPMNNode, HashSet<BPMNNode>> flows = new HashMap<BPMNNode, HashSet<BPMNNode>>();

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getFlows() ) {
			if( !flows.containsKey(f.getSource()) ) {
				flows.put( f.getSource(), new HashSet<BPMNNode>() );
				flows.get(f.getSource()).add(f.getTarget());
			} else {
				if( flows.get(f.getSource()).contains(f.getTarget()) ) towAway.add(f);
				else flows.get(f.getSource()).add(f.getTarget());
			}
		}

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : towAway ) {
			LOGGER.info("DoubleFlow removed: " + ff.getSource().getId() + " => " + ff.getTarget().getId());
			diagram.removeEdge(ff);
			if( ff.getSource() instanceof Gateway )	checkFakeGateway((Gateway) ff.getSource());
			if( ff.getTarget() instanceof Gateway )	checkFakeGateway((Gateway) ff.getTarget());
		}
	}

	private void checkFakeGateway(Gateway g) {
		LOGGER.info("Checking fake gateways: " + g.getId());
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
			LOGGER.info("Found a fake gate: " + g.getLabel() + "[" + g.getId() + "]");
			diagram.removeEdge(in);
			diagram.removeEdge(out);
			diagram.addFlow(in.getSource(), out.getTarget(), "");
			diagram.removeGateway(g);
			subversives.add("<\\w+ id=\"" + g.getId() + "\" name=\"" + g.getLabel() + "\"/>\n");
		}
	}

	private void removeMultipleEndPlaces() {
		HashMap<SubProcess, HashSet<Event>> sp2ee = new HashMap<SubProcess, HashSet<Event>>();
		Gateway g = null;
		Event fakeEnd = null;

		for( SubProcess sp : diagram.getSubProcesses() ) sp2ee.put(sp, new HashSet<Event>());
		sp2ee.put(null, new HashSet<Event>()); //adding topLevel Process

		/* adding each endEvent to the right set */
		for( Event e : diagram.getEvents() )
			if( e.getEventType() == Event.EventType.END ) sp2ee.get(e.getParentSubProcess()).add(e);

		/* for all those processes with multiple endEvents it is created a fakeEndPoint */
		for( SubProcess spe : sp2ee.keySet() ) {
			if( sp2ee.get(spe).size() > 1 ) {

				g = diagram.addGateway("XOR_fakeEnd", Gateway.GatewayType.DATABASED, spe);
				LOGGER.info("Pushed NEW Gateway!");

				for( Event e : sp2ee.get(spe) ) {
					diagram.addFlow(e, g, "fake_endFlow");
					endEvents.add(e);
				}

				fakeEnd = diagram.addEvent("fakeEnd", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.CATCH, spe, true, null);
				fakeEndEvents.add(fakeEnd);
				diagram.addFlow(g, fakeEnd, "toFakeEnd");
				LOGGER.info("Added NEW FakeEnd : " + fakeEnd.getId());
			}
		}
	}

	private void handleBoundaryEvents() {
		Activity a;
		Gateway g;
		BPMNNode tgt;

		for( Event e : diagram.getEvents() )
			if( (a = e.getBoundingNode()) != null )
				for( Flow f : diagram.getFlows() )
					if( f.getSource().equals(a) ) {
						tgt = f.getTarget();
						if( (tgt instanceof Gateway) && ((((Gateway) tgt).getGatewayType() == Gateway.GatewayType.EVENTBASED) ||
					 			 						 (((Gateway) tgt).getGatewayType() == Gateway.GatewayType.DATABASED)) )
						{
					 			g = (Gateway) tgt;
						} else {
								g = pushGateway("XOR", a.getParentSubProcess());
								diagram.removeEdge(f);
								diagram.addFlow(a, g, "");
								diagram.addFlow(g, tgt, "");
						}
						diagram.addFlow(g, e, "");
						e.setParentSubprocess(g.getParentSubProcess());
						boundaryEvents.put(e, a);
						LOGGER.info("Boundary Event Found for Activity:Event - " + a.getLabel() + ":" + e.getLabel());
						break;
					}
	}

	private String generateJson(long processID, SubProcess parentProcess, Collection<Flow> children, boolean recursive) {

		Map<NodeID, UUID> processedNodes = new HashMap<>();
		Map<Long, SubProcess> subProcesses = new HashMap<>();

		Set<JsonTask> tasks = new HashSet<>();
		Set<JsonGateway> gateways = new HashSet<>();
		Set<JsonFlow> flows = new HashSet<>();
		JsonTask jTask;
		JsonGateway jGate;
		JsonFlow jFlow;
		JsonProcess jProcess;

		String jResponse;

		BPMNNode src, tgt;
		UUID srcUID, tgtUID;


		for( Flow flow : children ) {

			src = flow.getSource();
			tgt = flow.getTarget();

			/**** we cannot map these elements within the json scheme and so neither the current flow ****/
			if( src instanceof DataObject || src instanceof Swimlane || src instanceof TextAnnotation ||
				tgt instanceof DataObject || tgt instanceof Swimlane || tgt instanceof TextAnnotation ) {
					unmappableEdges.add(flow);
					continue;
			}

			/**** checking whether srcNode and tgtNode have been already processed before ****/
			srcUID = processedNodes.get(src.getId());
			tgtUID = processedNodes.get(tgt.getId());

			/**** first time we meet this tgtNode ****/
			if( tgtUID == null ) {
				taskCounter++;
				if(tgt instanceof Gateway) {
					jGate = new JsonGateway(taskCounter, ((Gateway) tgt).getGatewayType());
					gateways.add(jGate);
					tgtUID = jGate.uid;
					LOGGER.info("[" + Long.toString(processID) + "] added Gate: " + jGate.toString());
					LOGGER.info("[" + Long.toString(processID) + "] added Gate: " + tgt.getId());
				} else {
					jTask = new JsonTask(taskCounter, Long.toString(taskCounter));
					tasks.add(jTask);
					tgtUID = jTask.uid;
					if( (tgt instanceof SubProcess) && recursive) {
						subProcesses.put(taskCounter, (SubProcess) tgt);
						LOGGER.info("[" + Long.toString(processID) + "] added SubProcess: " + jTask.toString());
					} else {
						LOGGER.info("[" + Long.toString(processID) + "] added Task: " + jTask.toString());
					}
				}
				processedNodes.put(tgt.getId(), tgtUID);
				mappedNodes.put(taskCounter, tgt);
			}

			/**** first time we meet this srcNode ****/
			if( srcUID == null ) {
				taskCounter++;
				if(src instanceof Gateway) {
					jGate = new JsonGateway(taskCounter, ((Gateway) src).getGatewayType());
					gateways.add(jGate);
					srcUID = jGate.uid;
					LOGGER.info("[" + Long.toString(processID) + "] added Gate: " + jGate.toString());
				} else {
					jTask = new JsonTask(taskCounter, Long.toString(taskCounter));
					tasks.add(jTask);
					srcUID = jTask.uid;
					if( (src instanceof SubProcess) && recursive ) {
						subProcesses.put(taskCounter, (SubProcess) src);
						LOGGER.info("[" + Long.toString(processID) + "] added SubProcess: " + jTask.toString());
					} else {
						LOGGER.info("[" + Long.toString(processID) + "] added Task: " + jTask.toString());
					}
				}
				processedNodes.put(src.getId(), srcUID);
				mappedNodes.put(taskCounter, src);
			}

			jFlow = new JsonFlow(flow.getLabel(), srcUID, tgtUID);
			flows.add(jFlow);
			LOGGER.info("[" + Long.toString(processID) + "] added Flow: " + jFlow.toString());
		}

		jProcess = new JsonProcess(Long.toString(processID), tasks, gateways, flows);

		try {
			System.out.println("Process:" + jProcess.toString());

			jResponse = bpStruct(jProcess.toString());
			if( jResponse == null ) throw new Exception("Process NULL.");

			System.out.println("Response:" + jResponse);

		} catch (Exception e) {
			System.out.println("Exception [" + e.getClass().getName() + "] for process: " + Long.toString(processID) + "\t");
			e.printStackTrace(System.out);
			jResponse = jProcess.toString();
			System.out.println("Response [COPY of Process]:" + jResponse);
		}

		/**** for each subProcess discovered inside this processed subProcess we call recursively this method ****/
		for( Long subprocessID : subProcesses.keySet() )
			idToJson.put(subprocessID, generateJson(subprocessID, subProcesses.get(subprocessID), diagram.getFlows(subProcesses.get(subprocessID)), recursive));

		if(parentProcess != null) analyzedSubprocesses.add(parentProcess.getId());
		return jResponse;
	}

	private boolean rebuildProcess(long processID, SubProcess parentProcess) {
		LOGGER.info("Rebuilding subProcess: " + processID);
		Set<Long> greyList = new HashSet<Long>();
		Map<String, BPMNNode> idToNode = new HashMap<String, BPMNNode>();

    	JSONObject jsonProcessObject;
    	JSONArray tasks, gateways, flows;
    	JSONObject o;
		int i = 0;

		Flow flow;
		BPMNNode node, src, tgt;
		String nodeUID, srcUID, tgtUID;
		long taskID;

		boolean error = false;

		try {
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
    					if( node == null ) return true;
    				} else {
    					/* the first time we encounter a node we retrieve the original one and mark it in the greyList */
    					node = mappedNodes.get(taskID);
    					greyList.add(taskID);

    					/*
    					 * if node is a subProcess we must rebuild it before continue,
    					 * because if accidently BPstruct has duplicated it while structuring
    					 * @parentProcess we will find a duplicate of this node and we should duplicate it,
    					 * but we could not perform a duplication if the subProcess has not been rebuilt yet.
    					 * In this way recursively we will rebuild all the subProcesses.
    					 */
    					if( node instanceof SubProcess ) error = rebuildProcess(taskID, (SubProcess) node);
    					if( error ) return error;
    				}
    			} else throw new JSONException("errore: parsing tasks.");

    			idToNode.put(nodeUID, node);
    		}

    		/** pushing gateways **/
            gateways = jsonProcessObject.getJSONArray("gateways");
            for( i = 0; i < gateways.length(); i++ ) {
            	o = gateways.getJSONObject(i);
    			nodeUID = o.getString("id");
				node = pushGateway(o.getString("type"), parentProcess);
				if( node == null ) return true;
				idToNode.put(nodeUID, node);
    		}

    		/** applying mapped flows **/
            flows = jsonProcessObject.getJSONArray("flows");
            for( i = 0; i < flows.length(); i++ ) {
            	o = flows.getJSONObject(i);
    			srcUID = o.getString("src");
    			tgtUID = o.getString("tgt");

    			if( idToNode.containsKey(srcUID) ) src = idToNode.get(srcUID);
    			else throw new JSONException("errore: parsing flows. Source not found: " + srcUID);

    			if( idToNode.containsKey(tgtUID) ) tgt = idToNode.get(tgtUID);
    			else throw new JSONException("errore: parsing flows. Target not found: " + tgtUID);

    			flow = diagram.addFlow(src, tgt, o.getString("label"));
				flow.setParent(parentProcess);
    		}

		} catch(Exception e) {
			LOGGER.error("Error rebuilding subProcess: " + processID, e);
			error = true;
		}

		return error;
	}

	private Gateway pushGateway(String gateType, SubProcess parentProcess) {

		if(gateType.equalsIgnoreCase("xor")) return diagram.addGateway("", Gateway.GatewayType.DATABASED, parentProcess );
		else if(gateType.equalsIgnoreCase("or")) return diagram.addGateway("", Gateway.GatewayType.INCLUSIVE, parentProcess );
		else if( gateType.equalsIgnoreCase("and")) return diagram.addGateway("", Gateway.GatewayType.PARALLEL, parentProcess );
		return null;
	}

	private BPMNNode duplicateNode(BPMNNode node, SubProcess parentProcess, boolean blackMark) {
		BPMNNode duplicate = null;
		NodeID nodeID = node.getId();


		if( blackMark ) {
			if( !blackList.containsKey(nodeID) ) blackList.put(nodeID, new HashSet<BPMNNode>());
		} else {
			if( !whiteList.containsKey(nodeID) ) whiteList.put(nodeID, new HashSet<BPMNNode>());
		}

		if( node instanceof SubProcess ) {
			Flow flow;
			BPMNNode src, tgt;
			boolean mark;

			duplicate = diagram.addSubProcess( ((SubProcess) node).getLabel(),
											   ((SubProcess) node).isBLooped(),
											   ((SubProcess) node).isBAdhoc(),
											   ((SubProcess) node).isBCompensation(),
											   ((SubProcess) node).isBMultiinstance(),
											   ((SubProcess) node).isBCollapsed(),
											   ((SubProcess) node).getTriggeredByEvent(),
											   parentProcess);

			((SubProcess) duplicate).setParentSwimlane(((SubProcess) node).getParentSwimlane());
			((SubProcess) duplicate).setDecorator(((SubProcess) node).getDecorator());

			for( ContainableDirectedGraphElement e : ((SubProcess) node).getChildren() ) {
				if( e instanceof Flow )	{
					flow = ((Flow) e);
					src = flow.getSource();
					tgt = flow.getTarget();

					/* checking if these nodes have been duplicated or not */
					mark = false;
					if( matrices.containsKey(src.getId()) ) {
						src = matrices.get(src.getId());
						mark = blackList.containsKey(src.getId());
					}
					src = duplicateNode(src, (SubProcess) duplicate, mark);

					mark = false;
					if( matrices.containsKey(tgt.getId()) ) {
						tgt = matrices.get(tgt.getId());
						mark = blackList.containsKey(tgt.getId());
					}
					tgt = duplicateNode(tgt, (SubProcess) duplicate, mark);

					if( src == null || tgt == null ) return null;

	    			flow = diagram.addFlow( src, tgt, flow.getLabel() );
	    			flow.setParent((SubProcess) duplicate);
				}
			}
		} else if( node instanceof Activity ) {
			duplicate = diagram.addActivity(((Activity) node).getLabel(),
											((Activity) node).isBLooped(),
											((Activity) node).isBAdhoc(),
											((Activity) node).isBCompensation(),
											((Activity) node).isBMultiinstance(),
											((Activity) node).isBCollapsed(),
											 parentProcess);

			((Activity) duplicate).setParentSwimlane(((Activity) node).getParentSwimlane());
			((Activity) duplicate).setDecorator(((Activity) node).getDecorator());

		} else if( node instanceof CallActivity ) {
			duplicate = diagram.addCallActivity(((CallActivity) node).getLabel(),
												((CallActivity) node).isBLooped(),
												((CallActivity) node).isBAdhoc(),
												((CallActivity) node).isBCompensation(),
												((CallActivity) node).isBMultiinstance(),
												((CallActivity) node).isBCollapsed(),
												 parentProcess);

			((CallActivity) duplicate).setParentSwimlane(((CallActivity) node).getParentSwimlane());
			((CallActivity) duplicate).setDecorator(((CallActivity) node).getDecorator());

		} else if( node instanceof Event ) {
			duplicate = diagram.addEvent(((Event) node).getLabel(),
										 ((Event) node).getEventType(),
										 ((Event) node).getEventTrigger(),
										 ((Event) node).getEventUse(),
										 parentProcess,
										 true,
										 null);

			((Event) duplicate).setParentSwimlane(((Event) node).getParentSwimlane());
			((Event) duplicate).setDecorator(((Event) node).getDecorator());

			if( endEvents.contains((Event) node) ) endEvents.add((Event) duplicate);
			/* this should happen only during a subProcess duplication, because BPstruct is supposed to do not duplicate end Events */
			if( fakeEndEvents.contains((Event) node) ) fakeEndEvents.add((Event) duplicate);

		} else if( node instanceof Gateway ) {
			duplicate = diagram.addGateway( ((Gateway) node).getLabel(),
											((Gateway) node).getGatewayType(),
											parentProcess);

			//((Gateway) duplicate).setParentSwimlane(((Gateway) node).getParentSwimlane());
			((Gateway) duplicate).setMarkerVisible(((Gateway) node).isMarkerVisible());
			((Gateway) duplicate).setDecorator(((Gateway) node).getDecorator());
		}

		if( duplicate != null ) {
			if(blackMark) blackList.get(nodeID).add(duplicate);
			else whiteList.get(nodeID).add(duplicate);

			matrices.put(duplicate.getId(), node);
		}

		return duplicate;
	}

	private void restoreEdges() {
		BPMNNode src, tgt, node;
		NodeID srcID, tgtID;
		Set<BPMNNode> restorableNodes = new HashSet<BPMNNode>();

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : unmappableEdges ) {
			src = e.getSource();
			tgt = e.getTarget();
			srcID = src.getId();
			tgtID = tgt.getId();

			if( blackList.containsKey(srcID) || blackList.containsKey(tgtID) ||
				whiteList.containsKey(srcID) || whiteList.containsKey(tgtID) ) continue;

			if( unmappableNodes.contains(src) ) {
				unmappableNodes.remove(src);
				restorableNodes.add(src);
			}

			if( unmappableNodes.contains(tgt) ) {
				unmappableNodes.remove(tgt);
				restorableNodes.add(tgt);
			}

			if( e instanceof MessageFlow ) {
				if( ((MessageFlow) e).getParentSubProcess() != null )
					diagram.addMessageFlow(src, tgt, ((MessageFlow) e).getParentSubProcess(), e.getLabel());
				else
					diagram.addMessageFlow(src, tgt, ((MessageFlow) e).getParentSwimlane(), e.getLabel());
			}
			else if( e instanceof Flow ) (diagram.addFlow(src, tgt, e.getLabel())).setParent(e.getParent());
			else if( e instanceof Association ) (diagram.addAssociation(src, tgt, ((Association) e).getDirection())).setParent(e.getParent());
			else if( e instanceof DataAssociation ) (diagram.addDataAssociation(src, tgt, e.getLabel())).setParent(e.getParent());

			LOGGER.info("Edge restored: " + src.getLabel() + " => " + tgt.getLabel());
		}

		for( BPMNNode n : restorableNodes ) {
			node = null;

			if( n instanceof TextAnnotation ) node = diagram.addTextAnnotations((TextAnnotation) n);
			else if( n instanceof DataObject ) node = diagram.addDataObject(n.getLabel());

			if( node != null ) {
				node.setParentSubprocess(n.getParentSubProcess());
				node.setParentSwimlane(n.getParentSwimlane());
			}
		}
	}

	private void restoreEvents() {
		Activity a;
		Gateway g;
		boolean error;
		Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> fakeFlows;
		Set<Event> removableFakeEnds = new HashSet<Event>();

		/* Restoring boundary events */
		for( Event e : boundaryEvents.keySet() ) {
			a = boundaryEvents.get(e);
			if( !blackList.containsKey(a.getId()) && !blackList.containsKey(e.getId()) &&
				!whiteList.containsKey(a.getId()) && !whiteList.containsKey(e.getId()) ) {
				tryToFixBound(e, a, false);
			} else {
				e.setExceptionFor(null);
				continue;
			}

			if( whiteList.containsKey(a.getId()) && whiteList.containsKey(e.getId()) ) {
				tryToFixBound(e, a, false);
				for( BPMNNode en : whiteList.get(e.getId()) )
					for( BPMNNode an : whiteList.get(a.getId()) ) tryToFixBound((Event) en, (Activity) an, true);
			}
		}


		/* Restoring multiple end Events */
		for( Event fee : fakeEndEvents ) {
			LOGGER.info("FakeEnd : " + fee.getId());
			g = null;
			error = false;
			fakeFlows = new HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>();

			for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getFlows() ) //loop on flows to find the fakeGateway of this fakeEndEvent
				if( (f.getTarget() == fee) && (f.getSource() instanceof Gateway) ) {
					g = (Gateway) f.getSource();
					for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : diagram.getFlows() ) { //loop on flows to find all the fakeEndFlows
						if( ff.getTarget() == g ) {
							if( endEvents.contains(ff.getSource()) ) fakeFlows.add(ff);
							else error = true;
						}
					}
					break;
				}

			if( g != null ) {
				if (!error) {
					LOGGER.info("[REMOVING][NO ERRORS] for : " + fee.getId());
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> fff : fakeFlows) diagram.removeEdge(fff);
					diagram.removeGateway(g);
					subversives.add("<\\w+ id=\"" + g.getId() + "\" name=\"" + g.getLabel() + "\"/>\n");
					removableFakeEnds.add(fee);
				} else {
					LOGGER.info("[FIXING][ERRORS] for : " + fee.getId());
					fixEndPoints(fakeFlows);
				}
			}
		}

		for( Event rfe : removableFakeEnds ) {
			diagram.removeEvent(rfe);
			subversives.add("<\\w+ id=\"" + rfe.getId() + "\" name=\"" + rfe.getLabel() + "\"/>\n");
		}

	}

	private void tryToFixBound(Event e, Activity a, boolean increment) {
		Gateway g;
		BPMNNode tgt = null;
		BPMNNode src = null;
		Flow in = null;
		Flow out = null;
		long outgoing = 0;
		long incoming = 0;

		if( !(e.getParentSubProcess() == a.getParentSubProcess()) ) return;

		for(Flow f : diagram.getFlows()) {

			if( f.getTarget().equals(e) && (f.getSource() instanceof Gateway) ) {

				g = (Gateway) f.getSource();
				diagram.removeEdge(f);

				for(Flow ff : diagram.getFlows()) {
					if( ff.getSource().equals(g) ) {
						outgoing++;
						out = ff;
						tgt = out.getTarget();
					}

					if( ff.getTarget().equals(g) ) {
						incoming++;
						in = ff;
						src = ff.getSource();
					}
				}

				if( (outgoing == 1) && (incoming == 1) )
				{
					diagram.addFlow(src, tgt, "");
					diagram.removeEdge(in);
					diagram.removeEdge(out);
					diagram.removeGateway(g);
					subversives.add("<\\w+ id=\"" + g.getId() + "\" name=\"" + g.getLabel() + "\"/>\n");
					LOGGER.info("Removed Boundary event gateway: " + e.getLabel() );
				}

				e.setExceptionFor(a);
				if( increment ) a.incNumOfBoundaryEvents();
				LOGGER.info("Event restored: " + e.getLabel() + " => " + a.getLabel());
				return;
			}
		}
	}

	private void fixEndPoints( Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> fakeFlows ) {
		Activity a;

		for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : fakeFlows ) {
			a = diagram.addActivity( "originalEndPoint: " + f.getSource().getLabel(), false, false, false, false, false, f.getSource().getParentSubProcess() );
			diagram.removeEdge(f);
			diagram.removeEvent( (Event) f.getSource());
			subversives.add("<\\w+ id=\"" + f.getSource().getId() + "\" name=\"" + f.getSource().getLabel() + "\"/>\n");
			diagram.addFlow(a, f.getTarget(), "fakeFlow");
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

	private class JsonProcess {
		private String jsonProcess;

		private JsonProcess(String name, Set<JsonTask> tasks, Set<JsonGateway> gateways, Set<JsonFlow> flows) {
			boolean first;
			Set<String>  duplicate = new HashSet<String>();

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
