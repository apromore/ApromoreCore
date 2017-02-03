package org.apromore.service.logvisualizer.fuzzyminer;

import com.jgraph.layout.JGraphFacade;
import org.jgraph.JGraph;
import org.jgraph.event.*;
import org.jgraph.graph.*;
import org.processmining.framework.util.Cast;
import org.processmining.framework.util.Cleanable;
import org.processmining.framework.util.ui.scalableview.ScalableComponent;
import org.processmining.models.graphbased.AttributeMapOwner;
import org.processmining.models.graphbased.Expandable;
import org.processmining.models.graphbased.ExpansionListener;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.*;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.factory.ProMCellViewFactory;

import javax.swing.*;
import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class ProMJGraph extends JGraph implements GraphModelListener, GraphLayoutCacheListener, GraphSelectionListener,
        Cleanable, ExpansionListener, ScalableComponent {

    private static final long serialVersionUID = -8477633603192312230L;

    private final ProMGraphModel model;
    private final Map<DirectedGraphNode, ProMGraphCell> nodeMap = new HashMap<DirectedGraphNode, ProMGraphCell>();
    private final Map<BoundaryDirectedGraphNode, ProMGraphPort> boundaryNodeMap = new HashMap<BoundaryDirectedGraphNode, ProMGraphPort>();
    private final Map<DirectedGraphEdge<?, ?>, ProMGraphEdge> edgeMap = new HashMap<DirectedGraphEdge<?, ?>, ProMGraphEdge>();

    private final ViewSpecificAttributeMap viewSpecificAttributes;



    public ProMJGraph(ProMGraphModel model, ViewSpecificAttributeMap viewSpecificAttributes) {
        this(model, false, viewSpecificAttributes);
    }

    public ProMJGraph(ProMGraphModel model, boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
        super(model, new GraphLayoutCache(model, new ProMCellViewFactory(isPIP, viewSpecificAttributes), true));

        getGraphLayoutCache().setShowsInvisibleEditedCells(false);
        this.viewSpecificAttributes = viewSpecificAttributes;

        getGraphLayoutCache().setMovesChildrenOnExpand(true);
        // Strange: setResizesParentsOnCollapse has to be set to FALSE!
        getGraphLayoutCache().setResizesParentsOnCollapse(false);
        getGraphLayoutCache().setMovesParentsOnCollapse(true);

        this.model = model;

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
                    toInsert.add(addEdge(e));
                } else {
                    addEdge(e);
                }
            } else {
                toInsert.add(addEdge(e));
            }
        }
        // Add the listeners, only AFTER copying the graph.

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
        getGraphLayoutCache().removeCells(cells.toArray());

        for (Cleanable cell : cells) {
            cell.cleanUp();
        }

        model.removeGraphModelListener(this);
        removeGraphSelectionListener(this);
        getGraphLayoutCache().removeGraphLayoutCacheListener(this);

        removeAll();
        setVisible(false);
        setEnabled(false);
        setLayout(null);
        setGraphLayoutCache(null);

    }

    private ProMGraphCell addCell(DirectedGraphNode node) {
        ProMGraphCell cell = new ProMGraphCell(node, model);

        // TODO: This is probably wrong.
        // cell.addPort(new Point2D.Double(0,0));
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
        ProMGraphPort port = cell.addPort(node);
        assert (port.getParent() == cell);

        boundaryNodeMap.put(node, port);

        return port;
    }

    private ProMGraphEdge addEdge(DirectedGraphEdge<?, ?> e) {
        ProMGraphEdge edge = new ProMGraphEdge(e, model);
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

        for (ProMGraphElement cell : cells) {
            cell.updateViewsFromMap();
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
        for (UpdateListener l : updateListeners) {
            l.updated();
        }
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


                    if (handleNodeChange(cell, node)) {
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
        }
    }

    private boolean handleNodeChange(ProMGraphCell cell, DirectedGraphNode node) {
        boolean changed = false;

        //		// get the view's bounds and put them in the attributemap
        //		Rectangle2D rect = cell.getView().getBounds();
        //		rect = GraphConstants.getBounds(cell.getAttributes());

        return changed;
    }

    private boolean handleEdgeChange(ProMGraphEdge cell, DirectedGraphEdge<?, ?> edge, List<?> points) {
        boolean changed = false;

        return changed;
    }

    public void graphLayoutCacheChanged(GraphLayoutCacheEvent e) {
        handleChange(e.getChange());
        for (UpdateListener l : updateListeners) {
            l.updated();
        }
    }

    public void valueChanged(GraphSelectionEvent e) {
        // Ignore for now
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

    public int hashCode() {
        return model.getGraph().hashCode();
    }

    public ViewSpecificAttributeMap getViewSpecificAttributes() {
        return viewSpecificAttributes;
    }

    public void nodeCollapsed(Expandable source) {
        ProMGraphCell cell = (nodeMap.get(source));
        // Before calling collapse, set the size of cell to the collapsed size

        getGraphLayoutCache().collapse(DefaultGraphModel.getDescendants(model, new Object[] { cell }).toArray());
    }

    public void nodeExpanded(Expandable source) {
        ProMGraphCell cell = (nodeMap.get(source));
        getGraphLayoutCache().expand(DefaultGraphModel.getDescendants(model, new Object[] { cell }).toArray());
    }

    Set<UpdateListener> updateListeners = new HashSet<UpdateListener>();

    @Override
    public JComponent getComponent() {
        return null;
    }

    public void addUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }

    public void removeUpdateListener(UpdateListener listener) {
        updateListeners.remove(listener);
    }

    public void layoutConnectionUpdated(AttributeMapOwner... owners) {
        update((Object[]) owners);
    }

}