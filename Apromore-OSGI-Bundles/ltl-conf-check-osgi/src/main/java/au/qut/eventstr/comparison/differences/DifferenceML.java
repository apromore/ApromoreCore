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

package au.qut.eventstr.comparison.differences;


import org.codehaus.jackson.map.ObjectMapper;

import java.text.DecimalFormat;
import java.util.*;

/**
 * This is the main container of a difference.
 * It contains a sentence, the runs in model 1 and 2
 * expressing the discovered difference between the 
 * models. 
 */

public class DifferenceML implements Comparable<DifferenceML>{
	private String sentence;
	private List<String> start;
	private List<String> a;
	private List<String> b;
	private List<String> end;
	private List<String> greys;
    private List<String> newTasks;
	// TASKRELOC
	private List<String> start2;
	private List<String> end2;

	private String type;
	private float ranking;

//    private static final Map<String, Integer> typeRanking;
//    static
//    {
//        typeRanking = new HashMap<String, Integer>();
//        typeRanking.put("TASKRELOC",5);
//        typeRanking.put("TASKSKIP",7);
//        typeRanking.put("UNMREPETITION",8);
//        typeRanking.put("UNOBSACYCLIC",9);
//        typeRanking.put("UNOBSCYCLIC",10);
//        typeRanking.put("TASKSUB",4);
//    }

	public DifferenceML(float ranking){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        this.ranking = Float.parseFloat(df.format(ranking));

		this.start2 = new LinkedList<>();
		this.end2 = new LinkedList<>();
    }

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

	public void setStart2(List<String> start2) {
		this.start2 = new ArrayList<>(new HashSet<>(start2));
	}

	public List<String> getStart2() {
		return start2;
	}

	public void setStart(List<String> start) {
		this.start = new ArrayList<>();

		if(!start.isEmpty())
			this.start.add(start.get(0));
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

	public List<String> getEnd2() {
		return end2;
	}

	public void setEnd2(List<String> end2) {
		this.end2 = new ArrayList<>(new HashSet<>(end2));
	}

	public List<String> getEnd() {
		return end;
	}

	public void setEnd(List<String> end) {
		this.end = new ArrayList<>();

		if(!end.isEmpty())
			this.end.add(end.get(0));
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
        this.newTasks = new ArrayList<>(new LinkedHashSet<>(newTasks));
    }

    public float getRanking(){ return ranking; }

	public static String toJSON(DifferencesML diffs) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(diffs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

    public int compareTo(DifferenceML differenceML){
        if(this.ranking > differenceML.ranking)
            return -1;

//        int tR1 = typeRanking.containsKey(this.getType()) ? typeRanking.get(this.type).intValue() : 0;
//        int tR2 = typeRanking.containsKey(differenceML.getType()) ? typeRanking.get(differenceML.getType()).intValue() : 0;;
//
//        if(((tR1 == 0&& tR2 != 0) || (tR1 != 0&& tR2 == 0)) && tR1 > tR2)
//            return -1;

        return 1;
    }
}
