package ee.ut.eventstr.comparison;

import com.google.common.collect.*;
import ee.ut.bpmn.BPMNReader;
import ee.ut.bpmn.replayer.BPMNReplayerML;
import ee.ut.bpmn.replayer.Pomset;
import ee.ut.bpmn.replayer.Trace;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.comparison.differences.*;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.org.processmining.framework.util.Pair;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.pm.*;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;

import ee.ut.eventstr.BehaviorRelation;

import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Op;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.State;
import org.jbpt.utils.IOUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;

public class DiffMLGraphicalVerbalizer {
	private long totalStartTime;

	private DifferencesML differences;

	// Model abstractions
	private ModelAbstractions model;
	public NewUnfoldingPESSemantics<Integer> pes2;
	private PetriNet net;
	private ExpandedPomsetPrefix<Integer> expprefix;

	// Log abstractions
	public PrimeEventStructure<Integer> logpes;
	private PESSemantics<Integer> pes1;

	private HashSet<String> commonLabels;
	private BPMNReplayerML replayer;
	private BPMNReader loader;

	private List<List<Operation>> opSeqs;
	private Set<Operation> lhideOps;
	private Set<Operation> rhideOps;
	private Map<Operation, Operation> predMatch;
	private Map<Operation, Operation> succMatch;
	private Table<BitSet, Multiset<Integer>, Map<Multiset<String>, State>> stateSpace;
	private State root;
	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;

	private Table<State, Operation, Pair<State, Operation>> confMismatches;
	private Map<Pair<Integer, Integer>, Multimap<State, Pair<Integer, Integer>>> conflictMismatches;
	private Map<Pair<Integer, Integer>, Multimap<State, Pair<Integer, Integer>>> causalityConcurrencyMismatches;
	private Map<Pair<Integer, Integer>, Set<State>> eventSubstitutionMismatches;
	private Table<State, Operation, Pair<Integer,Integer>> confTableBridge;

	private Set<Operation> operations;

	private Map<State, Operation> lastMatchMap;

//	private Set<String> statements;

	public static final boolean DEBUG = false;

	public DiffMLGraphicalVerbalizer(ModelAbstractions model, XLog log, HashSet<String> silents) throws Exception{
		this.model = model;
		this.net = model.getNet();
		silents.add("_0_");
		silents.add("_1_");

		System.out.println(model.getLabels());
        for(Transition t : net.getTransitions())
            if(!model.getLabels().contains(t.getName()))
				silents.add(t.getName());

		this.pes2 = getUnfoldingPES(model.getNet(), silents);
		this.expprefix = new ExpandedPomsetPrefix<Integer>(pes2);

		this.logpes = getLogPES(log);
		this.pes1 = new PESSemantics<Integer>(logpes);

		this.commonLabels = new HashSet<String>();
		this.commonLabels.addAll(this.model.getLabels());
		this.commonLabels.addAll(pes1.getLabels());
		this.commonLabels.removeAll(silents);

		System.out.println("Common labels = " + commonLabels);

		this.differences = new DifferencesML();
		this.loader = model.getReader();
		this.replayer = new BPMNReplayerML((Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) loader.getModel(), commonLabels,  pes2);

		this.opSeqs = new ArrayList<>();
		this.lhideOps = new HashSet<>();
		this.rhideOps = new HashSet<>();
		this.predMatch = new HashMap<>();
		this.succMatch = new HashMap<>();
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();

		this.stateSpace = HashBasedTable.create();
		this.root = new State(new BitSet(), HashMultiset.<String> create(), HashMultiset.<Integer> create());

		this.confMismatches = HashBasedTable.create();
		this.causalityConcurrencyMismatches = new HashMap<>();
		this.conflictMismatches = new HashMap<>();
		this.eventSubstitutionMismatches = new HashMap<>();
		this.confTableBridge = HashBasedTable.create();
		this.lastMatchMap = new HashMap<>();
//		this.statements = new TreeSet<>();
	}

	private NewUnfoldingPESSemantics<Integer> getUnfoldingPES(PetriNet net, HashSet<String> silents) throws Exception {
		Set<String> labels = new HashSet<>();
		for (Transition t : net.getTransitions()) {
			if (!silents.contains(t.getName()) && t.getName().length() > 0) {
				labels.add(t.getName());
			}
		}

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, BPstructBP.MODE.ESPARZA, silents);
		unfolder.computeUnfolding();

		Unfolding2PES pes = new Unfolding2PES(unfolder, labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		return pessem;
	}

	private PrimeEventStructure<Integer> getLogPES(XLog log) throws Exception {
		totalStartTime = System.nanoTime();

		AlphaRelations alphaRelations = new AlphaRelations(log);

		PORuns runs = new PORuns();

		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;

		for (XTrace trace : log) {
			PORun porun = new PORun(alphaRelations, trace);

			runs.add(porun);

			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());

			runs.add(porun);
		}

		runs.mergePrefix();

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, "LOGPES");

		return pes;
	}

	public void addPSP(List<Operation> opSeq) {
		opSeqs.add(opSeq);
	}

	public void verbalize() {
		for (List<Operation> opSeq: opSeqs)
			addPSPBranchToGlobalPSP(opSeq);
		prune();

//		System.out.println(toDot());
		Map<Pair<Operation, Operation>, Pair<State, State>> pending = new HashMap<>();
		findCausalityConcurrencyMismatches(root, new HashSet<Pair<State,Operation>>(), new HashSet<State>(), new LinkedList<Operation>(), pending);

//		if (!pending.isEmpty())
//			throw new RuntimeException("Something wrong with some causality/concurrency mismatching events: " + pending);
		findConflictMismatches(root, new HashSet<Pair<State,Operation>>(), new HashSet<Pair<State,Operation>>(), new HashSet<State>(), new LinkedList<Operation>());
		markExpandedPrefix(root, HashMultimap.<String, Operation> create(), HashMultimap.<String, Operation> create(), new HashSet<State>(), null);
		findSkipMismatches(root, new HashMap<Integer, Pair<State, Operation>>(), new HashMap<Integer, Pair<State, Operation>>(), new HashSet<State>());
		verbalizeAdditionalModelBehavior();

		for (Pair<Integer, Integer> p: causalityConcurrencyMismatches.keySet()) {
			Map<Pair<Integer, Integer>, State> map = new HashMap<>();
//			System.out.println("Analizing: " + p);
			for (Entry<State, Pair<Integer, Integer>> ctx : causalityConcurrencyMismatches.get(p).entries()) {
//				System.out.println(ctx);
				State state = ctx.getKey();
				Pair<Integer,Integer> cfpair = ctx.getValue();
				State exstate = map.get(cfpair);
				if (exstate == null || state.c1.cardinality() <= exstate.c1.cardinality() && state.c2.size() <= exstate.c2.size())
					map.put(cfpair, state);
			}

			for (Entry<Pair<Integer,Integer>, State> ctx: map.entrySet()) {
                if (DEBUG)
                    System.out.printf(">> Causality/Concurrency mismatch (%s, %s, %s, %s, %s)\n", ctx.getValue(),
                            translate(pes1, p.getFirst()), translate(pes1, ctx.getKey().getFirst()),
                            translate(pes2, p.getSecond()), translate(pes2, ctx.getKey().getSecond()));

                //// ==============================
                ////   Verbalization
                //// ==============================
                if (pes1.getBRelation(ctx.getKey().getFirst(), p.getFirst()) == BehaviorRelation.CAUSALITY){
                    String sentence = String.format("In the log, after '%s', '%s' occurs before '%s', while in the model they are concurrent",
                            translate(pes1, ((Pair<Integer, Integer>) lastMatchMap.get(ctx.getValue()).target).getFirst()),
                            translate(pes1, ctx.getKey().getFirst()), translate(pes1, p.getFirst()));

                    DifferenceML diff = print2Tasks(ctx.getKey().getSecond(), p.getSecond(), pes2, replayer, net, loader, sentence);
                    if(diff != null) {
                        diff.setType("CAUSCONC1");
                        differences.add(diff);
                    }
                }else {
                    String sentence = String.format("In the model, after '%s', '%s' occurs before '%s', while in the log they are concurrent",
                            translate(pes2, ((Pair<Integer, Integer>) lastMatchMap.get(ctx.getValue()).target).getSecond()),
                            translate(pes2, ctx.getKey().getSecond()), translate(pes2, p.getSecond()));

                    DifferenceML diff = print2Tasks(ctx.getKey().getSecond(), p.getSecond(), pes2, replayer, net, loader, sentence);
                    if(diff != null) {
                        diff.setType("CAUSCONC2");
                        differences.add(diff);
                    }
                }
			}
		}

		for (Pair<Integer, Integer> p: eventSubstitutionMismatches.keySet()) {
			State enablingState = null;
			for (State state : eventSubstitutionMismatches.get(p)) {
				if (enablingState == null || state.c1.cardinality() <= enablingState.c1.cardinality() && state.c2.size() <= enablingState.c2.size())
					enablingState = state;
			}
			if (DEBUG)
				System.out.printf(">> Event substition (%s, %s, %s)\n", enablingState,
						translate(pes1, p.getFirst()), translate(pes2, p.getSecond()));
			String sentence = String.format("In the log, after '%s', '%s' is substituted by '%s'",
					translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(enablingState).target).getFirst()),
					translate(pes1, p.getFirst()), translate(pes2, p.getSecond()));

            List<Integer> singleton= new LinkedList<>();
            singleton.add(p.getSecond());

            DifferenceML diff = printTasksGO(singleton, pes2, replayer, net, loader, sentence);
            if(diff != null) {
                diff.setType("TASKSUB");

                // Add new task
                List<String> newTasks = new ArrayList<>();
                newTasks.add(translate(pes1, p.getFirst()));
                diff.setNewTasks(newTasks);

                differences.add(diff);
            }
		}

		for (Pair<Integer, Integer> p: conflictMismatches.keySet()) {
			Map<Pair<Integer, Integer>, State> map = new HashMap<>();
			for (Entry<State, Pair<Integer, Integer>> ctx : conflictMismatches.get(p).entries()) {
				State state = ctx.getKey();
				Pair<Integer,Integer> cfpair = ctx.getValue();
				State exstate = map.get(cfpair);
				if (exstate == null || state.c1.cardinality() <= exstate.c1.cardinality() && state.c2.size() <= exstate.c2.size())
					map.put(cfpair, state);
			}

			for (Entry<Pair<Integer,Integer>, State> ctx: map.entrySet()) {
                if (DEBUG)
                    System.out.printf(">> Conflict mismatch (%s, %s, %s, %s, %s)\n", ctx.getValue(),
                            translate(pes1, p.getFirst()), translate(pes1, ctx.getKey().getFirst()),
                            translate(pes2, p.getSecond()), translate(pes2, ctx.getKey().getSecond()));

                if (pes1.getBRelation(p.getFirst(), ctx.getKey().getFirst()) == BehaviorRelation.CONCURRENCY) {
                    String sentence = String.format("In the log, after '%s', '%s' and '%s' are concurrent, while in the model they are mutually exclusive",
							//translate(pes1, p.getFirst()),
							translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getFirst()),
							translate(pes1, ctx.getKey().getFirst()),
							translate(pes1, p.getFirst()));

                    DifferenceML diff = print2Tasks(p.getSecond(), ctx.getKey().getSecond(), pes2, replayer, net, loader, sentence);
                    if (diff != null) {
                        diff.setType("CONFLICT1");
                        differences.add(diff);
                    }

                } else if (pes2.getBRelation(p.getSecond(), ctx.getKey().getSecond()) == BehaviorRelation.CONCURRENCY) {
                    String sentence = String.format("In the model, after '%s', '%s' and '%s' are concurrent, while in the log they are mutually exclusive",
                            translate(pes2, ((Pair<Integer, Integer>) lastMatchMap.get(ctx.getValue()).target).getSecond()),
                            translate(pes2, ctx.getKey().getSecond()), translate(pes2, p.getSecond()));

                    DifferenceML diff = print2Tasks(p.getSecond(), ctx.getKey().getSecond(), pes2, replayer, net, loader, sentence);
                    if (diff != null) {
                        diff.setType("CONFLICT2");
                        differences.add(diff);
                    }

                } else if (pes1.getBRelation(ctx.getKey().getFirst(), p.getFirst()) == BehaviorRelation.CAUSALITY){
                    String sentence = String.format("In the log, after '%s', '%s' occurs before '%s', while in the model they are mutually exclusive",
                            translate(pes1, p.getFirst()), translate(pes1, ctx.getKey().getFirst()),
                            translate(pes1, ctx.getKey().getFirst()), translate(pes1, p.getFirst()));

                    DifferenceML diff = print2Tasks(p.getSecond(), ctx.getKey().getSecond(), pes2, replayer, net, loader, sentence);
                    if (diff != null) {
                        diff.setType("CONFLICT3");
                        differences.add(diff);
                    }

                }else {
                    String sentence = String.format("In the log, after '%s', '%s' occurs before '%s', while in the model they are mutually exclusive",
                            translate(pes2, ((Pair<Integer, Integer>) lastMatchMap.get(ctx.getValue()).target).getSecond()),
                            translate(pes2, ctx.getKey().getSecond()), translate(pes2, p.getSecond()));

                    DifferenceML diff = print2Tasks(p.getSecond(), ctx.getKey().getSecond(), pes2, replayer, net, loader, sentence);
                    if (diff != null) {
                        diff.setType("CONFLICT4");
                        differences.add(diff);
                    }
                }
			}
		}

//		System.out.println("================");
//		for (String stm: statements)
//			System.out.println(stm);

        System.out.println(DifferenceML.toJSON(differences));
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
			if (root.equals(curr)) continue;

			State pred = ancestors.get(curr).iterator().next();
			if (pred != null) {
				for (Operation op: descendants.get(pred))
					if (op.nextState.equals(curr)) {
						operations.add(op);
						break;
					}
				if (!visited.contains(pred) && !open.contains(pred))
					open.push(pred);
			}
		}


		for (State s: visited) {
			Set<Operation> toDelete = new HashSet<>();
			for (Operation o: descendants.get(s))
				if (!operations.contains(o))
					toDelete.add(o);
			for (Operation o: toDelete)
				descendants.remove(s, o);
		}

		lhideOps.retainAll(operations);
		rhideOps.retainAll(operations);
	}

	public DifferencesML getDifferences(){
		return differences;
	}

	private void verbalizeAdditionalModelBehavior() {
		for (Entry<State, List<Integer>> entry:	expprefix.getAdditionalAcyclicIntervals().entries()) {
			if (DEBUG)
				System.out.printf("In the log, %s do(es) not occur after %s\n", translate(entry.getValue()), entry.getKey());
			String sentence = String.format("In the log, %s do(es) not occur after '%s'",
					translate(entry.getValue()), translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst()));

			DifferenceML diff = printTasksGO(entry.getValue(), pes2, replayer, net, loader, sentence);
            if(diff != null) {
                diff.setType("UNOBSACYCLICINTER");
                differences.add(diff);
            }
		}
		for (Entry<State, Multiset<Integer>> entry:	expprefix.getAdditionalCyclicIntervals().entries()) {
			if (DEBUG)
				System.out.printf("In the log, the cycle involving %s does not occur after %s\n", translate(entry.getValue()), entry.getKey());
			String sentence = String.format("In the log, the cycle involving %s does not occur after '%s'",
					translate(entry.getValue()), translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst()));

			DifferenceML diff = printTasksHL(new ArrayList<Integer>(entry.getValue()), pes2, replayer, net, loader, sentence);
            if(diff != null) {
                diff.setType("UNOBSCYCLICINTER");
                differences.add(diff);
            }
		}
	}

	private Set<String> translate(Collection<Integer> multiset) {
		Set<String> set = new HashSet<>();
		for (Integer ev: multiset)
			if (!pes2.getInvisibleEvents().contains(ev))
				set.add(translate(pes2, ev));
		return set;
	}

	private void markExpandedPrefix(State curr, Multimap<String, Operation> lpending,
									Multimap<String, Operation> rpending, Set<State> visited, Operation lastMatch) {
		visited.add(curr);
		lastMatchMap.put(curr, lastMatch);

		for (Operation op: descendants.get(curr)) {
			expprefix.mark(op.nextState, op);

			if (lhideOps.contains(op)) {
				if (curr.labels.contains(op.label)) {
					if (DEBUG)
						System.out.printf("In the log, '%s' is repeated after '%s'\n", translate(pes1, (Integer) op.target), curr);
					String sentence = String.format("In the log, '%s' is repeated after '%s'",
							translate(pes1, (Integer) op.target),
							translate(pes1, ((Pair<Integer, Integer>)lastMatch.target).getFirst()));

                    List<Integer> singleton = new LinkedList<>();
                    singleton.add(((Pair<Integer, Integer>)lastMatch.target).getSecond());

                    DifferenceML diff = printTasksHL(singleton, pes2, replayer, net, loader, sentence);
                    if(diff != null) {
                        diff.setType("UNMREPETITION");
                        differences.add(diff);
                    }
				} else
					lpending.put(op.label, op);
			} else if (rhideOps.contains(op)) {
				if (curr.labels.contains(op.label)) {
					if (DEBUG)
						System.out.printf("In the model, '%s' is repeated after '%s'\n", translate(pes2, (Integer) op.target), curr);

                    String sentence = String.format("In the model, '%s' is repeated after '%s'",
                            translate(pes2, (Integer) op.target),
                            translate(pes2, ((Pair<Integer, Integer>)lastMatch.target).getSecond()));

                    List<Integer> singleton = new LinkedList<>();
                    singleton.add((Integer) op.target);

                    DifferenceML diff = printTasksHL(singleton, pes2, replayer, net, loader, sentence);
                    if(diff != null) {
                        diff.setType("UNOBSCYCLICINTER");
                        differences.add(diff);
                    }
				} else
					rpending.put(op.label, op);
			}

			if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT) {
				for (Operation h: rpending.values())
					if (!succMatch.containsKey(h))
						succMatch.put(h, op);
				for (Operation h: lpending.values())
					if (!succMatch.containsKey(h))
						succMatch.put(h, op);

				if (!visited.contains(op.nextState))
					markExpandedPrefix(op.nextState, lpending, rpending, visited, op);

			} else {
				if (!predMatch.containsKey(op))
					predMatch.put(op, lastMatch);
				if (!visited.contains(op.nextState))
					markExpandedPrefix(op.nextState, lpending, rpending, visited, lastMatch);
			}

			if (lpending.containsEntry(op.label, op)) {
				Collection<Operation> right = rpending.get(op.label);

				if (lpending.get(op.label).size() == 1 && right.size() == 1) {
					Operation pred = predMatch.get(right.iterator().next());
					Operation succ = succMatch.get(op);
					if (DEBUG)
						System.out.printf("In the log, \"%s\" occurs after \"%s\" instead of \"%s\"\n", translate(pes1, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));

					String sentence = String.format("In the log, '%s' occurs after '%s' instead of '%s'",
							translate(pes1, (Integer) op.target),
							pred == null || pred.label.equals("_0_") ? "<start state>" :
									translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()),
                                    // translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()), // Original
							succ == null || succ.label.equals("_1_") ? "<end state>" :
									translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond()));


                    List<Integer> singleton = new LinkedList<>();
                    singleton.add(((Pair<Integer, Integer>)lastMatch.target).getSecond());

                    List<Integer> singletonPast = new LinkedList<>();
                    singletonPast.add(((Pair<Integer, Integer>)pred.target).getSecond());

                    DifferenceML diff = printTasksGOHL(singletonPast, singleton, pes2, replayer, net, loader, sentence);
                    if(diff != null) {
                        diff.setType("TASKRELOC1");

                        // Add new task
                        List<String> newTasks = new ArrayList<>();
                        newTasks.add(translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()));
                        diff.setNewTasks(newTasks);

                        differences.add(diff);
                    }

					rpending.removeAll(op.label);
				} else {
					Operation pred = predMatch.get(op);
					Operation succ = succMatch.get(op);
					if (DEBUG)
						System.out.printf("In the log, \"%s\" occurs after \"%s\" and before \"%s\"\n", translate(pes1, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));

					String sentence = String.format("In the log, '%s' occurs after '%s' and before '%s'",
							translate(pes1, (Integer) op.target),
							pred == null || pred.label.equals("_0_") ? "<start state>" :
									translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()),
							succ == null || succ.label.equals("_1_") ? "<end state>" :
									translate(pes1, ((Pair<Integer, Integer>)succ.target).getFirst()));

                    List<Integer> singletonPred = new LinkedList<>();
                    singletonPred.add(((Pair<Integer, Integer>)pred.target).getSecond());

                    DifferenceML diff = addTasks(singletonPred, pes2, replayer, net, loader, sentence);
                    if(diff != null) {
                        diff.setType("TASKABS");
                        List<String> newTaks = new ArrayList<>();
                        newTaks.add(translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()));
                        newTaks.add(translate(pes1, ((Pair<Integer, Integer>)succ.target).getFirst()));
                        diff.setNewTasks(newTaks);

                        differences.add(diff);
                    }
                }
				lpending.remove(op.label, op);
			} else if (rpending.containsEntry(op.label, op)) {
				Collection<Operation> left = lpending.get(op.label);

				if (rpending.get(op.label).size() == 1 && left.size() == 1) {
					Operation pred = predMatch.get(left.iterator().next());
					Operation succ = succMatch.get(op);

					if (DEBUG)
						System.out.printf("In the model, \"%s\" occurs after \"%s\" instead of \"%s\"\n", translate(pes2, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));
					String sentence = String.format("In the model, '%s' occurs after '%s' instead of '%s'",
							translate(pes2, (Integer) op.target),
							pred == null || pred.label.equals("_0_") ? "<start state>" :
									translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()),
							succ == null || succ.label.equals("_1_") ? "<end state>" :
									translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond()));

                    List<Integer> singleton = new LinkedList<>();
                    singleton.add((Integer) op.target);

                    List<Integer> singletonPast = new LinkedList<>();
                    singletonPast.add(((Pair<Integer, Integer>)pred.target).getSecond());

                    DifferenceML diff = printTasksGOHL(singletonPast, singleton, pes2, replayer, net, loader, sentence);
                    if(diff != null) {
                        diff.setType("TASKRELOC2");

                        // Add new task
                        List<String> newTasks = new ArrayList<>();
                        newTasks.add(translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()));
                        diff.setNewTasks(newTasks);

                        differences.add(diff);
                    }

                    lpending.removeAll(op.label);
				} else {
					Operation pred = predMatch.get(op);
					Operation succ = succMatch.get(op);
					if (DEBUG)
						System.out.printf("In the model, \"%s\" occurs after \"%s\" and before \"%s\"\n", translate(pes2, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));

					String sentence = String.format("In the model, '%s' occurs after '%s' and before '%s'",
							translate(pes2, (Integer) op.target),
							pred == null || pred.label.equals("_0_") ? "<start state>" :
									translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()),
							succ == null || succ.label.equals("_1_") ? "<end state>" :
									translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond()));

                    List<Integer> targets = new LinkedList<>();
                    targets.add((Integer) op.target);
                    DifferenceML diff = printTasksGO(targets, pes2, replayer, net, loader, sentence);

                    if(diff != null) {
                        diff.setType("TASKABS");
//                        List<String> newTaks = new ArrayList<>();
//                        newTaks.add(translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()));
//                        newTaks.add(translate(pes1, ((Pair<Integer, Integer>)succ.target).getFirst()));
//                        diff.setNewTasks(newTaks);
                        differences.add(diff);
                    }
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
			} else {
				Integer target = ((Pair<Integer,Integer>)pair.getSecond().target).getSecond();
				rhides.put((Integer)op.target, new Pair<>(target, op));
			}
		}

		for (Operation op: descendants.get(curr)) {
			if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT) {
				Pair<Integer,Integer> deltaEvents = (Pair)op.target;
				Integer e = deltaEvents.getFirst();
				Integer f = deltaEvents.getSecond();

				if (lhides.containsKey(e)) {
					Integer target = lhides.get(e).getFirst();
					ltargets.put(target, new Pair<>(curr, lhides.get(e).getSecond()));
					if (!visited.contains(op.nextState))
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
					ltargets.remove(target);
				} else if (rhides.containsKey(f)) {
					Integer target = rhides.get(f).getFirst();
					rtargets.put(target, new Pair<>(curr, rhides.get(f).getSecond()));
					if (!visited.contains(op.nextState))
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
					rtargets.remove(target);
				} else {
					if (ltargets.containsKey(e)) {
						if (DEBUG)
							System.out.printf("In the model, after '%s', '%s' is optional\n",
									translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(ltargets.get(e).getFirst()).target).getFirst()),
									translate(pes1, (Integer)ltargets.get(e).getSecond().target));
						String sentence = String.format("In the model, after '%s', '%s' is optional",
								translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(ltargets.get(e).getFirst()).target).getFirst()),
								translate(pes1, (Integer)ltargets.get(e).getSecond().target));

						// Remove the corresponding conflict mismatch
						conflictMismatches.remove(confTableBridge.get(ltargets.get(e).getFirst(), ltargets.get(e).getSecond()));
						ltargets.remove(e);

                        List<Integer> singleton = new LinkedList<>();
                        singleton.add((Integer)rtargets.get(f).getSecond().target);

                        DifferenceML diff = printTasksHL(singleton, pes2, replayer, net, loader, sentence);
                        if(diff != null) {
                            diff.setType("TASKSKIP2");
                            differences.add(diff);
                        }

					} else if (rtargets.containsKey(f)) {
						if (DEBUG)
							System.out.printf("In the log, after '%s', '%s' is optional\n",
									translate(pes2, ((Pair<Integer,Integer>)lastMatchMap.get(rtargets.get(f).getFirst()).target).getSecond()),
									translate(pes2, (Integer)rtargets.get(f).getSecond().target));
						String sentence = String.format("In the log, after '%s', '%s' is optional",
								translate(pes2, ((Pair<Integer,Integer>)lastMatchMap.get(rtargets.get(f).getFirst()).target).getSecond()),
								translate(pes2, (Integer)rtargets.get(f).getSecond().target));

                        List<Integer> singleton = new LinkedList<>();
                        singleton.add((Integer)rtargets.get(f).getSecond().target);

                        DifferenceML diff = printTasksHL(singleton, pes2, replayer, net, loader, sentence);
                        if(diff != null) {
                            diff.setType("TASKSKIP1");
                            differences.add(diff);
                        }

						// Remove the corresponding conflict mismatch
						conflictMismatches.remove(confTableBridge.get(rtargets.get(f).getFirst(), rtargets.get(f).getSecond()));
						rtargets.remove(f);
					}
					if (!visited.contains(op.nextState))
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
				}
			} else if (!visited.contains(op.nextState))
				findSkipMismatches(op.nextState, ltargets, rtargets, visited);
		}
	}

	private void findConflictMismatches(State sigma, Set<Pair<State, Operation>> cms, Set<Pair<State, Operation>> chs, Set<State> visited, LinkedList<Operation> stack) {
		visited.add(sigma);
		for (Operation op: descendants.get(sigma)) {
			stack.push(op);
			Pair<Integer,Integer> deltaEvents = getDeltaEvents(op);
			Integer e = deltaEvents.getFirst();
			Integer f = deltaEvents.getSecond();

			if ((op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT) && pes2.getInvisibleEvents().contains(f)) {
				if (!visited.contains(op.nextState)) {
					findConflictMismatches(op.nextState, cms, chs, visited, stack);
					stack.pop();
				}
				continue;
			}

			Integer ep = null, fp = null;
			Set<Pair<State, Operation>> n_cms = retainCommutative(cms, e, f);
			Set<Pair<State, Operation>> n_chs = retainCommutative(chs, e, f);
//			System.out.println("**" + op);
			switch (op.op) {
				case LHIDE:
					Pair<Pair<State, Operation>, Integer> tuple = findConflictingMatchForLHide(cms, e);
					System.out.println("the tuple is " + tuple);
					if (tuple != null) {
						Pair<State, Operation> pair = tuple.getFirst();
						Pair<Integer, Integer> p = (Pair)pair.getSecond().target;
						f = tuple.getSecond();
						ep = p.getFirst();
						fp = p.getSecond();

						State enablingState = findEnablingState(stack, op, pair.getSecond());
						System.out.printf("Conflict related mismatch %s enabling state: %s\n", tuple, enablingState);

						assertConflictMismatch(enablingState, e, ep, f, fp);

						lhideOps.remove(op);
						n_cms.remove(pair);
					} else
						n_chs.add(new Pair<>(sigma, op));
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
						System.out.printf("Conflict related mismatch %s enabling state: %s\n", tuplep, enablingState);

						assertConflictMismatch(enablingState, e, ep, f, fp);

						rhideOps.remove(op);
						n_cms.remove(pair);
					} else
						n_chs.add(new Pair<>(sigma, op));
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
						if (hideOp.op == Op.LHIDE)
							enablingState = findEnablingState(stack, hideOp, op);
						else
							enablingState = findEnablingState(stack, op, hideOp);

						System.out.printf("Conflict related mismatch %s enabling state: %s\n", tupleq, enablingState);

						// Here, (e,f) refer to the matched events. That is why I changed the order of the parameters
						assertConflictMismatch(enablingState, ep, e, fp, f);

						if (hideOp.op == Op.LHIDE)
							lhideOps.remove(hideOp);
						else
							lhideOps.remove(hideOp);

						////// ===================================
						confMismatches.put(pair.getFirst(), pair.getSecond(), new Pair<>(sigma, op));
						confTableBridge.put(pair.getFirst(), pair.getSecond(), new Pair<Integer,Integer>(ep,fp));
						////// ===================================

						n_chs.remove(pair);
					} else
						n_cms.add(new Pair<>(sigma, op));
			}
			if (!visited.contains(op.nextState))
				findConflictMismatches(op.nextState, n_cms, n_chs, visited, stack);
			stack.pop();
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
				for (int ev = _fCauses.nextSetBit(0); ev >= 0; ev = _fCauses.nextSetBit(ev + 1))
					fCauses.add(ev);

				for (Integer fp: pes2.getPossibleExtensions(fCauses)) {
					// Immediate conflict: Event enabled by the same causes of f' which is in conflict with f'
					if (pes2.getBRelation(fp, f) == BehaviorRelation.CONFLICT &&
							pes2.getLabel(fp).equals(pes1.getLabel(ep)))
						return new Pair<>(pair, new Pair<>(ep, fp));
				}

			} else {
				Integer fp = (Integer)op.target;

				BitSet eCauses = pes1.getLocalConfiguration(e);
				eCauses.clear(e);
				BitSet dconf = (BitSet)pes1.getPossibleExtensions(eCauses).clone();
				dconf.and(pes1.getConflictSet(e));

				for (int ep = dconf.nextSetBit(0); ep >= 0; ep = dconf.nextSetBit(ep + 1))
					if (pes1.getLabel(ep).equals(pes2.getLabel(fp)))
						return new Pair<>(pair, new Pair<>(ep, fp));
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
			for (int ev = _fpCauses.nextSetBit(0); ev >= 0; ev = _fpCauses.nextSetBit(ev + 1))
				fpCauses.add(ev);

			for (Integer pe: pes2.getPossibleExtensions(fpCauses)) {
				// Immediate conflict: Event enabled by the same causes of f' which is in conflict with f'
				if (pes2.getBRelation(pe, fp) == BehaviorRelation.CONFLICT &&
						pes2.getLabel(pe).equals(pes1.getLabel(e)))
					return new Pair<>(pair, pe);
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

			for (int ev = dconf.nextSetBit(0); ev >= 0; ev = dconf.nextSetBit(ev + 1))
				if (pes1.getLabel(ev).equals(pes2.getLabel(f)))
					return new Pair<>(pair, ev);
		}
		return null;
	}

	private void findCausalityConcurrencyMismatches(State sigma, Set<Pair<State, Operation>> chs, Set<State> visited, LinkedList<Operation> stack, Map<Pair<Operation, Operation>, Pair<State, State>> pending) {
		visited.add(sigma);
		for (Operation op: descendants.get(sigma)) {
			stack.push(op);
			Pair<Integer,Integer> deltaEvents = getDeltaEvents(op);
			Integer e = deltaEvents.getFirst();
			Integer f = deltaEvents.getSecond();
			Set<Pair<State, Operation>> n_chs = new HashSet<>(chs);

			switch (op.op) {
				case LHIDE:
					Pair<State, Operation> pair = findRHide(chs, e);
					if (pair != null) {
						f = (Integer)pair.getSecond().target;

						Pair<List<Integer>,List<Integer>> tuplePair = findCausalInconsistency(sigma, e, f, stack);

						if (tuplePair != null) {
							List<Integer> tuple = tuplePair.getFirst();

							State enablingState = findEnablingState(stack, op, pair.getSecond());
							System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple, enablingState);
//						assertCausalityConcurrencyMismatch(pair.getFirst(), e, tuple.get(1), tuple.get(2), tuple.get(3));
							assertCausalityConcurrencyMismatch(enablingState, e, tuple.get(1), tuple.get(2), tuple.get(3));

							lhideOps.remove(op);
							rhideOps.remove(pair.getSecond());
							n_chs.remove(pair);
						} else
							pending.put(new Pair<>(op, pair.getSecond()), new Pair<>(sigma, pair.getFirst()));
					} else
						n_chs.add(new Pair<>(sigma, op));
					break;
				case RHIDE:
				case RHIDENSHIFT:
					// Line 14:
					Pair<State, Operation> pairp = findLHide(chs, f);
					if (pairp != null) {
						e = (Integer)pairp.getSecond().target;

						// Line 15:
						Pair<List<Integer>,List<Integer>> tuplePair = findCausalInconsistency(sigma, e, f, stack);

						if (tuplePair != null) {
							List<Integer> tuple = tuplePair.getFirst();

							State enablingState = findEnablingState(stack, pairp.getSecond(), op);
							System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple, enablingState);
//						assertCausalityConcurrencyMismatch(pairp.getFirst(), e, tuple.get(1), tuple.get(2), tuple.get(3));
							assertCausalityConcurrencyMismatch(enablingState, e, tuple.get(1), tuple.get(2), tuple.get(3));

							rhideOps.remove(op);
							lhideOps.remove(pairp.getSecond());
							n_chs.remove(pairp);
						} else
							pending.put(new Pair<>(pairp.getSecond(), op), new Pair<>(pairp.getFirst(), sigma));
					} else
						n_chs.add(new Pair<>(sigma, op));
					break;
				default:
					List<Pair<Operation, Operation>> toRemove = new ArrayList<>();
					for (Pair<Operation, Operation> opPair: pending.keySet()) {
						Integer ep = (Integer)opPair.getFirst().target;
						Integer fp = (Integer)opPair.getSecond().target;
						Pair<List<Integer>,List<Integer>> tuplePair = findCausalInconsistency(sigma, ep, fp, stack);
						if (tuplePair != null) {
							List<Integer> tuple = tuplePair.getFirst();

							Pair<State, State> states = pending.get(opPair);
							State leftState = states.getFirst(), rightState = states.getSecond();

							State enablingState = findEnablingState(stack, opPair.getFirst(), opPair.getSecond());
							System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple, enablingState);

							assertCausalityConcurrencyMismatch(enablingState, tuple.get(0), tuple.get(1), tuple.get(2), tuple.get(3));
							toRemove.add(opPair);
							n_chs.remove(new Pair<>(leftState, opPair.getFirst()));
							chs.remove(new Pair<>(leftState, opPair.getFirst()));
							n_chs.remove(new Pair<>(rightState, opPair.getSecond()));
							chs.remove(new Pair<>(rightState, opPair.getSecond()));
						} else {
//						throw new RuntimeException("Something wrong with a Causality/Concurrency mismatch" + opPair);
						}
					}
					for (Pair<Operation, Operation> opPair: toRemove)
						pending.remove(opPair);

					n_chs = retainCommutative(chs, e, f);

					Map<Integer, Pair<State, Operation>> lhides = new HashMap<>();
					Map<Integer, Pair<State, Operation>> rhides = new HashMap<>();
					for (Pair<State, Operation> p: chs) {
						if (!n_chs.contains(p)) {
							Operation oper = p.getSecond();
							Integer ev = (Integer)oper.target;
							if (oper.op == Op.LHIDE)
								lhides.put(ev, p);
							else if (!pes2.getInvisibleEvents().contains(ev))
								rhides.put(ev, p);
						}
					}
//				System.out.println("Left: " + lhides);
//				System.out.println("Right: " + rhides);

					while (!lhides.isEmpty() && !rhides.isEmpty()) {
						Set<Integer> left = new HashSet<>(lhides.keySet());
						TreeMultimap<String, Integer> lmap = TreeMultimap.create();
						for (Integer ev: lhides.keySet()) {
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

//					System.out.println("Left cand: " + lmap);

						Set<Integer> right = new HashSet<>(rhides.keySet());
						TreeMultimap<String, Integer> rmap = TreeMultimap.create();
						for (Integer ev: rhides.keySet()) {
							Collection<Integer> dpred = pes2.getDirectPredecessors(ev);
							boolean found = false;
							for (Integer dp: dpred)
								if (right.contains(dp)) {
									found = true;
									break;
								}
							if (!found)
								rmap.put(pes2.getLabel(ev), ev);
						}
//					System.out.println("Right cand: " + rmap);

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

							if (lpair.getFirst().c1.cardinality() <= rpair.getFirst().c1.cardinality() &&
									lpair.getFirst().c2.size() <= rpair.getFirst().c2.size())
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
		if (lop.op == Op.LHIDE)
			e = (Integer) lop.target;
		else
			e = ((Pair<Integer,Integer>)lop.target).getFirst();

		for (int i = 0; i < stack.size(); i++) {
			Operation op = stack.get(i);
			State state = op.nextState;
			if (op.equals(lop) || op.equals(rop))
				continue;
			switch (op.op) {
				case MATCH:
				case MATCHNSHIFT:
					Pair<Integer, Integer> pair = (Pair)op.target;
					if (pes1.getBRelation(pair.getFirst(), e).equals(BehaviorRelation.CAUSALITY))
						return op.nextState;
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
		if (mmap == null)
			mismatches.put(ef, mmap = HashMultimap.create());
		mmap.put(enablingState, new Pair<>(ep, fp));
	}

	private Set<Pair<State, Operation>> retainCommutative(Set<Pair<State, Operation>> set, Integer e, Integer f) {
		Set<Pair<State, Operation>> result = new HashSet<Pair<State,Operation>>();
		for (Pair<State, Operation> pair: set) {
			Operation oper = pair.getSecond();
			switch (oper.op) {
				case LHIDE:
					if (areCommutative(e, (Integer)oper.target, f, null))
						result.add(pair);
					break;
				case RHIDE:
				case RHIDENSHIFT:
					if (areCommutative(e, null, f, (Integer)oper.target))
						result.add(pair);
					break;
				default:
					Pair<Integer, Integer> p = (Pair)oper.target;
					if (areCommutative(e, p.getFirst(), f, p.getSecond()))
						result.add(pair);
					break;
			}
		}
		return result;
	}

	private boolean areCommutative(Integer e, Integer ep, Integer f, Integer fp) {
		return (e == null || ep == null || pes1.getBRelation(e, ep) == BehaviorRelation.CONCURRENCY) &&
				(f == null || fp == null || pes2.getBRelation(f, fp) == BehaviorRelation.CONCURRENCY);
	}
	private String translate(NewUnfoldingPESSemantics<Integer> pes, Integer f) {
		if (DEBUG)
			return String.format("%s(%d)", f != null ? pes.getLabel(f) : null, f);
		else
			return String.format("%s", f != null ? pes.getLabel(f) : null);
	}
	private String translate(PESSemantics<Integer> pes, Integer e) {
		if (DEBUG)
			return String.format("%s(%d)", pes.getLabel(e), e);
		else
			return String.format("%s", pes.getLabel(e));
	}

	private Pair<State, Operation> findRHide(Set<Pair<State, Operation>> chs, Integer e) {
		for (Pair<State, Operation> pair: chs) {
			Operation op = pair.getSecond();
			if (op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT) {
				Integer f = (Integer) op.target;
				if (pes1.getLabel(e).equals(pes2.getLabel(f)))
					return pair;
			}
		}
		return null;
	}

	private Pair<State, Operation> findLHide(Set<Pair<State, Operation>> chs, Integer f) {
		for (Pair<State, Operation> pair: chs) {
			Operation op = pair.getSecond();
			if (op.op == Op.LHIDE) {
				Integer e = (Integer) op.target;
				if (pes1.getLabel(e).equals(pes2.getLabel(f)))
					return pair;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Pair<Integer, Integer> getDeltaEvents(Operation op) {
		if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT)
			return (Pair<Integer,Integer>)op.target;
		else if (op.op == Op.LHIDE)
			return new Pair<Integer,Integer>((Integer)op.target, null);
		else
			return new Pair<Integer,Integer>(null, (Integer)op.target);
	}

	public Pair<List<Integer>, List<Integer>> findCausalInconsistency(State sigma, Integer e, Integer f, LinkedList<Operation> stack) {
		BitSet epred = pes1.getDirectPredecessors(e);
		BitSet fpred = new BitSet();
		for (Integer pred: pes2.getDirectPredecessors(f))
			fpred.set(pred);
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
						return new Pair<>(Arrays.asList(e,ep,localF,fp), cutoffs);
					break;
				case LHIDE:
					epred.or(pes1.getDirectPredecessors(ep));
					epred.clear(ep);
					break;
				case RHIDE:
					for (Integer pred: pes2.getDirectPredecessors(fp))
						fpred.set(pred);
					fpred.clear(fp);
					break;
				case MATCHNSHIFT:
					epred.clear(ep);

					int corrFp = pes2.getCorresponding(fp);

					for (Integer pred: pes2.getDirectPredecessors(fp))
						fpred.set(pred);
					fpred.andNot(pes2.getLocalConfiguration(corrFp));

					cutoffs.add(fp);

					localF = pes2.unshift(f, fp);
					if (!causallyConsistent(e, ep, localF, fp))
						return new Pair<>(Arrays.asList(e,ep,localF,fp), cutoffs);
					break;
				case RHIDENSHIFT:
					localF = pes2.unshift(f, fp);

					int corrFpp = pes2.getCorresponding(fp);
					for (Integer pred: pes2.getDirectPredecessors(fp))
						fpred.set(pred);
					fpred.andNot(pes2.getLocalConfiguration(corrFpp));

					cutoffs.add(fp);
					break;
			}
			if (epred.isEmpty() && fpred.isEmpty())
				break;
		}
		return null;
	}

	private boolean causallyConsistent(Integer e, Integer ep, Integer f, Integer fp) {
		return pes1.getBRelation(ep, e) == pes2.getBRelation(fp, f);
	}

	private void addPSPBranchToGlobalPSP(List<Operation> opSeq) {
		State pred = root;

		for (int i = 0; i < opSeq.size(); i++) {
			Operation curr = opSeq.get(i);

			if (curr.op == Op.RHIDE || curr.op == Op.RHIDENSHIFT)
				if (!pes2.getInvisibleEvents().contains(curr.target))
					rhideOps.add(curr);
				else if (curr.op == Op.LHIDE)
					lhideOps.add(curr);

			State state = curr.nextState;
			Map<Multiset<String>, State> map = stateSpace.get(state.c1, state.c2);
			if (map == null)
				stateSpace.put(state.c1, state.c2, map = new HashMap<>());
			if (map.containsKey(state.labels)) {
				state = map.get(state.labels);
				curr.nextState = state;
			} else
				map.put(state.labels, state);

			boolean found = false;
			for (Operation desc: descendants.get(pred))
				if (desc.op == curr.op) {
					if (curr.op == Op.MATCH || curr.op == Op.MATCHNSHIFT) {
						Pair<Integer, Integer> pair1 = (Pair)curr.target;
						Pair<Integer, Integer> pair2 = (Pair)desc.target;
						if (pair1.equals(pair2)) {
							found = true;
							break;
						}
					} else {
						Integer ev1 = (Integer)curr.target;
						Integer ev2 = (Integer)desc.target;
						if (ev1.equals(ev2)) {
							found = true;
							break;
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

		for (Operation op: descendants.values())
			out.printf("\tn%d [label=\"%s\"];\n", op.nextState.hashCode(), op.nextState);

		for (Entry<State,Operation> entry: descendants.entries())
			out.printf("\tn%d -> n%d [label=\"%s\"];\n", entry.getKey().hashCode(), entry.getValue().nextState.hashCode(), entry.getValue());

		out.println("}");

		return str.toString();
	}

    public void printModels(String prefix, String suffix, PetriNet net, BPMNReader loader, HashMap<Object, String> colorsPN,
                            HashMap<String, String> colorsBPMN1, HashMap<String, Integer> repetitions1, HashMap<String, Integer> repetitions2) {
        Random r = new Random();
        int rand = r.nextInt();

        try {
            PrintStream out = new PrintStream("target/tex/difference" + prefix
                    + "-" + rand + "-" + suffix + ".dot");

            if (colorsPN != null) {
                out.print(net.toDot(colorsPN));
                out.close();
            }

            out = new PrintStream("target/tex/difference" + prefix + "-" + rand
                    + "-" + suffix + "BPMN.dot");
            @SuppressWarnings("unchecked")
            String modelColor = printBPMN2DOT(colorsBPMN1, (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>)loader.getModel(), loader, repetitions1, repetitions2);
            out.print(modelColor);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String printBPMN2DOT(HashMap<String, String> colorsUnf,
                                 Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model,
                                 BPMNReader loader, HashMap<String, Integer> repetitions1,
                                 HashMap<String, Integer> repetitions2) {
        String result = "";

        if (repetitions2 == null)
            repetitions2 = new HashMap<String, Integer>();

        result += "digraph G {\n";
        result += "rankdir=LR \n";

        for (Event e : model.getEvents()) {
            if (colorsUnf.containsKey(e.getId())) {
                result += String
                        .format("  n%s[shape=ellipse,label=\"%s(x %s)(x%s)\", color=\"%s\"];\n",
                                e.getId().replace("-", ""), e.getName(),
                                "",
                                "",
                                colorsUnf.get(e.getId()));
            } else
                result += String.format("  n%s[shape=ellipse,label=\"%s\"];\n",
                        e.getId().replace("-", ""), e.getName());
        }
        result += "\n";

        for (Activity a : model.getActivities()) {
            if (colorsUnf.containsKey(a.getId()))
                result += String
                        .format("  n%s[shape=box,label=\"%s(x%s)(x%s)\",color=\"%s\"];\n",
                                a.getId().replace("-", ""), a.getName(),
                                "",
                                "",
                                colorsUnf.get(a.getId()));
            else
                result += String.format("  n%s[shape=box,label=\"%s\"];\n", a
                        .getId().replace("-", ""), a.getName());
        }
        result += "\n";

        for (Gateway g : model.getGateways(AndGateway.class)) {
            if (colorsUnf.containsKey(g.getId()))
                result += String
                        .format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
                                g.getId().replace("-", ""), "AND",
                                "",
                                "",
                                colorsUnf.get(g.getId()));
            else
                result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
                        g.getId().replace("-", ""), "AND");
        }
        for (Gateway g : model.getGateways(XorGateway.class)) {
            if (colorsUnf.containsKey(g.getId()))
                result += String
                        .format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
                                g.getId().replace("-", ""), "XOR",
                                "",
                                "",
                                colorsUnf.get(g.getId()));
            else
                result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
                        g.getId().replace("-", ""), "XOR");
        }
        for (Gateway g : model.getGateways(OrGateway.class)) {
            if (colorsUnf.containsKey(g.getId()))
                result += String
                        .format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
                                g.getId().replace("-", ""), "OR",
                                "",
                                "",
                                colorsUnf.get(g.getId()));
            else
                result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
                        g.getId().replace("-", ""), "OR");
        }
        for (Gateway g : model.getGateways(AlternativGateway.class))
            result += String.format("  n%s[shape=diamond,label=\"%s\"];\n", g
                    .getId().replace("-", ""), "?");
        result += "\n";

        for (DataNode d : model.getDataNodes()) {
            result += String.format("  n%s[shape=note,label=\"%s\"];\n", d
                            .getId().replace("-", ""),
                    d.getName().concat(" [" + d.getState() + "]"));
        }
        result += "\n";

        for (ControlFlow<FlowNode> cf : model.getControlFlow()) {
            if (cf.getLabel() != null && cf.getLabel() != "")
                result += String.format("  n%s->n%s[label=\"%s\"];\n", cf
                        .getSource().getId().replace("-", ""), cf.getTarget()
                        .getId().replace("-", ""), cf.getLabel());
            else
                result += String.format("  n%s->n%s;\n", cf.getSource().getId()
                                .replace("-", ""),
                        cf.getTarget().getId().replace("-", ""));
        }
        result += "\n";

        for (Activity a : model.getActivities()) {
            for (IDataNode d : a.getReadDocuments()) {
                result += String.format("  n%s->n%s;\n",
                        d.getId().replace("-", ""), a.getId().replace("-", ""));
            }
            for (IDataNode d : a.getWriteDocuments()) {
                result += String.format("  n%s->n%s;\n",
                        a.getId().replace("-", ""), d.getId().replace("-", ""));
            }
        }
        result += "}";

        return result;
    }


//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	public void verbalize() {
//		for (List<Operation> opSeq : opSeqs) {
//			Operation finalSt = opSeq.get(opSeq.size() - 1);
//			BiMap<Integer, Integer> mappings = finalSt.nextState.mappings;
//			BiMap<Integer, Integer> extendedMap = HashBiMap.<Integer, Integer> create(mappings);
//
//			BitSet c1 = finalSt.nextState.c1;
//			BitSet c2 = finalSt.nextState.c2;
//
//			for (int i = c1.nextSetBit(0); i >= 0; i = c1.nextSetBit(i + 1)) {
//				if (!extendedMap.containsKey(i) && this.commonLabels.contains(pes1.getLabel(i))) {
//					boolean found = false;
//
//					for (int j = c2.nextSetBit(0); j >= 0; j = c2.nextSetBit(j + 1))
//						if (!extendedMap.containsValue(j) && pes1.getLabel(i).equals(pes2.getLabel(j))) {
//							extendedMap.put(i, j);
//							found = true;
//							break;
//						}
//
//					if (!found)
//						for (int j = 0; j < pes2.getLabels().size(); j++)
//							if (!extendedMap.containsValue(j) && pes2.getLabel(j).equals(pes1.getLabel(i))) {
//								extendedMap.put(i, j);
//								found = true;
//								break;
//							}
//
//					if (!found)
//						verbalizeNotFound("model 1", pes1.getLabel(i), getContext(i, mappings, 1), i, pes1, replayerBPMN1, loader1);
//				}
//			}
//
//			for (int i = c2.nextSetBit(0); i >= 0; i = c2.nextSetBit(i + 1))
//				if (!extendedMap.containsValue(i) && this.commonLabels.contains(pes2.getLabel(i))) {
//					boolean found = false;
//
//					for (int j = 0; j < pes1.getLabels().size(); j++)
//						if (!extendedMap.containsKey(j) && pes1.getLabel(j).equals(pes2.getLabel(i))) {
//							extendedMap.put(j, i);
//							found = true;
//							break;
//						}
//
//					if (!found)
//						verbalizeNotFound("model2", pes2.getLabel(i), getContext(i, mappings, 2), i, pes2, replayerBPMN2, loader2);
//				}
//
//			LinkedList<Entry<Integer, Integer>> list = new LinkedList<>(extendedMap.entrySet());
//
//			for (int i = 0; i < list.size() - 1; i++) {
//				Entry<Integer, Integer> entry1 = list.get(i);
//				for (int j = i; j < list.size(); j++) {
//					Entry<Integer, Integer> entry2 = list.get(j);
//					if (i != j && this.commonLabels.contains(pes1.getLabel(entry1.getKey()))
//							&& this.commonLabels.contains(pes1.getLabel(entry2.getKey()))) {
//						BehaviorRelation rel1 = pes1.getBRelation(entry1.getKey(), entry2.getKey());
//						BehaviorRelation rel2 = pes2.getBRelation(entry1.getValue(), entry2.getValue());
//
//						if (!rel1.equals(rel2))
//							verbalize(entry1, entry2, getContext(entry1, entry2, mappings));
//					}
//				}
//			}
//		}
//
//		flushNonCommonTasks();
//		compareCyclicBehavior();
//	}
//
//	private String verbalizeRepeated(String task, String model) {
//		String statement = String.format("Task %s can be repeated in %s, but not in the other model.", task, model);
//
//		return statement;
//	}
//
//	private String verbalizeNotCommon(String task, String model) {
//		String statement = String.format("Task %s only occurs in %s.", task, model);
//
//		return statement;
//	}
//
//	private void verbalizeNotFound(String model, String task, String context, int i, PESSemantics<Integer> pes, BPMNReplayer replayer, BPMNReader loader) {
//		String statement = String.format(
//				"In " + model + ", there is a state after %s where %s can occur, whereas it cannot occur in the matching state in the other model", context, task);
//
//		Runs runs1 = null;
//		Runs runs2 = null;
//
//		if(model.equals("model 1"))
//			runs1 = printTask(i, pes, replayer, net1, loader, statement);
//
//		if(model.equals("model 2"))
//			runs2 = printTask(i, pes, replayer, net2, loader, statement);
//
//		Difference diff = new Difference(runs1, runs2);
//
//		if (statement != null) {
//			diff.setSentence(statement);
//			differences.add(diff);
//		}
//
//		statements.add(statement);
//	}
//
//	private void flushNonCommonTasks() {
//		for (String str : obsLabel1)
//			if (!commonLabels.contains(str) && !str.equals("_0_") && !str.equals("_1_")){
//				String statement = verbalizeNotCommon(str, "model 1");
//				Runs runs1 = spotTask(str, replayerBPMN1, loader1, statement);
//				Difference diff = new Difference(runs1, null);
//
//				if (statement != null) {
//					diff.setSentence(statement);
//					differences.add(diff);
//					statements.add(statement);
//				}
//			}
//
//		for (String str : obsLabel2)
//			if (!commonLabels.contains(str) && !str.equals("_0_") && !str.equals("_1_")){
//				String statement = verbalizeNotCommon(str, "model 2");
//				Runs runs2 = spotTask(str, replayerBPMN2, loader2, statement);
//				Difference diff = new Difference(null, runs2);
//
//				if (statement != null) {
//					diff.setSentence(statement);
//					differences.add(diff);
//					statements.add(statement);
//				}
//			}
//	}
//
//	private void compareCyclicBehavior() {
//		for (String s : pes1.getCyclicTasks())
//			if (!pes2.getCyclicTasks().contains(s) && commonLabels.contains(s)){
//				String statement = verbalizeRepeated(s, "model 1");
//				Runs runs1 = spotTask(s, replayerBPMN1, loader1, statement);
//				Runs runs2 = spotTask(s, replayerBPMN2, loader2, statement);
//
//				Difference diff = new Difference(runs1, runs2);
//
//				if (statement != null) {
//					diff.setSentence(statement);
//					differences.add(diff);
//					this.statements.add(statement);
//				}
//			}
//
//		for (String s : pes2.getCyclicTasks())
//			if (!pes1.getCyclicTasks().contains(s) && commonLabels.contains(s)){
//				String statement =  verbalizeRepeated(s, "model 2");
//				Runs runs1 = spotTask(s, replayerBPMN1, loader1, statement);
//				Runs runs2 = spotTask(s, replayerBPMN2, loader2, statement);
//
//				Difference diff = new Difference(runs1, runs2);
//
//				if (statement != null) {
//					diff.setSentence(statement);
//					differences.add(diff);
//					this.statements.add(statement);
//				}
//			}
//	}
//
//	public void verboseNotCommonTasks(String taskLabel, String m1) {
//		String statement = verbalizeNotCommon(taskLabel, m1);
//		Runs runs1 = null;
//		Runs runs2 = null;
//
//		if(m1.equals("model 1"))
//			runs1 = spotTask(taskLabel, replayerBPMN1, loader1, statement);
//
//		if(m1.equals("model 2"))
//			runs2 = spotTask(taskLabel, replayerBPMN2, loader2, statement);
//
//		Difference diff = new Difference(runs1, runs2);
//
//		if (statement != null) {
//			diff.setSentence(statement);
//			differences.add(diff);
//		}
//	}
//
//
//	private String getContext(int i, BiMap<Integer, Integer> mappings, int mode) {
//		BitSet lc1;
//
//		if (mode == 1)
//			lc1 = (BitSet) pes1.getLocalConfiguration(i).clone();
//		else
//			lc1 = (BitSet) pes2.getLocalConfiguration(i).clone();
//
//		// BitSet map1 = new BitSet();
//		//
//		//
//		// for(Entry<Integer, Integer> entry : mappings.entrySet()){
//		// if(mode == 1)
//		// map1.set(entry.getKey());
//		// else
//		// map1.set(entry.getValue());
//		// }
//		//
//		// lc1.and(map1);
//
//		if (mode == 1) {
//			HashSet<String> filt = new HashSet<>(pes1.getConfigurationLabels(lc1));
//			filt.retainAll(commonLabels);
//			return filt.toString();
//		}
//
//		HashSet<String> filt = new HashSet<>(pes2.getConfigurationLabels(lc1));
//		filt.retainAll(commonLabels);
//		return filt.toString();
//	}
//
//	// BehaviorRelation rel1 = pes1.getBRelation(entry1.getKey(),
//	// entry2.getKey());
//	// BehaviorRelation rel2 = pes2.getBRelation(entry1.getValue(),
//	// entry2.getValue());
//	//
//	// if(!rel1.equals(rel2))
//	// verbalize(pes1.getLabel(entry1.getKey()), pes1.getLabel(entry2.getKey()),
//	// rel1, rel2, getContext(entry1, entry2, mappings));
//
//	private void verbalize(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2, String context) {
//		BehaviorRelation r1 = pes1.getBRelation(entry1.getKey(), entry2.getKey());
//		Integer event1 = entry1.getKey();
//		Integer event1a = entry2.getKey();
//
//		if(r1.equals(BehaviorRelation.INV_CAUSALITY)){
//			r1 = BehaviorRelation.CAUSALITY;
//			Integer event1b = event1;
//			event1 = event1a;
//			event1a = event1b;
//		}
//
//		BehaviorRelation r2 = pes2.getBRelation(entry1.getValue(), entry2.getValue());
//		Integer event2 = entry1.getValue();
//		Integer event2a = entry2.getValue();
//
//		if(r2.equals(BehaviorRelation.INV_CAUSALITY)){
//			r2 = BehaviorRelation.CAUSALITY;
//			Integer event2b = event2;
//			event2 = event2a;
//			event2a = event2b;
//		}
//
//		String statement = getSentence(pes1.getLabel(event1), pes1.getLabel(event1a), pes2.getLabel(event2), pes2.getLabel(event2a), r1, r2, context);
//
//		Runs runs1;
//		Runs runs2;
//
//		if (r1.equals(BehaviorRelation.CAUSALITY)) {
//			runs1 = printDifferenceCausality(event1, event1a, pes1, replayerBPMN1, "m1", net1, loader1, statement);
//		} else if (r1.equals(BehaviorRelation.CONFLICT)) {
//			runs1 = printDifferenceConflict(event1, event1a, pes1, replayerBPMN1, "m1", net1, loader1, statement);
//		} else {
//			runs1 = printDifferenceConcurrency(event1, event1a, pes1, replayerBPMN1, "m1", net1, loader1, statement);
//		}
//
//		if (r2.equals(BehaviorRelation.CAUSALITY)) {
//			runs2 = printDifferenceCausality(event2, event2a, pes2, replayerBPMN2, "m2", net2, loader2, statement);
//		} else if (r2.equals(BehaviorRelation.CONFLICT)) {
//			runs2 = printDifferenceConflict(event2, event2a, pes2, replayerBPMN2, "m2", net2, loader2, statement);
//		} else {
//			runs2 = printDifferenceConcurrency(event2, event2a, pes2, replayerBPMN2, "m2", net2, loader2, statement);
//		}
//
//		Difference diff = new Difference(runs1, runs2);
//
//		if (statement != null) {
//			diff.setSentence(statement);
//			differences.add(diff);
//			statements.add(statement);
//		}
//	}
//
//	private Runs spotTask(String label, BPMNReplayer replayerBPMN, BPMNReader loader, String sentence) {
//		Runs runs = new Runs();
//
//		HashMap<String, String> colorsBP = replayerBPMN.spotTask(label);
//		runs.addRun(new Run(colorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>(), loader, sentence, null));
//
//		return runs;
//	}
//
//	private Runs printDifferenceConflict(Integer event1, Integer event1a, PESSemantics<Integer> pes,
//			BPMNReplayer replayerBPMN, String suffix, PetriNet net, BPMNReader loader, String sentence) {
//		Runs runs = new Runs();
//
//		// All configurations
//		BitSet conf1 = pes.getLocalConfiguration(event1);
//		BitSet conf1Minus = (BitSet) conf1.clone();
//		conf1Minus.set(event1, false);
//
//		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
//		HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1), repetitions, null);
//
////		HashMap<String, Integer> repetitionsPre = new HashMap<String, Integer>();
////		HashMap<String, String> colorsBPMNPre = replayerBPMN.execute("87668757645756454", pes.getPomset(conf1Minus), repetitionsPre, null);
////		HashMap<String, String> colorsBPMNFinal = getDifferenceColor(colorsBPMNPre, colorsBPMN, repetitions, repetitionsPre);
//
//		BitSet conf1a = pes.getLocalConfiguration(event1a);
//		BitSet conf1aMinus = (BitSet) conf1a.clone();
//		conf1aMinus.set(event1a, false);
//
//		HashMap<String, Integer> repetitions2 = new HashMap<String, Integer>();
//		HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event1a), pes.getPomset(conf1a), repetitions2, null);
////		HashMap<String, Integer> repetitions2Pre = new HashMap<String, Integer>();
////		HashMap<String, String> colorsBPMN2Pre = replayerBPMN.execute("87668757645756454", pes.getPomset(conf1aMinus), repetitions2Pre, null);
//
////		HashMap<String, String> colorsBPMN2Final = getDifferenceColor(colorsBPMN2Pre, colorsBPMN2, repetitions2, repetitions2Pre);
//		HashMap<String, String> newColorsBP = unifyColorsBPConflict(colorsBPMN, colorsBPMN2);
//
////		if(loader.equals(loader1))
////			printModels("m", "1", net, loader, null, newColorsBP, repetitions, repetitions2);
////		else
////			printModels("m", "2", net, loader, null, newColorsBP, repetitions, repetitions2);
//
//		runs.addRun(new Run(newColorsBP, repetitions, repetitions2, loader, sentence, pes.getPomset(conf1, conf1a)));
//
//		return runs;
//	}
//
//	private Runs printDifferenceConcurrency(Integer event1, Integer event1a, PESSemantics<Integer> pes,
//			BPMNReplayer replayerBPMN, String suffix, PetriNet net, BPMNReader loader, String sentence) {
//		Runs runs = new Runs();
//
//		BitSet conf1 = pes.getLocalConfiguration(event1);
//		BitSet conf1a = pes.getLocalConfiguration(event1a);
//		conf1.or(conf1a);
//
//		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
//		HashMap<String, String> colorsBPMN = replayerBPMN.executeC(pes.getLabel(event1), pes.getLabel(event1a), pes.getPomset(conf1), repetitions, null);
//
////		HashMap<String, Integer> repetitions2 = new HashMap<String, Integer>();
////		HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event1a), pes.getPomset(conf1a), repetitions2, null);
////		HashMap<String, String> newColorsBP = unifyColorsBPConflict(colorsBPMN, colorsBPMN2);
//
//		runs.addRun(new Run(colorsBPMN, repetitions, repetitions, loader, sentence, pes.getPomset(conf1, conf1a)));
//
//		return runs;
//	}
//
//	private HashMap<String, String> unifyColorsBPConflict(HashMap<String, String> colors1,
//			HashMap<String, String> colors1a) {
//		HashMap<String, String> unifiedColors = new HashMap<String, String>();
//		for (String key : colors1.keySet())
//			if (colors1a.containsKey(key) && colors1a.get(key).equals("green") && colors1.get(key).equals("green"))
//				unifiedColors.put(key, "green");
//			else if (colors1.get(key).equals("red"))
//				unifiedColors.put(key, "red");
//			else if (!colors1a.containsKey(key))
//				unifiedColors.put(key, "yellow");
//			else
//				unifiedColors.put(key, colors1.get(key));
//
//		for (String key : colors1a.keySet())
//			if (colors1a.get(key).equals("red"))
//				unifiedColors.put(key, "red");
//			else if (!colors1.containsKey(key))
//				unifiedColors.put(key, "yellow");
//			else if (!unifiedColors.containsKey(key))
//				unifiedColors.put(key, colors1a.get(key));
//
//		return unifiedColors;
//	}
//
//	public void printModels(String prefix, String suffix, PetriNet net, BPMNReader loader, HashMap<Object, String> colorsPN,
//			HashMap<String, String> colorsBPMN1, HashMap<String, Integer> repetitions1, HashMap<String, Integer> repetitions2) {
//		Random r = new Random();
//		int rand = r.nextInt();
//
//		try {
//			PrintStream out = new PrintStream("target/tex/difference" + prefix
//					+ "-" + rand + "-" + suffix + ".dot");
//
//			if (colorsPN != null) {
//				out.print(net.toDot(colorsPN));
//				out.close();
//			}
//
//			out = new PrintStream("target/tex/difference" + prefix + "-" + rand
//					+ "-" + suffix + "BPMN.dot");
//			@SuppressWarnings("unchecked")
//			String modelColor = printBPMN2DOT(colorsBPMN1, (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>)loader.getModel(), loader, repetitions1, repetitions2);
//			out.print(modelColor);
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private String printBPMN2DOT(HashMap<String, String> colorsUnf,
//			Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model,
//			BPMNReader loader, HashMap<String, Integer> repetitions1,
//			HashMap<String, Integer> repetitions2) {
//		String result = "";
//
//		if (repetitions2 == null)
//			repetitions2 = new HashMap<String, Integer>();
//
//		result += "digraph G {\n";
//		result += "rankdir=LR \n";
//
//		for (Event e : model.getEvents()) {
//			if (colorsUnf.containsKey(e.getId())) {
//				result += String
//						.format("  n%s[shape=ellipse,label=\"%s(x %s)(x%s)\", color=\"%s\"];\n",
//								e.getId().replace("-", ""), e.getName(),
//								getLabel(repetitions1, e),
//								getLabel(repetitions2, e),
//								colorsUnf.get(e.getId()));
//			} else
//				result += String.format("  n%s[shape=ellipse,label=\"%s\"];\n",
//						e.getId().replace("-", ""), e.getName());
//		}
//		result += "\n";
//
//		for (Activity a : model.getActivities()) {
//			if (colorsUnf.containsKey(a.getId()))
//				result += String
//						.format("  n%s[shape=box,label=\"%s(x%s)(x%s)\",color=\"%s\"];\n",
//								a.getId().replace("-", ""), a.getName(),
//								getLabel(repetitions1, a),
//								getLabel(repetitions2, a),
//								colorsUnf.get(a.getId()));
//			else
//				result += String.format("  n%s[shape=box,label=\"%s\"];\n", a
//						.getId().replace("-", ""), a.getName());
//		}
//		result += "\n";
//
//		for (Gateway g : model.getGateways(AndGateway.class)) {
//			if (colorsUnf.containsKey(g.getId()))
//				result += String
//						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
//								g.getId().replace("-", ""), "AND",
//								getLabel(repetitions1, g),
//								getLabel(repetitions2, g),
//								colorsUnf.get(g.getId()));
//			else
//				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
//						g.getId().replace("-", ""), "AND");
//		}
//		for (Gateway g : model.getGateways(XorGateway.class)) {
//			if (colorsUnf.containsKey(g.getId()))
//				result += String
//						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
//								g.getId().replace("-", ""), "XOR",
//								getLabel(repetitions1, g),
//								getLabel(repetitions2, g),
//								colorsUnf.get(g.getId()));
//			else
//				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
//						g.getId().replace("-", ""), "XOR");
//		}
//		for (Gateway g : model.getGateways(OrGateway.class)) {
//			if (colorsUnf.containsKey(g.getId()))
//				result += String
//						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
//								g.getId().replace("-", ""), "OR",
//								getLabel(repetitions1, g),
//								getLabel(repetitions2, g),
//								colorsUnf.get(g.getId()));
//			else
//				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
//						g.getId().replace("-", ""), "OR");
//		}
//		for (Gateway g : model.getGateways(AlternativGateway.class))
//			result += String.format("  n%s[shape=diamond,label=\"%s\"];\n", g
//					.getId().replace("-", ""), "?");
//		result += "\n";
//
//		for (DataNode d : model.getDataNodes()) {
//			result += String.format("  n%s[shape=note,label=\"%s\"];\n", d
//					.getId().replace("-", ""),
//					d.getName().concat(" [" + d.getState() + "]"));
//		}
//		result += "\n";
//
//		for (ControlFlow<FlowNode> cf : model.getControlFlow()) {
//			if (cf.getLabel() != null && cf.getLabel() != "")
//				result += String.format("  n%s->n%s[label=\"%s\"];\n", cf
//						.getSource().getId().replace("-", ""), cf.getTarget()
//						.getId().replace("-", ""), cf.getLabel());
//			else
//				result += String.format("  n%s->n%s;\n", cf.getSource().getId()
//						.replace("-", ""),
//						cf.getTarget().getId().replace("-", ""));
//		}
//		result += "\n";
//
//		for (Activity a : model.getActivities()) {
//			for (IDataNode d : a.getReadDocuments()) {
//				result += String.format("  n%s->n%s;\n",
//						d.getId().replace("-", ""), a.getId().replace("-", ""));
//			}
//			for (IDataNode d : a.getWriteDocuments()) {
//				result += String.format("  n%s->n%s;\n",
//						a.getId().replace("-", ""), d.getId().replace("-", ""));
//			}
//		}
//		result += "}";
//
//		return result;
//	}
//
//	private String getLabel(HashMap<String, Integer> repetitions, FlowNode e) {
//		if (!repetitions.containsKey(e.getId()))
//			return "0";
//
//		return repetitions.get(e.getId()) + "";
//	}
//
//	private HashMap<String, String> getDifferenceColor(HashMap<String, String> colorsBPMNPre,
//			HashMap<String, String> colorsBPMN, HashMap<String, Integer> repetitions,
//			HashMap<String, Integer> repetitionsPre) {
//		HashMap<String, String> colors = new HashMap<String, String>();
//
//		for (String key : colorsBPMN.keySet())
//			if (colorsBPMN.get(key).equals("red"))
//				colors.put(key, "red");
//			else if (colorsBPMNPre.containsKey(key))
//				colors.put(key, "green");
//			else
//				colors.put(key, "yellow");
//		return colors;
//	}
////
////	private Runs printDifferenceCausality(Integer event1, Integer event1a, PESSemantics<Integer> pes,
////			BPMNReplayer replayerBPMN, String suffix, PetriNet net, BPMNReader loader, String sentence) {
////		Runs runs = new Runs();
////
////		// All configurations
////		BitSet conf1 = pes.getLocalConfiguration(event1);
////		Trace<Integer> trace = new Trace<>();
////		trace.addAllStrongCauses(pes.getEvents(conf1));
////
////		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
////		HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1),
////				repetitions, null);
////
////		BitSet conf1a = pes.getLocalConfiguration(event1a);
////		Trace<Integer> trace2 = new Trace<>();
////		trace2.addAllStrongCauses(pes.getEvents(conf1a));
////
////		BitSet exts = (BitSet) conf1a.clone();
////		BitSet intersects = (BitSet) conf1a.clone();
////		for (int i = conf1.nextSetBit(0); i >= 0; i = conf1.nextSetBit(i + 1))
////			exts.set(i, false);
////
////		intersects.and(conf1);
////
////		if (intersects.equals(conf1) && pes.arePossibleExtensions(conf1, exts)) {
////			HashMap<String, Integer> repetitions2 = new HashMap<String, Integer>();
////			HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event1a), pes.getPomset(conf1a),
////					repetitions2, null);
////
////			HashMap<String, String> newColorsBP = unifyColorsBP(colorsBPMN, colorsBPMN2);
////			HashMap<String, Integer> newRep1 = repetitions;
////			HashMap<String, Integer> newRep2 = repetitions2;
////
////			runs.addRun(new Run(newColorsBP, newRep1, newRep2, loader, sentence, pes.getPomset(conf1, conf1a)));
////		}
////
////		return runs;
////	}
//
//	private HashMap<String, String> unifyColorsBP(HashMap<String, String> colors1, HashMap<String, String> colors1a) {
//		HashMap<String, String> unifiedColors = new HashMap<String, String>();
//		for (String key : colors1.keySet())
//			if (colors1.get(key).equals("red"))
//				unifiedColors.put(key, "red");
//			else if (colors1a.containsKey(key))
//				unifiedColors.put(key, "green");
//			else
//				unifiedColors.put(key, "yellow");
//
//		for (String key : colors1a.keySet())
//			if (colors1a.get(key).equals("red"))
//				unifiedColors.put(key, "red");
//			else if (!colors1.containsKey(key))
//				unifiedColors.put(key, "yellow");
//
//		return unifiedColors;
//	}
//
//	private Runs printDifferenceCausality(Integer event1, Integer event1a, PESSemantics<Integer> pes,
//			BPMNReplayer replayerBPMN, String suffix, PetriNet net, BPMNReader loader, String sentence) {
//		Runs runs = new Runs();
//
//		// All configurations
//		BitSet conf1 = pes.getLocalConfiguration(event1);
//		Trace<Integer> trace = new Trace<>();
//		trace.addAllStrongCauses(pes.getEvents(conf1));
//
//		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
//		HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1),
//				repetitions, null);
//
//		BitSet conf1a = pes.getLocalConfiguration(event1a);
//		Trace<Integer> trace2 = new Trace<>();
//		trace2.addAllStrongCauses(pes.getEvents(conf1a));
//
//		BitSet exts = (BitSet) conf1a.clone();
//		BitSet intersects = (BitSet) conf1a.clone();
//		for (int i = conf1.nextSetBit(0); i >= 0; i = conf1.nextSetBit(i + 1))
//			exts.set(i, false);
//
//		intersects.and(conf1);
//
//		if (intersects.equals(conf1) && pes.arePossibleExtensions(conf1, exts)) {
//			HashMap<String, Integer> repetitions2 = new HashMap<String, Integer>();
//			HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event1a), pes.getPomset(conf1a),
//					repetitions2, null);
//
//			HashMap<String, String> newColorsBP = unifyColorsBP(colorsBPMN, colorsBPMN2);
//			HashMap<String, Integer> newRep1 = repetitions;
//			HashMap<String, Integer> newRep2 = repetitions2;
//
//			runs.addRun(new Run(newColorsBP, newRep1, newRep2, loader, sentence, pes.getPomset(conf1, conf1a)));
//
////			printModels("m", "1", net, loader, null, newColorsBP, newRep1, newRep2);
//		}
//
//		return runs;
//	}

	private DifferenceML printTasksHL(List<Integer> events, NewUnfoldingPESSemantics<Integer> pes, BPMNReplayerML replayerBPMN, PetriNet net, BPMNReader loader, String sentence) {
		List<String> start = new ArrayList<>();
		List<String> a = new ArrayList<>();
		List<String> end = new ArrayList<>();
		List<String> greys = new ArrayList<>();

		BitSet inter = null;
		BitSet union = null;

        HashMap<String, String> aColors = new HashMap<>();

		for(Integer event : events){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

			BitSet conf1 = pes.getLocalConfiguration(event);
			Trace<Integer> trace = new Trace<>();
			trace.addAllStrongCauses(pes.getEvents(conf1));

			HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

			for(Entry<String, String> entry : colorsBPMN.entrySet())
				if(entry.getValue().equals("red"))
                    a.add(entry.getKey());
                else if(!a.contains(entry.getKey()))
                    aColors.put(entry.getKey(), "green");

			if(inter == null)
				inter = (BitSet) conf1.clone();
			else
				inter.and(conf1);

			if(union == null)
				union = (BitSet) conf1.clone();
			else
				union.or(conf1);
		}

        for(Integer event : events)
            inter.set(event, false);

		Pomset pomset = pes.getPomset(inter, commonLabels);
		DirectedGraph g = pomset.getGraph();
		HashSet<Integer> sinks = new HashSet<>();
		for(Vertex v : g.getVertices())
			if(g.getEdgesWithSource(v).isEmpty())
				sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();

		for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

			BitSet conf1 = pes.getLocalConfiguration(event);
			Trace<Integer> trace = new Trace<>();
			trace.addAllStrongCauses(pes.getEvents(conf1));

			HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

			for(Entry<String, String> entry : colorsBPMN.entrySet())
				if(entry.getValue().equals("red"))
					start.add(entry.getKey());
                else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()))
                    startColors.put(entry.getKey(), "green");
		}


        Queue<Multiset<Integer>> queue = new LinkedList<>();
        Multiset ms = getMultiset(union);
        queue.offer(ms);
        HashSet<Multiset<Integer>> visited = new HashSet<>();
        visited.add(ms);

        HashMap<String, String> endColors = new HashMap<>();

        while(!queue.isEmpty()) {
            Multiset<Integer> current = queue.poll();
            Set<Integer> extensions = pes.getPossibleExtensions(current);
            endColors = new HashMap<>();

            for (Integer event : extensions) {
                if (!commonLabels.contains(pes.getLabel(event)))
                    continue;

                BitSet conf1 = pes.getLocalConfiguration(event);
                Trace<Integer> trace = new Trace<>();
                trace.addAllStrongCauses(pes.getEvents(conf1));

                HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

                for (Entry<String, String> entry : colorsBPMN.entrySet())
                    if (entry.getValue().equals("red"))
                        end.add(entry.getKey());
                    else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()) && !end.contains(entry.getKey()))
                        endColors.put(entry.getKey(), "green");
            }

            if(end.isEmpty()) {
                for(Integer ext : extensions) {
                    Multiset<Integer> copy = HashMultiset.<Integer> create(current);
                    copy.add(ext);
                    if(!visited.contains(copy)){
                        queue.add(copy);
                        visited.add(copy);
                    }
                }
            }else
                break;
        }

        for(String element : aColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element))
                greys.add(element);

        for(String element : endColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element) && !a.contains(element))
                greys.add(element);

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(a);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = replayerBPMN.getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

		DifferenceML diff = new DifferenceML();
		diff.setSentence(sentence);
		diff.setA(a);
		diff.setStart(start);
		diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "red");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

		return diff;
	}

    private DifferenceML print2Tasks(Integer event1, Integer event2, NewUnfoldingPESSemantics<Integer> pes, BPMNReplayerML replayerBPMN, PetriNet net, BPMNReader loader, String sentence) {
        if(!commonLabels.contains(pes.getLabel(event1)) && !commonLabels.contains(pes.getLabel(event2)))
            return null;

        List<String> start = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        BitSet conf1 = pes.getLocalConfiguration(event1);
        BitSet conf2 = pes.getLocalConfiguration(event2);

        BitSet inter = (BitSet) conf1.clone();
        inter.and((BitSet) conf2.clone());

        BitSet union = (BitSet) conf1.clone();
        union.or((BitSet) conf2.clone());

        HashMap<String, String> aColors = new HashMap<>();
        HashMap<String, String> bColors = new HashMap<>();

        // event 1
        Trace<Integer> trace1 = new Trace<>();
        trace1.addAllStrongCauses(pes.getEvents(conf1));
        HashMap<String, String> colorsBPMN1 = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

        for(Entry<String, String> entry : colorsBPMN1.entrySet())
            if(entry.getValue().equals("red"))
                a.add(entry.getKey());
            else if(!a.contains(entry.getKey()))
                aColors.put(entry.getKey(), "green");


        // event 2
        Trace<Integer> trace2 = new Trace<>();
        trace2.addAllStrongCauses(pes.getEvents(conf1));
        HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event2), pes.getPomset(conf2, commonLabels), new HashMap<String, Integer>(), null);

        for(Entry<String, String> entry : colorsBPMN2.entrySet())
            if(entry.getValue().equals("red"))
                b.add(entry.getKey());
            else if(!b.contains(entry.getKey()))
                bColors.put(entry.getKey(), "green");

        inter.set(event1, false);
        inter.set(event2, false);

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();

        for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf));

            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf, commonLabels), new HashMap<String, Integer>(), null);

            for(Entry<String, String> entry : colorsBPMN.entrySet())
                if(entry.getValue().equals("red"))
                    start.add(entry.getKey());
                else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()))
                    startColors.put(entry.getKey(), "green");
        }

        Set<String> future1 = pes.getPossibleFutureAsLabels(getMultiset(conf1));
        Set<String> future2 = pes.getPossibleFutureAsLabels(getMultiset(conf2));
        future1.retainAll(future2);

        HashMap<String, String> endColors = new HashMap<>();
        HashMap<String, String> colorsBPMN = replayerBPMN.getEnd(a.get(0), b.get(0), future1);
        for (Entry<String, String> entry : colorsBPMN.entrySet()) {
            if (entry.getValue().equals("red"))
                end.add(entry.getKey());
            else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()) && !end.contains(entry.getKey()))
                endColors.put(entry.getKey(), "green");
        }


//        Multiset ms = getMultiset(union);
//        queue.offer(ms);
//        HashSet<Multiset<Integer>> visited = new HashSet<>();
//        visited.add(ms);
//
//
//        while(!queue.isEmpty()) {
//            Multiset<Integer> current = queue.poll();
//            System.out.println(current);
//            Set<Integer> extensions = pes.getPossibleExtensions(current);
//            endColors = new HashMap<>();
//
//            for (Integer event : extensions) {
//                if (!commonLabels.contains(pes.getLabel(event)))
//                    continue;
//
//                BitSet conf = pes.getLocalConfiguration(event);
//                Trace<Integer> trace = new Trace<>();
//                trace.addAllStrongCauses(pes.getEvents(conf));
//
//                HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf, commonLabels), new HashMap<String, Integer>(), null);
//
//                for (Entry<String, String> entry : colorsBPMN.entrySet())
//                    if (entry.getValue().equals("red"))
//                        end.add(entry.getKey());
//                    else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()) && !end.contains(entry.getKey()))
//                        endColors.put(entry.getKey(), "green");
//            }
//
//            if(end.isEmpty()) {
//                for(Integer ext : extensions) {
//                    Multiset<Integer> copy = HashMultiset.<Integer> create(current);
//                    copy.add(ext);
//                    if(!visited.contains(copy)){
//                        queue.add(copy);
//                        visited.add(copy);
//                    }
//                }
//            }else
//                break;
//        }

        for(String element : aColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element) && !b.contains(element))
                greys.add(element);

        for(String element : bColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element) && !a.contains(element))
                greys.add(element);

        for(String element : endColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element) && !a.contains(element) && !b.contains(element))
                greys.add(element);

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(a);
        allReleventEdges.addAll(b);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = replayerBPMN.getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        DifferenceML diff = new DifferenceML();
        diff.setSentence(sentence);
        diff.setA(a);
        diff.setB(b);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "red");

        for (String s : b)
            newColorsBP.put(s, "red");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

    private DifferenceML printTasksGO(List<Integer> events, NewUnfoldingPESSemantics<Integer> pes, BPMNReplayerML replayerBPMN, PetriNet net, BPMNReader loader, String sentence) {
        List<String> start = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        BitSet inter = null;
        BitSet union = null;

        HashMap<String, String> aColors = new HashMap<>();

        for(Integer event : events){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

            for(Entry<String, String> entry : colorsBPMN.entrySet())
                if(entry.getValue().equals("red"))
                    a.add(entry.getKey());
                else if(!a.contains(entry.getKey()))
                    aColors.put(entry.getKey(), "green");

            if(inter == null)
                inter = (BitSet) conf1.clone();
            else
                inter.and(conf1);

            if(union == null)
                union = (BitSet) conf1.clone();
            else
                union.or(conf1);
        }

        if(inter == null)
            return null;

        for(Integer event : events)
            inter.set(event, false);

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();

        for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

            for(Entry<String, String> entry : colorsBPMN.entrySet())
                if(entry.getValue().equals("red"))
                    start.add(entry.getKey());
                else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()))
                    startColors.put(entry.getKey(), "green");
        }


        Queue<Multiset<Integer>> queue = new LinkedList<>();
        Multiset ms = getMultiset(union);
        queue.offer(ms);
        HashSet<Multiset<Integer>> visited = new HashSet<>();
        visited.add(ms);

        HashMap<String, String> endColors = new HashMap<>();

        while(!queue.isEmpty()) {
            Multiset<Integer> current = queue.poll();
            Set<Integer> extensions = pes.getPossibleExtensions(current);
            endColors = new HashMap<>();

            for (Integer event : extensions) {
                if (!commonLabels.contains(pes.getLabel(event)))
                    continue;

                BitSet conf1 = pes.getLocalConfiguration(event);
                Trace<Integer> trace = new Trace<>();
                trace.addAllStrongCauses(pes.getEvents(conf1));

                HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

                for (Entry<String, String> entry : colorsBPMN.entrySet())
                    if (entry.getValue().equals("red"))
                        end.add(entry.getKey());
                    else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()) && !end.contains(entry.getKey()))
                        endColors.put(entry.getKey(), "green");
            }

            if(end.isEmpty()) {
                for(Integer ext : extensions) {
                    Multiset<Integer> copy = HashMultiset.<Integer> create(current);
                    copy.add(ext);
                    if(!visited.contains(copy)){
                        queue.add(copy);
                        visited.add(copy);
                    }
                }
            }else
                break;
        }

        for(String element : aColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element))
                greys.add(element);

        for(String element : endColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element) && !a.contains(element))
                greys.add(element);

		greys.addAll(a);
		a.clear();

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
//        allReleventEdges.addAll(a);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = replayerBPMN.getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        DifferenceML diff = new DifferenceML();
        diff.setSentence(sentence);
        diff.setA(a);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "gray");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

    private DifferenceML addTasks(List<Integer> events, NewUnfoldingPESSemantics<Integer> pes, BPMNReplayerML replayerBPMN, PetriNet net, BPMNReader loader, String sentence) {
        List<String> start = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        BitSet inter = null;
        BitSet union = null;

        HashMap<String, String> aColors = new HashMap<>();

        for(Integer event : events){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

            for(Entry<String, String> entry : colorsBPMN.entrySet())
                if(entry.getValue().equals("red"))
                    a.add(entry.getKey());
                else if(!a.contains(entry.getKey()))
                    aColors.put(entry.getKey(), "green");

            if(inter == null)
                inter = (BitSet) conf1.clone();
            else
                inter.and(conf1);

            if(union == null)
                union = (BitSet) conf1.clone();
            else
                union.or(conf1);
        }

        if(inter == null)
            return null;

        for(Integer event : events)
            inter.set(event, false);

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();

        for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

            for(Entry<String, String> entry : colorsBPMN.entrySet())
                if(entry.getValue().equals("red"))
                    start.add(entry.getKey());
                else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()))
                    startColors.put(entry.getKey(), "green");
        }


        Queue<Multiset<Integer>> queue = new LinkedList<>();
        Multiset ms = getMultiset(union);
        queue.offer(ms);
        HashSet<Multiset<Integer>> visited = new HashSet<>();
        visited.add(ms);

        HashMap<String, String> endColors = new HashMap<>();

        while(!queue.isEmpty()) {
            Multiset<Integer> current = queue.poll();
            Set<Integer> extensions = pes.getPossibleExtensions(current);
            endColors = new HashMap<>();

            for (Integer event : extensions) {
                if (!commonLabels.contains(pes.getLabel(event)))
                    continue;

                BitSet conf1 = pes.getLocalConfiguration(event);
                Trace<Integer> trace = new Trace<>();
                trace.addAllStrongCauses(pes.getEvents(conf1));

                HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

                for (Entry<String, String> entry : colorsBPMN.entrySet())
                    if (entry.getValue().equals("red"))
                        end.add(entry.getKey());
                    else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()) && !end.contains(entry.getKey()))
                        endColors.put(entry.getKey(), "green");
            }

            if(end.isEmpty()) {
                for(Integer ext : extensions) {
                    Multiset<Integer> copy = HashMultiset.<Integer> create(current);
                    copy.add(ext);
                    if(!visited.contains(copy)){
                        queue.add(copy);
                        visited.add(copy);
                    }
                }
            }else
                break;
        }

        for(String element : aColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element))
                greys.add(element);

        for(String element : endColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element) && !a.contains(element))
                greys.add(element);

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(a);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = replayerBPMN.getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        DifferenceML diff = new DifferenceML();
        diff.setSentence(sentence);
        diff.setA(a);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "gray");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

    private DifferenceML printTasksGOHL(List<Integer> eventsGO,List<Integer> eventsHL, NewUnfoldingPESSemantics<Integer> pes, BPMNReplayerML replayerBPMN, PetriNet net, BPMNReader loader, String sentence) {
        List<String> start = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        BitSet inter = null;
        BitSet union = null;

        HashMap<String, String> greyColors = new HashMap<>();

        for(Integer event : eventsGO){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

            for(Entry<String, String> entry : colorsBPMN.entrySet())
                if(entry.getValue().equals("red"))
                    greys.add(entry.getKey());
                else if(!greys.contains(entry.getKey()))
                    greyColors.put(entry.getKey(), "green");

            if(inter == null)
                inter = (BitSet) conf1.clone();
            else
                inter.and(conf1);

            if(union == null)
                union = (BitSet) conf1.clone();
            else
                union.or(conf1);
        }

        if(inter == null)
            return null;

        for(Integer event : eventsGO)
            inter.set(event, false);

        HashMap<String, String> endColors = new HashMap<>();

        for(Integer event : eventsHL){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

            for(Entry<String, String> entry : colorsBPMN.entrySet())
                if(entry.getValue().equals("red"))
                    end.add(entry.getKey());
                else if(!end.contains(entry.getKey()))
                    endColors.put(entry.getKey(), "green");
        }

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();

        for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);

            for(Entry<String, String> entry : colorsBPMN.entrySet())
                if(entry.getValue().equals("red"))
                    start.add(entry.getKey());
                else if(!start.contains(entry.getKey()) && !end.contains(entry.getKey()))
                    startColors.put(entry.getKey(), "green");
        }


        Queue<Multiset<Integer>> queue = new LinkedList<>();
        Multiset ms = getMultiset(union);
        queue.offer(ms);
        HashSet<Multiset<Integer>> visited = new HashSet<>();
        visited.add(ms);

        for(String element : endColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element))
                greys.add(element);

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = replayerBPMN.getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        DifferenceML diff = new DifferenceML();
        diff.setSentence(sentence);
        diff.setEnd(end);
        diff.setStart(start);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

	public Multiset<Integer> getMultiset(BitSet bs){
		Multiset<Integer> multiset = HashMultiset. create();
		for (int event = bs.nextSetBit(0); event >= 0; event = bs.nextSetBit(event+1))
			multiset.add(event);

		return multiset;
	}

	private Runs printTask(Integer event1, PESSemantics<Integer> pes,
			BPMNReplayerML replayerBPMN, PetriNet net, BPMNReader loader, String sentence) {
		Runs runs = new Runs();

		// All configurations
		BitSet conf1 = pes.getLocalConfiguration(event1);
		Trace<Integer> trace = new Trace<>();
		trace.addAllStrongCauses(pes.getEvents(conf1));

		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
		HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1),
				repetitions, null);

		runs.addRun(new Run(colorsBPMN, repetitions, new HashMap<String, Integer>(), loader, sentence, pes.getPomset(conf1)));

		return runs;
	}
//
//	private String getSentence(String task1, String task2, String task3, String task4, BehaviorRelation rel1, BehaviorRelation rel2, String context) {
//		String verbR1 = "";
//
//		if (rel1.equals(BehaviorRelation.CAUSALITY))
//			verbR1 = String.format("task %s occurs before %s", task1, task2);
//		else if (rel1.equals(BehaviorRelation.INV_CAUSALITY))
//			verbR1 = String.format("task %s occurs before %s", task2, task1);
//		else if (rel1.equals(BehaviorRelation.CONFLICT))
//			verbR1 = String.format("either task %s occurs or %s", task1, task2);
//		else if (rel1.equals(BehaviorRelation.CONCURRENCY))
//			verbR1 = String.format("tasks %s and %s can occur in parallel", task1, task2);
//
//		String verbR2 = "";
//
//		if (rel2.equals(BehaviorRelation.CAUSALITY))
//			verbR2 = String.format("task %s occurs before %s", task3, task4);
//		else if (rel2.equals(BehaviorRelation.INV_CAUSALITY))
//			verbR2 = String.format("task %s occurs before %s", task4, task3);
//		else if (rel2.equals(BehaviorRelation.CONFLICT))
//			verbR2 = String.format("either task %s occurs or %s", task3, task4);
//		else if (rel2.equals(BehaviorRelation.CONCURRENCY))
//			verbR2 = String.format("tasks %s and %s can occur in parallel", task3, task4);
//
//		String statement = String.format("In model 1, there is a state after %s where %s, whereas in the matching state in model 2, %s.",
//				context, verbR1, verbR2);
//		// System.out.println(statement);
//		return statement;
//	}
//
//	private void verbalize(String task1, String task2, BehaviorRelation rel1, BehaviorRelation rel2, String context) {
//		String verbR1 = "";
//
//		if (rel1.equals(BehaviorRelation.CAUSALITY))
//			verbR1 = String.format("task %s occurs before %s", task1, task2);
//		else if (rel1.equals(BehaviorRelation.INV_CAUSALITY))
//			verbR1 = String.format("task %s occurs before %s", task2, task1);
//		else if (rel1.equals(BehaviorRelation.CONFLICT))
//			verbR1 = String.format("either task %s occurs or %s", task1, task2);
//		else if (rel1.equals(BehaviorRelation.CONCURRENCY))
//			verbR1 = String.format("tasks %s and %s can occur in parallel", task1, task2);
//
//		String verbR2 = "";
//
//		if (rel2.equals(BehaviorRelation.CAUSALITY))
//			verbR2 = String.format("task %s occurs before %s", task1, task2);
//		else if (rel2.equals(BehaviorRelation.INV_CAUSALITY))
//			verbR2 = String.format("task %s occurs before %s", task2, task1);
//		else if (rel2.equals(BehaviorRelation.CONFLICT))
//			verbR2 = String.format("either task %s occurs or %s", task1, task2);
//		else if (rel2.equals(BehaviorRelation.CONCURRENCY))
//			verbR2 = String.format("tasks %s and %s can occur in parallel", task1, task2);
//
//		String statement = String.format(
//				"In model 1, there is a state after %s where %s, whereas in the matching state in model 2, %s.",
//				context, verbR1, verbR2);
//		// System.out.println(statement);
//		statements.add(statement);
//	}
//
//	private String getContext(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2, BiMap<Integer, Integer> mappings) {
//		Integer evt1 = entry1.getKey();
//		Integer evt1a = entry2.getKey();
//
//		Integer evt2 = entry1.getValue();
//		Integer evt2a = entry2.getValue();
//
//		BitSet lc1 = (BitSet) pes1.getLocalConfiguration(evt1).clone();
//		lc1.and(pes1.getLocalConfiguration(evt1a));
//		lc1.set(evt1, false);
//		lc1.set(evt1a, false);
//
//		BitSet lc2 = (BitSet) pes2.getLocalConfiguration(evt2).clone();
//		lc2.and(pes2.getLocalConfiguration(evt2a));
//		lc2.set(evt2, false);
//		lc2.set(evt2a, false);
//
//		BitSet map1 = new BitSet();
//		BitSet map2 = new BitSet();
//		for (Entry<Integer, Integer> entry : mappings.entrySet()) {
//			map1.set(entry.getKey());
//			map2.set(entry.getValue());
//		}
//
//		lc1.and(map1);
//		lc2.and(map2);
//
//		HashSet<String> filt = new HashSet<>(pes1.getConfigurationLabels(lc1));
//		filt.retainAll(commonLabels);
//		return filt.toString();
//	}
//
//	private void verbalizeAcyclicDifferences(List<Operation> opSeq, List<int[]> diffIndexesList, int index) {
//		for (int[] diffIndexes : diffIndexesList) {
//			Operation firstMatching = opSeq.get(diffIndexes[0]);
//			Operation secondMatching = opSeq.get(diffIndexes[2]);
//			Operation firstHiding = opSeq.get(diffIndexes[1]);
//			Pair<Integer, Integer> firstMatchingEventPair = (Pair) firstMatching.target;
//			Pair<Integer, Integer> secondMatchingEventPair = (Pair) secondMatching.target;
//
//			BitSet context1 = (BitSet) secondMatching.nextState.c1.clone();
//			context1.andNot(firstMatching.nextState.c1);
//			context1.clear(secondMatchingEventPair.getFirst());
//
//			BitSet context2 = (BitSet) secondMatching.nextState.c2.clone();
//			context2.andNot(firstMatching.nextState.c2);
//			context2.set(secondMatchingEventPair.getSecond(), false);
//
//			String firstHidingLabel = firstHiding.label;
//
//			if (firstHiding.op == Op.LHIDE) {
//				Pair<Operation, Boolean> pair = findRHide(opSeq, diffIndexes, firstHidingLabel);
//				Operation secondHiding = pair.getFirst();
//
//				// Found a corresponding RHIDE
//				if (pair.getSecond()) {
//					context1.set(firstMatchingEventPair.getFirst());
//					context2.set(firstMatchingEventPair.getSecond());
//
//					if (!globalDiffs.contains(context1, context2)) {
//						// System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//						// firstMatching.label,
//						// firstMatchingEventPair.getFirst(),
//						// pes1.getBRelation(firstMatchingEventPair.getFirst(),
//						// (Integer)firstHiding.target),
//						// firstHidingLabel, (Integer)firstHiding.target);
//						// System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//						// firstMatching.label,
//						// firstMatchingEventPair.getSecond(),
//						// pes2.getBRelation(firstMatchingEventPair.getSecond(),
//						// (Integer)secondHiding.target),
//						// secondHiding.label, (Integer)secondHiding.target);
//						verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(), firstMatching.label,
//								(Integer) firstHiding.target, firstHidingLabel, firstMatchingEventPair.getSecond(),
//								firstMatching.label, (Integer) secondHiding.target, secondHiding.label);
//					}
//				} else if (secondHiding != null) {
//					// ========= Symmetric <<==
//					if (!globalDiffs.contains(context1, context2)) {
//						String statement = String.format(
//								"In the log, after the occurrence of %s(%d), %s(%d) is substituted by %s(%d)\n",
//								firstMatching.label, firstMatchingEventPair.getFirst(), firstHiding.label,
//								(Integer) firstHiding.target, secondHiding.label, (Integer) secondHiding.target);
//
//						statements.add(statement);
//						System.out.println(statement);
//					}
//				} else {
//					// No RHIDE found within difference context
//					if (firstMatching.nextState.labels.contains(firstHidingLabel)) {
//						if (!globalDiffs.contains(context1, context2)) {
//							String statement = String.format(
//									"In the log, after the occurrence of %s(%d), %s(%d) is duplicated, while in the model it is not\n",
//									firstMatching.label, firstMatchingEventPair.getFirst(), firstHidingLabel,
//									(Integer) firstHiding.target);
//
//							statements.add(statement);
//							System.out.println(statement);
//						}
//					} else {
//						int e2 = firstMatchingEventPair.getSecond();
//						BitSet dconflict = pes2.getDirectConflictSet(e2);
//						boolean found = false;
//						Integer e2p = null;
//
//						for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1))
//							if (!pe.equals(e2) && pes2.getBRelation(e2, pe) == BehaviorRelation.CONFLICT
//									&& firstHidingLabel.equals(pes2.getLabel(pe))) {
//								eventsConsideredByConflictRelation.add(pe);
//								found = true;
//								e2p = pe;
//								break;
//							}
//
//						if (found) {
//							context1.set(firstMatchingEventPair.getFirst());
//							context2.set(firstMatchingEventPair.getSecond());
//							context2.set(e2p);
//
//							if (!globalDiffs.contains(context1, context2)) {
//								// System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//								// firstMatching.label,
//								// firstMatchingEventPair.getFirst(),
//								// pes1.getBRelation(firstMatchingEventPair.getFirst(),
//								// (Integer)firstHiding.target),
//								// firstHidingLabel,
//								// (Integer)firstHiding.target);
//								// System.out.printf("RIGHT: %s(%d) %s
//								// %s(%d)\n",
//								// firstMatching.label,
//								// firstMatchingEventPair.getSecond(),
//								// pes2.getBRelation(firstMatchingEventPair.getSecond(),
//								// e2p),
//								// pes2.getLabel(e2p), e2p);
//								verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(),
//										firstMatching.label, (Integer) firstHiding.target, firstHiding.label,
//										firstMatchingEventPair.getSecond(), firstMatching.label, e2p,
//										pes2.getLabel(e2p));
//							}
//						} else {
//							e2 = firstMatchingEventPair.getSecond();
//							BitSet succs2 = pes2.getDirectSuccessors(e2);
//
//							succs2.andNot(context2);
//
//							found = false;
//							e2p = null;
//
//							for (int ev = succs2.nextSetBit(0); ev >= 0; ev = succs2.nextSetBit(ev + 1))
//								if (firstHidingLabel.equals(pes2.getLabel(ev))) {
//									found = true;
//									e2p = ev;
//									break;
//								}
//
//							if (found) {
//								context1.set(secondMatchingEventPair.getFirst());
//								context2.set(e2p);
//								context2.set(secondMatchingEventPair.getSecond());
//
//								if (!globalDiffs.contains(context1, context2)) {
//									// System.out.printf("LEFT: %s(%d) %s
//									// %s(%d)\n",
//									// secondMatching.label,
//									// secondMatchingEventPair.getFirst(),
//									// pes1.getBRelation(secondMatchingEventPair.getFirst(),
//									// (Integer)firstHiding.target),
//									// firstHidingLabel,
//									// (Integer)firstHiding.target);
//									// System.out.printf("RIGHT: %s(%d) %s
//									// %s(%d)\n",
//									// secondMatching.label,
//									// secondMatchingEventPair.getSecond(),
//									// pes2.getBRelation(secondMatchingEventPair.getSecond(),
//									// e2p),
//									// pes2.getLabel(e2p), e2p);
//									verbalizeBehDiffFromModelPerspective(secondMatchingEventPair.getFirst(),
//											secondMatching.label, (Integer) firstHiding.target, firstHiding.label,
//											secondMatchingEventPair.getSecond(), secondMatching.label, e2p,
//											pes2.getLabel(e2p));
//								}
//							} else {
//
//								found = false;
//								e2p = null;
//								BitSet directS = pes2.getDirectSuccessors(secondMatchingEventPair.getSecond());
//
//								for (int ev = directS.nextSetBit(0); ev >= 0; ev = directS.nextSetBit(ev + 1))
//									if (firstHidingLabel.equals(pes2.getLabel(ev))) {
//										found = true;
//										e2p = ev;
//										break;
//									}
//
//								if (found) {
//									context1.set(secondMatchingEventPair.getFirst());
//									context2.set(e2p);
//									context2.set(secondMatchingEventPair.getSecond());
//
//									if (!globalDiffs.contains(context1, context2)) {
//										// System.out.printf("LEFT: %s(%d) %s
//										// %s(%d)\n",
//										// secondMatching.label,
//										// secondMatchingEventPair.getFirst(),
//										// pes1.getBRelation(secondMatchingEventPair.getFirst(),
//										// (Integer)firstHiding.target),
//										// firstHidingLabel,
//										// (Integer)firstHiding.target);
//										// System.out.printf("RIGHT: %s(%d) %s
//										// %s(%d)\n",
//										// secondMatching.label,
//										// secondMatchingEventPair.getSecond(),
//										// pes2.getBRelation(secondMatchingEventPair.getSecond(),
//										// e2p),
//										// pes2.getLabel(e2p), e2p);
//										verbalizeBehDiffFromModelPerspective(secondMatchingEventPair.getFirst(),
//												secondMatching.label, (Integer) firstHiding.target, firstHiding.label,
//												secondMatchingEventPair.getSecond(), secondMatching.label, e2p,
//												pes2.getLabel(e2p));
//									}
//								} else {
//									found = false;
//									e2p = null;
//									for (int i = diffIndexes[0]; i > 0; i--) {
//										if (opSeq.get(i).op == Op.RHIDE) {
//											Integer hiddenEvent = (Integer) opSeq.get(i).target;
//											if (firstHidingLabel.equals(pes2.getLabel(hiddenEvent))) {
//												found = true;
//												e2p = hiddenEvent;
//												break;
//											}
//										}
//									}
//									if (found) {
//										context1.set(firstMatchingEventPair.getFirst());
//										context2.set(firstMatchingEventPair.getSecond());
//										context2.set(e2p);
//
//										if (!globalDiffs.contains(context1, context2)) {
//											// System.out.printf("LEFT: %s(%d)
//											// %s %s(%d)\n",
//											// firstMatching.label,
//											// firstMatchingEventPair.getFirst(),
//											// pes1.getBRelation(firstMatchingEventPair.getFirst(),
//											// (Integer)firstHiding.target),
//											// firstHidingLabel,
//											// (Integer)firstHiding.target);
//											// System.out.printf("RIGHT: %s(%d)
//											// %s %s(%d)\n",
//											// firstMatching.label,
//											// firstMatchingEventPair.getSecond(),
//											// pes2.getBRelation(firstMatchingEventPair.getSecond(),
//											// e2p),
//											// pes2.getLabel(e2p), e2p);
//
//											verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(),
//													firstMatching.label, (Integer) firstHiding.target,
//													firstHiding.label, firstMatchingEventPair.getSecond(),
//													firstMatching.label, e2p, pes2.getLabel(e2p));
//										}
//									} else {
//										context1.set(firstMatchingEventPair.getFirst());
//
//										if (!globalDiffs.contains(context1, context2)) {
//											String statement = String.format(
//													"In the log, %s(%d) occurs after %s(%d), while in the model it does not\n",
//													firstHidingLabel, (Integer) firstHiding.target, firstMatching.label,
//													firstMatchingEventPair.getFirst());
//
//											statements.add(statement);
//											// System.out.println(statement);
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//
//			} else {
//				Pair<Operation, Boolean> pair = findLHide(opSeq, diffIndexes, firstHidingLabel);
//				Operation secondHiding = pair.getFirst();
//
//				// Found an LHIDE on an event with the same label
//				if (pair.getSecond()) {
//					context1.set(firstMatchingEventPair.getFirst());
//					context2.set(firstMatchingEventPair.getSecond());
//
//					if (!globalDiffs.contains(context1, context2)) {
//						// System.out.printf("**LEFT: %s(%d) %s %s(%d)\n",
//						// firstMatching.label,
//						// firstMatchingEventPair.getFirst(),
//						// pes1.getBRelation(firstMatchingEventPair.getFirst(),
//						// (Integer)secondHiding.target),
//						// secondHiding.label, (Integer)secondHiding.target);
//						// System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//						// firstMatching.label,
//						// firstMatchingEventPair.getSecond(),
//						// pes2.getBRelation(firstMatchingEventPair.getSecond(),
//						// (Integer)firstHiding.target),
//						// firstHidingLabel, (Integer)firstHiding.target);
//
//						verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(), firstMatching.label,
//								(Integer) secondHiding.target, secondHiding.label, firstMatchingEventPair.getSecond(),
//								firstMatching.label, (Integer) firstHiding.target, firstHiding.label);
//					}
//				} else if (secondHiding != null) {
//					// ========= Symmetric <<==
//					if (!globalDiffs.contains(context1, context2)) {
//						String statement = String.format(
//								"In the log, after the occurrence of %s(%d), %s(%d) is substituted by %s(%d)\n",
//								firstMatching.label, firstMatchingEventPair.getFirst(), firstHiding.label,
//								(Integer) firstHiding.target, secondHiding.label, (Integer) secondHiding.target);
//
//						statements.add(statement);
//						System.out.println(statement);
//					}
//				} else {
//					// No LHIDE found within this Difference Context
//					if (firstMatching.nextState.labels.contains(firstHidingLabel)) {
//						if (!globalDiffs.contains(context1, context2)) {
//							String statement = String.format(
//									"In the model, after the occurrence of %s(%d), %s(%d) is duplicated, while in the log it is not\n",
//									firstMatching.label, firstMatchingEventPair.getFirst(), firstHidingLabel,
//									(Integer) firstHiding.target);
//
//							statements.add(statement);
//							System.out.println(statement);
//						}
//					} else {
//						Integer e1 = firstMatchingEventPair.getFirst();
//						boolean found = false;
//						Integer e1p = null;
//
//						BitSet dconflict = pes1.getDirectConflictSet(e1);
//
//						for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1))
//							if (!pe.equals(e1) && pes1.getBRelation(e1, pe) == BehaviorRelation.CONFLICT
//									&& firstHidingLabel.equals(pes1.getLabel(pe))) {
//								eventsConsideredByConflictRelation.add(pe);
//								found = true;
//								e1p = pe;
//								break;
//							}
//
//						if (found) {
//							context1.or(dconflict);
//							context1.set(e1);
//							context2.set(firstMatchingEventPair.getSecond());
//
//							if (!globalDiffs.contains(context1, context2)) {
//								// System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//								// firstMatching.label,
//								// firstMatchingEventPair.getFirst(),
//								// pes1.getBRelation(firstMatchingEventPair.getFirst(),
//								// e1p),
//								// pes1.getLabel(e1p), e1p);
//								// System.out.printf("RIGHT: %s(%d) %s
//								// %s(%d)\n",
//								// firstMatching.label,
//								// firstMatchingEventPair.getSecond(),
//								// pes2.getBRelation(firstMatchingEventPair.getSecond(),
//								// (Integer)firstHiding.target),
//								// firstHidingLabel,
//								// (Integer)firstHiding.target);
//
//								verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(),
//										firstMatching.label, (Integer) e1p, pes1.getLabel(e1p),
//										firstMatchingEventPair.getSecond(), firstMatching.label,
//										(Integer) firstHiding.target, firstHiding.label);
//
//							}
//						} else {
//							found = false;
//							e1p = null;
//							Pair<Integer, Integer> secondMatchingPair = (Pair) secondMatching.target;
//
//							e1 = secondMatchingPair.getFirst();
//							dconflict = pes1.getDirectConflictSet(e1);
//
//							for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1))
//								if (!pe.equals(e1) && pes1.getBRelation(e1, pe) == BehaviorRelation.CONFLICT
//										&& firstHidingLabel.equals(pes1.getLabel(pe))) {
//									eventsConsideredByConflictRelation.add(pe);
//									found = true;
//									e1p = pe;
//									break;
//								}
//							if (found) {
//								found = false;
//
//								for (Operation succ : descendants.get(firstMatching.nextState))
//									if (succ.op == Op.MATCH) {
//										found = matchSecond(succ.nextState, context2, secondMatchingPair.getSecond());
//										if (found)
//											break;
//									}
//
//								if (found) {
//									if (!globalDiffs.contains(context1, context2)) {
//										String statement = String.format(
//												"In the log, %s(%s) can be skipped, while in the model it cannot\n",
//												translate(context2), context2);
//
//										statements.add(statement);
//										System.out.println(statement);
//									}
//								} else {
//									context1.set(e1p);
//									context1.set(secondMatchingEventPair.getFirst());
//									context2.set(secondMatchingEventPair.getSecond());
//
//									if (!globalDiffs.contains(context1, context2)) {
//										// System.out.printf("LEFT: %s(%d) %s
//										// %s(%d)\n",
//										// pes1.getLabel(e1p), e1p,
//										// pes1.getBRelation(e1p,
//										// secondMatchingEventPair.getFirst()),
//										// secondMatching.label,
//										// secondMatchingEventPair.getFirst());
//										// System.out.printf("RIGHT: %s(%d) %s
//										// %s(%d)\n",
//										// firstHidingLabel,
//										// (Integer)firstHiding.target,
//										// pes2.getBRelation((Integer)firstHiding.target,
//										// secondMatchingEventPair.getSecond()),
//										// secondMatching.label,
//										// secondMatchingEventPair.getSecond());
//										verbalizeBehDiffFromModelPerspective((Integer) e1p, pes1.getLabel(e1p),
//												secondMatchingEventPair.getFirst(), secondMatching.label,
//												(Integer) firstHiding.target, firstHiding.label,
//												secondMatchingEventPair.getSecond(), secondMatching.label);
//									}
//								}
//							} else {
//								e1 = secondMatchingEventPair.getFirst();
//								BitSet preds1 = (BitSet) pes1.getDirectPredecessors(e1).clone();
//
//								preds1.andNot(context1);
//
//								found = false;
//								e1p = null;
//								for (Integer ev = preds1.nextSetBit(0); ev >= 0; ev = preds1.nextSetBit(ev + 1))
//									if (firstHidingLabel.equals(pes1.getLabel(ev))) {
//										found = true;
//										e1p = ev;
//										break;
//									}
//
//								if (found) {
//									context1.set(e1p);
//									context1.set(firstMatchingEventPair.getFirst());
//									context2.set(firstMatchingEventPair.getSecond());
//
//									if (!globalDiffs.contains(context1, context2)) {
//										// System.out.printf("LEFT: %s(%d) %s
//										// %s(%d)\n",
//										// firstMatching.label,
//										// firstMatchingEventPair.getFirst(),
//										// pes1.getBRelation(firstMatchingEventPair.getFirst(),
//										// e1p),
//										// firstHidingLabel, e1p);
//										// System.out.printf("RIGHT: %s(%d) %s
//										// %s(%d)\n",
//										// firstMatching.label,
//										// firstMatchingEventPair.getSecond(),
//										// pes2.getBRelation(firstMatchingEventPair.getSecond(),
//										// (Integer)firstHiding.target),
//										// firstHidingLabel,
//										// (Integer)firstHiding.target);
//										verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(),
//												firstMatching.label, (Integer) e1p, pes1.getLabel(e1p),
//												firstMatchingEventPair.getSecond(), firstMatching.label,
//												(Integer) firstHiding.target, firstHiding.label);
//									}
//								} else {
//
//									found = false;
//									e1p = null;
//
//									BitSet succs = pes1.getDirectSuccessors(firstMatchingEventPair.getFirst());
//									for (int ev = succs.nextSetBit(0); ev >= 0; ev = succs.nextSetBit(ev + 1))
//										if (firstHidingLabel.equals(pes1.getLabel(ev))) {
//											found = true;
//											e1p = ev;
//											break;
//										}
//
//									if (found) {
//										context1.set(e1p);
//										context1.set(secondMatchingEventPair.getFirst());
//										context2.set(secondMatchingEventPair.getSecond());
//
//										if (!globalDiffs.contains(context1, context2)) {
//											// System.out.printf("LEFT: %s(%d)
//											// %s %s(%d)\n",
//											// firstHidingLabel, e1p,
//											// pes1.getBRelation(e1p,
//											// secondMatchingEventPair.getFirst()),
//											// secondMatching.label,
//											// secondMatchingEventPair.getFirst());
//											// System.out.printf("RIGHT: %s(%d)
//											// %s %s(%d)\n",
//											// firstHidingLabel,
//											// (Integer)firstHiding.target,
//											// pes2.getBRelation((Integer)firstHiding.target,
//											// secondMatchingEventPair.getSecond()),
//											// secondMatching.label,
//											// secondMatchingEventPair.getSecond());
//											verbalizeBehDiffFromModelPerspective((Integer) e1p, pes1.getLabel(e1p),
//													secondMatchingEventPair.getFirst(), secondMatching.label,
//													(Integer) firstHiding.target, firstHiding.label,
//													secondMatchingEventPair.getSecond(), secondMatching.label);
//										}
//
//									} else {
//										context2.set((Integer) firstHiding.target);
//										if (!globalDiffs.contains(context1, context2)) {
//											String statement = String.format(
//													"In the model, %s(%d) occurs after %s(%d), while in the log it does not\n",
//													firstHidingLabel, (Integer) firstHiding.target, firstMatching.label,
//													firstMatchingEventPair.getSecond());
//
//											statements.add(statement);
//											System.out.println(statement);
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//
//			// System.out.printf("Context '%s, %s'\n", context1, context2);
//			String statement = String.format("\tFirst match [%s: (%d),(%d)], Second match [%s: (%d),(%d)]\n",
//					firstMatching.label, firstMatchingEventPair.getFirst(), firstMatchingEventPair.getSecond(),
//					secondMatching.label, secondMatchingEventPair.getFirst(), secondMatchingEventPair.getSecond());
//
//			statements.add(statement);
//			// System.out.println(statements);
//
//			Map<Integer, int[]> diffs = globalDiffs.get(context1, context2);
//			if (diffs == null)
//				globalDiffs.put(context1, context2, diffs = new HashMap<>());
//			if (!diffs.containsKey(index))
//				diffs.put(index, diffIndexes);
//		}
//	}
//
//	// private void verbalizeBehDiffFromLogPerspective(Integer e1,
//	// String e1l, Integer e1p, String e1pl,
//	// Integer e2, String e2l, Integer e2p, String e2pl) {
//	// System.out.printf("In the log, %s(%d) %s %s(%d), while in the model
//	// %s(%d) %s %s(%d)\n",
//	// e1l, e1, verbalizeBRel(pes1.getBRelation(e1, e1p)), e1pl, e1p,
//	// e2l, e2, verbalizeBRel(pes2.getBRelation(e2, e2p)), e2pl, e2p);
//	// }
//
//	private void verbalizeBehDiffFromModelPerspective(Integer e1, String e1l, Integer e1p, String e1pl, Integer e2,
//			String e2l, Integer e2p, String e2pl) {
//		String statement = String.format("In the model, %s(%d) %s %s(%d), while in the event log %s(%d) %s %s(%d)\n",
//				e2l, e2, verbalizeBRel(pes2.getBRelation(e2, e2p)), e2pl, e2p, e1l, e1,
//				verbalizeBRel(pes1.getBRelation(e1, e1p)), e1pl, e1p);
//
//		statements.add(statement);
//		System.out.println(statement);
//	}
//
//	private String verbalizeBRel(BehaviorRelation bRelation) {
//		switch (bRelation) {
//		case CAUSALITY:
//			return "occurs before";
//		case INV_CAUSALITY:
//			return "occurs after";
//		case CONCURRENCY:
//			return "occurs concurrently to";
//		case CONFLICT:
//			return "cannot occur in the same run with";
//		default:
//			break;
//		}
//		return null;
//	}
//
//	private boolean matchSecond(State curr, BitSet context2, Integer ev2) {
//		boolean result = false;
//		for (Operation op : descendants.get(curr)) {
//			switch (op.op) {
//			case MATCH:
//				// case MATCHNSHIFT:
//				Pair<Integer, Integer> pair = (Pair) op.target;
//				if (context2.get(pair.getSecond()))
//					result = matchSecond(op.nextState, context2, ev2);
//				else if (pair.getSecond().equals(ev2))
//					return true;
//				break;
//			case RHIDE:
//				// case RHIDENSHIFT:
//				if (context2.get(((Pair<Integer, Integer>) op.target).getSecond()))
//					result = matchSecond(op.nextState, context2, ev2);
//				break;
//			default:
//				break;
//			}
//		}
//		return result;
//	}
//
//	private Pair<Operation, Boolean> findLHide(List<Operation> opSeq, int[] diffIndexes, String firstHidingLabel) {
//		Operation firstRHiding = null;
//		for (int i = diffIndexes[1] + 1; i < diffIndexes[2]; i++) {
//			Operation secondHidingOperation = opSeq.get(i);
//			if (secondHidingOperation.op == Op.LHIDE) {
//				if (firstHidingLabel.equals(secondHidingOperation.label)) {
//					// System.out.println("Found a matching for hidden event: "
//					// + secondHidingOperation.target);
//					return new Pair<>(secondHidingOperation, true);
//				} else if (firstRHiding == null)
//					firstRHiding = secondHidingOperation;
//			}
//		}
//		return new Pair<>(firstRHiding, false);
//	}
//
//	private Pair<Operation, Boolean> findRHide(List<Operation> opSeq, int[] diffIndexes, String firstHidingLabel) {
//		Operation firstRHiding = null;
//		for (int i = diffIndexes[1] + 1; i < diffIndexes[2]; i++) {
//			Operation secondHidingOperation = opSeq.get(i);
//			if (secondHidingOperation.op == Op.RHIDE) {
//				if (firstHidingLabel.equals(secondHidingOperation.label)) {
//					// System.out.println("Found a matching for hidden event: "
//					// + secondHidingOperation.target);
//					return new Pair<>(secondHidingOperation, true);
//				} else if (firstRHiding == null)
//					firstRHiding = secondHidingOperation;
//			}
//		}
//		return new Pair<>(firstRHiding, false);
//	}
//
//	private List<int[]> getADiffContexts(List<Operation> opSeq) {
//		List<int[]> differences = new ArrayList<>();
//		int[] diffIndexes = null;
//		boolean visibleEventHasBeenHidden = false;
//
//		State pred = root;
//
//		for (int i = 0; i < opSeq.size(); i++) {
//			Operation curr = opSeq.get(i);
//
//			State state = curr.nextState;
//			Map<Multiset<String>, State> map = stateSpace.get(state.c1, state.c2);
//			if (map == null)
//				stateSpace.put(state.c1, state.c2, map = new HashMap<>());
//			if (map.containsKey(state.labels)) {
//				state = map.get(state.labels);
//				curr.nextState = state;
//			} else
//				map.put(state.labels, state);
//
//			boolean found = false;
//			for (Operation desc : descendants.get(pred))
//				if (desc.op == curr.op) {
//					if (curr.op == Op.MATCH) {
//						Pair<Integer, Integer> pair1 = (Pair) curr.target;
//						Pair<Integer, Integer> pair2 = (Pair) desc.target;
//						if (pair1.equals(pair2)) {
//							found = true;
//							break;
//						}
//					} else {
//						Integer ev1 = (Integer) curr.target;
//						Integer ev2 = (Integer) desc.target;
//						if (ev1.equals(ev2)) {
//							found = true;
//							break;
//						}
//					}
//				}
//			if (!found)
//				descendants.put(pred, curr);
//			pred = state;
//
//			if (curr.op == Op.MATCH)
//				unobservedEvents.remove((Integer) ((Pair) curr.target).getSecond());
//			else // if (curr.op != Op.LHIDE)
//				unobservedEvents.remove((Integer) curr.target);
//
//			if (diffIndexes == null) {
//				if (curr.op == Op.LHIDE) {
//					// System.out.println("Found earliest discrepancy (LHIDE): "
//					// + curr);
//					diffIndexes = new int[3];
//					diffIndexes[0] = i - 1;
//					diffIndexes[1] = i;
//					visibleEventHasBeenHidden = true;
//				} else if (curr.op == Op.RHIDE) {
//					Integer hiddenEvent = (Integer) curr.target;
//					// System.out.println("Found earliest discrepancy (RHIDE): "
//					// + curr);
//					diffIndexes = new int[3];
//					diffIndexes[0] = i - 1;
//
//					visibleEventHasBeenHidden = true; // !pes2.getInvisibleEvents().contains(hiddenEvent);
//					if (visibleEventHasBeenHidden)
//						diffIndexes[1] = i;
//				}
//			} else {
//				if (curr.op == Op.MATCH) {
//					if (visibleEventHasBeenHidden) {
//						// System.out.println("==> Context: " +
//						// opSeq.subList(diffIndexes[0], i+1));
//
//						diffIndexes[2] = i;
//						differences.add(diffIndexes);
//					} else {
//						// System.out.println("==> Context discarded: No visible
//						// event has been hidden");
//					}
//					diffIndexes = null;
//				} else {
//					if (!visibleEventHasBeenHidden) {
//						if (curr.op == Op.LHIDE) {
//							diffIndexes[1] = i;
//							visibleEventHasBeenHidden = true;
//						} else {
//							visibleEventHasBeenHidden = true; // !pes2.getInvisibleEvents().contains((Integer)curr.target);
//							if (visibleEventHasBeenHidden)
//								diffIndexes[1] = i;
//						}
//					}
//				}
//			}
//		}
//		return differences;
//	}
//
//	private void processUnobservedEvents() {
//
//		System.out.println("Unobserved: " + unobservedEvents);
//
//		// Map<Multiset<Integer>, Multiset<Integer>> footprints =
//		// pes2.getFootprints();
//		Set<Integer> toRemove = new HashSet<>();
//		// for (Multiset<Integer> footprint: footprints.keySet()) {
//		// if (unobservedEvents.containsAll(footprint)) {
//		// BitSet cycle = footprints.get(footprint);
//		// System.out.printf("In the model, the interval %s is repeated multiple
//		// times, while in the log it is not",
//		// translate(cycle));
//		// toRemove.addAll(footprints.get(footprint));
//		// }
//		// }
//
//		unobservedEvents.removeAll(toRemove);
//
//		Set<Integer> primeUnobservedEvents = new HashSet<>();
//
//		for (Integer ev : unobservedEvents) {
//			boolean found = false;
//			BitSet predecessors = pes2.getDirectPredecessors(ev);
//			for (int pred = predecessors.nextSetBit(0); pred >= 0; pred = predecessors.nextSetBit(pred + 1))
//				if (unobservedEvents.contains(pred)) {
//					found = true;
//					break;
//				}
//			if (!found)
//				primeUnobservedEvents.add(ev);
//		}
//
//		// We discard all the events that, in spite of not being explicitly
//		// represented in the PSPs, have already been considered for
//		// verbalization
//		primeUnobservedEvents.removeAll(eventsConsideredByConflictRelation);
//
//		for (Integer ev : primeUnobservedEvents) {
//			// if (pes2.getInvisibleEvents().contains(ev)) {
//			// BitSet _causes = pes2.getLocalConfiguration(ev);
//			// _causes.clear(ev);
//			// BitSet causes = new BitSet();
//			// for (int event = _causes.nextSetBit(0); event >=0; event =
//			// _causes.nextSetBit(event + 1))
//			// causes.set(event);
//			// BitSet pe2 = pes2.getPossibleExtensions(causes);
//			//
//			// for (int event = pe2.nextSetBit(0); event >= 0; event =
//			// pe2.nextSetBit(event+1))
//			// if (!pes2.getInvisibleEvents().contains(event) &&
//			// pes2.getBRelation(ev, event) == BehaviorRelation.CONFLICT)
//			// System.out.printf(" In the model, '%s' can be skipped, while in
//			// the event log it cannot\n", pes2.getLabel(event));
//			// } else
//			{
//				System.out.printf("    Task '%s' appears in PES2 and not in PES1\n", pes2.getLabel(ev));
//			}
//		}
//	}
//
//	private Object translate(BitSet bs) {
//		Set<String> set = new HashSet<>();
//
//		for (int ev = bs.nextSetBit(0); ev >= 0; ev = bs.nextSetBit(ev + 1))
//			set.add(pes2.getLabel(ev));
//		return set;
//	}
//
//	public Set<String> getStatements() {
//		return statements;
//	}
//
//	public List<List<Operation>> getOpSeqs() {
//		return this.opSeqs;
//	}
//
//	public Differences getDifferences() {
//		return differences;
//	}
}
