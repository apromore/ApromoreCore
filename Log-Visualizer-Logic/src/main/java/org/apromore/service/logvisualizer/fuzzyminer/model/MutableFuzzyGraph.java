package org.apromore.service.logvisualizer.fuzzyminer.model;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.xstream.XLogConverter;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.*;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.MetricsRepository;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.binary.BinaryMetric;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.unary.UnaryMetric;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FMLogEvents;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FuzzyMinerLog;

import java.text.NumberFormat;
import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class MutableFuzzyGraph extends AbstractDirectedGraph<FMNode, FMEdge<? extends FMNode, ? extends FMNode>>
        implements FuzzyGraph {

    protected UnaryMetric nodeSignificance;
    protected BinaryMetric edgeSignificance;
    protected BinaryMetric edgeCorrelation;

    protected int numberOfInitialNodes;
    protected FMLogEvents events;

    /*
     * When keeping references to XLogs, ProM needs to know how to serialize
     * them. Please note that this graphs SHOULD NOT keep a reference to an XLog
     * object. Instead, it should use the connection framework in ProM to keep
     * track of logs.
     */
    @XStreamConverter(XLogConverter.class)
    protected XLog log;

    protected FMNode[] primitiveNodes;
    protected ArrayList<FMClusterNode> clusterNodes = new ArrayList<FMClusterNode>();

    //protected HashSet<FMEdge<? extends FMNode,? extends FMNode>> graphEdges;
    protected Set<FMEdgeImpl> fmEdges = new HashSet<FMEdgeImpl>();

    protected double[][] actBinarySignificance;
    protected double[][] actBinaryCorrelation;
    protected FMNode[] nodeAliasMap;

    protected HashMap<String, String> attributes;

    //JF Add for performance analysis
    protected Set<FMNode> startNodes = new HashSet<FMNode>();
    protected Set<FMNode> endNodes = new HashSet<FMNode>();
    protected Map<FMNode, MutableFuzzyGraph> abstractnodeGraphMap;
    protected MetricsRepository metrics;

    //JiaoJiao Add for adaptive parameter setting
    protected double[][] actBinaryRespectiveSignificance;
    protected double[][] actBinaryRespectiveCorrelation;

    public static final String eventNameKey = "concept:name";
    public static final String eventTypeKey = "lifecycle:transition";

    protected static NumberFormat numberFormat = NumberFormat.getInstance();
    {
        numberFormat.setMinimumFractionDigits(3);
        numberFormat.setMaximumFractionDigits(3);
    }

    public static String format(double number) {
        return numberFormat.format(number);
    }

    public MutableFuzzyGraph(UnaryMetric nodeSignificance, BinaryMetric edgeSignificance, BinaryMetric edgeCorrelation,
                             XLog log, boolean toClone) {
        this.nodeSignificance = nodeSignificance;
        this.edgeSignificance = edgeSignificance;
        this.edgeCorrelation = edgeCorrelation;
        this.events = FuzzyMinerLog.getLogEvents(log);
        this.log = log;
        numberOfInitialNodes = this.events.size();
        primitiveNodes = new FMNode[numberOfInitialNodes];
        nodeAliasMap = new FMNode[numberOfInitialNodes];
        fmEdges = new HashSet<FMEdgeImpl>();
        clusterNodes = new ArrayList<FMClusterNode>();
        actBinarySignificance = new double[numberOfInitialNodes][numberOfInitialNodes];
        actBinaryCorrelation = new double[numberOfInitialNodes][numberOfInitialNodes];
        //if it is not to clone from the current graph, we need to add new nodes for transformation later,
        //otherwise, we just add nodes in primitiveNodes and nodeAliasMap.
        if (!toClone) {
            for (int x = 0; x < numberOfInitialNodes; x++) {
                for (int y = 0; y < numberOfInitialNodes; y++) {
                    actBinarySignificance[x][y] = edgeSignificance.getMeasure(x, y);
                    actBinaryCorrelation[x][y] = edgeCorrelation.getMeasure(x, y);
                }
            }

            for (int i = 0; i < numberOfInitialNodes; i++) {
                //create new FMNode		
                addNode(i);
            }
        }

        //JF
        startNodes = new HashSet<FMNode>();
        endNodes = new HashSet<FMNode>();
        abstractnodeGraphMap = new HashMap<FMNode, MutableFuzzyGraph>();
    }

    /*
     * JF add according to the addNode and deleteNode functions in the editor
     * panel
     */
    public MutableFuzzyGraph(UnaryMetric nodeSignificance, BinaryMetric edgeSignificance, BinaryMetric edgeCorrelation,
                             XLog log, FMLogEvents logEvents, boolean isFuzzyMap) {
        this.nodeSignificance = nodeSignificance;
        this.edgeSignificance = edgeSignificance;
        this.edgeCorrelation = edgeCorrelation;
        this.events = logEvents;
        this.log = log;
        numberOfInitialNodes = this.events.size();
        primitiveNodes = new FMNode[numberOfInitialNodes];
        nodeAliasMap = new FMNode[numberOfInitialNodes];
        fmEdges = new HashSet<FMEdgeImpl>();
        abstractnodeGraphMap = new HashMap<FMNode, MutableFuzzyGraph>();
        //	getAttributeMap().put(AttributeMap.LABEL, label);
        if (!isFuzzyMap) {// this is a fuzzy model
            initializeGraph();
        }
    }

    public MutableFuzzyGraph(MetricsRepository metrics) {
        this(metrics.getAggregateUnaryMetric(), metrics.getAggregateSignificanceBinaryMetric(), metrics
                .getAggregateCorrelationBinaryMetric(), metrics.getLogReader(), false);
        this.metrics = metrics;
    }

    /*
     * this constructor is used to create the molecular inner structure graph
     */
    public MutableFuzzyGraph(XLog log) {
        this.events = FuzzyMinerLog.getLogEvents(log);
        this.log = log;
        numberOfInitialNodes = this.events.getEventsCount();
        primitiveNodes = new FMNode[numberOfInitialNodes];
        nodeAliasMap = new FMNode[numberOfInitialNodes];
        fmEdges = new HashSet<FMEdgeImpl>();
        abstractnodeGraphMap = new HashMap<FMNode, MutableFuzzyGraph>();
    }

    public MetricsRepository getMetrics() {
        return metrics;
    }

    public void setMetrics(MetricsRepository mr) {
        metrics = mr;
    }

    public Set<FMNode> getStartNodes() {
        return startNodes;
    }

    public void setStartNodes(Set<FMNode> startNodes) {
        this.startNodes = startNodes;
    }

    public Set<FMNode> getEndNodes() {
        return endNodes;
    }

    public void setEndNodes(Set<FMNode> endNodes) {
        this.endNodes = endNodes;
    }

    public XLog getLog() {
        return log;
    }

    public Map<FMNode, MutableFuzzyGraph> getAbstractionNodeGraphMap() {
        return abstractnodeGraphMap;
    }

    public void getAbstractionNodeGraphMap(Map<FMNode, MutableFuzzyGraph> abstractionNodeGraphMap) {
        this.abstractnodeGraphMap = abstractionNodeGraphMap;
    }

    /**
     * initializes the graph structure as found in metrics repository
     */
    public void initializeGraph() {
        //delete the elements of graph first in order to redraw the graph

        for (FMEdgeImpl edge : fmEdges) {
            graphElementRemoved(edge);
        }
        for (FMClusterNode node : clusterNodes) {
            graphElementRemoved(node);
        }
        for (FMNode node : primitiveNodes) {
            if (node != null) {
                graphElementRemoved(node);
            }
        }
        //initialize the graph
        clusterNodes = new ArrayList<FMClusterNode>();
        fmEdges = new HashSet<FMEdgeImpl>();
        primitiveNodes = new FMNode[numberOfInitialNodes];
        actBinarySignificance = new double[numberOfInitialNodes][numberOfInitialNodes];
        actBinaryCorrelation = new double[numberOfInitialNodes][numberOfInitialNodes];

        for (int x = 0; x < numberOfInitialNodes; x++) {
            for (int y = 0; y < numberOfInitialNodes; y++) {
                actBinarySignificance[x][y] = edgeSignificance.getMeasure(x, y);
                actBinaryCorrelation[x][y] = edgeCorrelation.getMeasure(x, y);
            }
        }

        for (int i = 0; i < numberOfInitialNodes; i++) {
            //create new FMNode		
            addNode(i);
        }

        //JF
        startNodes = new HashSet<FMNode>();
        endNodes = new HashSet<FMNode>();
        abstractnodeGraphMap = new HashMap<FMNode, MutableFuzzyGraph>();

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#
     * getNumberOfInitialNodes()
     */
    public int getNumberOfInitialNodes() {
        return numberOfInitialNodes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#getLogEvents
     * ()
     */
    public FMLogEvents getLogEvents() {
        return events;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#getPrimitiveNode
     * (int)
     */
    public FMNode getPrimitiveNode(int index) {
        return primitiveNodes[index];
    }

    public void setPrimitiveNode(int index, FMNode node) {
        primitiveNodes[index] = node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#getNodeMappedTo
     * (int)
     */
    public FMNode getNodeMappedTo(int index) {
        return nodeAliasMap[index];
    }

    public void setNodeAliasMapping(int index, FMNode alias) {
        nodeAliasMap[index] = alias;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#getClusterNodes
     * ()
     */
    public List<FMClusterNode> getClusterNodes() {
        return clusterNodes;
    }

    public Set<FMEdgeImpl> getFMEdges() {
        return fmEdges;
    }

    public synchronized void addClusterNode(FMClusterNode cluster) {
        clusterNodes.add(cluster);
        graphElementAdded(cluster);
    }

    public synchronized boolean removeClusterNode(FMClusterNode cluster) {
        boolean isRemoved = false;
        isRemoved = clusterNodes.remove(cluster);
        if (isRemoved) {
            graphElementRemoved(cluster);
        }
        return isRemoved;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#
     * getBinarySignificance(int, int)
     */
    public double getBinarySignificance(int fromIndex, int toIndex) {
        return actBinarySignificance[fromIndex][toIndex];
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#
     * getBinaryCorrelation(int, int)
     */
    public double getBinaryCorrelation(int fromIndex, int toIndex) {
        return actBinaryCorrelation[fromIndex][toIndex];
    }

    public void setBinarySignificance(int fromIndex, int toIndex, double value) {
        actBinarySignificance[fromIndex][toIndex] = value;
    }

    public void setBinaryCorrelation(int fromIndex, int toIndex, double value) {
        actBinaryCorrelation[fromIndex][toIndex] = value;
    }

    public synchronized void removeEdgePermanently(FMEdgeImpl edge) {
        FMNode source = edge.getSource();
        FMNode target = edge.getTarget();

        //remove Edge from primitive nodes inside the Cluster to the outer FMNode 
        if (source instanceof FMClusterNode || target instanceof FMClusterNode) {
            removeClusterConcernedEdge(source, target);
        } else {
            setEdgeMeasureToZero(source.getIndex(), target.getIndex());
        }

        removeEdge(edge);
    }

    protected synchronized void removeClusterConcernedEdge(FMNode source, FMNode target) {
        if (source instanceof FMClusterNode) {
            for (FMNode sourcePrimitive : ((FMClusterNode) source).getPrimitives()) {
                removeClusterConcernedEdge(sourcePrimitive, target);
            }
        } else if (target instanceof FMClusterNode) {
            for (FMNode targetPrimitive : ((FMClusterNode) target).getPrimitives()) {
                removeClusterConcernedEdge(source, targetPrimitive);
            }
        } else {
            setEdgeMeasureToZero(source.getIndex(), target.getIndex());
        }
    }

    protected synchronized void setEdgeMeasureToZero(int from, int to) {
        this.edgeSignificance.setMeasure(from, to, 0.0);
        this.edgeCorrelation.setMeasure(from, to, 0.0);
        this.actBinarySignificance[from][to] = 0.0;
        this.actBinaryCorrelation[from][to] = 0.0;

    }

    public void hidePermanently(FMNode node) {
        if (node instanceof FMClusterNode) {
            // cluster, remove all primitives
            HashSet<FMNode> primitives = new HashSet<FMNode>(((FMClusterNode) node).getPrimitives());
            for (FMNode primitive : primitives) {
                hidePermanently(primitive);
            }
            clusterNodes.remove(node);
            //removeFMClusterNode((FMClusterNode) node);
            //	graphElementRemoved(node);
        } else {
            // primitive node, remove permanently
            int removeIndex = node.getIndex();
            // remove from metrics
            this.nodeSignificance.setMeasure(removeIndex, 0.0);
            for (int x = 0; x < this.numberOfInitialNodes; x++) {
                this.edgeSignificance.setMeasure(x, removeIndex, 0.0);
                this.edgeSignificance.setMeasure(removeIndex, x, 0.0);
                this.edgeCorrelation.setMeasure(x, removeIndex, 0.0);
                this.edgeCorrelation.setMeasure(removeIndex, x, 0.0);
                this.actBinarySignificance[x][removeIndex] = 0.0;
                this.actBinarySignificance[removeIndex][x] = 0.0;
                this.actBinaryCorrelation[x][removeIndex] = 0.0;
                this.actBinaryCorrelation[removeIndex][x] = 0.0;
            }
            // remove from clusters
            for (int i = 0; i < this.clusterNodes.size(); i++) {
                FMClusterNode cluster = this.clusterNodes.get(i);
                if (cluster.contains(node)) {
                    cluster.remove(node);
                    //removeNode(node);
                    if (cluster.size() == 0) {
                        clusterNodes.remove(i);
                        //removeFMClusterNode(cluster);
                        i--;
                    }
                }
            }
            // mark as deleted in node alias map
            nodeAliasMap[removeIndex] = null;
            //	removeSurroundingEdges(node);			
        }
    }

    public synchronized FMNode addNode(XEvent event) {
        events.add(event);
        numberOfInitialNodes++;
        this.nodeSignificance = addIndex(this.nodeSignificance);
        this.edgeSignificance = addIndex(this.edgeSignificance);
        this.edgeCorrelation = addIndex(this.edgeCorrelation);
        this.actBinarySignificance = addIndex(this.actBinarySignificance);
        this.actBinaryCorrelation = addIndex(this.actBinaryCorrelation);
        for (FMClusterNode cluster : clusterNodes) {
            cluster.setIndex(cluster.getIndex() + 1);
        }
        //Construct the FMNode label
        FMNode node = new FMNode(this, events.size() - 1, "");
        FMNode[] nPrimitiveNodes = new FMNode[numberOfInitialNodes];
        FMNode[] nNodeAliasMap = new FMNode[numberOfInitialNodes];
        for (int index = 0; index < (numberOfInitialNodes - 1); index++) {
            nPrimitiveNodes[index] = primitiveNodes[index];
            nNodeAliasMap[index] = nodeAliasMap[index];
        }
        nPrimitiveNodes[numberOfInitialNodes - 1] = node;
        nNodeAliasMap[numberOfInitialNodes - 1] = node;
        primitiveNodes = nPrimitiveNodes;
        nodeAliasMap = nNodeAliasMap;
        graphElementAdded(node);
        return node;
    }

    public synchronized void addNode(int index) {
        FMNode node = new FMNode(this, index, "");
        String nodeLabel = getNodeLabel(node);
        node.setLabel(nodeLabel);
        primitiveNodes[index] = node;
        nodeAliasMap[index] = node;
        graphElementAdded(node);
    }

    public static String getNodeLabel(FMNode fmNode) {
        String label = "";
        if(fmNode instanceof FMClusterNode) {
            FMClusterNode clusterNode = (FMClusterNode)fmNode;
            label = clusterNode.id() + " " + Integer.toString(clusterNode.size()) + " elements " + " " + MutableFuzzyGraph.format(clusterNode.getSignificance());
        } else {
            label = fmNode.getElementName() + " " + fmNode.getEventType() + " " + MutableFuzzyGraph.format(fmNode.getSignificance());
        }

        return label;
    }

    public synchronized void addNode(FMNode node, int index) {
        primitiveNodes[index] = node;
        nodeAliasMap[index] = node;
        graphElementAdded(node);
    }

    protected static BinaryMetric removeIndex(BinaryMetric original, int index) {
        BinaryMetric removed = new BinaryMetric(original.getName(), original.getDescription(), original.size() - 1);
        int removedX = 0;
        int removedY = 0;
        for (int x = 0; x < original.size(); x++) {
            if (x != index) {
                for (int y = 0; y < original.size(); y++) {
                    if (y != index) {
                        removed.setMeasure(removedX, removedY, original.getMeasure(x, y));
                        removedY++;
                    }
                }
                removedX++;
            }
            removedY = 0;
        }
        return removed;
    }

    protected static UnaryMetric removeIndex(UnaryMetric original, int index) {
        UnaryMetric removed = new UnaryMetric(original.getName(), original.getDescription(), original.size() - 1);
        int removedX = 0;
        for (int x = 0; x < original.size(); x++) {
            if (x != index) {
                removed.setMeasure(removedX, original.getMeasure(x));
                removedX++;
            }
        }
        return removed;
    }

    protected static double[][] removeIndex(double[][] original, int index) {
        int originalSize = original[0].length;
        double[][] removed = new double[originalSize - 1][originalSize - 1];
        int removedX = 0;
        int removedY = 0;
        for (int x = 0; x < originalSize; x++) {
            if (x != index) {
                for (int y = 0; y < originalSize; y++) {
                    if (y != index) {
                        removed[removedX][removedY] = original[x][y];
                        removedY++;
                    }
                }
                removedX++;
            }
            removedY = 0;
        }
        return removed;
    }

    protected static BinaryMetric addIndex(BinaryMetric original) {
        BinaryMetric added = new BinaryMetric(original.getName(), original.getDescription(), original.size() + 1);
        for (int x = 0; x < original.size(); x++) {
            for (int y = 0; y < original.size(); y++) {
                added.setMeasure(x, y, original.getMeasure(x, y));
            }
        }
        return added;
    }

    protected static UnaryMetric addIndex(UnaryMetric original) {
        UnaryMetric added = new UnaryMetric(original.getName(), original.getDescription(), original.size() + 1);
        for (int x = 0; x < original.size(); x++) {
            added.setMeasure(x, original.getMeasure(x));
        }
        return added;
    }

    protected double[][] addIndex(double[][] original) {
        int originalSize = original[0].length;
        double[][] added = new double[originalSize + 1][originalSize + 1];
        for (int x = 0; x < originalSize; x++) {
            for (int y = 0; y < originalSize; y++) {
                added[x][y] = original[x][y];
            }
        }
        for (int x = 0; x < originalSize + 1; x++) {
            added[x][originalSize] = 0.0;
            added[originalSize][x] = 0.0;
        }
        return added;
    }

    public double getThresholdShowingPrimitives(int numberOfPrimitives) {
        double[] significances = getSortedNodeSignificances();
        int index = significances.length - numberOfPrimitives;
        if (index < 0) {
            index = 0;
        }
        return significances[index];
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#
     * getMinimalNodeSignificance()
     */
    public double getMinimalNodeSignificance() {
        double[] significances = getSortedNodeSignificances();
        return significances[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#
     * getSortedNodeSignificances()
     */
    public double[] getSortedNodeSignificances() {
        double[] significances = new double[primitiveNodes.length];
        for (int i = 0; i < primitiveNodes.length; i++) {
            significances[i] = primitiveNodes[i].getSignificance();
        }
        Arrays.sort(significances); // in ascending numerical order
        return significances;
    }

    /*
     * This will return all nodes in the FuzzyGraph including the common Event
     * node and the Cluster Node.
     */
    public Set<FMNode> getNodes() {
        HashSet<FMNode> activeNodes = new HashSet<FMNode>();
        for (int i = 0; i < numberOfInitialNodes; i++) {
            if (nodeAliasMap[i] != null) {
                activeNodes.add(nodeAliasMap[i]);
            }
        }
        return activeNodes;
    }

    public Set<FMEdgeImpl> getEdgeImpls() {

        return fmEdges;
    }

    /*
     * this method is set to implement the method in the DirectedGraph.
     */
    public Set<FMEdge<? extends FMNode, ? extends FMNode>> getEdges() {
        Set<FMEdge<? extends FMNode, ? extends FMNode>> edges = new HashSet<FMEdge<? extends FMNode, ? extends FMNode>>();
        edges.addAll(fmEdges);
        return Collections.unmodifiableSet(edges);
    }

	/*
	 * Set the value of the edges in the FuzzyGraph
	 */

    public void setEdgeImpls() {

        for (int x = 0; x < numberOfInitialNodes; x++) {
            for (int y = 0; y < numberOfInitialNodes; y++) {
                FMNode source = nodeAliasMap[x];
                FMNode target = nodeAliasMap[y];
                if (source == null || target == null || (source.equals(target) && source instanceof FMClusterNode)) {
                    continue; // do not draw cluster self-references
                }
                if ((x == y) && actBinarySignificance[x][y] < 0.001) {
                    continue;
                }
                if (actBinarySignificance[x][y] > 0.0) {

                    addEdge(source, target, actBinarySignificance[x][y], actBinaryCorrelation[x][y]);
                }
            }
        }

    }

    public synchronized FMEdgeImpl addEdge(FMNode source, FMNode target, double significance, double correlation) {
        checkAddEdge(source, target);
        FMEdgeImpl edge = new FMEdgeImpl(source, target, significance, correlation);
        if (fmEdges.add(edge)) {
            graphElementAdded(edge);
            return edge;
        } else {
            for (FMEdgeImpl oE : fmEdges) {
                if (oE.equals(edge)) {
                    // merge to max value of the two merged edges
                    if (edge.significance > oE.significance) {
                        oE.significance = edge.significance;
                    }
                    if (edge.correlation > oE.correlation) {
                        oE.correlation = edge.correlation;
                    }
                    graphElementChanged(oE);
                    return oE;
                }
            }
        }
        assert (false);
        return null;
    }

    public synchronized FMEdgeImpl addEdge(FMEdgeImpl edge) {
        return addEdge(edge.getSource(), edge.getTarget(), edge.getSignificance(), edge.getCorrelation());
    }

    public int getEventClassIndex(XEvent ate) {
        return events.findLogEventNumber(ate);
    }

    public int getEventClassIndex(String element, String type) {
        return events.findLogEventNumber(element, type);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#
     * getEdgeCorrelationMetric()
     */
    public BinaryMetric getEdgeCorrelationMetric() {
        return this.edgeCorrelation;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#
     * getEdgeSignificanceMetric()
     */
    public BinaryMetric getEdgeSignificanceMetric() {
        return this.edgeSignificance;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#
     * getNodeSignificanceMetric()
     */
    public UnaryMetric getNodeSignificanceMetric() {
        return this.nodeSignificance;
    }

    public void setNodeSignificance(UnaryMetric nodeSignificance) {
        this.nodeSignificance = nodeSignificance;
    }

    public void setEdgeSignificance(BinaryMetric edgeSignificance) {
        this.edgeSignificance = edgeSignificance;
    }

    public void setEdgeCorrelation(BinaryMetric edgeCorrelation) {
        this.edgeCorrelation = edgeCorrelation;
    }

    //JiaoJiao Add Start for adaptive parameter setting
    public double getBinaryRespectiveSignificance(int fromIndex, int toIndex) {
        return actBinaryRespectiveSignificance[fromIndex][toIndex];
    }

    public void setBinaryRespectiveSignificance() {
        int numberOfNodes = getNumberOfInitialNodes();
        actBinaryRespectiveSignificance = new double[numberOfNodes][numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++) {
            for (int j = 0; j < numberOfNodes; j++) {
                actBinaryRespectiveSignificance[i][j] = 0.0;
            }
        }
        for (int fromIndex = 0; fromIndex < numberOfNodes; fromIndex++) {
            for (int toIndex = 0; toIndex < numberOfNodes; toIndex++) {
                if (fromIndex != toIndex) {
                    double sigRef = getBinarySignificance(fromIndex, toIndex);
                    if (sigRef > 0.0) {
                        // accumulate all outgoing significances of source node, and
                        // all incoming significances of target node, respectively.
                        double sigSourceOutAcc = 0.0;
                        double sigTargetInAcc = 0.0;
                        for (int i = getNumberOfInitialNodes() - 1; i >= 0; i--) {
                            if (i != fromIndex) { // ignore self-loops in calculation
                                sigSourceOutAcc += getBinarySignificance(fromIndex, i);
                            }
                            if (i != toIndex) { // ignore self-loops in calculation
                                sigTargetInAcc += getBinarySignificance(i, toIndex);
                            }
                        }
                        // relative importance is the product of the relative significances
                        // within the source's outgoing and the target's incoming links
                        double relativeImportance = ((sigRef / sigSourceOutAcc) + (sigRef / sigTargetInAcc)) / 2.0;
                        actBinaryRespectiveSignificance[fromIndex][toIndex] = relativeImportance;
                    }
                }
            }
        }
    }

    //JiaoJiao Add End

    public void updateLogEvent(int index, XEvent event) {
        assert (index >= 0 || index < events.size());
        events.remove(index);
        events.add(index, event);
    }

    public Object clone() {
        MutableFuzzyGraph clone = new MutableFuzzyGraph(this.nodeSignificance, this.edgeSignificance,
                this.edgeCorrelation, log, true);
        int cNumOfInitialNodes = clone.numberOfInitialNodes;
        for (int x = 0; x < cNumOfInitialNodes; x++) {

            for (int y = 0; y < cNumOfInitialNodes; y++) {
                clone.actBinarySignificance[x][y] = this.actBinarySignificance[x][y];
                clone.actBinaryCorrelation[x][y] = this.actBinaryCorrelation[x][y];
            }
        }
        HashMap<FMNode, FMNode> cloneNodeMap = new HashMap<FMNode, FMNode>();

        for (int i = 0; i < cNumOfInitialNodes; i++) {
            String nodeLabel = this.getPrimitiveNode(i).getLabel();
            FMNode node = new FMNode(clone, i, nodeLabel);
            clone.addNode(node, i); //add the new primitiveNodes and the new nodeAliasMap
            cloneNodeMap.put(this.getPrimitiveNode(i), node);
        }
        for (int i = 0; i < this.nodeAliasMap.length; i++) {
            if (this.nodeAliasMap[i] == null) {
                clone.nodeAliasMap[i] = null;
                clone.graphElementRemoved(clone.primitiveNodes[i]);
            }
        }
        // restore clustering
        for (FMClusterNode cluster : this.clusterNodes) {
            String clusterNodeLabel = cluster.getLabel();
            FMClusterNode cCluster = new FMClusterNode(clone, cluster.getIndex(), clusterNodeLabel);
            clone.addClusterNode(cCluster);
            for (FMNode primitive : cluster.getPrimitives()) {
                FMNode victim = cloneNodeMap.get(primitive);
                cCluster.add(victim);
                clone.nodeAliasMap[victim.getIndex()] = cCluster;
                clone.graphElementRemoved(victim);
            }
        }
        // restore attributes
        if (attributes != null) {
            clone.attributes = new HashMap<String, String>(attributes);
        } else {
            clone.attributes = null;
        }

        //restrore edges
        for (int x = 0; x < numberOfInitialNodes; x++) {
            for (int y = 0; y < numberOfInitialNodes; y++) {
                FMNode source = clone.nodeAliasMap[x];
                FMNode target = clone.nodeAliasMap[y];
                if (source == null || target == null || (source.equals(target) && source instanceof FMClusterNode)) {
                    continue; // do not draw cluster self-references
                }
                if ((x == y) && clone.actBinarySignificance[x][y] < 0.001) {
                    continue;
                }
                if (clone.actBinarySignificance[x][y] > 0.0) {
                    clone.addEdge(source, target, clone.actBinarySignificance[x][y], clone.actBinaryCorrelation[x][y]);
                }
            }
        }
        clone.metrics = new MetricsRepository();
        //restore startNodes
        for (FMNode orgStartNode : this.startNodes) {
            FMNode startNode = cloneNodeMap.get(orgStartNode);
            clone.startNodes.add(startNode);
        }
        //restore endNodes
        for (FMNode orgEndNode : this.endNodes) {
            FMNode endNode = cloneNodeMap.get(orgEndNode);
            clone.endNodes.add(endNode);
        }

        return clone;
    }

    //
    //	public Object clone() {
    //		MutableFuzzyGraph clone = null;
    //		try {
    //			clone = (MutableFuzzyGraph) super.clone();			
    //		} catch (CloneNotSupportedException e) {
    //			e.printStackTrace();
    //			return null;
    //		}
    //		clone = new MutableFuzzyGraph(this.nodeSignificance,
    //				  this.edgeSignificance,
    //				  this.edgeCorrelation,
    //				  log,
    //				  false);
    //		//delete the elements of graph first in order to redraw the graph
    //		for (FMEdgeImpl edge : clone.fmEdges) {
    //			clone.graphElementRemoved(edge);
    //		}
    //		for (FMClusterNode node : clone.clusterNodes) {
    //			clone.graphElementRemoved(node);
    //		}
    //		for (FMNode node : clone.primitiveNodes) {
    //			if (node != null) {
    //				clone.graphElementRemoved(node);
    //			}
    //		}
    //		
    //		
    //		clone.nodeSignificance = new UnaryMetric("Node significance", "Node significance", this.nodeSignificance);
    //		clone.edgeSignificance = new BinaryMetric("Edge significance", "Edge significance", this.edgeSignificance);
    //		clone.edgeCorrelation = new BinaryMetric("Edge correlation", "Edge correlation", this.edgeCorrelation);
    //		//clone.events = FuzzyMinerLog.getLogEvents(this.events.getLog());
    //		clone.events = new FMLogEvents(log);
    //		for (int i = 0; i < this.events.size(); i++) {
    //			XEvent event = this.events.get(i);
    //			/*
    //			 * clone.events.add(new XEvent(event.getModelElementName(),
    //			 * event.getEventType(), event.getOccurrenceCount()));
    //			 */
    //			clone.events.add((XEvent) (event.clone()));
    //		}
    //		clone.primitiveNodes = new FMNode[clone.numberOfInitialNodes];
    //		clone.nodeAliasMap = new FMNode[clone.numberOfInitialNodes];
    //		clone.clusterNodes = new ArrayList<FMClusterNode>();
    //		clone.actBinarySignificance = new double[clone.numberOfInitialNodes][clone.numberOfInitialNodes];
    //		clone.actBinaryCorrelation = new double[clone.numberOfInitialNodes][clone.numberOfInitialNodes];
    //		for (int x = 0; x < clone.numberOfInitialNodes; x++) {
    //			
    //			for (int y = 0; y < numberOfInitialNodes; y++) {
    //				clone.actBinarySignificance[x][y] = this.actBinarySignificance[x][y];
    //				clone.actBinaryCorrelation[x][y] = this.actBinaryCorrelation[x][y];
    //			}
    //		}
    //		HashMap<FMNode, FMNode> cloneNodeMap = new HashMap<FMNode, FMNode>();
    //		
    //		for (int i = 0; i < clone.numberOfInitialNodes; i++) {
    //			String nodeLabel = this.getPrimitiveNode(i).getLabel();
    //			FMNode node = new FMNode(clone, i, nodeLabel);
    //			clone.addNode(node, i);  //add the new primitiveNodes and the new nodeAliasMap
    //			cloneNodeMap.put(this.getPrimitiveNode(i), node);
    //		}
    //		for (int i = 0; i < this.nodeAliasMap.length; i++) {
    //			if (this.nodeAliasMap[i] == null) {
    //				clone.nodeAliasMap[i] = null;
    //				clone.graphElementRemoved(clone.primitiveNodes[i]);
    //			}
    //		}
    //		// restore clustering
    //		int clusterIndex = numberOfInitialNodes + 1;
    //		for (FMClusterNode cluster : this.clusterNodes) {
    //			String clusterNodeLabel = cluster.getLabel();
    //			FMClusterNode cCluster = new FMClusterNode(clone, cluster.getIndex(), clusterNodeLabel);
    //			clone.addClusterNode(cCluster);
    //			clusterIndex++;
    //			for (FMNode primitive : cluster.getPrimitives()) {
    //				FMNode victim = cloneNodeMap.get(primitive);
    //				cCluster.add(victim);
    //				clone.nodeAliasMap[victim.getIndex()] = cCluster;
    //				clone.graphElementRemoved(victim);
    //			}
    //		}
    //		// restore attributes
    //		if (attributes != null) {
    //			clone.attributes = new HashMap<String, String>(attributes);
    //		} else {
    //			clone.attributes = null;
    //		}
    //		
    //		//restrore edges
    //		clone.fmEdges = new HashSet<FMEdgeImpl>();
    //		for (int x = 0; x < numberOfInitialNodes; x++) {
    //			for (int y = 0; y < numberOfInitialNodes; y++) {
    //				FMNode source = clone.nodeAliasMap[x];
    //				FMNode target = clone.nodeAliasMap[y];
    //				if (source == null || target == null || (source.equals(target) && source instanceof FMClusterNode)) {
    //					continue; // do not draw cluster self-references
    //				}
    //				if ((x == y) && clone.actBinarySignificance[x][y] < 0.001) {
    //					continue;
    //				}
    //				//	System.out.println("The size of Nodes after tranform is " + nodeAliasMap.length);
    //				//System.out.println("The Nodes after tranform is " + nodeAliasMap.toString());
    //				if (clone.actBinarySignificance[x][y] > 0.0) {
    //
    //					clone.addEdge(source, target, clone.actBinarySignificance[x][y], clone.actBinaryCorrelation[x][y]);
    //				}
    //			}
    //		}
    //		//assert(this.equals(clone));
    //		//restore log
    //		clone.log = (XLog)(this.log.clone());
    //		clone.metrics = new MetricsRepository();
    //		//restore startNodes
    //		clone.startNodes = new HashSet<FMNode>();
    //		for(FMNode orgStartNode: this.startNodes){
    //			FMNode startNode = cloneNodeMap.get(orgStartNode);
    //			clone.startNodes.add(startNode);
    //		}
    //		//restore endNodes
    //		clone.endNodes = new HashSet<FMNode>();
    //		for(FMNode orgEndNode: this.endNodes){
    //			FMNode endNode = cloneNodeMap.get(orgEndNode);
    //			clone.endNodes.add(endNode);
    //		}
    //		clone.abstractnodeGraphMap = new HashMap<FMNode, MutableFuzzyGraph>();
    //		
    //		return clone;
    //	}

    public boolean equals000(Object o) {
        if (o instanceof MutableFuzzyGraph) {
            MutableFuzzyGraph other = (MutableFuzzyGraph) o;
            if (getNumberOfInitialNodes() != other.getNumberOfInitialNodes()) {
                return false;
            }
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).equals(other.getLogEvents().get(i)) == false) {
                    return false;
                }
                //if(getNodeMappedTo(i) != other.getNodeMappedTo(i)) {
                if ((getNodeMappedTo(i) == null) == (other.getNodeMappedTo(i) != null)) {
                    return false;
                }
            }
            for (int x = 0; x < numberOfInitialNodes; x++) {
                for (int y = 0; y < numberOfInitialNodes; y++) {
                    if (getBinarySignificance(x, y) != other.getBinarySignificance(x, y)) {
                        return false;
                    }
                    if (getBinaryCorrelation(x, y) != other.getBinaryCorrelation(x, y)) {
                        return false;
                    }
                }
            }
            List<FMClusterNode> myClusters = getClusterNodes();
            List<FMClusterNode> otherClusters = other.getClusterNodes();
            if (myClusters.size() != otherClusters.size()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#getAttribute
     * (java.lang.String)
     */
    public String getAttribute(String key) {
        if (attributes != null) {
            return attributes.get(key);
        } else {
            return null;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#getAttributeKeys
     * ()
     */
    public Collection<String> getAttributeKeys() {
        if (attributes != null) {
            return attributes.keySet();
        } else {
            return new HashSet<String>();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#resetAttributes
     * ()
     */
    public void resetAttributes() {
        attributes = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.processmining.plugins.fuzzymodel.miner.graph.FuzzyGraph#setAttribute
     * (java.lang.String, java.lang.String)
     */
    public void setAttribute(String key, String value) {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        attributes.put(key, value);
    }

    //implement the inherit methods from AbstractDirectedGraph
    public synchronized void removeEdge(@SuppressWarnings("rawtypes") DirectedGraphEdge edge) {
        if (edge instanceof FMEdgeImpl) {
            fmEdges.remove(edge);
        } else {
            assert (false);
        }
        graphElementRemoved(edge);
    }

    public synchronized FMEdge<FMNode, FMNode> removeEdge(FMNode source, FMNode target) {
        return removeFromEdges(source, target, fmEdges);

    }

    public synchronized FMEdge<FMNode, FMNode> removeEdge(FMEdge<? extends FMNode, ? extends FMNode> edge) {
        return removeFromEdges(edge.getSource(), edge.getTarget(), fmEdges);
    }

    public synchronized void removeNode(DirectedGraphNode node) {

        if (node instanceof FMClusterNode) {
            removeFMClusterNode((FMClusterNode) node);
        } else if (node instanceof FMNode) {
            removeFMNode((FMNode) node);
        } else {
            assert (false);
        }
    }

    public synchronized boolean removeFMClusterNode(FMClusterNode cluster) {
        HashSet<FMNode> primitives = new HashSet<FMNode>(cluster.getPrimitives());
        for (FMNode primitive : primitives) {
            removeFMNode(primitive);
        }
        removeSurroundingEdges(cluster);
        boolean isRemoved = clusterNodes.remove(cluster);
        //if (isRemoved) {
        graphElementRemoved(cluster);
        //}
        return isRemoved;
    }

    public synchronized void removeFMNode(FMNode node) {

        //First delete node and its ingoing and outgoing edges in JGraph
        removeSurroundingEdges(node);
        graphElementRemoved(node);
        // primitive node, remove permanently
        int removeIndex = node.getIndex();
        // remove from metrics
        this.nodeSignificance.setMeasure(removeIndex, 0.0);
        for (int x = 0; x < this.numberOfInitialNodes; x++) {
            this.edgeSignificance.setMeasure(x, removeIndex, 0.0);
            this.edgeSignificance.setMeasure(removeIndex, x, 0.0);
            this.edgeCorrelation.setMeasure(x, removeIndex, 0.0);
            this.edgeCorrelation.setMeasure(removeIndex, x, 0.0);
            this.actBinarySignificance[x][removeIndex] = 0.0;
            this.actBinarySignificance[removeIndex][x] = 0.0;
            this.actBinaryCorrelation[x][removeIndex] = 0.0;
            this.actBinaryCorrelation[removeIndex][x] = 0.0;
        }
        // remove from clusters
        for (int i = 0; i < this.clusterNodes.size(); i++) {
            FMClusterNode cluster = this.clusterNodes.get(i);
            if (cluster.contains(node)) {
                cluster.remove(node);
                //removeNode(node);
                if (cluster.size() == 0) {
                    clusterNodes.remove(i);
                    //removeFMClusterNode(cluster);
                    i--;
                }
            }
        }
        // mark as deleted in node alias map
        nodeAliasMap[removeIndex] = null;
        //	removeSurroundingEdges(node);		
    }

    //	public synchronized void removeFMNode(FMNode fmNodeElement) {
    //
    //		//First delete node and its ingoing and outgoing edges in JGraph
    //		removeSurroundingEdges(fmNodeElement);
    //		graphElementRemoved(fmNodeElement);
    //
    //		//Second delete the node from Fuzzy Graph
    //		int removeIndex = fmNodeElement.getIndex();
    //		// remove from metrics
    //		this.nodeSignificance = removeIndex(this.nodeSignificance, removeIndex);
    //		this.edgeSignificance = removeIndex(this.edgeSignificance, removeIndex);
    //		this.edgeCorrelation = removeIndex(this.edgeCorrelation, removeIndex);
    //		// remove from actual edge mappings
    //		this.actBinarySignificance = removeIndex(this.actBinarySignificance, removeIndex);
    //		this.actBinaryCorrelation = removeIndex(this.actBinaryCorrelation, removeIndex);
    //		// remove from clusters
    //		for (int i = 0; i < this.clusterNodes.size(); i++) {
    //			FMClusterNode cluster = this.clusterNodes.get(i);
    //			if (cluster.contains(fmNodeElement)) {
    //				cluster.remove(fmNodeElement);
    //			}
    //		}
    //
    //		// remove from index, log events and adjust number of nodes
    //		numberOfInitialNodes--;
    //
    //		FMNode[] nPrimitiveNodes = new FMNode[primitiveNodes.length - 1];
    //		FMNode[] nNodeAliasMap = new FMNode[nodeAliasMap.length - 1];
    //		int nIndex = 0;
    //		for (int index = 0; index < primitiveNodes.length; index++) {
    //			if (index != removeIndex) {
    //				nPrimitiveNodes[nIndex] = primitiveNodes[index];
    //				nNodeAliasMap[nIndex] = nodeAliasMap[index];
    //				nIndex++;
    //			}
    //		}
    //		primitiveNodes = nPrimitiveNodes;
    //		nodeAliasMap = nNodeAliasMap;
    //		// adjust node indices
    //		for (FMNode n : primitiveNodes) {
    //			if (n.getIndex() >= removeIndex) {
    //				n.setIndex(n.getIndex() - 1);
    //			}
    //		}
    //		events.remove(removeIndex);
    //
    //	}

    @Override
    protected MutableFuzzyGraph getEmptyClone() {
        //return new MutablFuzzyGraph(getLabel());

        return null;
    }

    protected Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(
            DirectedGraph<FMNode, FMEdge<? extends FMNode, ? extends FMNode>> graph) {
        HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

        for (FMNode node : graph.getNodes()) {
            mapping.put(node, new FMNode((MutableFuzzyGraph) graph, node.getIndex(), node.getLabel()));
        }
        getAttributeMap().clear();
        AttributeMap map = graph.getAttributeMap();
        for (String key : map.keySet()) {
            getAttributeMap().put(key, map.get(key));
        }
        return mapping;
    }

}