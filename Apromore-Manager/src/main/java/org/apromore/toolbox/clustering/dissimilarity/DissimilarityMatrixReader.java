package org.apromore.toolbox.clustering.dissimilarity;

import java.util.List;
import javax.inject.Inject;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.toolbox.clustering.containment.ContainmentRelation;
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

    @Override
    public void computeDissimilarity() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addDissimCalc(DissimilarityCalc calc) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addGedCalc(GEDMatrixCalc calc) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setDissThreshold(double dissThreshold) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
