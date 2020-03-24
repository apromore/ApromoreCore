package org.apromore.processmining.models.jgraph.elements;

import java.util.Map;

import org.apromore.jgraph.graph.CellView;

public interface ProMGraphElement {

	CellView getView();

	void updateViewsFromMap();

	Map getAttributes();
}
