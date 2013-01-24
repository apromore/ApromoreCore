package org.apromore.toolbox.similaritySearch.common.similarity;

import org.apromore.graph.canonical.CPFNode;
import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeSimilarity {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeSimilarity.class);

    public double findNodeSimilarity(CPFNode n, CPFNode m, double labelTreshold) {
        LOGGER.debug("NodeSimilarity.findNodeSimilarity(n, m, labelThreshold)");

        SemanticSimilarity semanticSimilarity = new SemanticSimilarity();
        // functions or events -
        // compare the labels of these nodes
        // tokenize, stem and find the similarity score
        if (GraphUtil.isWorkNode(n) && GraphUtil.isWorkNode(m) && AssingmentProblem.canMap(n, m)) {
            return LabelEditDistance.edTokensWithStemming(m.getLabel(), n.getLabel(), Settings.STRING_DELIMETER,
                    Settings.getEnglishStemmer(), true);
        } else if (GraphUtil.isGatewayNode(n) && GraphUtil.isGatewayNode(m)) {
            // splits can not be merged with joins
            if ((GraphUtil.isSplitNode(n) && GraphUtil.isJoinNode(m)) || (GraphUtil.isSplitNode(m) && GraphUtil.isJoinNode(n))) {
                return 0;
            }
            return semanticSimilarity.getSemanticSimilarity(n, m, labelTreshold);
        }
        return 0;
    }
}
