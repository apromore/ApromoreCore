package org.apromore.toolbox.similaritySearch.tools;

import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.similaritySearch.algorithms.FindModelSimilarity;
import org.apromore.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchForSimilarProcesses {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchForSimilarProcesses.class);

    public double findProcessesSimilarity(Canonical searchGraph, Canonical graph, String algortithm, double... param) {
        LOGGER.debug("SearchForSimilarProcesses.findProcessSimilarity(searchGraph, graph, algorithm, params...");
        FindModelSimilarity modelSimilarity = new FindModelSimilarity();

        GraphUtil.removeEmptyNodes(searchGraph);
        GraphUtil.removeEmptyNodes(graph);
        return modelSimilarity.findProcessSimilarity(searchGraph, graph, algortithm, param);
    }

}
