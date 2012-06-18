package org.apromore.dao;

import org.apromore.dao.model.User;

import java.util.List;

/**
 * Interface domain model Data access object User.
 *
 * @see org.apromore.dao.model.User
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface UserDao {

    /**
     * Finds a User in the System.
     * @param username the username of the user we are searching for.
     * @return the username of the user we are searching for.
     */
    User findUser(String username);

    /**
     * Returns a list of all the Users found in the system.
     * @return a collection (List) of Users found in the system.
     */
    List<User> findAllUsers();


    /**
     * Save the user.
     * @param user the user to persist
     */
    void save(User user);

    /**
     * Update the user.
     * @param user the user to update
     */
    User update(User user);

    /**
     * Remove the user.
     * @param user the user to remove
     */
    void delete(User user);
}
