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

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.WebAttributes;
import org.slf4j.Logger;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * The Base for all Servlet Request Handlers.
 *
 * @author Cameron James
 * @Since 1.0
 */
public abstract class BaseServletRequestHandler implements HttpRequestHandler {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(BaseServletRequestHandler.class);

    protected static final String FIRSTNAME = "firstname";
    protected static final String SURNAME = "surname";
    protected static final String ORGANIZATION = "organization";
    protected static final String ROLE = "role";
    protected static final String COUNTRY = "country";
    protected static final String PHONE = "phone";
    protected static final String SUBSCRIPTION = "subscription";

    protected static final String EMAIL = "email";
    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";
    protected static final String SECURITY_QUESTION = "securityQuestion";
    protected static final String SECURITY_ANSWER = "securityAnswer";
    protected static final String CONFIRM_PASSWORD = "confirmPassword";

    protected static final String LOGIN_PAGE = "/login.zul";
    protected static final String ERROR_EXTENSION = "?error=3";
    protected static final String USER_EXISTS_EXTENSION = "?error=4";
    protected static final String MESSAGE_EXTENSION = "?success=1";
    protected static final String RESET_EXTENSION = "?success=2";

    private boolean contextRelative;


    /**
     * Removes temporary authentication-related data which may have been stored in the session
     * during the authentication process.
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.REGISTRATION_EXCEPTION);
    }


    /**
     * Redirects the response to the supplied URL.
     * <p>
     * If <tt>contextRelative</tt> is set, the redirect value will be the value after the request context path. Note
     * that this will result in the loss of protocol information (HTTP or HTTPS), so will cause problems if a
     * redirect is being performed to change to HTTPS, for example.
     */
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Redirecting to '" + redirectUrl + "'");
        }

        response.sendRedirect(redirectUrl);
    }

    private String calculateRedirectUrl(String contextPath, String url) {
        if (!UrlUtils.isAbsoluteUrl(url)) {
            if (contextRelative) {
                return url;
            } else {
                return contextPath + url;
            }
        }

        // Full URL, including http(s)://
        if (!contextRelative) {
            return url;
        }

        // Calculate the relative URL from the fully qualified URL, minus the scheme and base context.
        url = url.substring(url.indexOf("://") + 3); // strip off scheme
        url = url.substring(url.indexOf(contextPath) + contextPath.length());

        if (url.length() > 1 && url.charAt(0) == '/') {
            url = url.substring(1);
        }

        return url;
    }

}
