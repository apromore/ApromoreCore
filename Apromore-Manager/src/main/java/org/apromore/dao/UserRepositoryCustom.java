package org.apromore.dao;

import org.apromore.dao.model.User;

/**
 * Interface domain model Data access object Workspace.
 *
 * @see org.apromore.dao.model.Workspace
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
public interface UserRepositoryCustom {

    /* ************************** JPA Methods here ******************************* */

    /**
     * Attempts to find the User and check their login details.
     * @param username the username.
     * @param password the password.
     * @return The user if found with a correct password or null.
     */
    User login(final String username, final String password);

    /**
     * Attempts to find the User with the passed in email.
     * @param email the email of the user.
     * @return The user if found with a correct password or null.
     */
    User findUserByEmail(final String email);

    /**
     * Check if user has specific permission.
     * @param userId, the id of the user we are searching for.
     * @param permissionId the id of the permission we are searching for.
     * @return true ro false.
     */
    boolean hasAccess(String userId, String permissionId);



    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
