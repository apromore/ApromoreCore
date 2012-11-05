package org.apromore.clustering.hierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.tm.is.graph.SimpleGraph;
import nl.tue.tm.is.led.StringEditDistance;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.clustering.containment.ContainmentRelation;
import org.apromore.clustering.dissimilarity.DissimilarityCalc;
import org.apromore.clustering.dissimilarity.DissimilarityMatrix;
import org.apromore.clustering.dissimilarity.measure.GEDDissimCalc;
import org.apromore.dao.ClusteringDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ComposerService;
import org.apromore.service.helper.SimpleGraphWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("DissimilarityMatrix")
@Transactional(propagation = Propagation.REQUIRED)
public class HierarchyAwareDissimMatrixGenerator implements DissimilarityMatrix {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchyAwareDissimMatrixGenerator.class);

    @Autowired @Qualifier("ClusteringDao")
    private ClusteringDao clusteringDao;
    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;

    @Autowired @Qualifier("ContainmentRelation")
    private ContainmentRelation crel;
    @Autowired @Qualifier("ComposerService")
    private ComposerService composer;

    /* Fragment Id -> SimpleGraph object containing all nodes and edges of the fragment. */
    private Map<Integer, SimpleGraph> models = new HashMap<Integer, SimpleGraph>();
    private List<DissimilarityCalc> chain = new LinkedList<DissimilarityCalc>();
    private MultiKeyMap dissimmap = null;
    private double dissThreshold;

    long startedTime = 0;
    int nfrag = 0;
    int totalPairs = 0;
    int reportingInterval = 0;
    int processedPairs = 0;


    /**
     * Compute the Distance Similarities.
     */
    public void computeDissimilarity() {
        startedTime = System.currentTimeMillis();
        List<Integer> processedFragmentIds = new ArrayList<Integer>();
        dissimmap = new MultiKeyMap();
        nfrag = crel.getNumberOfFragments();
        totalPairs = nfrag * (nfrag + 1) / 2;
        reportingInterval = 0;
        processedPairs = 0;

        List<Integer> roots = crel.getRoots();
        for (int p = 0; p < roots.size(); p++) {
            List<Integer> h1 = crel.getHierarchy(roots.get(p));
            h1.removeAll(processedFragmentIds);

            computeIntraHierarchyGEDs(h1);
            if (p < roots.size() - 1) {
                for (int q = p + 1; q < roots.size(); q++) {
                    List<Integer> h2 = crel.getHierarchy(roots.get(q));
                    computeInterHierarchyGEDs(h1, h2);
                }
            }

            processedFragmentIds.addAll(h1);
        }

        // GED values are written to the database periodically after reporting period.
        // If there are left over geds we have to write them here.
        if (!dissimmap.isEmpty()) {
            clusteringDao.insertDistances(dissimmap);
            dissimmap.clear();
        }
    }

    /**
     * Compute Inter Hierarchy Distance.
     * @param h1 hierarchy 1
     * @param h2 hierarchy 2
     */
    private void computeInterHierarchyGEDs(List<Integer> h1, List<Integer> h2) {
        StringEditDistance.clearWordCache();

        for (Integer fid1 : h1) {
            for (Integer fid2 : h2) {
                computeDissim(fid1, fid2);
            }
        }
    }

    /**
     * Compute Intra Hierarchy Distances.
     * @param h1 hierarchy
     */
    private void computeIntraHierarchyGEDs(List<Integer> h1) {
        StringEditDistance.clearWordCache();

        for (int i = 0; i < h1.size() - 1; i++) {
            for (int j = i + 1; j < h1.size(); j++) {
                Integer fid1 = h1.get(i);
                Integer fid2 = h1.get(j);

                computeDissim(fid1, fid2);
            }
        }

    }

    public Double getDissimilarity(Integer frag1, Integer frag2) {
        Double result = (Double) dissimmap.get(frag1, frag2);
        if (result == null) {
            result = (Double) dissimmap.get(frag2, frag1);
        }
        return result;
    }

    public void addDissimCalc(DissimilarityCalc calc) {
        chain.add(calc);
    }

    public double compute(Integer frag1, Integer frag2) {
        SimpleGraph g1 = getSimpleGraph(frag1);
        SimpleGraph g2 = getSimpleGraph(frag2);
        double disim = 1.0;

        for (DissimilarityCalc calc : chain) {
            disim = calc.compute(g1, g2);
            if (calc instanceof GEDDissimCalc) {
                if (!((GEDDissimCalc) calc).isDeterministicGED() && !calc.isAboveThreshold(disim)) {
                    LOGGER.info("Incurs in at least one non-deterministic mapping (cf. Greedy algorithm) between " + frag1 + " and " + frag2);
                }
            }
            if (calc.isAboveThreshold(disim)) {
                disim = 1.0;
                break;
            }
        }
        return disim;
    }

    public void setDissThreshold(double dissThreshold) {
        this.dissThreshold = dissThreshold;
    }



    private void computeDissim(Integer fid1, Integer fid2) {
        int fid1Index = crel.getFragmentIndex(fid1);
        int fid2Index = crel.getFragmentIndex(fid2);

        if (!crel.areInContainmentRelation(fid1Index, fid2Index)) {
            double dissim = compute(fid1, fid2);

            if (dissim <= dissThreshold) {
                dissimmap.put(fid1, fid2, dissim);
            }
        }

        reportingInterval++;
        processedPairs++;
        if (reportingInterval == 1000) {
            long duration = (System.currentTimeMillis() - startedTime) / 1000;
            reportingInterval = 0;
            double percentage = (double) processedPairs * 100 / totalPairs;
            percentage = (double) Math.round((percentage * 1000)) / 1000d;
            //System.out.println(processedPairs + " processed out of " + totalPairs + " | " + percentage + " % completed. | Elapsed time: " + duration + " s");

            clusteringDao.insertDistances(dissimmap);
            dissimmap.clear();
        }
    }

    private SimpleGraph getSimpleGraph(Integer fragId) {
        SimpleGraph graph = models.get(fragId);

        if (graph == null) {
            try {
                FragmentVersion fv = fvDao.findFragmentVersion(fragId);
                Canonical cpfGraph = composer.compose(fv.getUri());
                graph = new SimpleGraphWrapper(cpfGraph);

                // NOTE: this was commented out in the svn version
                if (graph.getEdges() != null && graph.getEdges().size() < 100) {
                    models.put(fragId, graph);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new SimpleGraph(graph);
    }

}
