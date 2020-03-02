/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
 */

package org.apromore.toolbox.clustering.hierarchy;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.tm.is.led.StringEditDistance;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ComposerService;
import org.apromore.service.helper.SimpleGraphWrapper;
import org.apromore.toolbox.clustering.containment.ContainmentRelation;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc;
import org.apromore.toolbox.clustering.dissimilarity.measure.SimpleGEDDeterministicGreedyCalc;
import org.apromore.toolbox.clustering.dissimilarity.measure.SizeBasedSimpleDissimilarityCalc;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class GEDSimilaritySearcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(GEDSimilaritySearcher.class);

    private double dissThreshold = 0.5;
    private long startedTime = 0;
    private int nfrag = 0;
    private int reportingInterval = 0;
    private int comparedFragments = 0;

    private List<ResultFragment> resultsFragments = null;
    private Map<Integer, SimpleGraph> models = new HashMap<>();
    private List<DissimilarityCalc> chain = new LinkedList<>();

    private ContainmentRelation containmentRelation;
    private FragmentVersionRepository fragmentVersionRepository;
    private ComposerService composerService;


    /**
     * Public Constructor used for because we don't implement an interface and use Proxys.
     */
    public GEDSimilaritySearcher() {
    }

    /**
     * Constructor used by spring to construct this bean.
     */
    @Inject
    public GEDSimilaritySearcher(final ContainmentRelation cRel, final FragmentVersionRepository fvRepo,
            final @Qualifier("composerServiceImpl") ComposerService cSrv) {
        containmentRelation = cRel;
        fragmentVersionRepository = fvRepo;
        composerService = cSrv;
    }


    public void setDissThreshold(double dissThreshold) {
        this.dissThreshold = dissThreshold;
        initialize();
    }

    public void initialize() {
        try {
            containmentRelation.setMinSize(2);
            containmentRelation.initialize();

            this.chain.clear();
            this.addDissimCalc(new SizeBasedSimpleDissimilarityCalc(dissThreshold));
            this.addDissimCalc(new SimpleGEDDeterministicGreedyCalc(dissThreshold, dissThreshold));
        } catch (Exception e) {
            String msg = "Failed to initialize the GEDSimilaritySearcher for distance threshold: " + dissThreshold;
            LOGGER.error(msg, e);
        }
    }

    public List<ResultFragment> search(Integer fragmentId) {
        return search(getSimpleGraph(fragmentId));
    }

    public List<ResultFragment> search(SimpleGraph query) {
        startedTime = System.currentTimeMillis();
        List<Integer> processedFragmentIds = new ArrayList<>();
        resultsFragments = new ArrayList<>();

        nfrag = containmentRelation.getNumberOfFragments();
//        System.out.println("Fragments to compare: " + nfrag);
        reportingInterval = 0;
        comparedFragments = 0;

        List<Integer> roots = containmentRelation.getRoots();
        for (Integer root : roots) {
            List<Integer> h1 = containmentRelation.getHierarchy(root);
            h1.removeAll(processedFragmentIds);

            // fill composerService cache with details of h1's fragments (i.e. fragments rooted at p)
            getSimpleGraph(root);

            computeGEDsWithHierarchy(query, h1);

            // at this point we have compared query fragments with all fragments of h1
            // so we can remove all h1's fragments from the cache
            models.keySet().removeAll(h1);
            //composerService.clearCache(h1);

            processedFragmentIds.addAll(h1);
        }

        return resultsFragments;
    }


    private void computeDissim(Integer fid1, SimpleGraph q) {
        //int fid1Index = containmentRelation.getFragmentIndex(fid1);

        double dissim = compute(fid1, q);

        if (dissim <= dissThreshold) {
            int fragSize = containmentRelation.getFragmentSize(fid1);
            ResultFragment rf = new ResultFragment();
            rf.setFragmentId(fid1);
            rf.setDistance(dissim);
            rf.setFragmentSize(fragSize);
            resultsFragments.add(rf);
        }

        reportingInterval++;
        comparedFragments++;

        if (reportingInterval == 1000) {
            long duration = (System.currentTimeMillis() - startedTime) / 1000;
            reportingInterval = 0;
            double percentage = (double) comparedFragments * 100 / nfrag;
            percentage = (double) Math.round((percentage * 1000)) / 1000d;
//            System.out.println(comparedFragments + " compared out of " + nfrag + " | " + percentage + " % completed. | Elapsed time: " + duration + " s | Distances to write: " + resultsFragments.size());
            LOGGER.info(comparedFragments + " compared out of " + nfrag + " | " + percentage + " % completed. | Elapsed time: " + duration + " s");
        }
    }


    private void computeGEDsWithHierarchy(SimpleGraph q, List<Integer> h1) {
        StringEditDistance.clearWordCache();
        for (Integer aH1 : h1) {
            computeDissim(aH1, q);
        }
    }

    public void addDissimCalc(DissimilarityCalc calc) {
        chain.add(calc);
    }

    public double compute(Integer frag1, SimpleGraph g2) {
        SimpleGraph g1 = getSimpleGraph(frag1);
        double disim = 1.0;

        // a filter for very large fragment
        if (g1.getVertices().size() > 200 || g2.getVertices().size() > 200) {
            return disim;
        }

        for (DissimilarityCalc calc : chain) {
            disim = calc.compute(g1, g2);
            if (calc instanceof SimpleGEDDeterministicGreedyCalc) {
                if (!((SimpleGEDDeterministicGreedyCalc) calc).isDeterministicGED() && !calc.isAboveThreshold(disim))
                    LOGGER.info("Incurs in at least one non-deterministic mapping (cf. Greedy algorithm) with " + frag1);
            }
            if (calc.isAboveThreshold(disim)) {
                disim = 1.0;
                break;
            }
        }
        return disim;
    }

    private SimpleGraph getSimpleGraph(Integer fragVersionId) {
        SimpleGraph graph = models.get(fragVersionId);

        if (graph == null) {
            try {
                Canonical cpfGraph = composerService.compose(fragmentVersionRepository.findOne(fragVersionId));
                graph = new SimpleGraphWrapper(cpfGraph);

                // NOTE: this was commented out in the svn version
                if (graph.getEdges().size() < 100) {
                    models.put(fragVersionId, graph);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new SimpleGraph(graph);
    }

}
