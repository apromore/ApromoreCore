package org.apromore.service;

import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;

import java.util.List;
import java.util.Set;

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
    List<User> findAllUsers();

    /**
     * Find a particular User by their Login name.
     * @param username the username of the user we are searching for.
     * @return the Found User
     * @throws UserNotFoundException when the user can not be found in the system
     */
    User findUserByLogin(String username) throws UserNotFoundException;

    /**
     * Currently only refreshes the users search history. Needs to do more in the future.
     * @param user the user to update.
     */
    void writeUser(User user);

    /**
     * Update the User with the search Histories passed in. We only want the last ten if possible and since this is
     * all the requests the user has made then we remove the previous for the user and replace with these.
     * @param user the user who we are updating the searches for.
     * @param searchHistories the searches the user has executed in the past.
     */
    void updateUserSearchHistory(User user, List<SearchHistory> searchHistories);
}
