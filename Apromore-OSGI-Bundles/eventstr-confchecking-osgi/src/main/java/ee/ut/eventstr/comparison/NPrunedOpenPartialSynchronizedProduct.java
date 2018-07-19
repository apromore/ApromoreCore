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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.org.processmining.framework.util.Pair;

public class NPrunedOpenPartialSynchronizedProduct<T> {
	
	public static class State implements Comparable<State> {
		BitSet c1;
		Multiset<Integer> c2;
		Multiset<String> labels;
		StateHint action;
		public short cost = 0;
		
		State(BitSet c1, Multiset<String> labels, Multiset<Integer> c2) {
			this.c1 = c1; this.c2 = c2; this.labels = labels;
		}
		
		public String toString() {
			return String.format("<%s,%s,%s,%d>", c1, labels, c2, cost);
		}

		public int compareTo(State o) {
			return Short.compare(this.cost, o.cost);
		}
	}

	enum StateHint {CREATED, MERGED, DISCARDED};
	enum Op {MATCH, LHIDE, RHIDE, MATCHNSHIFT, RHIDENSHIFT};
	static class Operation {
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
	
	private SinglePORunPESSemantics<T> pes1;
	private NewUnfoldingPESSemantics<T> pes2;
	private int numberOfTargets;
	private BitSet maxConf1;
	public State matchings;

	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;
	private State root;
	private Table<BitSet, Multiset<Integer>, Map<Multiset<String>, State>> stateSpaceTable;

	private List<State> states = new ArrayList<>();
	private Set<State> relevantStates;

	public NPrunedOpenPartialSynchronizedProduct(SinglePORunPESSemantics<T> pes1, NewUnfoldingPESSemantics<T> pes2) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();
		this.stateSpaceTable = HashBasedTable.create();
		
		this.numberOfTargets = pes1.getMaxConf().size();
		this.maxConf1 = pes1.getMaxConf().iterator().next();
		this.matchings = null;
	}
	
	public NPrunedOpenPartialSynchronizedProduct<T> perform() {
		Queue<State> open = new PriorityQueue<>();
		
		root = getState(new BitSet(), HashMultiset. <String> create(), HashMultiset.<Integer> create());
		states.add(root);
		
		open.offer(root);

		while (!open.isEmpty()) {
			State s = open.poll();
			
			if (isCandidate(s)) {
				BitSet lpe = pes1.getPossibleExtensions(s.c1);				
				Set<Integer> rpe = pes2.getPossibleExtensions(s.c2);
				
				if (lpe.isEmpty() && rpe.isEmpty()) {
					matchings = s;
					continue;
				}
				
				List<Operation> candidates = new ArrayList<>();
				BitSet pruned1 = new BitSet();
				BitSet pruned2 = new BitSet();
				
				Set<Integer> invisible2 = new HashSet<>();
					
				for (Integer e2: rpe) {
					
					if (pes2.getInvisibleEvents().contains(e2)) {
						invisible2.add(e2);
						continue;
					}
					String label2 = pes2.getLabel(e2);

					for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
						if (label2.equals(pes1.getLabel(e1)) && isOrderPreserving(s, e1, e2)) {
							
							String label1 = pes1.getLabel(e1);
							BitSet c1p = (BitSet)s.c1.clone();
							c1p.set(e1);
						
							pruned1.set(e1); pruned2.set(e2);
							
							Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);
							Multiset<String> labels = HashMultiset.create(s.labels);
							labels.add(label1);
							
							State nstate = getState(c1p, labels, extPair.getFirst());
							nstate.cost = s.cost; // A matching operation does not change the current cost
							
							Operation operation;
							if (extPair.getSecond())
								operation = Operation.matchnshift(nstate, new Pair<>(e1, e2), label1);
							else
								operation = Operation.match(nstate, new Pair<>(e1, e2), label1);

							candidates.add(operation);
						}
					}
				}
				
				Collections.sort(candidates, new Comparator<Operation>() {
					@Override
					public int compare(Operation o1, Operation o2) {
						int costCValue = Short.compare(o1.nextState.cost, o2.nextState.cost);
						if (costCValue != 0)
							return costCValue;
						else
							return o1.label.compareTo(o2.label);
					}
				});

				BitSet kept1 = new BitSet();
				BitSet kept2 = new BitSet();
				
				nextCandidate:
				for (Operation operation: candidates) {
					Pair<Integer, Integer> pair = (Pair)operation.target;
					int e1 = pair.getFirst();
					int e2 = pair.getSecond();
					
					for (int e1p = kept1.nextSetBit(0); e1p >= 0; e1p = kept1.nextSetBit(e1p + 1))
						if (pes1.getBRelation(e1, e1p) == BehaviorRelation.CONCURRENCY)
							continue nextCandidate;
					for (int e2p = kept2.nextSetBit(0); e2p >= 0; e2p = kept2.nextSetBit(e2p + 1))
						if (pes2.getBRelation(e2, e2p) == BehaviorRelation.CONCURRENCY)
							continue nextCandidate;
					
					kept1.set(e1);
					kept2.set(e2);
					states.add(operation.nextState);
					
					switch (operation.nextState.action) {
					case CREATED:
						open.offer(operation.nextState);
						ancestors.put(operation.nextState, s);
					case MERGED:							
						descendants.put(s, operation);
					default:
					}

//					IOUtils.toFile("psp.dot", toDot());
				}
				
				pruned1.andNot(kept1);
				pruned2.andNot(kept2);
				
				if (kept2.isEmpty() && invisible2.size() > 1) {
				
					BitSet invisibleToExplore = new BitSet();
					Map<Operation, Stack<Operation>> map = new HashMap<>();
					List<Operation> operations = new ArrayList<>();
					nextCandidate1:
					for (Integer e2: invisible2) {
						for (int e2p = kept2.nextSetBit(0); e2p >= 0; e2p = kept2.nextSetBit(e2p + 1))
							if (pes2.getBRelation(e2, e2p) == BehaviorRelation.CONCURRENCY) {
								pruned2.set(e2);
								continue nextCandidate1;
							}
						
						invisibleToExplore.set(e2);
						
						Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);
						State nstate = getState(s.c1, s.labels, extPair.getFirst());
						computeCost(nstate);
	
						Operation operation;
						if (extPair.getSecond())
							operation = Operation.rhidenshift(nstate, e2, pes2.getLabel(e2));
						else
							operation = Operation.rhide(nstate, e2, pes2.getLabel(e2));
	
						operations.add(operation);
						lookAheadForVisible(operation, e2, map, new Stack<Operation>());
					}
					
					BitSet kept2p = new BitSet();
					BitSet pruned2p = new BitSet();
					
	
					if (invisibleToExplore.cardinality() > 1) {
//						System.out.println("Needs further analysis");							
						
						candidates = new ArrayList<>(map.keySet());
						
						Collections.sort(candidates, new Comparator<Operation>() {
							@Override
							public int compare(Operation o1, Operation o2) {
								int costCValue = Short.compare(o1.nextState.cost, o2.nextState.cost);
								if (costCValue != 0)
									return costCValue;
								else
									return o1.label.compareTo(o2.label);
							}
						});
						
						nextOperation10:
						for (Operation _operation: candidates) {
							Operation operation = map.get(_operation).get(0);
							int e2 = (Integer)operation.target;
							
							for (int e2p = kept2p.nextSetBit(0); e2p >= 0; e2p = kept2p.nextSetBit(e2p + 1))
								if (pes2.getBRelation(e2, e2p).equals(BehaviorRelation.CONCURRENCY)) {
									pruned2p.set(e2);
									continue nextOperation10;
								}
							
							kept2p.set(e2);
	
							states.add(operation.nextState);
	
							switch (operation.nextState.action) {
							case CREATED:
								open.offer(operation.nextState);
								ancestors.put(operation.nextState, s);
							case MERGED:							
								descendants.put(s, operation);
							default:
							}
	
//							IOUtils.toFile("psp.dot", toDot());
						}
						
						
						for (Operation operation: operations)
							if (!map.containsKey(operation))
								pruned2.set((Integer)operation.target);
						
						kept2.or(kept2p);
					}

				}
				nextCandidate2:
				for (Integer e2: rpe) {
					if (pruned2.get(e2) || kept2.get(e2))
						continue;
					
					for (int e2p = kept2.nextSetBit(0); e2p >= 0; e2p = kept2.nextSetBit(e2p + 1))
						if (pes2.getBRelation(e2, e2p) == BehaviorRelation.CONCURRENCY)
							continue nextCandidate2;

					
					Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);
					State nstate = getState(s.c1, s.labels, extPair.getFirst());
					computeCost(nstate);
					states.add(nstate);
					
					switch (nstate.action) {
					case CREATED:
						open.offer(nstate);
						ancestors.put(nstate, s);
					case MERGED:
						if (extPair.getSecond())
							descendants.put(s, Operation.rhidenshift(nstate, e2, pes2.getLabel(e2)));
						else
							descendants.put(s, Operation.rhide(nstate, e2, pes2.getLabel(e2)));
					default:
					}
					
//					IOUtils.toFile("psp.dot", toDot());
				}
				
				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {					
					if (pruned1.get(e1) || kept1.get(e1))
						continue;

					BitSet c1p = (BitSet)s.c1.clone();
					c1p.set(e1);
					
					State nstate = getState(c1p, s.labels, s.c2);
					computeCost(nstate);
					states.add(nstate);
					
					switch (nstate.action) {
					case CREATED:
						open.offer(nstate);
						ancestors.put(nstate, s);
					case MERGED:
						descendants.put(s, Operation.lhide(nstate, e1, pes1.getLabel(e1)));
					default:
					}
					
//					IOUtils.toFile("psp.dot", toDot());
				}
			}
		}
		return this;
	}
	
	private void lookAheadForVisible(Operation operation, Integer pred, Map<Operation, Stack<Operation>> map, Stack<Operation> stack) {
		if (operation.op.equals(Op.RHIDE) || operation.op.equals(Op.RHIDENSHIFT)) {
			stack.push(operation);
			State s = operation.nextState;
						
			BitSet lpe = pes1.getPossibleExtensions(s.c1);				
			Set<Integer> rpe = pes2.getPossibleExtensions(s.c2);
			
			if (pes2.getCutoffEvents().contains(pred)) {
				Integer corr = pes2.getCorresponding(pred);
				BitSet localConf = pes2.getLocalConfiguration(pred);
				if (localConf.get(corr)) {
					rpe.retainAll(pes2.cutoff2CyclicEvents.get(pred));
				}
				
				pred = corr;
			}
			
			for (Integer e2: rpe) {
				if (!pes2.getBRelation(pred, e2).equals(BehaviorRelation.CAUSALITY))
					continue;
				String label2 = pes2.getLabel(e2);
								
				if (pes2.getInvisibleEvents().contains(e2)) {
					Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);
					State nstate = getState(s.c1, s.labels, extPair.getFirst());
					computeCost(nstate);

					Operation operationP;
					if (extPair.getSecond())
						operationP = Operation.rhidenshift(nstate, e2, pes2.getLabel(e2));
					else
						operationP = Operation.rhide(nstate, e2, pes2.getLabel(e2));

					lookAheadForVisible(operationP, e2, map, stack);
					continue;
				}
				
				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
					if (label2.equals(pes1.getLabel(e1)) && isOrderPreserving(s, e1, e2)) {
						String label1 = pes1.getLabel(e1);
						BitSet c1p = (BitSet)s.c1.clone();
						c1p.set(e1);
				
						Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);
						Multiset<String> labels = HashMultiset.create(s.labels);
						labels.add(label1);
						
						State nstate = getState(c1p, labels, extPair.getFirst());
						nstate.cost = s.cost; // A matching operation does not change the current cost
						
						Operation operationP;
						if (extPair.getSecond())
							operationP = Operation.matchnshift(nstate, new Pair<>(e1, e2), label1);
						else
							operationP = Operation.match(nstate, new Pair<>(e1, e2), label1);

						lookAheadForVisible(operationP, e2, map, stack);
					}
				}
			}
			stack.pop();
		} else if (operation.op.equals(Op.MATCH) || operation.op.equals(Op.MATCHNSHIFT)) {
			Stack<Operation> stackCopy = (Stack<Operation>)stack.clone();
			map.put(operation, stackCopy);
		}
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

	
	private State getState(BitSet c1, Multiset<String> labels, Multiset<Integer> c2) {
		State newState = new State(c1, labels, c2);
//		states.add(newState);

		newState.action = StateHint.CREATED;
		
//		if (stateSpaceTable.contains(c1, c2)) {
//			Map<Multiset<String>, State> map = stateSpaceTable.get(c1, c2);
//			if (map.containsKey(labels))
//				newState.action = StateHint.MERGED;
//			else
//				map.put(labels, newState);
//		} else {
//			Map<Multiset<String>, State> map = new HashMap<>();
//			map.put(labels, newState);
//			stateSpaceTable.put(c1, c2, map);
//		}
		return newState;
	}


	private boolean isCandidate(State s) {
		if (matchings == null)
				return true;
		return false;
	}
	
	public void computeCost(State s) {
		Multiset<Integer> c2copy = HashMultiset.create(s.c2);
		c2copy.removeAll(pes2.getInvisibleEvents());
		s.cost = (short)(
				g(s.c1, c2copy, s.labels)
				+ h(s)
				);
	}
		
	public int g(BitSet c1, Multiset<Integer> c2, Multiset<String> labels) {
		return (c1.cardinality() + c2.size() - labels.size() * 2);
	}
	
	public int h(State s) {
		Set<String> pf2 = pes2.getPossibleFutureAsLabels(s.c2);
		
		BitSet future = (BitSet)maxConf1.clone();
		future.andNot(s.c1);
		Set<String> diff = translate(future);
		diff.removeAll(pf2);
		
		return diff.size();
	}
	
	private Set<String> translate(BitSet bitset) {
		Set<String> set = new LinkedHashSet<>();
		for (int ev = bitset.nextSetBit(0); ev >= 0; ev = bitset.nextSetBit(ev+1)) {
			set.add(pes1.getLabel(ev));
		}
		return set;
	}
	
	public NPrunedOpenPartialSynchronizedProduct<T> prune() {
		Set<State> gvisited = new HashSet<>();
		Stack<State> open = new Stack<>();
		
		for (int i = 0; i < numberOfTargets; i++) {
			State s = matchings;
			if (s == null) continue;
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
		
		//System.out.println("Number of relevant states: " + relevantStates.size());
		
		return this;
	}


	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {\n\t node [shape=box];");
		Map<State, Integer> rstates = new HashMap<>();
		
		for (int i = 0; i < states.size(); i ++) {
			State s = states.get(i);
			if (relevantStates == null || relevantStates.contains(s)) {
				rstates.put(s, i);
//				if (matchings.containsValue(s))
//					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%3.2f\",color=blue];\n", i, s.c1, s.c2, s.labels, s.weight);
//				else
				if (matchings == s)
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%d\", color=red];\n", i, s.c1, s.c2, s.labels, s.cost);
				else
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%d\"];\n", i, s.c1, s.c2, s.labels, s.cost);					
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
}
