package au.edu.qut.processmining.log.graph.heuristic;

import au.edu.qut.processmining.log.graph.EventNode;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Adriano on 24/10/2016.
 */
public class HeuristicNet {

    private static final double PRUNING_FACTOR = 0.95;

    private XLog log;
    private HashSet<HeuristicEdge> edges;
    private HashMap<String, HeuristicNode> nodes;
    private HashMap<String, HeuristicEdge> candidateFollower;

    private HashMap<String, HashSet<HeuristicEdge>> outgoing;
//    private HashMap<String, HashSet<HeuristicEdge>> incoming;

    private HashMap<String, HashMap<String, HeuristicEdge>> net;

    public HeuristicNet(XLog log) {
        this.log = log;
        nodes = new HashMap<>();
        edges = new HashSet<>();
        candidateFollower = new HashMap<>();
        outgoing = new HashMap<>();
//        incoming = new HashMap<>();
        net = new HashMap<>();

        generateNet(log);
        evaluateDependecyScores();
        pruneHeuristicNet();
    }

    private void generateNet(XLog log) {
        int tIndex; //index to iterate on the log traces
        int eIndex; //index to iterate on the events of the trace

        XTrace trace;
        XEvent event;

        String eventLabel;
        String prevEventLabel;

        HeuristicNode node;
        HeuristicNode prevNode;
        HeuristicEdge edge;

        int totalTraces = log.size();
        int traceSize;
//        System.out.println("DEBUG - total traces: " + totalTraces);

        HeuristicNode autogenStart = new HeuristicNode("autogen-start", totalTraces);
        HeuristicNode autogenEnd = new HeuristicNode("autogen-end", totalTraces);

        for( tIndex = 0; tIndex < totalTraces; tIndex++ ) {

            trace = log.get(tIndex);
            traceSize = trace.size();
            //  System.out.println("DEBUG - analyzing trace: " + tIndex);

            prevEventLabel = autogenStart.getLabel();
            prevNode = autogenStart;
            node = null;

            for( eIndex = 0; eIndex < traceSize; eIndex++ ) {

                event = trace.get(eIndex);
                eventLabel = event.getAttributes().get("concept:name").toString();

                /* we assume there are no duplicate events, otherwise they will be treated as a unique event */
                if( !nodes.containsKey(eventLabel) ) {
                    node =  new HeuristicNode(eventLabel);
                    nodes.put(eventLabel, node);
                } else node = nodes.get(eventLabel);

                node.increaseFrequency();

                if( !net.containsKey(prevEventLabel) ) net.put(prevEventLabel, new HashMap<String, HeuristicEdge>());
                if( !net.get(prevEventLabel).containsKey(eventLabel) ) {
                    edge = new HeuristicEdge(prevNode, node);
                    edges.add(edge);
//                    if( !incoming.containsKey(eventLabel) ) incoming.put(eventLabel, new HashSet<HeuristicEdge>());
                    if( !outgoing.containsKey(prevEventLabel) ) outgoing.put(prevEventLabel, new HashSet<HeuristicEdge>());
//                    incoming.get(eventLabel).add(edge);
                    outgoing.get(prevEventLabel).add(edge);
                    net.get(prevEventLabel).put(eventLabel, edge);
                }
                net.get(prevEventLabel).get(eventLabel).increaseFrequency();

                prevEventLabel = eventLabel;
                prevNode = node;
            }

            eventLabel = autogenEnd.getLabel();
            node = autogenEnd;

            if( !net.containsKey(prevEventLabel) ) net.put(prevEventLabel, new HashMap<String, HeuristicEdge>());
            if( !net.get(prevEventLabel).containsKey(eventLabel) ) {
                edge = new HeuristicEdge(prevNode, node);
                edges.add(edge);
//                if( !incoming.containsKey(eventLabel) ) incoming.put(eventLabel, new HashSet<HeuristicEdge>());
                if( !outgoing.containsKey(prevEventLabel) ) outgoing.put(prevEventLabel, new HashSet<HeuristicEdge>());
//                incoming.get(eventLabel).add(edge);
                outgoing.get(prevEventLabel).add(edge);
                net.get(prevEventLabel).put(eventLabel, edge);
            }
            net.get(prevEventLabel).get(eventLabel).increaseFrequency();
        }
    }


    private void evaluateDependecyScores() {
        int src2tgt_frequency;
        int tgt2src_frequency;
        double localDependency;

        String src, tgt;

        for( HeuristicEdge e : edges ) {
            src2tgt_frequency = e.getFrequency();
            src = e.getSource().getLabel();
            tgt = e.getTarget().getLabel();
            if( net.containsKey(tgt) && net.get(tgt).containsKey(src) ) {
                // this means there is a reverse edge
                tgt2src_frequency = net.get(tgt).get(src).getFrequency();
            } else tgt2src_frequency = 0;

            localDependency = (double)(src2tgt_frequency - tgt2src_frequency)/(src2tgt_frequency + tgt2src_frequency + 1);
            e.setLocalDependencyScore(localDependency);

            if( !candidateFollower.containsKey(src) ) candidateFollower.put(src, e);
            else if( candidateFollower.get(src).getLocalDependencyScore() < localDependency ) candidateFollower.put(src, e);

            System.out.println("DEBUG - #" + src + " => " + tgt + " : " + localDependency);
        }
    }

    private void pruneHeuristicNet() {
        HashSet<HeuristicEdge> toRemove = new HashSet<>();
        String src;
        double maxDependencyScore;
        double dsThreshold;

        System.out.println();

        for( HeuristicEdge e : edges ) {
            src = e.getSource().getLabel();
            maxDependencyScore = candidateFollower.get(src).getLocalDependencyScore();
            dsThreshold = maxDependencyScore*PRUNING_FACTOR;
            if( e.getLocalDependencyScore() < dsThreshold ) toRemove.add(e);
        }

        for( HeuristicEdge e : toRemove ) {
            edges.remove(e);
            System.out.println("DEBUG - removing edge: " + e.getSource().getLabel() + " > " + e.getTarget().getLabel());
        }
    }

    public BPMNDiagram getFuzzyNet() {
        BPMNDiagram diagram = new BPMNDiagramImpl("fuzzy-net");
        HashMap<String, BPMNNode> mapping = new HashMap<>();
        Activity task;
        Event event;
        EventNode src, tgt;
        String label;
        BPMNNode srcNode, tgtNode;

        event = diagram.addEvent("autogen-start\n("+ log.size() +")", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, false, null);
        mapping.put("autogen-start", event);

        event = diagram.addEvent("autogen-end\n("+ log.size() +")", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, false, null);
        mapping.put("autogen-end", event);

        for( String eventLabel : nodes.keySet() ) {
            label = eventLabel + "\n(" + nodes.get(eventLabel).getFrequency() + ")";
            task = diagram.addActivity(label, false, false, false, false, false);
            mapping.put(eventLabel, task);
        }

        for( HeuristicEdge edge : edges ) {
            src = edge.getSource();
            tgt = edge.getTarget();

            if( src == null ) srcNode = mapping.get("autogen-start");
            else srcNode = mapping.get(src.getLabel());

            if( tgt == null ) tgtNode = mapping.get("autogen-end");
            else tgtNode = mapping.get(tgt.getLabel());

            diagram.addFlow(srcNode, tgtNode, Integer.toString(edge.getFrequency()));
        }

        System.out.println("DEBUG - directly follow relationships:");
        for( String srcEvent : net.keySet() )
            for( String tgtEvent : net.get(srcEvent).keySet() )
                System.out.println("DEBUG - directly follow dependency holds in between: " + srcEvent + " > " + tgtEvent + "[" + net.get(srcEvent).get(tgtEvent).getFrequency() + "]");

        return diagram;
    }

    public boolean isDirectlyFollow(String node, String follower) {
        return (net.containsKey(node) && net.get(node).containsKey(follower));
    }

    public int getFrequency(String name){ return nodes.get(name).getFrequency(); }


}
