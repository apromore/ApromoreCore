package org.apromore.service.impl;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ComposerServiceImpl implements ComposerService {

    private static final int CONCURRENCY_LEVEL = 4;
    private static final int CACHE_SIZE = 100000;
    private static final int EXPIRATION_TIME = 180;

    private FragmentVersionRepository fragmentVersionRepository;
    private GraphService gService;

    private Cache<Integer, Canonical> cache;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param graphService Graphing Services.
     */
    @Inject
    public ComposerServiceImpl(final FragmentVersionRepository fvRepository, final GraphService graphService) {
        fragmentVersionRepository = fvRepository;
        gService = graphService;

        cache = CacheBuilder.newBuilder().concurrencyLevel(CONCURRENCY_LEVEL).maximumSize(CACHE_SIZE).softValues()
                .expireAfterWrite(EXPIRATION_TIME, TimeUnit.SECONDS).build();
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
        return composeFragment(rootFragment);
    }

    /**
     * Compose a process Model graph from the DB.
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
        Canonical canonical = cache.getIfPresent(fv.getId());
        if (canonical == null) {
            canonical = new Canonical();
            gService.fillNodesByFragment(canonical, fv.getUri());
            gService.fillEdgesByFragmentURI(canonical, fv.getUri());
            cache.put(fv.getId(), canonical);
        }
        return canonical;
    }

}
