package org.apromore.security.exception;

import org.springframework.security.authentication.rcp.RemoteAuthenticationException;

/**
 * /**
 * Thrown if a <code>ApromoreRemoteAuthenticationManager</code> cannot validate the presented authentication request.
 * <p>
 * This is thrown rather than the normal <code>AuthenticationException</code> because
 * <code>AuthenticationException</code> contains additional properties which may cause issues for
 * the remoting protocol.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ApromoreRemoteAuthenticationException extends RemoteAuthenticationException {

    /**
     * Constructs a <code>ApromoreRemoteAuthenticationException</code> with the
     * specified message and no root cause.
     *
     * @param msg the detail message
     */
    public ApromoreRemoteAuthenticationException(String msg) {
        super(msg);
    }
}
