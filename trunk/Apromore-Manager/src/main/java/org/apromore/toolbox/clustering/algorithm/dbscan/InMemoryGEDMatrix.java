/**
 *
 */
package org.apromore.toolbox.clustering.algorithm.dbscan;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.ClusterRepository;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class InMemoryGEDMatrix {

    private static final Logger log = LoggerFactory.getLogger(InMemoryGEDMatrix.class);

    private ClusterRepository clusterRepository;

    private NeighbourhoodCache neighborhoodCache;
    private Map<FragmentPair, Double> inMemoryGEDs;
    private Map<Integer, InMemoryCluster> clusters;
    private List<FragmentDataObject> noise;
    private List<FragmentDataObject> unprocessedFragments;

    private ClusterSettings settings;


    /**
     * Public Constructor used for because we don't implement an interface and use Proxys.
     */
    public InMemoryGEDMatrix() { }

    /**
     * Public Constructor used for spring wiring of objects, also used for tests.
     */
    @Inject
    public InMemoryGEDMatrix(final ClusterRepository cRepo) {
        clusterRepository = cRepo;
    }


    public void initialize(ClusterSettings settings, Map<Integer, InMemoryCluster> clusters, List<FragmentDataObject> noise,
            List<FragmentDataObject> unprocessedFragments) throws RepositoryException {
        neighborhoodCache = new NeighbourhoodCache();
        this.settings = settings;
        this.clusters = clusters;
        this.noise = noise;
        this.unprocessedFragments = unprocessedFragments;

        loadInMemoryGEDs();
    }

    private void loadInMemoryGEDs() throws RepositoryException {
        double maxGED = settings.getMaxNeighborGraphEditDistance();
        log.debug("Loading GEDs to memory...");
        inMemoryGEDs = clusterRepository.getDistances(maxGED);
        log.debug("GEDs have been successfully loaded to memory.");
    }


    public double getGED(Integer fid1, Integer fid2) throws RepositoryException {
        if (fid1.equals(fid2)) {
            return 0;
        }

        double gedValue = 1;
        FragmentPair pair = new FragmentPair(fid1, fid2);
        if (inMemoryGEDs.containsKey(pair)) {
            gedValue = inMemoryGEDs.get(pair);
        }

        return gedValue;
    }

    public List<FragmentDataObject> getUnsharedCoreObjectNeighborhood(FragmentDataObject o, Integer sharableClusterId,
            List<Integer> allowedIds) throws RepositoryException {
        List<FragmentDataObject> nb = getCoreObjectNeighborhood(o, allowedIds);
        if (nb == null) {
            return null;
        }

        List<FragmentDataObject> unsharedNB = new ArrayList<FragmentDataObject>();
        for (FragmentDataObject fo : nb) {
            boolean containedInSharableCluster = false;
            InMemoryCluster sharableCluster = clusters.get(sharableClusterId);
            if (sharableCluster != null) {
                containedInSharableCluster = sharableCluster.getFragments().contains(fo);
            }

            if (unprocessedFragments.contains(fo) || noise.contains(fo) || containedInSharableCluster) {
                unsharedNB.add(fo);
            }
        }

        if (!unsharedNB.contains(o)) {
            unsharedNB.add(o);
        }

        if (unsharedNB.size() < settings.getMinPoints()) {
            return null;
        } else {
            return unsharedNB;
        }
    }

    public List<FragmentDataObject> getCoreObjectNeighborhood(FragmentDataObject o, List<Integer> allowedIds) throws RepositoryException {
        List<FragmentDataObject> nb = neighborhoodCache.getNeighborhood(o.getFragmentId());

        if (nb == null) {
            nb = getNeighbourhood(o);
            if (!nb.contains(o)) {
                nb.add(o);
            }
        }

        if (allowedIds != null) {
            List<FragmentDataObject> toBeRemoved = new ArrayList<FragmentDataObject>();
            for (FragmentDataObject nf : nb) {
                if (!allowedIds.contains(nf.getFragmentId())) {
                    toBeRemoved.add(nf);
                }
            }
            nb.removeAll(toBeRemoved);
        }

        if (nb.size() >= settings.getMinPoints()) {
            return nb;
        } else {
            return null;
        }
    }

    /**
     * @param o
     * @return
     */
    private List<FragmentDataObject> getNeighbourhood(FragmentDataObject o) {
        Integer oid = o.getFragmentId();
        List<FragmentDataObject> nb = new ArrayList<>();
        Set<FragmentPair> pairs = inMemoryGEDs.keySet();
        for (FragmentPair pair : pairs) {
            if (pair.getFid1().equals(oid)) {
                nb.add(new FragmentDataObject(pair.getFid2()));
            } else if (pair.getFid2().equals(oid)) {
                nb.add(new FragmentDataObject(pair.getFid1()));
            }
        }
        if (!nb.contains(o)) {
            nb.add(o);
        }
        return nb;
    }

}
