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

package ee.ut.eventstr;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PrimeEventStructure <T> {
	BitSet[] causality;
	protected BitSet[] dcausality;
	BitSet[] invcausality;
	BitSet[] concurrency;
	BitSet[] conflict;
	protected List<String> labels;
	List<Integer> sources;
	List<Integer> sinks;
	int tracecount;
	
	protected Map<Integer, Integer> occurrences;
	protected double[][] fmatrix;
	
	BehaviorRelation[][] matrix;
	private HashSet<String> cyclicTasks;

	public PrimeEventStructure(List<String> labels, BitSet[] causality, BitSet[] dcausality, BitSet[] invcausality, BitSet[] concurrency, BitSet[] conflict,
							   List<Integer> sources, List<Integer> sinks) {
		this.causality = causality;
		this.dcausality = dcausality;
		this.invcausality = invcausality;
		this.concurrency = concurrency;
		this.conflict = conflict;
		this.labels = labels;
		this.sources = sources;
		this.sinks = sinks;
		this.cyclicTasks = new HashSet<>();
	}

	public PrimeEventStructure(List<String> labels, BitSet[] causality, BitSet[] dcausality, BitSet[] invcausality, BitSet[] concurrency, BitSet[] conflict,
							   List<Integer> sources, List<Integer> sinks, Map<Integer, Integer> occurrences, double[][] fmatrix) {
		this.causality = causality;
		this.dcausality = dcausality;
		this.invcausality = invcausality;
		this.concurrency = concurrency;
		this.conflict = conflict;
		this.labels = labels;
		this.sources = sources;
		this.sinks = sinks;
		this.cyclicTasks = new HashSet<>();
		
		this.occurrences = occurrences;
		this.fmatrix = fmatrix;
		
		tracecount = 0;
		for (int sink: sinks) {
			tracecount += occurrences.get(sink);
		}
	}
	
	public BehaviorRelation[][] getBRelMatrix() {
		if (matrix == null) {
			int size = labels.size();

			System.out.println("\nSize: " + size);
			matrix = new BehaviorRelation[size][size];
			
			for (int i = 0; i < size; i++) {
				matrix[i][i] = BehaviorRelation.CONCURRENCY;
				for (int j = i + 1; j < size; j++) {
					if (causality[i].get(j)) {
						matrix[i][j] = BehaviorRelation.CAUSALITY;
						matrix[j][i] = BehaviorRelation.INV_CAUSALITY;
					} 
					else if (invcausality[i].get(j)) {
						matrix[i][j] = BehaviorRelation.INV_CAUSALITY;
						matrix[j][i] = BehaviorRelation.CAUSALITY;
					} 
					else if (concurrency[i].get(j)) {
						matrix[i][j] = matrix[j][i] = BehaviorRelation.CONCURRENCY;
					}
					else {
						matrix[i][j] = matrix[j][i] = BehaviorRelation.CONFLICT;
					}
				}
			}
		}
		return matrix;
	}
	
	public BitSet[] getDirectCausalRelations() {
		return dcausality;
	}
	
	public double[][] getFreqMatrix() {
		return fmatrix;
	}
	
	public int getEventOccurrenceCount(int event) {
		return occurrences.get(event);
	}
	
	public double getEventFrequency(int event) {
		return getEventOccurrenceCount(event) / tracecount;
	}
	
	// get amount of traces in the log of this event structure
	public int getTotalTraceCount() {
		return tracecount;
	}

	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		out.println("\tnode[shape=box];");
		for (int i = 0; i < labels.size(); i++)
			out.printf("\tn%d [label=\"%s(%d)\"];\n", i, labels.get(i), i);
		
		for (int src = 0; src < labels.size(); src++)
			for (int tgt = dcausality[src].nextSetBit(0); tgt >= 0; tgt = dcausality[src].nextSetBit(tgt+1))
				out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}

	public void setLabels(List<String> labels) {
		this.labels = new ArrayList<>(labels);
	}

	public String toDot(BitSet conf, BitSet hidings) {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		out.println("\tnode[shape=box];");
		for (int e = conf.nextSetBit(0); e >= 0; e = conf.nextSetBit(e+1)) {
			if (!hidings.get(e))
				out.printf("\tn%d [label=\"%s\"];\n", e, labels.get(e));
		}

		out.println("\tnode[shape=box,style=filled,color=red];");
		for (int e = hidings.nextSetBit(0); e >= 0; e = hidings.nextSetBit(e+1)) {
				out.printf("\tn%d [label=\"%s\"];\n", e, labels.get(e));			
		}
		
		for (int src = conf.nextSetBit(0); src >= 0; src = conf.nextSetBit(src+1))
			for (int tgt = conf.nextSetBit(0); tgt >= 0; tgt = conf.nextSetBit(tgt+1))
				if (dcausality[src].get(tgt))
					out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}
	
	public List<String> getLabels() {
		return labels;
	}

	public List<Integer> getSinks() {
		return sinks;
	}
	
	public void printBRelMatrix(PrintStream out) {
		if (matrix == null)
			getBRelMatrix();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				out.printf("%s ", getCharacter(matrix[i][j]));
			}
			out.println();
		}
	}

	private String getCharacter(BehaviorRelation behaviorRelation) {
		switch (behaviorRelation) {
		case CAUSALITY:
			return "<";
		case INV_CAUSALITY:
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

	public void setCyclicTasks(HashSet<String> cyclicTasks) {
		this.cyclicTasks = cyclicTasks;
	}
	
	public HashSet<String> getCyclicTasks(){
		return cyclicTasks;
	}
}
