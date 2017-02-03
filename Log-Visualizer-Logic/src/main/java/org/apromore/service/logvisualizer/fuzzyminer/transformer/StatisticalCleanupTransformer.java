package org.apromore.service.logvisualizer.fuzzyminer.transformer;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apromore.service.logvisualizer.fuzzyminer.model.FMNode;
import org.apromore.service.logvisualizer.fuzzyminer.model.FuzzyGraph;
import org.apromore.service.logvisualizer.fuzzyminer.model.MutableFuzzyGraph;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 3/2/17.
 */
public class StatisticalCleanupTransformer extends FuzzyGraphTransformer {

    SummaryStatistics nodeSignificanceStats;
    SummaryStatistics linkSignificanceStats;
    SummaryStatistics linkCorrelationStats;

    /**
     *
     */
    public StatisticalCleanupTransformer() {
        super("Statistical cleanup transformer");
        nodeSignificanceStats = new SummaryStatistics();
        linkSignificanceStats = new SummaryStatistics();
        linkCorrelationStats = new SummaryStatistics();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.processmining.mining.fuzzymining.graph.transform.FuzzyGraphTransformer
     * #transform(org.processmining.mining.fuzzymining.graph.FuzzyGraph)
     */
    public void transform(MutableFuzzyGraph graph) {
        createStatistics(graph);
        int numberOfNodes = graph.getNumberOfInitialNodes();
        // clean up node significance
        double nodeMaxDeviation = nodeSignificanceStats.getStandardDeviation();// * 2.0;
        double maxNodeSig = nodeSignificanceStats.getMean() + nodeMaxDeviation;
        double minNodeSig = nodeSignificanceStats.getMean() - nodeMaxDeviation;
        FMNode node;
        int attNodeCounter = 0;
        int delNodeCounter = 0;
        for (int i = 0; i < numberOfNodes; i++) {
            node = graph.getNodeMappedTo(i);
            if (node != null) {
                if (node.getSignificance() > maxNodeSig) {
                    // attenuate node significance
                    node.setSignificance(maxNodeSig);
                    attNodeCounter++;
                } else if (node.getSignificance() < minNodeSig) {
                    // remove node from graph
                    graph.setNodeAliasMapping(i, null);
                    delNodeCounter++;
                }
            }
        }
        // clean up link significance
        double linkMaxDeviation = linkSignificanceStats.getStandardDeviation();// * 2.0;
        double minLinkSig = linkSignificanceStats.getMean() - linkMaxDeviation;
        double maxLinkSig = linkSignificanceStats.getMean() + linkMaxDeviation;
        double curSig;
        int attLinkCounter = 0;
        int delLinkCounter = 0;
        for (int x = 0; x < numberOfNodes; x++) {
            for (int y = 0; y < numberOfNodes; y++) {
                curSig = graph.getBinarySignificance(x, y);
                if (curSig > maxLinkSig) {
                    // attenuate link significance
                    graph.setBinarySignificance(x, y, maxLinkSig);
                    attLinkCounter++;
                } else if (curSig < minLinkSig) {
                    // remove connection (incl. correlation entry)
                    graph.setBinarySignificance(x, y, 0.0);
                    graph.setBinaryCorrelation(x, y, 0.0);
                    delLinkCounter++;
                }
            }
        }
    }

    protected void createStatistics(FuzzyGraph graph) {
        nodeSignificanceStats.clear();
        linkSignificanceStats.clear();
        linkCorrelationStats.clear();
        double nodeSig, linkSig, linkCor;
        for (int x = 0; x < graph.getNumberOfInitialNodes(); x++) {
            nodeSig = graph.getNodeMappedTo(x).getSignificance();
            if (nodeSig > 0.0) {
                nodeSignificanceStats.addValue(nodeSig);
            }
            for (int y = 0; y < graph.getNumberOfInitialNodes(); y++) {
                linkSig = graph.getBinarySignificance(x, y);
                linkCor = graph.getBinaryCorrelation(x, y);
                if (linkSig > 0.0) {
                    linkSignificanceStats.addValue(linkSig);
                    linkCorrelationStats.addValue(linkCor);
                }
            }
        }
    }

    public void printStatistics() {
        System.out.println("Node significance statistics: ");
        print(nodeSignificanceStats);
        System.out.println("Link significance statistics: ");
        print(linkSignificanceStats);
        System.out.println("Link Correlation statistics: ");
        print(linkCorrelationStats);
    }

    protected void print(SummaryStatistics stats) {
        System.out.println("min: " + stats.getMin());
        System.out.println("max: " + stats.getMax());
        System.out.println("mean: " + stats.getMean());
        System.out.println("geometr. mean: " + stats.getGeometricMean());
        System.out.println("standard deviation: " + stats.getStandardDeviation());
        System.out.println("variance: " + stats.getVariance());
    }

}
