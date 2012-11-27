package org.apromore.service.impl;

import org.apromore.dao.ProcessRepository;
import org.apromore.service.DomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.inject.Inject;

/**
 * Implementation of the DomainService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class DomainServiceImpl implements DomainService {

    private ProcessRepository pRepository;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param processRepository Process Repository.
     */
    @Inject
    public DomainServiceImpl(final ProcessRepository processRepository) {
        pRepository = processRepository;
    }


    /**
     * @see org.apromore.service.DomainService#findAllDomains()
     *      {@inheritDoc}
     *      <p/>
     *      NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> findAllDomains() {
        return pRepository.getAllDomains();
    }

}
