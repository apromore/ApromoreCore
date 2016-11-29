package au.edu.qut.bpmn.helper;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.*;

/**
 * Created by Adriano on 28/11/2016.
 */
public class GatewayMap {
    private int FID;

    /* about the initialization */
    private BPMNNode entry;
    private BPMNNode exit;
    private Map<BPMNNode, List<BPMNNode>> children;
    private Map<BPMNNode, List<BPMNNode>> parents;

    /* about the map itself */
    private Set<Gateway> gateways;
    private Set<GatewayMapFlow> flows;
    private Map<Gateway, Set<Gateway>> successors; //successor gateways
    private Map<Gateway, Set<Gateway>> predecessors; //predecessor gateways
    private Map<Gateway, Map<Gateway, List<GatewayMapFlow>>> graph;


    public GatewayMap() {}

    public boolean generateMap(BPMNDiagram diagram) {
        ArrayList<BPMNNode> toVisit = new ArrayList<>();
        HashSet<BPMNNode> visited = new HashSet<>();
        ArrayList<BPMNNode> tasks;
        BPMNNode tmpChild;
        BPMNNode entry;

        flows = new HashSet<>();
        gateways = new HashSet<>();
        successors = new HashMap<>();
        predecessors = new HashMap<>();
        graph = new HashMap<>();

        if( !init(diagram) ) return false;

        System.out.println("DEBUG - starting generation of the map");

        entry = this.entry;
        tmpChild = this.entry;

        System.out.println("DEBUG - path entry: " + tmpChild);
        while( !gateways.contains(tmpChild) && (children.get(tmpChild).size() == 1) ) {
            //tmpChild is not a gateway neither the mainExit neither something weird
            tmpChild = children.get(tmpChild).get(0);
            System.out.println("DEBUG - next child: " + tmpChild);
        }
        System.out.println("DEBUG - path exit: " + tmpChild);


        if( gateways.contains(tmpChild) ) { entry = tmpChild; }

        //System.out.println("DEBUG - generating paths.");
        toVisit.add(0, entry);
        visited.add(exit);

        FID = 0;
        while( toVisit.size() != 0 ) {
            entry = toVisit.remove(0);
            visited.add(entry);

            //System.out.println("DEBUG - visiting: " + entry);

            for( BPMNNode child : children.get(entry) ) {
                tmpChild = child;
                tasks = new ArrayList<>();

                tasks.add(0, entry);
                while( !gateways.contains(tmpChild) && (children.get(tmpChild).size() == 1) ) {
                    //tmpChild is not a gateway neither the mainExit neither something weird
                    tasks.add(0, tmpChild);
                    tmpChild = children.get(tmpChild).get(0);
                }

                if( !gateways.contains(tmpChild) && !tmpChild.equals(exit) ) {
                    //found a node with multiple children that is not a gateway OR a node with zero children that is not the mainExit
                    System.out.println("ERROR - found a weird node");
                    return false;
                }

                if( gateways.contains(tmpChild) ) this.addFlow(entry, tmpChild, tasks.get(0));
                if( !toVisit.contains(tmpChild) && !visited.contains(tmpChild) ) toVisit.add(0, tmpChild);
            }
        }

        System.out.println("DEBUG - gateways: " + gateways.size() );
        System.out.println("DEBUG - flows: " + flows.size() );
        return true;
    }

    private boolean init(BPMNDiagram diagram) {
        boolean isValid;

        HashSet<BPMNNode> starts = new HashSet<>();
        HashSet<BPMNNode> ends = new HashSet<>();

        BPMNNode src;
        BPMNNode tgt;

        this.children = new HashMap<>();
        this.parents = new HashMap<>();

        for( Gateway g : diagram.getGateways() ) this.addGateway(g);

        starts.addAll(diagram.getNodes());
        ends.addAll(diagram.getNodes());

        for( Flow f : diagram.getFlows() ) {
            src = f.getSource();
            tgt = f.getTarget();

            if( !children.containsKey(src) ) children.put(src, new ArrayList<BPMNNode>());
            children.get(src).add(tgt);

            if( !parents.containsKey(tgt) ) parents.put(tgt, new ArrayList<BPMNNode>());
            parents.get(tgt).add(src);

            ends.remove(src);
            starts.remove(tgt);
        }

        if( (ends.size() == 1) && (starts.size() == 1) ) {
            isValid = true;

            for( BPMNNode s : starts ) {
                entry = s;
                //System.out.println("DEBUG - entry: " + s);
                //System.out.println("DEBUG - graph.entry: " + graph.getEntry());
                if( !parents.containsKey(s) ) {
                    parents.put(s, new ArrayList<BPMNNode>());
                    //System.out.println("DEBUG - added entry in parents.");
                } else {
                    System.out.println("ERROR - found one single entry but with parent nodes.");
                    isValid = false;
                }
            }

            for( BPMNNode s : ends ) {
                exit = s;
                //System.out.println("DEBUG - exit: " + s);
                //System.out.println("DEBUG - graph.exit: " + graph.getExit());
                if( !children.containsKey(s) ) {
                    children.put(s, new ArrayList<BPMNNode>());
                    //System.out.println("DEBUG - added exit in children.");
                } else {
                    System.out.println("ERROR - found one single exit but with children nodes.");
                    isValid = false;
                }
            }
        } else {
            System.out.println("ERROR - found multiple entry(" + starts.size() + ") or exit(" + ends.size() + ") points.");
            isValid = false;
        }

        System.out.println("DEBUG - initialization done.");
        return isValid;
    }


    private void addGateway(Gateway gate) {
        gateways.add(gate);
        successors.put(gate, new HashSet<Gateway>());
        predecessors.put(gate, new HashSet<Gateway>());
        graph.put(gate, new HashMap<Gateway, List<GatewayMapFlow>>());
    }

    private void addFlow(BPMNNode src, BPMNNode tgt, BPMNNode ref) {
        if( !((src instanceof Gateway) && (tgt instanceof Gateway)) ) {
            System.out.println("WARNING - not a flow instance");
            return;
        }
        Gateway entry = (Gateway) src;
        Gateway exit = (Gateway) tgt;

        GatewayMapFlow flow = new GatewayMapFlow(++FID, entry, exit, ref);

        flows.add(flow);
        successors.get(entry).add(exit);
        predecessors.get(exit).add(entry);

        if( !graph.get(entry).containsKey(exit) ) graph.get(entry).put(exit, new ArrayList<GatewayMapFlow>());
        graph.get(entry).get(exit).add(flow);
    }


    public BPMNDiagram getGatewayMap() {
        BPMNDiagram diagram = new BPMNDiagramImpl("gatemap");
        Gateway src, tgt;
        Gateway nSRC, nTGT;

        HashMap<Gateway, Gateway> mapping = new HashMap<>();

        for( GatewayMapFlow flow : flows ) {
            src = flow.getSource();
            tgt = flow.getTarget();

            if( !mapping.containsKey(src) ) {
                nSRC = diagram.addGateway("", src.getGatewayType());
                mapping.put(src, nSRC);
            } else nSRC = mapping.get(src);

            if( !mapping.containsKey(tgt) ) {
                nTGT = diagram.addGateway("", tgt.getGatewayType());
                mapping.put(tgt, nTGT);
            } else nTGT = mapping.get(tgt);

            diagram.addFlow(nSRC, nTGT, "");
        }

        return diagram;
    }


    private class GatewayMapFlow implements Comparable {
        int id;
        Gateway src, tgt;
        BPMNNode ref;

        GatewayMapFlow(int id, Gateway src, Gateway tgt, BPMNNode ref) {
            this.id = id;
            this.src = src;
            this.tgt = tgt;
            this.ref = ref;
        }

        BPMNNode getRef() { return ref; }
        Gateway getSource() { return src; }
        Gateway getTarget() { return tgt; }

        @Override
        public int compareTo(Object o) {
            if(o instanceof GatewayMapFlow) return (this.id - ((GatewayMapFlow)o).id);
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof GatewayMapFlow) return (this.id == ((GatewayMapFlow)o).id);
            return false;
        }

    }

}

