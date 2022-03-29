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


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.WebAttributes;
import org.apromore.portal.model.UserType;
import org.apromore.portal.util.RandomPasswordGenerator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

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
@Component("resetPassword")
public class ResetPasswordHttpServletRequestHandler extends BaseServletRequestHandler {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ResetPasswordHttpServletRequestHandler.class);

    @Autowired
    private ManagerService manager;

    private static final int minLength = 5;
    private static final int maxLength = 8;
    private static final int noCapitals = 1;
    private static final int noDigits = 1;
    private static final int noSpecial = 1;


    /* (non-Javadoc)
     * @see HttpRequestHandler#handleRequest(HttpServletRequest, HttpServletResponse)
     */
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserType userType;
        String url = LOGIN_PAGE;
        Set<String> messages = new HashSet<>();
        HttpSession session = request.getSession(false);

        try {
            if (isUserRequestOk(request, messages)) {
                userType = manager.readUserByEmail(request.getParameter(USERNAME));
                String newPwd = new String(RandomPasswordGenerator.generatePassword(minLength, maxLength, noCapitals, noDigits, noSpecial));
                if (resetUsersPassword(userType, newPwd)) {
                    clearAuthenticationAttributes(request);
                } else {
                    throw new Exception("Saving password failed");
                }
                url += RESET_EXTENSION;
            } else {
                if (session != null && !messages.isEmpty()) {
                    request.getSession().setAttribute(WebAttributes.REGISTRATION_EXCEPTION, messages);
                }
                url += ERROR_EXTENSION;
            }
        } catch (Exception e) {
            LOGGER.error("Error with resetting a users password!", e);
            messages.add("Error with resetting a users password! Please try again.");
            url += ERROR_EXTENSION;
        }

        sendRedirect(request, response, url);
    }



    /* Reset the password and update the userType record. */
    private boolean resetUsersPassword(UserType userType, String newPswd) {
        return manager.resetUserPassword(userType.getUsername(), newPswd);
    }

    /* Check that all the data is correct and present so we can proceed with user registration. */
    private boolean isUserRequestOk(HttpServletRequest request, Set<String> messages) {
        boolean ok = true;
        if (Strings.isNullOrEmpty(request.getParameter(USERNAME))) {
            ok = false;
            messages.add("Username cannot be empty!");
        }
        return ok;
    }

}
