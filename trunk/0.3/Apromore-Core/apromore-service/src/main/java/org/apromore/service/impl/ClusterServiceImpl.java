package org.apromore.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

/**
 * Implementation of the ClusterService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("ClusterService")
@Transactional(propagation = Propagation.REQUIRED)
public class ClusterServiceImpl implements ClusterService {

    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fragVersionDao;
    @Autowired @Qualifier("ClusteringDao")
    private ClusteringDao clusteringDao;
    @Autowired @Qualifier("FragmentService")
    private FragmentService fragmentService;
    @Autowired @Qualifier("DistanceMatrix")
    private DistanceMatrix dmatrix;
    @Autowired @Qualifier("DBscanClusterer")
    private InMemoryClusterer dbscanClusterer;


    /**
     * @see org.apromore.service.ClusterService#assignFragments(java.util.List, String)
     * {@inheritDoc}
     */
    @Override
    public void assignFragments(List<String> fragmentIds, String clusterId) {
        for (String frag : fragmentIds) {
            assignFragment(frag, clusterId);
        }
    }

    /**
     * @see org.apromore.service.ClusterService#assignFragment(String, String)
     * {@inheritDoc}
     */
    @Override
    public void assignFragment(String fragmentId, String clusterId) {
        FragmentVersion fragVersion = fragVersionDao.findFragmentVersion(fragmentId);
        fragVersion.setClusterId(clusterId);
        fragVersionDao.update(fragVersion);
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
     * @see org.apromore.service.ClusterService#getCluster(String)
     * {@inheritDoc}
     */
    @Override
    public org.apromore.service.model.Cluster getCluster(String clusterId) {
        Cluster cinfo = clusteringDao.getClusterSummary(clusterId);

        org.apromore.service.model.Cluster c = new org.apromore.service.model.Cluster();
        c.setCluster(cinfo);
        List<FragmentVersion> fs = clusteringDao.getFragments(clusterId);
        for (FragmentVersion f : fs) {
            MemberFragment fragment = new MemberFragment(f.getFragmentVersionId());
            fragment.setFragmentSize(f.getFragmentSize());
            Set<ProcessFragmentMap> pmap = f.getProcessFragmentMaps();
            for (ProcessFragmentMap m : pmap) {
                String pmvid = Integer.toString(m.getProcessModelVersion().getProcessModelVersionId());
                int pmvNumber = m.getProcessModelVersion().getVersionNumber();
                String branchName = m.getProcessModelVersion().getProcessBranch().getBranchName();
                String processId = Integer.toString(m.getProcessModelVersion().getProcessBranch().getProcess().getProcessId());
                String processName = m.getProcessModelVersion().getProcessBranch().getProcess().getName();

                ProcessAssociation pa = new ProcessAssociation();
                pa.setProcessVersionId(pmvid);
                pa.setProcessVersionNumber(Integer.toString(pmvNumber));
                pa.setProcessBranchName(branchName);
                pa.setProcessId(processId);
                pa.setProcessName(processName);
                fragment.getProcessAssociations().add(pa);
            }
            double distance = clusteringDao.getDistance(cinfo.getMedoidId(), f.getFragmentVersionId());
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
            List<FragmentVersion> fs = clusteringDao.getFragments(cinfo.getClusterId());
            for (FragmentVersion f : fs) {
                MemberFragment fragment = new MemberFragment(f.getFragmentVersionId());
                fragment.setFragmentSize(f.getFragmentSize());
                Set<ProcessFragmentMap> pmap = f.getProcessFragmentMaps();
                for (ProcessFragmentMap m : pmap) {
                    String pmvid = Integer.toString(m.getProcessModelVersion().getProcessModelVersionId());
                    int pmvNumber = m.getProcessModelVersion().getVersionNumber();
                    String branchName = m.getProcessModelVersion().getProcessBranch().getBranchName();
                    String processId = Integer.toString(m.getProcessModelVersion().getProcessBranch().getProcess().getProcessId());
                    String processName = m.getProcessModelVersion().getProcessBranch().getProcess().getName();

                    ProcessAssociation pa = new ProcessAssociation();
                    pa.setProcessVersionId(pmvid);
                    pa.setProcessVersionNumber(Integer.toString(pmvNumber));
                    pa.setProcessBranchName(branchName);
                    pa.setProcessId(processId);
                    pa.setProcessName(processName);
                    fragment.getProcessAssociations().add(pa);
                }
                double distance = clusteringDao.getDistance(cinfo.getMedoidId(), f.getFragmentVersionId());
                fragment.setDistance(distance);
                c.addFragment(fragment);
            }
            clusters.add(c);
        }
        return clusters;
    }

    /**
     * @see org.apromore.service.ClusterService#getFragmentIds(String)
     * {@inheritDoc}
     */
    @Override
    public List<String> getFragmentIds(String clusterId) {
        return clusteringDao.getFragmentIds(clusterId);
    }

    /**
     * @see org.apromore.service.ClusterService#getPairDistances(java.util.List)
     * {@inheritDoc}
     */
    @Override
    public Map<FragmentPair, Double> getPairDistances(List<String> fragmentIds) throws RepositoryException {
        Map<FragmentPair, Double> pairDistances = new HashMap<FragmentPair, Double>(0);

        for (int i = 0; i < fragmentIds.size() - 1; i++) {
            for (int j = i + 1; j < fragmentIds.size(); j++) {
                String fid1 = fragmentIds.get(i);
                String fid2 = fragmentIds.get(j);
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
                pairDistances.put(new FragmentPair(fid1, fid2), distance);
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
        fragVersionDao = fragVersionDAOJpa;
    }
}
