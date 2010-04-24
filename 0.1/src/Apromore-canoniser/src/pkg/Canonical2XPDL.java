package pkg;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.rlf.RelationType;
import org.apromore.rlf.RelationsType;
import org.wfmc._2008.xpdl2.Activities;
import org.wfmc._2008.xpdl2.Activity;
import org.wfmc._2008.xpdl2.ConnectorGraphicsInfo;
import org.wfmc._2008.xpdl2.ConnectorGraphicsInfos;
import org.wfmc._2008.xpdl2.Coordinates;
import org.wfmc._2008.xpdl2.EndEvent;
import org.wfmc._2008.xpdl2.Event;
import org.wfmc._2008.xpdl2.Implementation;
import org.wfmc._2008.xpdl2.IntermediateEvent;
import org.wfmc._2008.xpdl2.Join;
import org.wfmc._2008.xpdl2.NodeGraphicsInfo;
import org.wfmc._2008.xpdl2.NodeGraphicsInfos;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.ProcessType;
import org.wfmc._2008.xpdl2.Route;
import org.wfmc._2008.xpdl2.Split;
import org.wfmc._2008.xpdl2.StartEvent;
import org.wfmc._2008.xpdl2.Task;
import org.wfmc._2008.xpdl2.Transition;
import org.wfmc._2008.xpdl2.TransitionRef;
import org.wfmc._2008.xpdl2.TransitionRefs;
import org.wfmc._2008.xpdl2.TransitionRestriction;
import org.wfmc._2008.xpdl2.TransitionRestrictions;
import org.wfmc._2008.xpdl2.Transitions;
import org.wfmc._2008.xpdl2.WorkflowProcesses;

public class Canonical2XPDL {
	Map<NodeType, Activity> canon2xpdl = new HashMap<NodeType, Activity>();
	Map<EdgeType, Transition> edge2flow = new HashMap<EdgeType, Transition>();
	Map<BigInteger, NodeType> nodeRefMap = new HashMap<BigInteger, NodeType>();
	Map<BigInteger, EdgeType> edgeRefMap = new HashMap<BigInteger, EdgeType>();
	Map<String, Activity> xpdlRefMap = new HashMap<String, Activity>();
	Map<BigInteger, String> cid2xid = new HashMap<BigInteger, String>();
	
	Map<String, List<Transition>> outgoingFlows = new HashMap<String, List<Transition>>();
	Map<String, List<Transition>> incomingFlows = new HashMap<String, List<Transition>>();
	
	Map<EventType, Event> events = new HashMap<EventType, Event>();
	Map<RoutingType, TransitionRestrictions> gateways = new HashMap<RoutingType, TransitionRestrictions>();
	
	/**
	 * de-canonize data (canonical) into xpdl using rlf and anf data
	 * @param data
	 * @param xpdl
	 * @param rlf
	 * @param anf
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public Canonical2XPDL(InputStream data, OutputStream xpdl, InputStream rlf, InputStream anf) throws JAXBException {
		
		JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
		CanonicalProcessType cproc = rootElement.getValue();
		
		jc = JAXBContext.newInstance("org.apromore.rlf");
		u = jc.createUnmarshaller();
		JAXBElement<RelationsType> relsRootElement = (JAXBElement<RelationsType>) u.unmarshal(rlf);
		RelationsType rels = relsRootElement.getValue();

		jc = JAXBContext.newInstance("org.apromore.anf");
		u = jc.createUnmarshaller();
		JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf);
		AnnotationsType annotations = anfRootElement.getValue();

		PackageType pkg = new PackageType();
		pkg.setWorkflowProcesses(new WorkflowProcesses());
		
		for (NetType net: cproc.getNet()) {
			ProcessType bpmnproc = new ProcessType();
			translateNet(bpmnproc, net, rels, annotations);
			pkg.getWorkflowProcesses().getWorkflowProcess().add(bpmnproc);
		}
		
		jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
		Marshaller m = jc.createMarshaller();
		m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		JAXBElement<PackageType> cprocRootElem = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(pkg);
		m.marshal(cprocRootElem, xpdl);
	}

	private void translateNet(ProcessType bpmnproc, NetType net, RelationsType rels, AnnotationsType annotations) {
		Activities acts = new Activities();
		Transitions trans = new Transitions();
		
		bpmnproc.getContent().add(acts);
		bpmnproc.getContent().add(trans);
		
		for (NodeType node: net.getNode()) {
			Activity act = translateNode(bpmnproc, node);
			acts.getActivity().add(act);
		}
		
		mapNodeRelations(bpmnproc, net, rels);
		mapNodeAnnotations(bpmnproc, annotations);
		
		for (EdgeType edge: net.getEdge()) {
			Transition flow = translateEdge(bpmnproc, edge);
			trans.getTransition().add(flow);
		}
		
		mapEdgeRelations(bpmnproc, rels);
		mapEdgeAnnotations(bpmnproc, annotations);
		
		completeMapping(bpmnproc, net, rels, annotations);
	}

	private void completeMapping(ProcessType bpmnproc, NetType net, RelationsType rels,
			AnnotationsType annotations) {
		for (EventType event: events.keySet()) {
			Activity act = canon2xpdl.get(event);
			Event xevent = events.get(event);
			if (incomingFlows.get(act.getId()) == null) {
				StartEvent startEvent = new StartEvent();
				if (event instanceof MessageType)
					startEvent.setTrigger("Message");
				else
					startEvent.setTrigger("None");
				xevent.setStartEvent(startEvent);
			} else if (outgoingFlows.get(act.getId()) == null) {
				EndEvent endEvent = new EndEvent();
				if (event instanceof MessageType)
					endEvent.setResult("Message");
				else
					endEvent.setResult("None");
				xevent.setEndEvent(endEvent);				
			} else {
				IntermediateEvent endEvent = new IntermediateEvent();
				if (event instanceof MessageType)
					endEvent.setTrigger("Message");
				else if (event instanceof TimerType)
					endEvent.setTrigger("Timer");
				else
					endEvent.setTrigger("None");
				xevent.setIntermediateEvent(endEvent);				
			}
		}
		for (RoutingType croute: gateways.keySet()) {
			Activity act = canon2xpdl.get(croute);
			TransitionRestrictions trests = gateways.get(croute);
			if (outgoingFlows.get(act.getId()).size() > 1) {
				for (TransitionRestriction trest: trests.getTransitionRestriction()) {
					Split split = trest.getSplit();
					TransitionRefs refs = split.getTransitionRefs();
					for (Transition trans: outgoingFlows.get(act.getId())) {
						TransitionRef ref = new TransitionRef();
						ref.setId(trans.getId());
						refs.getTransitionRef().add(ref);
					}
				}
			}
		}
	}

	private void mapNodeAnnotations(ProcessType bpmnproc,
			AnnotationsType annotations) {
		for (AnnotationType annotation: annotations.getAnnotation()) {
			if (nodeRefMap.containsKey(annotation.getCpfId())) {
				// TODO: Handle 1-N mappings
				BigInteger cid = annotation.getCpfId();
				NodeType node = nodeRefMap.get(cid);
				Activity act = canon2xpdl.get(node);
				
				if (annotation instanceof GraphicsType) {
					GraphicsType cGraphInfo = (GraphicsType)annotation;
					NodeGraphicsInfos infos = new NodeGraphicsInfos();
					NodeGraphicsInfo info = new NodeGraphicsInfo();
					
					if (cGraphInfo.getFill() != null)
						// TODO: Parse color format
						info.setFillColor(cGraphInfo.getFill().getColor());
					
					info.setHeight(cGraphInfo.getSize().getHeight().doubleValue());
					info.setWidth(cGraphInfo.getSize().getWidth().doubleValue());
					
					Coordinates coords = new Coordinates();
					coords.setXCoordinate(cGraphInfo.getPosition().get(0).getX().doubleValue());
					coords.setYCoordinate(cGraphInfo.getPosition().get(0).getY().doubleValue());
					info.setCoordinates(coords);
					
					infos.getNodeGraphicsInfo().add(info);
					act.getContent().add(infos);
				}
			}			
		}
	}

	private void mapEdgeAnnotations(ProcessType bpmnproc,
			AnnotationsType annotations) {
		for (AnnotationType annotation: annotations.getAnnotation()) {
			if (edgeRefMap.containsKey(annotation.getCpfId())) {
				// TODO: Handle 1-N mappings
				BigInteger cid = annotation.getCpfId();
				EdgeType edge = edgeRefMap.get(cid);
				Transition flow = edge2flow.get(edge);
				
				if (annotation instanceof GraphicsType) {
					GraphicsType cGraphInfo = (GraphicsType)annotation;
					
					ConnectorGraphicsInfos infos = new ConnectorGraphicsInfos();
					ConnectorGraphicsInfo info = new ConnectorGraphicsInfo();
					
					for (PositionType pos: cGraphInfo.getPosition()) {
						Coordinates coords = new Coordinates();
						coords.setXCoordinate(pos.getX().doubleValue());
						coords.setYCoordinate(pos.getY().doubleValue());
						info.getCoordinates().add(coords);
					}
					
					infos.getConnectorGraphicsInfo().add(info);
					flow.setConnectorGraphicsInfos(infos);
				}
			}			
		}
	}

	private void mapNodeRelations(ProcessType bpmnproc, NetType net, RelationsType rels) {
		for (RelationType rel: rels.getRelation()) {
			if (nodeRefMap.containsKey(rel.getCpfId().get(0))) {
				// TODO: Handle 1-N mappings
				BigInteger cid = rel.getCpfId().get(0);
				String xid = rel.getNpfId();
				NodeType node = nodeRefMap.get(cid);
				Activity act = canon2xpdl.get(node);
				act.setId(xid);
				xpdlRefMap.put(xid, act);
				
				cid2xid.put(cid, xid);
			} else if (rel.getCpfId().get(0).equals(net.getId()))
				bpmnproc.setId(rel.getNpfId());
		}
	}

	private void mapEdgeRelations(ProcessType bpmnproc, RelationsType rels) {
		for (RelationType rel: rels.getRelation()) {
			if (edgeRefMap.containsKey(rel.getCpfId().get(0))) {
				BigInteger cid = rel.getCpfId().get(0);
				String xid = rel.getNpfId();
				EdgeType edge = edgeRefMap.get(cid);
				Transition flow = edge2flow.get(edge);
				flow.setId(xid);				
				cid2xid.put(cid, xid);				
			}
		}
	}
	
	private Transition translateEdge(ProcessType bpmnproc, EdgeType edge) {
		Transition flow = new Transition();
		flow.setFrom(cid2xid.get(edge.getSourceId()));
		flow.setTo(cid2xid.get(edge.getTargetId()));
		edge2flow.put(edge, flow);
		edgeRefMap.put(edge.getId(), edge);
		
		List<Transition> list = outgoingFlows.get(flow.getFrom());
		if (list == null) {
			list = new LinkedList<Transition>();
			outgoingFlows.put(flow.getFrom(), list);
		}
		list.add(flow);
		
		list = incomingFlows.get(flow.getTo());
		if (list == null) {
			list = new LinkedList<Transition>();
			incomingFlows.put(flow.getTo(), list);
		}
		list.add(flow);
		
		return flow;
	}

	private Activity translateNode(ProcessType bpmnproc, NodeType node) {
		Activity act = null;
		if (node instanceof TaskType) {
			act = translateTask(bpmnproc, node);
		} else if (node instanceof RoutingType) {
			act = translateGateway(bpmnproc, node);
		} else if (node instanceof EventType)
			act = translateEvent(bpmnproc, node);

		canon2xpdl.put(node, act);
		nodeRefMap.put(node.getId(), node);
		
		return act;
	}

	private Activity translateTask(ProcessType bpmnproc, NodeType node) {
		Activity act = new Activity();
		act.setName(node.getName());
		Implementation impl = new Implementation();
		act.getContent().add(impl);
		Task task = new Task();
		impl.setTask(task);
		return act;
	}

	private Activity translateGateway(ProcessType bpmnproc, NodeType node) {
		Activity act = new Activity();
		Route route = new Route();
		TransitionRestrictions trests = new TransitionRestrictions();

		if (node instanceof ANDSplitType) {
			TransitionRestriction trest = createSplitGateway(route, "Parallel", null);
			trests.getTransitionRestriction().add(trest);
		}
		
		if (node instanceof ANDJoinType) {
			TransitionRestriction trest = createJoinGateway(route, "Parallel");
			trests.getTransitionRestriction().add(trest);
		}

		if (node instanceof XORSplitType) {
			TransitionRestriction trest = createSplitGateway(route, "Exclusive", "Data");
			trests.getTransitionRestriction().add(trest);
		}
		
		if (node instanceof XORJoinType) {
			TransitionRestriction trest = createJoinGateway(route, "Exclusive");
			trests.getTransitionRestriction().add(trest);
		}

		if (node instanceof ORSplitType) {
			TransitionRestriction trest = createSplitGateway(route, "Inclusive", null);
			trests.getTransitionRestriction().add(trest);			
		}
		
		if (node instanceof ORJoinType) {
			TransitionRestriction trest = createJoinGateway(route, "Inclusive");
			trests.getTransitionRestriction().add(trest);			
		}
		
		act.getContent().add(route);
		act.getContent().add(trests);
		
		gateways.put((RoutingType)node, trests);
		return act;
	}

	private TransitionRestriction createSplitGateway(Route route, String type, String extra) {
		route.setGatewayType(type);
		TransitionRestriction trest = new TransitionRestriction();
		Split split = new Split();
		TransitionRefs refs = new TransitionRefs();
		split.setTransitionRefs(refs);
		split.setType(type);
		if (extra != null)
			split.setExclusiveType(extra);
		trest.setSplit(split);
		return trest;
	}

	private TransitionRestriction createJoinGateway(Route route, String type) {
		route.setGatewayType(type);
		TransitionRestriction trest = new TransitionRestriction();
		Join join = new Join();
		join.setType(type);
		trest.setJoin(join);
		return trest;
	}

	private Activity translateEvent(ProcessType bpmnproc, NodeType node) {
		Activity act = new Activity();
		Event event = new Event();
		
		act.getContent().add(event);
		
		events.put((EventType)node, event);
		return act;
	}

}
