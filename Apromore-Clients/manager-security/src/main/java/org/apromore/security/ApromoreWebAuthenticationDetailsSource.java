package org.apromore.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Implementation of {@link org.apromore.security.ApromoreAuthenticationDetailsSource} which builds the details object from
 * an <tt>HttpServletRequest</tt> object and <tt>UsernamePasswordAuthenticationToken</tt>, creating a {@code ApromoreWebAuthenticationDetails}.
 */
public class ApromoreWebAuthenticationDetailsSource implements
        ApromoreAuthenticationDetailsSource<HttpServletRequest, UsernamePasswordAuthenticationToken, ApromoreWebAuthenticationDetails> {

    /**
     * @param context the {@code HttpServletRequest} object.
     * @param token the {@code UsernamePasswordAuthenticationToken} object.
     * @return the {@code WebAuthenticationDetails} containing information about the current request
     */
    public ApromoreWebAuthenticationDetails buildDetails(HttpServletRequest context, UsernamePasswordAuthenticationToken token) {
        return new ApromoreWebAuthenticationDetails(context, token);
    }

}
