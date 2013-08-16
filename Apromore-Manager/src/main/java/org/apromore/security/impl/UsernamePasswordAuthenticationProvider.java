////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2006, Callisto Enterprises. All rights reserved.
//
// This is unpublished proprietary source code of Callisto Enterprises.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
////////////////////////////////////////////////////////////////////////////////
package org.apromore.security.impl;

import javax.inject.Inject;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.mapper.UserMapper;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapts {@link org.apromore.dao.UserRepository#login(String, String)} to the SpringSecurity AuthenticationProvider SPI.
 * Allows the AccountRepository to drive authentication in a Spring Security environment.
 * The authenticated Account is treated as the {@link org.springframework.security.core.Authentication#getPrincipal() Authentication Principal}.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private UserRepository userRepository;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param userRepo User Repository.
     */
    @Inject
    public UsernamePasswordAuthenticationProvider(final UserRepository userRepo) {
        userRepository = userRepo;
    }


    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        if (token.getName() == null || token.getName().length() == 0) {
            throw new UsernameNotFoundException("Username and/or password sent were empty! Not authenticating.");
        }

        User account = userRepository.login(token.getName(), hashPassword((String) token.getCredentials()));

        return authenticatedToken(account, authentication);
    }

    public boolean supports(Class<? extends Object> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    private Authentication authenticatedToken(User user, Authentication original) {
        List<GrantedAuthority> authorities = getAuthorities(user.getRoles());
        UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(original.getPrincipal(),
                original.getCredentials(), authorities);
        authenticated.setDetails(UserMapper.convertUserTypes(user));
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


    public String hashPassword(String password) {
        String hashword = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);
        } catch (NoSuchAlgorithmException nsae) {
            // ignore
        }
        return hashword;
    }
}