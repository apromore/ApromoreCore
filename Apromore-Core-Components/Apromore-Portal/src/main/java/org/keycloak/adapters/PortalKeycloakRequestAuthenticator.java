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

package org.keycloak.adapters;  // Pretend to be part of Keycloak; we need protected access to RequestAuthenticator

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
//import org.keycloak.adapters.KeycloakDeployment;
//import org.keycloak.adapters.OAuthRequestAuthenticator;
//import org.keycloak.adapters.OIDCAuthenticationError;
//import org.keycloak.adapters.RequestAuthenticator;
//import org.keycloak.adapters.ServerRequest;
import org.keycloak.adapters.rotation.AdapterTokenVerifier;
import org.keycloak.adapters.spi.AdapterSessionStore;
import org.keycloak.adapters.spi.AuthChallenge;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.common.util.UriUtils;
import org.keycloak.constants.AdapterConstants;
import org.keycloak.enums.TokenStore;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.util.TokenUtil;

/**
 * Selectively override {@link OAuthRequestAuthenticator} to explicitly set the OAuth redirect_url parameter.
 *
 * This is useful when the application is behind a proxy.
 */
public class PortalKeycloakRequestAuthenticator extends OAuthRequestAuthenticator {

    private static final Logger log = Logger.getLogger(PortalKeycloakRequestAuthenticator.class);

    private String loginURL;

    /**
     * @param loginURL  is this is not <code>null</code>, it will be used as the redirect URL
     */
    public PortalKeycloakRequestAuthenticator(RequestAuthenticator requestAuthenticator, HttpFacade facade,
        KeycloakDeployment deployment, int sslRedirectPort, AdapterSessionStore tokenStore, String loginURL) {

        super(requestAuthenticator, facade, deployment, sslRedirectPort, tokenStore);
        this.loginURL = loginURL;
    }

    /**
     * {@inheritDoc}
     *
     * This code is largely copied from {@link OAuthRequestAuthenticator#getRedirectUri} in Keycloak 14.
     */
    @Override
    protected String getRedirectUri(String state) {
        String url = loginURL == null ? getRequestUrl() : loginURL;
        log.debugf("callback uri: %s", url);

        if (!facade.getRequest().isSecure() && deployment.getSslRequired().isRequired(facade.getRequest().getRemoteAddr())) {
            int port = sslRedirectPort();
            if (port < 0) {
                // disabled?
                return null;
            }
            KeycloakUriBuilder secureUrl = KeycloakUriBuilder.fromUri(url).scheme("https").port(-1);
            if (port != 443) secureUrl.port(port);
            url = secureUrl.build().toString();
        }

        String loginHint = getQueryParamValue("login_hint");
        url = UriUtils.stripQueryParam(url,"login_hint");

        String idpHint = getQueryParamValue(AdapterConstants.KC_IDP_HINT);
        url = UriUtils.stripQueryParam(url, AdapterConstants.KC_IDP_HINT);

        String scope = getQueryParamValue(OAuth2Constants.SCOPE);
        url = UriUtils.stripQueryParam(url, OAuth2Constants.SCOPE);

        String prompt = getQueryParamValue(OAuth2Constants.PROMPT);
        url = UriUtils.stripQueryParam(url, OAuth2Constants.PROMPT);

        String maxAge = getQueryParamValue(OAuth2Constants.MAX_AGE);
        url = UriUtils.stripQueryParam(url, OAuth2Constants.MAX_AGE);

        String uiLocales = getQueryParamValue(OAuth2Constants.UI_LOCALES_PARAM);
        url = UriUtils.stripQueryParam(url, OAuth2Constants.UI_LOCALES_PARAM);
        log.infof("stripped uri: %s", url);
        log.infof("rewritten uri: %s", rewrittenRedirectUriCopy(url));

        KeycloakUriBuilder redirectUriBuilder = deployment.getAuthUrl().clone()
            .queryParam(OAuth2Constants.RESPONSE_TYPE, OAuth2Constants.CODE)
            .queryParam(OAuth2Constants.CLIENT_ID, deployment.getResourceName())
            .queryParam(OAuth2Constants.REDIRECT_URI, rewrittenRedirectUriCopy(url))
            .queryParam(OAuth2Constants.STATE, state)
            .queryParam("login", "true");
        if(loginHint != null && loginHint.length() > 0){
            redirectUriBuilder.queryParam("login_hint",loginHint);
        }
        if (idpHint != null && idpHint.length() > 0) {
            redirectUriBuilder.queryParam(AdapterConstants.KC_IDP_HINT,idpHint);
        }
        if (prompt != null && prompt.length() > 0) {
            redirectUriBuilder.queryParam(OAuth2Constants.PROMPT, prompt);
        }
        if (maxAge != null && maxAge.length() > 0) {
            redirectUriBuilder.queryParam(OAuth2Constants.MAX_AGE, maxAge);
        }
        if (uiLocales != null && uiLocales.length() > 0) {
            redirectUriBuilder.queryParam(OAuth2Constants.UI_LOCALES_PARAM, uiLocales);
        }

        scope = TokenUtil.attachOIDCScope(scope);
        redirectUriBuilder.queryParam(OAuth2Constants.SCOPE, scope);

        return redirectUriBuilder.build().toString();
    }

    /**
     * {@inheritDoc}
     *
     * This code is largely copied from {@link OAuthRequestAuthenticator#resolveCode} in Keycloak 14.
     */
    @Override
    protected AuthChallenge resolveCode(String code) {
        // abort if not HTTPS
        if (!isRequestSecure() && deployment.getSslRequired().isRequired(facade.getRequest().getRemoteAddr())) {
            log.error("Adapter requires SSL. Request: " + facade.getRequest().getURI());
            return challenge(403, OIDCAuthenticationError.Reason.SSL_REQUIRED, null);
        }

        log.debug("checking state cookie for after code");
        AuthChallenge challenge = checkStateCookie();
        if (challenge != null) return challenge;

        AccessTokenResponse tokenResponse = null;
        strippedOauthParametersRequestUri = loginURL == null
            ? rewrittenRedirectUriCopy(stripOauthParametersFromRedirect())
            : loginURL;

        try {
            // For COOKIE store we don't have httpSessionId and single sign-out won't be available
            String httpSessionId = deployment.getTokenStore() == TokenStore.SESSION ? reqAuthenticator.changeHttpSessionId(true) : null;
            tokenResponse = ServerRequest.invokeAccessCodeToToken(deployment, code, strippedOauthParametersRequestUri, httpSessionId);
        } catch (ServerRequest.HttpFailure failure) {
            log.error("failed to turn code into token");
            log.error("status from server: " + failure.getStatus());
            if (failure.getError() != null && !failure.getError().trim().isEmpty()) {
                log.error("   " + failure.getError());
            }
            return challenge(403, OIDCAuthenticationError.Reason.CODE_TO_TOKEN_FAILURE, null);

        } catch (IOException e) {
            log.error("failed to turn code into token", e);
            return challenge(403, OIDCAuthenticationError.Reason.CODE_TO_TOKEN_FAILURE, null);
        }

        tokenString = tokenResponse.getToken();
        refreshToken = tokenResponse.getRefreshToken();
        idTokenString = tokenResponse.getIdToken();

        log.debug("Verifying tokens");
        if (log.isTraceEnabled()) {
            logTokenCopy("\taccess_token", tokenString);
            logTokenCopy("\tid_token", idTokenString);
            logTokenCopy("\trefresh_token", refreshToken);
        }

        try {
            AdapterTokenVerifier.VerifiedTokens tokens = AdapterTokenVerifier.verifyTokens(tokenString, idTokenString, deployment);
            token = tokens.getAccessToken();
            idToken = tokens.getIdToken();
            log.debug("Token Verification succeeded!");
        } catch (VerificationException e) {
            log.error("failed verification of token: " + e.getMessage());
            return challenge(403, OIDCAuthenticationError.Reason.INVALID_TOKEN, null);
        }
        if (tokenResponse.getNotBeforePolicy() > deployment.getNotBefore()) {
            deployment.updateNotBefore(tokenResponse.getNotBeforePolicy());
        }
        if (token.getIssuedAt() < deployment.getNotBefore()) {
            log.error("Stale token");
            return challenge(403, OIDCAuthenticationError.Reason.STALE_TOKEN, null);
        }
        log.debug("successful authenticated");
        return null;
    }

    /**
     * Copied from {@link OAuthRequestAuthenticator#rewrittenRedirectUri} because it was private.
     *
     * When bumping Keycloak to a version beyond 14, this may need to be resychronized.
     */
    private String rewrittenRedirectUriCopy(String originalUri) {
        Map<String, String> rewriteRules = deployment.getRedirectRewriteRules();
        if(rewriteRules != null && !rewriteRules.isEmpty()) {
            try {
                URL url = new URL(originalUri);
                Map.Entry<String, String> rule =  rewriteRules.entrySet().iterator().next();
                StringBuilder redirectUriBuilder = new StringBuilder(url.getProtocol());
                redirectUriBuilder.append("://"+ url.getAuthority());
                redirectUriBuilder.append(url.getPath().replaceFirst(rule.getKey(), rule.getValue()));
                return redirectUriBuilder.toString();
            } catch (MalformedURLException ex) {
                log.error("Not a valid request url");
                throw new RuntimeException(ex);
            }
        }
        return originalUri;
    }

    /**
     * Copied from {@link OAuthRequestAuthenticator#logToken} because it was private.
     *
     * When bumping Keycloak to a version beyond 14, this may need to be resychronized.
     */
    private void logTokenCopy(String name, String token) {
        try {
            JWSInput jwsInput = new JWSInput(token);
            String wireString = jwsInput.getWireString();
            log.tracef("\t%s: %s", name, wireString.substring(0, wireString.lastIndexOf(".")) + ".signature");
        } catch (JWSInputException e) {
            log.errorf(e, "Failed to parse %s: %s", name, token);
        }
    }
}
