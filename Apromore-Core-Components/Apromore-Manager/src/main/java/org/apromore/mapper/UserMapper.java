/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.mapper;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.portal.model.*;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class UserMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMapper.class.getName());

    /**
     * Convert the List of Users to a UserNamesType Webservice object.
     * @param users the list of Users
     * @return the UsernameType object
     */
    public static UsernamesType convertUsernameTypes(List<User> users) {
        UsernamesType userNames = new UsernamesType();
        for (User usr : users) {
            userNames.getUsername().add(usr.getUsername());
        }
        return userNames;
    }

    /**
     * Convert a user object to a UserType Webservice object.
     * @param user the DB User Model
     * @param securityService  used to look up the <var>user</var> roles
     * @return the Webservice UserType
     */
    public static UserType convertUserTypes(User user, SecurityService securityService) {
        if (user == null) {
            return null;
        }

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        
        UserType userType = new UserType();
        userType.setId(user.getRowGuid());
        userType.setLastName(user.getLastName());
        userType.setFirstName(user.getFirstName());
        userType.setOrganization(user.getOrganization());
        userType.setRole(user.getRole());
        userType.setCountry(user.getCountry());
        userType.setPhone(user.getPhone());
        userType.setSubscription(user.getSubscription());
        userType.setUsername(user.getUsername());
        if (user.getLastActivityDate() != null) {
            userType.setLastActivityDate(formatter.format(user.getLastActivityDate()));
        }

        for (Role role : user.getRoles()) {
            RoleType newRole = new RoleType();
            newRole.setId(role.getRowGuid());
            newRole.setName(role.getName());            
            userType.getRoles().add(newRole);

            for (Permission permission : role.getPermissions()) {
                PermissionType permissionType = PermissionType.getPermissionTypeById(permission.getRowGuid());
                
                if (!userType.getPermissions().contains(permissionType)){
                    userType.getPermissions().add(permissionType);
                }
            }
        }

        List<SearchHistory> searchHistories = user.getSearchHistories();
        if (searchHistories != null) {
            for (SearchHistory searchHistory: searchHistories) {
                SearchHistoriesType searchHistoriesType = new SearchHistoriesType();
                searchHistoriesType.setSearch(searchHistory.getSearch());
                searchHistoriesType.setNum(searchHistory.getIndex());
                userType.getSearchHistories().add(searchHistoriesType);
            }
        }

        Membership membership = user.getMembership();
        if (membership != null){
            MembershipType membershipType = new MembershipType();
            membershipType.setEmail(membership.getEmail());
            membershipType.setApproved(membership.getIsApproved());
            membershipType.setLocked(membership.getIsLocked());
            membershipType.setFailedLogins(membership.getFailedPasswordAttempts());
            membershipType.setFailedAnswers(membership.getFailedAnswerAttempts());
            userType.setMembership(membershipType);
        }
        
        return userType;
    }
    
    

    /**
     * Convert from the WS (UserType) to the DB model (User).
     * @param userType the userType from the WebService
     * @param securityService  used to look up the <var>user</var> roles
     * @return the User dao model populated.
     */
    public static User convertFromUserType(UserType userType, SecurityService securityService) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        if (userType.getLastActivityDate() != null && !userType.getLastActivityDate().equals("")) {
            try {
                date = formatter.parse(userType.getLastActivityDate());
            } catch (ParseException ex) {
                LOGGER.error("Last Activity Date: " + userType.getLastActivityDate() + " could not be parsed.");
            }
        }

        User user = new User();
        user.setLastName(userType.getLastName());
        user.setFirstName(userType.getFirstName());
        user.setUsername(userType.getUsername());
        user.setOrganization(userType.getOrganization());
        user.setRole(userType.getRole());
        user.setCountry(userType.getCountry());
        user.setPhone(userType.getPhone());
        user.setSubscription(userType.getSubscription());
        user.setRowGuid(userType.getId());
        if (date != null){
            user.setLastActivityDate(date);
        }
        if (user.getSearchHistories() != null) {
            user.setSearchHistories(SearchHistoryMapper.convertFromSearchHistoriesType(userType.getSearchHistories()));
        }
        
        Membership membership = new Membership();
        if (userType.getMembership() != null) {
            membership.setDateCreated(new Date());
            membership.setEmail(userType.getMembership().getEmail());
            membership.setQuestion(userType.getMembership().getPasswordQuestion());
            membership.setAnswer(userType.getMembership().getPasswordAnswer());
            membership.setFailedPasswordAttempts(0);
            membership.setFailedAnswerAttempts(0);
            membership.setUser(user);

            if (userType.getMembership().getPassword() != null) {
                try {
                    securityService.updatePassword(membership, userType.getMembership().getPassword());

                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalArgumentException("Unable to hash password for " + userType.getUsername(), e);
                }
            } else {
                membership.setPassword("");
            }

            user.setMembership(membership);
        }

        for (RoleType roleType : userType.getRoles()){
            user.getRoles().add(securityService.findRoleByName(roleType.getName()));
        }

        return user;
    }

}
