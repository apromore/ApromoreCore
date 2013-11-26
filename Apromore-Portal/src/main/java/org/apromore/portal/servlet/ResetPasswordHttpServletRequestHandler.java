package org.apromore.portal.servlet;

import org.apache.commons.lang.StringUtils;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.MembershipType;
import org.apromore.model.UserType;
import org.apromore.portal.common.WebAttributes;
import org.apromore.portal.util.MailUtil;
import org.apromore.portal.util.RandomPasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component("resetPassword")
public class ResetPasswordHttpServletRequestHandler extends BaseServletRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordHttpServletRequestHandler.class);

    @Autowired
    private ManagerService manager;

    private static final int minLength = 5;
    private static final int maxLength = 8;
    private static final int noCapitals = 1;
    private static final int noDigits = 1;
    private static final int noSpecial = 1;

    private static final String EMAIL_ADDRESS = "apromore@qut.edu.au";
    private static final String EMAIL_SUBJECT = "Reset Password";
    private static final String EMAIL_START = "Hi, Here is your newly requested password: ";
    private static final String EMAIL_END = "Please try to login again!";


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
                userType = manager.readUser(request.getParameter(USERNAME));
                resetUsersPassword(userType);
                emailUserPassword(userType);
                clearAuthenticationAttributes(request);
                url += MESSAGE_EXTENSION;
            } else {
                url += ERROR_EXTENSION;
                if (session != null && !messages.isEmpty()) {
                    request.getSession().setAttribute(WebAttributes.REGISTRATION_EXCEPTION, messages);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error with resetting a users password!", e);
            messages.add("Error with resetting a users password! Please try again.");
            url += ERROR_EXTENSION;
        }

        sendRedirect(request, response, url);
    }



    /* Reset the password and update the userType record. */
    private void resetUsersPassword(UserType userType) throws Exception {
        String newPassword = new String(RandomPasswordGenerator.generatePassword(minLength, maxLength, noCapitals, noDigits, noSpecial));

        MembershipType membership = userType.getMembership();
        membership.setPassword(newPassword);
        userType.setMembership(membership);

        manager.writeUser(userType);
    }


    /* Email the Users Password to them. */
    private void emailUserPassword(UserType userType) {
        String emailText = EMAIL_START + userType.getMembership().getPassword() + EMAIL_END;
        MailUtil.sendEmailText(userType.getMembership().getEmail(), EMAIL_ADDRESS, EMAIL_SUBJECT, emailText);
    }


    /* Check that all the data is correct and present so we can proceed with user registration. */
    private boolean isUserRequestOk(HttpServletRequest request, Set<String> messages) {
        boolean ok = true;
        if (StringUtils.isEmpty(request.getParameter(USERNAME))) {
            ok = false;
            messages.add("Username can not be empty!");
        }
        return ok;
    }

}