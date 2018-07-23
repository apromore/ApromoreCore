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

package ee.ut.eventstr.comparison;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.NOpenPartialSynchronizedProduct.Operation.Op;
import ee.ut.org.processmining.framework.util.Pair;

public class NOpenPartialSynchronizedProduct<T> {
	
	enum StateHint {CREATED, MERGED, DISCARDED}
	public static class State {
		BitSet c1;
		Multiset<Integer> c2;
		float weight = 0.0f;
		Multiset<String> labels;
		
		StateHint hint;

		State(BitSet c1, Multiset<String> labels, Multiset<Integer> c2) {
			this.c1 = c1; this.c2 = c2; this.labels = labels;
		}
		
		public String toString() {
			return String.format("<%s,%s,%s,%3.2f>", c1, labels, c2, weight);
		}
	}
		
	static class Operation {
		enum Op {MATCH, LHIDE, RHIDE, MATCHNSHIFT, RHIDENSHIFT};
		Op op;
		String label;
		State nextState;
		Object target;
		
		private Operation(State state, Op op, Object target, String label) {
			this.nextState = state; this.target = target;
			this.op = op; this.label = label;
		}
		static Operation match(State state, Pair<Integer, Integer> target, String label) {
			return new Operation(state, Op.MATCH, target, label);
		}
		static Operation lhide(State state, Integer target, String label) {
			return new Operation(state, Op.LHIDE, target, label);
		}
		static Operation rhide(State state, Integer target, String label) {
			return new Operation(state, Op.RHIDE, target, label);
		}
		static Operation rhidenshift(State state, Integer target, String label) {
			return new Operation(state, Op.RHIDENSHIFT, target, label);
		}
		static Operation matchnshift(State state, Pair<Integer, Integer> target, String label) {
			return new Operation(state, Op.MATCHNSHIFT, target, label);
		}
		
		public String toString() {
			return String.format("%s(%s[%s])", op.toString().toLowerCase(), label, target);
		}
	}
	
	static class WeightBasedStateComparator implements Comparator<State> {
		public int compare(State o1, State o2) {
			return Float.compare(o1.weight, o2.weight);
		}		
	}

	private SinglePORunPESSemantics<T> pes1;
	private NewUnfoldingPESSemantics<T> pes2;
	private Multimap<BitSet, State> lMATCHES;
	private Multimap<Multiset<Integer>, State> rMATCHES;

	private List<State> states;
	private Set<State> relevantStates;
	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;
	private State root;
	private Table<BitSet, Multiset<Integer>, State> stateSpaceTable = HashBasedTable.create();
	
	private Multimap<Multiset<Integer>, State> cases;

	public NOpenPartialSynchronizedProduct(SinglePORunPESSemantics<T> pes1, NewUnfoldingPESSemantics<T> pes2) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.states = new ArrayList<>();
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();
		this.lMATCHES = HashMultimap.create();
		this.rMATCHES = HashMultimap.create();
	}

	public NOpenPartialSynchronizedProduct<T> perform() {
		Map<BitSet, Float> lGW = new HashMap<>();
		Map<BitSet, Float> rGW = new HashMap<>();

		for (BitSet mconf: pes1.getMaxConf())
			lGW.put(mconf, Float.MAX_VALUE);
		for (BitSet mconf: pes2.getMaxConf())
			rGW.put(mconf, Float.MAX_VALUE);

		Queue<State> open = new PriorityQueue<State>(11, new WeightBasedStateComparator());

		root = getState(new BitSet(), HashMultiset.<String> create(), HashMultiset.<Integer> create()).getSecond();
		open.offer(root);

		while (!open.isEmpty()) {
			State s = open.poll();
			
			if (isCandidate(s.c1, s, pes1, lGW)) {
				BitSet lpe = pes1.getPossibleExtensions(s.c1);				
				Set<Integer> rpe = pes2.getPossibleExtensions(s.c2);
				
				if (lpe.isEmpty() && rpe.isEmpty()) {
					updateMatches(s, lGW, rGW);
					continue;
				}
				
				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
					String label1 = pes1.getLabel(e1);
					BitSet c1p = (BitSet)s.c1.clone();
					c1p.set(e1);
					
					/// HIDE (left)
					Pair<StateHint, State> pair = getState(c1p, s.labels, s.c2);
					switch (pair.getFirst()) {
					case CREATED:
					case MERGED:
						open.offer(pair.getSecond()); // Only if not previously visited
						descendants.put(s, Operation.lhide(pair.getSecond(), e1, label1));
						ancestors.put(pair.getSecond(), s);
					default:
					}
					
//					IOUtils.toFile("psp.dot", toDot());


					for (Integer e2: rpe) {
						if (label1.equals(pes2.getLabel(e2)) 
								&& isOrderPreserving(s, e1, e2)
							) {
							Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);
							
							Multiset<String> labels = HashMultiset.create(s.labels);
							labels.add(label1);
							
							pair = getState(c1p, labels, extPair.getFirst());
							
							switch (pair.getFirst()) {
							case CREATED:
							case MERGED:
								open.offer(pair.getSecond()); // Only if not previously visited
								if (extPair.getSecond()) // TRUE == Shift
									descendants.put(s, Operation.matchnshift(pair.getSecond(), new Pair<>(e1, e2), label1));
								else
									descendants.put(s, Operation.match(pair.getSecond(), new Pair<>(e1, e2), label1));
								ancestors.put(pair.getSecond(), s);
							default:
							}
							
//							IOUtils.toFile("psp.dot", toDot());
						}
					}
				}
				
				for (Integer e2: rpe) {
					/// HIDE (right)
					Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);

					Pair<StateHint, State> pair = getState(s.c1, s.labels, extPair.getFirst());
					switch (pair.getFirst()) {
					case CREATED:
					case MERGED:
						open.offer(pair.getSecond()); // Only if not previously visited
						if (extPair.getSecond()) // TRUE == Shift
							descendants.put(s, Operation.rhidenshift(pair.getSecond(), e2, pes2.getLabel(e2)));
						else
							descendants.put(s, Operation.rhide(pair.getSecond(), e2, pes2.getLabel(e2)));
						ancestors.put(pair.getSecond(), s);
					default:
					}
					
//					IOUtils.toFile("psp.dot", toDot());
				}
			}
		}
		
		return this;
	}
	
	private boolean isOrderPreserving(State s, int e1, Integer e2) {
		BitSet e1dpred = (BitSet)pes1.getDirectPredecessors(e1).clone();
		Set<Integer> e2dpred = new HashSet<>(pes2.getDirectPredecessors(e2));
		
		Stack<State> open = new Stack<>();
		Set<State> visited = new HashSet<>();
		open.push(s);
		
		BitSet e1causes = pes1.getLocalConfiguration(e1);
		BitSet e2causes = pes2.getLocalConfiguration(e2);
		
		while (!open.isEmpty()) {
			if (e1dpred.isEmpty() && e2dpred.isEmpty())
				break;
			State curr = open.pop();
			visited.add(curr);
			
			for (State ancestor: ancestors.get(curr)) {
				if (visited.contains(ancestor) || open.contains(ancestor)) continue;
				for (Operation op: descendants.get(ancestor))
					if (op.nextState.equals(curr)) {
//						System.out.println(">> " + op);
						if (op.op == Op.MATCH) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> matchedEvents = (Pair<Integer,Integer>)op.target;
							e1dpred.clear(matchedEvents.getFirst());
							e2dpred.remove(matchedEvents.getSecond());
							
							if (!(e1causes.get(matchedEvents.getFirst()) == e2causes.get(matchedEvents.getSecond()))) {
//								System.out.println("====== It is not order preserving!");
								return false;
							}

						} else if (op.op == Op.MATCHNSHIFT) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> matchedEvents = (Pair<Integer,Integer>)op.target;
							e1dpred.clear(matchedEvents.getFirst());
							e2dpred.remove(matchedEvents.getSecond());
							
//							System.out.println("Performed inverse shift (+match): " + matchedEvents.getSecond());
							if (pes2.getBRelation(e2, matchedEvents.getSecond()) != BehaviorRelation.CONCURRENCY) {
								e2causes = pes2.unshift(e2causes, matchedEvents.getSecond());
//								e2causes = pes2.getLocalConfiguration(matchedEvents.getSecond());
							}
							
							if (!(e1causes.get(matchedEvents.getFirst()) == e2causes.get(matchedEvents.getSecond()))) {
//								System.out.println("====== It is not order preserving! (after inverse shift)");
								return false;
							}
						} else if (op.op == Op.RHIDENSHIFT || op.op == Op.RHIDE) {
							Integer hiddenEvent = (Integer)op.target;
//							if (e2dpred.contains(hiddenEvent)) {
								e2dpred.remove(hiddenEvent);
								e2dpred.addAll(pes2.getDirectPredecessors(hiddenEvent));
								if (op.op == Op.RHIDENSHIFT && pes2.getBRelation(e2, hiddenEvent) != BehaviorRelation.CONCURRENCY) {
//									System.out.println("Performed inverse shift: " + hiddenEvent);
									e2causes = pes2.unshift(e2causes, hiddenEvent);
//									e2causes.clear(hiddenEvent);
	//								e2causes = pes2.getLocalConfiguration(hiddenEvent);
								}
//							}
						} else {
							Integer hiddenEvent = (Integer)op.target;
							e1dpred.clear(hiddenEvent);
							e1dpred.or(pes1.getDirectPredecessors(hiddenEvent));
						}
					}
				open.push(ancestor);
			}
		}
		return true;
	}

	private void updateMatches(State s, Map<BitSet, Float> lGW, Map<BitSet, Float> rGW) {
		if (s.weight <= lGW.get(s.c1)) {
			lGW.put(s.c1, s.weight);
			lMATCHES.put(s.c1, s);
		}
	}
	
	private Pair<StateHint,State> getState(BitSet c1, Multiset<String> labels, Multiset<Integer> c2) {
		State newState = eta(new State(c1, labels, c2));
		StateHint action = newState.hint = StateHint.CREATED;
		states.add(newState);
		
		if (stateSpaceTable.contains(c1, c2)) {
			State pivot = stateSpaceTable.get(c1, c2);
			if (labels.containsAll(pivot.labels))
				newState.hint = action = StateHint.DISCARDED;
			// TODO: complete the code!!!
		} else {
			stateSpaceTable.put(c1, c2, newState);
		}
		return new Pair<>(action, newState);
	}

	public State eta(State s) {
		Multiset<Integer> c2copy = HashMultiset.create(s.c2);
		c2copy.removeAll(pes2.getInvisibleEvents());
		s.weight = g(s.c1, c2copy, s.labels)
				+ h(s.c1, s.c2)
				;
		return s;
	}
		
	public float g(BitSet c1, Multiset<Integer> c2, Multiset<String> labels) {
		return c1.cardinality() + c2.size() - labels.size() * 2.0f;
	}
	
	public float h(BitSet c1, Multiset<Integer> c2) {
		Set<String> pf1 = pes1.getPossibleFutureAsLabels(c1);
		Set<String> pf2 = pes2.getPossibleFutureAsLabels(c2);
		
		pf1.removeAll(pf2);
		return pf1.size();
	}


	private boolean isCandidate(BitSet c1, State s, SinglePORunPESSemantics<T> pes, Map<BitSet, Float> gW) {
		for (BitSet mconf: gW.keySet())
			if (contains(mconf, c1) && s.weight <= gW.get(mconf))
				return true;
		return false;
	}

	private boolean contains(BitSet superset, BitSet subset) {
		int cardinality = superset.cardinality();
		superset = (BitSet)superset.clone();
		superset.or(subset);
		return cardinality == superset.cardinality();
	}
	
	public NOpenPartialSynchronizedProduct<T> prune() {
		Set<State> gvisited = new HashSet<>();
		Set<State> finalStates = new HashSet<>(lMATCHES.values());
		finalStates.addAll(rMATCHES.values());
		Stack<State> open = new Stack<>();
		for (State s: finalStates) {
			open.push(s);
			Set<State> visited = new HashSet<>();
			while (!open.isEmpty()) {
				State curr = open.pop();
				visited.add(curr);
				
				for (State pred: ancestors.get(curr))
					if (!visited.contains(pred) && !open.contains(pred))
						open.push(pred);
			}
			

			gvisited.addAll(visited);
		}
		
		this.relevantStates = gvisited;
		
		//System.out.println("Number of final states: " + lMATCHES.values().size());
		//System.out.println("Number of relevant states: " + relevantStates.size());
		
		return this;
	}

	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		Map<State, Integer> rstates = new HashMap<>();
		
		for (int i = 0; i < states.size(); i ++) {
			State s = states.get(i);
			if (relevantStates == null || relevantStates.contains(s)) {
				rstates.put(s, i);
				if (lMATCHES.containsValue(s))
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%3.2f\",color=blue];\n", i, s.c1, s.c2, s.labels, s.weight);
				else
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%3.2f\"];\n", i, s.c1, s.c2, s.labels, s.weight);
			}
		}

		Collection<State> lstates = relevantStates != null ? relevantStates : states; 
		
		for (State s: lstates) {
			Integer src = rstates.get(s);
			for (Operation op: descendants.get(s)) {
				if (relevantStates == null || relevantStates.contains(op.nextState)) {
					Integer tgt = rstates.get(op.nextState);
					out.printf("\tn%d -> n%d [label=\"%s\"];\n", src, tgt, op);
				}
			}
		}
		out.println("}");
		return str.toString();
	}

	public void verbalize(Map<Multiset<Integer>, Multiset<Integer>> map) {
		if (relevantStates == null)
			prune();
		
//		Set<Integer> primeUnobservedEvents = new HashSet<>();
//		Set<Integer> unobservedEvents = new HashSet<>(pes2.getEvents());
//		for (State state: lMATCHES.values())
//			unobservedEvents.removeAll(state.c2);
//		
//		for (Integer ev: unobservedEvents) {
//			boolean found = false;
//			for (Integer pred: pes2.getDirectPredecessors(ev))
//				if (unobservedEvents.contains(pred)) {
//					found = true;
//					break;
//				}
//			if (!found) primeUnobservedEvents.add(ev);
//		}
//				
//		for (Integer ev: primeUnobservedEvents) {
//			if (pes2.getInvisibleEvents().contains(ev)) {
//				BitSet _causes = pes2.getLocalConfiguration(ev); _causes.clear(ev);
//				Multiset<Integer> causes = HashMultiset.create();
//				for (int event = _causes.nextSetBit(0); event >=0; event = _causes.nextSetBit(event + 1))
//					causes.add(event);
//				Set<Integer> pe2 = pes2.getPossibleExtensions(causes);
//
//				for (Integer event: pe2)
//					if (pes2.getBRelation(ev, event) == BehaviorRelation.CONFLICT)
//						System.out.printf("    In PES2, '%s' can be skipped, while in PES1 it cannot.\n", pes2.getLabel(event));
//			} else {
//				System.out.printf("    Task '%s' appears in PES2 and not in PES1\n", pes2.getLabel(ev));
//			}
//		}
		
		// Analysis -- acyclic mode!
		verbalizeAcyclicDifferences(dfs1());
				
		cases = HashMultimap.create();
		dfs(root, map.keySet(), HashMultiset.<Integer> create());
		
		for (Multiset<Integer> footprint: map.keySet()) {
			if (!cases.containsKey(footprint)) {
				System.out.printf("The repetition interval '%s' is not observed in the event log.\n", translate(map.get(footprint)));
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	////////    VERBALIZATION OF ACYCLIC DIFFERENCES !!!!!
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	private void verbalizeAcyclicDifferences(List<LinkedList<Pair<State, Operation>>> differences) {
		for (LinkedList<Pair<State, Operation>> diffContext: differences) {
			State ctxStartState = diffContext.getFirst().getFirst();
			State ctxEndState = diffContext.getLast().getFirst();
			
			State ctxPrestartState = getStatePredecessor(ctxStartState, diffContext.getFirst().getSecond());
			
			System.out.println("==================================");
			BitSet pe1 = pes1.getPossibleExtensions(ctxPrestartState.c1);
			System.out.printf("Possible extenssions PES1 %s, %s\n", pe1, translate(pe1));
			Multiset<Integer> pe2 = HashMultiset.create(pes2.getPossibleExtensions(ctxPrestartState.c2));
			System.out.printf("Possible extenssions PES2 %s, %s\n", pe2, translate(pe2));
			
			Pair<Integer, Integer> firstMatchedPair = (Pair)diffContext.getFirst().getSecond().target;
			
			BitSet conf1 = (BitSet)ctxEndState.c1.clone();
			conf1.andNot(ctxStartState.c1); conf1.set(firstMatchedPair.getFirst());
			System.out.println("Left context: " + conf1);
			
			HashMultiset<Integer> conf2 = HashMultiset.create(ctxEndState.c2);
			conf2.removeAll(ctxStartState.c2); conf2.add(firstMatchedPair.getSecond());
			System.out.println("Right context: " + conf2);
			
			Op hiddingOperation = diffContext.get(1).getSecond().op;
						
			if (hiddingOperation == Op.LHIDE) {
				processLHide(diffContext, conf1, conf2, pe2, firstMatchedPair, 1);
			} else if (hiddingOperation == Op.RHIDE) {
				int pos = -1;
				for (int i = 1; i < diffContext.size() - 1; i++) {
					Operation hoper = diffContext.get(i).getSecond();
					if (hoper.op == Op.LHIDE) {
						hiddingOperation = hoper.op;
						pos = i;
						break;
					} else if (
							(hoper.op == Op.RHIDE || hoper.op == Op.RHIDENSHIFT) &&
							!pes2.getInvisibleEvents().contains(pes2.getLabel((Integer)hoper.target))) {
						hiddingOperation = hoper.op;
						pos = i;
					}
				}
				
				if (hiddingOperation == Op.LHIDE)
					processLHide(diffContext, conf1, conf2, pe2, firstMatchedPair, 1);
				else
					processRHide(diffContext, pe1, firstMatchedPair, pos);				
			}

		}
	}

	private void processRHide(LinkedList<Pair<State, Operation>> diffContext,
			BitSet pe1, Pair<Integer, Integer> firstMatchedPair, int pos) {
		System.out.println("To process RHIDE");
		
		Integer firstHiding = (Integer)diffContext.get(1).getSecond().target;
		String firstHidingLabel = pes2.getLabel(firstHiding);
		boolean found = false;
		Pair<Integer, Integer> firstHidingMatching = null;
		
		System.out.printf("Hidden event: %s %s\n", firstHiding, firstHidingLabel);
		
		for (int i = pos + 1; i < diffContext.size() - 1; i++) {
			Operation secondHidingOperation = diffContext.get(i).getSecond();
			if ((secondHidingOperation.op == Op.LHIDE) &&
					firstHidingLabel.equals(pes1.getLabel((Integer)secondHidingOperation.target))) {
				firstHidingMatching = new Pair<Integer, Integer>((Integer)secondHidingOperation.target, firstHiding);
				System.out.println("Found a matching for a hidden event: " + firstHidingMatching);
				found = true;
				break;
			}
		}

		if (!found) {
			for (int ev = pe1.nextSetBit(0); ev >= 0; ev = pe1.nextSetBit(ev + 1))
				if (firstHidingLabel.equals(pes1.getLabel(ev))) {
					firstHidingMatching = new Pair<Integer, Integer>(ev, firstHiding);
					System.out.println("Found a matching for a hidden event: " + firstHidingMatching);
					found = true;
				}
		}
		
		if (found) {
			System.out.printf("    BR PES1: %s %s %s  \n", pes1.getLabel(firstMatchedPair.getFirst()), pes1.getBRelation(firstMatchedPair.getFirst(), firstHidingMatching.getFirst()), pes1.getLabel(firstHidingMatching.getFirst()));
			System.out.printf("    BR PES2: %s %s %s  \n", pes2.getLabel(firstMatchedPair.getSecond()), pes2.getBRelation(firstMatchedPair.getSecond(), firstHidingMatching.getSecond()), pes2.getLabel(firstHidingMatching.getSecond()));
		} else if (diffContext.get(0).getFirst().labels.contains(firstHidingLabel)) {
			System.out.printf("    In PES2, after occurrence of %s, event %s is duplicated, while in PES1 it is not\n",
					pes1.getLabel(firstMatchedPair.getFirst()), firstHidingLabel);	
		} else {
			State ctxStartState = diffContext.getFirst().getFirst();
			State matchingState = null;
			for (Operation op: descendants.get(ctxStartState))
				if ((op.op == Op.MATCH || op.op == Op.MATCHNSHIFT) && op.label.equals(firstHidingLabel)) {
					matchingState = op.nextState;
					break;
				}
			
			if (matchingState == null)
				System.out.printf("    Task '%s' appears in PES2 and not in PES1\n", firstHidingLabel);
			else
				System.out.printf("    Task '%s' can be skipped in PES2, but it is always executed in PES1\n", firstHidingLabel);				
		}

	}
	
	private void processLHide(LinkedList<Pair<State, Operation>> diffContext,
			BitSet conf1, Multiset<Integer> conf2, Multiset<Integer> pe2, Pair<Integer, Integer> firstMatchedPair, int pos) {
		Integer firstHiding = (Integer)diffContext.get(1).getSecond().target;
		Integer firstRHiding = null;
		String firstHidingLabel = pes1.getLabel(firstHiding);
		boolean found = false;
		Integer firstHidingMatching = null;
		
		for (int i = pos + 1; i < diffContext.size() - 1; i++) {
			Operation secondHidingOperation = diffContext.get(i).getSecond();
			if (secondHidingOperation.op == Op.RHIDE || secondHidingOperation.op == Op.RHIDENSHIFT) {
				if (firstHidingLabel.equals(pes2.getLabel((Integer)secondHidingOperation.target))) {
					firstHidingMatching = (Integer)secondHidingOperation.target;
					System.out.println("Found a matching for hidden event: " + firstHidingMatching);
					found = true;
					break;
				} else if (firstRHiding == null)
					firstRHiding = (Integer)secondHidingOperation.target;
			}
		}
		
		if (!found) {
			for (Integer ev: pe2)
				if (firstHidingLabel.equals(pes2.getLabel(ev))) {
					firstHidingMatching = ev;
					System.out.println("Found a matching for hidden event: " + firstHidingMatching);
					found = true;
				}
		}
		
		if (found) {
				System.out.println("Configuration 1: " + conf1);
				System.out.println("Direct Predecessors: " + pes1.getDirectPredecessors(firstHiding));

				System.out.println("Configuration 2: " + conf2);
				System.out.println("Direct Predecessors: " + pes2.getDirectPredecessors(firstHidingMatching));

				BitSet dpred1 = (BitSet)pes1.getDirectPredecessors(firstHiding).clone();
				Set<Integer> dpred2 = new HashSet<>(pes2.getDirectPredecessors(firstHidingMatching));
				
				List<Pair<Integer, Integer>> bdiffs = new ArrayList<>();
				
				Stack<Pair<State, Operation>> open = new Stack<>();
				open.push(diffContext.get(0));		
				while (!open.isEmpty()) {
					if (dpred1.cardinality() == 0 && dpred2.isEmpty())
						break;
					Pair<State, Operation> currentPair = open.pop();
					State curr = currentPair.getFirst();

					outer:
					for (State ancestor: ancestors.get(curr)) {
						if (relevantStates.contains(ancestor)) 
							for (Operation op: descendants.get(ancestor))
								if (op.nextState.equals(curr)) {
									if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT) {
										System.out.println("processing: " + op);
										Pair<Integer,Integer> m = (Pair)op.target;
										dpred1.clear(m.getFirst());
										dpred2.remove(m.getSecond());
										open.push(new Pair<>(ancestor, op));
										
										if (pes1.getBRelation(m.getFirst(), firstHiding) !=
												pes2.getBRelation(m.getSecond(), firstHidingMatching))
											bdiffs.add(m);
										
										break outer;
									} else {
										System.out.println("###### oops !!!");
//										throw new RuntimeException("Currently, only earliest discrepancy can be processed. Current context does not observe this constraint.");
									}
								}
					}
					
					if (bdiffs.size() > 0) {
						
					}
				}
				if (bdiffs.size() > 0) {
					for (Pair<Integer,Integer> m: bdiffs) {
						System.out.printf("    BR PES1: %s %s %s  \n", pes1.getLabel(m.getFirst()), pes1.getBRelation(m.getFirst(), firstHiding), pes1.getLabel(firstHiding));
						System.out.printf("    BR PES2: %s %s %s  \n", pes2.getLabel(m.getSecond()), pes2.getBRelation(m.getSecond(), firstHidingMatching), pes2.getLabel(firstHidingMatching));
					}
				}
		} else if (diffContext.get(0).getFirst().labels.contains(firstHidingLabel)) {
			System.out.printf("    In PES1, after occurrence of %s, event %s is duplicated, while in PES2 it is not.\n",
					pes1.getLabel(firstMatchedPair.getFirst()), firstHidingLabel);	
		} else {
			if (firstRHiding != null)
				System.out.printf("    In PES1, after occurrence of %s, event %s is substituted by %s.\n",
						pes1.getLabel(firstMatchedPair.getFirst()), firstHidingLabel, pes2.getLabel(firstRHiding));	
			else {
				System.out.printf("    In PES1, %s occurs after %s, whereas in PES2 it does not.\n",
						firstHidingLabel, pes1.getLabel(firstMatchedPair.getFirst()));	
			}
//			System.out.println("    Could be a SKIPPED event?");
		}
	}
	
	private State getStatePredecessor(State state, Operation operation) {
		for (State ancestor: ancestors.get(state))
			for (Operation op: descendants.get(ancestor))
				if (op.equals(operation)) {
					return ancestor;
				}
		return null;
	}

	private List<LinkedList<Pair<State, Operation>>> dfs1() {
		List<LinkedList<Pair<State, Operation>>> differences = new LinkedList<>();
		
		traverse(differences, new Pair<State, Operation>(root, null), null, false);
		
		int number = 0;
		for (LinkedList<Pair<State, Operation>> difference: differences) {
			StringWriter str = new StringWriter();
			PrintWriter out = new PrintWriter(str);
			
			out.println("digraph G {");

			Set<Integer> set1 = new HashSet<>();
			Set<Integer> set2 = new HashSet<>();
			for (Pair<State, Operation> pair: difference) {
				Operation op = pair.getSecond();
				switch (op.op) {
				case MATCH:
				case MATCHNSHIFT:
					Pair<Integer, Integer> mpair = (Pair)op.target;
					set1.add(mpair.getFirst());
					set2.add(mpair.getSecond());
					break;
				case LHIDE:
					set1.add((Integer)op.target);
					break;
				default:
					set2.add((Integer)op.target);
				}
			}
			
			pes1.toDot(out, set1);
			pes2.toDot(out, set2);
			
			out.println("}");
			
//			IOUtils.toFile("diff_" + number++ + ".dot", str.toString());
		}
		
//		System.exit(0);
		
		return differences;
	}
	
	private void traverse(List<LinkedList<Pair<State, Operation>>> differences,
			Pair<State, Operation> currentPair, LinkedList<Pair<State, Operation>> diffContext, boolean visibleEventHasBeenHidden) {
		State curr = currentPair.getFirst();
		System.out.println(curr);
		
		for (Operation op: descendants.get(curr)) {
			if (relevantStates.contains(op.nextState)) {
				if (diffContext == null) {
					Pair<State, Operation> desc = new Pair<>(op.nextState, op);

					if (op.op == Op.LHIDE) {
						System.out.println("Found earliest discrepancy (LHIDE): " + pes1.getLabel((Integer)op.target));	
						LinkedList<Pair<State, Operation>> diffContext1 = new LinkedList<>();
						diffContext1.add(currentPair);
						diffContext1.add(desc);
						traverse(differences, desc, diffContext1, true);
					} else if (op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT) {
						Integer hiddenEvent = (Integer)op.target;
						System.out.println("Found earliest discrepancy (RHIDE): " + pes2.getLabel(hiddenEvent));
							
						boolean visibleEventHasBeenHidden1 = !pes2.getInvisibleEvents().contains(hiddenEvent);
						LinkedList<Pair<State, Operation>> diffContext1 = new LinkedList<>();
						diffContext1.add(currentPair);
						diffContext1.add(desc);
						traverse(differences, desc, diffContext1, visibleEventHasBeenHidden1);
					} else {
						traverse(differences, desc, diffContext, visibleEventHasBeenHidden);
					}
				} else {
					Pair<State, Operation> desc = new Pair<>(op.nextState, op);
					diffContext.add(desc);
					if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT) {
						if (visibleEventHasBeenHidden) {
							System.out.println("==> Context: " + diffContext);
							differences.add(new LinkedList<>(diffContext));
							diffContext.removeLast();
							return;
						} else {
							System.out.println("==> Context discarded: No visible event has been hidden");
							traverse(differences, desc, null, false);
						}
					} else {//if ((op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT)) {
						Integer hiddenEvent = (Integer)op.target;
						boolean visibleEventHasBeenHidden1 = visibleEventHasBeenHidden;
						if (!visibleEventHasBeenHidden)
							visibleEventHasBeenHidden1 = !pes2.getInvisibleEvents().contains(hiddenEvent);
						traverse(differences, desc, diffContext, visibleEventHasBeenHidden1);
					}
				}
			}
		}
	}

	private Set<String> translate(Multiset<Integer> multiset) {
		Set<String> set = new HashSet<>();
		for (Integer ev: multiset)
			set.add(pes2.getLabel(ev));
		return set;
	}
	
	private Set<String> translate(Set<Integer> multiset) {
		Set<String> set = new HashSet<>();
		for (Integer ev: multiset)
			set.add(pes2.getLabel(ev));
		return set;
	}

	private Set<String> translate(BitSet bitset) {
		Set<String> set = new LinkedHashSet<>();
		for (int ev = bitset.nextSetBit(0); ev >= 0; ev = bitset.nextSetBit(ev+1)) {
			set.add(pes1.getLabel(ev));
		}
		return set;
	}

	private void dfs(State curr, Set<Multiset<Integer>> footprints, Multiset<Integer> parentFootprint) {
		Multiset<Integer> footprint = HashMultiset.create(curr.c2);
		footprint.retainAll(pes2.getCutoffEvents());
		if (footprints.contains(footprint)) {
			if (parentFootprint.equals(footprint)) {
				System.out.println("This was the second iteration");
				cases.put(footprint, curr);
				return;
			} else if (!parentFootprint.isEmpty()) {
				return;
			}
		}
		for (Operation op: descendants.get(curr)) {
			if (relevantStates.contains(op.nextState))
				dfs(op.nextState, footprints, footprint);
		}
	}

}
