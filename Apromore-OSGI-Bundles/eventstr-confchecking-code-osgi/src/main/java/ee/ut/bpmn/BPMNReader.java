/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package ee.ut.bpmn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ee.ut.bpmn.elements.BoundaryEvent;
import ee.ut.bpmn.elements.EndEvent;
import ee.ut.bpmn.elements.ExclusiveGateway;
import ee.ut.bpmn.elements.Node;
import ee.ut.bpmn.elements.ParallelGateway;
import ee.ut.bpmn.elements.SimpleEndEvent;
import ee.ut.bpmn.elements.SimpleTask;
import ee.ut.bpmn.elements.Subprocess;
import ee.ut.bpmn.elements.Task;
import ee.ut.bpmn.elements.TerminateEvent;
//import hub.top.petrinet.PetriNet;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.Flow;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.jbpt.pm.bpmn.StartEvent;

public class BPMNReader {
    public static Namespace ns = Namespace.getNamespace("bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL");

	private Map<String, Node> nodes = new HashMap<>();
	private BiMap<org.jbpt.petri.Node, FlowNode> nodesBPMN = HashBiMap.<org.jbpt.petri.Node, FlowNode>create();
	private BiMap<Node, FlowNode> map = HashBiMap.<Node, FlowNode>create();
	private Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp = new Bpmn<BpmnControlFlow<FlowNode>, FlowNode>();
//	public HashMap<String, String> mapNew2OldLbls = new HashMap<String, String>();
//    public HashMap<String, Integer> labelCounter = new HashMap<String, Integer>();
	
	private PetriNet net = new PetriNet();
	private org.jbpt.petri.PetriNet netJBPT = new org.jbpt.petri.PetriNet();
	
	static boolean ADD_OK_NOK = true;
	static boolean USE_SIMPLE = false;

	private Set<Subprocess> subprocesses = new HashSet<>();

	private HashSet<String> taskLabels = new HashSet<>();

    private Multimap<FlowNode, org.jbpt.petri.Node> tasksJBPTTrans = HashMultimap.<FlowNode, org.jbpt.petri.Node> create();// HashBiMap.<FlowNode, org.jbpt.petri.Node> create();
    private Multimap<org.jbpt.petri.Node, FlowNode> tasksJBPTTransReverse = HashMultimap.<org.jbpt.petri.Node, FlowNode> create();;// HashBiMap.<FlowNode, org.jbpt.petri.Node> create();

    private BiMap<FlowNode, hub.top.petrinet.Node> tasksUMATrans;
    private HashMap<hub.top.petrinet.Node, FlowNode> tasksUMATransReverse;
	
    public BPMNReader(InputStream input) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();  
        
		Document document = saxBuilder.build(input);
		
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> procExp = xpfac.compile("//bpmn:process", Filters.element(), null, ns);
		XPathExpression<Element> subprocExp = xpfac.compile(".//bpmn:subProcess", Filters.element(), null, ns);
		XPathExpression<Element> taskExp = xpfac.compile(".//bpmn:task", Filters.element(), null, ns);
		XPathExpression<Element> gatewaySelector = xpfac.compile(".//*[ends-with(name(),'ateway')]", Filters.element(), null, ns);
		
		XPathExpression<Element> startEventSelector = xpfac.compile(".//bpmn:startEvent", Filters.element(), null, ns);
		XPathExpression<Element> sequenceFlowSelector = xpfac.compile(".//bpmn:sequenceFlow", Filters.element(), null, ns);
		XPathExpression<Element> endEventSelector = xpfac.compile(".//bpmn:endEvent", Filters.element(), null, ns);
		XPathExpression<Element> boundaryEventSelector = xpfac.compile(".//bpmn:boundaryEvent", Filters.element(), null, ns);
		
		XPathExpression<Element> parentExp = xpfac.compile("(./ancestor::*[name() ='subProcess' or name() = 'process'])[last()]", Filters.element(), null, ns);

		for (Element process : procExp.evaluate(document) ) {
			addProcess(process);
			
			for (Element subprocess: subprocExp.evaluate(process)) {
				Element parentProcess = parentExp.evaluateFirst(subprocess);
				addSubprocess(subprocess, parentProcess);
			}

			for (Element task: taskExp.evaluate(process)) {
				Element parentProcess = parentExp.evaluateFirst(task);				
				addTask(task, parentProcess, bp);
			}
			
			for (Element gateway: gatewaySelector.evaluate(process)) {
				Element parentProcess = parentExp.evaluateFirst(gateway);				
				if (gateway.getName().equals("exclusiveGateway"))
					addExclusiveGateway(gateway, parentProcess, bp);
				else if (gateway.getName().equals("parallelGateway"))
					addParallelGateway(gateway, parentProcess, bp);
			}
			
			for (Element startEvent: startEventSelector.evaluate(process)) {
				Element parentProcess = parentExp.evaluateFirst(startEvent);				
				addStartEvent(startEvent, parentProcess, bp);
			}
			
			for (Element endEvent: endEventSelector.evaluate(process)) {
				Element parentProcess = parentExp.evaluateFirst(endEvent);
				
				if (endEvent.getChild("terminateEventDefinition", ns) != null)
					addTerminateEvent(endEvent, parentProcess, bp);
				else if (endEvent.getChild("errorEvent", ns) != null)
					addErrorEvent(endEvent, parentProcess);
				else
					addEndEvent(endEvent, parentProcess, bp);
			}
			
			for (Element boundaryEvent: boundaryEventSelector.evaluate(process)) {
				Node carryingProcess = nodes.get(boundaryEvent.getAttributeValue("attachedToRef"));
				addBoundaryEvent(boundaryEvent, (Subprocess)carryingProcess);
			}
			
			
			for (Element sequenceFlow: sequenceFlowSelector.evaluate(process)) {
				String sourceRef = sequenceFlow.getAttributeValue("sourceRef");
				String targetRef = sequenceFlow.getAttributeValue("targetRef");
				Node source = nodes.get(sourceRef);
				Node target = nodes.get(targetRef);

				BpmnControlFlow flow = bp.addControlFlow(map.get(source), map.get(target));
				flow.setId(sequenceFlow.getAttributeValue("id"));
				System.out.println(flow.getId());
				
				if (source != null && target != null) {
                    source.connectTo(target.getInputPlace());

                    if(source instanceof ExclusiveGateway && bp.getAllSuccessors(map.get(source)).size() >= 2) {
                        tasksJBPTTransReverse.put(source.getTransition(), map.get(source));
                    }
                }
			}
			
			Node proc = nodes.get(process.getAttributeValue("id"));
			if (proc != null)
				((Subprocess)proc).addFinalPlace();
		}
		
		for (Subprocess subprocess: subprocesses)
			subprocess.wireTerminateEvents();
		
		if (ADD_OK_NOK)
			for (Node node: nodes.values()) {
				if (node instanceof Task) {
					Task task = (Task) node;
					Subprocess parent = node.getParent();
					
					task.connectToOk(parent.getOk());
					task.connectToNOk(parent.getNOk());
				} else if (node instanceof EndEvent) {
					EndEvent endEvent = (EndEvent) node;
					Subprocess parent = node.getParent();
					
					endEvent.connectToOk(parent.getOk());
					endEvent.connectToNOk(parent.getNOk());
				}
			}

        this.tasksUMATrans = HashBiMap.<FlowNode, hub.top.petrinet.Node> create();
        this.tasksUMATransReverse = new HashMap<>();
		this.net = jbptToUma(netJBPT);
        System.out.println();
    }


//	TODO: plug map transformation
    public PetriNet jbptToUma(org.jbpt.petri.PetriNet net) {
        PetriNet copy = new PetriNet();
        Map<Vertex, Place> places = new HashMap<>();
        Map<Vertex, Transition> transitions = new HashMap<>();

        int index = 0;

        for (org.jbpt.petri.Place place: net.getPlaces()) {
            Place newPlace = copy.addPlace("p" + index++);
            places.put(place, newPlace);
        }

        for (org.jbpt.petri.Transition trans: net.getTransitions()) {
            String name = trans.getLabel()== null  || trans.getLabel().isEmpty() ? "t" + index++ : trans.getLabel();
            Transition newTrans = copy.addTransition(name);
            transitions.put(trans, newTrans);

            if(tasksJBPTTransReverse.containsKey(trans))
                for(FlowNode node : tasksJBPTTransReverse.get(trans)) {
                    tasksUMATrans.put(node, newTrans);
                    tasksUMATransReverse.put(newTrans, node);
                }
        }

        for (Flow flow: net.getFlow()) {
            if (flow.getSource() instanceof org.jbpt.petri.Place)
                copy.addArc(places.get(flow.getSource()), transitions.get(flow.getTarget()));
            else
                copy.addArc(transitions.get(flow.getSource()), places.get(flow.getTarget()));
        }

        for (org.jbpt.petri.Place place: net.getSourcePlaces())
            places.get(place).setTokens(1);

        return copy;
    }
    
	private void addParallelGateway(Element gateway, Element parentProcess, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp) {
		String id = gateway.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");
		
		Node node = new ParallelGateway(gateway, netJBPT);
		nodes.put(id, node);
		map.put(node, new AndGateway());
		map.get(node).setId(id);
		bp.addGateway((Gateway) map.get(node));

        // Mapping for the nodes and the transitions resulting from the transformation
        nodesBPMN.put(node.getTransition(), map.get(node));
        tasksJBPTTrans.put(map.get(node), node.getTransition());
        tasksJBPTTransReverse.put(node.getTransition(),map.get(node));

		node.setParent((Subprocess)nodes.get(parentId));		
	}

	private void addErrorEvent(Element endEvent, Element parentProcess) { /* TODO Auto-generated method stub */ }

	private void addTerminateEvent(Element endEvent, Element parentProcess, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp) {
		String id = endEvent.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");
		TerminateEvent node = new TerminateEvent(endEvent, netJBPT);
		nodes.put(id, node);
		map.put(node, new org.jbpt.pm.bpmn.EndEvent());
		map.get(node).setId(id);
		bp.addFlowNode(map.get(node));
		
		Subprocess parent = (Subprocess)nodes.get(parentId);
		node.setParent(parent);
		parent.addTerminateEvent(node);
	}

	private void addEndEvent(Element endEvent, Element parentProcess, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp) {
		String id = endEvent.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");
		Subprocess parent = (Subprocess)nodes.get(parentId);
		
		SimpleEndEvent node;
		if (parent.isRootProcess())
			node = new SimpleEndEvent(endEvent, netJBPT);
		else 
			node = new EndEvent(endEvent, netJBPT);
		
		nodes.put(id, node);
		map.put(node, new org.jbpt.pm.bpmn.EndEvent());
		map.get(node).setId(id);
		
		node.setParent(parent);
		parent.addEndEvent(node);
		bp.addFlowNode(map.get(node));
	}
	
	private void addBoundaryEvent(Element boundaryEvent,
			Subprocess carryingProcess) {
		String id = boundaryEvent.getAttributeValue("id");		
		BoundaryEvent node = new BoundaryEvent(boundaryEvent, netJBPT);
		nodes.put(id, node);

		carryingProcess.attachBoundaryEvent(node);
	}

	@SuppressWarnings("deprecation")
	private void addExclusiveGateway(Element gateway, Element parentProcess, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp) {
		String id = gateway.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");
		
		Node node = new ExclusiveGateway(gateway, netJBPT);
		nodes.put(id, node);
		map.put(node, new XorGateway());
		map.get(node).setId(id);
		bp.addGateway((Gateway) map.get(node));

        // Mapping for the nodes and the transitions resulting from the transformation
        nodesBPMN.put(node.getTransition(), map.get(node));
        tasksJBPTTrans.put(map.get(node), node.getTransition());
        tasksJBPTTransReverse.put(node.getTransition(), map.get(node));

		node.setParent((Subprocess)nodes.get(parentId));
	}

	@SuppressWarnings("deprecation")
	private void addTask(Element task, Element parentProcess, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp) {
		String id = task.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");
		Subprocess parent = (Subprocess)nodes.get(parentId);
		
		Node node;
		if (parent.isRootProcess() || USE_SIMPLE) node = new SimpleTask(task, netJBPT);//, mapNew2OldLbls, labelCounter);
		else node = new Task(task, netJBPT);//, mapNew2OldLbls);

        String originalName = task.getAttributeValue("name").trim();

		nodes.put(id, node);
		map.put(node, new Activity(originalName));
		map.get(node).setId(id);

		taskLabels.add(node.getTransition().getName());
//		mapNew2OldLbls.put(node.getTransition().getName(), originalName);
//
//        if(!labelCounter.containsKey(originalName))
//            labelCounter.put(originalName, 0);
//        else
//            labelCounter.put(originalName, labelCounter.get(originalName) + 1);

        nodesBPMN.put(node.getTransition(), map.get(node));
        tasksJBPTTrans.put(map.get(node), node.getTransition());
        tasksJBPTTransReverse.put(node.getTransition(), map.get(node));

        bp.addTask((Activity) map.get(node));
        node.setParent(parent);
	}

	private void addStartEvent(Element startEvent, Element parentProcess, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp) {
		String id = startEvent.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");
		Subprocess parent = (Subprocess)nodes.get(parentId);
		
		Node node;
		if (parent.isRootProcess() || USE_SIMPLE) node = new SimpleTask(startEvent, netJBPT);//, mapNew2OldLbls, labelCounter);
		else node = new Task(startEvent, netJBPT);//, mapNew2OldLbls);
		
		nodes.put(id, node);
		map.put(node, new StartEvent());
		map.get(node).setId(id);
		bp.addFlowNode(map.get(node));

		node.setParent(parent);
		parent.addStartEvent(node);
	}

	private void addSubprocess(Element subprocess, Element parentProcess) {
		String id = subprocess.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");

		Subprocess node = new Subprocess(subprocess, netJBPT);
		nodes.put(id, node);
		subprocesses.add(node);

		node.setParent((Subprocess)nodes.get(parentId));		
	}

	private void addProcess(Element process) {
		String id = process.getAttributeValue("id");
		Node node = new Subprocess(process, netJBPT);
		nodes.put(id, node);
	}

	public HashSet<String> getTaskLabels() {
		return taskLabels;
	}
	
	public BiMap<org.jbpt.petri.Node, FlowNode> getNodesBPMN() {
		return nodesBPMN;
	}
	
	public BiMap<FlowNode, org.jbpt.petri.Node> getNodesBPMNInv() {
		return nodesBPMN.inverse();
	}
	
	public  Bpmn<BpmnControlFlow<FlowNode>, FlowNode> getModel() {
		return bp;
	}

	public Collection<FlowNode> getVertices() {
		return bp.getFlowNodes();
	}
	
	public PetriNet getNet(){ return net; }

    public BiMap<FlowNode, hub.top.petrinet.Node> getTasksUMATrans() { return tasksUMATrans; }
    public HashMap<hub.top.petrinet.Node, FlowNode> getTasksUMATransReverse() { return tasksUMATransReverse; }
}
