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
import java.lang.Object;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import nl.tue.tm.is.led.StringEditDistance;
import org.apache.commons.collections15.map.MultiKeyMap;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.FragmentDistanceRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.*;
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

    private FolderRepository folderRepo;
    private FragmentVersionRepository fragmentVersionRepository;

    private ContainmentRelation crel;
    private FragmentDistanceRepository fragmentDistanceRepository;
    private ComposerService composerService;

    private Map<Integer, SimpleGraph> models = new ConcurrentHashMap<>();
    private Map<Integer, Canonical> canModels = new ConcurrentHashMap<>();

    private List<DissimilarityCalc> chain = new LinkedList<>();
    private List<GEDMatrixCalc> chain2 = new LinkedList<>();

    private double dissThreshold;
    private long startedTime = 0;
    private int totalPairs = 0;
    private AtomicInteger reportingInterval = new AtomicInteger();
    private AtomicInteger processedPairs = new AtomicInteger();

    private AtomicInteger coresUsed = new AtomicInteger();
    private int coresAvailable = Runtime.getRuntime().availableProcessors();

    private MultiKeyMap dissimmap = null;
    private ReentrantReadWriteLock readWriteLockDiss = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLockDiss = readWriteLockDiss.readLock();
    private ReentrantReadWriteLock.WriteLock writeLockDiss = readWriteLockDiss.writeLock();

    private StringBuffer reporting = new StringBuffer();

    private MultiKeyMap writtenDiss = new MultiKeyMap();
    private ReentrantReadWriteLock readWriteLockWrittenDiss = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLockWrittenDiss = readWriteLockWrittenDiss.readLock();
    private ReentrantReadWriteLock.WriteLock writeLockWrittenDiss = readWriteLockWrittenDiss.writeLock();

    private ConcurrentHashMap<Integer, Set<Folder>> folderMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private ExecutorService executor;

    private Set<FragmentVersion> getRootFragments(Integer fragmentID) {
        Set<FragmentVersion> rootFragments = new HashSet<>();
        List parentFragments = fragmentVersionRepository.getRealParentFragments(fragmentID);
        if(parentFragments.isEmpty()) {
            if(fragmentVersionRepository.findOne(fragmentID) != null) {
                rootFragments.add(fragmentVersionRepository.findOne(fragmentID));
            }
        }else {
            for(FragmentVersionDag fragmentVersionDag : (List<FragmentVersionDag>) parentFragments) {
                if(!fragmentID.equals(fragmentVersionDag.getFragmentVersion().getId())) {
                    rootFragments.addAll(getRootFragments(fragmentVersionDag.getFragmentVersion().getId()));
                }
            }
        }
        return rootFragments;
    }

    private Set<Folder> getFoldersOfProcessFragment(Integer fragmentID) {
        Set<Folder> folders;
        if((folders = folderMap.get(fragmentID)) == null) {
            Set<FragmentVersion> rootFragments = getRootFragments(fragmentID);

            folders = new HashSet<>();
            for (FragmentVersion rootFragment : rootFragments) {
                for (ProcessModelVersion processModelVersionRoot : rootFragment.getRootProcessModelVersions()) {
                    folders.add(processModelVersionRoot.getProcessBranch().getProcess().getFolder());
                }
            }
            folderMap.put(fragmentID, folders);
        }
        return folders;
    }

    private boolean isSameFolder(Integer root1, Integer root2) {
        Set<Folder> foldersRoot1 = getFoldersOfProcessFragment(root1);
        Set<Folder> foldersRoot2 = getFoldersOfProcessFragment(root2);

        for(Folder folder1 : foldersRoot1) {
            for(Folder folder2 : foldersRoot2) {
                if(isGEDFolder(folder1) && isGEDFolder(folder2) && (isSubFolder(folder1, folder2) || isSubFolder(folder2, folder1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSameFolder(Folder folder1, Folder folder2) {
        return convertFolderToString(folder1).equals(convertFolderToString(folder2));
    }

    private boolean isGEDFolder(Folder folder1) {
        Folder folder = folderRepo.findOne(folder1.getId());
        return folder.isGEDMatrixReady();
    }

    private String convertFolderToString(Folder folder) {
        String parent = "";
        if(folder.getParentFolder() != null) parent = convertFolderToString(folder.getParentFolder());
        return parent + "/" + folder.getName();
    }

    private boolean isSubFolder(Folder folder1, Folder folder2) {
        if(isSameFolder(folder1, folder2)) return true;
        for(Folder subfolder : folder1.getSubFolders()) {
            if(isSubFolder(subfolder, folder2)) {
                return true;
            }
        }
        return false;
    }

    private void writeOnWrittenDissimmap(Integer frag1, Integer frag2, Double distFid2Fid1) {
        writeLockWrittenDiss.lock();
        writtenDiss.put(frag1, frag2, distFid2Fid1);
        writeLockWrittenDiss.unlock();
    }

    private Object readFromWrittenDissimmap(Integer frag1, Integer frag2) {
        readLockWrittenDiss.lock();
        Object o = writtenDiss.get(frag1, frag2);
        readLockWrittenDiss.unlock();
        return o;
    }

    private void writeOnDissimmap(Integer frag1, Integer frag2, Double distFid2Fid1) {
        writeLockDiss.lock();
        dissimmap.put(frag1, frag2, distFid2Fid1);
        writeOnWrittenDissimmap(frag2, frag1, distFid2Fid1);
        writeLockDiss.unlock();
    }

    private Object readFromDissimmap(Integer frag1, Integer frag2) {
        readLockDiss.lock();
        Object o = dissimmap.get(frag1, frag2);
        readLockDiss.unlock();
        return o;
    }

    private int sizeDissimmap() {
        readLockDiss.lock();
        int i = dissimmap.size();
        readLockDiss.unlock();
        return i;
    }

    private void saveDissimmap() {
        MultiKeyMap clone = null;
        writeLockDiss.lock();
        if(!dissimmap.isEmpty()) {
            clone = (MultiKeyMap) dissimmap.clone();
            dissimmap.clear();
        }
        writeLockDiss.unlock();
        if(clone != null && !clone.isEmpty()) {
            executor.execute(new MatrixStorer(clone));
        }
    }

    @Inject
    public HierarchyAwareDissimMatrixGenerator(final ContainmentRelation rel, final FragmentDistanceRepository fragDistRepo,
            final ComposerService compSrv, final FragmentVersionRepository fragmentVersionRepository, final  FolderRepository folderRepository) {
        crel = rel;
        fragmentDistanceRepository = fragDistRepo;
        composerService = compSrv;
        folderRepo = folderRepository;
        this.fragmentVersionRepository = fragmentVersionRepository;
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
        Double result = (Double) readFromDissimmap(frag1, frag2);
        if (result == null) {
            result = (Double) readFromDissimmap(frag2, frag1);
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
        executor = Executors.newFixedThreadPool(coresAvailable);

        startedTime = System.currentTimeMillis();
        Set<Integer> processedFragmentIds = new HashSet<>();

        dissimmap = new MultiKeyMap();
        int nfrag = crel.getNumberOfFragments();
        totalPairs = nfrag * (nfrag + 1) / 2;
        reportingInterval.set(0);
        processedPairs.set(0);

        LOGGER.info("Cores Available " + coresAvailable);

        List<Integer> roots = crel.getRoots();

        for (int p = 0; p < roots.size(); p++) {
            intraRoot = roots.get(p);

            while(coresUsed.get() >= coresAvailable) {
                lock.lock();
                condition.awaitUninterruptibly();
                lock.unlock();
            }

            coresUsed.incrementAndGet();

            executor.execute(new MatrixExecutor(intraRoot, processedFragmentIds, roots, p));
            processedFragmentIds.addAll(crel.getHierarchy(intraRoot));
        }

        while(coresUsed.get() > 0) {
            lock.lock();
            condition.awaitUninterruptibly();
            lock.unlock();
        }

        // ged values are written to the database periodically after reporting period. if there are left over geds we have to write them here.
        saveDissimmap();

        folderMap.clear();
        writtenDiss.clear();

        if(reporting.length() > 0) {
            LOGGER.info(reporting.toString());
        }
    }

    private void clearCaches(List<Integer> h1) {
        for (Integer fragmentId : h1) {
            models.remove(fragmentId);
            canModels.remove(fragmentId);
        }
    }

//    private void clearCaches(List<Integer> processedFragmentIds, List<Integer> h1) {
//        processedFragmentIds.addAll(h1);
//        for (Integer fragmentId : h1) {
//            models.remove(fragmentId);
//            canModels.remove(fragmentId);
//        }
//    }


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
            if (isSameFolder(fid1, fid2) && !crel.areInContainmentRelation(crel.getFragmentIndex(fid1), crel.getFragmentIndex(fid2))) {
                Double distFid2Fid1 = (Double) readFromDissimmap(fid2, fid1);
                if (distFid2Fid1 != null) {
                    writeOnDissimmap(fid1, fid2, distFid2Fid1);
                } else {
//                    distFid2Fid1 = fragmentDistanceRepository.getDistance(fid2, fid1); //Removed by Raf
                    distFid2Fid1 = (Double) readFromWrittenDissimmap(fid2, fid1);
                    if (distFid2Fid1 != null) {
                        writeOnDissimmap(fid1, fid2, distFid2Fid1);
                    } else {
                        double dissim = computeFromGEDMatrixCalc(fid1, fid2); // computeFromDissimilarityCalc(fid1, fid2);
                        if (dissim <= dissThreshold) {
                            writeOnDissimmap(fid1, fid2, dissim);
                        }
                    }
                }
            }

            int r = reportingInterval.incrementAndGet();
            int p = processedPairs.incrementAndGet();
            if (r == 10000) {
                reportingInterval.addAndGet(-10000);
                long duration = (System.currentTimeMillis() - startedTime) / 1000;
                double percentage = (double) p * 100 / totalPairs;
                percentage = (double) Math.round((percentage * 1000)) / 1000d;
                int s = sizeDissimmap();
                if (s > 1000) {
                    LOGGER.info(p + " processed out of " + totalPairs + " | " + percentage + " % completed. | Elapsed time: " + duration + " s | Distances to write: " + s);
                    saveDissimmap();
                }
            }
        } catch (Exception e) { }
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

    class MatrixExecutor implements Runnable {

        Integer intraRoot;
        Integer interRoot;
        List<Integer> h1;
        List<Integer> h2;
        Set<Integer> processedFragmentIds;
        List<Integer> roots;
        int p;

        MatrixExecutor(Integer intraRoot, Set<Integer> processedFragmentIds, List<Integer> roots, int p){
            this.intraRoot = intraRoot;
            this.processedFragmentIds = (Set<Integer>) ((HashSet) processedFragmentIds).clone();
            this.roots = roots;
            this.p = p;
        }

        @Override
        public void run() {
            h1 = crel.getHierarchy(intraRoot);
            h1.removeAll(processedFragmentIds); //Added by Raf

            LOGGER.info("Processing Root: " + intraRoot);
            computeIntraHierarchyGEDs(h1); //Added by Raf

            if (p < roots.size() - 1) {
                for (int q = p + 1; q < roots.size(); q++) {
                    interRoot = roots.get(q);
                    h2 = crel.getHierarchy(interRoot);
                    computeInterHierarchyGEDs(h1, h2); //Added by Raf
                }
            }

            // at this point we have processed all fragments of h1, with fragments in the entire repository.
            // so we can remove all h1's fragments from the cache
            clearCaches(h1);

            coresUsed.decrementAndGet();

            lock.lock();
            condition.signalAll();
            lock.unlock();
        }
    }

    class MatrixStorer implements Runnable {

        private MultiKeyMap clone;

        public MatrixStorer(MultiKeyMap clone) {
            this.clone = clone;
        }

        @Override
        public void run() {
            fragmentDistanceRepository.saveDistances(clone);
        }
    }

}
