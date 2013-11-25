package org.apromore.portal.common;

/**
 * Well-known keys which are used to store Apromore information in request or session scope.
 *
 * @author Cameron James
 * @since 1
 */
public final class WebAttributes {

    /**
     * Used to cache an registration-failure exception in the session.
     */
    public static final String REGISTRATION_EXCEPTION = "APROMORE_USER_REGISTRATION_EXCEPTION";

    /**
     * Used to cache an registration-message, this could be for password reset or creation successful.
     */
    public static final String REGISTRATION_MESSAGE = "APROMORE_USER_REGISTRATION_MESSAGE";


}
