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

package ee.ut.eventstr.freq;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import ee.ut.eventstr.PrimeEventStructure;

public class FrequencyAwarePrimeEventStructure <T> extends PrimeEventStructure<T> {

	private Map<Integer, Integer> occurrences;
	private double[][] fmatrix;

	public FrequencyAwarePrimeEventStructure(List<String> labels, BitSet[] causality, BitSet[] dcausality,
			BitSet[] invcausality, BitSet[] concurrency, BitSet[] conflict, List<Integer> sources, List<Integer> sinks,
			Map<Integer, Integer> occurrences, double[][] fmatrix) {
		super(labels, causality, dcausality, invcausality, concurrency, conflict, sources, sinks);
		this.occurrences = occurrences;
		this.fmatrix = fmatrix;
	}
	
	public double[][] getFreqMatrix() {
		return fmatrix;
	}
	
	public Map<Integer, Integer> getOccurrences() {
		return occurrences;
	}
	
	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		out.println("\tnode[shape=box];");
		for (int i = 0; i < labels.size(); i++)
			out.printf("\tn%d [label=\"%s(%d)\\n%d\"];\n", i, labels.get(i), i, occurrences.get(i));
		
		for (int src = 0; src < labels.size(); src++)
			for (int tgt = dcausality[src].nextSetBit(0); tgt >= 0; tgt = dcausality[src].nextSetBit(tgt+1))
				out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}
}
