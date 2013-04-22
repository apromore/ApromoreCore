package org.apromore.service.impl;

import javax.inject.Inject;

import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ComposerService;
import org.apromore.service.GraphService;
import org.apromore.service.helper.OperationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ComposerServiceImpl implements ComposerService {

    private FragmentVersionRepository fragmentVersionRepository;
    private GraphService gService;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param graphService Graphing Services.
     */
    @Inject
    public ComposerServiceImpl(final FragmentVersionRepository fvRepository, final GraphService graphService) {
        fragmentVersionRepository = fvRepository;
        gService = graphService;
    }


    /**
     * Compose a process Model graph from the DB.
     * @param rootFragment the root Fragment we are going to build this model from.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
    @Override
    @Transactional(readOnly = false)
    public Canonical compose(final FragmentVersion rootFragment) throws ExceptionDao {
        OperationContext op = new OperationContext();
        Canonical g = new Canonical();
        op.setGraph(g);
        composeFragment(op, rootFragment);
        return g;
    }

    /**
     * Compose a process Model graph from the DB.
     * @param rootFragmentId the root Fragment Id.
     * @return the process model graph
     * @throws ExceptionDao if something fails.
     */
    @Override
    public Canonical compose(Integer rootFragmentId) throws ExceptionDao {
        OperationContext op = new OperationContext();
        Canonical g = new Canonical();
        op.setGraph(g);
        composeFragment(op, fragmentVersionRepository.findOne(rootFragmentId));
        return g;
    }



    /* Compose a Fragment. */
    private void composeFragment(OperationContext op, FragmentVersion fv) {
        Canonical g = op.getGraph();
        gService.fillNodesByFragment(g, fv.getUri());
        gService.fillEdgesByFragmentURI(g, fv.getUri());
    }
}
