/**
 *
 */
package org.apromore.toolbox.clustering.analyzers;

import java.util.List;
import java.util.Map;

import org.apromore.common.Constants;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.model.Cluster;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterSettings;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentDataObject;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryCluster;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryClusterer;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryGEDMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ClusterAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(InMemoryClusterer.class);

    @Autowired
    private InMemoryGEDMatrix gedMatrix;
    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fragmentVersionDao;

    private Map<String, Integer> fragmentSizes;


    public void loadFragmentSizes() {
        log.debug("Loading all fragment sizes from the database to memory...");
        fragmentSizes = fragmentVersionDao.getAllFragmentIdsWithSize();
        log.debug("Loading all fragment sizes complete.");
    }

    public Cluster analyzeCluster(InMemoryCluster c, ClusterSettings settings) throws RepositoryException {
        Cluster cd = new Cluster();
        cd.setClusterId(c.getClusterId());

        int sumOfFragmentSizes = 0;

        List<FragmentDataObject> fragments = c.getFragments();

        for (FragmentDataObject fragment : fragments) {
            fragment.setSize(fragmentSizes.get(fragment.getFragmentId()));
            sumOfFragmentSizes += fragment.getSize();
        }
        cd.setSize(fragments.size());

        float averageFragmentSize = sumOfFragmentSizes / fragments.size();
        cd.setAvgFragmentSize(round(averageFragmentSize));

        fillStandardizingDetails(cd, c, settings);

        return cd;
    }

    public void fillStandardizingDetails(Cluster cd, InMemoryCluster c, ClusterSettings settings) throws RepositoryException {
        double gedThreshold = settings.getMaxNeighborGraphEditDistance();
        try {
            String medoidFragmentId = "";
            int refactoringGain = 0;
            double standardizingEffot = 0;
//            double totalDistance = 0;
            double maxBenifitCostRatio = 0; // we want to maximise this
            double maxDistance = Double.MAX_VALUE; // we want to minimise this
//            Map<String, Integer> fSizes = new HashMap<String, Integer>();
            int sumOfSizes = 0;
            for (FragmentDataObject fragment : c.getFragments()) {
                sumOfSizes += fragment.getSize();
            }

            for (FragmentDataObject f : c.getFragments()) {
                double[] medoidProps = computeMedoidProperties(f, c.getFragments(), sumOfSizes);
                if (medoidProps[0] <= gedThreshold || !settings.isDbscanClustering()) {
                    if (medoidProps[1] > maxBenifitCostRatio) {
                        maxDistance = medoidProps[0];
                        maxBenifitCostRatio = medoidProps[1];
                        standardizingEffot = medoidProps[2];
                        refactoringGain = (int) medoidProps[3];
//                        totalDistance = medoidProps[4];
                        medoidFragmentId = f.getFragmentId();

                    } else if (medoidProps[1] == maxBenifitCostRatio) {
                        if (medoidProps[0] < maxDistance) {
                            System.out.println("New medoid is better :)");
                            maxDistance = medoidProps[0];
                            maxBenifitCostRatio = medoidProps[1];
                            standardizingEffot = medoidProps[2];
                            refactoringGain = (int) medoidProps[3];
//                            totalDistance = medoidProps[4];
                            medoidFragmentId = f.getFragmentId();
                        }
                    }
                }
            }

            log.debug("Standard fragment: " + medoidFragmentId + " (" + maxBenifitCostRatio + ")");

            cd.setMedoidId(medoidFragmentId);
            cd.setStandardizingEffort(round(standardizingEffot));
            cd.setRefactoringGain(refactoringGain);
            cd.setBCR(round(maxBenifitCostRatio));

        } catch (Exception e) {
            String msg = "Failed to compute standardizing detailed of the cluster " + c.getClusterId();
            log.error(msg, e);
            throw new RepositoryException(msg);
        }
    }

    private double[] computeMedoidProperties(FragmentDataObject candidate, List<FragmentDataObject> memberFragments, int sumOfSizes) throws RepositoryException {
        double standardizingEffort = 0; // sum of absolute geds
        double maxDistance = 0; // normalized max distance
        double totalDistance = 0; // sum of normalized geds
        int refactGain = 0;
        double benifitCostRatio = 0;

        for (FragmentDataObject memberFragment : memberFragments) {
            double normalizedDistance = gedMatrix.getGED(candidate.getFragmentId(), memberFragment.getFragmentId());
            if (normalizedDistance > maxDistance) {
                maxDistance = normalizedDistance;
            }
            totalDistance += normalizedDistance;
            double cost = normalizedDistance * (candidate.getSize() + memberFragment.getSize());
            standardizingEffort += cost;
        }

        int medoidSize = candidate.getSize();
        refactGain = (sumOfSizes - medoidSize) - (memberFragments.size() - 1);
        benifitCostRatio = refactGain / standardizingEffort;

        return new double[]{maxDistance, benifitCostRatio, standardizingEffort, refactGain, totalDistance};
    }

    private double round(double number) {
        return (double) Math.round(number * Constants.ROUND_OFF_AMOUNT) / Constants.ROUND_OFF_AMOUNT;
    }

    private float round(float number) {
        return (float) Math.round(number * Constants.ROUND_OFF_AMOUNT) / Constants.ROUND_OFF_AMOUNT;
    }




    /**
     * Set the GED Matrix.
     * @param gedMatrix the ged matrix
     */
    public void setGedMatrix(InMemoryGEDMatrix gedMatrix) {
        this.gedMatrix = gedMatrix;
    }


    /**
     * Set the Fragment Version Dao.
     * @param fragmentVersionDao the fragment version
     */
    public void setFragmentVersionDao(FragmentVersionDao fragmentVersionDao) {
        this.fragmentVersionDao = fragmentVersionDao;
    }
}
