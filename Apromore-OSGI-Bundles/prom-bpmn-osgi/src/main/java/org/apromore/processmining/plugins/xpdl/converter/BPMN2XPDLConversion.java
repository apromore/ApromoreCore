package org.apromore.processmining.plugins.xpdl.converter;

//import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.processmining.framework.plugin.PluginContext;
//import org.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
//import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
//import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
//import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
//import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
//import org.processmining.models.graphbased.directed.bpmn.elements.Event;
//import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
//import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
//import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
//import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
//import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
//import org.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;
//import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
//import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
//import org.processmining.models.jgraph.ProMGraphModel;
//import org.processmining.models.jgraph.ProMJGraphVisualizer;
//import org.processmining.models.jgraph.elements.ProMGraphCell;
//import org.processmining.models.jgraph.elements.ProMGraphEdge;
//import org.processmining.models.jgraph.views.JGraphPortView;
//import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
//import org.processmining.plugins.xpdl.Xpdl;
//import org.processmining.plugins.xpdl.XpdlAuthor;
//import org.processmining.plugins.xpdl.XpdlBlockActivity;
//import org.processmining.plugins.xpdl.XpdlCreated;
//import org.processmining.plugins.xpdl.XpdlEndEvent;
//import org.processmining.plugins.xpdl.XpdlEvent;
//import org.processmining.plugins.xpdl.XpdlImplementation;
//import org.processmining.plugins.xpdl.XpdlIntermediateEvent;
//import org.processmining.plugins.xpdl.XpdlJoin;
//import org.processmining.plugins.xpdl.XpdlPackageHeader;
//import org.processmining.plugins.xpdl.XpdlProcessHeader;
//import org.processmining.plugins.xpdl.XpdlRedefinableHeader;
//import org.processmining.plugins.xpdl.XpdlRoute;
//import org.processmining.plugins.xpdl.XpdlSplit;
//import org.processmining.plugins.xpdl.XpdlStartEvent;
//import org.processmining.plugins.xpdl.XpdlTask;
//import org.processmining.plugins.xpdl.XpdlTransitionRestriction;
//import org.processmining.plugins.xpdl.XpdlTriggerResultMessage;
//import org.processmining.plugins.xpdl.collections.XpdlActivities;
//import org.processmining.plugins.xpdl.collections.XpdlActivitySets;
//import org.processmining.plugins.xpdl.collections.XpdlArtifacts;
//import org.processmining.plugins.xpdl.collections.XpdlAssociations;
//import org.processmining.plugins.xpdl.collections.XpdlLanes;
//import org.processmining.plugins.xpdl.collections.XpdlMessageFlows;
//import org.processmining.plugins.xpdl.collections.XpdlPools;
//import org.processmining.plugins.xpdl.collections.XpdlTransitionRefs;
//import org.processmining.plugins.xpdl.collections.XpdlTransitionRestrictions;
//import org.processmining.plugins.xpdl.collections.XpdlTransitions;
//import org.processmining.plugins.xpdl.collections.XpdlWorkflowProcesses;
//import org.processmining.plugins.xpdl.graphics.XpdlConnectorGraphicsInfo;
//import org.processmining.plugins.xpdl.graphics.XpdlCoordinates;
//import org.processmining.plugins.xpdl.graphics.XpdlNodeGraphicsInfo;
//import org.processmining.plugins.xpdl.graphics.collections.XpdlConnectorGraphicsInfos;
//import org.processmining.plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;
//import org.processmining.plugins.xpdl.idname.XpdlActivity;
//import org.processmining.plugins.xpdl.idname.XpdlActivitySet;
//import org.processmining.plugins.xpdl.idname.XpdlLane;
//import org.processmining.plugins.xpdl.idname.XpdlMessageFlow;
//import org.processmining.plugins.xpdl.idname.XpdlMessageType;
//import org.processmining.plugins.xpdl.idname.XpdlPool;
//import org.processmining.plugins.xpdl.idname.XpdlTransition;
//import org.processmining.plugins.xpdl.idname.XpdlTransitionRef;
//import org.processmining.plugins.xpdl.idname.XpdlWorkflowProcess;
//import org.processmining.plugins.xpdl.text.XpdlDescription;
//import org.processmining.plugins.xpdl.text.XpdlDocumentation;
//import org.processmining.plugins.xpdl.text.XpdlPriority;
//import org.processmining.plugins.xpdl.text.XpdlText;
//import org.processmining.plugins.xpdl.text.XpdlVendor;
//import org.processmining.plugins.xpdl.text.XpdlVersion;
//import org.processmining.plugins.xpdl.text.XpdlXpdlVersion;
//
//public class BPMN2XPDLConversion {
//	
//	public static final String ID_DEFAULT_POOL = "Default-Pool";
//	public static final String ID_MAIN_PROCESS_WF = "MainProcess-WF_";
//	public static final String ID_PROCESS_WF = "Process-WF_";
//	public static final String ID_XPDL = "XPDL_";
//	public static final String ID_SUB_PROCESS_SUBWF = "subProcId";
//	
//	private static final List<EventTrigger> listOfXpdlEndTrigers = Arrays.asList(EventTrigger.NONE,
//			EventTrigger.MESSAGE, EventTrigger.ERROR, EventTrigger.CANCEL, 
//			EventTrigger.COMPENSATION, EventTrigger.SIGNAL, EventTrigger.TERMINATE, 
//			EventTrigger.MULTIPLE);  
//	
//	private BPMNDiagram bpmn;
//	private Xpdl xpdl;
//	
//	public BPMN2XPDLConversion(BPMNDiagram bpmn) {
//		this.bpmn=bpmn;
//	}
//	
//	public Xpdl convert2XPDL_noLayout() {
//		xpdl = fillXPDL(null);
//		return xpdl;
//	}
//	
//	public Xpdl convert2XPDL(PluginContext context) {
//		xpdl = fillXPDL(context);
//		return xpdl;
//	}
//	
//	/**
//	 * Convert BPMN diagram to XPDL without layout information
//	 * @return XPDL model
//	 */
////	public Xpdl convert2XPDL_noLayout(AbstractPluginContext context) {
////
////		fillXpdlActivities(context);
////		fillXpdlTransitions(context);
//////		fillSwimlanes();
////		return xpdl;
////	}
//
////	private void fillSwimlanes() {
////		// Create pool for each workflow process according to the XPDL specification
////		for(XpdlWorkflowProcess process : xpdl.getWorkflowProcesses().getList()) {
////			fillSwimlane(process);
////		}
////	}
//	
////	private void fillSwimlane(XpdlWorkflowProcess process) {
////		List<XpdlActivity> activityList = process.getActivities().getList();
////
//////		Map<String, String> lanes = new HashMap<String, String>();
//////		for (XpdlActivity activity : activityList) {
//////			
//////			if (bpmnNode.getParentLane() != null) {
//////				lanes.put("" + bpmnNode.hashCode(), "" + bpmnNode.getParentLane().hashCode());
//////			}
//////		}
////		Swimlane bpmnPool = retrievePool(bpmn);
////		XpdlPools xpdlPools = xpdl.getPools();
////		XpdlPool xpdlPool = new XpdlPool("Pool");
////		xpdlPool.setId(bpmnPool.getId().toString().replace(' ', '_'));
////		xpdlPool.setBoundaryVisible("false");
////		xpdlPool.setProcess(xpdl.getWorkflowProcesses().getList().get(0).getId());
////		xpdlPool.setMainPool("true");
////
////		XpdlLanes xpdlLanes = new XpdlLanes("Lanes");
////
////		Set<Swimlane> bpmnLanes = retrieveLanes(bpmn, bpmnPool);
////		for (Swimlane swimlane : bpmnLanes) {
////			XpdlLane lane = new XpdlLane("Lane");
////			lane.setParentPool(xpdlPool.getId());
////			lane.setName(swimlane.getLabel());
////			lane.setId("" + swimlane.hashCode());
////			xpdlLanes.add2List(lane);
////		}
////		xpdlPool.setLanes(xpdlLanes);
////
////		xpdlPools.add2List(xpdlPool);
////
////		for (XpdlActivity xpdlActivity : activityList) {
////			if (xpdlActivity.getNodeGraphicsInfos() != null) {
////				List<XpdlNodeGraphicsInfo> infos = xpdlActivity.getNodeGraphicsInfos().getList();
////				for (XpdlNodeGraphicsInfo xpdlNodeGraphicsInfo : infos) {
////					xpdlNodeGraphicsInfo.setLaneId(map3.get(xpdlActivity.getId()));
////				}
////			}
////		}
////	}
//
////	/**
////	 * @param bpmnDiagram
////	 * @return
////	 */
////	private Swimlane retrievePool(BPMNDiagram bpmnDiagram) {
////		for (Swimlane swimlane : bpmnDiagram.getSwimlanes()) {
////			if (swimlane.getParentSwimlane() == null) {
////				return swimlane;
////			}
////		}
////		return null;
////	}
////	
/////**
//// * 
//// * @param pool
//// * @return
//// */
////	private Set<Swimlane> retrieveLanes(BPMNDiagram diagram, Swimlane pool) {
////		Set<Swimlane> lanes = new HashSet<Swimlane>();
////		for (Swimlane swimlane : diagram.getSwimlanes()) {
////			Swimlane parent = swimlane.getParentSwimlane();
////			if (parent != null && parent.equals(pool)) {
////				lanes.add(swimlane);
////			}
////		}
////		return lanes;
////	}
//
////	/**
////	 * @param context
////	 * @param bpmn
////	 * @param xpdl
////	 */
////	private void fillXpdlActivityPositions(PluginContext context) {
////		ProMJGraphPanel graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmn);
////		ProMGraphModel graphModel = graphPanel.getGraph().getModel();
////
////		@SuppressWarnings("rawtypes")
////		List proMGraphCellList = graphModel.getRoots();
////		XpdlWorkflowProcess workflowProcess = xpdl.getWorkflowProcesses().getList().get(0);
////		Map<String, XpdlActivity> map = new HashMap<String, XpdlActivity>();
////		for (XpdlActivity xpdlActivity : workflowProcess.getActivities().getList()) {
////			map.put(xpdlActivity.getId(), xpdlActivity);
////		}
////		System.out.println("");
////		for (Object object : proMGraphCellList) {
////			if (object instanceof ProMGraphCell) {
////				ProMGraphCell proMGraphCell = (ProMGraphCell) object;
////				XpdlActivity xpdlActivity = map.get("" + proMGraphCell.hashCode());
////				if (xpdlActivity != null) {
////					Rectangle2D rectangle = proMGraphCell.getView().getBounds();
////					XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");
////
////					XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
////					nodeGraphicsInfo.setToolId("ProM");
////
////					XpdlCoordinates coordinates = new XpdlCoordinates("Coordinates");
////
////					nodeGraphicsInfo.setHeight("" + (int) rectangle.getHeight());
////					nodeGraphicsInfo.setWidth("" + (int) rectangle.getWidth());
////
////					coordinates.setxCoordinate("" + (int) rectangle.getX());
////					coordinates.setyCoordinate("" + (int) rectangle.getY());
////					nodeGraphicsInfo.setCoordinates(coordinates);
////
////					nodeGraphicsInfos.add2List(nodeGraphicsInfo);
////					xpdlActivity.setNodeGraphicsInfos(nodeGraphicsInfos);
////				} else {
////					@SuppressWarnings("unchecked")
////					List<Object> cells = proMGraphCell.getChildren();
////					for (Object object2 : cells) {
////						if (object2 instanceof ProMGraphCell) {
////							ProMGraphCell proMGraphCell2 = (ProMGraphCell) object2;
////							XpdlActivity xpdlActivity2 = map.get("" + proMGraphCell2.hashCode());
////							if (xpdlActivity2 != null) {
////								Rectangle2D rectangle = proMGraphCell2.getView().getBounds();
////								XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");
////
////								XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
////								nodeGraphicsInfo.setToolId("ProM");
////
////								XpdlCoordinates coordinates = new XpdlCoordinates("Coordinates");
////
////								nodeGraphicsInfo.setHeight("" + (int) rectangle.getHeight());
////								nodeGraphicsInfo.setWidth("" + (int) rectangle.getWidth());
////
////								coordinates.setxCoordinate("" + (int) rectangle.getX());
////								coordinates.setyCoordinate("" + (int) rectangle.getY());
////								nodeGraphicsInfo.setCoordinates(coordinates);
////
////								nodeGraphicsInfos.add2List(nodeGraphicsInfo);
////								xpdlActivity2.setNodeGraphicsInfos(nodeGraphicsInfos);
////							}
////						}
////					}
////				}
////			}
////		}
////
////	}
////
////	/**
////	 * @param bpmn
////	 * @param vendorSettings
////	 */
////	private void fillXpdlActivities(AbstractPluginContext context) {
////
////		List<XpdlActivity> activityList = new ArrayList<XpdlActivity>();
////		// XpdlImplementation, XpdlDocumentation, XpdlDescription, XpdlLimit is excluded
////		fillActivities(context, activityList, null);
////		fillEvents(context, activityList, null);
////		fillGateways(context, activityList, null);
////
////		XpdlWorkflowProcess mainWorkflowProcess = xpdl.getWorkflowProcesses().getList().get(0);
////
////		for (SubProcess subProcess : bpmn.getSubProcesses()) {
////			// define a subprocess as a sub flow activity in the main process
////			XpdlActivity xpdlActivity = new XpdlActivity("Activity");
////			xpdlActivity.setId("" + subProcess.hashCode());
////			xpdlActivity.setName(subProcess.getLabel());
////
////			XpdlBlockActivity activityBlockActivity = new XpdlBlockActivity("BlockActivity");
////			activityBlockActivity.setActivitySetId(ID_SUB_PROCESS_SUBWF + subProcess.getId().toString().replace(' ', '_'));
////			activityBlockActivity.setView("EXPANDED");
////
////			xpdlActivity.setBlockActivity(activityBlockActivity);
////			activityList.add(xpdlActivity);
////
////			// create workflow process for each subprocess
////			List<XpdlActivity> tempActivityList = new ArrayList<XpdlActivity>();
////			fillActivities(context, tempActivityList, subProcess);
////			fillEvents(context, tempActivityList, subProcess);
////			fillGateways(context, tempActivityList, subProcess);
////			XpdlActivitySet xpdlActivitySet = null;
////
////			for (XpdlActivitySet tempXpdlActivitySet : mainWorkflowProcess.getActivitySets().getList()) {
////				if (tempXpdlActivitySet.getId().equals(ID_SUB_PROCESS_SUBWF + subProcess.getId().toString().replace(' ', '_'))) {
////					xpdlActivitySet = tempXpdlActivitySet;
////					break;
////				}
////			}
////			xpdlActivitySet.getActivities().setList(tempActivityList);
////			mainWorkflowProcess.getActivitySets().add2List(xpdlActivitySet);
////		}
////		mainWorkflowProcess.getActivities().setList(activityList);
////	}
//
//	/**
//	 * @param bpmn
//	 * @param vendorSettings
//	 * @param activityList
//	 */
//	private void fillGateways(PluginContext context, List<XpdlActivity> activityList, 
//			ContainingDirectedGraphNode container) {
//		for (Gateway gateway : bpmn.getGateways()) {
//			if (gateway.getParent() == container) {
//				XpdlActivity xpdlActivity = new XpdlActivity("Activity");
//				xpdlActivity.setId("" + gateway.hashCode());
//				xpdlActivity.setName(gateway.getLabel());
//
//				XpdlRoute route = new XpdlRoute("Route");
//				String gatewayTypeStr = getGatewayType(gateway.getGatewayType());
//
//				if(!gatewayTypeStr.equals("XOR")) {
//					if(gatewayTypeStr.equals("AND")) {
//						route.setGatewayType("Parallel");
//					} else {
//						route.setGatewayType(gatewayTypeStr);
//					}
//				} 
//				setMarkerVendorSpecific(gateway, route);
//				xpdlActivity.setRoute(route);
//
//				XpdlTransitionRestrictions transitionRestrictions = new XpdlTransitionRestrictions(
//						"TransitionRestrictions");
//				XpdlTransitionRestriction transitionRestriction = new XpdlTransitionRestriction("TransitionRestriction");
//
//				XpdlTransitionRefs transitionRefs = new XpdlTransitionRefs("TransitionRefs");
//
//				if (isSplit(bpmn, gateway)) {
//
//					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> gatewayOutEdge : bpmn.getOutEdges(gateway)) {
//						XpdlTransitionRef transitionRef = new XpdlTransitionRef("TransitionRef");
//						transitionRef.setId("" + gatewayOutEdge.hashCode());
//						transitionRef.setName("" + gatewayOutEdge.hashCode());
//						transitionRefs.add2List(transitionRef);
//					}
//
//					XpdlSplit xpdlSplit = new XpdlSplit("Split");
//					xpdlSplit.setType(gatewayTypeStr);
//					xpdlSplit.setTransitionRefs(transitionRefs);
//					transitionRestriction.setSplit(xpdlSplit);
//
//				} else {
//					XpdlJoin xpdlJoin = new XpdlJoin("Join");
//					xpdlJoin.setType(gatewayTypeStr);
//					transitionRestriction.setJoin(xpdlJoin);
//				}
//				transitionRestrictions.add2List(transitionRestriction);
//				xpdlActivity.setTransitionRestrictions(transitionRestrictions);
//				if (context != null) {
//					XpdlNodeGraphicsInfos nodeGraphicsInfos 
//						= retrieveGraphicsForGraphNode(context, gateway);
//					xpdlActivity.setNodeGraphicsInfos(nodeGraphicsInfos);
//				}
//				activityList.add(xpdlActivity);
//			}
//
//		}
//	}
//
///**
// * @param activityList
// * @param container
// */
//	private void fillEvents(PluginContext context, List<XpdlActivity> activityList, ContainingDirectedGraphNode container) {
//		Map<EventTrigger, String> eventTriggerMap = new HashMap<EventTrigger, String>() {
//			{
//				put(EventTrigger.MESSAGE, "Message");
//				put(EventTrigger.NONE, "None");
//				put(EventTrigger.TIMER, "Timer");
//				put(EventTrigger.CONDITIONAL, "Rule");
//				put(EventTrigger.LINK, "Link");
//				put(EventTrigger.MULTIPLE, "Multiple");
//				put(EventTrigger.ERROR, "Error");
//				put(EventTrigger.COMPENSATION, "Compensation");
//				put(EventTrigger.CANCEL, "Cancel");
//				put( EventTrigger.TERMINATE, "Terminate");
//				put(EventTrigger.SIGNAL, "Signal");
//			}
//		};
//		for (Event event : bpmn.getEvents()) {
//			String trigger = eventTriggerMap.get(event.getEventTrigger());
//					
//			if (event.getParent() == container) {
//				XpdlActivity xpdlActivity = new XpdlActivity("Activity");
//				xpdlActivity.setId("" + event.hashCode());
//				xpdlActivity.setName(event.getLabel());
//
//				XpdlEvent e1 = null;
//				if (event.getEventType().equals(EventType.END)) {
//					e1 = new XpdlEvent("Event");
//					XpdlEndEvent endEvent = null;
//					endEvent = new XpdlEndEvent("EndEvent");
//					if(listOfXpdlEndTrigers.contains(event.getEventTrigger())) {
//						endEvent.setResult(trigger);
//					} else {
//						endEvent.setResult("None");
//					}
//					e1.setEndEvent(endEvent);
//				} else if (event.getEventType().equals(EventType.START)) {
//					e1 = new XpdlEvent("Event");
//					XpdlStartEvent startEvent = null;
//					startEvent = new XpdlStartEvent("StartEvent");
//					if (event.getEventTrigger() != null) {
//						startEvent.setTrigger(trigger);
//					} else {
//						startEvent.setTrigger("None");
//					}
//					e1.setStartEvent(startEvent);
//				} else if (event.getEventType().equals(EventType.INTERMEDIATE)) {
//					e1 = new XpdlEvent("Event");
//					XpdlIntermediateEvent intermediateEvent = null;
//					intermediateEvent = new XpdlIntermediateEvent("IntermediateEvent");
//					if (event.getEventTrigger() != null) {
//						intermediateEvent.setTrigger(trigger);
//						if(event.getEventTrigger().equals(EventTrigger.MESSAGE)) {
//							XpdlTriggerResultMessage resultMessage 
//								= new XpdlTriggerResultMessage("TriggerResultMessage");
//							resultMessage.setCatchThrow(event.getEventUse().toString());
//							XpdlMessageType message = new XpdlMessageType("Message");
//							resultMessage.setMessage(message);
//							intermediateEvent.setTriggerResultMessage(resultMessage);
//						}
//					} else {
//						intermediateEvent.setTrigger("None");
//					}
//					e1.setIntermediateEvent(intermediateEvent);
//				}
//				xpdlActivity.setEvent(e1);
//				if (context != null) {
//					XpdlNodeGraphicsInfos nodeGraphicsInfos 
//						= retrieveGraphicsForGraphNode(context, event);
//					xpdlActivity.setNodeGraphicsInfos(nodeGraphicsInfos);
//				}
//				activityList.add(xpdlActivity);
//			}
//
//		}
//	}
//
///**
// * @param context
// * @param activityList
// * @param container
// */
//	private void fillActivities(PluginContext context,
//			List<XpdlActivity> activityList, ContainingDirectedGraphNode container) {
//		for (Activity activity : bpmn.getActivities()) {
//			if (activity.getParent() == container) {
//				XpdlActivity xpdlActivity = new XpdlActivity("Activity");
//				xpdlActivity.setId("" + activity.hashCode());
//				xpdlActivity.setName(activity.getLabel());
//				XpdlImplementation xpdlImplementation = new XpdlImplementation("Implementation");
//				XpdlTask xpdlTask = new XpdlTask("Task");
//				xpdlImplementation.setTask(xpdlTask);
//				xpdlActivity.setImplementation(xpdlImplementation);
//				if (context != null) {
//					XpdlNodeGraphicsInfos nodeGraphicsInfos 
//						= retrieveGraphicsForGraphNode(context, activity);
//					xpdlActivity.setNodeGraphicsInfos(nodeGraphicsInfos);
//				}
//				activityList.add(xpdlActivity);
//			}
//		}
//	}
//	
//	private void fillTransitions(PluginContext context,
//			List<XpdlTransition> transitionList, ContainingDirectedGraphNode container) {
//		for (Flow flow : bpmn.getFlows()) {
//			if (flow.getParent() == container) {
//				XpdlTransition xpdlTransition = new XpdlTransition("Transition");
//				xpdlTransition.setFrom("" + flow.getSource().hashCode());
//				xpdlTransition.setTo("" + flow.getTarget().hashCode());
//				xpdlTransition.setId("" + flow.hashCode());
//
//				if (context != null) {
//					XpdlConnectorGraphicsInfos tConnectorGraphicsInfos 
//						= retrieveGraphicsForGraphEdge(context, flow);
//					xpdlTransition.setConnectorGraphicsInfos(tConnectorGraphicsInfos);
//				}
//				transitionList.add(xpdlTransition);
//			}
//		}
//	}
//	
//	private void fillMessageFlows(PluginContext context, List<XpdlMessageFlow> listOfFlows) {
//		for (MessageFlow flow : bpmn.getMessageFlows()) {
//			XpdlMessageFlow xpdlMessageFlow = new XpdlMessageFlow("MessageFlow");
//			xpdlMessageFlow.setSource("" + flow.getSource().hashCode());
//			xpdlMessageFlow.setTarget("" + flow.getTarget().hashCode());
//			xpdlMessageFlow.setId("" + flow.hashCode());
//
//			if (context != null) {
//				XpdlConnectorGraphicsInfos tConnectorGraphicsInfos = retrieveGraphicsForGraphEdge(context, flow);
//				xpdlMessageFlow.setConnectorsGraphicsInfos(tConnectorGraphicsInfos);
//			}
//			listOfFlows.add(xpdlMessageFlow);
//		}
//	}
//
//	/**
//	 * @param bpmn
//	 * @param gateway
//	 * @return
//	 */
//	private boolean isSplit(BPMNDiagram bpmn, Gateway gateway) {
//		boolean isSplit = true;
//		isSplit = (bpmn.getOutEdges(gateway).size() > 1);
//		return isSplit;
//	}
//
//	/**
//	 * @param gateway
//	 * @param route
//	 */
//	private void setMarkerVendorSpecific(Gateway gateway, XpdlRoute route) {
//
//		if (gateway.getGatewayType().equals(GatewayType.INCLUSIVE)) {
//			route.setMarkerVisible(""+gateway.isMarkerVisible());
//		} else if (gateway.getGatewayType().equals(GatewayType.DATABASED)) {
//			route.setMarkerVisible(""+gateway.isMarkerVisible());
//		} else {
//			route.setMarkerVisible(""+gateway.isMarkerVisible());
//		}
//	}
//
//	/**
//	 * @param activityNameStr
//	 * @return
//	 */
//	private String getGatewayType(GatewayType type) {
//		String typeStr = null;
//		if (type.equals(GatewayType.EVENTBASED) || type.equals(GatewayType.DATABASED)) {
//			typeStr = "XOR";
//		} else if (type.equals(GatewayType.PARALLEL)) {
//			typeStr = "AND";
//		} else if (type.equals(GatewayType.INCLUSIVE)) {
//			typeStr = "OR";
//		}
//		return typeStr;
//	}
//
//	/**
//	 * @param bpmn
//	 * @param xpdl
//	 */
////	private void fillXpdlTransitions(AbstractPluginContext context) {
////		for (Flow flow : bpmn.getFlows()) {
////			XpdlTransition t = new XpdlTransition("Transition");
////			t.setFrom("" + flow.getSource().hashCode());
////			t.setTo("" + flow.getTarget().hashCode());
////			t.setId("" + flow.hashCode());
////
////			if (layout) {
////				XpdlConnectorGraphicsInfos tConnectorGraphicsInfos 
////					= retrieveGraphicsForGraphEdge(context, flow);
////				t.setConnectorGraphicsInfos(tConnectorGraphicsInfos);
////			}
////
////			XpdlWorkflowProcess mainWorkflowProcess = xpdl.getWorkflowProcesses().getList().get(0);
////
////			if (flow.getParentSubProcess() == null) {
////				// transition is belong to main process
////				mainWorkflowProcess.getTransitions().add2List(t);
////			} else {
////				// transition is belong to subprocess's activity set
////				XpdlActivitySet activitySet = null;
////				for (XpdlActivitySet tempActivitySet : mainWorkflowProcess.getActivitySets().getList()) {
////					if (tempActivitySet.getId().equals(ID_SUB_PROCESS_SUBWF + flow.getParentSubProcess().getId().toString().replace(' ', '_'))) {
////						activitySet = tempActivitySet;
////						break;
////					}
////				}
////				activitySet.getTransitions().add2List(t);
////			}
////		}
////	}
//
//	/**
//	 * @param xpdl
//	 * @return
//	 */
//	private Xpdl fillXPDL(PluginContext context) {
//		// TODO No participant study is done
//		// TODO No datatype study is done
//		// TODO No extension study is done
//
//		Xpdl xpdl = new Xpdl();
//
//		// XPDL PACKAGE HEADER
//		XpdlPackageHeader packageHeader = setPackageHeader();
//
//		XpdlPools xpdlPools = new XpdlPools("Pools");
//
//		// XPDL WORKFLOW PROCESSES
//		XpdlWorkflowProcesses xpdlWorkflowProcesses = new XpdlWorkflowProcesses("WorkflowProcesses");
//		
//		// BUILD MAIN WORKFLOW PROCESS
//		XpdlWorkflowProcess mainWorkflowProcess = fillWorkflowProcess(null, context);
//		XpdlPool xpdlPool = fillPool(context, null);
//		xpdlPool.setProcess(mainWorkflowProcess.getId());
//		xpdlPools.add2List(xpdlPool);	
//		xpdlWorkflowProcesses.add2List(mainWorkflowProcess);
//		
//		// BUILD WORKFLOW PROCESSES FOR POOLS
//		for(Swimlane pool : bpmn.getPools()) {
//			XpdlWorkflowProcess workflowProcess = fillWorkflowProcess(pool, context);
//			xpdlPool = fillPool(context, pool);
//			xpdlPool.setProcess(workflowProcess.getId());
//			xpdlPools.add2List(xpdlPool);
//			xpdlWorkflowProcesses.add2List(workflowProcess);
//		}
//		XpdlMessageFlows messageFlows = new XpdlMessageFlows("MessageFlows");
//		List<XpdlMessageFlow> xpdlMessageFlowList = new ArrayList<XpdlMessageFlow>();
//		messageFlows.setList(xpdlMessageFlowList);
//		xpdl.setMessageFlows(messageFlows);
//		fillMessageFlows(context, xpdlMessageFlowList);
//
//		// FILL XPDL SETTINGS
//		xpdl.setId(ID_XPDL + bpmn.getLabel().replace(' ', '_'));
//		xpdl.setName(bpmn.getLabel().replace(' ', '_'));
//		xpdl.setPackageHeader(packageHeader);
//		xpdl.setPools(xpdlPools);
//		xpdl.setWorkflowProcesses(xpdlWorkflowProcesses);
//
//		return xpdl;
//	}
//	
//	private XpdlPool fillPool(PluginContext context, Swimlane pool) {
//		XpdlPool xpdlPool = new XpdlPool("Pool");
//		if (pool == null) { 
//			xpdlPool.setBoundaryVisible("false");
//			xpdlPool.setMainPool("true");
//			xpdlPool.setId(ID_DEFAULT_POOL + bpmn.getLabel().replace(' ', '_'));
//
//		} else {
//			xpdlPool.setBoundaryVisible("true");
//			xpdlPool.setMainPool("false");
//			xpdlPool.setId(pool.getId().toString().replace(' ', '_'));
//			if (context != null) {
//				XpdlNodeGraphicsInfos graphicsInfos = retrieveGraphicsForGraphNode(context, pool);
//				xpdlPool.setNodeGraphicsInfos(graphicsInfos);
//			}
//		}
//		fillLanes(context, pool);
//		return xpdlPool;
//	}
//				
//	private XpdlLanes fillLanes(PluginContext context, Swimlane pool) {
//		XpdlLanes xpdlLanes = new XpdlLanes("Lanes");
//		for (Swimlane lane : bpmn.getLanes(pool)) {
//			XpdlLane xpdlLane = new XpdlLane("Lane");
//			xpdlLane.setId(lane.getId().toString());
//			xpdlLane.setName(lane.getLabel());
//			if (lane.getParentLane() != null) {
//				xpdlLane.setParentLane(lane.getParentLane().getId().toString());
//			}
//			if (context != null) {
//				XpdlNodeGraphicsInfos graphicsInfos = retrieveGraphicsForGraphNode(context, lane);
//				xpdlLane.setNodeGraphicsInfos(graphicsInfos);
//			}
//			xpdlLanes.add2List(xpdlLane);
//		}
//		return xpdlLanes;
//	}
//	
//	
//	private XpdlNodeGraphicsInfos retrieveGraphicsForGraphNode(PluginContext context, 
//			BPMNNode bpmnNode) {
//		ProMJGraphPanel graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmn);
//		ProMGraphModel graphModel = graphPanel.getGraph().getModel();		
//		for(Object o : retrieveAllGraphElements(graphModel)) {
//			if (o instanceof ProMGraphCell) {
//				ProMGraphCell graphCell = (ProMGraphCell) o;			
//				if(bpmnNode.equals(graphCell.getNode())) {
//					XpdlNodeGraphicsInfos xpdlNodeGraphicsInfos 
//						= retrieveCellGraphicsInfo(context, graphCell);
//					return xpdlNodeGraphicsInfos;
//				}
//			}
//		}		
//		return null;
//	}
//	
//	private Set<Object> retrieveAllGraphElements(ProMGraphModel graphModel) {
//		Set<Object> modelElements = new HashSet<Object>();
//		for (Object o : graphModel.getRoots()) {
//			modelElements.add(o);
//			if (o instanceof ProMGraphCell) {
//				modelElements.addAll(retrieveAllChildren((ProMGraphCell)o));
//			}
//		}
//		return modelElements;
//	}
//	
//	private Set<Object> retrieveAllChildren(ProMGraphCell graphCell) {
//		Set<Object> resultSet = new HashSet<Object>();
//		for (Object o : graphCell.getChildren()) {
//			resultSet.add(o);
//			if (o instanceof ProMGraphCell) {
//				resultSet.addAll(retrieveAllChildren((ProMGraphCell) o));
//			}
//		}
//		return resultSet;
//	}
//	
//	private  XpdlConnectorGraphicsInfos retrieveGraphicsForGraphEdge(PluginContext context, 
//			BPMNEdge bpmnEdge) {
//		ProMJGraphPanel graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmn);
//		ProMGraphModel graphModel = graphPanel.getGraph().getModel();
//		for(Object o : retrieveAllGraphElements(graphModel)) {
//			if (o instanceof ProMGraphEdge) {
//				ProMGraphEdge graphEdge = (ProMGraphEdge) o;
//				if(bpmnEdge.equals(graphEdge.getEdge())) {
//					XpdlConnectorGraphicsInfos xpdlNodeGraphicsInfos 
//						= retrieveEdgeGraphicsInfo(context, graphEdge);
//					return xpdlNodeGraphicsInfos;
//				}
//			}
//		}
//		return null;
//	}
//	
//	private XpdlConnectorGraphicsInfos retrieveEdgeGraphicsInfo(PluginContext context, 
//			ProMGraphEdge edge) {
//		
//		XpdlConnectorGraphicsInfos connGraphicsInfos = new XpdlConnectorGraphicsInfos("ConnectorGraphicsInfos");
//		XpdlConnectorGraphicsInfo connGraphicsInfo = new XpdlConnectorGraphicsInfo("ConnectorGraphicsInfo");
//		connGraphicsInfos.add2List(connGraphicsInfo);
//		// Construct graph info
//		List<Object> objects = edge.getView().getPoints();
//		for(Object o : objects) {
//			if(o instanceof JGraphPortView) {
//				JGraphPortView portView = (JGraphPortView)o;
//				Point2D point = portView.getLocation();
//				XpdlCoordinates xpdlCoordinates = new XpdlCoordinates("Coordinates");
//				xpdlCoordinates.setxCoordinate(new Double(point.getX()).toString());
//				xpdlCoordinates.setyCoordinate(new Double(point.getY()).toString());
//				connGraphicsInfo.addCoordinates(xpdlCoordinates);
//			}	
//		}
//		return connGraphicsInfos;
//	}
//	
//	private XpdlNodeGraphicsInfos retrieveCellGraphicsInfo(PluginContext context, 
//			ProMGraphCell graphCell) {
//		XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");	
//		XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
//		nodeGraphicsInfos.add2List(nodeGraphicsInfo);
//		XpdlCoordinates xpdlCoordinates = new XpdlCoordinates("Coordinates");
//		// Construct graph info
//		Rectangle2D rectangle = graphCell.getView().getBounds();
//		xpdlCoordinates.setxCoordinate(new Double(rectangle.getX()).toString());
//		xpdlCoordinates.setyCoordinate(new Double(rectangle.getY()).toString());				
//		nodeGraphicsInfo.setCoordinates(xpdlCoordinates);
//		nodeGraphicsInfo.setWidth(new Double(rectangle.getWidth()).toString());
//		nodeGraphicsInfo.setHeight(new Double(rectangle.getHeight()).toString());
//		
//		return nodeGraphicsInfos;
//	}
//				
//	private XpdlWorkflowProcess fillWorkflowProcess(Swimlane pool, PluginContext context) {
//		// XPDL MAIN WORKFLOW PROCESS
//		XpdlWorkflowProcess workflowProcess = new XpdlWorkflowProcess("WorkflowProcess");
//		String id = (pool == null ? ID_MAIN_PROCESS_WF + bpmn.getLabel() 
//				: ID_PROCESS_WF + pool.getId().toString()).replace(' ', '_');
//		workflowProcess.setId(id);
//		String name = pool == null ? bpmn.getLabel().replace(' ', '_') : pool.getLabel();
//		workflowProcess.setName(name);
//		workflowProcess.setAccessLevel("PUBLIC");
//
//		// XPDL WORKFLOW PROCESS HEADER
//		XpdlProcessHeader processHeader = setProcessHeader();
//		workflowProcess.setProcessHeader(processHeader);
//
//		// XPDL WORKFLOW PROCESS REDEFINABLE HEADER
//		XpdlRedefinableHeader redefinableHeader = setRedefinableHeader();
//		workflowProcess.setRedefinableHeader(redefinableHeader);
//
//		// XPDL WORKFLOW PROCESS PARTICIPANTS
////		HashMap<String, String> participantAndType = new HashMap<String, String>();
////		participantAndType.put("currentOwner", "ROLE");
////		XpdlParticipants participants = setParticipants(participantAndType);
////		workflowProcess.setParticipants(participants);
//		
//		// XPDL ACTIVITIES
//		XpdlActivities mainXpdlActivities = new XpdlActivities("Activities");
//		List<XpdlActivity> activities = new ArrayList<XpdlActivity>();
//		
//		// XPDL WORKFLOW PROCESS ACTIVITY SETS FOR SUBPROCESSES
//		XpdlActivitySets activitySets = new XpdlActivitySets("ActivitySets");
//		List<XpdlActivitySet> activitySetList = new ArrayList<XpdlActivitySet>();
//		for (SubProcess subProcess : bpmn.getSubProcesses(pool)) {
//			XpdlActivitySet xpdlActivitySet = new XpdlActivitySet("ActivitySet");
//			xpdlActivitySet.setId(ID_SUB_PROCESS_SUBWF + subProcess.getId().toString().replace(' ', '_'));
//			xpdlActivitySet.setName(subProcess.getLabel());
//			xpdlActivitySet.setArtifacts(new XpdlArtifacts("Artifacts"));
//			xpdlActivitySet.setAssociations(new XpdlAssociations("Associations"));
//
//			// XPDL ACTIVITIES FOR SUBPROCESS
//			XpdlActivities xpdlActivities = new XpdlActivities("Activities");
//			List<XpdlActivity> subActivities = new ArrayList<XpdlActivity>();
//			xpdlActivities.setList(subActivities);
//			xpdlActivitySet.setActivities(xpdlActivities);
//			fillActivities(context, subActivities, subProcess);
//			fillEvents(context, subActivities, subProcess);
//			fillGateways(context, subActivities, subProcess);
//
//			// XPDL WORKFLOW PROCESS TRANSITIONS FOR SUBPROCESS
//			XpdlTransitions transitions = new XpdlTransitions("Transitions");
//			List<XpdlTransition> xpdlTransitionList = new ArrayList<XpdlTransition>();
//			transitions.setList(xpdlTransitionList);
//			xpdlActivitySet.setTransitions(transitions);
//			fillTransitions(context, xpdlTransitionList, subProcess);
//
//			activitySetList.add(xpdlActivitySet);
//			
//			// CREATE SUB_PROCESS
//			XpdlActivity xpdlSubProcess = new XpdlActivity("Activity"); 
//			xpdlSubProcess.setId("" + subProcess.hashCode());
//			XpdlBlockActivity blockActivity = new XpdlBlockActivity("BlockActivity");
//			blockActivity.setActivitySetId(ID_SUB_PROCESS_SUBWF + subProcess.getId().toString().replace(' ', '_'));
//			blockActivity.setView(subProcess.isBCollapsed()? "COLLAPSED" : "EXPANDED");
//			xpdlSubProcess.setBlockActivity(blockActivity);
//			activities.add(xpdlSubProcess);
//			if (context != null) {
//				XpdlNodeGraphicsInfos nodeGraphicsInfos 
//					= retrieveGraphicsForGraphNode(context, subProcess);
//				xpdlSubProcess.setNodeGraphicsInfos(nodeGraphicsInfos);
//			}
//		}
//		activitySets.setList(activitySetList);
//		workflowProcess.setActivitySets(activitySets);
//
//		fillActivities(context, activities, pool);
//		fillEvents(context, activities, pool);
//		fillGateways(context, activities, pool);
//		mainXpdlActivities.setList(activities);
//		workflowProcess.setActivities(mainXpdlActivities);
//
//		// XPDL WORKFLOW PROCESS TRANSITIONS
//		XpdlTransitions mainXpdlTransitions = new XpdlTransitions("Transitions");
//		List<XpdlTransition> xpdlTransitionList = new ArrayList<XpdlTransition>();
//		fillTransitions(context, xpdlTransitionList, pool);
//		mainXpdlTransitions.setList(xpdlTransitionList);
//		workflowProcess.setTransitions(mainXpdlTransitions);
//		
//		return workflowProcess;
//	}
//	
////	private XpdlParticipants setParticipants(HashMap<String, String> participantAndType) {
////		XpdlParticipants participants = new XpdlParticipants("Participants");
////
////		for (String tempParticipant : participantAndType.keySet()) {
////			XpdlParticipant participant = new XpdlParticipant("Participant");
////			participant.setId(tempParticipant);
////			XpdlParticipantType participantType = new XpdlParticipantType("ParticipantType");
////			participantType.setType(participantAndType.get(tempParticipant));
////			participant.setParticipantType(participantType);
////			participants.add2List(participant);
////		}
////		return participants;
////	}
//
//	private XpdlRedefinableHeader setRedefinableHeader() {
//		XpdlRedefinableHeader redefinableHeader = new XpdlRedefinableHeader("RedefinableHeader");
//
//		XpdlAuthor author = new XpdlAuthor("Author");
//		XpdlText authorXpdlText = new XpdlText("XpdlText");
//		authorXpdlText.setText("ProM");
//		author.setXpdlText(authorXpdlText);
//		redefinableHeader.setAuthor(author);
//
//		XpdlVersion version = new XpdlVersion("Version");
//		XpdlText versionXpdlText = new XpdlText("XpdlText");
//		versionXpdlText.setText("3.6");
//		version.setXpdlText(versionXpdlText);
//		redefinableHeader.setVersion(version);
//		return redefinableHeader;
//	}
//
//	private XpdlProcessHeader setProcessHeader() {
//		XpdlProcessHeader processHeader = new XpdlProcessHeader("ProcessHeader");
//
//		XpdlCreated processHeaderCreated = new XpdlCreated("Created");
//		String processHeaderCreateDate = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date());
//
//		XpdlText processHeaderCreateXpdlText = new XpdlText("XpdlText");
//		processHeaderCreateXpdlText.setText(processHeaderCreateDate);
//		processHeaderCreated.setXpdlText(processHeaderCreateXpdlText);
//
//		XpdlPriority processHeaderXpdlPriority = new XpdlPriority("Priority");
//		XpdlText processHeaderPriorityXpdlText = new XpdlText("XpdlText");
//		processHeaderPriorityXpdlText.setText("Normal");
//		processHeaderXpdlPriority.setXpdlText(processHeaderPriorityXpdlText);
//
//		processHeader.setCreated(processHeaderCreated);
//		processHeader.setPriority(processHeaderXpdlPriority);
//		return processHeader;
//	}
//
//	/**
//	 * @param packageHeaderVersion
//	 * @param packageHeaderVendor
//	 * @param packageHeaderToday
//	 * @param packageHeaderDescription
//	 * @param packageHeaderDocumentation
//	 * @return
//	 */
//	private XpdlPackageHeader setPackageHeader() {
//
//		// SET PACKAGE HEADER VARIABLES
//		String packageHeaderVersion = "2.2";
//		String packageHeaderVendor = "ProM";
//		Date packageHeaderToday = new Date();
//		String packageHeaderDescription = "ProM Mined BPMN Model";
//		String packageHeaderDocumentation = "ProM Mined BPMN Model";
//
//		XpdlPackageHeader packageHeader = new XpdlPackageHeader("PackageHeader");
//
//		XpdlXpdlVersion xpdlXpdlVersion = new XpdlXpdlVersion("XPDLVersion");
//		XpdlText versionText = new XpdlText("XpdlText");
//		versionText.setText(packageHeaderVersion);
//		xpdlXpdlVersion.setXpdlText(versionText);
//
//		XpdlVendor xpdlVendor = new XpdlVendor("Vendor");
//		XpdlText xpdlVendorXpdlText = new XpdlText("XpdlText");
//		xpdlVendorXpdlText.setText(packageHeaderVendor);
//		xpdlVendor.setXpdlText(xpdlVendorXpdlText);
//
//		XpdlCreated created = new XpdlCreated("Created");
//		String now = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(packageHeaderToday);
//
//		XpdlText createdXpdlText = new XpdlText("XpdlText");
//		createdXpdlText.setText(now);
//		created.setXpdlText(createdXpdlText);
//
//		XpdlDescription xpdlDescription = new XpdlDescription("Description");
//		XpdlText descriptionXpdlText = new XpdlText("XpdlText");
//		descriptionXpdlText.setText(packageHeaderDescription);
//		xpdlDescription.setXpdlText(descriptionXpdlText);
//
//		XpdlDocumentation xpdlDocumentation = new XpdlDocumentation("Documentation");
//		XpdlText documentationXpdlText = new XpdlText("XpdlText");
//		documentationXpdlText.setText(packageHeaderDocumentation);
//		xpdlDocumentation.setXpdlText(documentationXpdlText);
//
//		packageHeader.setVersion(xpdlXpdlVersion);
//		packageHeader.setVendor(xpdlVendor);
//		packageHeader.setCreated(created);
//		packageHeader.setDescription(xpdlDescription);
//		packageHeader.setDocumentation(xpdlDocumentation);
//		return packageHeader;
//	}
//
//}
