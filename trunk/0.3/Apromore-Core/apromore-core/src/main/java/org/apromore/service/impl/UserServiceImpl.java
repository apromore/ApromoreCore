package org.apromore.service.impl;

import org.apromore.dao.UserDao;
import org.apromore.dao.jpa.UserDaoJpa;
import org.apromore.dao.model.User;
import org.apromore.model.UserType;
import org.apromore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao usrDao;


    /**
     * @see org.apromore.service.UserService#findAllUsers()
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    public List<User> findAllUsers() {
        return usrDao.findAllUsers();
    }

    /**
     * @see org.apromore.service.UserService#findUser(String)
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    public User findUser(String username) {
        return usrDao.findUser(username);
    }

    /**
     * @see org.apromore.service.UserService#writeUser(org.apromore.dao.model.User)
     * {@inheritDoc}
     *
     * NOTE: We might need two of these methods, one for the Dao model and another for the WebService models.
     */
    @Override
    public void writeUser(User user) {
        User dbUser = usrDao.findUser(user.getUsername());
        dbUser.setSearchHistories(user.getSearchHistories());
        usrDao.update(dbUser);
    }



    /**
     * Set the User DAO object for this class. Mainly for spring tests.
     * @param usrDAOJpa the user Dao.
     */
    public void setUserDao(UserDaoJpa usrDAOJpa) {
        usrDao = usrDAOJpa;
    }
}
