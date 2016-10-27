package au.edu.qut.processmining.log.graph.heuristic;

import au.edu.qut.processmining.log.graph.EventNode;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import weka.classifiers.meta.END;

import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Created by Adriano on 24/10/2016.
 */
public class HeuristicNet {

    private static final double PRUNING_FACTOR = 0.95;

    private static final int STARTCODE = 0;
    private static final int ENDCODE = -1;

    private XLog log;

    private HashMap<String, Integer> sLog;
    private HashMap<Integer, String> events;
    private HashMap<String, Integer> parsed;

    private HashSet<HeuristicEdge> edges;
    private HashMap<Integer, HeuristicNode> nodes;

    private HashMap<Integer, HeuristicEdge> candidateFollower;
    private HashMap<Integer, HeuristicEdge> candidatePredecessor;

    private HashMap<Integer, HashSet<HeuristicEdge>> outgoing;
    private HashMap<Integer, HashSet<HeuristicEdge>> incoming;

    private HashMap<Integer, HashSet<Integer>> parallelisms;
    private HashMap<Integer, HashSet<Integer>> conflicts;

    private HashMap<Integer, HashMap<Integer, HeuristicEdge>> net;

    public HeuristicNet(XLog log) {
        this.log = log;
        nodes = new HashMap<>();
        edges = new HashSet<>();
        candidateFollower = new HashMap<>();
        candidatePredecessor = new HashMap<>();
        outgoing = new HashMap<>();
        incoming = new HashMap<>();
        net = new HashMap<>();

        preprocessing();
        generateNet();

//        generateNet(log);
        evaluateRelationships();
        evaluateDependecyScores();
        pruneHeuristicNet();
    }

    private void preprocessing() {
        parsed = new HashMap<>();
        events = new HashMap<>();
        sLog = new HashMap<>();

        int tIndex; //index to iterate on the log traces
        int eIndex; //index to iterate on the events of the trace

        XTrace trace;
        String sTrace;

        XEvent event;
        String label;
        int eventCounter;

        int totalTraces = log.size();
        int traceSize;

        events.put(STARTCODE, "autogen-start");
        events.put(ENDCODE, "autogen-end");

        eventCounter = 1;

        for( tIndex = 0; tIndex < totalTraces; tIndex++ ) {
            trace = log.get(tIndex);
            traceSize = trace.size();

            sTrace = "::" + Integer.toString(STARTCODE) + ":";
            for( eIndex = 0; eIndex < traceSize; eIndex++ ) {
                event = trace.get(eIndex);
                label = event.getAttributes().get("concept:name").toString();

                if( !parsed.containsKey(label) ) {
                    parsed.put(label, eventCounter);
                    events.put(eventCounter, label);
                    eventCounter++;
                }

                sTrace += ":" + parsed.get(label).toString() + ":";
            }
            sTrace += ":" + Integer.toString(ENDCODE) + "::";

            if( !sLog.containsKey(sTrace) ) sLog.put(sTrace, 0);
            sLog.put(sTrace, sLog.get(sTrace)+1);
        }

        System.out.println("DEBUG - total different events: " + (eventCounter-1));
        System.out.println("DEBUG - total different traces: " + sLog.size() );
        for( String t : sLog.keySet() ) {
            System.out.println("DEBUG - ["+ sLog.get(t) +"] trace: " + t);
        }
    }


    private void generateNet() {
        StringTokenizer trace;
        int traceFrequency;

        int event;
        int prevEvent;

        HeuristicNode node;
        HeuristicNode prevNode;
        HeuristicEdge edge;

//        System.out.println("DEBUG - total traces: " + totalTraces);

        HeuristicNode autogenStart = new HeuristicNode(events.get(STARTCODE), log.size());
        HeuristicNode autogenEnd = new HeuristicNode(events.get(ENDCODE), log.size());

        for( String t : sLog.keySet() ) {
            trace = new StringTokenizer(t, "::");
            traceFrequency = sLog.get(t);

            trace.nextToken(); //consuming the start event that is always 0
            prevEvent = STARTCODE;
            prevNode = autogenStart;

            while( trace.hasMoreTokens() ) {
                event = Integer.valueOf(trace.nextToken());

                if( !nodes.containsKey(event) ) {
                    node =  new HeuristicNode(events.get(event), event);
                    nodes.put(event, node);
                } else node = nodes.get(event);

                node.increaseFrequency(traceFrequency); //increasing frequency of this event occurrence

                if( !net.containsKey(prevEvent) ) net.put(prevEvent, new HashMap<Integer, HeuristicEdge>());
                if( !net.get(prevEvent).containsKey(event) ) {
                    edge = new HeuristicEdge(prevNode, node);
                    edges.add(edge);
                    if( !incoming.containsKey(event) ) incoming.put(event, new HashSet<HeuristicEdge>());
                    if( !outgoing.containsKey(prevEvent) ) outgoing.put(prevEvent, new HashSet<HeuristicEdge>());
                    incoming.get(event).add(edge);
                    outgoing.get(prevEvent).add(edge);
                    net.get(prevEvent).put(event, edge);
                }

                net.get(prevEvent).get(event).increaseFrequency(traceFrequency); //increasing frequency of this directly following relationship

                prevEvent = event;
                prevNode = node;
            }
        }
    }

//    private void generateNet(XLog log) {
//        int tIndex; //index to iterate on the log traces
//        int eIndex; //index to iterate on the events of the trace
//
//        XTrace trace;
//        XEvent event;
//
//        String eventLabel;
//        String prevEventLabel;
//
//        HeuristicNode node;
//        HeuristicNode prevNode;
//        HeuristicEdge edge;
//
//        int totalTraces = log.size();
//        int traceSize;
////        System.out.println("DEBUG - total traces: " + totalTraces);
//
//        HeuristicNode autogenStart = new HeuristicNode("autogen-start", totalTraces);
//        HeuristicNode autogenEnd = new HeuristicNode("autogen-end", totalTraces);
//
//        nodes.put("autogen-start", autogenStart);
//        nodes.put("autogen-end", autogenEnd);
//
//        for( tIndex = 0; tIndex < totalTraces; tIndex++ ) {
//
//            trace = log.get(tIndex);
//            traceSize = trace.size();
//            //  System.out.println("DEBUG - analyzing trace: " + tIndex);
//
//            prevEventLabel = autogenStart.getLabel();
//            prevNode = autogenStart;
//            node = null;
//
//            for( eIndex = 0; eIndex < traceSize; eIndex++ ) {
//
//                event = trace.get(eIndex);
//                eventLabel = event.getAttributes().get("concept:name").toString();
//
//                /* we assume there are no duplicate events, otherwise they will be treated as a unique event */
//                if( !nodes.containsKey(eventLabel) ) {
//                    node =  new HeuristicNode(eventLabel);
//                    nodes.put(eventLabel, node);
//                } else node = nodes.get(eventLabel);
//
//                node.increaseFrequency();
//
//                if( !net.containsKey(prevEventLabel) ) net.put(prevEventLabel, new HashMap<String, HeuristicEdge>());
//                if( !net.get(prevEventLabel).containsKey(eventLabel) ) {
//                    edge = new HeuristicEdge(prevNode, node);
//                    edges.add(edge);
//                    if( !incoming.containsKey(eventLabel) ) incoming.put(eventLabel, new HashSet<HeuristicEdge>());
//                    if( !outgoing.containsKey(prevEventLabel) ) outgoing.put(prevEventLabel, new HashSet<HeuristicEdge>());
//                    incoming.get(eventLabel).add(edge);
//                    outgoing.get(prevEventLabel).add(edge);
//                    net.get(prevEventLabel).put(eventLabel, edge);
//                }
//                net.get(prevEventLabel).get(eventLabel).increaseFrequency();
//
//                prevEventLabel = eventLabel;
//                prevNode = node;
//            }
//
//            eventLabel = autogenEnd.getLabel();
//            node = autogenEnd;
//
//            if( !net.containsKey(prevEventLabel) ) net.put(prevEventLabel, new HashMap<String, HeuristicEdge>());
//            if( !net.get(prevEventLabel).containsKey(eventLabel) ) {
//                edge = new HeuristicEdge(prevNode, node);
//                edges.add(edge);
//                if( !incoming.containsKey(eventLabel) ) incoming.put(eventLabel, new HashSet<HeuristicEdge>());
//                if( !outgoing.containsKey(prevEventLabel) ) outgoing.put(prevEventLabel, new HashSet<HeuristicEdge>());
//                incoming.get(eventLabel).add(edge);
//                outgoing.get(prevEventLabel).add(edge);
//                net.get(prevEventLabel).put(eventLabel, edge);
//            }
//            net.get(prevEventLabel).get(eventLabel).increaseFrequency();
//        }
//    }


    private void evaluateRelationships() {
        parallelisms = new HashMap<>();
        conflicts = new HashMap<>();

        for( int src : net.keySet() )
            for( int tgt : net.get(src).keySet() ) {
                if( net.containsKey(tgt) && (net.get(tgt).containsKey(src)) ) {
                    if( !parallelisms.containsKey(src) ) parallelisms.put(src, new HashSet<Integer>());
                    parallelisms.get(src).add(tgt);
                } else {
                    if( !conflicts.containsKey(src) ) conflicts.put(src, new HashSet<Integer>());
                    conflicts.get(src).add(tgt);
                }
            }
    }

    private boolean areConcurrent(int A, int B) {
        return (parallelisms.containsKey(A) && parallelisms.get(A).contains(B));
    }

    public boolean isDirectlyFollow(int A, int B) {
        return (net.containsKey(A) && net.get(A).containsKey(B));
    }

    private void evaluateDependecyScores() {
        int src2tgt_frequency;
        int tgt2src_frequency;
        double localDependency;

        int src;
        int tgt;

        for( HeuristicEdge e : edges ) {
            src2tgt_frequency = e.getFrequency();
            src = e.getSource().getCode();
            tgt = e.getTarget().getCode();

            if( src == tgt ) {
                localDependency = (double) (e.getFrequency())/(e.getFrequency() + 1);
                e.setLocalDependencyScore(localDependency);
            } else {
                if( net.containsKey(tgt) && net.get(tgt).containsKey(src) ) {
                    // this means there is a reverse edge
                    tgt2src_frequency = net.get(tgt).get(src).getFrequency();
                } else tgt2src_frequency = 0;

                localDependency = (double) (src2tgt_frequency - tgt2src_frequency) / (src2tgt_frequency + tgt2src_frequency + 1);
                e.setLocalDependencyScore(localDependency);

                if( !candidateFollower.containsKey(src) ) candidateFollower.put(src, e);
                else if( candidateFollower.get(src).getLocalDependencyScore() < localDependency ) candidateFollower.put(src, e);

                if( !candidatePredecessor.containsKey(tgt) ) candidatePredecessor.put(tgt, e);
                else if( candidatePredecessor.get(tgt).getLocalDependencyScore() < localDependency ) candidatePredecessor.put(tgt, e);
            }
            System.out.println("DEBUG - #" + src + " => " + tgt + " : " + localDependency);
        }
    }

    private void pruneHeuristicNet() {
        HashSet<HeuristicEdge> removableIncoming = new HashSet<>();
        HashSet<HeuristicEdge> removableOutgoing = new HashSet<>();
        int src, tgt;
        double maxDependencyScore;
        double dsThreshold;

        System.out.println("DEBUG - edges before pruning: " + edges.size());

        for( HeuristicEdge e : edges ) {
            src = e.getSource().getCode();
            tgt = e.getTarget().getCode();
            if(src == tgt) continue; //loop guard
            maxDependencyScore = candidateFollower.get(src).getLocalDependencyScore();
            dsThreshold = maxDependencyScore*PRUNING_FACTOR;
            if( e.getLocalDependencyScore() < dsThreshold ) removableOutgoing.add(e);
        }

        for( HeuristicEdge e : edges ) {
            src = e.getSource().getCode();
            tgt = e.getTarget().getCode();
            if(tgt == src) continue; //loop guard
            maxDependencyScore = candidatePredecessor.get(tgt).getLocalDependencyScore();
            dsThreshold = maxDependencyScore*PRUNING_FACTOR;
            if( e.getLocalDependencyScore() < dsThreshold ) removableIncoming.add(e);
        }

        for( HeuristicEdge e : removableIncoming )
            if( removableOutgoing.contains(e) ) {
                edges.remove(e);
                System.out.println("DEBUG - removing edge: " + e.getSource().getLabel() + " > " + e.getTarget().getLabel());
            }

        System.out.println("DEBUG - edges after pruning: " + edges.size());
    }

    public BPMNDiagram getHeuristicNet() {
        BPMNDiagram diagram = new BPMNDiagramImpl("heuristic-net");
        HashMap<Integer, BPMNNode> mapping = new HashMap<>();
        String label;
        Activity task;
        BPMNNode src, tgt;

        System.out.println("DEBUG - building the Heuristic net with [ nodes : edges ] = [" + nodes.size() + " : [ " + edges.size() + " ]");

        for( int event : nodes.keySet() ) {
            label = events.get(event) + "\n(" + nodes.get(event).getFrequency() + ")";
            task = diagram.addActivity(label, false, false, false, false, false);
            mapping.put(event, task);
        }

        for( HeuristicEdge edge : edges ) {
            src = mapping.get(edge.getSource().getCode());
            tgt = mapping.get(edge.getTarget().getCode());
            diagram.addFlow(src, tgt, Double.toString(edge.getLocalDependencyScore()));
        }

        return diagram;
    }
}
