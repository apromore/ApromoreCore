package org.apromore.security;

import org.apromore.security.exception.ApromoreRemoteAuthenticationException;
import org.springframework.security.core.Authentication;

/**
 * Allows remote clients to attempt authentication.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ApromoreRemoteAuthenticationManager {

    /**
     * Attempts to authenticate the remote client using the presented username and password. If authentication
     * is successful, a collection of {@code GrantedAuthority} objects will be returned.
     * <p>
     * In order to maximise remoting protocol compatibility, a design decision was taken to operate with minimal
     * arguments and return only the minimal amount of information required for remote clients to enable/disable
     * relevant user interface commands etc. There is nothing preventing users from implementing their own equivalent
     * package that works with more complex object types.
     *
     * @param username the username the remote client wishes to authenticate with.
     * @param password the password the remote client wishes to authenticate with.
     *
     * @return the authentication Object containing the the UserType used by Apromore.
     *
     * @throws org.apromore.security.exception.ApromoreRemoteAuthenticationException if the authentication failed.
     */
    Authentication attemptAuthentication(String username, String password)
            throws ApromoreRemoteAuthenticationException;

}
