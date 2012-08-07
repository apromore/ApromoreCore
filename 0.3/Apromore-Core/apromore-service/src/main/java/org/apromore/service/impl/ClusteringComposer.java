package org.apromore.service.impl;

import org.apromore.graph.JBPT.CPF;
import org.apromore.service.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("ClusteringComposer")
@Transactional(propagation = Propagation.REQUIRED)
public class ClusteringComposer {

    @Autowired @Qualifier("GraphService")
    private GraphService gSrv;

    private static final Logger log = LoggerFactory.getLogger(ClusteringComposer.class);

    public CPF compose(String fragmentId) {
        log.debug("Composing the content of fragment " + fragmentId);
        CPF g = new CPF();
        gSrv.fillNodesByFragmentId(g, fragmentId);
        gSrv.fillEdgesByFragmentId(g, fragmentId);
        return g;
    }
}
