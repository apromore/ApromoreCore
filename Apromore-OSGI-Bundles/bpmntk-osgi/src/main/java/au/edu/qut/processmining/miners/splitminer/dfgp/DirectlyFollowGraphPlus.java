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

package au.edu.qut.processmining.miners.splitminer.dfgp;

import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.log.graph.LogNode;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;

import com.raffaeleconforti.automaton.Automaton;
import com.raffaeleconforti.automaton.AutomatonFactory;
import com.raffaeleconforti.automaton.Edge;
import com.raffaeleconforti.automaton.Node;
import com.raffaeleconforti.context.FakePluginContext;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolver;
import com.raffaeleconforti.ilpsolverwrapper.impl.gurobi.Gurobi_Solver;
import com.raffaeleconforti.ilpsolverwrapper.impl.lpsolve.LPSolve_Solver;
import com.raffaeleconforti.noisefiltering.event.InfrequentBehaviourFilter;
import com.raffaeleconforti.noisefiltering.event.infrequentbehaviour.automaton.AutomatonInfrequentBehaviourDetector;
import com.raffaeleconforti.noisefiltering.event.optimization.wrapper.WrapperInfrequentBehaviourSolver;

import com.raffaeleconforti.noisefiltering.event.selection.NoiseFilterResult;
import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;

import java.util.*;

/**
 * Created by Adriano on 24/10/2016.
 */
public class DirectlyFollowGraphPlus {

    private SimpleLog log;
    private int startcode;
    private int endcode;

    private Set<DFGEdge> edges;
    private Map<Integer, DFGNode> nodes;
    private Map<Integer, HashSet<DFGEdge>> outgoings;
    private Map<Integer, HashSet<DFGEdge>> incomings;
    private Map<Integer, HashMap<Integer, DFGEdge>> dfgp;

    private Set<Integer> loopsL1;
    private Set<DFGEdge> loopsL2;
    private Map<Integer, HashSet<Integer>> parallelisms;

    private double frequencyThreshold;
    private double parallelismsThreshold;
    private DFGPUIResult.FilterType filterType;

    public DirectlyFollowGraphPlus(SimpleLog log) {
        this(log, DFGPUIResult.FREQUENCY_THRESHOLD, DFGPUIResult.PARALLELISMS_THRESHOLD, DFGPUIResult.FilterType.STD);
    }

    public DirectlyFollowGraphPlus(SimpleLog log, double frequencyThreshold, double parallelismsThreshold, DFGPUIResult.FilterType filterType) {
        this.log = log;
        this.startcode = log.getStartcode();
        this.endcode = log.getEndcode();
        this.frequencyThreshold = frequencyThreshold;
        this.parallelismsThreshold = parallelismsThreshold;
        this.filterType = filterType;
    }

    public BPMNDiagram getDFG() {
        buildDirectlyFollowsGraph();
        return getDFGP(true);
    }

    public BPMNDiagram getDFGP(boolean labels) {
        Map<Integer, String> events = log.getEvents();
        BPMNDiagram diagram = new BPMNDiagramImpl("DFGP-diagram");
        HashMap<Integer, BPMNNode> mapping = new HashMap<>();
        String label;
        Activity task;
        BPMNNode src, tgt;

        for( int event : nodes.keySet() ) {
            label = events.get(event) + "\n(" + nodes.get(event).getFrequency() + ")";
            task = diagram.addActivity( (labels ? label : Integer.toString(event)), false, false, false, false, false);
            mapping.put(event, task);
        }

        for( DFGEdge edge : edges ) {
            src = mapping.get(edge.getSourceCode());
            tgt = mapping.get(edge.getTargetCode());
            diagram.addFlow(src, tgt, edge.toString());
        }

        return diagram;
    }

    public BPMNDiagram convertIntoBPMNDiagram() {
        BPMNDiagram diagram = new BPMNDiagramImpl("eDFGP-diagram");
        HashMap<Integer, BPMNNode> mapping = new HashMap<>();
        String label;
        BPMNNode node;
        BPMNNode src, tgt;

        for( int event : nodes.keySet() ) {
            label = Integer.toString(event);

            if( event == startcode || event == endcode )
                node = diagram.addEvent(label, (event == startcode ? Event.EventType.START : Event.EventType.END), Event.EventTrigger.NONE, (event == startcode ? Event.EventUse.CATCH : Event.EventUse.THROW), true, null);
            else
                node = diagram.addActivity(label, loopsL1.contains(event), false, false, false, false);

            mapping.put(event, node);
        }

        for( DFGEdge edge : edges ) {
            src = mapping.get(edge.getSourceCode());
            tgt = mapping.get(edge.getTargetCode());
            diagram.addFlow(src, tgt, edge.toString());
        }

        return diagram;
    }

    public boolean areConcurrent(int A, int B) {
        return (parallelisms.containsKey(A) && parallelisms.get(A).contains(B));
    }

    public void buildDFGP() {
//        System.out.println("DFGP - starting ... ");
//        System.out.println("DFGP - [Settings] parallelisms threshold: " + parallelismsThreshold);

        buildDirectlyFollowsGraph();                //first method to execute
        detectLoops();                              //depends on buildDirectlyFollowsGraph()
        detectParallelisms(parallelismsThreshold);  //depends on detectLoops()

//        printEdges();
//        System.out.println("DEBUG - edges before filtering: " + edges.size());
        switch(filterType) {                        //depends on detectParallelisms()
            case STD: standardFiltering();
                break;
            case GUB: generateNoiseFilteredDFG();
                break;
            case LPS: noiseFiltering(false);
                break;
            case WTH: filterWithThreshold();
                break;
        }
//        System.out.println("DEBUG - edges after filtering: " + edges.size());

        exploreAndRemove();                         //last method to execute
    }

    private void buildDirectlyFollowsGraph() {
        Map<String, Integer> traces = log.getTraces();
        Map<Integer, String> events = log.getEvents();

        StringTokenizer trace;
        int traceFrequency;

        int event;
        int prevEvent;

        DFGNode node;
        DFGNode prevNode;
        DFGEdge edge;

        DFGNode autogenStart;
        DFGNode autogenEnd;

        nodes = new HashMap<>();
        edges = new HashSet<>();
        outgoings = new HashMap<>();
        incomings = new HashMap<>();
        dfgp = new HashMap<>();

        autogenStart = new DFGNode(events.get(startcode), startcode);
        this.addNode(autogenStart);
//        while parsing the simple log we will always skip the start event,
//        so we set now the maximum frequency because it is an artificial start event
        autogenStart.increaseFrequency(log.size());

        autogenEnd = new DFGNode(events.get(endcode), endcode);
        this.addNode(autogenEnd);

        for( String t : traces.keySet() ) {
            trace = new StringTokenizer(t, "::");
            traceFrequency = traces.get(t);

//            consuming the start event that is always 0
            trace.nextToken();
            prevEvent = startcode;
            prevNode = autogenStart;

            while( trace.hasMoreTokens() ) {
//                we read the next event of the trace until it is finished
                event = Integer.valueOf(trace.nextToken());

                if( !nodes.containsKey(event) ) {
                    node =  new DFGNode(events.get(event), event);
                    this.addNode(node);
                } else node = nodes.get(event);

//                  increasing frequency of this event occurrence
                node.increaseFrequency(traceFrequency);

                if( !dfgp.containsKey(prevEvent) || !dfgp.get(prevEvent).containsKey(event) ) {
                    edge = new DFGEdge(prevNode, node);
                    this.addEdge(edge);
                }

//                  increasing frequency of this directly following relationship
                dfgp.get(prevEvent).get(event).increaseFrequency(traceFrequency);

                prevEvent = event;
                prevNode = node;
            }
        }
    }

    private void detectLoops() {
        Map<String, Integer> traces = log.getTraces();
        HashSet<DFGEdge> removableLoopEdges = new HashSet();

        DFGEdge e2;
        int src;
        int tgt;

        String src2tgt_loop2Pattern;
        String tgt2src_loop2Pattern;

        int src2tgt_loop2Frequency;
        int tgt2src_loop2Frequency;

        int loop2score;

        loopsL1 = new HashSet<>();
        loopsL2 = new HashSet<>();

//        System.out.println("DFGP - evaluating loops length ONE ...");
        for( DFGEdge e : edges ) {
            src = e.getSourceCode();
            tgt = e.getTargetCode();
            if( src == tgt ) {
                loopsL1.add(src);
                removableLoopEdges.add(e);
            }
        }

//        we removed the loop length 1 edges, because late we will just mark them as self-loop activities
//        System.out.println("DFGP - loops length ONE found: " + loopsL1.size());
        for( DFGEdge e : removableLoopEdges ) this.removeEdge(e, false);

//        System.out.println("DEBUG - found " + loopsL1.size() + " self-loops:");
//        for( int code : loopsL1 ) System.out.println("DEBUG - self-loop: " + code);

        for( DFGEdge e1 : edges )  {
            src = e1.getSourceCode();
            tgt = e1.getTargetCode();

//            if src OR tgt are length 1 loops, we do not evaluate length 2 loops for this edge,
//            because a length 1 loop in parallel with something else
//            can generate pattern of the type [src :: tgt :: src] OR [tgt :: src :: tgt]
            if( !loopsL2.contains(e1) && dfgp.get(tgt).containsKey(src) && !loopsL1.contains(src) && !loopsL1.contains(tgt) ) {
                e2 = dfgp.get(tgt).get(src);

                src2tgt_loop2Pattern = "::" + src + "::" + tgt + "::" + src + "::";
                tgt2src_loop2Pattern = "::" + tgt + "::" + src + "::" + tgt + "::";
                src2tgt_loop2Frequency = 0;
                tgt2src_loop2Frequency = 0;

                for( String trace : traces.keySet() ) {
                    src2tgt_loop2Frequency += (StringUtils.countMatches(trace, src2tgt_loop2Pattern)*traces.get(trace));
                    tgt2src_loop2Frequency += (StringUtils.countMatches(trace, tgt2src_loop2Pattern)*traces.get(trace));
                }

                loop2score = src2tgt_loop2Frequency + tgt2src_loop2Frequency;

//                if the loop2score is not zero, it means we found patterns of the type:
//                [src :: tgt :: src] OR [tgt :: src :: tgt], so we set both edges as short-loops
                if( loop2score != 0 ) {
                    loopsL2.add(e1);
                    loopsL2.add(e2);
                }
            }
        }

//        System.out.println("DFGP - loops length TWO found: " + loopsL2.size()/2);
    }

    private void detectParallelisms(double epslon) {
        int totalParallelisms = 0;

        DFGEdge e2;
        int src;
        int tgt;

        int src2tgt_frequency;
        int tgt2src_frequency;
        double parallelismScore;

        HashSet<DFGEdge> removableEdges = new HashSet<>();

        parallelisms = new HashMap<>();

        for (DFGEdge e1 : edges) {
            src = e1.getSourceCode();
            tgt = e1.getTargetCode();

            if (!loopsL2.contains(e1) && !removableEdges.contains(e1) && dfgp.get(tgt).containsKey(src)) {
//                this means: src || tgt is candidate parallelism
                e2 = dfgp.get(tgt).get(src);

                src2tgt_frequency = e1.getFrequency();
                tgt2src_frequency = e2.getFrequency();
                parallelismScore = (double) (src2tgt_frequency - tgt2src_frequency) / (src2tgt_frequency + tgt2src_frequency);

                if (Math.abs(parallelismScore) < epslon) {
//                    if parallelismScore is less than the threshold epslon,
//                    we set src || tgt and vice-versa, and we remove e1 and e2
//                    if( bestEdges.contains(e1) && !bestEdges.contains(e2) )  continue;
//                    if( !bestEdges.contains(e1) && bestEdges.contains(e2) )  removableEdges.add(e1);
                    if (!parallelisms.containsKey(src)) parallelisms.put(src, new HashSet<Integer>());
                    parallelisms.get(src).add(tgt);
                    if (!parallelisms.containsKey(tgt)) parallelisms.put(tgt, new HashSet<Integer>());
                    parallelisms.get(tgt).add(src);
                    removableEdges.add(e1);
                    removableEdges.add(e2);
                    totalParallelisms++;
                } else {
//                    otherwise we remove the least frequent edge, e1 or e2
                    if( parallelismScore > 0 ) removableEdges.add(e2);
                    else removableEdges.add(e1);
                }
            }
        }

        ArrayList<DFGEdge> orderedRemovableEdges = new ArrayList<>(removableEdges);
        Collections.sort(orderedRemovableEdges);
        while( !orderedRemovableEdges.isEmpty() ) {
            DFGEdge re = orderedRemovableEdges.remove(0);
            if( !this.removeEdge(re, true) ) {
//                System.out.println("DEBUG - impossible remove: " + re.print());
                src = re.getSourceCode();
                tgt = re.getTargetCode();
                if( parallelisms.containsKey(src) ) parallelisms.get(src).remove(tgt);
                if( parallelisms.containsKey(tgt) ) parallelisms.get(tgt).remove(src);
                if( (re = dfgp.get(tgt).get(src)) != null ) this.removeEdge(re, true);
            }
        }

//        System.out.println("DFGP - parallelisms found: " + totalParallelisms);
    }

    private Set<DFGEdge> getMostFrequentSuccessorsAndPredecessors() {
        Set<DFGEdge> bestEdges = new HashSet<>();

        for( int node : nodes.keySet() ) {
            if( node != endcode ) bestEdges.add(Collections.max(outgoings.get(node)));
            if( node != startcode ) bestEdges.add(Collections.max(incomings.get(node)));
        }

        return bestEdges;
    }

    private void standardFiltering() {
        int src;
        int tgt;
        DFGEdge recoverableEdge;

        Set<DFGEdge> bestEdges = getMostFrequentSuccessorsAndPredecessors();
        ArrayList<DFGEdge> frequencyOrderedBestEdges = new ArrayList<>(bestEdges);

        for( DFGEdge e : new HashSet<>(edges) ) this.removeEdge(e, false);

        Collections.sort(frequencyOrderedBestEdges);
        for( int i = (frequencyOrderedBestEdges.size()-1); i >= 0; i-- ) {
            recoverableEdge = frequencyOrderedBestEdges.get(i);

            src = recoverableEdge.getSourceCode();
            tgt = recoverableEdge.getTargetCode();
            if( outgoings.get(src).isEmpty() || incomings.get(tgt).isEmpty() ) this.addEdge(recoverableEdge);
        }
    }

    private void filterWithThreshold() {
        int src;
        int tgt;
        int threshold;
        DFGEdge recoverableEdge;

        Set<DFGEdge> bestEdges = getMostFrequentSuccessorsAndPredecessors();
        ArrayList<DFGEdge> frequencyOrderedOptimalEdges = new ArrayList<>(bestEdges);

        Collections.sort(frequencyOrderedOptimalEdges);
        threshold = frequencyOrderedOptimalEdges.get(frequencyOrderedOptimalEdges.size()/3).getFrequency();

        for( DFGEdge e : bestEdges ) this.removeEdge(e, false);
        for( DFGEdge e : new HashSet<>(edges) ) {
            if( e.getFrequency() > threshold ) frequencyOrderedOptimalEdges.add(e);
            this.removeEdge(e, false);
        }

        Collections.sort(frequencyOrderedOptimalEdges);
        for( int i = (frequencyOrderedOptimalEdges.size()-1); i >= 0; i-- ) {
            recoverableEdge = frequencyOrderedOptimalEdges.get(i);
            if( recoverableEdge.getFrequency() > threshold ) this.addEdge(recoverableEdge);
            else {
                src = recoverableEdge.getSourceCode();
                tgt = recoverableEdge.getTargetCode();
                if( outgoings.get(src).isEmpty() || incomings.get(tgt).isEmpty() ) this.addEdge(recoverableEdge);
            }
        }

    }


    private void noiseFiltering(boolean gurobi) {
        ILPSolver ilp_solver;
        Automaton<String> automaton = new Automaton<>();
        Set<Edge<String>> removable = new HashSet<>();
        LogNode src, tgt;
        int srcID, tgtID;

        Map<Integer, Node<String>> anodes = new HashMap<>();
        Map<Edge<String>, DFGEdge> aedges = new HashMap<>();

        Node<String> srcANode, tgtANode;
        Edge<String> aedge;

        Set<DFGEdge> bestEdges = getMostFrequentSuccessorsAndPredecessors();

        for( DFGEdge e : edges ) {
            src = e.getSource();
            tgt = e.getTarget();

            srcID = src.getCode();
            tgtID = tgt.getCode();

            if((srcANode = anodes.get(srcID)) == null ) {
                srcANode = new Node<>(Integer.toString(srcID));
                srcANode.setFrequency(src.getFrequency());
                anodes.put(srcID, srcANode);
                automaton.addNode(srcANode);
            }


            if( !anodes.containsKey(tgtID) ) {
                tgtANode = new Node<>(Integer.toString(tgtID));
                tgtANode.setFrequency(tgt.getFrequency());
                anodes.put(tgtID, tgtANode);
                automaton.addNode(tgtANode);
            } else tgtANode = anodes.get(tgtID);

            aedge = new Edge<>(srcANode, tgtANode);
            aedge.setFrequency(e.getFrequency());
            automaton.addEdge(aedge);
            aedges.put(aedge, e);
            if( !bestEdges.contains(e) ) removable.add(aedge);
        }

//        System.out.println("DEBUG - automaton start: " + automaton.getAutomatonStart());
//        System.out.println("DEBUG - automaton end: " + automaton.getAutomatonEnd());

        automaton.getAutomatonStart();
        automaton.getAutomatonEnd();
        automaton.createDirectedGraph();

        if(gurobi) ilp_solver = new Gurobi_Solver();
        else ilp_solver = new LPSolve_Solver();
        WrapperInfrequentBehaviourSolver<String> solver;

        solver = new WrapperInfrequentBehaviourSolver<>(automaton, removable, automaton.getNodes(), true);
        removable = solver.identifyRemovableEdges(ilp_solver);

        for(Edge<String> ae : removable) {
//            System.out.println("DEBUG - removing edge: " + ae.getSource().getData() + " > " + ae.getTarget().getData());
            edges.remove(aedges.get(ae));
        }
    }

    private void generateNoiseFilteredDFG() {
        double percentile = frequencyThreshold;

        System.out.println("DEBUG - node before: " + nodes.size());
        System.out.println("DEBUG - edge before: " + edges.size());
        Map<Integer, String> oldEvents = log.getEvents();
        XLog xlog = log.getXLog();
        XEventClassifier classifier = new XEventNameClassifier();
        InfrequentBehaviourFilter filter = new InfrequentBehaviourFilter(classifier);
        AutomatonFactory automatonFactory = new AutomatonFactory(classifier);

        Automaton<String> automatonOriginal = automatonFactory.generate(xlog);
        NoiseFilterResult noisefilterParams = new NoiseFilterResult();
        noisefilterParams.setRepeated(true);
        noisefilterParams.setFixLevel(false);
        noisefilterParams.setPercentile(percentile);
        noisefilterParams.setNoiseLevel(filter.discoverThreshold(filter.discoverArcs(automatonOriginal, 1.0), percentile));
        noisefilterParams.setApproach(AutomatonInfrequentBehaviourDetector.AVE);
        noisefilterParams.setRequiredStates(automatonOriginal.getNodes());

        XLog fxLog = filter.filterLog(new FakePluginContext(), xlog, noisefilterParams);
        log = LogParser.getSimpleLog(fxLog);
        buildDirectlyFollowsGraph();
        alignParallelisms(oldEvents);
        System.out.println("DEBUG - node after: " + nodes.size());
        System.out.println("DEBUG - edge after: " + edges.size());
    }

    private void alignParallelisms(Map<Integer, String> oldEvents) {
        Map<Integer, String> events = log.getEvents();
        Map<Integer, HashSet<Integer>> oldParallelisms = this.parallelisms;
        int newSRC, newTGT;
        String srcLabel, tgtLabel;

        int totalParallelisms = 0;

        detectLoops();
        detectParallelisms(parallelismsThreshold);

        for( int src : oldParallelisms.keySet() )
            for( int tgt : oldParallelisms.get(src) ) {
                srcLabel = oldEvents.get(src);
                tgtLabel = oldEvents.get(tgt);
                newTGT = startcode;
                newSRC = endcode;

                for( int k : events.keySet() ) {
                    if( events.get(k).equalsIgnoreCase(srcLabel) ) newSRC = k;
                    if( events.get(k).equalsIgnoreCase(tgtLabel) ) newTGT = k;
                }

                if( (newTGT != startcode) && (newSRC != endcode) ) {
                    if( !parallelisms.containsKey(newSRC) ) parallelisms.put(newSRC, new HashSet<Integer>());
                    parallelisms.get(newSRC).add(newTGT);
                    if( !parallelisms.containsKey(newTGT) ) parallelisms.put(newTGT, new HashSet<Integer>());
                    parallelisms.get(newTGT).add(newSRC);
                    if(dfgp.containsKey(newSRC) && dfgp.get(newSRC).containsKey(newTGT)) removeEdge(dfgp.get(newSRC).get(newTGT), true);
                    if(dfgp.containsKey(newTGT) && dfgp.get(newTGT).containsKey(newSRC)) removeEdge(dfgp.get(newTGT).get(newSRC), true);
                    totalParallelisms++;
                }
            }

            System.out.println("DEBUG - old parallelisms added: " + totalParallelisms);
    }

    private void exploreAndRemove() {
        int src, tgt;

        LinkedList<Integer> toVisit = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Set<Integer> unvisited = new HashSet<>();

        toVisit.add(startcode);
        visited.add(startcode);
        unvisited.addAll(nodes.keySet());
        unvisited.remove(startcode);

        while( !toVisit.isEmpty() ) {
            src = toVisit.removeFirst();
            for( DFGEdge oe : outgoings.get(src) ) {
                tgt = oe.getTargetCode();
                if( !visited.contains(tgt) ) {
                    toVisit.addLast(tgt);
                    visited.add(tgt);
                    unvisited.remove(tgt);
                }
            }
        }

//        System.out.println("DFGP - removed nodes: " + unvisited.size());
        for(int n : unvisited) removeNode(n);
    }


    /* data objects management */

    private void addNode(DFGNode n) {
        int code = n.getCode();

        nodes.put(code, n);
        if( !incomings.containsKey(code) ) incomings.put(code, new HashSet<DFGEdge>());
        if( !outgoings.containsKey(code) ) outgoings.put(code, new HashSet<DFGEdge>());
        if( !dfgp.containsKey(code) ) dfgp.put(code, new HashMap<Integer, DFGEdge>());
    }

    private void removeNode(int code) {
        HashSet<DFGEdge> removable = new HashSet<>();
        nodes.remove(code);
        for( DFGEdge e : incomings.get(code) ) removable.add(e);
        for( DFGEdge e : outgoings.get(code) ) removable.add(e);
        for( DFGEdge e : removable ) removeEdge(e, false);
    }

    private void addEdge(DFGEdge e) {
        int src = e.getSourceCode();
        int tgt = e.getTargetCode();

        edges.add(e);
        incomings.get(tgt).add(e);
        outgoings.get(src).add(e);
        dfgp.get(src).put(tgt, e);

//        System.out.println("DEBUG - added edge: " + src + " -> " + tgt);
    }

    private boolean removeEdge(DFGEdge e, boolean safe) {
        int src = e.getSourceCode();
        int tgt = e.getTargetCode();
        if( safe && ((incomings.get(tgt).size() == 1) || (outgoings.get(src).size() == 1)) ) return false;
        incomings.get(tgt).remove(e);
        outgoings.get(src).remove(e);
        dfgp.get(src).remove(tgt);
        edges.remove(e);
        return true;
//        System.out.println("DEBUG - removed edge: " + src + " -> " + tgt);
    }


    /* DEBUG methods */

    private void printEdges() {
        for(DFGEdge e : edges)
            System.out.println("DEBUG - edge : " + e.print());
    }

    public void printNodes() {
        for( DFGNode n : nodes.values() )
            System.out.println("DEBUG - node : " + n.print());
    }

    public void printParallelisms() {
        System.out.println("DEBUG - printing parallelisms:");
        for( int A : parallelisms.keySet() ) {
            System.out.print("DEBUG - " + A + " || " );
            for( int B : parallelisms.get(A) ) System.out.print( B + ",");
            System.out.println();
        }
    }
}
