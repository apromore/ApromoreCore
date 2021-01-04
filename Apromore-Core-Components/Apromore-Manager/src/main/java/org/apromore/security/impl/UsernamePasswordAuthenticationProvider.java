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
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.mapper.UserMapper;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.SecurityService;
import org.springframework.security.authentication.AuthenticationProvider;
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
import java.util.ArrayList;
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
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    @Inject
    private UserRepository userRepository;

    @Inject
    private SecurityService securityService;


    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        if (token.getName() == null || token.getName().length() == 0) {
            throw new UsernameNotFoundException("Username and/or password sent were empty! Not authenticating.");
        }

        try {
            User account = userRepository.login(token.getName(), SecurityUtil.hashPassword((String) token.getCredentials()));

            return authenticatedToken(account, authentication);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Failed to find the user or the password was incorrect!");
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
