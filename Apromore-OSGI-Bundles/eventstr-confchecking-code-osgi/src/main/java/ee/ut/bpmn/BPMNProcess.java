package ee.ut.bpmn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class BPMNProcess<T> {
	enum NodeType {TASK, ANDGateway, ORGateway, XORGateway};
	
	private AtomicInteger nextNodeId = new AtomicInteger();
	private Map<Integer, NodeType> typeMap = new HashMap<>();
	private Map<Integer, String> labels = new HashMap<>();
	private Map<Integer, T> references = new HashMap<>();
	private Multimap<Integer, Integer> edges = HashMultimap.create();
	private Set<Integer> tasks = new HashSet<>();
	
	public Integer addTask(String name, String id, T reference) {
		Integer nodeId = nextNodeId.getAndIncrement();
		if (name == null || name.isEmpty())
			name = "Task" + nodeId;
		labels.put(nodeId, name);
		references.put(nodeId, reference);
		typeMap.put(nodeId, NodeType.TASK);
		tasks.add(nodeId);
		return nodeId;
	}

	public Integer addANDGateway(String name, String id, T reference) {
		Integer nodeId = nextNodeId.getAndIncrement();
		if (name == null || name.isEmpty())
			name = "AND" + nodeId;
		labels.put(nodeId, name);
		references.put(nodeId, reference);
		typeMap.put(nodeId, NodeType.ANDGateway);
		return nodeId;
	}

	public Integer addORGateway(String name, String id, T reference) {
		Integer nodeId = nextNodeId.getAndIncrement();
		if (name == null || name.isEmpty())
			name = "OR" + nodeId;
		labels.put(nodeId, name);
		references.put(nodeId, reference);
		typeMap.put(nodeId, NodeType.ORGateway);
		return nodeId;
	}

	public Integer addXORGateway(String name, String id, T reference) {
		Integer nodeId = nextNodeId.getAndIncrement();
		if (name == null || name.isEmpty())
			name = "XOR" + nodeId;
		labels.put(nodeId, name);
		references.put(nodeId, reference);
		typeMap.put(nodeId, NodeType.XORGateway);
		return nodeId;
	}

	public void addEdge(Integer src, Integer tgt, T reference) {
		edges.put(src, tgt);
	}

	public Collection<Entry<Integer, Integer>> getEdges() {
		return edges.entries();
	}
	
	public boolean isTask(Integer node) {
		return typeMap.get(node).equals(NodeType.TASK);
	}
	
	public boolean isANDGateway(Integer node) {
		return typeMap.get(node).equals(NodeType.ANDGateway);
	}
	
	public boolean isORGateway(Integer node) {
		return typeMap.get(node).equals(NodeType.ORGateway);
	}
	
	public boolean isXORGateway(Integer node) {
		return typeMap.get(node).equals(NodeType.XORGateway);
	}

	public String getName(Integer node) {
		return labels.get(node);
	}

	public Map<Integer, String> getLabels() {
		return labels;
	}
	
	public Set<Integer> getVisibleNodes() {
		return tasks;
	}
	
	public Set<Integer> getSources() {
		Set<Integer> sources = new HashSet<>(tasks);
		sources.removeAll(edges.values());
		return sources;
	}
	
	public Set<Integer> getSinks() {
		Set<Integer> sinks = new HashSet<>(tasks);
		sinks.removeAll(edges.keys());
		return sinks;
	}
}
