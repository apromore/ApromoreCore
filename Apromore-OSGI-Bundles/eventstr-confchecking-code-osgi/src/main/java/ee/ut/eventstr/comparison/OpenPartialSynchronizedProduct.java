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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.eventstr.comparison.OpenPartialSynchronizedProduct.Operation.Op;
import ee.ut.org.processmining.framework.util.Pair;

public class OpenPartialSynchronizedProduct<T> {
	private PESSemantics<T> pes1;
	private UnfoldingPESSemantics<T> pes2;
	private Multimap<BitSet, State> lMATCHES;
	private Multimap<Multiset<Integer>, State> rMATCHES;
	private float gDenominator;
	private List<State> states;
	private Set<State> relevantStates;
	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;
	private State root;
		
	private Table<BitSet, Multiset<Integer>, State> stateSpaceTable = HashBasedTable.create();
	private Multimap<State, State> mergedStates = HashMultimap.create();
	
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
		enum Op {MATCH, LHIDE, RHIDE, RMATCHNSHIFT, RSHIFT};
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
		static Operation rshift(State state, Integer target, String label) {
			return new Operation(state, Op.RSHIFT, target, label);
		}
		static Operation rmatchnshift(State state, Pair<Integer, Integer> target, String label) {
			return new Operation(state, Op.RMATCHNSHIFT, target, label);
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
	
	public OpenPartialSynchronizedProduct(PESSemantics<T> pes1, UnfoldingPESSemantics<T> pes2) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.states = new ArrayList<>();
		this.gDenominator = pes1.getLabels().size() + pes2.getLabels().size();
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();
		this.lMATCHES = HashMultimap.create();
		this.rMATCHES = HashMultimap.create();

	}

	public OpenPartialSynchronizedProduct<T> perform() {
		Map<BitSet, Float> lGW = new HashMap<>();
		Map<BitSet, Float> rGW = new HashMap<>();

		for (BitSet mconf: pes1.getMaxConf())
			lGW.put(mconf, Float.POSITIVE_INFINITY);
		for (BitSet mconf: pes2.getMaxConf())
			rGW.put(mconf, Float.POSITIVE_INFINITY);

		Queue<State> open = new PriorityQueue<State>(11, new WeightBasedStateComparator());

		root = getState(new BitSet(), HashMultiset.<String> create(), HashMultiset.<Integer> create()).getSecond();
		open.offer(root);

		while (!open.isEmpty()) {
			State s = open.poll();

			if (isCandidate(s.c1, s, pes1, lGW)) { // || isCandidate(s.c2, s, pes2, rGW)) {
				if (pes1.getMaxConf().contains(s.c1)) {// && pes2.getMaxConf().contains(getFlattenedC2(s.c2, pes2))) {
//					updateMatches(s.c1, s, lMATCHES, lGW);
//					updateMatches(s.c2, s, rMATCHES, rGW);
					updateMatches(s, lGW, rGW);
					continue;
				}
				
				if (s.hint == StateHint.MERGED) continue;
				
				System.out.println("Right Configuration: " + s.c2);
				
				BitSet lpe = pes1.getPossibleExtensions(s.c1);
				
				
				if (lpe.isEmpty())
					continue;
				
				Set<Integer> rpe = new HashSet<>(pes2.getPossibleExtensions(s.c2));
				
				if (lpe.isEmpty() && rpe.isEmpty()) 
					continue;
				
				// Is there any cutoff in the set of possible extensions?
				Set<Integer> cutoffEvents = new HashSet<>(pes2.getCutoffEvents());
				cutoffEvents.retainAll(rpe);
				rpe.removeAll(cutoffEvents);
				for (Integer cutoff: cutoffEvents) {
					Multiset<Integer> c2p = HashMultiset.create(s.c2);
					c2p.add(cutoff);
					boolean matched = false;
					
					State shiftingState = null;
					BitSet leftConfiguration = null;
					Multiset<String> shiftingLabels = null;
					
					for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
						String label1 = pes1.getLabel(e1);
						if (label1.equals(pes2.getLabel(cutoff)) 
								&& isOrderPreserving(s, e1, cutoff, Collections.EMPTY_MAP, Collections.EMPTY_SET)) {
							BitSet c1p = (BitSet)s.c1.clone();
							c1p.set(e1);
							
							Multiset<String> labels = HashMultiset.create(s.labels);
							labels.add(label1);

							Pair<StateHint, State> pair = getState(c1p, labels, c2p);
							switch (pair.getFirst()) {
							case CREATED:
							case MERGED:
		//						open.offer(pair.getSecond()); // Only if not previously visited
								descendants.put(s, Operation.rmatchnshift(pair.getSecond(), new Pair<>(e1, cutoff), pes2.getLabel(cutoff)));
								ancestors.put(pair.getSecond(), s);
							default:
							}
							
							shiftingState = pair.getSecond();
							leftConfiguration = c1p;
							shiftingLabels = labels;
							matched = true;
							break;
						}
					}

					if (!matched) {
						Pair<StateHint, State> pair = getState(s.c1, s.labels, c2p);
						switch (pair.getFirst()) {
						case CREATED:
						case MERGED:
	//						open.offer(pair.getSecond()); // Only if not previously visited
							descendants.put(s, Operation.rshift(pair.getSecond(), cutoff, pes2.getLabel(cutoff)));
							ancestors.put(pair.getSecond(), s);
						default:
						}
						shiftingState = pair.getSecond();
						leftConfiguration = s.c1;
						shiftingLabels = s.labels;
					}
					
//					IOUtils.toFile("psp.dot", toDot());

					Set<Integer> cutoffPE = pes2.getCutoffPossibleExtensions(cutoff);
					Set<Integer> shiftedPE = pes2.getPossibleExtensions(c2p);
					Map<Integer, Integer> invShift = new HashMap<Integer, Integer>();
					for (Integer e: cutoffPE)
						for (Integer ep: shiftedPE)
							if (pes2.getLabel(e).equals(pes2.getLabel(ep)))
								invShift.put(ep, e);
					invShift.put(pes2.getCorrespondingEvent(cutoff), cutoff);
					System.out.println("Cutoff PE: " + cutoffPE);
					System.out.println("Shifted PE: " + shiftedPE);
					for (int e2: shiftedPE) {
						/// HIDE (right)
						Multiset<Integer> c2pp = HashMultiset.create(c2p);
						c2pp.add(e2);

						Pair<StateHint, State> pairp = getState(leftConfiguration, shiftingLabels, c2pp);
						switch (pairp.getFirst()) {
						case CREATED:
						case MERGED:
							open.offer(pairp.getSecond()); // Only if not previously visited
							descendants.put(shiftingState, Operation.rhide(pairp.getSecond(), e2, pes2.getLabel(e2)));
							ancestors.put(pairp.getSecond(), shiftingState);
						default:
						}
						for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
							String label1 = pes1.getLabel(e1);
							if (label1.equals(pes2.getLabel(e2)) 
									&& isOrderPreserving(shiftingState, e1, e2, invShift, shiftedPE)) {
								BitSet c1p = (BitSet)leftConfiguration.clone();
								c1p.set(e1);
								
								System.out.println("Right event: " + label1);

								
								/// MATCH !!!

								Multiset<String> labels = HashMultiset.create(shiftingLabels);
								labels.add(label1);
								
								Pair<StateHint, State> pairpp = getState(c1p, labels, c2pp);
								
								switch (pairpp.getFirst()) {
								case CREATED:
								case MERGED:
									open.offer(pairpp.getSecond()); // Only if not previously visited
									descendants.put(shiftingState, Operation.match(pairpp.getSecond(), new Pair<>(e1, e2), label1));
									ancestors.put(pairpp.getSecond(), shiftingState);
								default:
								}
							}

						}
					}

//					IOUtils.toFile("psp.dot", toDot());
					
//					rpe.addAll(pe);
				}
				
				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
					String label1 = pes1.getLabel(e1);
					BitSet c1p = (BitSet)s.c1.clone();
					c1p.set(e1);
					
					
					System.out.println("Left event: " + label1);
					
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
					
					for (int e2: rpe) {
						if (label1.equals(pes2.getLabel(e2)) 
								&& isOrderPreserving(s, e1, e2, Collections.EMPTY_MAP, Collections.EMPTY_SET)) {
							
							System.out.println("Right event: " + label1);

							
							/// MATCH !!!
							Multiset<Integer> c2p = HashMultiset.create(s.c2);
							c2p.add(e2);

							Multiset<String> labels = HashMultiset.create(s.labels);
							labels.add(label1);
							
							pair = getState(c1p, labels, c2p);
							
							switch (pair.getFirst()) {
							case CREATED:
							case MERGED:
								open.offer(pair.getSecond()); // Only if not previously visited
								descendants.put(s, Operation.match(pair.getSecond(), new Pair<>(e1, e2), label1));
								ancestors.put(pair.getSecond(), s);
							default:
							}
						}
					}
				}
				
				for (int e2: rpe) {
					/// HIDE (right)
					Multiset<Integer> c2p = HashMultiset.create(s.c2);
					c2p.add(e2);

					Pair<StateHint, State> pair = getState(s.c1, s.labels, c2p);
					switch (pair.getFirst()) {
					case CREATED:
					case MERGED:
						open.offer(pair.getSecond()); // Only if not previously visited
						descendants.put(s, Operation.rhide(pair.getSecond(), e2, pes2.getLabel(e2)));
						ancestors.put(pair.getSecond(), s);
					default:
					}
				}
			}
			
//			IOUtils.toFile("psp.dot", toDot());
			// else ... the State is discarded
		}
		
		return this;
	}
	
	private boolean isOrderPreserving(State s, int e1, int e2, Map<Integer, Integer> invShift, Set<Integer> shiftedPE) {
		if (invShift.containsKey(e2))
			e2 = invShift.get(e2);
		BitSet e1dpred = (BitSet)pes1.getDirectPredecessors(e1).clone();
		Set<Integer> e2dpred = new HashSet<>(pes2.getDirectPredecessors(e2));
		
		Integer shiftedE2 = null;
		
		Stack<State> open = new Stack<>();
		Set<State> visited = new HashSet<>();
		open.push(s);
		Set<Integer> toRemove = new HashSet<>();
		
		while (!open.isEmpty()) {
			if (e1dpred.isEmpty())
				if (e2dpred.isEmpty() || toRemove.containsAll(e2dpred))
					break;
			State curr = open.pop();
			visited.add(curr);
			
			for (State ancestor: ancestors.get(curr)) {
				if (visited.contains(ancestor) || open.contains(ancestor)) continue;
				for (Operation op: descendants.get(ancestor))
					if (op.nextState.equals(curr)) {
						System.out.println(op);
						if (op.op == Op.MATCH) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> matchedEvents = (Pair<Integer,Integer>)op.target;
							
							if (shiftedE2 == null) {
								Integer matchedE2 = matchedEvents.getSecond();
								if (invShift.containsKey(matchedE2))
									matchedE2 = invShift.get(matchedE2);
								if (!pes1.getBRelation(e1, matchedEvents.getFirst()).equals(pes2.getBRelation(e2, matchedE2)))
									return false;
								
								e1dpred.clear(matchedEvents.getFirst());
								e2dpred.remove(matchedEvents.getSecond());
							} else {
								Integer corresponding = pes2.getCorrespondingEvent(shiftedE2);
								
								BitSet localConf = (BitSet)pes2.getLocalConfiguration(corresponding).clone();
								for (Integer pred: pes2.getDirectPredecessors(e2))
									if (localConf.get(pred))
										toRemove.add(pred);

								if (shiftedPE.contains(e2) && pes1.getBRelation(e1, matchedEvents.getFirst()) != BehaviorRelation.INV_CAUSALITY)
									return false;
								e1dpred.clear(matchedEvents.getFirst());
							}
						} else if (op.op == Op.RMATCHNSHIFT) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> matchedEvents = (Pair<Integer,Integer>)op.target;
							shiftedE2 = pes2.getCorrespondingEvent(matchedEvents.getSecond()); 
							System.out.println("LEFT: " + pes1.getBRelation(e1, matchedEvents.getFirst()));
							System.out.println("RIFTH: " + pes2.getBRelation(e2, matchedEvents.getSecond()));
							System.out.println("RIGHT (shifted): " + pes2.getBRelation(e2, shiftedE2));
							if (!(pes1.getBRelation(e1, matchedEvents.getFirst()).equals(pes2.getBRelation(e2, matchedEvents.getSecond()))
									||
									pes1.getBRelation(e1, matchedEvents.getFirst()).equals(pes2.getBRelation(e2, shiftedE2))
									)) // shiftedE2
								return false;
							e1dpred.clear(matchedEvents.getFirst());
							e2dpred.remove(matchedEvents.getSecond()); // shiftedE2 ?
							e2dpred.remove(shiftedE2); // shiftedE2 ?
							shiftedE2 = null;
						} else if (op.op == Op.RSHIFT) {
							System.out.println(op.target);
							shiftedE2 = (Integer)op.target;
						} else
							e1dpred.clear((Integer)op.target);;
					}
				open.push(ancestor);
			}
		}
		return true;
	}

	public OpenPartialSynchronizedProduct<T> prune() {
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
				
				if (mergedStates.get(curr).size() > 1) {
					for (State merged: mergedStates.get(curr))
						if (!curr.equals(merged)) open.push(merged);
				}

				for (State pred: ancestors.get(curr))
					if (!visited.contains(pred) && !open.contains(pred))
						open.push(pred);
			}
			

			gvisited.addAll(visited);
		}
		
		this.relevantStates = gvisited;
		
		System.out.println("Number of final states: " + rMATCHES.values().size());
		System.out.println("Number of relevant states: " + relevantStates.size());
		
		return this;
	}
			
	private Pair<StateHint,State> getState(BitSet c1, Multiset<String> labels, Multiset<Integer> c2) {
		StateHint action = StateHint.CREATED;
		State newState = eta(new State(c1, labels, c2));
		states.add(newState);
		newState.hint = StateHint.CREATED;
		
		if (stateSpaceTable.contains(c1, c2)) {
			State pivot = stateSpaceTable.get(c1, c2);
			
			if (newState.weight == pivot.weight) {
				boolean found = false;
				for (State sibling: mergedStates.get(pivot))
					if (sibling.labels.equals(labels)) {
						found = true;
						break;
					}
				if (!found) {
					newState.hint = action = StateHint.MERGED;
//					mergedStates.put(pivot, newState);
				} else
					newState.hint = action = StateHint.DISCARDED;				
			} else {
				if (!pivot.labels.equals(labels))
//					mergedStates.put(pivot, newState)
					;
				else
					newState.hint = action = StateHint.DISCARDED;
			}
		} else {
			stateSpaceTable.put(c1, c2, newState);
			mergedStates.put(newState, newState);
		}
		return new Pair<>(action, newState);
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

	public State eta(State s) {
		Set<String> pf1 = pes1.getPossibleFutureAsLabels(s.c1);
		Set<String> pf2 = pes2.getPossibleFutureAsLabels(s.c2);
		Multiset<Integer> c2copy = HashMultiset.create(s.c2);
		c2copy.removeAll(pes2.getInvisibleEvents());
		s.weight = g(s.c1, c2copy, s.labels)
//				+ h(pf1, pf2)
				;
		return s;
	}
		
	public float g(BitSet c1, Multiset<Integer> c2, Multiset<String> labels) {
		return (c1.cardinality() + c2.size() - labels.size() * 2.0f) / gDenominator + gDenominator/ (1.0f + labels.size());
	}

	public float h(Set<String> pf1, Set<String> pf2) {
		Set<String> union = pf1;
		Set<String> intersection = new HashSet<>(pf1);
		intersection.retainAll(pf2);
		union.addAll(pf2);
		return (union.size() - intersection.size() + 0.0f) / gDenominator;
	}
		
	public Multimap<Multiset<Integer>, State> getRightMatches() {
		return rMATCHES;
	}
	public Multimap<BitSet, State> getLeftMatches() {
		return lMATCHES;
	}
	
	private void updateMatches(State s, Map<BitSet, Float> lGW, Map<BitSet, Float> rGW) {
		BitSet c2bitset = getFlattenedC2(s.c2, pes2);

		if (s.weight < lGW.get(s.c1)) {// || s.weight < rGW.get(c2bitset)) {
			if (s.weight < lGW.get(s.c1)) {
				lMATCHES.removeAll(s.c1);
				lGW.put(s.c1, s.weight);
			}
//			if (s.weight < rGW.get(c2bitset)) {
//				rMATCHES.removeAll(s.c2);
//				rGW.put(c2bitset, s.weight);
//			}
			lMATCHES.put(s.c1, s);
//			rMATCHES.put(s.c2, s);
		} else {
			if (s.weight == lGW.get(s.c1))
				lMATCHES.put(s.c1, s);
//			if (s.weight == rGW.get(c2bitset))
//				rMATCHES.put(s.c2, s);
		}
	}

//	private void updateMatches(BitSet c, State s, Multimap<BitSet, State> matches, Map<BitSet, Float> gW) {
//		if (s.weight < gW.get(c)) {
//			matches.removeAll(c);
//			matches.put(c, s);
//			gW.put(c, s.weight);
//		} else if (s.weight == gW.get(c)) {
//			matches.put(c, s);			
//		}
//	}

	private boolean isCandidate(BitSet c1, State s, PESSemantics<T> pes, Map<BitSet, Float> gW) {
		for (BitSet mconf: gW.keySet())
			if (contains(mconf, c1) && s.weight <= gW.get(mconf))
				return true;
		return false;
	}

	
	Multimap<Multiset<Integer>, BitSet> flattenedC2 = HashMultimap.create();
	
	private BitSet getFlattenedC2(Multiset<Integer> c2, UnfoldingPESSemantics<T> pes) {
		BitSet c2bitset = new BitSet();
		for (Integer e: c2.elementSet())
			c2bitset.set(e);
		c2bitset.andNot(pes.getResidualEvents());
		return c2bitset;
	}

	private boolean isCandidate(Multiset<Integer> c2, State s, UnfoldingPESSemantics<T> pes, Map<BitSet, Float> gW) {
		BitSet c2bitset = getFlattenedC2(c2, pes);
		for (BitSet mconf: gW.keySet())
			if (contains(mconf, c2bitset) && s.weight <= gW.get(mconf))
				return true;
		return false;
	}

	private boolean contains(BitSet superset, BitSet subset) {
		int cardinality = superset.cardinality();
		superset = (BitSet)superset.clone();
		superset.or(subset);
		return cardinality == superset.cardinality();
	}

	
	public BitSet getFlattened(Multiset<Integer> c2) {
		BitSet c2bitset = new BitSet();
		for (Integer e: c2.elementSet())
			c2bitset.set(e);
		return c2bitset;
	}

	static class Info {
		Multiset<String> skippedLeft = HashMultiset.create();
		Multiset<String> skippedRight = HashMultiset.create();
		Multiset<String> matchedRight = HashMultiset.create();
		public Info() {};
	}
	public void analyze() {
		pes2.analyzeIntervals();
		
//		Multimap<BitSet, Multiset<Integer>> matchedConfs = HashMultimap.create();
//		for (Entry<Multiset<Integer>, State> entry: getRightMatches().entries()) {
//			BitSet conf = getFlattenedC2(entry.getKey(), pes2);
//			matchedConfs.put(conf, entry.getKey());
//		}		
//		
//		for (Integer beginning: pes2.getBeginning2ConfsMap().keySet()) {
//			List<Info> list = new LinkedList<>();
//			for (BitSet conf: pes2.getBeginning2ConfsMap().get(beginning)) {
//				
//			}
//		}
		
		for (Entry<Multiset<Integer>, State> entry: getRightMatches().entries()) {
			BitSet conf = getFlattenedC2(entry.getKey(), pes2);
			if (!pes2.isAcyclicConf(conf)) {
				System.out.println("=================================================");
				System.out.println("Found ... repetitive behavior " +entry.getValue());
				Integer beginning = pes2.getBeginningFor(conf);
				
				// Traverse backwards
				LinkedList<State> visited = new LinkedList<>();
				Queue<State> open = new LinkedList<>();
				open.offer(entry.getValue());
				while (!open.isEmpty()) {
					State curr = open.poll();
					visited.addFirst(curr);
					
					if (ancestors.get(curr).isEmpty()) continue;
					State parent = ancestors.get(curr).iterator().next();
					
					for (Operation op: descendants.get(parent))
						if (op.nextState.equals(curr)) {
							if (op.target instanceof Pair) {
								Pair<Integer,Integer> pair = (Pair<Integer,Integer>)op.target;
								
								if (!pair.getSecond().equals(beginning))
									open.offer(parent);
								else
									visited.addFirst(parent);
							} else {
								if (!op.target.equals(beginning))
									open.offer(parent);
								else
									visited.addFirst(parent);
							}
							break;
						}
				}
				

				Multiset<String> skippedLeft = HashMultiset.create();
				Multiset<String> skippedRight = HashMultiset.create();
				Multiset<String> matchedRight = HashMultiset.create();
				
				done:
				for (BitSet interval: pes2.getIntervalsFor(beginning)) {
					State parent = visited.get(0);
					for (int next = 1; next < visited.size(); next++) {
						State curr = visited.get(next);
						for (Operation op: descendants.get(parent))
							if (op.nextState.equals(curr)) {
								if (op.op == Op.LHIDE) {
									skippedLeft.add(op.label);
									break;
								} else {
									Integer target = null;
									switch (op.op) {
									case MATCH:
									case RMATCHNSHIFT:
										target = ((Pair<Integer,Integer>)op.target).getSecond();
										break;
									default:
										target = (Integer)op.target;
									}
									if (!(interval.get(target) || target.equals(beginning)))
										break done;
									switch (op.op) {
									case MATCH:
									case RMATCHNSHIFT:
										matchedRight.add(op.label);
										break;
									default:
										if (!pes2.getInvisibleEvents().contains(target))
											skippedRight.add(op.label);
									}
								}
							}
						parent = curr;
					}
				}
				
				System.out.println("Skipped (Left): " + skippedLeft);
				System.out.println("Skipped (Right): " + skippedRight);				
				System.out.println("Matched (Right): " + matchedRight);				
			}
		}		
	}
}
