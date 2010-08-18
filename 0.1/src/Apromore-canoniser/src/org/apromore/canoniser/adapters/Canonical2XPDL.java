package org.apromore.canoniser.adapters;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

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
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
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
import org.wfmc._2008.xpdl2.Lane;
import org.wfmc._2008.xpdl2.Lanes;
import org.wfmc._2008.xpdl2.NodeGraphicsInfo;
import org.wfmc._2008.xpdl2.NodeGraphicsInfos;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.Pool;
import org.wfmc._2008.xpdl2.Pools;
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
	List<BigInteger> resource_ref_list = new LinkedList<BigInteger>();
	
	private PackageType xpdl; 
	/**
	 * de-canonize data (canonical) into xpdl
	 * @param data 
	 * @param xpdl 
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public Canonical2XPDL(CanonicalProcessType cpf) throws JAXBException {
		
		this.xpdl = new PackageType();
		this.xpdl.setWorkflowProcesses(new WorkflowProcesses());
		this.xpdl.setPools(new Pools());
		
		for (NetType net: cpf.getNet()) {
			ProcessType bpmnproc = new ProcessType();
			bpmnproc.setId(net.getId().toString());
			translateNet(bpmnproc, net);
			translateResources(bpmnproc, cpf);
			this.xpdl.getWorkflowProcesses().getWorkflowProcess().add(bpmnproc);
		}
		
	}
	
	/**
	 * de-canonize data (canonical) into xpdl using anf data
	 * @param data
	 * @param xpdl
	 * @param anf
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public Canonical2XPDL(CanonicalProcessType cpf, AnnotationsType anf) throws JAXBException {
		
		this.xpdl = new PackageType();
		this.xpdl.setWorkflowProcesses(new WorkflowProcesses());
		this.xpdl.setPools(new Pools());
		
		for (NetType net: cpf.getNet()) {
			ProcessType bpmnproc = new ProcessType();
			bpmnproc.setId(net.getId().toString());
			translateNet(bpmnproc, net, anf);
			translateResources(bpmnproc, cpf);
			this.xpdl.getWorkflowProcesses().getWorkflowProcess().add(bpmnproc);
		}
		
	}

	private void translateResources(ProcessType bpmnproc, CanonicalProcessType cpf)
	{
		if(resource_ref_list.size() == 1)
		{
			Pool p = new Pool();
			//p.setName(value);
			p.setId(resource_ref_list.get(0).toString());
			p.setProcess(bpmnproc.getId());
			this.xpdl.getPools().getPool().add(p);
		}
		else if (resource_ref_list.size() > 1) {
			for(ResourceTypeType res: cpf.getResourceType())
			{
				if(resource_ref_list.contains(res.getId()) && res.getSpecializationIds().size() > 0)
				{
					Pool p = new Pool();
					p.setName(res.getName());
					p.setId(res.getId().toString());
					p.setProcess(bpmnproc.getId());
					p.setLanes(new Lanes());
					for(BigInteger id : res.getSpecializationIds())
					{
						Lane lane = new Lane();
						lane.setId(id.toString());
						//lane.setName(value);
						p.getLanes().getLane().add(lane);
					}
				}
					
			}
		}
		
		resource_ref_list.clear();
	}
	
	private void translateNet(ProcessType bpmnproc, NetType net) {
		Activities acts = new Activities();
		Transitions trans = new Transitions();
		
		bpmnproc.getContent().add(acts);
		bpmnproc.getContent().add(trans);
		
		for (NodeType node: net.getNode()) {
			Activity act = translateNode(bpmnproc, node);
			acts.getActivity().add(act);
		}
		setActivitiesId(bpmnproc);
		
		for (EdgeType edge: net.getEdge()) {
			Transition flow = translateEdge(bpmnproc, edge);
			trans.getTransition().add(flow);
		}
		setTransitionsId(bpmnproc);
		
		completeMapping(bpmnproc, net);
	}
	
	private void translateNet(ProcessType bpmnproc, NetType net,  AnnotationsType annotations) {
		Activities acts = new Activities();
		Transitions trans = new Transitions();
		
		bpmnproc.getContent().add(acts);
		bpmnproc.getContent().add(trans);
		
		for (NodeType node: net.getNode()) {
			Activity act = translateNode(bpmnproc, node);
			acts.getActivity().add(act);
		}
		
		setActivitiesId(bpmnproc);
		mapNodeAnnotations(bpmnproc, annotations);
		
		for (EdgeType edge: net.getEdge()) {
			Transition flow = translateEdge(bpmnproc, edge);
			trans.getTransition().add(flow);
		}
		
		setTransitionsId(bpmnproc);
		mapEdgeAnnotations(bpmnproc, annotations);
		
		completeMapping(bpmnproc, net);
	}

	private void completeMapping(ProcessType bpmnproc, NetType net) {
		for (EventType event: events.keySet()) {
			Activity act = canon2xpdl.get(event);
			act.setName(event.getName());
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
			
			if (outgoingFlows.get(act.getId()) != null) {
				if (outgoingFlows.get(act.getId()).size() > 1) {
					for (TransitionRestriction trest : trests
							.getTransitionRestriction()) {
						Split split = trest.getSplit();
						if (split != null) {
							TransitionRefs refs = split.getTransitionRefs();
							for (Transition trans : outgoingFlows.get(act
									.getId())) {
								TransitionRef ref = new TransitionRef();
								ref.setId(trans.getId());
								refs.getTransitionRef().add(ref);
							}
						}
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
					
					if(cGraphInfo.getSize() != null && cGraphInfo.getSize().getHeight() != null && cGraphInfo.getSize().getWidth() != null) {
						info.setHeight(cGraphInfo.getSize().getHeight().doubleValue());
						info.setWidth(cGraphInfo.getSize().getWidth().doubleValue());
					}
					Coordinates coords = new Coordinates();
					if(cGraphInfo.getPosition() != null)
					{
						try {
							coords.setXCoordinate(cGraphInfo.getPosition().get(0).getX().doubleValue());
							coords.setYCoordinate(cGraphInfo.getPosition().get(0).getY().doubleValue());
							info.setCoordinates(coords);
						} catch (IndexOutOfBoundsException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						} catch (NullPointerException e) {
							
						}
					}
					
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
	
	private void setActivitiesId(ProcessType bpmnproc)
	{
		for(NodeType node: nodeRefMap.values())
		{
			Activity act = canon2xpdl.get(node);
			act.setId(node.getId().toString());
			xpdlRefMap.put(node.getId().toString(), act);
			cid2xid.put(node.getId(), act.getId());
		}
	}

	private void setTransitionsId(ProcessType bpmnproc)
	{
		for(EdgeType edge: edgeRefMap.values())
		{
			Transition flow = edge2flow.get(edge);
			flow.setId(edge.getId().toString());
			cid2xid.put(edge.getId(), flow.getId());
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
			for(ResourceTypeRefType ref: ((TaskType)node).getResourceTypeRef())
			{
				if(!resource_ref_list.contains(ref.getResourceTypeId()))
					resource_ref_list.add(ref.getResourceTypeId());
			}
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

	public PackageType getXpdl() {
		return xpdl;
	}

}
