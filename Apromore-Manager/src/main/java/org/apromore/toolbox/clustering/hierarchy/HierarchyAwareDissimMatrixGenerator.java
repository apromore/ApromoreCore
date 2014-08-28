/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
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
import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.dao.FragmentDistanceRepository;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ComposerService;
import org.apromore.service.helper.SimpleGraphWrapper;
import org.apromore.toolbox.clustering.containment.ContainmentRelation;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix;
import org.apromore.toolbox.clustering.dissimilarity.GEDMatrixCalc;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class HierarchyAwareDissimMatrixGenerator implements DissimilarityMatrix {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchyAwareDissimMatrixGenerator.class);

    private ContainmentRelation crel;
    private FragmentDistanceRepository fragmentDistanceRepository;
    private ComposerService composerService;

    private Map<Integer, SimpleGraph> models = new HashMap<>();
    private Map<Integer, Canonical> canModels = new HashMap<>();

    private List<DissimilarityCalc> chain = new LinkedList<>();
    private List<GEDMatrixCalc> chain2 = new LinkedList<>();
    private MultiKeyMap dissimmap = null;

    private double dissThreshold;
    private long startedTime = 0;
    private int totalPairs = 0;
    private int reportingInterval = 0;
    private int processedPairs = 0;


    @Inject
    public HierarchyAwareDissimMatrixGenerator(final ContainmentRelation rel, final FragmentDistanceRepository fragDistRepo,
            final ComposerService compSrv) {
        crel = rel;
        fragmentDistanceRepository = fragDistRepo;
        composerService = compSrv;
    }


    /**
     * @see org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#setDissThreshold(double)
     * {@inheritDoc}
     */
    @Override
    public void setDissThreshold(double dissThreshold) {
        this.dissThreshold = dissThreshold;
    }


    /**
     * @see org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#getDissimilarity(Integer, Integer)
     * {@inheritDoc}
     */
    @Override
    public Double getDissimilarity(Integer frag1, Integer frag2) {
        Double result = (Double) dissimmap.get(frag1, frag2);
        if (result == null) {
            result = (Double) dissimmap.get(frag2, frag1);
        }
        return result;
    }


    /**
     * @see org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#addDissimCalc(org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc)
     * {@inheritDoc}
     */
    @Override
    public void addDissimCalc(DissimilarityCalc calc) {
        chain.add(calc);
    }

    /**
     * @see org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#addGedCalc(org.apromore.toolbox.clustering.dissimilarity.GEDMatrixCalc)
     * {@inheritDoc}
     */
    @Override
    public void addGedCalc(GEDMatrixCalc calc) {
        chain2.add(calc);
    }


    /**
     * @see org .apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#computeDissimilarity()
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void computeDissimilarity() {
        Integer intraRoot;
        Integer interRoot;
        List<Integer> h1;
        List<Integer> h2;

        startedTime = System.currentTimeMillis();
        List<Integer> processedFragmentIds = new ArrayList<>();

        dissimmap = new MultiKeyMap();
        int nfrag = crel.getNumberOfFragments();
        totalPairs = nfrag * (nfrag + 1) / 2;
        reportingInterval = 0;
        processedPairs = 0;

        List<Integer> roots = crel.getRoots();
        for (int p = 0; p < roots.size(); p++) {
            intraRoot = roots.get(p);
            h1 = crel.getHierarchy(intraRoot);
            h1.removeAll(processedFragmentIds);

            LOGGER.info("Processing Root: " + intraRoot);
            computeIntraHierarchyGEDs(h1);

            if (p < roots.size() - 1) {
                for (int q = p + 1; q < roots.size(); q++) {
                    interRoot = roots.get(q);
                    h2 = crel.getHierarchy(interRoot);
                    computeInterHierarchyGEDs(h1, h2);
                }
            }

            // at this point we have processed all fragments of h1, with fragments in the entire repository.
            // so we can remove all h1's fragments from the cache
            clearCaches(processedFragmentIds, h1);
        }

        // ged values are written to the database periodically after reporting period. if there are left over geds we have to write them here.
        if (!dissimmap.isEmpty()) {
            fragmentDistanceRepository.saveDistances(dissimmap);
            dissimmap.clear();
        }
    }

    private void clearCaches(List<Integer> processedFragmentIds, List<Integer> h1) {
        processedFragmentIds.addAll(h1);
        for (Integer fragmentId : h1) {
            models.remove(fragmentId);
            canModels.remove(fragmentId);
        }
    }


    /* Computers the Intra (Outer) root fragments dissimilarity. */
    private void computeIntraHierarchyGEDs(List<Integer> h1) {
        StringEditDistance.clearWordCache();
        for (int i = 0; i < h1.size() - 1; i++) {
            for (int j = i + 1; j < h1.size(); j++) {
                computeDissim(h1.get(i), h1.get(j));
            }
        }
    }

    /* Computers the Inter (inner) fragments dissimilarity with the root. */
    private void computeInterHierarchyGEDs(List<Integer> h1, List<Integer> h2) {
        StringEditDistance.clearWordCache();
        for (Integer fid1 : h1) {
            for (Integer fid2 : h2) {
                computeDissim(fid1, fid2);
            }
        }
    }

    /* Computes the Dissimilarity for the two fragments. */
    private void computeDissim(Integer fid1, Integer fid2) {
        try {
            if (!crel.areInContainmentRelation(crel.getFragmentIndex(fid1), crel.getFragmentIndex(fid2))) {
                double dissim =  computeFromGEDMatrixCalc(fid1, fid2); // computeFromDissimilarityCalc(fid1, fid2);
                if (dissim <= dissThreshold) {
                    dissimmap.put(fid1, fid2, dissim);
                }
            }

            reportingInterval++;
            processedPairs++;
            if (reportingInterval == 1000) {
                reportingInterval = 0;
                long duration = (System.currentTimeMillis() - startedTime) / 1000;
                double percentage = (double) processedPairs * 100 / totalPairs;
                percentage = (double) Math.round((percentage * 1000)) / 1000d;
                LOGGER.info(processedPairs + " processed out of " + totalPairs + " | " + percentage + " % completed. | Elapsed time: " + duration + " s | Distances to write: " + dissimmap.size());
                fragmentDistanceRepository.saveDistances(dissimmap);
                dissimmap.clear();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to compute GED between {} and {} due to {}. " +
                    "GED computation between other fragments will proceed normally.",
                    fid1, fid2, e.getMessage());
        }
    }


    /* Asks each of the Calculators to do it's thing. */
    public double computeFromGEDMatrixCalc(Integer frag1, Integer frag2) {
        double disim = 1.0;

        // a filter for very large fragment
        if (crel.getFragmentSize(frag1) > DissimilarityMatrix.LARGE_FRAGMENTS || crel.getFragmentSize(frag2) > DissimilarityMatrix.LARGE_FRAGMENTS) {
            return disim;
        } else if (crel.getFragmentSize(frag1) < DissimilarityMatrix.SMALL_FRAGMENTS || crel.getFragmentSize(frag2) < DissimilarityMatrix.SMALL_FRAGMENTS) {
            return disim;
        }

        Canonical g1 = getCanonicalGraph(frag1);
        Canonical g2 = getCanonicalGraph(frag2);
        for (GEDMatrixCalc calc : chain2) {
            disim = calc.compute(g1, g2);
            if (calc.isAboveThreshold(disim)) {
                disim = 1.0;
                break;
            }
        }

        return disim;
    }

    /* Asks each of the Calculators to do it's thing. */
    private double computeFromDissimilarityCalc(Integer frag1, Integer frag2) {
        double disim = 1.0;

        // a filter for very large fragment
        if (crel.getFragmentSize(frag1) > DissimilarityMatrix.LARGE_FRAGMENTS || crel.getFragmentSize(frag2) > DissimilarityMatrix.LARGE_FRAGMENTS) {
            return disim;
        } else if (crel.getFragmentSize(frag1) < DissimilarityMatrix.SMALL_FRAGMENTS || crel.getFragmentSize(frag2) < DissimilarityMatrix.SMALL_FRAGMENTS) {
            return disim;
        }

        SimpleGraph sg1 = getSimpleGraph(frag1);
        SimpleGraph sg2 = getSimpleGraph(frag2);
        for (DissimilarityCalc calc : chain) {
            disim = calc.compute(sg1, sg2);
            if (calc.isAboveThreshold(disim)) {
                disim = 1.0;
                break;
            }
        }
        return disim;
    }


    /* Finds the Canonical Graph used in the GED Matrix computations. */
    private Canonical getCanonicalGraph(Integer frag) {
        Canonical graph = canModels.get(frag);

        if (graph == null) {
            try {
                graph = composerService.compose(frag);
                canModels.put(frag, graph);
            } catch (Exception e) {
                LOGGER.error("Failed to get graph of fragment {}", frag);
                e.printStackTrace();
            }
        }

        return graph;
    }

    /* Finds the Simple graph used in the GED Matrix computations. */
    private SimpleGraph getSimpleGraph(Integer frag) {
        SimpleGraph graph = models.get(frag);

        if (graph == null) {
            try {
                Canonical cpfGraph = composerService.compose(frag);
                graph = new SimpleGraphWrapper(cpfGraph);
                models.put(frag, graph);
            } catch (Exception e) {
                LOGGER.error("Failed to get graph of fragment {}", frag);
                e.printStackTrace();
            }
        }

        return graph; //new SimpleGraph(graph);
    }

}
