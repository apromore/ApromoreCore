package org.apromore.security;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.jaas.AuthorityGranter;

public class AuthorityGranterImpl implements AuthorityGranter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityGranterImpl.class);

    private String principalClassName;
    private final Set<String> grants = new HashSet<>();

    public void setPrincipalClassName(String newPrincipalClassName) {
        this.principalClassName = newPrincipalClassName;
    }

    public void setGrants(Set<String> newGrants) {
        this.grants.clear();
        this.grants.addAll(newGrants);
    }

    // Implementation of AuthorityGranter

    @Override
    public Set<String> grant(Principal principal) {

        // Create a new set rather than ever returning grants, because malicious code could try mutating it
        Set<String> result = new HashSet<>();

        if (principal == null) {
            LOGGER.warn("Null principal was not granted any roles");

        } else {
            if (principalClassName == null) {
                LOGGER.warn("No principal class name configured");

            } else if (principalClassName.equals(principal.getClass().getName())) {
                result.addAll(grants);
            }

            LOGGER.debug("Grant " + principal + " of class " + principal.getClass() + " roles " + result);
        }
        return result;
    }
}
