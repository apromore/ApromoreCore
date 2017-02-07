package au.edu.qut.processmining.miners.heuristic.net;

import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.heuristic.ui.net.HNMUIResult;
import org.apache.commons.lang3.StringUtils;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;

import java.util.*;

/**
 * Created by Adriano on 24/10/2016.
 */
public class HeuristicNet {

    private SimpleLog log;
    private int startcode;
    private int endcode;

    private Map<String, Integer> traces;
    private Map<Integer, String> events;

    private Set<HeuristicEdge> edges;
    private Map<Integer, HeuristicNode> nodes;

    private Map<Integer, HeuristicEdge> candidateSuccessor;
    private Map<Integer, HeuristicEdge> candidatePredecessor;

    private Map<Integer, HashSet<HeuristicEdge>> outgoings;
    private Map<Integer, HashSet<HeuristicEdge>> incomings;

    private Map<Integer, HashSet<Integer>> parallelisms;

    private Set<Integer> loopsL1;
    private Set<HeuristicEdge> loopsL2;

    private Map<Integer, HashMap<Integer, HeuristicEdge>> net;

    private double frequencyThreshold;
    private double parallelismsThreshold;

    public HeuristicNet(SimpleLog log) {
        this(log,   HNMUIResult.FREQUENCY_THRESHOLD,
                    HNMUIResult.PARALLELISMS_THRESHOLD);
    }

    public HeuristicNet(SimpleLog log, double frequencyThreshold, double parallelismsThreshold) {
        this.log = log;
        traces = log.getTraces();
        events = log.getEvents();

        startcode = log.getStartcode();
        endcode = log.getEndcode();

        this.frequencyThreshold = frequencyThreshold;
        this.parallelismsThreshold = parallelismsThreshold;
    }


    public BPMNDiagram getHeuristicDiagram(boolean labels) {
        BPMNDiagram diagram = new BPMNDiagramImpl("heuristic-net");
        HashMap<Integer, BPMNNode> mapping = new HashMap<>();
        String label;
        Activity task;
        BPMNNode src, tgt;

//        System.out.println("DEBUG - building the Heuristic net with [ nodes : edges ] = [" + nodes.size() + " : " + edges.size() + " ]");

        for( int event : nodes.keySet() ) {
            label = events.get(event) + "\n(" + nodes.get(event).getFrequency() + ")";
            task = diagram.addActivity( (labels ? label : Integer.toString(event)), false, false, false, false, false);
            mapping.put(event, task);
        }

        for( HeuristicEdge edge : edges ) {
            src = mapping.get(edge.getSource().getCode());
            tgt = mapping.get(edge.getTarget().getCode());
            diagram.addFlow(src, tgt, edge.toString());
        }

        return diagram;
    }

    public BPMNDiagram convertIntoBPMNDiagram() {
        BPMNDiagram diagram = new BPMNDiagramImpl("bpmn-diagram");
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

        for( HeuristicEdge edge : edges ) {
            src = mapping.get(edge.getSource().getCode());
            tgt = mapping.get(edge.getTarget().getCode());
            diagram.addFlow(src, tgt, edge.toString());
        }

        return diagram;
    }

    public boolean areConcurrent(int A, int B) {
        return (parallelisms.containsKey(A) && parallelisms.get(A).contains(B));
    }

    public void generateHeuristicNet() {
        System.out.println("HNM - starting ... ");
        System.out.println("HNM - [Setting] frequency threshold: " + frequencyThreshold);
        System.out.println("HNM - [Setting] parallelisms threshold: " + parallelismsThreshold);

        evaluateDirectlyFollowDependencies();               //first method to execute
        evaluateParallelisms();                             //depends on evaluateDirectlyFollowDependencies()
        evaluateLoopsDependencyScores();                    //depends on evaluateParallelisms()
        evaluateDependencyScores();                         //depends on evaluateLoops()
        pruneHeuristicNet(parallelismsThreshold, true);     //depends on evaluateDependencyScores()
        removeWeakParallelisms();                           //depends on pruneHeuristicNet(parallelismsThreshold)
        pruneHeuristicNet(frequencyThreshold, false);       //depends on removeWeakParallelisms()
        repair();                                           //last method to execute
    }

    private void evaluateDirectlyFollowDependencies() {
        StringTokenizer trace;
        int traceFrequency;

        int event;
        int prevEvent;

        HeuristicNode node;
        HeuristicNode prevNode;
        HeuristicEdge edge;

        HeuristicNode autogenStart;
        HeuristicNode autogenEnd;

        nodes = new HashMap<>();
        edges = new HashSet<>();
        outgoings = new HashMap<>();
        incomings = new HashMap<>();
        net = new HashMap<>();

        autogenStart = new HeuristicNode(events.get(startcode), startcode);
        this.addNode(autogenStart);
//        while parsing the simple log we will always skip the start event,
//        so we set now the maximum frequency because it is an artificial start event
        autogenStart.increaseFrequency(log.size());

        autogenEnd = new HeuristicNode(events.get(endcode), endcode);
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
                    node =  new HeuristicNode(events.get(event), event);
                    this.addNode(node);
                } else node = nodes.get(event);

//                  increasing frequency of this event occurrence
                node.increaseFrequency(traceFrequency);

                if( !net.containsKey(prevEvent) || !net.get(prevEvent).containsKey(event) ) {
                    edge = new HeuristicEdge(prevNode, node);
                    this.addEdge(edge);
                }

//                  increasing frequency of this directly following relationship
                net.get(prevEvent).get(event).increaseFrequency(traceFrequency);

                prevEvent = event;
                prevNode = node;
            }
        }
    }

    private void evaluateParallelisms() {
        parallelisms = new HashMap<>();

        System.out.println("HNM - evaluating parallelism ...");
        for( int src : net.keySet() )
            for( int tgt : net.get(src).keySet() )
                if( net.containsKey(tgt) && (net.get(tgt).containsKey(src)) ) {
                    if( !parallelisms.containsKey(src) ) parallelisms.put(src, new HashSet<Integer>());
                    parallelisms.get(src).add(tgt);
                }
    }

    private void evaluateLoopsDependencyScores() {
        HashSet<HeuristicEdge> removableLoopEdges = new HashSet();
        HashMap<Integer, HashMap<Integer, Double>> loop2Frequencies = new HashMap<>();
        int src, tgt;

        String src2tgt_loop2Pattern;
        String tgt2src_loop2Pattern;

        int src2tgt_loop2Frequency;
        int tgt2src_loop2Frequency;

        double loop2DependencyScore;

        loopsL1 = new HashSet<>();
        loopsL2 = new HashSet<>();

        System.out.println("HNM - evaluating loops length ONE ...");
        for( HeuristicEdge e : edges ) {
            src = e.getSource().getCode();
            tgt = e.getTarget().getCode();
            if( src == tgt ) {
                loopsL1.add(src);
                removableLoopEdges.add(e);
            }
        }

//        we removed the loop length 1 edges, because late we will just mark them as self-loop activities
        System.out.println("HNM - removing loops length ONE ...");
        for( HeuristicEdge e : removableLoopEdges ) this.removeEdge(e);

//        System.out.println("DEBUG - found " + loopsL1.size() + " self-loops:");
//        for( int code : loopsL1 ) System.out.println("DEBUG - self-loop: " + code);

        System.out.println("HNM - evaluating loops length TWO ...");
        for( HeuristicEdge e : edges ) {
            src = e.getSource().getCode();
            tgt = e.getTarget().getCode();

//            if src OR (previously AND) tgt are length 1 loops,
//            we do not evaluate length 2 loops for this edge,
//            because a length 1 loop (src|tgt) in parallel with something else (tgt|src)
//            can generate pattern of the type [src > tgt > src] OR [tgt > src > tgt]
            if( loopsL1.contains(src) || loopsL1.contains(tgt) ) continue;

            if( loop2Frequencies.containsKey(tgt) && loop2Frequencies.get(tgt).containsKey(src) ) {
//                this is a computational optimization, due to the fact if we have A > B loop length 2
//                B > A will be as well loop length 2, and they will have of course the same dependency score.
//                ref. "Process Mining with Heuristics Miner Algorithm" (Weijters et al.) - p.9
                loop2DependencyScore = loop2Frequencies.get(tgt).get(src);
            } else {
                src2tgt_loop2Pattern = "::" + src + "::" + tgt + "::" + src + "::";
                tgt2src_loop2Pattern = "::" + tgt + "::" + src + "::" + tgt + "::";
                src2tgt_loop2Frequency = 0;
                tgt2src_loop2Frequency = 0;

                for( String trace : traces.keySet() ) {
                    src2tgt_loop2Frequency += (StringUtils.countMatches(trace, src2tgt_loop2Pattern)*traces.get(trace));
                    tgt2src_loop2Frequency += (StringUtils.countMatches(trace, tgt2src_loop2Pattern)*traces.get(trace));
                }

//                this formula is taken from: "Process Mining with Heuristics Miner Algorithm" (Weijters et al.) - p.9
                loop2DependencyScore = (double)(src2tgt_loop2Frequency + tgt2src_loop2Frequency)/(src2tgt_loop2Frequency + tgt2src_loop2Frequency + 1);

                if( !loop2Frequencies.containsKey(src) ) loop2Frequencies.put(src, new HashMap<Integer, Double>());
                if( !loop2Frequencies.get(src).containsKey(tgt) ) loop2Frequencies.get(src).put(tgt, loop2DependencyScore);
            }

//            System.out.println("DEBUG - l2-score: " + src + " >> " + tgt + " : " + loop2DependencyScore);
            if( loop2DependencyScore != 0 ) {
//                if the dependency score of the loop length 2 is greater than zero:
//                1. we mark the edge as loop length 2 edge
//                2. we save its dependency score
                loopsL2.add(e);
                e.setLocalDependencyScore(loop2DependencyScore);

//                finally, we have to remove the parallelism relationship between src and tgt,
//                because it wasn't a real parallelism but just a short loop
                if( parallelisms.containsKey(src) && parallelisms.get(src).remove(tgt) );
//                    System.out.println("DEBUG - successfully removed short-loop parallelism: " + src + " || " + tgt);
            }
        }
    }

    private void evaluateDependencyScores() {
        int src2tgt_frequency;
        int tgt2src_frequency;
        double localDependency;

        Map<Integer, HeuristicEdge> candidateLoopSuccessor = new HashMap<>();
        Map<Integer, HeuristicEdge> candidateLoopPredecessor = new HashMap<>();

        int src;
        int tgt;

        candidateSuccessor = new HashMap<>();
        candidatePredecessor = new HashMap<>();

        System.out.println("HNM - evaluating dependency scores ...");
        for( HeuristicEdge e : edges ) {
            src = e.getSource().getCode();
            tgt = e.getTarget().getCode();

            if( !loopsL2.contains(e) ) {
//            note: we do not have to care about loops length 1, because they were removed, this is just a debug
                src2tgt_frequency = e.getFrequency();

                if( net.containsKey(tgt) && net.get(tgt).containsKey(src) ) {
//                    (src > tgt) AND (tgt > src) hold both, therefore we can consider the frequency of the second
//                    otherwise, we set the frequency of the second at 0, done in the ELSE of this IF
                    tgt2src_frequency = net.get(tgt).get(src).getFrequency();
                } else tgt2src_frequency = 0;

//                here we compute the local dependency applying the formula of the paper:
//                "Process Mining with Heuristics Miner Algorithm" (Weijters et al.) - p.7
                localDependency = (double) (src2tgt_frequency - tgt2src_frequency) / (src2tgt_frequency + tgt2src_frequency + 1);
                e.setLocalDependencyScore(localDependency);

//            we save the best successor and the best predecessor for each node
//            this will be used successively, to prune the heuristic net and to repair it
                if( !candidateSuccessor.containsKey(src) ) candidateSuccessor.put(src, e);
                else if( candidateSuccessor.get(src).getLocalDependencyScore() < localDependency ) candidateSuccessor.put(src, e);

                if( !candidatePredecessor.containsKey(tgt) ) candidatePredecessor.put(tgt, e);
                else if( candidatePredecessor.get(tgt).getLocalDependencyScore() < localDependency ) candidatePredecessor.put(tgt, e);

            } else {
//                if this edge is a loop length 2 edge, we have already computed its dependency score,
//                so we can just get it as attribute of the edge
                localDependency = e.getLocalDependencyScore();

                if( !candidateLoopSuccessor.containsKey(src) ) candidateLoopSuccessor.put(src, e);
                else if( candidateLoopSuccessor.get(src).getLocalDependencyScore() < localDependency ) candidateLoopSuccessor.put(src, e);

                if( !candidateLoopPredecessor.containsKey(tgt) ) candidateLoopPredecessor.put(tgt, e);
                else if( candidateLoopPredecessor.get(tgt).getLocalDependencyScore() < localDependency ) candidateLoopPredecessor.put(tgt, e);
            }

//            here we rectify the candidate successors and predecessors, that means:
//            a candidate successor or predecessor should always be a non loop edge,
//            however if a node has not forward edges incomings or outgoings
//            we set the loop as candidate successor or predecessor
            for( int node : candidateLoopSuccessor.keySet() ) if( !candidateSuccessor.containsKey(node) ) candidateSuccessor.put(node, candidateLoopSuccessor.get(node));
            for( int node : candidateLoopPredecessor.keySet() ) if( !candidatePredecessor.containsKey(node) ) candidatePredecessor.put(node, candidateLoopPredecessor.get(node));

//                System.out.println("DEBUG - dScore: " + src + " => " + tgt + " : " + localDependency);
        }
    }

    private void pruneHeuristicNet(double threshold, boolean keepBests) {
        HashSet<HeuristicEdge> toBeRemoved = new HashSet<>();
        int src, tgt;

        System.out.println("HNM - edges before pruning: " + edges.size());

        for( HeuristicEdge e : edges ) {
            src = e.getSource().getCode();
            tgt = e.getTarget().getCode();

//            we never remove the best successor or predecessor,
            if( keepBests ) if( candidateSuccessor.get(src).equals(e) || candidatePredecessor.get(tgt).equals(e) ) continue;
            else if( candidateSuccessor.get(src).equals(e) && candidatePredecessor.get(tgt).equals(e) ) continue;

//            this is a new implementation of the pruning policy,
//            we just remove edges that are under the given threshold
            if( e.getLocalDependencyScore() < threshold ) toBeRemoved.add(e);
        }

        for( HeuristicEdge e : toBeRemoved ) this.removeEdge(e);
        System.out.println("HNM - edges after pruning: " + edges.size());
    }

    private void removeWeakParallelisms() {
        int src;
        int tgt;
        int counter = 0;

//        if it happens that we have edges between A and B, they cannot be in parallel,
//        because the edge means that the directly follow relationship between A and B is strong
//        and they are not in parallel, otherwise no edges would remain between A and B
//        the natural consequence is that we remove their parallelism from the set of parallelisms
        System.out.println("HNM - evaluating weak parallelisms ...");
        for( HeuristicEdge e : edges ) {
            src = e.getSource().getCode();
            tgt = e.getTarget().getCode();

            if( parallelisms.containsKey(src) && parallelisms.get(src).remove(tgt) ) counter++;
            if( parallelisms.containsKey(tgt) && parallelisms.get(tgt).remove(src) );
        }
        System.out.println("HNM - weak parallelisms found and removed: " + counter);
    }

    private void repair() {
        recoverEdges();
        exploreAndRemove();
    }

    private void recoverEdges() {
//    it can happen that some activities remain without incoming or outgoing edges due to the pruning,
//    we restore the edges here, getting only the best predecessor and/or the best successor
        int src;
        int tgt;
        int counter = 0;
        HeuristicEdge recoverableEdge;

        ArrayList<HeuristicEdge> bestEdges = new ArrayList<>();
        for( int n : nodes.keySet() ) {
            if( n != startcode ) bestEdges.add(candidatePredecessor.get(n));
            if( n != endcode ) bestEdges.add(candidateSuccessor.get(n));
        }

        System.out.println("DEBUG - recovering edges from candidates: " + bestEdges.size() );

        Collections.sort(bestEdges);
        for( int i = 0; i < bestEdges.size(); i++ ) {
            recoverableEdge = bestEdges.get(i);
//            System.out.println("DEBUG - edge: " + recoverableEdge.getLocalDependencyScore() );
            src = recoverableEdge.getSource().getCode();

            if( outgoings.get(src).isEmpty() ) {
                counter++;
                addEdge(recoverableEdge);
            }
        }

        for( int i = 0; i < bestEdges.size(); i++ ) {
            recoverableEdge = bestEdges.get(i);
//            System.out.println("DEBUG - edge: " + recoverableEdge.getLocalDependencyScore() );
            tgt = recoverableEdge.getTarget().getCode();

            if( incomings.get(tgt).isEmpty() ) {
                counter++;
                addEdge(recoverableEdge);
            }
        }

        System.out.println("HNM - recovered edges: " + counter);
    }

    private void exploreAndRemove() {
        int src, tgt;

        System.out.println("HNM - removing unreachable nodes ...");

        LinkedList<Integer> toVisit = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Set<Integer> unvisited = new HashSet<>();

        toVisit.add(startcode);
        visited.add(startcode);
        visited.add(endcode);
        unvisited.addAll(nodes.keySet());
        unvisited.remove(startcode);
        unvisited.remove(endcode);

        while( !toVisit.isEmpty() ) {
            src = toVisit.removeFirst();
            for( HeuristicEdge oe : outgoings.get(src) ) {
                tgt = oe.getTarget().getCode();
                if( !visited.contains(tgt) ) {
                    toVisit.addLast(tgt);
                    visited.add(tgt);
                    unvisited.remove(tgt);
                }
            }
        }

        System.out.println("HNM - removed nodes: " + unvisited.size());
        for(int n : unvisited) removeNode(n);
    }


    /* data objects management */

    private void addNode(HeuristicNode n) {
        int code = n.getCode();

        nodes.put(code, n);
        if( !incomings.containsKey(code) ) incomings.put(code, new HashSet<HeuristicEdge>());
        if( !outgoings.containsKey(code) ) outgoings.put(code, new HashSet<HeuristicEdge>());
    }

    private void removeNode(int code) {
        HashSet<HeuristicEdge> removable = new HashSet<>();
        nodes.remove(code);
        for( HeuristicEdge e : incomings.get(code) ) removable.add(e);
        for( HeuristicEdge e : outgoings.get(code) ) removable.add(e);
        for( HeuristicEdge e : removable ) removeEdge(e);
    }

    private void addEdge(HeuristicEdge e) {
        int src = e.getSource().getCode();
        int tgt = e.getTarget().getCode();

        edges.add(e);
        incomings.get(tgt).add(e);
        outgoings.get(src).add(e);

        if( !net.containsKey(src) ) net.put(src, new HashMap<Integer, HeuristicEdge>());
        net.get(src).put(tgt, e);

//        System.out.println("DEBUG - added edge: " + src + " -> " + tgt);
    }

    private void removeEdge(HeuristicEdge e) {
        int src = e.getSource().getCode();
        int tgt = e.getTarget().getCode();
        incomings.get(tgt).remove(e);
        outgoings.get(src).remove(e);
        net.get(src).remove(tgt);
        edges.remove(e);
//        System.out.println("DEBUG - removed edge: " + src + " -> " + tgt);
    }


    /* DEBUG methods */

    public void printFrequencies() {
        System.out.println("DEBUG - printing frequencies:");
        for( HeuristicNode node : nodes.values() )
            System.out.println("DEBUG - " + node.getCode() + " = " + node.getFrequency());
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
