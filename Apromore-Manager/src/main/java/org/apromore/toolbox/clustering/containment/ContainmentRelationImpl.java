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

package org.apromore.toolbox.clustering.containment;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.dataObject.FragmentVersionDO;
import org.apromore.dao.dataObject.FragmentVersionDagDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ContainmentRelationImpl implements ContainmentRelation {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainmentRelationImpl.class);

    private FragmentVersionRepository fragmentVersionRepository;
    private FragmentVersionDagRepository fragmentVersionDagRepository;
    private ProcessModelVersionRepository processModelVersionRepository;

    private Map<Integer, Integer> idIndexMap = new HashMap<>();
    private Map<Integer, Integer> indexIdMap = new HashMap<>();
    private Map<Integer, Integer> fragSize = new HashMap<>();
    private List<Integer> rootIds = new ArrayList<>();

    /* Mapping from root fragment Id -> Ids of all ascendant fragments of that root fragment */
    private Map<Integer, List<Integer>> hierarchies = new HashMap<>();
    private boolean[][] contmatrix;
    private int minSize = 3;

    /**
     * Public Constructor used for spring wiring of objects, also used for tests.
     */
    @Inject
    public ContainmentRelationImpl(final FragmentVersionRepository fragVersionRepo, final FragmentVersionDagRepository fragDagRepo,
            final ProcessModelVersionRepository pmvRepo) {
        fragmentVersionRepository = fragVersionRepo;
        fragmentVersionDagRepository = fragDagRepo;
        processModelVersionRepository = pmvRepo;
    }


    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#initialize()
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws Exception {
        indexIdMap.clear();
        idIndexMap.clear();
        fragSize.clear();
        rootIds.clear();
        hierarchies.clear();
        contmatrix = null;

        queryFragments();
        initContainmentMatrix();
    }

    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#getRoots()
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getRoots() {
        return rootIds;
    }

    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#getHierarchy(Integer)
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getHierarchy(Integer rootFragmentId) {
        return hierarchies.get(rootFragmentId);
    }

    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#getFragmentSize(Integer)
     * {@inheritDoc}
     */
    @Override
    public int getFragmentSize(Integer fragmentId) {
        return fragSize.get(fragmentId);
    }

    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#setMinSize(int)
     * {@inheritDoc}
     */
    @Override
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#getNumberOfFragments()
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfFragments() {
        return idIndexMap.size();
    }

    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#getFragmentId(Integer)
     * {@inheritDoc}
     */
    @Override
    public Integer getFragmentId(Integer frag) {
        return indexIdMap.get(frag);
    }

    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#getFragmentIndex(Integer)
     * {@inheritDoc}
     */
    @Override
    public Integer getFragmentIndex(Integer frag) {
        return idIndexMap.get(frag);
    }

    /**
     * @see org.apromore.toolbox.clustering.containment.ContainmentRelation#areInContainmentRelation(Integer, Integer)
     * {@inheritDoc}
     */
    @Override
    public boolean areInContainmentRelation(Integer frag1, Integer frag2) {
        return contmatrix[frag1][frag2];
    }



    /* Query the Fragments and setup the different data structures. */
    private void queryFragments() throws Exception {
        Integer index;
        List<FragmentVersionDO> fs = fragmentVersionRepository.getFragmentsBetweenSize(minSize, 5000);
        for (FragmentVersionDO f : fs) {
            index = idIndexMap.size();
            idIndexMap.put(f.getId(), index);
            indexIdMap.put(index, f.getId());
            fragSize.put(f.getId(), f.getFragmentSize());
        }
    }

    /* Initialise the Containment Matrix. */
    private void initContainmentMatrix() throws Exception {
        Integer parentIndex;
        Integer childIndex;
        contmatrix = new boolean[idIndexMap.size()][idIndexMap.size()];

        // Initialize the containment matrix using the parent-child relation
        List<FragmentVersionDagDO> dags = fragmentVersionDagRepository.getAllDAGEntriesBySize(minSize);
        for (FragmentVersionDagDO fdag : dags) {
            parentIndex = idIndexMap.get(fdag.getFragmentVersionId());
            childIndex = idIndexMap.get(fdag.getChildFragmentVersionId());
            if (parentIndex != null && childIndex != null) {
                contmatrix[parentIndex][childIndex] = true;
            }
        }
        // Compute the transitive closure (i.e., ancestor-descendant relation)
        for (int i = 0; i < contmatrix.length; i++) {
            for (int j = 0; j < contmatrix.length; j++) {
                if (contmatrix[j][i]) {
                    for (int k = 0; k < contmatrix.length; k++) {
                        contmatrix[j][k] = contmatrix[j][k] | contmatrix[i][k];
                    }
                }
            }
        }

        // Compute the symmetric relation
        for (int i = 0; i < contmatrix.length; i++) {
            for (int j = 0; j < contmatrix.length; j++) {
                if (contmatrix[i][j]) {
                    contmatrix[j][i] = true;
                }
            }
        }

        initHierarchies();
    }

    /* Initialise the Hierarchies. */
    private void initHierarchies() throws Exception {
        Integer rootIndex;
        List<Integer> hierarchy;
        rootIds = queryRoots();
        LOGGER.debug("Total roots: " + rootIds.size());

        for (Integer rootId : rootIds) {
            hierarchy = new ArrayList<>();
            hierarchies.put(rootId, hierarchy);
            hierarchy.add(rootId);

            rootIndex = getFragmentIndex(rootId);
            for (Integer fIndex : indexIdMap.keySet()) {
                if (!fIndex.equals(rootIndex) && areInContainmentRelation(rootIndex, fIndex)) {
                    hierarchy.add(getFragmentId(fIndex));
                }
            }
        }
    }

    /* Query the root fragments from the repo. */
    private List<Integer> queryRoots() throws Exception {
        return processModelVersionRepository.getRootFragments(minSize);
    }

}
