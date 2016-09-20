package ee.ut.bpmn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	public HashMap<String, String> mapNew2OldLbls = new HashMap<String, String>();
	
	private PetriNet net = new PetriNet();
	private org.jbpt.petri.PetriNet netJBPT = new org.jbpt.petri.PetriNet();
	
	static boolean ADD_OK_NOK = true;
	static boolean USE_SIMPLE = false;

	private Set<Subprocess> subprocesses = new HashSet<>();

	private HashSet<String> taskLabels = new HashSet<>();
	
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
				
				bp.addControlFlow(map.get(source), map.get(target));
				
				if (source != null && target != null)
					source.connectTo(target.getInputPlace());
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
		
		this.net = jbptToUma(netJBPT);
    }
    
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

		node.setParent((Subprocess)nodes.get(parentId));		
	}

//	public PetriNet getPetriNet() {
//    	return PetriNetUtils.jbptToUma(net);
//    }
    
	private void addErrorEvent(Element endEvent, Element parentProcess) {
		// TODO Auto-generated method stub
		
	}

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
		
		node.setParent((Subprocess)nodes.get(parentId));
	}

	@SuppressWarnings("deprecation")
	private void addTask(Element task, Element parentProcess, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp) {
		String id = task.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");
		Subprocess parent = (Subprocess)nodes.get(parentId);
		
		Node node;
		if (parent.isRootProcess() || USE_SIMPLE) node = new SimpleTask(task, netJBPT, mapNew2OldLbls);
		else node = new Task(task, netJBPT, mapNew2OldLbls);
		
		nodes.put(id, node);
		map.put(node, new Activity(task.getAttributeValue("name").trim()));
		map.get(node).setId(id);
//		taskLabels.add(task.getAttributeValue("name").trim());
		taskLabels.add(node.getTransition().getName());
//		System.out.println(node.getTransition().getName());
		mapNew2OldLbls.put(node.getTransition().getName(), task.getAttributeValue("name").trim());
		bp.addTask((Activity) map.get(node));
		nodesBPMN.put(node.getTransition(), map.get(node));
		
		node.setParent(parent);
	}

	private void addStartEvent(Element startEvent, Element parentProcess, Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bp) {
		String id = startEvent.getAttributeValue("id");
		String parentId = parentProcess.getAttributeValue("id");
		Subprocess parent = (Subprocess)nodes.get(parentId);
		
		Node node;
		if (parent.isRootProcess() || USE_SIMPLE) node = new SimpleTask(startEvent, netJBPT, mapNew2OldLbls);
		else node = new Task(startEvent, netJBPT, mapNew2OldLbls);
		
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
	
	public PetriNet getNet(){
		return net;
	}
}
