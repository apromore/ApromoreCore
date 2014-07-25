package org.apromore.security.filter;

import javax.servlet.http.HttpServletRequest;

import org.apromore.security.ApromoreAuthenticationDetailsSource;
import org.apromore.security.ApromoreWebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Apromore needs to have some extra information sent from the server to operate.
 * This class allows the extra info to be include while no effecting the existing security code.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ApromoreUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    protected ApromoreAuthenticationDetailsSource<HttpServletRequest, UsernamePasswordAuthenticationToken, ?> authenticationDetailsSource =
            new ApromoreWebAuthenticationDetailsSource();

    /**
     * Constructor that called the Super classes constructor.
     */
    public ApromoreUsernamePasswordAuthenticationFilter() {
        super();
    }


    /**
     * Provided so that subclasses may configure what is put into the authentication request's details
     * property.
     * @param request that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details set
     */
    @Override
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request, authRequest));
    }


}
