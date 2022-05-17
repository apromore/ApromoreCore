/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.service.impl;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the SecurityService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("securityService")
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
    rollbackFor = Exception.class, value = "transactionManager")
public class SecurityServiceImpl implements SecurityService {

  private static final Logger LOGGER =
      Logger.getLogger(SecurityServiceImpl.class.getCanonicalName());

  private static final String ROLE_ANALYST = "ROLE_ANALYST";
  private static final String ROLE_VIEWER = "ROLE_VIEWER";
  private static final String EMAIL_SUBJECT = "Reset Password";
  private static final String EMAIL_START = "Hi, Here is your newly requested password: ";
  private static final String EMAIL_END = "\nPlease try to login again!";

  @Value("${allowedPasswordHashingAlgorithms}")
  String allowedPasswordHashingAlgorithms;

  @Value("${passwordHashingAlgorithm}")
  String passwordHashingAlgorithm;

  @Value("${saltLength}")
  int saltLength;

  @Value("${upgradePasswords}")
  boolean upgradePasswords;

  @Value("${assignViewerRole}")
  boolean assignViewerRole;

  private UserRepository userRepo;
  private GroupRepository groupRepo;
  private RoleRepository roleRepo;
  private PermissionRepository permissionRepo;
  private MembershipRepository membershipRepo;
  private WorkspaceService workspaceService;
  private MailSender mailSender;


  /**
   * Default Constructor allowing Spring to Autowire for testing and normal use.
   * 
   * @param userRepository User Repository.
   * @param groupRepository Group Repository.
   * @param roleRepository Role Repository.
   */
  @Inject
  public SecurityServiceImpl(final UserRepository userRepository,
      final GroupRepository groupRepository, final RoleRepository roleRepository,
      final PermissionRepository permissionRepository,
      final MembershipRepository membershipRepository, final WorkspaceService wrkSrv,
      final MailSender mailSender) {

    userRepo = userRepository;
    groupRepo = groupRepository;
    roleRepo = roleRepository;
    permissionRepo = permissionRepository;
    membershipRepo = membershipRepository;
    workspaceService = wrkSrv;
    this.mailSender = mailSender;

  }


  /**
   * @see org.apromore.service.SecurityService#getAllUsers() {@inheritDoc}
   *
   *      NOTE: This might need to convert (or allow for) to the models used in the webservices.
   */
  @Override
  public List<User> getAllUsers() {
    return userRepo.findAll();
  }

  /**
   * @see org.apromore.service.SecurityService#getUserByName(String) {@inheritDoc}
   */
  @Override
  public User getUserByName(String username) {
    User user = userRepo.findByUsername(username);
    if (user == null) {
      return null;
    }
    Hibernate.initialize(user.getRoles());
    for (Role role : user.getRoles()) {
      Hibernate.initialize(role.getPermissions());
    }
    Hibernate.initialize(user.getMembership());
    Hibernate.initialize(user.getSearchHistories());
    return user;
  }

  /**
   * @see org.apromore.service.SecurityService#getUserByName(String) {@inheritDoc}
   */
  @Override
  public List<User> searchUsers(String searchString) {
    return userRepo.findByUsernameLike(searchString);
  }

  @Override
  @Transactional(readOnly = false)
  public Group createGroup(String name) {
    Group group = new Group();
    group.setName(name);
    group.setType(Group.Type.GROUP);
    Group result = groupRepo.saveAndFlush(group);

    postEvent(EventType.CREATE_GROUP, null, result);

    return result;
  }

  @Override
  @Transactional(readOnly = false)
  public Group updateGroup(Group group) {
    if (group.getType() != Group.Type.GROUP) {
      throw new IllegalArgumentException("Group " + group.getName() + " cannot be modified");
    }
    Group result = groupRepo.saveAndFlush(group);
    postEvent(EventType.UPDATE_GROUP, null, result);

    return result;
  }

  @Override
  @Transactional(readOnly = false)
  public void deleteGroup(Group group) {
    postEvent(EventType.DELETE_GROUP, null, group);

    groupRepo.delete(group);
  }

  @Override
  public List<Group> findAllGroups() {
    return groupRepo.findAllGroups();
  }

  @Override
  public List<Group> findElectiveGroups() {
    return groupRepo.findElectiveGroups();
  }

  @Override
  public Group getGroupByName(String name) {
    Group group = groupRepo.findByName(name);
    if (group != null) {
      group.getUsers().size();
    }
    return group;
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
      Role role = roleRepo.findByName(name);
      if (role != null) {
          //Load users in the role object
          //@TODO: Use another way to load the users so we don't return an unused value.
          role.getUsers().size();
      }
      return role;
  }

  @Override
  public Set<Role> findRolesByUser(User user) {
    return roleRepo.findByUser(user);
  }

  @Override
  public Role updateRole(Role role) {
    Role sameNameRole = roleRepo.findByName(role.getName());
    if (sameNameRole != null && !sameNameRole.getRowGuid().equals(role.getRowGuid())) {
      throw new DuplicateKeyException("A role with this name already exists");
    }

    return roleRepo.saveAndFlush(role);
  }

  /**
   * @see org.apromore.service.SecurityService#deleteRole(org.apromore.dao.model.Role) {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public void deleteRole(Role role) {
    //Remove users from role before deleting. This fixes an issue where the
    //individual user group is deleted when deleting a role.
    role.setUsers(new HashSet<>());
    roleRepo.delete(role);
  }

  /**
   * @see org.apromore.service.SecurityService#getUserByEmail(String) {@inheritDoc}
   */
  @Override
  public User getUserByEmail(String email) {
    return userRepo.findByMembershipEmail(email);
  }

  /**
   * @see org.apromore.service.SecurityService#getUserById(String) {@inheritDoc}
   */
  @Override
  public User getUserById(String guid) throws UserNotFoundException {
    User user = userRepo.findByRowGuid(guid);
    if (user != null) {
      return user;
    } else {
      throw new UserNotFoundException("User with id (" + guid + ") could not be found.");
    }
  }

  /**
   * @see org.apromore.service.SecurityService#getPermission(String) {@inheritDoc}
   */
  @Override
  public Permission getPermission(String name) {
    return permissionRepo.findByName(name);
  }

  /**
   * @see org.apromore.service.SecurityService#getUserPermissions(String) {@inheritDoc}
   */
  @Override
  public List<Permission> getUserPermissions(String userGuid) {
    return permissionRepo.findByUser(userGuid);
  }

  /**
   * @see org.apromore.service.SecurityService#getRolePermissions(String) {@inheritDoc}
   */
  @Override
  public List<Permission> getRolePermissions(String roleName) {
    return permissionRepo.findByRole(roleName);
  }

  /**
   * @see org.apromore.service.SecurityService#hasAccess(String, String) {@inheritDoc}
   */
  @Override
  public boolean hasAccess(String userId, String permissionId) {
    return userRepo.hasAccess(userId, permissionId);
  }

  /**
   * @see org.apromore.service.SecurityService#createRole(org.apromore.dao.model.Role) {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public Role createRole(Role role) {
    if (roleRepo.findByName(role.getName()) != null) {
      throw new DuplicateKeyException("A role with this name already exists");
    }
    return roleRepo.saveAndFlush(role);
  }

  /**
   * @see org.apromore.service.SecurityService#createUser(org.apromore.dao.model.User) {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public User createUser(User user) {
    LOGGER.info("Creating user " + user.getUsername());

    Group publicGroup = groupRepo.findPublicGroup();
    if (publicGroup == null) {
      throw new RuntimeException(
          "Could not create user because public group not present in database");
    }

    // Every user needs a personal access control group
    Group group = new Group();
    group.setName(user.getUsername());
    group.setType(Group.Type.USER);
    group = groupRepo.saveAndFlush(group);
    LOGGER.info("  Created group " + group.getId() + " named " + group.getName());

    // Create the actual user record
    user.setDateCreated(Calendar.getInstance().getTime());
    user.setLastActivityDate(null); // null indicates "never connected"
    user.setRowGuid(UUID.randomUUID().toString());
    user.setGroup(group);

    user.getMembership().setDateCreated(user.getDateCreated());


    Role assignedRole=null;
    if(assignViewerRole){
      assignedRole = roleRepo.findByName(ROLE_VIEWER);
    }else{
      assignedRole = roleRepo.findByName(ROLE_ANALYST);
    }

    Set<Role> roles = new HashSet<>();
    if (assignedRole == null) {
      throw new RuntimeException("Could not create user because "+(assignViewerRole?ROLE_VIEWER:ROLE_ANALYST)+" not present in database");
    }
    roles.add(assignedRole);
    user.setRoles(roles);

    // All users are in the compulsory groups: their personal singleton group, and the public group
    Set<Group> userGroups = new HashSet<>();
    userGroups.add(group);
    userGroups.add(publicGroup);
    user.setGroups(userGroups);
    LOGGER.info("  Added to groups " + userGroups);

    user = userRepo.saveAndFlush(user); // Only at this point does the system assign a primary key
                                        // to the new user


    // Membership has a link back to the associated user, so it can be created afterwards
    // user.setMembership(user.getMembership());
    // user.getMembership().setUser(user);
    // membershipRepo.save(user.getMembership());

    postEvent(EventType.CREATE_USER, user, null);

    LOGGER.info("Created user " + user.getUsername());
    return user;
  }

  /**
   * @see org.apromore.service.SecurityService#createUser(org.apromore.dao.model.User, String) {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public User createUser(User user, String password) throws NoSuchAlgorithmException {

      hashPassword(user.getMembership(), password);

      return createUser(user);
  }

  @Override
  @Transactional(readOnly = false)
  public User updateUser(User user) {

    // A user can always access their personal group and the public group
    Set<Group> groups = new HashSet<>();
    groups.addAll(groupRepo.findCompulsoryGroups(user));
    groups.addAll(user.getGroups());
    user.setGroups(groups);

    User result = userRepo.save(user);
    postEvent(EventType.UPDATE_USER, result, null);

    return result;
  }

  @Override
  @Transactional(readOnly = false)
  public void deleteUser(User user) {
    postEvent(EventType.DELETE_USER, user, null);

    workspaceService.updateOwnerAfterDeleteUser(user);
    userRepo.delete(user);
  }

  /**
   * @see org.apromore.service.SecurityService#resetUserPassword(String, String) {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public boolean resetUserPassword(String username, String newPassword) {

    User user = userRepo.findByUsername(username);
    Membership membership = user.getMembership();
    try {
      // Email the password to the user
      emailUserPassword(membership, newPassword);

      // Change the password in the database
      updatePassword(membership, newPassword);

      return true;

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e, () -> "Unable to reset password for user " + membership.getEmail());
      return false;
    }
  }

  /**
   * @see org.apromore.service.SecurityService#changeUserPassword(String, String, String)
   *      {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public boolean changeUserPassword(String username, String oldPassword, String newPassword) {

    User user = userRepo.findByUsername(username);
    Membership membership = user.getMembership();

    // Check that the password hashing algorithm is one we accept
    if (!Arrays.asList(allowedPasswordHashingAlgorithms.split("\\s+")).contains(membership.getHashingAlgorithm())) {
        return false;
    }

    try {
      // Authenticate
      if (!SecurityUtil.authenticate(membership, oldPassword)) {
          return false;
      }

      // Update database with the new password
      updatePassword(membership, newPassword);

      return true;

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e, () -> "Unable to change password for user \"" + user.getUsername() + "\"");
      return false;
    }
  }

  /** Email the User's Password to them. */
  private void emailUserPassword(Membership membership, String newPassword) throws MailException {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(membership.getEmail());
    message.setSubject(EMAIL_SUBJECT);
    message.setText(EMAIL_START + newPassword + EMAIL_END);
    mailSender.send(message);
  }

  @Transactional(readOnly = false)
  public void updatePassword(Membership membership, final String newPassword) throws NoSuchAlgorithmException {
    hashPassword(membership, newPassword);
    membershipRepo.save(membership);
  }

  /**
   * @param membership  will be modified to have a new password hash; if the hashing algorithm is <code>null</code>
   *     it will be initialized to <code>passwordHashingAlgorithm</code>; if the salt is <code>null</code> it will
   *     be set to a new random value (or the empty string for <code>MD5-UNSALTED</code>)
   * @param newPassword  cleartext password
   * @throws NoSuchAlgorithmException if the configured <code>passwordHashingAlgorithm</code> is not supported
   */
  private void hashPassword(Membership membership, final String newPassword) throws NoSuchAlgorithmException {
    if (upgradePasswords || membership.getHashingAlgorithm() == null) {
      membership.setHashingAlgorithm(passwordHashingAlgorithm);
    }

    if (Membership.MD5_UNSALTED.equals(membership.getHashingAlgorithm())) {
      if (membership.getSalt() == null) {
        membership.setSalt("");
      }
      membership.setPassword(SecurityUtil.hash(newPassword, "MD5", Charset.defaultCharset()));

    } else {
      membership.setSalt(RandomStringUtils.randomAlphanumeric(saltLength));
      membership.setPassword(SecurityUtil.hash(newPassword + membership.getSalt(), membership.getHashingAlgorithm()));
    }
  }

  /**
   * Mutator methods publish changes to {@link SecurityService#EVENT_TOPIC}.
   *
   * @param type which more specific mutation occurred, available as the <code>type</code> property
   *        on the posted event
   * @param user unless <code>null</code>, will add <code>user.name</code> and
   *        <code>user.rowGuid</code> properties to the posted event
   * @param group unless <code>null</code>, will add <code>group.name</code> and
   *        <code>group.rowGuid</code> properties to the posted event
   */
  private void postEvent(EventType type, User user, Group group) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("type", type.toString());
    if (user != null) {
      properties.put("user.rowGuid", user.getRowGuid());
      properties.put("user.name", user.getUsername());
    }
    if (group != null) {
      properties.put("group.rowGuid", group.getRowGuid());
      properties.put("group.name", group.getName());
    }
    // eventAdmin.postEvent(new Event(EVENT_TOPIC, properties));
  }
}
