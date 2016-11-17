package ee.ut.bpmn.replayer;

import com.google.common.collect.BiMap;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import hub.top.petrinet.Node;
import org.jbpt.pm.*;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;

import java.util.*;

public class BPMNReplayerML {

	private Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model;
	private HashSet<String> observable;
	private NewUnfoldingPESSemantics<Integer> es;

	public BPMNReplayerML(Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model, HashSet<String> observable, NewUnfoldingPESSemantics<Integer> es) {
		this.model = model;
		this.observable = observable;
		this.es = es;
	}

	public HashMap<String, String> spotTask(String label) {
		HashMap<String, String> colors = new HashMap<>(); 
		
		for(FlowNode node : model.getFlowNodes())
			if(node.getName().equals(label))
				colors.put(node.getId(), "red");
		return colors;
	}

	public HashSet<String> getEdgesBetween(HashSet<String> nodes){
		HashSet<String> edgesBT = new HashSet<>();

		for(ControlFlow flow : model.getControlFlow())
			if(nodes.contains(flow.getSource().getId()) && nodes.contains(flow.getTarget().getId())){
				ControlFlow edge = model.getDirectedEdge((FlowNode) flow.getSource(), (FlowNode) flow.getTarget());
				edgesBT.add(edge.getId());
			}

		return edgesBT;
	}
	
	public HashMap<String, String> executeC(String end1, String end2, Pomset conf, HashMap<String, Integer> repetitions, BiMap<Node, Node> mapping) {
		HashMap<String, String> exec = execute(end1 + "xyz123", conf, repetitions, mapping);
		
		for(String key : exec.keySet()){
			FlowNode node = getActivity(key);
			if(node != null && (node.getName().equals(end1) || node.getName().equals(end2)))
				exec.put(key, "red");
		}
			
		return exec;
	}
	
	private FlowNode getActivity(String key) {
		for(FlowNode node : model.getActivities())
			if(node.getId().equals(key))
				return node;
		
		return null;
	}

	public HashMap<String, String> getEnd(String a, String b, Set<String> labels2Spot){
		HashMap<String, String> allColors = new HashMap<>();

		FlowNode task1 = getActivity(a);
		FlowNode task2 = getActivity(b);

		HashSet<FlowNode> visited1 = new HashSet<>();
		visited1.add(task1);
		HashSet<FlowNode> visited2 = new HashSet<>();
		visited2.add(task2);

		HashSet<FlowNode> observed1 = new HashSet<>();
		HashSet<FlowNode> observed2 = new HashSet<>();

		while(!visited1.isEmpty() || !visited2.isEmpty()){
			HashSet<FlowNode> visited1New = new HashSet<>();
			HashSet<FlowNode> visited2New = new HashSet<>();

			for(FlowNode n : visited1)
				if(!observed1.contains(n)) {
					observed1.add(n);
					visited1New.addAll(model.getDirectSuccessors(n));
					allColors.put(n.getId(), "green");
				}

			for(FlowNode n : visited2)
				if(!observed2.contains(n)) {
					observed2.add(n);
					visited2New.addAll(model.getDirectSuccessors(n));
					allColors.put(n.getId(), "green");
				}

			HashSet<FlowNode> intersect = new HashSet<>(observed1);
			intersect.retainAll(observed2);

			if(!intersect.isEmpty()) {
				HashMap<String, String> map = new HashMap<>();
				for (FlowNode node : intersect) {
					if (labels2Spot.contains(node.getName()))
						map.put(node.getId(), "red");
				}

				if(map.size() > 0) {
					for(Map.Entry<String, String> entry : allColors.entrySet())
						if(!map.containsKey(entry.getKey()))
							map.put(entry.getKey(), entry.getValue());

					return map;
				}
			}

			visited1 = new HashSet<>(visited1New);
			visited2 = new HashSet<>(visited2New);
		}

		return new HashMap<>();
	}

	public HashMap<String, String> execute(String end, Pomset conf, HashMap<String, Integer> repetitions, BiMap<Node, Node> mapping) {
		HashSet<FlowNode> marking = getMarking();
		HashSet<QueueEntry> visited = new HashSet<QueueEntry>();
		PriorityQueue<QueueEntry> queue = new PriorityQueue<>();
		queue.add(new QueueEntry(marking, conf));
		System.out.println("IN: ");
		
		while (!queue.isEmpty()) {
			QueueEntry current = queue.remove();
			visited.add(current);
			
			System.out.print(current.pomset.getSize()+", ");
			//System.out.println(current.pomset.toDOT());

			if (current.getNext().size() == 0) {
				repetitions.putAll(current.repetitions);
				return current.colors;
			}

			for (FlowNode f : current.candidates) {
				if (f instanceof AndGateway) {
					boolean join = model.getIncomingControlFlow(f).size() > 1 ? true : false;

					if (!join || current.history.containsAll(model.getDirectPredecessors(f))) {
						QueueEntry copy = current.clone();
						copy.add2History(f);
						copy.candidates.remove(f);
						for (FlowNode s : model.getDirectSuccessors(f))
//							if(!copy.history.contains(s))
								copy.add2Candidates(s);

						if (!queue.contains(copy) && !visited.contains(copy))
							queue.add(copy);

						copy.add2Counter(f);
						copy.add2Colors(f, "green");
					}
				} else if (f instanceof XorGateway) {
					// boolean join = model.getIncomingControlFlow(f).size() > 1
					// ? true : false;

					for (FlowNode s : model.getDirectSuccessors(f)) {
						QueueEntry copy = current.clone();
						copy.add2History(f);
						copy.candidates.remove(f);
						copy.add2Candidates(s);

						if(!copy.history.contains(s)){
							if (!queue.contains(copy) && !visited.contains(copy))
									queue.add(copy);
							
							copy.add2Counter(f);
							copy.add2Colors(f, "green");
						}
					}
					
					current.add2Colors(f, "green");
				} else {
					if (f instanceof Activity && observable.contains(f.getName())){
						if (!es.getLabels(current.getNext()).contains(f.getName())) 
							continue;
						
						Integer v = getVertex(current, f.getName());
						QueueEntry copy = current.clone(v);
						copy.add2History(f);
						copy.candidates.remove(f);
						copy.addMapping(v, f);

						copy.add2Counter(f);

						for (FlowNode s : model.getDirectSuccessors(f))
//							if(!copy.history.contains(s))
								copy.add2Candidates(s);

						if (copy.getNext().isEmpty() && f.getName().equals(end)) {
							copy.add2Colors(f, "red");
							repetitions.putAll(copy.repetitions);
							System.out.println();
							return copy.colors;
						} else {
							if (mapping == null || (mapping != null && !(mapping.containsKey(v) || mapping.containsValue(v))))
								copy.add2Colors(f, "green");
							else if (mapping != null && (mapping.containsKey(v) || mapping.containsValue(v)))
								copy.add2Colors(f, "green");

							if (!queue.contains(copy) && !visited.contains(copy))
								queue.add(copy);
						}
					} else {
						// fix bug .... when a silent task is in a cycle (it will always add it again)
						if(!current.contains(f.getName())){
							QueueEntry copy = null;
							if(current.containsPO(f.getName())){
								Integer v = getVertex(current, f.getName());
								copy = current.clone(v);
							}else
								copy = current.clone();
							copy.add2History(f);
							copy.candidates.remove(f);
	
							for (FlowNode s : model.getDirectSuccessors(f))
								if(!s.equals(f))
								copy.add2Candidates(s);
	
							copy.add2Counter(f);
							copy.add2Colors(f, "green");
	
							if (!queue.contains(copy) && !visited.contains(copy))
								queue.add(copy);
						}
					}
				}
			}
		}

		return new HashMap<String, String>();
	}
	
	private Integer getVertex(QueueEntry current, String name) {
		Set<Integer> next = current.getNext();
		
		for(Integer n : next)
			if(es.getLabel(n).equals(name))
				return n;
		
		return null;
	}

	private HashSet<FlowNode> getMarking() {
		HashSet<FlowNode> m = new HashSet<FlowNode>();

		for (FlowNode f : model.getFlowNodes())
			if (model.getDirectPredecessors(f) == null || model.getDirectPredecessors(f).isEmpty())
				m.add(f);

		return m;
	}
}
