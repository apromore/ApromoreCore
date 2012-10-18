package org.apromore.service.impl;

import nl.tue.tm.is.graph.SimpleGraph;
import org.apromore.clustering.dissimilarity.measure.GEDDissimCalc;
import org.apromore.dao.ClusteringDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessFragmentMap;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.ClusterService;
import org.apromore.service.FragmentService;
import org.apromore.service.helper.SimpleGraphWrapper;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.service.model.MemberFragment;
import org.apromore.service.model.ProcessAssociation;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryClusterer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Implementation of the ClusterService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("ClusterService")
@Transactional(propagation = Propagation.REQUIRED)
public class ClusterServiceImpl implements ClusterService {

    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;
    @Autowired @Qualifier("ClusteringDao")
    private ClusteringDao clusteringDao;
    @Autowired @Qualifier("FragmentService")
    private FragmentService fragmentService;
    @Autowired @Qualifier("DistanceMatrix")
    private DistanceMatrix dmatrix;
    @Autowired @Qualifier("DBscanClusterer")
    private InMemoryClusterer dbscanClusterer;


    /**
     * @see org.apromore.service.ClusterService#assignFragments(java.util.List, Integer)
     * {@inheritDoc}
     */
    @Override
    public void assignFragments(List<Integer> fragmentIds, Integer clusterId) {
        for (Integer frag : fragmentIds) {
            assignFragment(frag, clusterId);
        }
    }

    /**
     * @see org.apromore.service.ClusterService#assignFragment(Integer, Integer)
     * {@inheritDoc}
     */
    @Override
    public void assignFragment(Integer fragmentId, Integer clusterId) {
        FragmentVersion fragVersion = fvDao.findFragmentVersion(fragmentId);
        fragVersion.setCluster(clusteringDao.getCluster(clusterId));
        fvDao.update(fragVersion);
    }

    /**
     * @see org.apromore.service.ClusterService#cluster(org.apromore.service.model.ClusterSettings)
     * {@inheritDoc}
     */
    @Override
    public void cluster(ClusterSettings settings) throws RepositoryException {
        computeDistanceMatrix();
        clusteringDao.clearClusters();
        dbscanClusterer.clusterRepository(settings);
    }

    /**
     * @see org.apromore.service.ClusterService#getClusteringSummary
     * {@inheritDoc}
     */
    @Override
    public ClusteringSummary getClusteringSummary() {
        return clusteringDao.getClusteringSummary();
    }

    /**
     * @see org.apromore.service.ClusterService#getClusterSummaries(org.apromore.service.model.ClusterFilter)
     * {@inheritDoc}
     */
    @Override
    public List<Cluster> getClusterSummaries(ClusterFilter filter) {
        return clusteringDao.getFilteredClusters(filter);
    }

    /**
     * @see org.apromore.service.ClusterService#getCluster(Integer)
     * {@inheritDoc}
     */
    @Override
    public org.apromore.service.model.Cluster getCluster(Integer clusterId) {
        Cluster cinfo = clusteringDao.getClusterSummary(clusterId);

        org.apromore.service.model.Cluster c = new org.apromore.service.model.Cluster();
        c.setCluster(cinfo);
        List<FragmentVersion> fs = clusteringDao.getFragments(clusterId);
        for (FragmentVersion f : fs) {
            MemberFragment fragment = new MemberFragment(f.getId());
            fragment.setFragmentSize(f.getFragmentSize());
            Set<ProcessFragmentMap> pmap = f.getProcessFragmentMaps();
            for (ProcessFragmentMap m : pmap) {
                Integer pmvid = m.getProcessModelVersion().getId();
                Double pmvNumber = m.getProcessModelVersion().getVersionNumber();
                String branchName = m.getProcessModelVersion().getProcessBranch().getBranchName();
                Integer processId = m.getProcessModelVersion().getProcessBranch().getProcess().getId();
                String processName = m.getProcessModelVersion().getProcessBranch().getProcess().getName();

                ProcessAssociation pa = new ProcessAssociation();
                pa.setProcessVersionId(pmvid);
                pa.setProcessVersionNumber(pmvNumber);
                pa.setProcessBranchName(branchName);
                pa.setProcessId(processId);
                pa.setProcessName(processName);
                fragment.getProcessAssociations().add(pa);
            }
            double distance = clusteringDao.getDistance(cinfo.getMedoidId(), f.getId());
            fragment.setDistance(distance);
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
        return clusteringDao.getAllClusters();
    }

    /**
     * @see org.apromore.service.ClusterService#getClusters(org.apromore.service.model.ClusterFilter)
     * {@inheritDoc}
     */
    @Override
    public List<org.apromore.service.model.Cluster> getClusters(ClusterFilter filter) {
        List<org.apromore.service.model.Cluster> clusters = new ArrayList<org.apromore.service.model.Cluster>();
        List<Cluster> cinfos = clusteringDao.getFilteredClusters(filter);
        for (Cluster cinfo : cinfos) {
            org.apromore.service.model.Cluster c = new org.apromore.service.model.Cluster();
            c.setCluster(cinfo);
            List<FragmentVersion> fs = clusteringDao.getFragments(cinfo.getId());
            for (FragmentVersion f : fs) {
                MemberFragment fragment = new MemberFragment(f.getId());
                fragment.setFragmentSize(f.getFragmentSize());
                Set<ProcessFragmentMap> pmap = f.getProcessFragmentMaps();
                for (ProcessFragmentMap m : pmap) {
                    Integer pmvid = m.getProcessModelVersion().getId();
                    Double pmvNumber = m.getProcessModelVersion().getVersionNumber();
                    String branchName = m.getProcessModelVersion().getProcessBranch().getBranchName();
                    Integer processId = m.getProcessModelVersion().getProcessBranch().getProcess().getId();
                    String processName = m.getProcessModelVersion().getProcessBranch().getProcess().getName();

                    ProcessAssociation pa = new ProcessAssociation();
                    pa.setProcessVersionId(pmvid);
                    pa.setProcessVersionNumber(pmvNumber);
                    pa.setProcessBranchName(branchName);
                    pa.setProcessId(processId);
                    pa.setProcessName(processName);
                    fragment.getProcessAssociations().add(pa);
                }
                double distance = clusteringDao.getDistance(cinfo.getMedoidId(), f.getId());
                fragment.setDistance(distance);
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
        return clusteringDao.getFragmentIds(clusterId);
    }

    /**
     * @see org.apromore.service.ClusterService#getPairDistances(java.util.List)
     * {@inheritDoc}
     */
    @Override
    public Map<FragmentPair, Double> getPairDistances(List<Integer> fragmentIds) throws RepositoryException {
        Map<FragmentPair, Double> pairDistances = new HashMap<FragmentPair, Double>(0);

        for (int i = 0; i < fragmentIds.size() - 1; i++) {
            for (int j = i + 1; j < fragmentIds.size(); j++) {
                Integer fid1 = fragmentIds.get(i);
                Integer fid2 = fragmentIds.get(j);
                double distance = clusteringDao.getDistance(fid1, fid2);
                if (distance < 0) {

                    try {
                        CPF g1 = fragmentService.getFragment(fid1, false);
                        CPF g2 = fragmentService.getFragment(fid2, false);

                        SimpleGraph sg1 = new SimpleGraphWrapper(g1);
                        SimpleGraph sg2 = new SimpleGraphWrapper(g2);

                        GEDDissimCalc calc = new GEDDissimCalc(1, 0.4);
                        distance = calc.compute(sg1, sg2);

                    } catch (LockFailedException e) {
                        throw new RepositoryException(e);
                    }
                }

                FragmentPair pair = new FragmentPair(fvDao.findFragmentVersion(fid1), fvDao.findFragmentVersion(fid2));
                pairDistances.put(pair, distance);
            }
        }

        return pairDistances;
    }


    /* Computers the fragment distances. */
    private void computeDistanceMatrix() {
        try {
            dmatrix.compute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Set the Fragment Version DAO object for this class. Mainly for spring tests.
     *
     * @param fragVersionDAOJpa the Fragment Version Dao.
     */
    public void setFragmentVersionDao(FragmentVersionDao fragVersionDAOJpa) {
        fvDao = fragVersionDAOJpa;
    }
}
