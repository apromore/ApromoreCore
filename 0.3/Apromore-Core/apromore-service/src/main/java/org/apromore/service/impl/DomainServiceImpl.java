package org.apromore.service.impl;

import org.apromore.dao.ProcessDao;
import org.apromore.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the DomainService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("DomainService")
@Transactional(propagation = Propagation.REQUIRED)
public class DomainServiceImpl implements DomainService {

    @Autowired @Qualifier("ProcessDao")
    private ProcessDao procDao;


    /**
     * @see org.apromore.service.DomainService#findAllDomains()
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> findAllDomains() {
        return procDao.getAllDomains();
    }



    /**
     * Set the Process DAO object for this class. Mainly for spring tests.
     * @param processDAOJpa the user Dao.
     */
    public void setProcDao(ProcessDao processDAOJpa) {
        procDao = processDAOJpa;
    }
}
