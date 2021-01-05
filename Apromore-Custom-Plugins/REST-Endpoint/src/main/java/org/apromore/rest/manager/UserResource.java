/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.rest.manager;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apromore.dao.model.User;
import org.apromore.mapper.UserMapper;
import org.apromore.portal.model.MembershipType;
import org.apromore.portal.model.UserType;
import org.apromore.rest.AbstractResource;
import org.apromore.rest.ResourceException;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST endpoint for user management.
 *
 * Only JSON payloads supported.
 */
@Path("/user")
public final class UserResource extends AbstractResource {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @DELETE
    @Path("{name}")
    public Response deleteUser(final @PathParam("name") String name) throws ResourceException {

        // Authenticate and authorize the request
        UserType authenticatedUser = authenticatedUser();
        authorize(authenticatedUser, "ROLE_ADMIN");

        // Perform the deletion
        SecurityService securityService = osgiService(SecurityService.class);
        User user = findUserByName(name, securityService);
        securityService.deleteUser(user);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(final @PathParam("name") String name) throws ResourceException {

        // Authenticate and authorize the request
        UserType authenticatedUser = authenticatedUser();
        authorize(authenticatedUser, "ROLE_ADMIN");

        // Lookup the user and convert to DTO
        SecurityService securityService = osgiService(SecurityService.class);
        User user = findUserByName(name, securityService);
        UserType userType = UserMapper.convertUserTypes(user, securityService);

        return Response.status(Response.Status.OK).entity(userType).build();
    }

    /**
     * Users may only be created, not modified.
     *
     * @param userType  a template for a desired user account to create
     * @return the actual user created, including the generated id
     * @throws ResourceException if <var>userType</var> isn't suitable
     */
    @POST
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserType postUser(final @PathParam("name") String name,
                             final UserType userType) throws ResourceException {

        // Authenticate and authorize the request
        UserType authenticatedUser = authenticatedUser();
        authorize(authenticatedUser, "ROLE_ADMIN");

        // Validate the request
        if (userType.getId() != null) {
            throw new ResourceException(Response.Status.NOT_ACCEPTABLE,
                "User field \"id\" prohibited when posting to this URL");
        }
        if (userType.getUsername() != null && !userType.getUsername().equals(name)) {
            throw new ResourceException(Response.Status.NOT_ACCEPTABLE,
                "Requested username \"" + userType.getUsername() + "\" doesn't match this URL");
        }
        if (userType.getMembership() == null || userType.getMembership().getEmail() == null) {
            throw new ResourceException(Response.Status.NOT_ACCEPTABLE,
                "Membership email not provided");
        }

        // Copy just the required fields from the request template to the one we'll actually use
        MembershipType membershipType = new MembershipType();
        membershipType.setEmail(userType.getMembership().getEmail());
        membershipType.setPassword(userType.getMembership().getPassword());

        UserType creatingUserType = new UserType();
        creatingUserType.setUsername(name);
        creatingUserType.setLastName(userType.getLastName());
        creatingUserType.setFirstName(userType.getFirstName());
        creatingUserType.setMembership(membershipType);
        creatingUserType.setOrganization(userType.getOrganization());

        // Ask the security service to create the user
        SecurityService securityService = osgiService(SecurityService.class);
        User user = UserMapper.convertFromUserType(creatingUserType, securityService);
        User createdUser = securityService.createUser(user);

        // Describe the created user for the caller
        UserType createdUserType = UserMapper.convertUserTypes(createdUser, securityService);
        return createdUserType;
    }

    // Internal methods

    /**
     * @param name  the username of some user
     * @return a DTO describing the named user
     * @throws ResourceException if there's no user with the given <var>name</var>
     */
    private User findUserByName(final String name, final SecurityService securityService) throws ResourceException {
        User user = securityService.getUserByName(name);
        if (user == null) {
            throw new ResourceException(Response.Status.NOT_FOUND, "No such user");
        }
        return user;
    }
}
