package org.apromore.service.logvisualizer.fuzzyminer;

import org.jgraph.graph.CellView;

import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public interface ProMGraphElement {

    CellView getView();

    void updateViewsFromMap();

    Map getAttributes();
}
