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

package ee.ut.utilities;

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.PrimeEventStructure;

public class PESViewer {
	
	private static Set<Integer> getSilents(List<String> labels) {
		Set<Integer> silents = new HashSet<Integer>();
		for (int i = 0; i < labels.size(); i++) {
			if ((labels.get(i).startsWith("XOR")) || (labels.get(i).startsWith("AND"))) {
				silents.add(i);
			}
		}
		return silents;
	}
	
	private static void updateDCausality(BitSet[] dcausality, Set<Integer> silents) {
		for (int s: silents) {
			for (int i = 0; i < dcausality.length; i++) {
				if (dcausality[i].get(s)) {
					dcausality[i].or(dcausality[s]);
					//dcausality[i].clear(s);
				}
			}
		}
	}
	
	public static void display(PrimeEventStructure<Integer> pes) {
		System.out.println(getStringMatrix(pes));
	}
	
	public static String getRelevantStringMatrix(PrimeEventStructure<Integer> pes, Set<String> relevantLabels, BitSet[] dcausality, BitSet[] dconflict, Boolean propagateSilents, Boolean selectedLabelsOnly, Boolean propagateSources) {
		BehaviorRelation[][] m = pes.getBRelMatrix();
		String br;

		String matrix = "";

		if (propagateSilents) updateDCausality(dcausality, getSilents(pes.getLabels()));
		
		Set<Integer> li = new HashSet<Integer>();
		Set<Integer> causals = new HashSet<Integer>();
		
		// get all pes labels that are in relevantLabels and get all its direct successors and predecessors
		for (String rl: relevantLabels) {
			for (int i = 0; i < pes.getLabels().size(); i++) {
				if (pes.getLabels().get(i).equals(rl)) {
					li.add(i);
					if (!selectedLabelsOnly) {	
						for (int j = 0; j < pes.getLabels().size(); j++) {
							if ((!propagateSilents) ||
									((!pes.getLabels().get(j).startsWith("AND")) && (!pes.getLabels().get(j).startsWith("XOR")))) { // exclude silents if they are propagated
								
								if (dcausality[i].get(j)) {
									li.add(j);
								}
								else if (dcausality[j].get(i)) {
									li.add(j);
									causals.add(j);
								}
							}
						}
					}
				}
			}
		}
		
		if (!selectedLabelsOnly) {
			// get all direct successors of all direct predecessors of the relevantLabels
			if (propagateSources) {
				for (int i: causals) {
					for (int j = 0; j < pes.getLabels().size(); j++) {
						if (dcausality[i].get(j)) {
							li.add(j);
						}
					}
				}
			}
		}
		
		matrix += getLabelString(pes, li);

		// display relations of all total relevant labels
		for (int i: li) {
			for (int j: li) {
				if (dcausality[i].get(j)) {
					br = "\\";
				}
				else if (dcausality[j].get(i)) {
					br = "/";
				}
				else if ((dconflict != null) && (dconflict[i].get(j))) {
					br = "X";
				}
				else {
					switch(m[i][j]) {
						case CAUSALITY: br = "<"; break;
						case INV_CAUSALITY: br = ">"; break;
						case CONCURRENCY: br = "|"; break;
						default: br = "#";
					}
				}
				matrix += br + " ";
			}
			matrix += "\r\n";
		}
		
		return matrix;
	}
	
	public static String getDirectStringMatrix(PrimeEventStructure<Integer> pes, BitSet[] dcausality, BitSet[] dconflict, Boolean propagateSilents) {
		BehaviorRelation[][] m = pes.getBRelMatrix();
		String br;

		String matrix = "";

		matrix += getLabelString(pes);		
		
		if (propagateSilents) updateDCausality(dcausality, getSilents(pes.getLabels()));
		
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				if (dcausality[i].get(j)) {
					br = "\\";
				}
				else if (dcausality[j].get(i)) {
					br = "/";
				}
				else if ((dconflict != null) && (dconflict[i].get(j))) {
					br = "X";
				}
				else {
					switch(m[i][j]) {
						case CAUSALITY: br = "<"; break;
						case INV_CAUSALITY: br = ">"; break;
						case CONCURRENCY: br = "|"; break;
						default: br = "#";
					}
				}
				matrix += br + " ";
			}
			matrix += "\r\n";
		}
		
		return matrix;
	}
	
	private static String getLabelString(PrimeEventStructure<Integer> pes, Set<Integer> labels) {
		String matrix = "";
		for (int i: labels) {
			matrix += pes.getLabels().get(i) + " ";
		}
		matrix += "\r\n";
		
		return matrix;
	}
	
	private static String getLabelString(PrimeEventStructure<Integer> pes) {
		String matrix = "";
		for (int i = 0; i < pes.getLabels().size(); i++) {
			matrix += pes.getLabels().get(i) + " ";
		}
		matrix += "\r\n";
		
		return matrix;
	}
	
	public static String getStringMatrix(PrimeEventStructure<Integer> pes) {
		BehaviorRelation[][] m = pes.getBRelMatrix();
		String br;
		String matrix = "";
		
		matrix += getLabelString(pes);
		
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				switch(m[i][j]) {
				case CAUSALITY: br = "<"; break;
				case INV_CAUSALITY: br = ">"; break;
				case CONCURRENCY: br = "|"; break;
				default: br = "#";
				}
				
				matrix += br + " ";
			}
			matrix += "\r\n";
		}
		return matrix;
	}
}
