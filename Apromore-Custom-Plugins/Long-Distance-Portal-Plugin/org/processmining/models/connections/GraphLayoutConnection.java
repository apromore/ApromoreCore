package org.processmining.models.connections;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jgraph.graph.GraphConstants;
import org.processmining.framework.connections.DynamicConnection;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMapOwner;
import org.processmining.models.graphbased.Expandable;
import org.processmining.models.graphbased.ExpansionListener;
import org.processmining.models.graphbased.ExpansionListener.ListenerList;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphElement;

public class GraphLayoutConnection extends AbstractConnection implements DynamicConnection {

	/**
	 * A List<java.awt.geom.Point2D> of points, which are the inner points of
	 * the spline.
	 * 
	 */
	protected final static String EDGEPOINTS = "edgepoints";

	public boolean setEdgePoints(AttributeMapOwner owner, List<Point2D> edgepoints) {
		return map.putViewSpecific(owner, EDGEPOINTS, edgepoints);
	}

	public List<Point2D> getEdgePoints(AttributeMapOwner owner) {
		return map.get(owner, EDGEPOINTS, Collections.<Point2D>emptyList());
	}

	/**
	 * a get on size returns a java.awt.geom.Dimension2D.
	 */
	protected static final String SIZE = "size";

	public boolean setSize(AttributeMapOwner owner, Dimension2D size) {
		return map.putViewSpecific(owner, SIZE, size);
	}

	public Dimension getSize(AttributeMapOwner owner) {
		return map.get(owner, SIZE, new Dimension(100, 100));
	}

	/**
	 * a get on size returns a java.awt.geom.Point2D.
	 */
	protected static final String POSITION = "position";

	public boolean setPosition(AttributeMapOwner owner, Point2D position) {
		return map.putViewSpecific(owner, POSITION, position);
	}

	public Point2D getPosition(AttributeMapOwner owner) {
		return map.get(owner, POSITION, null);
	}

	protected static final String COLLAPSED = "collapsed";

	public boolean collapse(AttributeMapOwner owner) {
		return map.putViewSpecific(owner, COLLAPSED, false);
	}

	public boolean expand(AttributeMapOwner owner) {
		return map.putViewSpecific(owner, COLLAPSED, true);
	}

	public boolean isCollapsed(AttributeMapOwner owner) {
		return map.get(owner, COLLAPSED, false);
	}

	protected final static String PORTOFFSET = "portOffset";

	public boolean setPortOffset(AttributeMapOwner owner, Point2D position) {
		return map.putViewSpecific(owner, PORTOFFSET, position);
	}

	public Point2D getPortOffset(AttributeMapOwner owner) {
		return map.get(owner, PORTOFFSET, new Point2D.Double(GraphConstants.PERMILLE / 2, GraphConstants.PERMILLE / 2));
	}

	public static interface Listener {
		public void layoutConnectionUpdated(AttributeMapOwner... owners);
	}

	public static final String GRAPH = "graph";
	private boolean layedOut = false;
	private final ViewSpecificAttributeMap map;
	private transient ExpansionListener.ListenerList expListeners = new ExpansionListener.ListenerList();
	private transient List<Listener> listeners = new ArrayList<Listener>();

	public GraphLayoutConnection(GraphLayoutConnection cloneFrom) {
		super(cloneFrom.getLabel());
		this.map = cloneFrom.map.createClone();
		put(GRAPH, cloneFrom.get(GRAPH));
		this.layedOut = cloneFrom.layedOut;

	}

	public GraphLayoutConnection(DirectedGraph<?, ?> graph) {
		super("Layout information for " + graph.getLabel());
		this.map = new ViewSpecificAttributeMap();
		put(GRAPH, graph);

		for (AttributeMapOwner node : graph.getNodes()) {
			setSize(node, node.getAttributeMap().get(AttributeMap.SIZE, new Dimension(50, 50)));
			setPortOffset(
					node,
					node.getAttributeMap().get(AttributeMap.PORTOFFSET,
							new Point2D.Double(GraphConstants.PERMILLE / 2, GraphConstants.PERMILLE / 2)));
		}

	}

	public DirectedGraph<?, ?> getGraph() {
		return (DirectedGraph<?, ?>) get(GRAPH);
	}

	//	public ViewSpecificAttributeMap getMap() {
	//		return map;
	//	}

	public void setLayedOut(boolean layedOut) {
		this.layedOut = layedOut;
		updatedAttributes();
	}

	public boolean isLayedOut() {
		return layedOut;
	}

	public void expandAll() {
		List<Expandable> updated = new ArrayList<Expandable>();
		for (AttributeMapOwner owner : map.keySet()) {
			if (owner instanceof Expandable) {
				updated.add((Expandable) owner);
			}
		}
		expandAll(updated);
	}

	public void collapseAll() {
		List<Expandable> updated = new ArrayList<Expandable>();
		for (AttributeMapOwner owner : map.keySet()) {
			if (owner instanceof Expandable) {
				updated.add((Expandable) owner);
			}
		}
		collapseAll(updated);
	}

	public void expandAll(Collection<Expandable> toExpand) {
		for (Expandable owner : toExpand) {
			expand(owner, false);
		}
		updatedAttributes(toExpand.toArray(new Expandable[0]));
	}

	public void collapseAll(Collection<Expandable> toCollapse) {
		for (Expandable owner : toCollapse) {
			collapse(owner, false);
		}
		updatedAttributes(toCollapse.toArray(new Expandable[0]));
	}

	public void expand(Expandable expandable) {
		expand(expandable, true);
	}

	public void collapse(Expandable expandable) {
		collapse(expandable, true);
	}

	void expand(Expandable expandable, boolean update) {
		expListeners.fireNodeExpanded(expandable);
	}

	void collapse(Expandable expandable, boolean update) {
		expListeners.fireNodeCollapsed(expandable);
	}

	public ExpansionListener.ListenerList getExpansionListeners() {
		return expListeners;
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void updatedAttributes(AttributeMapOwner... owners) {
		for (Listener l : listeners) {
			l.layoutConnectionUpdated(owners);
		}
		super.updated();
	}

	private Object readResolve() {
		this.expListeners = new ListenerList();
		listeners = new ArrayList<Listener>();
		return this;

	}

	protected static final String MULTIGRAPHELEMENTS = "multigraphelements";

	public boolean setMultiGraphElements(AttributeMapOwner owner, List<DirectedGraphElement> tempGraphElements) {
		return map.putViewSpecific(owner, MULTIGRAPHELEMENTS, tempGraphElements);
	}

	public List<DirectedGraphElement> getMultiGraphElements(AttributeMapOwner owner) {
		return map.get(owner, MULTIGRAPHELEMENTS, Collections.<DirectedGraphElement>emptyList());
	}

}
