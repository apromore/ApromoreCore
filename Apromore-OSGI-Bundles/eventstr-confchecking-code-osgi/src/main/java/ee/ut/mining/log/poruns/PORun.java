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

package ee.ut.mining.log.poruns;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.graph.transitivity.MatrixBasedTransitivity;
import ee.ut.mining.log.ConcurrencyRelations;
import ee.ut.org.processmining.framework.util.Pair;

public class PORun implements PORunConst {
	private static AtomicInteger nextId = new AtomicInteger();

	protected boolean[][] adjmatrix;
	protected Map<Integer, String> labels;
	private Multimap<Integer, Integer> concurrency;
	protected List<Integer> vertices;
	protected Map<Integer, Integer> vertexIndexMap;
	private Multimap<Integer, Integer> predList = null;
	private String idTrace;

	public PORun(ConcurrencyRelations alphaRelations, XTrace trace, String id) {
		this(alphaRelations, trace, null, id);
	}
	
	public PORun(ConcurrencyRelations alphaRelations, XTrace trace, Map<Integer, Pair<XEvent,String>> xeventMap, String idLog) {
		this.labels = new HashMap<>();	
		this.vertices = new ArrayList<>();
		this.vertexIndexMap = new HashMap<>();
		this.concurrency = HashMultimap.create();
		this.idTrace = idLog;

		XAttribute nameXML = trace.getAttributes().get("concept:name");
        String traceId = "case" + idLog;
        if(nameXML != null)
            traceId = nameXML.toString();

		// === Map events to local identifiers
		Integer id = nextId.getAndIncrement();
		vertices.add(id);
		vertexIndexMap.put(id, 0);
		labels.put(id, GLOBAL_SOURCE_LABEL);
		for (XEvent e: trace)
			if (isCompleteEvent(e) && e.getAttributes().get(XConceptExtension.KEY_NAME) != null) {
				id = nextId.getAndIncrement();
				vertices.add(id);
				vertexIndexMap.put(id, vertexIndexMap.size());
				labels.put(id, getEventName(e));
				
				if (xeventMap != null) xeventMap.put(id, new Pair<>(e, traceId));
			}
		id = nextId.getAndIncrement();
		vertices.add(id);
		vertexIndexMap.put(id, vertexIndexMap.size());
		labels.put(id, GLOBAL_SINK_LABEL);
//		id = nextId.getAndIncrement();
//		vertices.add(id);
//		vertexIndexMap.put(id, vertexIndexMap.size());
//		labels.put(id, GLOBAL_SINK_PRIME_LABEL);
		
		// === We compute the transitive closure of causality
		// === Initially we have a trivial sequence. The order on events reflects
		// === the causal relation. Therefore, the transitive closure of causality
		// === corresponds with an upper triangle matrix.
		int size = labels.size();
		this.adjmatrix = new boolean[size][size];
		for (int i = 0; i < size-1; i++)
			Arrays.fill(adjmatrix[i], i+1, size, true);
//		System.out.println("=============================");
//		GraphUtils.print(adjmatrix);
		
		// ======================================================
		// Introduce the concurrency relation
		// ======================================================
		for (int i = 1; i < size - 1; i++) {
			Integer vertex1 = vertices.get(i);
			String label1 = labels.get(vertex1);
			for (int j = i + 1; j < size - 1; j++) {
				Integer vertex2 = vertices.get(j);
				String label2 = labels.get(vertex2);
				if (!label1.equals(label2) && alphaRelations.areConcurrent(label1, label2)) {
					adjmatrix[i][j] = false;
					concurrency.put(vertex1, vertex2);
					concurrency.put(vertex2, vertex1);
				}
			}
		}
		
		MatrixBasedTransitivity.transitiveReduction(adjmatrix);
	}

	public String getIdTrace(){ return idTrace; }

	private String getEventName(XEvent e) {
		return e.getAttributes().get(XConceptExtension.KEY_NAME).toString();
	}

	private boolean isCompleteEvent(XEvent e) {
		XAttributeMap amap = e.getAttributes();
		if (amap.get(XLifecycleExtension.KEY_TRANSITION) != null)
			return (amap.get(XLifecycleExtension.KEY_TRANSITION).toString().toLowerCase().equals("complete"));
		else
			return false;
	}
	
	public Map<Integer, String> getLabelMap() {
		return labels;
	}
	
	public Multimap<Integer, Integer> asSuccessorsList() {
		Multimap<Integer, Integer> succList = HashMultimap.create();
		this.predList = HashMultimap.create();
		int size = adjmatrix.length;
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++)
				if (adjmatrix[i][j]) {
					succList.put(vertices.get(i), vertices.get(j));
					predList.put(vertices.get(j), vertices.get(i));
				}
		}
		return succList;
	}
	
	public Multimap<Integer, Integer> asPredecessorsList() {
		if (predList == null) {
			predList = HashMultimap.create();
			int size = adjmatrix.length;
			for (int i = 0; i < size - 1; i++) {
				for (int j = i + 1; j < size; j++)
					if (adjmatrix[i][j])
						predList.put(vertices.get(j), vertices.get(i));
			}
		}
		return predList;
	}
	
	public Multimap<Integer, Integer> getConcurrencyRelation() {
		return concurrency;
	}
	
	public Integer getSource() {
		return vertices.get(0);
	}
	
	public Integer getSink() {
		return vertices.get(vertices.size() - 1);
	}

	protected String toDot(Map<Integer, String> labels, Multimap<Integer, Integer> successors) {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		for (Integer vertex: labels.keySet())
			out.printf("\tn%d [label=\"%s(%d)\"];\n", vertex, labels.get(vertex), vertex);

		for (Integer src: labels.keySet())
			for (Integer tgt: successors.get(src))
				out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}

	public String toDot() {
		return toDot(labels, asSuccessorsList());
	}
}
