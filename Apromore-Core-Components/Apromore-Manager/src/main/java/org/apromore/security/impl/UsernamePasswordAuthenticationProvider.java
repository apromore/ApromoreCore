/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.mapper.UserMapper;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Adapts {@link org.apromore.dao.UserRepository#login(String, String)} to the SpringSecurity AuthenticationProvider SPI.
 * Allows the AccountRepository to drive authentication in a Spring Security environment.
 * The authenticated Account is treated as the {@link org.springframework.security.core.Authentication#getPrincipal() Authentication Principal}.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsernamePasswordAuthenticationProvider.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private SecurityService securityService;

    @Value("${allowedPasswordHashingAlgorithms}")
    private String allowedPasswordHashingAlgorithms;

    @Value("${passwordHashingAlgorithm}")
    private String passwordHashingAlgorithm;

    @Value("${saltLength}")
    private int saltLength;

    @Value("${upgradePasswords}")
    private boolean upgradePasswords;

    /**
     * Authentication policy for accounts stored in the local database.
     *
     * The {@link #allowedPasswordHashingAlgorithms} configuration lists the hashing
     * algorithms which can be used to authenticate.
     * If the {@link #upgradePasswords} configuration is set, this implementation
     * will transparently upgrade password hashes to {@link #passwordHashingAlgorithm}
     * or hashes with salt less than the required length of {@link #saltLength}.
     *
     * @param authentication  must be a {@link UsernamePasswordAuthenticationToken}
     * @throws UsernameNotFoundException if the <var>authentication</var>'s name isn't
     *   either an existing username or email address
     * @throws BadCredentialsException if the <var>authentication</var>'s credentials
     *   aren't the correct password
     * @throws InternalAuthenticationServiceException if the password hash stored in the
     *   database isn't a valid hexadecimal string
     */
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        User user = findUser(token.getName());
        Membership membership = user.getMembership();
        String password = (String) token.getCredentials();

        // Check that the password hashing algorithm is one we accept
        if (!Arrays.asList(allowedPasswordHashingAlgorithms.split(",")).contains(membership.getHashingAlgorithm())) {
            throw new InternalAuthenticationServiceException("Unacceptable legacy password hash " +
                membership.getHashingAlgorithm() + " for user " + user.getUsername());
        }

        // Parse the password field from the membership
        try {
            // Authenticate
            if (!SecurityUtil.authenticate(membership, password)) {
                throw new BadCredentialsException("Incorrect password");
            }

            // Although we've accepted these credentials now, we may tighten our criteria in the future.
            // Since this is the only time we know the cleartext password, it's our opportunity to
            // transparently upgrade the hashing scheme.  Otherwise we have to wait until the next login.

            if (!membership.getHashingAlgorithm().equals(passwordHashingAlgorithm)) {
                // Rehash because the current hash doesn't use our preferred algorithm
                LOGGER.info("User \"{}\" authenticated with {} instead of {}", user.getUsername(),
                    membership.getHashingAlgorithm(), passwordHashingAlgorithm);
                rehash(user, password);

            } else if (membership.getSalt().length() < saltLength) {
                // Rehash because the current salt is below the configured size
                LOGGER.info("User \"{}\" has a password with {} characters of salt, less than the configured threshold of {}.",
                    user.getUsername(), membership.getSalt().length(), saltLength);
                rehash(user, password);
            }

            return authenticatedToken(user, authentication);

        } catch (NoSuchAlgorithmException e) {
            throw new InternalAuthenticationServiceException("Unsupported algorithm " + membership.getHashingAlgorithm() +
                " for password hash for user " + user.getUsername(), e);
        }
    }

    /**
     * Find an account by name.
     *
     * If the name provided in the login credentials does not match any username, we will fall back to matching
     * it against email addresses.
     *
     * @param name  either a username or an email address identifying a local user account, never <code>null</code>
     *     or empty
     * @return the user, never <code>null</code>
     * @throws UsernameNotFoundException if <var>name</var> is <code>null</code>, empty, or doesn't match any
     *     existing username or email address
     */
    private User findUser(final String name) throws UsernameNotFoundException {
        if (name == null || name.isEmpty()) {
            throw new UsernameNotFoundException("Empty username");
        }

        try {
            return userRepository.findByUsername(name);

        } catch (NoResultException e) {
            User user = userRepository.findUserByEmail(name);
            if (user == null) {
                throw new UsernameNotFoundException("Neither a username nor email of any existing user: " + name);
            }
            return user;
        }
    }

    /**
     * This method retains the user's existing password, but may upgrade its salt and hash depending on configuration.
     *
     * No change will occur if {@link #upgradePasswords} is not true.
     *
     * @param user  whose password to resalt
     * @param password  the current cleartext password for the <var>user</var>
     */
    private void rehash(final User user, final String password) {
        if (!upgradePasswords) {
            LOGGER.debug("Retaining the existing password hash because upgrading is not enabled.");
        } else if (securityService.changeUserPassword(user.getUsername(), password, password)) {
            LOGGER.info("Rehashed password for user {}", user.getUsername());
        } else {
            LOGGER.warn("Failed to rehash password for user {}", user.getUsername());
        }
    }

    public boolean supports(Class<? extends Object> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    private Authentication authenticatedToken(User user, Authentication original) {
        List<GrantedAuthority> authorities = getAuthorities(user.getRoles());
        UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(original.getPrincipal(),
                original.getCredentials(), authorities);
        authenticated.setDetails(UserMapper.convertUserTypes(user, securityService));

        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        return authenticated;
    }

    /**
     * Retrieves the correct ROLE type depending on the access level, where access level is an Integer.
     * Basically, this interprets the access value whether it's for a regular user or admin.
     *
     * @param access an integer value representing the access of the user
     * @return collection of granted authorities
     */
    public List<GrantedAuthority> getAuthorities(Set<Role> access) {
        List<GrantedAuthority> authList = new ArrayList<>();
        for (Role role : access) {
            authList.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authList;
    }

}
