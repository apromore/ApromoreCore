package org.apromore.service.impl;

import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.ComposerService;
import org.apromore.service.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("SimpleComposerService")
@Transactional(propagation = Propagation.REQUIRED)
public class SimpleComposerImpl implements ComposerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleComposerImpl.class);

    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;
    @Autowired @Qualifier("GraphService")
    private GraphService gSrv;

    /**
     * @see ComposerService#compose(String)
     * {@inheritDoc}
     */
    @Override
    public CPF compose(String fragmentUri) {
        LOGGER.debug("Composing the content of fragment " + fragmentUri);
        CPF g = new CPF();
        FragmentVersion fv = fvDao.findFragmentVersionByURI(fragmentUri);
        gSrv.fillNodesByFragmentId(g, fv.getId());
        gSrv.fillEdgesByFragmentId(g, fv.getId());
        return g;
    }
}
