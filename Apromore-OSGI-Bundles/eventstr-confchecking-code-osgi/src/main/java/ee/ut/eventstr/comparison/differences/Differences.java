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

package ee.ut.eventstr.comparison.differences;

import java.util.LinkedList;

import org.codehaus.jackson.map.ObjectMapper;


/**
 * Collection of differences to be retrieved by the 
 * REST service. This class can be seen as a wrapper
 * for generating the JSON file retrieved by the 
 * REST service. 
 */

public class Differences {
	private LinkedList<Difference> differences;
	private int numberOfDifferences;
	private int commonLabels;

	public Differences() {
		differences = new LinkedList<Difference>();
		setNumberOfDifferences(0);
		commonLabels = 0;
	}

	public void add(Difference diff) {
		if (!contains(diff)) {
			differences.add(diff);
			setNumberOfDifferences(differences.size());
		}
	}

	public LinkedList<Difference> getDifferences() {
		return differences;
	}

	public void setDifferences(LinkedList<Difference> differences) {
		this.differences = differences;
		setNumberOfDifferences(differences.size());
	}

	public String toString() {
		String result = "";

		for (Difference dif : differences)
			result += dif.getSentence() + "\n";

		return result;
	}

	public static String toJSON(Differences diffs) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(diffs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setCommonLabels(int commonLabels) {
		this.commonLabels = commonLabels;
	}

	public int getCommonLabels() {
		return commonLabels;
	}

	public int getNumberOfDifferences() {
		return numberOfDifferences;
	}

	public void setNumberOfDifferences(int numberOfDifferences) {
		this.numberOfDifferences = numberOfDifferences;
	}

	public boolean contains(Difference difference) {
		for (Difference diff : differences)  
			if (diff.getSentence().equals(difference.getSentence()))
				return true;

		return false;
	}

	public Differences clone() {
		Differences diff = new Differences();
		diff.setCommonLabels(commonLabels);

		for (Difference d : differences)
			diff.add(d);

		return diff;
	}

	public void addAll(LinkedList<Difference> differences) {
		for (Difference d : differences)
			add(d);
	}
}
