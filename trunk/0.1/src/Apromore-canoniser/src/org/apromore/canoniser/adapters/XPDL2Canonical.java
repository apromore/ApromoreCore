package org.apromore.canoniser.adapters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.anf.FillType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
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
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.rlf.RelationType;
import org.apromore.rlf.RelationsType;
import org.wfmc._2008.xpdl2.Activities;
import org.wfmc._2008.xpdl2.Activity;
import org.wfmc._2008.xpdl2.Condition;
import org.wfmc._2008.xpdl2.ConnectorGraphicsInfo;
import org.wfmc._2008.xpdl2.ConnectorGraphicsInfos;
import org.wfmc._2008.xpdl2.Coordinates;
import org.wfmc._2008.xpdl2.EndEvent;
import org.wfmc._2008.xpdl2.Event;
import org.wfmc._2008.xpdl2.Implementation;
import org.wfmc._2008.xpdl2.IntermediateEvent;
import org.wfmc._2008.xpdl2.NodeGraphicsInfo;
import org.wfmc._2008.xpdl2.NodeGraphicsInfos;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.ProcessType;
import org.wfmc._2008.xpdl2.Route;
import org.wfmc._2008.xpdl2.StartEvent;
import org.wfmc._2008.xpdl2.Transition;
import org.wfmc._2008.xpdl2.TransitionRestriction;
import org.wfmc._2008.xpdl2.TransitionRestrictions;
import org.wfmc._2008.xpdl2.Transitions;

public class XPDL2Canonical {

	List<Activity> activities = new LinkedList<Activity>();
	List<Transition> transitions = new LinkedList<Transition>();
	Map<String, Activity> xpdlRefMap = new HashMap<String, Activity>();
	Map<Activity, NodeType> xpdl2canon = new HashMap<Activity, NodeType>();
	Map<Transition, EdgeType> edgeMap = new HashMap<Transition, EdgeType>();
	Map<NodeType, NodeType> implicitANDSplit = new HashMap<NodeType, NodeType>();
	Map<NodeType, NodeType> implicitORSplit = new HashMap<NodeType, NodeType>();
	Map<NodeType, NodeType> implicitJoin = new HashMap<NodeType, NodeType>();
	Map<NodeType, List<EdgeType>> outgoings = new HashMap<NodeType, List<EdgeType>>();
	Set<NodeType> linked = new HashSet<NodeType>();

	long cpfId = 1;
	long rlfId = 1;
	long anfId = 1;

	CanonicalProcessType cpf;
	RelationsType rlf;
	AnnotationsType anf;

	public CanonicalProcessType getCpf() {
		return cpf;
	}

	public RelationsType getRlf() {
		return rlf;
	}

	public AnnotationsType getAnf() {
		return anf;
	}

	public XPDL2Canonical(PackageType pkg) throws JAXBException {

		this.cpf = new CanonicalProcessType();
		this.rlf = new RelationsType();
		this.anf = new AnnotationsType();

		this.cpf.setName(pkg.getName());

		for (ProcessType bpmnproc: pkg.getWorkflowProcesses().getWorkflowProcess()) {
			NetType net = new NetType();
			net.setId(BigInteger.valueOf(cpfId++));
			translateProcess(net, bpmnproc);
			recordRelations(bpmnproc, net, this.rlf);
			recordAnnotations(bpmnproc, this.anf);
			this.cpf.getNet().add(net);
		}
	}

	private void recordAnnotations(ProcessType bpmnproc,
			AnnotationsType annotations) {
		for (Activity act: activities) {
			GraphicsType cGraphInfo = new GraphicsType();
			cGraphInfo.setId(BigInteger.valueOf(anfId++));
			cGraphInfo.setCpfId(xpdl2canon.get(act).getId());
			for (Object obj: act.getContent()) {
				if (obj instanceof NodeGraphicsInfos) {
					for (NodeGraphicsInfo xGraphInfo: ((NodeGraphicsInfos)obj).getNodeGraphicsInfo()){
						if (xGraphInfo.getFillColor() != null) {
							FillType fill = new FillType();
							StringTokenizer tokenizer = new StringTokenizer(xGraphInfo.getFillColor(), ",");
							fill.setColor(String.format("R:%sG:%sB:%s", tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
							cGraphInfo.setFill(fill);
						}

						if (xGraphInfo.getCoordinates() != null) {
							PositionType pos = new PositionType();
							pos.setX(BigDecimal.valueOf(xGraphInfo.getCoordinates().getXCoordinate()));
							pos.setY(BigDecimal.valueOf(xGraphInfo.getCoordinates().getYCoordinate()));
							cGraphInfo.getPosition().add(pos);
						}

						SizeType size = new SizeType();
						size.setHeight(BigDecimal.valueOf(xGraphInfo.getHeight()));
						size.setWidth(BigDecimal.valueOf(xGraphInfo.getWidth()));
						cGraphInfo.setSize(size);
					}
				}
			}
			annotations.getAnnotation().add(cGraphInfo);
		}	

		for (Transition trans: transitions) {
			GraphicsType cGraphInfo = new GraphicsType();
			cGraphInfo.setId(BigInteger.valueOf(anfId++));
			cGraphInfo.setCpfId(edgeMap.get(trans).getId());
			ConnectorGraphicsInfos infos = trans.getConnectorGraphicsInfos();
			for (ConnectorGraphicsInfo xGraphInfo: infos.getConnectorGraphicsInfo()) {
				for (Coordinates coord: xGraphInfo.getCoordinates()) {
					PositionType pos = new PositionType();
					pos.setX(BigDecimal.valueOf(coord.getXCoordinate()));
					pos.setY(BigDecimal.valueOf(coord.getYCoordinate()));
					cGraphInfo.getPosition().add(pos);
				}
			}
			annotations.getAnnotation().add(cGraphInfo);
		}
	}

	private void recordRelations(ProcessType bpmnproc, NetType net, RelationsType rels) {
		RelationType rel0 = new RelationType();
		rel0.setId(BigInteger.valueOf(rlfId++));
		rel0.setNpfId(bpmnproc.getId());
		rel0.getCpfId().add(net.getId());
		rels.getRelation().add(rel0);

		for (Activity act: activities) {
			RelationType rel = new RelationType();
			rel.setId(BigInteger.valueOf(rlfId++));
			rel.setNpfId(act.getId());
			rel.getCpfId().add(xpdl2canon.get(act).getId());
			if (implicitANDSplit.containsKey(act))
				rel.getCpfId().add(implicitANDSplit.get(act).getId());
			if (implicitORSplit.containsKey(act))
				rel.getCpfId().add(implicitORSplit.get(act).getId());
			if (implicitJoin.containsKey(act))
				rel.getCpfId().add(implicitJoin.get(act).getId());
			rels.getRelation().add(rel);
		}

		for (Transition flow: transitions) {
			RelationType rel = new RelationType();
			rel.setId(BigInteger.valueOf(rlfId++));
			rel.setNpfId(flow.getId());
			rel.getCpfId().add(edgeMap.get(flow).getId());
			rels.getRelation().add(rel);			
		}
	}

	private void translateProcess(NetType net, ProcessType bpmnproc) {
		System.out.println(bpmnproc.getName());
		for (Object obj: bpmnproc.getContent()) {
			if (obj instanceof Activities)
				activities = ((Activities)obj).getActivity();
			else if (obj instanceof Transitions)
				transitions = ((Transitions)obj).getTransition();
		}

		for (Activity act: activities)
			translateActivity(net, act);

		for (Transition flow: transitions)
			translateSequenceFlow(net, flow);

		linkImplicitOrSplits(net);		
	}

	private void linkImplicitOrSplits(NetType net) {
		for (NodeType act: implicitORSplit.keySet()) {
			NodeType andSplit = implicitANDSplit.get(act);
			NodeType orSplit = implicitORSplit.get(act);

			if (outgoings.get(andSplit) != null && outgoings.get(andSplit).size() > 0)
				addEdge(net, andSplit, orSplit);
			else {
				EdgeType edge = outgoings.get(act).get(0);
				edge.setTargetId(orSplit.getId());
				net.getNode().remove(andSplit);
			}
		}
	}

	private void translateSequenceFlow(NetType net, Transition flow) {
		Activity xsrc = xpdlRefMap.get(flow.getFrom());
		Activity xtgt = xpdlRefMap.get(flow.getTo());

		NodeType csrc = xpdl2canon.get(xsrc);
		NodeType ctgt = xpdl2canon.get(xtgt);

		if (csrc instanceof TaskType && flow.getCondition() != null) {
			Condition cond = flow.getCondition();
			System.out.println("Condition type: " + cond.getType());

			NodeType split = implicitORSplit.get(csrc);
			if (split == null) {
				split = new ORSplitType();
				split.setId(BigInteger.valueOf(cpfId++));
				implicitORSplit.put(csrc, split);
				net.getNode().add(split);
			}
			csrc = split;
		} else {			
			if (implicitANDSplit.containsKey(csrc)) {
				NodeType split = implicitANDSplit.get(csrc);
				if (!linked.contains(split)) {
					addEdge(net, csrc, split);
					linked.add(split);
				}
				csrc = split;
			}

			if (implicitJoin.containsKey(ctgt)) {
				NodeType join = implicitJoin.get(ctgt);
				if (!linked.contains(join)) {
					addEdge(net, join, ctgt);
					linked.add(join);
				}
				ctgt = join;
			}
		}

		EdgeType edge = addEdge(net, csrc, ctgt);

		edgeMap.put(flow, edge);
	}

	private EdgeType addEdge(NetType net, NodeType src, NodeType tgt) {
		EdgeType edge = new EdgeType();
		edge.setId(BigInteger.valueOf(cpfId++));
		edge.setSourceId(src.getId());
		edge.setTargetId(tgt.getId());
		List<EdgeType> trans = outgoings.get(src);
		if (trans == null) {
			trans = new LinkedList<EdgeType>();	
			outgoings.put(src, trans);
		}
		trans.add(edge);
		net.getEdge().add(edge);
		return edge;
	}

	private void translateActivity(NetType net, Activity act) {
		NodeType node = new NodeType();
		Route route = null;
		Event event = null;
		Implementation implementation = null;
		TransitionRestrictions trests = null;
		for (Object obj: act.getContent())
			if (obj instanceof Route)
				route = (Route) obj;
			else if (obj instanceof Event)
				event = (Event) obj;
			else if (obj instanceof Implementation)
				implementation = (Implementation) obj;
			else if (obj instanceof TransitionRestrictions)
				trests = (TransitionRestrictions) obj;
		if (route != null ){
			node = translateGateway(net, act, route, trests);
			System.out.println("Gateway: " + route.getGatewayType());
		} else if (event != null) {
			node = translateEvent(net, act, event);
			System.out.println("Event: " + act.getName());
		} else {
			// TODO: Subprocesses ...
			node = translateTask(net, act);
			System.out.println("Activity: " + act.getName());
		}

		node.setId(BigInteger.valueOf(cpfId++));
		net.getNode().add(node);
		xpdl2canon.put(act, node);
		xpdlRefMap.put(act.getId(), act);		
	}

	private NodeType translateTask(NetType net, Activity act) {
		NodeType node = new TaskType();
		boolean isSplit = false, isJoin = false;

		for (Object obj: act.getContent()) {
			if (obj instanceof TransitionRestrictions) {
				for (TransitionRestriction tres: ((TransitionRestrictions)obj).getTransitionRestriction()) {
					if (tres.getSplit() != null)
						isSplit = true;
					else if (tres.getJoin() != null)
						isJoin = true;
				}
			}
		}

		if (isSplit) {
			NodeType split = new ANDSplitType();
			split.setId(BigInteger.valueOf(cpfId++));
			net.getNode().add(split);
			System.out.println("Implicit split");
		}

		if (isJoin){
			NodeType join = new XORJoinType();
			join.setId(BigInteger.valueOf(cpfId++));
			net.getNode().add(join);			
			System.out.println("Implicit join");
		}
		node.setName(act.getName());
		return node;
	}

	private NodeType translateEvent(NetType net, Activity act, Event event) {
		NodeType node = null;

		if (event.getStartEvent() != null) {
			StartEvent startEvent = event.getStartEvent();
			if (startEvent.getTrigger().equals("None") ||
					startEvent.getTrigger().equals("Conditional"))
				node = new EventType();
			else if (startEvent.getTrigger().equals("Message"))
				node = new MessageType();
			else if (startEvent.getTrigger().equals("Timer"))
				node = new TimerType();
			else {
				System.err.println("Event type not supported: " + startEvent.getTrigger());
				System.exit(-1);
			}
		} else if (event.getEndEvent() != null) {
			EndEvent endEvent = event.getEndEvent();
			if (endEvent.getResult().equals("None"))
				node = new EventType();
			else if (endEvent.getResult().equals("Message"))
				node = new MessageType();
			else {
				System.err.println("Event type not supported: " + endEvent.getResult());
				System.exit(-1);
			}
		} else {
			IntermediateEvent interEvent = event.getIntermediateEvent();
			if (interEvent.getTrigger().equals("None"))
				node = new EventType();
			else if (interEvent.getTrigger().equals("Message"))
				node = new MessageType();
			else if (interEvent.getTrigger().equals("Timer"))
				node = new TimerType();
			else {
				System.err.println("Event type not supported: " + interEvent.getTrigger());
				System.exit(-1);
			}
		}
		node.setName(act.getName());
		return node;
	}

	private NodeType translateGateway(NetType net, Activity act, Route route, TransitionRestrictions trests) {
		boolean isSplit = false;
		boolean isJoin = false;

		for (TransitionRestriction trest: trests.getTransitionRestriction()) {
			if (trest.getSplit() != null)
				isSplit = true;
			if (trest.getJoin() != null)
				isJoin = true;
		}

		NodeType node = null;

		if (route.getGatewayType().equals("Parallel")) {
			if (isSplit)
				node = new ANDSplitType();
			else
				node = new ANDJoinType();
		} else if (route.getGatewayType().equals("Exclusive")) {
			if (route.getExclusiveType().equals("Data")) {
				if (isSplit)
					node = new XORSplitType();
				else
					node = new XORJoinType();
			} else
				node = new StateType();
		} else if (route.getGatewayType().equals("Inclusive")) { 
			if (isSplit)
				node = new ORSplitType();
			else
				node = new ORJoinType();
		} else {
			System.err.println("Gateway type not supported:[DEPRECATED] " + route.getGatewayType());
			System.exit(-1);
		}

		return node;
	}
}
