package org.apromore.security.impl;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.security.model.ApromorePermissionDetails;
import org.apromore.security.model.ApromoreRoleDetails;
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
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
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

        return new ApromoreUserDetails(usr.getRowGuid(), usr.getFirstName(), usr.getLastName(), username, membership.getPassword(),
                !membership.getIsLocked(), true, true, true, membership.getEmail(), dbAuthsSet, dbRoleSet, dbPermSet);
    }

}
