/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

import com.google.common.collect.*;
import ee.ut.bpmn.BPMNReader;
import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Op;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.State;
import ee.ut.eventstr.comparison.differences.*;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.org.processmining.framework.util.Pair;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.hypergraph.abs.GObject;
import org.jbpt.pm.*;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;

public class DiffMLGraphicalVerbalizerNew {
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

    private List<List<Operation>> opSeqs;
    private Set<Operation> lhideOps;
    private Set<Operation> rhideOps;
    private Table<BitSet, Multiset<Integer>, Map<Multiset<String>, State>> stateSpace;
    private State root;
    private Multimap<State, Operation> descendants;
    private Multimap<State, State> ancestors;

    private Set<Operation> operations;

    private Map<State, Operation> lastMatchMap;

    public static final boolean DEBUG = false;


    public DiffMLGraphicalVerbalizerNew(ModelAbstractions model, XLog log, HashSet<String> silents) throws Exception {
        this.model = model;
        this.net = model.getNet();
        silents.add("_0_");
        silents.add("_1_");

        for (Transition t : net.getTransitions())
            if (!model.getLabels().contains(t.getName()))
                silents.add(t.getName());

        // Compute the PES of the model
        this.pes2 = model.getUnfoldingPESSemantics(model.getNet(), silents);
        this.expprefix = new ExpandedPomsetPrefix<Integer>(pes2);

        // Compute the PES of the log
        this.logpes = getLogPES(log);
        this.pes1 = new PESSemantics<Integer>(logpes);

        this.commonLabels = new HashSet<String>();
        this.commonLabels.addAll(model.getLabels());
        this.commonLabels.addAll(pes1.getLabels());
        this.commonLabels.removeAll(silents);

        this.differences = new DifferencesML();

        this.opSeqs = new ArrayList<>();
        this.lhideOps = new HashSet<>();
        this.rhideOps = new HashSet<>();
        this.descendants = HashMultimap.create();
        this.ancestors = HashMultimap.create();

        this.stateSpace = HashBasedTable.create();
        this.root = new State(new BitSet(), HashMultiset.<String>create(), HashMultiset.<Integer>create());

        this.lastMatchMap = new HashMap<>();
    }

    private PrimeEventStructure<Integer> getLogPES(XLog log) throws Exception {
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
        // Merge all PSPs and eliminate non-optimal
        for (List<Operation> opSeq : opSeqs)
            addPSPBranchToGlobalPSP(opSeq);
        prune();

        // Keep track of all Hide operations and the used ones.
        // The non-used Hide operations will be spelled out at
        // the end with the pattern TaskABS.
        HashSet<Operation> allHides = new HashSet<>();
        HashSet<Operation> usedHides = new HashSet<>();

        // Maximal matches
        TreeSet<State> leaves = new TreeSet<>(new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                if(o1.cost != o2.cost)
                    return o1.cost - o2.cost;
                else if(o1.c1.cardinality() != o2.c1.cardinality())
                    return o2.c1.cardinality() - o1.c1.cardinality();
                else if(o1.c2.size() != o2.c2.size())
                    return o2.c2.size() - o1.c2.size();

                return 1;
            }
        });
        leaves.addAll(getAllLeaves());

        // Traverse the optimal matches captured in the PSP,
        // one maximal match at a time
        for (State leaf : leaves) {
            // Path in the PSP for the maximal matching
            LinkedList<Operation> path2Leaf = getPath(leaf);

            // Left and right Hide operations in the path
            LinkedList<Operation> lhide = new LinkedList<>();
            LinkedList<Operation> rhide = new LinkedList<>();

            for (Operation op : path2Leaf) {
                expprefix.mark(op.nextState, op);
                if (isHide(op)) {
                    allHides.add(op);

                    if (isLHide(op)) lhide.add(op);
                    else rhide.add(op);
                }
            }

            filterOutSilent(lhide);
            filterOutSilent(rhide);
            filterOutSilent(allHides);

            filterCausalConcMismatches(lhide, rhide, path2Leaf);

            /* Start verbalization of Intervals-related differences */

            LinkedList<Operation> lhideCommonSL = new LinkedList<>(lhide);
            LinkedList<Operation> rhideCommonSL = new LinkedList<>(rhide);

            while(!lhideCommonSL.isEmpty() || !rhideCommonSL.isEmpty()){
                Pair<LinkedList<Operation>, LinkedList<Operation>> commonSubL = getLCList(lhideCommonSL, rhideCommonSL, path2Leaf);

                if(commonSubL.getFirst().isEmpty() || commonSubL.getSecond().isEmpty())
                    break;
                else if(!commonSubL.getFirst().isEmpty() && !commonSubL.getSecond().isEmpty()) {
                    // Priority No. 1,2 --  TASKRELOC
                    lhide.removeAll(commonSubL.getFirst());
                    rhide.removeAll(commonSubL.getSecond());

                    usedHides.addAll(commonSubL.getFirst());
                    usedHides.addAll(commonSubL.getSecond());

                    DifferenceML difference = getTASKRELOCDiff(commonSubL, path2Leaf);
                    if(difference != null) differences.add(difference);
                }

                lhideCommonSL.removeAll(commonSubL.getFirst());
                rhideCommonSL.removeAll(commonSubL.getSecond());
            }

            // Priority No. 3 --  TASKSKIP
            HashSet<LinkedList<Operation>> contiguousRHide = getContiguousGroups(path2Leaf, rhide);
            HashSet<LinkedList<Operation>> toDeleteRHG = new HashSet<>();
            for (LinkedList<Operation> hides : contiguousRHide) {
                int i = path2Leaf.indexOf(hides.get(0))-1;

                if (i > 0 && check4Skip(path2Leaf, i, hides)) {
                    toDeleteRHG.add(hides);
                    rhide.removeAll(hides);

                    usedHides.addAll(hides);

                    DifferenceML difference = getTASKSKIPDiff(hides, path2Leaf, i);
                    if(difference != null) differences.add(difference);
                }
            }
            contiguousRHide.removeAll(toDeleteRHG);
            contiguousRHide = filterOutAllEmpty(contiguousRHide);

            // Priority No. 4 --  TASKSKIP
            HashSet<LinkedList<Operation>> contiguousLHide = getContiguousGroups(path2Leaf, lhide);
            HashSet<LinkedList<Operation>> toDeleteLHG = new HashSet<>();
            for (LinkedList<Operation> hides : contiguousLHide) {
                int i = path2Leaf.indexOf(hides.get(0))-1;

                if (i > 0 && check4Skip(path2Leaf, i, hides)) {
                    toDeleteLHG.add(hides);
                    lhide.removeAll(hides);

                    usedHides.addAll(hides);

                    DifferenceML difference = getTASKRELOCDiff2(hides, path2Leaf, i);
                    if(difference != null) differences.add(difference);
                }
            }
            contiguousLHide.removeAll(toDeleteLHG);

            //Priority No. 5 -- UNMREPETITION
            toDeleteRHG = new HashSet<>();
            for (LinkedList<Operation> hides : contiguousRHide) {
                int i = path2Leaf.indexOf(hides.get(0));
                Pair<List<Operation>, LinkedList<Operation>> unmRepIntervalPair = look4UnmRep(path2Leaf, i, hides);

                if (unmRepIntervalPair != null) {
                    LinkedList<Operation> unmRepInterval = unmRepIntervalPair.getSecond();
                    toDeleteRHG.add(unmRepInterval);
                    rhide.removeAll(unmRepInterval);

                    usedHides.addAll(unmRepInterval);

                    DifferenceML difference = getUNMRepetitionDiff(hides, path2Leaf, i);
                    if(difference != null) differences.add(difference);
                    i += unmRepInterval.size() + 2;
                }
            }
            contiguousRHide.removeAll(toDeleteRHG);

            //Priority No. 6 -- UNMREPETITION
            toDeleteLHG = new HashSet<>();
            for (LinkedList<Operation> hides : contiguousLHide) {
                int i = path2Leaf.indexOf(hides.get(0));
                Pair<List<Operation>, LinkedList<Operation>> unmRepIntervalPair = look4UnmRep(path2Leaf, i, hides);

                if (unmRepIntervalPair != null) {
                    LinkedList<Operation> unmRepInterval = unmRepIntervalPair.getSecond();

                    if(usedHides.containsAll(unmRepInterval))
                        continue;

                    toDeleteLHG.add(unmRepInterval);
                    lhide.removeAll(unmRepInterval);

                    usedHides.addAll(unmRepInterval);

                    DifferenceML difference = getUNMRepetitionDiff2(unmRepIntervalPair, path2Leaf, i, hides);
                    if(difference != null) differences.add(difference);
                    i += unmRepInterval.size() + 2;
                }
            }
            contiguousLHide.removeAll(toDeleteLHG);

            toDeleteLHG = new HashSet<>();
            toDeleteRHG = new HashSet<>();
            // Priority No. 8 -- TASKSUB
            if (!contiguousLHide.isEmpty() && !contiguousRHide.isEmpty()) {
                List<LinkedList<Operation>> sourceGroups = new LinkedList<>(contiguousLHide);
                List<LinkedList<Operation>> targetGroups = new LinkedList<>(contiguousRHide);

                if (contiguousRHide.size() < contiguousLHide.size()) {
                    sourceGroups = new LinkedList<>(contiguousRHide);
                    targetGroups = new LinkedList<>(contiguousLHide);
                }

                Collections.sort(sourceGroups, new Comparator<LinkedList<Operation>>() {
                    @Override
                    public int compare(LinkedList<Operation> o1, LinkedList<Operation> o2) {
                        return o2.size() - o1.size();
                    }
                });
                Collections.sort(targetGroups, new Comparator<LinkedList<Operation>>() {
                    @Override
                    public int compare(LinkedList<Operation> o1, LinkedList<Operation> o2) {
                        return o2.size() - o1.size();
                    }
                });

                for (LinkedList<Operation> source : sourceGroups) {
                    if(!usedHides.containsAll(source))
                        for (LinkedList<Operation> target : targetGroups) {
                            if(usedHides.containsAll(target))
                                continue;

                            DifferenceML difference = getTASKSUBDiff(path2Leaf, source, target);
                            if(difference != null) differences.add(difference);

                            if (contiguousLHide.contains(source)) {
                                toDeleteLHG.add(source);
                                toDeleteRHG.add(target);
                            } else {
                                toDeleteLHG.add(target);
                                toDeleteRHG.add(source);
                            }

                            usedHides.addAll(source);
                            usedHides.addAll(target);
                            break;
                        }

                    targetGroups.removeAll(toDeleteLHG);
                    targetGroups.removeAll(toDeleteRHG);
                }
                sourceGroups.removeAll(toDeleteLHG);
                sourceGroups.removeAll(toDeleteRHG);
            } else if (contiguousLHide.size() == 0 && contiguousRHide.size() > 0) {
                for (LinkedList<Operation> group : contiguousRHide) {
                    int i = path2Leaf.indexOf(group.get(0)) - 1;
                    LinkedList<Operation> subt = find4TaskSub(path2Leaf, i, group, Op.LHIDE);
                    if (isMatch(path2Leaf.get(i)) && subt != null) {
                        toDeleteRHG.add(group);
                        usedHides.addAll(subt);

                        float impact = computeImpact(getMinimum(subt));
                        System.out.println("Impact  = " + impact);

                        DifferenceML difference = getTASKSUBDiff(path2Leaf, group, subt);
                        if(difference != null) differences.add(difference);
                    }
                }
            } else if (contiguousLHide.size() > 0 && contiguousRHide.size() == 0) {
                for (LinkedList<Operation> group : contiguousLHide) {
                    int i = path2Leaf.indexOf(group.get(0)) - 1;
                    LinkedList<Operation> subt = find4TaskSub(path2Leaf, i, group, Op.RHIDE);
                    if (isMatch(path2Leaf.get(i)) && subt != null) {
                        toDeleteLHG.add(group);
                        usedHides.addAll(subt);

                        DifferenceML difference = getTASKSUBDiff(path2Leaf, subt, group);
                        if(difference != null) differences.add(difference);
                    }
                }
            }

            contiguousRHide.removeAll(toDeleteRHG);
            contiguousLHide.removeAll(toDeleteLHG);
            /* End verbalization of Intervals-related differences */
        }

        // Detect UNOBSACYCLIC model behaviour
        Multimap<State, List<Integer>> acyclicIntervals = expprefix.getAdditionalAcyclicIntervals();

        // Priority No. 4 --  TASKSKIP -- EXPPrefix
        Multimap<State, List<Integer>> optionalIntervals = expprefix.getOptionalAcyclicIntervals();
        for(State state : optionalIntervals.keySet()){
            for(List<Integer> interval : optionalIntervals.get(state)){
                DifferenceML difference = getTASKRELOCDiff2(interval, state);
                if(difference != null) differences.add(difference);

                if(acyclicIntervals.containsKey(state)){
                    Pair<State, List<Integer>> continuation = isContSkip(state, interval, acyclicIntervals);
                    if(continuation != null) {
                        acyclicIntervals.remove(state, continuation.getSecond());
                        break;
                    }
                }
            }
        }

        // Detect UNOBSCYCLIC behaviour
        verbalizeAdditionalCyclicModelBehavior();

        if (usedHides.containsAll(allHides))
            return;

        /* Start verbalization of Binary-related differences */
        for (State leaf : leaves) {
            LinkedList<Operation> path2Leaf = getPath(leaf);
            LinkedList<Operation> lhide = new LinkedList<>();
            LinkedList<Operation> rhide = new LinkedList<>();

            for (Operation op : path2Leaf)
                if (isHide(op)) {
                    if (isLHide(op)) lhide.add(op);
                    else rhide.add(op);
                }

            lhide.removeAll(usedHides);
            rhide.removeAll(usedHides);

            // Priority No. 9 && 10 -- CAUSCONC
            check4CAUSCONCMist(path2Leaf, lhide, rhide, usedHides);
            if (lhide.isEmpty() && rhide.isEmpty())
                continue;

            // Priority No. 11-14 -- CONFLICT
            check4ConfMistmatches(path2Leaf, lhide, rhide, usedHides, acyclicIntervals);
        }
        /* End verbalization of Binary-related differences */

        /* Start verbalization of Unary-related differences */
        HashSet<Operation> hides2Report = new HashSet<>(allHides);
        hides2Report.removeAll(usedHides);
        while (!hides2Report.isEmpty()) {
            Operation op = hides2Report.iterator().next();
            if (!usedHides.contains(op)) {
                Operation oppositeOp = findOpposite(op, allHides);

                if (oppositeOp != null) {
                    usedHides.add(op);
                    usedHides.add(oppositeOp);

                    hides2Report = new HashSet<>(allHides);
                    hides2Report.removeAll(usedHides);

                    Operation logCont = oppositeOp;
                    Operation modelCont = op;
                    if (isLHide(op)) {
                        logCont = op;
                        modelCont = oppositeOp;
                    }

                    DifferenceML difference = getTASKINSDiff(op, logCont, modelCont);
                    if(difference != null) differences.add(difference);
                } else {
                    usedHides.add(op);

                    hides2Report = new HashSet<>(allHides);
                    hides2Report.removeAll(usedHides);

                    if (isLHide(op)) {
                        BitSet historyEvt = pes1.getLocalConfiguration((Integer) op.target);
                        historyEvt.set((Integer) op.target, false);
                        BitSet ext = pes1.getDirectSuccessors((Integer) op.target);
                        HashSet<String> extList = new HashSet<>();
                        for (int i = ext.nextSetBit(0); i >= 0; i = ext.nextSetBit(i + 1))
                            extList.add(pes1.getLabel(i));

                        DifferenceML difference = getTASKABSLDiff(op, historyEvt, extList);
                        if(difference != null) differences.add(difference);
                    } else {
                        BitSet historyEvt = pes2.getLocalConfiguration((Integer) op.target);
                        historyEvt.set((Integer) op.target, false);
                        Collection<Integer> ext = pes2.getDirectSuccessors((Integer) op.target);
                        HashSet<String> extList = new HashSet<>();
                        for (Integer evt : ext)
                            extList.add(pes2.getLabel(evt));

                        DifferenceML difference = getTASKABSMDiff(op, historyEvt, extList);
                        if(difference != null) differences.add(difference);
                    }
                }
            }
        }
        /* End verbalization of Unary-related differences */

        // Verbalize remaining acyclic behavior
        verbalizeAdditionalAcyclicModelBehavior(acyclicIntervals.entries());

        System.out.println(toDot());
    }

    private DifferenceML getTASKABSMDiff(Operation op, BitSet historyEvt, HashSet<String> extList) {
        float impact = 1.0f;

        Operation nextSt = descendants.get(op.nextState).iterator().next();

        BitSet modelConf = (BitSet) historyEvt.clone();
        modelConf.set((Integer)op.target, false);
        String modelContextSt = getContext(modelConf, pes2);

        HashSet<String> pruned = new HashSet<>();
        for(String label : extList) {
            if (label.equals("_0_"))
                pruned.add("start event");
            else if(label.equals("_1_"))
                pruned.add("end event");
            else
                pruned.add(label);
        }

        String sentence = verbalizeTaskAbs("model", op.label, modelContextSt, pruned.toString());

        if(!commonLabels.contains(pes2.getLabel((Integer) op.target)))
            return null;

        FlowNode taskToHide = model.getTaskFromEvent((Integer) op.target);

        FlowNode startTask = model.getBpmnModel().getDirectPredecessors(taskToHide).iterator().next();
        List<String> start = new LinkedList<>();
        start.add(startTask.getId());

        FlowNode endTask = model.getBpmnModel().getDirectSuccessors(taskToHide).iterator().next();
        List<String> end = new LinkedList<>();
        end.add(endTask.getId());

        List<String> greys = new LinkedList<>();
        greys.add(taskToHide.getId());
        greys.add(model.getBpmnModel().getDirectedEdge(startTask, taskToHide).getId());
        greys.add(model.getBpmnModel().getDirectedEdge(taskToHide, endTask).getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setGreys(greys);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setType("TASKABSModel");

//        printDiff(start, end, greys, new LinkedList<String>());

        return difference;
    }

    private DifferenceML getTASKABSLDiff(Operation op, BitSet historyEvt, HashSet<String> extList) {
        float occurrence = pes1.getPES().getEventOccurrenceCount((Integer) op.target);
        float impact = computeImpact(occurrence);

        Operation nextSt = descendants.get(op.nextState).iterator().next();

        BitSet logConf = (BitSet) historyEvt.clone();
        logConf.set((Integer)op.target, false);
        String logContextSt = getContext(logConf, pes1.getPES());

        List<String> newTasks = new LinkedList<>();
        newTasks.add(op.label);

        LinkedList<Operation> path = getPath(op.nextState);
        int indexPath = path.size() - 2;
        Operation endState = path.get(indexPath);
        while(isLHide(endState) && indexPath >= 0)
            endState = path.get(--indexPath);

        Integer event = isMatch(endState) ? ((Pair<Integer,Integer>) endState.target).getSecond() : (Integer)endState.target;

        LinkedList<Integer> historyEvts = new LinkedList<>();
        BitSet bs = pes2.getLocalConfiguration(event);
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1))
            historyEvts.add(i);

        Collections.sort(historyEvts, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return pes2.getLocalConfiguration(o2).cardinality() - pes2.getLocalConfiguration(o1).cardinality();
            }
        });

        FlowNode task = model.getEnd();
        Iterator<Integer> it = historyEvts.iterator();
        while(it.hasNext()){
            Integer next = it.next();
            if(model.getTaskFromEvent(next) != null){
                task = model.getTaskFromEvent(next);
                break;
            }
        }

        if(task == model.getEnd())
            task = model.getStart();

        List<String> start = new LinkedList<>();
        start.add(task.getId());

        FlowNode endTask = model.getBpmnModel().getDirectSuccessors(task).iterator().next();
        List<String> end = new LinkedList<>();
        end.add(endTask.getId());

        List<String> greys = new LinkedList<>();
        greys.add(model.getBpmnModel().getDirectedEdge(task, endTask).getId());

        HashSet<String> pruned = new HashSet<>();
        for(String label : extList) {
            if (label.equals("_0_"))
                pruned.add("start event");
            else if(label.equals("_1_"))
                pruned.add("end event");
            else
                pruned.add(label);
        }

        String sentence = verbalizeTaskAbs("log", op.label, logContextSt, pruned.toString());
        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setNewTasks(newTasks);
        difference.setGreys(greys);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setType("TASKABSLog");

//        printDiff(start, end, new LinkedList<String>(), new LinkedList<String>());

        return difference;
    }

    private DifferenceML getTASKINSDiff(Operation op, Operation logCont, Operation modelCont) {
        float occurrence = pes1.getPES().getEventOccurrenceCount((Integer)logCont.target);
        float impact = computeImpact(occurrence);

        BitSet logConf = (BitSet) logCont.nextState.c1.clone();
        logConf.set((Integer)logCont.target, false);
        BitSet modelConf = (BitSet) modelCont.nextState.c1.clone();
        modelConf.set((Integer)logCont.target, false);
        String logContextSt = getContext(logConf, pes1.getPES());
        String modelContextSt = getContext(modelConf, pes1.getPES());

        String sentence = verbalizeTaskIns(op.label, logContextSt, modelContextSt);
        System.out.println(sentence + " -- Impact = " + impact);

        Integer event = (Integer)modelCont.target;
        if(!commonLabels.contains(pes2.getLabel(event)))
            return null;

        List<String> a = new LinkedList<>();
        a.add(model.getTaskFromEvent(event).getId());

        HashSet<Integer> ids = new HashSet<>();
        ids.add((Integer) modelCont.target);

        HashSet<GObject> modelElements = model.getModelSegment(ids);
        HashSet<FlowNode> firstEl = findFirst(modelElements);
        HashSet<FlowNode> lastEl = findLast(modelElements);

        List<String> start = new LinkedList<>();
        List<String> greys = new ArrayList<>();
        List<String> end = new LinkedList<>();

        for(GObject elem : modelElements)
            a.add(elem.getId());

        for(FlowNode gObj: firstEl) {
            for(FlowNode st : model.getBpmnModel().getDirectPredecessors(gObj))
                start.add(st.getId());

            for (ControlFlow flow : model.getBpmnModel().getIncomingControlFlow(gObj))
                greys.add(flow.getId());
        }

        for(FlowNode gObj: lastEl) {
            for(FlowNode en : model.getBpmnModel().getDirectSuccessors(gObj))
                end.add(en.getId());

            for (ControlFlow flow : model.getBpmnModel().getOutgoingControlFlow(gObj))
                greys.add(flow.getId());
        }

        DifferenceML difference = new DifferenceML(impact);
        difference.setA(a);
        difference.setStart(start);
        difference.setGreys(greys);
        difference.setEnd(end);
        difference.setSentence(sentence);
        difference.setType("TASKABSLog");

//        printDiff(start, end, greys, a);

        return difference;
    }

    private DifferenceML getTASKSUBDiff(LinkedList<Operation> path2Leaf, LinkedList<Operation> source, LinkedList<Operation> target) {
        LinkedList<Operation> prunedSource = new LinkedList<>();
        LinkedList<Operation> prunedTarget = new LinkedList<>();

        for(Operation op: source)
            if(commonLabels.contains(op.label))
                prunedSource.add(op);

        for(Operation op: target)
            if(commonLabels.contains(op.label))
                prunedTarget.add(op);

        if(prunedSource.isEmpty() || prunedTarget.isEmpty())
            return null;

        int i = Math.min(path2Leaf.indexOf(source.get(0)), path2Leaf.indexOf(target.get(0))) - 1;

        float impact = 1;
        String sentence = "";
        LinkedList<Operation> rHidesOps;
        LinkedList<String> newTasks = new LinkedList<>();

        if(prunedSource.getFirst().op.equals(Op.RHIDE)) {
            impact = computeImpact(getMinimum(prunedTarget));
            rHidesOps = prunedSource;
        }else {
            impact = computeImpact(getMinimum(prunedSource));
            rHidesOps = prunedTarget;
        }

//        if (path2Leaf.indexOf(source.get(0)) < path2Leaf.indexOf(target.get(0)))
        if(isRHide(source.getFirst()))
            sentence = verbalizeTaskSub("model", path2Leaf.get(i).label, source, target);
        else
            sentence = verbalizeTaskSub("model", path2Leaf.get(i).label, target, source);

        System.out.println(sentence + " -- Impact = " + impact);

        HashSet<Integer> ids = new HashSet<>();
        for(Operation op : rHidesOps)
            if(commonLabels.contains(pes2.getLabel((Integer) op.target)))
                ids.add((Integer) op.target);

        if(ids.isEmpty())
            return null;

        HashSet<GObject> modelElements = model.getModelSegment(ids);
        List<String> grey = new ArrayList<>();
        for(GObject gObj: modelElements)
            grey.add(gObj.getId());

        HashSet<FlowNode> firstEl = findFirst(modelElements);
        HashSet<FlowNode> lastEl = findLast(modelElements);

        List<String> start = new LinkedList<>();
        List<String> end = new LinkedList<>();

        for(FlowNode gObj: firstEl)
            for(FlowNode st : model.getBpmnModel().getDirectPredecessors(gObj))
                start.add(st.getId());

        for(FlowNode gObj: lastEl)
            for(FlowNode en : model.getBpmnModel().getDirectSuccessors(gObj))
                end.add(en.getId());

        if(source == rHidesOps) {
            for (Operation tgt : target)
                newTasks.add(tgt.label);
        }else{
            for (Operation src : source)
                newTasks.add(src.label);
        }

        DifferenceML difference = new DifferenceML(impact);
        difference.setStart(start);
        difference.setGreys(grey);
        difference.setNewTasks(newTasks);
        difference.setEnd(end);
        difference.setSentence(sentence);
        difference.setType("TASKSUB");

        // DEBUG
//        printDiff(start, end, grey);

        return difference;
    }

    public boolean containsIntervalLabel(LinkedList<Operation> interval, String label){
        for(Operation op : interval)
            if(op.label.equals(label))
                return  true;

        return false;
    }

    private DifferenceML getUNMRepetitionDiff2(Pair<List<Operation>, LinkedList<Operation>> pairMatches, LinkedList<Operation> path2Leaf, int i, LinkedList<Operation> hides) {
        float impact = computeImpact(getMinimum(pairMatches.getSecond()));
        int index = path2Leaf.indexOf(hides.getFirst())-1;
        Operation context = path2Leaf.get(index);
        while(!commonLabels.contains(context.label) && index >= 0)
            context = path2Leaf.get(--index);

        if(index < 0)
            context = path2Leaf.get(0);

        boolean isUNMRep = true;

        if((isRHide(context) || isMatch(context)) && !containsIntervalLabel(hides,context.label))
            return getDuplicationDiff(pairMatches, path2Leaf, i, hides, impact, context);

        String sentence = verbalizeUnmRep("log", path2Leaf.get(i).label, hides);
        HashSet<Integer> ids = new HashSet<>();
        for(Operation op : pairMatches.getFirst())
            if(commonLabels.contains(pes2.getLabel(((Pair<Integer,Integer>)op.target).getSecond())))
                ids.add(((Pair<Integer,Integer>)op.target).getSecond());

        if(ids.isEmpty())
            return null;

        LinkedList<Integer> idSort = new LinkedList<>(ids);

        Collections.sort(idSort, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return pes2.getLocalConfiguration(o1).cardinality() - pes2.getLocalConfiguration(o2).cardinality();
            }
        });

        List<String> a = new ArrayList<>();
        for(Integer gObj: idSort)
            a.add(model.getTaskFromEvent(gObj).getId());

        HashSet<GObject> modelElements = model.getModelSegment(ids);
        HashSet<FlowNode> firstEl = findFirst(modelElements);
        HashSet<FlowNode> lastEl = findLast(modelElements);

        for(GObject additionalTask : modelElements)
            if(!a.contains(additionalTask.getId()) && additionalTask instanceof Activity)
                a.add(additionalTask.getId());

        List<String> start = new LinkedList<>();
        List<String> end = new LinkedList<>();
        List<String> greys = new LinkedList<>();

        for(FlowNode gObj: firstEl)
            for(FlowNode st : model.getBpmnModel().getDirectPredecessors(gObj)) {
                start.add(st.getId());
                greys.add(model.getBpmnModel().getDirectedEdge(st, gObj).getId());
                break;
            }

        for(FlowNode gObj: lastEl)
            for(FlowNode en : model.getBpmnModel().getDirectSuccessors(gObj)) {
                end.add(en.getId());
                greys.add(model.getBpmnModel().getDirectedEdge(gObj, en).getId());
                break;
            }

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setStart(start);
        difference.setGreys(greys);
        difference.setA(a);
        difference.setEnd(end);
        difference.setType("UNMREPETITION2");

        // DEBUG
//        printDiff(start, end, new LinkedList<String>(), a);

        return difference;
    }

    private DifferenceML getDuplicationDiff(Pair<List<Operation>, LinkedList<Operation>> pairMatches, LinkedList<Operation> path2Leaf, int i, LinkedList<Operation> hides, float impact, Operation context) {
        String sentence = verbalizeUnmRep("log", context.label, hides);
        HashSet<Integer> ids = new HashSet<>();
        for(Operation op : pairMatches.getFirst())
            if(commonLabels.contains(pes2.getLabel(((Pair<Integer,Integer>)op.target).getSecond())))
                ids.add(((Pair<Integer,Integer>)op.target).getSecond());

        if(ids.isEmpty())
            return null;

        LinkedList<Integer> idSort = new LinkedList<>(ids);

        Collections.sort(idSort, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return pes2.getLocalConfiguration(o1).cardinality() - pes2.getLocalConfiguration(o2).cardinality();
            }
        });

        List<String> newTasks = new ArrayList<>();
        for(Integer gObj: idSort)
            newTasks.add(pes2.getLabel(gObj));

        List<String> start = new LinkedList<>();
        FlowNode startNode = null;
        if(isMatch(context))
            startNode = model.getTaskFromEvent(((Pair<Integer, Integer>)context.target).getSecond());
        else
            startNode = model.getTaskFromEvent((Integer)context.target);
        start.add(startNode.getId());


        List<String> end = new LinkedList<>();
        FlowNode endNode = model.getBpmnModel().getDirectSuccessors(startNode).iterator().next();
        end.add(endNode.getId());

        List<String> greys = new LinkedList<>();
        greys.add(model.getBpmnModel().getDirectedEdge(startNode, endNode).getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);

        difference.setStart(start);
        difference.setGreys(greys);
        difference.setNewTasks(newTasks);
        difference.setEnd(end);
        difference.setType("UNMREPETITIONINTERVAL");

        // DEBUG
//        printDiff(start, end, new LinkedList<String>(), a);

        return difference;
    }

    private DifferenceML getUNMRepetitionDiff(LinkedList<Operation> hides, LinkedList<Operation> path2Leaf, int i) {
        float impact = 1.0f;

        String sentence = verbalizeUnmRep("model", path2Leaf.get(i).label, hides);
        System.out.println(sentence + " -- Impact = " + impact);

        HashSet<Integer> ids = new HashSet<>();
        for(Operation op : hides)
            if(commonLabels.contains(pes2.getLabel((Integer)op.target)))
                ids.add((Integer)op.target);

        if(ids.isEmpty())
            return null;

        HashSet<GObject> modelElements = model.getModelSegment(ids);
        HashSet<FlowNode> firstEl = findFirst(modelElements);
        HashSet<FlowNode> lastEl = findLast(modelElements);

        List<String> greys = new ArrayList<>();
        List<String> a = new ArrayList<>();

        for(GObject elem : modelElements)
            a.add(elem.getId());

        FlowNode xorGSp = null;
        FlowNode xorGJn = null;

        HashSet<FlowNode> greyNodes = new HashSet<>();

        for(FlowNode gObj: firstEl)
            if(gObj instanceof XorGateway) {
                xorGSp = gObj;
                for (FlowNode pred : model.getBpmnModel().getDirectPredecessors(gObj)) {
                    greys.add(pred.getId());
                    greyNodes.add(pred);
                }
            }

        for(FlowNode gObj: lastEl)
            if(gObj instanceof XorGateway) {
                xorGJn = gObj;
                for (FlowNode pred : model.getBpmnModel().getDirectSuccessors(gObj)) {
                    greys.add(pred.getId());
                    greyNodes.add(pred);
                }
            }

        for(FlowNode node : greyNodes) {
            for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(node))
                greys.add(flow.getId());

            for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(node))
                greys.add(flow.getId());
        }


        List<String> start = new LinkedList<>();
        start.add(xorGSp.getId());

        List<String> end = new LinkedList<>();
        end.add(xorGJn.getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setA(a);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setGreys(greys);
        difference.setType("UNMREPETITION");

        // DEBUG
//        printDiff(start, end, greys, a);

        return difference;
    }

    private DifferenceML getUNMRepetitionDiff(Entry<State, Multiset<Integer>> entry) {
        float impact = 1.0f;
        String contextSt = getContext(entry.getKey().c2, pes2);
        HashSet<Integer> interval = new LinkedHashSet<>();
        for(Integer evt : entry.getValue().elementSet())
            if (!pes2.getInvisibleEvents().contains(evt))
                interval.add(evt);

        LinkedList<Integer> intervalOrdered = new LinkedList<>();
        for(Integer id : interval)
            if(commonLabels.contains(pes2.getLabel(id)))
                intervalOrdered.add(id);

        if(intervalOrdered.isEmpty())
            return null;

        Collections.sort(intervalOrdered, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return pes2.getLocalConfiguration(o1).cardinality() - pes2.getLocalConfiguration(o2).cardinality();
            }
        });

        String sentence = String.format("In the log, the cycle involving %s does not occur after '%s'", translate(intervalOrdered), contextSt);
        System.out.println(sentence + " -- Impact = " + impact);

        HashSet<Integer> ids = new HashSet<>(interval);

        HashSet<GObject> modelElements = model.getModelSegment(ids);
        HashSet<FlowNode> firstEl = findFirst(modelElements);
        HashSet<FlowNode> lastEl = findLast(modelElements);

        List<String> greys = new ArrayList<>();
        List<String> a = new ArrayList<>();

        for(Integer elem : intervalOrdered)
            a.add(model.getTaskFromEvent(elem).getId());

        FlowNode xorGSp = null;
        FlowNode xorGJn = null;

        HashSet<FlowNode> greyNodes = new HashSet<>();

        for(FlowNode gObj: firstEl)
            for (FlowNode pred : model.getBpmnModel().getDirectPredecessors(gObj)) {
                if (pred instanceof XorGateway)
                    xorGSp = pred;

                greys.add(pred.getId());
                greyNodes.add(pred);
            }

        for(FlowNode gObj: lastEl)
            for (FlowNode pred : model.getBpmnModel().getDirectSuccessors(gObj)) {
                if(pred instanceof XorGateway)
                    xorGJn = pred;

                greys.add(pred.getId());
                greyNodes.add(pred);
            }

        if(xorGSp == null)
            xorGSp = model.getBpmnModel().getDirectPredecessors(firstEl.iterator().next()).iterator().next();

        if(xorGJn == null)
            xorGJn = model.getBpmnModel().getDirectSuccessors(lastEl.iterator().next()).iterator().next();

        for(FlowNode node : greyNodes) {
            for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(node))
                greys.add(flow.getId());

            for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(node))
                greys.add(flow.getId());
        }

        LinkedList<Integer> historyEvts = new LinkedList<>();
        for(Integer evt : entry.getKey().c2.elementSet())
            historyEvts.add(evt);

        Collections.sort(historyEvts, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return pes2.getLocalConfiguration(o2).cardinality() - pes2.getLocalConfiguration(o1).cardinality();
            }
        });

        List<String> start = new LinkedList<>();
        for(int i = 0; i < historyEvts.size(); i++) {
            FlowNode taskModel = model.getTaskFromEvent(historyEvts.get(i));
            if (taskModel != null && !modelElements.contains(taskModel)) {
                start.add(taskModel.getId());
                break;
            }
        }

        List<String> end = new LinkedList<>();
        for(FlowNode endNode : model.getBpmnModel().getDirectSuccessors(xorGJn))
            if(!greyNodes.contains(endNode)) {
                end.add(endNode.getId());
                break;
            }

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setA(a);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setGreys(greys);
        difference.setType("UNOBSCYCLICINTER");

        // DEBUG
//        printDiff(start, end, greys, a);

        return difference;
    }

    private DifferenceML getTASKRELOCDiff2(List<Integer> ids, State state) {
        float impact = 1.0f;
        String contextSt = getContext(state.c2, pes2);

        String sentence = verbalizeTaskSkip("model", contextSt, getLabelsModel(ids));
        System.out.println(sentence + " -- Impact = " + impact);

        HashSet<Integer> idsRelevant = new HashSet<>();
        for(Integer id : ids)
            if(commonLabels.contains(pes2.getLabel(id)))
                idsRelevant.add(id);

        if(idsRelevant.isEmpty())
            return null;

        HashSet<GObject> modelElements = model.getModelSegment(idsRelevant);
        HashSet<FlowNode> firstEl = findFirst(modelElements);
        HashSet<FlowNode> lastEl = findLast(modelElements);

        List<String> greys = new ArrayList<>();
        List<String> a = new ArrayList<>();

        for(GObject elem : modelElements)
            if(elem instanceof Activity)
                a.add(elem.getId());

        FlowNode xorGSp = null;
        FlowNode xorGJn = null;

        for(FlowNode gObj: firstEl)
            for (FlowNode pred : model.getBpmnModel().getDirectPredecessors(gObj)) {
                if (pred instanceof XorGateway)
                    xorGSp = pred;

                greys.add(pred.getId());

                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(pred))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(pred))
                    greys.add(flow.getId());
            }

        for(FlowNode gObj: lastEl)
            for (FlowNode pred : model.getBpmnModel().getDirectSuccessors(gObj)) {
                if(pred instanceof XorGateway)
                    xorGJn = pred;

                greys.add(pred.getId());

                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(pred))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(pred))
                    greys.add(flow.getId());
            }

        List<String> start = new LinkedList<>();
        if(xorGSp != null)
            start.add(model.getBpmnModel().getDirectPredecessors(xorGSp).iterator().next().getId());
        else
            start.add(model.getBpmnModel().getDirectPredecessors(firstEl).iterator().next().getId());

        List<String> end = new LinkedList<>();
        if(xorGJn!=null)
            end.add(model.getBpmnModel().getDirectSuccessors(xorGJn).iterator().next().getId());
        else
            end.add(model.getBpmnModel().getDirectSuccessors(lastEl).iterator().next().getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setA(a);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setGreys(greys);
        difference.setType("TASKSKIP2");

        // DEBUG
//        printDiff(start, end, greys, a);

        return difference;
    }

    private DifferenceML getTASKRELOCDiff2(LinkedList<Operation> hides, LinkedList<Operation> path2Leaf, int i) {
        int index = path2Leaf.indexOf(hides.getLast());
        Operation nextMatch = getNextMatch(path2Leaf, path2Leaf.indexOf(hides.getLast()));
        float count = 0.0f;
        if(index < path2Leaf.size())
            count = (float) pes1.getPES().getEventOccurrenceCount(((Pair<Integer, Integer>)nextMatch.target).getFirst());
        float impact = computeImpact(count);

        String sentence = verbalizeTaskSkip("model", path2Leaf.get(i).label, hides);
        System.out.println(sentence + " -- Impact = " + impact);

        HashSet<Integer> ids = new HashSet<>();
        for(Operation op : hides)
            if(commonLabels.contains(pes2.getLabel((Integer)op.target)))
                ids.add((Integer)op.target);

        if(ids.isEmpty())
            return null;

        HashSet<GObject> modelElements = model.getModelSegment(ids);
        HashSet<FlowNode> firstEl = findFirst(modelElements);
        HashSet<FlowNode> lastEl = findLast(modelElements);

        List<String> greys = new ArrayList<>();
        List<String> a = new ArrayList<>();

        for(GObject elem : modelElements)
            a.add(elem.getId());

        FlowNode xorGSp = null;
        FlowNode xorGJn = null;

        for(FlowNode gObj: firstEl)
            if(gObj instanceof XorGateway) {
                xorGSp = gObj;
                for (FlowNode pred : model.getBpmnModel().getDirectPredecessors(gObj)) {
                    greys.add(pred.getId());

                    for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(gObj))
                        greys.add(flow.getId());

                    for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(gObj))
                        greys.add(flow.getId());
                }
            }

        for(FlowNode gObj: lastEl)
            if(gObj instanceof XorGateway) {
                xorGJn = gObj;
                for (FlowNode pred : model.getBpmnModel().getDirectSuccessors(gObj)) {
                    greys.add(pred.getId());

                    for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(gObj))
                        greys.add(flow.getId());

                    for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(gObj))
                        greys.add(flow.getId());
                }
            }

        List<String> start = new LinkedList<>();
        start.add(xorGSp.getId());

        List<String> end = new LinkedList<>();
        end.add(xorGJn.getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setA(a);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setGreys(greys);
        difference.setType("TASKSKIP2");

        // DEBUG
//        printDiff(start, end, greys, a);

        return difference;
    }

    private DifferenceML getTASKSKIPDiff(LinkedList<Operation> hides, LinkedList<Operation> path2Leaf, int i) {
        int index = path2Leaf.indexOf(hides.getLast());
        Operation nextMatch = getNextMatch(path2Leaf, path2Leaf.indexOf(hides.getLast()));
        float count = 0.0f;
        if(index < path2Leaf.size())
            count = (float) pes1.getPES().getEventOccurrenceCount(((Pair<Integer, Integer>)nextMatch.target).getFirst());
        float impact = computeImpact(count);

        String sentence = verbalizeTaskSkip("log", path2Leaf.get(i).label, hides);
        System.out.println(sentence + " -- Impact = " + impact);

        HashSet<Integer> ids = new HashSet<>();
        for(Operation op : hides)
            if(commonLabels.contains(pes2.getLabel((Integer)op.target)))
                ids.add((Integer)op.target);

        if(ids.isEmpty())
            return null;

        HashSet<GObject> modelElements = model.getModelSegment(ids);
        HashSet<FlowNode> firstEl = findFirst(modelElements);
        HashSet<FlowNode> lastEl = findLast(modelElements);

        List<String> start = new LinkedList<>();
        List<String> greys = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> end = new LinkedList<>();

        for(GObject elem : modelElements)
            if(elem instanceof Activity)
                a.add(elem.getId());

        for(FlowNode gObj: firstEl) {
            for(FlowNode st : model.getBpmnModel().getDirectPredecessors(gObj))
                start.add(st.getId());

            for (ControlFlow flow : model.getBpmnModel().getIncomingControlFlow(gObj))
                greys.add(flow.getId());
        }

        for(FlowNode gObj: lastEl) {
            for(FlowNode en : model.getBpmnModel().getDirectSuccessors(gObj))
                end.add(en.getId());

            for (ControlFlow flow : model.getBpmnModel().getOutgoingControlFlow(gObj))
                greys.add(flow.getId());
        }

        a.removeAll(greys);

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setStart(start);
        difference.setA(a);
        difference.setGreys(greys);
        difference.setEnd(end);
        difference.setType("TASKSKIP1");

        // DEBUG
//        printDiff(start, end, greys, a);

        return difference;
    }

    private HashSet<FlowNode> findFirst(HashSet<GObject> modelElements) {
        HashSet<FlowNode> first = new HashSet<>();

        for(GObject elem : modelElements)
            if(elem instanceof FlowNode)
                if(!modelElements.containsAll(model.getBpmnModel().getDirectPredecessors((FlowNode) elem)))
                    first.add((FlowNode) elem);

        return first;
    }

    private HashSet<FlowNode> findLast(HashSet<GObject> modelElements) {
        HashSet<FlowNode> last = new HashSet<>();

        for(GObject elem : modelElements)
            if(elem instanceof FlowNode)
                if(!modelElements.containsAll(model.getBpmnModel().getDirectSuccessors((FlowNode) elem)))
                    last.add((FlowNode) elem);

        return last;
    }

    private DifferenceML getTASKRELOCDiff(Pair<LinkedList<Operation>, LinkedList<Operation>> commonSubL, LinkedList<Operation> path2Leaf) {
        float impact = computeImpact(getMinimum(commonSubL.getFirst()));

        String sentence = verbalizeTaskReloc(commonSubL.getFirst(), commonSubL.getSecond(), path2Leaf);
        System.out.println(sentence + " -- Impact = " + impact);

        LinkedList<Operation> rHides = commonSubL.getSecond();
        List<String> greys = new ArrayList<>();
        HashSet<Integer> ids = new HashSet<>();
        for(Operation op : rHides)
            if(commonLabels.contains(pes2.getLabel((Integer)op.target)))
                ids.add((Integer)op.target);

        if(ids.isEmpty())
            return null;

        HashSet<GObject> modelElements = model.getModelSegment(ids);
        for(GObject gObj: modelElements)
            greys.add(gObj.getId());

        LinkedList<Operation> predOps = getPredOps(commonSubL.getFirst().getFirst(), path2Leaf, "model");
        Integer event = isMatch(predOps.getLast())?
                ((Pair<Integer, Integer>)predOps.getLast().target).getSecond() :
                (Integer) predOps.getLast().target;

        List<String> start = new ArrayList<>();
        start.add(model.getTaskFromEvent(event).getId());

        List<String> end = new ArrayList<>();
        FlowNode successor = model.getBpmnModel().getDirectSuccessors(model.getTaskFromEvent(event)).iterator().next();
        end.add(successor.getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setSentence(sentence);
        difference.setStart(start);
        difference.setGreys(greys);
        difference.setEnd(end);
        difference.setType("TASKRELOC");

        // DEBUG
//        printDiff(start, end, greys);

        return difference;
    }

//    private void printDiff(List<String> start, List<String> end, List<String> greys, List<String> a){
//        // For testing
//        HashMap<String, String> newColorsBP = new HashMap<>();
//        for (String s : a)
//            newColorsBP.put(s, "red");
//
//        for (String s : start)
//            newColorsBP.put(s, "blue");
//
//        for (String s : end)
//            newColorsBP.put(s, "blue");
//
//        for (String s : greys)
//            newColorsBP.put(s, "gray");
//
//        printModels("m", "1", net, model.getReader(), null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());
//    }
//
//    private void printDiff(List<String> start, List<String> end, List<String> greys) {
//        // For testing
//        HashMap<String, String> newColorsBP = new HashMap<>();
//        for (String s : end)
//            newColorsBP.put(s, "blue");
//
//        for (String s : start)
//            newColorsBP.put(s, "blue");
//
//        for (String s : greys)
//            newColorsBP.put(s, "gray");
//
//        printModels("m", "" + ThreadLocalRandom.current().nextInt(0, 1000 + 1), net, model.getReader(), null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());
//    }

    private void filterOutSilent(Collection<Operation> operations) {
        LinkedList<Operation> toRemove = new LinkedList<>();

        for(Operation op : operations)
            if(!commonLabels.contains(op.label))
                toRemove.add(op);

        operations.removeAll(toRemove);
    }

    private String getContext(BitSet configuration, NewUnfoldingPESSemantics<Integer> pes) {
        LinkedList<String> contextSt = new LinkedList<>();
        for (int i = configuration.nextSetBit(0); i >= 0; i = configuration.nextSetBit(i+1))
            if(commonLabels.contains(pes.getLabels().get(i)))
                contextSt.add(pes.getLabels().get(i));

        Collections.sort(contextSt);
        return contextSt.toString();
    }

    private String getContext(Multiset<Integer> configuration, NewUnfoldingPESSemantics<Integer> pes) {
        String context = "[";

        for (Integer i : configuration)
            if(commonLabels.contains(pes.getLabels().get(i))){
                if(context.length() > 1)
                    context += ", ";
                context += pes.getLabels().get(i);
            }

        return context + "]";
    }

    private String getContext(BitSet configuration, PrimeEventStructure<Integer> pes) {
        String context = "[";

        for (int i = configuration.nextSetBit(0); i >= 0; i = configuration.nextSetBit(i+1))
            if(commonLabels.contains(pes.getLabels().get(i))){
                if(context.length() > 1)
                    context += ", ";
                context += pes.getLabels().get(i);
            }

        if(context.endsWith(", "))
            context = context.substring(0, context.length()-2);

        return context + "]";
    }

    private Operation getNextMatch(LinkedList<Operation> path2Leaf, int i) {
        for(int j = i+1; j < path2Leaf.size(); j++)
            if(isMatch(path2Leaf.get(j)))
                return path2Leaf.get(j);

        return null;
    }


    private float getMinimum(LinkedList<Operation> first) {
        int min = Integer.MAX_VALUE;

        for (Operation op : first)
            if (op.op.equals(Op.LHIDE)){
                if (pes1.getPES().getEventOccurrenceCount((Integer) op.target) < min)
                    min = pes1.getPES().getEventOccurrenceCount((Integer) op.target);
            }else{
                try {
                    throw new Exception("Error");
                }catch(java.lang.Exception e){e.printStackTrace();}
            }

        return (float)min;
    }

    private float computeImpact(float dividend) {
        return dividend / (float) pes1.getPES().getTotalTraceCount();
    }

    private HashSet<LinkedList<Operation>> filterOutAllEmpty(HashSet<LinkedList<Operation>> contiguousRHide) {
        HashSet<LinkedList<Operation>> nonEmpty = new HashSet<>();
        for(LinkedList<Operation> list : contiguousRHide)
            if(list.size() > 0)
                nonEmpty.add(list);

        return nonEmpty;
    }

    private Pair<State, List<Integer>> isContSkip(State state, List<Integer> interval, Multimap<State, List<Integer>> acyclicIntervals) {
        Multiset<Integer> newConf = HashMultiset.<Integer> create(state.c2);
        newConf.addAll(interval);

        for(List<Integer> intervalUnobs : acyclicIntervals.get(state)){
            HashSet<Integer> extensions = new HashSet<>(intervalUnobs);

            while(!extensions.isEmpty()){
                Set<Integer> possibleExt = new HashSet<>(pes2.getPossibleExtensions(newConf));
                possibleExt.retainAll(extensions);

                if(possibleExt.size() > 0) {
                    extensions.removeAll(possibleExt);
                    newConf.addAll(possibleExt);
                }else break;
            }

            if(extensions.isEmpty()){
                return new Pair<State, List<Integer>>(state, intervalUnobs);
            }
        }

        return null;
    }

    private String verbalizeUnobsAcyclic(String label, String context) {
        return String.format("In the log, the interval %s does not occur after '%s'", label, context);
    }

    private String verbalizeTaskAbs(String rep, String first, String pre, String post) {
        return String.format("In the %s, '%s' occurs after '%s' and before '%s'", rep, first, pre, post);
    }

    private String verbalizeTaskIns(String first, String logCont, String modelCont) {
        return String.format("In the log, '%s' occurs after '%s' instead of '%s'", first, logCont, modelCont);
    }

    private Operation findOpposite(Operation op, HashSet<Operation> allHides) {
        for (Operation opp : allHides)
            if (op.label.equals(opp.label))
                if ((isLHide(op) && isRHide(opp)) || (isRHide(op) && isLHide(opp)))
                    return opp;

        return null;
    }

    private boolean isHide(Operation op) {
        return isLHide(op) || isRHide(op);
    }

    private boolean isMatch(Operation op) {
        return op.op.equals(Op.MATCH) || op.op.equals(Op.MATCHNSHIFT);
    }

    private boolean isLHide(Operation op) {
        return op.op.equals(Op.LHIDE);
    }

    private boolean isRHide(Operation op) {
        return op.op.equals(Op.RHIDE) || op.op.equals(Op.RHIDENSHIFT);
    }

    private void check4ConfMistmatches(LinkedList<Operation> path2Leaf, LinkedList<Operation> hidesL, LinkedList<Operation> hidesR, HashSet<Operation> usedHides, Multimap<State, List<Integer>> acyclicIntervals) {
        HashSet<Operation> toRemoveL = new HashSet<>();
        HashSet<Operation> toRemoveR = new HashSet<>();

        for (Operation op : path2Leaf) {
            if (op.op.equals(Op.MATCH) || op.op.equals(Op.MATCHNSHIFT)) {
                // In this pair the first element is part of path2Leaf
                Pair<Pair<Operation, Operation>, Pair<Operation, Operation>> matchHide = findMatchHideSucc(op, path2Leaf);

                if (matchHide != null) {
                    Pair<Operation, Operation> pairInPath2Leaf = matchHide.getFirst();

                    Integer evt1P1 = -1;
                    Integer evt2P1 = -1;

                    // Extraordinary case, when the second pair in matchHide is null
                    if (matchHide.getSecond() == null) {
                        evt2P1 = (Integer) pairInPath2Leaf.getSecond().target;
                        BehaviorRelation bRLog = null;

                        Integer evt1P2 = ((Pair<Integer, Integer>)pairInPath2Leaf.getFirst().target).getSecond();
                        Integer evt2P2 = -1;

                        if(isLHide(pairInPath2Leaf.getSecond())) {
                            List<Integer> interval = getAcyInt(acyclicIntervals, op.nextState, pes1.getLabel(evt2P1));

                            if(interval!= null) {
                                evt1P1 = ((Pair<Integer, Integer>) pairInPath2Leaf.getFirst().target).getFirst();
                                toRemoveL.add(pairInPath2Leaf.getSecond());
                                bRLog = pes1.getBRelation(evt1P1, evt2P1);
                                acyclicIntervals.remove(op.nextState, interval);

                                for(Integer evt : interval)
                                    if(pes1.getLabel(evt2P1).equals(pes2.getLabel(evt)))
                                        evt2P2 = evt;

                            }else continue;
                        } else continue;

                        DifferenceML difference = getConfMisDiff(bRLog, op, evt1P1, evt2P1, evt1P2, evt2P2);
                        if(difference != null) differences.add(difference);

                        continue;
                    }

                    Pair<Operation, Operation> complementingPair = matchHide.getSecond();

                    if (isMatch(pairInPath2Leaf.getFirst())) {
                        if(isHide(complementingPair.getFirst())){
                            evt1P1 = ((Pair<Integer, Integer>) pairInPath2Leaf.getFirst().target).getFirst();
                            Integer evt1P2 = ((Pair<Integer, Integer>) pairInPath2Leaf.getFirst().target).getSecond();

                            evt2P1 = ((Pair<Integer, Integer>) complementingPair.getSecond().target).getFirst();
                            Integer evt2P2 = ((Pair<Integer, Integer>) complementingPair.getSecond().target).getSecond();

                            if (pairInPath2Leaf.getSecond().op.equals(Op.LHIDE)) {
                                if(isRHide(pairInPath2Leaf.getSecond())) toRemoveR.add(pairInPath2Leaf.getSecond());
                                else toRemoveL.add(pairInPath2Leaf.getSecond());

                                if(isRHide(pairInPath2Leaf.getFirst())) toRemoveR.add(pairInPath2Leaf.getFirst());
                                else toRemoveL.add(pairInPath2Leaf.getFirst());

                                Integer evt3P1 = (Integer) pairInPath2Leaf.getSecond().target;
                                Integer evt3P2 = (Integer) complementingPair.getFirst().target;

//                                BehaviorRelation bRModel = pes2.getBRelation(evt1P2, evt2P2);
                                BehaviorRelation bRLog = pes1.getBRelation(evt1P1, evt3P1);


                                DifferenceML difference = getConfMisDiff(bRLog, op, evt1P1, evt3P1, evt1P2, evt2P2);
                                if(difference != null) differences.add(difference);
                            } else {
                                if(isRHide(pairInPath2Leaf.getSecond())) toRemoveR.add(pairInPath2Leaf.getSecond());
                                else toRemoveL.add(pairInPath2Leaf.getSecond());

                                if(isRHide(complementingPair.getFirst())) toRemoveR.add(complementingPair.getFirst());
                                else toRemoveL.add(complementingPair.getFirst());

                                BehaviorRelation bRModel = pes2.getBRelation(evt1P2, evt2P2);
                                BehaviorRelation bRLog = pes1.getBRelation(evt1P1, evt2P1);

                                DifferenceML difference = getConfMisDiff2(bRModel, op, evt1P1, evt2P1, evt1P2, evt2P2);
                                if(difference != null) differences.add(difference);
                            }
                        }else{
                            if(isRHide(pairInPath2Leaf.getSecond())) toRemoveR.add(pairInPath2Leaf.getSecond());
                            else toRemoveL.add(pairInPath2Leaf.getSecond());

                            if(isRHide(complementingPair.getSecond())) toRemoveR.add(complementingPair.getSecond());
                            else toRemoveL.add(complementingPair.getSecond());

                            evt1P1 = ((Pair<Integer, Integer>) pairInPath2Leaf.getFirst().target).getFirst();
                            Integer evt1P2 = ((Pair<Integer, Integer>) pairInPath2Leaf.getFirst().target).getSecond();

                            evt2P1 = ((Pair<Integer, Integer>) complementingPair.getFirst().target).getFirst();
                            Integer evt2P2 = ((Pair<Integer, Integer>) complementingPair.getFirst().target).getSecond();

                            BehaviorRelation bRLog = pes1.getBRelation(evt1P1, evt2P1);
                            BehaviorRelation bRModel = pes2.getBRelation(evt1P2, evt2P2);

                            DifferenceML difference = getConfMisDiff3(op, bRLog, bRModel, evt1P1, evt2P1, evt1P2, evt2P2);
                            if(difference != null) differences.add(difference);
                        }
                    } else {
                        // Matches
                        evt2P1 = ((Pair<Integer, Integer>) pairInPath2Leaf.getSecond().target).getFirst();
                        Integer evt2P2 = ((Pair<Integer, Integer>) pairInPath2Leaf.getSecond().target).getSecond();

                        Integer evt3P1 = ((Pair<Integer, Integer>) complementingPair.getFirst().target).getFirst();
                        Integer evt3P2 = ((Pair<Integer, Integer>) complementingPair.getFirst().target).getSecond();

                        BehaviorRelation bRModel = pes1.getBRelation(evt2P1, evt3P1);
                        BehaviorRelation bRLog = pes2.getBRelation(evt2P2, evt3P2);

                        if (pairInPath2Leaf.getFirst().op.equals(Op.LHIDE)) {
                            if(isRHide(pairInPath2Leaf.getFirst())) toRemoveR.add(pairInPath2Leaf.getFirst());
                            else toRemoveL.add(pairInPath2Leaf.getFirst());

                            if(isRHide(complementingPair.getSecond())) toRemoveR.add(complementingPair.getSecond());
                            else toRemoveL.add(complementingPair.getSecond());

                            DifferenceML difference = getConfMisDiff(bRLog, op, evt1P1, evt2P1, evt2P2, evt3P2);
                            if(difference != null) differences.add(difference);
                        } else {
                            if(isRHide(pairInPath2Leaf.getFirst())) toRemoveR.add(pairInPath2Leaf.getFirst());
                            else toRemoveL.add(pairInPath2Leaf.getFirst());

                            if(isRHide(complementingPair.getSecond())) toRemoveR.add(complementingPair.getSecond());
                            else toRemoveL.add(complementingPair.getSecond());


                            DifferenceML difference = getConfMisDiff2(bRModel, op, evt1P1, evt2P1, evt2P1, evt3P1);
                            if(difference != null) differences.add(difference);
                        }
                    }
                }
            }
        }

        hidesL.removeAll(toRemoveL);
        hidesR.removeAll(toRemoveR);
        usedHides.addAll(toRemoveL);
        usedHides.addAll(toRemoveR);
    }

    private DifferenceML getConfMisDiff3(Operation op, BehaviorRelation bRLog, BehaviorRelation bRModel, Integer evt1P1, Integer evt2P1, Integer evt1M, Integer evt2M) {
        String sentence = "";
        float impact = 1.0f;
        String type = "";

        if (bRLog.equals(BehaviorRelation.CONCURRENCY)) {
            float min = (float) Math.min(pes1.getPES().getEventOccurrenceCount(evt1P1), pes1.getPES().getEventOccurrenceCount(evt2P1));
            impact = computeImpact(min);

            List<String> labels = new LinkedList<>();
            labels.add(pes2.getLabel(evt1M));
            labels.add(pes2.getLabel(evt2M));
            Collections.sort(labels);

            // Priority No. 11 -- CONCCONF
            sentence = verbalizeConcConf("log", "model", op.label, labels.get(0), labels.get(1));
            type = "CONFLICT1";
//                            }
//                            else if (bRLog.equals(BehaviorRelation.CAUSALITY)) {
//                                // Priority No. 13 -- CAUSCONF
//                                sentence = verbalizeCausConf("log", "model", op.label, pes2.getLabel(evt1P2), pes2.getLabel(evt2P2));
//                            } else if (bRLog.equals(BehaviorRelation.INV_CAUSALITY)) {
//                                // Priority No. 13 -- CAUSCONF
//                                sentence = verbalizeCausConf("log", "model", op.label, pes2.getLabel(evt2P2), pes2.getLabel(evt1P2));
        }
        else if (bRModel.equals(BehaviorRelation.CONCURRENCY)) {
            float sum = pes1.getPES().getEventOccurrenceCount(evt1P1) + pes1.getPES().getEventOccurrenceCount(evt2P1);
            impact = computeImpact(sum);

            List<String> labels = new LinkedList<>();
            labels.add(pes2.getLabel(evt1M));
            labels.add(pes2.getLabel(evt2M));
            Collections.sort(labels);

            // Priority No. 11 -- CONCCONF
            sentence = verbalizeConcConf("model", "log", op.label, labels.get(0), labels.get(1));
            type = "CONFLICT2";
        }else
            sentence = "ERROR: CONFLICT RELATION";

        System.out.println(sentence + " -- Impact = " + impact);

        List<String> a = new LinkedList<>();
        List<String> b = new LinkedList<>();
        List<String> start = new LinkedList<>();
        List<String> end = new LinkedList<>();
        List<String> greys = new ArrayList<>();

        if(!commonLabels.contains(pes2.getLabel(evt1M)) || !commonLabels.contains(pes2.getLabel(evt2M)))
            return null;

        HashSet<Integer> ids = new HashSet<>();
        ids.add(evt1M);
        ids.add(evt2M);

        HashSet<GObject> elem = model.getModelSegment(ids);
        for(GObject obj : elem)
            if(obj instanceof ControlFlow)
                greys.add(obj.getId());

        a.add(model.getTaskFromEvent(evt1M).getId());
        b.add(model.getTaskFromEvent(evt2M).getId());

        HashSet<FlowNode> originators = new HashSet<>();
        originators.add(model.getTaskFromEvent(evt1M));
        originators.add(model.getTaskFromEvent(evt2M));

        FlowNode firstEl = getPredEvt(evt1M, evt2M);
        FlowNode lastEl = getNextCommon(originators).getFirst();

        if(type.equals("CONFLICT2")){
            if(firstEl instanceof AndGateway) {
                greys.add(firstEl.getId());

                for(ControlFlow contF : model.getBpmnModel().getIncomingControlFlow(firstEl))
                    if(!greys.contains(contF.getId()))
                        greys.add(contF.getId());

                for(ControlFlow contF : model.getBpmnModel().getOutgoingControlFlow(firstEl))
                    if(!greys.contains(contF.getId()))
                        greys.add(contF.getId());

                firstEl = model.getBpmnModel().getDirectPredecessors(firstEl).iterator().next();


            }

            if(lastEl instanceof AndGateway) {
                greys.add(lastEl.getId());

                for(ControlFlow contF : model.getBpmnModel().getIncomingControlFlow(lastEl))
                    if(!greys.contains(contF.getId()))
                        greys.add(contF.getId());

                for(ControlFlow contF : model.getBpmnModel().getOutgoingControlFlow(lastEl))
                    if(!greys.contains(contF.getId()))
                        greys.add(contF.getId());

                lastEl = model.getBpmnModel().getDirectSuccessors(lastEl).iterator().next();
            }
        }

        start.add(firstEl.getId());
        end.add(lastEl.getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setA(a);
        difference.setB(b);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setGreys(greys);
        difference.setSentence(sentence);
        difference.setType(type);

        // DEBUG
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

//        printModels("m", "1", net, model.getReader(), null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());


        return difference;
    }

    private DifferenceML getConfMisDiff2(BehaviorRelation bRModel, Operation op, Integer evt1P1, Integer evt2P1, Integer evt1M, Integer evt2M) {
        String sentence = "";
        float impact = 1.0f;
        String type = "";

        List<String> labels = new LinkedList<>();
        labels.add(pes1.getLabel(evt1P1));
        labels.add(pes1.getLabel(evt2P1));
        Collections.sort(labels);

        // Priority No. 12 -- CONCCONF
        if (bRModel.equals(BehaviorRelation.CONCURRENCY)) {
            float sum = pes1.getPES().getEventOccurrenceCount(evt1P1) + pes1.getPES().getEventOccurrenceCount(evt2P1);
            impact = computeImpact(sum);
            System.out.println("Impact = " + impact);

            sentence = verbalizeConcConf("model", "log", op.label, labels.get(0), labels.get(1));
            type = "CONFLICT2";
        } else if (bRModel.equals(BehaviorRelation.CAUSALITY)) {
            float sum = pes1.getPES().getEventOccurrenceCount(evt1P1) + pes1.getPES().getEventOccurrenceCount(evt2P1);
            impact = computeImpact(sum);
            System.out.println("Impact = " + impact);

            // Priority No. 14 -- CAUSCONF
            sentence = verbalizeCausConf("model", "log", op.label, pes1.getLabel(evt1P1), pes1.getLabel(evt2P1));
            type = "CONFLICT4";
        } else if (bRModel.equals(BehaviorRelation.INV_CAUSALITY)) {
            float sum = pes1.getPES().getEventOccurrenceCount(evt1P1) + pes1.getPES().getEventOccurrenceCount(evt2P1);
            impact = computeImpact(sum);
            System.out.println("Impact = " + impact);

            // Priority No. 14 -- CAUSCONF
            sentence = verbalizeCausConf("model", "log", op.label, pes1.getLabel(evt2P1), pes1.getLabel(evt1P1));
            type = "CONFLICT4";
        } else
            sentence ="ERROR: CONFLICT RELATION";

        System.out.println(sentence + " -- Impact = " + impact);

        List<String> a = new LinkedList<>();
        List<String> b = new LinkedList<>();
        List<String> start = new LinkedList<>();
        List<String> end = new LinkedList<>();
        List<String> greys = new ArrayList<>();

        if(!commonLabels.contains(pes2.getLabel(evt1M)) || !commonLabels.contains(pes2.getLabel(evt2M)))
            return null;

        HashSet<Integer> ids = new HashSet<>();
        ids.add(evt1M);
        ids.add(evt2M);

        HashSet<GObject> elem = model.getModelSegment(ids);
        for(GObject obj : elem)
            if(obj instanceof ControlFlow)
                greys.add(obj.getId());

        a.add(model.getTaskFromEvent(evt1M).getId());
        b.add(model.getTaskFromEvent(evt2M).getId());

        HashSet<FlowNode> originators = new HashSet<>();
        originators.add(model.getTaskFromEvent(evt1M));
        originators.add(model.getTaskFromEvent(evt2M));

        FlowNode firstEl = getPredEvt(evt1M, evt2M);
        FlowNode lastEl = getNextCommon(originators).getFirst();

        start.add(firstEl.getId());
        end.add(lastEl.getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setA(a);
        difference.setB(b);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setGreys(greys);
        difference.setSentence(sentence);
        difference.setType(type);

        // DEBUG
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

//        printModels("m", "1", net, model.getReader(), null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return difference;
    }

    private DifferenceML getConfMisDiff(BehaviorRelation bRLog, Operation op, Integer evt1P1, Integer evt2P1, Integer evt1M, Integer evt2M) {
        String sentence = "";
        float impact = 1.0f;
        String type = "";

        List<String> labels = new LinkedList<>();
        labels.add(pes1.getLabel(evt1P1));
        labels.add(pes1.getLabel(evt2P1));
        Collections.sort(labels);

        if (bRLog.equals(BehaviorRelation.CONCURRENCY)) {
            float min = (float) Math.min(pes1.getPES().getEventOccurrenceCount(evt1P1), pes1.getPES().getEventOccurrenceCount(evt2P1));
            impact = computeImpact(min);
            System.out.println("Impact = " + impact);

            // Priority No. 11 -- CONCCONF
            sentence = verbalizeConcConf("log", "model", op.label, labels.get(0), labels.get(1));
            type = "CONFLICT1";
        } else if (bRLog.equals(BehaviorRelation.CAUSALITY)) {
            float min = (float) Math.min(pes1.getPES().getEventOccurrenceCount(evt1P1), pes1.getPES().getEventOccurrenceCount(evt2P1));
            impact = computeImpact(min);
            System.out.println("Impact = " + impact);

            // Priority No. 13 -- CAUSCONF
            sentence = verbalizeCausConf("log", "model", op.label, pes1.getLabel(evt1P1), pes1.getLabel(evt2P1));
            type = "CONFLICT3";
        } else if (bRLog.equals(BehaviorRelation.INV_CAUSALITY)) {
            float min = (float) Math.min(pes1.getPES().getEventOccurrenceCount(evt1P1), pes1.getPES().getEventOccurrenceCount(evt2P1));
            impact = computeImpact(min);
            System.out.println("Impact = " + impact);

            // Priority No. 13 -- CAUSCONF
            sentence = verbalizeCausConf("log", "model", op.label, pes1.getLabel(evt2P1), pes1.getLabel(evt1P1));
            type = "CONFLICT3";
        } else
            sentence = "ERROR: CONFLICT RELATION";

        System.out.println(sentence + " -- Impact = " + impact);

        List<String> a = new LinkedList<>();
        List<String> b = new LinkedList<>();
        List<String> start = new LinkedList<>();
        List<String> end = new LinkedList<>();
        List<String> greys = new ArrayList<>();

        if(!commonLabels.contains(pes2.getLabel(evt1M)) || !commonLabels.contains(pes2.getLabel(evt2M)))
            return null;

        HashSet<Integer> ids = new HashSet<>();
        ids.add(evt1M);
        ids.add(evt2M);

        HashSet<GObject> elem = model.getModelSegment(ids);
        for(GObject obj : elem)
            if(obj instanceof ControlFlow)
                greys.add(obj.getId());

        a.add(model.getTaskFromEvent(evt1M).getId());
        b.add(model.getTaskFromEvent(evt2M).getId());

        HashSet<FlowNode> originators = new HashSet<>();
        originators.add(model.getTaskFromEvent(evt1M));
        originators.add(model.getTaskFromEvent(evt2M));

        FlowNode firstEl = getPredEvt(evt1M, evt2M);
        FlowNode lastEl = getNextCommon(originators).getFirst();

        if(firstEl instanceof XorGateway) {
            greys.add(firstEl.getId());

            for(ControlFlow contF : model.getBpmnModel().getIncomingControlFlow(firstEl))
                if(!greys.contains(contF.getId()))
                    greys.add(contF.getId());

            for(ControlFlow contF : model.getBpmnModel().getOutgoingControlFlow(firstEl))
                if(!greys.contains(contF.getId()))
                    greys.add(contF.getId());

            firstEl = model.getBpmnModel().getDirectPredecessors(firstEl).iterator().next();


        }

        if(lastEl instanceof XorGateway) {
            greys.add(lastEl.getId());

            for(ControlFlow contF : model.getBpmnModel().getIncomingControlFlow(lastEl))
                if(!greys.contains(contF.getId()))
                    greys.add(contF.getId());

            for(ControlFlow contF : model.getBpmnModel().getOutgoingControlFlow(lastEl))
                if(!greys.contains(contF.getId()))
                    greys.add(contF.getId());

            lastEl = model.getBpmnModel().getDirectSuccessors(lastEl).iterator().next();
        }

        start.add(firstEl.getId());
        end.add(lastEl.getId());

        DifferenceML difference = new DifferenceML(impact);
        difference.setA(a);
        difference.setB(b);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setGreys(greys);
        difference.setSentence(sentence);
        difference.setType(type);

        // DEBUG
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

//        printModels("m", "1", net, model.getReader(), null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return difference;
    }

    private List<Integer> getAcyInt(Multimap<State, List<Integer>> acyclicIntervals, State state, String label) {
        for(List<Integer> interval : acyclicIntervals.get(state))
            for(Integer i : interval)
                if(pes2.getLabel(i).equals(label))
                    return interval;

        return null;
    }

    private String verbalizeCausConf(String rep1, String rep2, String context, String first, String second) {
        return String.format("In the %s, after '%s', '%s' occurs before '%s', while in the %s they are mutually exclusive", rep1, context, first, second, rep2);
    }

    private Pair<Pair<Operation, Operation>, Pair<Operation, Operation>> findMatchHideSucc(Operation contextOp, LinkedList<Operation> path2Leaf) {
        int i = path2Leaf.indexOf(contextOp);
        if (i + 1 >= path2Leaf.size())
            return null;

        // Operation 1 is the first operation in path2Leaf (match or hide)
        Operation op1 = path2Leaf.get(i + 1);
        // Operation 2 is the counter operation of op1 and it is NOT in the path2Leaf
        Operation op2 = null;

        // Operation 3 is the second operation in path2Leaf (match or hide)
        Operation op3 = null;
        // Operation 4 is the counter of op3 and it is NOT in the path2Leaf
        Operation op4 = null;

            for(int j = i+2; j < path2Leaf.size(); j++)
                if(isMatch(op1) && isHide(path2Leaf.get(j))) {
                    op3 = path2Leaf.get(j);
//                    String hideLabelOp3 = path2Leaf.get(j).label;
                    break;
                }

            if(op3 != null) {
                Pair<Operation, Operation> counterImage = findCounterImage(contextOp, op1, op3);

                if(counterImage != null) {
                    op2 = counterImage.getFirst();
                    op4 = counterImage.getSecond();
                }
            }

            // Double check in case a conflicting event could not be
            // matched in an optimal match
            if (op2 == null) {
                Integer contextEvt = ((Pair<Integer, Integer>) contextOp.target).getSecond();
                Integer extension = -1;
                for (int j = i + 1; j < path2Leaf.size(); j++) {
                    if (isMatch(op1) && path2Leaf.get(j).op.equals(Op.LHIDE)) {
                        Set<Integer> extensions = pes2.getPossibleExtensions(contextOp.nextState.c2);
                        for (Integer ext : extensions)
                            if (pes2.getLabel(ext).equals(path2Leaf.get(j).label)) {
                                extension = ext;
                                op3 = path2Leaf.get(j);
                                break;
                            }

                        if (op3 != null)
                            break;
                    }
                }
            }

        if (op1 == null || op3 == null)
            return null;
        else if (op2 == null && op4 == null)
            return new Pair<>(new Pair<>(op1, op3), null);

        return new Pair<>(new Pair<>(op1, op3), new Pair<>(op2, op4));
    }

    private Pair<Operation,Operation> findCounterImage(Operation contextOp, Operation op1, Operation op3) {
        Operation op2 = null;
        Operation op4 = null;

        for (Operation op : descendants.get(contextOp.nextState)) {
            if(op.equals(op1))
                continue;
            op2 = op;
            Operation counterOp = null;
            if (isMatch(op1) && op.label.equals(op1.label) && isHide(op) && op.op.equals(op3.op))
                counterOp = findRecursiveMatch(op2.nextState, op3.label);
            else if(isMatch(op1) && isMatch(op) && op.label.equals(op3.label))
                counterOp = findRecursiveHide(op2.nextState, op1.label, op3.op);
            else if(isHide(op1) && op1.label.equals(op.label) && isMatch(op))
                counterOp = findRecursiveHide(op2.nextState, op3.label, op1.op);

            if (counterOp != null) {
                op4 = counterOp;
                break;
            }
        }

        if(op2 == null || op4 == null)
            return null;

        return new Pair<>(op2, op4);
    }

    private Operation findRecursiveHide(State rootState, String label, Op operation) {
        for (Operation op : descendants.get(rootState)) {
            if (op.label.equals(label))
                if (op.op.equals(operation) || ((operation.equals(Op.RHIDE) || operation.equals(Op.RHIDENSHIFT)) && isRHide(op)))
                    return op;
            Operation recursive = findRecursiveHide(op.nextState, label, operation);
            if (recursive != null)
                return recursive;
        }

        return null;
    }

    private void check4CAUSCONCMist(LinkedList<Operation> path2Leaf, LinkedList<Operation> hidesL, LinkedList<Operation> hidesR, HashSet<Operation> usedHides) {
        HashSet<Operation> toRemoveL = new HashSet<>();
        HashSet<Operation> toRemoveR = new HashSet<>();

        for (Operation op : path2Leaf) {
            if (op.op.equals(Op.MATCH)) {
                Pair<Integer, Integer> matchedEvents = (Pair<Integer, Integer>) op.target;

                for (int i = 0; i < hidesL.size(); i++) {
                    Operation opL = hidesL.get(i);
                    Operation opR = findByLabel(hidesR, opL.label);

                    if (opR == null)
                        continue;

                    if (isCausConcMist(pes1.getBRelation(matchedEvents.getFirst(), (Integer) opL.target),
                            pes2.getBRelation(matchedEvents.getSecond(), (Integer) opR.target))) {
                        String context = "Start";
                        Operation matchContext = getContext(path2Leaf, op, opL, opR);
                        if (matchContext == null)
                            context = matchContext.label;

                        DifferenceML difference = getCAUSCONCDiff(matchedEvents.getFirst(), (Integer) opL.target, matchedEvents.getSecond(), (Integer) opR.target, context);
                        if(difference != null) differences.add(difference);

                        toRemoveL.add(opL);
                        toRemoveR.add(opR);

                        usedHides.add(opL);
                        usedHides.add(opR);
                    }
                }
            }
        }

        hidesL.removeAll(toRemoveL);
        hidesR.removeAll(toRemoveR);
    }

    private DifferenceML getCAUSCONCDiff(Integer evt1L, Integer evt2L, Integer evt1M, Integer evt2M, String context) {
        float min = (float) Math.min(pes1.getPES().getEventOccurrenceCount(evt1L), pes1.getPES().getEventOccurrenceCount(evt2L));
        float impact = computeImpact(min);

        String sentence = "";
        List<String> a = new LinkedList<>();
        List<String> b = new LinkedList<>();
        List<String> start = new LinkedList<>();
        List<String> end = new LinkedList<>();
        List<String> greys = new ArrayList<>();
        String type = "";

        if (pes1.getBRelation(evt1L, evt2L).equals(BehaviorRelation.CONCURRENCY)) {
            // Priority No. 9 -- CAUSCONC
            sentence = verbalizeCausConc("model", "log", context, pes1.getLabel(evt1L), pes1.getLabel(evt2L));
            type = "CAUSCONC2";
        }else {
            // Priority No. 10 -- CAUSCONC
            if(pes1.getBRelation(evt1L, evt2L).equals(BehaviorRelation.INV_CAUSALITY)){
                Integer evtBk = evt1L;
                evt1L = (Integer) evt2L;
                evt2L = evtBk;

                evtBk = evt1M;
                evt1M = evt2M;
                evt2M = evtBk;
            }
            sentence = verbalizeCausConc("log", "model", context, pes1.getLabel(evt1L), pes1.getLabel(evt2L));
            type = "CAUSCONC1";
        }

        System.out.println(sentence + " -- Impact = " + impact);

        if(!commonLabels.contains(pes2.getLabel(evt1M)) || !commonLabels.contains(pes2.getLabel(evt2M)))
            return null;
        
        HashSet<Integer> ids = new HashSet<>();
        ids.add(evt1M);
        ids.add(evt2M);

        HashSet<GObject> elem = model.getModelSegment(ids);
        for(GObject obj : elem)
            if(obj instanceof ControlFlow)
                greys.add(obj.getId());

        FlowNode taskA = model.getTaskFromEvent(evt1M);
        FlowNode taskB = model.getTaskFromEvent(evt2M);

        FlowNode firstEl = getPredEvt(evt1M, evt2M);
        FlowNode lastEl = model.getBpmnModel().getDirectSuccessors(taskB).iterator().next();//getSuccEvt(evt1M, evt2M);

        if(type.equals("CAUSCONC1")){
            if(firstEl instanceof AndGateway){// && isSafe2RemSp(firstEl, taskA, taskB)) {
                greys.add(firstEl.getId());

                for(ControlFlow contF : model.getBpmnModel().getIncomingControlFlow(firstEl))
                    if(!greys.contains(contF.getId()))
                        greys.add(contF.getId());

                for(ControlFlow contF : model.getBpmnModel().getOutgoingControlFlow(firstEl))
                    if(!greys.contains(contF.getId()))
                        greys.add(contF.getId());

                firstEl = model.getBpmnModel().getDirectPredecessors(firstEl).iterator().next();
            }

            if(lastEl instanceof AndGateway){// && isSafe2RemJn(lastEl, taskA, taskB)) {
                greys.add(lastEl.getId());

                for(ControlFlow contF : model.getBpmnModel().getIncomingControlFlow(lastEl))
                    if(!greys.contains(contF.getId()))
                        greys.add(contF.getId());

                for(ControlFlow contF : model.getBpmnModel().getOutgoingControlFlow(lastEl))
                    if(!greys.contains(contF.getId()))
                        greys.add(contF.getId());

                lastEl = model.getBpmnModel().getDirectSuccessors(lastEl).iterator().next();
            }
        }

        start.add(firstEl.getId());
        end.add(lastEl.getId());

//        if(type.equals("CAUSCONC1") && pes1.getBRelation(evt1L, evt2L).equals(BehaviorRelation.INV_CAUSALITY)){
//            b.add(model.getTaskFromEvent(evt1M).getId());
//            a.add(model.getTaskFromEvent(evt2M).getId());
//        }else{
            a.add(taskA.getId());
            b.add(taskB.getId());
//        }

        DifferenceML difference = new DifferenceML(impact);
        difference.setA(a);
        difference.setB(b);
        difference.setStart(start);
        difference.setEnd(end);
        difference.setGreys(greys);
        difference.setSentence(sentence);
        difference.setType(type);

        // DEBUG
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

//        printModels("m", "1", net, model.getReader(), null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());


        return difference;
    }

    private boolean isSafe2RemSp(FlowNode firstEl, FlowNode taskA, FlowNode taskB) {
        return (model.getBpmnModel().getDirectedEdge(firstEl, taskA) != null && model.getBpmnModel().getDirectedEdge(firstEl, taskB) != null);
    }

    private boolean isSafe2RemJn(FlowNode firstEl, FlowNode taskA, FlowNode taskB) {
        return (model.getBpmnModel().getDirectedEdge(taskA, firstEl) != null && model.getBpmnModel().getDirectedEdge(taskB, firstEl) != null);
    }

    private FlowNode getPredEvt(Integer evt1M, Integer evt2M) {
        return model.getCommonPredUnf(evt1M, evt2M);
    }

    private FlowNode getSuccEvt(Integer evt1M, Integer evt2M) {
        return model.getCommonSuccUnf(evt1M, evt2M);
    }

    private Operation getContext(LinkedList<Operation> path2Leaf, Operation op1, Operation op2, Operation op3) {
        int init = Math.min(path2Leaf.indexOf(op1), Math.min(path2Leaf.indexOf(op2), path2Leaf.indexOf(op3))) - 1;

        if (init < 0)
            return null;
        else {
            for (int i = init; i >= 0; i--) {
                if (path2Leaf.get(i).op.equals(Op.MATCH))
                    return path2Leaf.get(i);
            }
        }

        return null;
    }

    private Operation findByLabel(List<Operation> hides2Query, String label) {
        for (Operation op : hides2Query)
            if (op.label.equals(label))
                return op;

        return null;
    }

    private String verbalizeConcConf(String rep1, String rep2, String context, String first, String second) {
        return String.format("In the %s, after '%s', '%s' and '%s' are concurrent, while in the %s they are mutually exclusive", rep1, context, first, second, rep2);
    }

    private String verbalizeCausConc(String rep1, String rep2, String context, String first, String second) {
        return String.format("In the %s, after '%s', '%s' occurs before '%s', while in the %s they are concurrent", rep1, context, first, second, rep2);
    }

    private String verbalizeTaskSub(String rep, String label, LinkedList<Operation> hideOriginal, LinkedList<Operation> hideNew) {
        return String.format("In the %s, after '%s', '%s' is substituted by '%s'", rep, label, getLabelsInterval(hideOriginal), getLabelsInterval(hideNew));
    }

    private LinkedList<Operation> find4TaskSub(LinkedList<Operation> path2Leaf, int i, LinkedList<Operation> hides, Op operation) {
        int l = 0;
        State rootSub = path2Leaf.get(i).nextState;

        // Check if after the hides there are only matches
        for (int j = i + hides.size(); j < path2Leaf.size(); j++)
            if (!(path2Leaf.get(j).op.equals(Op.MATCH) || path2Leaf.get(j).op.equals(Op.MATCHNSHIFT)))
                return null;

        HashSet<LinkedList<Operation>> hideGroups = findOpHidesFrom(rootSub, operation);
        LinkedList<Operation> usedGroup = null;
        for (LinkedList<Operation> hideGroup : hideGroups) {
            State current = hideGroup.get(hideGroup.size() - 1).nextState;
            for (int j = i + hides.size(); j < path2Leaf.size(); j++) {
                current = findMatch(current, path2Leaf.get(j).label);
                if (current == null)
                    break;
            }

            if (current != null) {
                usedGroup = hideGroup;
                break;
            }
        }

        return usedGroup;
    }

    private HashSet<LinkedList<Operation>> findOpHidesFrom(State root, Op operation) {
        HashSet<LinkedList<Operation>> groups = new HashSet<>();

        for (Operation op : descendants.get(root))
            if ((operation.equals(Op.LHIDE) && (op.op.equals(Op.RHIDE) || op.op.equals(Op.RHIDENSHIFT))) ||
                    (!operation.equals(Op.LHIDE) && op.op.equals(Op.LHIDE))) {
                HashSet<LinkedList<Operation>> hidesFrom = findOpHidesFrom(op.nextState, operation);
                if (hidesFrom.isEmpty()) {
                    LinkedList<Operation> newList = new LinkedList<>();
                    newList.add(op);
                    groups.add(newList);
                } else {
                    for (List<Operation> hideList : hidesFrom) {
                        LinkedList<Operation> copy = new LinkedList<>(hideList);
                        copy.add(0, op);
                        groups.add(copy);
                    }
                }
            }

        return groups;
    }

    private String verbalizeUnmRep(String rep, String context, LinkedList<Operation> hides) {
        return String.format("In the %s, '%s' is repeated after '%s'", rep, getLabelsInterval(hides), context);
    }

    private Pair<List<Operation>, LinkedList<Operation>> look4UnmRep(LinkedList<Operation> path2Leaf, int position, LinkedList<Operation> hides) {
        LinkedList<Operation> matches = new LinkedList<>();
        for (int i = 0; i < position + 1; i++)
            if (isMatch(path2Leaf.get(i)))
                matches.add(path2Leaf.get(i));

        Pair<List<Operation>, LinkedList<Operation>> pair = getLCList(matches, hides);

        if (pair.getSecond().isEmpty())
            return null;

        return pair;
    }

    private String verbalizeTaskSkip(String rep, String context, LinkedList<Operation> hides) {
        return String.format("In the %s, after '%s', '%s' is optional", rep, context, getLabelsInterval(hides));
    }

    private String verbalizeTaskSkip(String rep, String context, String hides) {
        return String.format("In the %s, after '%s', '%s' is optional", rep, context, hides);
    }

    private boolean check4Skip(LinkedList<Operation> path2Leaf, int i, LinkedList<Operation> hides) {
        /* Skip all silent transition in the skip,
         * since they can not be matched */
        LinkedList<Operation> toDelete = new LinkedList<>();
        for (int l = 0; l < hides.size(); l++)
            if (!commonLabels.contains(hides.get(l).label))
                toDelete.add(hides.get(l));
        hides.removeAll(toDelete);

        int l = 0;
        State rootSkipM = path2Leaf.get(i).nextState;
        boolean isSkip = true;

        for (Operation hide : hides) {
            rootSkipM = findMatch(rootSkipM, hide.label);
            if (rootSkipM == null)
                return false;
            l++;
        }

        if (l > 0) {
            int q = path2Leaf.indexOf(hides.get(l - 1));
            if (path2Leaf.get(q + 1).op.equals(Op.MATCH) || path2Leaf.get(q + 1).op.equals(Op.MATCHNSHIFT))
                if (findMatch(rootSkipM, path2Leaf.get(q + 1).label) != null)
                    return true;
        }

        return false;
    }

    private State findMatch(State rootSkipM, String label) {
        for (Operation op : descendants.get(rootSkipM))
            if (isMatch(op))
                if (op.label.equals(label))
                    return op.nextState;

        return null;
    }

    private Operation findRecursiveMatch(State rootSkipM, String label) {
        for (Operation op : descendants.get(rootSkipM)) {
            if (isMatch(op))
                if (op.label.equals(label))
                    return op;
            Operation recursive = findRecursiveMatch(op.nextState, label);
            if (recursive != null)
                return recursive;
        }

        return null;
    }

    private LinkedHashSet<LinkedList<Operation>> getContiguousGroups(LinkedList<Operation> path2Leaf, LinkedList<Operation> hides) {
        LinkedHashSet<LinkedList<Operation>> groups = new LinkedHashSet<>();

        int i = 0;
        int prev = -1;
        LinkedList<Operation> group = new LinkedList<>();
        while (i < hides.size()) {
            if (group.isEmpty() || (prev >= 0 && path2Leaf.indexOf(hides.get(i)) == prev + 1)) {
                prev = path2Leaf.indexOf(hides.get(i));
                group.add(hides.get(i));
                i++;
            } else {
                groups.add(new LinkedList<Operation>(group));
                prev = -1;
                group.clear();
            }
        }

        if (group.size() > 0 && !groups.contains(group))
            groups.add(group);

        LinkedHashSet<LinkedList<Operation>> subGroups = new LinkedHashSet<>();
        for(LinkedList<Operation> subgroup : groups)
            for(int size = 0; size < subgroup.size(); size++)
                subGroups.add(new LinkedList<Operation>(subgroup.subList(size,subgroup.size())));

        return subGroups;
    }

    private String verbalizeTaskReloc(LinkedList<Operation> lhide, LinkedList<Operation> rhide, LinkedList<Operation> path2Leaf) {
        Operation opL = lhide.getFirst();
        Operation opM = rhide.getFirst();

        String repr = "log";
        String compRepr = "model";
        String source = getPred(opL, path2Leaf, repr);
        String target = getPred(opM, path2Leaf, compRepr);

        if (path2Leaf.indexOf(opL) < path2Leaf.indexOf(opM)) {
            repr = "model";
            compRepr = "log";
            String tmp = source;
            source = target;
            target = tmp;
        }

        String interval = getLabelsInterval(lhide);

        String sentence = String.format("In the %s, '%s' occurs after '%s' instead of '%s'", repr, interval, source, target);
        return sentence;
    }

    private String getLabelsInterval(LinkedList<Operation> lhide) {
        String interval = "[";
        int i = 0;
        for (Operation op : lhide) {
            interval += op.label;

            if (i < lhide.size() - 1) interval += ",";
            i++;
        }

        return interval + "]";
    }

    private String getLabelsModel(List<Integer> events) {
        String interval = "[";
        int i = 0;
        for (Integer event : events)
            interval += pes2.getLabel(event);

        return interval + "]";
    }

    private String getPred(Operation op, LinkedList<Operation> path2Leaf, String source) {
        String path = "[";

        int i = path2Leaf.indexOf(op);
        /* Collect the labels in the log or in the model to describe the context
         * of the difference (Matches and Hides(either of the log or the model)) */
        for (int j = 0; j < i; j++)
            if (commonLabels.contains(path2Leaf.get(j).label)) {
                if (isMatch(path2Leaf.get(j))){
                    if(path.length() > 1)
                        path += ", ";

                    path += path2Leaf.get(j).label;
                }else if (source.equals("log") && isLHide(path2Leaf.get(j))){
                    if(path.length() > 1)
                        path += ", ";

                    path += path2Leaf.get(j).label;
                }else if (source.equals("model") && isRHide(path2Leaf.get(j))) {
                    if(path.length() > 1)
                        path += ", ";

                    path += path2Leaf.get(j).label;
                }
            }

        return path + "]";
    }

    private LinkedList<Operation> getPredOps(Operation op, LinkedList<Operation> path2Leaf, String source) {
        LinkedList<Operation> path = new LinkedList<>();

        int i = path2Leaf.indexOf(op);
        /* Collect the labels in the log or in the model to describe the context
         * of the difference (Matches and Hides(either of the log or the model)) */
        for (int j = 0; j < i; j++)
            if (commonLabels.contains(path2Leaf.get(j).label)) {
                if (isMatch(path2Leaf.get(j)))
                    path.add(path2Leaf.get(j));
                else if (source.equals("model") && isRHide(path2Leaf.get(j)))
                    path.add(path2Leaf.get(j));
            }

        return path;
    }

    private void filterCausalConcMismatches(List<Operation> hidesL, List<Operation> hidesR, LinkedList<Operation> path2Leaf) {
        HashSet<Operation> toRemoveL = new HashSet<>();
        HashSet<Operation> toRemoveR = new HashSet<>();

        for (Operation op : path2Leaf) {
            if (op.op.equals(Op.MATCH)) {
                Pair<Integer, Integer> matchedEvents = (Pair<Integer, Integer>) op.target;

                for (int i = 0; i < hidesL.size(); i++) {
                    Operation opL = hidesL.get(i);
                    Operation opR = findByLabel(hidesR, opL.label);

                    if (opR != null && isCausConcMist(pes1.getBRelation(matchedEvents.getFirst(), (Integer) opL.target),
                            pes2.getBRelation(matchedEvents.getSecond(), (Integer) opR.target))) {
                        toRemoveL.add(opL);
                        toRemoveR.add(opR);
                    }
                }
            }
        }

        hidesL.removeAll(toRemoveL);
        hidesR.removeAll(toRemoveR);
    }

    private boolean isCausConcMist(BehaviorRelation bRelation1, BehaviorRelation bRelation2) {

        if (bRelation2.equals(BehaviorRelation.CONCURRENCY)) {
            if (bRelation1.equals(BehaviorRelation.CAUSALITY) || bRelation1.equals(BehaviorRelation.INV_CAUSALITY))
                return true;
        } else if (bRelation1.equals(BehaviorRelation.CONCURRENCY)) {
            if (bRelation2.equals(BehaviorRelation.INV_CAUSALITY) || bRelation2.equals(BehaviorRelation.CAUSALITY))
                return true;
        }

        return false;
    }

    private Pair<LinkedList<Operation>, LinkedList<Operation>> getLCList(LinkedList<Operation> lhide, LinkedList<Operation> rhide, LinkedList<Operation> path2Leaf) {
        int start1 = 0;
        int max = 0;

        int start2 = 0;
        for (int i = 0; i < lhide.size(); i++) {
            for (int j = 0; j < rhide.size(); j++) {
                int x = 0;
                while (lhide.get(i + x).label.equals(rhide.get(j + x).label)) {
                    if (x > 0) {
                        int l = path2Leaf.indexOf(lhide.get(i + x));
                        int r = path2Leaf.indexOf(rhide.get(j + x));

                        int lM1 = path2Leaf.indexOf(lhide.get(i + x - 1));
                        int rM1 = path2Leaf.indexOf(rhide.get(j + x - 1));

                        if (l - 1 != lM1 || r - 1 != rM1)
                            break;
                    }

                    x++;
                    if (((i + x) >= lhide.size()) || ((j + x) >= rhide.size())) break;
                }
                if (x > max) {
                    max = x;
                    start1 = i;
                    start2 = j;
                }
            }
        }

        return new Pair<LinkedList<Operation>, LinkedList<Operation>>(new LinkedList<>(lhide.subList(start1, (start1 + max))), new LinkedList<>(rhide.subList(start2, (start2 + max))));
    }

    private Pair<List<Operation>, LinkedList<Operation>> getLCList(LinkedList<Operation> lhide, LinkedList<Operation> rhide) {
        int start1 = 0;
        int max = 0;

        int start2 = 0;
        for (int i = 0; i < lhide.size(); i++) {
            for (int j = 0; j < rhide.size(); j++) {
                int x = 0;
                while (lhide.get(i + x).label.equals(rhide.get(j + x).label)) {
                    if (x > 0) {
                        int l = i + x;
                        int r = j + x;

                        int lM1 = i + x - 1;
                        int rM1 = j + x - 1;

                        if (l - 1 != lM1 || r - 1 != rM1)
                            break;
                    }

                    x++;
                    if (((i + x) >= lhide.size()) || ((j + x) >= rhide.size())) break;
                }
                if (x > max) {
                    max = x;
                    start1 = i;
                    start2 = j;
                }
            }
        }

        return new Pair<List<Operation>, LinkedList<Operation>>(lhide.subList(start1, (start1 + max)), new LinkedList<>(rhide.subList(start2, (start2 + max))));
    }

    public LinkedList<Operation> getPath(State leaf) {
        LinkedList<Operation> path = new LinkedList<>();

//        for (State st : ancestors.keySet())
//            if (ancestors.get(st).size() > 1) {
//                System.out.println("Error (A node has more than one predecessor)!: " + ancestors.get(st).size());
//                System.exit(0);
//            }

        State target = leaf;
        State source = ancestors.get(leaf).iterator().next();

        while (source != null)
            for (Operation op : descendants.get(source))
                if (op.nextState.equals(target)) {
                    path.add(0, op);
                    target = source;
                    if (!source.equals(root))
                        source = ancestors.get(source).iterator().next();
                    else
                        source = null;
                    break;
                }

        return path;
    }

    public HashSet<State> getAllLeaves() {
        HashSet<State> leaves = new HashSet<>();
        for (State antecesor : ancestors.keySet())
            if (!descendants.containsKey(antecesor))
                leaves.add(antecesor);

        return leaves;
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
                for (Operation op : descendants.get(pred))
                    if (op.nextState.equals(curr)) {
                        operations.add(op);
                        break;
                    }
                if (!visited.contains(pred) && !open.contains(pred))
                    open.push(pred);
            }
        }


        for (State s : visited) {
            Set<Operation> toDelete = new HashSet<>();
            for (Operation o : descendants.get(s))
                if (!operations.contains(o))
                    toDelete.add(o);
            for (Operation o : toDelete)
                descendants.remove(s, o);
        }

        lhideOps.retainAll(operations);
        rhideOps.retainAll(operations);
    }

    public DifferencesML getDifferences() {
        return differences;
    }

    private void verbalizeAdditionalAcyclicModelBehavior(Collection<Entry<State, List<Integer>>> acyclicIntervals) {
        for (Entry<State, List<Integer>> entry : acyclicIntervals) {
            LinkedList<Integer> context = new LinkedList<>(entry.getKey().c2.elementSet());

            String sentence = String.format("In the log, the interval %s does not occur after '%s'",
                    translate(entry.getValue()), pes2.getLabels(entry.getKey().c2.elementSet()).toString());
            float impact = 1.0f;

            LinkedList<Integer> orderedEvts = new LinkedList<>();
            for(Integer evt : entry.getValue())
                orderedEvts.add(evt);

            Comparator<Integer> comparator = new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return pes2.getLocalConfiguration(o1).cardinality() - pes2.getLocalConfiguration(o2).cardinality();
                }
            };

            Collections.sort(orderedEvts, comparator);
            Collections.sort(context, comparator);

            List<String> newTasks = new LinkedList<>();
            for(Integer evt : orderedEvts)
                newTasks.add(pes2.getLabel(evt));

            FlowNode startTask = null;
            int i = context.size()-1;
            while(startTask == null){
                startTask = model.getTaskFromEvent(context.get(i));
                i--;
            }

            List<String> start =  new LinkedList<>();
            start.add(startTask.getId());

            FlowNode endTask = model.getBpmnModel().getDirectSuccessors(startTask).iterator().next();
            List<String> end =  new LinkedList<>();
            end.add(endTask.getId());

            DifferenceML difference = new DifferenceML(impact);
            difference.setSentence(sentence);
            difference.setNewTasks(newTasks);
            difference.setStart(start);
            difference.setEnd(end);
            difference.setType("UNOBSACYCLICINTER");
            if(difference != null) differences.add(difference);
        }
    }

    private void verbalizeAdditionalCyclicModelBehavior() {
        for (Entry<State, Multiset<Integer>> entry : expprefix.getAdditionalCyclicIntervals().entries()) {
            Set<String> cycleTask = translate(entry.getValue());

            if (cycleTask.size() == 0)
                continue;

            float impact = 1.0f;
            System.out.println("Impact = " + impact);

            DifferenceML difference = getUNMRepetitionDiff(entry);
            if(difference != null) differences.add(difference);
        }
    }

    private Set<String> translate(Collection<Integer> multiset) {
        Set<String> set = new HashSet<>();
        for (Integer ev : multiset)
            if (!pes2.getInvisibleEvents().contains(ev))
                set.add(translate(pes2, ev));
        return set;
    }

    private String translate(NewUnfoldingPESSemantics<Integer> pes, Integer f) {
        if (DEBUG)
            return String.format("%s(%d)", f != null ? pes.getLabel(f) : null, f);
        else
            return String.format("%s", f != null ? pes.getLabel(f) : null);
    }

    private void addPSPBranchToGlobalPSP(List<Operation> opSeq) {
        State pred = root;

        for (int i = 0; i < opSeq.size(); i++) {
            Operation curr = opSeq.get(i);

            if (curr.op == Op.RHIDE || curr.op == Op.RHIDENSHIFT) {
                if (!pes2.getInvisibleEvents().contains(curr.target))
                    rhideOps.add(curr);
            } else if (curr.op == Op.LHIDE)
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
            for (Operation desc : descendants.get(pred))
                if (desc.op == curr.op) {
                    if (curr.op == Op.MATCH || curr.op == Op.MATCHNSHIFT) {
                        Pair<Integer, Integer> pair1 = (Pair) curr.target;
                        Pair<Integer, Integer> pair2 = (Pair) desc.target;
                        if (pair1.equals(pair2)) {
                            found = true;
                            break;
                        }
                    } else {
                        Integer ev1 = (Integer) curr.target;
                        Integer ev2 = (Integer) desc.target;
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

        for (Operation op : descendants.values())
            out.printf("\tn%d [label=\"%s\"];\n", op.nextState.hashCode(), op.nextState);

        for (Entry<State, Operation> entry : descendants.entries())
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
            String modelColor = printBPMN2DOT(colorsBPMN1, (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) loader.getModel(), loader, repetitions1, repetitions2);
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

    public Pair<FlowNode, HashSet<FlowNode>> getNextCommon(HashSet<FlowNode> originators) {
        Queue<Pair<FlowNode, FlowNode>> queue = new LinkedList<>();
        for (FlowNode node : originators)
            queue.add(new Pair<FlowNode, FlowNode>(node, node));

        HashMap<FlowNode, HashSet<FlowNode>> marked = new HashMap<>();
        while (!queue.isEmpty()) {
            Pair<FlowNode, FlowNode> pair = queue.poll();

            if (!marked.containsKey(pair.getSecond()))
                marked.put(pair.getSecond(), new HashSet<FlowNode>());

            marked.get(pair.getSecond()).add(pair.getFirst());
            if (marked.get(pair.getSecond()).containsAll(originators) && !originators.contains(pair.getSecond())) {
                HashSet<FlowNode> visited = new HashSet<>(marked.keySet());
                visited.remove(pair.getSecond());
                return new Pair(pair.getSecond(), visited);
            }

            for (FlowNode successor : model.getBpmnModel().getDirectSuccessors(pair.getSecond()))
                queue.add(new Pair<FlowNode, FlowNode>(pair.getFirst(), successor));
        }

        return null;
    }

    public HashSet<String> getMatchedActivities() {
        HashSet<String> matchedAct = new HashSet<>();

        for (Entry<State, Operation> entry : lastMatchMap.entrySet()) {
            if (entry.getValue() == null)
                continue;
            Pair<Integer, Integer> pair = (Pair<Integer, Integer>) entry.getValue().target;
            if (pes2.getLabel(pair.getSecond()).equals("_0_") || pes2.getLabel(pair.getSecond()).equals("_1_"))
                continue;

            matchedAct.add(model.getTaskFromEvent(pair.getSecond()).getId());
        }

        return matchedAct;
    }

    public HashSet<String> getEdgesBetween(HashSet<String> nodes) {
        HashSet<String> edgesBT = new HashSet<>();

        for (ControlFlow flow : model.getBpmnModel().getControlFlow())
            if (nodes.contains(flow.getSource().getId()) && nodes.contains(flow.getTarget().getId())) {
                ControlFlow edge = model.getBpmnModel().getDirectedEdge((FlowNode) flow.getSource(), (FlowNode) flow.getTarget());
                edgesBT.add(edge.getId());
            }

        return edgesBT;
    }

    public Multiset<Integer> getMultiset(BitSet bs) {
        Multiset<Integer> multiset = HashMultiset.create();
        for (int event = bs.nextSetBit(0); event >= 0; event = bs.nextSetBit(event + 1))
            multiset.add(event);

        return multiset;
    }
}
