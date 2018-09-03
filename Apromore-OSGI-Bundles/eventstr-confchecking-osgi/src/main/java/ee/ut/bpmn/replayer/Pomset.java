/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package ee.ut.bpmn.replayer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gwt.dev.util.collect.HashSet;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.HashMap;
import java.util.Map.Entry;

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
