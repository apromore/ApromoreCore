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
import java.util.TreeMap;

import com.google.common.collect.BiMap;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct.Operation.Op;
import ee.ut.org.processmining.framework.util.Pair;

public class PartialSynchronizedProduct<T> {
	private PESSemantics<T> pes1;
	private PESSemantics<T> pes2;
	private Multimap<BitSet, State> lMATCHES;
	private Multimap<BitSet, State> rMATCHES;
	private float gDenominator;
	private List<State> states;
	private Set<State> relevantStates;
	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;
	private State root;
//	private DiffMMVerbalizer<Integer> verbalizer;
	private DiffMMGraphicalVerbalizer verbalizer;
	
	private Table<BitSet, BitSet, HashSet<State>> stateSpaceTable = HashBasedTable.create();
	private Multimap<State, State> mergedStates = HashMultimap.create();

	enum StateHint {
		CREATED, MERGED, DISCARDED
	}

	static class State {
		BitSet c1, c2;
		float weight = 0.0f;
		Multiset<String> labels;
		StateHint hint;
		BiMap<Integer, Integer> mappings;

		State(BitSet c1, Multiset<String> labels, BiMap<Integer, Integer> mappings, BitSet c2) {
			this.c1 = c1;
			this.c2 = c2;
			this.labels = labels;
			this.mappings = HashBiMap. <Integer, Integer> create(mappings);
		}

		public String toString() {
			return String.format("<%s,%s,%s,%3.2f>", c1, labels, c2, weight);
		}
	}

	static class Operation {
		enum Op {
			MATCH, LHIDE, RHIDE
		};

		Op op;
		String label;
		State nextState;
		Object target;

		private Operation(State state, Op op, Object target, String label) {
			this.nextState = state;
			this.target = target;
			this.op = op;
			this.label = label;
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

		public String toString() {
			return String.format("%s(%s[%s])", op, label, target);
		}
	}

	static class WeightBasedStateComparator implements Comparator<State> {
		public int compare(State o1, State o2) {
			return Float.compare(o1.weight, o2.weight);
		}
	}
	
	public PartialSynchronizedProduct(PESSemantics<T> pes1, PESSemantics<T> pes2) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.states = new ArrayList<>();
		this.gDenominator = pes1.getLabels().size() + pes2.getLabels().size();
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();
		this.lMATCHES = HashMultimap.create();
		this.rMATCHES = HashMultimap.create();
	}

	public PartialSynchronizedProduct<T> perform() {
		Map<BitSet, Float> lGW = new HashMap<>();
		Map<BitSet, Float> rGW = new HashMap<>();

		for (BitSet mconf : pes1.getMaxConf())
			lGW.put(mconf, Float.POSITIVE_INFINITY);
		for (BitSet mconf : pes2.getMaxConf())
			rGW.put(mconf, Float.POSITIVE_INFINITY);

		Queue<State> open = new PriorityQueue<State>(11, new WeightBasedStateComparator());

		root = getState(new BitSet(), HashMultiset.<String> create(), HashBiMap. <Integer, Integer> create(),new BitSet()).getSecond();
		open.offer(root);

		while (!open.isEmpty()) {
			State s = open.poll();
			if (isCandidate(s.c1, s, pes1, lGW) || isCandidate(s.c2, s, pes2, rGW)) {
				if (pes1.getMaxConf().contains(s.c1) && pes2.getMaxConf().contains(s.c2)) {
					// updateMatches(s.c1, s, lMATCHES, lGW);
					// updateMatches(s.c2, s, rMATCHES, rGW);
					updateMatches(s, lGW, rGW);
				}

				if (s.hint == StateHint.MERGED) continue;

				BitSet lpe = pes1.getPossibleExtensions(s.c1);
				BitSet rpe = pes2.getPossibleExtensions(s.c2);

				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1 + 1)) {
					String label1 = pes1.getLabel(e1);
					BitSet c1p = (BitSet) s.c1.clone();
					c1p.set(e1);

					/// HIDE (left)
					Pair<StateHint, State> pair = getState(c1p, s.labels, s.mappings, s.c2);
					switch (pair.getFirst()) {
					case CREATED:
					case MERGED:
						open.offer(pair.getSecond()); // Only if not previously
														// visited
						descendants.put(s, Operation.lhide(pair.getSecond(), e1, label1));
						ancestors.put(pair.getSecond(), s);
						break;
					default:
						descendants.put(s, Operation.lhide(pair.getSecond(), e1, label1));
						ancestors.put(pair.getSecond(), s);
						break;
					}

					for (int e2 = rpe.nextSetBit(0); e2 >= 0; e2 = rpe.nextSetBit(e2 + 1)) {
						if (label1.equals(pes2.getLabel(e2)) && isOrderPreserving(s, e1, e2)) {
							/// MATCH !!!
							BitSet c2p = (BitSet) s.c2.clone();
							c2p.set(e2);

							Multiset<String> labels = HashMultiset.create(s.labels);
							labels.add(label1);
							
							BiMap<Integer, Integer> mappings = HashBiMap. <Integer, Integer> create(s.mappings);
							mappings.put(e1, e2);

							pair = getState(c1p, labels, mappings, c2p);

							switch (pair.getFirst()) {
							case CREATED:
							case MERGED:
								open.offer(pair.getSecond()); // Only if not
																// previously
																// visited
								descendants.put(s, Operation.match(pair.getSecond(), new Pair<>(e1, e2), label1));
								ancestors.put(pair.getSecond(), s);
								break;
							default:
								descendants.put(s, Operation.match(pair.getSecond(), new Pair<>(e1, e2), label1));
								ancestors.put(pair.getSecond(), s);
								break;
							}
						}
					}
				}
				
				for (int e2 = rpe.nextSetBit(0); e2 >= 0; e2 = rpe.nextSetBit(e2 + 1)) {
					/// HIDE (right)
					BitSet c2p = (BitSet) s.c2.clone();
					c2p.set(e2);

					Pair<StateHint, State> pair = getState(s.c1, s.labels, s.mappings, c2p);
					switch (pair.getFirst()) {
					case CREATED:
					case MERGED:
						open.offer(pair.getSecond()); // Only if not previously
														// visited
						descendants.put(s, Operation.rhide(pair.getSecond(), e2, pes2.getLabel(e2)));
						ancestors.put(pair.getSecond(), s);
						break;
					default:
						descendants.put(s, Operation.rhide(pair.getSecond(), e2, pes2.getLabel(e2)));
						ancestors.put(pair.getSecond(), s);
						break;
					}
				}
			}
			// IOUtils.toFile("psp1.dot", toDot());
			// System.out.print(".");
			// else ... the State is discarded
		}

		return this;
	}

	private boolean isOrderPreserving(State s, int e1, int e2) {
		Stack<State> open = new Stack<>();
		Set<State> visited = new HashSet<>();
		open.push(s);
		while (!open.isEmpty()) {
			State curr = open.pop();
			visited.add(curr);

			for (State ancestor : ancestors.get(curr)) {
				if (visited.contains(ancestor) || open.contains(ancestor))
					continue;
				for (Operation op : descendants.get(ancestor))
					if (op.op == Op.MATCH && op.nextState.equals(curr)) {
						@SuppressWarnings("unchecked")
						Pair<Integer, Integer> matchedEvents = (Pair<Integer, Integer>) op.target;

						if (!pes1.getBRelation(e1, matchedEvents.getFirst())
								.equals(pes2.getBRelation(e2, matchedEvents.getSecond())))
							return false;
					}
				open.push(ancestor);
			}
		}
		return true;
	}

	public PartialSynchronizedProduct<T> prune() {
		Set<State> gvisited = new HashSet<>();
		Set<State> finalStates = new HashSet<>(lMATCHES.values());
		finalStates.addAll(rMATCHES.values());

		Set<BitSet> finalStates1 = new HashSet<>();
		Set<BitSet> finalStates2 = new HashSet<>();

		Stack<State> open = new Stack<>();
		for (State s : finalStates) {
			if (!contains(finalStates1,s.c1) || !contains(finalStates2,s.c2)) {
				LinkedList<Operation> opSeq = new LinkedList<>();
				open.push(s);
				
				finalStates1.add(s.c1);
				finalStates2.add(s.c2);

				Set<State> visited = new HashSet<>();
				while (!open.isEmpty()) {
					State curr = open.pop();
					visited.add(curr);

					if (mergedStates.get(curr).size() > 1) {
						for (State merged : mergedStates.get(curr))
							if (!curr.equals(merged))
								open.push(merged);
					}

					for (State pred : ancestors.get(curr))
						if (!visited.contains(pred) && !open.contains(pred)){
							open.push(pred);
							
							for (Operation op: descendants.get(pred)) {
								if (op.nextState.equals(curr)) {
									opSeq.addFirst(op);
									break;
								}
							}
						}
				}
				
				if(this.verbalizer != null)
					this.verbalizer.addPSP(opSeq);
				
				gvisited.addAll(visited);
			}
		}

		this.relevantStates = gvisited;

//		System.out.println("Number of final states: " + rMATCHES.values().size());
//		System.out.println("Number of relevant states: " + relevantStates.size());

		return this;
	}

	private boolean contains(Set<BitSet> setBitSets, BitSet c) {
		for(BitSet b : setBitSets)
			if(b.equals(c))
				return true;
		
		return false;
	}

	private Pair<StateHint, State> getState(BitSet c1, Multiset<String> labels,BiMap<Integer, Integer> mappings, BitSet c2) {
		StateHint action = StateHint.CREATED;
		State newState = eta(new State(c1, labels, mappings, c2));
		states.add(newState);
		newState.hint = StateHint.CREATED;

		State pivot = null;
		if(stateSpaceTable.contains(c1, c2))
			pivot = getPivot(stateSpaceTable.get(c1, c2), mappings);
		
//		if (stateSpaceTable.contains(c1, c2)) {
		if(pivot != null){	
			 if (newState.weight == pivot.weight) {
			 boolean found = false;
			 for (State sibling: mergedStates.get(pivot))
			if (pivot.labels.containsAll(labels)) {
				 found = true;
				 break;
				 }
				 if (!found) {
				 newState.hint = action = StateHint.MERGED;
				 mergedStates.put(pivot, newState);
				 } else
				 newState.hint = action = StateHint.DISCARDED;
			}
			 else {
			action = StateHint.DISCARDED;
			states.remove(newState);
			return new Pair<>(action, pivot);
			 }
		} else {
			stateSpaceTable.put(c1, c2, new HashSet<State>());
			stateSpaceTable.get(c1, c2).add(newState);
			mergedStates.put(newState, newState);
		}
		return new Pair<>(action, newState);
	}

	private State getPivot(HashSet<State> states, BiMap<Integer, Integer> mappings) {
		for(State st : states)
			if(st.mappings.equals(mappings))
				return st;
		
		return null;
	}

	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);

		out.println("digraph G {");
		Map<State, Integer> rstates = new HashMap<>();

		for (int i = 0; i < states.size(); i++) {
			State s = states.get(i);
			if (relevantStates == null || relevantStates.contains(s)) {
				rstates.put(s, i);
				if (lMATCHES.containsValue(s))
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%3.2f\",color=blue];\n", i, "", "", "", s.weight);
				// out.printf("\tn%d
				// [label=\"%s,%s\\n%s\\n%3.2f\",color=blue];\n", i, s.c1, s.c2,
				// s.labels, s.weight);
				else
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%3.2f\"];\n", i, "", "", "", s.weight);
				// out.printf("\tn%d [label=\"%s,%s\\n%s\\n%3.2f\"];\n", i,
				// s.c1, s.c2, s.labels, s.weight);
			}
		}

		Collection<State> lstates = relevantStates != null ? relevantStates : states;

		for (State s : lstates) {
			Integer src = rstates.get(s);
			for (Operation op : descendants.get(s)) {
				if (relevantStates == null || relevantStates.contains(op.nextState)) {
					Integer tgt = rstates.get(op.nextState);
					if (op.op == Op.MATCH)
						out.printf("\tn%d -> n%d [label=\"match(%s)\",color=blue];\n", src, tgt, op.label);
					else if (op.op == Op.LHIDE)
						out.printf("\tn%d -> n%d [label=\"lhide(%s)\",color=red];\n", src, tgt, op.label);
					else if(op.op == Op.RHIDE)
						out.printf("\tn%d -> n%d [label=\"rhide(%s)\",color=orange];\n", src, tgt, op.label);
				}
			}
		}
		out.println("}");
		return str.toString();
	}

	public State eta(State s) {
		Set<String> pf1 = pes1.getPossibleFutureAsLabels(s.c1);
		Set<String> pf2 = pes2.getPossibleFutureAsLabels(s.c2);
		s.weight = g(s.c1, s.c2, s.labels) + h(pf1, pf2);
		return s;
	}

	public float g(BitSet c1, BitSet c2, Multiset<String> labels) {
		return (c1.cardinality() + c2.cardinality() - labels.size() * 2.0f) / gDenominator;
	}

	public float h(Set<String> pf1, Set<String> pf2) {
		Set<String> union = pf1;
		Set<String> intersection = new HashSet<>(pf1);
		intersection.retainAll(pf2);
		union.addAll(pf2);
		return (union.size() - intersection.size() + 0.0f) / gDenominator;
	}

	public float hAbel(Set<String> pf1, Set<String> pf2) {
		Set<String> union = pf1;
		Set<String> intersection = new HashSet<>(pf1);
		intersection.retainAll(pf2);
		union.addAll(pf2);
		return 1.0f - ((intersection.size() + 0.0f) / union.size());
	}

	public Multimap<BitSet, State> getRightMatches() {
		return rMATCHES;
	}

	public Multimap<BitSet, State> getLeftMatches() {
		return lMATCHES;
	}

	private void updateMatches(State s, Map<BitSet, Float> lGW, Map<BitSet, Float> rGW) {
		if (s.weight < lGW.get(s.c1) || s.weight < rGW.get(s.c2)) {
			if (s.weight < lGW.get(s.c1)) {
				lMATCHES.removeAll(s.c1);
				lGW.put(s.c1, s.weight);
			}
			if (s.weight < rGW.get(s.c2)) {
				rMATCHES.removeAll(s.c2);
				rGW.put(s.c2, s.weight);
			}
			lMATCHES.put(s.c1, s);
			rMATCHES.put(s.c2, s);
		} else {
			if (s.weight == lGW.get(s.c1))
				lMATCHES.put(s.c1, s);
			if (s.weight == rGW.get(s.c2))
				rMATCHES.put(s.c2, s);
		}
	}

	// private void updateMatches(BitSet c, State s, Multimap<BitSet, State>
	// matches, Map<BitSet, Float> gW) {
	// if (s.weight < gW.get(c)) {
	// matches.removeAll(c);
	// matches.put(c, s);
	// gW.put(c, s.weight);
	// } else if (s.weight == gW.get(c)) {
	// matches.put(c, s);
	// }
	// }

	private boolean isCandidate(BitSet c1, State s, PESSemantics<T> pes, Map<BitSet, Float> gW) {
		for (BitSet mconf : gW.keySet())
			if (contains(mconf, c1) && s.weight <= gW.get(mconf))
				return true;
		return false;
	}

	private boolean contains(BitSet superset, BitSet subset) {
		int cardinality = superset.cardinality();
		superset = (BitSet) superset.clone();
		superset.or(subset);
		return cardinality == superset.cardinality();
	}

	// ======================================================================
	// ======================================================================
	// ======================================================================
	Set<Operation> processedMissmatches = new HashSet<>();

	class MaximalStateAnalyzer {
		Set<State> visited = new HashSet<>();
		Pair<Integer, Integer> targetPair;
		List<Pair<Integer, Integer>> localMatches = new ArrayList<>();
		Operation earliestMissmatch;
		Multimap<String, Operation> labelOperationMap = HashMultimap.create();

		public MaximalStateAnalyzer partitionStateSpace(Collection<State> maxStates) {
			Stack<State> open = new Stack<>();
			for (State mstate : maxStates)
				open.push(mstate);

			while (!open.isEmpty()) {
				State curr = open.pop();
				visited.add(curr);
				if (mergedStates.get(curr).size() > 1) {
					for (State merged : mergedStates.get(curr))
						if (!curr.equals(merged))
							open.push(merged);
				}
				for (State pred : ancestors.get(curr))
					if (!visited.contains(pred) && !open.contains(pred))
						open.push(pred);
			}
			return this;
		}

		public MaximalStateAnalyzer forwardAnalysis() {
			Queue<State> open = new LinkedList<>();
			open.offer(root);
			while (!open.isEmpty()) {
				State curr = open.poll();

				// Look for earliest mismatch
				if (earliestMissmatch == null)
					for (Operation op : descendants.get(curr))
						if (visited.contains(op.nextState) && op.op != Op.MATCH) {
							earliestMissmatch = op;
							break;
						}

				// Continue the traversal and gather list of matchings
				for (Operation op : descendants.get(curr))
					if (visited.contains(op.nextState)) {
						if (op.op == Op.MATCH) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> pair = (Pair<Integer, Integer>) op.target;
							localMatches.add(pair);
						}
						open.offer(op.nextState);
						labelOperationMap.put(op.label, op);
					}
			}
			return this;
		}

		public MaximalStateAnalyzer selectExplainingPair() {
			for (Pair<Integer, Integer> pair : localMatches) {
				if (earliestMissmatch.op == Op.LHIDE && earliestMissmatch.target.equals(pair.getFirst())) {
					targetPair = pair;
					break;
				} else if (earliestMissmatch.op == Op.RHIDE && earliestMissmatch.target.equals(pair.getSecond())) {
					targetPair = pair;
					break;
				}
			}
			return this;
		}

		public String getEarliestDifference() {

			if (earliestMissmatch != null) {
				if (processedMissmatches.contains(earliestMissmatch))
					return null;

				processedMissmatches.add(earliestMissmatch);

				if (labelOperationMap.get(earliestMissmatch.label).size() > 1) {
					String eventLabel = earliestMissmatch.label;

					PriorityQueue<Operation> leftHidings = new PriorityQueue<Operation>(11,
							new Comparator<Operation>() {
								public int compare(Operation o1, Operation o2) {
									return Float.compare(o1.nextState.weight, o2.nextState.weight);
								}
							});
					PriorityQueue<Operation> rightHidings = new PriorityQueue<Operation>(11,
							new Comparator<Operation>() {
								public int compare(Operation o1, Operation o2) {
									return Float.compare(o1.nextState.weight, o2.nextState.weight);
								}
							});
					for (Operation o : labelOperationMap.get(eventLabel)) {
						if (earliestMissmatch != o) {
							if (earliestMissmatch.op == Op.LHIDE && o.op == Op.RHIDE) {
								BitSet dpred1 = pes1.getDirectPredecessors((Integer) earliestMissmatch.target);
								dpred1.and(earliestMissmatch.nextState.c1);
								String str1 = getEventLabels(pes1, dpred1);

								BitSet dpred2 = pes2.getDirectPredecessors((Integer) o.target);
								dpred2.and(o.nextState.c2);
								String str2 = getEventLabels(pes2, dpred2);

								return String.format(
										"In model 1 the task \"%s(%s)\" occurs just after tasks \"%s\", whereas in model 2 the same task occurs only after \"%s\"",
										earliestMissmatch.label, earliestMissmatch.target, str1, str2);
							} else if (earliestMissmatch.op == Op.RHIDE && o.op == Op.LHIDE) {
								BitSet dpred1 = pes2.getDirectPredecessors((Integer) earliestMissmatch.target);
								dpred1.and(earliestMissmatch.nextState.c2);
								String str1 = getEventLabels(pes2, dpred1);

								BitSet dpred2 = pes1.getDirectPredecessors((Integer) o.target);
								dpred2.and(o.nextState.c1);
								String str2 = getEventLabels(pes1, dpred2);

								return String.format(
										"In model 2 the task \"%s(%s)\" occurs just after tasks \"%s\", whereas in model 1 the same task occurs only after \"%s\"",
										earliestMissmatch.label, earliestMissmatch.target, str1, str2);
							}
							if (o.op == Op.RHIDE)
								rightHidings.offer(o);
							if (o.op == Op.LHIDE)
								leftHidings.offer(o);
						}
					}

					if (earliestMissmatch.op == Op.LHIDE) {
						List<Operation> seq = new ArrayList<>();
						Operation prev = earliestMissmatch;
						for (Operation o : leftHidings) {
							BitSet conf = (BitSet) o.nextState.c1.clone();
							conf.or(prev.nextState.c1);
							if (conf.cardinality() == o.nextState.c1.cardinality()) {
								seq.add(o);
							}
						}
						if (seq.size() > 0)
							return String.format("Task \"%s(%s)\" is repeated %d more times in model 1",
									earliestMissmatch.label, earliestMissmatch.target, seq.size());
					} else {
						List<Operation> seq = new ArrayList<>();
						Operation prev = earliestMissmatch;
						for (Operation o : leftHidings) {
							BitSet conf = (BitSet) o.nextState.c2.clone();
							conf.or(prev.nextState.c2);
							if (conf.cardinality() == o.nextState.c2.cardinality()) {
								seq.add(o);
							}
						}
						if (seq.size() > 0)
							return String.format("Task \"%s(%s)\" is repeated %d more times in model 2",
									earliestMissmatch.label, earliestMissmatch.target, seq.size());
					}
				}

				if (targetPair != null) {
					if (targetPair.getFirst().intValue() == ((Integer) earliestMissmatch.target).intValue())
						return String.format(
								"Task \"%s(%s)\" can be skipped in model 1, whereas in model 2 it is always executed",
								earliestMissmatch.label, earliestMissmatch.target);
					if (targetPair.getSecond().intValue() == ((Integer) earliestMissmatch.target).intValue())
						return String.format(
								"Task \"%s(%s)\" can be skipped in model 2, whereas in model 1 it is always executed",
								earliestMissmatch.label, earliestMissmatch.target);

					for (Pair<Integer, Integer> pair : localMatches) {
						BehaviorRelation br1 = pes1.getBRelation(targetPair.getFirst(), pair.getFirst());
						BehaviorRelation br2 = pes2.getBRelation(targetPair.getSecond(), pair.getSecond());
						if (!br1.equals(br2)) {
							return String.format(
									"Models exhibit differences in the behavior relation of the following tasks: %s[%s], %s[%s]: %s vs %s",
									targetPair, pes1.getLabel(targetPair.getFirst()), pair,
									pes1.getLabel(pair.getFirst()), br1, br2);
						}
					}
				} else {
					if (earliestMissmatch.op == Op.LHIDE) {
						BitSet dpred = pes1.getDirectPredecessors((Integer) earliestMissmatch.target);
						dpred.and(earliestMissmatch.nextState.c1);
						String str = getEventLabels(pes1, dpred);

						return String.format(
								"Task \"%s(%s)\" occurs in model 1 just after tasks \"%s\", whereas the same task does not occur in model 2",
								earliestMissmatch.label, earliestMissmatch.target, str);
					} else {
						BitSet dpred = pes2.getDirectPredecessors((Integer) earliestMissmatch.target);
						dpred.and(earliestMissmatch.nextState.c2);
						String str = getEventLabels(pes2, dpred);

						return String.format(
								"Task \"%s(%s)\" occurs in model 2 just after tasks \"%s\", whereas the same task does not occur in model 1",
								earliestMissmatch.label, earliestMissmatch.target, str);
					}
				}
			}
			return null;
		}

		private String getEventLabels(PESSemantics<T> pes, BitSet set) {
			StringBuilder str = new StringBuilder("{");
			boolean firstIteration = true;
			for (int e = set.nextSetBit(0); e >= 0; e = set.nextSetBit(e + 1)) {
				if (!firstIteration)
					str.append(",");
				firstIteration = false;
				str.append(String.format("%s(%d)", pes.getLabel(e), e));
			}
			return str.append("}").toString();
		}

	}

	public List<String> getDiff() {
		List<String> verbalizations = new ArrayList<>();
		Set<State> remainingStates = new HashSet<>();
		remainingStates.addAll(lMATCHES.values());
		remainingStates.addAll(rMATCHES.values());

		// First let's work compound maximal states
		// ==== MODEL 1
		for (BitSet c1 : lMATCHES.keySet()) {
			if (lMATCHES.get(c1).size() > 1) {
				for (State pivot : lMATCHES.get(c1)) {
					if (pivot.weight > 0) {
						remainingStates.removeAll(lMATCHES.get(c1));
						MaximalStateAnalyzer analizer = new MaximalStateAnalyzer();
						analizer.partitionStateSpace(lMATCHES.get(c1)).forwardAnalysis().selectExplainingPair();

						String verb = analizer.getEarliestDifference();
						if (verb != null)
							verbalizations.add(verb);
						break;
					}
				}
			}
		}
		// ==== MODEL 2
		for (BitSet c2 : rMATCHES.keySet()) {
			if (rMATCHES.get(c2).size() > 1) {
				for (State pivot : rMATCHES.get(c2)) {
					if (pivot.weight > 0) {
						remainingStates.removeAll(rMATCHES.get(c2));
						MaximalStateAnalyzer analizer = new MaximalStateAnalyzer();
						analizer.partitionStateSpace(rMATCHES.get(c2)).forwardAnalysis().selectExplainingPair();

						String verb = analizer.getEarliestDifference();
						if (verb != null)
							verbalizations.add(verb);
						break;
					}
				}
			}
		}
		for (State pivot : remainingStates) {
			if (pivot.weight > 0) {
				MaximalStateAnalyzer analizer = new MaximalStateAnalyzer();
				analizer.partitionStateSpace(Collections.singleton(pivot)).forwardAnalysis().selectExplainingPair();

				String verb = analizer.getEarliestDifference();
				if (verb != null)
					verbalizations.add(verb);
			}
		}
		return verbalizations;
	}

	public void shortestPathDijkstra() {
		final Map<State, Integer> dist = new HashMap<>();
		Set<Operation> hidings = new HashSet<>();
		Map<State, State> prev = new HashMap<>();
		PriorityQueue<State> open = new PriorityQueue<State>(11, new Comparator<State>() {
			public int compare(State o1, State o2) {
				return Integer.compare(dist.get(o1), dist.get(o2));
			}
		});

		for (State s : relevantStates)
			dist.put(s, Integer.MAX_VALUE);
		dist.put(root, 0);

		open.offer(root);
		while (!open.isEmpty()) {
			State curr = open.poll();
			for (Operation o : descendants.get(curr)) {
				State succ = o.nextState;
//				System.out.print(o);
				if (relevantStates.contains(succ)) {
//					System.out.println("  <<");
					int distance = dist.get(curr) + 1;
					if (distance < dist.get(succ)) {
						open.remove(succ);
						dist.put(succ, distance);
						prev.get(curr);
						open.offer(succ);
					}
					if (o.op != Operation.Op.MATCH)
						hidings.add(o);
				} else
					;
			}
		}

//		System.out.println("Number of hidings: " + hidings.size());
		Map<Integer, List<Operation>> orderedH = new TreeMap<>();
		for (Operation o : hidings) {
			List<Operation> list = orderedH.get(dist.get(o.nextState));
			if (list == null)
				orderedH.put(dist.get(o.nextState), list = new ArrayList<>());
			list.add(o);
		}

//		for (Entry<Integer, List<Operation>> entry : orderedH.entrySet()) {
//			System.out.println(entry);
//		}
	}

//	public void setVerbalizer(DiffMMVerbalizer<Integer> verbalizer) {
//		this.verbalizer = verbalizer;
//	}
	
	public void setVerbalizer(DiffMMGraphicalVerbalizer verbalizer) {
		this.verbalizer = verbalizer;
	}
	
	public Set<State> getRelevantStates() {
		return this.relevantStates;
	}
}
