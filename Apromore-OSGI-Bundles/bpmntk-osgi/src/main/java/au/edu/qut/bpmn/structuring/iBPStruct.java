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

package au.edu.qut.bpmn.structuring;

import au.edu.qut.bpmn.helper.DiagramHandler;
import au.edu.qut.bpmn.structuring.core.StructuringCore;
import au.edu.qut.bpmn.structuring.graph.Graph;
import au.edu.qut.bpmn.structuring.graph.Path;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.*;

/**
 * Created by Adriano on 18/02/2016.
 */
public class iBPStruct {
//    private static final Logger LOGGER = LoggerFactory.getLogger(iBPStruct.class);


    /* class' support data structs */
    private boolean isValid;
    private StructuringCore.Policy policy;
    private int maxDepth;
    private int maxSol;
    private int maxChildren;
    private int maxStates;
    private int maxMinutes;
    private boolean timeBounded;
    private boolean keepBisimulation;
    private int g = 0;

    private DiagramHandler diagramHandler;


    /* setup and rebuild data structs */
    private Map<String, BPMNNode> nodes;
    private Map<String, Gateway.GatewayType> gateways;

    private Map<String, List<String>> children;
    private Map<String, List<String>> parents;

    private Map<Integer, LinkedList<String>> originalPaths;

    private BPMNDiagram diagram;
    private HashMap<String, BPMNNode> knownGates = new HashMap<>();


    /* core structs for the algorithm*/
    private Graph graph;
    private Set<Graph> rigids;
    private Set<Graph> structuredRigids;
    private StructuringCore core;


    /* utilities methods */
    public iBPStruct(   StructuringCore.Policy policy,
                        int maxDepth,
                        int maxSol,
                        int maxChildren,
                        int maxStates,
                        int maxMinutes,
                        boolean timeBounded,
                        boolean keepBisimulation,
                        boolean forceStructuring) {
        isValid = false;
        this.policy = policy;
        this.maxDepth = maxDepth;
        this.maxSol = maxSol;
        this.maxChildren = maxChildren;
        this.maxStates = maxStates;
        this.maxMinutes = maxMinutes;
        this.timeBounded = timeBounded;
        this.keepBisimulation = keepBisimulation;

        diagramHandler = new DiagramHandler();

        System.out.println("iBPStruct - starting: ");
        System.out.println("iBPStruct - [Setting] policy: " + policy);
        System.out.println("iBPStruct - [Setting] pull-up: " + (!keepBisimulation));

        if( policy == StructuringCore.Policy.ASTAR ) {
            System.out.println("iBPStruct - [A*] time bounded: " + timeBounded);
            if( timeBounded )
                System.out.println("iBPStruct - [A*] max minutes: " + maxMinutes);
        }

        if( (policy == StructuringCore.Policy.LIM_ASTAR) || timeBounded ) {
            System.out.println("iBPStruct - [Limited A*] max solutions to get: " + maxSol);
            System.out.println("iBPStruct - [Limited A*] max children to generate: " + maxChildren);
            System.out.println("iBPStruct - [Limited A*] max states to keep: " + maxStates);
        }

        if( policy == StructuringCore.Policy.DEPTH )
            System.out.println("iBPStruct - [Depth-First] max depth to reach: " + maxDepth);

    }

    public boolean setProcess(Collection<BPMNNode> nodes, Collection<Flow> flows) {
        HashSet<String> starts = new HashSet<>();
        HashSet<String> ends = new HashSet<>();
        String nodeID;
        String srcID;
        String tgtID;

        //System.out.println("DEBUG - setting the process.");

        this.nodes = new HashMap<>();
        this.gateways = new HashMap<>();

        this.children = new HashMap<>();
        this.parents = new HashMap<>();

        for( BPMNNode n : nodes) {
            nodeID = n.getId().toString();
            this.nodes.put(nodeID, n);
            if( n instanceof Gateway ) gateways.put(nodeID, ((Gateway) n).getGatewayType());
        }

        starts.addAll(this.nodes.keySet());
        ends.addAll(this.nodes.keySet());

        for( Flow f : flows ) {

            srcID = f.getSource().getId().toString();
            tgtID = f.getTarget().getId().toString();

            if( !(isValid = this.nodes.containsKey(srcID)) ) {
                System.out.println("ERROR - found flow with an unknown source: " + srcID);
                return false;
            }

            if( !(isValid = this.nodes.containsKey(tgtID)) ) {
                System.out.println("ERROR - found flow with an unknown target: " + tgtID);
                return false;
            }

            if( !children.containsKey(srcID) ) children.put(srcID, new ArrayList<String>());
            children.get(srcID).add(tgtID);

            if( !parents.containsKey(tgtID) ) parents.put(tgtID, new ArrayList<String>());
            parents.get(tgtID).add(srcID);

            ends.remove(srcID);
            starts.remove(tgtID);
        }

        this.graph = new Graph(keepBisimulation, gateways);

        if( (ends.size() == 1) && (starts.size() == 1) ) {
            isValid = true;

            for( String s : starts ) {
                graph.setEntry(s);
                //System.out.println("DEBUG - entry: " + s);
                //System.out.println("DEBUG - graph.entry: " + graph.getEntry());
                if( !parents.containsKey(s) ) {
                    parents.put(s, new ArrayList<String>());
                    //System.out.println("DEBUG - added entry in parents.");
                } else {
                    System.out.println("ERROR - found one single entry but with parent nodes.");
                    isValid = false;
                }
            }

            for( String s : ends ) {
                graph.setExit(s);
                //System.out.println("DEBUG - exit: " + s);
                //System.out.println("DEBUG - graph.exit: " + graph.getExit());
                if( !children.containsKey(s) ) {
                    children.put(s, new ArrayList<String>());
                    //System.out.println("DEBUG - added exit in children.");
                } else {
                    System.out.println("ERROR - found one single exit but with children nodes.");
                    isValid = false;
                }
            }

            //System.out.println("DEBUG - found correct entry and exit points.");

        } else {
            System.out.println("ERROR - found multiple entry(" + starts.size() + ") or exit(" + ends.size() + ") points.");
            for(String s : starts) System.out.println("DEBUG - extra start(" + s + "): " + this.nodes.get(s).getLabel());
            for(String s : ends) System.out.println("DEBUG - extra end(" + s + "): " + this.nodes.get(s).getLabel());
            isValid = false;
        }

        //System.out.println("DEBUG - process ready.");
        return isValid;
    }

    public BPMNDiagram getDiagram() {
        return diagram;
    }


    /* core method */
    public boolean structure() {
        Graph backup;

        if( !isValid ) return false;

        if( !generatePaths() ) {
            System.out.println("ERROR - paths not generated correctly.");
            return false;
        }

        graph.detectLoops();
        graph.simplify();

        backup = new Graph(graph);

        try {
            rigids = graph.decompose();
            System.out.println("DEBUG - got " + rigids.size() + " rigids.");
        } catch( Exception e ) {
            System.out.println("ERROR - jbpt threw an exception. Structuring the whole diagram.");
            System.out.println("DEBUG - backing up and re-trying.");
            //e.printStackTrace(System.out);
            graph = backup;
            rigids = new HashSet<>();
            rigids.add(graph.minimalDecomposition());
        }

        core = new StructuringCore(policy, maxDepth, maxSol, maxChildren, maxStates,  maxMinutes, timeBounded);
        structuredRigids = core.structureAll(rigids);

        if( !graph.recompose(structuredRigids) ) {
            System.out.println("ERROR - impossible recompose the diagram.");
            return false;
        }

        //printGraph();

        buildDiagram();
        System.out.println("DEBUG - diagram built correctly.");

        return true;
    }

    /* setup method */
    private boolean generatePaths() {
        int PID = 0;
        String entry = graph.getEntry();
        String exit = graph.getExit();

        LinkedList<String> tasks;
        String tmpChild;

        ArrayList<String> toVisit = new ArrayList<>();
        HashSet<String> visited = new HashSet<>();

        originalPaths = new HashMap<>();

        toVisit.add(0, entry);
        visited.add(exit);

        //System.out.println("DEBUG - generating paths.");

        while( toVisit.size() != 0 ) {

            entry = toVisit.remove(0);
            visited.add(entry);

            //System.out.println("DEBUG - visiting: " + entry);

            for( String child : children.get(entry) ) {

                tmpChild = child;
                tasks = new LinkedList<>();

                while( !gateways.containsKey(tmpChild) && (children.get(tmpChild).size() == 1) ) {
                    //tmpChild is not a gateway neither the mainExit neither something weird
                    tasks.add(tmpChild);
                    tmpChild = children.get(tmpChild).get(0);
                }

                if( !gateways.containsKey(tmpChild) && !tmpChild.equals(exit) ) {
                    //found a node with multiple children that is not a gateway OR a node with zero children that is not the mainExit
                    System.out.println("ERROR - found a weird node: " + tmpChild);
                    System.out.println("ERROR - exit: " + exit);
                    return false;
                }

                PID++;
                originalPaths.put(-PID, tasks);
                graph.addPath(new Path(PID, -PID, entry, tmpChild, tasks.size(), false));
                if( !toVisit.contains(tmpChild) && !visited.contains(tmpChild) ) toVisit.add(0, tmpChild);
            }
        }

        System.out.println("DEBUG - (" + originalPaths.size() + ")paths generated successfully.");

        graph.setPID(PID);
        return true;
    }


    /* rebuilding methods */
    private void buildDiagram() {
        String entry;
        String exit;
        Path path;

        BPMNNode entryNode;
        BPMNNode exitNode;

        BPMNNode unfoldedPathExit;

        diagram = new BPMNDiagramImpl("process");
        knownGates = new HashMap<>();

        System.out.println("DEBUG - starting building diagram (" + graph.getAlivePaths().size() + ")");

        for( int pid : graph.getAlivePaths() ) {
            path = graph.getPath(pid);

            entry = path.getEntry();
            exit = path.getExit();

            generateGates(entry, exit, path.hasLoop());

            entryNode = knownGates.get(entry);
            exitNode = knownGates.get(exit);

            unfoldedPathExit = unfoldPath(pid, entryNode, exitNode);

            if( (unfoldedPathExit instanceof Gateway) && (exitNode instanceof Gateway) ) {
                ((Gateway) exitNode).setGatewayType(((Gateway) unfoldedPathExit).getGatewayType());
            } else System.out.println("WARNING - unfolded path returned a non gateway exit!");
        }
    }

    private BPMNNode unfoldPath(int pid, BPMNNode oEntry, BPMNNode oExit) {
        Path path = graph.getPath(pid);

        String entryID = path.getEntry();
        String exitID = path.getExit();

        BPMNNode entry = nodes.get(entryID);
        BPMNNode exit  = nodes.get(exitID);

        Gateway.GatewayType entryType;
        Gateway.GatewayType exitType;
        Gateway.GatewayType correctType = null;

        if( (entry instanceof Gateway) && (exit instanceof Gateway) ) {
            entryType = ((Gateway) entry).getGatewayType();
            exitType = ((Gateway) exit).getGatewayType();
            //correctType = bestType(entryType, exitType, path.isLoop() && (!path.getReverseBrothers().isEmpty()));
            correctType = bestType(entryType, exitType, path.isLoop());
        }

        entry = oEntry;
        exit = oExit;

        //System.out.println("DEBUG - unfolding path (" + path.getChain().size() + ")(" + path.getBrothers().size() + ")(" + path.getReverseBrothers().size() + "): " + path.getPID() + " - (" + oEntry + ":" + oExit + ")");

        if( path.getBrothers().size() != 0 ) {
            //System.out.println("DEBUG - path (" + path.getPID() + ") has brothers.");
            if( !(oEntry instanceof Gateway) || (((Gateway) oEntry).getGatewayType() != correctType)) {
                //System.out.println("DEBUG - creating entry for the brother");
                entry = createGateway(correctType);
                diagram.addFlow(oEntry, entry, "");
                //System.out.println("DEBUG - attaching edge: (" + oEntry + ":" + entry + ")");
            }

            if( !(oExit instanceof Gateway) || (((Gateway) oExit).getGatewayType() != correctType)) {
                //System.out.println("DEBUG - creating exit for the brother");
                exit = createGateway(correctType);
                if(oExit != null) {
                    diagram.addFlow(exit, oExit, "");
                    //System.out.println("DEBUG - attaching edge: (" + exit + ":" + oExit + ")");
                }
            }

            for( int i : path.getBrothers() ) unfoldPath(i, entry, exit);
        }

        if( path.getReverseBrothers().size() != 0 ) {
            //System.out.println("DEBUG - path (" + path.getPID() + ") has reverse brothers.");
            //if( !(oEntry instanceof Gateway) || (((Gateway) oEntry).getGatewayType() != Gateway.GatewayType.DATABASED) ) {
                //System.out.println("DEBUG - creating entry for the reverse brother (it will be the exit)");
                entry = createGateway(Gateway.GatewayType.DATABASED);
                diagram.addFlow(oEntry, entry, "");
                //System.out.println("DEBUG - attaching edge: (" + oEntry + ":" + entry + ")");
            //}

            //if( !(oExit instanceof Gateway) || (((Gateway) oExit).getGatewayType() != Gateway.GatewayType.DATABASED)  ) {
                //System.out.println("DEBUG - creating exit for reverse brother (it will be the entry)");
                exit = createGateway(Gateway.GatewayType.DATABASED);
                if(oExit != null) {
                    diagram.addFlow(exit, oExit, "");
                    //System.out.println("DEBUG - attaching edge: (" + exit + ":" + oExit + ")");
                }
            //}

            for( int i : path.getReverseBrothers() ) unfoldPath(i, exit, entry);
        }

        List<Integer> chain = path.getChain();
        int cSize = chain.size();
        int i = 0;
        int cPid = chain.get(i);

        while( i < cSize - 1 ) {
            if( cPid < 0 ) entry = generateNodes(cPid, entry);
            else entry = unfoldPath(cPid, entry, null);
            i++;
            cPid = chain.get(i);
        }

        if( cPid < 0 ) entry = generateNodes(cPid, entry);
        else entry = unfoldPath(cPid, entry, exit);

        if( exit != null && (!entry.equals(exit)) ) {
            diagram.addFlow(entry, exit, "");
            //System.out.println("DEBUG - attaching edge: (" + entry + ":" + exit + ")");
            return exit;
        } else return entry; //the exit of the last path in the chain if this path does not have brothers.
    }



    private void generateGates(String entry, String exit, boolean isLoop) {
        BPMNNode entryNode;
        BPMNNode exitNode;
        Gateway.GatewayType entryType;
        Gateway.GatewayType exitType;
        Gateway.GatewayType correctType;

        if( !knownGates.containsKey(entry) ) {
            entryNode = createNode(entry);
            knownGates.put(entry, entryNode);
        } else entryNode = knownGates.get(entry);

        if( !knownGates.containsKey(exit) ) {
            exitNode = createNode(exit);
            knownGates.put(exit, exitNode);
        } else exitNode = knownGates.get(exit);
/*
        if( (entryNode instanceof Gateway) && (exitNode instanceof Gateway) ) {
            //check soundness
            entryType = ((Gateway) entryNode).getGatewayType();
            exitType = ((Gateway) exitNode).getGatewayType();
            if( !(entryType == exitType) ) {
                correctType = bestType(entryType, exitType, isLoop);
                ((Gateway) entryNode).setGatewayType(correctType);
                ((Gateway) exitNode).setGatewayType(correctType);
            }
        }
*/
    }

    private Gateway.GatewayType bestType(Gateway.GatewayType entryType, Gateway.GatewayType exitType, boolean isLoop) {
        if( isLoop ) return Gateway.GatewayType.DATABASED;
        //if( keepBisimulation ) return entryType;
        return entryType;
        //if( (entryType == Gateway.GatewayType.PARALLEL) || (exitType == Gateway.GatewayType.PARALLEL) ) return Gateway.GatewayType.PARALLEL;
        //return Gateway.GatewayType.DATABASED;
    }

    private Gateway createGateway(Gateway.GatewayType gateType) {
        g++;
        return diagram.addGateway(Integer.toString(g), gateType);
    }

    private BPMNNode generateNodes(int pid, BPMNNode entry) {
        BPMNNode node;

        //System.out.print("DEBUG - unfolding path: " + pid + " - (" + entry + ":");

        LinkedList<String> tasks = originalPaths.get(pid);
        int size = tasks.size();
        int i = 0;

        while( i < size ) {
            diagram.addFlow(entry, (node = createNode(tasks.get(i))), "");
            //System.out.println("DEBUG - attaching edge: (" + entry + ":" + node + ")");
            entry = node;
            i++;
        }

        //System.out.println(entry + ")");

        return entry;
    }

    private BPMNNode createNode(String id) {
        BPMNNode node;
        BPMNNode duplicate = null;

        if( !nodes.containsKey(id) ) {
            System.out.println("ERROR - looked up for a node that does not exist.");
            return null;
        }

        node = nodes.get(id);
        duplicate = diagramHandler.copyNode(diagram, node, id);
        return duplicate;
    }


    private void printGraph() {
        System.out.println("Path created: " + graph.getPaths().size() );
        for( Path p : graph.getPaths().values() ) {
            System.out.println("(" + p.getEntry() + " -> " + p.getEntry() + ") Path: " + p.getPID());
            System.out.print("(" + p.getBrothers().size() + ") Brothers: ");
            for( int b : p.getBrothers() ) System.out.print(" " + b + " " );
            System.out.println(".");

            System.out.print("(" + p.getReverseBrothers().size() + ") Reverse Brothers: ");
            for( int r : p.getReverseBrothers() ) System.out.print(" " + r + " " );
            System.out.println(".");

            System.out.print("(" + p.getChain().size() + ") Chain: ");
            for( int c : p.getChain() ) System.out.print(" " + c + " " );
            System.out.println(".");
        }

        System.out.print("Alive paths: ");
        for( int a : graph.getAlivePaths() ) System.out.print(" " + a + " " );
        System.out.println(".");
    }

}
