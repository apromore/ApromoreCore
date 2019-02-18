package org.apromore.security;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.jaas.AuthorityGranter;

public class AuthorityGranterImpl implements AuthorityGranter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityGranterImpl.class);

    @Override
    public Set<String> grant(Principal principal) {
        Set<String> result = "com.sun.security.auth.UserPrincipal".equals(principal.getClass().getName())
            ? Collections.singleton("ROLE_USER")
            : Collections.emptySet();

        LOGGER.debug("Grant " + principal + " of class " + principal.getClass() + " roles " + result);
        return result;
    }
}
