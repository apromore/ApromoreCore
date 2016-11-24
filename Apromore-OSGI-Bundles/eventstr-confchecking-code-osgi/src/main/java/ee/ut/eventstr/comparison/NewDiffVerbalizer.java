package ee.ut.eventstr.comparison;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Op;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.State;
import ee.ut.org.processmining.framework.util.Pair;

public class NewDiffVerbalizer<T> {
	private PESSemantics<T> pes1;
	private NewUnfoldingPESSemantics<T> pes2;
	private List<List<Operation>> opSeqs;
	private Set<Operation> lhideOps;
	private Set<Operation> rhideOps;
	private Map<Operation, Operation> predMatch;
	private Map<Operation, Operation> succMatch;
	private Table<BitSet, Multiset<Integer>, Map<Multiset<String>, State>> stateSpace;
	private State root;
	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;
	private ExpandedPomsetPrefix<T> expandedPrefix;
	private Set<Operation> operations;

	private Table<State, Operation, Pair<State, Operation>> confMismatches;
	private Map<Pair<Integer, Integer>, Multimap<State, Pair<Integer, Integer>>> conflictMismatches;
	private Map<Pair<Integer, Integer>, Multimap<State, Pair<Integer, Integer>>> causalityConcurrencyMismatches;
	private Map<Pair<Integer, Integer>, Set<State>> eventSubstitutionMismatches;
	private Table<State, Operation, Pair<Integer,Integer>> confTableBridge;

	private Map<State, Operation> lastMatchMap;

	private Set<String> statements;

	public static final boolean DEBUG = false;

	public NewDiffVerbalizer(PESSemantics<T> pes1, NewUnfoldingPESSemantics<T> pes2, ExpandedPomsetPrefix<T> epp) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.expandedPrefix = epp;
		this.opSeqs = new ArrayList<>();
		this.lhideOps = new HashSet<>();
		this.rhideOps = new HashSet<>();
		this.predMatch = new HashMap<>();
		this.succMatch = new HashMap<>();
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();

		this.stateSpace = HashBasedTable.create();
		this.root = new State(new BitSet(), HashMultiset.<String>create(), HashMultiset.<Integer>create());

		this.confMismatches = HashBasedTable.create();
		this.causalityConcurrencyMismatches = new HashMap<>();
		this.conflictMismatches = new HashMap<>();
		this.eventSubstitutionMismatches = new HashMap<>();
		this.confTableBridge = HashBasedTable.create();
		this.lastMatchMap = new HashMap<>();
		this.statements = new TreeSet<>();

	}

	public void addPSP(List<Operation> opSeq) {
		opSeqs.add(opSeq);
	}

	public Set<String> verbalize() {
		for (List<Operation> opSeq: opSeqs) {
			addPSPBranchToGlobalPSP(opSeq);
		}
		prune();

//		IOUtils.toFile("spsp.dot", toDot());
		Map<Pair<Operation, Operation>, Pair<State, State>> pending = new HashMap<>();
		findCausalityConcurrencyMismatches(root, new HashSet<Pair<State,Operation>>(), new HashSet<State>(), new LinkedList<Operation>(), pending);
//		if (!pending.isEmpty())
//			throw new RuntimeException("Something wrong with some causality/concurrency mismatching events: " + pending);
		findConflictMismatches(root, new HashSet<Pair<State,Operation>>(), new HashSet<Pair<State,Operation>>(), new HashSet<State>(), new LinkedList<Operation>());
		markExpandedPrefix(root, HashMultimap.<String, Operation>create(), HashMultimap.<String, Operation>create(), new HashSet<State>(), null);
		findSkipMismatches(root, new HashMap<Integer, Pair<State, Operation>> (), new HashMap<Integer, Pair<State, Operation>> (), new HashSet<State>());
		verbalizeAdditionalModelBehavior();
		verbalizeOptionalModelBehavior();

		for (Pair<Integer, Integer> p: causalityConcurrencyMismatches.keySet()) {
			Map<Pair<Integer, Integer>, State> map = new HashMap<>();
//			System.out.println("Analizing: " + p);
			for (Entry<State, Pair<Integer, Integer>> ctx : causalityConcurrencyMismatches.get(p).entries()) {
//				System.out.println(ctx);
				State state = ctx.getKey();
				Pair<Integer,Integer> cfpair = ctx.getValue();
				State exstate = map.get(cfpair);
				if (exstate == null || state.c1.cardinality() <= exstate.c1.cardinality() && state.c2.size() <= exstate.c2.size()) {
					map.put(cfpair, state);
				}
			}

			for (Entry<Pair<Integer,Integer>, State> ctx: map.entrySet()) {
				if (DEBUG) {
					System.out.printf(">> Causality/Concurrency mismatch (%s, %s, %s, %s, %s)\n", ctx.getValue(),
							translate(pes1, p.getFirst()), translate(pes1, ctx.getKey().getFirst()),
							translate(pes2, p.getSecond()), translate(pes2, ctx.getKey().getSecond()));
				}
				//// ==============================
				////   Verbalization
				//// ==============================
				if (pes1.getBRelation(ctx.getKey().getFirst(), p.getFirst()) == BehaviorRelation.CAUSALITY) {
					statements.add(String.format("In the log, after '%s', '%s' occurs before '%s', while in the model they are concurrent",
							translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getSecond()), //getFirst changed into getSecond
							translate(pes1, ctx.getKey().getFirst()), translate(pes1, p.getFirst())));
				}
				else {
					statements.add(String.format("In the model, after '%s', '%s' occurs before '%s', while in the log they are concurrent",
							translate(pes2, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getSecond()),
							translate(pes2, ctx.getKey().getSecond()), translate(pes2, p.getSecond())));
				}
			}
		}

		for (Pair<Integer, Integer> p: eventSubstitutionMismatches.keySet()) {
			State enablingState = null;
			for (State state : eventSubstitutionMismatches.get(p)) {
				if (enablingState == null || state.c1.cardinality() <= enablingState.c1.cardinality() && state.c2.size() <= enablingState.c2.size()) {
					enablingState = state;
				}
			}
			if (DEBUG) {
				System.out.printf(">> Event substition (%s, %s, %s)\n", enablingState,
						translate(pes1, p.getFirst()), translate(pes2, p.getSecond()));
			}
			statements.add(String.format("In the log, after '%s', '%s' is substituted by '%s'",
					translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(enablingState).target).getFirst()),
					translate(pes2, p.getSecond()),
					translate(pes1, p.getFirst())));
		}

		for (Pair<Integer, Integer> p: conflictMismatches.keySet()) {
			Map<Pair<Integer, Integer>, State> map = new HashMap<>();
			for (Entry<State, Pair<Integer, Integer>> ctx : conflictMismatches.get(p).entries()) {
				State state = ctx.getKey();
				Pair<Integer,Integer> cfpair = ctx.getValue();
				State exstate = map.get(cfpair);
				if ((exstate == null) || (state.c1.cardinality() <= exstate.c1.cardinality()) && (state.c2.size() <= exstate.c2.size())) {
					map.put(cfpair, state);
				}
			}

			for (Entry<Pair<Integer,Integer>, State> ctx: map.entrySet()) {
				if (DEBUG) {
					System.out.printf(">> Conflict mismatch (%s, %s, %s, %s, %s)\n", ctx.getValue(),
							translate(pes1, p.getFirst()), translate(pes1, ctx.getKey().getFirst()),
							translate(pes2, p.getSecond()), translate(pes2, ctx.getKey().getSecond()));
				}

				if (pes1.getBRelation(p.getFirst(), ctx.getKey().getFirst()) == BehaviorRelation.CONCURRENCY) {
					statements.add(String.format("In the log, after '%s', '%s' and '%s' are concurrent, while in the model they are mutually exclusive",
//							translate(pes1, p.getFirst()),
							translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getFirst()),
							translate(pes1, ctx.getKey().getFirst()),
							translate(pes1, p.getFirst())));
				}
				else if (pes2.getBRelation(p.getSecond(), ctx.getKey().getSecond()) == BehaviorRelation.CONCURRENCY) {
					String firstEvent, secondEvent;
					firstEvent = translate(pes2, ctx.getKey().getSecond());
					secondEvent = translate(pes2, p.getSecond());

					if (firstEvent.compareTo(secondEvent) > 0) {
						String temp = firstEvent;
						firstEvent = secondEvent;
						secondEvent = temp;
					}

					statements.add(String.format("In the model, after '%s', '%s' and '%s' are concurrent, while in the log they are mutually exclusive",
							translate(pes2, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getSecond()),
							firstEvent,
							secondEvent));
				}
				else if (pes1.getBRelation(ctx.getKey().getFirst(), p.getFirst()) == BehaviorRelation.CAUSALITY) {
					statements.add(String.format("In the log, after '%s', '%s' occurs before '%s', while in the model they are mutually exclusive",
//							translate(pes1, p.getFirst()),
							translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getFirst()),
							translate(pes1, ctx.getKey().getFirst()),
							translate(pes1, p.getFirst())));
				}
				else if (pes2.getBRelation(ctx.getKey().getSecond(), p.getSecond()) == BehaviorRelation.CAUSALITY) {
//				else {
					statements.add(String.format("In the model, after '%s', '%s' occurs before '%s', while in the log they are mutually exclusive",
							translate(pes2, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getSecond()),
							translate(pes2, ctx.getKey().getSecond()),
							translate(pes2, p.getSecond())));
				}
			}
		}

//		System.out.println("================");
//		for (String stm: statements) {
//			System.out.println(stm);
//		}
		return statements;
	}

	private void prune() {
		Set<State> sinks = new HashSet<>(ancestors.keys());
		sinks.removeAll(ancestors.values());

		Set<State> visited = new HashSet<>();
		operations = new HashSet<>();
		LinkedList<State> open = new LinkedList<>(sinks);
		while (!open.isEmpty()) {
			State curr = open.pop();
			visited.add(curr);
			if (root.equals(curr)) {
				continue;
			}

			State pred = ancestors.get(curr).iterator().next();
			if (pred != null) {
				for (Operation op: descendants.get(pred)) {
					if (op.nextState.equals(curr)) {
						operations.add(op);
						break;
					}
				}
				if (!visited.contains(pred) && !open.contains(pred)) {
					open.push(pred);
				}
			}
		}


		for (State s: visited) {
			Set<Operation> toDelete = new HashSet<>();
			for (Operation o: descendants.get(s)) {
				if (!operations.contains(o)) {
					toDelete.add(o);
				}
			}
			for (Operation o: toDelete) {
				descendants.remove(s, o);
			}
		}

		lhideOps.retainAll(operations);
		rhideOps.retainAll(operations);
	}

	private void verbalizeAdditionalModelBehavior() {
		for (Entry<State, List<Integer>> entry:	expandedPrefix.getAdditionalAcyclicIntervals().entries()) {
			if (DEBUG) {
				System.out.printf("In the log, %s do(es) not occur after %s\n", translate(entry.getValue()), entry.getKey());
			}
			List<String> interval = translate(entry.getValue());

			if (interval.size() > 0) {
				statements.add(String.format("In the log, the interval %s does not occur after '%s'",
						interval, translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst())));
			}
			else {
				statements.add(entry.toString() + " empty interval");
//				statements.add(opSeqs.toString());
			}
		}

//		System.out.println(	expandedPrefix.getAdditionalAcyclicIntervals().entries() + " intervals");
		for (Entry<State, Multiset<Integer>> entry:	expandedPrefix.getAdditionalCyclicIntervals().entries()) {
			if (DEBUG) {
				System.out.printf("In the log, the cycle involving %s does not occur after %s\n", translate(entry.getValue()), entry.getKey());
			}

			// some ugly stuff to clean up redundant statements
			List<String> cycleEvents = translate(entry.getValue());
			String first = "";
			String second = "";
			Set<String> redundant = new HashSet<String>();
			String curstat;

			for (String s: statements) {
				curstat = s;
				s = s.replace("In the log, ", "");
				s = s.replace(" occurs after", "");
				s = s.replace(" and before", "");
				s = s.replace("'", "");

				int pos = s.indexOf(" ");
				if (pos > 0) {
					first = s.substring(0, s.indexOf(" "));
				}
				pos = s.indexOf(" ", first.length() + 1);
				if (pos > 0) {
					second = s.substring(first.length() + 1, pos);
				}

				if (cycleEvents.contains(first) && cycleEvents.contains(second)) {
					redundant.add(curstat);
				}
			}
			statements.removeAll(redundant);
			// end of the ugly stuff to remove redundant statements

			List<String> interval = translate(entry.getValue());
			String localstate = translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst());

//			if (interval.contains(localstate)) {
				statements.add(String.format("In the log, the cycle involving %s does not occur after '%s'",
						interval, localstate));
//			}
		}
	}

	private List<String> translate(Collection<Integer> multiset) {
		List<String> set = new ArrayList<String>();
		for (Integer ev: multiset) {
			if (!pes2.getInvisibleEvents().contains(ev)) {
				set.add(translate(pes2, ev));
			}
		}
		return set;
	}

	private void markExpandedPrefix(State curr, Multimap<String, Operation> lpending,
									Multimap<String, Operation> rpending, Set<State> visited, Operation lastMatch) {
		visited.add(curr);
		lastMatchMap.put(curr, lastMatch);

		for (Operation op: descendants.get(curr)) {
			expandedPrefix.mark(op.nextState, op);

			if (lhideOps.contains(op)) {
				if (curr.labels.contains(op.label)) {
					if (DEBUG) {
						System.out.printf("In the log, '%s' is repeated after '%s'\n", translate(pes1, (Integer) op.target), curr);
					}
					statements.add(String.format("In the log, '%s' is repeated after '%s'",
							translate(pes1, (Integer) op.target),
							translate(pes1, ((Pair<Integer, Integer>)lastMatch.target).getFirst())));
				}
				else {
					lpending.put(op.label, op);
				}
			}
			else if (rhideOps.contains(op)) {
				if (curr.labels.contains(op.label)) {
					if (DEBUG) {
						System.out.printf("In the model, '%s' is repeated after '%s'\n", translate(pes2, (Integer) op.target), curr);
					}
					statements.add(String.format("In the model, '%s' is repeated after '%s'",
							translate(pes2, (Integer) op.target),
							translate(pes2, ((Pair<Integer, Integer>)lastMatch.target).getSecond())));
				}
				else {
					rpending.put(op.label, op);
				}
			}

			if ((op.op == Op.MATCH) || (op.op == Op.MATCHNSHIFT)) {
				for (Operation h: rpending.values()) {
					if (!succMatch.containsKey(h)) {
						succMatch.put(h, op);
					}
				}
				for (Operation h: lpending.values()) {
					if (!succMatch.containsKey(h)) {
						succMatch.put(h, op);
					}
				}

				if (!visited.contains(op.nextState)) {
					markExpandedPrefix(op.nextState, lpending, rpending, visited, op);
				}

			}
			else {
				if (!predMatch.containsKey(op)) {
					predMatch.put(op, lastMatch);
				}
				if (!visited.contains(op.nextState)) {
					markExpandedPrefix(op.nextState, lpending, rpending, visited, lastMatch);
				}
			}

			if (lpending.containsEntry(op.label, op)) {
				Collection<Operation> right = rpending.get(op.label);

				if (lpending.get(op.label).size() == 1 && right.size() == 1) {
					Operation pred = predMatch.get(right.iterator().next());
					Operation succ = succMatch.get(op);
					if (DEBUG) {
						System.out.printf("In the log, \"%s\" occurs after \"%s\" instead of \"%s\"\n", translate(pes1, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));
					}
					statements.add(String.format("In the model, '%s' occurs after '%s' instead of '%s'",
							translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()),
							translate(pes1, ((Pair<Integer, Integer>)pred.target).getSecond()),
							translate(pes1, (Integer) op.target)));

//					statements.add(String.format("In the log, '%s' occurs after '%s' instead of '%s'",
//							translate(pes1, (Integer) op.target),
//							pred == null || pred.label.equals("_0_") ? "<start state>" :
//								translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()),
//							succ == null || succ.label.equals("_1_") ? "<end state>" :
//								translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond())));

					rpending.removeAll(op.label);
				}
				else {
					Operation pred = predMatch.get(op);
					Operation succ = succMatch.get(op);
					if (DEBUG) {
						System.out.printf("In the log, \"%s\" occurs after \"%s\" and before \"%s\"\n", translate(pes1, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));
					}

					statements.add(String.format("In the log, '%s' occurs after '%s' and before '%s'",
							translate(pes1, (Integer) op.target),
							pred == null || pred.label.equals("_0_") ? "<start state>" :
									translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()),
							succ == null || succ.label.equals("_1_") ? "<end state>" :
									translate(pes1, ((Pair<Integer, Integer>)succ.target).getFirst())));
				}
				lpending.remove(op.label, op);
			}
			else if (rpending.containsEntry(op.label, op)) {
				Collection<Operation> left = lpending.get(op.label);

				if (rpending.get(op.label).size() == 1 && left.size() == 1) {
					Operation pred = predMatch.get(left.iterator().next());
					Operation succ = succMatch.get(op);

					if (DEBUG) {
						System.out.printf("In the model, \"%s\" occurs after \"%s\" instead of \"%s\"\n", translate(pes2, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));
					}
//					System.out.println(translate(pes2, (Integer) op.target));
//					System.out.println(translate(pes2, ((Pair<Integer, Integer>)pred.target).getFirst()));
//					System.out.println(translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()));
//					System.out.println(translate(pes2, ((Pair<Integer, Integer>)succ.target).getFirst()));
//					System.out.println(translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond()));

					statements.add(String.format("In the model, '%s' occurs after '%s' instead of '%s'",
							translate(pes2, ((Pair<Integer, Integer>)pred.target).getFirst()),
							translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()),
							translate(pes2, (Integer) op.target)));

//					statements.add(String.format("In the model, '%s' occurs after '%s' instead of '%s'",
//							translate(pes2, (Integer) op.target),
//							pred == null || pred.label.equals("_0_") ? "<start state>" :
//								translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()),
//							succ == null || succ.label.equals("_1_") ? "<end state>" :
//								translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond())));

					lpending.removeAll(op.label);
				}
				else {
					Operation pred = predMatch.get(op);
					Operation succ = succMatch.get(op);
					if (DEBUG) {
						System.out.printf("In the model, \"%s\" occurs after \"%s\" and before \"%s\"\n", translate(pes2, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));
					}

					statements.add(String.format("In the model, '%s' occurs after '%s' and before '%s'",
							translate(pes2, (Integer) op.target),
							pred == null || pred.label.equals("_0_") ? "<start state>" :
									translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()),
							succ == null || succ.label.equals("_1_") ? "<end state>" :
									translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond())));

				}
				rpending.remove(op.label, op);
			}
		}
	}

	private void findSkipMismatches(State curr, Map<Integer, Pair<State, Operation>> ltargets, Map<Integer, Pair<State, Operation>> rtargets, Set<State> visited) {
		visited.add(curr);
		Map<Integer, Pair<Integer, Operation>> lhides = new HashMap<>();
		Map<Integer, Pair<Integer, Operation>> rhides = new HashMap<>();

		for (Operation op: confMismatches.row(curr).keySet()) {
			Pair<State, Operation> pair = confMismatches.get(curr, op);
			if (op.op == Op.LHIDE) {
				Integer target = ((Pair<Integer,Integer>)pair.getSecond().target).getFirst();
				lhides.put((Integer)op.target, new Pair<>(target, op));
			}
			else if (op.op == Op.RHIDE) {
				Integer target = ((Pair<Integer,Integer>)pair.getSecond().target).getSecond();
				rhides.put((Integer)op.target, new Pair<>(target, op));
			}
		}

		for (Operation op: descendants.get(curr)) {
			if ((op.op == Op.MATCH) || (op.op == Op.MATCHNSHIFT)) {
				Pair<Integer,Integer> deltaEvents = (Pair)op.target;
				Integer e = deltaEvents.getFirst();
				Integer f = deltaEvents.getSecond();

				if (lhides.containsKey(e)) {
					Integer target = lhides.get(e).getFirst();
					ltargets.put(target, new Pair<>(curr, lhides.get(e).getSecond()));
					if (!visited.contains(op.nextState)) {
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
					}
					ltargets.remove(target);
				}
				else if (rhides.containsKey(f)) {
					Integer target = rhides.get(f).getFirst();
					rtargets.put(target, new Pair<>(curr, rhides.get(f).getSecond()));
					if (!visited.contains(op.nextState)) {
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
					}
					rtargets.remove(target);
				}
				else {
					if (ltargets.containsKey(e)) {
						if (DEBUG) {
							System.out.printf("In the model, after '%s', '%s' is optional\n",
									translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(ltargets.get(e).getFirst()).target).getFirst()),
									translate(pes1, (Integer)ltargets.get(e).getSecond().target));
						}
						statements.add(String.format("In the model, after '%s', '%s' is optional",
								translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(ltargets.get(e).getFirst()).target).getFirst()),
								translate(pes1, (Integer)ltargets.get(e).getSecond().target)));

						// Remove the corresponding conflict mismatch
						conflictMismatches.remove(confTableBridge.get(ltargets.get(e).getFirst(), ltargets.get(e).getSecond()));
						ltargets.remove(e);
					}
					else if (rtargets.containsKey(f)) {
						if (DEBUG) {
							System.out.printf("In the log, after '%s', '%s' is optional\n",
									translate(pes2, ((Pair<Integer,Integer>)lastMatchMap.get(rtargets.get(f).getFirst()).target).getSecond()),
									translate(pes2, (Integer)rtargets.get(f).getSecond().target));
						}

						// remove statement from model perspective that reflects the optional event (which is verbalized here)
						statements.remove(String.format("In the model, '%s' occurs after '%s' and before '%s'",
								translate(pes2, (Integer)rtargets.get(f).getSecond().target),
								translate(pes2, ((Pair<Integer,Integer>)lastMatchMap.get(rtargets.get(f).getFirst()).target).getSecond()),
								op.label));

						statements.add(String.format("In the log, after '%s', '%s' is optional",
								translate(pes2, ((Pair<Integer,Integer>)lastMatchMap.get(rtargets.get(f).getFirst()).target).getSecond()),
								translate(pes2, (Integer)rtargets.get(f).getSecond().target)));

						// Remove the corresponding conflict mismatch
						conflictMismatches.remove(confTableBridge.get(rtargets.get(f).getFirst(), rtargets.get(f).getSecond()));
						rtargets.remove(f);
					}
					if (!visited.contains(op.nextState)) {
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
					}
				}
			}
			else {
				if (!visited.contains(op.nextState)) {
					findSkipMismatches(op.nextState, ltargets, rtargets, visited);
				}
			}
		}
	}

	private void findConflictMismatches(State sigma, Set<Pair<State, Operation>> cms, Set<Pair<State, Operation>> chs, Set<State> visited, LinkedList<Operation> stack) {
		visited.add(sigma);
		for (Operation op: descendants.get(sigma)) {
			stack.push(op);
			Pair<Integer,Integer> deltaEvents = getDeltaEvents(op);
			Integer e = deltaEvents.getFirst();
			Integer f = deltaEvents.getSecond();

			if (((op.op == Op.RHIDE) || (op.op == Op.RHIDENSHIFT)) && (pes2.getInvisibleEvents().contains(f))) {
				if (!visited.contains(op.nextState)) {
					findConflictMismatches(op.nextState, cms, chs, visited, stack);
					stack.pop();
				}
				continue;
			}

			Integer ep = null, fp = null;
			Set<Pair<State, Operation>> n_cms = retainCommutative(cms, e, f);
			Set<Pair<State, Operation>> n_chs = retainCommutative(chs, e, f);
//			System.out.println(op);
			switch (op.op) {
				case LHIDE:
					Pair<Pair<State, Operation>, Integer> tuple = findConflictingMatchForLHide(cms, e);
					if (tuple != null) {
						Pair<State, Operation> pair = tuple.getFirst();
						Pair<Integer, Integer> p = (Pair)pair.getSecond().target;
						f = tuple.getSecond();
						ep = p.getFirst();
						fp = p.getSecond();

						State enablingState = findEnablingState(stack, op, pair.getSecond());
//					System.out.printf("Conflict related mismatch %s enabling state: %s\n", tuple, enablingState);

						assertConflictMismatch(enablingState, e, ep, f, fp);

						lhideOps.remove(op);
						n_cms.remove(pair);
					}
					else {
						n_chs.add(new Pair<>(sigma, op));
					}
					break;
				case RHIDE:
				case RHIDENSHIFT:
					Pair<Pair<State, Operation>, Integer> tuplep = findConflictingMatchForRHide(cms, f);
					if (tuplep != null) {
						Pair<State, Operation> pair = tuplep.getFirst();
						Pair<Integer, Integer> p = (Pair)pair.getSecond().target;
						e = tuplep.getSecond();
						ep = p.getFirst();
						fp = p.getSecond();

						State enablingState = findEnablingState(stack, pair.getSecond(), op);
//					System.out.printf("Conflict related mismatch %s enabling state: %s\n", tuplep, enablingState);

						assertConflictMismatch(enablingState, e, ep, f, fp);

						rhideOps.remove(op);
						n_cms.remove(pair);
					}
					else {
						n_chs.add(new Pair<>(sigma, op));
					}
					break;
				case MATCH:
				case MATCHNSHIFT:
					Pair<Pair<State, Operation>, Pair<Integer,Integer>> tupleq = findConflictingHideforMatch(chs, e, f);
					if (tupleq != null) {
						Pair<State, Operation> pair = tupleq.getFirst();
						Operation hideOp = pair.getSecond();
						ep = tupleq.getSecond().getFirst();
						fp = tupleq.getSecond().getSecond();

						State enablingState = null;
						if (hideOp.op == Op.LHIDE) {
							enablingState = findEnablingState(stack, hideOp, op);
						}
						else {
							enablingState = findEnablingState(stack, op, hideOp);
						}

//					System.out.printf("Conflict related mismatch %s enabling state: %s\n", tupleq, enablingState);

						// Here, (e,f) refer to the matched events. That is why I changed the order of the parameters
						assertConflictMismatch(enablingState, ep, e, fp, f);

						if (hideOp.op == Op.LHIDE) {
							lhideOps.remove(hideOp);
						}
						else {
							lhideOps.remove(hideOp);
						}

						////// ===================================
						confMismatches.put(pair.getFirst(), pair.getSecond(), new Pair<>(sigma, op));
						confTableBridge.put(pair.getFirst(), pair.getSecond(), new Pair<Integer,Integer>(ep,fp));
						////// ===================================

						n_chs.remove(pair);
					}
					else {
						n_cms.add(new Pair<>(sigma, op));
					}
			}
			if (!visited.contains(op.nextState)) {
				findConflictMismatches(op.nextState, n_cms, n_chs, visited, stack);
				stack.pop();
			}
		}
	}

	private Pair<Pair<State, Operation>, Pair<Integer, Integer>> findConflictingHideforMatch(
			Set<Pair<State, Operation>> chs, Integer e, Integer f) {

		for (Pair<State, Operation> pair: chs) {
			Operation op = pair.getSecond();
			if (op.op == Op.LHIDE) {
				Integer ep = (Integer)op.target;

				BitSet _fCauses = pes2.getCausesOf(f);
				Multiset<Integer> fCauses = HashMultiset.create();
				for (int ev = _fCauses.nextSetBit(0); ev >= 0; ev = _fCauses.nextSetBit(ev + 1)) {
					fCauses.add(ev);
				}

				for (Integer fp: pes2.getPossibleExtensions(fCauses)) {
					// Immediate conflict: Event enabled by the same causes of f' which is in conflict with f'
					if ((pes2.getBRelation(fp, f) == BehaviorRelation.CONFLICT) &&
							(pes2.getLabel(fp).equals(pes1.getLabel(ep)))) {
						return new Pair<>(pair, new Pair<>(ep, fp));
					}
				}

			}
			else {
				Integer fp = (Integer)op.target;

				BitSet eCauses = pes1.getLocalConfiguration(e);
				eCauses.clear(e);
				BitSet dconf = (BitSet)pes1.getPossibleExtensions(eCauses).clone();
				dconf.and(pes1.getConflictSet(e));

				for (int ep = dconf.nextSetBit(0); ep >= 0; ep = dconf.nextSetBit(ep + 1)) {
					if (pes1.getLabel(ep).equals(pes2.getLabel(fp))) {
						return new Pair<>(pair, new Pair<>(ep, fp));
					}
				}
			}
		}

		return null;
	}

	private Pair<Pair<State, Operation>, Integer> findConflictingMatchForLHide(
			Set<Pair<State, Operation>> cms, Integer e) {
		for (Pair<State, Operation> pair: cms) {
			Operation op = pair.getSecond();
			Pair<Integer,Integer> p = (Pair)op.target;
			Integer ep = p.getFirst();
			Integer fp = p.getSecond();

			BitSet _fpCauses = pes2.getCausesOf(fp);
			Multiset<Integer> fpCauses = HashMultiset.create();
			for (int ev = _fpCauses.nextSetBit(0); ev >= 0; ev = _fpCauses.nextSetBit(ev + 1)) {
				fpCauses.add(ev);
			}

			for (Integer pe: pes2.getPossibleExtensions(fpCauses)) {
				// Immediate conflict: Event enabled by the same causes of f' which is in conflict with f'
				if ((pes2.getBRelation(pe, fp) == BehaviorRelation.CONFLICT) &&
						(pes2.getLabel(pe).equals(pes1.getLabel(e)))) {
					return new Pair<>(pair, pe);
				}
			}
		}
		return null;
	}

	private Pair<Pair<State, Operation>, Integer> findConflictingMatchForRHide(
			Set<Pair<State, Operation>> cms, Integer f) {
		for (Pair<State, Operation> pair: cms) {
			Operation op = pair.getSecond();
			Pair<Integer,Integer> p = (Pair)op.target;
			Integer ep = p.getFirst();
			Integer fp = p.getSecond();

			BitSet epCauses = pes1.getLocalConfiguration(ep);
			epCauses.clear(ep);
			BitSet dconf = (BitSet)pes1.getPossibleExtensions(epCauses).clone();
			dconf.and(pes1.getConflictSet(ep));

			for (int ev = dconf.nextSetBit(0); ev >= 0; ev = dconf.nextSetBit(ev + 1)) {
				if (pes1.getLabel(ev).equals(pes2.getLabel(f))) {
					return new Pair<>(pair, ev);
				}
			}
		}
		return null;
	}

	private void findCausalityConcurrencyMismatches(State sigma, Set<Pair<State, Operation>> chs, Set<State> visited,
													LinkedList<Operation> stack, Map<Pair<Operation, Operation>, Pair<State, State>> pending) {
		visited.add(sigma);
		for (Operation op : descendants.get(sigma)) {
			stack.push(op);
			Pair<Integer, Integer> deltaEvents = getDeltaEvents(op);
			Integer e = deltaEvents.getFirst();
			Integer f = deltaEvents.getSecond();
			Set<Pair<State, Operation>> n_chs = new HashSet<>(chs);

			switch (op.op) {
				case LHIDE:
					Pair<State, Operation> pair = findRHide(chs, e);
					if (pair != null) {
						f = (Integer) pair.getSecond().target;

						HashSet<Pair<List<Integer>, List<Integer>>> tuplePairs = findCausalInconsistency(sigma, e, f,
								stack);

						for (Pair<List<Integer>, List<Integer>> tuplePair : tuplePairs) {
							if (tuplePair != null) {
								List<Integer> tuple = tuplePair.getFirst();

								State enablingState = findEnablingState(stack, op, pair.getSecond());
//							System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple,
//									enablingState);
								// assertCausalityConcurrencyMismatch(pair.getFirst(),
								// e, tuple.get(1), tuple.get(2), tuple.get(3));
								assertCausalityConcurrencyMismatch(enablingState, e, tuple.get(1), tuple.get(2),
										tuple.get(3));

								lhideOps.remove(op);
								rhideOps.remove(pair.getSecond());
								n_chs.remove(pair);
							} else
								pending.put(new Pair<>(op, pair.getSecond()), new Pair<>(sigma, pair.getFirst()));
						}
					} else
						n_chs.add(new Pair<>(sigma, op));
					break;
				case RHIDE:
				case RHIDENSHIFT:
					// Line 14:
					Pair<State, Operation> pairp = findLHide(chs, f);
					if (pairp != null) {
						e = (Integer) pairp.getSecond().target;

						// Line 15:
						HashSet<Pair<List<Integer>, List<Integer>>> tuplePairs = findCausalInconsistency(sigma, e, f,
								stack);
						for (Pair<List<Integer>, List<Integer>> tuplePair : tuplePairs) {
							if (tuplePair != null) {
								List<Integer> tuple = tuplePair.getFirst();

								State enablingState = findEnablingState(stack, pairp.getSecond(), op);
//							System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple,
//									enablingState);
								// assertCausalityConcurrencyMismatch(pairp.getFirst(),
								// e, tuple.get(1), tuple.get(2), tuple.get(3));
								assertCausalityConcurrencyMismatch(enablingState, e, tuple.get(1), tuple.get(2),
										tuple.get(3));

								rhideOps.remove(op);
								lhideOps.remove(pairp.getSecond());
								n_chs.remove(pairp);
							} else
								pending.put(new Pair<>(pairp.getSecond(), op), new Pair<>(pairp.getFirst(), sigma));
						}
					} else
						n_chs.add(new Pair<>(sigma, op));
					break;
				default:
					List<Pair<Operation, Operation>> toRemove = new ArrayList<>();
					for (Pair<Operation, Operation> opPair : pending.keySet()) {
						Integer ep = (Integer) opPair.getFirst().target;
						Integer fp = (Integer) opPair.getSecond().target;
						HashSet<Pair<List<Integer>, List<Integer>>> tuplePairs = findCausalInconsistency(sigma, ep, fp,
								stack);
						for (Pair<List<Integer>, List<Integer>> tuplePair : tuplePairs) {
							if (tuplePair != null) {
								List<Integer> tuple = tuplePair.getFirst();

								Pair<State, State> states = pending.get(opPair);
								State leftState = states.getFirst(), rightState = states.getSecond();

								State enablingState = findEnablingState(stack, opPair.getFirst(), opPair.getSecond());
//							System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple,
//									enablingState);

								assertCausalityConcurrencyMismatch(enablingState, tuple.get(0), tuple.get(1), tuple.get(2),
										tuple.get(3));
								toRemove.add(opPair);
								n_chs.remove(new Pair<>(leftState, opPair.getFirst()));
								chs.remove(new Pair<>(leftState, opPair.getFirst()));
								n_chs.remove(new Pair<>(rightState, opPair.getSecond()));
								chs.remove(new Pair<>(rightState, opPair.getSecond()));
							} else {
								// throw new RuntimeException("Something wrong with
								// a Causality/Concurrency mismatch" + opPair);
							}
						}
					}
					for (Pair<Operation, Operation> opPair : toRemove)
						pending.remove(opPair);

					n_chs = retainCommutative(chs, e, f);

					Map<Integer, Pair<State, Operation>> lhides = new HashMap<>();
					Map<Integer, Pair<State, Operation>> rhides = new HashMap<>();
					for (Pair<State, Operation> p : chs) {
						if (!n_chs.contains(p)) {
							Operation oper = p.getSecond();
							Integer ev = (Integer) oper.target;
							if (oper.op == Op.LHIDE)
								lhides.put(ev, p);
							else if (!pes2.getInvisibleEvents().contains(ev))
								rhides.put(ev, p);
						}
					}
					// System.out.println("Left: " + lhides);
					// System.out.println("Right: " + rhides);

					while (!lhides.isEmpty() && !rhides.isEmpty()) {
						Set<Integer> left = new HashSet<>(lhides.keySet());
						TreeMultimap<String, Integer> lmap = TreeMultimap.create();
						for (Integer ev : lhides.keySet()) {
							BitSet dpred = pes1.getDirectPredecessors(ev);
							boolean found = false;
							for (int dp = dpred.nextSetBit(0); dp >= 0; dp = dpred.nextSetBit(dp + 1))
								if (left.contains(dp)) {
									found = true;
									break;
								}
							if (!found)
								lmap.put(pes1.getLabel(ev), ev);
						}

						// System.out.println("Left cand: " + lmap);

						Set<Integer> right = new HashSet<>(rhides.keySet());
						TreeMultimap<String, Integer> rmap = TreeMultimap.create();
						for (Integer ev : rhides.keySet()) {
							Collection<Integer> dpred = pes2.getDirectPredecessors(ev);
							boolean found = false;
							for (Integer dp : dpred)
								if (right.contains(dp)) {
									found = true;
									break;
								}
							if (!found)
								rmap.put(pes2.getLabel(ev), ev);
						}
						// System.out.println("Right cand: " + rmap);

						while (!lmap.isEmpty() && !rmap.isEmpty()) {
							Entry<String, Integer> lentry = lmap.entries().iterator().next();
							Entry<String, Integer> rentry = rmap.entries().iterator().next();
							lmap.remove(lentry.getKey(), lentry.getValue());
							rmap.remove(rentry.getKey(), rentry.getValue());

							Pair<State, Operation> lpair = lhides.get(lentry.getValue());
							Pair<State, Operation> rpair = rhides.get(rentry.getValue());

							lhides.remove(lentry.getValue());
							rhides.remove(rentry.getValue());

							State enablingState = null;

							if (lpair.getFirst().c1.cardinality() <= rpair.getFirst().c1.cardinality()
									&& lpair.getFirst().c2.size() <= rpair.getFirst().c2.size())
								enablingState = lpair.getFirst();
							else
								enablingState = rpair.getFirst();

							Pair<Integer, Integer> ef = new Pair<>(lentry.getValue(), rentry.getValue());
							Set<State> set = eventSubstitutionMismatches.get(ef);
							if (set == null)
								eventSubstitutionMismatches.put(ef, set = new HashSet<>());
							set.add(enablingState);

							rhideOps.remove(rpair.getSecond());
							lhideOps.remove(lpair.getSecond());
						}
					}
					break;
			}
			if (!visited.contains(op.nextState))
				findCausalityConcurrencyMismatches(op.nextState, n_chs, visited, stack, pending);
			stack.pop();
		}
	}
	private State findEnablingState(LinkedList<Operation> stack, Operation lop, Operation rop) {
		Integer e = null;
		if (lop.op == Op.LHIDE) {
			e = (Integer) lop.target;
		}
		else {
			e = ((Pair<Integer,Integer>)lop.target).getFirst();
		}

		for (int i = 0; i < stack.size(); i++) {
			Operation op = stack.get(i);
			State state = op.nextState;
			if (op.equals(lop) || op.equals(rop)) {
				continue;
			}
			switch (op.op) {
				case MATCH:
				case MATCHNSHIFT:
					Pair<Integer, Integer> pair = (Pair)op.target;
					if (pes1.getBRelation(pair.getFirst(), e).equals(BehaviorRelation.CAUSALITY)) {
						return op.nextState;
					}
				default:
					break;
			}
		}
		return null;
	}

	private void assertCausalityConcurrencyMismatch(State enablingState, Integer e, Integer ep, Integer f, Integer fp) {
		assertElementaryMismatch(causalityConcurrencyMismatches, enablingState, e, ep, f, fp);
	}

	private void assertConflictMismatch(State enablingState, Integer e, Integer ep, Integer f, Integer fp) {
		assertElementaryMismatch(conflictMismatches, enablingState, e, ep, f, fp);
	}

	private void assertElementaryMismatch(Map<Pair<Integer, Integer>, Multimap<State, Pair<Integer, Integer>>> mismatches, State enablingState, Integer e, Integer ep, Integer f, Integer fp) {
		Pair<Integer, Integer> ef = new Pair<>(e, f);
		Multimap<State, Pair<Integer, Integer>> mmap = mismatches.get(ef);
		if (mmap == null) {
			mismatches.put(ef, mmap = HashMultimap.create());
		}
		mmap.put(enablingState, new Pair<>(ep, fp));
	}

	private Set<Pair<State, Operation>> retainCommutative(Set<Pair<State, Operation>> set, Integer e, Integer f) {
		Set<Pair<State, Operation>> result = new HashSet<Pair<State,Operation>>();
		for (Pair<State, Operation> pair: set) {
			Operation oper = pair.getSecond();
			switch (oper.op) {
				case LHIDE:
					if (areCommutative(e, (Integer)oper.target, f, null)) {
						result.add(pair);
					}
					break;
				case RHIDE:
				case RHIDENSHIFT:
					if (areCommutative(e, null, f, (Integer)oper.target)) {
						result.add(pair);
					}
					break;
				default:
					Pair<Integer, Integer> p = (Pair)oper.target;
					if (areCommutative(e, p.getFirst(), f, p.getSecond())) {
						result.add(pair);
					}
					break;
			}
		}
		return result;
	}

	private boolean areCommutative(Integer e, Integer ep, Integer f, Integer fp) {
		return ((e == null) || (ep == null) || (pes1.getBRelation(e, ep) == BehaviorRelation.CONCURRENCY)) &&
				((f == null) || (fp == null) || (pes2.getBRelation(f, fp) == BehaviorRelation.CONCURRENCY));
	}

	private String translate(NewUnfoldingPESSemantics<T> pes, Integer f) {
		if (DEBUG) {
			return String.format("%s(%d)", f != null ? pes.getLabel(f) : null, f);
		}
		else {
			return String.format("%s", f != null ? pes.getLabel(f) : null);
		}
	}

	private String translate(PESSemantics<T> pes, Integer e) {
		if (DEBUG) {
			return String.format("%s(%d)", pes.getLabel(e), e);
		}
		else {
			return String.format("%s", pes.getLabel(e));
		}
	}

	private Pair<State, Operation> findRHide(Set<Pair<State, Operation>> chs, Integer e) {
		for (Pair<State, Operation> pair: chs) {
			Operation op = pair.getSecond();
			if ((op.op == Op.RHIDE) || (op.op == Op.RHIDENSHIFT)) {
				Integer f = (Integer) op.target;
				if (pes1.getLabel(e).equals(pes2.getLabel(f))) {
					return pair;
				}
			}
		}
		return null;
	}

	private Pair<State, Operation> findLHide(Set<Pair<State, Operation>> chs, Integer f) {
		for (Pair<State, Operation> pair: chs) {
			Operation op = pair.getSecond();
			if (op.op == Op.LHIDE) {
				Integer e = (Integer) op.target;
				if (pes1.getLabel(e).equals(pes2.getLabel(f))) {
					return pair;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Pair<Integer, Integer> getDeltaEvents(Operation op) {
		if ((op.op == Op.MATCH) || (op.op == Op.MATCHNSHIFT)) {
			return (Pair<Integer,Integer>)op.target;
		}
		else if (op.op == Op.LHIDE) {
			return new Pair<Integer,Integer>((Integer)op.target, null);
		}
		else {
			return new Pair<Integer,Integer>(null, (Integer)op.target);
		}
	}

	public HashSet<Pair<List<Integer>, List<Integer>>> findCausalInconsistency(State sigma, Integer e, Integer f,
																			   LinkedList<Operation> stack) {
		// BitSet epred = pes1.getDirectPredecessors(e);
		// BitSet fpred = new BitSet();
		// for (Integer pred: pes2.getDirectPredecessors(f))
		// fpred.set(pred);

		HashSet<Pair<List<Integer>, List<Integer>>> pairDiff = new HashSet<>();

		BitSet epred = (BitSet) pes1.getStrictCausesOf(e).clone();
		BitSet fpred = (BitSet) pes2.getCausesOf(f).clone();

		List<Integer> cutoffs = new ArrayList<>();
		Integer localF = f;

		for (int i = 0; i < stack.size(); i++) {
			Operation op_i = stack.get(i);

			Pair<Integer, Integer> pair = getDeltaEvents(op_i);
			Integer ep = pair.getFirst();
			Integer fp = pair.getSecond();

			switch (op_i.op) {
				case MATCH:
					epred.clear(ep);
					fpred.clear(fp);
					if (!causallyConsistent(e, ep, localF, fp))
						pairDiff.add(new Pair<>(Arrays.asList(e, ep, localF, fp), cutoffs));
					// return new Pair<>(Arrays.asList(e,ep,localF,fp), cutoffs);

					break;
				case LHIDE:
					// epred.or(pes1.getDirectPredecessors(ep));
					epred.or(pes1.getStrictCausesOf(ep));
					epred.clear(ep);
					break;
				case RHIDE:
					// for (Integer pred: pes2.getDirectPredecessors(fp))
					// fpred.set(pred);
					fpred.or(pes2.getCausesOf(f));
					fpred.clear(fp);
					break;
				case MATCHNSHIFT:
					epred.clear(ep);

					int corrFp = pes2.getCorresponding(fp);

					for (Integer pred : pes2.getDirectPredecessors(fp))
						fpred.set(pred);
					fpred.andNot(pes2.getLocalConfiguration(corrFp));

					cutoffs.add(fp);

					localF = pes2.unshift(f, fp);
					if (!causallyConsistent(e, ep, localF, fp))
						pairDiff.add(new Pair<>(Arrays.asList(e, ep, localF, fp), cutoffs));
					// return new Pair<>(Arrays.asList(e,ep,localF,fp), cutoffs);
					break;
				case RHIDENSHIFT:
					localF = pes2.unshift(f, fp);

					int corrFpp = pes2.getCorresponding(fp);
					for (Integer pred : pes2.getDirectPredecessors(fp))
						fpred.set(pred);
					fpred.andNot(pes2.getLocalConfiguration(corrFpp));

					cutoffs.add(fp);
					break;
			}
			if (epred.isEmpty() && fpred.isEmpty())
				break;
		}
		return pairDiff;
	}

	private boolean causallyConsistent(Integer e, Integer ep, Integer f, Integer fp) {
		return pes1.getBRelation(ep, e) == pes2.getBRelation(fp, f);
	}

	private void addPSPBranchToGlobalPSP(List<Operation> opSeq) {
		State pred = root;

		for (int i = 0; i < opSeq.size(); i++) {
			Operation curr = opSeq.get(i);

			if ((curr.op == Op.RHIDE) || (curr.op == Op.RHIDENSHIFT)) {
				if (!pes2.getInvisibleEvents().contains(curr.target)) {
					rhideOps.add(curr);
				}
			}
			else if (curr.op == Op.LHIDE) {
				lhideOps.add(curr);
			}

			State state = curr.nextState;
			Map<Multiset<String>, State> map = stateSpace.get(state.c1, state.c2);
			if (map == null) {
				stateSpace.put(state.c1, state.c2, map = new HashMap<>());
			}
			if (map.containsKey(state.labels)) {
				state = map.get(state.labels);
				curr.nextState = state;
			}
			else {
				map.put(state.labels, state);
			}

			boolean found = false;
			for (Operation desc: descendants.get(pred)) {
				if (desc.op == curr.op) {
					if (curr.op == Op.MATCH || curr.op == Op.MATCHNSHIFT) {
						Pair<Integer, Integer> pair1 = (Pair)curr.target;
						Pair<Integer, Integer> pair2 = (Pair)desc.target;
						if (pair1.equals(pair2)) {
							found = true;
							break;
						}
					}
					else {
						Integer ev1 = (Integer)curr.target;
						Integer ev2 = (Integer)desc.target;
						if (ev1.equals(ev2)) {
							found = true;
							break;
						}
					}
				}
			}
			if (!found) {
				descendants.put(pred, curr);
				ancestors.put(curr.nextState, pred);
			}
			pred = state;
		}
	}

	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);

		out.println("digraph G {");

		out.println("\tnode[shape=box];");
		int i = 0;

		out.printf("\tn%d [label=\"%s\"];\n", root.hashCode(), i++);

		for (Operation op: descendants.values()) {
			out.printf("\tn%d [label=\"%s\"];\n", op.nextState.hashCode(), op.nextState);
		}

		for (Entry<State,Operation> entry: descendants.entries()) {
			out.printf("\tn%d -> n%d [label=\"%s\"];\n", entry.getKey().hashCode(), entry.getValue().nextState.hashCode(), entry.getValue());
		}

		out.println("}");

		return str.toString();
	}

	public Set<String> getStatements(){
		return this.statements;
	}

	private void verbalizeOptionalModelBehavior() {
		for (Entry<State, List<Integer>> entry:	expandedPrefix.getOptionalAcyclicIntervals().entries()) {
			List<String> interval = translate(entry.getValue());

			if (interval.size() > 0) {
				statements.add(String.format("In the model, after %s, %s is optional",
						translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst()), interval));
			}
		}
	}

}