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

package au.edu.qut.bpmn.structuring.graph;

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Adriano on 28/02/2016.
 */
public class Graph {
//    private static final Logger LOGGER = LoggerFactory.getLogger(Graph.class);

    private static final int PID_RANGE = 10000;

    private String entry;
    private String exit;

    private Map<String, Gateway.GatewayType> gateways;

    private Map<Integer, Path> allPaths;        //path history
    private Set<Integer> alivePaths;            //current alive and valid paths of the model

    private Map<String, List<Integer>> incoming; //incoming edges to a gateways
    private Map<String, List<Integer>> outgoing; //outgoing edges from a gateways

    private Map<String, Map<String, List<Integer>>> brothers;    //brothers matrix

    private Set<Graph> subgraphs;

    private int PID;

    private int move;

    private boolean jsWarning;
    private boolean valid;
    private boolean keepBisimulation;

    final static boolean jsLimitation = true;


    public Graph(boolean keepBisimulation, Map<String, Gateway.GatewayType> gateways) {
        this.gateways = gateways;

        allPaths = new HashMap<>();
        alivePaths = new HashSet<>();

        incoming = new HashMap<>();
        outgoing = new HashMap<>();

        brothers = new HashMap<>();
        move = 0;
        valid = true;
        jsWarning = false;

        this.keepBisimulation = keepBisimulation;
    }

    public Graph(String entry, String exit, boolean keepBisimulation, Map<String, Gateway.GatewayType> gateways) {
        this.gateways = gateways;

        allPaths = new HashMap<>();
        alivePaths = new HashSet<>();

        incoming = new HashMap<>();
        outgoing = new HashMap<>();

        brothers = new HashMap<>();

        this.entry = new String(entry);
        this.exit = new String(exit);

        /* this is for sake of completeness */
        incoming.put(this.entry, new ArrayList<Integer>()); //this set will remain always empty, no incoming path to the main entry
        outgoing.put(this.exit, new ArrayList<Integer>()); //this set will remain always empty, no outgoing path from the main exit

        move = 0;
        valid = true;
        jsWarning = false;

        this.keepBisimulation = keepBisimulation;
    }

    public Graph(Graph mould) {
        this.entry = new String(mould.getEntry());
        this.exit = new String(mould.getExit());

        allPaths = new HashMap<>();
        for( int pid : mould.allPaths.keySet() ) allPaths.put(pid, new Path(pid, mould.allPaths.get(pid)));

        alivePaths = new HashSet<>();
        alivePaths.addAll(mould.alivePaths);

        incoming = new HashMap<>();
        for( String gate : mould.incoming.keySet() ) {
            this.incoming.put(gate, new ArrayList<Integer>());
            for( int pid : mould.incoming.get(gate) ) this.incoming.get(gate).add(pid);
        }

        outgoing = new HashMap<>();
        for( String gate : mould.outgoing.keySet() ) {
            this.outgoing.put(gate, new ArrayList<Integer>());
            for( int pid : mould.outgoing.get(gate) ) this.outgoing.get(gate).add(pid);
        }

        brothers = new HashMap<>();
        for( String entry : mould.brothers.keySet() ) {
            brothers.put(entry, new HashMap<String, List<Integer>>());
            for( String exit : mould.brothers.get(entry).keySet() )
                brothers.get(entry).put(exit, new ArrayList<Integer>(mould.brothers.get(entry).get(exit)));
        }

        jsWarning = mould.jsWarning;
        valid = mould.valid;
        move = mould.move;
        PID = mould.PID;

        this.gateways = mould.gateways;
        this.keepBisimulation = mould.keepBisimulation;
    }

    public void detectLoops() {
        int l = 0;
        HashSet<String> unvisited = new HashSet<>(outgoing.keySet());
        HashSet<String> visiting = new HashSet<>();
        HashMap<String, Boolean> visitedGates = new HashMap<>();
        HashSet<Integer> visitedEdges = new HashSet<>();

        HashSet<Integer> loopEdges = new HashSet<>();
        HashSet<Integer> forwardEdges = new HashSet<>();

        //System.out.println("DEBUG - outgoing size: " + unvisited.size() );

        exploreLoops(this.entry, unvisited, visiting, visitedGates, visitedEdges, loopEdges, forwardEdges);

        //System.out.println("DEBUG - forwardEdges size: " + forwardEdges.size() );
        //System.out.println("DEBUG - loops size: " + loopEdges.size() );
        for( int pid : loopEdges )
            if( !forwardEdges.contains(pid) ) {
                allPaths.get(pid).setLoop();
                l++;
            }

        System.out.println("DEBUG - detected loops: " + l);
    }

    private boolean exploreLoops(String entry, HashSet<String> unvisited, HashSet<String> visiting,
                                 HashMap<String, Boolean> visitedGates, HashSet<Integer> visitedEdges,
                                 HashSet<Integer> loopEdges, HashSet<Integer> forwardEdges )
    {
        String next;
        boolean loopEdge = false;
        boolean forwardEdge = false;
        boolean visited = true;

        unvisited.remove(entry);
        visiting.add(entry);

        if( entry == exit ) forwardEdge = true;

        for( int pid : outgoing.get(entry) ) {
            next = allPaths.get(pid).getExit();
            visitedEdges.add(pid);
            if( unvisited.contains(next) ) {
                if( exploreLoops(next, unvisited, visiting, visitedGates, visitedEdges, loopEdges, forwardEdges) ) {
                    loopEdge = true;
                    loopEdges.add(pid);
                } else {
                    forwardEdge = true;
                    forwardEdges.add(pid);
                }
            } else if( visiting.contains(next) ) {
                loopEdge = true;
                loopEdges.add(pid);
            } else if( visitedGates.containsKey(next) ) {
                if( visitedGates.get(next) ) {
                    loopEdge = true;
                    loopEdges.add(pid);
                } else {
                    forwardEdge = true;
                    forwardEdges.add(pid);
                }
            }
        }

        visiting.remove(entry);
        for( int pid : incoming.get(entry) ) if( !visitedEdges.contains(pid) ) visited = false;
        if( visited ) visitedGates.put(entry, (loopEdge && !forwardEdge));
        else unvisited.add(entry);

        return (loopEdge && !forwardEdge);
    }


    public void simplify() {
        //System.out.println("DEBUG - paths before helper: " + alivePaths.size());

        seekBrothers();
        concatenation();

        while(true) { if( seekBrothers() && concatenation() ) break; }

        //System.out.println("DEBUG - paths after helper: " + alivePaths.size());
    }


    private boolean seekBrothers() {
        List<Integer> brotherhood;
        List<Integer> twinBrotherhood;
        Path bigBrother;
        Path twinBrother;
        int size;
        boolean ntd = true;
        //System.out.println("DEBUG - alive paths before merging brothers: " + alivePaths.size() );

        //looking up brothers. it is a matrix entry-exit with a set of int in each cell.
        //if the set size is greater than 1 means all the int inside the set are brother paths.
        for( String entry : brothers.keySet() )
            for( String exit : brothers.get(entry).keySet() )
                if( (size = (brotherhood = brothers.get(entry).get(exit)).size()) > 1 ) {
                    //System.out.println("DEBUG - found " + brotherhood.size() + " brothers (entry: " + entry + ") (exit: " + exit + ")");
                    ntd = false;
                    bigBrother = allPaths.get(brotherhood.get(size-1));
                    while( brotherhood.size() != 1 ) {
                        bigBrother.addBrother(allPaths.get(brotherhood.get(0)));
                        //System.out.println("DEBUG - merged brothers [" + bigBrother.getPID() + " + " + brotherhood.get(0) + "] with loop [" + bigBrother.isLoop() + ":" + allPaths.get(brotherhood.get(0)).isLoop() + "]");
                        removePath(brotherhood.get(0));
                    }
                }

        //System.out.println("DEBUG - alive paths after merging brothers: " + alivePaths.size() );

        //at this point there are no brothers except for cycles.
        for( String entry : brothers.keySet() )
            for( String exit : brothers.get(entry).keySet() ) {
                brotherhood = brothers.get(entry).get(exit); //this can only be empty or with 1 element.

                if( brothers.containsKey(exit) && brothers.get(exit).containsKey(entry) ) {
                    twinBrotherhood = brothers.get(exit).get(entry); //this can only be empty or with 1 element.
                    if( (brotherhood.size() == 1) && (twinBrotherhood.size() == 1) ) {
                        //System.out.println("DEBUG - found reverse brothers (entry: " + entry + ") (exit: " + exit + ")");
                        bigBrother = allPaths.get(brotherhood.get(0));
                        twinBrother = allPaths.get(twinBrotherhood.get(0));

                        if( bigBrother.isLoop() && twinBrother.isLoop() ) {
//                            System.out.println("WARNING - got double loop, merging is dangerous.");
                            if( outgoing.get(entry).size() == 1 ) {
                                bigBrother.addReverseBrother(twinBrother);
                                removePath(twinBrotherhood.get(0));
                            } else {
                                twinBrother.addReverseBrother(bigBrother);
                                removePath(brotherhood.get(0));
                            }
                        } else if( bigBrother.isLoop() ) {
                            twinBrother.addReverseBrother(bigBrother);
                            removePath(brotherhood.get(0));
                        } else if( twinBrother.isLoop() ) {
                            bigBrother.addReverseBrother(twinBrother);
                            removePath(twinBrotherhood.get(0));
                        } else System.out.println("ERROR - impossible merge loop brothers.");
                    }
                }
            }

        //System.out.println("DEBUG - alive paths after merging reverse brothers: " + alivePaths.size() );
        return ntd;
    }

    private boolean concatenation() {
        boolean ntd = true;
        //System.out.println("DEBUG - alive paths before concatenation: " + alivePaths.size() );

        for( String o : incoming.keySet() )
            if( o.equals(entry) || o.equals(exit) ) continue;
            else if( (incoming.get(o).size() == 1) && ((outgoing.get(o)).size() == 1) ) {
                concatenate(incoming.get(o).get(0), outgoing.get(o).get(0));
                ntd = false;
            }

        //System.out.println("DEBUG - alive paths after concatenation: " + alivePaths.size() );
        return ntd;
    }

    private void concatenate(int firstPID, int secondPID) {
        Path first = allPaths.get(firstPID);
        Path second = allPaths.get(secondPID);

        //System.out.println("DEBUG - contatenating: " + firstPID + "("+ first.canConcat()+")" + " -> " + secondPID);

        if( first.canConcat() ) {
            first.concat(second);
            updatePath(firstPID, first.getEntry(), first.getEntry(), first.getExit(), second.getEntry());
            removePath(secondPID);
            if( second.isLoop() ) first.setLoop();
        } else {
            PID++;
            addPath(new Path(PID, first, second));
            removePath(firstPID);
            removePath(secondPID);
        }
    }

    public Graph minimalDecomposition() {
        subgraphs = new HashSet<>();
        HashSet<String> gates = new HashSet<>(outgoing.keySet());
        gates.remove(entry);
        gates.remove(exit);

        return detachGraph(allPaths.get(outgoing.get(entry).get(0)).getExit(), allPaths.get(incoming.get(exit).get(0)).getEntry(), gates);
    }

    public Set<Graph> decompose() {
        subgraphs = new HashSet<>();

        IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
        HashMap<String, Vertex> mapping = new HashMap<>();
        Vertex src;
        Vertex tgt;
        HashSet<String> gates;

        String entry;
        String exit;

        RPST rpst;
        RPSTNode root;
        LinkedList<RPSTNode> toVisit = new LinkedList<>();

        for( int i : alivePaths ) {
            entry = allPaths.get(i).getEntry();
            exit = allPaths.get(i).getExit();

            if( !mapping.containsKey(entry) ) {
                src = new Vertex(entry);
                mapping.put(entry, src);
            } else src = mapping.get(entry);

            if( !mapping.containsKey(exit) ) {
                tgt = new Vertex(exit);
                mapping.put(exit, tgt);
            } else tgt = mapping.get(exit);

            graph.addEdge(src, tgt);
        }

        rpst = new RPST(graph);
        root = rpst.getRoot();
        toVisit.add(root);

        while( toVisit.size() != 0 ) {

            root = toVisit.removeFirst();

            for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
                switch(n.getType()) {
                    case R:
                        //System.out.println("DEBUG - found RIGID element (children: " + rpst.getChildren(root).size() + " )");
                        //toVisit.add(n);

                        gates = new HashSet<>();
                        for( IVertex v : new HashSet<IVertex>(n.getFragment().getVertices()) ) gates.add(v.getName());

                        subgraphs.add(detachGraph(n.getEntry().getName(), n.getExit().getName(), gates));
                        break;

                    case T:
                        //System.out.println("DEBUG - found TRIVIAL element.");
                        break;

                    case P:
                        //System.out.println("DEBUG - found POLYGON element.");
                        toVisit.add(n);
                        break;

                    case B:
                        //System.out.println("DEBUG - found BOND element.");
                        toVisit.add(n);
                        break;

                    default:
                        System.out.println("ERROR - found WEIRD element.");
                }
            }
        }

        //System.out.println("DEBUG - alive paths after decomposition: " + alivePaths.size());
        return subgraphs;
    }

    private Graph detachGraph(String entry, String exit, Set<String> gates) {
        Graph graph = new Graph(entry, exit, keepBisimulation, gateways);
        HashSet<Integer> edges;
        Path path;

        gates.add(entry);
        gates.add(exit);

        if( gates.contains(this.entry) ) System.out.println("DEBUG - detaching also main entry.");
        if( gates.contains(this.exit) ) System.out.println("DEBUG - detaching also main exit.");
        //System.out.println("DEBUG - detaching graph (entry: " + entry + ") (exit: " + exit + ")");

        edges = new HashSet<>();

        for( String gate : gates ) {
            edges.addAll(outgoing.get(gate));
            edges.addAll(incoming.get(gate));
        }

        for( int e : edges ) {
            path = allPaths.get(e);
            if( gates.contains(path.getEntry()) && gates.contains(path.getExit()) ) {
                erasePath(e);
                graph.addPath(new Path(e, path));
            } else System.out.println("WARNING - detected path inbetween the rigid.");
        }

        //System.out.println("DEBUG - graph detached [incoming(entry): " + incoming.get(entry).size() + "][outgoing(entry): " + outgoing.get(entry).size() + "]");
        //System.out.println("DEBUG - graph detached [incoming(exit): " + incoming.get(exit).size() + "][outgoing(exit): " + outgoing.get(exit).size() + "]");

        graph.setPID(PID+((subgraphs.size()+1)*PID_RANGE));
        return graph;
    }

    public boolean recompose(Set<Graph> graphs) {
        String entry;
        String exit;

        for( Graph g : graphs ) {
            entry = g.getEntry();
            exit = g.getExit();

            if( g.getAlivePaths().size() != 1 ) System.out.println("WARNING - maximum structured graph with: " + g.getAlivePaths().size() + " paths.");

            if( !(outgoing.get(entry).isEmpty() && incoming.get(exit).isEmpty()) ) System.out.println("WARNING - something wrong with entry and exit of the rigid.");
/*
            for( Path p : g.getPaths().values() ) {
                this.addPath(p);
                if( !g.isPathAlive(p.getPID()) ) this.removePath(p.getPID());
            }
*/
            for( int pid : g.allPaths.keySet() ) allPaths.put(pid, new Path(pid, g.allPaths.get(pid)));
            alivePaths.addAll(g.alivePaths);

            for( String gate : g.incoming.keySet() ) {
                if( !incoming.containsKey(gate)) incoming.put(gate, new ArrayList<Integer>());
                for( int pid : g.incoming.get(gate) ) incoming.get(gate).add(pid);
            }

            for( String gate : g.outgoing.keySet() ) {
                if( !outgoing.containsKey(gate)) outgoing.put(gate, new ArrayList<Integer>());
                for( int pid : g.outgoing.get(gate) ) outgoing.get(gate).add(pid);
            }

            for( String gEntry : g.brothers.keySet() ) {
                if( !brothers.containsKey(gEntry) ) brothers.put(gEntry, new HashMap<String, List<Integer>>());
                for( String gExit : g.brothers.get(gEntry).keySet() ) {
                    if( !brothers.get(gEntry).containsKey(gExit) ) brothers.get(gEntry).put(gExit, new ArrayList<Integer>(g.brothers.get(gEntry).get(gExit)));
                    else brothers.get(gEntry).get(gExit).addAll(g.brothers.get(gEntry).get(gExit));
                }
            }
        }

        simplify();
        return true;
    }

    public boolean isMoveValid(int toExtend, int extension, Move.MoveType type) {
        Path extensionPath = allPaths.get(extension);
        Path injection = allPaths.get(toExtend);
        String exitGate = extensionPath.getExit();
        String entryGate = extensionPath.getEntry();


        switch(type) {
            case PUSHDOWN:
                if( !keepBisimulation ) return false;
                if( jsLimitation ) {
                    if( exitGate.equals(entry) && (outgoing.get(entry).size() != 1) ) {
                        //System.out.println("WARNING - found invalid move [PUSHDOWN: join/split on entry]!");
                        return false;
                    }

                    if( exitGate.equals(exit) && outgoing.get(exit).size() != 0 ) {
                        //System.out.println("WARNING - found invalid move [PUSHDOWN: join/split on exit]!");
                        return false;
                    }

                    if( outgoing.get(exitGate).size() > 1 ) {
                        if( injection.isLoop() ) return false;
                        for( int pid : incoming.get(exitGate) ) if( allPaths.get(pid).isLoop() ) {
                            //System.out.println("WARNING - found invalid move [PUSHDOWN: join/split]");
                            return false;
                        }
                        for( int pid : outgoing.get(exitGate) ) if( allPaths.get(pid).isLoop() ) {
                            //System.out.println("WARNING - found invalid move [PUSHDOWN: join/split]");
                            return false;
                        }
                    }
                }
                break;

            case PULLUP:
                if( keepBisimulation ) return false;
                if( jsLimitation ) {

                    if( entryGate.equals(exit) && (incoming.get(exit).size() != 1) ) {
                        //System.out.println("WARNING - found invalid move [PULLUP: join/split on exit]!");
                        return false;
                    }
                    if( entryGate.equals(entry) && (incoming.get(entry).size() != 0) ) {
                        //System.out.println("WARNING - found invalid move [PULLUP: join/split on entry]!");
                        return false;
                    }

                    if( incoming.get(exitGate).size() > 1 ) {
                        if( injection.isLoop() ) return false;
                        for( int pid : incoming.get(exitGate) ) if( allPaths.get(pid).isLoop() ) {
                            //System.out.println("WARNING - found invalid move [PULLUP: join/split]");
                            return false;
                        }
                        for( int pid : outgoing.get(exitGate) ) if( allPaths.get(pid).isLoop() ) {
                            //System.out.println("WARNING - found invalid move [PULLUP: join/split]");
                            return false;
                        }
                    }
                }
                break;

            default:
            System.out.println("ERROR - wrong move to validate.");
            return false;
        }

        return true;
    }

    public boolean applyMove(Move move, String middleGate) {
        int extension = move.getExtension();
        int toExtend = move.getToExtend();
        Path duplicate;
        Path mould;

        String entryGate, exitGate;

        boolean done = false;

        try {
            if( (gateways.get(middleGate) == Gateway.GatewayType.PARALLEL) )
                 switch( move.getType() ) {
                     case PUSHDOWN:
                        entryGate = allPaths.get(toExtend).getEntry();
                        exitGate = allPaths.get(extension).getExit();

                        if ((gateways.get(entryGate) == Gateway.GatewayType.PARALLEL) && (gateways.get(exitGate) == Gateway.GatewayType.PARALLEL)) {
                            updatePath(toExtend, entryGate, entryGate, exitGate, middleGate);
                            allPaths.get(toExtend).setExit(exitGate);
                            //System.out.println("DEBUG - eterogenous trick done!");
                            done = true;
                        }
                     break;

                     case PULLUP:
                         entryGate = allPaths.get(extension).getExit();
                         exitGate = allPaths.get(toExtend).getExit();

                         if( (gateways.get(entryGate) == Gateway.GatewayType.PARALLEL) ) {
                             updatePath(toExtend, entryGate, middleGate, exitGate, exitGate);
                             allPaths.get(toExtend).setEntry(entryGate);
                             //System.out.println("DEBUG - eterogenous trick done!");
                             done = true;
                         }
                     break;
                }
        } catch (NullPointerException npe) {
            System.out.println("ERROR - found unexisting gateway.");
        }

        try {
            if ((gateways.get(middleGate) == Gateway.GatewayType.PARALLEL) && (move.getType() == Move.MoveType.PULLUP)) {

            }
        } catch (NullPointerException npe) {
            System.out.println("ERROR - found unexisting gateway.");
        }

        if( !done ) {
            mould = allPaths.get(extension);

            PID++;
            duplicate = new Path(PID, mould);
            //System.out.println("DEBUG - duplicated: " + mould.getPID());
            addPath(duplicate);

            switch (move.getType()) {
                case PULLUP:
                    concatenate(PID, toExtend);
                    break;

                case PUSHDOWN:
                    concatenate(toExtend, PID);
                    break;

                default:
                    System.out.println("ERROR - wrong move to apply.");
                    return false;
            }
        }

        try {
            if( !jsWarning ) {
                for (String g : outgoing.keySet())
                    if ((outgoing.get(g).size() > 1) && (incoming.get(g).size() > 1)) {
                        for (int pid : incoming.get(g))
                            if (allPaths.get(pid).isLoop()) {
                                jsWarning = true;
                                if( jsLimitation ) return false;
                            }
                        for (int pid : outgoing.get(g))
                            if (allPaths.get(pid).isLoop()) {
                                jsWarning = true;
                                if( jsLimitation ) return false;
                            }
                    }
            }
        } catch (NullPointerException npe) {
            System.out.println("WARNING - detected a gateway without outgoing or incoming paths.");
            return false;
        }

//        simplify();
        this.move++;
        return true;
    }

    public void enablePullUp() {keepBisimulation = false;}

    public void setPID(int pid) { this.PID = pid; }

    public void setEntry(String entry) {
        this.entry = entry;
        incoming.put(this.entry, new ArrayList<Integer>());
    }

    public void setExit(String exit) {
        this.exit = exit;
        outgoing.put(this.exit, new ArrayList<Integer>());
    }

    public int getMove(){ return move; }

    public String getEntry() { return entry; }
    public String getExit() { return exit; }

    public List<Integer> getIncoming(String gate) { return incoming.get(gate); }
    public List<Integer> getOutgoing(String gate) { return outgoing.get(gate); }

    public Set<Integer> getAlivePaths(){ return alivePaths; }
    public Map<Integer, Path> getPaths(){ return allPaths; }

    public Path getPath(int pid) { return allPaths.get(pid); }
    public boolean isPathAlive(int pid) { return alivePaths.contains(pid); }
    public boolean isValid() { return valid; }

    public boolean containsJoinSplit() { return jsWarning; }

    public void addPath(Path path) {
        int pid = path.getPID();
        String entry = path.getEntry();
        String exit = path.getExit();

        //System.out.println("DEBUG - adding path: " + pid);

        /* populating data structs for the algorithm */
        allPaths.put(pid, path);
        alivePaths.add(pid);

        if( !outgoing.containsKey(entry) ) outgoing.put(entry, new ArrayList<Integer>());
        outgoing.get(entry).add(pid);

        if( !incoming.containsKey(exit) ) incoming.put(exit, new ArrayList<Integer>());
        incoming.get(exit).add(pid);

        if( !brothers.containsKey(entry) ) brothers.put(entry, new HashMap<String, List<Integer>>());
        if( !brothers.get(entry).containsKey(exit) ) brothers.get(entry).put(exit, new ArrayList<Integer>());
        brothers.get(entry).get(exit).add(0, pid);
    }

    public void updatePath(int pid, String newEntry, String oldEntry, String newExit, String oldExit) {

        //System.out.println("DEBUG - updating path: " + pid);

        if( newEntry != oldEntry ) {
            outgoing.get(oldEntry).remove((Integer) pid);
            outgoing.get(newEntry).add(pid);
        }

        if( newExit != oldExit ) {
            incoming.get(oldExit).remove((Integer) pid);
            incoming.get(newExit).add(pid);
        }

        brothers.get(oldEntry).get(oldExit).remove((Integer) pid);

        if( !brothers.containsKey(newEntry) ) brothers.put(newEntry, new HashMap<String, List<Integer>>());
        if( !brothers.get(newEntry).containsKey(newExit) ) brothers.get(newEntry).put(newExit, new ArrayList<Integer>());
        brothers.get(newEntry).get(newExit).add(0, pid);
    }

    public void removePath(int pid) {
        String entry;
        String exit;

        //System.out.println("DEBUG - removing path: " + pid);

        if( !allPaths.containsKey(pid) ) {
            System.out.println("ERROR - trying to remove a non existing path: " + pid);
            return;
        }

        entry = allPaths.get(pid).getEntry();
        exit = allPaths.get(pid).getExit();

        alivePaths.remove(pid);

        outgoing.get(entry).remove((Integer) pid);
        incoming.get(exit).remove((Integer) pid);

        brothers.get(entry).get(exit).remove((Integer) pid);
    }

    private void erasePath(int pid) {
        removePath(pid);
        allPaths.remove(pid);
    }


}
