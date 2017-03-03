/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
package org.apromore.prodrift.mining.log.local;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apromore.prodrift.config.BehaviorRelation;
import org.apromore.prodrift.graph.util.GraphUtils;
import org.apromore.prodrift.synthesis.PES2Net;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

public class PrimeEventStructure<T> {
	
	
	protected BehaviorRelation[][] matrix;
	protected boolean[][] dcausality;
	protected List<List<Integer>> postset = new ArrayList<List<Integer>>();
	protected List<List<Integer>> preset = new ArrayList<List<Integer>>();
	protected Map<T, Integer> map;
	protected List<T> ridx;
	protected Map<Integer, Map<Integer, Set<Integer>>> preconflictEvents;

	protected List<String> labels;
	private String modelName;

	public PrimeEventStructure(BehaviorRelation[][] matrix,
			Map<T, Integer> map, List<T> ridx, List<String> labels, String modelName) {
		this.matrix = matrix;
		this.map = map;
		this.labels = labels;
		this.ridx = ridx;
		this.modelName = modelName;

		this.dcausality = new boolean[matrix.length][matrix.length];
	}
	public List<T> getReverseIndex() { return ridx; }
	
	public String getModelName() {
		return modelName;
	}

	public void pack() {
		fillOutCausalityMatrix();
		computeTransitiveReduction();
	}

	protected void fillOutCausalityMatrix() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++)
				if (matrix[i][j] == BehaviorRelation.FOLLOW)
					dcausality[i][j] = true;
		}
	}

	private void computeTransitiveReduction() {
		postset.clear();
		preset.clear();
		GraphUtils.transitiveReduction(dcausality);

		for (int i = 0; i < matrix.length; i++) {
			postset.add(new ArrayList<Integer>());
			preset.add(new ArrayList<Integer>());
		}

		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix.length; j++)
				if (dcausality[i][j]) {
					postset.get(i).add(j);
					preset.get(j).add(i);
				}
	}

	public void printMatrix(PrintStream out) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				out.printf("%s ", getCharacter(matrix[i][j]));
			}
			out.println();
		}
	}

	private String getCharacter(BehaviorRelation behaviorRelation) {
		switch (behaviorRelation) {
		case FOLLOW:
			return "<";
		case INV_FOLLOW:
			return ".";
		case CONFLICT:
			return "#";
		case CONCURRENCY:
			return "|";
		case ASYM_CONFLICT:
			return "/";
		case INV_ASYM_CONFLICT:
			return ".";
		}
		return null;
	}

	public Set<Integer> getIndexes(String... strings) {
		Set<String> strs = new HashSet<String>();
		Set<Integer> indexes = new HashSet<Integer>();

		for (String s : strings)
			strs.add(s);

		int i = 0;
		for (String l : labels) {
			if (strs.contains(l))
				indexes.add(i);
			i++;
		}

		return indexes;
	}

	public List<String> getLabels() {
		return labels;
	}

	public String getLabel(T event) {
		return labels.get(map.get(event));
	}

	public BehaviorRelation getRelation(T t, T t2) {
		return matrix[map.get(t)][map.get(t2)];
	}

	public Integer getIndex(T t) {
		return map.get(t);
	}

	public T getEvent(Integer i) {
		return ridx.get(i);
	}

	public BehaviorRelation getRelation(Integer i, Integer j) {
		return matrix[i][j];
	}

	public void toLatex(PrintStream out) {
		out.print("\\documentclass{article}\n");
		out.print("\\usepackage{tikz}\n");
		out.print("\\usepackage[paperheight=15in,paperwidth=8.5in]{geometry}\n");
		out.print("\\usetikzlibrary{arrows,shapes}\n");
		out.print("\\usepackage{dot2texi}\n");
		out.print("\\begin{document}\n");

		out.print("\\begin{tikzpicture}[>=stealth',scale=0.4]\n");
		out.print("\\tikzstyle{causality} = [draw, thick]\n");
		out.print("\\begin{dot2tex}[dot,tikz,codeonly,styleonly,options=-s]\n");

		out.print("digraph G {\n");

		for (int i = 0; i < ridx.size(); i++)
			out.printf("\tn%d [label=\"%s(%d)\"];\n", i, labels.get(i), i);

		out.print("\tedge [style=\"causality\"];\n");

		for (int i = 0; i < ridx.size(); i++)
			for (int j = 0; j < ridx.size(); j++) {
				if (dcausality[i][j])
					out.printf("\tn%d -> n%d;\n", i, j);
			}

		out.print("}\n");
		out.print("\\end{dot2tex}\n");

		for (Set<Integer> cluster : PES2Net.getConflictClusters(this))
			for (Integer src : cluster)
				for (Integer tgt : cluster)
					if (src < tgt)
						out.printf(
								"\\draw[red,thick]  [bend right] (n%d) to [bend left] (n%d);\n",
								src, tgt);

		out.print("\\end{tikzpicture}\n");
		out.print("\\end{document}\n");
	}

	public PrimeEventStructure<T> abstractUnobservableEvents(
			Set<Integer> toFilterOut) {
		int size = this.matrix.length - toFilterOut.size();
		BehaviorRelation[][] newMatrix = new BehaviorRelation[size][size];

		Map<T, Integer> newMap = new HashMap<T, Integer>();
		List<T> newRidx = new LinkedList<T>();
		List<String> newLabels = new LinkedList<String>();

		// Fill in newMap, newRidx and newLabels
		for (T t : this.ridx) {
			if (!toFilterOut.contains(ridx.indexOf(t))) {
				newMap.put(t, newMap.size());
				newRidx.add(t);
				newLabels.add(getLabel(t));
			}
		}

		// Fill in the newMatrix
		for (int i = 0; i < this.matrix.length; i++) {
			if (!toFilterOut.contains(i)) {
				T tI = this.ridx.get(i);
				for (int j = 0; j < this.matrix.length; j++) {
					if (!toFilterOut.contains(j)) {
						T tJ = this.ridx.get(j);
						newMatrix[newMap.get(tI)][newMap.get(tJ)] = this.matrix[i][j];
					}
				}
			}
		}

		return new PrimeEventStructure<T>(newMatrix, newMap, newRidx, newLabels, modelName);
	}

	public List<Integer> getDirectPredecessors(Integer target) {
		return preset.get(target);
	}

	public List<Integer> getDirectSuccessors(Integer source) {
		return postset.get(source);
	}

	public Set<Integer> getSinkNodes() {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < postset.size(); i++)
			if (postset.get(i).size() == 0)
				result.add(i);
		return result;
	}

	public Set<Integer> getSourceNodes() {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < preset.size(); i++)
			if (preset.get(i).size() == 0)
				result.add(i);
		return result;
	}

	public Set<Integer> conflictSet(Integer pivot) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < matrix.length; i++)
			if (matrix[pivot][i] == BehaviorRelation.CONFLICT)
				result.add(i);
		return result;
	}

	// --------------- POMSETS
	private Set<DirectedGraph> runs;
	public Map<Vertex, Integer> rmap = new HashMap<>();

	public Set<DirectedGraph> enumerateRuns() {
		if (runs == null) {
			runs = new HashSet<>();

			for (Integer sink : getSinkNodes())
				runs.add(subgraph(sink));
		}
		return runs;
	}

	private DirectedGraph subgraph(Integer sink) {
		Queue<Integer> open = new LinkedList<>();
		open.add(sink);
		Set<Integer> visited = new HashSet<Integer>();
		while (!open.isEmpty()) {
			Integer curr = open.poll();
			visited.add(curr);
			for (Integer pred : getDirectPredecessors(curr))
				if (!open.contains(pred) && !visited.contains(pred))
					open.add(pred);
		}

		DirectedGraph sub = new DirectedGraph();
		Map<Integer, Vertex> map = new HashMap<>();

		for (Integer e : visited) {
			Vertex v = new Vertex(labels.get(e));
			map.put(e, v);
			rmap.put(v, e);
		}

		for (Integer src : visited)
			for (Integer tgt : visited)
				// if (dcausality[src][tgt])
				if (matrix[src][tgt] == BehaviorRelation.FOLLOW)
					sub.addEdge(map.get(src), map.get(tgt));

		return sub;
	}

	public boolean directSuccessor(Integer src, Integer tgt) {
		return dcausality[src][tgt];
	}

	public DirectedGraph getDirectCausalityAsDiGraph() {
		DirectedGraph graph = new DirectedGraph();
		List<Vertex> vertices = new LinkedList<Vertex>();

		for (String label : labels)
			vertices.add(new Vertex(label));

		for (int src = 0; src < vertices.size(); src++)
			for (int tgt : postset.get(src))
				graph.addEdge(vertices.get(src), vertices.get(tgt));
		return graph;
	}

	public BehaviorRelation[][] getMatrix() {
		return this.matrix;
	}

	public Map<T, Integer> getMap() {
		return this.map;
	}

	public Set<Integer> concurrentSet(Integer pivot) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < matrix.length; i++)
			if ( i != pivot && matrix[pivot][i] == BehaviorRelation.CONCURRENCY)
				result.add(i);
		return result;
	}

	public void setRelation(Integer j, Integer k, BehaviorRelation relation) {
		matrix[j][k] = relation;
	}

	public void updateRelation(Integer j, Integer k, BehaviorRelation relation) {
		

		if (relation == BehaviorRelation.FOLLOW) {
			matrix[j][k] = BehaviorRelation.FOLLOW;
			matrix[k][j] = BehaviorRelation.INV_FOLLOW;
			dcausality[j][k] = true;
		} else {
			matrix[j][k] = relation;
			matrix[k][j] = relation;
			dcausality[j][k] = false;
			dcausality[k][j] = false;
		}
	}
	
	public void printConfiguration(Set<Integer> configuration) {
		boolean firsttime = true;
		for (Integer node : configuration) {
			if (firsttime)
				firsttime = false;
			else
				System.out.print(", ");
			System.out.printf("%s(%d)", labels.get(node), node);
		}
		System.out.println();
	}

	public boolean areCausalyRelated(Integer e1, Integer e2) {
		return matrix[e1][e2] == BehaviorRelation.FOLLOW || matrix[e2][e1] == BehaviorRelation.FOLLOW;
	}
	
	public boolean firstIsCausalPredecessorOfSecond(Integer pred, Integer succ) {
		return matrix[pred][succ] == BehaviorRelation.FOLLOW;
	}

	public Set<Integer> getSuccessorsOf(Integer event) {
		Set<Integer> successors = new HashSet<Integer>();
		for (int i = 0; i < matrix.length; i++)
			if (matrix[event][i] == BehaviorRelation.FOLLOW)
				successors.add(i);
		return successors;
	}

	public Set<Integer> getPredecessorsOf(Integer event) {
		Set<Integer> predecessors = new HashSet<Integer>();
		for (int i = 0; i < matrix.length; i++)
			if (matrix[i][event] == BehaviorRelation.FOLLOW)
				predecessors.add(i);
		return predecessors;
	}
}
