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

import hub.top.petrinet.Node;
import hub.top.petrinet.Place;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Marking implements Comparable<Marking>{
	private HashSet<Place> marking;
	private HashSet<Node> history;
	private Trace<Node> trace;
	private HashMap<Node, Node> mappings;
	private HashMap<Object, String> colors;
	private boolean foundEnd;
	private LinkedList<Node> underlyingConf;
	
	public Marking(HashSet<Place> marking, Trace<Node> trace){
		this.marking = marking;
		this.trace = trace;
		history = new HashSet<>();
		mappings = new HashMap<>();
		colors = new HashMap<>();
		foundEnd = false;
		underlyingConf = new LinkedList<Node>();
	}
	
	public Marking(HashSet<Place> marking, Trace<Node> trace, HashSet<Node> history, HashMap<Node, Node> mappings, HashMap<Object, String> colors, LinkedList<Node> underlyingConf){
		this.marking = marking;
		this.trace = trace;
		this.history = history;
		this.mappings = mappings;
		this.colors = colors;
		foundEnd = false;
		this.underlyingConf = underlyingConf;
	}

	public HashSet<Place> getMarking() {
		return marking;
	}

	public void setMarking(HashSet<Place> marking) {
		this.marking = marking;
	}

	public HashSet<Node> getHistory() {
		return history;
	}

	public void setHistory(HashSet<Node> history) {
		this.history = history;
	}

	public Trace<Node> getTrace() {
		return trace;
	}

	public void setTrace(Trace<Node> trace) {
		this.trace = trace;
	}
	
	@Override
	public int compareTo(Marking o) {
		if(marking.size() != o.marking.size())
			return marking.size() - o.marking.size();
		
		if(marking.containsAll(o.marking) && o.mappings.equals(mappings))
			return 0;
					
		return -1;
	}
	
	public String toString(){
		return "Marking = ("+marking+"), history =("+history+")";
	}
	
	@Override
	public Marking clone(){
		return new Marking(new HashSet<Place>(marking), trace.clone(), new HashSet<Node>(history), new HashMap<Node, Node>(mappings), new HashMap<Object, String>(colors), new LinkedList<Node>(underlyingConf)); 
	}
	
	public void removePlaces(List<Place> places){
		marking.removeAll(places);
	}
	
	public void addTransition2History(Node t){
		history.add(t);
	}

	public void addPlaces(List<Place> places) {
		marking.addAll(places);
	}

	public HashMap<Node, Node>  getMappings() {	
		return mappings;
	}

	public void addMapping(Node source, Node target) {
		mappings.put(source, target);
	}

	public void addColor(Node t, String color) {
		if(color.equals("red"))
			foundEnd = true;
		
		colors.put(t, color);
	}

	public HashMap<Object, String> getColors() {
		return colors;
	}

	public void removeStrCause(Node str) {
		this.trace.log.remove(str);
	}
	
	public void removeWkCause(Node wk) {
		this.trace.log.remove(wk);
	}

	public boolean foundEnd() {
		return foundEnd;
	}

	public void addUnderConf(Node str) {
		underlyingConf.add(str);
	}

	public HashSet<Node> getUnderConf() {
		return new HashSet<Node>(underlyingConf);
	}
}
