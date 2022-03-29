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

package org.apromore.portal.servlet;


import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.WebAttributes;
import org.apromore.portal.model.MembershipType;
import org.apromore.portal.model.UserType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * HACK to get new user registrations working outside of ZK.
 *
 * Couldn't get zk, spring and bootstrap css to work nicely together so doing this hack.
 * Dont' repeat this hack, spend the time the implement the correct bootstrap, spring and zk integration
 * when ZK 7 is finished.
 *
 * @author Cameron James
 * @since 1.0
 */
@Component("newUserRegistration")
public class NewUserRegistrationHttpServletRequestHandler extends BaseServletRequestHandler {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(NewUserRegistrationHttpServletRequestHandler.class);

    @Autowired
    private ManagerService manager;


    /* (non-Javadoc)
     * @see HttpRequestHandler#handleRequest(HttpServletRequest, HttpServletResponse)
     */
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserType userType;
        String url = LOGIN_PAGE;
        Set<String> messages = new HashSet<>();
        HttpSession session = request.getSession(false);
        request.setCharacterEncoding("UTF-8");

        try {
            if (isUserRequestOk(request, messages)) {
                userType = constructUserType(request);
                manager.writeUser(userType);
                clearAuthenticationAttributes(request);
                url += MESSAGE_EXTENSION;
            } else {
                url += ERROR_EXTENSION;
                if (session != null && !messages.isEmpty()) {
                    request.getSession().setAttribute(WebAttributes.REGISTRATION_EXCEPTION, messages);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error with registering a new user!", e);
            messages.add("Error with registering a new user! Please try again.");
            url += USER_EXISTS_EXTENSION;
        }

        sendRedirect(request, response, url);
    }


    /* Construct the usertype for the portal from the user entered data in the request. */
    private UserType constructUserType(HttpServletRequest request) {
        UserType user = new UserType();
        user.setFirstName(request.getParameter(FIRSTNAME));
        user.setLastName(request.getParameter(SURNAME));
        user.setOrganization(request.getParameter(ORGANIZATION));
        user.setRole(request.getParameter(ROLE));
        user.setCountry(request.getParameter(COUNTRY));
        user.setPhone(request.getParameter(PHONE));
        user.setSubscription(request.getParameter(SUBSCRIPTION));
        user.setUsername(request.getParameter(USERNAME));

        MembershipType membership = new MembershipType();
        membership.setEmail(request.getParameter(EMAIL));
        membership.setPassword(request.getParameter(PASSWORD));
        membership.setPasswordQuestion(request.getParameter(SECURITY_QUESTION));
        membership.setPasswordAnswer(request.getParameter(SECURITY_ANSWER));
        membership.setFailedLogins(0);
        membership.setFailedAnswers(0);
        user.setMembership(membership);

        return user;
    }

    /* Check that all the data is correct and present so we can proceed with user registration. */
    private boolean isUserRequestOk(HttpServletRequest request, Set<String> messages) {
        boolean ok = true;
        if (Strings.isNullOrEmpty(request.getParameter(USERNAME))) {
            ok = false;
            messages.add("Username cannot be empty!");
        }
        if (Strings.isNullOrEmpty(request.getParameter(FIRSTNAME))) {
            ok = false;
            messages.add("First Name cannot be empty!");
        }
        if (Strings.isNullOrEmpty(request.getParameter(SURNAME))) {
            ok = false;
            messages.add("Surname cannot be empty!");
        }
        if (Strings.isNullOrEmpty(request.getParameter(EMAIL))) {
            ok = false;
            messages.add("Email cannot be empty!");
        }
        if (Strings.isNullOrEmpty(request.getParameter(PASSWORD))) {
            ok = false;
            messages.add("Password cannot be empty!");
        }
        if (Strings.isNullOrEmpty(request.getParameter(CONFIRM_PASSWORD))) {
            ok = false;
            messages.add("Confirm Password cannot be empty!");
        }
        if (ok && !request.getParameter(PASSWORD).equals(request.getParameter(CONFIRM_PASSWORD))) {
            ok = false;
            messages.add("Password and Confirm Password do not match!");
        }
        return ok;
    }

}
