/*
 * Copyright © 2018-2019 The University of Melbourne.
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

package au.edu.unimelb.processmining.optimization;

import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.splitminer.dfgp.DFGEdge;
import au.edu.qut.processmining.miners.splitminer.dfgp.DirectlyFollowGraphPlus;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;

import java.util.*;

public class SimpleDirectlyFollowGraph extends DirectlyFollowGraphPlus {

    public enum PERTYPE {FIT, PREC};

    private static Random random = new Random(1);

    private SimpleLog slog;
    private int startcode;
    private int endcode;

    private Map<Integer, HashSet<Integer>> parallelisms;
    private Set<Integer> loopsL1;
    private Set<Integer> tabu;

    private BitSet dfg;
    private Integer[] outgoings;
    private Integer[] incomings;
    private int size;

    public SimpleDirectlyFollowGraph(SimpleDirectlyFollowGraph sdfg) {
        this.slog = sdfg.slog;
        this.startcode = sdfg.startcode;
        this.endcode = sdfg.endcode;
        this.dfg = (BitSet) sdfg.dfg.clone();
        this.parallelisms = sdfg.parallelisms;
        this.loopsL1 = sdfg.loopsL1;
        this.size = sdfg.size;
        this.outgoings = sdfg.outgoings.clone();
        this.incomings = sdfg.incomings.clone();
        this.tabu = new HashSet<>(sdfg.tabu);
    }

    public SimpleDirectlyFollowGraph(DirectlyFollowGraphPlus directlyFollowGraphPlus, boolean tabuSearch) {
        this.parallelisms = directlyFollowGraphPlus.getParallelisms();
        this.loopsL1 = directlyFollowGraphPlus.getLoopsL1();
        this.startcode = directlyFollowGraphPlus.getStartcode();
        this.endcode = directlyFollowGraphPlus.getEndcode();
        this.slog = directlyFollowGraphPlus.getSimpleLog();

        size = directlyFollowGraphPlus.size();
        tabu = new HashSet<>();

        outgoings = new Integer[size];
        incomings = new Integer[size];
        for(int i = 0; i<size; i++) outgoings[i] = incomings[i] = 0;

//        this bit array represents a graph (the directly-follows)
//        the cell (i,j) is set to TRUE if there exists an edge in the DFG with srcID = i and tgtID = j
//        reminder: matrix[i][j] = array[i*size + j];
//        i = 0 is the source of the graph (i.e. no incoming edges)
//        i = size-1 is the sink of the graph (i.e. no outgoing edges)
        dfg = new BitSet(size*size);

        int src, tgt;
        for( DFGEdge e : directlyFollowGraphPlus.getEdges() ) {
            src = e.getSourceCode();
            tgt = (e.getTargetCode() == endcode ? size-1 : e.getTargetCode());

//        reminder: matrix[i][j] = array[i*size + j];
            outgoings[src]++;
            incomings[tgt]++;

            dfg.set(src*size + tgt);
            if(tabuSearch) tabu.add(src*size + tgt);
        }
    }

    public Set<Integer> getTabuSet() { return this.tabu; }
    public void setTabuSet(Set<Integer> tabu) { this.tabu = new HashSet<>(tabu); }

    public void setParallelisms(Map<Integer, HashSet<Integer>> parallelisms) { this.parallelisms = parallelisms; }

    @Override
    public SimpleLog getSimpleLog(){ return slog; }

    @Override
    public boolean areConcurrent(int A, int B) {
        return (parallelisms.containsKey(A) && parallelisms.get(A).contains(B));
    }

    @Override
    public int enhance( Set<String> subtraces ) {
        int enhancement = 0;
        StringTokenizer trace;
        int src, tgt;

        for( String t : subtraces ) {
            trace = new StringTokenizer(t, ":");
            src = Integer.valueOf(trace.nextToken());

            while( trace.hasMoreTokens() ) {
                tgt = Integer.valueOf(trace.nextToken());
                if( tgt == endcode ) tgt = size -1;
                if( !dfg.get(src*size + tgt) ) {
                    dfg.set(src*size + tgt);
                    outgoings[src]++;
                    incomings[tgt]++;
                    enhancement++;
                }
                src = tgt;
            }
        }
        return enhancement;
    }

    @Override
    public int reduce( Set<String> subtraces ) {
        int reduction = 0;
        StringTokenizer trace;
        int src, tgt;

        for( String t : subtraces ) {
            trace = new StringTokenizer(t, ":");
            src = Integer.valueOf(trace.nextToken());

            while( trace.hasMoreTokens() ) {
                tgt = Integer.valueOf(trace.nextToken());
                if( tgt == endcode ) tgt = size -1;
                if( dfg.get(src*size + tgt) && checkAndRemove(src*size + tgt) ) {
//                dfg.clear(src*size + tgt);
                    outgoings[src]--;
                    incomings[tgt]--;
                    reduction++;
                }
                src = tgt;
            }
        }
        return reduction;
    }

    public String enhance( String subtrace, int strength ) {
        String leftover;
        int enhancement = 0;
        int src, tgt;

        StringTokenizer trace = new StringTokenizer(subtrace, ":");
        src = Integer.valueOf(trace.nextToken());

        while( trace.hasMoreTokens() && enhancement != strength) {
            tgt = Integer.valueOf(trace.nextToken());
            if( tgt == endcode ) tgt = size -1;
            if( !dfg.get(src*size + tgt) && isAddable(src, tgt) ) {
                dfg.set(src*size + tgt);
                outgoings[src]++;
                incomings[tgt]++;
                enhancement++;
            }
            src = tgt;
        }

        if( !trace.hasMoreTokens() ) {
            if( enhancement == strength ) return new String();
            else return null;
        }

        leftover =  ":" + src + ":";
        while( trace.hasMoreTokens() ) leftover = leftover + trace.nextToken() + ":";

        return leftover;
    }

    public String reduce( String subtrace, int strength ) {
        String leftover;
        int reduction = 0;
        int src, tgt;

        StringTokenizer trace = new StringTokenizer(subtrace, ":");
        src = Integer.valueOf(trace.nextToken());

        while( trace.hasMoreTokens() && reduction != strength ) {
            tgt = Integer.valueOf(trace.nextToken());
            if( tgt == endcode ) tgt = size -1;
            if( dfg.get(src*size + tgt) && checkAndRemove(src*size + tgt) ) {
//                dfg.clear(src*size + tgt);
                outgoings[src]--;
                incomings[tgt]--;
                reduction++;
            }
            src = tgt;
        }


        if( !trace.hasMoreTokens() ) {
            if( reduction == strength ) return new String();
            else return null;
        }

        leftover =  ":" + src + ":";
        while( trace.hasMoreTokens() ) leftover = leftover + trace.nextToken() + ":";

        return leftover;
    }

    public void perturb(int strength, PERTYPE pertype) {
        int bound = size*size;
        int next;
        int src, tgt;
        int perturbations;
        int attempts;
        int maxa = strength * 4;


        System.out.println("DEBUG - perturbing.");

        attempts = 0;
        perturbations = strength;

        switch (pertype) {
            case FIT:
//            this type of perturbation will add edges, aiming to increase fitness
                while( perturbations != 0 && attempts++ != maxa) {
                    next = random.nextInt(bound);
                    if( !dfg.get(next) && isAddable(src = next/size, tgt = next%size) ) {
                        dfg.set(next);
                        outgoings[src]++;
                        incomings[tgt]++;
                        perturbations--;
                    }
                }
                break;
            case PREC:
//            this type of perturbation will remove edges, aiming to increase precision
                while( perturbations != 0 && attempts++ != maxa) {
                    next = random.nextInt(bound);
                    if( dfg.get(next) && checkAndRemove(next) ) {
//                        dfg.clear(next); this is done within checkAndRemove()
                        outgoings[next/size]--;
                        incomings[next%size]--;
                        perturbations--;
                    }
                }
                break;
        }
        System.out.println("DEBUG - attempts(" + pertype.toString() + ") & perturbations(left): " + attempts + " & " + perturbations);
    }

    private boolean isAddable(int src, int tgt) {
        if( areConcurrent(src,tgt) ) {
            parallelisms.get(src).remove(tgt);
            parallelisms.get(tgt).remove(src);
            System.out.println("WATCHOUT - removed parallelism");
            return true;
        }
        return (src != tgt) && (src != (size-1)) && (tgt != 0);// && !areConcurrent(src, tgt) ;
    }


    private boolean isRemovable(int src, int tgt) {
        return outgoings[src] > 1 && incomings[tgt] > 1 && !tabu.contains(src*size+tgt) ;
    }

    private boolean checkAndRemove(int edge) {
        int src = edge/size;
        int tgt = edge%size;
        int end;

        if( outgoings[src] == 1 || incomings[tgt] == 1 || tabu.contains(edge) ) return false;
        dfg.clear(edge);

        ArrayList<Integer> toVisit = new ArrayList<>();
        Set<Integer> unvisited = new HashSet<>();

//      forward exploration
        toVisit.add(0);
        for(int n = 1; n<size; n++) unvisited.add(n);

        while( !toVisit.isEmpty() ) {
            src = toVisit.remove(0);
            for( tgt = 1; tgt < size; tgt++ )
                if( dfg.get(src*size+tgt) && unvisited.contains(tgt) ) {
                    toVisit.add(tgt);
                    unvisited.remove(tgt);
                }
        }

        if( !unvisited.isEmpty() ) {
            dfg.set(edge);
            return false;
        }

//      backward exploration
        end = size-1;
        toVisit.add(end);
        for(int n = 0; n < end; n++) unvisited.add(n);

        while( !toVisit.isEmpty() ) {
            tgt = toVisit.remove(0);
            for( src = 0; src < end; src++ )
                if( dfg.get(src*size+tgt) && unvisited.contains(src) ) {
                    toVisit.add(src);
                    unvisited.remove(src);
                }
        }

        if( !unvisited.isEmpty() ) {
            dfg.set(edge);
            return false;
        }

        return true;
    }

    @Override
    public BPMNDiagram convertIntoBPMNDiagram() {
        BPMNDiagram diagram = new BPMNDiagramImpl("eSDFG-diagram");
        HashMap<Integer, BPMNNode> mapping = new HashMap<>();
        BPMNNode node;
        BPMNNode src, tgt;

        node = diagram.addEvent("0", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, true, null);
        mapping.put(0, node);

        for( int taskID = 1; taskID < (size-1); taskID++ ) {
            node = diagram.addActivity( Integer.toString(taskID), loopsL1.contains(taskID), false, false, false, false);
            mapping.put(taskID, node);
        }

        node = diagram.addEvent("-1", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, true, null);
        mapping.put(size-1, node);

        for( int srcID = 0; srcID < size; srcID++ ) {
            src = mapping.get(srcID);
            for( int tgtID = 0; tgtID < size; tgtID++ ) {
                tgt = mapping.get(tgtID);
                if( dfg.get(srcID*size + tgtID) ) diagram.addFlow(src, tgt, "");
            }
        }
//        System.out.println("INFO - returning a BPMN diagram from a bis-set.");
        return diagram;
    }

    @Override
    public int hashCode() {
        return dfg.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof SimpleDirectlyFollowGraph)
            return dfg.equals(((SimpleDirectlyFollowGraph) o).dfg);
        else return false;
    }

}
