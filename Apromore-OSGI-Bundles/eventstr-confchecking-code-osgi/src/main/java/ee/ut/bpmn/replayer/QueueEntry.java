package ee.ut.bpmn.replayer;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jbpt.pm.FlowNode;

public class QueueEntry implements Comparable<QueueEntry>{
	LinkedList<FlowNode> history;
	LinkedList<FlowNode> candidates;
	BitSet underConf;
	BitSet trace;
	HashMap<String, String> colors;
	HashMap<String, Integer> repetitions;
	boolean found = true;
	HashMap<Integer, FlowNode> mapping;
	Pomset pomset;

	public QueueEntry(HashSet<FlowNode> nodes, BitSet trace) {
		history = new LinkedList<>();
		candidates = new LinkedList<>(nodes);
		underConf = new BitSet(trace.size());
		colors = new HashMap<String, String>();
		this.trace = (BitSet) trace.clone();
		mapping = new HashMap<Integer, FlowNode>();
		repetitions = new HashMap<String, Integer>();
	}
	
	public QueueEntry(HashSet<FlowNode> nodes, Pomset trace) {
		history = new LinkedList<>();
		candidates = new LinkedList<>(nodes);
		colors = new HashMap<String, String>();
		mapping = new HashMap<Integer, FlowNode>();
		repetitions = new HashMap<String, Integer>();
		this.pomset = trace;
	}
	
	public QueueEntry(HashSet<FlowNode> nodes, Pomset trace, LinkedList<FlowNode> history, LinkedList<FlowNode> candidates, 
			HashMap<String, String> colors, HashMap<Integer, FlowNode> mapping, HashMap<String, Integer> repetitions) {
		this.history = new LinkedList<>(history);
		this.candidates = new LinkedList<>(candidates);
		this.colors = new HashMap<String, String>(colors);
		this.mapping = new HashMap<Integer, FlowNode>(mapping);
		this.repetitions = new HashMap<String, Integer>(repetitions);
		this.pomset = trace;
	}
	
	public QueueEntry(LinkedList<FlowNode> history, LinkedList<FlowNode> candidates, BitSet underConf,
			HashMap<String, String> colors, BitSet trace, HashMap<Integer, FlowNode> mapping, HashMap<String, Integer> repetitions) {
		this.history = new LinkedList<>(history);
		this.candidates = new LinkedList<>(candidates);
		this.underConf = (BitSet) underConf.clone();
		this.colors = new HashMap<String, String>(colors);
		this.trace =  (BitSet) trace.clone();
		this.mapping = new HashMap<Integer, FlowNode>(mapping);
		this.repetitions = new HashMap<String, Integer>(repetitions);
	}

	public QueueEntry clone() {
//		return new QueueEntry(history, candidates, underConf, colors,trace, mapping, repetitions);
		return new QueueEntry(new HashSet<FlowNode>(candidates), pomset, history, candidates, colors, mapping, repetitions);
	}

	public QueueEntry clone(Integer v) {
		QueueEntry queue = new QueueEntry(new HashSet<FlowNode>(candidates), pomset.removeVertex(v), history, candidates, colors, mapping, repetitions);
		return queue;
	}

	public void add2History(FlowNode f) {
		this.history.add(f);
	}

	public void add2Candidates(FlowNode s) {
		this.candidates.add(s);
	}

	public void add2Colors(FlowNode f, String string) {
		if (string.equals("red"))
			found = true;

		colors.put(f.getId(), string);
	}

	public void addMapping(Integer n, FlowNode f){
		mapping.put(n, f);
	}
	
	public BitSet getTrace() {
		return trace;
	}

	public String toString() {
		return "(" + history + " -- " + colors + " -- " + trace + " -- "
				+ candidates + ")";
	}

	public void add2Conf(Integer n) {
		this.underConf.set(n);
	}

	@Override
	public int compareTo(QueueEntry o) {
		if(pomset.getSize() != o.pomset.getSize())
			return pomset.getSize() - o.pomset.getSize();
		if(!history.isEmpty() && o.history.equals(this.history))
			if(mapping.equals(o.mapping))
				return 0;
		
		return -1;
	}

	public void removeFromTrace(Integer n) {
		trace.set(n, false);
	}

	public void add2Counter(FlowNode f) {
			if(!repetitions.containsKey(f.getId()))
				repetitions.put(f.getId(), 1);
			else
				repetitions.put(f.getId(), repetitions.get(f.getId())+1);
	}

	public Set<Integer> getNext() {
		return pomset.getNext();
	}
	
	public boolean contains(String name){
		for(FlowNode node : history)
			if(node.getName().equals(name))
				return true;
		
		return false;
	}
	
	public boolean containsPO(String name){
		return pomset.labels.values().contains(name);
	}
	
}
