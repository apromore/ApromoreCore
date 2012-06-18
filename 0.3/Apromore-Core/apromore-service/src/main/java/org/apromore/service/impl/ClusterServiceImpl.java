package org.apromore.service.impl;

import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * @see org.apromore.service.ClusterService#clearClusters()
     * {@inheritDoc}
     */
    @Override
    public void clearClusters() {
        List<FragmentVersion> frags = fragVersionDao.getAllFragmentVersion();
        for (FragmentVersion frag : frags) {
            frag.setClusterId("UNCLASSIFIED");
            fragVersionDao.update(frag);
        }
    }




    /**
     * Set the Fragment Version DAO object for this class. Mainly for spring tests.
     * @param fragVersionDAOJpa the Fragment Version Dao.
     */
    public void setFragmentVersionDao(FragmentVersionDao fragVersionDAOJpa) {
        fragVersionDao = fragVersionDAOJpa;
    }
}
