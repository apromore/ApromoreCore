package org.apromore.processmining.models.jgraph.factory;

import java.util.ArrayList;
import java.util.List;

import org.apromore.jgraph.graph.DefaultCellViewFactory;
import org.apromore.jgraph.graph.Edge;
import org.apromore.jgraph.graph.EdgeView;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.jgraph.graph.Port;
import org.apromore.jgraph.graph.PortView;
import org.apromore.jgraph.graph.VertexView;
import org.apromore.processmining.models.cast.Cast;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.jgraph.elements.ProMGraphCell;
import org.apromore.processmining.models.jgraph.elements.ProMGraphEdge;
import org.apromore.processmining.models.jgraph.elements.ProMGraphPort;
import org.apromore.processmining.models.jgraph.views.JGraphEdgeView;
import org.apromore.processmining.models.jgraph.views.JGraphPortView;
import org.apromore.processmining.models.jgraph.views.JGraphShapeView;

public class ProMCellViewFactory extends DefaultCellViewFactory {

	private static final long serialVersionUID = -2424217390990685801L;
	private final boolean isPIP;
	private final ViewSpecificAttributeMap viewSpecificAttributes;

	public ProMCellViewFactory(boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;
	}

	@Override
	protected VertexView createVertexView(Object v) {
		ProMGraphCell cell = (ProMGraphCell) v;
		JGraphShapeView view = new JGraphShapeView(cell, isPIP, viewSpecificAttributes);
		cell.setView(view);
		return view;
	}

	@Override
	@Deprecated
	protected EdgeView createEdgeView(Edge e) {
		return createEdgeView((Object) e);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected EdgeView createEdgeView(Object e) {
		ProMGraphEdge cell = Cast.<ProMGraphEdge>cast(e);

		List list = new ArrayList(cell.getInternalPoints());
		list.add(0, cell.getSource().getView());
		list.add(cell.getTarget().getView());
		GraphConstants.setPoints(cell.getAttributes(), list);

		JGraphEdgeView view = new JGraphEdgeView(cell, isPIP, viewSpecificAttributes);
		cell.setView(view);
		return view;
	}

	@Override
	@Deprecated
	protected PortView createPortView(Port e) {
		return createPortView((Object) e);
	}

	@Override
	protected PortView createPortView(Object e) {
		ProMGraphPort cell = Cast.<ProMGraphPort>cast(e);
		JGraphPortView view = new JGraphPortView(cell, isPIP, viewSpecificAttributes);
		cell.setView(view);
		cell.updateViewsFromMap();
		return view;
	}

}
