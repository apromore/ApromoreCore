/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.toolbox.clustering.dissimilarity;

import java.util.List;
import javax.inject.Inject;

import org.apache.commons.collections15.map.MultiKeyMap;
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
