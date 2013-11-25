package org.apromore.portal.servlet;

import org.apache.commons.lang.StringUtils;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.MembershipType;
import org.apromore.model.UserType;
import org.apromore.portal.common.WebAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

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
 */
@Component("newUserRegistration")
public class NewUserRegistrationHttpServletRequestHandler implements HttpRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewUserRegistrationHttpServletRequestHandler.class);

    private static final String FIRSTNAME = "firstname";
    private static final String SURNAME = "surname";
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SECURITY_QUESTION = "securityQuestion";
    private static final String SECURITY_ANSWER = "securityAnswer";
    private static final String CONFIRM_PASSWORD = "confirmPassword";

    private static final String LOGIN_PAGE = "/login.zul";
    private static final String ERROR_EXTENSION = "?error=3";
    private static final String MESSAGE_EXTENSION = "?success=1";

    private boolean contextRelative;

    @Autowired
    private ManagerService manager;

    /* (non-Javadoc)
     * @see HttpRequestHandler#handleRequest(HttpServletRequest, HttpServletResponse)
     */
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        UserType userType;
        String url = LOGIN_PAGE;
        Set<String> messages = new HashSet<>();
        HttpSession session = request.getSession(false);

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
            url += ERROR_EXTENSION;
        }

        sendRedirect(request, response, url);
    }


    /* Construct the usertype for the manager from the user entered data in the request. */
    private UserType constructUserType(HttpServletRequest request) {
        UserType user = new UserType();
        user.setFirstName(request.getParameter(FIRSTNAME));
        user.setLastName(request.getParameter(SURNAME));
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
        if (StringUtils.isEmpty(request.getParameter(USERNAME))) {
            ok = false;
            messages.add("Username can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(FIRSTNAME))) {
            ok = false;
            messages.add("First Name can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(SURNAME))) {
            ok = false;
            messages.add("Surname can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(EMAIL))) {
            ok = false;
            messages.add("Email can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(PASSWORD))) {
            ok = false;
            messages.add("Password can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(CONFIRM_PASSWORD))) {
            ok = false;
            messages.add("Confirm Password can not be empty!");
        }
        if (ok && !request.getParameter(PASSWORD).equals(request.getParameter(CONFIRM_PASSWORD))) {
            ok = false;
            messages.add("Password and Confirm Password Don't match!");
        }
        return ok;
    }



    /**
     * Redirects the response to the supplied URL.
     * <p>
     * If <tt>contextRelative</tt> is set, the redirect value will be the value after the request context path. Note
     * that this will result in the loss of protocol information (HTTP or HTTPS), so will cause problems if a
     * redirect is being performed to change to HTTPS, for example.
     */
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
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

    /**
     * Removes temporary authentication-related data which may have been stored in the session
     * during the authentication process.
     */
    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.REGISTRATION_EXCEPTION);
    }
}