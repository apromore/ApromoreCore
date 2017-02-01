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


import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This is the main container of a difference.
 * It contains a sentence, the runs in model 1 and 2
 * expressing the discovered difference between the 
 * models. 
 */

public class DifferenceML {
	private String sentence;
	private List<String> start;
	private List<String> a;
	private List<String> b;
	private List<String> end;
	private List<String> greys;
    private List<String> newTasks;
    private String type;

	public DifferenceML(){}

	public DifferenceML(String sentence) {
		this.sentence = sentence;
	}

	public void setSentence(String sentence){
		this.sentence = sentence;
	}

	public String getSentence() {
		return sentence.replace('\'', '\"');
	}

	public String toString(){
		return sentence;
	}

	public List<String> getStart() {
		return start;
	}

	public void setStart(List<String> start) {
		this.start = new ArrayList<>(new HashSet<>(start));
	}

	public List<String> getA() {
		return a;
	}

	public void setA(List<String> a) {
		this.a = new ArrayList<>(new LinkedHashSet<>(a));
	}

	public List<String> getB() {
		return b;
	}

	public void setB(List<String> b) {
		this.b = new ArrayList<>(new HashSet<>(b));
	}

	public List<String> getEnd() {
		return end;
	}

	public void setEnd(List<String> end) {
		this.end = new ArrayList<>(new HashSet<>(end));
	}

	public List<String> getGreys() {
		return greys;
	}

	public void setGreys(List<String> greys) {
		this.greys = new ArrayList<>(new HashSet<>(greys));
	}

	public void setType(String type){ this.type = type; }

	public String getType(){ return type; }

    public List<String> getNewTasks() {
        return newTasks;
    }

    public void setNewTasks(List<String> newTasks) {
        this.newTasks = new ArrayList<>(new HashSet<>(newTasks));
    }

	public static String toJSON(DifferencesML diffs) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(diffs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
