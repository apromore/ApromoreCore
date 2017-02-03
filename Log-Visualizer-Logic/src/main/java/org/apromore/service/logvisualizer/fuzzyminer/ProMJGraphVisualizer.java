package org.apromore.service.logvisualizer.fuzzyminer;

import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.jgraph.ProMGraphModel;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class ProMJGraphVisualizer {

    protected ProMJGraphVisualizer() {
    }

    private static ProMJGraphVisualizer instance = null;

    public static ProMJGraphVisualizer instance() {
        if (instance == null) {
            instance = new ProMJGraphVisualizer();
        }
        return instance;
    }

    public ProMJGraph visualizeGraph(DirectedGraph<?, ?> graph) {
        return visualizeGraph(graph, new ViewSpecificAttributeMap());
    }

    private ProMJGraph visualizeGraph(DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map) {

        ProMGraphModel model = new ProMGraphModel(graph);
        ProMJGraph jgraph;
		/*
		 * Make sure that only a single ProMJGraph is created at every time.
		 * The underlying JGrpah code cannot handle creating multiple creations at the same time.
		 */
        synchronized (instance) {
            jgraph = new ProMJGraph(model, map);
        }


        return jgraph;

    }

}
