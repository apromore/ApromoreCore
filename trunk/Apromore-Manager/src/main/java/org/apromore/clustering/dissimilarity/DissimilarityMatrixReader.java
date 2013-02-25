package org.apromore.clustering.dissimilarity;

import java.util.List;
import javax.inject.Inject;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.clustering.containment.ContainmentRelation;
import org.apromore.dao.ClusterRepository;
import org.apromore.dao.FragmentDistanceRepository;
import org.apromore.dao.model.FragmentDistance;
import org.springframework.stereotype.Service;

@Service
public class DissimilarityMatrixReader implements DissimilarityMatrix {

    private FragmentDistanceRepository fragmentDistanceRepository;

    private MultiKeyMap dissimmap = new MultiKeyMap();


    /**
     * Constructor for Spring to inject the code.
     * @param fragmentDistanceRepo the FragmentDistance Repo.
     */
    @Inject
    public DissimilarityMatrixReader(final FragmentDistanceRepository fragmentDistanceRepo) {
        fragmentDistanceRepository = fragmentDistanceRepo;
    }


    /**
     * Initializes the Object.
     * @param threshold the threshold for dis-similarity
     */
    @Override
    public void initialize(ContainmentRelation containmentRelation, double threshold) {
        List<FragmentDistance> geds = fragmentDistanceRepository.findByDistanceLessThan(threshold);
        for (FragmentDistance ged : geds) {
            Integer fid1 = ged.getFragmentVersionId1().getId();
            Integer fid2 = ged.getFragmentVersionId2().getId();
            double value = ged.getDistance();
            dissimmap.put(containmentRelation.getFragmentIndex(fid1), containmentRelation.getFragmentIndex(fid2), value);
        }
    }


    /**
     * @see DissimilarityMatrix#getDissimilarity(Integer, Integer)
     */
    public Double getDissimilarity(Integer frag1, Integer frag2) {
        Double result = (Double) dissimmap.get(frag1, frag2);
        if (result == null) {
            result = (Double) dissimmap.get(frag2, frag1);
        }
        return result;
    }

}
