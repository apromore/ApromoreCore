package org.apromore.processmining.models.jgraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.event.GraphLayoutCacheEvent;
import org.apromore.jgraph.event.GraphLayoutCacheListener;
import org.apromore.jgraph.event.GraphModelEvent;
import org.apromore.jgraph.event.GraphModelListener;
import org.apromore.jgraph.event.GraphSelectionEvent;
import org.apromore.jgraph.event.GraphSelectionListener;
import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.ConnectionSet;
import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.graph.DefaultPort;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.jgraph.graph.GraphLayoutCache;
import org.apromore.jgraph.graph.ParentMap;
import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;
import org.apromore.processmining.models.cast.Cast;
import org.apromore.processmining.models.connections.GraphLayoutConnection;
import org.apromore.processmining.models.connections.GraphLayoutConnection.Listener;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.AttributeMapOwner;
import org.apromore.processmining.models.graphbased.Expandable;
import org.apromore.processmining.models.graphbased.ExpansionListener;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.graphbased.directed.BoundaryDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.DirectedGraph;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge.MultipleSources;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge.MultipleTargets;
import org.apromore.processmining.models.jgraph.elements.Cleanable;
import org.apromore.processmining.models.jgraph.elements.ProMGraphCell;
import org.apromore.processmining.models.jgraph.elements.ProMGraphEdge;
import org.apromore.processmining.models.jgraph.elements.ProMGraphElement;
import org.apromore.processmining.models.jgraph.elements.ProMGraphPort;
import org.apromore.processmining.models.jgraph.factory.ProMCellViewFactory;

public class ProMJGraph extends JGraph implements GraphModelListener, GraphLayoutCacheListener, GraphSelectionListener,
		Cleanable, ExpansionListener, Listener { //ScalableComponent,  {

	private static final long serialVersionUID = -8477633603192312230L;

	public static final String PIPVIEWATTRIBUTE = "signalPIPView";

	private final ProMGraphModel model;
	private final Map<DirectedGraphNode, ProMGraphCell> nodeMap = new HashMap<>();
	private final Map<BoundaryDirectedGraphNode, ProMGraphPort> boundaryNodeMap = new HashMap<>();
	private final Map<DirectedGraphEdge<?, ?>, ProMGraphEdge> edgeMap = new HashMap<>();
	private final Map<DirectedGraphEdge<?, ?>, Collection<ProMGraphElement>> multiEdgeMap = new HashMap<>();

	private JGraphLayout layout;

	private final ViewSpecificAttributeMap viewSpecificAttributes;

	private final boolean isPIP;

	private final GraphLayoutConnection layoutConnection;

	public ProMJGraph(ProMGraphModel model, ViewSpecificAttributeMap viewSpecificAttributes,
			GraphLayoutConnection layoutConnection) {
		this(model, false, viewSpecificAttributes, layoutConnection);
	}

	public ProMJGraph(ProMGraphModel model, boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes,
			GraphLayoutConnection layoutConnection) {
		super(model, new GraphLayoutCache(model, new ProMCellViewFactory(isPIP, viewSpecificAttributes), true));
		this.layoutConnection = layoutConnection;
		layoutConnection.addListener(this);
		getGraphLayoutCache().setShowsInvisibleEditedCells(false);
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;

		getGraphLayoutCache().setMovesChildrenOnExpand(true);
		// Strange: setResizesParentsOnCollapse has to be set to FALSE!
		getGraphLayoutCache().setResizesParentsOnCollapse(false);
		getGraphLayoutCache().setMovesParentsOnCollapse(true);

		this.model = model;

		setHighlightColor(Color.ORANGE);
		setLockedHandleColor(Color.RED);

		setAntiAliased(true);
		setDisconnectable(false);
		setConnectable(false);
		setGridEnabled(false);
		setDoubleBuffered(true);
		setSelectionEnabled(!isPIP);
		setMoveBelowZero(false);
		setPortsVisible(true);
		setPortsScaled(true);

		DirectedGraph<?, ?> net = model.getGraph();

		List<DirectedGraphNode> todo = new ArrayList<DirectedGraphNode>(net.getNodes());
		List<Object> toInsert = new ArrayList<Object>();
		while (!todo.isEmpty()) {
			Iterator<DirectedGraphNode> it = todo.iterator();
			while (it.hasNext()) {
				DirectedGraphNode n = it.next();
				if (n instanceof BoundaryDirectedGraphNode) {
					DirectedGraphNode m = ((BoundaryDirectedGraphNode) n).getBoundingNode();
					if ((m != null) && !nodeMap.containsKey(m)) {
						// first make sure the bounding node is added
						continue;
					} else if (m != null) {
						// add as port
						addPort((BoundaryDirectedGraphNode) n, m);
						it.remove();
						continue;
					}
				}
				if (n instanceof ContainableDirectedGraphElement) {
					ContainingDirectedGraphNode c = Cast.<ContainableDirectedGraphElement>cast(n).getParent();
					if ((c != null) && !nodeMap.containsKey(c)) {
						// if parent is not added yet, then continue
						continue;
					} else if (c == null) {
						toInsert.add(addCell(n));
					} else {
						addCell(n);
					}
				} else {
					toInsert.add(addCell(n));
				}

				it.remove();
			}
		}

		//		getGraphLayoutCache().insert(toInsert.toArray());

		//		getGraphLayoutCache().insert(boundaryNodeMap.values().toArray());
		for (DirectedGraphEdge<?, ?> e : net.getEdges()) {
			if (e instanceof ContainableDirectedGraphElement) {
				ContainingDirectedGraphNode m = Cast.<ContainableDirectedGraphElement>cast(e).getParent();
				if (m == null) {
					if (e instanceof MultipleSources<?> || e instanceof MultipleTargets<?>) {
						toInsert.addAll(addMultiEdge(e));
					} else {
						toInsert.add(addEdge(e));
					}
				} else {
					if (e instanceof MultipleSources<?> || e instanceof MultipleTargets<?>) {
						addMultiEdge(e);
					} else {
						addEdge(e);

					}
				}
			} else {
				if (e instanceof MultipleSources<?> || e instanceof MultipleTargets<?>) {

					toInsert.addAll(addMultiEdge(e));
				} else {
					toInsert.add(addEdge(e));
				}
			}
		}
		getGraphLayoutCache().insert(toInsert.toArray());
		// Add the listeners, only AFTER copying the graph.

		registerAsListener();
		//layoutConnection.getExpansionListeners().add(this);

		if (!isPIP) {
			//addMouseListener(new JGraphFoldingManager(layoutConnection));
		}

		//ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * Returns the <code>GraphModel</code> that is providing the data.
	 * 
	 * @return the model that is providing the data
	 */
	public ProMGraphModel getModel() {
		return (ProMGraphModel) graphModel;
	}

	public void cleanUp() {

		List<Cleanable> cells = new ArrayList<Cleanable>(nodeMap.values());
		cells.addAll(boundaryNodeMap.values());
		cells.addAll(edgeMap.values());
		// multiEdgeMap elements are contained in other maps as well.

		getGraphLayoutCache().removeCells(cells.toArray());

		for (Cleanable cell : cells) {
			cell.cleanUp();
		}

		model.removeGraphModelListener(this);
		removeGraphSelectionListener(this);
		getGraphLayoutCache().removeGraphLayoutCacheListener(this);
		ToolTipManager.sharedInstance().unregisterComponent(this);

		removeAll();
		setVisible(false);
		setEnabled(false);
		setLayout(null);
		setGraphLayoutCache(null);

	}

	private ProMGraphCell addCell(DirectedGraphNode node) {
		ProMGraphCell cell = new ProMGraphCell(node, model, layoutConnection);

		cell.addPort();
		((DefaultPort) cell.getChildAt(0)).setUserObject("default port");

		// getting the size
		nodeMap.put(node, cell);

		// if the node is contained in another node, its cell must be contained in the cell of that node
		if (node instanceof ContainableDirectedGraphElement) {
			ContainingDirectedGraphNode parent = Cast.<ContainableDirectedGraphElement>cast(node).getParent();
			if (parent != null) {
				ProMGraphCell parentNode = nodeMap.get(parent);
				parentNode.add(cell);
				cell.setParent(parentNode);
			}
		}

		return cell;
	}

	private ProMGraphPort addPort(BoundaryDirectedGraphNode node, DirectedGraphNode boundingNode) {
		ProMGraphCell cell = nodeMap.get(boundingNode);
		ProMGraphPort port = cell.addPort(new Point2D.Float(10, 10), node);
		assert (port.getParent() == cell);

		boundaryNodeMap.put(node, port);

		return port;
	}

	private ProMGraphEdge addEdge(DirectedGraphEdge<?, ?> e) {
		ProMGraphEdge edge = new ProMGraphEdge(e, model, layoutConnection);
		// For now, assume a single port.
		ProMGraphPort srcPort;
		if ((e.getSource() instanceof BoundaryDirectedGraphNode)
				&& ((BoundaryDirectedGraphNode) e.getSource()).getBoundingNode() != null) {
			srcPort = boundaryNodeMap.get(e.getSource());
		} else {
			srcPort = (ProMGraphPort) nodeMap.get(e.getSource()).getChildAt(0);
		}
		ProMGraphPort tgtPort;
		if ((e.getTarget() instanceof BoundaryDirectedGraphNode)
				&& ((BoundaryDirectedGraphNode) e.getTarget()).getBoundingNode() != null) {
			tgtPort = boundaryNodeMap.get(e.getTarget());
		} else {
			tgtPort = (ProMGraphPort) nodeMap.get(e.getTarget()).getChildAt(0);
		}

		edge.setSource(srcPort);
		edge.setTarget(tgtPort);

		srcPort.addEdge(edge);
		tgtPort.addEdge(edge);

		edgeMap.put(e, edge);

		// if the edge is contained in a node, its cell must be contained in the cell of that node
		if (e instanceof ContainableDirectedGraphElement) {
			ContainingDirectedGraphNode parent = Cast.<ContainableDirectedGraphElement>cast(e).getParent();
			if (parent != null) {
				nodeMap.get(parent).add(edge);
				assert (edge.getParent() == nodeMap.get(parent));
			}
		}

		return edge;
	}

	private Collection<ProMGraphElement> addMultiEdge(final DirectedGraphEdge<?, ?> e) {
		assert e instanceof MultipleSources || e instanceof MultipleTargets;

		Collection<ProMGraphElement> elements = new ArrayList<>();

		Collection<DirectedGraphNode> sources;
		if (e instanceof MultipleSources<?>) {
			sources = new ArrayList<>(((MultipleSources<?>) e).getSources().size());
			sources.addAll(((MultipleSources<?>) e).getSources());
		} else {
			sources = new ArrayList<>(1);
			sources.add(e.getSource());
		}

		Collection<DirectedGraphNode> targets;
		if (e instanceof MultipleTargets<?>) {
			targets = new ArrayList<>(((MultipleTargets<?>) e).getTargets().size());
			targets.addAll(((MultipleTargets<?>) e).getTargets());
		} else {
			targets = new ArrayList<>(1);
			targets.add(e.getTarget());
		}
		List<DirectedGraphElement> tempGraphElements = new ArrayList<DirectedGraphElement>(1 + targets.size()
				+ sources.size());
		//layoutConnection.setMultiGraphElements(e, tempGraphElements);
		multiEdgeMap.put(e, elements);

		if (targets.size() == 1 && sources.size() == 1) {
			elements.add(addEdge(e));
			return elements;
		}

		final DirectedGraphNodeForMultiGraph intermediate = new DirectedGraphNodeForMultiGraph(e);

//		layoutConnection.setSize(intermediate,
//				intermediate.getAttributeMap().get(AttributeMap.SIZE, new Dimension(50, 50)));
//		layoutConnection.setPortOffset(
//				intermediate,
//				intermediate.getAttributeMap().get(AttributeMap.PORTOFFSET,
//						new Point2D.Double(GraphConstants.PERMILLE / 2, GraphConstants.PERMILLE / 2)));

		tempGraphElements.add(intermediate);
		elements.add(addCell(intermediate));

		for (final DirectedGraphNode source : sources) {
			DirectedGraphEdge<?, ?> tmpEdge = new DirectedGraphEdgeForMultiGraph(source, intermediate, e);
			tempGraphElements.add(tmpEdge);
			ProMGraphEdge edge = addEdge(tmpEdge);// new ProMGraphEdge(tmpEdge, model, layoutConnection);
			elements.add(edge);

			ProMGraphPort srcPort;
			if ((tmpEdge.getSource() instanceof BoundaryDirectedGraphNode)
					&& ((BoundaryDirectedGraphNode) tmpEdge.getSource()).getBoundingNode() != null) {
				srcPort = boundaryNodeMap.get(tmpEdge.getSource());
			} else {
				srcPort = (ProMGraphPort) nodeMap.get(tmpEdge.getSource()).getChildAt(0);
			}
			ProMGraphPort tgtPort = (ProMGraphPort) nodeMap.get(tmpEdge.getTarget()).getChildAt(0);

			edge.setSource(srcPort);
			edge.setTarget(tgtPort);

			srcPort.addEdge(edge);
			tgtPort.addEdge(edge);

			// if the edge is contained in a node, its cell must be contained in the cell of that node
			if (e instanceof ContainableDirectedGraphElement) {
				ContainingDirectedGraphNode parent = Cast.<ContainableDirectedGraphElement>cast(e).getParent();
				if (parent != null) {
					nodeMap.get(parent).add(edge);
					assert (edge.getParent() == nodeMap.get(parent));
				}
			}

		}

		for (final DirectedGraphNode target : targets) {
			DirectedGraphEdge<?, ?> tmpEdge = new DirectedGraphEdgeForMultiGraph(intermediate, target, e);
			tempGraphElements.add(tmpEdge);

			ProMGraphEdge edge = addEdge(tmpEdge);//ProMGraphEdge edge = new ProMGraphEdge(tmpEdge, model, layoutConnection);
			elements.add(edge);

			ProMGraphPort srcPort = (ProMGraphPort) nodeMap.get(tmpEdge.getSource()).getChildAt(0);

			ProMGraphPort tgtPort;
			if ((tmpEdge.getTarget() instanceof BoundaryDirectedGraphNode)
					&& ((BoundaryDirectedGraphNode) tmpEdge.getTarget()).getBoundingNode() != null) {
				tgtPort = boundaryNodeMap.get(tmpEdge.getTarget());
			} else {
				tgtPort = (ProMGraphPort) nodeMap.get(tmpEdge.getTarget()).getChildAt(0);
			}
			edge.setSource(srcPort);
			edge.setTarget(tgtPort);

			srcPort.addEdge(edge);
			tgtPort.addEdge(edge);

			// if the edge is contained in a node, its cell must be contained in the cell of that node
			if (e instanceof ContainableDirectedGraphElement) {
				ContainingDirectedGraphNode parent = Cast.<ContainableDirectedGraphElement>cast(e).getParent();
				if (parent != null) {
					nodeMap.get(parent).add(edge);
					assert (edge.getParent() == nodeMap.get(parent));
				}
			}
		}

		return elements;
	}

	public void update(Object... elements) {
		updateElements(Arrays.asList(elements));
	}

	public void update(Set<?> elements) {
		updateElements(elements);
	}

	private void updateElements(Collection<?> elements) {

		// For each updated element, find the corresponding view of the corresponding cell and copy the
		// attributes that matter, i.e. size/position/points.

		//The order in which cells, ports and edges are added matters:
		//Cells first, ports second and edges third (because ports are attached to cells and edges to ports.)
		Vector<ProMGraphElement> cellsToAdd = new Vector<ProMGraphElement>();
		Vector<ProMGraphElement> portsToAdd = new Vector<ProMGraphElement>();
		Vector<ProMGraphElement> edgesToAdd = new Vector<ProMGraphElement>();
		Vector<CellView> cellViewsToAdd = new Vector<CellView>();
		Vector<CellView> portViewsToAdd = new Vector<CellView>();
		Vector<CellView> edgeViewsToAdd = new Vector<CellView>();

		for (Object element : elements) {
			if ((element instanceof BoundaryDirectedGraphNode) ? ((BoundaryDirectedGraphNode) element)
					.getBoundingNode() != null : false) {
				ProMGraphPort cell = boundaryNodeMap.get(element);
				if (cell != null) {
					// An update on a cell that does not exist in the view should not be done.
					portsToAdd.add(cell);
					portViewsToAdd.add(cell.getView());
				}
			} else if (element instanceof DirectedGraphNode) {
				ProMGraphCell cell = nodeMap.get(element);
				if (cell != null) {
					// An update on a cell that does not exist in the view should not be done.
					cellsToAdd.add(cell);
					cellViewsToAdd.add(cell.getView());
				}
			} else if (element instanceof MultipleSources || element instanceof MultipleTargets) {
				Collection<ProMGraphElement> cells = multiEdgeMap.get(element);
				if (cells != null) {
					// An update on a cell that does not exist in the view should not be done.
					for (ProMGraphElement cell : cells) {
						if (cell instanceof ProMGraphCell) {
							// An update on a cell that does not exist in the view should not be done.
							cellsToAdd.add(cell);
							cellViewsToAdd.add(cell.getView());
						} else {
							edgesToAdd.add(cell);
							edgeViewsToAdd.add(cell.getView());
						}
					}
				}
			} else if (element instanceof DirectedGraphEdge<?, ?>) {
				ProMGraphEdge cell = edgeMap.get(element);
				if (cell != null) {
					// An update on a cell that does not exist in the view should not be done.
					edgesToAdd.add(cell);
					edgeViewsToAdd.add(cell.getView());
				}
			} else if (element instanceof DirectedGraph<?, ?>) {
				// graph has changed
			} else {
				assert (false);
			}
		}

		Vector<CellView> views = cellViewsToAdd;
		views.addAll(portViewsToAdd);
		views.addAll(edgeViewsToAdd);
		Vector<ProMGraphElement> cells = cellsToAdd;
		cells.addAll(portsToAdd);
		cells.addAll(edgesToAdd);
		Rectangle2D oldBound = GraphLayoutCache.getBounds(views.toArray(new CellView[0]));
		for (ProMGraphElement cell : cells) {
			cell.updateViewsFromMap();
		}
		if (oldBound != null) {
			Rectangle2D.union(oldBound, GraphLayoutCache.getBounds(views.toArray(new CellView[0])), oldBound);
		}
		//		repaint(oldBound.getBounds());
		getGraphLayoutCache().cellViewsChanged(views.toArray(new CellView[0]));
		// HV: Refresh the graph to show the changes.
		this.refresh();
	}

	public String toString() {
		return model.toString();
	}

	public void graphChanged(GraphModelEvent e) {
		handleChange(e.getChange());
		changeHandled();
//		for (UpdateListener l : updateListeners) {
//			l.updated();
//		}
	}

	/**
	 * Might be overridden to signal that a change was handled
	 */
	protected void changeHandled() {
		//layoutConnection.updated();
	}

	private void handleChange(GraphLayoutCacheEvent.GraphLayoutCacheChange change) {
		// A change originated in from the graph. This needs to be reflected in
		// the layoutConnection (if applicable)
		synchronized (model) {
			boolean signalChange = false;
			Object[] changed = change.getChanged();

			Set<AttributeMapOwner> changedOwners = new HashSet<AttributeMapOwner>();
			Set<ProMGraphEdge> edges = new HashSet<ProMGraphEdge>();
			for (Object o : changed) {
				if (o instanceof ProMGraphCell) {
					// handle a change for a cell
					ProMGraphCell cell = (ProMGraphCell) o;

					DirectedGraphNode node = cell.getNode();

					Rectangle2D rect;
					if (change.getSource() instanceof ProMGraphModel) {
						rect = GraphConstants.getBounds(cell.getAttributes());
					} else {
						rect = cell.getView().getBounds();
					}

					if (handleNodeChange(cell, node, rect)) {
						changedOwners.add(node);
						signalChange = true;
					}
				}
				if (o instanceof ProMGraphEdge) {
					edges.add((ProMGraphEdge) o);
				}
			}
			for (ProMGraphEdge cell : edges) {
				// handle a change for a cell
				DirectedGraphEdge<?, ?> edge = cell.getEdge();

				List<?> points;
				if (change.getSource() instanceof ProMGraphModel) {
					points = GraphConstants.getPoints(cell.getAttributes());
				} else {
					points = cell.getView().getPoints();
				}

				if (handleEdgeChange(cell, edge, points)) {
					changedOwners.add(edge);
					signalChange = true;
				}
			}
			if (signalChange && !isPIP) {
				//layoutConnection.updatedAttributes(changedOwners.toArray(new AttributeMapOwner[0]));
			}
		}
	}

	private boolean handleNodeChange(ProMGraphCell cell, DirectedGraphNode node, Rectangle2D rect) {
		boolean changed = false;

		//		// get the view's bounds and put them in the attributemap
		//		Rectangle2D rect = cell.getView().getBounds();
		//		rect = GraphConstants.getBounds(cell.getAttributes());

		if (rect != null) {
			// SIZE
			Dimension2D size = new Dimension((int) rect.getWidth(), (int) rect.getHeight());
			//changed |= layoutConnection.setSize(node, size);

			// POSITION
			Point2D pos = new Point2D.Double(rect.getX(), rect.getY());
			//changed |= layoutConnection.setPosition(node, pos);

		}

		return changed;
	}

	private boolean handleEdgeChange(ProMGraphEdge cell, DirectedGraphEdge<?, ?> edge, List<?> points) {
		boolean changed = false;

		List<Point2D> list = new ArrayList<Point2D>(3);
		if (points != null) {
			for (int i = 1; i < points.size() - 1; i++) {
				Point2D point = (Point2D) points.get(i);
				list.add(new Point2D.Double(point.getX(), point.getY()));
			}
		}
		//changed |= layoutConnection.setEdgePoints(edge, list);

		return changed;
	}

	public void graphLayoutCacheChanged(GraphLayoutCacheEvent e) {
		handleChange(e.getChange());
		changeHandled();
//		for (UpdateListener l : updateListeners) {
//			l.updated();
//		}
	}

	public void valueChanged(GraphSelectionEvent e) {
		// Ignore for now
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		// get first cell under the mouse pointer's position
		Object cell = getFirstCellForLocation(event.getX(), event.getY());

		ViewSpecificAttributeMap map = getViewSpecificAttributes();

		// determine what is being pointed to by the mouse pointer
		if (cell instanceof ProMGraphCell) {
			// mouse is pointing to a node or a port on that node
			ProMGraphCell c = ((ProMGraphCell) cell);
			return map.get(c.getNode(), AttributeMap.TOOLTIP, c.getLabel());
		} else if (cell instanceof ProMGraphEdge) {
			ProMGraphEdge e = ((ProMGraphEdge) cell);
			return map.get(e.getEdge(), AttributeMap.TOOLTIP, e.getLabel());
		}

		return null;
	}

	// returns the original origin
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void repositionToOrigin() {

		//		facade.translateCells(facade.getVertices(), 100.0, 100.0);
		//		facade.translateCells(facade.getEdges(), 100.0, 100.0);
		//		getGraphLayoutCache().edit(facade.createNestedMap(true, false));
		/*
		 * Second, pull everything back to (2,2). Works like a charm, even when
		 * a hack...
		 */

		//TODO Doesn't correctly handle collapsed nodes.

		JGraphFacade facade = new JGraphFacade(this);
		facade.setIgnoresHiddenCells(true);
		facade.setIgnoresCellsInGroups(false);
		facade.setIgnoresUnconnectedCells(false);

		double x = facade.getGraphOrigin().getX();
		double y = facade.getGraphOrigin().getY();

		ArrayList cells = new ArrayList();
		cells.addAll(facade.getVertices());
		cells.addAll(facade.getEdges());
		facade.translateCells(cells, 2.0 - x, 2.0 - y);
		Map map = facade.createNestedMap(true, false);
		getGraphLayoutCache().edit(map);

	}

	public DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode>> getProMGraph() {
		return model.getGraph();
	}

	private void registerAsListener() {
		model.addGraphModelListener(this);
		addGraphSelectionListener(this);
		getGraphLayoutCache().addGraphLayoutCacheListener(this);
	}

	public int hashCode() {
		return model.getGraph().hashCode();
	}

	public JGraphLayout getUpdateLayout() {
		return layout;
	}

//	public GraphLayoutConnection getLayoutConnection() {
//		return layoutConnection;
//	}

	public void setUpdateLayout(JGraphLayout layout) {
		this.layout = layout;
	}

	public ViewSpecificAttributeMap getViewSpecificAttributes() {
		return viewSpecificAttributes;
	}

	public void nodeCollapsed(Expandable source) {
		ProMGraphCell cell = (nodeMap.get(source));
		// Before calling collapse, set the size of cell to the collapsed size

		Point2D pos = null; //layoutConnection.getPosition(source);
		if (pos == null) {
			pos = new Point2D.Double(10, 10);
		}

		Dimension size = source.getCollapsedSize();

		Rectangle2D bounds = GraphConstants.getBounds(cell.getAttributes());
		bounds.setFrame(pos.getX(), pos.getY(), size.getWidth(), size.getHeight());
		getGraphLayoutCache().collapse(DefaultGraphModel.getDescendants(model, new Object[] { cell }).toArray());
	}

	public void nodeExpanded(Expandable source) {
		ProMGraphCell cell = (nodeMap.get(source));
		getGraphLayoutCache().expand(DefaultGraphModel.getDescendants(model, new Object[] { cell }).toArray());
	}

	public JComponent getComponent() {
		// for interface Scalable
		return this;
	}

//	Set<UpdateListener> updateListeners = new HashSet<UpdateListener>();

//	public void addUpdateListener(UpdateListener listener) {
//		updateListeners.add(listener);
//	}
//
//	public void removeUpdateListener(UpdateListener listener) {
//		updateListeners.remove(listener);
//	}

	public void layoutConnectionUpdated(AttributeMapOwner... owners) {
		update((Object[]) owners);
	}

}

class Change implements GraphModelEvent.GraphModelChange {

	private final Collection<Object> added;
	private final Collection<Object> removed;
	private final Collection<Object> changed;
	private final ProMGraphModel source;
	private final Rectangle2D dirtyRegion;

	public Change(ProMGraphModel source, Collection<Object> added, Collection<Object> removed,
			Collection<Object> changed, Rectangle2D dirtyRegion) {
		this.source = source;
		this.added = added;
		this.removed = removed;
		this.changed = changed;
		this.dirtyRegion = dirtyRegion;

	}

	public ConnectionSet getConnectionSet() {
		return null;
	}

	public ParentMap getParentMap() {
		return null;
	}

	public ConnectionSet getPreviousConnectionSet() {
		return null;
	}

	public ParentMap getPreviousParentMap() {
		return null;
	}

	public CellView[] getViews(GraphLayoutCache view) {
		return null;
	}

	public void putViews(GraphLayoutCache view, CellView[] cellViews) {

	}

	public Map<?, ?> getAttributes() {
		return null;
	}

	public Object[] getChanged() {
		return changed.toArray();
	}

	public Object[] getContext() {
		return null;
	}

	public Rectangle2D getDirtyRegion() {
		return dirtyRegion;
	}

	public Object[] getInserted() {
		return added.toArray();
	}

	public Map<?, ?> getPreviousAttributes() {
		return null;
	}

	public Object[] getRemoved() {
		return removed.toArray();
	}

	public Object getSource() {
		return source;
	}

	public void setDirtyRegion(Rectangle2D dirty) {

	}

}