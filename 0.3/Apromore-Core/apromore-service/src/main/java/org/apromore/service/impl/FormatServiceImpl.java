package org.apromore.service.impl;

import org.apromore.dao.NativeTypeDao;
import org.apromore.dao.UserDao;
import org.apromore.dao.jpa.NativeTypeDaoJpa;
import org.apromore.dao.jpa.UserDaoJpa;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.User;
import org.apromore.service.FormatService;
import org.apromore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the FormatService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class FormatServiceImpl implements FormatService {

    @Autowired
    private NativeTypeDao natTypeDao;


    /**
     * @see org.apromore.service.UserService#findAllUsers()
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NativeType> findAllFormats() {
        return natTypeDao.findAllFormats();
    }



    /**
     * Set the Native Type DAO object for this class. Mainly for spring tests.
     * @param nativeTypeDAOJpa the user Dao.
     */
    public void setNativeTypeDao(NativeTypeDaoJpa nativeTypeDAOJpa) {
        natTypeDao = nativeTypeDAOJpa;
    }
}
