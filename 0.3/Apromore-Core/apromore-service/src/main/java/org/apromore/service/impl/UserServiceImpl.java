package org.apromore.service.impl;

import org.apromore.dao.UserDao;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("UserService")
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService {

    @Autowired @Qualifier("UserDao")
    private UserDao usrDao;


    /**
     * @see org.apromore.service.UserService#findAllUsers()
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return usrDao.findAllUsers();
    }

    /**
     * @see org.apromore.service.UserService#findUser(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User findUser(String username) throws UserNotFoundException {
        User user = usrDao.findUser(username);
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException("User with username (" + username + ") could not be found.");
        }
    }

    /**
     * @see org.apromore.service.UserService#writeUser(org.apromore.dao.model.User)
     * {@inheritDoc}
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
    public void setUserDao(UserDao usrDAOJpa) {
        usrDao = usrDAOJpa;
    }
}
