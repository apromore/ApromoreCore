package org.apromore.service.impl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.graph.SimpleGraph;
import org.apromore.dao.ClusterAssignmentRepository;
import org.apromore.dao.ClusterRepository;
import org.apromore.dao.FragmentDistanceRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.RepositoryException;
import org.apromore.service.ClusterService;
import org.apromore.service.FragmentService;
import org.apromore.service.helper.SimpleGraphWrapper;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.service.model.MemberFragment;
import org.apromore.service.model.ProcessAssociation;
import org.apromore.toolbox.clustering.DMatrix;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;
import org.apromore.toolbox.clustering.algorithm.dbscan.InMemoryClusterer;
import org.apromore.toolbox.clustering.algorithm.hac.HACClusterer;
import org.apromore.toolbox.clustering.dissimilarity.measure.GEDDissimCalc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private FragmentVersionRepository fvRepository;
    private FragmentDistanceRepository fdRepository;
    private FragmentService fService;

    private InMemoryClusterer dbscanClusterer;
    private HACClusterer hacCluster;

    private DMatrix dmatrix;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param clusterRepository Cluster Repository.
     * @param clusterAssignmentRepository Cluster Assignment Repository.
     * @param fragmentVersionRepository Fragment Version Repository.
     * @param fragmentDistanceRepository Fragment Distance Repository.
     * @param fragmentService Fragment Repository.
     * @param inMemoryClusterer in Memory Clusterer.
     */
    @Inject
    public ClusterServiceImpl(final ClusterRepository clusterRepository,
            final ClusterAssignmentRepository clusterAssignmentRepository, final FragmentVersionRepository fragmentVersionRepository,
            final FragmentDistanceRepository fragmentDistanceRepository, final FragmentService fragmentService,
            final InMemoryClusterer inMemoryClusterer, final HACClusterer hacClusterer, final DMatrix matrix) {
        cRepository = clusterRepository;
        caRepository = clusterAssignmentRepository;
        fvRepository = fragmentVersionRepository;
        fdRepository = fragmentDistanceRepository;
        dbscanClusterer = inMemoryClusterer;
        hacCluster = hacClusterer;
        fService = fragmentService;
        dmatrix = matrix;
    }


    /**
     * @see org.apromore.service.ClusterService#cluster(org.apromore.service.model.ClusterSettings)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void cluster(ClusterSettings settings) throws RepositoryException {
        clearClusters();

        //computeGEDMatrix();

        if (DBSCAN.equals(settings.getAlgorithm())) {
            dbscanClusterer.clusterRepository(settings);
        } else if (HAC.equals(settings.getAlgorithm())) {
            hacCluster.clusterRepository(settings);
        }
    }

    /**
     * @see org.apromore.service.ClusterService#cluster(org.apromore.service.model.ClusterSettings)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void computeGEDMatrix() {
        LOGGER.debug("Computing the GED Matrix....");
        try {
            fdRepository.deleteAll();
            fdRepository.flush();
            dmatrix.compute();
        } catch (Exception e) {
            LOGGER.error("An error occurred while computing the GED matrix for the first time. This could result in lesser number of clusters. PLEASE RERUN THE COMPUTATION.", e);
            e.printStackTrace();
        }
        LOGGER.debug("Completed computing the GED Matrix....");
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
        Cluster cinfo = cRepository.findOne(clusterId);

        org.apromore.service.model.Cluster c = new org.apromore.service.model.Cluster();
        c.setCluster(cinfo);
        List<FragmentVersion> fs = fvRepository.findByClusterId(clusterId);
        for (FragmentVersion f : fs) {
            MemberFragment fragment = new MemberFragment(f.getId());
            fragment.setFragmentSize(f.getFragmentSize());
            Set<ProcessModelVersion> pmap = f.getProcessModelVersions();
            for (ProcessModelVersion m : pmap) {
                Integer pmvid = m.getId();
                Double pmvNumber = m.getVersionNumber();
                String branchName = m.getProcessBranch().getBranchName();
                Integer processId = m.getProcessBranch().getProcess().getId();
                String processName = m.getProcessBranch().getProcess().getName();

                ProcessAssociation pa = new ProcessAssociation();
                pa.setProcessVersionId(pmvid);
                pa.setProcessVersionNumber(pmvNumber);
                pa.setProcessBranchName(branchName);
                pa.setProcessId(processId);
                pa.setProcessName(processName);
                fragment.getProcessAssociations().add(pa);
            }
            fragment.setDistance(fdRepository.findByFragmentVersionId1AndFragmentVersionId2(cinfo.getMedoidId(), f.getId()).getDistance());
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
        List<org.apromore.service.model.Cluster> clusters = new ArrayList<>();
        List<Cluster> cinfos = cRepository.getFilteredClusters(filter);
        for (Cluster cinfo : cinfos) {
            org.apromore.service.model.Cluster c = new org.apromore.service.model.Cluster();
            c.setCluster(cinfo);
            List<FragmentVersion> fs = fvRepository.findByClusterId(cinfo.getId());
            for (FragmentVersion f : fs) {
                MemberFragment fragment = new MemberFragment(f.getId());
                fragment.setFragmentSize(f.getFragmentSize());
                Set<ProcessModelVersion> pmap = f.getProcessModelVersions();
                for (ProcessModelVersion m : pmap) {
                    Integer pmvid = m.getId();
                    Double pmvNumber = m.getVersionNumber();
                    String branchName = m.getProcessBranch().getBranchName();
                    Integer processId = m.getProcessBranch().getProcess().getId();
                    String processName = m.getProcessBranch().getProcess().getName();

                    ProcessAssociation pa = new ProcessAssociation();
                    pa.setProcessVersionId(pmvid);
                    pa.setProcessVersionNumber(pmvNumber);
                    pa.setProcessBranchName(branchName);
                    pa.setProcessId(processId);
                    pa.setProcessName(processName);
                    fragment.getProcessAssociations().add(pa);
                }
                fragment.setDistance(fdRepository.findByFragmentVersionId1AndFragmentVersionId2(cinfo.getMedoidId(), f.getId()).getDistance());
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
        Map<FragmentPair, Double> pairDistances = new HashMap<>(0);
        Integer fid1;
        Integer fid2;
        SimpleGraph sg1;
        SimpleGraph sg2;
        GEDDissimCalc calc;

        for (int i = 0; i < fragmentIds.size() - 1; i++) {
            for (int j = i + 1; j < fragmentIds.size(); j++) {
                fid1 = fragmentIds.get(i);
                fid2 = fragmentIds.get(j);
                double distance = fdRepository.findByFragmentVersionId1AndFragmentVersionId2(fid1, fid2).getDistance();

                if (distance < 0) {
                    try {
                        sg1 = new SimpleGraphWrapper(fService.getFragment(fid1, false));
                        sg2 = new SimpleGraphWrapper(fService.getFragment(fid2, false));

                        calc = new GEDDissimCalc(1, 0.4);
                        distance = calc.compute(sg1, sg2);
                    } catch (LockFailedException e) {
                        throw new RepositoryException(e);
                    }
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



}
