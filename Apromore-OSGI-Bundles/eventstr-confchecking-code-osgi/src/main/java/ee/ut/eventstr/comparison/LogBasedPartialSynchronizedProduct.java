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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.org.processmining.framework.util.Pair;

public class LogBasedPartialSynchronizedProduct<T> {

	public static class State implements Comparable<State> {
		BitSet c1;
		BitSet c2;
		Multiset<String> labels;
		StateHint action;
		public short cost = 0;

		State(BitSet c1, Multiset<String> labels, BitSet c2) {
			this.c1 = c1;
			this.c2 = c2;
			this.labels = labels;
		}

		public String toString() {
			return String.format("<%s,%s,%s,%d>", c1, labels, c2, cost);
		}

		public int compareTo(State o) {
			return Short.compare(this.cost, o.cost);
		}
	}

	enum StateHint {
		CREATED, MERGED, DISCARDED
	};

	public enum Op {
		MATCH, LHIDE, RHIDE, MATCHNSHIFT, RHIDENSHIFT
	};

	public static class Operation {
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
	private SinglePORunPESSemantics<T> pes2;
	private int numberOfTargets;
	private BitSet maxConf1;
	public State matchings;

	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;
	private State root;
	private Table<BitSet, BitSet, Map<Multiset<String>, State>> stateSpaceTable;

	private List<State> states = new ArrayList<>();
	private Set<State> relevantStates;
	private LinkedList<Operation> opSeq;

	public LogBasedPartialSynchronizedProduct(SinglePORunPESSemantics<T> pes1, SinglePORunPESSemantics<T> pes2) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();
		this.stateSpaceTable = HashBasedTable.create();

		this.numberOfTargets = pes1.getMaxConf().size();
		this.maxConf1 = pes1.getMaxConf().iterator().next();
		this.matchings = null;
	}

	public LogBasedPartialSynchronizedProduct<T> perform() {
		Queue<State> open = new PriorityQueue<>();

		root = getState(new BitSet(), HashMultiset.<String> create(), new BitSet());

		open.offer(root);

		while (!open.isEmpty()) {
			State s = open.poll();

			if (isCandidate(s)) {
				BitSet lpe = pes1.getPossibleExtensions(s.c1);
				BitSet rpe = pes2.getPossibleExtensions(s.c2);

				if (lpe.isEmpty() && rpe.isEmpty()) {
					matchings = s;
					continue;
				}

				// System.out.println("State: " +s);
				List<Operation> candidates = new ArrayList<>();
				BitSet pruned1 = new BitSet();
				BitSet pruned2 = new BitSet();

				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1 + 1)) {
					String label1 = pes1.getLabel(e1);
					BitSet c1p = (BitSet) s.c1.clone();
					c1p.set(e1);

					for (int e2 = rpe.nextSetBit(0); e2 >= 0; e2 = rpe.nextSetBit(e2 + 1)) {
						if (label1.equals(pes2.getLabel(e2)) && isOrderPreserving(s, e1, e2)) {
							pruned1.set(e1);
							pruned2.set(e2);

							// if (prev != null &&
							// pes1.getBRelation(e1, prevPair.getFirst()) ==
							// BehaviorRelation.CONCURRENCY &&
							// pes2.getBRelation(e2, prevPair.getSecond()) ==
							// BehaviorRelation.CONCURRENCY &&
							// label1.compareTo(prev.label) > 0)
							// continue;

							BitSet c2p = (BitSet) s.c2.clone();
							c2p.set(e2);
							Multiset<String> labels = HashMultiset.create(s.labels);
							labels.add(label1);

							State nstate = getState(c1p, labels, c2p);
							nstate.cost = s.cost; // A matching operation does
													// not change the current
													// cost

							Operation operation = Operation.match(nstate, new Pair<>(e1, e2), label1);

							candidates.add(operation);

							// switch (pair.getFirst()) {
							// case CREATED:
							// open.offer(nstate);
							// ancestors.put(nstate, s);
							// case MERGED:
							// Operation operation;
							// if (extPair.getSecond())
							// operation = Operation.matchnshift(nstate, new
							// Pair<>(e1, e2), label1);
							// else
							// operation = Operation.match(nstate, new
							// Pair<>(e1, e2), label1);
							//
							// descendants.put(s, operation);
							// default:
							// }

							// IOUtils.toFile("psp.dot", toDot());
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

				nextCandidate: for (Operation operation : candidates) {
					Pair<Integer, Integer> pair = (Pair) operation.target;
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

					switch (operation.nextState.action) {
					case CREATED:
						open.offer(operation.nextState);
						ancestors.put(operation.nextState, s);
					case MERGED:
						descendants.put(s, operation);
					default:
					}

					// IOUtils.toFile("psp.dot", toDot());
				}

				pruned1.andNot(kept1);
				pruned2.andNot(kept2);

				nextCandidate2: for (int e2 = rpe.nextSetBit(0); e2 >= 0; e2 = rpe.nextSetBit(e2 + 1)) {
					if (pruned2.get(e2) || kept2.get(e2))
						continue;

					for (int e2p = kept2.nextSetBit(0); e2p >= 0; e2p = kept2.nextSetBit(e2p + 1))
						if (pes2.getBRelation(e2, e2p) == BehaviorRelation.CONCURRENCY)
							continue nextCandidate2;

					BitSet c2p = (BitSet) s.c2.clone();
					c2p.set(e2);
					State nstate = getState(s.c1, s.labels, c2p);

					computeCost(nstate);

					switch (nstate.action) {
					case CREATED:
						open.offer(nstate);
						ancestors.put(nstate, s);
					case MERGED:
						descendants.put(s, Operation.rhide(nstate, e2, pes2.getLabel(e2)));
					default:
					}

					// IOUtils.toFile("psp.dot", toDot());
				}

				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1 + 1)) {
					if (pruned1.get(e1) || kept1.get(e1))
						continue;

					BitSet c1p = (BitSet) s.c1.clone();
					c1p.set(e1);

					State nstate = getState(c1p, s.labels, s.c2);
					computeCost(nstate);

					switch (nstate.action) {
					case CREATED:
						open.offer(nstate);
						ancestors.put(nstate, s);
					case MERGED:
						descendants.put(s, Operation.lhide(nstate, e1, pes1.getLabel(e1)));
					default:
					}

					// IOUtils.toFile("psp.dot", toDot());
				}
			}
		}
		return this;
	}

	public List<State> getStates() {
		return states;
	}

	private boolean isOrderPreserving(State s, int e1, Integer e2) {
		BitSet e1dpred = (BitSet) pes1.getDirectPredecessors(e1).clone();
		BitSet e2dpred = (BitSet) pes2.getDirectPredecessors(e2).clone();

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

			for (State ancestor : ancestors.get(curr)) {
				if (visited.contains(ancestor) || open.contains(ancestor))
					continue;
				for (Operation op : descendants.get(ancestor))
					if (op.nextState.equals(curr)) {
						// System.out.println(">> " + op);
						if (op.op == Op.MATCH) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> matchedEvents = (Pair<Integer, Integer>) op.target;
							e1dpred.clear(matchedEvents.getFirst());
							e2dpred.clear(matchedEvents.getSecond());

							if (!(e1causes.get(matchedEvents.getFirst()) == e2causes.get(matchedEvents.getSecond()))) {
								// System.out.println("====== It is not order
								// preserving!");
								return false;
							}

						} else if (op.op == Op.MATCHNSHIFT) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> matchedEvents = (Pair<Integer, Integer>) op.target;
							e1dpred.clear(matchedEvents.getFirst());
							e2dpred.clear(matchedEvents.getSecond());

							// System.out.println("Performed inverse shift
							// (+match): " + matchedEvents.getSecond());
							if (pes2.getBRelation(e2, matchedEvents.getSecond()) != BehaviorRelation.CONCURRENCY) {

								// e2causes = pes2.unshift(e2causes,
								// matchedEvents.getSecond());
								//
								e2causes = pes2.getLocalConfiguration(matchedEvents.getSecond());
							}

							if (!(e1causes.get(matchedEvents.getFirst()) == e2causes.get(matchedEvents.getSecond()))) {
								// System.out.println("====== It is not order
								// preserving! (after inverse shift)");
								return false;
							}
						} else if (op.op == Op.RHIDENSHIFT || op.op == Op.RHIDE) {
							Integer hiddenEvent = (Integer) op.target;
							// if (e2dpred.contains(hiddenEvent)) {
							e2dpred.clear(hiddenEvent);
							e2dpred.or(pes2.getDirectPredecessors(hiddenEvent));
							if (op.op == Op.RHIDENSHIFT
									&& pes2.getBRelation(e2, hiddenEvent) != BehaviorRelation.CONCURRENCY) {
								// System.out.println("Performed inverse shift:
								// " + hiddenEvent);

								// e2causes = pes2.unshift(e2causes,
								// hiddenEvent);
								//
								e2causes.clear(hiddenEvent);
								// e2causes =
								// pes2.getLocalConfiguration(hiddenEvent);
							}
							// }
						} else {
							Integer hiddenEvent = (Integer) op.target;
							e1dpred.clear(hiddenEvent);
							e1dpred.or(pes1.getDirectPredecessors(hiddenEvent));
						}
					}
				open.push(ancestor);
			}
		}
		return true;
	}

	private State getState(BitSet c1, Multiset<String> labels, BitSet c2) {
		State newState = new State(c1, labels, c2);
		states.add(newState);

		newState.action = StateHint.CREATED;

		if (stateSpaceTable.contains(c1, c2)) {
			Map<Multiset<String>, State> map = stateSpaceTable.get(c1, c2);
			if (map.containsKey(labels))
				newState.action = StateHint.MERGED;
			else
				map.put(labels, newState);
		} else {
			Map<Multiset<String>, State> map = new HashMap<>();
			map.put(labels, newState);
			stateSpaceTable.put(c1, c2, map);
		}
		return newState;
	}

	private boolean isCandidate(State s) {
		if (matchings == null)
			return true;
		return false;
	}

	public void computeCost(State s) {
		s.cost = (short) (g(s.c1, s.c2, s.labels) + h(s));
	}

	public int g(BitSet c1, BitSet c2, Multiset<String> labels) {
		return (c1.cardinality() + c2.cardinality() - labels.size() * 2);
	}

	public int h(State s) {
		Set<String> pf2 = pes2.getPossibleFutureAsLabels(s.c2);

		BitSet future = (BitSet) maxConf1.clone();
		future.andNot(s.c1);
		Set<String> diff = translate(future);
		diff.removeAll(pf2);

		return diff.size();
	}

	private Set<String> translate(BitSet bitset) {
		Set<String> set = new LinkedHashSet<>();
		for (int ev = bitset.nextSetBit(0); ev >= 0; ev = bitset.nextSetBit(ev + 1)) {
			set.add(pes1.getLabel(ev));
		}
		return set;
	}

	public LogBasedPartialSynchronizedProduct<T> prune() {
		Set<State> gvisited = new HashSet<>();
		Stack<State> open = new Stack<>();
		this.opSeq = new LinkedList<>();

		for (int i = 0; i < numberOfTargets; i++) {
			State s = matchings;
			if (s == null)
				continue;
			open.push(s);
			Set<State> visited = new HashSet<>();
			while (!open.isEmpty()) {
				State curr = open.pop();
				visited.add(curr);

				for (State pred : ancestors.get(curr)) {
					if (!visited.contains(pred) && !open.contains(pred)) {
						open.push(pred);
						for (Operation op : descendants.get(pred)) {
							if (op.nextState.equals(curr)) {
								opSeq.addFirst(op);
								break;
							}
						}
					}
				}
			}

			gvisited.addAll(visited);
		}

		this.relevantStates = gvisited;

		// System.out.println("Number of relevant states: " +
		// relevantStates.size());

		return this;
	}

	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);

		out.println("digraph G {\n\t node [shape=box];");
		Map<State, Integer> rstates = new HashMap<>();

		for (int i = 0; i < states.size(); i++) {
			State s = states.get(i);
			if (relevantStates == null || relevantStates.contains(s)) {
				rstates.put(s, i);
				// if (matchings.containsValue(s))
				// out.printf("\tn%d
				// [label=\"%s,%s\\n%s\\n%3.2f\",color=blue];\n", i, s.c1, s.c2,
				// s.labels, s.weight);
				// else
				if (matchings.equals(s))
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%d\", color=red];\n", i, s.c1, s.c2, s.labels, s.cost);
				else
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%d\"];\n", i, s.c1, s.c2, s.labels, s.cost);
			}
		}

		Collection<State> lstates = relevantStates != null ? relevantStates : states;

		for (State s : lstates) {
			Integer src = rstates.get(s);
			for (Operation op : descendants.get(s)) {
				if (relevantStates == null || relevantStates.contains(op.nextState)) {
					Integer tgt = rstates.get(op.nextState);
					out.printf("\tn%d -> n%d [label=\"%s\"];\n", src, tgt, op);
				}
			}
		}
		out.println("}");
		return str.toString();
	}

	public List<Operation> getOperationSequence() {
		if (opSeq == null)
			prune();
		return opSeq;
	}
}
