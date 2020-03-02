/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service;

import org.apromore.dao.model.Group;
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
     * Searches for a group by (approximate) name.
     *
     * @param searchString the name of the group we are searching for
     * @return the candidate groups that were found
     */
    List<Group> searchGroups(String searchString);

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

    /**
     * Update the user password with the new one passed in.
     * @param username the user to find.
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return if success or not.
     */
    boolean changeUserPassword(String username, String oldPassword, String newPassword);
}
