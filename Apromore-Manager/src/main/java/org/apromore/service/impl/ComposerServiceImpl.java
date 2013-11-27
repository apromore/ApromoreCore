package org.apromore.service.impl;

import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ComposerService;
import org.apromore.service.GraphService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ComposerServiceImpl implements ComposerService {

    @Inject
    private FragmentVersionRepository fragmentVersionRepository;
    @Inject
    private GraphService gService;



    /**
     * Compose a process Model graph from the DB.
     *
     * @param rootFragment the root Fragment we are going to build this model from.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
    @Override
    @Transactional(readOnly = false)
    public Canonical compose(final FragmentVersion rootFragment) throws ExceptionDao {
        return composeFragment(rootFragment);
    }

    /**
     * Compose a process Model graph from the DB.
     *
     * @param rootFragmentId the root Fragment Id.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
    @Override
    @Transactional(readOnly = false)
    public Canonical compose(Integer rootFragmentId) throws ExceptionDao {
        return composeFragment(fragmentVersionRepository.findOne(rootFragmentId));
    }


    /* Compose a Fragment. */
    private Canonical composeFragment(FragmentVersion fv) {
        Canonical canonical = new Canonical();
        canonical = gService.fillNodesByFragment(canonical, fv.getUri());
        canonical = gService.fillEdgesByFragmentURI(canonical, fv.getUri());
        return canonical;
    }

}
