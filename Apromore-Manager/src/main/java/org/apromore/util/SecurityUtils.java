package org.apromore.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security Utils.
 *
 * @author Cameron James
 * @since 1.0
 */
public class SecurityUtils {

    private SecurityUtils() { }

    /**
     * get the logged in User in the System.
     * @return the username
     */
    public static String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        } else {
            String username = authentication.getPrincipal().toString();
            if (username != null) {
                return username;
            } else {
                return null;
            }
        }
    }

}
