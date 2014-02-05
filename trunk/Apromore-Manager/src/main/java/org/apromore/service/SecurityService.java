package org.apromore.service;

import org.apromore.dao.model.Permission;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;

import java.util.List;

/**
 * Interface for the User Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface SecurityService {

    /**
     * Gets all the users in the system and returns them in Username sort order.
     * @return a List of users in the system.
     */
    List<User> getAllUsers();

    /**
     * Get a particular User.
     * @param username the username of the user we are searching for.
     * @return the Found User
     */
    User getUserByName(String username);

    /**
     * Searches user by username.
     * @param searchString the username of the user we are searching for.
     * @return the username of the user we are searching for.
     */
    List<User> searchUsers(String searchString);

    /**
     * Get a particular User by their email.
     * @param email the username of the user we are searching for.
     * @return the logged in User         
     */
    User getUserByEmail(String email);

    /**
     * Get a particular User.
     * @param guid the unique id of the user we are searching for.
     * @return the Found User
     * @throws org.apromore.exception.UserNotFoundException when the user can not be found in the system
     */
    User getUserById(String guid) throws UserNotFoundException;

    /**
     * Gets all user permissions.
     * @param guid the users Globally Unique Id
     * @return a List of permissions for the specific user.
     */
    List<Permission> getUserPermissions(String guid);

    /**
     * Gets all user permissions.
     * @return a List of permissions for the specific user.
     */
    User createUser(User user);

    /**
     * Checks whether user has specific permission.
     * @return a List of permissions for the specific user.
     */
    boolean hasAccess(String userId, String permissionId);

    /**
     * Update the user password with the new one passed in.
     * @param username the user to find.
     * @param password the new password
     * @return if success or not.
     */
    boolean resetUserPassword(String username, String password);
}
