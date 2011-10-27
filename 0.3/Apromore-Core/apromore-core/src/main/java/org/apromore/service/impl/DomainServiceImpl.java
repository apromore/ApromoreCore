package org.apromore.service.impl;

import org.apromore.dao.ProcessDao;
import org.apromore.dao.jpa.ProcessDaoJpa;
import org.apromore.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the DomainService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
public class DomainServiceImpl implements DomainService {

    @Autowired
    private ProcessDao procDao;


    /**
     * @see org.apromore.service.DomainService#findAllDomains()
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    public List<String> findAllDomains() {
        List<Object> domains = procDao.getAllDomains();
        List<String> doms = new ArrayList<String>();

        for (Object obj : domains) {
            doms.add((String) obj);
        }

        return doms;
    }



    /**
     * Set the Process DAO object for this class. Mainly for spring tests.
     * @param processDAOJpa the user Dao.
     */
    public void setProcDao(ProcessDaoJpa processDAOJpa) {
        procDao = processDAOJpa;
    }
}
