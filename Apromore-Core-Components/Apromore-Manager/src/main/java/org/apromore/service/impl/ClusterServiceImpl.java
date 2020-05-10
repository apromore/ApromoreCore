/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.service.impl;

import org.apromore.aop.Event;
import org.apromore.dao.ClusterAssignmentRepository;
import org.apromore.dao.ClusterRepository;
import org.apromore.dao.FragmentDistanceRepository;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.FragmentDistance;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.HistoryEvent;
import org.apromore.dao.model.HistoryEnum;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.RepositoryException;
import org.apromore.service.ClusterService;
import org.apromore.service.FragmentService;
import org.apromore.service.GEDMatrixBean;
import org.apromore.service.HistoryEventService;
import org.apromore.service.helper.SimpleGraphWrapper;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.service.model.MemberFragment;
import org.apromore.service.model.ProcessAssociation;
import org.apromore.toolbox.clustering.DMatrix;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;
import org.apromore.toolbox.clustering.algorithm.dbscan.InMemoryClusterer;
import org.apromore.toolbox.clustering.algorithm.hac.HACClusterer;
import org.apromore.toolbox.clustering.dissimilarity.measure.SimpleGEDDeterministicGreedyCalc;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the ClusterService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ClusterServiceImpl implements ClusterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterServiceImpl.class);

    private static final String DBSCAN = "DBSCAN";
    private static final String HAC = "HAC";

    private ClusterRepository cRepository;
    private ClusterAssignmentRepository caRepository;
    private FragmentDistanceRepository fdRepository;
    private FragmentService fService;
    private HistoryEventService historyService;

    private InMemoryClusterer dbscanClusterer;
    private HACClusterer hacCluster;

    private DMatrix dmatrix;

    private GEDMatrixBean gedMatrixBean;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param clusterRepository Cluster Repository.
     * @param clusterAssignmentRepository Cluster Assignment Repository.
     * @param fragmentDistanceRepository Fragment Distance Repository.
     * @param historyEventService the HistoryEvent Auditable Service.
     * @param fragmentService Fragment Repository.
     * @param inMemoryClusterer in Memory Clusterer.
     */
    @Inject
    public ClusterServiceImpl(final ClusterRepository clusterRepository, final ClusterAssignmentRepository clusterAssignmentRepository,
            final FragmentDistanceRepository fragmentDistanceRepository, final HistoryEventService historyEventService,
            final FragmentService fragmentService, final InMemoryClusterer inMemoryClusterer, final HACClusterer hacClusterer,
            final DMatrix matrix, final GEDMatrixBean gedMatrixBean) {
        cRepository = clusterRepository;
        caRepository = clusterAssignmentRepository;
        fdRepository = fragmentDistanceRepository;
        historyService = historyEventService;
        dbscanClusterer = inMemoryClusterer;
        hacCluster = hacClusterer;
        fService = fragmentService;
        dmatrix = matrix;
        this.gedMatrixBean = gedMatrixBean;
    }


    /**
     * @see org.apromore.service.ClusterService#cluster(org.apromore.service.model.ClusterSettings)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    @Event(message = HistoryEnum.CLUSTERING_COMPUTATION)
    public void cluster(ClusterSettings settings) throws RepositoryException {
        LOGGER.debug("Create the Clusters");
        clearClusters();

        if (DBSCAN.equals(settings.getAlgorithm())) {
            dbscanClusterer.clusterRepository(settings);
        } else if (HAC.equals(settings.getAlgorithm())) {
            hacCluster.clusterRepository(settings);
        }
        LOGGER.debug("Completed the creation the Clusters.");
    }

    /**
     * @see org.apromore.service.ClusterService#computeGEDMatrix()
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    @Event(message = HistoryEnum.GED_MATRIX_COMPUTATION)
    public void computeGEDMatrix() throws RepositoryException {
        LOGGER.debug("Computing the GED Matrix....");
        if(gedMatrixBean.isEnabled()) {
            clearGEDMatrix();

            try {
                dmatrix.compute();
            } catch (Exception e) {
                LOGGER.error("An error occurred while computing the GED matrix for the first time. This could result in lesser number of clusters. PLEASE RERUN THE COMPUTATION.", e);
                throw new RepositoryException(e);
            }
            LOGGER.debug("Completed computing the GED Matrix....");
        }
    }

    /**
     * @see org.apromore.service.ClusterService#getGedMatrixLastExecutionTime()
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public HistoryEvent getGedMatrixLastExecutionTime() {
        return historyService.findLatestHistoryEventType(HistoryEnum.GED_MATRIX_COMPUTATION);
    }

    /**
     * @see org.apromore.service.ClusterService#getClusteringSummary
     * {@inheritDoc}
     */
    @Override
    public ClusteringSummary getClusteringSummary() {
        ClusteringSummary summary = new ClusteringSummary();
        List<Object[]> summaryObj = cRepository.getClusteringSummary();
        for (Object[] objects : summaryObj) {
            if (objects[0] != null && ((Long) objects[0]).intValue() > 0) {
                if (objects[0] != null) {
                    summary.setNumClusters(((Long) objects[0]).intValue());
                }
                if (objects[1] != null) {
                    summary.setMinClusterSize((Integer) objects[1]);
                }
                if (objects[2] != null) {
                    summary.setMaxClusterSize((Integer) objects[2]);
                }
                if (objects[3] != null) {
                    summary.setMinAvgFragmentSize((Float) objects[3]);
                }
                if (objects[4] != null) {
                    summary.setMaxAvgFragmentSize((Float) objects[4]);
                }
                if (objects[5] != null) {
                    summary.setMinBCR((Double) objects[5]);
                }
                if (objects[6] != null) {
                    summary.setMaxBCR((Double) objects[6]);
                }
            }
        }
        return summary;
    }

    /**
     * @see org.apromore.service.ClusterService#getClusterSummaries(org.apromore.service.model.ClusterFilter)
     * {@inheritDoc}
     */
    @Override
    public List<Cluster> getClusterSummaries(ClusterFilter filter) {
        return cRepository.getFilteredClusters(filter);
    }

    /**
     * @see org.apromore.service.ClusterService#getCluster(Integer)
     * {@inheritDoc}
     */
    @Override
    public org.apromore.service.model.Cluster getCluster(Integer clusterId) {
        MemberFragment fragment;
        ProcessAssociation pa;
        FragmentDistance distance;
        Cluster cinfo = cRepository.findOne(clusterId);

        org.apromore.service.model.Cluster c = new org.apromore.service.model.Cluster();
        c.setCluster(cinfo);

        List<FragmentVersion> fs = caRepository.findFragmentVersionByClusterId(clusterId);
        for (FragmentVersion f : fs) {
            fragment = new MemberFragment(f.getId());
            fragment.setFragmentSize(f.getFragmentSize());
            for (ProcessModelVersion m : f.getProcessModelVersions()) {
                pa = new ProcessAssociation();
                pa.setProcessVersionId(m.getId());
                pa.setProcessVersionNumber(m.getVersionNumber());
                pa.setProcessBranchName(m.getProcessBranch().getBranchName());
                pa.setProcessId(m.getProcessBranch().getProcess().getId());
                pa.setProcessName(m.getProcessBranch().getProcess().getName());
                fragment.getProcessAssociations().add(pa);
            }

            distance = fdRepository.findByFragmentVersionId1AndFragmentVersionId2(cinfo.getMedoidId(), f.getId());
            if (distance != null) {
                fragment.setDistance(distance.getDistance());
            } else {
                fragment.setDistance(-1d);
            }
            c.addFragment(fragment);
        }
        return c;
    }

    /**
     * @see org.apromore.service.ClusterService#getClusters()
     * {@inheritDoc}
     */
    @Override
    public List<Cluster> getClusters() {
        return cRepository.findAll();
    }

    /**
     * @see org.apromore.service.ClusterService#getClusters(org.apromore.service.model.ClusterFilter)
     * {@inheritDoc}
     */
    @Override
    public List<org.apromore.service.model.Cluster> getClusters(ClusterFilter filter) {
        MemberFragment fragment;
        ProcessAssociation pa;
        FragmentDistance distance;
        List<FragmentVersion> fs;
        List<org.apromore.service.model.Cluster> clusters = new ArrayList<>();

        List<Cluster> cinfos = cRepository.getFilteredClusters(filter);
        for (Cluster cinfo : cinfos) {
            org.apromore.service.model.Cluster c = new org.apromore.service.model.Cluster();
            c.setCluster(cinfo);

            fs = caRepository.findFragmentVersionByClusterId(cinfo.getId());
            for (FragmentVersion f : fs) {
                fragment = new MemberFragment(f.getId());
                fragment.setFragmentSize(f.getFragmentSize());
                for (ProcessModelVersion m : f.getProcessModelVersions()) {
                    pa = new ProcessAssociation();
                    pa.setProcessVersionId(m.getId());
                    pa.setProcessVersionNumber(m.getVersionNumber());
                    pa.setProcessBranchName(m.getProcessBranch().getBranchName());
                    pa.setProcessId(m.getProcessBranch().getProcess().getId());
                    pa.setProcessName(m.getProcessBranch().getProcess().getName());
                    fragment.getProcessAssociations().add(pa);
                }

                distance = fdRepository.findByFragmentVersionId1AndFragmentVersionId2(cinfo.getMedoidId(), f.getId());
                if (distance != null) {
                    fragment.setDistance(distance.getDistance());
                } else {
                    fragment.setDistance(-1d);
                }
                c.addFragment(fragment);
            }
            clusters.add(c);
        }
        return clusters;
    }

    /**
     * @see org.apromore.service.ClusterService#getFragmentIds(Integer)
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getFragmentIds(Integer clusterId) {
        return cRepository.getFragmentIds(clusterId);
    }

    /**
     * @see org.apromore.service.ClusterService#getPairDistances(java.util.List)
     * {@inheritDoc}
     */
    @Override
    public Map<FragmentPair, Double> getPairDistances(List<Integer> fragmentIds) throws RepositoryException {
        FragmentDistance fragmentDistance;
        Map<FragmentPair, Double> pairDistances = new HashMap<>();
        double distance;
        Integer fid1;
        Integer fid2;
        SimpleGraph sg1;
        SimpleGraph sg2;
        SimpleGEDDeterministicGreedyCalc calc;

        for (int i = 0; i < fragmentIds.size() - 1; i++) {
            for (int j = i + 1; j < fragmentIds.size(); j++) {
                fid1 = fragmentIds.get(i);
                fid2 = fragmentIds.get(j);
                fragmentDistance = fdRepository.findByFragmentVersionId1AndFragmentVersionId2(fid1, fid2);

                if (fragmentDistance == null || fragmentDistance.getDistance() < 0) {
                    try {
                        sg1 = new SimpleGraphWrapper(fService.getFragment(fid1, false));
                        sg2 = new SimpleGraphWrapper(fService.getFragment(fid2, false));

                        calc = new SimpleGEDDeterministicGreedyCalc(1, 0.4);
                        distance = calc.compute(sg1, sg2);
                    } catch (LockFailedException e) {
                        throw new RepositoryException(e);
                    }
                } else {
                    distance = fragmentDistance.getDistance();
                }

                FragmentPair pair = new FragmentPair(fid1, fid2);
                pairDistances.put(pair, distance);
            }
        }

        return pairDistances;
    }


    /* Delete the previous cluster run. */
    @Transactional(readOnly = false)
    private void clearClusters() {
        cRepository.deleteAll();
        caRepository.deleteAll();
    }

    /* Delete the previous GED MATRIX run. */
    @Transactional(readOnly = false)
    private void clearGEDMatrix() {
        fdRepository.deleteAll();
    }


}
