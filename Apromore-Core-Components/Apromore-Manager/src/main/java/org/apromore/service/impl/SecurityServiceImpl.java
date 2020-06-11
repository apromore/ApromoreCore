/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.service.impl;

import org.apromore.dao.GroupRepository;
import org.apromore.dao.MembershipRepository;
import org.apromore.dao.PermissionRepository;
import org.apromore.dao.RoleRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.SecurityService;
import org.apromore.service.WorkspaceService;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the SecurityService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class SecurityServiceImpl implements SecurityService {

    private static final Logger LOGGER = Logger.getLogger(SecurityServiceImpl.class.getCanonicalName());

    private static final String  ROLE_USER     = "ROLE_USER";
    private static final String  EMAIL_SUBJECT = "Reset Password";
    private static final String  EMAIL_START   = "Hi, Here is your newly requested password: ";
    private static final String  EMAIL_END     = "\nPlease try to login again!";


    private UserRepository userRepo;
    private GroupRepository groupRepo;
    private RoleRepository roleRepo;
    private PermissionRepository permissionRepo;
    private MembershipRepository membershipRepo;
    private WorkspaceService workspaceService;
    private MailSender mailSender;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param userRepository User Repository.
     * @param groupRepository Group Repository.
     * @param roleRepository Role Repository.
     */
    @Inject
    public SecurityServiceImpl(final UserRepository       userRepository,
                               final GroupRepository      groupRepository,
                               final RoleRepository       roleRepository,
                               final PermissionRepository permissionRepository,
                               final MembershipRepository membershipRepository,
                               final WorkspaceService     wrkSrv,
                               final MailSender           mailSender) {

        userRepo         = userRepository;
        groupRepo        = groupRepository;
        roleRepo         = roleRepository;
        permissionRepo   = permissionRepository;
        membershipRepo   = membershipRepository;
        workspaceService = wrkSrv;
        this.mailSender  = mailSender;
    }


    /**
     * @see org.apromore.service.SecurityService#getAllUsers()
     * {@inheritDoc}
     *
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @see org.apromore.service.SecurityService#getUserByName(String)
     * {@inheritDoc}
     */
    @Override
    public User getUserByName(String username) {
        return userRepo.findByUsername(username);
    }

    /**
     * @see org.apromore.service.SecurityService#getUserByName(String)
     * {@inheritDoc}
     */
    @Override
    public List<User> searchUsers(String searchString) {
        return userRepo.findByUsernameLike(searchString);
    }

    @Override
    public Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);
        group.setType(Group.Type.GROUP);

        return groupRepo.saveAndFlush(group);
    }

    @Override
    public Group updateGroup(Group group) {
        return groupRepo.saveAndFlush(group);
    }

    @Override
    public void deleteGroup(Group group) {
        groupRepo.delete(group);
    }

    @Override
    public List<Group> findElectiveGroups() {
        return groupRepo.findElectiveGroups();
    }

    @Override
    public List<Group> searchGroups(String searchString) {
        return groupRepo.findByNameLike(searchString);
    }

    @Override
    public Group findGroupByRowGuid(String rowGuid) {
        return groupRepo.findByRowGuid(rowGuid);
    }

    @Override
    public Set<Group> findGroupsByUser(User user) {
        return groupRepo.findByUser(user);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    public Role findRoleByName(String name) {
        return roleRepo.findByName(name);
    }

    @Override
    public Set<Role> findRolesByUser(User user) {
        return roleRepo.findByUser(user);
    }

    /**
     * @see org.apromore.service.SecurityService#getUserByEmail(String)
     * {@inheritDoc}
     */
    @Override
    public User getUserByEmail(String email){
        return userRepo.findUserByEmail(email);
    }

    /**
     * @see org.apromore.service.SecurityService#getUserById(String)
     * {@inheritDoc}
     */
    @Override
    public User getUserById(String guid) throws UserNotFoundException{
        User user = userRepo.findByRowGuid(guid);
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException("User with id (" + guid + ") could not be found.");
        }
    }

    /**
     * @see org.apromore.service.SecurityService#getUserPermissions(String)
     * {@inheritDoc}
     */
    @Override
    public List<Permission> getUserPermissions(String userGuid){
        return permissionRepo.findByUser(userGuid);
    }

    /**
     * @see org.apromore.service.SecurityService#hasAccess(String, String)
     * {@inheritDoc}
     */
    @Override
    public boolean hasAccess(String userId, String permissionId){
        return userRepo.hasAccess(userId, permissionId);
    }

    /**
     * @see org.apromore.service.SecurityService#createUser(org.apromore.dao.model.User)
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) {
        LOGGER.info("Creating user " + user.getUsername());

        Group publicGroup = groupRepo.findPublicGroup();
        if (publicGroup == null) {
            throw new RuntimeException("Could not create user because public group not present in database");
        }

        // Every user needs a personal access control group
        Group group = new Group();
        group.setName(user.getUsername());
        group.setType(Group.Type.USER);
        group = groupRepo.saveAndFlush(group);
        LOGGER.info("  Created group " + group.getId() + " named " + group.getName());

        // Create the actual user record
        user.setDateCreated(Calendar.getInstance().getTime());
        user.setLastActivityDate(null);  // null indicates "never connected"
        user.setRowGuid(UUID.randomUUID().toString());
        user.setGroup(group);

        // A new user is in the USER role
        Role existingRole = roleRepo.findByName(ROLE_USER);
        if (existingRole == null) {
            throw new RuntimeException("Could not create user because ROLE_USER not present in database");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(existingRole);
        user.setRoles(roles);

        // All users are in the compulsory groups: their personal singleton group, and the public group
        Set<Group> userGroups = new HashSet<>();
        userGroups.add(group);
        userGroups.add(publicGroup);
        user.setGroups(userGroups);
        LOGGER.info("  Added to groups " + userGroups);

        user = userRepo.saveAndFlush(user);  // Only at this point does the system assign a primary key to the new user

        // Membership has a link back to the associated user, so it can be created afterwards
        user.setMembership(user.getMembership());
        user.getMembership().setUser(user);
        membershipRepo.save(user.getMembership());

        LOGGER.info("Created user " + user.getUsername());
        return user;
    }

    @Override
    public User updateUser(User user) {

        // A user can always access their personal group and the public group
        Set<Group> groups = new HashSet<>();
        groups.addAll(groupRepo.findCompulsoryGroups(user));
        groups.addAll(user.getGroups());
        user.setGroups(groups);

        return userRepo.save(user);
    }

    /**
     * @see org.apromore.service.SecurityService#resetUserPassword(String, String)
     * {@inheritDoc}
     */
    @Override
    public boolean resetUserPassword(String username, String newPassword) {

        User user = userRepo.findByUsername(username);
        Membership membership = user.getMembership();
        try {
            // Email the password to the user
            emailUserPassword(membership, newPassword);

            // Change the password in the database
            membership.setPassword(SecurityUtil.hashPassword(newPassword));
            membership = membershipRepo.save(membership);

            return membership.getPassword().equals(SecurityUtil.hashPassword(newPassword));

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to reset password for user " + membership.getEmail(), e);
            return false;
        }
    }

    /**
     * @see org.apromore.service.SecurityService#changeUserPassword(String, String, String)
     * {@inheritDoc}
     */
    @Override
    public boolean changeUserPassword(String username, String oldPassword, String newPassword) {

        User user = userRepo.findByUsername(username);
        Membership membership = user.getMembership();

        if (!membership.getPassword().equals(SecurityUtil.hashPassword(oldPassword))) {
            LOGGER.warning("Failed attempt to change password for user " + membership.getEmail());
            return false;
        }

        try {
            // Change the password in the database
            membership.setPassword(SecurityUtil.hashPassword(newPassword));
            membership = membershipRepo.save(membership);

            return membership.getPassword().equals(SecurityUtil.hashPassword(newPassword));

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to change password for user " + membership.getEmail(), e);
            return false;
        }
    }

    /* Email the Users Password to them. */
    private void emailUserPassword(Membership membership, String newPswd) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(membership.getEmail());
        message.setSubject(EMAIL_SUBJECT);
        message.setText(EMAIL_START + newPswd + EMAIL_END);
        mailSender.send(message);
    }
}
