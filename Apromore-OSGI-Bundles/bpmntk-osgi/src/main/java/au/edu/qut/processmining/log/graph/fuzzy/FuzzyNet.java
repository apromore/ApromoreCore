package au.edu.qut.processmining.log.graph.fuzzy;

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
 * Created by Adriano on 14/06/2016.
 */
public class FuzzyNet {
    private XLog log;

    private HashSet<FuzzyEdge> edges;
    private HashMap<String, FuzzyNode> nodes;

    private HashMap<String, HashSet<FuzzyEdge>> outgoing;
    private HashMap<String, HashSet<FuzzyEdge>> incoming;

    private HashMap<String, HashMap<String, FuzzyEdge>> net;

    public FuzzyNet(XLog log) {
        this.log = log;
        nodes = new HashMap<>();
        edges = new HashSet<>();
        outgoing = new HashMap<>();
        incoming = new HashMap<>();
        net = new HashMap<>();

        generateNet(log);
    }

    private void generateNet(XLog log) {

        int tIndex; //index to iterate on the log traces
        int eIndex; //index to iterate on the events of the trace

        XTrace trace;
        XEvent event;

        String eventLabel;
        String prevEventLabel;

        FuzzyNode node;
        FuzzyNode prevNode;
        FuzzyEdge edge;


        int totalTraces = log.size();
        int traceSize;
//        System.out.println("DEBUG - total traces: " + totalTraces);

        FuzzyNode autogenStart = new FuzzyNode("autogen-start");
        autogenStart.increaseFrequency(totalTraces);
        FuzzyNode autogenEnd = new FuzzyNode("autogen-end");
        autogenEnd.increaseFrequency(totalTraces);

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
                    node =  new FuzzyNode(eventLabel);
                    nodes.put(eventLabel, node);
                } else node = nodes.get(eventLabel);

                node.increaseFrequency();
                if( eIndex == 0 ) node.incStartFrequency();

                if( !net.containsKey(prevEventLabel) ) net.put(prevEventLabel, new HashMap<String, FuzzyEdge>());
                if( !net.get(prevEventLabel).containsKey(eventLabel) ) {
                    edge = new FuzzyEdge(prevNode, node);
                    edges.add(edge);
                    if( !incoming.containsKey(eventLabel) ) incoming.put(eventLabel, new HashSet<FuzzyEdge>());
                    if( !outgoing.containsKey(prevEventLabel) ) outgoing.put(prevEventLabel, new HashSet<FuzzyEdge>());
                    incoming.get(eventLabel).add(edge);
                    outgoing.get(prevEventLabel).add(edge);
                    net.get(prevEventLabel).put(eventLabel, edge);
                }
                net.get(prevEventLabel).get(eventLabel).increaseFrequency();

                prevEventLabel = eventLabel;
                prevNode = node;
            }

            node.incEndFrequency();

            eventLabel = autogenEnd.getLabel();
            node = autogenEnd;

            if( !net.containsKey(prevEventLabel) ) net.put(prevEventLabel, new HashMap<String, FuzzyEdge>());
            if( !net.get(prevEventLabel).containsKey(eventLabel) ) {
                edge = new FuzzyEdge(prevNode, node);
                edges.add(edge);
                if( !incoming.containsKey(eventLabel) ) incoming.put(eventLabel, new HashSet<FuzzyEdge>());
                if( !outgoing.containsKey(prevEventLabel) ) outgoing.put(prevEventLabel, new HashSet<FuzzyEdge>());
                incoming.get(eventLabel).add(edge);
                outgoing.get(prevEventLabel).add(edge);
                net.get(prevEventLabel).put(eventLabel, edge);
            }
            net.get(prevEventLabel).get(eventLabel).increaseFrequency();
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

        for( FuzzyEdge edge : edges ) {
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


    public FuzzyNode getNode(String label) {return nodes.get(label); }

    public boolean isDirectlyFollow(String node, String follower) {
        return (net.containsKey(node) && net.get(node).containsKey(follower));
    }
}
