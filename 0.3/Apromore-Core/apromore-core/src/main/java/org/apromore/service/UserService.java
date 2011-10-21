package org.apromore.service;

import org.apromore.dao.model.User;
import org.apromore.model.UserType;

import java.util.List;

/**
 * Interface for the User Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface UserService {

    /**
     * Finds all the users in the system and returns them in Username sort order.
     * @return a List of users in the system.
     */
    List<User> findAllUsers ();

    /**
     * Find a particular User.
     * @param username the username of the user we are searching for.
     * @return the Found User
     */
    User findUser (String username);

    /**
     * Currently only refreshes the users search history. Needs to do more in the future.
     * @param user the user to update.
     */
    void writeUser (User user);
}
