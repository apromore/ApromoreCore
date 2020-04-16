/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 *
 */
package org.apromore.toolbox.clustering.analyzers;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.common.Constants;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterSettings;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentDataObject;
import org.apromore.toolbox.clustering.algorithm.dbscan.InMemoryCluster;
import org.apromore.toolbox.clustering.algorithm.dbscan.InMemoryClusterer;
import org.apromore.toolbox.clustering.algorithm.dbscan.InMemoryGEDMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ClusterAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(InMemoryClusterer.class);

    private InMemoryGEDMatrix inMemoryGEDMatrix;
    private FragmentVersionRepository fragmentVersionRepository;

    private Map<Integer, Integer> fragmentSizes;


    /**
     * Public Constructor used for because we don't implement an interface and use Proxys.
     */
    public ClusterAnalyzer() { }

    /**
     * Public Constructor used for spring wiring of objects, also used for tests.
     */
    @Inject
    public ClusterAnalyzer(final InMemoryGEDMatrix matrix, final FragmentVersionRepository fragVersionRepo) {
        inMemoryGEDMatrix = matrix;
        fragmentVersionRepository = fragVersionRepo;
    }


    public void loadFragmentSizes() {
        log.debug("Loading all fragment sizes from the database to memory...");
        fragmentSizes = new HashMap<>();
        List<FragmentVersion> fragmentVersions = fragmentVersionRepository.findAll();
        for (FragmentVersion fragmentVersion : fragmentVersions) {
            if (fragmentVersion.getFragmentSize() != null) {
                fragmentSizes.put(fragmentVersion.getId(), fragmentVersion.getFragmentSize());
            }
        }
        log.debug("Loading all fragment sizes complete.");
    }

    public Cluster analyzeCluster(InMemoryCluster c, ClusterSettings settings) throws RepositoryException {
        Cluster cd = new Cluster();
        cd.setId(c.getClusterId());

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
            Integer medoidFragmentId = 0;
            int refactoringGain = 0;
            double standardizingEffot = 0;
            double maxBenifitCostRatio = 0; // we want to maximise this
            double maxDistance = Double.MAX_VALUE; // we want to minimise this
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
                        medoidFragmentId = f.getFragmentId();

                    } else if (medoidProps[1] == maxBenifitCostRatio) {
                        if (medoidProps[0] < maxDistance) {
                            maxDistance = medoidProps[0];
                            maxBenifitCostRatio = medoidProps[1];
                            standardizingEffot = medoidProps[2];
                            refactoringGain = (int) medoidProps[3];
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
        int refactGain;
        double benifitCostRatio;

        for (FragmentDataObject memberFragment : memberFragments) {
            double normalizedDistance = inMemoryGEDMatrix.getGED(candidate.getFragmentId(), memberFragment.getFragmentId());
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

}
