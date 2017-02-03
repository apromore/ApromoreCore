package org.apromore.service.logvisualizer.fuzzyminer.model;

import org.apromore.service.logvisualizer.fuzzyminer.ProMJGraph;
import org.apromore.service.logvisualizer.fuzzyminer.ProMJGraphVisualizer;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.Octagon;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class FMClusterNode extends FMNode {

    protected String elementName;
    protected String elementType;
    protected HashSet<FMNode> primitives;

    public FMClusterNode(MutableFuzzyGraph graph, int index, String label) {
        super(graph, index, label);
        elementName = "Cluster " + index;
        elementType = "complete";
        primitives = new HashSet<FMNode>();
        getAttributeMap().put(AttributeMap.LABEL, label);
        getAttributeMap().put(AttributeMap.SHAPE, new Octagon(0.2));
        getAttributeMap().put(AttributeMap.RESIZABLE, true);
        getAttributeMap().put(AttributeMap.SHOWLABEL, true);
    }

    public void add(FMNode node) {
        primitives.add(node);
    }

    public boolean remove(FMNode node) {
        return primitives.remove(node);
    }

    public Set<FMNode> getPrimitives() {
        return primitives;
    }

    public int size() {
        return primitives.size();
    }

    public double getSignificance() {
        double sig = 0.0;
        for (FMNode node : primitives) {
            sig += node.getSignificance();
        }
        return sig / primitives.size();
    }

    public void setSignificance(double significance) {
        throw new AssertionError("Significance of cluster node cannot be modified!");
    }

    public String id() {
        return "Cluster_" + index;
    }

    public boolean contains(FMNode node) {
        return primitives.contains(node);
    }

    public boolean isDirectlyConnectedTo(FMNode other) {
        for (FMNode node : primitives) {
            if (node.isDirectlyConnectedTo(other)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.mining.fuzzymining.graph.Node#directlyFollows(org.
     * processmining.mining.fuzzymining.graph.Node)
     */
    public boolean directlyFollows(FMNode other) {
        for (FMNode node : primitives) {
            if (node.directlyFollows(other)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.processmining.mining.fuzzymining.graph.Node#getPredecessors()
     */
    public Set<FMNode> getPredecessors() {
        HashSet<FMNode> predecessors = new HashSet<FMNode>();
        for (FMNode node : primitives) {
            predecessors.addAll(node.getPredecessors());
        }
        predecessors.removeAll(primitives);
        predecessors.remove(this);
        return predecessors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.processmining.mining.fuzzymining.graph.Node#getSuccessors()
     */
    public Set<FMNode> getSuccessors() {
        HashSet<FMNode> successors = new HashSet<FMNode>();
        for (FMNode node : primitives) {
            successors.addAll(node.getSuccessors());
        }
        successors.removeAll(primitives);
        successors.remove(this);
        return successors;
    }

    /*
     * get all the start nodes inside the cluster
     */
    public Set<FMNode> getStartNodes() {
        HashSet<FMNode> startNodes = new HashSet<FMNode>();
        for (FMNode node : primitives) {
            Set<FMNode> nodePredecessors = node.getPredecessors();
            if (nodePredecessors.contains(this)) {
                nodePredecessors.remove(this);
            }
            //if this primitive node has predecessor nodes 
            //and not all of them are also primitive nodes in this cluster,
            //then this primitive node is the start node inside this cluster node
            if (!nodePredecessors.isEmpty() && !primitives.containsAll(nodePredecessors)) {
                startNodes.add(node);
            }
        }
        return startNodes;
    }

    /*
     * get all the end nodes inside the cluster
     */
    public Set<FMNode> getEndNodes() {
        HashSet<FMNode> endNodes = new HashSet<FMNode>();
        for (FMNode node : primitives) {
            Set<FMNode> nodeSuccessors = node.getSuccessors();
            if (nodeSuccessors.contains(this)) {
                nodeSuccessors.remove(this);
            }
            //if this primitive node has predecessor nodes 
            //and not all of them are also primitive nodes in this cluster,
            //then this primitive node is the start node inside this cluster node
            if (!nodeSuccessors.isEmpty() && !primitives.containsAll(nodeSuccessors)) {
                endNodes.add(node);
            }
        }

        return endNodes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.processmining.mining.fuzzymining.graph.Node#getElementName()
     */
    @Override
    public String getElementName() {
        return elementName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.processmining.mining.fuzzymining.graph.Node#getEventType()
     */
    @Override
    public String getEventType() {
        return elementType;
    }

    public void setElementName(String name) {
        elementName = name;
    }

    public void setEventType(String type) {
        throw new AssertionError("type cannot be changed for cluster nodes!");
    }

    protected void addEdgesBetweenSets(Set<FMNode> sources, Set<FMNode> targets, FMEdges edges) {
        int x, y;
        double sig, cor;
        for (FMNode source : sources) {
            x = source.getIndex();
            for (FMNode target : targets) {
                y = target.getIndex();
                if ((x < graph.getNumberOfInitialNodes()) && (y < graph.getNumberOfInitialNodes())) {
                    sig = graph.getBinarySignificance(x, y);
                    cor = graph.getBinaryCorrelation(x, y);
                } else {
                    sig = 0.5;
                    cor = 0.5;
                }
                if ((x == y) && (sig < 0.001)) {
                    continue;
                }
                if (sig > 0.0) {
                    edges.addEdge(source, target, sig, cor);
                }
            }
        }
    }

    public ProMJGraph getClusterGraphPanel(XLog log) throws Exception {

        ProMJGraph graphPanel;
        //make a new fuzzy graph according to the old one
        MutableFuzzyGraph clusterGraph = new MutableFuzzyGraph(graph.nodeSignificance, graph.edgeSignificance,
                graph.edgeCorrelation, log, graph.events, false);

        // write adjacent predecessor and successor nodes
        Set<FMNode> predecessors = getPredecessors();
        Set<FMNode> successors = getSuccessors();
        //LJF add start
        //add start nodes and end nodes of the inner graph of this cluster node
        Set<FMNode> startNodes = getStartNodes();
        clusterGraph.setStartNodes(startNodes);
        Set<FMNode> endNodes = getEndNodes();
        clusterGraph.setEndNodes(endNodes);
        //LJF add end
        // unified set, to prevent duplicate nodes (both predecessor and successor)
        HashSet<FMNode> adjacentNodes = new HashSet<FMNode>();
        adjacentNodes.addAll(predecessors);
        adjacentNodes.addAll(successors);

        //remove all the primitiveNodes
        for (int i = 0; i < clusterGraph.numberOfInitialNodes; i++) {
            FMNode node = clusterGraph.primitiveNodes[i];
            if (node != null) {
                clusterGraph.primitiveNodes[i] = null;
                clusterGraph.nodeAliasMap[i] = null;
                clusterGraph.graphElementRemoved(node);
            }
        }

        //Set the  primitive Nodes in the cluster to the clusterGraph
        int indexofPrimitives = 0;
        for (FMNode node : primitives) {
            clusterGraph.primitiveNodes[indexofPrimitives] = node;
            clusterGraph.nodeAliasMap[indexofPrimitives] = node;
            clusterGraph.graphElementAdded(node);
            indexofPrimitives++;
        }

        //keep the adjacent nodes and remove those which are not adjacent to the cluster node
        for (FMNode node : adjacentNodes) {

            clusterGraph.primitiveNodes[indexofPrimitives] = node;
            clusterGraph.nodeAliasMap[indexofPrimitives] = node;
            clusterGraph.graphElementAdded(node);
            indexofPrimitives++;
        }

        //reset the edges of the detail graph of this Cluster node
        clusterGraph.fmEdges = new HashSet<FMEdgeImpl>();

        // asssemble edges
        FMEdges clusterEdges = new FMEdges(clusterGraph);
        // write edges within clusters
        addEdgesBetweenSets(primitives, primitives, clusterEdges);
        for (FMEdgeImpl edge : clusterEdges.getEdges()) {
            clusterGraph.addEdge(edge);
        }
        // create external edges
        FMEdges externalEdges = new FMEdges(clusterGraph);
        // write edges from predecessors to cluster nodes
        addEdgesBetweenSets(predecessors, primitives, externalEdges);
        // write edges from cluster nodes to successors
        addEdgesBetweenSets(primitives, successors, externalEdges);
        // write edges
        for (FMEdgeImpl edge : externalEdges.getEdges()) {
            clusterGraph.addEdge(edge);
        }

        graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(clusterGraph);
        //JF add start to get compact layout
        //		ProMJGraph jgraph = graphPanel.getGraph();
        //		//change the layout of the graph
        //		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
        //		layout.setCompactLayout(true);
        //		layout.setInterRankCellSpacing(40);
        //		layout.setInterHierarchySpacing(10);
        //		layout.setIntraCellSpacing(10);
        //
        //		layout.setDeterministic(false);
        //		layout.setFineTuning(true);
        //		layout.setParallelEdgeSpacing(20);
        //
        //		jgraph.setUpdateLayout(layout);
        //JF add end
        return graphPanel;
    }

}