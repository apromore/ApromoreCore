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

package au.edu.qut.processmining.miners.splitminer.dfgp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.plugins.bpmn.plugins.BpmnExportPlugin;

import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;

/**
 * Created by Adriano on 24/10/2016.
 */
public class DirectlyFollowGraphPlus {

    private static boolean completeCloning = false;

    protected SimpleLog log;
    protected int startcode;
    protected int endcode;

    protected Set<DFGEdge> edges;
    protected Map<Integer, DFGNode> nodes;
    protected Map<Integer, HashSet<DFGEdge>> outgoings;
    protected Map<Integer, HashSet<DFGEdge>> incomings;
    protected Map<Integer, HashMap<Integer, DFGEdge>> dfgp;

    protected Set<Integer> loopsL1;
    protected Set<DFGEdge> loopsL2;
    protected Map<Integer, HashSet<Integer>> parallelisms;
    protected Set<DFGEdge> bestEdges;
    protected Set<DFGEdge> untouchableEdges;

    protected double percentileFrequencyThreshold;
    protected double parallelismsThreshold;
    protected DFGPUIResult.FilterType filterType;
    protected int filterThreshold;
//    private boolean percentileOnBest;
    protected boolean parallelismsFirst;


    protected DirectlyFollowGraphPlus() {}

    public DirectlyFollowGraphPlus(SimpleLog log) {
        this(log, DFGPUIResult.FREQUENCY_THRESHOLD, DFGPUIResult.PARALLELISMS_THRESHOLD, DFGPUIResult.STD_FILTER, DFGPUIResult.PARALLELISMS_FIRST);
    }

    public DirectlyFollowGraphPlus(SimpleLog log, double percentileFrequencyThreshold, double parallelismsThreshold, DFGPUIResult.FilterType filterType, boolean parallelismsFirst) {
        this.log = log;
        this.startcode = log.getStartcode();
        this.endcode = log.getEndcode();
        this.percentileFrequencyThreshold = percentileFrequencyThreshold;
        this.parallelismsThreshold = parallelismsThreshold;
        this.filterType = percentileFrequencyThreshold == 0 ? DFGPUIResult.FilterType.NOF : filterType;
//        this.percentileOnBest = percentileOnBest;
        this.parallelismsFirst = parallelismsFirst;
    }

    public DirectlyFollowGraphPlus(DirectlyFollowGraphPlus directlyFollowGraphPlus) {
        this.startcode = directlyFollowGraphPlus.startcode;
        this.endcode = directlyFollowGraphPlus.endcode;
        this.edges = new HashSet<>(directlyFollowGraphPlus.edges);
        this.nodes = directlyFollowGraphPlus.nodes;
        this.loopsL1 = directlyFollowGraphPlus.loopsL1;

        if(completeCloning) {
            this.log = directlyFollowGraphPlus.log;
            this.loopsL2 = directlyFollowGraphPlus.loopsL2;
            this.parallelisms = directlyFollowGraphPlus.parallelisms;
            this.bestEdges = directlyFollowGraphPlus.bestEdges;
            this.percentileFrequencyThreshold = directlyFollowGraphPlus.percentileFrequencyThreshold;
            this.parallelismsThreshold = directlyFollowGraphPlus.parallelismsThreshold;
            this.filterType = directlyFollowGraphPlus.filterType;
            this.filterThreshold = directlyFollowGraphPlus.filterThreshold;
            this.parallelismsFirst = directlyFollowGraphPlus.parallelismsFirst;
        }
    }

    public int size() { return nodes.size(); }
    public Set<DFGEdge> getEdges() { return edges; }
    public SimpleLog getSimpleLog() { return log; }
    public int getStartcode() { return startcode; }
    public int getEndcode() { return endcode; }
    public Set<Integer> getLoopsL1() { return loopsL1; }
    public Map<Integer, HashSet<Integer>> getParallelisms() { return parallelisms; }

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
        System.out.println("DFGP - settings > " + percentileFrequencyThreshold + " : " + parallelismsThreshold + " : " + filterType.toString());
        untouchableEdges = new HashSet<>();

        buildDirectlyFollowsGraph();                //first method to execute
        detectLoops();                              //depends on buildDirectlyFollowsGraph()
        detectParallelisms();                       //depends on detectLoops()

        switch(filterType) {                        //depends on detectParallelisms()
            case FWG:
                filterWithGuarantees();
                break;
            case WTH:
                filterWithThreshold();
                exploreAndRemove();
                break;
            case STD:
                standardFilter();
                exploreAndRemove();
                break;
            case NOF:
//                filterWithGuarantees();
//                exploreAndRemove();
                break;
        }

    }

    public void buildSafeDFGP() {
        System.out.println("DFGP - settings > " + percentileFrequencyThreshold + " : " + parallelismsThreshold + " : " + filterType.toString());

        buildDirectlyFollowsGraph();                //first method to execute
        bestEdgesOnMaxCapacitiesForConnectedness(); //this ensure a strongly connected graph (density may be impaired)
        detectLoops();                              //depends on buildDirectlyFollowsGraph()
        detectParallelisms();                       //depends on detectLoops()

        switch(filterType) {                        //depends on detectParallelisms()
            case FWG:
                filterWithGuarantees();
                break;
            case WTH:
                filterWithThreshold();
                exploreAndRemove();
                break;
            case STD:
                standardFilter();
                exploreAndRemove();
                break;
            case NOF:
//                filterWithGuarantees();
//                exploreAndRemove();
                break;
        }

    }

    public void buildDirectlyFollowsGraph() {
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

    private void detectParallelisms() {
//        int totalParallelisms = 0;
//        int confirmedParallelisms = 0;
//        int notParallel = 0;
        boolean priorityCheck;

        DFGEdge e2;
        int src;
        int tgt;

        int src2tgt_frequency;
        int tgt2src_frequency;
        double parallelismScore;

        HashSet<DFGEdge> removableEdges = new HashSet<>();

        parallelisms = new HashMap<>();

        if( parallelismsThreshold == 0 ) return;

        for (DFGEdge e1 : edges) {
            src = e1.getSourceCode();
            tgt = e1.getTargetCode();

            if( parallelismsFirst ) priorityCheck = !loopsL2.contains(e1);
            else priorityCheck = !loopsL2.contains(e1) && !loopsL1.contains(src) && !loopsL1.contains(tgt);

            if( dfgp.get(tgt).containsKey(src) && priorityCheck && !removableEdges.contains(e1)) {
//                this means: src || tgt is candidate parallelism
                    e2 = dfgp.get(tgt).get(src);

                    src2tgt_frequency = e1.getFrequency();
                    tgt2src_frequency = e2.getFrequency();
                    parallelismScore = (double) (src2tgt_frequency - tgt2src_frequency) / (src2tgt_frequency + tgt2src_frequency);

                    if (Math.abs(parallelismScore) < parallelismsThreshold) {
//                    if parallelismScore is less than the threshold epslon,
//                    we set src || tgt and vice-versa, and we remove e1 and e2
                        if (!parallelisms.containsKey(src)) parallelisms.put(src, new HashSet<Integer>());
                        parallelisms.get(src).add(tgt);
                        if (!parallelisms.containsKey(tgt)) parallelisms.put(tgt, new HashSet<Integer>());
                        parallelisms.get(tgt).add(src);
                        removableEdges.add(e1);
                        removableEdges.add(e2);
//                        totalParallelisms+=2;
                    } else {
//                    otherwise we remove the least frequent edge, e1 or e2
                        if (parallelismScore > 0) removableEdges.add(e2);
                        else removableEdges.add(e1);
//                        notParallel++;
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
//            else { confirmedParallelisms++; }
        }

//        System.out.println("DFGP - parallelisms found (total, confirmed): (" + totalParallelisms + " , " + confirmedParallelisms + ")");
    }

    private void standardFilter() {
        int src;
        int tgt;
        DFGEdge recoverableEdge;

        bestEdgesOnMaxFrequencies();
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

    private void bestEdgesOnMaxFrequencies() {
        bestEdges = new HashSet<>();

        for( int node : nodes.keySet() ) {
            if( node != endcode ) bestEdges.add(Collections.max(outgoings.get(node)));
            if( node != startcode ) bestEdges.add(Collections.max(incomings.get(node)));
        }
    }

    private void filterWithThreshold() {
        int src;
        int tgt;
        DFGEdge recoverableEdge;

        bestEdgesOnMaxFrequencies();
        computeFilterThreshold();

        ArrayList<DFGEdge> orderedMostFrequentEdges = new ArrayList<>(bestEdges);

        for( DFGEdge e : orderedMostFrequentEdges ) this.removeEdge(e, false);
        for( DFGEdge e : new HashSet<>(edges) ) {
            if( e.getFrequency() > filterThreshold) orderedMostFrequentEdges.add(e);
            this.removeEdge(e, false);
        }

        Collections.sort(orderedMostFrequentEdges);
        for( int i = (orderedMostFrequentEdges.size()-1); i >= 0; i-- ) {
            recoverableEdge = orderedMostFrequentEdges.get(i);
            if( recoverableEdge.getFrequency() > filterThreshold) this.addEdge(recoverableEdge);
            else {
                src = recoverableEdge.getSourceCode();
                tgt = recoverableEdge.getTargetCode();
                if( outgoings.get(src).isEmpty() || incomings.get(tgt).isEmpty() ) this.addEdge(recoverableEdge);
            }
        }
    }

    private void computeFilterThreshold() {
        ArrayList<DFGEdge> frequencyOrderedEdges = new ArrayList<>();
        int i;

        frequencyOrderedEdges.addAll(bestEdges);
//        if( percentileOnBest )
//        else frequencyOrderedEdges.addAll(edges);

        Collections.sort(frequencyOrderedEdges);
        i = (int)Math.round(frequencyOrderedEdges.size()*percentileFrequencyThreshold);
        if( i == frequencyOrderedEdges.size() ) i--;
        filterThreshold = frequencyOrderedEdges.get(i).getFrequency();
//        System.out.println("DEBUG - filter threshold: " + filterThreshold);
    }

    private void filterWithGuarantees() {
        bestEdgesOnMaxFrequencies();
        computeFilterThreshold();

        bestEdgesOnMaxCapacities();
        for( DFGEdge e : new HashSet<>(edges) )
            if( !bestEdges.contains(e) && !(e.getFrequency() >= filterThreshold) ) removeEdge(e, false);
    }

    private void bestEdgesOnMaxCapacities() {
        int src, tgt, cap, maxCap;
        DFGEdge bp, bs;

        LinkedList<Integer> toVisit = new LinkedList<>();
        Set<Integer> unvisited = new HashSet<>();

        HashMap<Integer, DFGEdge> bestPredecessorFromSource = new HashMap<>();
        HashMap<Integer, DFGEdge> bestSuccessorToSink = new HashMap<>();

        Map<Integer, Integer> maxCapacitiesFromSource = new HashMap<>();
        Map<Integer, Integer> maxCapacitiesToSink = new HashMap<>();

        for( int n : nodes.keySet() ) {
            maxCapacitiesFromSource.put(n, 0);
            maxCapacitiesToSink.put(n, 0);
        }

        maxCapacitiesFromSource.put(startcode, Integer.MAX_VALUE);
        maxCapacitiesToSink.put(endcode, Integer.MAX_VALUE);

//      forward exploration
        toVisit.add(startcode);
        unvisited.addAll(nodes.keySet());
        unvisited.remove(startcode);

        while( !toVisit.isEmpty() ) {
            src = toVisit.removeFirst();
            cap = maxCapacitiesFromSource.get(src);
            for( DFGEdge oe : outgoings.get(src) ) {
                tgt = oe.getTargetCode();
                maxCap = (cap > oe.getFrequency() ? oe.getFrequency() : cap);
                if( (maxCap > maxCapacitiesFromSource.get(tgt)) ) { //|| ((maxCap == maxCapacitiesFromSource.get(tgt)) && (bestPredecessorFromSource.get(tgt).getFrequency() < oe.getFrequency())) ) {
                    maxCapacitiesFromSource.put(tgt, maxCap);
                    bestPredecessorFromSource.put(tgt, oe);
                    if( !toVisit.contains(tgt) ) unvisited.add(tgt);
                }
                if( unvisited.contains(tgt) ) {
                    toVisit.addLast(tgt);
                    unvisited.remove(tgt);
                }
            }
        }


//      backward exploration
        toVisit.add(endcode);
        unvisited.clear();
        unvisited.addAll(nodes.keySet());
        unvisited.remove(endcode);

        while( !toVisit.isEmpty() ) {
            tgt = toVisit.removeFirst();
            cap = maxCapacitiesToSink.get(tgt);
            for( DFGEdge ie : incomings.get(tgt) ) {
                src = ie.getSourceCode();
                maxCap = (cap > ie.getFrequency() ? ie.getFrequency() : cap);
                if( (maxCap > maxCapacitiesToSink.get(src)) ) { //|| ((maxCap == maxCapacitiesToSink.get(src)) && (bestSuccessorToSink.get(src).getFrequency() < ie.getFrequency())) ) {
                    maxCapacitiesToSink.put(src, maxCap);
                    bestSuccessorToSink.put(src, ie);
                    if( !toVisit.contains(src) ) unvisited.add(src);
                }
                if( unvisited.contains(src) ) {
                    toVisit.addLast(src);
                    unvisited.remove(src);
                }
            }
        }

        bestEdges = new HashSet<>();
        for( int n : nodes.keySet() ) {
            bestEdges.add(bestPredecessorFromSource.get(n));
            bestEdges.add(bestSuccessorToSink.get(n));
        }
        bestEdges.remove(null);

//        for( int n : nodes.keySet() ) {
//            System.out.println("DEBUG - " + n + " : [" + maxCapacitiesFromSource.get(n) + "][" + maxCapacitiesToSink.get(n) + "]");
//        }
    }

    private void exploreAndRemove() {
        int src, tgt;

        LinkedList<Integer> toVisit = new LinkedList<>();
        Set<Integer> unvisited = new HashSet<>();

//      forward exploration
        toVisit.add(startcode);
        unvisited.addAll(nodes.keySet());
        unvisited.remove(startcode);

        while( !toVisit.isEmpty() ) {
            src = toVisit.removeFirst();
            for( DFGEdge oe : outgoings.get(src) ) {
                tgt = oe.getTargetCode();
                if( unvisited.contains(tgt) ) {
                    toVisit.addLast(tgt);
                    unvisited.remove(tgt);
                }
            }
        }

        for(int n : unvisited) {
            System.out.println("DEBUG - fwd removed: " + nodes.get(n).print());
            removeNode(n);
        }

//      backward exploration
        toVisit.add(endcode);
        unvisited.clear();
        unvisited.addAll(nodes.keySet());
        unvisited.remove(endcode);

        while( !toVisit.isEmpty() ) {
            tgt = toVisit.removeFirst();
            for( DFGEdge oe : incomings.get(tgt) ) {
                src = oe.getSourceCode();
                if( unvisited.contains(src) ) {
                    toVisit.addLast(src);
                    unvisited.remove(src);
                }
            }
        }

        for(int n : unvisited) {
            System.out.println("DEBUG - bkw removed: " + nodes.get(n).print());
            removeNode(n);
        }
    }


    /* data objects management */

    protected void addNode(DFGNode n) {
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

    protected void addEdge(DFGEdge e) {
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
        if( (incomings.get(tgt).size() == 1) || (outgoings.get(src).size() == 1) ) return false;
        if( safe && untouchableEdges.contains(e) ) {
            System.out.println("DEBUG - this edge ensures connectedness! not removable!");
            return false;
        }
        incomings.get(tgt).remove(e);
        outgoings.get(src).remove(e);
        dfgp.get(src).remove(tgt);
        edges.remove(e);
        return true;
//        System.out.println("DEBUG - removed edge: " + src + " -> " + tgt);
    }

    public int enhance( Set<String> subtraces ) {
        int enhancement = 0;
        StringTokenizer trace;

        DFGNode node, prevNode;
        DFGEdge edge;

        int event, prevEvent;

        for( String t : subtraces ) {
            System.out.println("INFO - (dfgp) subtrace : " + t);
            trace = new StringTokenizer(t, ":");

            prevEvent = Integer.valueOf(trace.nextToken());
            prevNode = nodes.get(prevEvent);

            while( trace.hasMoreTokens() ) {

                event = Integer.valueOf(trace.nextToken());
                node = nodes.get(event);

                if( !dfgp.containsKey(prevEvent) || !dfgp.get(prevEvent).containsKey(event) ) {
                    edge = new DFGEdge(prevNode, node);
                    this.addEdge(edge);
                    enhancement++;
                }

                prevEvent = event;
                prevNode = node;
            }
        }

        return enhancement;
    }

    public int reduce( Set<String> subtraces ) {
        int reduction = 0;
        StringTokenizer trace;
        int event, prevEvent;

        for( String t : subtraces ) {
            trace = new StringTokenizer(t, ":");
            prevEvent = Integer.valueOf(trace.nextToken());

            while( trace.hasMoreTokens() ) {
                event = Integer.valueOf(trace.nextToken());
                if( dfgp.containsKey(prevEvent) && dfgp.get(prevEvent).containsKey(event) ) {
                    if(this.removeEdge(dfgp.get(prevEvent).get(event), false)) reduction++;
                }
                prevEvent = event;
            }
        }

//        detectParallelisms();
        return reduction;
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

//    EXPERIMENTAL

// this method is exactly the same of bestEdgesOnMaxCapacities
    private void bestEdgesOnMaxCapacitiesForConnectedness() {
        int src, tgt, cap, maxCap;
        DFGEdge bp, bs;

        LinkedList<Integer> toVisit = new LinkedList<>();
        Set<Integer> unvisited = new HashSet<>();

        HashMap<Integer, DFGEdge> bestPredecessorFromSource = new HashMap<>();
        HashMap<Integer, DFGEdge> bestSuccessorToSink = new HashMap<>();

        Map<Integer, Integer> maxCapacitiesFromSource = new HashMap<>();
        Map<Integer, Integer> maxCapacitiesToSink = new HashMap<>();

        for( int n : nodes.keySet() ) {
            maxCapacitiesFromSource.put(n, 0);
            maxCapacitiesToSink.put(n, 0);
        }

        maxCapacitiesFromSource.put(startcode, Integer.MAX_VALUE);
        maxCapacitiesToSink.put(endcode, Integer.MAX_VALUE);

//      forward exploration
        toVisit.add(startcode);
        unvisited.addAll(nodes.keySet());
        unvisited.remove(startcode);

        while( !toVisit.isEmpty() ) {
            src = toVisit.removeFirst();
            cap = maxCapacitiesFromSource.get(src);
            for( DFGEdge oe : outgoings.get(src) ) {
                tgt = oe.getTargetCode();
                maxCap = (cap > oe.getFrequency() ? oe.getFrequency() : cap);
                if( (maxCap > maxCapacitiesFromSource.get(tgt)) ) { //|| ((maxCap == maxCapacitiesFromSource.get(tgt)) && (bestPredecessorFromSource.get(tgt).getFrequency() < oe.getFrequency())) ) {
                    maxCapacitiesFromSource.put(tgt, maxCap);
                    bestPredecessorFromSource.put(tgt, oe);
                    if( !toVisit.contains(tgt) ) unvisited.add(tgt);
                }
                if( unvisited.contains(tgt) ) {
                    toVisit.addLast(tgt);
                    unvisited.remove(tgt);
                }
            }
        }


//      backward exploration
        toVisit.add(endcode);
        unvisited.clear();
        unvisited.addAll(nodes.keySet());
        unvisited.remove(endcode);

        while( !toVisit.isEmpty() ) {
            tgt = toVisit.removeFirst();
            cap = maxCapacitiesToSink.get(tgt);
            for( DFGEdge ie : incomings.get(tgt) ) {
                src = ie.getSourceCode();
                maxCap = (cap > ie.getFrequency() ? ie.getFrequency() : cap);
                if( (maxCap > maxCapacitiesToSink.get(src)) ) { //|| ((maxCap == maxCapacitiesToSink.get(src)) && (bestSuccessorToSink.get(src).getFrequency() < ie.getFrequency())) ) {
                    maxCapacitiesToSink.put(src, maxCap);
                    bestSuccessorToSink.put(src, ie);
                    if( !toVisit.contains(src) ) unvisited.add(src);
                }
                if( unvisited.contains(src) ) {
                    toVisit.addLast(src);
                    unvisited.remove(src);
                }
            }
        }

        untouchableEdges = new HashSet<>();
        for( int n : nodes.keySet() ) {
            untouchableEdges.add(bestPredecessorFromSource.get(n));
            untouchableEdges.add(bestSuccessorToSink.get(n));
        }
        untouchableEdges.remove(null);

//        for( int n : nodes.keySet() ) {
//            System.out.println("DEBUG - " + n + " : [" + maxCapacitiesFromSource.get(n) + "][" + maxCapacitiesToSink.get(n) + "]");
//        }
    }

    private boolean isConnected() {
        int src, tgt;

        LinkedList<Integer> toVisit = new LinkedList<>();
        Set<Integer> unvisited = new HashSet<>();

//      forward exploration
        toVisit.add(startcode);
        unvisited.addAll(nodes.keySet());
        unvisited.remove(startcode);

        while( !toVisit.isEmpty() ) {
            src = toVisit.removeFirst();
            for( DFGEdge oe : outgoings.get(src) ) {
                tgt = oe.getTargetCode();
                if( unvisited.contains(tgt) ) {
                    toVisit.addLast(tgt);
                    unvisited.remove(tgt);
                }
            }
        }

        if(!unvisited.isEmpty()) return false;

//      backward exploration
        toVisit.add(endcode);
        unvisited.clear();
        unvisited.addAll(nodes.keySet());
        unvisited.remove(endcode);

        while( !toVisit.isEmpty() ) {
            tgt = toVisit.removeFirst();
            for( DFGEdge oe : incomings.get(tgt) ) {
                src = oe.getSourceCode();
                if( unvisited.contains(src) ) {
                    toVisit.addLast(src);
                    unvisited.remove(src);
                }
            }
        }

        if(!unvisited.isEmpty()) return false;

        return true;
    }
    
    // Bruce: for debug only
    private void writeDiagram(BPMNDiagram d, String filename) {
        try {
            UIContext context = new UIContext();
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIPluginContext uiPluginContext = context.getMainPluginContext();
            BpmnExportPlugin exportPlugin = new BpmnExportPlugin();
            exportPlugin.export(uiPluginContext, d, new File(filename));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    

}
