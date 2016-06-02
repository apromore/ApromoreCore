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

	/**
	 * Initialize the unfolder to construct a finite complete prefix of a safe
	 * Petri net.
	 * 
	 * @param net
	 *            a safe Petri net
	 */
	public Unfolder_PetriNet(PetriNet net, MODE mode) {
		try {
			sys = new BPstructBPSys(net);

			Options o = new Options(sys);
			// configure to unfold a Petri net
			o.configure_PetriNet();
			// stop construction of unfolding when reaching an unsafe marking
			o.configure_stopIfUnSafe();

			// initialize unfolder
			bp = new BPstructBP(sys, o);

			bp.setMode(mode);

		} catch (InvalidModelException e) {

			System.err.println("Error! Invalid model.");
			System.err.println(e);
			sys = null;
			bp = null;
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
}
