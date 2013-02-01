package org.apromore.toolbox.similaritySearch.common.similarity;

import org.apromore.graph.canonical.CPFNode;
import org.apromore.toolbox.similaritySearch.common.NodePair;
import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


public class SemanticSimilarity {

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticSimilarity.class);

    /**
     * Find the Semantics Similarity.
     * @param v1 the first node
     * @param v2 he second node
     * @param labelThreshold the label threshold
     * @return the similarity
     */
    public double getSemanticSimilarity(CPFNode v1, CPFNode v2, double labelThreshold) {
        AssingmentProblem assingmentProblem = new AssingmentProblem();

        Set<CPFNode> v1NonGWParents = GraphUtil.getNonGatewayParentNodes(v1);
        Set<CPFNode> v2NonGWParents = GraphUtil.getNonGatewayParentNodes(v2);
        Set<CPFNode> v1NonGWChildren = GraphUtil.getNonGatewayChildrenNodes(v1);
        Set<CPFNode> v2NonGWChildren = GraphUtil.getNonGatewayChildrenNodes(v2);

        Set<NodePair> parentMappings = assingmentProblem.getMappingsNodes(v1NonGWParents, v2NonGWParents, labelThreshold,
                Settings.getEnglishStemmer(), 0);
        Set<NodePair> childMappings = assingmentProblem.getMappingsNodes(v1NonGWChildren, v2NonGWChildren, labelThreshold,
                Settings.getEnglishStemmer(), 0);

        return (double) (parentMappings.size() + childMappings.size())
             / (double) (Math.max(v1NonGWParents.size(), v2NonGWParents.size()) + Math.max(v1NonGWChildren.size(), v2NonGWChildren.size()));
    }
}
