package ee.ut.bpmn.replayer;

import java.util.HashMap;
import java.util.Map.Entry;

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gwt.dev.util.collect.HashSet;

public class Pomset {
	HashMap<Integer, String> labels;
	BiMap<Vertex, Integer> map;
	DirectedGraph graph;

	public Pomset(DirectedGraph graph, BiMap<Vertex, Integer> map, HashMap<Integer, String> labels) {
		this.graph = graph;
		this.map = map;
		this.labels = labels;
	}

	public HashSet<Integer> getNext() {
		HashSet<Integer> ext = new HashSet<>();

		for (Vertex v : graph.getVertices())
			if (graph.getDirectPredecessors(v).isEmpty())
				ext.add(map.get(v));

		return ext;
	}

	public Pomset removeVertex(Integer v) {
		BiMap<Vertex, Integer> newMap = HashBiMap.<Vertex, Integer> create();
		DirectedGraph newGraph = new DirectedGraph();

		for (Vertex vertex : graph.getVertices())
			if (map.get(vertex) != v) {
				newMap.put(vertex, map.get(vertex));
				newGraph.addVertex(vertex);
			}

		for (DirectedEdge edge : graph.getEdges())
			if (newMap.containsKey(edge.getSource())
					&& newMap.containsKey(edge.getTarget()))
				newGraph.addEdge(edge.getSource(), edge.getTarget());

		HashMap<Integer, String> newLabels = new HashMap<>(labels);
		newLabels.remove(v);

		return new Pomset(newGraph, newMap, newLabels);
	}

	public String toDOT() {
		return graph.toDOT();
	}

	public int getSize() {
		return graph.getVertices().size();
	}

	public String toString() {
		String s = "[";
		int i = 0;
		for (Entry<Vertex, Integer> entry : map.entrySet()) {
			s += "{\\\"data\\\":{\\\"id\\\":\\\"" + entry.getValue() + "\\\",\\\"label\\\":\\\"" + labels.get(entry.getValue()) + "\\\"},\\\"group\\\":\\\"nodes\\\",\\\"classes\\\":\\\"\\\"}";

			if (i + 1 < map.size() || graph.getEdges().size() > 0)
				s += ",";
			i++;
		}
		
		HashSet<DirectedEdge> toDelete = new HashSet<>();
		for(Vertex x : graph.getVertices())
			for(Vertex y : graph.getDirectSuccessors(x))
				for(Vertex z : graph.getDirectSuccessors(y))
					if(graph.getEdge(x, z) != null)
						toDelete.add(graph.getEdge(x, z));
		
		i = 0;
		for (DirectedEdge edge : graph.getEdges()) {
			if(!toDelete.contains(edge)){
				Integer source = map.get(edge.getSource());
				Integer target = map.get(edge.getTarget());
				String id = labels.get(source) + "" + labels.get(target) + "" + i;

				s += "{\\\"data\\\":{\\\"id\\\":\\\""+id+"\\\",\\\"weight\\\":2,\\\"source\\\":\\\""+source+"\\\",\\\"target\\\":\\\""+ target +"\\\"},\\\"group\\\":\\\"edges\\\"}";

				if (i + 1 < (graph.getEdges().size()-toDelete.size()))
					s += ",";
				
				i++;
			}
		}

		s += "]";

		return s;
	}

	public HashMap<Integer, String> getLabels(){
		return labels;
	}
	
	public DirectedGraph getGraph(){
		return graph;
	}

	public BiMap<Vertex, Integer> getMap() {
		return map;
	}

}
