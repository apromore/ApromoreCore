/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

package org.apromore.security.impl;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.security.model.ApromorePermissionDetails;
import org.apromore.security.model.ApromoreRoleDetails;
import org.apromore.security.model.ApromoreSearchHistoryDetails;
import org.apromore.security.model.ApromoreUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <tt>UserDetailsServiceImpl</tt> implementation which retrieves the user details
 * (username, password, enabled flag, and authorities) from a database using JDBC queries.
 *
 * @author Cameron James
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserRepository userRepo;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param userRepository User Repository.
     */
    @Inject
    public UserDetailsServiceImpl(final UserRepository userRepository) {
        userRepo = userRepository;
    }

    /**
     * Load the User detail record from the username passed in.
     * @param username the user we are looking for.
     * @return the userDetails object required by Apromore.
     * @throws UsernameNotFoundException if the user isn't in the system.
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = loadUserDetailsByUsername(username);

        if (user.getAuthorities().size() == 0) {
            LOGGER.debug("User '" + username + "' has no authorities and will be treated as 'not found'");

            throw new UsernameNotFoundException("User " + username + " has no GrantedAuthority");
        }

        return user;
    }

    /**
     * Executes the SQL <tt>usersByUsernameQuery</tt> and returns a list of UserDetails objects.
     * There should normally only be one matching user.
     */
    protected UserDetails loadUserDetailsByUsername(String username) {
        Set<ApromoreRoleDetails> dbRoleSet = new HashSet<>();
        Set<ApromorePermissionDetails> dbPermSet = new HashSet<>();
        Set<ApromoreSearchHistoryDetails> dbSearchSet = new HashSet<>();
        Set<GrantedAuthority> dbAuthsSet = new HashSet<>();
        User usr = userRepo.findByUsername(username);
        Membership membership = usr.getMembership();

        for (Role role : usr.getRoles()) {
            dbAuthsSet.add(new SimpleGrantedAuthority(role.getName()));
            dbRoleSet.add(new ApromoreRoleDetails(role.getRowGuid(), role.getName()));

            for (Permission permission : role.getPermissions()) {
                dbPermSet.add(new ApromorePermissionDetails(permission.getRowGuid(), permission.getName()));
            }
        }
        for (SearchHistory searchHistory : usr.getSearchHistories()) {
            dbSearchSet.add(new ApromoreSearchHistoryDetails(searchHistory.getId(), searchHistory.getSearch()));
        }

        return new ApromoreUserDetails(usr.getRowGuid(), usr.getFirstName(), usr.getLastName(), username, membership.getPassword(),
                !membership.getIsLocked(), true, true, true, membership.getEmail(), dbAuthsSet, dbRoleSet, dbPermSet, dbSearchSet);
    }

}
