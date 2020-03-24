package org.apromore.processmining.plugins.bpmn;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Deprecated
public class XPDLReader {

	private BPMNDiagram bpmndiagram = null;
	private Node activitySets = null;
	private final Map<String, BPMNNode> id2node = new HashMap<String, BPMNNode>();

	public BPMNDiagram read(InputStream input, BPMNDiagram bpmndiagram) throws Exception {

		this.bpmndiagram = bpmndiagram;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);

		Document rootNode = dbf.newDocumentBuilder().parse(input);

		parse(rootNode);

		return bpmndiagram;
	}

	private void parse(Node rootnode) {
		//Get the first process
		Node n = getChildNodeWithName(rootnode, "xpdl2:Package");
		if (n == null) {
			System.out
					.println("org.processmining.plugins.bpmn.XPDLReader.parse - Could not find <xpdl2:Package> in the file.");
			return;
		} //TODO: This needs to be transformed into standard ProM error messages.

		Node pools = getChildNodeWithName(n, "xpdl2:Pools");
		Node pool = getChildNodeWithName(pools, "xpdl2:Pool");
		if (pool!= null) {
			Node lanes = getChildNodeWithName(pool, "xpdl2:Lanes");
			parseLanes(lanes, null);
		}
		
		n = getChildNodeWithName(n, "xpdl2:WorkflowProcesses");
		if (n == null) {
			System.out
					.println("org.processmining.plugins.bpmn.XPDLReader.parse - Could not find <xpdl2:WorkflowProcesses> within <xpdl2:Package> in the file.");
			return;
		}
		n = getChildNodeWithName(n, "xpdl2:WorkflowProcess");
		if (n == null) {
			System.out
					.println("org.processmining.plugins.bpmn.XPDLReader.parse - Could not find <xpdl2:WorkflowProcess> within <xpdl2:WorkflowProcesses> in the file.");
			return;
		}

		//Remember activity sets
		activitySets = getChildNodeWithName(n, "xpdl2:ActivitySets");

		//Parse the activities
		Node activities = getChildNodeWithName(n, "xpdl2:Activities");
		if (activities == null) {
			System.out
					.println("org.processmining.plugins.bpmn.XPDLReader.parse - Could not find <xpdl2:Activities> within <xpdl2:WorkflowProcess> in the file.");
			return;
		}
		parseActivities(activities, null);

		//Parse the transitions
		Node transitions = getChildNodeWithName(n, "xpdl2:Transitions");
		if (transitions == null) {
			System.out
					.println("org.processmining.plugins.bpmn.XPDLReader.parse - Could not find <xpdl2:Transitions> within <xpdl2:WorkflowProcess> in the file.");
			return;
		}
		parseTransitions(transitions, null);
	}

	private void parseLanes(Node lanes, Object object) {
		NodeList nl = lanes.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeName().equals("xpdl2:Lane") || n.getNodeName().equals("Lane")) {
				String id = getAttribute(n, "Id");
				if (id == null) {
					System.out
							.println("org.processmining.plugins.bpmn.XPDLReader.parseActivities - Node without id found; all nodes must have an id.");
					return;
				}
				String name = getAttribute(n, "Name");
				name = (name == null) ? "" : name;
				if (name!=null) {
					Swimlane lane = bpmndiagram.addSwimlane(name, null, SwimlaneType.LANE);
					id2node.put(id, lane);
				}
			}
		}
	}

	private void parseSubProcess(Node invocation, SubProcess subprocess) {
		String activitySetIDString = getAttribute(invocation, "ActivitySetId");
		if ((activitySetIDString != null) && (activitySets != null)) {
			Node activitySet = getChildNodeWithID(activitySets, activitySetIDString);

			//Now parse the subprocess and give all nodes/transitions the parent as argument.
			//Parse the activities
			Node activities = getChildNodeWithName(activitySet, "xpdl2:Activities");
			if (activities == null) {
				System.out
						.println("org.processmining.plugins.bpmn.XPDLReader.parse - Could not find <xpdl2:Activities> within <xpdl2:WorkflowProcess> in the file.");
				return;
			}
			parseActivities(activities, subprocess);

			//Parse the transitions
			Node transitions = getChildNodeWithName(activitySet, "xpdl2:Transitions");
			if (transitions == null) {
				System.out
						.println("org.processmining.plugins.bpmn.XPDLReader.parse - Could not find <xpdl2:Transitions> within <xpdl2:WorkflowProcess> in the file.");
				return;
			}
			parseTransitions(transitions, subprocess);
		}
	}

	private void parseActivities(Node activities, SubProcess parent) {
		NodeList nl = activities.getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeName().equals("xpdl2:Activity") || n.getNodeName().equals("Activity")) {
				//Activity, now parse it
				String id = getAttribute(n, "Id");
				if (id == null) {
					System.out
							.println("org.processmining.plugins.bpmn.XPDLReader.parseActivities - Node without id found; all nodes must have an id.");
					return;
				}
				String name = getAttribute(n, "Name");
				name = (name == null) ? "" : name;

				Node event = getChildNodeWithName(n, "xpdl2:Event");
				if (event != null) {
					//This node is an event

					//Get the event type
					Node eventInfo = getChildNodeWithName(event, "xpdl2:StartEvent");
					EventType eventType = null;
					if (eventInfo != null) {
						//This event is a start event
						eventType = EventType.START;
					} else {
						eventInfo = getChildNodeWithName(event, "xpdl2:IntermediateEvent");
						if (eventInfo != null) {
							//This event is an intermediate event 
							eventType = EventType.INTERMEDIATE;
						} else {
							eventInfo = getChildNodeWithName(event, "xpdl2:EndEvent");
							if (eventInfo != null) {
								//This event is an end event
								eventType = EventType.END;
							} else {
								System.out
										.println("org.processmining.plugins.bpmn.XPDLReader.parseActivities - Event with id "
												+ id + " has no type.");
								return;
							}
						}
					}

					//Get the event use
					/*
					 * XPDL 2.0 does not use the difference between throw/catch
					 * events. Below we make educated guesses based on the type
					 * eventType/Trigger and the allowed combinations.
					 */
					EventUse eventUse = EventUse.CATCH;

					//Get the event trigger
					String trigger = null;
					if (eventType == EventType.END) {
						trigger = getAttribute(eventInfo, "Result");
						eventUse = EventUse.THROW;
					} else {
						trigger = getAttribute(eventInfo, "Trigger");
					}
					EventTrigger eventTrigger = EventTrigger.NONE;
					if (trigger != null) {
						if (trigger.equalsIgnoreCase("Message")) {
							eventTrigger = EventTrigger.MESSAGE;
						} else if (trigger.equalsIgnoreCase("Timer")) {
							eventTrigger = EventTrigger.TIMER;
						} else if (trigger.equalsIgnoreCase("Rule")) {
							eventTrigger = EventTrigger.CONDITIONAL;
						} else if (trigger.equalsIgnoreCase("Link")) {
							eventTrigger = EventTrigger.LINK;
						} else if (trigger.equalsIgnoreCase("Multiple")) {
							eventTrigger = EventTrigger.MULTIPLE;
						} else if (trigger.equalsIgnoreCase("Error")) {
							eventTrigger = EventTrigger.ERROR;
						} else if (trigger.equalsIgnoreCase("Compensation")) {
							eventTrigger = EventTrigger.COMPENSATION;
						} else if (trigger.equalsIgnoreCase("Cancel")) {
							eventTrigger = EventTrigger.CANCEL;
						} else if (trigger.equalsIgnoreCase("Terminate")) {
							eventTrigger = EventTrigger.TERMINATE;
						} else
							eventTrigger = EventTrigger.NONE;
					}

					//Get the event's boundary-node (if any)
					Activity boundaryNode = null;
					String boundaryNodeID = getAttribute(eventInfo, "Target");
					if (boundaryNodeID != null) {
						BPMNNode potentialBoundaryNode = id2node.get(boundaryNodeID);
						if (potentialBoundaryNode instanceof Activity) {
							boundaryNode = (Activity) potentialBoundaryNode;
						}
					}

					if (parent == null) {
						Node nodegraphicsInfos = getChildNodeWithName(n, "xpdl2:NodeGraphicsInfos");
						Node nodegraphicsInfo = getChildNodeWithName(nodegraphicsInfos, "xpdl2:NodeGraphicsInfo");
						BPMNNode bpmnNode = null;
						if (nodegraphicsInfo != null) {
							String laneID = getAttribute(nodegraphicsInfo, "LaneId");
							bpmnNode = id2node.get(laneID);
						}

						if (bpmnNode != null) {
							id2node.put(id, bpmndiagram.addEvent(name, eventType, eventTrigger, eventUse,
									(Swimlane) bpmnNode, boundaryNode));
						} else {
							id2node.put(id, bpmndiagram.addEvent(name, eventType, eventTrigger, eventUse, boundaryNode));
						}

					} else {
						id2node.put(id,
								bpmndiagram.addEvent(name, eventType, eventTrigger, eventUse, parent, boundaryNode));
					}

				} else {
					Node gateway = getChildNodeWithName(n, "xpdl2:Route");
					if (gateway != null) {
						//This node is a gateway

						//Get the gateway type
						GatewayType gatewayType = GatewayType.DATABASED;
						String gatewayTypeString = getAttribute(gateway, "GatewayType");
						if (gatewayTypeString != null) {
							if (gatewayTypeString.equalsIgnoreCase("XOR")) {
								String instantiateString = getAttribute(gateway, "Instantiate");
								if ((instantiateString != null) && (instantiateString.equalsIgnoreCase("true"))) {
									gatewayType = GatewayType.EVENTBASED;
								}
							} else if (gatewayTypeString.equalsIgnoreCase("AND")) {
								gatewayType = GatewayType.PARALLEL;
							} else if (gatewayTypeString.equalsIgnoreCase("OR")) {
								gatewayType = GatewayType.INCLUSIVE;
							} else if (gatewayTypeString.equalsIgnoreCase("Complex")) {
								gatewayType = GatewayType.COMPLEX;
							}
						}

						if (parent == null) {
							Node nodegraphicsInfos = getChildNodeWithName(n, "xpdl2:NodeGraphicsInfos");
							Node nodegraphicsInfo = getChildNodeWithName(nodegraphicsInfos, "xpdl2:NodeGraphicsInfo");
							BPMNNode bpmnNode = null;
							if (nodegraphicsInfo != null) {
								String laneID = getAttribute(nodegraphicsInfo, "LaneId");
								bpmnNode = id2node.get(laneID);
							}
							if (bpmnNode != null) {
								id2node.put(id, bpmndiagram.addGateway(name, gatewayType, (Swimlane) bpmnNode));
							} else {
								id2node.put(id, bpmndiagram.addGateway(name, gatewayType));
							}
						} else {
							id2node.put(id, bpmndiagram.addGateway(name, gatewayType, parent));
						}
					} else {
						//This node is a regular activity
						boolean isLoop = false;
						boolean isMultiInstance = false;

						Node loopInfo = getChildNodeWithName(n, "xpdl2:Loop");
						if (loopInfo != null) {
							String loopTypeString = getAttribute(loopInfo, "LoopType");
							if (loopTypeString != null) {
								if (loopTypeString.equalsIgnoreCase("MultiInstance")) {
									isMultiInstance = true;
								} else if (loopTypeString.equalsIgnoreCase("Standard")) {
									isLoop = true;
								}
							}
						}

						Node invocation = getChildNodeWithName(n, "xpdl2:BlockActivity");

						if (invocation == null) {
							if (parent == null) {
								Node nodegraphicsInfos = getChildNodeWithName(n, "xpdl2:NodeGraphicsInfos");
								Node nodegraphicsInfo = getChildNodeWithName(nodegraphicsInfos, "xpdl2:NodeGraphicsInfo");
								BPMNNode bpmnNode = null;
								if (nodegraphicsInfo != null) {
									String laneID = getAttribute(nodegraphicsInfo, "LaneId");
									bpmnNode = id2node.get(laneID);
								}
								if (bpmnNode != null) {
									id2node.put(id, bpmndiagram.addActivity(name, isLoop, false, false,
											isMultiInstance, false, (Swimlane) bpmnNode));
								} else {
									id2node.put(id,
											bpmndiagram.addActivity(name, isLoop, false, false, isMultiInstance, false));
								}
							} else {
								id2node.put(id, bpmndiagram.addActivity(name, isLoop, false, false, isMultiInstance,
										false, parent));
							}

						} else {
							if (parent == null) {
								Node nodegraphicsInfos = getChildNodeWithName(n, "xpdl2:NodeGraphicsInfos");
								if (nodegraphicsInfos!=null) {
									Node nodegraphicsInfo = getChildNodeWithName(nodegraphicsInfos,
											"xpdl2:NodeGraphicsInfo");
									BPMNNode bpmnNode = null;
									if (nodegraphicsInfo != null) {
										String laneID = getAttribute(nodegraphicsInfo, "LaneId");
										bpmnNode = id2node.get(laneID);
									}
									if (bpmnNode != null) {
										SubProcess subProcess = bpmndiagram.addSubProcess(name, isLoop, false, false,
												isMultiInstance, false, (Swimlane) bpmnNode);
										id2node.put(id, subProcess);
										parseSubProcess(invocation, subProcess);

									} else {
										SubProcess subProcess = bpmndiagram.addSubProcess(name, isLoop, false, false,
												isMultiInstance, false);
										id2node.put(id, subProcess);
										parseSubProcess(invocation, subProcess);
									}
								}
							} else {
								SubProcess subProcess = bpmndiagram.addSubProcess(name, isLoop, false, false,
										isMultiInstance, false, parent);
								id2node.put(id, subProcess);
								parseSubProcess(invocation, subProcess);
							}

						}
					}
				}
			}
		}
	}

	private void parseTransitions(Node transitions, SubProcess parent) {
		NodeList nl = transitions.getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeName().equals("xpdl2:Transition") || n.getNodeName().equals("Transition")) {
				//Transition, now parse it
				String fromID = getAttribute(n, "From");
				String toID = getAttribute(n, "To");
				if ((fromID == null) || (toID == null)) {
					System.out
							.println("org.processmining.plugins.bpmn.XPDLReader.parseTransitions - Transition with ID "
									+ getAttribute(n, "Id") + " has invalid to or from ID.");
				}
				BPMNNode fromNode = id2node.get(fromID);
				BPMNNode toNode = id2node.get(toID);
				if ((fromNode == null) || (toNode == null)) {
					System.out
							.println("org.processmining.plugins.bpmn.XPDLReader.parseTransitions - Transition with ID "
									+ getAttribute(n, "Id") + " has invalid to or from ID.");
				}

				if (parent == null) {
					Node nodegraphicsInfos = getChildNodeWithName(n, "xpdl2:ConnectorGraphicsInfos");
					if (nodegraphicsInfos!=null) {
						Node nodegraphicsInfo = getChildNodeWithName(nodegraphicsInfos,
								"xpdl2:ConnectorGraphicsInfo");
						BPMNNode bpmnNode = null;
						if (nodegraphicsInfo != null) {
							String laneID = getAttribute(nodegraphicsInfo, "LaneId");
							bpmnNode = id2node.get(laneID);
						}
						if (bpmnNode != null) {
							bpmndiagram.addFlow(fromNode, toNode, (Swimlane) bpmnNode, null);

						} else {
							bpmndiagram.addFlow(fromNode, toNode, null);
						}
					}
				} else {
					bpmndiagram.addFlow(fromNode, toNode, parent, null);
				}

			}
		}
	}

	private String getAttribute(Node n, String attribute) {
		String result = null;
		NamedNodeMap nnm = n.getAttributes();
		if (nnm != null) {
			Node ni = nnm.getNamedItem(attribute);
			if (ni != null) {
				result = ni.getNodeValue();
			}
		}
		return result;
	}

	/**
	 * Returns the first child node of childFrom with the given name. Returns
	 * null if there is no such child node.
	 */
	private Node getChildNodeWithName(Node childFrom, String name) {
		NodeList nl = childFrom.getChildNodes();
		Node childNode = null;

		for (int i = 0; (i < nl.getLength()) && (childNode == null); i++) {
			Node n = nl.item(i);
			if (n.getNodeName().equals(name) || n.getNodeName().equals(name.replaceAll("xpdl2:", ""))) {
				childNode = n;
			}
		}

		return childNode;
	}

	/**
	 * Returns the first child node of childFrom with the value of attribute Id
	 * equal to id. Returns null if there is no such child node.
	 */
	private Node getChildNodeWithID(Node childFrom, String id) {
		NodeList nl = childFrom.getChildNodes();
		Node childNode = null;

		for (int i = 0; (i < nl.getLength()) && (childNode == null); i++) {
			Node n = nl.item(i);
			String childId = getAttribute(n, "Id");
			if ((childId != null) && (childId.equals(id))) {
				childNode = n;
			}
		}

		return childNode;
	}
}
