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

package ee.ut.utilities.FES;

import java.util.LinkedHashSet;
import java.util.Set;

public class FreqDifference {
	private String fromLabel;
	private String toLabel;
	private int[] pathCount;
	
	private Set<Double> val1;
	private Set<Double> val2;
	private int occurrence1;
	private int occurrence2;
	
	private String ordinal(int nr) {
		String ord = nr + "";
		
		if ((ord.length() > 1) && (Integer.parseInt(ord.substring(ord.length() - 2)) > 10)) {
			ord += "th";
		}
		else {
			switch (ord.substring(ord.length() - 1)) {
				case "1": ord += "st"; break;
				case "2": ord += "nd"; break;
				case "3": ord += "rd"; break;
				default: ord += "th";
			}
		}
		
		return ord;
	}
	
	private void cutLabels() {
		fromLabel = fromLabel.substring(0, fromLabel.lastIndexOf("_"));
		toLabel = toLabel.substring(0, toLabel.lastIndexOf("_"));
	}
	
	private String getTotalValue(Set<Double> valueset, int pathIndex) {
		double totalvalue = 0;
		
		for (double val: valueset) {
			totalvalue += val;
		}
		totalvalue /= pathCount[pathIndex];
		return (double) Math.round(totalvalue * 1000) / 10 + "%"; // round to 1 digit
	}
	
	private String getAllValues(Set<Double> valueset) {
		String values = "";
		
		for (double val: valueset) {
			values += (double) Math.round(val * 1000) / 10 + "%, "; // round to 1 digit
		}
		
		values = values.substring(0, values.length() - 2); // remove last comma
		
		if (valueset.size() > 1) {
			int last = values.lastIndexOf(",");
			values = values.subSequence(0, last) + " or " + values.substring(last + 2); // replace last comma with "or"
		}
		return values;
	}
	
	private void setupFreqDiff(int pathCount1, int pathCount2, Set<Double> val1, Set<Double> val2, int occurrence1, int occurrence2){
		this.pathCount = new int[2];
		this.pathCount[0] = pathCount1;
		this.pathCount[1] = pathCount2;
		this.val1 = val1;
		this.val2 = val2;
		this.occurrence1 = occurrence1;
		this.occurrence2 = occurrence2;
		cutLabels();
	}
	
	public FreqDifference(String fromLabel, String toLabel, int pathCount1, int pathCount2, Set<Double> val1, Set<Double> val2, int occurrence1, int occurrence2) {
		this.fromLabel = fromLabel;
		this.toLabel = toLabel;
		setupFreqDiff(pathCount1, pathCount2, val1, val2, occurrence1, occurrence2);
	}
	
	public FreqDifference(Set<String> labels, int pathCount1, int pathCount2, Set<Double> val1, Set<Double> val2, int occurrence1, int occurrence2) {
		this.fromLabel = (String) labels.toArray()[0];
		this.toLabel = (String) labels.toArray()[labels.size() - 1];
		setupFreqDiff(pathCount1, pathCount2, val1, val2, occurrence1, occurrence2);
	}
	
	public Set<String> getLabels() {
		Set<String> labels = new LinkedHashSet<String>();
		
		labels.add(fromLabel);
		labels.add(toLabel);
		return labels;
	}
	
	public Set<Double> getValue1() {
		return val1;
	}
	
	public Set<Double> getValue2() {
		return val2;
	}
	
	public String verbalise(Boolean combineValues, Boolean useQuotation) {
		String verbalisation;
		String quot = "";
		if (useQuotation) quot = "\"";
		
		if (combineValues) {
			verbalisation = "In model 1, after the execution of ";
			if (occurrence1 > 0) verbalisation += "the " + ordinal(occurrence1) + " occurrence of ";
			verbalisation += quot + fromLabel + quot + " the frequency of branching to ";
			if (occurrence2 > 0) verbalisation += "the " + ordinal(occurrence2) + " occurrence of ";
			verbalisation += quot + toLabel + quot + " is " + getTotalValue(val1, 0) + "; "; 
			
			verbalisation += "whereas in model 2, after the execution of ";
			if (occurrence1 > 0) verbalisation += "the " + ordinal(occurrence1) + " occurrence of ";
			verbalisation += quot + fromLabel + quot + " the frequency of branching to ";
			if (occurrence2 > 0) verbalisation += "the " + ordinal(occurrence2) + " occurrence of ";
			verbalisation += quot + toLabel + quot + " is " + getTotalValue(val2, 1);
		}
		else {
			verbalisation = "In model 1, after the execution of ";
			if (occurrence1 > 0) verbalisation += "the " + ordinal(occurrence1) + " occurrence of ";
			verbalisation += quot + fromLabel + quot + " the frequency of branching to ";
			if (occurrence2 > 0) verbalisation += "the " + ordinal(occurrence2) + " occurrence of ";
			verbalisation += quot + toLabel + quot + " is " + getAllValues(val1) + "; "; 
			
			verbalisation += "whereas in model 2, after the execution of ";
			if (occurrence1 > 0) verbalisation += "the " + ordinal(occurrence1) + " occurrence of ";
			verbalisation += quot + fromLabel + quot + " the frequency of branching to ";
			if (occurrence2 > 0) verbalisation += "the " + ordinal(occurrence2) + " occurrence of ";
			verbalisation += quot + toLabel + quot + " is " + getAllValues(val2);
			if (val1.size() > 1) verbalisation += " respectively";
		}
		return verbalisation;
	}
	
	public String verbalisePlain() {
		String verbalisation = "";
		String quot = "\"";

		if (occurrence1 > 0) verbalisation += ordinal(occurrence1) + " ";
		verbalisation += quot + fromLabel + quot + " -> ";
		if (occurrence2 > 0) verbalisation += ordinal(occurrence2) + " ";
		verbalisation += quot + toLabel + quot + ": " + getTotalValue(val1, 0) + " vs " + getTotalValue(val2, 1);
		
		return verbalisation;
	}
	
	public String toString() {
		String result = "";
		
		result = "[" + fromLabel + ", " + toLabel + "] \n";
		result += "model1: " + val1 + "\n";
		result += "model2: " + val2 + "\n";
		
		return result;
	}
}
