package org.apromore.service.logvisualizer.fuzzyminer.model;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.binary.BinaryMetric;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.unary.UnaryMetric;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FMLogEvents;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public interface FuzzyGraph extends DirectedGraph<FMNode, FMEdge<? extends FMNode, ? extends FMNode>> {

    //primitive node
    public void addNode(int index);

    public void removeFMNode(FMNode fmNodeElement);

    //Cluster node
    public void addClusterNode(FMClusterNode cluster);

    public boolean removeFMClusterNode(FMClusterNode cluster);

    //edge
    public FMEdgeImpl addEdge(FMEdgeImpl edge);

    public FMEdgeImpl addEdge(FMNode source, FMNode target, double significance, double correlation);

    public FMEdge<FMNode, FMNode> removeEdge(FMNode source, FMNode target);

    public FMEdge<FMNode, FMNode> removeEdge(FMEdge<? extends FMNode, ? extends FMNode> edge);

    /**
     * Returns the set of nodes (primitive and cluster nodes alike) which are
     * contained in the simplified Fuzzy Graph.
     *
     * @return
     */
    //public abstract Set<FMNode> getNodes();

    /**
     * Returns the set of edges (i.e., binary relations) which are contained in
     * the simplified Fuzzy Graph in the format Set<FMEdge>.
     *
     * @return
     */
    //public abstract Set<FMEdge<? extends FMNode,? extends FMNode>> getEdges();

    /**
     * Returns the set of edges (i.e., binary relations) which are contained in
     * the simplified Fuzzy Graph in the format of FMEdges instead of
     * Set<FMEdge>.
     *
     * @return
     */
    public abstract Set<FMEdgeImpl> getEdgeImpls();

    public abstract void setEdgeImpls();

    /**
     * Helper method: maps an audit trail entry to its corresponding index in
     * the log's LogEvents collection. Use this method for indexing your events
     * when interfacing with a Fuzzy Graph.
     * <p>
     * <b>Important:</b> If the audit trail entry could not be mapped to an
     * event class in the original log, a value of <code>-1</code> will be
     * returned, which indicates an error on your part!
     *
     * @param ate
     *            any audit trail entry from a suitable log
     * @return The index of the event class in the log's LogEvents collection.
     */
    public abstract int getEventClassIndex(XEvent ate);

    /**
     * Helper method: maps an event name / type combination, i.e. an event
     * class, to its corresponding index in the log's LogEvents collection. Use
     * this method for indexing your events when interfacing with a Fuzzy Graph.
     * <p>
     * <b>Important:</b> If the data supplied could not be mapped to an event
     * class in the original log, a value of <code>-1</code> will be returned,
     * which indicates an error on your part!
     *
     * @param element
     *            The event name to be mapped
     * @param type
     *            The event type to be mapped
     * @return The index of the event class in the log's LogEvents collection.
     */
    public abstract int getEventClassIndex(String element, String type);

    /**
     * Returns the number of initial nodes in the non-simplified graph (i.e.,
     * the number of event classes in the log)
     *
     * @return
     */
    public abstract int getNumberOfInitialNodes();

    /**
     * Returns the unary node significance metric used for defining this fuzzy
     * graph
     *
     * @return
     */
    public abstract UnaryMetric getNodeSignificanceMetric();

    /**
     * Returns the binary edge significance metric used for defining this fuzzy
     * graph
     *
     * @return
     */
    public abstract BinaryMetric getEdgeSignificanceMetric();

    /**
     * Returns the binary edge correlation metric used for defining this fuzzy
     * graph
     *
     * @return
     */
    public abstract BinaryMetric getEdgeCorrelationMetric();

    /**
     * Returns a copy of the LogEvents contained in the log, which has been
     * mined with the Fuzzy Miner. Used for convenience, i.e. for referencing
     * nodes from the graph.
     *
     * @return
     */
    public abstract FMLogEvents getLogEvents();

    /**
     * Returns an indexed primitive, i.e. initial, node in the graph. Note that
     * this node may no longer be contained in the graph, or may be clustered.
     * <p>
     * <b>Note:</b> You may use one of the <code>getEventClassIndex()</code>
     * methods provided in this interface for retrieving indices for addressing
     * in this method.
     *
     * @param index
     *            Index of the requested node, as found in the log's LogEvents
     *            collection.
     * @return the Node object corresponding to the given index.
     */
    public abstract FMNode getPrimitiveNode(int index);

    /**
     * Returns the Node which has been mapped to the given event index, as found
     * in the mined log's LogEvents collection. This may return one of three
     * alternatives:
     * <p>
     * <ul>
     * <li>a) a primitive <code>Node</code> object, if the event class has not
     * been simplified</li>
     * <li>b) <code>null</code>, if the event class has been removed from the
     * model (i.e., abstracted from)</li>
     * <li>c) a higher-level <code>ClusterNode</code> object, if the respective
     * event class has been clustered (i.e., aggregated)</li>. Note that in this
     * case, the returned <code>ClusterNode</code> object will represent
     * multiple elementary nodes, i.e. event classes!</li>
     * </ul>
     * <p>
     * <b>Note:</b> You may use one of the <code>getEventClassIndex()</code>
     * methods provided in this interface for retrieving indices for addressing
     * in this method.
     *
     * @param index
     *            The index of the event class, to which the corresponding node
     *            is requested (as found in the mined log's LogEvents
     *            collection)
     * @return The node representing the indexed event class (may be a
     *         ClusterNode), or <code>null</code>, if the event class has been
     *         abstracted from.
     */
    public abstract FMNode getNodeMappedTo(int index);

    /**
     * Returns the list of higher-level cluster nodes, which are contained in
     * this simplified Fuzzy Graph.
     *
     * @return
     */
    public abstract List<FMClusterNode> getClusterNodes();

    /**
     * Returns the aggregate binary significance between two event classes, as
     * specified by their indices. This will return the value which has been
     * used for simplification of this graph.
     * <p>
     * <b>Note:</b> You may use one of the <code>getEventClassIndex()</code>
     * methods provided in this interface for retrieving indices for addressing
     * in this method.
     *
     * @param fromIndex
     *            Index of the originating event class (source event)
     * @param toIndex
     *            Index of the terminal event class (target event)
     * @return A value within [0, 1], representing the binary significance of
     *         the relation between the indexed event classes / nodes.
     */
    public abstract double getBinarySignificance(int fromIndex, int toIndex);

    /**
     * Returns the aggregate binary correlation between two event classes, as
     * specified by their indices. This will return the value which has been
     * used for simplification of this graph.
     * <p>
     * <b>Note:</b> You may use one of the <code>getEventClassIndex()</code>
     * methods provided in this interface for retrieving indices for addressing
     * in this method.
     *
     * @param fromIndex
     *            Index of the originating event class (source event)
     * @param toIndex
     *            Index of the terminal event class (target event)
     * @return A value within [0, 1], representing the binary correlation of the
     *         relation between the indexed event classes / nodes.
     */
    public abstract double getBinaryCorrelation(int fromIndex, int toIndex);

    public abstract void setBinarySignificance(int fromIndex, int toIndex, double value);

    public abstract void setBinaryCorrelation(int fromIndex, int toIndex, double value);

    /**
     * Returns the minimal significance of any node in the simplified graph (the
     * maximal value of node significance will always be normalized to 1.0).
     *
     * @return
     */
    public abstract double getMinimalNodeSignificance();

    /**
     * Returns all node significances in the simplified graph, sorted in
     * ascending numeral order, in an array of double values within [0, 1].
     *
     * @return
     */
    public abstract double[] getSortedNodeSignificances();

    public abstract String getAttribute(String key);

    public abstract void setAttribute(String key, String value);

    public abstract Collection<String> getAttributeKeys();

    public abstract void resetAttributes();

    /**
     * Returns a JPanel containing this Fuzzy Graph with navigation facilities,
     * ready for use in a user interface.
     *
     * @return
     * @throws Exception
     */
	/* public abstract JPanel getGraphPanel() throws Exception; */

    /**
     * Public clone method (as defined in Object). Must be implemented correctly
     * for each implementation to allow for deep copies.
     *
     * @return A deep clone of this FuzzyGraph object
     */
    public abstract Object clone();

}