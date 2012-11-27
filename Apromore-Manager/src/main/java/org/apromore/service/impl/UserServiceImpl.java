package org.apromore.service.impl;

import org.apromore.dao.UserRepository;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.inject.Inject;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param userRepository User Repository.
     */
    @Inject
    public UserServiceImpl(final UserRepository userRepository) {
        userRepo = userRepository;
    }



    /**
     * @see org.apromore.service.UserService#findAllUsers()
     *      {@inheritDoc}
     *      <p/>
     *      NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @see org.apromore.service.UserService#findUserByLogin(String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User findUserByLogin(String username) throws UserNotFoundException {
        User user = userRepo.findUserByLogin(username);
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException("User with username (" + username + ") could not be found.");
        }
    }

    /**
     * @see org.apromore.service.UserService#writeUser(org.apromore.dao.model.User)
     *      {@inheritDoc}
     */
    @Override
    public void writeUser(User user) {
        User dbUser = userRepo.findUserByLogin(user.getUsername());
        dbUser.setSearchHistories(user.getSearchHistories());
        userRepo.save(dbUser);
    }

}
