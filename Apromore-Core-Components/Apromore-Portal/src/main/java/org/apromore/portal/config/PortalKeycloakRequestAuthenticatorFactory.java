/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.portal.config;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.AbstractHttpClient;
import org.keycloak.adapters.AdapterTokenStore;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.OAuthRequestAuthenticator;
import org.keycloak.adapters.PortalKeycloakRequestAuthenticator;
import org.keycloak.adapters.RequestAuthenticator;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.authentication.RequestAuthenticatorFactory;
import org.keycloak.adapters.springsecurity.authentication.SpringSecurityRequestAuthenticator;

/**
 * Modify the OpenID Connect authentication flow to allow use of a bookmarkable URL when it redirects to the Keycloak login
 * page, and to allow disabling connection pooling.
 *
 * Overrides the {@link org.keycloak.adapters.springsecurity.authentication.SpringSecurityRequestAuthenticatorFactory}
 * to return {@link PortalKeycloakRequestAuthenticator} instead of {@link OAuthRequestAuthenticator}.
 */
@Slf4j
class PortalKeycloakRequestAuthenticatorFactory implements RequestAuthenticatorFactory {

    private final boolean isConnectionPoolingEnabled;
    private final String loginURL;

    /**
     * @param loginURL  if non-null, the redirection URL to the front-end proxy,
     *     e.g. <code>"https://example.com/sso/login"</code>
     */
    PortalKeycloakRequestAuthenticatorFactory(final String loginURL, final boolean connectionPoolingEnabled) {
        this.isConnectionPoolingEnabled = connectionPoolingEnabled;
        this.loginURL = loginURL;
    }

    @Override
    public RequestAuthenticator createRequestAuthenticator(HttpFacade facade, HttpServletRequest request,
            KeycloakDeployment deployment, AdapterTokenStore tokenStore, int sslRedirectPort) {

        if (!isConnectionPoolingEnabled) {
            log.info("Disabling HTTP connection reuse within the Keycloak OIDC filter");
            ((AbstractHttpClient) deployment.getClient()).setReuseStrategy(NoConnectionReuseStrategy.INSTANCE);
        }

        return new SpringSecurityRequestAuthenticator(facade, request, deployment, tokenStore, sslRedirectPort) {

            @Override
            protected OAuthRequestAuthenticator createOAuthAuthenticator() {
                return new PortalKeycloakRequestAuthenticator(this, facade, deployment, sslRedirectPort, tokenStore, loginURL);
            }
        };
    }
}
