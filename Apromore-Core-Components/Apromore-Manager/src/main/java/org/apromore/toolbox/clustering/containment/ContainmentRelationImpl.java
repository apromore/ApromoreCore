/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.toolbox.clustering.containment;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.dataObject.FragmentVersionDO;
import org.apromore.dao.dataObject.FragmentVersionDagDO;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.toolbox.clustering.topologicalsort.TopologicalSortGraph;
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
    private Set<Integer> contmatrix;
    private Set<Integer> contmatrixTransitive;
    private int base;
    private int minSize = 3;

    private int coresUsed = 0;
    private int coresAvailable = Runtime.getRuntime().availableProcessors();

    private ReentrantLock coreLock = new ReentrantLock();
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private ExecutorService executorService;

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
        return contmatrix.contains(matrixToInt(frag1, frag2));
    }

    private int matrixToInt(int x, int y) {
        return y + (x * base);
    }


    private boolean isContainedTransitive(Integer fragment1, Integer fragment2) {
        return contmatrixTransitive.contains(matrixToInt(fragment1, fragment2));
    }

    private boolean isContained(Integer fragment1, Integer fragment2) {
        return contmatrix.contains(matrixToInt(fragment1, fragment2));
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
        LOGGER.error("Starting with " + coresAvailable + " cores");
        Integer parentIndex;
        Integer childIndex;
        executorService = Executors.newFixedThreadPool(40 * coresAvailable);
        contmatrix = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
        contmatrixTransitive = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
        base = idIndexMap.size();

        TopologicalSortGraph topologicalSortGraph = new TopologicalSortGraph(base);
        TopologicalSortGraph.Node parentNode;
        TopologicalSortGraph.Node childNode;

        // Initialize the containment matrix using the parent-child relation
        List<FragmentVersionDagDO> dags = fragmentVersionDagRepository.getAllDAGEntriesBySize(minSize);
        for (FragmentVersionDagDO fdag : dags) {
            parentIndex = idIndexMap.get(fdag.getFragmentVersionId());
            childIndex = idIndexMap.get(fdag.getChildFragmentVersionId());
            if (parentIndex != null && childIndex != null) {
                contmatrix.add(matrixToInt(parentIndex, childIndex));

                parentNode = topologicalSortGraph.new Node(parentIndex);
                childNode = topologicalSortGraph.new Node(childIndex);
                topologicalSortGraph.addNeighbor(parentNode, childNode);
            }
        }

        int[] sortedFragments = topologicalSortGraph.topologicalSort();
        int[] array = new int[base];
        int max, i, j, k, iPos, jPos, kPos, pos;

        MatrixExecutor[] matrixExecutors = new MatrixExecutor[base];
        for (iPos = base - 1; iPos >= 0; iPos--) {
            max = 0;
            i = sortedFragments[iPos];
            for (kPos = base - 1; kPos > iPos; kPos--) {
                k = sortedFragments[kPos];
                if (isContained(i, k) || isContainedTransitive(i, k)) {
                    array[max] = k;
                    max++;
                }
            }
            if(iPos % 1000 == 0) LOGGER.error("Waiting " + iPos + " max " + max);

            if(max > 0) {
                for (jPos = iPos - 1; jPos >= 0; jPos--) {
                    j = sortedFragments[jPos];

                    increaseUsedCores();
                    pos = (base - 1) - jPos;
                    if(matrixExecutors[pos] == null) {
                        matrixExecutors[pos] = new MatrixExecutor();
                    }
                    matrixExecutors[pos].setParameters(j, i, max, array);
                    executorService.execute(matrixExecutors[pos]);
                }
            }

            while(getUsedCores() > 0) {
                threadSleep();
            }
        }
        contmatrix.addAll(contmatrixTransitive);

        // Compute the symmetric relation
        for (i = 0; i < base; i++) {
            for (j = 0; j < base; j++) {
                if (contmatrix.contains(matrixToInt(i, j))) {
                    contmatrix.add(matrixToInt(j, i));
                }
            }
        }

        initHierarchies();
        LOGGER.error("Completed");
    }

    private void increaseUsedCores() {
        coreLock.lock();
        coresUsed++;
        coreLock.unlock();
    }

    private void decreaseUsedCores() {
        coreLock.lock();
        coresUsed--;
        coreLock.unlock();
    }

    private int getUsedCores() {
        coreLock.lock();
        int res = coresUsed;
        coreLock.unlock();
        return res;
    }

    private void threadSleep() {
        lock.lock();
//        long time = System.nanoTime();
        condition.awaitUninterruptibly();
//        time = System.nanoTime() - time;
//        LOGGER.error("Slept for " + time + " nanoseconds");
        lock.unlock();
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

    class MatrixExecutor implements Runnable {
        private int j;
        private int i;
        private int k;
        private int max;
        private int[] array;

        public MatrixExecutor() {

        }

        public MatrixExecutor(int j, int i, int max, int[] array) {
            this.j = j;
            this.i = i;
            this.max = max;
            this.array = array;
        }

        public void setParameters(int j, int i, int max, int[] array) {
            this.j = j;
            this.i = i;
            this.max = max;
            this.array = array;
        }

        @Override
        public void run() {
            Set<Integer> set = new HashSet<>();
            if(isContained(j, i)) {
                for (k = 0; k < max; k++) {
                    set.add(matrixToInt(j, array[k]));
                }
            }
            contmatrixTransitive.addAll(set);

            decreaseUsedCores();

            lock.lock();
            if(getUsedCores() == 0) {
                condition.signalAll();
            }
            lock.unlock();
        }
    }
}
