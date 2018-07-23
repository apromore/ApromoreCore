/* 
 * Copyright (C) 2010 - Artem Polyvyanyy, Luciano Garcia Banuelos
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ee.ut.nets.unfolding;

import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;
import hub.top.uma.DNode;
import hub.top.uma.InvalidModelException;
import hub.top.uma.Options;
import hub.top.uma.DNodeSet.DNodeSetElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ee.ut.nets.unfolding.BPstructBP.MODE;

/**
 * This class is a modification to the original implementation provided in uma
 * package This version provides access to the Unfolding and allows incremental
 * unfolding.
 * 
 * @author Luciano Garcia Banuelos, Artem Polyvyanyy
 */
public class Unfolder_PetriNet {

	// a special representation of the Petri net
	private BPstructBPSys sys;

	// a branching process of the Petri net (the unfolding)
	private BPstructBP bp;
	
	BiMap<Node, String> transitionLabel;

	HashMap<String, String> label2Label;
	private HashMap<Short, Node> mapDNodeTrans;

	/**
	 * Initialize the unfolder to construct a finite complete prefix of a safe
	 * Petri net.
	 * 
	 * @param net
	 *            a safe Petri net
	 */
	public Unfolder_PetriNet(PetriNet net, MODE mode, HashSet<String> silent) {
		try {
			relabel(net);
			
			sys = new BPstructBPSys(net);

			Options o = new Options(sys);
			// configure to unfold a Petri net
			o.configure_PetriNet();
			// stop construction of unfolding when reaching an unsafe marking
			o.configure_stopIfUnSafe();

			// initialize unfolder
			bp = new BPstructBP(sys, o);
			bp.setMode(mode);
			bp.setSilent(silent);
			
			mapDNodeTrans = new HashMap<>();

			for (Node trans : net.getTransitions())
				mapDNodeTrans.put(sys.getResultNode(trans).id, trans);
			
		} catch (InvalidModelException e) {
			System.err.println("Error! Invalid model.");
			System.err.println(e);
			sys = null;
			bp = null;
		}
	}

	private void relabel(PetriNet net) {
		transitionLabel = HashBiMap.<Node, String> create();
		label2Label = new HashMap<>();
		HashMap<String, Integer> counterLabel = new HashMap<>();

		counterLabel.put("silent", 0);
		for(Transition t : net.getTransitions()){
			
			if(t.getName().equals("")){
				String newLabel = "silent_added_" + (counterLabel.get("silent")+1);
				counterLabel.put("silent", counterLabel.get("silent")+1);
				label2Label.put(newLabel, "");
				t.setName(newLabel);
				transitionLabel.put(t, newLabel);
			}else if(!counterLabel.containsKey(t.getName()))
				counterLabel.put(t.getName(), 0);
			else{
				String newLabel = t.getName() + "_copy_" + (counterLabel.get(t.getName())+1);
				counterLabel.put(t.getName(), counterLabel.get(t.getName())+1);
				label2Label.put(newLabel, t.getName());
				t.setName(newLabel);
				transitionLabel.put(t, newLabel);
			}
		}
		
		for(Node t : net.getPlaces()){
			if(t.getName().equals("")){
				String newLabel = "silent_added_" + (counterLabel.get("silent")+1);
				counterLabel.put("silent", counterLabel.get("silent")+1);
				label2Label.put(newLabel, "");
				t.setName(newLabel);
				transitionLabel.put(t, newLabel);
			}else if(!counterLabel.containsKey(t.getName()))
				counterLabel.put(t.getName(), 0);
			else{
				String newLabel = t.getName() + "_copy_" + (counterLabel.get(t.getName())+1);
				counterLabel.put(t.getName(), counterLabel.get(t.getName() + 1));
				label2Label.put(newLabel, t.getName());
				t.setName(newLabel);
				transitionLabel.put(t, newLabel);
			}
		}
	}

	/**
	 * Compute the unfolding of the net
	 */
	public void computeUnfolding() {
		int total_steps = 0;
		int current_steps = 0;
		// extend unfolding until no more events can be added
		while ((current_steps = bp.step()) > 0) {
			total_steps += current_steps;
			System.out.print(total_steps + "... ");
		}
	}

	public PetriNet getUnfoldingAsPetriNet(Set<String> visibleLabels, HashMap<Node, Multiplicity> repetitionIndex, HashMap<Node, Node> originalMap) {
		PetriNet unfolding = new PetriNet();
		DNodeSetElement allNodes = bp.getBranchingProcess().getAllNodes();
		BiMap<Node, DNode> map = HashBiMap.create();	
		
		sys.packageProperNames();

		HashMap<Integer, Node> nodeMap = new HashMap<>();

		// first print all conditions
		for (DNode n : allNodes) {
			if (n.isEvent)
				continue;

			// if (!option_printAnti && n.isAnti) continue;

			String name = n.toString();
			if (n.isAnti)
				name = "NOT " + name;
			else if (n.isCutOff){
//				name ="CUT(" + name + ")";
			}
				
			Place p = unfolding.addPlace(name);
			nodeMap.put(n.globalId, p);

			if (bp.getBranchingProcess().initialConditions.contains(n))
				p.setTokens(1);
		}

		for (DNode n : allNodes) {
			if (!n.isEvent)
				continue;

			// if (!option_printAnti && n.isAnti) continue;

			String name = sys.properNames[n.id];
			if (n.isAnti)
				name = "NOT " + name;
			else if (n.isCutOff){
//				name = "CUT(" + name + ")";
			}
			
			Transition t = unfolding.addTransition(name);

			if(originalMap!= null && mapDNodeTrans != null && mapDNodeTrans.get(n.id) != null)
				originalMap.put(t, mapDNodeTrans.get(n.id));
			
			nodeMap.put(n.globalId, t);
			map.put(t, n);
		}

		for (DNode n : allNodes) {
			if (n.isEvent) {
				for (DNode pre : n.pre) {
					unfolding.addArc((Place) nodeMap.get(pre.globalId),
							(Transition) nodeMap.get(n.globalId));
				}
			} else {
				for (DNode pre : n.pre) {
					unfolding.addArc((Transition) nodeMap.get(pre.globalId),
							(Place) nodeMap.get(n.globalId));
				}
			}
		}
		
		HashSet<DNode> steadyEvts = getSteadyEvts();
		HashSet<Short> steadyEvtIds = new HashSet<Short>();
		for(DNode n : steadyEvts)
			steadyEvtIds.add(n.id);
		
		steadyEvts = getSteadyEvts(visibleLabels);
		steadyEvtIds = new HashSet<Short>();
		for(DNode n : steadyEvts)
			steadyEvtIds.add(n.id);

		updateMultiplicity(repetitionIndex, map, nodeMap, visibleLabels, steadyEvtIds);
		
//		if(mapDT != null)
//			mapDT.putAll(mapDNodeTrans);
		
		return unfolding;
	}
	
	private void updateMultiplicity(HashMap<Node, Multiplicity> repetitionIndex,
			BiMap<Node, DNode> map, HashMap<Integer, Node> nodeMap, Set<String> visibleLabels, HashSet<Short> steadyEvtIds) {
		HashMap<Short, LinkedList<DNode>> byIds = groupById(bp.getBranchingProcess().getAllEvents());
		HashMap<Short, Multiplicity> repetitionI = new HashMap<>();
		
		for(Short key : byIds.keySet())
			repetitionI.put(key, Multiplicity.ZERO);
		
		for(Short key : byIds.keySet())
			for(DNode d1 : byIds.get(key)){
				HashSet<DNode> repetitions = countOcc(d1);
				
				if(repetitions.size() > 0)
					for(DNode d2 : repetitions)
						if(d1 != d2 && isDirectPred(d2, visibleLabels, d1))
							if(steadyEvtIds.contains(d1.id))
								repetitionI.put(key, Multiplicity.LOOP_ONE_MORE);
							else
								repetitionI.put(key, Multiplicity.LOOP_ZERO_MORE);
						else if(steadyEvtIds.contains(d1.id) && !(repetitionI.get(key).equals(Multiplicity.LOOP_ZERO_MORE) || repetitionI.get(key).equals(Multiplicity.LOOP_ONE_MORE)))
							repetitionI.put(key, Multiplicity.ONE_MORE);
						else if(!(repetitionI.get(key).equals(Multiplicity.LOOP_ZERO_MORE) || repetitionI.get(key).equals(Multiplicity.LOOP_ONE_MORE)) && !repetitionI.get(key).equals(Multiplicity.ONE_MORE))
							repetitionI.put(key, Multiplicity.ZERO_MORE);
			}
		
		BiMap<DNode, Node> inverseMap = map.inverse();
		
		for(DNode n : inverseMap.keySet())
			repetitionIndex.put(inverseMap.get(n), repetitionI.get(n.id));
		
//		System.out.println("Repetitions = " + repetitionIndex);
	}
	
	private HashSet<DNode> countOcc(DNode n) {
		HashSet<DNode> occurrences = new HashSet<DNode>();
		
		for(DNode p : n.getAllPredecessors())
			if(p.isEvent && p != n)
				if(p.id == n.id)
					occurrences.add(p);
		
		return occurrences;
	}
	
	private boolean isDirectPred(DNode r, Set<String> visibleLabels, DNode n) {
		boolean dPred = true;
		
		LinkedList<DNode> precs = new LinkedList<>(n.getAllPredecessors());
		precs.removeAll(r.getAllPredecessors());
		
		for(DNode p : precs)
			if(p.isEvent && p != n){
				if(visibleLabels.contains(sys.properNames[p.id]))
						dPred = false;
			}else if(!p.getAllPredecessors().contains(r))
				dPred = false;
		
		return dPred;
	}
	
	private HashMap<Short, LinkedList<DNode>> groupById(DNodeSetElement allEvents) {
		HashMap<Short, LinkedList<DNode>> nodes = new HashMap<Short, LinkedList<DNode>>(); 
		
		for(DNode n : allEvents){
			if(!nodes.containsKey(n.id))
				nodes.put(n.id, new LinkedList<DNode>());
			nodes.get(n.id).add(n);
		}
		
		return nodes;
	}
	
	private HashSet<DNode> getSteadyEvts() {
		DNodeSetElement allEvents = bp.getBranchingProcess().getAllEvents();
		HashSet<DNode> steady = null;
		
		for (DNode n : allEvents)
			if(!n.isCutOff && isSink(n))
				if(steady == null){
					steady = new HashSet<DNode>(n.getAllPredecessors());
				}else
					steady.retainAll(n.getAllPredecessors());
		
		HashSet<DNode> toRemove = new HashSet<>();
		for(DNode n : steady)
			if(!n.isEvent)
				toRemove.add(n);
		
		steady.removeAll(toRemove);
		
		return steady;
	}
	
	private HashSet<DNode> getSteadyEvts(Set<String> visibleLabels) {
		DNodeSetElement allEvents = bp.getBranchingProcess().getAllEvents();
		HashSet<DNode> steady = null;
		
		for (DNode n : allEvents)
			if(!n.isCutOff && isSink(n))
				if(steady == null){
					steady = new HashSet<DNode>(n.getAllPredecessors());
				}else
					steady.retainAll(n.getAllPredecessors());
		
		HashSet<DNode> toRemove = new HashSet<>();
		for(DNode n : steady)
			if(!n.isEvent || !visibleLabels.contains(sys.properNames[n.id]))
				toRemove.add(n);
		
		steady.removeAll(toRemove);
		
		return steady;
	}
	
	private boolean isSink(DNode n) {
		DNode[] post = n.post;
		
		for(int i=0 ; i < post.length; i++)
			if(post[i].post == null || post[i].post.length == 0)
				return true;
		
		return false;
	}
	
	/**
	 * Convert the unfolding into a Petri net and return this Petri net
	 */
	public PetriNet getUnfoldingAsPetriNet() {
		PetriNet unfolding = new PetriNet();
		DNodeSetElement allNodes = bp.getBranchingProcess().getAllNodes();
		
		sys.packageProperNames();

		HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();

		// first print all conditions
		for (DNode n : allNodes) {
			if (n.isEvent)
				continue;

			System.out.println("Data about generator = " + n.id + " -- "+ n.idGenerator+ " -- "+ n.idGen + " -- ");
			
			// if (!option_printAnti && n.isAnti) continue;
			
			String name = n.toString();
			if (n.isAnti)
				name = "NOT " + name;
			else if (n.isCutOff)
				name = "CUT(" + name + ")";

			Place p = unfolding.addPlace(name);
			nodeMap.put(n.globalId, p);

			if (bp.getBranchingProcess().initialConditions.contains(n))
				p.setTokens(1);
		}

		for (DNode n : allNodes) {
			if (!n.isEvent)
				continue;

			// if (!option_printAnti && n.isAnti) continue;

			String name = sys.properNames[n.id];
			if (n.isAnti)
				name = "NOT " + name;
			else if (n.isCutOff)
				name = "CUT(" + name + ")";

			Transition t = unfolding.addTransition(name);
			nodeMap.put(n.globalId, t);
		}

		for (DNode n : allNodes) {
			if (n.isEvent) {
				for (DNode pre : n.pre) {
					unfolding.addArc((Place) nodeMap.get(pre.globalId),
							(Transition) nodeMap.get(n.globalId));
				}
			} else {
				for (DNode pre : n.pre) {
					unfolding.addArc((Transition) nodeMap.get(pre.globalId),
							(Place) nodeMap.get(n.globalId));
				}
			}
		}
		return unfolding;
	}

	/**
	 * @return the unfolding in GraphViz dot format
	 */
	public String getUnfoldingAsDot() {
		return bp.getBranchingProcess().toDot(sys.properNames);
	}

	/**
	 * Get the branching process
	 */
	public BPstructBP getBP() {
		return bp;
	}

	public BPstructBPSys getSys() {
		return sys;
	}

	public String getOriginalLabel(String string) {
		if(label2Label != null && label2Label.containsKey(string))
			return label2Label.get(string);
		
		return string;
	}

    public HashMap<Short, Node> getMapDNodeTrans() {
        return mapDNodeTrans;
    }
}
