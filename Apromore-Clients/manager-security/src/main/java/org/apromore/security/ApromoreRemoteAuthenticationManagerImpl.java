package org.apromore.security;

import org.apromore.security.exception.ApromoreRemoteAuthenticationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * Server-side processor of a remote authentication request.
 * <p>
 * This bean requires no security interceptor to protect it. Instead, the bean uses the configured
 * <code>AuthenticationManager</code> to resolve an authentication request.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ApromoreRemoteAuthenticationManagerImpl implements ApromoreRemoteAuthenticationManager, InitializingBean {

    private AuthenticationManager authenticationManager;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.authenticationManager, "authenticationManager is required");
    }

    public Authentication attemptAuthentication(String username, String password) throws ApromoreRemoteAuthenticationException {
        UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(username, password);

        try {
            return authenticationManager.authenticate(request);
        } catch (AuthenticationException authEx) {
            throw new ApromoreRemoteAuthenticationException(authEx.getMessage());
        }
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
